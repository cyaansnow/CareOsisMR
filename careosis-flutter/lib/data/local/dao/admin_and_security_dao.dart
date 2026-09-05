import 'dart:async';
import '../db/careosis_database.dart';
import '../entities/admin_and_security_entities.dart';
import '../../../core/engine/incentive_calculation_engine.dart';

class AdminAndSecurityDao {
  final CareOsisDatabase _db;
  AdminAndSecurityDao(this._db);

  Stream<List<UserAccount>> getAllUsers() {
    return Stream.value(_db.userAccounts.values.toList()..sort((a, b) => a.name.compareTo(b.name)));
  }

  Stream<List<UserAccount>> getAllAdmins() {
    return Stream.value(_db.userAccounts.values.where((u) => u.role == "ADMIN" || u.role == "SUPER_ADMIN").toList());
  }

  Stream<List<UserAccount>> getAllEmployees() {
    return Stream.value(_db.userAccounts.values.where((u) => u.role == "EMPLOYEE").toList());
  }

  Stream<List<UserAccount>> getEmployeesByRegion(String regionId) {
    return Stream.value(_db.userAccounts.values.where((u) =>
      u.role == "EMPLOYEE" && u.assignedRegionIds.split(",").map((s) => s.trim()).contains(regionId)
    ).toList());
  }

  UserAccount? getUserById(String id) {
    return _db.userAccounts[id];
  }

  UserAccount? authenticateUser(String id, String password) {
    final user = _db.userAccounts[id];
    if (user != null && user.password == password) {
      return user;
    }
    return null;
  }

  Future<void> insertUser(UserAccount user) async {
    _db.userAccounts[user.id] = user;
    _db.notifyUsers();
  }

  Future<void> insertUsers(List<UserAccount> users) async {
    for (final u in users) {
      _db.userAccounts[u.id] = u;
    }
    _db.notifyUsers();
  }

  Future<void> updateUser(UserAccount user) async {
    _db.userAccounts[user.id] = user;
    _db.notifyUsers();
  }

  Future<void> updateUserStatus(String id, String status) async {
    final u = _db.userAccounts[id];
    if (u != null) {
      _db.userAccounts[id] = UserAccount(
        id: u.id,
        name: u.name,
        email: u.email,
        phone: u.phone,
        role: u.role,
        password: u.password,
        status: status,
        assignedRegionIds: u.assignedRegionIds,
        employeeScopeMode: u.employeeScopeMode,
        assignedEmployeeIds: u.assignedEmployeeIds,
        permissions: u.permissions,
        canCreateEmployees: u.canCreateEmployees,
        baseSalary: u.baseSalary,
        fixedAllowance: u.fixedAllowance,
        travelAllowance: u.travelAllowance,
        otherAllowance: u.otherAllowance,
        deductions: u.deductions,
        monthlyTarget: u.monthlyTarget,
        reportingAdminId: u.reportingAdminId,
        joiningDate: u.joiningDate,
        designation: u.designation,
        territoryName: u.territoryName,
        createdBy: u.createdBy,
        createdAt: u.createdAt,
      );
      _db.notifyUsers();
    }
  }

  Future<void> updateAdminScope(
    String adminId,
    String regionIds,
    String permissions,
    bool canCreateEmployees,
    String scopeMode,
    String assignedEmployeeIds,
  ) async {
    final u = _db.userAccounts[adminId];
    if (u != null) {
      _db.userAccounts[adminId] = UserAccount(
        id: u.id,
        name: u.name,
        email: u.email,
        phone: u.phone,
        role: u.role,
        password: u.password,
        status: u.status,
        assignedRegionIds: regionIds,
        employeeScopeMode: scopeMode,
        assignedEmployeeIds: assignedEmployeeIds,
        permissions: permissions,
        canCreateEmployees: canCreateEmployees,
        baseSalary: u.baseSalary,
        fixedAllowance: u.fixedAllowance,
        travelAllowance: u.travelAllowance,
        otherAllowance: u.otherAllowance,
        deductions: u.deductions,
        monthlyTarget: u.monthlyTarget,
        reportingAdminId: u.reportingAdminId,
        joiningDate: u.joiningDate,
        designation: u.designation,
        territoryName: u.territoryName,
        createdBy: u.createdBy,
        createdAt: u.createdAt,
      );
      _db.notifyUsers();
    }
  }

