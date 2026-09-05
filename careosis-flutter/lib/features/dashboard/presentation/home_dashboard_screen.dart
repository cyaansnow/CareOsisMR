import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:intl/intl.dart';
import '../../../core/theme/careosis_theme.dart';
import '../../../core/components/careosis_components.dart';
import '../../../data/repository/careosis_repository.dart';
import '../../../data/local/entities/doctor_and_mr_entities.dart';
import '../../../data/local/entities/commercial_entities.dart';
import 'incentive_breakdown_modal.dart';

class HomeDashboardScreen extends StatelessWidget {
  final CareOsisRepository repository;
  const HomeDashboardScreen({super.key, required this.repository});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: AppBar(
        backgroundColor: CareOsisColors.medicalEmeraldPrimary,
        elevation: 0,
        title: StreamBuilder<MRProfile?>(
          stream: repository.getMRProfile(),
          builder: (context, snapshot) {
            final profile = snapshot.data;
            return Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  profile?.name ?? "Aman Chhabra",
                  style: const TextStyle(fontSize: 17, fontWeight: FontWeight.bold, color: Colors.white),
                ),
                Text(
                  profile?.territory ?? "North Delhi & Rohini Central",
                  style: const TextStyle(fontSize: 12, color: Colors.white70),
                ),
              ],
            );
          },
        ),
        actions: [
          StreamBuilder<int>(
            stream: repository.getUnreadNotificationCount(),
            builder: (context, snapshot) {
              final count = snapshot.data ?? 0;
              return Stack(
                children: [
                  IconButton(
                    icon: const Icon(Icons.notifications_none, color: Colors.white),
                    onPressed: () => context.push('/notifications'),
                  ),
                  if (count > 0)
                    Positioned(
                      right: 8,
                      top: 8,
                      child: Container(
                        padding: const EdgeInsets.all(4),
                        decoration: const BoxDecoration(color: Colors.red, shape: BoxShape.circle),
                        child: Text(
                          "$count",
                          style: const TextStyle(color: Colors.white, fontSize: 10, fontWeight: FontWeight.bold),
                        ),
                      ),
                    ),
                ],
              );
            },
          ),
          IconButton(
            icon: const Icon(Icons.account_circle, color: Colors.white),
            onPressed: () => context.push('/profile'),
          ),
        ],
      ),
      body: StreamBuilder<int>(
        stream: repository.getPendingSyncCount(),
        builder: (context, syncSnapshot) {
          final pendingSync = syncSnapshot.data ?? 0;
          return Column(
            children: [
              CareOsisOfflineBanner(
                pendingCount: pendingSync,
                onSync: () => repository.performSync(),
              ),
              Expanded(
                child: RefreshIndicator(
                  onRefresh: () async => repository.performSync(),
                  child: SingleChildScrollView(
                    physics: const AlwaysScrollableScrollPhysics(),
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.stretch,
                      children: [
                        _buildIncentiveCard(context),
                        const SizedBox(height: 12),
                        _buildAttendanceBanner(context),
                        const SizedBox(height: 16),
                        _buildKpiGrid(context),
                        const SizedBox(height: 16),
                        _buildRouteCta(context),
                        const SizedBox(height: 16),
                        _buildQuickActions(context),
                        const SizedBox(height: 16),
                        _buildFollowUpsSection(context),
                      ],
                    ),
                  ),
                ),
              ),
            ],
          );
        },
      ),
    );
  }

  Widget _buildIncentiveCard(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          colors: [CareOsisColors.medicalEmeraldPrimary, Color(0xFF004D40)],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: CareOsisColors.medicalEmeraldPrimary.withOpacity(0.3),
            blurRadius: 10,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      padding: const EdgeInsets.all(18),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Row(
                children: [
                  Icon(Icons.workspace_premium, color: CareOsisColors.goldMetallic, size: 22),
                  SizedBox(width: 6),
                  Text(
                    "INCENTIVE THIS MONTH",
                    style: TextStyle(color: Colors.white70, fontSize: 12, fontWeight: FontWeight.bold, letterSpacing: 1),
                  ),
                ],
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                decoration: BoxDecoration(
                  color: Colors.white.withOpacity(0.18),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: const Text(
                  "Estimated",
                  style: TextStyle(color: Colors.white, fontSize: 11, fontWeight: FontWeight.bold),
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          const Row(
            crossAxisAlignment: CrossAxisAlignment.baseline,
            textBaseline: TextBaseline.alphabetic,
            children: [
              Text(
                "₹8,450",
                style: TextStyle(color: Colors.white, fontSize: 32, fontWeight: FontWeight.bold),
              ),
              SizedBox(width: 8),
              Text(
                "August 2026",
                style: TextStyle(color: Colors.white70, fontSize: 13),
              ),
            ],
          ),
          const SizedBox(height: 14),
          Container(
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: Colors.black.withOpacity(0.15),
              borderRadius: BorderRadius.circular(10),
            ),
            child: const Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                _IncentiveMetric(label: "Target", value: "₹2,00,000"),
                _IncentiveMetric(label: "Achievement", value: "82%"),
                _IncentiveMetric(label: "Incentive Rate", value: "3.0%"),
              ],
            ),
          ),
          const SizedBox(height: 14),
          SizedBox(
            width: double.infinity,
            child: OutlinedButton.icon(
              onPressed: () => IncentiveBreakdownModal.show(context, repository),
              icon: const Icon(Icons.analytics_outlined, size: 16, color: Colors.white),
              label: const Text("View Calculation Breakdown", style: TextStyle(color: Colors.white, fontSize: 13, fontWeight: FontWeight.bold)),
              style: OutlinedButton.styleFrom(
                side: const BorderSide(color: Colors.white38),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildKpiGrid(BuildContext context) {
    return StreamBuilder<MRProfile?>(
      stream: repository.getMRProfile(),
      builder: (context, snapshot) {
        final profile = snapshot.data;
        final visitsDone = profile?.completedVisitsToday ?? 12;
        final targetVisits = profile?.targetVisitsToday ?? 15;
        final sales = profile?.monthlySales ?? 164000.0;
        final target = profile?.monthlyTarget ?? 200000.0;
        final achPercent = target > 0 ? ((sales / target) * 100).toInt() : 0;

        return GridView.count(
          crossAxisCount: 2,
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          mainAxisSpacing: 12,
          crossAxisSpacing: 12,
          childAspectRatio: 1.5,
          children: [
            _KpiCard(
              title: "Calls Today",
              value: "$visitsDone / $targetVisits",
              subtitle: "${((visitsDone / targetVisits) * 100).toInt()}% Done",
              icon: Icons.person_pin_circle_outlined,
              color: CareOsisColors.medicalEmeraldPrimary,
              onTap: () => context.push('/visits'),
            ),
            _KpiCard(
              title: "MTD Sales",
              value: "₹${NumberFormat('#,##,###').format(sales)}",
              subtitle: "$achPercent% of ₹${(target / 100000).toStringAsFixed(1)}L",
              icon: Icons.trending_up,
              color: const Color(0xFF00875A),
              onTap: () => context.push('/performance'),
            ),
            _KpiCard(
              title: "Orders Booked",
              value: "6 Orders",
              subtitle: "₹42,500 Total",
              icon: Icons.shopping_bag_outlined,
              color: const Color(0xFF0052CC),
              onTap: () => context.push('/orders'),
            ),
            _KpiCard(
              title: "Academy Mastery",
              value: "78%",
              subtitle: "Expert MR Tier",
              icon: Icons.school_outlined,
              color: CareOsisColors.medicalTertiary,
              onTap: () => context.push('/academy'),
            ),
          ],
        );
      },
    );
  }

  Widget _buildRouteCta(BuildContext context) {
    return Card(
      elevation: 0,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
        side: const BorderSide(color: Color(0xFFE2E8F0)),
      ),
      child: ListTile(
        leading: Container(
          padding: const EdgeInsets.all(10),
          decoration: BoxDecoration(
            color: CareOsisColors.medicalEmeraldPrimary.withOpacity(0.1),
            borderRadius: BorderRadius.circular(10),
          ),
          child: const Icon(Icons.navigation_outlined, color: CareOsisColors.medicalEmeraldPrimary),
        ),
        title: const Text("Today's Field Beat Route", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
        subtitle: const Text("Shalimar Bagh & Rohini Beat (6 Doctors, 4 Chemists)", style: TextStyle(fontSize: 12)),
        trailing: const Icon(Icons.chevron_right),
        onTap: () => context.push('/routes'),
      ),
    );
  }

  Widget _buildAttendanceBanner(BuildContext context) {
    return StreamBuilder<MRProfile?>(
      stream: repository.getMRProfile(),
      builder: (context, snapshot) {
        final profile = snapshot.data;
        final isCheckedIn = profile?.isCheckedInToday ?? false;
        final checkInTime = profile?.checkInTime ?? "";

        return InkWell(
          onTap: () => context.push('/attendance'),
          borderRadius: BorderRadius.circular(12),
          child: Container(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
            decoration: BoxDecoration(
              color: isCheckedIn ? const Color(0xFFF0FDF4) : const Color(0xFFFEF2F2),
              borderRadius: BorderRadius.circular(12),
              border: Border.all(
                color: isCheckedIn ? const Color(0xFF86EFAC) : const Color(0xFFFECACA),
              ),
            ),
            child: Row(
              children: [
                Icon(
                  isCheckedIn ? Icons.verified_user : Icons.fingerprint,
                  color: isCheckedIn ? CareOsisColors.medicalEmeraldPrimary : const Color(0xFFDC2626),
                  size: 26,
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        isCheckedIn ? "Field Duty Active • GPS Tracking On" : "Field Duty Inactive • Not Checked In",
                        style: TextStyle(
                          fontWeight: FontWeight.bold,
                          fontSize: 13,
                          color: isCheckedIn ? const Color(0xFF166534) : const Color(0xFF991B1B),
                        ),
                      ),
                      Text(
                        isCheckedIn
                            ? "Checked in at $checkInTime. Tap to view duty log or check out."
                            : "Tap to record geotagged attendance and start GPS tracking.",
                        style: TextStyle(
                          fontSize: 11,
                          color: isCheckedIn ? const Color(0xFF15803D) : const Color(0xFFB91C1C),
                        ),
                      ),
                    ],
                  ),
                ),
                Icon(
                  Icons.arrow_forward_ios,
                  size: 14,
                  color: isCheckedIn ? const Color(0xFF166534) : const Color(0xFF991B1B),
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildQuickActions(BuildContext context) {
    final actions = [
      ("Attendance", Icons.fingerprint, '/attendance'),
      ("Prescribers", Icons.people_outline, '/doctors'),
      ("Start Visit", Icons.add_location_alt_outlined, '/visits'),
      ("POB Order", Icons.add_shopping_cart, '/orders/create'),
      ("Log Expense", Icons.receipt_long_outlined, '/expenses/add'),
      ("Field Route", Icons.navigation_outlined, '/routes'),
    ];

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text("Quick Actions", style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold)),
        const SizedBox(height: 10),
        GridView.builder(
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 3,
            mainAxisSpacing: 10,
            crossAxisSpacing: 10,
            childAspectRatio: 1.1,
          ),
          itemCount: actions.length,
          itemBuilder: (context, index) {
            final item = actions[index];
            return InkWell(
              onTap: () => context.push(item.$3),
              borderRadius: BorderRadius.circular(12),
              child: Container(
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(12),
                  border: Border.all(color: const Color(0xFFE2E8F0)),
                ),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(item.$2, color: CareOsisColors.medicalEmeraldPrimary, size: 26),
                    const SizedBox(height: 6),
                    Text(
                      item.$1,
                      style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w600),
                      textAlign: TextAlign.center,
                    ),
                  ],
                ),
              ),
            );
          },
        ),
      ],
    );
  }

  Widget _buildFollowUpsSection(BuildContext context) {
    return StreamBuilder<List<FollowUpModel>>(
      stream: repository.getPendingFollowUps(),
      builder: (context, snapshot) {
        final list = snapshot.data ?? [];
        if (list.isEmpty) return const SizedBox.shrink();

        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text("Pending Action Items", style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold)),
                Text("View All", style: TextStyle(fontSize: 12, color: CareOsisColors.medicalEmeraldPrimary, fontWeight: FontWeight.w600)),
              ],
            ),
            const SizedBox(height: 10),
            ListView.builder(
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              itemCount: list.length,
              itemBuilder: (context, index) {
                final item = list[index];
                return Card(
                  margin: const EdgeInsets.only(bottom: 8),
                  child: ListTile(
                    leading: CircleAvatar(
                      backgroundColor: CareOsisColors.medicalEmeraldPrimary.withOpacity(0.1),
                      child: const Icon(Icons.event_note, color: CareOsisColors.medicalEmeraldPrimary, size: 20),
                    ),
                    title: Text(item.personName, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
                    subtitle: Text(item.reason, maxLines: 1, overflow: TextOverflow.ellipsis, style: const TextStyle(fontSize: 12)),
                    trailing: CareOsisStatusChip(label: item.priority),
                    onTap: () => context.push('/follow-ups'),
                  ),
                );
              },
            ),
          ],
        );
      },
    );
  }
}

class _IncentiveMetric extends StatelessWidget {
  final String label;
  final String value;
  const _IncentiveMetric({required this.label, required this.value});

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(label, style: const TextStyle(color: Colors.white60, fontSize: 11)),
        const SizedBox(height: 2),
        Text(value, style: const TextStyle(color: Colors.white, fontSize: 13, fontWeight: FontWeight.bold)),
      ],
    );
  }
}

class _KpiCard extends StatelessWidget {
  final String title;
  final String value;
  final String subtitle;
  final IconData icon;
  final Color color;
  final VoidCallback onTap;

  const _KpiCard({
    required this.title,
    required this.value,
    required this.subtitle,
    required this.icon,
    required this.color,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(12),
      child: Container(
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: const Color(0xFFE2E8F0)),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(title, style: const TextStyle(color: Colors.black54, fontSize: 12, fontWeight: FontWeight.w500)),
                Icon(icon, color: color, size: 18),
              ],
            ),
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(value, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                Text(subtitle, style: TextStyle(color: color, fontSize: 11, fontWeight: FontWeight.w600)),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
