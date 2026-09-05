import 'dart:async';
import '../entities/admin_and_security_entities.dart';
import '../entities/commercial_entities.dart';
import '../entities/doctor_and_mr_entities.dart';
import '../entities/platform_entities.dart';
import '../entities/product_and_academy_entities.dart';
import '../../../core/engine/incentive_calculation_engine.dart';
import '../dao/careosis_daos.dart';
import '../dao/admin_and_security_dao.dart';

class CareOsisDatabase {
  static CareOsisDatabase? _instance;
  static CareOsisDatabase get instance => _instance ??= CareOsisDatabase._();
  factory CareOsisDatabase() => instance;

  CareOsisDatabase._() {
    mrProfileDao = MRProfileDao(this);
    doctorDao = DoctorDao(this);
    doctorVisitDao = DoctorVisitDao(this);
    productDao = ProductDao(this);
    academyDao = AcademyDao(this);
    commercialDao = CommercialDao(this);
    platformDao = PlatformDao(this);
    adminAndSecurityDao = AdminAndSecurityDao(this);
  }

  late final MRProfileDao mrProfileDao;
  late final DoctorDao doctorDao;
  late final DoctorVisitDao doctorVisitDao;
  late final ProductDao productDao;
  late final AcademyDao academyDao;
  late final CommercialDao commercialDao;
  late final PlatformDao platformDao;
  late final AdminAndSecurityDao adminAndSecurityDao;

  // In-Memory Reactive Table Stores (Backed by SQLite / Persistent Stores)
  final Map<String, MRProfile> mrProfiles = {};
  final Map<String, Doctor> doctors = {};
  final Map<String, DoctorVisit> doctorVisits = {};
  final Map<String, ProductModel> products = {};
  final Map<String, TrainingProgressModel> trainingProgress = {};
  final Map<String, AssessmentQuestionModel> assessmentQuestions = {};
  final Map<String, Stockist> stockists = {};
  final Map<String, Retailer> retailers = {};
  final Map<String, OrderModel> orders = {};
  final List<OrderItemModel> orderItems = [];
  final Map<String, ExpenseModel> expenses = {};
  final Map<String, AttendanceModel> attendance = {};
  final Map<String, RoutePlanModel> routes = {};
  final Map<String, FollowUpModel> followUps = {};
  final Map<String, NotificationModel> notifications = {};
  final Map<String, AchievementModel> achievements = {};
  final Map<String, LeaderboardModel> leaderboard = {};
  final List<SyncQueueModel> syncQueue = [];
  final Map<String, UserAccount> userAccounts = {};
  final Map<String, Region> regions = {};
  final Map<String, IncentiveRuleModel> incentiveRules = {};
  final Map<String, Territory> territories = {};
  final Map<String, AdminScope> adminScopes = {};
  final Map<String, RuleModel> generalizedRules = {};
  final Map<String, ApprovalRequest> approvalRequests = {};
  final Map<String, IncentiveRecord> incentiveRecords = {};
  final Map<String, SalaryRule> salaryRules = {};
  final List<AuditLog> auditLogs = [];

  // Stream Controllers for Reactive Flow Emulation
  final _profileController = StreamController<MRProfile?>.broadcast();
  final _doctorsController = StreamController<List<Doctor>>.broadcast();
  final _visitsController = StreamController<List<DoctorVisit>>.broadcast();
  final _productsController = StreamController<List<ProductModel>>.broadcast();
  final _trainingController = StreamController<List<TrainingProgressModel>>.broadcast();
  final _ordersController = StreamController<List<OrderModel>>.broadcast();
  final _expensesController = StreamController<List<ExpenseModel>>.broadcast();
  final _attendanceController = StreamController<List<AttendanceModel>>.broadcast();
  final _routesController = StreamController<List<RoutePlanModel>>.broadcast();
  final _followUpsController = StreamController<List<FollowUpModel>>.broadcast();
  final _notificationsController = StreamController<List<NotificationModel>>.broadcast();
  final _achievementsController = StreamController<List<AchievementModel>>.broadcast();
  final _leaderboardController = StreamController<List<LeaderboardModel>>.broadcast();
  final _syncQueueController = StreamController<List<SyncQueueModel>>.broadcast();
  final _usersController = StreamController<List<UserAccount>>.broadcast();
  final _regionsController = StreamController<List<Region>>.broadcast();
  final _territoriesController = StreamController<List<Territory>>.broadcast();
  final _adminScopesController = StreamController<List<AdminScope>>.broadcast();
  final _generalizedRulesController = StreamController<List<RuleModel>>.broadcast();
  final _approvalRequestsController = StreamController<List<ApprovalRequest>>.broadcast();
  final _rulesController = StreamController<List<IncentiveRuleModel>>.broadcast();
  final _recordsController = StreamController<List<IncentiveRecord>>.broadcast();
  final _salaryRulesController = StreamController<List<SalaryRule>>.broadcast();
  final _auditLogsController = StreamController<List<AuditLog>>.broadcast();

