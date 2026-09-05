import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../core/theme/careosis_theme.dart';
import '../../../core/components/careosis_components.dart';
import '../../../data/repository/careosis_repository.dart';
import '../../../data/local/entities/commercial_entities.dart';
import '../../../core/engine/rule_engine.dart';

class ExpenseManagementScreen extends StatelessWidget {
  final CareOsisRepository repository;
  const ExpenseManagementScreen({super.key, required this.repository});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: CareOsisTopBar(
        title: "Field Expenses",
        subtitle: "Claims & Governance Policy",
        actions: [
          IconButton(
            icon: const Icon(Icons.add, color: Colors.white),
            onPressed: () => context.push('/expenses/add'),
          ),
        ],
      ),
      body: StreamBuilder<List<ExpenseModel>>(
        stream: repository.getAllExpenses(),
        builder: (context, snapshot) {
          final expenses = snapshot.data ?? [];
          final double totalSpent = expenses.fold(0.0, (sum, e) => sum + e.amount);

          return SingleChildScrollView(
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
                  child: Column(
                    children: [
                      const Text("Total MTD Expense Claims", style: TextStyle(color: Colors.black54, fontSize: 13)),
                      const SizedBox(height: 4),
                      Text(
                        "₹${totalSpent.toStringAsFixed(0)}",
                        style: const TextStyle(fontSize: 28, fontWeight: FontWeight.bold, color: CareOsisColors.medicalEmeraldPrimary),
                      ),
                      const SizedBox(height: 12),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          _buildExpenseStat("Approved", "₹12,400", CareOsisColors.statusGreen),
                          _buildExpenseStat("Pending", "₹3,150", CareOsisColors.statusOrange),
                          _buildExpenseStat("Policy Limit", "₹25,000", Colors.blueGrey),
                        ],
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 20),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    const Text("Expense Claims History", style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold)),
                    ElevatedButton.icon(
                      onPressed: () => context.push('/expenses/add'),
                      icon: const Icon(Icons.add, size: 16),
                      label: const Text("Log Claim"),
                      style: ElevatedButton.styleFrom(
                        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 10),
                if (expenses.isEmpty)
                  const Card(
                    child: Padding(
                      padding: EdgeInsets.all(24),
                      child: Text("No field expenses logged for this period.", textAlign: TextAlign.center),
                    ),
                  )
                else
                  ListView.builder(
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    itemCount: expenses.length,
                    itemBuilder: (context, index) {
                      final e = expenses[index];
                      final isRejected = e.status.toLowerCase().contains("reject");
                      return Card(
                        margin: const EdgeInsets.only(bottom: 8),
                        child: ListTile(
                          leading: CircleAvatar(
                            backgroundColor: CareOsisColors.medicalEmeraldPrimary.withOpacity(0.1),
                            child: Icon(_resolveIcon(e.category), color: CareOsisColors.medicalEmeraldPrimary, size: 20),
                          ),
                          title: Text(e.category, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                          subtitle: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text("${e.date} • ${e.description}", style: const TextStyle(fontSize: 12)),
                              if (isRejected)
                                Padding(
                                  padding: const EdgeInsets.only(top: 4),
                                  child: Text(
                                    "Reason: ${e.status.replaceFirst('Rejected: ', '')}",
                                    style: const TextStyle(fontSize: 11, color: Colors.red, fontWeight: FontWeight.bold),
                                  ),
                                ),
                            ],
                          ),
                          trailing: Column(
                            mainAxisAlignment: MainAxisAlignment.center,
                            crossAxisAlignment: CrossAxisAlignment.end,
                            children: [
                              Text("₹${e.amount.toStringAsFixed(0)}", style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                              const SizedBox(height: 2),
                              CareOsisStatusChip(label: isRejected ? "Rejected" : e.status),
                            ],
                          ),
                        ),
                      );
                    },
                  ),
              ],
            ),
          );
        },
      ),
    );
  }

  Widget _buildExpenseStat(String label, String value, Color color) {
    return Column(
      children: [
        Text(label, style: const TextStyle(fontSize: 11, color: Colors.black54)),
        const SizedBox(height: 2),
        Text(value, style: TextStyle(fontSize: 13, fontWeight: FontWeight.bold, color: color)),
      ],
    );
  }

  IconData _resolveIcon(String category) {
    switch (category.toLowerCase()) {
      case "fuel":
      case "travel":
        return Icons.local_gas_station_outlined;
      case "food":
        return Icons.restaurant_outlined;
      case "hotel":
        return Icons.hotel_outlined;
      default:
        return Icons.receipt_outlined;
    }
  }
}

class LogExpenseFormScreen extends StatefulWidget {
  final CareOsisRepository repository;
  const LogExpenseFormScreen({super.key, required this.repository});

  @override
  State<LogExpenseFormScreen> createState() => _LogExpenseFormScreenState();
}

