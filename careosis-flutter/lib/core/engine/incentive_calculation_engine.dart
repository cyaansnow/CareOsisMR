import 'dart:convert';
import 'package:uuid/uuid.dart';

enum RuleType {
  SLAB_BASED,
  TARGET_ACHIEVEMENT_PERCENT,
  PERCENTAGE_OF_SALES,
  FIXED_AMOUNT,
  MULTI_COMPONENT
}

enum TargetSource {
  TOTAL_SALES,
  PRODUCT_SALES,
  DOCTOR_CALLS,
  COLLECTION
}

enum TargetPriority {
  EMPLOYEE_FIRST,
  RULE_DEFAULT,
  HYBRID
}

class SlabConfig {
  final double minPercent;
  final double maxPercent;
  final double ratePercent;
  final double fixedAmount;
  final String label;

  const SlabConfig({
    required this.minPercent,
    required this.maxPercent,
    this.ratePercent = 0.0,
    this.fixedAmount = 0.0,
    this.label = "",
  });

  Map<String, dynamic> toJson() => {
        'min': minPercent,
        'max': maxPercent,
        'rate': ratePercent,
        'fixed': fixedAmount,
        'label': label,
      };

  factory SlabConfig.fromJson(Map<String, dynamic> json) => SlabConfig(
        minPercent: (json['min'] as num?)?.toDouble() ?? 0.0,
        maxPercent: (json['max'] as num?)?.toDouble() ?? 100.0,
        ratePercent: (json['rate'] as num?)?.toDouble() ?? 0.0,
        fixedAmount: (json['fixed'] as num?)?.toDouble() ?? 0.0,
        label: json['label'] as String? ?? "",
      );
}

class ComponentConfig {
  final String name;
  final String type;
  final double weightPercent;
  final double minThresholdPercent;
  final String rewardType;
  final double rewardValue;

  const ComponentConfig({
    required this.name,
    this.type = "KPI",
    this.weightPercent = 0.0,
    this.minThresholdPercent = 0.0,
    this.rewardType = "FIXED_AMOUNT",
    this.rewardValue = 0.0,
  });

  Map<String, dynamic> toJson() => {
        'name': name,
        'type': type,
        'weightPercent': weightPercent,
        'minThresholdPercent': minThresholdPercent,
        'rewardType': rewardType,
        'rewardValue': rewardValue,
      };

  factory ComponentConfig.fromJson(Map<String, dynamic> json) =>
      ComponentConfig(
        name: json['name'] as String? ?? "",
        type: json['type'] as String? ?? "KPI",
        weightPercent: (json['weightPercent'] as num?)?.toDouble() ?? 0.0,
        minThresholdPercent:
            (json['minThresholdPercent'] as num?)?.toDouble() ?? 0.0,
        rewardType: json['rewardType'] as String? ?? "FIXED_AMOUNT",
        rewardValue: (json['rewardValue'] as num?)?.toDouble() ?? 0.0,
      );
}

class IncentiveSlab {
  final String id;
  final double minThresholdPercent;
  final double maxThresholdPercent;
  final double incentivePercent;
  final double fixedRewardAmount;
  final String label;

  IncentiveSlab({
    String? id,
    required this.minThresholdPercent,
    required this.maxThresholdPercent,
    this.incentivePercent = 0.0,
    this.fixedRewardAmount = 0.0,
    this.label = "",
  }) : id = id ?? const Uuid().v4().substring(0, 8);

  Map<String, dynamic> toJson() => {
        'id': id,
        'min': minThresholdPercent,
        'max': maxThresholdPercent,
        'rate': incentivePercent,
        'fixed': fixedRewardAmount,
        'label': label,
      };

