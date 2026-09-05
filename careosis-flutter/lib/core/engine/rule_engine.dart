import 'dart:convert';
import 'dart:math' as math;
import '../../data/local/entities/admin_and_security_entities.dart';

class AttendanceRuleConfig {
  final String checkInStartTime; // "09:00"
  final int gracePeriodMinutes; // 15
  final String lateThresholdTime; // "09:15"
  final double halfDayHoursThreshold; // 4.0
  final double minWorkingHours; // 8.0
  final bool gpsRequired; // true
  final bool approvalRequiredForExceptions; // true

  const AttendanceRuleConfig({
    this.checkInStartTime = "09:00",
    this.gracePeriodMinutes = 15,
    this.lateThresholdTime = "09:15",
    this.halfDayHoursThreshold = 4.0,
    this.minWorkingHours = 8.0,
    this.gpsRequired = true,
    this.approvalRequiredForExceptions = true,
  });

  factory AttendanceRuleConfig.fromJson(Map<String, dynamic> json) => AttendanceRuleConfig(
        checkInStartTime: json['checkInStartTime'] as String? ?? "09:00",
        gracePeriodMinutes: (json['gracePeriodMinutes'] as num?)?.toInt() ?? 15,
        lateThresholdTime: json['lateThresholdTime'] as String? ?? "09:15",
        halfDayHoursThreshold: (json['halfDayHoursThreshold'] as num?)?.toDouble() ?? 4.0,
        minWorkingHours: (json['minWorkingHours'] as num?)?.toDouble() ?? 8.0,
        gpsRequired: json['gpsRequired'] as bool? ?? true,
        approvalRequiredForExceptions: json['approvalRequiredForExceptions'] as bool? ?? true,
      );

  Map<String, dynamic> toJson() => {
        'checkInStartTime': checkInStartTime,
        'gracePeriodMinutes': gracePeriodMinutes,
        'lateThresholdTime': lateThresholdTime,
        'halfDayHoursThreshold': halfDayHoursThreshold,
        'minWorkingHours': minWorkingHours,
        'gpsRequired': gpsRequired,
        'approvalRequiredForExceptions': approvalRequiredForExceptions,
      };
}

class GpsRuleConfig {
  final bool gpsRequired; // true
  final double maxAccuracyMeters; // 100.0
  final bool mockLocationDetection; // true
  final double doctorVisitRadiusMeters; // 500.0
  final int trackingFrequencyMinutes; // 15

  const GpsRuleConfig({
    this.gpsRequired = true,
    this.maxAccuracyMeters = 100.0,
    this.mockLocationDetection = true,
    this.doctorVisitRadiusMeters = 500.0,
    this.trackingFrequencyMinutes = 15,
  });

  factory GpsRuleConfig.fromJson(Map<String, dynamic> json) => GpsRuleConfig(
        gpsRequired: json['gpsRequired'] as bool? ?? true,
        maxAccuracyMeters: (json['maxAccuracyMeters'] as num?)?.toDouble() ?? 100.0,
        mockLocationDetection: json['mockLocationDetection'] as bool? ?? true,
        doctorVisitRadiusMeters: (json['doctorVisitRadiusMeters'] as num?)?.toDouble() ?? 500.0,
        trackingFrequencyMinutes: (json['trackingFrequencyMinutes'] as num?)?.toInt() ?? 15,
      );

  Map<String, dynamic> toJson() => {
        'gpsRequired': gpsRequired,
        'maxAccuracyMeters': maxAccuracyMeters,
        'mockLocationDetection': mockLocationDetection,
        'doctorVisitRadiusMeters': doctorVisitRadiusMeters,
        'trackingFrequencyMinutes': trackingFrequencyMinutes,
      };
}

class DoctorVisitRuleConfig {
  final int minVisitDurationMinutes; // 5
  final bool gpsRequired; // true
  final double maxDistanceMeters; // 500.0
  final bool notesRequired; // true
  final bool followUpRequired; // false
  final bool approvalRequiredForExceptions; // true

