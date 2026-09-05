package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MRProfileDao {
    @Query("SELECT * FROM mr_profiles LIMIT 1")
    fun getProfile(): Flow<MRProfileEntity?>

    @Query("SELECT * FROM mr_profiles LIMIT 1")
    suspend fun getProfileSync(): MRProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: MRProfileEntity)

    @Update
    suspend fun updateProfile(profile: MRProfileEntity)

    @Query("UPDATE mr_profiles SET isCheckedInToday = :isCheckedIn, checkInTime = :time WHERE empId = :empId")
    suspend fun updateCheckIn(empId: String, isCheckedIn: Boolean, time: String)

    @Query("UPDATE mr_profiles SET completedVisitsToday = completedVisitsToday + 1 WHERE empId = :empId")
    suspend fun incrementCompletedVisits(empId: String)
}

@Dao
interface DoctorDao {
    @Query("SELECT * FROM doctors ORDER BY priority DESC, name ASC")
    fun getAllDoctors(): Flow<List<DoctorEntity>>

    @Query("SELECT * FROM doctors WHERE id = :id")
    fun getDoctorById(id: String): Flow<DoctorEntity?>

    @Query("SELECT * FROM doctors WHERE id = :id")
    suspend fun getDoctorByIdSync(id: String): DoctorEntity?

    @Query("SELECT * FROM doctors WHERE name LIKE '%' || :query || '%' OR specialty LIKE '%' || :query || '%' OR clinicHospital LIKE '%' || :query || '%'")
    fun searchDoctors(query: String): Flow<List<DoctorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoctor(doctor: DoctorEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(doctors: List<DoctorEntity>)

    @Update
    suspend fun updateDoctor(doctor: DoctorEntity)
}

@Dao
interface DoctorVisitDao {
    @Query("SELECT * FROM doctor_visits ORDER BY createdAt DESC")
    fun getAllVisits(): Flow<List<DoctorVisitEntity>>

    @Query("SELECT * FROM doctor_visits WHERE doctorId = :doctorId ORDER BY createdAt DESC")
    fun getVisitsForDoctor(doctorId: String): Flow<List<DoctorVisitEntity>>

    @Query("SELECT * FROM doctor_visits WHERE visitDate = :date ORDER BY createdAt DESC")
    fun getVisitsForDate(date: String): Flow<List<DoctorVisitEntity>>

    @Query("SELECT COUNT(*) FROM doctor_visits WHERE visitDate = :date")
    fun getVisitCountForDate(date: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisit(visit: DoctorVisitEntity)

    @Update
    suspend fun updateVisit(visit: DoctorVisitEntity)
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    fun getProductById(id: String): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE isFocusProduct = 1")
    fun getFocusProducts(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)
}

@Dao
interface AcademyDao {
    @Query("SELECT * FROM training_progress ORDER BY completionPercentage DESC")
    fun getAllTrainingProgress(): Flow<List<TrainingProgressEntity>>

    @Query("SELECT * FROM training_progress WHERE productId = :productId")
    fun getProgressForProduct(productId: String): Flow<TrainingProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: TrainingProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProgress(list: List<TrainingProgressEntity>)

    @Query("SELECT * FROM assessment_questions WHERE productId = :productId")
    fun getQuestionsForProduct(productId: String): Flow<List<AssessmentQuestionEntity>>

    @Query("SELECT * FROM assessment_questions WHERE productId = :productId")
    suspend fun getQuestionsForProductSync(productId: String): List<AssessmentQuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<AssessmentQuestionEntity>)
}

@Dao
interface CommercialDao {
    // Stockists
    @Query("SELECT * FROM stockists ORDER BY companyName ASC")
    fun getAllStockists(): Flow<List<StockistEntity>>

    @Query("SELECT * FROM stockists WHERE id = :id")
    fun getStockistById(id: String): Flow<StockistEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockists(stockists: List<StockistEntity>)

    // Retailers
    @Query("SELECT * FROM retailers ORDER BY shopName ASC")
    fun getAllRetailers(): Flow<List<RetailerEntity>>

    @Query("SELECT * FROM retailers WHERE id = :id")
    fun getRetailerById(id: String): Flow<RetailerEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRetailers(retailers: List<RetailerEntity>)

    // Orders
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :id")
    fun getOrderById(id: String): Flow<OrderEntity?>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getItemsForOrder(orderId: String): Flow<List<OrderItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Query("UPDATE orders SET status = :status WHERE id = :id")
    suspend fun updateOrderStatus(id: String, status: String)

    @Query("SELECT SUM(totalAmount) FROM orders WHERE orderDate = :date")
    fun getOrderTotalForDate(date: String): Flow<Double?>

    // Expenses
    @Query("SELECT * FROM expenses ORDER BY createdAt DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE id = :id")
    fun getExpenseById(id: String): Flow<ExpenseEntity?>

    @Query("UPDATE expenses SET status = :status WHERE id = :id")
    suspend fun updateExpenseStatus(id: String, status: String)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpense(id: String)

    @Query("SELECT SUM(amount) FROM expenses WHERE date = :date")
    fun getExpenseTotalForDate(date: String): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    // Attendance
    @Query("SELECT * FROM attendance ORDER BY date DESC")
    fun getAllAttendance(): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE date = :date LIMIT 1")
    fun getAttendanceForDate(date: String): Flow<AttendanceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity)

    // Routes
    @Query("SELECT * FROM route_plans ORDER BY date DESC")
    fun getAllRoutes(): Flow<List<RoutePlanEntity>>

    @Query("SELECT * FROM route_plans WHERE date = :date LIMIT 1")
    fun getRouteForDate(date: String): Flow<RoutePlanEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: RoutePlanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutes(routes: List<RoutePlanEntity>)

    // Follow-ups
    @Query("SELECT * FROM follow_ups ORDER BY followUpDate ASC, priority DESC")
    fun getAllFollowUps(): Flow<List<FollowUpEntity>>

    @Query("SELECT * FROM follow_ups WHERE status = 'Pending' ORDER BY followUpDate ASC")
    fun getPendingFollowUps(): Flow<List<FollowUpEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollowUp(followUp: FollowUpEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollowUps(followUps: List<FollowUpEntity>)

    @Update
    suspend fun updateFollowUp(followUp: FollowUpEntity)
}

@Dao
interface PlatformDao {
    // Notifications
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadNotificationCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()

    // Achievements
    @Query("SELECT * FROM achievements ORDER BY isUnlocked DESC, progress DESC")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    // Leaderboard
    @Query("SELECT * FROM leaderboard ORDER BY rank ASC")
    fun getLeaderboard(): Flow<List<LeaderboardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboard(records: List<LeaderboardEntity>)

    // Sync Queue
    @Query("SELECT * FROM sync_queue WHERE status = 'PENDING' ORDER BY createdAt ASC")
    fun getPendingSyncItems(): Flow<List<SyncQueueEntity>>

    @Query("SELECT COUNT(*) FROM sync_queue WHERE status = 'PENDING'")
    fun getPendingSyncCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueueSync(item: SyncQueueEntity)

    @Query("UPDATE sync_queue SET status = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: Long, status: String)

    @Query("DELETE FROM sync_queue WHERE status = 'SYNCED'")
    suspend fun clearSynced()
}
