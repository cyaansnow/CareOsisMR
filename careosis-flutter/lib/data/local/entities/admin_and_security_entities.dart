enum UserRole {
  SUPER_ADMIN,
  ADMIN,
  EMPLOYEE // Field MR
}

enum EmployeeStatus {
  ACTIVE,
  INACTIVE,
  SUSPENDED,
  TERMINATED
}

enum EmployeeScopeMode {
  ALL_IN_REGION,
  SPECIFIC_EMPLOYEES
}

class UserAccount {
  final String id;
  final String name;
  final String email;
  final String phone;
  final String role; // SUPER_ADMIN, ADMIN, EMPLOYEE
  final String password;
  final String status;
  final String assignedRegionIds;
  final String employeeScopeMode;
  final String assignedEmployeeIds;
  final String permissions;
  final bool canCreateEmployees;
  final double baseSalary;
  final double fixedAllowance;
  final double travelAllowance;
  final double otherAllowance;
  final double deductions;
  final double monthlyTarget;
  final String reportingAdminId;
  final String joiningDate;
  final String designation;
  final String territoryName;
  final String createdBy;
  final int createdAt;

  String get assignedTerritoryIds => territoryName;

  const UserAccount({
    required this.id,
    required this.name,
    required this.email,
    required this.phone,
    required this.role,
    required this.password,
    this.status = "ACTIVE",
    this.assignedRegionIds = "DELHI_NCR",
    this.employeeScopeMode = "ALL_IN_REGION",
    this.assignedEmployeeIds = "ALL",
    this.permissions = "VIEW_EMPLOYEES,VIEW_DOCTORS,VIEW_ORDERS,VIEW_EXPENSES,APPROVE_ORDER,APPROVE_EXPENSE",
    this.canCreateEmployees = true,
    this.baseSalary = 35000.0,
    this.fixedAllowance = 8000.0,
    this.travelAllowance = 5000.0,
    this.otherAllowance = 2000.0,
    this.deductions = 1500.0,
    this.monthlyTarget = 200000.0,
    this.reportingAdminId = "CO-ADM-101",
    this.joiningDate = "01 Jan 2025",
    this.designation = "Medical Representative",
    this.territoryName = "North Delhi - Zone 1",
    this.createdBy = "SYSTEM",
    required this.createdAt,
  });

  Map<String, dynamic> toMap() => {
        'id': id,
        'name': name,
        'email': email,
        'phone': phone,
        'role': role,
        'password': password,
        'status': status,
        'assignedRegionIds': assignedRegionIds,
        'employeeScopeMode': employeeScopeMode,
        'assignedEmployeeIds': assignedEmployeeIds,
        'permissions': permissions,
        'canCreateEmployees': canCreateEmployees ? 1 : 0,
        'baseSalary': baseSalary,
        'fixedAllowance': fixedAllowance,
        'travelAllowance': travelAllowance,
        'otherAllowance': otherAllowance,
        'deductions': deductions,
        'monthlyTarget': monthlyTarget,
        'reportingAdminId': reportingAdminId,
        'joiningDate': joiningDate,
        'designation': designation,
        'territoryName': territoryName,
        'createdBy': createdBy,
        'createdAt': createdAt,
      };