  const DoctorVisitRuleConfig({
    this.minVisitDurationMinutes = 5,
    this.gpsRequired = true,
    this.maxDistanceMeters = 500.0,
    this.notesRequired = true,
    this.followUpRequired = false,
    this.approvalRequiredForExceptions = true,
  });

  factory DoctorVisitRuleConfig.fromJson(Map<String, dynamic> json) => DoctorVisitRuleConfig(
        minVisitDurationMinutes: (json['minVisitDurationMinutes'] as num?)?.toInt() ?? 5,
        gpsRequired: json['gpsRequired'] as bool? ?? true,
        maxDistanceMeters: (json['maxDistanceMeters'] as num?)?.toDouble() ?? 500.0,
        notesRequired: json['notesRequired'] as bool? ?? true,
        followUpRequired: json['followUpRequired'] as bool? ?? false,
        approvalRequiredForExceptions: json['approvalRequiredForExceptions'] as bool? ?? true,
      );

  Map<String, dynamic> toJson() => {
        'minVisitDurationMinutes': minVisitDurationMinutes,
        'gpsRequired': gpsRequired,
        'maxDistanceMeters': maxDistanceMeters,
        'notesRequired': notesRequired,
        'followUpRequired': followUpRequired,
        'approvalRequiredForExceptions': approvalRequiredForExceptions,
      };
}

class ExpenseRuleConfig {
  final double dailyLimit; // 1500.0
  final double monthlyLimit; // 25000.0
  final double receiptRequiredThreshold; // 200.0
  final bool approvalRequired; // true
  final Map<String, double> categoryLimits;

  const ExpenseRuleConfig({
    this.dailyLimit = 1500.0,
    this.monthlyLimit = 25000.0,
    this.receiptRequiredThreshold = 200.0,
    this.approvalRequired = true,
    this.categoryLimits = const {
      "Travel / Fuel": 1200.0,
      "Doctor Engagement": 1500.0,
      "Daily Allowance": 400.0,
      "Hotel & Lodging": 3500.0,
      "Stationery & Samples": 500.0,
      "Miscellaneous": 500.0,
    },
  });

  factory ExpenseRuleConfig.fromJson(Map<String, dynamic> json) => ExpenseRuleConfig(
        dailyLimit: (json['dailyLimit'] as num?)?.toDouble() ?? 1500.0,
        monthlyLimit: (json['monthlyLimit'] as num?)?.toDouble() ?? 25000.0,
        receiptRequiredThreshold: (json['receiptRequiredThreshold'] as num?)?.toDouble() ?? 200.0,
        approvalRequired: json['approvalRequired'] as bool? ?? true,
        categoryLimits: (json['categoryLimits'] as Map<String, dynamic>?)?.map(
              (k, v) => MapEntry(k, (v as num).toDouble()),
            ) ??
            const {
              "Travel / Fuel": 1200.0,
              "Doctor Engagement": 1500.0,
              "Daily Allowance": 400.0,
              "Hotel & Lodging": 3500.0,
              "Stationery & Samples": 500.0,
              "Miscellaneous": 500.0,
            },
      );

  Map<String, dynamic> toJson() => {
        'dailyLimit': dailyLimit,
        'monthlyLimit': monthlyLimit,
        'receiptRequiredThreshold': receiptRequiredThreshold,
        'approvalRequired': approvalRequired,
        'categoryLimits': categoryLimits,
      };
}

class AttendanceEvaluationResult {
  final bool isLate;
  final bool isGpsValid;
  final bool requiresApproval;
  final String status; // ON_TIME, LATE, EXCEPTION_PENDING_APPROVAL
  final String exceptionReason;
  final int ruleVersion;

  const AttendanceEvaluationResult({
    required this.isLate,
    required this.isGpsValid,
    required this.requiresApproval,
    required this.status,
    this.exceptionReason = "",
    this.ruleVersion = 1,
  });
}

