import 'dart:async';
import 'package:intl/intl.dart';
import '../local/db/careosis_database.dart';
import '../local/entities/admin_and_security_entities.dart';
import '../local/entities/commercial_entities.dart';
import '../local/entities/doctor_and_mr_entities.dart';
import '../local/entities/platform_entities.dart';
import '../local/entities/product_and_academy_entities.dart';
import '../local/seed/seed_data_provider.dart';
import '../../core/engine/incentive_calculation_engine.dart';
import '../../core/services/audit_service.dart';
import '../../core/services/approval_service.dart';
import '../../core/engine/rule_engine.dart';
import '../../core/services/location_tracking_service.dart';
import '../../core/services/supabase_sync_service.dart';
import '../../core/services/auth_service.dart';

class CareOsisRepository {
  final CareOsisDatabase _database;
  late final AuditService auditService;
  late final ApprovalService approvalService;
  late final AuthService authService;

  CareOsisRepository(this._database) {
    auditService = AuditService(_database);
    approvalService = ApprovalService(_database, auditService);
    authService = AuthService(_database);
    restoreSession();
  }

  UserAccount? _currentUser;
  final _currentUserController = StreamController<UserAccount?>.broadcast();
  Stream<UserAccount?> get currentUserStream => _currentUserController.stream;
  UserAccount? get currentUser => _currentUser;
  CareOsisDatabase get database => _database;

  /// Restores saved user session on startup
  Future<void> restoreSession() async {
    final user = await authService.restoreSession();
    if (user != null) {
      _currentUser = user;
      _currentUserController.add(user);
    }
  }

  /// Optional test-only helper to seed isolated mock data if explicitly requested in unit tests
  Future<void> seedDatabaseIfEmpty() async {
    final currentProfile = _database.mrProfileDao.getProfileSync();
    if (currentProfile == null) {
      await _database.adminAndSecurityDao.insertUsers(SeedDataProvider.getInitialUserAccounts());
      await _database.adminAndSecurityDao.insertRegions(SeedDataProvider.getInitialRegions());
      await _database.adminAndSecurityDao.insertTerritories(SeedDataProvider.getInitialTerritories());
      await _database.adminAndSecurityDao.insertAdminScopes(SeedDataProvider.getInitialAdminScopes());
      await _database.adminAndSecurityDao.insertGeneralizedRules(SeedDataProvider.getInitialGeneralizedRules());
      await _database.adminAndSecurityDao.insertIncentiveRules(SeedDataProvider.getInitialIncentiveRules());
      await _database.adminAndSecurityDao.insertApprovalRequests(SeedDataProvider.getInitialApprovalRequests());
    }
  }

  // Authentication & Session
  Future<UserAccount?> authenticate(String id, String password) async {
    final user = await authService.signIn(id, password);
    if (user != null) {
      _currentUser = user;
      _currentUserController.add(user);
    }
    return user;
  }

  Future<UserAccount?> register({
    required String email,
    required String password,
    required String fullName,
    required String hqTerritory,
    String phone = "",
    String role = "EMPLOYEE",
  }) async {
    final user = await authService.signUp(
      email: email,
      password: password,
      fullName: fullName,
      hqTerritory: hqTerritory,
      phone: phone,
      role: role,
    );
    if (user != null) {
      _currentUser = user;
      _currentUserController.add(user);
    }
    return user;
  }

  Future<void> logout() async {
    await authService.signOut();
    _currentUser = null;
    _currentUserController.add(null);
  }

  void setCurrentUser(UserAccount? user) {
    _currentUser = user;
    _currentUserController.add(user);
  }

  // Admin & Users
  Stream<List<UserAccount>> getAllUsers() => _database.adminAndSecurityDao.getAllUsers();
  Stream<List<UserAccount>> getAllAdmins() => _database.adminAndSecurityDao.getAllAdmins();
  Stream<List<UserAccount>> getAllEmployees() => _database.adminAndSecurityDao.getAllEmployees();
  Stream<List<UserAccount>> getEmployeesByRegion(String regionId) => _database.adminAndSecurityDao.getEmployeesByRegion(regionId);