  factory UserAccount.fromMap(Map<String, dynamic> map) => UserAccount(
        id: map['id'] as String,
        name: map['name'] as String,
        email: map['email'] as String,
        phone: map['phone'] as String,
        role: map['role'] as String,
        password: map['password'] as String,
        status: map['status'] as String? ?? "ACTIVE",
        assignedRegionIds: map['assignedRegionIds'] as String? ?? "DELHI_NCR",
        employeeScopeMode: map['employeeScopeMode'] as String? ?? "ALL_IN_REGION",
        assignedEmployeeIds: map['assignedEmployeeIds'] as String? ?? "ALL",
        permissions: map['permissions'] as String? ?? "",
        canCreateEmployees: (map['canCreateEmployees'] == 1 || map['canCreateEmployees'] == true),
        baseSalary: (map['baseSalary'] as num?)?.toDouble() ?? 35000.0,
        fixedAllowance: (map['fixedAllowance'] as num?)?.toDouble() ?? 8000.0,
        travelAllowance: (map['travelAllowance'] as num?)?.toDouble() ?? 5000.0,
        otherAllowance: (map['otherAllowance'] as num?)?.toDouble() ?? 2000.0,
        deductions: (map['deductions'] as num?)?.toDouble() ?? 1500.0,
        monthlyTarget: (map['monthlyTarget'] as num?)?.toDouble() ?? 200000.0,
        reportingAdminId: map['reportingAdminId'] as String? ?? "CO-ADM-101",
        joiningDate: map['joiningDate'] as String? ?? "01 Jan 2025",
        designation: map['designation'] as String? ?? "Medical Representative",
        territoryName: map['territoryName'] as String? ?? "North Delhi - Zone 1",
        createdBy: map['createdBy'] as String? ?? "SYSTEM",
        createdAt: (map['createdAt'] as num?)?.toInt() ?? DateTime.now().millisecondsSinceEpoch,
      );
}

class Region {
  final String id;
  final String name;
  final String state;
  final String code;
  final String headquarters;
  final int activeMRCount;
  final int doctorCount;
  final double monthlyTarget;
  final String status;
  final String createdBy;
  final int createdAt;
  final int updatedAt;

  // Compatibility getters matching Master Governance Sheet
  String get regionId => id;
  String get regionName => name;
  String get regionCode => code;

  const Region({
    required this.id,
    required this.name,
    required this.state,
    required this.code,
    required this.headquarters,
    this.activeMRCount = 12,
    this.doctorCount = 145,
    this.monthlyTarget = 2400000.0,
    this.status = "ACTIVE",
    this.createdBy = "CO-SA-001",
    required this.createdAt,
    int? updatedAt,
  }) : updatedAt = updatedAt ?? createdAt;

  Map<String, dynamic> toMap() => {
        'id': id,
        'name': name,
        'state': state,
        'code': code,
        'headquarters': headquarters,
        'activeMRCount': activeMRCount,
        'doctorCount': doctorCount,
        'monthlyTarget': monthlyTarget,
        'status': status,
        'createdBy': createdBy,
        'createdAt': createdAt,
        'updatedAt': updatedAt,
      };

  factory Region.fromMap(Map<String, dynamic> map) => Region(
        id: map['id'] as String? ?? map['regionId'] as String? ?? "REG-001",
        name: map['name'] as String? ?? map['regionName'] as String? ?? "Delhi NCR",
        state: map['state'] as String? ?? "Delhi",
        code: map['code'] as String? ?? map['regionCode'] as String? ?? "NCR",
        headquarters: map['headquarters'] as String? ?? "Connaught Place, New Delhi",
        activeMRCount: (map['activeMRCount'] as num?)?.toInt() ?? 12,
        doctorCount: (map['doctorCount'] as num?)?.toInt() ?? 145,
        monthlyTarget: (map['monthlyTarget'] as num?)?.toDouble() ?? 2400000.0,
        status: map['status'] as String? ?? "ACTIVE",
        createdBy: map['createdBy'] as String? ?? "CO-SA-001",
        createdAt: (map['createdAt'] as num?)?.toInt() ?? DateTime.now().millisecondsSinceEpoch,
        updatedAt: (map['updatedAt'] as num?)?.toInt() ?? DateTime.now().millisecondsSinceEpoch,
      );
}

class Territory {
  final String territoryId;
  final String territoryName;
  final String regionId;
  final String status;
  final String createdBy;
  final int createdAt;
  final int updatedAt;

  // Compatibility getter
  String get id => territoryId;
  String get name => territoryName;

  const Territory({
    required this.territoryId,
    required this.territoryName,
    required this.regionId,
    this.status = "ACTIVE",
    this.createdBy = "CO-SA-001",
    required this.createdAt,
    int? updatedAt,
  }) : updatedAt = updatedAt ?? createdAt;

  Map<String, dynamic> toMap() => {
        'territoryId': territoryId,
        'territoryName': territoryName,
        'regionId': regionId,
        'status': status,
        'createdBy': createdBy,
        'createdAt': createdAt,
        'updatedAt': updatedAt,
      };

