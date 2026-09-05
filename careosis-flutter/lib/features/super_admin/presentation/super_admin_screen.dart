import 'package:flutter/material.dart';
import '../../../core/theme/careosis_theme.dart';
import '../../../core/components/careosis_components.dart';
import '../../../data/repository/careosis_repository.dart';
import '../../../data/local/entities/admin_and_security_entities.dart';
import 'widgets/super_admin_dialogs.dart';

class SuperAdminScreen extends StatefulWidget {
  final CareOsisRepository repository;
  const SuperAdminScreen({super.key, required this.repository});

  @override
  State<SuperAdminScreen> createState() => _SuperAdminScreenState();
}

class _SuperAdminScreenState extends State<SuperAdminScreen> with SingleTickerProviderStateMixin {
  late TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 6, vsync: this);
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: AppBar(
        backgroundColor: const Color(0xFF0F172A),
        foregroundColor: Colors.white,
        title: const Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text("Executive Master Hub", style: TextStyle(fontSize: 17, fontWeight: FontWeight.bold)),
            Text("CareOsis Global Governance & Control Center", style: TextStyle(fontSize: 11, color: Colors.white70)),
          ],
        ),
        bottom: TabBar(
          controller: _tabController,
          indicatorColor: CareOsisColors.goldMetallic,
          labelColor: CareOsisColors.goldMetallic,
          unselectedLabelColor: Colors.white60,
          isScrollable: true,
          tabs: const [
            Tab(text: "Global Overview"),
            Tab(text: "Organization"),
            Tab(text: "Admin Scopes"),
            Tab(text: "Rule Engine"),
            Tab(text: "Executive Approvals"),
            Tab(text: "Audit Trail"),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: [
          _buildOverviewTab(),
          _buildOrganizationTab(),
          _buildAdminScopesTab(),
          _buildRuleEngineTab(),
          _buildExecutiveApprovalsTab(),
          _buildAuditTrailTab(),
        ],
      ),
    );
  }

  Widget _buildOverviewTab() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Container(
            padding: const EdgeInsets.all(18),
            decoration: BoxDecoration(
              gradient: const LinearGradient(colors: [Color(0xFF0F172A), Color(0xFF1E293B)]),
              borderRadius: BorderRadius.circular(16),
            ),
            child: const Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text("GLOBAL PERFORMANCE TOTALS", style: TextStyle(color: Colors.white60, fontSize: 11, fontWeight: FontWeight.bold)),
                SizedBox(height: 6),
                Text("₹1.42 Crores / ₹1.60 Cr", style: TextStyle(color: Colors.white, fontSize: 24, fontWeight: FontWeight.bold)),
                SizedBox(height: 4),
                Text("4 Operating Regions • 12 Territories • 38 Field MRs • 4 Regional Admins", style: TextStyle(color: Colors.white70, fontSize: 12)),
              ],
            ),
          ),
          const SizedBox(height: 16),
          _buildSummaryCard("Active Field Personnel", "38 Representatives", Icons.people),
          _buildSummaryCard("Target Prescribers Covered", "520 Doctors", Icons.local_hospital),
          _buildSummaryCard("Commercial Orders Transmitted", "148 Invoices", Icons.receipt_long),
          _buildSummaryCard("Governance Slabs & Rules Active", "6 System Engine Rules", Icons.gavel),
        ],
      ),
    );
  }

  Widget _buildOrganizationTab() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text("Operating Regions", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
              ElevatedButton.icon(
                style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF0F172A), foregroundColor: Colors.white),
                onPressed: () => SuperAdminDialogs.showCreateRegionDialog(context, widget.repository),
                icon: const Icon(Icons.add, size: 16),
                label: const Text("Add Region"),
              ),
            ],
          ),
          const SizedBox(height: 10),
          StreamBuilder<List<Region>>(
            stream: widget.repository.getAllRegions(),
            builder: (context, snapshot) {
              final regions = snapshot.data ?? [];
              if (regions.isEmpty) return const Text("No regions registered.");
              return ListView.builder(
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                itemCount: regions.length,
                itemBuilder: (context, index) {
                  final reg = regions[index];
                  return Card(
                    margin: const EdgeInsets.only(bottom: 8),
                    child: ListTile(
                      title: Text("${reg.name} (${reg.code})", style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                      subtitle: Text("ID: ${reg.id} • Target: ₹${reg.monthlyTarget.toStringAsFixed(0)} • HQ: ${reg.headquarters}"),
                      trailing: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          CareOsisStatusChip(label: reg.status),
                          IconButton(
                            icon: const Icon(Icons.edit, size: 18),
                            onPressed: () => SuperAdminDialogs.showEditRegionDialog(context, widget.repository, reg),
                          ),
                        ],
                      ),
                    ),
                  );
                },
              );
            },
          ),
          const SizedBox(height: 24),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text("Territories & Area Beats", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
              ElevatedButton.icon(
                style: ElevatedButton.styleFrom(backgroundColor: CareOsisColors.medicalEmeraldPrimary, foregroundColor: Colors.white),
                onPressed: () => SuperAdminDialogs.showCreateTerritoryDialog(context, widget.repository),
                icon: const Icon(Icons.add_location_alt, size: 16),
                label: const Text("Add Territory"),
              ),
            ],
          ),
          const SizedBox(height: 10),
          StreamBuilder<List<Territory>>(
            stream: widget.repository.getAllTerritories(),
            builder: (context, snapshot) {
              final territories = snapshot.data ?? [];
              if (territories.isEmpty) return const Text("No territories mapped.");
              return ListView.builder(
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                itemCount: territories.length,
                itemBuilder: (context, index) {
                  final ter = territories[index];
                  return Card(
                    margin: const EdgeInsets.only(bottom: 8),
                    child: ListTile(
                      title: Text(ter.territoryName, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
                      subtitle: Text("ID: ${ter.territoryId} • Assigned Region: ${ter.regionId}"),
                      trailing: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          CareOsisStatusChip(label: ter.status),
                          IconButton(
                            icon: const Icon(Icons.drive_file_move_outline, size: 18),
                            tooltip: "Move Territory",
                            onPressed: () => SuperAdminDialogs.showMoveTerritoryDialog(context, widget.repository, ter),
                          ),
                        ],
                      ),
                    ),
                  );
                },
              );
            },
          ),
        ],
      ),
    );
  }

  Widget _buildAdminScopesTab() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text("Admin Access Scopes", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
              ElevatedButton.icon(
                style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF0F172A), foregroundColor: Colors.white),
                onPressed: () => SuperAdminDialogs.showCreateAdminScopeDialog(context, widget.repository),
                icon: const Icon(Icons.admin_panel_settings, size: 16),
                label: const Text("Assign Scope"),
              ),
            ],
          ),
          const SizedBox(height: 10),
          StreamBuilder<List<AdminScope>>(
            stream: widget.repository.getAllAdminScopes(),
            builder: (context, snapshot) {
              final scopes = snapshot.data ?? [];
              if (scopes.isEmpty) return const Text("No admin scopes configured.");
              return ListView.builder(
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                itemCount: scopes.length,
                itemBuilder: (context, index) {
                  final s = scopes[index];
                  return Card(
                    margin: const EdgeInsets.only(bottom: 10),
                    child: Padding(
                      padding: const EdgeInsets.all(12),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Text("Scope #${s.scopeId} • Admin: ${s.adminId}", style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
                              CareOsisStatusChip(label: s.status),
                            ],
                          ),
                          const SizedBox(height: 6),
                          Text("Scope Type: ${s.scopeType} • Region: ${s.regionId}", style: const TextStyle(fontSize: 12)),
                          if (s.territoryId.isNotEmpty) Text("Territories: ${s.territoryId}", style: const TextStyle(fontSize: 12, color: Colors.black54)),
                          if (s.employeeId.isNotEmpty) Text("Assigned Reps: ${s.employeeId}", style: const TextStyle(fontSize: 12, color: Colors.black54)),
                        ],
                      ),
                    ),
                  );
                },
              );
            },
          ),
          const SizedBox(height: 20),
          const Text("Canonical Permission Definitions (P-001 → P-014)", style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold)),
          const SizedBox(height: 8),
          Container(
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(10), border: Border.all(color: Colors.black12)),
            child: const Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text("• P-001 to P-002: Employee VIEW / EDIT (Scope: REGION/TERRITORY/EMPLOYEE)", style: TextStyle(fontSize: 12)),
                Text("• P-003 to P-004: Attendance VIEW / APPROVE (Scope: REGION)", style: TextStyle(fontSize: 12)),
                Text("• P-005 to P-006: Doctor Visit VIEW / APPROVE (Scope: REGION)", style: TextStyle(fontSize: 12)),
                Text("• P-007: Target CREATE / EDIT (Scope: REGION)", style: TextStyle(fontSize: 12)),
                Text("• P-008 to P-009: Incentive Rule VIEW / CREATE / EDIT (Scope: REGION)", style: TextStyle(fontSize: 12)),
                Text("• P-010 to P-011: Expense VIEW / APPROVE / REJECT (Scope: REGION)", style: TextStyle(fontSize: 12)),
                Text("• P-012 to P-014: Region, Admin & Permissions Management (Scope: GLOBAL SUPER_ADMIN)", style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildRuleEngineTab() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text("Configured Platform Rules", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
              ElevatedButton.icon(
                style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF0F172A), foregroundColor: Colors.white),
                onPressed: () => SuperAdminDialogs.showCreateRuleDialog(context, widget.repository),
                icon: const Icon(Icons.add, size: 16),
                label: const Text("New Rule"),
              ),
            ],
          ),
          const SizedBox(height: 10),
          StreamBuilder<List<RuleModel>>(
            stream: widget.repository.getAllGeneralizedRules(),
            builder: (context, snapshot) {
              final rules = snapshot.data ?? [];
              if (rules.isEmpty) return const Text("No engine rules registered.");
              return ListView.builder(
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                itemCount: rules.length,
                itemBuilder: (context, index) {
                  final r = rules[index];
                  return Card(
                    margin: const EdgeInsets.only(bottom: 10),
                    child: ExpansionTile(
                      leading: Icon(
                        r.ruleType == "ATTENDANCE"
                            ? Icons.fingerprint
                            : r.ruleType == "GPS"
                                ? Icons.my_location
                                : r.ruleType == "DOCTOR_VISIT"
                                    ? Icons.local_hospital
                                    : r.ruleType == "INCENTIVE"
                                        ? Icons.workspace_premium
                                        : r.ruleType == "EXPENSE"
                                            ? Icons.receipt_long
                                            : Icons.track_changes,
                        color: CareOsisColors.medicalEmeraldPrimary,
                      ),
                      title: Text("${r.ruleName} (v${r.version})", style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
                      subtitle: Text("Type: ${r.ruleType} • Priority: ${r.priority} • Scope: ${r.scope}"),
                      trailing: CareOsisStatusChip(label: r.status),
                      children: [
                        Padding(
                          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text("Conditions: ${r.conditionsJson}", style: const TextStyle(fontSize: 12, fontFamily: 'monospace')),
                              const SizedBox(height: 6),
                              Text("Effective: ${r.effectiveFrom} to ${r.effectiveTo}", style: const TextStyle(fontSize: 11, color: Colors.grey)),
                            ],
                          ),
                        ),
                      ],
                    ),
                  );
                },
              );
            },
          ),
        ],
      ),
    );
  }

  Widget _buildExecutiveApprovalsTab() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text("Executive Financial & Target Approvals", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
          const SizedBox(height: 10),
          StreamBuilder<List<ApprovalRequest>>(
            stream: widget.repository.getAllApprovalRequests(),
            builder: (context, snapshot) {
              final approvals = snapshot.data ?? [];
              final pending = approvals.where((a) => a.status == "PENDING").toList();
              if (pending.isEmpty) {
                return const Card(
                  child: Padding(
                    padding: EdgeInsets.all(20),
                    child: Center(child: Text("Zero pending executive approvals. All clear.")),
                  ),
                );
              }
              return ListView.builder(
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                itemCount: pending.length,
                itemBuilder: (context, index) {
                  final a = pending[index];
                  return Card(
                    margin: const EdgeInsets.only(bottom: 10),
                    child: ListTile(
                      title: Text(a.title, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
                      subtitle: Text("${a.details}\nSubmitted by: ${a.submittedByName} • SLA: ${a.sla}"),
                      trailing: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          IconButton(
                            icon: const Icon(Icons.check_circle, color: Colors.green),
                            onPressed: () async {
                              final user = widget.repository.currentUser;
                              await widget.repository.approveApprovalRequest(
                                approvalId: a.approvalId,
                                reviewerId: user?.id ?? "CO-SA-001",
                                reviewerName: user?.name ?? "Super Admin",
                                reviewerRole: "SUPER_ADMIN",
                              );
                            },
                          ),
                        ],
                      ),
                    ),
                  );
                },
              );
            },
          ),
        ],
      ),
    );
  }

  Widget _buildAuditTrailTab() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text("Tamper-Proof Governance Audit Trail", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
          const SizedBox(height: 10),
          StreamBuilder<List<AuditLog>>(
            stream: widget.repository.getAllAuditLogs(),
            builder: (context, snapshot) {
              final logs = snapshot.data ?? [];
              if (logs.isEmpty) return const Text("No audit records.");
              return ListView.builder(
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                itemCount: logs.length,
                itemBuilder: (context, index) {
                  final l = logs[index];
                  return Card(
                    margin: const EdgeInsets.only(bottom: 8),
                    child: ListTile(
                      leading: const Icon(Icons.security, size: 20, color: Color(0xFF0F172A)),
                      title: Text("${l.action} • ${l.targetEntity}", style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
                      subtitle: Text("By: ${l.userName} (${l.userRole})\n${l.formattedDate}${l.newValue.isNotEmpty ? '\nNew Value: ' + l.newValue : ''}"),
                      isThreeLine: true,
                    ),
                  );
                },
              );
            },
          ),
        ],
      ),
    );
  }

  Widget _buildSummaryCard(String title, String value, IconData icon) {
    return Card(
      margin: const EdgeInsets.only(bottom: 10),
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: CareOsisColors.medicalEmeraldPrimary.withOpacity(0.1),
          child: Icon(icon, color: CareOsisColors.medicalEmeraldPrimary),
        ),
        title: Text(title, style: const TextStyle(fontSize: 13, color: Colors.black54)),
        trailing: Text(value, style: const TextStyle(fontSize: 14, fontWeight: FontWeight.bold)),
      ),
    );
  }
}
