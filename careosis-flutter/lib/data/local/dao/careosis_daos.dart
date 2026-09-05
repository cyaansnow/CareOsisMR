import 'dart:async';
import '../db/careosis_database.dart';
import '../entities/doctor_and_mr_entities.dart';
import '../entities/commercial_entities.dart';
import '../entities/platform_entities.dart';
import '../entities/product_and_academy_entities.dart';

class MRProfileDao {
  final CareOsisDatabase _db;
  MRProfileDao(this._db);

  Stream<MRProfile?> getProfile() {
    return _db.profileStream;
  }

  MRProfile? getProfileSync() {
    return _db.mrProfiles.values.firstOrNull;
  }

  Future<void> insertProfile(MRProfile profile) async {
    _db.mrProfiles[profile.empId] = profile;
    _db.notifyProfile();
  }

  Future<void> updateProfile(MRProfile profile) async {
    _db.mrProfiles[profile.empId] = profile;
    _db.notifyProfile();
  }

  Future<void> updateCheckIn(String empId, {required bool isCheckedIn, required String time}) async {
    final current = _db.mrProfiles[empId];
    if (current != null) {
      _db.mrProfiles[empId] = MRProfile(
        empId: current.empId,
        name: current.name,
        phone: current.phone,
        email: current.email,
        territory: current.territory,
        managerName: current.managerName,
        joiningDate: current.joiningDate,
        designation: current.designation,
        level: current.level,
        trainingProgressPercent: current.trainingProgressPercent,
        monthlyTarget: current.monthlyTarget,
        monthlySales: current.monthlySales,
        currentIncentive: current.currentIncentive,
        photoUrl: current.photoUrl,
        isCheckedInToday: isCheckedIn,
        checkInTime: time,
        completedVisitsToday: current.completedVisitsToday,
        targetVisitsToday: current.targetVisitsToday,
      );
      _db.notifyProfile();
    }
  }

  Future<void> incrementCompletedVisits(String empId) async {
    final current = _db.mrProfiles[empId];
    if (current != null) {
      _db.mrProfiles[empId] = MRProfile(
        empId: current.empId,
        name: current.name,
        phone: current.phone,
        email: current.email,
        territory: current.territory,
        managerName: current.managerName,
        joiningDate: current.joiningDate,
        designation: current.designation,
        level: current.level,
        trainingProgressPercent: current.trainingProgressPercent,
        monthlyTarget: current.monthlyTarget,
        monthlySales: current.monthlySales,
        currentIncentive: current.currentIncentive,
        photoUrl: current.photoUrl,
        isCheckedInToday: current.isCheckedInToday,
        checkInTime: current.checkInTime,
        completedVisitsToday: current.completedVisitsToday + 1,
        targetVisitsToday: current.targetVisitsToday,
      );
      _db.notifyProfile();
    }
  }
}

class DoctorDao {
  final CareOsisDatabase _db;
  DoctorDao(this._db);

  Stream<List<Doctor>> getAllDoctors() {
    return _db.doctorsStream;
  }

  Stream<Doctor?> getDoctorById(String id) {
    return _db.doctorsStream.map((list) => _db.doctors[id]);
  }

  Doctor? getDoctorByIdSync(String id) {
    return _db.doctors[id];
  }

  Stream<List<Doctor>> searchDoctors(String query) {
    final q = query.toLowerCase();
    return _db.doctorsStream.map((list) => list.where((d) =>
      d.name.toLowerCase().contains(q) ||
      d.specialty.toLowerCase().contains(q) ||
      d.clinicHospital.toLowerCase().contains(q)
    ).toList());
  }

  Future<void> insertDoctor(Doctor doctor) async {
    _db.doctors[doctor.id] = doctor;
    _db.notifyDoctors();
  }

  Future<void> insertAll(List<Doctor> doctors) async {
    for (final d in doctors) {
      _db.doctors[d.id] = d;
    }
    _db.notifyDoctors();
  }

  Future<void> updateDoctor(Doctor doctor) async {
    _db.doctors[doctor.id] = doctor;
    _db.notifyDoctors();
  }
}

class DoctorVisitDao {
  final CareOsisDatabase _db;
  DoctorVisitDao(this._db);

  Stream<List<DoctorVisit>> getAllVisits() {
    return _db.visitsStream;
  }