  factory Territory.fromMap(Map<String, dynamic> map) => Territory(
        territoryId: map['territoryId'] as String? ?? map['id'] as String? ?? "TER-001",
        territoryName: map['territoryName'] as String? ?? map['name'] as String? ?? "North Delhi",
        regionId: map['regionId'] as String? ?? "REG-001",
        status: map['status'] as String? ?? "ACTIVE",
        createdBy: map['createdBy'] as String? ?? "CO-SA-001",
        createdAt: (map['createdAt'] as num?)?.toInt() ?? DateTime.now().millisecondsSinceEpoch,
        updatedAt: (map['updatedAt'] as num?)?.toInt() ?? DateTime.now().millisecondsSinceEpoch,
      );
}

class AdminScope {
  final String scopeId;
  final String adminId;
  final String scopeType; // GLOBAL, REGION, TERRITORY, EMPLOYEE
  final String regionId;
  final String territoryId;
  final String employeeId; // Optional specific employee or comma-separated list
  final String permissionSetId;
  final String status; // ACTIVE, INACTIVE
  final String assignedBy;
  final int createdAt;
  final int updatedAt;

  const AdminScope({
    required this.scopeId,
    required this.adminId,
    required this.scopeType,
    required this.regionId,
    this.territoryId = "",
    this.employeeId = "",
    this.permissionSetId = "PERM-001",
    this.status = "ACTIVE",
    this.assignedBy = "CO-SA-001",
    required this.createdAt,
    int? updatedAt,
  }) : updatedAt = updatedAt ?? createdAt;

  Map<String, dynamic> toMap() => {
        'scopeId': scopeId,
        'adminId': adminId,
        'scopeType': scopeType,
        'regionId': regionId,
        'territoryId': territoryId,
        'employeeId': employeeId,
        'permissionSetId': permissionSetId,
        'status': status,
        'assignedBy': assignedBy,
        'createdAt': createdAt,
        'updatedAt': updatedAt,
      };

  factory AdminScope.fromMap(Map<String, dynamic> map) => AdminScope(
        scopeId: map['scopeId'] as String,
        adminId: map['adminId'] as String,
        scopeType: map['scopeType'] as String? ?? "REGION",
        regionId: map['regionId'] as String? ?? "REG-001",
        territoryId: map['territoryId'] as String? ?? "",
        employeeId: map['employeeId'] as String? ?? "",
        permissionSetId: map['permissionSetId'] as String? ?? "PERM-001",
        status: map['status'] as String? ?? "ACTIVE",
        assignedBy: map['assignedBy'] as String? ?? "CO-SA-001",
        createdAt: (map['createdAt'] as num?)?.toInt() ?? DateTime.now().millisecondsSinceEpoch,
        updatedAt: (map['updatedAt'] as num?)?.toInt() ?? DateTime.now().millisecondsSinceEpoch,
      );
}

class PermissionModel {
  final String permissionId; // P-001 to P-014
  final String module; // Employee, Attendance, Doctor Visit, Target, Incentive Rule, Expense, Region, Admin, Permissions
  final String action; // VIEW, EDIT, CREATE, APPROVE, APPROVE/REJECT, CREATE/EDIT
  final String scope; // REGION, GLOBAL, SELF
  final String role; // ADMIN, SUPER_ADMIN, MR
  final String key; // Canonical key like VIEW_EMPLOYEE, APPROVE_EXPENSE

  const PermissionModel({
    required this.permissionId,
    required this.module,
    required this.action,
    required this.scope,
    required this.role,
    required this.key,
  });

  Map<String, dynamic> toMap() => {
        'permissionId': permissionId,
        'module': module,
        'action': action,
        'scope': scope,
        'role': role,
        'key': key,
      };

  factory PermissionModel.fromMap(Map<String, dynamic> map) => PermissionModel(
        permissionId: map['permissionId'] as String,
        module: map['module'] as String,
        action: map['action'] as String,
        scope: map['scope'] as String,
        role: map['role'] as String,
        key: map['key'] as String,
      );
}

