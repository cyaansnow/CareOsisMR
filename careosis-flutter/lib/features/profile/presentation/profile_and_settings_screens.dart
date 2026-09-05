import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../core/theme/careosis_theme.dart';
import '../../../core/components/careosis_components.dart';
import '../../../data/repository/careosis_repository.dart';
import '../../../data/local/entities/doctor_and_mr_entities.dart';
import '../../../data/local/entities/platform_entities.dart';
import '../../../core/ai/ai_assistant.dart';

class ProfileScreen extends StatelessWidget {
  final CareOsisRepository repository;
  const ProfileScreen({super.key, required this.repository});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: const CareOsisTopBar(title: "Field Representative Profile"),
      body: StreamBuilder<MRProfile?>(
        stream: repository.getMRProfile(),
        builder: (context, snapshot) {
          final p = snapshot.data;
          final user = repository.currentUser;

          return SingleChildScrollView(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Card(
                  child: Padding(
                    padding: const EdgeInsets.all(18),
                    child: Column(
                      children: [
                        CircleAvatar(
                          radius: 36,
                          backgroundColor: CareOsisColors.medicalEmeraldPrimary,
                          child: Text(
                            (p?.name ?? "MR").substring(0, 1),
                            style: const TextStyle(fontSize: 28, color: Colors.white, fontWeight: FontWeight.bold),
                          ),
                        ),
                        const SizedBox(height: 12),
                        Text(p?.name ?? "Aman Chhabra", style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                        Text(p?.designation ?? "Senior Medical Representative", style: const TextStyle(fontSize: 13, color: Colors.black54)),
                        const SizedBox(height: 6),
                        CareOsisStatusChip(label: user?.role ?? "EMPLOYEE"),
                      ],
                    ),
                  ),
                ),
                const SizedBox(height: 16),
                _buildMenuItem(Icons.analytics_outlined, "Live Incentive Simulator", () => context.push('/incentives')),
                _buildMenuItem(Icons.leaderboard_outlined, "Zonal MR Leaderboard", () => context.push('/leaderboard')),
                _buildMenuItem(Icons.support_agent_outlined, "AI Prescriber Objection Desk", () => context.push('/help')),
                _buildMenuItem(Icons.notifications_outlined, "Headquarters Broadcasts", () => context.push('/notifications')),
                if (user?.role == "ADMIN" || user?.role == "SUPER_ADMIN")
                  _buildMenuItem(Icons.admin_panel_settings_outlined, "Regional Admin Command", () => context.push('/admin/dashboard')),
                if (user?.role == "SUPER_ADMIN")
                  _buildMenuItem(Icons.security_outlined, "Super Admin Master Hub", () => context.push('/super-admin')),
                const SizedBox(height: 16),
                OutlinedButton.icon(
                  onPressed: () {
                    repository.setCurrentUser(null);
                    context.go('/login');
                  },
                  icon: const Icon(Icons.logout, color: Colors.red),
                  label: const Text("Sign Out", style: TextStyle(color: Colors.red, fontWeight: FontWeight.bold)),
                  style: OutlinedButton.styleFrom(side: const BorderSide(color: Colors.red)),
                ),
              ],
            ),
          );
        },
      ),
    );
  }

  Widget _buildMenuItem(IconData icon, String title, VoidCallback onTap) {
    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      child: ListTile(
        leading: Icon(icon, color: CareOsisColors.medicalEmeraldPrimary),
        title: Text(title, style: const TextStyle(fontSize: 14, fontWeight: FontWeight.w600)),
        trailing: const Icon(Icons.chevron_right, size: 20),
        onTap: onTap,
      ),
    );
  }
}

class HelpSupportScreen extends StatefulWidget {
  final CareOsisRepository repository;
  const HelpSupportScreen({super.key, required this.repository});

  @override
  State<HelpSupportScreen> createState() => _HelpSupportScreenState();
}

class _HelpSupportScreenState extends State<HelpSupportScreen> {
  final _aiEngine = LocalCareOsisAIEngine();
  final _queryController = TextEditingController();
  ObjectionResolution? _resolution;
  bool _isLoading = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CareOsisTopBar(title: "AI Prescriber Objection Desk", showBack: true),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Card(
              color: CareOsisColors.medicalEmeraldPrimary.withOpacity(0.06),
              child: const Padding(
                padding: EdgeInsets.all(14),
                child: Text(
                  "Enter any prescriber skepticism or clinical price objection to synthesize an immediate, trial-backed scientific counter-pitch.",
                  style: TextStyle(fontSize: 13, height: 1.4),
                ),
              ),
            ),
            const SizedBox(height: 16),
            TextField(
              controller: _queryController,
              decoration: const InputDecoration(
                labelText: "Prescriber Objection / Question",
                hintText: "e.g. Doctor said Calci Fizz is expensive vs generic tablets",
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 12),
            ElevatedButton.icon(
              onPressed: () async {
                if (_queryController.text.trim().isEmpty) return;
                setState(() => _isLoading = true);
                final res = await _aiEngine.resolveDoctorObjection("Calci Fizz", _queryController.text.trim());
                setState(() {
                  _resolution = res;
                  _isLoading = false;
                });
              },
              icon: _isLoading ? const SizedBox(width: 16, height: 16, child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white)) : const Icon(Icons.psychology_outlined),
              label: Text(_isLoading ? "Synthesizing Counter-Pitch..." : "Generate Scientific Pitch"),
            ),
            if (_resolution != null) ...[
              const SizedBox(height: 20),
              Card(
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text("Synthesized Clinical Response", style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold)),
                      const Divider(height: 16),
                      Text(_resolution!.suggestedPitch, style: const TextStyle(fontSize: 13, height: 1.5, fontWeight: FontWeight.w500)),
                      const SizedBox(height: 12),
                      Text("Scientific Evidence: ${_resolution!.scientificCounterPoint}", style: const TextStyle(fontSize: 12, color: CareOsisColors.medicalEmeraldPrimary)),
                    ],
                  ),
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }
}

class NotificationsScreen extends StatelessWidget {
  final CareOsisRepository repository;
  const NotificationsScreen({super.key, required this.repository});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CareOsisTopBar(title: "Broadcasts & Alerts", showBack: true),
      body: StreamBuilder<List<NotificationModel>>(
        stream: repository.getAllNotifications(),
        builder: (context, snapshot) {
          final list = snapshot.data ?? [];
          if (list.isEmpty) return const Center(child: Text("No alerts or broadcasts."));

          return ListView.builder(
            padding: const EdgeInsets.all(12),
            itemCount: list.length,
            itemBuilder: (context, index) {
              final n = list[index];
              return Card(
                child: ListTile(
                  title: Text(n.title, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
                  subtitle: Text("${n.timeFormatted}\n${n.message}", style: const TextStyle(fontSize: 12)),
                  trailing: CareOsisStatusChip(label: n.type),
                ),
              );
            },
          );
        },
      ),
    );
  }
}
