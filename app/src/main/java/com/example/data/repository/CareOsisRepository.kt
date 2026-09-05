package com.example.data.repository

import com.example.data.local.db.CareOsisDatabase
import com.example.data.local.entity.*
import com.example.data.local.seed.SeedDataProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CareOsisRepository(private val database: CareOsisDatabase) {

    private val mrProfileDao = database.mrProfileDao()
    private val doctorDao = database.doctorDao()
    private val doctorVisitDao = database.doctorVisitDao()
    private val productDao = database.productDao()
    private val academyDao = database.academyDao()
    private val commercialDao = database.commercialDao()
    private val platformDao = database.platformDao()
    private val adminAndSecurityDao = database.adminAndSecurityDao()

    private val _currentUser = kotlinx.coroutines.flow.MutableStateFlow<UserAccountEntity?>(null)
    val currentUser: Flow<UserAccountEntity?> = _currentUser

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedDatabaseIfEmpty()
        }
    }

    suspend fun seedDatabaseIfEmpty() = withContext(Dispatchers.IO) {
        val currentProfile = mrProfileDao.getProfileSync()
        if (currentProfile == null) {
            mrProfileDao.insertProfile(SeedDataProvider.getDefaultProfile())
            productDao.insertProducts(SeedDataProvider.getInitialProducts())
            academyDao.insertAllProgress(SeedDataProvider.getInitialTrainingProgress())
            academyDao.insertQuestions(SeedDataProvider.getInitialQuestions())
            doctorDao.insertAll(SeedDataProvider.getInitialDoctors())
            commercialDao.insertStockists(SeedDataProvider.getInitialStockists())
            commercialDao.insertRetailers(SeedDataProvider.getInitialRetailers())
            commercialDao.insertRoutes(SeedDataProvider.getInitialRoutes())
            commercialDao.insertFollowUps(SeedDataProvider.getInitialFollowUps())
            platformDao.insertNotifications(SeedDataProvider.getInitialNotifications())
            platformDao.insertAchievements(SeedDataProvider.getInitialAchievements())
            platformDao.insertLeaderboard(SeedDataProvider.getInitialLeaderboard())
            
            // Seed Admin & Security
            adminAndSecurityDao.insertUsers(SeedDataProvider.getInitialUserAccounts())
            adminAndSecurityDao.insertRegions(SeedDataProvider.getInitialRegions())
            adminAndSecurityDao.insertIncentiveRules(SeedDataProvider.getInitialIncentiveRules())
            adminAndSecurityDao.insertSalaryRule(SeedDataProvider.getInitialSalaryRules())
            for (log in SeedDataProvider.getInitialAuditLogs()) {
                adminAndSecurityDao.insertAuditLog(log)
            }

            // Set default initial logged in user to default MR
            _currentUser.value = adminAndSecurityDao.getUserById("CO-MR-8492")

            for (order in SeedDataProvider.getInitialOrders()) {
                commercialDao.insertOrder(order)
            }
            for (expense in SeedDataProvider.getInitialExpenses()) {
                commercialDao.insertExpense(expense)
            }
            for (visit in SeedDataProvider.getInitialVisits()) {
                doctorVisitDao.insertVisit(visit)
            }
        } else {
            // Ensure security and admin tables are seeded even if MR profile was already present
            val superAdmin = adminAndSecurityDao.getUserById("CO-SA-001")
            if (superAdmin == null) {
                adminAndSecurityDao.insertUsers(SeedDataProvider.getInitialUserAccounts())
                adminAndSecurityDao.insertRegions(SeedDataProvider.getInitialRegions())
                adminAndSecurityDao.insertIncentiveRules(SeedDataProvider.getInitialIncentiveRules())
                adminAndSecurityDao.insertIncentiveRecords(SeedDataProvider.getInitialIncentiveRecords())
                adminAndSecurityDao.insertSalaryRule(SeedDataProvider.getInitialSalaryRules())
                for (log in SeedDataProvider.getInitialAuditLogs()) {
                    adminAndSecurityDao.insertAuditLog(log)
                }
            } else {
                // Ensure initial incentive records exist
                adminAndSecurityDao.insertIncentiveRecords(SeedDataProvider.getInitialIncentiveRecords())
                adminAndSecurityDao.insertIncentiveRules(SeedDataProvider.getInitialIncentiveRules())
            }
            // Ensure currentUser is initialized
            if (_currentUser.value == null) {
                _currentUser.value = adminAndSecurityDao.getUserById("CO-MR-8492")
            }
        }
    }

    // Authentication & User Accounts
    suspend fun authenticate(id: String, password: String): UserAccountEntity? = withContext(Dispatchers.IO) {
        val user = adminAndSecurityDao.authenticateUser(id.trim(), password.trim())
        if (user != null) {
            _currentUser.value = user
        }
        user
    }

    suspend fun setCurrentUser(user: UserAccountEntity?) {
        _currentUser.value = user
    }

    fun getAllUsers(): Flow<List<UserAccountEntity>> = adminAndSecurityDao.getAllUsers()
    fun getAllAdmins(): Flow<List<UserAccountEntity>> = adminAndSecurityDao.getAllAdmins()
    fun getAllEmployees(): Flow<List<UserAccountEntity>> = adminAndSecurityDao.getAllEmployees()
    fun getEmployeesByRegion(regionId: String): Flow<List<UserAccountEntity>> = adminAndSecurityDao.getEmployeesByRegion(regionId)
    
    suspend fun createUser(user: UserAccountEntity, creatorId: String) = withContext(Dispatchers.IO) {
        adminAndSecurityDao.insertUser(user)
        adminAndSecurityDao.insertAuditLog(
            AuditLogEntity(
                userId = creatorId,
                userName = _currentUser.value?.name ?: "Admin",
                userRole = _currentUser.value?.role ?: "SUPER_ADMIN",
                action = if (user.role == "ADMIN") "ADMIN_CREATED" else "EMPLOYEE_CREATED",
                targetEntity = "${user.role}: ${user.name} (${user.id})",
                oldValue = "",
                newValue = "Region: ${user.assignedRegionIds}, Scope: ${user.employeeScopeMode}",
                formattedDate = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
            )
        )
    }

    suspend fun updateAdminScope(
        adminId: String,
        regionIds: String,
        permissions: String,
        canCreateEmployees: Boolean,
        scopeMode: String,
        assignedEmployeeIds: String,
        actorId: String
    ) = withContext(Dispatchers.IO) {
        adminAndSecurityDao.updateAdminScope(adminId, regionIds, permissions, canCreateEmployees, scopeMode, assignedEmployeeIds)
        adminAndSecurityDao.insertAuditLog(
            AuditLogEntity(
                userId = actorId,
                userName = _currentUser.value?.name ?: "Super Admin",
                userRole = "SUPER_ADMIN",
                action = "ADMIN_SCOPE_UPDATED",
                targetEntity = "Admin $adminId",
                oldValue = "",
                newValue = "Regions: $regionIds, Perms: $permissions, Scope: $scopeMode",
                formattedDate = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
            )
        )
    }

    suspend fun updateUserStatus(id: String, status: String, actorId: String) = withContext(Dispatchers.IO) {
        adminAndSecurityDao.updateUserStatus(id, status)
        adminAndSecurityDao.insertAuditLog(
            AuditLogEntity(
                userId = actorId,
                userName = _currentUser.value?.name ?: "Admin",
                userRole = _currentUser.value?.role ?: "SUPER_ADMIN",
                action = "USER_STATUS_UPDATED",
                targetEntity = "User $id",
                oldValue = "",
                newValue = "Status changed to $status",
                formattedDate = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
            )
        )
    }

    // Regions
    fun getAllRegions(): Flow<List<RegionEntity>> = adminAndSecurityDao.getAllRegions()
    suspend fun createRegion(region: RegionEntity, actorId: String) = withContext(Dispatchers.IO) {
        adminAndSecurityDao.insertRegion(region)
        adminAndSecurityDao.insertAuditLog(
            AuditLogEntity(
                userId = actorId,
                userName = _currentUser.value?.name ?: "Super Admin",
                userRole = "SUPER_ADMIN",
                action = "REGION_CREATED",
                targetEntity = "Region ${region.name} (${region.id})",
                oldValue = "",
                newValue = "HQ: ${region.headquarters}, Target: ₹${region.monthlyTarget}",
                formattedDate = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
            )
        )
    }

    // Incentive Rules
    fun getActiveIncentiveRules(): Flow<List<IncentiveRuleEntity>> = adminAndSecurityDao.getActiveIncentiveRules()
    fun getAllIncentiveRules(): Flow<List<IncentiveRuleEntity>> = adminAndSecurityDao.getAllIncentiveRules()
    
    suspend fun getIncentiveRuleById(id: String): IncentiveRuleEntity? = withContext(Dispatchers.IO) {
        adminAndSecurityDao.getIncentiveRuleById(id)
    }

    suspend fun saveIncentiveRule(rule: IncentiveRuleEntity, actorId: String, createNewVersion: Boolean = false) = withContext(Dispatchers.IO) {
        val finalRule = if (createNewVersion) {
            // Increment version
            val newVersion = rule.versionNumber + 1
            rule.copy(
                id = "RULE-${rule.ruleType.take(4)}-${System.currentTimeMillis().toString().takeLast(6)}-V$newVersion",
                versionNumber = newVersion,
                updatedAt = System.currentTimeMillis(),
                updatedBy = actorId
            )
        } else {
            rule.copy(updatedAt = System.currentTimeMillis(), updatedBy = actorId)
        }

        adminAndSecurityDao.insertIncentiveRule(finalRule)
        adminAndSecurityDao.insertAuditLog(
            AuditLogEntity(
                userId = actorId,
                userName = _currentUser.value?.name ?: "Admin",
                userRole = _currentUser.value?.role ?: "SUPER_ADMIN",
                action = if (createNewVersion) "RULE_VERSIONED" else "RULE_UPDATED",
                targetEntity = "Incentive Rule: ${finalRule.ruleName} (v${finalRule.versionNumber})",
                oldValue = if (createNewVersion) "Version ${rule.versionNumber}" else "Active",
                newValue = "Type: ${finalRule.ruleType}, Scope: ${finalRule.regionId}, Effective: ${finalRule.effectiveFrom}",
                formattedDate = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
            )
        )
    }

    suspend fun deleteIncentiveRule(id: String, actorId: String) = withContext(Dispatchers.IO) {
        adminAndSecurityDao.deleteIncentiveRule(id)
        adminAndSecurityDao.insertAuditLog(
            AuditLogEntity(
                userId = actorId,
                userName = _currentUser.value?.name ?: "Admin",
                userRole = _currentUser.value?.role ?: "SUPER_ADMIN",
                action = "RULE_DELETED",
                targetEntity = "Incentive Rule $id",
                oldValue = "Active",
                newValue = "Deleted",
                formattedDate = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
            )
        )
    }

    suspend fun publishNewIncentiveRule(rule: IncentiveRuleEntity, actorId: String) = withContext(Dispatchers.IO) {
        saveIncentiveRule(rule, actorId, createNewVersion = false)
    }

    // Incentive Records (Calculations & Approvals)
    fun getIncentiveRecord(employeeId: String, period: String = "August 2026"): Flow<IncentiveRecordEntity?> =
        adminAndSecurityDao.getIncentiveRecord(employeeId, period)

    fun getIncentiveRecordsForEmployee(employeeId: String): Flow<List<IncentiveRecordEntity>> =
        adminAndSecurityDao.getIncentiveRecordsForEmployee(employeeId)

    fun getAllIncentiveRecords(): Flow<List<IncentiveRecordEntity>> =
        adminAndSecurityDao.getAllIncentiveRecords()

    fun getIncentiveRecordsByPeriod(period: String): Flow<List<IncentiveRecordEntity>> =
        adminAndSecurityDao.getIncentiveRecordsByPeriod(period)

    suspend fun saveIncentiveRecord(record: IncentiveRecordEntity) = withContext(Dispatchers.IO) {
        adminAndSecurityDao.insertIncentiveRecord(record)
    }

    suspend fun approveIncentiveRecord(recordId: String, approverId: String, approverName: String) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        adminAndSecurityDao.updateIncentiveStatus(recordId, "FINAL", approverName, now)
        adminAndSecurityDao.insertAuditLog(
            AuditLogEntity(
                userId = approverId,
                userName = approverName,
                userRole = _currentUser.value?.role ?: "ADMIN",
                action = "INCENTIVE_APPROVED",
                targetEntity = "Incentive Record $recordId",
                oldValue = "PENDING_APPROVAL / ESTIMATED",
                newValue = "Approved as FINAL",
                formattedDate = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
            )
        )
    }

    suspend fun recalculateAndStoreIncentive(
        employee: UserAccountEntity,
        period: String,
        actualSales: Double,
        doctorVisitsDone: Int = 12,
        doctorVisitsTarget: Int = 15,
        newDocs: Int = 6,
        collectionAmt: Double = 180000.0,
        collectionTarget: Double = 200000.0,
        isMonthClosed: Boolean = false
    ): com.example.core.engine.IncentiveResult = withContext(Dispatchers.IO) {
        val rules = adminAndSecurityDao.getActiveIncentiveRules()
        // Synchronously collect rules from dao or query
        val activeRules = mutableListOf<IncentiveRuleEntity>()
        // Fallback rule resolution
        val applicableRule = com.example.core.engine.IncentiveCalculationEngine.resolveApplicableRule(
            employee,
            SeedDataProvider.getInitialIncentiveRules() // fallback or active rules
        ) ?: SeedDataProvider.getInitialIncentiveRules().first()

        val input = com.example.core.engine.CalculationInput(
            employee = employee,
            period = period,
            actualSales = actualSales,
            doctorVisitsDone = doctorVisitsDone,
            doctorVisitsTarget = doctorVisitsTarget,
            newDoctorsActivated = newDocs,
            collectionAmount = collectionAmt,
            collectionTarget = collectionTarget,
            isMonthClosed = isMonthClosed
        )

        val result = com.example.core.engine.IncentiveCalculationEngine.calculateIncentive(input, applicableRule)
        val recordEntity = com.example.core.engine.IncentiveCalculationEngine.toRecordEntity(result)
        adminAndSecurityDao.insertIncentiveRecord(recordEntity)
        result
    }

    // Salary Rules
    fun getActiveSalaryRule(): Flow<SalaryRuleEntity?> = adminAndSecurityDao.getActiveSalaryRule()
    suspend fun updateSalaryRule(rule: SalaryRuleEntity, actorId: String) = withContext(Dispatchers.IO) {
        adminAndSecurityDao.insertSalaryRule(rule)
        adminAndSecurityDao.insertAuditLog(
            AuditLogEntity(
                userId = actorId,
                userName = _currentUser.value?.name ?: "Super Admin",
                userRole = _currentUser.value?.role ?: "SUPER_ADMIN",
                action = "SALARY_RULE_UPDATED",
                targetEntity = "Salary Rule (v${rule.versionNumber})",
                oldValue = "",
                newValue = "Base: ₹${rule.baseSalary}, TA/km: ₹${rule.travelAllowancePerKm}, DA/day: ₹${rule.dailyAllowancePerDay}",
                formattedDate = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
            )
        )
    }

    // Audit Logs
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>> = adminAndSecurityDao.getAllAuditLogs()

    // Dynamic Calculation Engine Helper
    fun calculateIncentive(salesAmount: Double, targetAmount: Double, rules: List<IncentiveRuleEntity>): Double {
        if (targetAmount <= 0.0) return 0.0
        val achievementPercent = (salesAmount / targetAmount) * 100.0
        val matchingRule = rules.firstOrNull { rule ->
            rule.status == "ACTIVE" &&
            rule.ruleType == "SALES_TIER" &&
            achievementPercent >= rule.minThresholdPercent &&
            achievementPercent < rule.maxThresholdPercent
        } ?: rules.lastOrNull { it.status == "ACTIVE" && it.ruleType == "SALES_TIER" }

        val percentageRate = matchingRule?.incentivePercent ?: 0.0
        val fixedReward = matchingRule?.fixedRewardAmount ?: 0.0
        return (salesAmount * (percentageRate / 100.0)) + fixedReward
    }

    // Profile
    fun getProfile(): Flow<MRProfileEntity?> = mrProfileDao.getProfile()
    suspend fun updateProfile(profile: MRProfileEntity) = mrProfileDao.updateProfile(profile)
    suspend fun setCheckedIn(empId: String, isCheckedIn: Boolean, time: String) = mrProfileDao.updateCheckIn(empId, isCheckedIn, time)

    // Doctors
    fun getAllDoctors(): Flow<List<DoctorEntity>> = doctorDao.getAllDoctors()
    fun getDoctorById(id: String): Flow<DoctorEntity?> = doctorDao.getDoctorById(id)
    suspend fun insertDoctor(doctor: DoctorEntity) {
        doctorDao.insertDoctor(doctor)
        platformDao.enqueueSync(
            SyncQueueEntity(
                entityType = "DOCTOR",
                entityId = doctor.id,
                action = "INSERT",
                payloadPreview = "New Doctor: ${doctor.name} (${doctor.specialty})"
            )
        )
    }

    // Doctor Visits
    fun getAllVisits(): Flow<List<DoctorVisitEntity>> = doctorVisitDao.getAllVisits()
    fun getVisitsForDoctor(doctorId: String): Flow<List<DoctorVisitEntity>> = doctorVisitDao.getVisitsForDoctor(doctorId)
    suspend fun recordDoctorVisit(visit: DoctorVisitEntity, empId: String) {
        doctorVisitDao.insertVisit(visit)
        mrProfileDao.incrementCompletedVisits(empId)
        platformDao.enqueueSync(
            SyncQueueEntity(
                entityType = "DOCTOR_VISIT",
                entityId = visit.id,
                action = "INSERT",
                payloadPreview = "Visit: ${visit.doctorName} - ${visit.purpose}"
            )
        )
    }

    // Products & Academy
    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()
    fun getProductById(id: String): Flow<ProductEntity?> = productDao.getProductById(id)
    fun getAllTrainingProgress(): Flow<List<TrainingProgressEntity>> = academyDao.getAllTrainingProgress()
    fun getProgressForProduct(productId: String): Flow<TrainingProgressEntity?> = academyDao.getProgressForProduct(productId)
    fun getQuestionsForProduct(productId: String): Flow<List<AssessmentQuestionEntity>> = academyDao.getQuestionsForProduct(productId)
    
    suspend fun saveTrainingProgress(progress: TrainingProgressEntity) {
        academyDao.insertProgress(progress)
    }

    // Stockists & Retailers
    fun getAllStockists(): Flow<List<StockistEntity>> = commercialDao.getAllStockists()
    fun getStockistById(id: String): Flow<StockistEntity?> = commercialDao.getStockistById(id)
    fun getAllRetailers(): Flow<List<RetailerEntity>> = commercialDao.getAllRetailers()
    fun getRetailerById(id: String): Flow<RetailerEntity?> = commercialDao.getRetailerById(id)

    // Orders
    fun getAllOrders(): Flow<List<OrderEntity>> = commercialDao.getAllOrders()
    fun getOrderById(id: String): Flow<OrderEntity?> = commercialDao.getOrderById(id)
    fun getOrderItems(orderId: String): Flow<List<OrderItemEntity>> = commercialDao.getItemsForOrder(orderId)
    
    suspend fun createOrder(order: OrderEntity, items: List<OrderItemEntity>) {
        commercialDao.insertOrder(order)
        commercialDao.insertOrderItems(items)
        platformDao.enqueueSync(
            SyncQueueEntity(
                entityType = "ORDER",
                entityId = order.id,
                action = "INSERT",
                payloadPreview = "Order #${order.id} for ${order.customerName} (₹${order.totalAmount}) [${order.status}]"
            )
        )
    }

    suspend fun updateOrderStatus(orderId: String, status: String) {
        commercialDao.updateOrderStatus(orderId, status)
        platformDao.enqueueSync(
            SyncQueueEntity(
                entityType = "ORDER_STATUS",
                entityId = orderId,
                action = "UPDATE",
                payloadPreview = "Order #$orderId status updated to $status"
            )
        )
    }

    suspend fun sendOrderToHq(orderId: String) {
        commercialDao.updateOrderStatus(orderId, "Submitted")
        platformDao.enqueueSync(
            SyncQueueEntity(
                entityType = "ORDER_TRANSMIT",
                entityId = orderId,
                action = "SYNC_HQ",
                payloadPreview = "Order #$orderId transmitted to HQ Central Server"
            )
        )
    }

    // Expenses
    fun getAllExpenses(): Flow<List<ExpenseEntity>> = commercialDao.getAllExpenses()
    fun getExpenseById(id: String): Flow<ExpenseEntity?> = commercialDao.getExpenseById(id)
    suspend fun createExpense(expense: ExpenseEntity) {
        commercialDao.insertExpense(expense)
        platformDao.enqueueSync(
            SyncQueueEntity(
                entityType = "EXPENSE",
                entityId = expense.id,
                action = "INSERT",
                payloadPreview = "Expense: ${expense.category} - ₹${expense.amount}"
            )
        )
    }
    suspend fun updateExpenseStatus(expenseId: String, status: String) {
        commercialDao.updateExpenseStatus(expenseId, status)
        platformDao.enqueueSync(
            SyncQueueEntity(
                entityType = "EXPENSE_STATUS",
                entityId = expenseId,
                action = "UPDATE",
                payloadPreview = "Expense #$expenseId updated to $status"
            )
        )
    }
    suspend fun deleteExpense(expenseId: String) {
        commercialDao.deleteExpense(expenseId)
        platformDao.enqueueSync(
            SyncQueueEntity(
                entityType = "EXPENSE",
                entityId = expenseId,
                action = "DELETE",
                payloadPreview = "Expense #$expenseId deleted"
            )
        )
    }

    // Attendance
    fun getAllAttendance(): Flow<List<AttendanceEntity>> = commercialDao.getAllAttendance()
    suspend fun markAttendance(attendance: AttendanceEntity, empId: String) {
        commercialDao.insertAttendance(attendance)
        mrProfileDao.updateCheckIn(empId, isCheckedIn = attendance.checkOutTime.isEmpty(), time = attendance.checkInTime)
        platformDao.enqueueSync(
            SyncQueueEntity(
                entityType = "ATTENDANCE",
                entityId = attendance.id,
                action = "INSERT",
                payloadPreview = "Attendance ${attendance.date}: ${attendance.status} (${attendance.checkInTime})"
            )
        )
    }

    // Routes
    fun getAllRoutes(): Flow<List<RoutePlanEntity>> = commercialDao.getAllRoutes()

    // Follow-ups
    fun getAllFollowUps(): Flow<List<FollowUpEntity>> = commercialDao.getAllFollowUps()
    fun getPendingFollowUps(): Flow<List<FollowUpEntity>> = commercialDao.getPendingFollowUps()
    suspend fun updateFollowUp(followUp: FollowUpEntity) = commercialDao.updateFollowUp(followUp)
    suspend fun addFollowUp(followUp: FollowUpEntity) = commercialDao.insertFollowUp(followUp)

    // Platform (Notifications, Achievements, Leaderboard, Sync)
    fun getAllNotifications(): Flow<List<NotificationEntity>> = platformDao.getAllNotifications()
    fun getUnreadNotificationCount(): Flow<Int> = platformDao.getUnreadNotificationCount()
    suspend fun markNotificationAsRead(id: String) = platformDao.markAsRead(id)
    suspend fun markAllNotificationsAsRead() = platformDao.markAllAsRead()

    fun getAllAchievements(): Flow<List<AchievementEntity>> = platformDao.getAllAchievements()
    fun getLeaderboard(): Flow<List<LeaderboardEntity>> = platformDao.getLeaderboard()

    fun getPendingSyncItems(): Flow<List<SyncQueueEntity>> = platformDao.getPendingSyncItems()
    fun getPendingSyncCount(): Flow<Int> = platformDao.getPendingSyncCount()

    suspend fun performSync() = withContext(Dispatchers.IO) {
        val pending = platformDao.getPendingSyncItems().firstOrNull() ?: emptyList()
        for (item in pending) {
            platformDao.updateSyncStatus(item.id, "SYNCED")
        }
    }
}