class DoctorVisitEvaluationResult {
  final bool isValidDuration;
  final bool isLocationVerified;
  final bool requiresApproval;
  final String status; // VERIFIED, LOCATION_EXCEPTION, PENDING_APPROVAL
  final String reason;
  final double distanceMeters;
  final int ruleVersion;

  const DoctorVisitEvaluationResult({
    required this.isValidDuration,
    required this.isLocationVerified,
    required this.requiresApproval,
    required this.status,
    this.reason = "",
    this.distanceMeters = 0.0,
    this.ruleVersion = 1,
  });
}

class ExpenseEvaluationResult {
  final bool isWithinDailyLimit;
  final bool isWithinCategoryLimit;
  final bool isReceiptAttached;
  final bool isReceiptRequired;
  final bool isEligible;
  final String status; // APPROVED, REQUIRES_APPROVAL, REJECTED
  final String violationReason;
  final int ruleVersion;

  const ExpenseEvaluationResult({
    required this.isWithinDailyLimit,
    required this.isWithinCategoryLimit,
    required this.isReceiptAttached,
    required this.isReceiptRequired,
    required this.isEligible,
    required this.status,
    this.violationReason = "",
    this.ruleVersion = 1,
  });
}

class RuleEngine {
  /// Selects the highest priority active rule for a given ruleType
  /// Resolution Priority: EMPLOYEE -> TERRITORY -> REGION -> ROLE -> GLOBAL DEFAULT
  static RuleModel? resolveRule({
    required List<RuleModel> allRules,
    required String ruleType,
    String? employeeId,
    String? territoryId,
    String? regionId,
  }) {
    final activeRules = allRules.where((r) => r.ruleType == ruleType && r.status == "ACTIVE").toList();
    if (activeRules.isEmpty) return null;

    // 1. Employee-specific rule
    if (employeeId != null && employeeId.isNotEmpty) {
      final empRule = activeRules.firstWhere(
        (r) => r.scope == "EMPLOYEE" && r.scopeId == employeeId,
        orElse: () => const RuleModel(ruleId: '', ruleName: '', ruleType: '', createdAt: 0),
      );
      if (empRule.ruleId.isNotEmpty) return empRule;
    }

    // 2. Territory-specific rule
    if (territoryId != null && territoryId.isNotEmpty) {
      final terRule = activeRules.firstWhere(
        (r) => r.scope == "TERRITORY" && r.scopeId == territoryId,
        orElse: () => const RuleModel(ruleId: '', ruleName: '', ruleType: '', createdAt: 0),
      );
      if (terRule.ruleId.isNotEmpty) return terRule;
    }

    // 3. Region-specific rule
    if (regionId != null && regionId.isNotEmpty) {
      final regRule = activeRules.firstWhere(
        (r) => r.scope == "REGION" && r.scopeId == regionId,
        orElse: () => const RuleModel(ruleId: '', ruleName: '', ruleType: '', createdAt: 0),
      );
      if (regRule.ruleId.isNotEmpty) return regRule;
    }

    // 4. Global default rule
    final defaultRule = activeRules.firstWhere(
      (r) => r.scope == "GLOBAL" || r.scopeId == "GLOBAL" || r.priority == "Default",
      orElse: () => activeRules.first,
    );
    return defaultRule;
  }