  Future<void> createUser(UserAccount user, String creatorId) async {
    await _database.adminAndSecurityDao.insertUser(user);
    await _database.adminAndSecurityDao.insertAuditLog(
      AuditLog(
        userId: creatorId,
        userName: _currentUser?.name ?? "Admin",
        userRole: _currentUser?.role ?? "SUPER_ADMIN",
        action: user.role == "ADMIN" ? "ADMIN_CREATED" : "EMPLOYEE_CREATED",
        targetEntity: "${user.role}: ${user.name} (${user.id})",
        newValue: "Region: ${user.assignedRegionIds}, Scope: ${user.employeeScopeMode}",
        timestamp: DateTime.now().millisecondsSinceEpoch,
        formattedDate: DateFormat("dd MMM yyyy, hh:mm a").format(DateTime.now()),
      ),
    );
  }

  Future<void> updateAdminScope({
    required String adminId,
    required String regionIds,
    required String permissions,
    required bool canCreateEmployees,
    required String scopeMode,
    required String assignedEmployeeIds,
    required String actorId,
  }) async {
    await _database.adminAndSecurityDao.updateAdminScope(
      adminId,
      regionIds,
      permissions,
      canCreateEmployees,
      scopeMode,
      assignedEmployeeIds,
    );
    await _database.adminAndSecurityDao.insertAuditLog(
      AuditLog(
        userId: actorId,
        userName: _currentUser?.name ?? "Super Admin",
        userRole: "SUPER_ADMIN",
        action: "ADMIN_SCOPE_UPDATED",
        targetEntity: "Admin $adminId",
        newValue: "Regions: $regionIds, Perms: $permissions, Scope: $scopeMode",
        timestamp: DateTime.now().millisecondsSinceEpoch,
        formattedDate: DateFormat("dd MMM yyyy, hh:mm a").format(DateTime.now()),
      ),
    );
  }

  Future<void> updateUserStatus(String id, String status, String actorId) async {
    await _database.adminAndSecurityDao.updateUserStatus(id, status);
    await _database.adminAndSecurityDao.insertAuditLog(
      AuditLog(
        userId: actorId,
        userName: _currentUser?.name ?? "Admin",
        userRole: _currentUser?.role ?? "SUPER_ADMIN",
        action: "USER_STATUS_UPDATED",
        targetEntity: "User $id",
        newValue: "Status changed to $status",
        timestamp: DateTime.now().millisecondsSinceEpoch,
        formattedDate: DateFormat("dd MMM yyyy, hh:mm a").format(DateTime.now()),
      ),
    );
  }

  // Incentive Rules & Calculations
  Stream<List<IncentiveRuleModel>> getActiveIncentiveRules() => _database.adminAndSecurityDao.getActiveIncentiveRules();
  Stream<List<IncentiveRuleModel>> getAllIncentiveRules() => _database.adminAndSecurityDao.getAllIncentiveRules();