  Stream<List<Region>> getAllRegions() {
    return Stream.value(_db.regions.values.toList()..sort((a, b) => a.name.compareTo(b.name)));
  }

  Region? getRegionById(String id) => _db.regions[id];

  Future<void> insertRegion(Region region) async {
    _db.regions[region.id] = region;
    _db.notifyRegions();
  }

  Future<void> updateRegion(Region region) async {
    _db.regions[region.id] = region;
    _db.notifyRegions();
  }

  Future<void> updateRegionStatus(String regionId, String status) async {
    final reg = _db.regions[regionId];
    if (reg != null) {
      _db.regions[regionId] = Region(
        id: reg.id,
        name: reg.name,
        state: reg.state,
        code: reg.code,
        headquarters: reg.headquarters,
        activeMRCount: reg.activeMRCount,
        doctorCount: reg.doctorCount,
        monthlyTarget: reg.monthlyTarget,
        status: status,
        createdBy: reg.createdBy,
        createdAt: reg.createdAt,
        updatedAt: DateTime.now().millisecondsSinceEpoch,
      );
      _db.notifyRegions();
    }
  }

  Future<void> insertRegions(List<Region> list) async {
    for (final r in list) {
      _db.regions[r.id] = r;
    }
    _db.notifyRegions();
  }

  Stream<List<IncentiveRuleModel>> getActiveIncentiveRules() {
    return Stream.value(_db.incentiveRules.values.where((r) => r.status == "ACTIVE").toList()..sort((a, b) => a.priority.compareTo(b.priority)));
  }

  Stream<List<IncentiveRuleModel>> getAllIncentiveRules() {
    return Stream.value(_db.incentiveRules.values.toList()..sort((a, b) => b.updatedAt.compareTo(a.updatedAt)));
  }

  IncentiveRuleModel? getIncentiveRuleById(String id) {
    return _db.incentiveRules[id];
  }

  Future<void> insertIncentiveRule(IncentiveRuleModel rule) async {
    _db.incentiveRules[rule.id] = rule;
    _db.notifyRules();
  }

  Future<void> insertIncentiveRules(List<IncentiveRuleModel> rules) async {
    for (final r in rules) {
      _db.incentiveRules[r.id] = r;
    }
    _db.notifyRules();
  }

  Future<void> archiveRule(String id) async {
    final r = _db.incentiveRules[id];
    if (r != null) {
      _db.incentiveRules[id] = IncentiveRuleModel(
        id: r.id,
        ruleName: r.ruleName,
        ruleType: r.ruleType,
        targetSource: r.targetSource,
        defaultTarget: r.defaultTarget,
        targetPriority: r.targetPriority,
        minThresholdPercent: r.minThresholdPercent,
        maxThresholdPercent: r.maxThresholdPercent,
        incentivePercent: r.incentivePercent,
        fixedRewardAmount: r.fixedRewardAmount,
        slabsJson: r.slabsJson,
        componentsJson: r.componentsJson,
        regionId: r.regionId,
        assignedEmployeeIds: r.assignedEmployeeIds,
        employeeCategory: r.employeeCategory,
        priority: r.priority,
        versionNumber: r.versionNumber,
        effectiveFrom: r.effectiveFrom,
        effectiveTo: r.effectiveTo,
        status: "ARCHIVED",
        formulaDescription: r.formulaDescription,
        updatedBy: r.updatedBy,
        updatedAt: DateTime.now().millisecondsSinceEpoch,
      );
      _db.notifyRules();
    }
  }