  factory IncentiveSlab.fromJson(Map<String, dynamic> json) => IncentiveSlab(
        id: json['id'] as String? ?? const Uuid().v4().substring(0, 8),
        minThresholdPercent: (json['min'] as num?)?.toDouble() ?? 0.0,
        maxThresholdPercent: (json['max'] as num?)?.toDouble() ?? 100.0,
        incentivePercent: (json['rate'] as num?)?.toDouble() ?? 0.0,
        fixedRewardAmount: (json['fixed'] as num?)?.toDouble() ?? 0.0,
        label: json['label'] as String? ?? "",
      );
}

class MultiComponentConfig {
  final double salesThresholdPercent;
  final double salesIncentivePercent;
  final double doctorCoverageThresholdPercent;
  final double doctorCoverageReward;
  final int newDoctorCountThreshold;
  final double newDoctorReward;
  final double collectionThresholdPercent;
  final double collectionReward;

  const MultiComponentConfig({
    this.salesThresholdPercent = 100.0,
    this.salesIncentivePercent = 5.0,
    this.doctorCoverageThresholdPercent = 80.0,
    this.doctorCoverageReward = 1000.0,
    this.newDoctorCountThreshold = 5,
    this.newDoctorReward = 500.0,
    this.collectionThresholdPercent = 90.0,
    this.collectionReward = 1000.0,
  });

  Map<String, dynamic> toJson() => {
        'salesThresholdPercent': salesThresholdPercent,
        'salesIncentivePercent': salesIncentivePercent,
        'doctorCoverageThresholdPercent': doctorCoverageThresholdPercent,
        'doctorCoverageReward': doctorCoverageReward,
        'newDoctorCountThreshold': newDoctorCountThreshold,
        'newDoctorReward': newDoctorReward,
        'collectionThresholdPercent': collectionThresholdPercent,
        'collectionReward': collectionReward,
      };

  factory MultiComponentConfig.fromJson(Map<String, dynamic> json) =>
      MultiComponentConfig(
        salesThresholdPercent:
            (json['salesThresholdPercent'] as num?)?.toDouble() ?? 100.0,
        salesIncentivePercent:
            (json['salesIncentivePercent'] as num?)?.toDouble() ?? 5.0,
        doctorCoverageThresholdPercent:
            (json['doctorCoverageThresholdPercent'] as num?)?.toDouble() ??
                80.0,
        doctorCoverageReward:
            (json['doctorCoverageReward'] as num?)?.toDouble() ?? 1000.0,
        newDoctorCountThreshold:
            (json['newDoctorCountThreshold'] as num?)?.toInt() ?? 5,
        newDoctorReward:
            (json['newDoctorReward'] as num?)?.toDouble() ?? 500.0,
        collectionThresholdPercent:
            (json['collectionThresholdPercent'] as num?)?.toDouble() ?? 90.0,
        collectionReward:
            (json['collectionReward'] as num?)?.toDouble() ?? 1000.0,
      );
}

class CalculationInput {
  final String employeeId;
  final String employeeName;
  final double employeeMonthlyTarget;
  final String employeeRole;
  final String employeeDesignation;
  final String employeeAssignedRegions;
  final String period; // e.g. "August 2026"
  final double actualSales;
  final int doctorVisitsDone;
  final int doctorVisitsTarget;
  final int newDoctorsActivated;
  final double collectionAmount;
  final double collectionTarget;
  final bool isMonthClosed;
  final double? customTarget;

  const CalculationInput({
    required this.employeeId,
    required this.employeeName,
    required this.employeeMonthlyTarget,
    this.employeeRole = "EMPLOYEE",
    this.employeeDesignation = "Medical Representative",
    this.employeeAssignedRegions = "DELHI_NCR",
    required this.period,
    required this.actualSales,
    this.doctorVisitsDone = 12,
    this.doctorVisitsTarget = 15,
    this.newDoctorsActivated = 6,
    this.collectionAmount = 180000.0,
    this.collectionTarget = 200000.0,
    this.isMonthClosed = false,
    this.customTarget,
  });
}

class BreakdownComponentItem {
  final String title;
  final String description;
  final double amount;
  final String rateOrUnit;