  Future<void> saveIncentiveRule(IncentiveRuleModel rule, String actorId, {bool createNewVersion = false}) async {
    final finalRule = createNewVersion
        ? IncentiveRuleModel(
            id: "RULE-${rule.ruleType.substring(0, 4)}-${DateTime.now().millisecondsSinceEpoch.toString().substring(7)}-V${rule.versionNumber + 1}",
            ruleName: rule.ruleName,
            ruleType: rule.ruleType,
            targetSource: rule.targetSource,
            defaultTarget: rule.defaultTarget,
            targetPriority: rule.targetPriority,
            minThresholdPercent: rule.minThresholdPercent,
            maxThresholdPercent: rule.maxThresholdPercent,
            incentivePercent: rule.incentivePercent,
            fixedRewardAmount: rule.fixedRewardAmount,
            slabsJson: rule.slabsJson,
            componentsJson: rule.componentsJson,
            regionId: rule.regionId,
            assignedEmployeeIds: rule.assignedEmployeeIds,
            employeeCategory: rule.employeeCategory,
            priority: rule.priority,
            versionNumber: rule.versionNumber + 1,
            effectiveFrom: rule.effectiveFrom,
            effectiveTo: rule.effectiveTo,
            status: "ACTIVE",
            formulaDescription: rule.formulaDescription,
            updatedBy: actorId,
            updatedAt: DateTime.now().millisecondsSinceEpoch,
          )
        : rule;

    await _database.adminAndSecurityDao.insertIncentiveRule(finalRule);
    await _database.adminAndSecurityDao.insertAuditLog(
      AuditLog(
        userId: actorId,
        userName: _currentUser?.name ?? "Admin",
        userRole: _currentUser?.role ?? "ADMIN",
        action: createNewVersion ? "RULE_VERSION_INCREMENTED" : "RULE_SAVED",
        targetEntity: "Rule ${finalRule.ruleName} (v${finalRule.versionNumber})",
        newValue: "Type: ${finalRule.ruleType}, Target: ₹${finalRule.defaultTarget.toInt()}",
        timestamp: DateTime.now().millisecondsSinceEpoch,
        formattedDate: DateFormat("dd MMM yyyy, hh:mm a").format(DateTime.now()),
      ),
    );
  }

  // Doctors & Prescribers
  Stream<List<Doctor>> getAllDoctors() => _database.doctorDao.getAllDoctors();
  Stream<Doctor?> getDoctorById(String id) => _database.doctorDao.getDoctorById(id);
  Stream<List<Doctor>> searchDoctors(String query) => _database.doctorDao.searchDoctors(query);
  Future<void> addDoctor(Doctor doctor) async {
    await _database.doctorDao.insertDoctor(doctor);
    await _database.platformDao.enqueueSync(
      SyncQueueModel(
        entityType: "DOCTOR",
        entityId: doctor.id,
        action: "INSERT",
        payloadPreview: "Doctor: ${doctor.name} (${doctor.specialty})",
        createdAt: DateTime.now().millisecondsSinceEpoch,
      ),
    );
  }

  // Visits
  Stream<List<DoctorVisit>> getAllVisits() => _database.doctorVisitDao.getAllVisits();
  Stream<List<DoctorVisit>> getVisitsForDoctor(String doctorId) => _database.doctorVisitDao.getVisitsForDoctor(doctorId);
  Future<void> recordVisit(DoctorVisit visit) async {
    await _database.doctorVisitDao.insertVisit(visit);
    final mrId = _currentUser?.id ?? _database.mrProfiles.values.firstOrNull?.empId ?? "MR";
    await _database.mrProfileDao.incrementCompletedVisits(mrId);
    await _database.platformDao.enqueueSync(
      SyncQueueModel(
        entityType: "DOCTOR_VISIT",
        entityId: visit.id,
        action: "INSERT",
        payloadPreview: "Visit to ${visit.doctorName} on ${visit.visitDate}",
        createdAt: DateTime.now().millisecondsSinceEpoch,
      ),
    );
    // Push visit to Supabase Cloud
    SupabaseSyncService.instance.syncDoctorVisit(visit, mrId: mrId);
  }

  // Products & Academy
  Stream<List<ProductModel>> getAllProducts() => _database.productDao.getAllProducts();
  Stream<ProductModel?> getProductById(String id) => _database.productDao.getProductById(id);
  Stream<List<ProductModel>> getFocusProducts() => _database.productDao.getFocusProducts();
  Stream<List<TrainingProgressModel>> getAllTrainingProgress() => _database.academyDao.getAllTrainingProgress();
  Stream<List<AssessmentQuestionModel>> getQuestionsForProduct(String productId) => _database.academyDao.getQuestionsForProduct(productId);