  Stream<List<IncentiveRecord>> getAllIncentiveRecords() {
    return Stream.value(_db.incentiveRecords.values.toList()..sort((a, b) => b.calculatedAt.compareTo(a.calculatedAt)));
  }

  Stream<List<IncentiveRecord>> getIncentiveRecordsForEmployee(String empId) {
    return Stream.value(_db.incentiveRecords.values.where((r) => r.employeeId == empId).toList()..sort((a, b) => b.calculatedAt.compareTo(a.calculatedAt)));
  }

  IncentiveRecord? getIncentiveRecordById(String id) {
    return _db.incentiveRecords[id];
  }

  Future<void> insertIncentiveRecord(IncentiveRecord record) async {
    _db.incentiveRecords[record.id] = record;
    _db.notifyRecords();
  }

  Future<void> insertIncentiveRecords(List<IncentiveRecord> records) async {
    for (final r in records) {
      _db.incentiveRecords[r.id] = r;
    }
    _db.notifyRecords();
  }

  Future<void> updateIncentiveRecordStatus(String id, String status, {int? approvedAt, String? approvedBy}) async {
    final r = _db.incentiveRecords[id];
    if (r != null) {
      _db.incentiveRecords[id] = IncentiveRecord(
        id: r.id,
        employeeId: r.employeeId,
        employeeName: r.employeeName,
        period: r.period,
        target: r.target,
        actualSales: r.actualSales,
        achievementPercent: r.achievementPercent,
        ruleId: r.ruleId,
        ruleName: r.ruleName,
        ruleVersion: r.ruleVersion,
        ruleType: r.ruleType,
        applicableSlab: r.applicableSlab,
        incentiveRate: r.incentiveRate,
        baseIncentive: r.baseIncentive,
        coverageIncentive: r.coverageIncentive,
        newDoctorIncentive: r.newDoctorIncentive,
        collectionIncentive: r.collectionIncentive,
        additionalIncentives: r.additionalIncentives,
        deductions: r.deductions,
        finalIncentive: r.finalIncentive,
        status: status,
        breakdownJson: r.breakdownJson,
        calculatedAt: r.calculatedAt,
        approvedAt: approvedAt ?? r.approvedAt,
        approvedBy: approvedBy ?? r.approvedBy,
      );
      _db.notifyRecords();
    }
  }

  Stream<List<SalaryRule>> getAllSalaryRules() {
    return Stream.value(_db.salaryRules.values.toList()..sort((a, b) => b.updatedAt.compareTo(a.updatedAt)));
  }

  Future<void> insertSalaryRule(SalaryRule rule) async {
    _db.salaryRules[rule.id] = rule;
    _db.notifySalaryRules();
  }

  Stream<List<AuditLog>> getAllAuditLogs() {
    return Stream.value(_db.auditLogs.reversed.toList());
  }

  Future<void> insertAuditLog(AuditLog log) async {
    _db.auditLogs.add(log);
    _db.notifyAuditLogs();
  }

  // --- Territories Management ---
  Stream<List<Territory>> getAllTerritories() {
    return Stream.value(_db.territories.values.toList()..sort((a, b) => a.territoryName.compareTo(b.territoryName)));
  }

  Stream<List<Territory>> getTerritoriesByRegion(String regionId) {
    return Stream.value(_db.territories.values.where((t) => t.regionId == regionId).toList());
  }

  Territory? getTerritoryById(String id) => _db.territories[id];

  Future<void> insertTerritory(Territory territory) async {
    _db.territories[territory.territoryId] = territory;
    _db.notifyTerritories();
  }

  Future<void> insertTerritories(List<Territory> list) async {
    for (final t in list) {
      _db.territories[t.territoryId] = t;
    }
    _db.notifyTerritories();
  }

  Future<void> updateTerritory(Territory territory) async {
    _db.territories[territory.territoryId] = territory;
    _db.notifyTerritories();
  }

