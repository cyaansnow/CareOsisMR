import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../../../core/theme/careosis_theme.dart';
import '../../../core/components/careosis_components.dart';
import '../../../data/repository/careosis_repository.dart';
import '../../../data/local/entities/doctor_and_mr_entities.dart';
import '../../../core/engine/rule_engine.dart';

class StartVisitScreen extends StatefulWidget {
  final CareOsisRepository repository;
  final String doctorId;
  const StartVisitScreen({super.key, required this.repository, required this.doctorId});

  @override
  State<StartVisitScreen> createState() => _StartVisitScreenState();
}

class _StartVisitScreenState extends State<StartVisitScreen> {
  String _purpose = "New Product Introduction";
  String _doctorResponse = "Positive";
  String _potential = "High";
  final _samplesController = TextEditingController(text: "Booster (2 strips)");
  final _notesController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return StreamBuilder<Doctor?>(
      stream: widget.repository.getDoctorById(widget.doctorId),
      builder: (context, snapshot) {
        final doc = snapshot.data;
        return Scaffold(
          appBar: CareOsisTopBar(
            title: "Log Field Call",
            subtitle: doc?.name ?? "Doctor Detailing",
            showBack: true,
          ),
          body: SingleChildScrollView(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                DropdownButtonFormField<String>(
                  value: _purpose,
                  decoration: const InputDecoration(labelText: "Call Objective / Purpose", border: OutlineInputBorder()),
                  items: const [
                    DropdownMenuItem(value: "New Product Introduction", child: Text("New Product Introduction")),
                    DropdownMenuItem(value: "Follow-up", child: Text("Follow-up")),
                    DropdownMenuItem(value: "Product Reminder", child: Text("Product Reminder")),
                    DropdownMenuItem(value: "Sample Follow-up", child: Text("Sample Follow-up")),
                    DropdownMenuItem(value: "Prescription Discussion", child: Text("Prescription Discussion")),
                  ],
                  onChanged: (v) => setState(() => _purpose = v ?? _purpose),
                ),
                const SizedBox(height: 16),
                DropdownButtonFormField<String>(
                  value: _doctorResponse,
                  decoration: const InputDecoration(labelText: "Doctor Response", border: OutlineInputBorder()),
                  items: const [
                    DropdownMenuItem(value: "Positive", child: Text("Positive (Highly Receptive)")),
                    DropdownMenuItem(value: "Neutral", child: Text("Neutral (Interested)")),
                    DropdownMenuItem(value: "Needs Follow-up", child: Text("Needs Clinical Evidence")),
                  ],
                  onChanged: (v) => setState(() => _doctorResponse = v ?? _doctorResponse),
                ),
                const SizedBox(height: 16),
                TextField(
                  controller: _samplesController,
                  decoration: const InputDecoration(labelText: "Samples / LBL Handed Over", border: OutlineInputBorder()),
                ),
                const SizedBox(height: 16),
                TextField(
                  controller: _notesController,
                  maxLines: 3,
                  decoration: const InputDecoration(labelText: "Detailing Discussion Notes", border: OutlineInputBorder()),
                ),
                const SizedBox(height: 24),
                ElevatedButton(
                  onPressed: () async {
                    final user = widget.repository.currentUser;
                    final visitRule = widget.repository.resolveRule("DOCTOR_VISIT", employeeId: user?.id, regionId: user?.assignedRegionIds);
                    final gpsRule = widget.repository.resolveRule("GPS", employeeId: user?.id, regionId: user?.assignedRegionIds);

                    // Evaluate 120m distance & 8 min duration against policy
                    final eval = RuleEngine.evaluateDoctorVisit(
                      doctorLat: 28.7041,
                      doctorLng: 77.1025,
                      mrLat: 28.7050,
                      mrLng: 77.1030,
                      durationMinutes: 8,
                      visitRule: visitRule,
                      gpsRule: gpsRule,
                    );

                    final visitId = "VISIT-${DateTime.now().millisecondsSinceEpoch.toString().substring(7)}";
                    final visit = DoctorVisit(
                      id: visitId,
                      doctorId: widget.doctorId,
                      doctorName: doc?.name ?? "Dr. Prescriber",
                      clinicName: doc?.clinicHospital ?? "Clinic",
                      startTime: DateFormat("hh:mm a").format(DateTime.now()),
                      visitDate: DateFormat("dd MMM yyyy").format(DateTime.now()),
                      purpose: _purpose,
                      doctorResponse: _doctorResponse,
                      prescriptionPotential: _potential,
                      samplesGiven: _samplesController.text.trim(),
                      notes: _notesController.text.trim(),
                      status: eval.requiresApproval ? "Pending Exception Approval" : "Completed",
                      createdAt: DateTime.now().millisecondsSinceEpoch,
                    );

                    await widget.repository.recordVisit(visit);

                    if (eval.requiresApproval) {
                      await widget.repository.submitApprovalRequest(
                        module: "DOCTOR_VISIT",
                        entityId: visitId,
                        title: "Visit Exception: ${doc?.name ?? 'Doctor Call'}",
                        details: eval.reason,
                        submittedBy: user?.id ?? "CO-MR-8492",
                        submittedByName: user?.name ?? "Aman Chhabra",
                        scope: user?.assignedRegionIds ?? "REG-001",
                        sla: "24h",
                      );
                    }

                    if (mounted) {
                      ScaffoldMessenger.of(context).showSnackBar(
                        SnackBar(
                          content: Text(eval.requiresApproval
                              ? "Field call recorded with exception. Submitted for Admin review."
                              : "Field call recorded & verified successfully!"),
                          backgroundColor: eval.requiresApproval ? Colors.orange.shade800 : CareOsisColors.medicalEmeraldPrimary,
                        ),
                      );
                      Navigator.pop(context);
                    }
                  },
                  child: const Text("Complete & Submit Visit"),
                ),
              ],
            ),
          ),
        );
      },
    );
  }
}

class VisitHistoryScreen extends StatelessWidget {
  final CareOsisRepository repository;
  const VisitHistoryScreen({super.key, required this.repository});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CareOsisTopBar(title: "Field Call History", showBack: true),
      body: StreamBuilder<List<DoctorVisit>>(
        stream: repository.getAllVisits(),
        builder: (context, snapshot) {
          final visits = snapshot.data ?? [];
          if (visits.isEmpty) return const Center(child: Text("No field calls logged yet"));

          return ListView.builder(
            padding: const EdgeInsets.all(12),
            itemCount: visits.length,
            itemBuilder: (context, index) {
              final v = visits[index];
              return Card(
                margin: const EdgeInsets.only(bottom: 8),
                child: ListTile(
                  leading: const CircleAvatar(
                    backgroundColor: CareOsisColors.medicalEmeraldPrimary,
                    child: Icon(Icons.check, color: Colors.white),
                  ),
                  title: Text(v.doctorName, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                  subtitle: Text("${v.visitDate} • ${v.purpose}\nSamples: ${v.samplesGiven}", style: const TextStyle(fontSize: 12)),
                  trailing: CareOsisStatusChip(label: v.doctorResponse),
                ),
              );
            },
          );
        },
      ),
    );
  }
}