  Stream<List<DoctorVisit>> getVisitsForDoctor(String doctorId) {
    return _db.visitsStream.map((list) => list.where((v) => v.doctorId == doctorId).toList());
  }

  Stream<List<DoctorVisit>> getVisitsForDate(String date) {
    return _db.visitsStream.map((list) => list.where((v) => v.visitDate == date).toList());
  }

  Future<void> insertVisit(DoctorVisit visit) async {
    _db.doctorVisits[visit.id] = visit;
    _db.notifyVisits();
  }

  Future<void> updateVisit(DoctorVisit visit) async {
    _db.doctorVisits[visit.id] = visit;
    _db.notifyVisits();
  }

  Future<void> updateVisitApprovalStatus(String id, String status) async {
    final v = _db.doctorVisits[id];
    if (v != null) {
      _db.doctorVisits[id] = DoctorVisit(
        id: v.id,
        doctorId: v.doctorId,
        doctorName: v.doctorName,
        clinicName: v.clinicName,
        startTime: v.startTime,
        endTime: v.endTime,
        visitDate: v.visitDate,
        purpose: v.purpose,
        productsDiscussed: v.productsDiscussed,
        samplesGiven: v.samplesGiven,
        doctorResponse: v.doctorResponse,
        prescriptionPotential: v.prescriptionPotential,
        nextFollowUpDate: v.nextFollowUpDate,
        notes: v.notes,
        status: status,
        latitude: v.latitude,
        longitude: v.longitude,
        isSynced: v.isSynced,
        createdAt: v.createdAt,
      );
      _db.notifyVisits();
    }
  }
}

class ProductDao {
  final CareOsisDatabase _db;
  ProductDao(this._db);

  Stream<List<ProductModel>> getAllProducts() {
    return Stream.value(_db.products.values.toList()..sort((a, b) => a.name.compareTo(b.name)));
  }

  Stream<ProductModel?> getProductById(String id) {
    return Stream.value(_db.products[id]);
  }

  Stream<List<ProductModel>> getFocusProducts() {
    return Stream.value(_db.products.values.where((p) => p.isFocusProduct).toList());
  }

  Future<void> insertProducts(List<ProductModel> products) async {
    for (final p in products) {
      _db.products[p.id] = p;
    }
    _db.notifyProducts();
  }
}

class AcademyDao {
  final CareOsisDatabase _db;
  AcademyDao(this._db);

  Stream<List<TrainingProgressModel>> getAllTrainingProgress() {
    return Stream.value(_db.trainingProgress.values.toList()..sort((a, b) => b.completionPercentage.compareTo(a.completionPercentage)));
  }

  Stream<TrainingProgressModel?> getProgressForProduct(String productId) {
    return Stream.value(_db.trainingProgress[productId]);
  }

  Future<void> insertProgress(TrainingProgressModel progress) async {
    _db.trainingProgress[progress.productId] = progress;
    _db.notifyTraining();
  }

  Future<void> insertAllProgress(List<TrainingProgressModel> list) async {
    for (final p in list) {
      _db.trainingProgress[p.productId] = p;
    }
    _db.notifyTraining();
  }

  Stream<List<AssessmentQuestionModel>> getQuestionsForProduct(String productId) {
    return Stream.value(_db.assessmentQuestions.values.where((q) => q.productId == productId).toList());
  }

  List<AssessmentQuestionModel> getQuestionsForProductSync(String productId) {
    return _db.assessmentQuestions.values.where((q) => q.productId == productId).toList();
  }

  Future<void> insertQuestions(List<AssessmentQuestionModel> questions) async {
    for (final q in questions) {
      _db.assessmentQuestions[q.id] = q;
    }
  }
}

class CommercialDao {
  final CareOsisDatabase _db;
  CommercialDao(this._db);

  Stream<List<Stockist>> getAllStockists() {
    return Stream.value(_db.stockists.values.toList()..sort((a, b) => a.companyName.compareTo(b.companyName)));
  }

  Stream<Stockist?> getStockistById(String id) {
    return Stream.value(_db.stockists[id]);
  }

  Future<void> insertStockists(List<Stockist> list) async {
    for (final s in list) {
      _db.stockists[s.id] = s;
    }
  }

  Stream<List<Retailer>> getAllRetailers() {
    return Stream.value(_db.retailers.values.toList()..sort((a, b) => a.shopName.compareTo(b.shopName)));
  }

