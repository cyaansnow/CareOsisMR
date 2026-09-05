import '../../data/local/entities/admin_and_security_entities.dart';

class RBACService {
  static bool isSuperAdmin(UserAccount? user) {
    return user?.role == "SUPER_ADMIN";
  }

  static bool isAdmin(UserAccount? user) {
    return user?.role == "ADMIN" || user?.role == "SUPER_ADMIN";
  }

  static bool isEmployee(UserAccount? user) {
    return user?.role == "EMPLOYEE";
  }

  static bool hasPermission(UserAccount? user, String permissionKey) {
    if (user == null) return false;
    if (user.role == "SUPER_ADMIN") return true;
    if (user.permissions.contains("ALL")) return true;
    final perms = user.permissions.split(",").map((s) => s.trim()).toList();
    return perms.contains(permissionKey);
  }

  static bool canManageRegion(UserAccount? user, String regionId) {
    if (user == null) return false;
    if (user.role == "SUPER_ADMIN") return true;
    final regions = user.assignedRegionIds.split(",").map((s) => s.trim()).toList();
    return regions.contains(regionId) || regions.contains("GLOBAL");
  }

  static bool canAccessEmployeeData(UserAccount? user, String employeeId, String employeeRegionId) {
    if (user == null) return false;
    if (user.role == "SUPER_ADMIN") return true;
    if (user.role == "EMPLOYEE") return user.id == employeeId;

    // Admin scope check
    if (user.employeeScopeMode == "SPECIFIC_EMPLOYEES") {
      final empIds = user.assignedEmployeeIds.split(",").map((s) => s.trim()).toList();
      return empIds.contains(employeeId) || empIds.contains("ALL");
    } else {
      // ALL_IN_REGION
      return canManageRegion(user, employeeRegionId);
    }
  }
}