  /// Evaluates Attendance check-in against resolved Attendance & GPS rules
  static AttendanceEvaluationResult evaluateAttendance({
    required String checkInTimeFormatted, // "08:45 AM" or "09:30 AM"
    required double accuracyMeters,
    required RuleModel? attendanceRule,
    required RuleModel? gpsRule,
  }) {
    AttendanceRuleConfig attConfig = const AttendanceRuleConfig();
    if (attendanceRule != null && attendanceRule.conditionsJson.isNotEmpty) {
      try {
        attConfig = AttendanceRuleConfig.fromJson(jsonDecode(attendanceRule.conditionsJson));
      } catch (_) {}
    }

    GpsRuleConfig gpsConfig = const GpsRuleConfig();
    if (gpsRule != null && gpsRule.conditionsJson.isNotEmpty) {
      try {
        gpsConfig = GpsRuleConfig.fromJson(jsonDecode(gpsRule.conditionsJson));
      } catch (_) {}
    }

    bool isLate = false;
    // Check late arrival
    final timeClean = checkInTimeFormatted.toUpperCase().trim();
    if (timeClean.contains(":") && timeClean.contains("M")) {
      try {
        final parts = timeClean.replaceAll(RegExp(r'[AP]M'), '').trim().split(':');
        int hour = int.parse(parts[0]);
        final min = int.parse(parts[1]);
        if (timeClean.contains("PM") && hour != 12) hour += 12;
        if (timeClean.contains("AM") && hour == 12) hour = 0;
        final checkInMinutes = hour * 60 + min;

        // Compare with start + grace
        final startParts = attConfig.checkInStartTime.split(':');
        final startMin = int.parse(startParts[0]) * 60 + int.parse(startParts[1]) + attConfig.gracePeriodMinutes;
        if (checkInMinutes > startMin) {
          isLate = true;
        }
      } catch (_) {}
    }

    final isGpsValid = !gpsConfig.gpsRequired || (accuracyMeters <= gpsConfig.maxAccuracyMeters);
    final requiresApproval = (isLate || !isGpsValid) && attConfig.approvalRequiredForExceptions;

    String status = "ON_TIME";
    String reason = "";
    if (isLate && !isGpsValid) {
      status = "EXCEPTION_PENDING_APPROVAL";
      reason = "Late check-in & GPS accuracy (${accuracyMeters.toStringAsFixed(0)}m > ${gpsConfig.maxAccuracyMeters.toStringAsFixed(0)}m)";
    } else if (isLate) {
      status = requiresApproval ? "EXCEPTION_PENDING_APPROVAL" : "LATE";
      reason = "Check-in after grace period (${attConfig.lateThresholdTime})";
    } else if (!isGpsValid) {
      status = requiresApproval ? "EXCEPTION_PENDING_APPROVAL" : "GPS_EXCEPTION";
      reason = "GPS accuracy (${accuracyMeters.toStringAsFixed(0)}m exceeds ${gpsConfig.maxAccuracyMeters.toStringAsFixed(0)}m threshold)";
    }

    return AttendanceEvaluationResult(
      isLate: isLate,
      isGpsValid: isGpsValid,
      requiresApproval: requiresApproval,
      status: status,
      exceptionReason: reason,
      ruleVersion: attendanceRule?.version ?? 1,
    );
  }

  /// Evaluates Doctor Visit distance and duration
  static DoctorVisitEvaluationResult evaluateDoctorVisit({
    required double doctorLat,
    required double doctorLng,
    required double mrLat,
    required double mrLng,
    required int durationMinutes,
    required RuleModel? visitRule,
    required RuleModel? gpsRule,
  }) {
    DoctorVisitRuleConfig visitConfig = const DoctorVisitRuleConfig();
    if (visitRule != null && visitRule.conditionsJson.isNotEmpty) {
      try {
        visitConfig = DoctorVisitRuleConfig.fromJson(jsonDecode(visitRule.conditionsJson));
      } catch (_) {}
    }

    final distanceMeters = calculateHaversineDistanceMeters(doctorLat, doctorLng, mrLat, mrLng);
    final isLocationVerified = !visitConfig.gpsRequired || (distanceMeters <= visitConfig.maxDistanceMeters);
    final isValidDuration = durationMinutes >= visitConfig.minVisitDurationMinutes;

    final requiresApproval = (!isLocationVerified || !isValidDuration) && visitConfig.approvalRequiredForExceptions;

    String status = "VERIFIED";
    String reason = "";
    if (!isLocationVerified && !isValidDuration) {
      status = "PENDING_APPROVAL";
      reason = "Distance (${distanceMeters.toStringAsFixed(0)}m > ${visitConfig.maxDistanceMeters.toStringAsFixed(0)}m) and duration ($durationMinutes min < ${visitConfig.minVisitDurationMinutes} min)";
    } else if (!isLocationVerified) {
      status = "LOCATION_EXCEPTION";
      reason = "MR location is ${distanceMeters.toStringAsFixed(0)}m away from doctor clinic (max allowed ${visitConfig.maxDistanceMeters.toStringAsFixed(0)}m)";
    } else if (!isValidDuration) {
      status = "PENDING_APPROVAL";
      reason = "Visit duration ($durationMinutes min) shorter than mandatory threshold (${visitConfig.minVisitDurationMinutes} min)";
    }

    return DoctorVisitEvaluationResult(
      isValidDuration: isValidDuration,
      isLocationVerified: isLocationVerified,
      requiresApproval: requiresApproval,
      status: status,
      reason: reason,
      distanceMeters: distanceMeters,
      ruleVersion: visitRule?.version ?? 1,
    );
  }