  Stream<Retailer?> getRetailerById(String id) {
    return Stream.value(_db.retailers[id]);
  }

  Future<void> insertRetailers(List<Retailer> list) async {
    for (final r in list) {
      _db.retailers[r.id] = r;
    }
  }

  Stream<List<OrderModel>> getAllOrders() {
    return Stream.value(_db.orders.values.toList()..sort((a, b) => b.createdAt.compareTo(a.createdAt)));
  }

  Stream<OrderModel?> getOrderById(String id) {
    return Stream.value(_db.orders[id]);
  }

  Stream<List<OrderItemModel>> getItemsForOrder(String orderId) {
    return Stream.value(_db.orderItems.where((i) => i.orderId == orderId).toList());
  }

  Future<void> insertOrder(OrderModel order) async {
    _db.orders[order.id] = order;
    _db.notifyOrders();
  }

  Future<void> insertOrderItems(List<OrderItemModel> items) async {
    _db.orderItems.addAll(items);
  }

  Future<void> updateOrderStatus(String id, String status) async {
    final o = _db.orders[id];
    if (o != null) {
      _db.orders[id] = OrderModel(
        id: o.id,
        customerId: o.customerId,
        customerName: o.customerName,
        customerType: o.customerType,
        mrId: o.mrId,
        orderDate: o.orderDate,
        subtotal: o.subtotal,
        discountPercent: o.discountPercent,
        discountAmount: o.discountAmount,
        gstAmount: o.gstAmount,
        totalAmount: o.totalAmount,
        itemsSummary: o.itemsSummary,
        status: status,
        notes: o.notes,
        isSynced: o.isSynced,
        createdAt: o.createdAt,
      );
      _db.notifyOrders();
    }
  }

  Stream<List<ExpenseModel>> getAllExpenses() {
    return Stream.value(_db.expenses.values.toList()..sort((a, b) => b.createdAt.compareTo(a.createdAt)));
  }

  Stream<ExpenseModel?> getExpenseById(String id) {
    return Stream.value(_db.expenses[id]);
  }

  Future<void> insertExpense(ExpenseModel expense) async {
    _db.expenses[expense.id] = expense;
    _db.notifyExpenses();
  }

  Future<void> updateExpenseStatus(String id, String status) async {
    final e = _db.expenses[id];
    if (e != null) {
      _db.expenses[id] = ExpenseModel(
        id: e.id,
        date: e.date,
        category: e.category,
        amount: e.amount,
        description: e.description,
        receiptPath: e.receiptPath,
        location: e.location,
        status: status,
        isSynced: e.isSynced,
        createdAt: e.createdAt,
      );
      _db.notifyExpenses();
    }
  }

  Future<void> deleteExpense(String id) async {
    _db.expenses.remove(id);
    _db.notifyExpenses();
  }

  Stream<List<AttendanceModel>> getAllAttendance() {
    return _db.attendanceStream;
  }

  AttendanceModel? getAttendanceByIdSync(String id) {
    return _db.attendance[id];
  }

  Future<void> insertAttendance(AttendanceModel attendance) async {
    _db.attendance[attendance.id] = attendance;
    _db.notifyAttendance();
  }

  Future<void> updateAttendanceApprovalStatus(String id, String status) async {
    final a = _db.attendance[id];
    if (a != null) {
      _db.attendance[id] = AttendanceModel(
        id: a.id,
        date: a.date,
        checkInTime: a.checkInTime,
        checkOutTime: a.checkOutTime,
        workingHours: a.workingHours,
        visitsCompleted: a.visitsCompleted,
        status: status,
        checkInLocation: a.checkInLocation,
        isSynced: a.isSynced,
      );
      _db.notifyAttendance();
    }
  }

  Stream<List<RoutePlanModel>> getAllRoutes() {
    return Stream.value(_db.routes.values.toList()..sort((a, b) => b.date.compareTo(a.date)));
  }

  Future<void> insertRoutes(List<RoutePlanModel> list) async {
    for (final r in list) {
      _db.routes[r.id] = r;
    }
    _db.notifyRoutes();
  }

  Stream<List<FollowUpModel>> getAllFollowUps() {
    return Stream.value(_db.followUps.values.toList()..sort((a, b) => a.followUpDate.compareTo(b.followUpDate)));
  }

  Stream<List<FollowUpModel>> getPendingFollowUps() {
    return Stream.value(_db.followUps.values.where((f) => f.status == "Pending").toList());
  }