  Future<void> moveTerritory(String territoryId, String newRegionId) async {
    final t = _db.territories[territoryId];
    if (t != null) {
      _db.territories[territoryId] = Territory(
        territoryId: t.territoryId,
        territoryName: t.territoryName,
        regionId: newRegionId,
        status: t.status,
        createdBy: t.createdBy,
        createdAt: t.createdAt,
        updatedAt: DateTime.now().millisecondsSinceEpoch,
      );
      _db.notifyTerritories();
    }
  }

  // --- Admin Scope Management ---
  Stream<List<AdminScope>> getAllAdminScopes() {
    return Stream.value(_db.adminScopes.values.toList());
  }

  Stream<List<AdminScope>> getScopesForAdmin(String adminId) {
    return Stream.value(_db.adminScopes.values.where((s) => s.adminId == adminId).toList());
  }

  Future<void> insertAdminScope(AdminScope scope) async {
    _db.adminScopes[scope.scopeId] = scope;
    _db.notifyAdminScopes();
  }

  Future<void> insertAdminScopes(List<AdminScope> list) async {
    for (final s in list) {
      _db.adminScopes[s.scopeId] = s;
    }
    _db.notifyAdminScopes();
  }

  Future<void> updateAdminScopeModel(AdminScope scope) async {
    _db.adminScopes[scope.scopeId] = scope;
    _db.notifyAdminScopes();
  }

  // --- Generalized Rule Engine ---
  Stream<List<RuleModel>> getAllGeneralizedRules() {
    return Stream.value(_db.generalizedRules.values.toList());
  }

  Stream<List<RuleModel>> getGeneralizedRulesByType(String ruleType) {
    return Stream.value(_db.generalizedRules.values.where((r) => r.ruleType == ruleType).toList());
  }

  RuleModel? getGeneralizedRuleById(String id) => _db.generalizedRules[id];

  Future<void> insertGeneralizedRule(RuleModel rule) async {
    _db.generalizedRules[rule.ruleId] = rule;
    _db.notifyGeneralizedRules();
  }

  Future<void> insertGeneralizedRules(List<RuleModel> list) async {
    for (final r in list) {
      _db.generalizedRules[r.ruleId] = r;
    }
    _db.notifyGeneralizedRules();
  }

  Future<void> updateGeneralizedRule(RuleModel rule) async {
    _db.generalizedRules[rule.ruleId] = rule;
    _db.notifyGeneralizedRules();
  }

  // --- Approval Engine Requests ---
  Stream<List<ApprovalRequest>> getAllApprovalRequests() {
    return Stream.value(_db.approvalRequests.values.toList()..sort((a, b) => b.createdAt.compareTo(a.createdAt)));
  }

  Stream<List<ApprovalRequest>> getPendingApprovals() {
    return Stream.value(_db.approvalRequests.values.where((a) => a.status == "PENDING").toList()..sort((a, b) => b.createdAt.compareTo(a.createdAt)));
  }

  Stream<List<ApprovalRequest>> getPendingApprovalsForScope(String scope) {
    return Stream.value(_db.approvalRequests.values.where((a) => a.status == "PENDING" && (a.scope == scope || a.scope == "GLOBAL")).toList()..sort((a, b) => b.createdAt.compareTo(a.createdAt)));
  }

  ApprovalRequest? getApprovalById(String id) => _db.approvalRequests[id];

  Future<void> insertApprovalRequest(ApprovalRequest req) async {
    _db.approvalRequests[req.approvalId] = req;
    _db.notifyApprovalRequests();
  }

  Future<void> insertApprovalRequests(List<ApprovalRequest> list) async {
    for (final a in list) {
      _db.approvalRequests[a.approvalId] = a;
    }
    _db.notifyApprovalRequests();
  }

  Future<void> updateApprovalRequest(ApprovalRequest req) async {
    _db.approvalRequests[req.approvalId] = req;
    _db.notifyApprovalRequests();
  }
}
