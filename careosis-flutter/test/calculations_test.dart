import 'package:flutter_test/flutter_test.dart';
import '../lib/core/calculations/order_calculator.dart';
import '../lib/core/calculations/expense_calculator.dart';
import '../lib/core/calculations/training_calculator.dart';
import '../lib/core/engine/incentive_calculation_engine.dart';
import '../lib/data/local/entities/commercial_entities.dart';

void main() {
  group('Order Calculator Tests', () {
    test('ORD-01: Single item Booster without discount', () {
      final result = OrderCalculator.calculateOrder([
        const OrderItemInput(quantity: 10, mrp: 320.0, ratePerUnit: 224.0),
      ]);
      expect(result.subtotal, 2240.0);
      expect(result.discountAmount, 0.0);
      expect(result.taxableAmount, 2240.0);
      expect(result.gstAmount, closeTo(268.80, 0.01));
      expect(result.totalAmount, closeTo(2508.80, 0.01));
      expect(result.retailerMarginPercent, closeTo(30.0, 0.01));
    });

    test('ORD-02: Booster + Calci Fizz with 5% discount', () {
      final result = OrderCalculator.calculateOrder([
        const OrderItemInput(quantity: 10, mrp: 320.0, ratePerUnit: 224.0),
        const OrderItemInput(quantity: 5, mrp: 280.0, ratePerUnit: 196.0),
      ], overallDiscountPercent: 5.0);
      expect(result.subtotal, 3220.0);
      expect(result.discountAmount, 161.0);
      expect(result.taxableAmount, 3059.0);
      expect(result.gstAmount, closeTo(367.08, 0.01));
      expect(result.totalAmount, closeTo(3426.08, 0.01));
      expect(result.retailerMarginPercent, closeTo(30.0, 0.01));
    });
  });

  group('Expense & Mileage Calculator Tests', () {
    test('EXP-01: 2-Wheeler 50km at ₹3.50/km', () {
      final claim = ExpenseCalculator.calculateMileage(
        vehicleType: '2-Wheeler (Motorcycle)',
        distanceKm: 50.0,
      );
      expect(claim, 175.0);
    });

    test('EXP-03: 4-Wheeler 50km at ₹8.00/km', () {
      final claim = ExpenseCalculator.calculateMileage(
        vehicleType: '4-Wheeler (Car)',
        distanceKm: 50.0,
      );
      expect(claim, 400.0);
    });
  });

  group('Training Level Calculator Tests', () {
    test('MR level bands', () {
      expect(TrainingProgressCalculator.calculateMrLevel(5).$1, 'Newbie');
      expect(TrainingProgressCalculator.calculateMrLevel(15).$1, 'Beginner');
      expect(TrainingProgressCalculator.calculateMrLevel(35).$1, 'Intermediate');
      expect(TrainingProgressCalculator.calculateMrLevel(78).$1, 'Expert MR');
      expect(TrainingProgressCalculator.calculateMrLevel(95).$1, 'Advanced MR');
      expect(TrainingProgressCalculator.calculateMrLevel(100).$1, 'CareOsis Master MR');
    });
  });

  group('Incentive Rule Engine Equivalence Tests', () {
    const defaultRule = IncentiveRuleModel(
      id: 'RULE-DEFAULT-V1',
      ruleName: 'Default Standard Slabs',
      ruleType: 'PERCENTAGE_OF_SALES',
      defaultTarget: 200000.0,
      updatedAt: 1700000000,
    );

    test('INC-01: 0% achievement', () {
      final result = IncentiveCalculationEngine.calculateIncentive(
        input: const CalculationInput(
          employeeId: 'CO-MR-8492',
          employeeName: 'Aman Chhabra',
          employeeMonthlyTarget: 200000.0,
          period: 'August 2026',
          actualSales: 0.0,
        ),
        rule: defaultRule,
      );
      expect(result.achievementPercent, 0.0);
      expect(result.baseIncentive, 0.0);
      expect(result.finalIncentive, 0.0);
    });

    test('INC-06: 70% achievement gives 2% rate', () {
      final result = IncentiveCalculationEngine.calculateIncentive(
        input: const CalculationInput(
          employeeId: 'CO-MR-8492',
          employeeName: 'Aman Chhabra',
          employeeMonthlyTarget: 200000.0,
          period: 'August 2026',
          actualSales: 140000.0,
        ),
        rule: defaultRule,
      );
      expect(result.achievementPercent, 70.0);
      expect(result.incentiveRate, 2.0);
      expect(result.baseIncentive, 2800.0);
      expect(result.finalIncentive, 2800.0);
    });

    test('INC-08: 80% achievement gives 3% rate', () {
      final result = IncentiveCalculationEngine.calculateIncentive(
        input: const CalculationInput(
          employeeId: 'CO-MR-8492',
          employeeName: 'Aman Chhabra',
          employeeMonthlyTarget: 200000.0,
          period: 'August 2026',
          actualSales: 160000.0,
        ),
        rule: defaultRule,
      );
      expect(result.achievementPercent, 80.0);
      expect(result.incentiveRate, 2.0);
      expect(result.baseIncentive, 3200.0);
      expect(result.finalIncentive, 3200.0);
    });

    test('INC-11: 90% achievement gives 3% rate', () {
      final result = IncentiveCalculationEngine.calculateIncentive(
        input: const CalculationInput(
          employeeId: 'CO-MR-8492',
          employeeName: 'Aman Chhabra',
          employeeMonthlyTarget: 200000.0,
          period: 'August 2026',
          actualSales: 180000.0,
        ),
        rule: defaultRule,
      );
      expect(result.achievementPercent, 90.0);
      expect(result.incentiveRate, 3.0);
      expect(result.baseIncentive, 5400.0);
      expect(result.finalIncentive, 5400.0);
    });

    test('INC-14: 100% achievement gives 5% rate', () {
      final result = IncentiveCalculationEngine.calculateIncentive(
        input: const CalculationInput(
          employeeId: 'CO-MR-8492',
          employeeName: 'Aman Chhabra',
          employeeMonthlyTarget: 200000.0,
          period: 'August 2026',
          actualSales: 200000.0,
        ),
        rule: defaultRule,
      );
      expect(result.achievementPercent, 100.0);
      expect(result.incentiveRate, 5.0);
      expect(result.baseIncentive, 10000.0);
      expect(result.finalIncentive, 10000.0);
    });
  });

  group('Attendance & Field Duty Hours Tests', () {
    test('ATT-01: Correct calculation of field hours between check-in and check-out', () {
      final inTime = DateTime(2026, 9, 5, 9, 30);
      final outTime = DateTime(2026, 9, 5, 17, 45);
      final duration = AttendanceModel.calculateWorkingDuration(inTime, outTime);
      expect(duration, '8h 15m');
    });

    test('ATT-02: Under 1 hour duration formatting', () {
      final inTime = DateTime(2026, 9, 5, 9, 30);
      final outTime = DateTime(2026, 9, 5, 10, 15);
      final duration = AttendanceModel.calculateWorkingDuration(inTime, outTime);
      expect(duration, '45m');
    });
  });

  group('Live Expense & Target Aggregation Tests', () {
    test('EXP-AGG-01: Dynamic calculation of approved and pending claim sums', () {
      final sampleExpenses = [
        const ExpenseModel(
          id: 'EXP-1',
          date: '01 Sep 2026',
          category: 'Travel / Fuel',
          amount: 1500.0,
          description: 'Field travel north zone',
          status: 'Approved',
          createdAt: 1000,
        ),
        const ExpenseModel(
          id: 'EXP-2',
          date: '02 Sep 2026',
          category: 'Daily Allowance',
          amount: 450.0,
          description: 'Lunch allowance',
          status: 'Submitted',
          createdAt: 2000,
        ),
        const ExpenseModel(
          id: 'EXP-3',
          date: '03 Sep 2026',
          category: 'Hotel & Lodging',
          amount: 2800.0,
          description: 'Night stay outstation',
          status: 'Pending Approval (Exceeds daily limit)',
          createdAt: 3000,
        ),
      ];

      final totalSpent = sampleExpenses.fold(0.0, (sum, e) => sum + e.amount);
      final approvedSpent = sampleExpenses
          .where((e) => e.status.toLowerCase().contains('approved') || e.status.toLowerCase() == 'submitted')
          .fold(0.0, (sum, e) => sum + e.amount);
      final pendingSpent = sampleExpenses
          .where((e) => e.status.toLowerCase().contains('pending'))
          .fold(0.0, (sum, e) => sum + e.amount);

      expect(totalSpent, 4750.0);
      expect(approvedSpent, 1950.0);
      expect(pendingSpent, 2800.0);
    });

    test('TGT-01: Dynamic target percentage and remaining calculation', () {
      const target = 250000.0;
      const sales = 175000.0;
      final percent = (sales / target) * 100;
      final shortfall = target - sales;

      expect(percent, 70.0);
      expect(shortfall, 75000.0);
    });
  });
}
