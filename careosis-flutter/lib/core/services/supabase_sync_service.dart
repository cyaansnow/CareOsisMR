import 'package:flutter/foundation.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:supabase_flutter/supabase_flutter.dart';
import '../../data/local/entities/commercial_entities.dart';
import '../../data/local/entities/doctor_and_mr_entities.dart';

/// Supabase Cloud Synchronization Service
/// Handles real-time and background sync between the local Drift/SQLite store
/// and the Supabase PostgreSQL backend.
class SupabaseSyncService {
  static final SupabaseSyncService instance = SupabaseSyncService._internal();
  SupabaseSyncService._internal();

  bool _isInitialized = false;
  bool get isInitialized => _isInitialized;

  SupabaseClient? get client => _isInitialized ? Supabase.instance.client : null;

  /// Initializes Supabase using credentials stored in .env
  Future<bool> initialize() async {
    try {
      // Load environment variables safely
      try {
        await dotenv.load(fileName: ".env");
      } catch (e) {
        debugPrint("Notice: .env file not loaded directly: $e");
      }

      final supabaseUrl = dotenv.env['SUPABASE_URL'] ??
          const String.fromEnvironment('SUPABASE_URL');
      final supabaseAnonKey = dotenv.env['SUPABASE_ANON_KEY'] ??
          const String.fromEnvironment('SUPABASE_ANON_KEY');

      if (supabaseUrl.isEmpty || supabaseAnonKey.isEmpty) {
        debugPrint("Supabase credentials missing in .env. Running in pure offline mode.");
        return false;
      }

      await Supabase.initialize(
        url: supabaseUrl,
        anonKey: supabaseAnonKey,
      );

      _isInitialized = true;
      debugPrint("🟢 Supabase client initialized successfully at: $supabaseUrl");
      return true;
    } catch (e) {
      debugPrint("⚠️ Supabase initialization error: $e");
      _isInitialized = false;
      return false;
    }
  }

  // ==========================================
  // Attendance Cloud Sync
  // ==========================================

  /// Upserts attendance record into Supabase attendances table
  Future<bool> syncAttendanceRecord(AttendanceModel attendance, {String? mrId}) async {
    if (!_isInitialized || client == null) return false;

    try {
      final payload = {
        'id': attendance.id,
        'mr_id': mrId ?? 'MR',
        'date': attendance.date,
        'check_in_time': attendance.checkInTime,
        'check_in_lat': attendance.checkInLatitude,
        'check_in_lng': attendance.checkInLongitude,
        'check_in_address': attendance.checkInLocation,
        'check_out_time': attendance.checkOutTime,
        'check_out_lat': attendance.checkOutLatitude,
        'check_out_lng': attendance.checkOutLongitude,
        'check_out_address': attendance.checkOutLocation,
        'status': attendance.status,
        'sync_status': 'SYNCED',
      };

      await client!.from('attendances').upsert(payload);
      return true;
    } catch (e) {
      debugPrint("Attendance sync to Supabase failed: $e");
      return false;
    }
  }

  // ==========================================
  // Doctor Visits Cloud Sync
  // ==========================================

  /// Upserts doctor visit into Supabase doctor_visits table
  Future<bool> syncDoctorVisit(DoctorVisit visit, {String? mrId}) async {
    if (!_isInitialized || client == null) return false;

    try {
      final payload = {
        'id': visit.id,
        'mr_id': mrId ?? 'MR',
        'doctor_id': visit.doctorId,
        'doctor_name': visit.doctorName,
        'clinic_name': visit.clinicName,
        'visit_timestamp': visit.visitDate.isNotEmpty ? "${visit.visitDate} ${visit.startTime}" : DateTime.now().toIso8601String(),
        'latitude': visit.latitude,
        'longitude': visit.longitude,
        'visit_type': visit.purpose,
        'status': visit.status,
        'remarks': "${visit.doctorResponse} | Notes: ${visit.notes}",
        'sync_status': 'SYNCED',
      };

      await client!.from('doctor_visits').upsert(payload);
      return true;
    } catch (e) {
      debugPrint("Doctor visit sync to Supabase failed: $e");
      return false;
    }
  }

  // ==========================================
  // Doctor Directory Fetch
  // ==========================================

  /// Fetches doctors from Supabase cloud database
  Future<List<Doctor>> fetchCloudDoctors() async {
    if (!_isInitialized || client == null) return [];

    try {
      final List<dynamic> records = await client!.from('doctors').select();
      return records.map((r) {
        return Doctor(
          id: r['id']?.toString() ?? '',
          name: r['name']?.toString() ?? '',
          specialty: r['specialty']?.toString() ?? '',
          qualification: r['qualification']?.toString() ?? 'MBBS',
          clinicHospital: r['clinic_name']?.toString() ?? '',
          address: r['address']?.toString() ?? '',
          phone: r['phone']?.toString() ?? '',
          email: r['email']?.toString() ?? '',
          preferredVisitingTime: '10:00 AM - 1:00 PM',
          potentialCategory: r['classification']?.toString() ?? 'A',
          priority: 'High',
        );
      }).toList();
    } catch (e) {
      debugPrint("Fetch cloud doctors failed: $e");
      return [];
    }
  }

  // ==========================================
  // Expenses Cloud Sync
  // ==========================================

  /// Upserts expense record into Supabase expenses table
  Future<bool> syncExpense(ExpenseModel expense, {String? mrId}) async {
    if (!_isInitialized || client == null) return false;

    try {
      final payload = {
        'id': expense.id,
        'mr_id': mrId ?? 'MR',
        'expense_date': expense.date,
        'category': expense.category,
        'amount': expense.amount,
        'receipt_url': expense.receiptPath,
        'notes': expense.description,
        'status': expense.status,
        'sync_status': 'SYNCED',
      };

      await client!.from('expenses').upsert(payload);
      return true;
    } catch (e) {
      debugPrint("Expense sync to Supabase failed: $e");
      return false;
    }
  }

  // ==========================================
  // Targets & Incentives Cloud Sync
  // ==========================================

  /// Upserts monthly target and incentive performance to Supabase targets_incentives table
  Future<bool> syncTargetIncentive({
    required String mrId,
    required String month,
    required double targetAmount,
    required double achievedAmount,
    required double incentiveEarned,
    String payoutStatus = "PENDING",
  }) async {
    if (!_isInitialized || client == null) return false;

    try {
      final id = "TI-$mrId-$month".replaceAll(" ", "-");
      final payload = {
        'id': id,
        'mr_id': mrId,
        'month': month,
        'target_amount': targetAmount,
        'achieved_amount': achievedAmount,
        'incentive_earned': incentiveEarned,
        'payout_status': payoutStatus,
        'updated_at': DateTime.now().toIso8601String(),
      };

      await client!.from('targets_incentives').upsert(payload);
      return true;
    } catch (e) {
      debugPrint("Target & Incentive sync to Supabase failed: $e");
      return false;
    }
  }
}