  // Orders
  Stream<List<OrderModel>> getAllOrders() => _database.commercialDao.getAllOrders();
  Stream<OrderModel?> getOrderById(String id) => _database.commercialDao.getOrderById(id);
  Stream<List<OrderItemModel>> getOrderItems(String orderId) => _database.commercialDao.getItemsForOrder(orderId);

  Future<void> createOrder(OrderModel order, List<OrderItemModel> items) async {
    await _database.commercialDao.insertOrder(order);
    await _database.commercialDao.insertOrderItems(items);
    await _database.platformDao.enqueueSync(
      SyncQueueModel(
        entityType: "ORDER",
        entityId: order.id,
        action: "INSERT",
        payloadPreview: "Order #${order.id} for ${order.customerName} (₹${order.totalAmount.toStringAsFixed(0)})",
        createdAt: DateTime.now().millisecondsSinceEpoch,
      ),
    );
  }

  Future<void> updateOrderStatus(String orderId, String status) async {
    await _database.commercialDao.updateOrderStatus(orderId, status);
    await _database.platformDao.enqueueSync(
      SyncQueueModel(
        entityType: "ORDER_STATUS",
        entityId: orderId,
        action: "UPDATE",
        payloadPreview: "Order #$orderId status updated to $status",
        createdAt: DateTime.now().millisecondsSinceEpoch,
      ),
    );
  }

  Future<void> sendOrderToHq(String orderId) async {
    await _database.commercialDao.updateOrderStatus(orderId, "Submitted");
    await _database.platformDao.enqueueSync(
      SyncQueueModel(
        entityType: "ORDER_TRANSMIT",
        entityId: orderId,
        action: "SYNC_HQ",
        payloadPreview: "Order #$orderId transmitted to HQ Central Server",
        createdAt: DateTime.now().millisecondsSinceEpoch,
      ),
    );
  }

  // Expenses
  Stream<List<ExpenseModel>> getAllExpenses() => _database.commercialDao.getAllExpenses();
  Stream<ExpenseModel?> getExpenseById(String id) => _database.commercialDao.getExpenseById(id);
  Future<void> createExpense(ExpenseModel expense) async {
    await _database.commercialDao.insertExpense(expense);
    final mrId = _currentUser?.id ?? _database.mrProfiles.values.firstOrNull?.empId ?? "MR";
    await _database.platformDao.enqueueSync(
      SyncQueueModel(
        entityType: "EXPENSE",
        entityId: expense.id,
        action: "INSERT",
        payloadPreview: "Expense: ${expense.category} - ₹${expense.amount.toStringAsFixed(0)}",
        createdAt: DateTime.now().millisecondsSinceEpoch,
      ),
    );
    // Push expense to Supabase Cloud
    SupabaseSyncService.instance.syncExpense(expense, mrId: mrId);
  }

  Future<void> updateExpenseStatus(String expenseId, String status) async {
    await _database.commercialDao.updateExpenseStatus(expenseId, status);
  }

  Future<void> deleteExpense(String expenseId) async {
    await _database.commercialDao.deleteExpense(expenseId);
  }

  // Attendance & Routes
  Stream<List<AttendanceModel>> getAllAttendance() => _database.commercialDao.getAllAttendance();

  AttendanceModel? getTodayAttendanceSync() {
    final today = DateFormat("yyyy-MM-dd").format(DateTime.now());
    return _database.commercialDao.getAttendanceByIdSync(today);
  }

