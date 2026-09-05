import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:supabase_flutter/supabase_flutter.dart';
import '../../data/local/db/careosis_database.dart';
import '../../data/local/entities/admin_and_security_entities.dart';
import '../../data/local/entities/doctor_and_mr_entities.dart';
import 'supabase_sync_service.dart';

/// Core Authentication & Session Management Service
class AuthService {
  static const String _prefKeyUserId = "careosis_auth_user_id";
  static const String _prefKeyUserData = "careosis_auth_user_data";

  final CareOsisDatabase database;
  AuthService(this.database);

  /// Authenticate with Employee ID or Email and Password
  Future<UserAccount?> signIn(String identifier, String password) async {
    final cleanId = identifier.trim();
    final cleanPass = password.trim();
    if (cleanId.isEmpty || cleanPass.isEmpty) return null;

    UserAccount? authenticatedUser;

    // 1. Try Supabase Auth if online and identifier is an email
    final supabase = SupabaseSyncService.instance.client;
    if (SupabaseSyncService.instance.isInitialized && supabase != null) {
      if (cleanId.contains('@')) {
        try {
          final res = await supabase.auth.signInWithPassword(
            email: cleanId,
            password: cleanPass,
          );
          if (res.user != null) {
            final u = res.user!;
            final meta = u.userMetadata ?? {};
            authenticatedUser = UserAccount(
              id: meta['employee_code'] ?? u.id.substring(0, 10),
              name: meta['name'] ?? meta['full_name'] ?? cleanId.split('@').first,
              email: cleanId,
              phone: u.phone ?? "",
              role: meta['role'] ?? "EMPLOYEE",
              password: cleanPass,
              status: "ACTIVE",
              assignedRegionIds: meta['region_id'] ?? "REG-001",
              employeeScopeMode: "ALL_IN_REGION",
              assignedEmployeeIds: "",
              permissions: "ALL",
              canCreateEmployees: meta['role'] == "SUPER_ADMIN" || meta['role'] == "ADMIN",
              baseSalary: 35000.0,
              fixedAllowance: 8000.0,
              travelAllowance: 6000.0,
              otherAllowance: 2000.0,
              deductions: 1500.0,
              monthlyTarget: 200000.0,
              reportingAdminId: "CO-ADM-101",
              joiningDate: DateTime.now().toIso8601String().substring(0, 10),
              designation: meta['designation'] ?? "Medical Representative",
              territoryName: meta['territory'] ?? "Assigned Territory",
              createdBy: "SUPABASE_AUTH",
              createdAt: DateTime.now().millisecondsSinceEpoch,
            );
            await database.adminAndSecurityDao.insertUser(authenticatedUser);
          }
        } catch (e) {
          debugPrint("Supabase email login notice: $e");
        }
      }
    }

    // 2. Check local database credentials (or fallback if offline)
    if (authenticatedUser == null) {
      authenticatedUser = database.adminAndSecurityDao.authenticateUser(cleanId, cleanPass);
      
      // If user matched by email instead of ID
      if (authenticatedUser == null && cleanId.contains('@')) {
        final allUsers = database.userAccounts.values;
        for (final u in allUsers) {
          if (u.email.toLowerCase() == cleanId.toLowerCase() && u.password == cleanPass) {
            authenticatedUser = u;
            break;
          }
        }
      }
    }

    // 3. If authenticated, persist session and initialize profile
    if (authenticatedUser != null) {
      await _persistSession(authenticatedUser);
      await _ensureProfileForUser(authenticatedUser);
      return authenticatedUser;
    }

    return null;
  }

  /// Register a new Field Representative
  Future<UserAccount?> signUp({
    required String email,
    required String password,
    required String fullName,
    required String hqTerritory,
    String phone = "",
    String role = "EMPLOYEE",
  }) async {
    final cleanEmail = email.trim();
    final cleanPass = password.trim();
    final cleanName = fullName.trim();
    final cleanTerritory = hqTerritory.trim();

    if (cleanEmail.isEmpty || cleanPass.isEmpty || cleanName.isEmpty) return null;

    final empId = "CO-MR-${(DateTime.now().millisecondsSinceEpoch % 9000) + 1000}";

    // 1. Register with Supabase if online
    final supabase = SupabaseSyncService.instance.client;
    if (SupabaseSyncService.instance.isInitialized && supabase != null) {
      try {
        await supabase.auth.signUp(
          email: cleanEmail,
          password: cleanPass,
          data: {
            'employee_code': empId,
            'name': cleanName,
            'role': role,
            'territory': cleanTerritory,
          },
        );

        // Upsert into Supabase mr_profiles table
        await supabase.from('mr_profiles').upsert({
          'id': empId,
          'employee_code': empId,
          'full_name': cleanName,
          'email': cleanEmail,
          'phone': phone,
          'hq_territory': cleanTerritory,
          'role': role,
        });
      } catch (e) {
        debugPrint("Notice: Supabase signUp sync: $e");
      }
    }

    // 2. Create UserAccount
    final user = UserAccount(
      id: empId,
      name: cleanName,
      email: cleanEmail,
      phone: phone,
      role: role,
      password: cleanPass,
      status: "ACTIVE",
      assignedRegionIds: "REG-001",
      employeeScopeMode: "ALL_IN_REGION",
      assignedEmployeeIds: "",
      permissions: "ALL",
      canCreateEmployees: role == "SUPER_ADMIN" || role == "ADMIN",
      baseSalary: 35000.0,
      fixedAllowance: 8000.0,
      travelAllowance: 6000.0,
      otherAllowance: 2000.0,
      deductions: 1500.0,
      monthlyTarget: 200000.0,
      reportingAdminId: "CO-ADM-101",
      joiningDate: DateTime.now().toIso8601String().substring(0, 10),
      designation: "Medical Representative",
      territoryName: cleanTerritory,
      createdBy: "SELF_REGISTER",
      createdAt: DateTime.now().millisecondsSinceEpoch,
    );

    await database.adminAndSecurityDao.insertUser(user);
    await _ensureProfileForUser(user);
    await _persistSession(user);

    return user;
  }

