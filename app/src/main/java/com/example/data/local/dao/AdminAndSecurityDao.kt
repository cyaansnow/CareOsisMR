package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AdminAndSecurityDao {

    // User Accounts
    @Query("SELECT * FROM user_accounts WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE id = :id AND password = :password LIMIT 1")
    suspend fun authenticateUser(id: String, password: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts")
    fun getAllUsers(): Flow<List<UserAccountEntity>>

    @Query("SELECT * FROM user_accounts WHERE role = 'ADMIN'")
    fun getAllAdmins(): Flow<List<UserAccountEntity>>

    @Query("SELECT * FROM user_accounts WHERE role = 'EMPLOYEE'")
    fun getAllEmployees(): Flow<List<UserAccountEntity>>

    @Query("SELECT * FROM user_accounts WHERE role = 'EMPLOYEE' AND (assignedRegionIds LIKE '%' || :regionId || '%' OR :regionId = 'ALL')")
    fun getEmployeesByRegion(regionId: String): Flow<List<UserAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserAccountEntity>)

    @Update
    suspend fun updateUser(user: UserAccountEntity)

    @Query("UPDATE user_accounts SET status = :status WHERE id = :id")
    suspend fun updateUserStatus(id: String, status: String)

    @Query("UPDATE user_accounts SET assignedRegionIds = :regionIds, permissions = :permissions, canCreateEmployees = :canCreateEmployees, employeeScopeMode = :scopeMode, assignedEmployeeIds = :assignedEmployeeIds WHERE id = :adminId")
    suspend fun updateAdminScope(
        adminId: String,
        regionIds: String,
        permissions: String,
        canCreateEmployees: Boolean,
        scopeMode: String,
        assignedEmployeeIds: String
    )

    // Regions
    @Query("SELECT * FROM regions")
    fun getAllRegions(): Flow<List<RegionEntity>>

    @Query("SELECT * FROM regions WHERE id = :id LIMIT 1")
    suspend fun getRegionById(id: String): RegionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegion(region: RegionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegions(regions: List<RegionEntity>)

    // Incentive Rules
    @Query("SELECT * FROM incentive_rules WHERE status = 'ACTIVE' ORDER BY priority ASC, versionNumber DESC")
    fun getActiveIncentiveRules(): Flow<List<IncentiveRuleEntity>>

    @Query("SELECT * FROM incentive_rules ORDER BY versionNumber DESC, priority ASC")
    fun getAllIncentiveRules(): Flow<List<IncentiveRuleEntity>>

    @Query("SELECT * FROM incentive_rules WHERE id = :id LIMIT 1")
    suspend fun getIncentiveRuleById(id: String): IncentiveRuleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncentiveRule(rule: IncentiveRuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncentiveRules(rules: List<IncentiveRuleEntity>)

    @Query("DELETE FROM incentive_rules WHERE id = :id")
    suspend fun deleteIncentiveRule(id: String)

    @Query("UPDATE incentive_rules SET status = 'ARCHIVED' WHERE versionNumber < :newVersion")
    suspend fun archiveOlderIncentiveVersions(newVersion: Int)

    // Incentive Records (Calculated Snapshots for MR and Admin)
    @Query("SELECT * FROM incentive_records WHERE employeeId = :employeeId AND period = :period LIMIT 1")
    fun getIncentiveRecord(employeeId: String, period: String): Flow<IncentiveRecordEntity?>

    @Query("SELECT * FROM incentive_records WHERE employeeId = :employeeId ORDER BY calculatedAt DESC")
    fun getIncentiveRecordsForEmployee(employeeId: String): Flow<List<IncentiveRecordEntity>>

    @Query("SELECT * FROM incentive_records ORDER BY calculatedAt DESC")
    fun getAllIncentiveRecords(): Flow<List<IncentiveRecordEntity>>

    @Query("SELECT * FROM incentive_records WHERE period = :period ORDER BY calculatedAt DESC")
    fun getIncentiveRecordsByPeriod(period: String): Flow<List<IncentiveRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncentiveRecord(record: IncentiveRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncentiveRecords(records: List<IncentiveRecordEntity>)

    @Query("UPDATE incentive_records SET status = :status, approvedBy = :approvedBy, approvedAt = :approvedAt WHERE id = :id")
    suspend fun updateIncentiveStatus(id: String, status: String, approvedBy: String, approvedAt: Long)

    // Salary Rules
    @Query("SELECT * FROM salary_rules WHERE status = 'ACTIVE' LIMIT 1")
    fun getActiveSalaryRule(): Flow<SalaryRuleEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSalaryRule(rule: SalaryRuleEntity)

    // Audit Logs
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)

    // Calculation Snapshots
    @Query("SELECT * FROM calculation_snapshots WHERE monthPeriod = :monthPeriod")
    fun getCalculationSnapshots(monthPeriod: String): Flow<List<CalculationSnapshotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculationSnapshot(snapshot: CalculationSnapshotEntity)
}