  Future<AttendanceModel> checkInMR({
    required String empId,
    required String locationName,
    double latitude = 0.0,
    double longitude = 0.0,
  }) async {
    final now = DateTime.now();
    final today = DateFormat("yyyy-MM-dd").format(now);
    final timeStr = DateFormat("hh:mm a").format(now);

    final existing = _database.commercialDao.getAttendanceByIdSync(today);
    final att = AttendanceModel(
      id: today,
      date: today,
      checkInTime: timeStr,
      checkOutTime: "",
      workingHours: "",
      visitsCompleted: existing?.visitsCompleted ?? 0,
      status: "Present",
      checkInLocation: locationName,
      checkInLatitude: latitude,
      checkInLongitude: longitude,
      isSynced: false,
    );

    await _database.commercialDao.insertAttendance(att);
    await _database.mrProfileDao.updateCheckIn(
      empId,
      isCheckedIn: true,
      time: timeStr,
    );

    // Start background GPS tracking
    await LocationTrackingService.instance.startTracking(
      mrId: empId,
      attendanceId: today,
    );

    await _database.platformDao.enqueueSync(
      SyncQueueModel(
        entityType: "ATTENDANCE",
        entityId: att.id,
        action: "CHECK_IN",
        payloadPreview: "MR $empId Checked-In on $today at $timeStr ($locationName)",
        createdAt: now.millisecondsSinceEpoch,
      ),
    );

    // Push to Supabase Cloud
    SupabaseSyncService.instance.syncAttendanceRecord(att, mrId: empId);

    return att;
  }

  Future<AttendanceModel?> checkOutMR({
    required String empId,
    required String locationName,
    double latitude = 0.0,
    double longitude = 0.0,
  }) async {
    final now = DateTime.now();
    final today = DateFormat("yyyy-MM-dd").format(now);
    final timeStr = DateFormat("hh:mm a").format(now);

    final existing = _database.commercialDao.getAttendanceByIdSync(today);

    // Calculate working duration
    String hoursStr = "8h 00m";
    if (existing != null && existing.checkInTime.isNotEmpty) {
      try {
        final parsedCheckIn = DateFormat("hh:mm a").parse(existing.checkInTime);
        final checkInDt = DateTime(now.year, now.month, now.day, parsedCheckIn.hour, parsedCheckIn.minute);
        hoursStr = AttendanceModel.calculateWorkingDuration(checkInDt, now);
      } catch (_) {
        hoursStr = "Field Completed";
      }
    }

    final updated = (existing ?? AttendanceModel(
      id: today,
      date: today,
      checkInTime: timeStr,
      checkInLocation: locationName,
    )).copyWith(
      checkOutTime: timeStr,
      checkOutLocation: locationName,
      checkOutLatitude: latitude,
      checkOutLongitude: longitude,
      workingHours: hoursStr,
      status: "Completed",
      isSynced: false,
    );

    await _database.commercialDao.insertAttendance(updated);
    await _database.mrProfileDao.updateCheckIn(
      empId,
      isCheckedIn: false,
      time: "",
    );

    // Stop background GPS tracking
    await LocationTrackingService.instance.stopTracking();

    await _database.platformDao.enqueueSync(
      SyncQueueModel(
        entityType: "ATTENDANCE",
        entityId: updated.id,
        action: "CHECK_OUT",
        payloadPreview: "MR $empId Checked-Out on $today at $timeStr. Duration: $hoursStr",
        createdAt: now.millisecondsSinceEpoch,
      ),
    );

    // Push to Supabase Cloud
    SupabaseSyncService.instance.syncAttendanceRecord(updated, mrId: empId);

    return updated;
  }

  Future<void> markAttendance(AttendanceModel attendance, String empId) async {
    await _database.commercialDao.insertAttendance(attendance);
    await _database.mrProfileDao.updateCheckIn(
      empId,
      isCheckedIn: attendance.checkOutTime.isEmpty,
      time: attendance.checkInTime,
    );
    await _database.platformDao.enqueueSync(
      SyncQueueModel(
        entityType: "ATTENDANCE",
        entityId: attendance.id,
        action: "INSERT",
        payloadPreview: "Attendance ${attendance.date}: ${attendance.status} (${attendance.checkInTime})",
        createdAt: DateTime.now().millisecondsSinceEpoch,
      ),
    );
  }