  const BreakdownComponentItem({
    required this.title,
    required this.description,
    required this.amount,
    this.rateOrUnit = "",
  });

  Map<String, dynamic> toJson() => {
        'title': title,
        'description': description,
        'amount': amount,
        'rateOrUnit': rateOrUnit,
      };

  factory BreakdownComponentItem.fromJson(Map<String, dynamic> json) =>
      BreakdownComponentItem(
        title: json['title'] as String? ?? "Component",
        description: json['description'] as String? ?? "",
        amount: (json['amount'] as num?)?.toDouble() ?? 0.0,
        rateOrUnit: json['rateOrUnit'] as String? ?? "",
      );
}

class IncentiveResult {
  final String employeeId;
  final String employeeName;
  final String period;
  final double target;
  final double actualSales;
  final double achievementPercent;
  final String applicableRuleId;
  final String applicableRuleName;
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
  final String status; // "ESTIMATED", "PENDING_APPROVAL", "FINAL"
  final List<BreakdownComponentItem> breakdownItems;
  final int calculationTimestamp;

  const IncentiveResult({
    required this.employeeId,
    required this.employeeName,
    required this.period,
    required this.target,
    required this.actualSales,
    required this.achievementPercent,
    required this.applicableRuleId,
    required this.applicableRuleName,
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
    required this.status,
    required this.breakdownItems,
    required this.calculationTimestamp,
  });
}

class IncentiveRuleModel {
  final String id;
  final String ruleName;
  final String ruleType;
  final String targetSource;
  final double defaultTarget;
  final String targetPriority;
  final double minThresholdPercent;
  final double maxThresholdPercent;
  final double incentivePercent;
  final double fixedRewardAmount;
  final String slabsJson;
  final String componentsJson;
  final String regionId;
  final String assignedEmployeeIds;
  final String employeeCategory;
  final int priority;
  final int versionNumber;
  final String effectiveFrom;
  final String effectiveTo;
  final String status;
  final String formulaDescription;
  final String updatedBy;
  final int updatedAt;

  const IncentiveRuleModel({
    required this.id,
    required this.ruleName,
    this.ruleType = "PERCENTAGE_OF_SALES",
    this.targetSource = "TOTAL_SALES",
    this.defaultTarget = 200000.0,
    this.targetPriority = "EMPLOYEE_FIRST",
    this.minThresholdPercent = 0.0,
    this.maxThresholdPercent = 100.0,
    this.incentivePercent = 0.0,
    this.fixedRewardAmount = 0.0,
    this.slabsJson = "",
    this.componentsJson = "",
    this.regionId = "GLOBAL",
    this.assignedEmployeeIds = "ALL",
    this.employeeCategory = "ALL",
    this.priority = 4,
    this.versionNumber = 1,
    this.effectiveFrom = "01-08-2026",
    this.effectiveTo = "31-12-2026",
    this.status = "ACTIVE",
    this.formulaDescription = "Sales * Percentage when achievement is within tier",
    this.updatedBy = "CO-ADM-101",
    required this.updatedAt,
  });
}

class IncentiveCalculationEngine {
  static List<SlabConfig> parseSlabs(String slabsJson) {
    if (slabsJson.trim().isNotEmpty) {
      try {
        final List<dynamic> array = jsonDecode(slabsJson) as List<dynamic>;
        final list = <SlabConfig>[];
        for (final item in array) {
          list.add(SlabConfig.fromJson(item as Map<String, dynamic>));
        }
        if (list.isNotEmpty) {
          list.sort((a, b) => a.minPercent.compareTo(b.minPercent));
          return list;
        }
      } catch (_) {}
    }
    return const [
      SlabConfig(minPercent: 70.0, maxPercent: 79.99, ratePercent: 2.0, fixedAmount: 0.0, label: "70% - 79.99%"),
      SlabConfig(minPercent: 80.0, maxPercent: 89.99, ratePercent: 3.0, fixedAmount: 0.0, label: "80% - 89.99%"),
      SlabConfig(minPercent: 90.0, maxPercent: 99.99, ratePercent: 4.0, fixedAmount: 0.0, label: "90% - 99.99%"),
      SlabConfig(minPercent: 100.0, maxPercent: 1000.0, ratePercent: 5.0, fixedAmount: 2500.0, label: "100%+"),
    ];
  }

