import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../../../core/theme/careosis_theme.dart';
import '../../../core/components/careosis_components.dart';
import '../../../data/repository/careosis_repository.dart';
import '../../../core/engine/incentive_calculation_engine.dart';
import '../../../core/services/supabase_sync_service.dart';
import '../../../data/local/entities/platform_entities.dart';
import '../../../data/local/entities/doctor_and_mr_entities.dart';

class PerformanceScreen extends StatelessWidget {
  final CareOsisRepository repository;
  const PerformanceScreen({super.key, required this.repository});

  @override
  Widget build(BuildContext context) {
    final now = DateTime.now();
    final currentMonth = DateFormat("MMMM yyyy").format(now);
    final lastDay = DateTime(now.year, now.month + 1, 0);
    final daysRemaining = lastDay.day - now.day;

    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: const CareOsisTopBar(title: "Target & Incentive Analytics"),
      body: StreamBuilder<MRProfile?>(
        stream: repository.getMRProfile(),
        builder: (context, snapshot) {
          final profile = snapshot.data;
          final target = profile?.monthlyTarget ?? 200000.0;
          final sales = profile?.monthlySales ?? 0.0;
          final percent = target > 0 ? ((sales / target) * 100) : 0.0;
          final progressRatio = (percent / 100.0).clamp(0.0, 1.0);

          const defaultRule = IncentiveRuleModel(
            id: "RULE-SLAB-CAREOSIS-Q3",
            ruleName: "Standard CareOsis Field Slab Policy",
            ruleType: "PERCENTAGE_OF_SALES",
            defaultTarget: 200000.0,
            updatedAt: 1788590000,
          );

          final res = IncentiveCalculationEngine.calculateIncentive(
            input: CalculationInput(
              employeeId: profile?.empId ?? repository.currentUser?.id ?? "MR",
              employeeName: profile?.name ?? "Field MR",
              employeeMonthlyTarget: target,
              period: currentMonth,
              actualSales: sales,
            ),
            rule: defaultRule,
          );

          return SingleChildScrollView(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                // Monthly Target Card
                Container(
                  padding: const EdgeInsets.all(18),
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(16),
                    border: Border.all(color: const Color(0xFFE2E8F0)),
                  ),
                  child: Column(
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Text(
                            "$currentMonth Target",
                            style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w600, color: Colors.black54),
                          ),
                          Container(
                            padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                            decoration: BoxDecoration(
                              color: percent >= 100 ? Colors.green.shade50 : Colors.blue.shade50,
                              borderRadius: BorderRadius.circular(6),
                            ),
                            child: Text(
                              percent >= 100 ? "🎯 Target Achieved" : "$daysRemaining Days Left",
                              style: TextStyle(
                                fontSize: 11,
                                fontWeight: FontWeight.bold,
                                color: percent >= 100 ? Colors.green.shade700 : CareOsisColors.medicalEmeraldPrimary,
                              ),
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 10),
                      Text(
                        "₹${NumberFormat('#,##,###').format(sales.toInt())} / ₹${NumberFormat('#,##,###').format(target.toInt())}",
                        style: const TextStyle(
                          fontSize: 24,
                          fontWeight: FontWeight.bold,
                          color: CareOsisColors.medicalEmeraldPrimary,
                        ),
                      ),
                      const SizedBox(height: 12),
                      ClipRRect(
                        borderRadius: BorderRadius.circular(6),
                        child: LinearProgressIndicator(
                          value: progressRatio,
                          backgroundColor: const Color(0xFFE2E8F0),
                          color: percent >= 100 ? CareOsisColors.medicalEmeraldPrimary : CareOsisColors.statusOrange,
                          minHeight: 10,
                        ),
                      ),
                      const SizedBox(height: 8),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Text(
                            "${percent.toStringAsFixed(1)}% Achieved",
                            style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold),
                          ),
                          Text(
                            "Shortfall: ₹${(target - sales > 0 ? target - sales : 0).toStringAsFixed(0)}",
                            style: TextStyle(fontSize: 12, color: Colors.grey.shade700),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 16),

                // Live Estimated Incentive Payout Card
                Container(
                  padding: const EdgeInsets.all(18),
                  decoration: BoxDecoration(
                    gradient: const LinearGradient(
                      colors: [CareOsisColors.medicalEmeraldPrimary, Color(0xFF0F766E)],
                      begin: Alignment.topLeft,
                      end: Alignment.bottomRight,
                    ),
                    borderRadius: BorderRadius.circular(16),
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          const Text(
                            "CURRENT EARNED INCENTIVE",
                            style: TextStyle(color: Colors.white70, fontSize: 11, fontWeight: FontWeight.bold, letterSpacing: 0.5),
                          ),
                          Container(
                            padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                            decoration: BoxDecoration(
                              color: Colors.white.withOpacity(0.2),
                              borderRadius: BorderRadius.circular(6),
                            ),
                            child: Text(
                              "Tier: ${res.applicableSlab}",
                              style: const TextStyle(color: Colors.white, fontSize: 11, fontWeight: FontWeight.bold),
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 8),
                      Text(
                        "₹${NumberFormat('#,##,###').format(res.finalIncentive.toInt())}",
                        style: const TextStyle(color: Colors.white, fontSize: 32, fontWeight: FontWeight.bold),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        "Incentive Rate: ${res.incentiveRate.toStringAsFixed(1)}% of net sales",
                        style: const TextStyle(color: Colors.white70, fontSize: 12),
                      ),
                      const Divider(color: Colors.white24, height: 20),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          const Text(
                            "Base Incentive:",
                            style: TextStyle(color: Colors.white70, fontSize: 12),
                          ),
                          Text(
                            "₹${res.baseIncentive.toStringAsFixed(0)}",
                            style: const TextStyle(color: Colors.white, fontSize: 12, fontWeight: FontWeight.bold),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 16),

                // Incentive Policy Slab Breakdown Table
                Card(
                  elevation: 0,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                    side: BorderSide(color: Colors.grey.shade200),
                  ),
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const Row(
                          children: [
                            Icon(Icons.layers_outlined, size: 18, color: CareOsisColors.medicalEmeraldPrimary),
                            SizedBox(width: 6),
                            Text(
                              "Incentive Slab Policy Structure",
                              style: TextStyle(fontSize: 14, fontWeight: FontWeight.bold),
                            ),
                          ],
                        ),
                        const SizedBox(height: 12),
                        _buildSlabRow("< 70%", "0.0%", "Minimum threshold not met", percent < 70),
                        _buildSlabRow("70% - 89%", "2.0%", "Standard field rate", percent >= 70 && percent < 90),
                        _buildSlabRow("90% - 99%", "3.0%", "High performer bonus", percent >= 90 && percent < 100),
                        _buildSlabRow("100% - 119%", "5.0%", "100% Target Achiever Club", percent >= 100 && percent < 120),
                        _buildSlabRow("≥ 120%", "7.5%", "Super-Achiever Max Bonus", percent >= 120),
                      ],
                    ),
                  ),
                ),
                const SizedBox(height: 16),

                // Supabase Cloud Sync Action
                ElevatedButton.icon(
                  onPressed: () async {
                    final mrId = profile?.empId ?? repository.currentUser?.id ?? "MR";
                    final success = await SupabaseSyncService.instance.syncTargetIncentive(
                      mrId: mrId,
                      month: currentMonth,
                      targetAmount: target,
                      achievedAmount: sales,
                      incentiveEarned: res.finalIncentive,
                      payoutStatus: percent >= 100 ? "QUALIFIED" : "IN_PROGRESS",
                    );

                    if (context.mounted) {
                      ScaffoldMessenger.of(context).showSnackBar(
                        SnackBar(
                          content: Text(success
                              ? "Monthly targets & incentive calculation pushed to Supabase Cloud!"
                              : "Calculations updated locally (Offline mode active)."),
                          backgroundColor: success ? CareOsisColors.medicalEmeraldPrimary : Colors.blueGrey,
                        ),
                      );
                    }
                  },
                  icon: const Icon(Icons.cloud_upload_outlined),
                  label: const Text("Sync Target & Payout with Cloud"),
                  style: ElevatedButton.styleFrom(
                    padding: const EdgeInsets.symmetric(vertical: 14),
                    backgroundColor: CareOsisColors.medicalEmeraldPrimary,
                    foregroundColor: Colors.white,
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                  ),
                ),
              ],
            ),
          );
        },
      ),
    );
  }

  Widget _buildSlabRow(String slabRange, String payoutRate, String description, bool isCurrent) {
    return Container(
      margin: const EdgeInsets.only(bottom: 8),
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 8),
      decoration: BoxDecoration(
        color: isCurrent ? CareOsisColors.medicalEmeraldPrimary.withOpacity(0.08) : Colors.transparent,
        borderRadius: BorderRadius.circular(6),
        border: isCurrent ? Border.all(color: CareOsisColors.medicalEmeraldPrimary.withOpacity(0.4)) : null,
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Row(
            children: [
              if (isCurrent)
                const Icon(Icons.arrow_right, color: CareOsisColors.medicalEmeraldPrimary, size: 18),
              Text(
                slabRange,
                style: TextStyle(
                  fontSize: 13,
                  fontWeight: isCurrent ? FontWeight.bold : FontWeight.w500,
                  color: isCurrent ? CareOsisColors.medicalEmeraldPrimary : Colors.black87,
                ),
              ),
            ],
          ),
          Text(
            description,
            style: TextStyle(fontSize: 11, color: Colors.grey.shade600),
          ),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
            decoration: BoxDecoration(
              color: isCurrent ? CareOsisColors.medicalEmeraldPrimary : Colors.grey.shade200,
              borderRadius: BorderRadius.circular(4),
            ),
            child: Text(
              payoutRate,
              style: TextStyle(
                fontSize: 11,
                fontWeight: FontWeight.bold,
                color: isCurrent ? Colors.white : Colors.black87,
              ),
            ),
          ),
        ],
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
  double _salesSim = 160000.0;
  bool _initialized = false;

  @override
  Widget build(BuildContext context) {
    return StreamBuilder<MRProfile?>(
      stream: widget.repository.getMRProfile(),
      builder: (context, snapshot) {
        final profile = snapshot.data;
        final target = profile?.monthlyTarget ?? 200000.0;
        final currentMonth = DateFormat("MMMM yyyy").format(DateTime.now());

        if (!_initialized && profile != null && profile.monthlySales > 0) {
          _salesSim = profile.monthlySales;
          _initialized = true;
        }

        const defaultRule = IncentiveRuleModel(
          id: "RULE-SLAB-CAREOSIS-Q3",
          ruleName: "Standard CareOsis Q3 Slab Policy",
          ruleType: "PERCENTAGE_OF_SALES",
          defaultTarget: 200000.0,
          updatedAt: 1788590000,
        );

        final res = IncentiveCalculationEngine.calculateIncentive(
          input: CalculationInput(
            employeeId: profile?.empId ?? widget.repository.currentUser?.id ?? "MR",
            employeeName: profile?.name ?? "Field MR",
            employeeMonthlyTarget: target,
            period: currentMonth,
            actualSales: _salesSim,
          ),
          rule: defaultRule,
        );

        final simPercent = target > 0 ? (_salesSim / target) * 100 : 0.0;

        return Scaffold(
          backgroundColor: const Color(0xFFF8FAFC),
          appBar: const CareOsisTopBar(title: "Live Incentive Simulator", showBack: true),
          body: SingleChildScrollView(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                // Estimated Payout Display
                Container(
                  padding: const EdgeInsets.all(20),
                  decoration: BoxDecoration(
                    gradient: const LinearGradient(
                      colors: [CareOsisColors.medicalEmeraldPrimary, Color(0xFF0D9488)],
                      begin: Alignment.topLeft,
                      end: Alignment.bottomRight,
                    ),
                    borderRadius: BorderRadius.circular(16),
                  ),
                  child: Column(
                    children: [
                      const Text(
                        "SIMULATED ESTIMATED PAYOUT",
                        style: TextStyle(color: Colors.white70, fontSize: 11, fontWeight: FontWeight.bold, letterSpacing: 0.5),
                      ),
                      const SizedBox(height: 6),
                      Text(
                        "₹${NumberFormat('#,##,###').format(res.finalIncentive.toInt())}",
                        style: const TextStyle(color: Colors.white, fontSize: 36, fontWeight: FontWeight.bold),
                      ),
                      const SizedBox(height: 6),
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                        decoration: BoxDecoration(
                          color: Colors.white.withOpacity(0.2),
                          borderRadius: BorderRadius.circular(8),
                        ),
                        child: Text(
                          "Tier: ${res.applicableSlab} • Payout Rate: ${res.incentiveRate}%",
                          style: const TextStyle(color: Colors.white, fontSize: 12, fontWeight: FontWeight.bold),
                        ),
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 20),

                // Interactive Simulator Slider
                Card(
                  elevation: 0,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                    side: BorderSide(color: Colors.grey.shade200),
                  ),
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            const Text("Adjust Simulated Sales:", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                            Text(
                              "₹${NumberFormat('#,##,###').format(_salesSim.toInt())}",
                              style: const TextStyle(
                                fontWeight: FontWeight.bold,
                                fontSize: 16,
                                color: CareOsisColors.medicalEmeraldPrimary,
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: 4),
                        Text(
                          "Achievement: ${simPercent.toStringAsFixed(1)}% of Target (₹${NumberFormat('#,##,###').format(target.toInt())})",
                          style: TextStyle(fontSize: 12, color: Colors.grey.shade700),
                        ),
                        const SizedBox(height: 12),
                        Slider(
                          value: _salesSim,
                          min: 0.0,
                          max: target * 1.6,
                          divisions: 32,
                          activeColor: CareOsisColors.medicalEmeraldPrimary,
                          onChanged: (v) => setState(() => _salesSim = v),
                        ),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            const Text("₹0", style: TextStyle(fontSize: 11, color: Colors.grey)),
                            Text("₹${(target).toStringAsFixed(0)} (100%)", style: const TextStyle(fontSize: 11, color: Colors.grey)),
                            Text("₹${(target * 1.6).toStringAsFixed(0)} (160%)", style: const TextStyle(fontSize: 11, color: Colors.grey)),
                          ],
                        ),
                      ],
                    ),
                  ),
                ),
                const SizedBox(height: 16),

                // Quick Preset Buttons
                Row(
                  children: [
                    Expanded(
                      child: OutlinedButton(
                        onPressed: () => setState(() => _salesSim = target * 0.8),
                        child: const Text("80% Quota"),
                      ),
                    ),
                    const SizedBox(width: 8),
                    Expanded(
                      child: OutlinedButton(
                        onPressed: () => setState(() => _salesSim = target * 1.0),
                        child: const Text("100% Quota"),
                      ),
                    ),
                    const SizedBox(width: 8),
                    Expanded(
                      child: OutlinedButton(
                        onPressed: () => setState(() => _salesSim = target * 1.25),
                        child: const Text("125% Quota"),
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        );
      },
    );
  }
}

class LeaderboardScreen extends StatelessWidget {
  final CareOsisRepository repository;
  const LeaderboardScreen({super.key, required this.repository});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: const CareOsisTopBar(title: "Zonal MR Leaderboard", showBack: true),
      body: StreamBuilder<List<LeaderboardModel>>(
        stream: repository.getLeaderboard(),
        builder: (context, snapshot) {
          final list = snapshot.data ?? [];
          if (list.isEmpty) {
            return const CareOsisEmptyState(
              icon: Icons.leaderboard_outlined,
              title: "No Leaderboard Data",
              message: "Monthly zonal rankings will display here as representatives achieve their quotas.",
            );
          }

          return ListView.builder(
            padding: const EdgeInsets.all(12),
            itemCount: list.length,
            itemBuilder: (context, index) {
              final item = list[index];
              return Card(
                elevation: 0,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                  side: BorderSide(color: Colors.grey.shade200),
                ),
                margin: const EdgeInsets.only(bottom: 8),
                child: ListTile(
                  leading: CircleAvatar(
                    backgroundColor: item.rank == 1 ? CareOsisColors.goldMetallic : CareOsisColors.medicalEmeraldPrimary,
                    child: Text("${item.rank}", style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
                  ),
                  title: Text(item.mrName, style: const TextStyle(fontWeight: FontWeight.bold)),
                  subtitle: Text("${item.territory} • ${item.achievementPercent.toStringAsFixed(0)}% Achieved"),
                  trailing: Text(
                    "${item.points} pts",
                    style: const TextStyle(fontWeight: FontWeight.bold, color: CareOsisColors.medicalEmeraldPrimary),
                  ),
                ),
              );
            },
          );
        },
      ),
    );
  }
}
