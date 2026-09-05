import 'package:flutter/material.dart';
import '../../../core/theme/careosis_theme.dart';
import '../../../core/components/careosis_components.dart';
import '../../../data/repository/careosis_repository.dart';
import '../../../core/engine/incentive_calculation_engine.dart';
import '../../../data/local/entities/platform_entities.dart';

class PerformanceScreen extends StatelessWidget {
  final CareOsisRepository repository;
  const PerformanceScreen({super.key, required this.repository});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: const CareOsisTopBar(title: "Field Performance Analytics"),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Container(
              padding: const EdgeInsets.all(18),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(16),
                border: Border.all(color: const Color(0xFFE2E8F0)),
              ),
              child: const Column(
                children: [
                  Text("August 2026 Monthly Target Progress", style: TextStyle(fontSize: 13, color: Colors.black54)),
                  SizedBox(height: 6),
                  Text("₹1,64,000 / ₹2,00,000", style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold, color: CareOsisColors.medicalEmeraldPrimary)),
                  SizedBox(height: 8),
                  LinearProgressIndicator(value: 0.82, backgroundColor: Color(0xFFE2E8F0), color: CareOsisColors.medicalEmeraldPrimary, minHeight: 8),
                  SizedBox(height: 6),
                  Text("82.0% Achieved • 10 Days Remaining", style: TextStyle(fontSize: 12, fontWeight: FontWeight.w600, color: CareOsisColors.statusOrange)),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class IncentiveScreen extends StatefulWidget {
  final CareOsisRepository repository;
  const IncentiveScreen({super.key, required this.repository});

  @override
  State<IncentiveScreen> createState() => _IncentiveScreenState();
}

class _IncentiveScreenState extends State<IncentiveScreen> {
  double _salesSim = 164000.0;

  @override
  Widget build(BuildContext context) {
    const defaultRule = IncentiveRuleModel(
      id: "RULE-SLAB-AUG2026-V1",
      ruleName: "Standard CareOsis Q3 Slab Policy",
      ruleType: "PERCENTAGE_OF_SALES",
      defaultTarget: 200000.0,
      updatedAt: 1700000000,
    );

    final res = IncentiveCalculationEngine.calculateIncentive(
      input: CalculationInput(
        employeeId: "CO-MR-8492",
        employeeName: "Aman Chhabra",
        employeeMonthlyTarget: 200000.0,
        period: "August 2026",
        actualSales: _salesSim,
      ),
      rule: defaultRule,
    );

    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: const CareOsisTopBar(title: "Live Incentive Simulator", showBack: true),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Container(
              padding: const EdgeInsets.all(18),
              decoration: BoxDecoration(
                color: CareOsisColors.medicalEmeraldPrimary,
                borderRadius: BorderRadius.circular(16),
              ),
              child: Column(
                children: [
                  const Text("Estimated Payout", style: TextStyle(color: Colors.white70, fontSize: 12)),
                  const SizedBox(height: 4),
                  Text("₹${res.finalIncentive.toStringAsFixed(0)}", style: const TextStyle(color: Colors.white, fontSize: 32, fontWeight: FontWeight.bold)),
                  const SizedBox(height: 4),
                  Text("Tier: ${res.applicableSlab} (${res.incentiveRate}%)", style: const TextStyle(color: Colors.white, fontSize: 13, fontWeight: FontWeight.w600)),
                ],
              ),
            ),
            const SizedBox(height: 20),
            Card(
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text("Simulate Sales: ₹${_salesSim.toStringAsFixed(0)}", style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                    Slider(
                      value: _salesSim,
                      min: 0.0,
                      max: 400000.0,
                      divisions: 40,
                      activeColor: CareOsisColors.medicalEmeraldPrimary,
                      onChanged: (v) => setState(() => _salesSim = v),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class LeaderboardScreen extends StatelessWidget {
  final CareOsisRepository repository;
  const LeaderboardScreen({super.key, required this.repository});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CareOsisTopBar(title: "Zonal MR Leaderboard", showBack: true),
      body: StreamBuilder<List<LeaderboardModel>>(
        stream: repository.getLeaderboard(),
        builder: (context, snapshot) {
          final list = snapshot.data ?? [];
          return ListView.builder(
            padding: const EdgeInsets.all(12),
            itemCount: list.length,
            itemBuilder: (context, index) {
              final item = list[index];
              return Card(
                child: ListTile(
                  leading: CircleAvatar(
                    backgroundColor: item.rank == 1 ? CareOsisColors.goldMetallic : CareOsisColors.medicalEmeraldPrimary,
                    child: Text("${item.rank}", style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
                  ),
                  title: Text(item.mrName, style: const TextStyle(fontWeight: FontWeight.bold)),
                  subtitle: Text("${item.territory} • ${item.achievementPercent.toStringAsFixed(0)}% Achieved"),
                  trailing: Text("${item.points} pts", style: const TextStyle(fontWeight: FontWeight.bold, color: CareOsisColors.medicalEmeraldPrimary)),
                ),
              );
            },
          );
        },
      ),
    );
  }
}