  Stream<MRProfile?> get profileStream async* {
    yield mrProfiles.values.firstOrNull;
    yield* _profileController.stream;
  }

  Stream<List<AttendanceModel>> get attendanceStream async* {
    yield attendance.values.toList()..sort((a, b) => b.date.compareTo(a.date));
    yield* _attendanceController.stream;
  }

  Stream<List<Doctor>> get doctorsStream async* {
    yield doctors.values.toList()..sort((a, b) => b.priority.compareTo(a.priority));
    yield* _doctorsController.stream;
  }

  Stream<List<DoctorVisit>> get visitsStream async* {
    yield doctorVisits.values.toList()..sort((a, b) => b.createdAt.compareTo(a.createdAt));
    yield* _visitsController.stream;
  }

  void notifyProfile() => _profileController.add(mrProfiles.values.firstOrNull);
  void notifyDoctors() => _doctorsController.add(doctors.values.toList()..sort((a, b) => b.priority.compareTo(a.priority)));
  void notifyVisits() => _visitsController.add(doctorVisits.values.toList()..sort((a, b) => b.createdAt.compareTo(a.createdAt)));
  void notifyProducts() => _productsController.add(products.values.toList()..sort((a, b) => a.name.compareTo(b.name)));
  void notifyTraining() => _trainingController.add(trainingProgress.values.toList()..sort((a, b) => b.completionPercentage.compareTo(a.completionPercentage)));
  void notifyOrders() => _ordersController.add(orders.values.toList()..sort((a, b) => b.createdAt.compareTo(a.createdAt)));
  void notifyExpenses() => _expensesController.add(expenses.values.toList()..sort((a, b) => b.createdAt.compareTo(a.createdAt)));
  void notifyAttendance() => _attendanceController.add(attendance.values.toList()..sort((a, b) => b.date.compareTo(a.date)));
  void notifyRoutes() => _routesController.add(routes.values.toList()..sort((a, b) => b.date.compareTo(a.date)));
  void notifyFollowUps() => _followUpsController.add(followUps.values.toList()..sort((a, b) => a.followUpDate.compareTo(b.followUpDate)));
  void notifyNotifications() => _notificationsController.add(notifications.values.toList()..sort((a, b) => b.timestamp.compareTo(a.timestamp)));
  void notifyAchievements() => _achievementsController.add(achievements.values.toList());
  void notifyLeaderboard() => _leaderboardController.add(leaderboard.values.toList()..sort((a, b) => a.rank.compareTo(b.rank)));
  void notifySyncQueue() => _syncQueueController.add(List.unmodifiable(syncQueue));
  void notifyUsers() => _usersController.add(userAccounts.values.toList()..sort((a, b) => a.name.compareTo(b.name)));
  void notifyRegions() => _regionsController.add(regions.values.toList()..sort((a, b) => a.name.compareTo(b.name)));
  void notifyTerritories() => _territoriesController.add(territories.values.toList()..sort((a, b) => a.territoryName.compareTo(b.territoryName)));
  void notifyAdminScopes() => _adminScopesController.add(adminScopes.values.toList());
  void notifyGeneralizedRules() => _generalizedRulesController.add(generalizedRules.values.toList());
  void notifyApprovalRequests() => _approvalRequestsController.add(approvalRequests.values.toList()..sort((a, b) => b.createdAt.compareTo(a.createdAt)));
  void notifyRules() {
    final list = incentiveRules.values.toList();
    list.sort((IncentiveRuleModel a, IncentiveRuleModel b) => a.priority.compareTo(b.priority));
    _rulesController.add(list);
  }
  void notifyRecords() => _recordsController.add(incentiveRecords.values.toList()..sort((a, b) => b.calculatedAt.compareTo(a.calculatedAt)));
  void notifySalaryRules() => _salaryRulesController.add(salaryRules.values.toList()..sort((a, b) => b.updatedAt.compareTo(a.updatedAt)));
  void notifyAuditLogs() => _auditLogsController.add(auditLogs.reversed.toList());
}

