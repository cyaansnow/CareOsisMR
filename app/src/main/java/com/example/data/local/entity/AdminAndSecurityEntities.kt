package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    SUPER_ADMIN,
    ADMIN,
    EMPLOYEE // Field MR
}

enum class EmployeeStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    TERMINATED
}

enum class EmployeeScopeMode {
    ALL_IN_REGION,
    SPECIFIC_EMPLOYEES
}

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
    @PrimaryKey val id: String, // e.g. CO-SA-001, CO-ADM-101, CO-MR-8492
    val name: String,
    val email: String,
    val phone: String,
    val role: String, // SUPER_ADMIN, ADMIN, EMPLOYEE
    val password: String,
    val status: String = "ACTIVE", // ACTIVE, INACTIVE, SUSPENDED, TERMINATED
    val assignedRegionIds: String = "DELHI_NCR", // Comma-separated region IDs
    val employeeScopeMode: String = "ALL_IN_REGION", // ALL_IN_REGION, SPECIFIC_EMPLOYEES
    val assignedEmployeeIds: String = "ALL", // Comma-separated employee IDs or "ALL"
    val permissions: String = "VIEW_EMPLOYEES,VIEW_DOCTORS,VIEW_ORDERS,VIEW_EXPENSES,APPROVE_ORDER,APPROVE_EXPENSE", // Comma-separated
    val canCreateEmployees: Boolean = true,
    val baseSalary: Double = 35000.0,
    val fixedAllowance: Double = 8000.0,
    val travelAllowance: Double = 5000.0,
    val otherAllowance: Double = 2000.0,
    val deductions: Double = 1500.0,
    val monthlyTarget: Double = 200000.0,
    val reportingAdminId: String = "CO-ADM-101",
    val joiningDate: String = "01 Jan 2025",
    val designation: String = "Medical Representative",
    val territoryName: String = "North Delhi - Zone 1",
    val createdBy: String = "SYSTEM",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "regions")
data class RegionEntity(
    @PrimaryKey val id: String, // e.g. DELHI_NCR, NOIDA, GHAZIABAD, GURGAON, MUMBAI_CENTRAL, BENGALURU_SOUTH
    val name: String,
    val state: String,
    val code: String,
    val headquarters: String,
    val activeMRCount: Int = 12,
    val doctorCount: Int = 145,
    val monthlyTarget: Double = 2400000.0,
    val status: String = "ACTIVE",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "incentive_rules")
data class IncentiveRuleEntity(
    @PrimaryKey val id: String,
    val ruleName: String,
    val ruleType: String = "PERCENTAGE_OF_SALES", // SLAB_BASED, PERCENTAGE_OF_SALES, TARGET_ACHIEVEMENT_PERCENT, FIXED_AMOUNT, MULTI_COMPONENT
    val targetSource: String = "TOTAL_SALES", // TOTAL_SALES, NET_SALES, COLLECTION, PRODUCT_SALES, APPROVED_SALES
    val defaultTarget: Double = 200000.0,
    val targetPriority: String = "EMPLOYEE_FIRST", // EMPLOYEE_FIRST, REGION_FIRST, RULE_DEFAULT
    val minThresholdPercent: Double = 0.0,
    val maxThresholdPercent: Double = 100.0,
    val incentivePercent: Double = 0.0,
    val fixedRewardAmount: Double = 0.0,
    val slabsJson: String = "", // Serialized list of IncentiveSlab
    val componentsJson: String = "", // Serialized MultiComponentConfig
    val regionId: String = "GLOBAL", // GLOBAL or specific region ID
    val assignedEmployeeIds: String = "ALL", // ALL or comma-separated employee IDs
    val employeeCategory: String = "ALL", // ALL, SENIOR_MR, JUNIOR_MR
    val priority: Int = 4, // 1: Employee-specific, 2: Region-specific, 3: Role-specific, 4: Default company rule
    val versionNumber: Int = 1,
    val effectiveFrom: String = "01-08-2026",
    val effectiveTo: String = "31-12-2026",
    val status: String = "ACTIVE", // ACTIVE, ARCHIVED, DRAFT
    val formulaDescription: String = "Sales * Percentage when achievement is within tier",
    val updatedBy: String = "CO-ADM-101",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "incentive_records")
data class IncentiveRecordEntity(
    @PrimaryKey val id: String, // e.g. INC-CO-MR-8492-AUG-2026
    val employeeId: String,
    val employeeName: String,
    val period: String, // "August 2026", "July 2026", "June 2026"
    val target: Double,
    val actualSales: Double,
    val achievementPercent: Double,
    val ruleId: String,
    val ruleName: String,
    val ruleVersion: Int,
    val ruleType: String,
    val applicableSlab: String,
    val incentiveRate: Double,
    val baseIncentive: Double,
    val coverageIncentive: Double = 0.0,
    val newDoctorIncentive: Double = 0.0,
    val collectionIncentive: Double = 0.0,
    val additionalIncentives: Double = 0.0,
    val deductions: Double = 0.0,
    val finalIncentive: Double,
    val status: String = "ESTIMATED", // "ESTIMATED", "PENDING_APPROVAL", "FINAL"
    val breakdownJson: String = "",
    val calculatedAt: Long = System.currentTimeMillis(),
    val approvedAt: Long? = null,
    val approvedBy: String? = null
)

@Entity(tableName = "salary_rules")
data class SalaryRuleEntity(
    @PrimaryKey val id: String,
    val ruleName: String,
    val baseSalary: Double = 35000.0,
    val fixedAllowance: Double = 8000.0,
    val travelAllowancePerKm: Double = 4.5,
    val dailyAllowancePerDay: Double = 350.0,
    val performanceBonusMax: Double = 15000.0,
    val deductionPfPercent: Double = 12.0,
    val regionId: String = "GLOBAL",
    val versionNumber: Int = 1,
    val effectiveFrom: String = "01-08-2026",
    val effectiveTo: String = "31-12-2026",
    val status: String = "ACTIVE",
    val updatedBy: String = "CO-SA-001",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val userName: String,
    val userRole: String,
    val action: String, // ADMIN_CREATED, EMPLOYEE_CREATED, STATUS_CHANGED, REGION_ASSIGNED, PERMISSION_UPDATED, RULE_CHANGED, ORDER_APPROVED, EXPENSE_APPROVED
    val targetEntity: String, // e.g. User CO-ADM-101, IncentiveRule v2, Order #ORD-101
    val oldValue: String = "",
    val newValue: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val formattedDate: String = ""
)

@Entity(tableName = "calculation_snapshots")
data class CalculationSnapshotEntity(
    @PrimaryKey val id: String,
    val employeeId: String,
    val employeeName: String,
    val regionId: String,
    val monthPeriod: String, // August 2026
    val ruleVersion: Int,
    val inputSales: Double,
    val inputTarget: Double,
    val achievementPercent: Double,
    val calculatedIncentive: Double,
    val fixedSalary: Double,
    val totalPayout: Double,
    val approvedBy: String,
    val approvedDate: String,
    val breakdownSummary: String
)
