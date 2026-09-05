import 'package:flutter/material.dart';
import '../../../../data/repository/careosis_repository.dart';
import '../../../../core/engine/incentive_calculation_engine.dart';

class AdminIncentiveBuilderModal extends StatefulWidget {
  final CareOsisRepository repository;
  const AdminIncentiveBuilderModal({super.key, required this.repository});

  static void show(BuildContext context, CareOsisRepository repository) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (context) => AdminIncentiveBuilderModal(repository: repository),
    );
  }

  @override
  State<AdminIncentiveBuilderModal> createState() => _AdminIncentiveBuilderModalState();
}

class _AdminIncentiveBuilderModalState extends State<AdminIncentiveBuilderModal> {
  final _ruleNameController = TextEditingController(text: "Delhi NCR Q3 Target Incentive");
  String _ruleType = "PERCENTAGE_OF_SALES";
  final _defaultTargetController = TextEditingController(text: "200000");

  @override
  Widget build(BuildContext context) {
    return Container(
      height: MediaQuery.of(context).size.height * 0.85,
      decoration: const BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.vertical(top: Radius.circular(24)),
      ),
      padding: const EdgeInsets.all(20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Center(
            child: Container(
              margin: const EdgeInsets.only(bottom: 12),
              width: 40,
              height: 4,
              decoration: BoxDecoration(color: Colors.grey.shade300, borderRadius: BorderRadius.circular(2)),
            ),
          ),
          const Text("Configurable Incentive Rule Builder", style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
          const Text("Create or upgrade company incentive slabs and rules.", style: TextStyle(fontSize: 12, color: Colors.black54)),
          const SizedBox(height: 16),
          Expanded(
            child: ListView(
              children: [
                TextField(
                  controller: _ruleNameController,
                  decoration: const InputDecoration(labelText: "Rule Title", border: OutlineInputBorder()),
                ),
                const SizedBox(height: 14),
                DropdownButtonFormField<String>(
                  value: _ruleType,
                  decoration: const InputDecoration(labelText: "Engine Calculation Model", border: OutlineInputBorder()),
                  items: const [
                    DropdownMenuItem(value: "PERCENTAGE_OF_SALES", child: Text("Percentage of Sales Volume")),
                    DropdownMenuItem(value: "SLAB_BASED", child: Text("Tiered Slab Milestones")),
                    DropdownMenuItem(value: "MULTI_COMPONENT", child: Text("Multi-Component KPI Formula")),
                  ],
                  onChanged: (v) => setState(() => _ruleType = v ?? _ruleType),
                ),
                const SizedBox(height: 14),
                TextField(
                  controller: _defaultTargetController,
                  keyboardType: TextInputType.number,
                  decoration: const InputDecoration(labelText: "Default Monthly Target (₹)", border: OutlineInputBorder()),
                ),
              ],
            ),
          ),
          ElevatedButton(
            onPressed: () async {
              final target = double.tryParse(_defaultTargetController.text) ?? 200000.0;
              final rule = IncentiveRuleModel(
                id: "RULE-${_ruleType.substring(0, 4)}-${DateTime.now().millisecondsSinceEpoch.toString().substring(7)}-V1",
                ruleName: _ruleNameController.text.trim(),
                ruleType: _ruleType,
                defaultTarget: target,
                updatedAt: DateTime.now().millisecondsSinceEpoch,
              );
              await widget.repository.saveIncentiveRule(rule, widget.repository.currentUser?.id ?? "CO-ADM-101", createNewVersion: false);
              if (mounted) Navigator.pop(context);
            },
            child: const Text("Deploy Incentive Rule"),
          ),
        ],
      ),
    );
  }
}
