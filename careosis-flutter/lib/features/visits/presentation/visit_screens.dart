import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:geolocator/geolocator.dart';
import '../../../core/theme/careosis_theme.dart';
import '../../../core/components/careosis_components.dart';
import '../../../data/repository/careosis_repository.dart';
import '../../../data/local/entities/doctor_and_mr_entities.dart';
import '../../../core/services/location_tracking_service.dart';
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
  String _selectedProduct = "CardioVasc 20mg";
  final _samplesController = TextEditingController(text: "Samples (2 strips) + LBL Folder");
  final _notesController = TextEditingController();
  DateTime? _nextFollowUpDate;

  Position? _currentPosition;
  bool _isLoadingGps = true;
  String _gpsStatusText = "Acquiring live clinic GPS coordinates...";

  @override
  void initState() {
    super.initState();
    _acquireGpsLocation();
  }

  Future<void> _acquireGpsLocation() async {
    setState(() {
      _isLoadingGps = true;
      _gpsStatusText = "Acquiring live clinic GPS coordinates...";
    });

    try {
      final pos = await LocationTrackingService.instance.getCurrentPosition();
      if (mounted) {
        setState(() {
          _currentPosition = pos;
          _isLoadingGps = false;
          _gpsStatusText = pos != null
              ? "GPS Fixed (${pos.latitude.toStringAsFixed(4)}, ${pos.longitude.toStringAsFixed(4)})"
              : "GPS fix unavailable (Using offline coordinates)";
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() {
          _isLoadingGps = false;
          _gpsStatusText = "GPS error: $e";
        });
      }
    }
  }

  @override
  void dispose() {
    _samplesController.dispose();
    _notesController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return StreamBuilder<Doctor?>(
      stream: widget.repository.getDoctorById(widget.doctorId),
      builder: (context, snapshot) {
        final doc = snapshot.data;
        final clinicName = doc?.clinicHospital.isNotEmpty == true ? doc!.clinicHospital : "Private Clinic";
        final docLat = doc?.latitude ?? 0.0;
        final docLng = doc?.longitude ?? 0.0;

        // Proximity calculation
        double? distanceMeters;
        bool isAtClinic = false;
        if (_currentPosition != null && docLat != 0.0 && docLng != 0.0) {
          distanceMeters = Geolocator.distanceBetween(
            _currentPosition!.latitude,
            _currentPosition!.longitude,
            docLat,
            docLng,
          );
          isAtClinic = distanceMeters <= 300;
        }

        return Scaffold(
          backgroundColor: const Color(0xFFF8FAFC),
          appBar: CareOsisTopBar(
            title: "Log Field Detailing",
            subtitle: doc?.name ?? "Doctor Detailing Call",
            showBack: true,
          ),
          body: SingleChildScrollView(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                // Geotagged Clinic Verification Card
                Card(
                  elevation: 0,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                    side: BorderSide(
                      color: isAtClinic
                          ? CareOsisColors.medicalEmeraldPrimary.withOpacity(0.4)
                          : Colors.blueGrey.shade200,
                    ),
                  ),
                  color: isAtClinic
                      ? CareOsisColors.medicalEmeraldPrimary.withOpacity(0.06)
                      : Colors.white,
                  child: Padding(
                    padding: const EdgeInsets.all(14),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          children: [
                            Icon(
                              Icons.location_on,
                              color: isAtClinic
                                  ? CareOsisColors.medicalEmeraldPrimary
                                  : Colors.grey.shade500,
                              size: 22,
                            ),
                            const SizedBox(width: 8),
                            Expanded(
                              child: Text(
                                clinicName,
                                style: const TextStyle(
                                  fontWeight: FontWeight.bold,
                                  fontSize: 15,
                                ),
                              ),
                            ),
                            IconButton(
                              icon: const Icon(Icons.refresh, size: 18),
                              onPressed: _acquireGpsLocation,
                              tooltip: "Re-acquire GPS Fix",
                            ),
                          ],
                        ),
                        if (doc?.address.isNotEmpty == true) ...[
                          const SizedBox(height: 2),
                          Text(
                            doc!.address,
                            style: TextStyle(fontSize: 12, color: Colors.grey.shade700),
                          ),
                        ],
                        const Divider(height: 16),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Row(
                              children: [
                                if (_isLoadingGps)
                                  const SizedBox(
                                    width: 14,
                                    height: 14,
                                    child: CircularProgressIndicator(strokeWidth: 2),
                                  )
                                else
                                  Icon(
                                    _currentPosition != null ? Icons.gps_fixed : Icons.gps_off,
                                    size: 15,
                                    color: _currentPosition != null
                                        ? CareOsisColors.medicalEmeraldPrimary
                                        : Colors.orange,
                                  ),
                                const SizedBox(width: 6),
                                Text(
                                  _gpsStatusText,
                                  style: TextStyle(
                                    fontSize: 11,
                                    fontWeight: FontWeight.w600,
                                    color: Colors.grey.shade800,
                                  ),
                                ),
                              ],
                            ),
                            if (distanceMeters != null)
                              Container(
                                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                                decoration: BoxDecoration(
                                  color: isAtClinic ? Colors.green.shade100 : Colors.orange.shade100,
                                  borderRadius: BorderRadius.circular(8),
                                ),
                                child: Text(
                                  isAtClinic
                                      ? "📍 Clinic Verified (${distanceMeters.toStringAsFixed(0)}m)"
                                      : "⚠️ Off-Site (${(distanceMeters / 1000).toStringAsFixed(1)}km)",
                                  style: TextStyle(
                                    fontSize: 10,
                                    fontWeight: FontWeight.bold,
                                    color: isAtClinic ? Colors.green.shade800 : Colors.orange.shade900,
                                  ),
                                ),
                              ),
                          ],
                        ),
                      ],
                    ),
                  ),
                ),
                const SizedBox(height: 16),

                // Detailing Form Section
                const Text(
                  "Call Details & Detailing Objective",
                  style: TextStyle(fontSize: 14, fontWeight: FontWeight.bold, color: Colors.black87),
                ),
                const SizedBox(height: 8),

                // Call Objective
                DropdownButtonFormField<String>(
                  value: _purpose,
                  decoration: InputDecoration(
                    labelText: "Call Objective / Purpose",
                    border: OutlineInputBorder(borderRadius: BorderRadius.circular(10)),
                    filled: true,
                    fillColor: Colors.white,
                  ),
                  items: const [
                    DropdownMenuItem(value: "New Product Introduction", child: Text("New Product Introduction")),
                    DropdownMenuItem(value: "Follow-up Call", child: Text("Follow-up Call")),
                    DropdownMenuItem(value: "Product Reminder", child: Text("Product Reminder")),
                    DropdownMenuItem(value: "Sample Handover", child: Text("Sample Handover")),
                    DropdownMenuItem(value: "Prescription Discussion", child: Text("Prescription Discussion")),
                    DropdownMenuItem(value: "CME / Clinical Invitation", child: Text("CME / Clinical Invitation")),
                  ],
                  onChanged: (v) => setState(() => _purpose = v ?? _purpose),
                ),
                const SizedBox(height: 14),

                // Product Discussed
                DropdownButtonFormField<String>(
                  value: _selectedProduct,
                  decoration: InputDecoration(
                    labelText: "Primary Product Detailed",
                    border: OutlineInputBorder(borderRadius: BorderRadius.circular(10)),
                    filled: true,
                    fillColor: Colors.white,
                  ),
                  items: const [
                    DropdownMenuItem(value: "CardioVasc 20mg", child: Text("CardioVasc 20mg (Atorvastatin)")),
                    DropdownMenuItem(value: "Glucofit SR 500", child: Text("Glucofit SR 500 (Metformin)")),
                    DropdownMenuItem(value: "NeuroBoost Forte", child: Text("NeuroBoost Forte (Mecobalamin)")),
                    DropdownMenuItem(value: "Orthocare Gel", child: Text("Orthocare Fast Pain Relief Gel")),
                    DropdownMenuItem(value: "ImmunoPlus Drops", child: Text("ImmunoPlus Pediatric Drops")),
                  ],
                  onChanged: (v) => setState(() => _selectedProduct = v ?? _selectedProduct),
                ),
                const SizedBox(height: 14),

                // Doctor Response / Receptivity
                DropdownButtonFormField<String>(
                  value: _doctorResponse,
                  decoration: InputDecoration(
                    labelText: "Doctor Response & Receptivity",
                    border: OutlineInputBorder(borderRadius: BorderRadius.circular(10)),
                    filled: true,
                    fillColor: Colors.white,
                  ),
                  items: const [
                    DropdownMenuItem(value: "Positive", child: Text("Positive (Agreed to Prescribe)")),
                    DropdownMenuItem(value: "Neutral", child: Text("Neutral (Requested Clinical Literature)")),
                    DropdownMenuItem(value: "Needs Follow-up", child: Text("Needs Follow-up (Evaluating Samples)")),
                    DropdownMenuItem(value: "Competitor Loyal", child: Text("Competitor Loyal (Brand Switching Pitch)")),
                  ],
                  onChanged: (v) => setState(() => _doctorResponse = v ?? _doctorResponse),
                ),
                const SizedBox(height: 14),

                // Prescription Potential
                DropdownButtonFormField<String>(
                  value: _potential,
                  decoration: InputDecoration(
                    labelText: "Prescription Potential",
                    border: OutlineInputBorder(borderRadius: BorderRadius.circular(10)),
                    filled: true,
                    fillColor: Colors.white,
                  ),
                  items: const [
                    DropdownMenuItem(value: "High", child: Text("High (Top Prescriber)")),
                    DropdownMenuItem(value: "Medium", child: Text("Medium (Occasional Prescriber)")),
                    DropdownMenuItem(value: "Low", child: Text("Low (New / Junior Doctor)")),
                  ],
                  onChanged: (v) => setState(() => _potential = v ?? _potential),
                ),
                const SizedBox(height: 14),

                // Samples Given
                TextField(
                  controller: _samplesController,
                  decoration: InputDecoration(
                    labelText: "Samples / LBL Distributed",
                    border: OutlineInputBorder(borderRadius: BorderRadius.circular(10)),
                    filled: true,
                    fillColor: Colors.white,
                  ),
                ),
                const SizedBox(height: 14),

                // Next Follow-up Date Picker
                InkWell(
                  onTap: () async {
                    final picked = await showDatePicker(
                      context: context,
                      initialDate: DateTime.now().add(const Duration(days: 7)),
                      firstDate: DateTime.now(),
                      lastDate: DateTime.now().add(const Duration(days: 90)),
                    );
                    if (picked != null) {
                      setState(() => _nextFollowUpDate = picked);
                    }
                  },
                  child: Container(
                    padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 14),
                    decoration: BoxDecoration(
                      color: Colors.white,
                      border: Border.all(color: Colors.grey.shade400),
                      borderRadius: BorderRadius.circular(10),
                    ),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Text(
                          _nextFollowUpDate != null
                              ? "Follow-up: ${DateFormat('dd MMM yyyy').format(_nextFollowUpDate!)}"
                              : "Schedule Next Follow-Up Date (Optional)",
                          style: TextStyle(
                            color: _nextFollowUpDate != null ? Colors.black87 : Colors.grey.shade700,
                            fontSize: 14,
                          ),
                        ),
                        const Icon(Icons.calendar_today, size: 18, color: Colors.grey),
                      ],
                    ),
                  ),
                ),
                const SizedBox(height: 14),

                // Detailing Discussion Notes
                TextField(
                  controller: _notesController,
                  maxLines: 3,
                  decoration: InputDecoration(
                    labelText: "Doctor Detailing Discussion Notes",
                    hintText: "Key clinical points discussed, objections addressed, feedback...",
                    border: OutlineInputBorder(borderRadius: BorderRadius.circular(10)),
                    filled: true,
                    fillColor: Colors.white,
                  ),
                ),
                const SizedBox(height: 24),

                // Submit Action
                ElevatedButton.icon(
                  onPressed: () async {
                    final user = widget.repository.currentUser;
                    final visitRule = widget.repository.resolveRule("DOCTOR_VISIT", employeeId: user?.id, regionId: user?.assignedRegionIds);
                    final gpsRule = widget.repository.resolveRule("GPS", employeeId: user?.id, regionId: user?.assignedRegionIds);

                    final mrLat = _currentPosition?.latitude ?? (docLat != 0.0 ? docLat : 28.6139);
                    final mrLng = _currentPosition?.longitude ?? (docLng != 0.0 ? docLng : 77.2090);

                    final eval = RuleEngine.evaluateDoctorVisit(
                      doctorLat: docLat != 0.0 ? docLat : mrLat,
                      doctorLng: docLng != 0.0 ? docLng : mrLng,
                      mrLat: mrLat,
                      mrLng: mrLng,
                      durationMinutes: 10,
                      visitRule: visitRule,
                      gpsRule: gpsRule,
                    );

                    final visitId = "VISIT-${DateTime.now().millisecondsSinceEpoch.toString().substring(7)}";
                    final visit = DoctorVisit(
                      id: visitId,
                      doctorId: widget.doctorId,
                      doctorName: doc?.name ?? "Dr. Prescriber",
                      clinicName: clinicName,
                      startTime: DateFormat("hh:mm a").format(DateTime.now()),
                      visitDate: DateFormat("dd MMM yyyy").format(DateTime.now()),
                      purpose: _purpose,
                      doctorResponse: _doctorResponse,
                      prescriptionPotential: _potential,
                      samplesGiven: _samplesController.text.trim(),
                      productsDiscussed: _selectedProduct,
                      nextFollowUpDate: _nextFollowUpDate != null ? DateFormat("yyyy-MM-dd").format(_nextFollowUpDate!) : "",
                      notes: _notesController.text.trim(),
                      status: eval.requiresApproval ? "Pending Exception Approval" : "Completed",
                      latitude: mrLat,
                      longitude: mrLng,
                      isSynced: false,
                      createdAt: DateTime.now().millisecondsSinceEpoch,
                    );

                    await widget.repository.recordVisit(visit);

                    if (eval.requiresApproval) {
                      await widget.repository.submitApprovalRequest(
                        module: "DOCTOR_VISIT",
                        entityId: visitId,
                        title: "Visit Exception: ${doc?.name ?? 'Doctor Call'}",
                        details: eval.reason,
                        submittedBy: user?.id ?? "MR",
                        submittedByName: user?.name ?? "Field Representative",
                        scope: user?.assignedRegionIds ?? "REG-001",
                        sla: "24h",
                      );
                    }

                    if (mounted) {
                      ScaffoldMessenger.of(context).showSnackBar(
                        SnackBar(
                          content: Text(eval.requiresApproval
                              ? "Field call recorded with geotag exception. Submitted for review."
                              : "Field call recorded & geotagged successfully!"),
                          backgroundColor: eval.requiresApproval
                              ? Colors.orange.shade800
                              : CareOsisColors.medicalEmeraldPrimary,
                        ),
                      );
                      Navigator.pop(context);
                    }
                  },
                  icon: const Icon(Icons.check_circle_outline),
                  label: const Text(
                    "Complete & Submit Field Call",
                    style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15),
                  ),
                  style: ElevatedButton.styleFrom(
                    padding: const EdgeInsets.symmetric(vertical: 16),
                    backgroundColor: CareOsisColors.medicalEmeraldPrimary,
                    foregroundColor: Colors.white,
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                  ),
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
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: const CareOsisTopBar(title: "Field Call History", showBack: true),
      body: StreamBuilder<List<DoctorVisit>>(
        stream: repository.getAllVisits(),
        builder: (context, snapshot) {
          final visits = snapshot.data ?? [];
          if (visits.isEmpty) {
            return const CareOsisEmptyState(
              icon: Icons.assignment_outlined,
              title: "No Field Calls Logged",
              message: "Completed doctor visits and detailing call records will appear here live.",
            );
          }

          return ListView.builder(
            padding: const EdgeInsets.all(12),
            itemCount: visits.length,
            itemBuilder: (context, index) {
              final v = visits[index];
              final isCompleted = v.status == "Completed";

              return Card(
                elevation: 0,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                  side: BorderSide(color: Colors.grey.shade200),
                ),
                margin: const EdgeInsets.only(bottom: 10),
                child: Padding(
                  padding: const EdgeInsets.all(14),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          CircleAvatar(
                            backgroundColor: isCompleted
                                ? CareOsisColors.medicalEmeraldPrimary
                                : Colors.orange.shade700,
                            radius: 20,
                            child: Icon(
                              isCompleted ? Icons.check : Icons.hourglass_top,
                              color: Colors.white,
                              size: 20,
                            ),
                          ),
                          const SizedBox(width: 12),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(
                                  v.doctorName,
                                  style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15),
                                ),
                                const SizedBox(height: 2),
                                Text(
                                  v.clinicName,
                                  style: TextStyle(fontSize: 12, color: Colors.grey.shade700),
                                ),
                              ],
                            ),
                          ),
                          CareOsisStatusChip(label: v.doctorResponse),
                        ],
                      ),
                      const Divider(height: 18),
                      Row(
                        children: [
                          Icon(Icons.calendar_today, size: 13, color: Colors.grey.shade600),
                          const SizedBox(width: 4),
                          Text(
                            "${v.visitDate} • ${v.startTime}",
                            style: TextStyle(fontSize: 12, color: Colors.grey.shade700),
                          ),
                          const Spacer(),
                          if (v.latitude != 0.0 && v.longitude != 0.0)
                            Row(
                              children: [
                                const Icon(Icons.location_on, size: 13, color: CareOsisColors.medicalEmeraldPrimary),
                                const SizedBox(width: 2),
                                Text(
                                  "${v.latitude.toStringAsFixed(3)}, ${v.longitude.toStringAsFixed(3)}",
                                  style: const TextStyle(fontSize: 11, fontWeight: FontWeight.w500),
                                ),
                              ],
                            ),
                        ],
                      ),
                      if (v.productsDiscussed.isNotEmpty) ...[
                        const SizedBox(height: 6),
                        Row(
                          children: [
                            const Icon(Icons.medication_outlined, size: 14, color: Colors.teal),
                            const SizedBox(width: 4),
                            Expanded(
                              child: Text(
                                "Product: ${v.productsDiscussed}",
                                style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w600),
                              ),
                            ),
                          ],
                        ),
                      ],
                      if (v.samplesGiven.isNotEmpty) ...[
                        const SizedBox(height: 4),
                        Text(
                          "Samples: ${v.samplesGiven}",
                          style: TextStyle(fontSize: 11, color: Colors.grey.shade700),
                        ),
                      ],
                      if (v.notes.isNotEmpty) ...[
                        const SizedBox(height: 6),
                        Container(
                          padding: const EdgeInsets.all(8),
                          decoration: BoxDecoration(
                            color: Colors.grey.shade100,
                            borderRadius: BorderRadius.circular(6),
                          ),
                          child: Text(
                            "Notes: ${v.notes}",
                            style: const TextStyle(fontSize: 11, fontStyle: FontStyle.italic),
                          ),
                        ),
                      ],
                    ],
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
