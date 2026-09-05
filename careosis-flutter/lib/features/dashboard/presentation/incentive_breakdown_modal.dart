import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../../../core/theme/careosis_theme.dart';
import '../../../data/repository/careosis_repository.dart';
import '../../../core/engine/incentive_calculation_engine.dart';

class IncentiveBreakdownModal extends StatelessWidget {
  final CareOsisRepository repository;
  const IncentiveBreakdownModal({super.key, required this.repository});

  static void show(BuildContext context, CareOsisRepository repository) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (context) => IncentiveBreakdownModal(repository: repository),
    );
  }

  @override
  Widget build(BuildContext context) {
    final user = repository.currentUser;
    final profile = repository.database.mrProfileDao.getProfileSync();
    final periodStr = DateFormat('MMMM yyyy').format(DateTime.now());

    const defaultRule = IncentiveRuleModel(
      id: "RULE-SLAB-DYNAMIC",
      ruleName: "Standard CareOsis Slab Policy",
      ruleType: "PERCENTAGE_OF_SALES",
      defaultTarget: 200000.0,
      updatedAt: 1700000000,
    );

    final actualSales = profile?.monthlySales ?? 0.0;
    final target = profile?.monthlyTarget ?? user?.monthlyTarget ?? 200000.0;
    final visitsDone = profile?.completedVisitsToday ?? 0;
    final visitsTarget = profile?.targetVisitsToday ?? 15;

    final result = IncentiveCalculationEngine.calculateIncentive(
      input: CalculationInput(
        employeeId: user?.id ?? profile?.empId ?? "MR",
        employeeName: user?.name ?? profile?.name ?? "Representative",
        employeeMonthlyTarget: target,
        period: periodStr,
        actualSales: actualSales,
        doctorVisitsDone: visitsDone,
        doctorVisitsTarget: visitsTarget,
        newDoctorsActivated: 0,
        collectionAmount: actualSales,
        collectionTarget: target,
      ),
      rule: defaultRule,
    );

    return Container(
      height: MediaQuery.of(context).size.height * 0.85,
      decoration: const BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.vertical(top: Radius.circular(24)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Center(
            child: Container(
              margin: const EdgeInsets.symmetric(vertical: 12),
              width: 40,
              height: 4,
              decoration: BoxDecoration(
                color: Colors.grey.shade300,
                borderRadius: BorderRadius.circular(2),
              ),
            ),
          ),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text("Incentive Statement", style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                    Text("August 2026 • Transparent Mathematical Audit", style: TextStyle(fontSize: 12, color: Colors.black54)),
                  ],
                ),
                IconButton(
                  icon: const Icon(Icons.close),
                  onPressed: () => Navigator.pop(context),
                ),
              ],
            ),
          ),
          const Divider(),
          Expanded(
            child: ListView(
              padding: const EdgeInsets.all(20),
              children: [
                Container(
                  padding: const EdgeInsets.all(16),
                  decoration: BoxDecoration(
                    color: CareOsisColors.medicalEmeraldPrimary.withOpacity(0.08),
                    borderRadius: BorderRadius.circular(12),
                    border: Border.all(color: CareOsisColors.medicalEmeraldPrimary.withOpacity(0.2)),
                  ),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const Text("Total Payout (Estimated)", style: TextStyle(fontSize: 12, color: Colors.black54)),
                          const SizedBox(height: 4),
                          Text(
                            "₹${result.finalIncentive.toStringAsFixed(0)}",
                            style: const TextStyle(fontSize: 26, fontWeight: FontWeight.bold, color: CareOsisColors.medicalEmeraldPrimary),
                          ),
                        ],
                      ),
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
                        decoration: BoxDecoration(
                          color: CareOsisColors.statusOrange.withOpacity(0.15),
                          borderRadius: BorderRadius.circular(20),
                        ),
                        child: const Text("Estimated", style: TextStyle(color: CareOsisColors.statusOrange, fontWeight: FontWeight.bold, fontSize: 12)),
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 20),
                const Text("Core Performance Matrix", style: TextStyle(fontSize: 14, fontWeight: FontWeight.bold)),
                const SizedBox(height: 10),
                _buildRow("Target Assigned", "₹${result.target.toStringAsFixed(0)}"),
                _buildRow("Actual Sales Volume", "₹${result.actualSales.toStringAsFixed(0)}"),
                _buildRow("Achievement Percentage", "${result.achievementPercent.toStringAsFixed(1)}%"),
                _buildRow("Active Slab Tier", result.applicableSlab),
                _buildRow("Applied Incentive Rate", "${result.incentiveRate}%"),
                const SizedBox(height: 20),
                const Text("Itemized Component Breakdown", style: TextStyle(fontSize: 14, fontWeight: FontWeight.bold)),
                const SizedBox(height: 10),
                ...result.breakdownItems.map((item) => Card(
                      margin: const EdgeInsets.only(bottom: 8),
                      child: ListTile(
                        title: Text(item.title, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
                        subtitle: Text(item.description, style: const TextStyle(fontSize: 12)),
                        trailing: Text(
                          "₹${item.amount.toStringAsFixed(0)}",
                          style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14, color: CareOsisColors.medicalEmeraldPrimary),
                        ),
                      ),
                    )),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label, style: const TextStyle(fontSize: 13, color: Colors.black54)),
          Text(value, style: const TextStyle(fontSize: 13, fontWeight: FontWeight.bold)),
        ],
      ),
    );
  }
}