class RuleModel {
  final String ruleId; // e.g. RULE-ATT-001, RULE-GPS-001, RULE-INC-001
  final String ruleName;
  final String ruleType; // ATTENDANCE, GPS, DOCTOR_VISIT, TARGET, INCENTIVE, EXPENSE
  final String scope; // GLOBAL, REGION, TERRITORY, EMPLOYEE
  final String scopeId; // e.g. GLOBAL, REG-001, TER-001, CO-MR-8492
  final String priority; // Default, Region, Territory, Employee
  final String conditionsJson;
  final String actionsJson;
  final String effectiveFrom;
  final String effectiveTo;
  final String status; // ACTIVE, INACTIVE
  final int version;
  final String createdBy;
  final String updatedBy;
  final int createdAt;
  final int updatedAt;

  const RuleModel({
    required this.ruleId,
    required this.ruleName,
    required this.ruleType,
    this.scope = "GLOBAL",
    this.scopeId = "GLOBAL",
    this.priority = "Default",
    this.conditionsJson = "{}",
    this.actionsJson = "{}",
    this.effectiveFrom = "01-08-2026",
    this.effectiveTo = "31-12-2026",
    this.status = "ACTIVE",
    this.version = 1,
    this.createdBy = "CO-SA-001",
    this.updatedBy = "CO-SA-001",
    required this.createdAt,
    int? updatedAt,
  }) : updatedAt = updatedAt ?? createdAt;

  Map<String, dynamic> toMap() => {
        'ruleId': ruleId,
        'ruleName': ruleName,
        'ruleType': ruleType,
        'scope': scope,
        'scopeId': scopeId,
        'priority': priority,
        'conditionsJson': conditionsJson,
        'actionsJson': actionsJson,
        'effectiveFrom': effectiveFrom,
        'effectiveTo': effectiveTo,
        'status': status,
        'version': version,
        'createdBy': createdBy,
        'updatedBy': updatedBy,
        'createdAt': createdAt,
        'updatedAt': updatedAt,
      };

  factory RuleModel.fromMap(Map<String, dynamic> map) => RuleModel(
        ruleId: map['ruleId'] as String,
        ruleName: map['ruleName'] as String,
        ruleType: map['ruleType'] as String,
        scope: map['scope'] as String? ?? "GLOBAL",
        scopeId: map['scopeId'] as String? ?? "GLOBAL",
        priority: map['priority'] as String? ?? "Default",
        conditionsJson: map['conditionsJson'] as String? ?? "{}",
        actionsJson: map['actionsJson'] as String? ?? "{}",
        effectiveFrom: map['effectiveFrom'] as String? ?? "01-08-2026",
        effectiveTo: map['effectiveTo'] as String? ?? "31-12-2026",
        status: map['status'] as String? ?? "ACTIVE",
        version: (map['version'] as num?)?.toInt() ?? 1,
        createdBy: map['createdBy'] as String? ?? "CO-SA-001",
        updatedBy: map['updatedBy'] as String? ?? "CO-SA-001",
        createdAt: (map['createdAt'] as num?)?.toInt() ?? DateTime.now().millisecondsSinceEpoch,
        updatedAt: (map['updatedAt'] as num?)?.toInt() ?? DateTime.now().millisecondsSinceEpoch,
      );
}

class IncentiveSlabItem {
  final double minPercent;
  final double maxPercent;
  final double incentiveAmount;
  final String type; // FIXED, PERCENTAGE

  const IncentiveSlabItem({
    required this.minPercent,
    required this.maxPercent,
    required this.incentiveAmount,
    this.type = "FIXED",
  });

  Map<String, dynamic> toMap() => {
        'minPercent': minPercent,
        'maxPercent': maxPercent,
        'incentiveAmount': incentiveAmount,
        'type': type,
      };

  factory IncentiveSlabItem.fromMap(Map<String, dynamic> map) => IncentiveSlabItem(
        minPercent: (map['minPercent'] as num).toDouble(),
        maxPercent: (map['maxPercent'] as num).toDouble(),
        incentiveAmount: (map['incentiveAmount'] as num).toDouble(),
        type: map['type'] as String? ?? "FIXED",
      );
}

