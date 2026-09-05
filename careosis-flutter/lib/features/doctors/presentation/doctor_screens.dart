import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../core/theme/careosis_theme.dart';
import '../../../core/components/careosis_components.dart';
import '../../../data/repository/careosis_repository.dart';
import '../../../data/local/entities/doctor_and_mr_entities.dart';

class DoctorListScreen extends StatefulWidget {
  final CareOsisRepository repository;
  const DoctorListScreen({super.key, required this.repository});

  @override
  State<DoctorListScreen> createState() => _DoctorListScreenState();
}

class _DoctorListScreenState extends State<DoctorListScreen> {
  String _searchQuery = "";
  String _selectedCategory = "ALL";

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: CareOsisTopBar(
        title: "Prescriber CRM",
        subtitle: "Target Doctors Directory",
        actions: [
          IconButton(
            icon: const Icon(Icons.person_add_alt_1, color: Colors.white),
            onPressed: () => context.push('/doctors/add'),
          ),
        ],
      ),
      body: Column(
        children: [
          Container(
            color: Colors.white,
            padding: const EdgeInsets.all(12),
            child: Column(
              children: [
                TextField(
                  onChanged: (v) => setState(() => _searchQuery = v),
                  decoration: InputDecoration(
                    hintText: "Search doctor name, hospital, specialty...",
                    prefixIcon: const Icon(Icons.search),
                    contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                    border: OutlineInputBorder(borderRadius: BorderRadius.circular(10)),
                  ),
                ),
                const SizedBox(height: 8),
                SingleChildScrollView(
                  scrollDirection: Axis.horizontal,
                  child: Row(
                    children: ["ALL", "Category A", "Category B", "Category C"].map((cat) {
                      final isSelected = _selectedCategory == cat;
                      return Padding(
                        padding: const EdgeInsets.only(right: 8),
                        child: ChoiceChip(
                          label: Text(cat),
                          selected: isSelected,
                          selectedColor: CareOsisColors.medicalEmeraldPrimary.withOpacity(0.2),
                          onSelected: (selected) {
                            if (selected) setState(() => _selectedCategory = cat);
                          },
                        ),
                      );
                    }).toList(),
                  ),
                ),
              ],
            ),
          ),
          Expanded(
            child: StreamBuilder<List<Doctor>>(
              stream: widget.repository.getAllDoctors(),
              builder: (context, snapshot) {
                var doctors = snapshot.data ?? [];
                if (_searchQuery.isNotEmpty) {
                  final q = _searchQuery.toLowerCase();
                  doctors = doctors.where((d) =>
                    d.name.toLowerCase().contains(q) ||
                    d.specialty.toLowerCase().contains(q) ||
                    d.clinicHospital.toLowerCase().contains(q)
                  ).toList();
                }
                if (_selectedCategory != "ALL") {
                  final catLetter = _selectedCategory.split(" ").last;
                  doctors = doctors.where((d) => d.potentialCategory == catLetter).toList();
                }

                if (doctors.isEmpty) {
                  return const Center(child: Text("No prescribers matching criteria"));
                }

                return ListView.builder(
                  padding: const EdgeInsets.all(12),
                  itemCount: doctors.length,
                  itemBuilder: (context, index) {
                    final doc = doctors[index];
                    return Card(
                      margin: const EdgeInsets.only(bottom: 10),
                      child: ListTile(
                        leading: CircleAvatar(
                          backgroundColor: CareOsisColors.medicalEmeraldPrimary,
                          child: Text(
                            doc.name.replaceFirst("Dr. ", "").substring(0, 1),
                            style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
                          ),
                        ),
                        title: Text(doc.name, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                        subtitle: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(doc.specialty, style: const TextStyle(fontSize: 12, color: Colors.black87)),
                            Text(doc.clinicHospital, style: const TextStyle(fontSize: 11, color: Colors.black54)),
                          ],
                        ),
                        trailing: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            CareOsisStatusChip(label: "Cat ${doc.potentialCategory}"),
                          ],
                        ),
                        onTap: () => context.push('/doctors/${doc.id}'),
                      ),
                    );
                  },
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}

class DoctorDetailScreen extends StatelessWidget {
  final CareOsisRepository repository;
  final String doctorId;
  const DoctorDetailScreen({super.key, required this.repository, required this.doctorId});

  @override
  Widget build(BuildContext context) {
    return StreamBuilder<Doctor?>(
      stream: repository.getDoctorById(doctorId),
      builder: (context, snapshot) {
        final doc = snapshot.data;
        if (doc == null) {
          return const Scaffold(body: Center(child: CircularProgressIndicator()));
        }

        return Scaffold(
          backgroundColor: const Color(0xFFF8FAFC),
          appBar: CareOsisTopBar(
            title: doc.name,
            subtitle: doc.specialty,
            showBack: true,
          ),
          body: SingleChildScrollView(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Card(
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Text(doc.name, style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                            CareOsisStatusChip(label: "Category ${doc.potentialCategory}"),
                          ],
                        ),
                        Text(doc.qualification, style: const TextStyle(fontSize: 13, color: Colors.black54)),
                        const SizedBox(height: 12),
                        _buildInfoRow(Icons.local_hospital_outlined, doc.clinicHospital),
                        _buildInfoRow(Icons.location_on_outlined, doc.address),
                        _buildInfoRow(Icons.phone_outlined, doc.phone),
                        _buildInfoRow(Icons.access_time_outlined, doc.preferredVisitingTime),
                      ],
                    ),
                  ),
                ),
                const SizedBox(height: 16),
                ElevatedButton.icon(
                  onPressed: () => context.push('/visits/start/${doc.id}'),
                  icon: const Icon(Icons.play_arrow),
                  label: const Text("Start Detailing Visit"),
                  style: ElevatedButton.styleFrom(
                    padding: const EdgeInsets.symmetric(vertical: 14),
                    backgroundColor: CareOsisColors.medicalEmeraldPrimary,
                  ),
                ),
                const SizedBox(height: 16),
                const Text("Call History & Detailing Notes", style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold)),
                const SizedBox(height: 8),
                StreamBuilder<List<DoctorVisit>>(
                  stream: repository.getVisitsForDoctor(doctorId),
                  builder: (context, visitSnapshot) {
                    final visits = visitSnapshot.data ?? [];
                    if (visits.isEmpty) {
                      return const Card(
                        child: Padding(
                          padding: EdgeInsets.all(16),
                          child: Text("No previous visits recorded for this prescriber.", textAlign: TextAlign.center),
                        ),
                      );
                    }
                    return ListView.builder(
                      shrinkWrap: true,
                      physics: const NeverScrollableScrollPhysics(),
                      itemCount: visits.length,
                      itemBuilder: (context, index) {
                        final v = visits[index];
                        return Card(
                          margin: const EdgeInsets.only(bottom: 8),
                          child: ListTile(
                            title: Text(v.purpose, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
                            subtitle: Text("Date: ${v.visitDate} • Response: ${v.doctorResponse}", style: const TextStyle(fontSize: 12)),
                            trailing: CareOsisStatusChip(label: v.prescriptionPotential),
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
      },
    );
  }

  Widget _buildInfoRow(IconData icon, String text) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 8),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Icon(icon, size: 16, color: CareOsisColors.medicalEmeraldPrimary),
          const SizedBox(width: 8),
          Expanded(child: Text(text, style: const TextStyle(fontSize: 13))),
        ],
      ),
    );
  }
}

class AddDoctorScreen extends StatefulWidget {
  final CareOsisRepository repository;
  const AddDoctorScreen({super.key, required this.repository});

  @override
  State<AddDoctorScreen> createState() => _AddDoctorScreenState();
}

class _AddDoctorScreenState extends State<AddDoctorScreen> {
  final _nameController = TextEditingController();
  final _specialtyController = TextEditingController();
  final _hospitalController = TextEditingController();
  final _addressController = TextEditingController();
  final _phoneController = TextEditingController();
  String _category = "A";

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CareOsisTopBar(title: "Add New Prescriber", showBack: true),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            TextField(controller: _nameController, decoration: const InputDecoration(labelText: "Doctor Full Name (e.g. Dr. Rajesh Kumar)")),
            const SizedBox(height: 12),
            TextField(controller: _specialtyController, decoration: const InputDecoration(labelText: "Medical Specialty")),
            const SizedBox(height: 12),
            TextField(controller: _hospitalController, decoration: const InputDecoration(labelText: "Clinic / Hospital Name")),
            const SizedBox(height: 12),
            TextField(controller: _addressController, decoration: const InputDecoration(labelText: "Address & Territory")),
            const SizedBox(height: 12),
            TextField(controller: _phoneController, decoration: const InputDecoration(labelText: "Phone Number")),
            const SizedBox(height: 16),
            DropdownButtonFormField<String>(
              value: _category,
              decoration: const InputDecoration(labelText: "Potential Category"),
              items: const [
                DropdownMenuItem(value: "A", child: Text("Category A (High Yield)")),
                DropdownMenuItem(value: "B", child: Text("Category B (Medium)")),
                DropdownMenuItem(value: "C", child: Text("Category C (Standard)")),
              ],
              onChanged: (v) => setState(() => _category = v ?? "A"),
            ),
            const SizedBox(height: 24),
            ElevatedButton(
              onPressed: () async {
                if (_nameController.text.trim().isEmpty) return;
                final doc = Doctor(
                  id: "DOC-${DateTime.now().millisecondsSinceEpoch.toString().substring(7)}",
                  name: _nameController.text.trim(),
                  specialty: _specialtyController.text.trim(),
                  qualification: "MBBS",
                  clinicHospital: _hospitalController.text.trim(),
                  address: _addressController.text.trim(),
                  phone: _phoneController.text.trim(),
                  email: "",
                  preferredVisitingTime: "11:00 AM - 01:00 PM",
                  potentialCategory: _category,
                );
                await widget.repository.addDoctor(doc);
                if (mounted) Navigator.pop(context);
              },
              child: const Text("Register Prescriber"),
            ),
          ],
        ),
      ),
    );
  }
}
