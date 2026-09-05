import '../../data/local/entities/admin_and_security_entities.dart';

class Permissions {
  static const String viewEmployee = "VIEW_EMPLOYEE";
  static const String createEmployee = "CREATE_EMPLOYEE";
  static const String editEmployee = "EDIT_EMPLOYEE";
  static const String deactivateEmployee = "DEACTIVATE_EMPLOYEE";

  static const String viewAttendance = "VIEW_ATTENDANCE";
  static const String approveAttendance = "APPROVE_ATTENDANCE";
  static const String rejectAttendance = "REJECT_ATTENDANCE";

  static const String viewGps = "VIEW_GPS";
  static const String viewDoctorVisits = "VIEW_DOCTOR_VISITS";
  static const String approveDoctorVisit = "APPROVE_DOCTOR_VISIT";
  static const String rejectDoctorVisit = "REJECT_DOCTOR_VISIT";

  static const String viewTarget = "VIEW_TARGET";
  static const String createTarget = "CREATE_TARGET";
  static const String editTarget = "EDIT_TARGET";

  static const String viewIncentive = "VIEW_INCENTIVE";
  static const String createIncentiveRule = "CREATE_INCENTIVE_RULE";
  static const String editIncentiveRule = "EDIT_INCENTIVE_RULE";
  static const String finalizeIncentive = "FINALIZE_INCENTIVE";

  static const String viewExpense = "VIEW_EXPENSE";
  static const String approveExpense = "APPROVE_EXPENSE";
  static const String rejectExpense = "REJECT_EXPENSE";

  static const String viewReports = "VIEW_REPORTS";
  static const String exportReports = "EXPORT_REPORTS";

  static const String manageRegion = "MANAGE_REGION";
  static const String manageTerritory = "MANAGE_TERRITORY";
  static const String manageAdmin = "MANAGE_ADMIN";
  static const String managePermissions = "MANAGE_PERMISSIONS";
}

class AccessControlService {
  /// Evaluates Role + Permission + Scope = Access
  static bool canAccess({
    required UserAccount? user,
    required String permission,
    String scopeType = "REGION",
    String? regionId,
    String? territoryId,
    String? employeeId,
    List<AdminScope> adminScopes = const [],
  }) {
    if (user == null) return false;

    // 1. SUPER_ADMIN has global authority for all operations
    if (user.role == "SUPER_ADMIN") return true;

    // 2. MR / Field Employee: restricted to own data with SELF scope
    if (user.role == "EMPLOYEE") {
      if (permission == Permissions.viewEmployee ||
          permission == Permissions.viewAttendance ||
          permission == Permissions.viewDoctorVisits ||
          permission == Permissions.viewExpense ||
          permission == Permissions.viewTarget ||
          permission == Permissions.viewIncentive) {
        return employeeId == null || employeeId == user.id;
      }
      return false;
    }

    // 3. ADMIN: must possess the required permission
    if (user.role == "ADMIN") {
      final hasPerm = _hasPermission(user, permission);
      if (!hasPerm) return false;

      // Check admin scopes if configured explicitly
      if (adminScopes.isNotEmpty) {
        final activeScopes = adminScopes.where((s) => s.adminId == user.id && s.status == "ACTIVE").toList();
        if (activeScopes.isNotEmpty) {
          for (final scope in activeScopes) {
            if (scope.scopeType == "GLOBAL") return true;
            if (scope.scopeType == "REGION" && (regionId == null || scope.regionId == regionId)) {
              return true;
            }
            if (scope.scopeType == "TERRITORY" && (territoryId == null || scope.territoryId == territoryId)) {
              return true;
            }
            if (scope.scopeType == "EMPLOYEE") {
              final emps = scope.employeeId.split(",").map((s) => s.trim()).toList();
              if (employeeId != null && (emps.contains(employeeId) || emps.contains("ALL"))) {
                return true;
              }
            }
          }
          return false;
        }
      }

      // Default Admin scoping checks via UserAccount fields
      if (employeeId != null) {
        if (user.employeeScopeMode == "SPECIFIC_EMPLOYEES") {
          final allowedEmployees = user.assignedEmployeeIds.split(",").map((s) => s.trim()).toList();
          if (!allowedEmployees.contains(employeeId) && !allowedEmployees.contains("ALL")) {
            return false;
          }
        }
      }

      if (territoryId != null && user.assignedTerritoryIds.isNotEmpty && user.assignedTerritoryIds != "ALL") {
        final allowedTerritories = user.assignedTerritoryIds.split(",").map((s) => s.trim()).toList();
        if (!allowedTerritories.contains(territoryId) && !allowedTerritories.contains("ALL")) {
          return false;
        }
      }

      if (regionId != null && user.assignedRegionIds.isNotEmpty && user.assignedRegionIds != "GLOBAL") {
        final allowedRegions = user.assignedRegionIds.split(",").map((s) => s.trim()).toList();
        if (!allowedRegions.contains(regionId) && !allowedRegions.contains("GLOBAL")) {
          return false;
        }
      }

      return true;
    }

    return false;
  }

  static bool _hasPermission(UserAccount user, String permKey) {
    if (user.role == "SUPER_ADMIN") return true;
    if (user.permissions.contains("ALL")) return true;
    final perms = user.permissions.split(",").map((s) => s.trim()).toList();
    return perms.contains(permKey);
  }

  /// Convenience helper for expense approval check
  static bool canApproveExpense(UserAccount? user, {String? expenseEmployeeId, String? regionId}) {
    return canAccess(
      user: user,
      permission: Permissions.approveExpense,
      employeeId: expenseEmployeeId,
      regionId: regionId,
    );
  }

  /// Convenience helper for attendance approval check
  static bool canApproveAttendance(UserAccount? user, {String? attendanceEmployeeId, String? regionId}) {
    return canAccess(
      user: user,
      permission: Permissions.approveAttendance,
      employeeId: attendanceEmployeeId,
      regionId: regionId,
    );
  }

  /// Convenience helper for doctor visit approval check
  static bool canApproveDoctorVisit(UserAccount? user, {String? visitEmployeeId, String? regionId}) {
    return canAccess(
      user: user,
      permission: Permissions.approveDoctorVisit,
      employeeId: visitEmployeeId,
      regionId: regionId,
    );
  }

  /// Convenience helper for managing targets
  static bool canManageTarget(UserAccount? user, {String? regionId, String? territoryId, String? employeeId}) {
    return canAccess(
      user: user,
      permission: Permissions.createTarget,
      regionId: regionId,
      territoryId: territoryId,
      employeeId: employeeId,
    );
  }

  /// Convenience helper for managing incentive rules
  static bool canManageIncentiveRules(UserAccount? user, {String? regionId}) {
    return canAccess(
      user: user,
      permission: Permissions.createIncentiveRule,
      regionId: regionId,
    );
  }

  /// Convenience helper for organization management (Super Admin only)
  static bool canManageOrganization(UserAccount? user) {
    return canAccess(
      user: user,
      permission: Permissions.manageRegion,
      scopeType: "GLOBAL",
    );
  }
}
