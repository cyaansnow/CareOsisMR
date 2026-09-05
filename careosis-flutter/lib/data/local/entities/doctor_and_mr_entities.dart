class MRProfile {
  final String empId;
  final String name;
  final String phone;
  final String email;
  final String territory;
  final String managerName;
  final String joiningDate;
  final String designation;
  final String level;
  final int trainingProgressPercent;
  final double monthlyTarget;
  final double monthlySales;
  final double currentIncentive;
  final String photoUrl;
  final bool isCheckedInToday;
  final String checkInTime;
  final int completedVisitsToday;
  final int targetVisitsToday;

  const MRProfile({
    required this.empId,
    required this.name,
    required this.phone,
    required this.email,
    required this.territory,
    required this.managerName,
    required this.joiningDate,
    required this.designation,
    required this.level,
    required this.trainingProgressPercent,
    required this.monthlyTarget,
    required this.monthlySales,
    required this.currentIncentive,
    this.photoUrl = "",
    this.isCheckedInToday = false,
    this.checkInTime = "",
    this.completedVisitsToday = 0,
    this.targetVisitsToday = 15,
  });

  Map<String, dynamic> toMap() => {
        'empId': empId,
        'name': name,
        'phone': phone,
        'email': email,
        'territory': territory,
        'managerName': managerName,
        'joiningDate': joiningDate,
        'designation': designation,
        'level': level,
        'trainingProgressPercent': trainingProgressPercent,
        'monthlyTarget': monthlyTarget,
        'monthlySales': monthlySales,
        'currentIncentive': currentIncentive,
        'photoUrl': photoUrl,
        'isCheckedInToday': isCheckedInToday ? 1 : 0,
        'checkInTime': checkInTime,
        'completedVisitsToday': completedVisitsToday,
        'targetVisitsToday': targetVisitsToday,
      };

  factory MRProfile.fromMap(Map<String, dynamic> map) => MRProfile(
        empId: map['empId'] as String,
        name: map['name'] as String,
        phone: map['phone'] as String,
        email: map['email'] as String,
        territory: map['territory'] as String,
        managerName: map['managerName'] as String,
        joiningDate: map['joiningDate'] as String,
        designation: map['designation'] as String,
        level: map['level'] as String? ?? "Beginner",
        trainingProgressPercent: (map['trainingProgressPercent'] as num?)?.toInt() ?? 0,
        monthlyTarget: (map['monthlyTarget'] as num?)?.toDouble() ?? 200000.0,
        monthlySales: (map['monthlySales'] as num?)?.toDouble() ?? 0.0,
        currentIncentive: (map['currentIncentive'] as num?)?.toDouble() ?? 0.0,
        photoUrl: map['photoUrl'] as String? ?? "",
        isCheckedInToday: map['isCheckedInToday'] == 1 || map['isCheckedInToday'] == true,
        checkInTime: map['checkInTime'] as String? ?? "",
        completedVisitsToday: (map['completedVisitsToday'] as num?)?.toInt() ?? 0,
        targetVisitsToday: (map['targetVisitsToday'] as num?)?.toInt() ?? 15,
      );
}

class Doctor {
  final String id;
  final String name;
  final String specialty;
  final String qualification;
  final String clinicHospital;
  final String address;
  final String phone;
  final String email;
  final String preferredVisitingTime;
  final String birthday;
  final String anniversary;
  final String potentialCategory;
  final String priority;
  final String notes;
  final String lastVisitDate;
  final String nextFollowUpDate;
  final String productsDiscussed;
  final double latitude;
  final double longitude;
  final bool isSynced;

  const Doctor({
    required this.id,
    required this.name,
    required this.specialty,
    required this.qualification,
    required this.clinicHospital,
    required this.address,
    required this.phone,
    required this.email,
    required this.preferredVisitingTime,
    this.birthday = "",
    this.anniversary = "",
    this.potentialCategory = "A",
    this.priority = "High",
    this.notes = "",
    this.lastVisitDate = "",
    this.nextFollowUpDate = "",
    this.productsDiscussed = "",
    this.latitude = 0.0,
    this.longitude = 0.0,
    this.isSynced = true,
  });

  Map<String, dynamic> toMap() => {
        'id': id,
        'name': name,
        'specialty': specialty,
        'qualification': qualification,
        'clinicHospital': clinicHospital,
        'address': address,
        'phone': phone,
        'email': email,
        'preferredVisitingTime': preferredVisitingTime,
        'birthday': birthday,
        'anniversary': anniversary,
        'potentialCategory': potentialCategory,
        'priority': priority,
        'notes': notes,
        'lastVisitDate': lastVisitDate,
        'nextFollowUpDate': nextFollowUpDate,
        'productsDiscussed': productsDiscussed,
        'latitude': latitude,
        'longitude': longitude,
        'isSynced': isSynced ? 1 : 0,
      };

