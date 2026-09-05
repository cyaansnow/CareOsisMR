import 'package:flutter/material.dart';
import '../../../../core/theme/careosis_theme.dart';
import '../../../../data/repository/careosis_repository.dart';
import '../../../../data/local/entities/admin_and_security_entities.dart';

class SuperAdminDialogs {
  static void showCreateRegionDialog(BuildContext context, CareOsisRepository repository) {
    final idController = TextEditingController(text: "REG-${DateTime.now().millisecondsSinceEpoch.toString().substring(8)}");
    final nameController = TextEditingController();
    final codeController = TextEditingController();
    final stateController = TextEditingController(text: "Delhi");
    final hqController = TextEditingController();
    final targetController = TextEditingController(text: "2000000");

    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text("Create Operating Region", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(controller: idController, decoration: const InputDecoration(labelText: "Region ID (e.g. REG-005)")),
              TextField(controller: nameController, decoration: const InputDecoration(labelText: "Region Name (e.g. Pune Metro)")),
              TextField(controller: codeController, decoration: const InputDecoration(labelText: "Region Code (e.g. PUN)")),
              TextField(controller: stateController, decoration: const InputDecoration(labelText: "State")),
              TextField(controller: hqController, decoration: const InputDecoration(labelText: "Headquarters")),
              TextField(controller: targetController, keyboardType: TextInputType.number, decoration: const InputDecoration(labelText: "Monthly Target (₹)")),
            ],
          ),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.of(ctx).pop(), child: const Text("Cancel")),
          ElevatedButton(
            style: ElevatedButton.styleFrom(backgroundColor: CareOsisColors.medicalEmeraldPrimary, foregroundColor: Colors.white),
            onPressed: () async {
              if (nameController.text.trim().isEmpty) return;
              final region = Region(
                id: idController.text.trim(),
                name: nameController.text.trim(),
                state: stateController.text.trim(),
                code: codeController.text.trim().toUpperCase(),
                headquarters: hqController.text.trim(),
                monthlyTarget: double.tryParse(targetController.text.trim()) ?? 2000000.0,
                status: "ACTIVE",
                createdBy: repository.currentUser?.id ?? "CO-SA-001",
                createdAt: DateTime.now().millisecondsSinceEpoch,
              );
              await repository.createRegion(region, repository.currentUser?.id ?? "CO-SA-001");
              if (ctx.mounted) Navigator.of(ctx).pop();
            },
            child: const Text("Create Region"),
          ),
        ],
      ),
    );
  }

  static void showEditRegionDialog(BuildContext context, CareOsisRepository repository, Region region) {
    final nameController = TextEditingController(text: region.name);
    final targetController = TextEditingController(text: region.monthlyTarget.toStringAsFixed(0));
    final status = ValueNotifier<String>(region.status);

    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Text("Edit Region: ${region.code}", style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(controller: nameController, decoration: const InputDecoration(labelText: "Region Name")),
              TextField(controller: targetController, keyboardType: TextInputType.number, decoration: const InputDecoration(labelText: "Monthly Target (₹)")),
              const SizedBox(height: 12),
              ValueListenableBuilder<String>(
                valueListenable: status,
                builder: (context, val, _) {
                  return DropdownButtonFormField<String>(
                    value: val,
                    decoration: const InputDecoration(labelText: "Status"),
                    items: const [
                      DropdownMenuItem(value: "ACTIVE", child: Text("ACTIVE")),
                      DropdownMenuItem(value: "INACTIVE", child: Text("INACTIVE")),
                    ],
                    onChanged: (newVal) => status.value = newVal ?? "ACTIVE",
                  );
                },
              ),
            ],
          ),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.of(ctx).pop(), child: const Text("Cancel")),
          ElevatedButton(
            onPressed: () async {
              final updated = Region(
                id: region.id,
                name: nameController.text.trim(),
                state: region.state,
                code: region.code,
                headquarters: region.headquarters,
                monthlyTarget: double.tryParse(targetController.text.trim()) ?? region.monthlyTarget,
                status: status.value,
                createdBy: region.createdBy,
                createdAt: region.createdAt,
                updatedAt: DateTime.now().millisecondsSinceEpoch,
              );
              await repository.updateRegion(updated, repository.currentUser?.id ?? "CO-SA-001");
              if (ctx.mounted) Navigator.of(ctx).pop();
            },
            child: const Text("Save Changes"),
          ),
        ],
      ),
    );
  }

  static void showCreateTerritoryDialog(BuildContext context, CareOsisRepository repository) {
    final idController = TextEditingController(text: "TER-${DateTime.now().millisecondsSinceEpoch.toString().substring(8)}");
    final nameController = TextEditingController();
    final regionIdController = TextEditingController(text: "REG-001");

    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text("Add Territory", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(controller: idController, decoration: const InputDecoration(labelText: "Territory ID (e.g. TER-007)")),
            TextField(controller: nameController, decoration: const InputDecoration(labelText: "Territory Name (e.g. West Delhi)")),
            TextField(controller: regionIdController, decoration: const InputDecoration(labelText: "Parent Region ID (e.g. REG-001)")),
          ],
        ),
        actions: [
          TextButton(onPressed: () => Navigator.of(ctx).pop(), child: const Text("Cancel")),
          ElevatedButton(
            onPressed: () async {
              if (nameController.text.trim().isEmpty) return;
              final territory = Territory(
                territoryId: idController.text.trim(),
                territoryName: nameController.text.trim(),
                regionId: regionIdController.text.trim(),
                status: "ACTIVE",
                createdBy: repository.currentUser?.id ?? "CO-SA-001",
                createdAt: DateTime.now().millisecondsSinceEpoch,
              );
              await repository.createTerritory(territory, repository.currentUser?.id ?? "CO-SA-001");
              if (ctx.mounted) Navigator.of(ctx).pop();
            },
            child: const Text("Add Territory"),
          ),
        ],
      ),
    );
  }

  static void showMoveTerritoryDialog(BuildContext context, CareOsisRepository repository, Territory territory) {
    final newRegionController = TextEditingController(text: territory.regionId);

    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Text("Move Territory: ${territory.territoryName}", style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text("Current Region: ${territory.regionId}"),
            const SizedBox(height: 10),
            TextField(controller: newRegionController, decoration: const InputDecoration(labelText: "New Parent Region ID (e.g. REG-002)")),
          ],
        ),
        actions: [
          TextButton(onPressed: () => Navigator.of(ctx).pop(), child: const Text("Cancel")),
          ElevatedButton(
            onPressed: () async {
              await repository.moveTerritory(territory.territoryId, newRegionController.text.trim(), repository.currentUser?.id ?? "CO-SA-001");
              if (ctx.mounted) Navigator.of(ctx).pop();
            },
            child: const Text("Confirm Move"),
          ),
        ],
      ),
    );
  }

  static void showCreateAdminScopeDialog(BuildContext context, CareOsisRepository repository) {
    final scopeId = "SCOPE-${DateTime.now().millisecondsSinceEpoch.toString().substring(8)}";
    final adminIdController = TextEditingController(text: "CO-ADM-101");
    final scopeType = ValueNotifier<String>("REGION");
    final regionController = TextEditingController(text: "REG-001");
    final territoriesController = TextEditingController(text: "TER-001,TER-002");
    final employeesController = TextEditingController(text: "CO-MR-8492");

    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text("Assign Admin Scope", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(controller: adminIdController, decoration: const InputDecoration(labelText: "Admin ID (e.g. CO-ADM-101)")),
              const SizedBox(height: 10),
              ValueListenableBuilder<String>(
                valueListenable: scopeType,
                builder: (context, val, _) {
                  return DropdownButtonFormField<String>(
                    value: val,
                    decoration: const InputDecoration(labelText: "Access Scope Type"),
                    items: const [
                      DropdownMenuItem(value: "REGION", child: Text("Entire Region")),
                      DropdownMenuItem(value: "TERRITORY", child: Text("Selected Territories")),
                      DropdownMenuItem(value: "EMPLOYEE", child: Text("Specific Employees")),
                    ],
                    onChanged: (newVal) => scopeType.value = newVal ?? "REGION",
                  );
                },
              ),
              TextField(controller: regionController, decoration: const InputDecoration(labelText: "Region ID (e.g. REG-001)")),
              TextField(controller: territoriesController, decoration: const InputDecoration(labelText: "Territories (comma-separated)")),
              TextField(controller: employeesController, decoration: const InputDecoration(labelText: "Specific Employees (comma-separated)")),
            ],
          ),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.of(ctx).pop(), child: const Text("Cancel")),
          ElevatedButton(
            onPressed: () async {
              final scope = AdminScope(
                scopeId: scopeId,
                adminId: adminIdController.text.trim(),
                scopeType: scopeType.value,
                regionId: regionController.text.trim(),
                territoryId: territoriesController.text.trim(),
                employeeId: employeesController.text.trim(),
                status: "ACTIVE",
                assignedBy: repository.currentUser?.id ?? "CO-SA-001",
                createdAt: DateTime.now().millisecondsSinceEpoch,
              );
              await repository.saveAdminScope(scope, repository.currentUser?.id ?? "CO-SA-001");
              if (ctx.mounted) Navigator.of(ctx).pop();
            },
            child: const Text("Save Scope"),
          ),
        ],
      ),
    );
  }

  static void showCreateRuleDialog(BuildContext context, CareOsisRepository repository) {
    final idController = TextEditingController(text: "RULE-${DateTime.now().millisecondsSinceEpoch.toString().substring(8)}");
    final nameController = TextEditingController();
    final ruleType = ValueNotifier<String>("ATTENDANCE");
    final scope = ValueNotifier<String>("GLOBAL");
    final conditionsController = TextEditingController(text: '{"param":"value"}');

    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text("Create Engine Rule", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(controller: idController, decoration: const InputDecoration(labelText: "Rule ID")),
              TextField(controller: nameController, decoration: const InputDecoration(labelText: "Rule Name")),
              const SizedBox(height: 10),
              ValueListenableBuilder<String>(
                valueListenable: ruleType,
                builder: (context, val, _) {
                  return DropdownButtonFormField<String>(
                    value: val,
                    decoration: const InputDecoration(labelText: "Rule Type"),
                    items: const [
                      DropdownMenuItem(value: "ATTENDANCE", child: Text("ATTENDANCE")),
                      DropdownMenuItem(value: "GPS", child: Text("GPS")),
                      DropdownMenuItem(value: "DOCTOR_VISIT", child: Text("DOCTOR_VISIT")),
                      DropdownMenuItem(value: "TARGET", child: Text("TARGET")),
                      DropdownMenuItem(value: "INCENTIVE", child: Text("INCENTIVE")),
                      DropdownMenuItem(value: "EXPENSE", child: Text("EXPENSE")),
                    ],
                    onChanged: (newVal) => ruleType.value = newVal ?? "ATTENDANCE",
                  );
                },
              ),
              TextField(controller: conditionsController, maxLines: 3, decoration: const InputDecoration(labelText: "Conditions JSON")),
            ],
          ),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.of(ctx).pop(), child: const Text("Cancel")),
          ElevatedButton(
            onPressed: () async {
              if (nameController.text.trim().isEmpty) return;
              final rule = RuleModel(
                ruleId: idController.text.trim(),
                ruleName: nameController.text.trim(),
                ruleType: ruleType.value,
                scope: scope.value,
                conditionsJson: conditionsController.text.trim(),
                status: "ACTIVE",
                version: 1,
                createdBy: repository.currentUser?.id ?? "CO-SA-001",
                createdAt: DateTime.now().millisecondsSinceEpoch,
              );
              await repository.saveGeneralizedRule(rule, repository.currentUser?.id ?? "CO-SA-001");
              if (ctx.mounted) Navigator.of(ctx).pop();
            },
            child: const Text("Save Rule"),
          ),
        ],
      ),
    );
  }
}