  Future<void> insertFollowUp(FollowUpModel followUp) async {
    _db.followUps[followUp.id] = followUp;
    _db.notifyFollowUps();
  }

  Future<void> updateFollowUp(FollowUpModel followUp) async {
    _db.followUps[followUp.id] = followUp;
    _db.notifyFollowUps();
  }
}

class PlatformDao {
  final CareOsisDatabase _db;
  PlatformDao(this._db);

  Stream<List<NotificationModel>> getAllNotifications() {
    return Stream.value(_db.notifications.values.toList()..sort((a, b) => b.timestamp.compareTo(a.timestamp)));
  }

  Stream<int> getUnreadNotificationCount() {
    return Stream.value(_db.notifications.values.where((n) => !n.isRead).length);
  }

  Future<void> insertNotifications(List<NotificationModel> list) async {
    for (final n in list) {
      _db.notifications[n.id] = n;
    }
    _db.notifyNotifications();
  }

  Future<void> markAsRead(String id) async {
    final n = _db.notifications[id];
    if (n != null) {
      _db.notifications[id] = NotificationModel(
        id: n.id,
        title: n.title,
        message: n.message,
        type: n.type,
        timestamp: n.timestamp,
        timeFormatted: n.timeFormatted,
        isRead: true,
        actionRoute: n.actionRoute,
      );
      _db.notifyNotifications();
    }
  }

  Future<void> markAllAsRead() async {
    for (final key in _db.notifications.keys) {
      final n = _db.notifications[key]!;
      _db.notifications[key] = NotificationModel(
        id: n.id,
        title: n.title,
        message: n.message,
        type: n.type,
        timestamp: n.timestamp,
        timeFormatted: n.timeFormatted,
        isRead: true,
        actionRoute: n.actionRoute,
      );
    }
    _db.notifyNotifications();
  }

  Stream<List<AchievementModel>> getAllAchievements() {
    return Stream.value(_db.achievements.values.toList());
  }

  Future<void> insertAchievements(List<AchievementModel> list) async {
    for (final a in list) {
      _db.achievements[a.id] = a;
    }
    _db.notifyAchievements();
  }

  Stream<List<LeaderboardModel>> getLeaderboard() {
    return Stream.value(_db.leaderboard.values.toList()..sort((a, b) => a.rank.compareTo(b.rank)));
  }

  Future<void> insertLeaderboard(List<LeaderboardModel> list) async {
    for (final l in list) {
      _db.leaderboard[l.id] = l;
    }
    _db.notifyLeaderboard();
  }

  Stream<List<SyncQueueModel>> getPendingSyncItems() {
    return Stream.value(_db.syncQueue.where((s) => s.status == "PENDING").toList());
  }

  Stream<int> getPendingSyncCount() {
    return Stream.value(_db.syncQueue.where((s) => s.status == "PENDING").length);
  }

  Future<void> enqueueSync(SyncQueueModel item) async {
    _db.syncQueue.add(item);
    _db.notifySyncQueue();
  }

  Future<void> updateSyncStatus(int? id, String status) async {
    for (int i = 0; i < _db.syncQueue.length; i++) {
      if (_db.syncQueue[i].id == id || (id == null && _db.syncQueue[i].status == "PENDING")) {
        _db.syncQueue[i] = SyncQueueModel(
          id: _db.syncQueue[i].id,
          entityType: _db.syncQueue[i].entityType,
          entityId: _db.syncQueue[i].entityId,
          action: _db.syncQueue[i].action,
          payloadPreview: _db.syncQueue[i].payloadPreview,
          status: status,
          retryCount: _db.syncQueue[i].retryCount,
          createdAt: _db.syncQueue[i].createdAt,
        );
      }
    }
    _db.notifySyncQueue();
  }

  Future<void> updateAttendanceApprovalStatus(String id, String status) async {
    final a = _db.attendance[id];
    if (a != null) {
      _db.attendance[id] = AttendanceModel(
        id: a.id,
        date: a.date,
        checkInTime: a.checkInTime,
        checkOutTime: a.checkOutTime,
        workingHours: a.workingHours,
        visitsCompleted: a.visitsCompleted,
        status: status,
        checkInLocation: a.checkInLocation,
        isSynced: a.isSynced,
      );
      _db.notifyAttendance();
    }
  }
}