  factory Doctor.fromMap(Map<String, dynamic> map) => Doctor(
        id: map['id'] as String,
        name: map['name'] as String,
        specialty: map['specialty'] as String,
        qualification: map['qualification'] as String,
        clinicHospital: map['clinicHospital'] as String,
        address: map['address'] as String,
        phone: map['phone'] as String,
        email: map['email'] as String,
        preferredVisitingTime: map['preferredVisitingTime'] as String,
        birthday: map['birthday'] as String? ?? "",
        anniversary: map['anniversary'] as String? ?? "",
        potentialCategory: map['potentialCategory'] as String? ?? "A",
        priority: map['priority'] as String? ?? "High",
        notes: map['notes'] as String? ?? "",
        lastVisitDate: map['lastVisitDate'] as String? ?? "",
        nextFollowUpDate: map['nextFollowUpDate'] as String? ?? "",
        productsDiscussed: map['productsDiscussed'] as String? ?? "",
        latitude: (map['latitude'] as num?)?.toDouble() ?? 0.0,
        longitude: (map['longitude'] as num?)?.toDouble() ?? 0.0,
        isSynced: map['isSynced'] == 1 || map['isSynced'] == true,
      );
}

class DoctorVisit {
  final String id;
  final String doctorId;
  final String doctorName;
  final String clinicName;
  final String startTime;
  final String endTime;
  final String visitDate;
  final String purpose;
  final String doctorResponse;
  final String prescriptionPotential;
  final String samplesGiven;
  final String productsDiscussed;
  final String nextFollowUpDate;
  final String notes;
  final String status;
  final double latitude;
  final double longitude;
  final bool isSynced;
  final int createdAt;

  const DoctorVisit({
    required this.id,
    required this.doctorId,
    required this.doctorName,
    required this.clinicName,
    required this.startTime,
    this.endTime = "",
    required this.visitDate,
    required this.purpose,
    this.doctorResponse = "Positive",
    this.prescriptionPotential = "High",
    this.samplesGiven = "",
    this.productsDiscussed = "",
    this.nextFollowUpDate = "",
    this.notes = "",
    this.status = "Completed",
    this.latitude = 0.0,
    this.longitude = 0.0,
    this.isSynced = false,
    required this.createdAt,
  });

  Map<String, dynamic> toMap() => {
        'id': id,
        'doctorId': doctorId,
        'doctorName': doctorName,
        'clinicName': clinicName,
        'startTime': startTime,
        'endTime': endTime,
        'visitDate': visitDate,
        'purpose': purpose,
        'doctorResponse': doctorResponse,
        'prescriptionPotential': prescriptionPotential,
        'samplesGiven': samplesGiven,
        'productsDiscussed': productsDiscussed,
        'nextFollowUpDate': nextFollowUpDate,
        'notes': notes,
        'status': status,
        'latitude': latitude,
        'longitude': longitude,
        'isSynced': isSynced ? 1 : 0,
        'createdAt': createdAt,
      };

  factory DoctorVisit.fromMap(Map<String, dynamic> map) => DoctorVisit(
        id: map['id'] as String,
        doctorId: map['doctorId'] as String,
        doctorName: map['doctorName'] as String,
        clinicName: map['clinicName'] as String,
        startTime: map['startTime'] as String,
        endTime: map['endTime'] as String? ?? "",
        visitDate: map['visitDate'] as String,
        purpose: map['purpose'] as String,
        doctorResponse: map['doctorResponse'] as String? ?? "Positive",
        prescriptionPotential: map['prescriptionPotential'] as String? ?? "High",
        samplesGiven: map['samplesGiven'] as String? ?? "",
        productsDiscussed: map['productsDiscussed'] as String? ?? "",
        nextFollowUpDate: map['nextFollowUpDate'] as String? ?? "",
        notes: map['notes'] as String? ?? "",
        status: map['status'] as String? ?? "Completed",
        latitude: (map['latitude'] as num?)?.toDouble() ?? 0.0,
        longitude: (map['longitude'] as num?)?.toDouble() ?? 0.0,
        isSynced: map['isSynced'] == 1 || map['isSynced'] == true,
        createdAt: (map['createdAt'] as num?)?.toInt() ?? DateTime.now().millisecondsSinceEpoch,
      );
}
