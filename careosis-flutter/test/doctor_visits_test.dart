import 'package:flutter_test/flutter_test.dart';
import '../lib/data/local/db/careosis_database.dart';
import '../lib/data/local/entities/doctor_and_mr_entities.dart';
import '../lib/data/repository/careosis_repository.dart';
import '../lib/core/engine/rule_engine.dart';

void main() {
  group('Doctor & Geotagged Visit Entity Tests', () {
    test('DOC-01: Doctor model with coordinates serialization', () {
      const doc = Doctor(
        id: "DOC-999",
        name: "Dr. Ananya Sharma",
        specialty: "Cardiology",
        qualification: "MBBS, MD",
        clinicHospital: "City Heart Institute",
        address: "42 MG Road, Bangalore",
        phone: "+91 98765 43210",
        email: "dr.sharma@example.com",
        preferredVisitingTime: "11:00 AM - 1:00 PM",
        latitude: 12.9716,
        longitude: 77.5946,
      );

      final map = doc.toMap();
      expect(map['id'], "DOC-999");
      expect(map['latitude'], 12.9716);
      expect(map['longitude'], 77.5946);

      final restored = Doctor.fromMap(map);
      expect(restored.name, "Dr. Ananya Sharma");
      expect(restored.latitude, 12.9716);
      expect(restored.longitude, 77.5946);
    });

    test('VISIT-01: DoctorVisit serialization with live GPS tags and detailing notes', () {
      final visit = DoctorVisit(
        id: "VISIT-001",
        doctorId: "DOC-999",
        doctorName: "Dr. Ananya Sharma",
        clinicName: "City Heart Institute",
        startTime: "11:30 AM",
        visitDate: "05 Sep 2026",
        purpose: "New Product Introduction",
        doctorResponse: "Positive",
        prescriptionPotential: "High",
        samplesGiven: "CardioVasc (2 strips)",
        productsDiscussed: "CardioVasc 20mg",
        notes: "Detailed Atorvastatin efficacy trial results. Doctor agreed to initiate 5 patients.",
        latitude: 12.9718,
        longitude: 77.5948,
        createdAt: 1788590000000,
      );

      final map = visit.toMap();
      expect(map['id'], "VISIT-001");
      expect(map['productsDiscussed'], "CardioVasc 20mg");
      expect(map['latitude'], 12.9718);
      expect(map['longitude'], 77.5948);

      final restored = DoctorVisit.fromMap(map);
      expect(restored.doctorName, "Dr. Ananya Sharma");
      expect(restored.prescriptionPotential, "High");
      expect(restored.latitude, 12.9718);
      expect(restored.longitude, 77.5948);
    });
  });

  group('Clinic Proximity & Rule Engine Tests', () {
    test('GEO-01: Proximity calculation within clinic boundary', () {
      // Doctor Clinic: Bangalore MG Road (12.9716, 77.5946)
      // MR Position: 50m away (12.9720, 77.5946)
      final distance = RuleEngine.calculateHaversineDistanceMeters(
        12.9716,
        77.5946,
        12.9720,
        77.5946,
      );

      expect(distance, lessThan(100.0));
      expect(distance, greaterThan(20.0));
    });

    test('GEO-02: RuleEngine evaluates verified visit vs exception', () {
      // 1. Within 300m threshold
      final resultVerified = RuleEngine.evaluateDoctorVisit(
        doctorLat: 12.9716,
        doctorLng: 77.5946,
        mrLat: 12.9718,
        mrLng: 77.5948,
        durationMinutes: 10,
        visitRule: null,
        gpsRule: null,
      );

      expect(resultVerified.isLocationVerified, isTrue);
      expect(resultVerified.status, "VERIFIED");
      expect(resultVerified.requiresApproval, isFalse);

      // 2. Far away off-site (5km away)
      final resultOffsite = RuleEngine.evaluateDoctorVisit(
        doctorLat: 12.9716,
        doctorLng: 77.5946,
        mrLat: 13.0100,
        mrLng: 77.6200,
        durationMinutes: 10,
        visitRule: null,
        gpsRule: null,
      );

      expect(resultOffsite.isLocationVerified, isFalse);
      expect(resultOffsite.requiresApproval, isTrue);
    });
  });

  group('Repository Doctor Visit Lifecycle Tests', () {
    test('REPO-01: recordVisit saves visit and increments completedVisitsToday', () async {
      final db = CareOsisDatabase.instance;
      final repo = CareOsisRepository(db);

      // Create and insert initial profile
      const profile = MRProfile(
        empId: "CO-MR-8492",
        name: "Aman Chhabra",
        phone: "+91 9988776655",
        email: "aman@careosis.com",
        territory: "North Bangalore",
        managerName: "Rajesh V",
        joiningDate: "01 Jan 2024",
        designation: "Medical Representative",
        level: "Senior",
        trainingProgressPercent: 85,
        monthlyTarget: 300000,
        monthlySales: 150000,
        currentIncentive: 12000,
        completedVisitsToday: 2,
        targetVisitsToday: 10,
      );
      await db.mrProfileDao.insertProfile(profile);

      final initialProfile = db.mrProfileDao.getProfileSync();
      expect(initialProfile?.completedVisitsToday, 2);

      // Record a new visit
      final visit = DoctorVisit(
        id: "VISIT-TEST-100",
        doctorId: "DOC-101",
        doctorName: "Dr. Vikram Seth",
        clinicName: "Apollo Clinic",
        startTime: "02:15 PM",
        visitDate: "05 Sep 2026",
        purpose: "Follow-up Call",
        doctorResponse: "Positive",
        latitude: 12.9716,
        longitude: 77.5946,
        createdAt: DateTime.now().millisecondsSinceEpoch,
      );

      await repo.recordVisit(visit);

      // Check visit is in database
      final allVisits = await repo.getAllVisits().first;
      expect(allVisits.any((v) => v.id == "VISIT-TEST-100"), isTrue);

      // Verify counter incremented from 2 to 3
      final updatedProfile = db.mrProfileDao.getProfileSync();
      expect(updatedProfile?.completedVisitsToday, 3);
    });
  });
}