  Stream<List<RoutePlanModel>> getAllRoutes() => _database.commercialDao.getAllRoutes();
  Stream<List<FollowUpModel>> getAllFollowUps() => _database.commercialDao.getAllFollowUps();
  Stream<List<FollowUpModel>> getPendingFollowUps() => _database.commercialDao.getPendingFollowUps();

  // Stockists & Retailers
  Stream<List<Stockist>> getAllStockists() => _database.commercialDao.getAllStockists();
  Stream<List<Retailer>> getAllRetailers() => _database.commercialDao.getAllRetailers();

  // Profile & Platform
  Stream<MRProfile?> getMRProfile() => _database.mrProfileDao.getProfile();
  Stream<List<NotificationModel>> getAllNotifications() => _database.platformDao.getAllNotifications();
  Stream<int> getUnreadNotificationCount() => _database.platformDao.getUnreadNotificationCount();
  Future<void> markNotificationAsRead(String id) => _database.platformDao.markAsRead(id);
  Future<void> markAllNotificationsAsRead() => _database.platformDao.markAllAsRead();

  Stream<List<AchievementModel>> getAllAchievements() => _database.platformDao.getAllAchievements();
  Stream<List<LeaderboardModel>> getLeaderboard() => _database.platformDao.getLeaderboard();
  Stream<List<AuditLog>> getAllAuditLogs() => _database.adminAndSecurityDao.getAllAuditLogs();

  // Offline Sync Queue
  Stream<List<SyncQueueModel>> getPendingSyncItems() => _database.platformDao.getPendingSyncItems();
  Stream<int> getPendingSyncCount() => _database.platformDao.getPendingSyncCount();

  Future<void> performSync() async {
    await _database.platformDao.updateSyncStatus(null, "SYNCED");
  }

  // --- Organization: Regions & Territories ---
  Stream<List<Region>> getAllRegions() => _database.adminAndSecurityDao.getAllRegions();
  Region? getRegionById(String id) => _database.adminAndSecurityDao.getRegionById(id);

  Future<void> createRegion(Region region, String creatorId) async {
    await _database.adminAndSecurityDao.insertRegion(region);
    await auditService.logAction(
      actorId: creatorId,
      actorName: _currentUser?.name ?? "Super Admin",
      actorRole: _currentUser?.role ?? "SUPER_ADMIN",
      action: "REGION_CREATED",
      targetEntity: "REGION",
      entityId: region.id,
      oldValue: "",
      newValue: "${region.name} (${region.code})",
    );
  }

  Future<void> updateRegion(Region region, String actorId) async {
    await _database.adminAndSecurityDao.updateRegion(region);
    await auditService.logAction(
      actorId: actorId,
      actorName: _currentUser?.name ?? "Super Admin",
      actorRole: _currentUser?.role ?? "SUPER_ADMIN",
      action: "REGION_UPDATED",
      targetEntity: "REGION",
      entityId: region.id,
      newValue: "${region.name} Target: ₹${region.monthlyTarget.toStringAsFixed(0)}",
    );
  }

  Future<void> updateRegionStatus(String regionId, String status, String actorId) async {
    await _database.adminAndSecurityDao.updateRegionStatus(regionId, status);
    await auditService.logAction(
      actorId: actorId,
      actorName: _currentUser?.name ?? "Super Admin",
      actorRole: _currentUser?.role ?? "SUPER_ADMIN",
      action: "REGION_STATUS_CHANGED",
      targetEntity: "REGION",
      entityId: regionId,
      newValue: status,
    );
  }

  Stream<List<Territory>> getAllTerritories() => _database.adminAndSecurityDao.getAllTerritories();
  Stream<List<Territory>> getTerritoriesByRegion(String regionId) => _database.adminAndSecurityDao.getTerritoriesByRegion(regionId);
  Territory? getTerritoryById(String id) => _database.adminAndSecurityDao.getTerritoryById(id);

