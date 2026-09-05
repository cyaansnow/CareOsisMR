import 'package:flutter/material.dart';
import '../../../core/theme/careosis_theme.dart';
import '../../../core/components/careosis_components.dart';
import '../../../data/repository/careosis_repository.dart';
import '../../../data/local/entities/admin_and_security_entities.dart';
import '../../../data/local/entities/commercial_entities.dart';
import '../../../core/engine/incentive_calculation_engine.dart';
import '../../super_admin/presentation/super_admin_screen.dart';
import 'widgets/admin_incentive_builder_modal.dart';
import 'widgets/admin_approval_dialog.dart';

class AdminDashboardScreen extends StatefulWidget {
  final CareOsisRepository repository;
  const AdminDashboardScreen({super.key, required this.repository});

  @override
  State<AdminDashboardScreen> createState() => _AdminDashboardScreenState();
}

class _AdminDashboardScreenState extends State<AdminDashboardScreen> {
  int _logoTapCount = 0;
  String _selectedApprovalFilter = "ALL";

  void _onLogoTap() {
    setState(() => _logoTapCount++);
    if (_logoTapCount >= 5) {
      _logoTapCount = 0;
      _showSuperAdminGateway();
    }
  }

  void _showSuperAdminGateway() {
    final passwordController = TextEditingController();
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Row(
          children: [
            Icon(Icons.security, color: Color(0xFF0F172A)),
            SizedBox(width: 8),
            Text("Super Admin Gateway", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
          ],
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text("Enter Master Passkey to access Global Governance Hub:", style: TextStyle(fontSize: 12)),
            const SizedBox(height: 10),
            TextField(
              controller: passwordController,
              obscureText: true,
              decoration: const InputDecoration(labelText: "Master Passkey", hintText: "SuperAdmin@2026"),
            ),
          ],
        ),
        actions: [
          TextButton(onPressed: () => Navigator.of(ctx).pop(), child: const Text("Cancel")),
          ElevatedButton(
            style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF0F172A), foregroundColor: Colors.white),
            onPressed: () {
              if (passwordController.text.trim() == "SuperAdmin@2026" || passwordController.text.trim() == "Admin@123") {
                Navigator.of(ctx).pop();
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (_) => SuperAdminScreen(repository: widget.repository)),
                );
              } else {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text("Invalid Master Passkey"), backgroundColor: Colors.red),
                );
              }
            },
            child: const Text("Unlock"),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final user = widget.repository.currentUser;
    final regionLabel = user?.assignedRegionIds.contains("DELHI") == true || user?.assignedRegionIds.contains("REG-001") == true
        ? "Delhi NCR (NCR)"
        : "Assigned Region (${user?.assignedRegionIds ?? 'GLOBAL'})";

    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: AppBar(
        backgroundColor: CareOsisColors.medicalEmeraldPrimary,
        foregroundColor: Colors.white,
        title: GestureDetector(
          onTap: _onLogoTap,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text("Regional Admin Command", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
              Text("$regionLabel • ${user?.name ?? 'Admin'}", style: const TextStyle(fontSize: 11, color: Colors.white70)),
            ],
          ),
        ),
        actions: [
          IconButton(
            tooltip: "Executive Super Admin Hub",
            icon: const Icon(Icons.admin_panel_settings_outlined, color: Colors.white),
            onPressed: _showSuperAdminGateway,
          ),
          IconButton(
            tooltip: "Rule Configurator",
            icon: const Icon(Icons.tune, color: Colors.white),
            onPressed: () => AdminIncentiveBuilderModal.show(context, widget.repository),
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            // Scoped Overview Header
            Container(
              padding: const EdgeInsets.all(18),
              decoration: BoxDecoration(
                gradient: const LinearGradient(
                  colors: [CareOsisColors.medicalEmeraldPrimary, Color(0xFF0F766E)],
                ),
                borderRadius: BorderRadius.circular(16),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text("REGIONAL FIELD SCOPE", style: TextStyle(color: Colors.white70, fontSize: 11, fontWeight: FontWeight.bold)),
                  const SizedBox(height: 6),
                  Text("$regionLabel Operations", style: const TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold)),
                  const SizedBox(height: 4),
                  const Text("Scope: Active Reps • 185 Doctors • 2 Assigned Territories", style: TextStyle(color: Colors.white70, fontSize: 12)),
                ],
              ),
            ),
            const SizedBox(height: 20),

            // PENDING APPROVALS OPERATIONS CENTER
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Row(
                  children: [
                    Icon(Icons.pending_actions, color: CareOsisColors.medicalEmeraldPrimary, size: 20),
                    SizedBox(width: 6),
                    Text("Pending Approvals Hub", style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold)),
                  ],
                ),
                StreamBuilder<List<ApprovalRequest>>(
                  stream: widget.repository.getScopedPendingApprovals(user),
                  builder: (context, snapshot) {
                    final count = snapshot.data?.length ?? 0;
                    return Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                      decoration: BoxDecoration(
                        color: count > 0 ? Colors.orange.shade800 : Colors.green,
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: Text(
                        "$count Pending",
                        style: const TextStyle(color: Colors.white, fontSize: 11, fontWeight: FontWeight.bold),
                      ),
                    );
                  },
                ),
              ],
            ),
            const SizedBox(height: 10),

            // Filter Chips for Approvals
            SingleChildScrollView(
              scrollDirection: Axis.horizontal,
              child: Row(
                children: ["ALL", "EXPENSE", "ATTENDANCE", "DOCTOR_VISIT", "TARGET", "INCENTIVE"].map((filter) {
                  final isSelected = _selectedApprovalFilter == filter;
                  return Padding(
                    padding: const EdgeInsets.only(right: 6),
                    child: FilterChip(
                      selected: isSelected,
                      label: Text(filter, style: TextStyle(fontSize: 11, fontWeight: isSelected ? FontWeight.bold : FontWeight.normal)),
                      selectedColor: CareOsisColors.medicalEmeraldPrimary.withOpacity(0.2),
                      onSelected: (_) => setState(() => _selectedApprovalFilter = filter),
                    ),
                  );
                }).toList(),
              ),
            ),
            const SizedBox(height: 8),

            StreamBuilder<List<ApprovalRequest>>(
              stream: widget.repository.getScopedPendingApprovals(user),
              builder: (context, snapshot) {
                final allPending = snapshot.data ?? [];
                final filtered = _selectedApprovalFilter == "ALL"
                    ? allPending
                    : allPending.where((a) => a.module == _selectedApprovalFilter).toList();

                if (filtered.isEmpty) {
                  return Card(
                    child: Padding(
                      padding: const EdgeInsets.all(16),
                      child: Center(
                        child: Text(
                          "No pending approvals for ${_selectedApprovalFilter == 'ALL' ? 'your region' : _selectedApprovalFilter}.",
                          style: const TextStyle(fontSize: 12, color: Colors.black54),
                        ),
                      ),
                    ),
                  );
                }

                return ListView.builder(
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  itemCount: filtered.length,
                  itemBuilder: (context, index) {
                    final req = filtered[index];
                    return Card(
                      margin: const EdgeInsets.only(bottom: 8),
                      child: Padding(
                        padding: const EdgeInsets.all(12),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Row(
                                  children: [
                                    Icon(
                                      req.module == "EXPENSE"
                                          ? Icons.receipt_long
                                          : req.module == "ATTENDANCE"
                                              ? Icons.fingerprint
                                              : req.module == "DOCTOR_VISIT"
                                                  ? Icons.local_hospital
                                                  : Icons.task_alt,
                                      size: 16,
                                      color: CareOsisColors.medicalEmeraldPrimary,
                                    ),
                                    const SizedBox(width: 6),
                                    Text(
                                      "${req.module} (#${req.approvalId})",
                                      style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13),
                                    ),
                                  ],
                                ),
                                Container(
                                  padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                                  decoration: BoxDecoration(color: Colors.orange.shade50, borderRadius: BorderRadius.circular(6)),
                                  child: Text("SLA: ${req.sla}", style: TextStyle(fontSize: 10, color: Colors.orange.shade900, fontWeight: FontWeight.bold)),
                                ),
                              ],
                            ),
                            const SizedBox(height: 6),
                            Text(req.title, style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w600)),
                            Text(req.details, style: const TextStyle(fontSize: 11, color: Colors.black87)),
                            const SizedBox(height: 6),
                            Text("By: ${req.submittedByName} (${req.submittedBy})", style: const TextStyle(fontSize: 10, color: Colors.black54)),
                            const SizedBox(height: 10),
                            Row(
                              mainAxisAlignment: MainAxisAlignment.end,
                              children: [
                                OutlinedButton.icon(
                                  style: OutlinedButton.styleFrom(
                                    foregroundColor: Colors.red,
                                    side: const BorderSide(color: Colors.red),
                                    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                                  ),
                                  onPressed: () => AdminApprovalDialog.show(context, widget.repository, req),
                                  icon: const Icon(Icons.close, size: 14),
                                  label: const Text("Reject...", style: TextStyle(fontSize: 11)),
                                ),
                                const SizedBox(width: 8),
                                ElevatedButton.icon(
                                  style: ElevatedButton.styleFrom(
                                    backgroundColor: CareOsisColors.medicalEmeraldPrimary,
                                    foregroundColor: Colors.white,
                                    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
                                  ),
                                  onPressed: () async {
                                    await widget.repository.approveApprovalRequest(
                                      approvalId: req.approvalId,
                                      reviewerId: user?.id ?? "CO-ADM-101",
                                      reviewerName: user?.name ?? "Regional Admin",
                                      reviewerRole: user?.role ?? "ADMIN",
                                    );
                                    if (context.mounted) {
                                      ScaffoldMessenger.of(context).showSnackBar(
                                        SnackBar(
                                          content: Text("${req.module} #${req.approvalId} approved."),
                                          backgroundColor: CareOsisColors.medicalEmeraldPrimary,
                                        ),
                                      );
                                    }
                                  },
                                  icon: const Icon(Icons.check, size: 14),
                                  label: const Text("Quick Approve", style: TextStyle(fontSize: 11)),
                                ),
                              ],
                            ),
                          ],
                        ),
                      ),
                    );
                  },
                );
              },
            ),
            const SizedBox(height: 20),

            // REGIONAL INCENTIVE RULES CARD
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text("Active Incentive Governance", style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold)),
                TextButton.icon(
                  onPressed: () => AdminIncentiveBuilderModal.show(context, widget.repository),
                  icon: const Icon(Icons.edit_note, size: 16),
                  label: const Text("Adjust Slabs"),
                ),
              ],
            ),
            StreamBuilder<List<IncentiveRuleModel>>(
              stream: widget.repository.getActiveIncentiveRules(),
              builder: (context, snapshot) {
                final rules = snapshot.data ?? [];
                return ListView.builder(
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  itemCount: rules.length,
                  itemBuilder: (context, index) {
                    final r = rules[index];
                    return Card(
                      margin: const EdgeInsets.only(bottom: 8),
                      child: ListTile(
                        leading: const Icon(Icons.workspace_premium, color: CareOsisColors.medicalEmeraldPrimary),
                        title: Text("${r.ruleName} (v${r.versionNumber})", style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
                        subtitle: Text("Type: ${r.ruleType} • Target: ₹${r.defaultTarget.toStringAsFixed(0)} • Scope: ${r.regionId}"),
                        trailing: CareOsisStatusChip(label: r.status),
                      ),
                    );
                  },
                );
              },
            ),
            const SizedBox(height: 20),

            // SCOPED FIELD TEAM SECTION
            const Text("Scoped Medical Representatives", style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold)),
            const SizedBox(height: 8),
            StreamBuilder<List<UserAccount>>(
              stream: widget.repository.getScopedEmployees(user),
              builder: (context, snapshot) {
                final employees = snapshot.data ?? [];
                if (employees.isEmpty) return const Text("No representatives in assigned scope.");
                return ListView.builder(
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  itemCount: employees.length,
                  itemBuilder: (context, index) {
                    final emp = employees[index];
                    return Card(
                      margin: const EdgeInsets.only(bottom: 8),
                      child: ListTile(
                        leading: CircleAvatar(
                          backgroundColor: CareOsisColors.medicalEmeraldPrimary.withOpacity(0.1),
                          child: Text(emp.name.substring(0, 1), style: const TextStyle(color: CareOsisColors.medicalEmeraldPrimary, fontWeight: FontWeight.bold)),
                        ),
                        title: Text(emp.name, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
                        subtitle: Text("ID: ${emp.id} • Region: ${emp.assignedRegionIds} • Target: ₹${emp.monthlyTarget.toStringAsFixed(0)}"),
                        trailing: CareOsisStatusChip(label: emp.status),
                      ),
                    );
                  },
                );
              },
            ),
          ],
        ),
      ),
    );
  }
}