  /// Restore saved session from SharedPreferences
  Future<UserAccount?> restoreSession() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final userId = prefs.getString(_prefKeyUserId);
      if (userId == null || userId.isEmpty) return null;

      // Check in local DAO
      var user = database.adminAndSecurityDao.getUserById(userId);
      if (user != null) return user;

      // Check cached JSON
      final rawJson = prefs.getString(_prefKeyUserData);
      if (rawJson != null) {
        final Map<String, dynamic> data = jsonDecode(rawJson);
        user = UserAccount(
          id: data['id'] ?? userId,
          name: data['name'] ?? "Representative",
          email: data['email'] ?? "",
          phone: data['phone'] ?? "",
          role: data['role'] ?? "EMPLOYEE",
          password: "",
          status: "ACTIVE",
          assignedRegionIds: data['assignedRegionIds'] ?? "REG-001",
          employeeScopeMode: "ALL_IN_REGION",
          assignedEmployeeIds: "",
          permissions: "ALL",
          canCreateEmployees: false,
          baseSalary: 35000.0,
          fixedAllowance: 8000.0,
          travelAllowance: 6000.0,
          otherAllowance: 2000.0,
          deductions: 1500.0,
          monthlyTarget: (data['monthlyTarget'] as num?)?.toDouble() ?? 200000.0,
          reportingAdminId: "",
          joiningDate: "",
          designation: data['designation'] ?? "Medical Representative",
          territoryName: data['territoryName'] ?? "Territory",
          createdBy: "SESSION_RESTORE",
          createdAt: DateTime.now().millisecondsSinceEpoch,
        );
        await database.adminAndSecurityDao.insertUser(user);
        await _ensureProfileForUser(user);
        return user;
      }
    } catch (e) {
      debugPrint("Session restore notice: $e");
    }
    return null;
  }

  /// Clear session
  Future<void> signOut() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      await prefs.remove(_prefKeyUserId);
      await prefs.remove(_prefKeyUserData);

      if (SupabaseSyncService.instance.isInitialized) {
        await SupabaseSyncService.instance.client?.auth.signOut();
      }
    } catch (e) {
      debugPrint("Sign out notice: $e");
    }
  }

  Future<void> _persistSession(UserAccount user) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      await prefs.setString(_prefKeyUserId, user.id);
      final jsonStr = jsonEncode({
        'id': user.id,
        'name': user.name,
        'email': user.email,
        'phone': user.phone,
        'role': user.role,
        'territoryName': user.territoryName,
        'designation': user.designation,
        'monthlyTarget': user.monthlyTarget,
        'assignedRegionIds': user.assignedRegionIds,
      });
      await prefs.setString(_prefKeyUserData, jsonStr);
    } catch (e) {
      debugPrint("Persist session notice: $e");
    }
  }

  Future<void> _ensureProfileForUser(UserAccount user) async {
    final existing = database.mrProfileDao.getProfileSync();
    if (existing == null || existing.empId != user.id) {
      final freshProfile = MRProfile(
        empId: user.id,
        name: user.name,
        phone: user.phone,
        email: user.email,
        territory: user.territoryName.isNotEmpty ? user.territoryName : "Field Territory",
        managerName: "Regional Operations",
        joiningDate: user.joiningDate.isNotEmpty ? user.joiningDate : DateTime.now().toIso8601String().substring(0, 10),
        designation: user.designation.isNotEmpty ? user.designation : "Medical Representative",
        level: "L1",
        trainingProgressPercent: 0,
        monthlyTarget: user.monthlyTarget > 0 ? user.monthlyTarget : 200000.0,
        monthlySales: 0.0,
        currentIncentive: 0.0,
        completedVisitsToday: 0,
        targetVisitsToday: 15,
        isCheckedInToday: false,
      );
      await database.mrProfileDao.insertProfile(freshProfile);
    }
  }
}