  Future<void> createTerritory(Territory territory, String creatorId) async {
    await _database.adminAndSecurityDao.insertTerritory(territory);
    await auditService.logAction(
      actorId: creatorId,
      actorName: _currentUser?.name ?? "Super Admin",
      actorRole: _currentUser?.role ?? "SUPER_ADMIN",
      action: "TERRITORY_CREATED",
      targetEntity: "TERRITORY",
      entityId: territory.territoryId,
      newValue: "${territory.territoryName} in Region ${territory.regionId}",
    );
  }

  Future<void> updateTerritory(Territory territory, String actorId) async {
    await _database.adminAndSecurityDao.updateTerritory(territory);
    await auditService.logAction(
      actorId: actorId,
      actorName: _currentUser?.name ?? "Super Admin",
      actorRole: _currentUser?.role ?? "SUPER_ADMIN",
      action: "TERRITORY_UPDATED",
      targetEntity: "TERRITORY",
      entityId: territory.territoryId,
      newValue: territory.territoryName,
    );
  }

  Future<void> moveTerritory(String territoryId, String newRegionId, String actorId) async {
    final existing = _database.adminAndSecurityDao.getTerritoryById(territoryId);
    final oldRegionId = existing?.regionId ?? "UNKNOWN";
    await _database.adminAndSecurityDao.moveTerritory(territoryId, newRegionId);
    await auditService.logAction(
      actorId: actorId,
      actorName: _currentUser?.name ?? "Super Admin",
      actorRole: _currentUser?.role ?? "SUPER_ADMIN",
      action: "TERRITORY_MOVED",
      targetEntity: "TERRITORY",
      entityId: territoryId,
      oldValue: "Region: $oldRegionId",
      newValue: "Region: $newRegionId",
    );
  }

  // --- Admin Scope Management ---
  Stream<List<AdminScope>> getAllAdminScopes() => _database.adminAndSecurityDao.getAllAdminScopes();
  Stream<List<AdminScope>> getScopesForAdmin(String adminId) => _database.adminAndSecurityDao.getScopesForAdmin(adminId);

  Future<void> saveAdminScope(AdminScope scope, String actorId) async {
    await _database.adminAndSecurityDao.insertAdminScope(scope);
    await auditService.logAction(
      actorId: actorId,
      actorName: _currentUser?.name ?? "Super Admin",
      actorRole: _currentUser?.role ?? "SUPER_ADMIN",
      action: "ADMIN_SCOPE_CHANGED",
      targetEntity: "ADMIN_SCOPE",
      entityId: scope.scopeId,
      newValue: "Admin: ${scope.adminId}, Type: ${scope.scopeType}, Region: ${scope.regionId}, Territories: ${scope.territoryId}, Employees: ${scope.employeeId}",
    );
  }

  // --- Generalized Rule Engine ---
  Stream<List<RuleModel>> getAllGeneralizedRules() => _database.adminAndSecurityDao.getAllGeneralizedRules();
  Stream<List<RuleModel>> getGeneralizedRulesByType(String type) => _database.adminAndSecurityDao.getGeneralizedRulesByType(type);
  RuleModel? getGeneralizedRuleById(String id) => _database.adminAndSecurityDao.getGeneralizedRuleById(id);

  Future<void> saveGeneralizedRule(RuleModel rule, String actorId) async {
    await _database.adminAndSecurityDao.insertGeneralizedRule(rule);
    await auditService.logAction(
      actorId: actorId,
      actorName: _currentUser?.name ?? "Super Admin",
      actorRole: _currentUser?.role ?? "SUPER_ADMIN",
      action: "RULE_SAVED",
      targetEntity: "RULE",
      entityId: rule.ruleId,
      newValue: "${rule.ruleName} (${rule.ruleType}) v${rule.version}",
    );
  }