class ApprovalRequest {
  final String approvalId; // APR-001 to APR-005
  final String module; // ATTENDANCE, DOCTOR_VISIT, EXPENSE, TARGET, INCENTIVE, ORDER
  final String entityId;
  final String title;
  final String details;
  final String submittedBy;
  final String submittedByName;
  final String approverId;
  final String approverRole; // ADMIN, SUPER_ADMIN
  final String scope; // Assigned scope / GLOBAL
  final String status; // PENDING, APPROVED, REJECTED
  final String comment;
  final String sla; // 24h, 48h, 72h
  final int createdAt;
  final int? reviewedAt;

  const ApprovalRequest({
    required this.approvalId,
    required this.module,
    required this.entityId,
    required this.title,
    required this.details,
    required this.submittedBy,
    required this.submittedByName,
    this.approverId = "CO-ADM-101",
    this.approverRole = "ADMIN",
    this.scope = "REG-001",
    this.status = "PENDING",
    this.comment = "",
    this.sla = "24h",
    required this.createdAt,
    this.reviewedAt,
  });

  Map<String, dynamic> toMap() => {
        'approvalId': approvalId,
        'module': module,
        'entityId': entityId,
        'title': title,
        'details': details,
        'submittedBy': submittedBy,
        'submittedByName': submittedByName,
        'approverId': approverId,
        'approverRole': approverRole,
        'scope': scope,
        'status': status,
        'comment': comment,
        'sla': sla,
        'createdAt': createdAt,
        'reviewedAt': reviewedAt,
      };

  factory ApprovalRequest.fromMap(Map<String, dynamic> map) => ApprovalRequest(
        approvalId: map['approvalId'] as String,
        module: map['module'] as String,
        entityId: map['entityId'] as String,
        title: map['title'] as String,
        details: map['details'] as String,
        submittedBy: map['submittedBy'] as String,
        submittedByName: map['submittedByName'] as String,
        approverId: map['approverId'] as String? ?? "CO-ADM-101",
        approverRole: map['approverRole'] as String? ?? "ADMIN",
        scope: map['scope'] as String? ?? "REG-001",
        status: map['status'] as String? ?? "PENDING",
        comment: map['comment'] as String? ?? "",
        sla: map['sla'] as String? ?? "24h",
        createdAt: (map['createdAt'] as num?)?.toInt() ?? DateTime.now().millisecondsSinceEpoch,
        reviewedAt: (map['reviewedAt'] as num?)?.toInt(),
      );
}

class SalaryRule {
  final String id;
  final String ruleName;
  final double baseSalary;
  final double fixedAllowance;
  final double travelAllowancePerKm;
  final double dailyAllowancePerDay;
  final double performanceBonusMax;
  final double deductionPfPercent;
  final String regionId;
  final int versionNumber;
  final String effectiveFrom;
  final String effectiveTo;
  final String status;
  final String updatedBy;
  final int updatedAt;

  const SalaryRule({
    required this.id,
    required this.ruleName,
    this.baseSalary = 35000.0,
    this.fixedAllowance = 8000.0,
    this.travelAllowancePerKm = 4.5,
    this.dailyAllowancePerDay = 350.0,
    this.performanceBonusMax = 15000.0,
    this.deductionPfPercent = 12.0,
    this.regionId = "GLOBAL",
    this.versionNumber = 1,
    this.effectiveFrom = "01-08-2026",
    this.effectiveTo = "31-12-2026",
    this.status = "ACTIVE",
    this.updatedBy = "CO-SA-001",
    required this.updatedAt,
  });

  Map<String, dynamic> toMap() => {
        'id': id,
        'ruleName': ruleName,
        'baseSalary': baseSalary,
        'fixedAllowance': fixedAllowance,
        'travelAllowancePerKm': travelAllowancePerKm,
        'dailyAllowancePerDay': dailyAllowancePerDay,
        'performanceBonusMax': performanceBonusMax,
        'deductionPfPercent': deductionPfPercent,
        'regionId': regionId,
        'versionNumber': versionNumber,
        'effectiveFrom': effectiveFrom,
        'effectiveTo': effectiveTo,
        'status': status,
        'updatedBy': updatedBy,
        'updatedAt': updatedAt,
      };