  /// Evaluates Expense claim against limits and receipt requirements
  static ExpenseEvaluationResult evaluateExpense({
    required double amount,
    required String category,
    required bool hasReceipt,
    required double currentDayTotalSoFar,
    required RuleModel? expenseRule,
  }) {
    ExpenseRuleConfig config = const ExpenseRuleConfig();
    if (expenseRule != null && expenseRule.conditionsJson.isNotEmpty) {
      try {
        config = ExpenseRuleConfig.fromJson(jsonDecode(expenseRule.conditionsJson));
      } catch (_) {}
    }

    final categoryLimit = config.categoryLimits[category] ?? config.dailyLimit;
    final isWithinDailyLimit = (currentDayTotalSoFar + amount) <= config.dailyLimit;
    final isWithinCategoryLimit = amount <= categoryLimit;
    final isReceiptRequired = amount >= config.receiptRequiredThreshold;
    final isReceiptValid = !isReceiptRequired || hasReceipt;

    final isEligible = isWithinDailyLimit && isWithinCategoryLimit && isReceiptValid;

    String status = isEligible ? "APPROVED" : "REQUIRES_APPROVAL";
    String violationReason = "";
    if (!isReceiptValid) {
      violationReason = "Receipt required for claims above ₹${config.receiptRequiredThreshold.toStringAsFixed(0)}";
    } else if (!isWithinCategoryLimit) {
      violationReason = "Amount ₹$amount exceeds category limit ₹${categoryLimit.toStringAsFixed(0)} for $category";
    } else if (!isWithinDailyLimit) {
      violationReason = "Total day claim ₹${currentDayTotalSoFar + amount} exceeds daily policy limit of ₹${config.dailyLimit.toStringAsFixed(0)}";
    }

    return ExpenseEvaluationResult(
      isWithinDailyLimit: isWithinDailyLimit,
      isWithinCategoryLimit: isWithinCategoryLimit,
      isReceiptAttached: hasReceipt,
      isReceiptRequired: isReceiptRequired,
      isEligible: isEligible,
      status: status,
      violationReason: violationReason,
      ruleVersion: expenseRule?.version ?? 1,
    );
  }

  /// Calculates distance in meters between two GPS coordinates using Haversine formula
  static double calculateHaversineDistanceMeters(
    double lat1,
    double lon1,
    double lat2,
    double lon2,
  ) {
    if (lat1 == 0.0 && lon1 == 0.0) return 0.0;
    if (lat2 == 0.0 && lon2 == 0.0) return 0.0;

    const double earthRadiusMeters = 6371000.0;
    final dLat = _toRadians(lat2 - lat1);
    final dLon = _toRadians(lon2 - lon1);

    final a = math.sin(dLat / 2) * math.sin(dLat / 2) +
        math.cos(_toRadians(lat1)) * math.cos(_toRadians(lat2)) * math.sin(dLon / 2) * math.sin(dLon / 2);
    final c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a));
    return earthRadiusMeters * c;
  }

  static double _toRadians(double degree) => degree * (math.pi / 180.0);
}