  static String serializeSlabConfigs(List<SlabConfig> slabs) {
    return jsonEncode(slabs.map((s) => s.toJson()).toList());
  }

  static String serializeComponents(List<ComponentConfig> components) {
    return jsonEncode(components.map((c) => c.toJson()).toList());
  }

  static List<IncentiveSlab> parseRuleSlabs(IncentiveRuleModel rule) {
    if (rule.slabsJson.trim().isNotEmpty) {
      try {
        final List<dynamic> array = jsonDecode(rule.slabsJson) as List<dynamic>;
        final list = <IncentiveSlab>[];
        for (final item in array) {
          list.add(IncentiveSlab.fromJson(item as Map<String, dynamic>));
        }
        if (list.isNotEmpty) {
          list.sort((a, b) => a.minThresholdPercent.compareTo(b.minThresholdPercent));
          return list;
        }
      } catch (_) {}
    }
    return getDefaultStandardSlabs(rule.ruleType);
  }

  static List<IncentiveSlab> getDefaultStandardSlabs(String ruleType) {
    switch (ruleType) {
      case "SLAB_BASED":
        return [
          IncentiveSlab(minThresholdPercent: 0.0, maxThresholdPercent: 50.0, fixedRewardAmount: 0.0, label: "Below 50%"),
          IncentiveSlab(minThresholdPercent: 50.0, maxThresholdPercent: 70.0, fixedRewardAmount: 1000.0, label: "50% - 69.99%"),
          IncentiveSlab(minThresholdPercent: 70.0, maxThresholdPercent: 80.0, fixedRewardAmount: 2500.0, label: "70% - 79.99%"),
          IncentiveSlab(minThresholdPercent: 80.0, maxThresholdPercent: 90.0, fixedRewardAmount: 4000.0, label: "80% - 89.99%"),
          IncentiveSlab(minThresholdPercent: 90.0, maxThresholdPercent: 100.0, fixedRewardAmount: 6000.0, label: "90% - 99.99%"),
          IncentiveSlab(minThresholdPercent: 100.0, maxThresholdPercent: 110.0, fixedRewardAmount: 8000.0, label: "100% - 109.99%"),
          IncentiveSlab(minThresholdPercent: 110.0, maxThresholdPercent: 500.0, fixedRewardAmount: 12000.0, label: "110%+"),
        ];
      case "FIXED_AMOUNT":
        return [
          IncentiveSlab(minThresholdPercent: 0.0, maxThresholdPercent: 100.0, fixedRewardAmount: 0.0, label: "Below 100%"),
          IncentiveSlab(minThresholdPercent: 100.0, maxThresholdPercent: 110.0, fixedRewardAmount: 5000.0, label: "100% Milestone"),
          IncentiveSlab(minThresholdPercent: 110.0, maxThresholdPercent: 120.0, fixedRewardAmount: 7500.0, label: "110% Milestone"),
          IncentiveSlab(minThresholdPercent: 120.0, maxThresholdPercent: 500.0, fixedRewardAmount: 10000.0, label: "120%+ Milestone"),
        ];
      default:
        return [
          IncentiveSlab(minThresholdPercent: 0.0, maxThresholdPercent: 50.0, incentivePercent: 0.0, label: "Below 50%"),
          IncentiveSlab(minThresholdPercent: 50.0, maxThresholdPercent: 70.0, incentivePercent: 1.0, label: "50% - 69.99%"),
          IncentiveSlab(minThresholdPercent: 70.0, maxThresholdPercent: 90.0, incentivePercent: 2.0, label: "70% - 89.99%"),
          IncentiveSlab(minThresholdPercent: 90.0, maxThresholdPercent: 100.0, incentivePercent: 3.0, label: "90% - 99.99%"),
          IncentiveSlab(minThresholdPercent: 100.0, maxThresholdPercent: 500.0, incentivePercent: 5.0, label: "100%+"),
        ];
    }
  }