  factory SalaryRule.fromMap(Map<String, dynamic> map) => SalaryRule(
        id: map['id'] as String,
        ruleName: map['ruleName'] as String,
        baseSalary: (map['baseSalary'] as num?)?.toDouble() ?? 35000.0,
        fixedAllowance: (map['fixedAllowance'] as num?)?.toDouble() ?? 8000.0,
        travelAllowancePerKm: (map['travelAllowancePerKm'] as num?)?.toDouble() ?? 4.5,
        dailyAllowancePerDay: (map['dailyAllowancePerDay'] as num?)?.toDouble() ?? 350.0,
        performanceBonusMax: (map['performanceBonusMax'] as num?)?.toDouble() ?? 15000.0,
        deductionPfPercent: (map['deductionPfPercent'] as num?)?.toDouble() ?? 12.0,
        regionId: map['regionId'] as String? ?? "GLOBAL",
        versionNumber: (map['versionNumber'] as num?)?.toInt() ?? 1,
        effectiveFrom: map['effectiveFrom'] as String? ?? "01-08-2026",
        effectiveTo: map['effectiveTo'] as String? ?? "31-12-2026",
        status: map['status'] as String? ?? "ACTIVE",
        updatedBy: map['updatedBy'] as String? ?? "CO-SA-001",
        updatedAt: (map['updatedAt'] as num?)?.toInt() ?? DateTime.now().millisecondsSinceEpoch,
      );
}

class AuditLog {
  final int? id;
  final String auditId; // AUD-001
  final String userId;
  final String userName;
  final String userRole;
  final String action;
  final String targetEntity;
  final String entityId;
  final String oldValue;
  final String newValue;
  final int timestamp;
  final String formattedDate;

  // Compatibility getters
  String get actorId => userId;
  String get actorRole => userRole;
  String get entityType => targetEntity;

  const AuditLog({
    this.id,
    String? auditId,
    required this.userId,
    required this.userName,
    required this.userRole,
    required this.action,
    required this.targetEntity,
    this.entityId = "",
    this.oldValue = "",
    this.newValue = "",
    required this.timestamp,
    required this.formattedDate,
  }) : auditId = auditId ?? "AUD-${timestamp}";

  Map<String, dynamic> toMap() => {
        'id': id,
        'auditId': auditId,
        'userId': userId,
        'userName': userName,
        'userRole': userRole,
        'action': action,
        'targetEntity': targetEntity,
        'entityId': entityId,
        'oldValue': oldValue,
        'newValue': newValue,
        'timestamp': timestamp,
        'formattedDate': formattedDate,
      };

  factory AuditLog.fromMap(Map<String, dynamic> map) => AuditLog(
        id: (map['id'] as num?)?.toInt(),
        auditId: map['auditId'] as String?,
        userId: map['userId'] as String? ?? map['actorId'] as String? ?? "CO-SA-001",
        userName: map['userName'] as String? ?? "Administrator",
        userRole: map['userRole'] as String? ?? map['actorRole'] as String? ?? "ADMIN",
        action: map['action'] as String,
        targetEntity: map['targetEntity'] as String? ?? map['entityType'] as String? ?? "SYSTEM",
        entityId: map['entityId'] as String? ?? "",
        oldValue: map['oldValue'] as String? ?? "",
        newValue: map['newValue'] as String? ?? "",
        timestamp: (map['timestamp'] as num?)?.toInt() ?? DateTime.now().millisecondsSinceEpoch,
        formattedDate: map['formattedDate'] as String? ?? "",
      );
}