class _LogExpenseFormScreenState extends State<LogExpenseFormScreen> {
  String _category = "Travel / Fuel";
  final _amountController = TextEditingController();
  final _descController = TextEditingController();
  double _distanceKm = 0.0;
  String _vehicleType = "2-Wheeler (Motorcycle / Scooter)";
  bool _hasReceipt = true;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CareOsisTopBar(title: "Log Field Expense", showBack: true),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            DropdownButtonFormField<String>(
              value: _category,
              decoration: const InputDecoration(labelText: "Expense Category", border: OutlineInputBorder()),
              items: const [
                DropdownMenuItem(value: "Travel / Fuel", child: Text("Travel / Fuel Mileage")),
                DropdownMenuItem(value: "Daily Allowance", child: Text("Daily Food Allowance")),
                DropdownMenuItem(value: "Hotel & Lodging", child: Text("Hotel & Lodging")),
                DropdownMenuItem(value: "Doctor Engagement", child: Text("Doctor Engagement / CME")),
                DropdownMenuItem(value: "Stationery & Samples", child: Text("Stationery & Promotional Samples")),
                DropdownMenuItem(value: "Miscellaneous", child: Text("Miscellaneous Expenses")),
              ],
              onChanged: (v) => setState(() => _category = v ?? "Travel / Fuel"),
            ),
            const SizedBox(height: 16),
            if (_category == "Travel / Fuel") ...[
              Card(
                color: CareOsisColors.medicalEmeraldPrimary.withOpacity(0.06),
                child: Padding(
                  padding: const EdgeInsets.all(14),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text("Mileage Policy Calculator (Rule: RULE-EXP-001)", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
                      const SizedBox(height: 8),
                      DropdownButtonFormField<String>(
                        value: _vehicleType,
                        decoration: const InputDecoration(labelText: "Vehicle Tier", border: OutlineInputBorder()),
                        items: const [
                          DropdownMenuItem(value: "2-Wheeler (Motorcycle / Scooter)", child: Text("2-Wheeler (₹3.50 / km)")),
                          DropdownMenuItem(value: "4-Wheeler (Car)", child: Text("4-Wheeler (₹8.00 / km)")),
                        ],
                        onChanged: (v) {
                          setState(() {
                            _vehicleType = v ?? _vehicleType;
                            final rate = _vehicleType.contains("4-Wheeler") ? 8.0 : 3.5;
                            _amountController.text = (_distanceKm * rate).toStringAsFixed(0);
                          });
                        },
                      ),
                      const SizedBox(height: 10),
                      TextField(
                        keyboardType: TextInputType.number,
                        decoration: const InputDecoration(labelText: "Distance Traveled (KM)", border: OutlineInputBorder()),
                        onChanged: (v) {
                          final km = double.tryParse(v) ?? 0.0;
                          setState(() {
                            _distanceKm = km;
                            final rate = _vehicleType.contains("4-Wheeler") ? 8.0 : 3.5;
                            _amountController.text = (km * rate).toStringAsFixed(0);
                          });
                        },
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 16),
            ],
            TextField(
              controller: _amountController,
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(labelText: "Claim Amount (₹)", border: OutlineInputBorder()),
            ),
            const SizedBox(height: 16),
            TextField(
              controller: _descController,
              maxLines: 2,
              decoration: const InputDecoration(labelText: "Description / Purpose", border: OutlineInputBorder()),
            ),
            const SizedBox(height: 16),
            CheckboxListTile(
              title: const Text("Physical / Digital Receipt Attached", style: TextStyle(fontSize: 13)),
              subtitle: const Text("Mandatory for claims above ₹200 as per Expense Policy", style: TextStyle(fontSize: 11, color: Colors.grey)),
              value: _hasReceipt,
              onChanged: (v) => setState(() => _hasReceipt = v ?? true),
              controlAffinity: ListTileControlAffinity.leading,
            ),
            const SizedBox(height: 24),
            ElevatedButton(
              onPressed: () async {
                final amount = double.tryParse(_amountController.text.trim()) ?? 0.0;
                if (amount <= 0) return;

                final user = widget.repository.currentUser;
                final rule = widget.repository.resolveRule(
                  "EXPENSE",
                  employeeId: user?.id,
                  regionId: user?.assignedRegionIds,
                );

                final eval = RuleEngine.evaluateExpense(
                  amount: amount,
                  category: _category,
                  hasReceipt: _hasReceipt,
                  currentDayTotalSoFar: 0.0,
                  expenseRule: rule,
                );

                final expenseId = "EXP-${DateTime.now().millisecondsSinceEpoch.toString().substring(7)}";
                final exp = ExpenseModel(
                  id: expenseId,
                  date: "21 Aug 2026",
                  category: _category,
                  amount: amount,
                  description: _descController.text.trim(),
                  status: eval.isEligible ? "Submitted" : "Pending Approval (${eval.violationReason})",
                  createdAt: DateTime.now().millisecondsSinceEpoch,
                );

                await widget.repository.createExpense(exp);

                // Unified Approval Engine request submission
                await widget.repository.submitApprovalRequest(
                  module: "EXPENSE",
                  entityId: expenseId,
                  title: "$_category Claim ₹${amount.toStringAsFixed(0)}",
                  details: "${_descController.text.trim()} (Receipt: ${_hasReceipt ? 'Yes' : 'No'})",
                  submittedBy: user?.id ?? "CO-MR-8492",
                  submittedByName: user?.name ?? "Aman Chhabra",
                  scope: user?.assignedRegionIds ?? "REG-001",
                  sla: "48h",
                );

                if (mounted) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(
                      content: Text("Expense claim #$expenseId submitted for Admin review (SLA: 48h)."),
                      backgroundColor: CareOsisColors.medicalEmeraldPrimary,
                    ),
                  );
                  Navigator.pop(context);
                }
              },
              child: const Text("Submit Expense Claim"),
            ),
          ],
        ),
      ),
    );
  }
}
