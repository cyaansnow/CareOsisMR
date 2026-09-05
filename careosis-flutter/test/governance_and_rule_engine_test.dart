import 'package:flutter_test/flutter_test.dart';
import 'package:careosis_mr/data/local/db/careosis_database.dart';
import 'package:careosis_mr/data/local/entities/admin_and_security_entities.dart';
import 'package:careosis_mr/data/repository/careosis_repository.dart';
import 'package:careosis_mr/core/security/access_control_service.dart';
import 'package:careosis_mr/core/engine/rule_engine.dart';
import 'package:careosis_mr/core/engine/incentive_calculation_engine.dart';

void main() {
  group('CareOsis MVP Foundation & Governance Tests', () {
    late CareOsisDatabase database;
    late CareOsisRepository repository;

    setUp(() async {
      database = CareOsisDatabase.instance;
      repository = CareOsisRepository(database);
      await repository.seedDatabaseIfEmpty();
    });

    test('1. Region and Territory Hierarchy Initialized & Queryable', () async {
      final regions = await repository.getAllRegions().first;
      expect(regions.length, greaterThanOrEqualTo(4));
      final reg1 = regions.firstWhere((r) => r.id == "REG-001");
      expect(reg1.name, "Delhi NCR");
      expect(reg1.code, "NCR");

      final territories = await repository.getAllTerritories().first;
      expect(territories.length, greaterThanOrEqualTo(6));
      final reg1Territories = await repository.getTerritoriesByRegion("REG-001").first;
      expect(reg1Territories.any((t) => t.territoryId == "TER-001"), isTrue);
    });

    test('2. RBAC & AccessControlService Evaluates Role + Permission + Scope', () {
      final superAdmin = UserAccount(
        id: "CO-SA-001",
        name: "Super Admin",
        email: "sa@careosis.com",
        phone: "+91 9999900001",
        role: "SUPER_ADMIN",
        password: "Pass",
        status: "ACTIVE",
        assignedRegionIds: "GLOBAL",
        permissions: "ALL",
        createdAt: 0,
      );

      final regionalAdmin = UserAccount(
        id: "CO-ADM-101",
        name: "Regional Admin",
        email: "admin@careosis.com",
        phone: "+91 9811122334",
        role: "ADMIN",
        password: "Pass",
        status: "ACTIVE",
        assignedRegionIds: "REG-001",
        employeeScopeMode: "SPECIFIC_EMPLOYEES",
        assignedEmployeeIds: "CO-MR-8492",
        permissions: "VIEW_EMPLOYEE,APPROVE_EXPENSE,APPROVE_ATTENDANCE",
        createdAt: 0,
      );

      final mr = UserAccount(
        id: "CO-MR-8492",
        name: "Aman Chhabra",
        email: "mr@careosis.com",
        phone: "+91 9876543210",
        role: "EMPLOYEE",
        password: "Pass",
        status: "ACTIVE",
        assignedRegionIds: "REG-001",
        permissions: "VIEW_EMPLOYEE,VIEW_ATTENDANCE",
        createdAt: 0,
      );

      // Super Admin has global access to everything
      expect(AccessControlService.canAccess(user: superAdmin, permission: Permissions.manageRegion), isTrue);
      expect(AccessControlService.canAccess(user: superAdmin, permission: Permissions.approveExpense), isTrue);

      // Regional Admin can approve expense within scope
      expect(AccessControlService.canApproveExpense(regionalAdmin, expenseEmployeeId: "CO-MR-8492", regionId: "REG-001"), isTrue);
      // Regional Admin cannot manage regions or access out-of-scope employee
      expect(AccessControlService.canAccess(user: regionalAdmin, permission: Permissions.manageRegion), isFalse);
      expect(AccessControlService.canApproveExpense(regionalAdmin, expenseEmployeeId: "CO-MR-9999", regionId: "REG-002"), isFalse);

      // MR can view own data but cannot approve expenses
      expect(AccessControlService.canAccess(user: mr, permission: Permissions.viewEmployee, employeeId: "CO-MR-8492"), isTrue);
      expect(AccessControlService.canAccess(user: mr, permission: Permissions.viewEmployee, employeeId: "CO-MR-9999"), isFalse);
      expect(AccessControlService.canAccess(user: mr, permission: Permissions.approveExpense), isFalse);
    });

    test('3. Rule Engine Priority Resolution (EMPLOYEE -> TERRITORY -> REGION -> GLOBAL)', () {
      final allRules = [
        const RuleModel(ruleId: "R-GLOB", ruleName: "Global Rule", ruleType: "TARGET", scope: "GLOBAL", priority: "Default", createdAt: 0),
        const RuleModel(ruleId: "R-REG", ruleName: "Region Rule", ruleType: "TARGET", scope: "REGION", scopeId: "REG-001", priority: "Region", createdAt: 0),
        const RuleModel(ruleId: "R-EMP", ruleName: "Employee Rule", ruleType: "TARGET", scope: "EMPLOYEE", scopeId: "CO-MR-8492", priority: "Employee", createdAt: 0),
      ];

      // Employee match takes top priority
      final resolvedEmp = RuleEngine.resolveRule(allRules: allRules, ruleType: "TARGET", employeeId: "CO-MR-8492", regionId: "REG-001");
      expect(resolvedEmp?.ruleId, "R-EMP");

      // Without employee rule, fallback to region
      final resolvedReg = RuleEngine.resolveRule(allRules: allRules, ruleType: "TARGET", employeeId: "CO-MR-9999", regionId: "REG-001");
      expect(resolvedReg?.ruleId, "R-REG");

      // Without region match, fallback to global default
      final resolvedGlob = RuleEngine.resolveRule(allRules: allRules, ruleType: "TARGET", employeeId: "CO-MR-9999", regionId: "REG-999");
      expect(resolvedGlob?.ruleId, "R-GLOB");
    });

    test('4. Slab-based Incentive Calculation Engine matches Governance Sheet Slabs', () {
      final rule = IncentiveRuleModel(
        id: "RULE-INC-001",
        ruleName: "Monthly Slab Incentive",
        ruleType: "SLAB_BASED",
        targetSource: "TOTAL_SALES",
        defaultTarget: 200000.0,
        slabsJson: '[{"min":0,"max":60,"fixed":0,"label":"0% - 60%"},{"min":60.01,"max":70,"fixed":500,"label":"60.01% - 70%"},{"min":70.01,"max":80,"fixed":800,"label":"70.01% - 80%"},{"min":80.01,"max":90,"fixed":1000,"label":"80.01% - 90%"},{"min":90.01,"max":100,"fixed":1250,"label":"90.01% - 100%"},{"min":100.01,"max":110,"fixed":1500,"label":"100.01% - 110%"},{"min":110.01,"max":999,"fixed":2000,"label":"110.01%+"}]',
        updatedAt: 0,
      );

      // 82% Achievement on ₹2,00,000 Target (Actual Sales: ₹1,64,000)
      final input82 = const CalculationInput(
        employeeId: "CO-MR-8492",
        employeeName: "Aman Chhabra",
        employeeMonthlyTarget: 200000.0,
        period: "August 2026",
        actualSales: 164000.0,
      );
      final res82 = IncentiveCalculationEngine.calculateIncentive(input: input82, rule: rule);
      expect(res82.finalIncentive, 1000.0);
      expect(res82.applicableSlab, contains("80.01% - 90%"));

      // 105% Achievement on ₹2,00,000 Target (Actual Sales: ₹2,10,000)
      final input105 = const CalculationInput(
        employeeId: "CO-MR-8492",
        employeeName: "Aman Chhabra",
        employeeMonthlyTarget: 200000.0,
        period: "August 2026",
        actualSales: 210000.0,
      );
      final res105 = IncentiveCalculationEngine.calculateIncentive(input: input105, rule: rule);
      expect(res105.finalIncentive, 1500.0);
      expect(res105.applicableSlab, contains("100.01% - 110%"));
    });

    test('5. Approval Engine & Mandatory Rejection Reason Validation', () async {
      final req = await repository.submitApprovalRequest(
        module: "EXPENSE",
        entityId: "EXP-TEST-001",
        title: "Test Claim ₹1,200",
        details: "Fuel allowance",
        submittedBy: "CO-MR-8492",
        submittedByName: "Aman Chhabra",
        scope: "REG-001",
      );
      expect(req.status, "PENDING");

      // Rejection without reason throws ArgumentError
      expect(
        () async => repository.rejectApprovalRequest(
          approvalId: req.approvalId,
          reviewerId: "CO-ADM-101",
          reviewerName: "Rajesh Verma",
          reviewerRole: "ADMIN",
          rejectionReason: "   ",
        ),
        throwsA(isA<ArgumentError>()),
      );

      // Rejection with reason succeeds
      final success = await repository.rejectApprovalRequest(
        approvalId: req.approvalId,
        reviewerId: "CO-ADM-101",
        reviewerName: "Rajesh Verma",
        reviewerRole: "ADMIN",
        rejectionReason: "Missing original toll tax receipt",
      );
      expect(success, isTrue);

      final rejectedReq = database.adminAndSecurityDao.getApprovalById(req.approvalId);
      expect(rejectedReq?.status, "REJECTED");
      expect(rejectedReq?.comment, "Missing original toll tax receipt");
    });

    test('6. Tamper-Proof Audit Logging Integrity', () async {
      final log = await repository.auditService.logAction(
        actorId: "CO-SA-001",
        actorName: "Dr. Vikramaditya Singhania",
        actorRole: "SUPER_ADMIN",
        action: "TARGET_OVERRIDE",
        targetEntity: "TARGET",
        entityId: "CO-MR-8492",
        oldValue: "₹2,00,000",
        newValue: "₹2,20,000",
      );

      expect(log.auditId, startsWith("AUD-"));
      expect(log.actorRole, "SUPER_ADMIN");
      expect(log.newValue, "₹2,20,000");

      final allLogs = await repository.getAllAuditLogs().first;
      expect(allLogs.any((l) => l.action == "TARGET_OVERRIDE"), isTrue);
    });
  });
}