class IncentiveRecord {
  final String id;
  final String employeeId;
  final String employeeName;
  final String period;
  final double target;
  final double actualSales;
  final double achievementPercent;
  final String ruleId;
  final String ruleName;
  final int ruleVersion;
  final String ruleType;
  final String applicableSlab;
  final double incentiveRate;
  final double baseIncentive;
  final double coverageIncentive;
  final double newDoctorIncentive;
  final double collectionIncentive;
  final double additionalIncentives;
  final double deductions;
  final double finalIncentive;
  final String status;
  final String breakdownJson;
  final int calculatedAt;
  final int? approvedAt;
  final String? approvedBy;

  const IncentiveRecord({
    required this.id,
    required this.employeeId,
    required this.employeeName,
    required this.period,
    required this.target,
    required this.actualSales,
    required this.achievementPercent,
    required this.ruleId,
    required this.ruleName,
    required this.ruleVersion,
    required this.ruleType,
    required this.applicableSlab,
    required this.incentiveRate,
    required this.baseIncentive,
    this.coverageIncentive = 0.0,
    this.newDoctorIncentive = 0.0,
    this.collectionIncentive = 0.0,
    this.additionalIncentives = 0.0,
    this.deductions = 0.0,
    required this.finalIncentive,
    this.status = "ESTIMATED",
    this.breakdownJson = "",
    required this.calculatedAt,
    this.approvedAt,
    this.approvedBy,
  });

  Map<String, dynamic> toMap() => {
        'id': id,
        'employeeId': employeeId,
        'employeeName': employeeName,
        'period': period,
        'target': target,
        'actualSales': actualSales,
        'achievementPercent': achievementPercent,
        'ruleId': ruleId,
        'ruleName': ruleName,
        'ruleVersion': ruleVersion,
        'ruleType': ruleType,
        'applicableSlab': applicableSlab,
        'incentiveRate': incentiveRate,
        'baseIncentive': baseIncentive,
        'coverageIncentive': coverageIncentive,
        'newDoctorIncentive': newDoctorIncentive,
        'collectionIncentive': collectionIncentive,
        'additionalIncentives': additionalIncentives,
        'deductions': deductions,
        'finalIncentive': finalIncentive,
        'status': status,
        'breakdownJson': breakdownJson,
        'calculatedAt': calculatedAt,
        'approvedAt': approvedAt,
        'approvedBy': approvedBy,
      };

  factory IncentiveRecord.fromMap(Map<String, dynamic> map) => IncentiveRecord(
        id: map['id'] as String,
        employeeId: map['employeeId'] as String,
        employeeName: map['employeeName'] as String,
        period: map['period'] as String,
        target: (map['target'] as num?)?.toDouble() ?? 0.0,
        actualSales: (map['actualSales'] as num?)?.toDouble() ?? 0.0,
        achievementPercent: (map['achievementPercent'] as num?)?.toDouble() ?? 0.0,
        ruleId: map['ruleId'] as String,
        ruleName: map['ruleName'] as String,
        ruleVersion: (map['ruleVersion'] as num?)?.toInt() ?? 1,
        ruleType: map['ruleType'] as String,
        applicableSlab: map['applicableSlab'] as String,
        incentiveRate: (map['incentiveRate'] as num?)?.toDouble() ?? 0.0,
        baseIncentive: (map['baseIncentive'] as num?)?.toDouble() ?? 0.0,
        coverageIncentive: (map['coverageIncentive'] as num?)?.toDouble() ?? 0.0,
        newDoctorIncentive: (map['newDoctorIncentive'] as num?)?.toDouble() ?? 0.0,
        collectionIncentive: (map['collectionIncentive'] as num?)?.toDouble() ?? 0.0,
        additionalIncentives: (map['additionalIncentives'] as num?)?.toDouble() ?? 0.0,
        deductions: (map['deductions'] as num?)?.toDouble() ?? 0.0,
        finalIncentive: (map['finalIncentive'] as num?)?.toDouble() ?? 0.0,
        status: map['status'] as String? ?? "ESTIMATED",
        breakdownJson: map['breakdownJson'] as String? ?? "",
        calculatedAt: (map['calculatedAt'] as num?)?.toInt() ?? DateTime.now().millisecondsSinceEpoch,
        approvedAt: (map['approvedAt'] as num?)?.toInt(),
        approvedBy: map['approvedBy'] as String?,
      );
}