  RuleModel? resolveRule(String ruleType, {String? employeeId, String? territoryId, String? regionId}) {
    final all = _database.generalizedRules.values.toList();
    return RuleEngine.resolveRule(
      allRules: all,
      ruleType: ruleType,
      employeeId: employeeId,
      territoryId: territoryId,
      regionId: regionId,
    );
  }

  // --- Approval Engine ---
  Stream<List<ApprovalRequest>> getAllApprovalRequests() => _database.adminAndSecurityDao.getAllApprovalRequests();
  Stream<List<ApprovalRequest>> getPendingApprovals() => _database.adminAndSecurityDao.getPendingApprovals();
  Stream<List<ApprovalRequest>> getPendingApprovalsForScope(String scope) => _database.adminAndSecurityDao.getPendingApprovalsForScope(scope);

  Future<ApprovalRequest> submitApprovalRequest({
    required String module,
    required String entityId,
    required String title,
    required String details,
    required String submittedBy,
    required String submittedByName,
    String? approverId,
    String? approverRole,
    String? scope,
    String sla = "24h",
  }) {
    return approvalService.submitRequest(
      module: module,
      entityId: entityId,
      title: title,
      details: details,
      submittedBy: submittedBy,
      submittedByName: submittedByName,
      approverId: approverId,
      approverRole: approverRole,
      scope: scope,
      sla: sla,
    );
  }

  Future<bool> approveApprovalRequest({
    required String approvalId,
    required String reviewerId,
    required String reviewerName,
    required String reviewerRole,
    String comment = "Approved",
  }) {
    return approvalService.approveRequest(
      approvalId: approvalId,
      reviewerId: reviewerId,
      reviewerName: reviewerName,
      reviewerRole: reviewerRole,
      comment: comment,
    );
  }

  Future<bool> rejectApprovalRequest({
    required String approvalId,
    required String reviewerId,
    required String reviewerName,
    required String reviewerRole,
    required String rejectionReason,
  }) {
    return approvalService.rejectRequest(
      approvalId: approvalId,
      reviewerId: reviewerId,
      reviewerName: reviewerName,
      reviewerRole: reviewerRole,
      rejectionReason: rejectionReason,
    );
  }

  // --- Scoped Data Access for Admins ---
  Stream<List<UserAccount>> getScopedEmployees(UserAccount? admin) {
    if (admin == null) return Stream.value([]);
    if (admin.role == "SUPER_ADMIN") return getAllEmployees();

    if (admin.employeeScopeMode == "SPECIFIC_EMPLOYEES") {
      final empIds = admin.assignedEmployeeIds.split(",").map((s) => s.trim()).toList();
      return Stream.value(_database.userAccounts.values.where(
        (u) => u.role == "EMPLOYEE" && (empIds.contains(u.id) || empIds.contains("ALL")),
      ).toList());
    } else {
      final regIds = admin.assignedRegionIds.split(",").map((s) => s.trim()).toList();
      return Stream.value(_database.userAccounts.values.where(
        (u) => u.role == "EMPLOYEE" && (regIds.contains(u.assignedRegionIds) || regIds.contains("GLOBAL")),
      ).toList());
    }
  }

  Stream<List<ApprovalRequest>> getScopedPendingApprovals(UserAccount? admin) {
    if (admin == null) return Stream.value([]);
    if (admin.role == "SUPER_ADMIN") return getPendingApprovals();

    final regIds = admin.assignedRegionIds.split(",").map((s) => s.trim()).toList();
    return Stream.value(_database.approvalRequests.values.where(
      (a) => a.status == "PENDING" && (regIds.contains(a.scope) || a.scope == "GLOBAL" || a.approverId == admin.id),
    ).toList()..sort((a, b) => b.createdAt.compareTo(a.createdAt)));
  }
}