  static MultiComponentConfig parseMultiComponentConfig(IncentiveRuleModel rule) {
    if (rule.componentsJson.trim().isNotEmpty) {
      try {
        final Map<String, dynamic> obj = jsonDecode(rule.componentsJson) as Map<String, dynamic>;
        return MultiComponentConfig.fromJson(obj);
      } catch (_) {}
    }
    return const MultiComponentConfig();
  }

  static IncentiveRuleModel? resolveApplicableRule(
    CalculationInput employee,
    List<IncentiveRuleModel> rules,
  ) {
    final activeRules = rules.where((r) => r.status == "ACTIVE").toList();
    if (activeRules.isEmpty) return null;

    // 1. Employee-specific rule
    for (final rule in activeRules) {
      if (rule.assignedEmployeeIds != "ALL") {
        final ids = rule.assignedEmployeeIds.split(",").map((s) => s.trim()).toList();
        if (ids.contains(employee.employeeId)) return rule;
      }
    }

    // 2. Region-specific rule
    final empRegions = employee.employeeAssignedRegions.split(",").map((s) => s.trim()).toList();
    for (final rule in activeRules) {
      if (rule.regionId != "GLOBAL" && empRegions.contains(rule.regionId)) {
        return rule;
      }
    }

    // 3. Category/Role-specific rule
    for (final rule in activeRules) {
      if (rule.employeeCategory != "ALL") {
        if (rule.employeeCategory.toLowerCase() == employee.employeeDesignation.toLowerCase() ||
            rule.employeeCategory.toLowerCase() == employee.employeeRole.toLowerCase()) {
          return rule;
        }
      }
    }

    // 4. Default global rule with lowest priority rank (highest priority value)
    final globals = activeRules.where((r) => r.regionId == "GLOBAL").toList();
    if (globals.isNotEmpty) {
      globals.sort((a, b) => a.priority.compareTo(b.priority));
      return globals.first;
    }

    return activeRules.first;
  }

  static IncentiveResult calculateIncentive({
    required CalculationInput input,
    required IncentiveRuleModel rule,
  }) {
    // 1. Target Determination (Priority: Employee Specific > Custom > Rule Default)
    final double target = input.customTarget != null && input.customTarget! > 0
        ? input.customTarget!
        : (input.employeeMonthlyTarget > 0 ? input.employeeMonthlyTarget : (rule.defaultTarget > 0 ? rule.defaultTarget : 200000.0));

    // 2. Achievement %
    final double actualSales = input.actualSales;
    final double achievementPercent = target > 0 ? (actualSales / target) * 100.0 : 0.0;

    final slabs = parseRuleSlabs(rule);
    final breakdownItems = <BreakdownComponentItem>[];

    String applicableSlabLabel = "Standard Tier";
    double incentiveRate = 0.0;
    double baseIncentive = 0.0;
    double coverageIncentive = 0.0;
    double newDoctorIncentive = 0.0;
    double collectionIncentive = 0.0;
    const double deductions = 0.0;

    switch (rule.ruleType) {
      case "SLAB_BASED":
        IncentiveSlab? matchedSlab;
        for (final slab in slabs) {
          if (achievementPercent >= slab.minThresholdPercent && achievementPercent < slab.maxThresholdPercent) {
            matchedSlab = slab;
            break;
          }
        }
        matchedSlab ??= (achievementPercent >= (slabs.isNotEmpty ? slabs.last.minThresholdPercent : 0.0) ? (slabs.isNotEmpty ? slabs.last : null) : (slabs.isNotEmpty ? slabs.first : null));

        if (matchedSlab != null) {
          applicableSlabLabel = matchedSlab.label.isNotEmpty
              ? matchedSlab.label
              : "${matchedSlab.minThresholdPercent.toInt()}% - ${matchedSlab.maxThresholdPercent > 200 ? "100%+" : "${matchedSlab.maxThresholdPercent.toInt()}%"}";

          baseIncentive = matchedSlab.fixedRewardAmount > 0
              ? matchedSlab.fixedRewardAmount
              : actualSales * (matchedSlab.incentivePercent / 100.0);
          incentiveRate = matchedSlab.incentivePercent;

          breakdownItems.add(
            BreakdownComponentItem(
              title: "Slab Tier Incentive",
              description: "Achievement at ${achievementPercent.toStringAsFixed(1)}% ($applicableSlabLabel)",
              amount: baseIncentive,
              rateOrUnit: matchedSlab.fixedRewardAmount > 0 ? "Fixed Slab Reward" : "${matchedSlab.incentivePercent}% of Sales",
            ),
          );
        }
        break;

      case "PERCENTAGE_OF_SALES":
        IncentiveSlab? matchedSlab;
        for (final slab in slabs) {
          if (achievementPercent >= slab.minThresholdPercent && achievementPercent < slab.maxThresholdPercent) {
            matchedSlab = slab;
            break;
          }
        }
        matchedSlab ??= (achievementPercent >= (slabs.isNotEmpty ? slabs.last.minThresholdPercent : 0.0) ? (slabs.isNotEmpty ? slabs.last : null) : (slabs.isNotEmpty ? slabs.first : null));

        if (matchedSlab != null) {
          applicableSlabLabel = matchedSlab.label.isNotEmpty
              ? matchedSlab.label
              : "${matchedSlab.minThresholdPercent.toInt()}% - ${matchedSlab.maxThresholdPercent > 200 ? "100%+" : "${matchedSlab.maxThresholdPercent.toInt()}%"}";

          incentiveRate = matchedSlab.incentivePercent;
          baseIncentive = actualSales * (matchedSlab.incentivePercent / 100.0) + matchedSlab.fixedRewardAmount;

          breakdownItems.add(
            BreakdownComponentItem(
              title: "Sales Volume Incentive",
              description: "₹${actualSales.toStringAsFixed(0)} × ${matchedSlab.incentivePercent}% ($applicableSlabLabel)",
              amount: actualSales * (matchedSlab.incentivePercent / 100.0),
              rateOrUnit: "${matchedSlab.incentivePercent}%",
            ),
          );

          if (matchedSlab.fixedRewardAmount > 0) {
            breakdownItems.add(
              BreakdownComponentItem(
                title: "Tier Achievement Bonus",
                description: "Milestone bonus for reaching $applicableSlabLabel",
                amount: matchedSlab.fixedRewardAmount,
                rateOrUnit: "Fixed Bonus",
              ),
            );
          }
        }
        break;

      case "MULTI_COMPONENT":
        final multiConfig = parseMultiComponentConfig(rule);
        if (achievementPercent >= multiConfig.salesThresholdPercent) {
          baseIncentive = actualSales * (multiConfig.salesIncentivePercent / 100.0);
          incentiveRate = multiConfig.salesIncentivePercent;
          applicableSlabLabel = "100%+ Core Sales Target";

          breakdownItems.add(
            BreakdownComponentItem(
              title: "Core Sales Volume",
              description: "${multiConfig.salesIncentivePercent}% on ₹${actualSales.toStringAsFixed(0)}",
              amount: baseIncentive,
              rateOrUnit: "${multiConfig.salesIncentivePercent}%",
            ),
          );
        }

        final double coveragePct = input.doctorVisitsTarget > 0
            ? (input.doctorVisitsDone / input.doctorVisitsTarget) * 100.0
            : 0.0;
        if (coveragePct >= multiConfig.doctorCoverageThresholdPercent) {
          coverageIncentive = multiConfig.doctorCoverageReward;
          breakdownItems.add(
            BreakdownComponentItem(
              title: "Doctor Coverage Milestone",
              description: "Visited ${input.doctorVisitsDone}/${input.doctorVisitsTarget} target doctors (${coveragePct.toStringAsFixed(0)}%)",
              amount: coverageIncentive,
              rateOrUnit: "Coverage Bonus",
            ),
          );
        }

        if (input.newDoctorsActivated >= multiConfig.newDoctorCountThreshold) {
          newDoctorIncentive = input.newDoctorsActivated * multiConfig.newDoctorReward;
          breakdownItems.add(
            BreakdownComponentItem(
              title: "New Doctor Onboarding",
              description: "${input.newDoctorsActivated} new prescribers × ₹${multiConfig.newDoctorReward.toInt()}",
              amount: newDoctorIncentive,
              rateOrUnit: "Activation Bonus",
            ),
          );
        }

        final double colPct = input.collectionTarget > 0
            ? (input.collectionAmount / input.collectionTarget) * 100.0
            : 0.0;
        if (colPct >= multiConfig.collectionThresholdPercent) {
          collectionIncentive = multiConfig.collectionReward;
          breakdownItems.add(
            BreakdownComponentItem(
              title: "Commercial Collection Target",
              description: "Collected ₹${input.collectionAmount.toStringAsFixed(0)} (${colPct.toStringAsFixed(0)}% recovery)",
              amount: collectionIncentive,
              rateOrUnit: "Collection Bonus",
            ),
          );
        }
        break;

      default:
        // Standard baseline calculations
        if (achievementPercent >= 100.0) {
          incentiveRate = 5.0;
          baseIncentive = actualSales * 0.05 + 2500.0;
          applicableSlabLabel = "100%+ (Bonus)";
        } else if (achievementPercent >= 90.0) {
          incentiveRate = 4.0;
          baseIncentive = actualSales * 0.04;
          applicableSlabLabel = "90% - 99.99%";
        } else if (achievementPercent >= 80.0) {
          incentiveRate = 3.0;
          baseIncentive = actualSales * 0.03;
          applicableSlabLabel = "80% - 89.99%";
        } else if (achievementPercent >= 70.0) {
          incentiveRate = 2.0;
          baseIncentive = actualSales * 0.02;
          applicableSlabLabel = "70% - 79.99%";
        } else {
          incentiveRate = 0.0;
          baseIncentive = 0.0;
          applicableSlabLabel = "Below 70%";
        }

        breakdownItems.add(
          BreakdownComponentItem(
            title: "Sales Volume Incentive",
            description: "Achievement at ${achievementPercent.toStringAsFixed(1)}% ($applicableSlabLabel)",
            amount: baseIncentive,
            rateOrUnit: "${incentiveRate}%",
          ),
        );
        break;
    }

    final double finalIncentive = (baseIncentive + coverageIncentive + newDoctorIncentive + collectionIncentive - deductions).clamp(0.0, double.infinity);

    final String status = input.isMonthClosed ? "FINAL" : "ESTIMATED";

    return IncentiveResult(
      employeeId: input.employeeId,
      employeeName: input.employeeName,
      period: input.period,
      target: target,
      actualSales: actualSales,
      achievementPercent: achievementPercent,
      applicableRuleId: rule.id,
      applicableRuleName: rule.ruleName,
      ruleVersion: rule.versionNumber,
      ruleType: rule.ruleType,
      applicableSlab: applicableSlabLabel,
      incentiveRate: incentiveRate,
      baseIncentive: baseIncentive,
      coverageIncentive: coverageIncentive,
      newDoctorIncentive: newDoctorIncentive,
      collectionIncentive: collectionIncentive,
      deductions: deductions,
      finalIncentive: finalIncentive,
      status: status,
      breakdownItems: breakdownItems,
      calculationTimestamp: DateTime.now().millisecondsSinceEpoch,
    );
  }
}
