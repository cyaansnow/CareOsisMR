package com.example.core.engine

import com.example.data.local.entity.IncentiveRecordEntity
import com.example.data.local.entity.IncentiveRuleEntity
import com.example.data.local.entity.UserAccountEntity
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * Data structures for configurable Incentive Rule Engine
 */
enum class RuleType {
    SLAB_BASED,
    TARGET_ACHIEVEMENT_PERCENT,
    PERCENTAGE_OF_SALES,
    FIXED_AMOUNT,
    MULTI_COMPONENT
}

enum class TargetSource {
    TOTAL_SALES,
    PRODUCT_SALES,
    DOCTOR_CALLS,
    COLLECTION
}

enum class TargetPriority {
    EMPLOYEE_FIRST,
    RULE_DEFAULT,
    HYBRID
}

data class SlabConfig(
    val minPercent: Double,
    val maxPercent: Double,
    val ratePercent: Double = 0.0,
    val fixedAmount: Double = 0.0,
    val label: String = ""
)

data class ComponentConfig(
    val name: String,
    val type: String = "KPI",
    val weightPercent: Double = 0.0,
    val minThresholdPercent: Double = 0.0,
    val rewardType: String = "FIXED_AMOUNT",
    val rewardValue: Double = 0.0
)

data class IncentiveSlab(
    val id: String = UUID.randomUUID().toString().take(8),
    val minThresholdPercent: Double,
    val maxThresholdPercent: Double,
    val incentivePercent: Double = 0.0, // For rate-based slabs (% of sales)
    val fixedRewardAmount: Double = 0.0, // For fixed slab amounts (₹)
    val label: String = ""
)

data class MultiComponentConfig(
    val salesThresholdPercent: Double = 100.0,
    val salesIncentivePercent: Double = 5.0,
    val doctorCoverageThresholdPercent: Double = 80.0,
    val doctorCoverageReward: Double = 1000.0,
    val newDoctorCountThreshold: Int = 5,
    val newDoctorReward: Double = 500.0,
    val collectionThresholdPercent: Double = 90.0,
    val collectionReward: Double = 1000.0
)

data class CalculationInput(
    val employee: UserAccountEntity,
    val period: String, // e.g. "August 2026"
    val actualSales: Double,
    val doctorVisitsDone: Int = 12,
    val doctorVisitsTarget: Int = 15,
    val newDoctorsActivated: Int = 6,
    val collectionAmount: Double = 180000.0,
    val collectionTarget: Double = 200000.0,
    val isMonthClosed: Boolean = false,
    val customTarget: Double? = null
)

data class BreakdownComponentItem(
    val title: String,
    val description: String,
    val amount: Double,
    val rateOrUnit: String = ""
)

data class IncentiveResult(
    val employeeId: String,
    val employeeName: String,
    val period: String,
    val target: Double,
    val actualSales: Double,
    val achievementPercent: Double,
    val applicableRuleId: String,
    val applicableRuleName: String,
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
    val status: String, // "ESTIMATED", "PENDING_APPROVAL", "FINAL"
    val breakdownItems: List<BreakdownComponentItem>,
    val calculationTimestamp: Long = System.currentTimeMillis()
)

/**
 * CareOsis Custom Incentive Rule Engine
 * Centralized, non-hardcoded business logic service for all incentive calculations
 */
object IncentiveCalculationEngine {

    /**
     * Parse slabs from JSON string into SlabConfig
     */
    fun parseSlabs(slabsJson: String): List<SlabConfig> {
        if (slabsJson.isNotBlank()) {
            try {
                val array = JSONArray(slabsJson)
                val list = mutableListOf<SlabConfig>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        SlabConfig(
                            minPercent = obj.optDouble("min", 0.0),
                            maxPercent = obj.optDouble("max", 100.0),
                            ratePercent = obj.optDouble("rate", 0.0),
                            fixedAmount = obj.optDouble("fixed", 0.0),
                            label = obj.optString("label", "")
                        )
                    )
                }
                if (list.isNotEmpty()) return list.sortedBy { it.minPercent }
            } catch (_: Exception) {
                // Fallback below
            }
        }
        return listOf(
            SlabConfig(70.0, 79.99, 2.0, 0.0, "70% - 79.99%"),
            SlabConfig(80.0, 89.99, 3.0, 0.0, "80% - 89.99%"),
            SlabConfig(90.0, 99.99, 4.0, 0.0, "90% - 99.99%"),
            SlabConfig(100.0, 1000.0, 5.0, 2500.0, "100%+")
        )
    }

    /**
     * Serialize SlabConfigs to JSON string
     */
    fun serializeSlabConfigs(slabs: List<SlabConfig>): String {
        val array = JSONArray()
        for (slab in slabs) {
            val obj = JSONObject()
            obj.put("min", slab.minPercent)
            obj.put("max", slab.maxPercent)
            obj.put("rate", slab.ratePercent)
            obj.put("fixed", slab.fixedAmount)
            obj.put("label", slab.label)
            array.put(obj)
        }
        return array.toString()
    }

    /**
     * Serialize ComponentConfigs to JSON string
     */
    fun serializeComponents(components: List<ComponentConfig>): String {
        val array = JSONArray()
        for (comp in components) {
            val obj = JSONObject()
            obj.put("name", comp.name)
            obj.put("type", comp.type)
            obj.put("weightPercent", comp.weightPercent)
            obj.put("minThresholdPercent", comp.minThresholdPercent)
            obj.put("rewardType", comp.rewardType)
            obj.put("rewardValue", comp.rewardValue)
            array.put(obj)
        }
        return array.toString()
    }

    /**
     * Parse slabs from JSON or construct defaults from rule entity fields
     */
    fun parseSlabs(rule: IncentiveRuleEntity): List<IncentiveSlab> {
        if (rule.slabsJson.isNotBlank()) {
            try {
                val array = JSONArray(rule.slabsJson)
                val list = mutableListOf<IncentiveSlab>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        IncentiveSlab(
                            id = obj.optString("id", UUID.randomUUID().toString().take(8)),
                            minThresholdPercent = obj.optDouble("min", 0.0),
                            maxThresholdPercent = obj.optDouble("max", 100.0),
                            incentivePercent = obj.optDouble("rate", 0.0),
                            fixedRewardAmount = obj.optDouble("fixed", 0.0),
                            label = obj.optString("label", "")
                        )
                    )
                }
                if (list.isNotEmpty()) return list.sortedBy { it.minThresholdPercent }
            } catch (_: Exception) {
                // Fallback below
            }
        }

        // Default standard slabs if no JSON provided
        return getDefaultStandardSlabs(rule.ruleType)
    }

    /**
     * Serialize slabs into JSON string
     */
    fun serializeSlabs(slabs: List<IncentiveSlab>): String {
        val array = JSONArray()
        for (slab in slabs) {
            val obj = JSONObject()
            obj.put("id", slab.id)
            obj.put("min", slab.minThresholdPercent)
            obj.put("max", slab.maxThresholdPercent)
            obj.put("rate", slab.incentivePercent)
            obj.put("fixed", slab.fixedRewardAmount)
            obj.put("label", slab.label)
            array.put(obj)
        }
        return array.toString()
    }

    /**
     * Parse multi-component configuration from JSON
     */
    fun parseMultiComponentConfig(rule: IncentiveRuleEntity): MultiComponentConfig {
        if (rule.componentsJson.isNotBlank()) {
            try {
                val obj = JSONObject(rule.componentsJson)
                return MultiComponentConfig(
                    salesThresholdPercent = obj.optDouble("salesThresholdPercent", 100.0),
                    salesIncentivePercent = obj.optDouble("salesIncentivePercent", 5.0),
                    doctorCoverageThresholdPercent = obj.optDouble("doctorCoverageThresholdPercent", 80.0),
                    doctorCoverageReward = obj.optDouble("doctorCoverageReward", 1000.0),
                    newDoctorCountThreshold = obj.optInt("newDoctorCountThreshold", 5),
                    newDoctorReward = obj.optDouble("newDoctorReward", 500.0),
                    collectionThresholdPercent = obj.optDouble("collectionThresholdPercent", 90.0),
                    collectionReward = obj.optDouble("collectionReward", 1000.0)
                )
            } catch (_: Exception) {
                // Fallback below
            }
        }
        return MultiComponentConfig()
    }

    /**
     * Serialize multi-component configuration to JSON
     */
    fun serializeMultiComponentConfig(config: MultiComponentConfig): String {
        val obj = JSONObject()
        obj.put("salesThresholdPercent", config.salesThresholdPercent)
        obj.put("salesIncentivePercent", config.salesIncentivePercent)
        obj.put("doctorCoverageThresholdPercent", config.doctorCoverageThresholdPercent)
        obj.put("doctorCoverageReward", config.doctorCoverageReward)
        obj.put("newDoctorCountThreshold", config.newDoctorCountThreshold)
        obj.put("newDoctorReward", config.newDoctorReward)
        obj.put("collectionThresholdPercent", config.collectionThresholdPercent)
        obj.put("collectionReward", config.collectionReward)
        return obj.toString()
    }

    /**
     * Default standard slabs for quick rule setup
     */
    fun getDefaultStandardSlabs(ruleType: String): List<IncentiveSlab> {
        return when (ruleType) {
            "SLAB_BASED" -> listOf(
                IncentiveSlab(minThresholdPercent = 0.0, maxThresholdPercent = 50.0, fixedRewardAmount = 0.0, label = "Below 50%"),
                IncentiveSlab(minThresholdPercent = 50.0, maxThresholdPercent = 70.0, fixedRewardAmount = 1000.0, label = "50% - 69.99%"),
                IncentiveSlab(minThresholdPercent = 70.0, maxThresholdPercent = 80.0, fixedRewardAmount = 2500.0, label = "70% - 79.99%"),
                IncentiveSlab(minThresholdPercent = 80.0, maxThresholdPercent = 90.0, fixedRewardAmount = 4000.0, label = "80% - 89.99%"),
                IncentiveSlab(minThresholdPercent = 90.0, maxThresholdPercent = 100.0, fixedRewardAmount = 6000.0, label = "90% - 99.99%"),
                IncentiveSlab(minThresholdPercent = 100.0, maxThresholdPercent = 110.0, fixedRewardAmount = 8000.0, label = "100% - 109.99%"),
                IncentiveSlab(minThresholdPercent = 110.0, maxThresholdPercent = 500.0, fixedRewardAmount = 12000.0, label = "110%+")
            )
            "FIXED_AMOUNT" -> listOf(
                IncentiveSlab(minThresholdPercent = 0.0, maxThresholdPercent = 100.0, fixedRewardAmount = 0.0, label = "Below 100%"),
                IncentiveSlab(minThresholdPercent = 100.0, maxThresholdPercent = 110.0, fixedRewardAmount = 5000.0, label = "100% Milestone"),
                IncentiveSlab(minThresholdPercent = 110.0, maxThresholdPercent = 120.0, fixedRewardAmount = 7500.0, label = "110% Milestone"),
                IncentiveSlab(minThresholdPercent = 120.0, maxThresholdPercent = 500.0, fixedRewardAmount = 10000.0, label = "120%+ Milestone")
            )
            else -> listOf( // PERCENTAGE_OF_SALES, TARGET_ACHIEVEMENT_PERCENT, MULTI_COMPONENT
                IncentiveSlab(minThresholdPercent = 0.0, maxThresholdPercent = 50.0, incentivePercent = 0.0, label = "Below 50%"),
                IncentiveSlab(minThresholdPercent = 50.0, maxThresholdPercent = 70.0, incentivePercent = 1.0, label = "50% - 69.99%"),
                IncentiveSlab(minThresholdPercent = 70.0, maxThresholdPercent = 90.0, incentivePercent = 2.0, label = "70% - 89.99%"),
                IncentiveSlab(minThresholdPercent = 90.0, maxThresholdPercent = 100.0, incentivePercent = 3.0, label = "90% - 99.99%"),
                IncentiveSlab(minThresholdPercent = 100.0, maxThresholdPercent = 500.0, incentivePercent = 5.0, label = "100%+")
            )
        }
    }

    /**
     * Resolves the highest-priority applicable rule for an employee:
     * 1. Employee-specific rule
     * 2. Region-specific rule
     * 3. Role/Category-specific rule
     * 4. Global company default rule
     */
    fun resolveApplicableRule(
        employee: UserAccountEntity,
        rules: List<IncentiveRuleEntity>
    ): IncentiveRuleEntity? {
        val activeRules = rules.filter { it.status == "ACTIVE" }
        if (activeRules.isEmpty()) return null

        // 1. Employee-specific rule
        val empRule = activeRules.firstOrNull { rule ->
            rule.assignedEmployeeIds != "ALL" &&
                    rule.assignedEmployeeIds.split(",").map { it.trim() }.contains(employee.id)
        }
        if (empRule != null) return empRule

        // 2. Region-specific rule
        val empRegions = employee.assignedRegionIds.split(",").map { it.trim() }
        val regRule = activeRules.firstOrNull { rule ->
            rule.regionId != "GLOBAL" && empRegions.contains(rule.regionId)
        }
        if (regRule != null) return regRule

        // 3. Category/Role-specific rule
        val catRule = activeRules.firstOrNull { rule ->
            rule.employeeCategory != "ALL" &&
                    (rule.employeeCategory.equals(employee.designation, ignoreCase = true) ||
                            rule.employeeCategory.equals(employee.role, ignoreCase = true))
        }
        if (catRule != null) return catRule

        // 4. Default global rule with lowest priority rank (highest priority value)
        return activeRules.filter { it.regionId == "GLOBAL" }
            .minByOrNull { it.priority }
            ?: activeRules.firstOrNull()
    }

    /**
     * Main Calculation Engine:
     * Evaluates rules against employee data and returns complete IncentiveResult with transparent breakdown
     */
    fun calculateIncentive(
        input: CalculationInput,
        rule: IncentiveRuleEntity
    ): IncentiveResult {
        // 1. Target Determination (Priority: Employee Specific > Custom > Rule Default)
        val target = when {
            input.customTarget != null && input.customTarget > 0 -> input.customTarget
            input.employee.monthlyTarget > 0 -> input.employee.monthlyTarget
            rule.defaultTarget > 0 -> rule.defaultTarget
            else -> 200000.0
        }

        // 2. Achievement %
        val actualSales = input.actualSales
        val achievementPercent = if (target > 0) (actualSales / target) * 100.0 else 0.0

        val slabs = parseSlabs(rule)
        val breakdownItems = mutableListOf<BreakdownComponentItem>()

        var applicableSlabLabel = "Standard Tier"
        var incentiveRate = 0.0
        var baseIncentive = 0.0
        var coverageIncentive = 0.0
        var newDoctorIncentive = 0.0
        var collectionIncentive = 0.0
        val deductions = 0.0

        when (rule.ruleType) {
            "SLAB_BASED" -> {
                // Find matching slab
                val matchedSlab = slabs.firstOrNull { slab ->
                    achievementPercent >= slab.minThresholdPercent && achievementPercent < slab.maxThresholdPercent
                } ?: if (achievementPercent >= (slabs.lastOrNull()?.minThresholdPercent ?: 0.0)) slabs.lastOrNull() else slabs.firstOrNull()

                if (matchedSlab != null) {
                    applicableSlabLabel = if (matchedSlab.label.isNotBlank()) matchedSlab.label
                    else "${matchedSlab.minThresholdPercent.toInt()}% - ${if (matchedSlab.maxThresholdPercent > 200) "100%+" else "${matchedSlab.maxThresholdPercent.toInt()}%"}"

                    baseIncentive = if (matchedSlab.fixedRewardAmount > 0) {
                        matchedSlab.fixedRewardAmount
                    } else {
                        actualSales * (matchedSlab.incentivePercent / 100.0)
                    }
                    incentiveRate = matchedSlab.incentivePercent

                    breakdownItems.add(
                        BreakdownComponentItem(
                            title = "Slab Tier Incentive",
                            description = "Achievement at ${String.format("%.1f", achievementPercent)}% (${applicableSlabLabel})",
                            amount = baseIncentive,
                            rateOrUnit = if (matchedSlab.fixedRewardAmount > 0) "Fixed Slab Reward" else "${matchedSlab.incentivePercent}% of Sales"
                        )
                    )
                }
            }

            "PERCENTAGE_OF_SALES" -> {
                val matchedSlab = slabs.firstOrNull { slab ->
                    achievementPercent >= slab.minThresholdPercent && achievementPercent < slab.maxThresholdPercent
                } ?: if (achievementPercent >= (slabs.lastOrNull()?.minThresholdPercent ?: 0.0)) slabs.lastOrNull() else slabs.firstOrNull()

                if (matchedSlab != null) {
                    applicableSlabLabel = if (matchedSlab.label.isNotBlank()) matchedSlab.label
                    else "${matchedSlab.minThresholdPercent.toInt()}% - ${if (matchedSlab.maxThresholdPercent > 200) "100%+" else "${matchedSlab.maxThresholdPercent.toInt()}%"}"

                    incentiveRate = matchedSlab.incentivePercent
                    baseIncentive = actualSales * (matchedSlab.incentivePercent / 100.0) + matchedSlab.fixedRewardAmount

                    breakdownItems.add(
                        BreakdownComponentItem(
                            title = "Sales Volume Incentive",
                            description = "₹${String.format("%,.0f", actualSales)} × ${matchedSlab.incentivePercent}% (${applicableSlabLabel})",
                            amount = actualSales * (matchedSlab.incentivePercent / 100.0),
                            rateOrUnit = "${matchedSlab.incentivePercent}%"
                        )
                    )

                    if (matchedSlab.fixedRewardAmount > 0) {
                        breakdownItems.add(
                            BreakdownComponentItem(
                                title = "Tier Achievement Bonus",
                                description = "Milestone bonus for reaching ${applicableSlabLabel}",
                                amount = matchedSlab.fixedRewardAmount,
                                rateOrUnit = "Fixed Bonus"
                            )
                        )
                    }
                }
            }

            "TARGET_ACHIEVEMENT_PERCENT" -> {
                val matchedSlab = slabs.firstOrNull { slab ->
                    achievementPercent >= slab.minThresholdPercent && achievementPercent < slab.maxThresholdPercent
                } ?: if (achievementPercent >= (slabs.lastOrNull()?.minThresholdPercent ?: 0.0)) slabs.lastOrNull() else slabs.firstOrNull()

                val rate = matchedSlab?.incentivePercent ?: (if (achievementPercent >= 100) 5.0 else if (achievementPercent >= 80) 3.0 else 1.0)
                incentiveRate = rate
                applicableSlabLabel = "${achievementPercent.toInt()}% Target Rate"
                baseIncentive = actualSales * (rate / 100.0)

                breakdownItems.add(
                    BreakdownComponentItem(
                        title = "Target Achievement Payout",
                        description = "${String.format("%.1f", achievementPercent)}% Target Achieved → ${rate}% Rate on Sales",
                        amount = baseIncentive,
                        rateOrUnit = "${rate}%"
                    )
                )
            }

            "FIXED_AMOUNT" -> {
                val matchedSlab = slabs.firstOrNull { slab ->
                    achievementPercent >= slab.minThresholdPercent && achievementPercent < slab.maxThresholdPercent
                } ?: if (achievementPercent >= (slabs.lastOrNull()?.minThresholdPercent ?: 0.0)) slabs.lastOrNull() else slabs.firstOrNull()

                if (matchedSlab != null) {
                    applicableSlabLabel = matchedSlab.label.ifBlank { "${matchedSlab.minThresholdPercent.toInt()}% Milestone" }
                    baseIncentive = matchedSlab.fixedRewardAmount

                    breakdownItems.add(
                        BreakdownComponentItem(
                            title = "Fixed Milestone Incentive",
                            description = "Milestone achieved: ${applicableSlabLabel}",
                            amount = baseIncentive,
                            rateOrUnit = "Fixed"
                        )
                    )
                }
            }

            "MULTI_COMPONENT" -> {
                val multiConfig = parseMultiComponentConfig(rule)

                // Component 1: Sales Achievement
                applicableSlabLabel = "Multi-Component Matrix"
                if (achievementPercent >= multiConfig.salesThresholdPercent) {
                    val salesPart = actualSales * (multiConfig.salesIncentivePercent / 100.0)
                    baseIncentive += salesPart
                    incentiveRate = multiConfig.salesIncentivePercent
                    breakdownItems.add(
                        BreakdownComponentItem(
                            title = "Component 1: Sales Target",
                            description = "${String.format("%.1f", achievementPercent)}% Achieved (Threshold: ${multiConfig.salesThresholdPercent.toInt()}%)",
                            amount = salesPart,
                            rateOrUnit = "${multiConfig.salesIncentivePercent}%"
                        )
                    )
                } else {
                    breakdownItems.add(
                        BreakdownComponentItem(
                            title = "Component 1: Sales Target",
                            description = "${String.format("%.1f", achievementPercent)}% Achieved (Threshold: ${multiConfig.salesThresholdPercent.toInt()}% not met)",
                            amount = 0.0,
                            rateOrUnit = "0%"
                        )
                    )
                }

                // Component 2: Doctor Coverage
                val coveragePercent = if (input.doctorVisitsTarget > 0) (input.doctorVisitsDone.toDouble() / input.doctorVisitsTarget.toDouble()) * 100.0 else 0.0
                if (coveragePercent >= multiConfig.doctorCoverageThresholdPercent) {
                    coverageIncentive = multiConfig.doctorCoverageReward
                    breakdownItems.add(
                        BreakdownComponentItem(
                            title = "Component 2: Doctor Coverage",
                            description = "${String.format("%.0f", coveragePercent)}% HCP Visits Done (Target: ${multiConfig.doctorCoverageThresholdPercent.toInt()}%)",
                            amount = coverageIncentive,
                            rateOrUnit = "₹${multiConfig.doctorCoverageReward.toInt()}"
                        )
                    )
                } else {
                    breakdownItems.add(
                        BreakdownComponentItem(
                            title = "Component 2: Doctor Coverage",
                            description = "${String.format("%.0f", coveragePercent)}% HCP Visits Done (Below ${multiConfig.doctorCoverageThresholdPercent.toInt()}%)",
                            amount = 0.0,
                            rateOrUnit = "₹0"
                        )
                    )
                }

                // Component 3: New Doctor Activation
                if (input.newDoctorsActivated >= multiConfig.newDoctorCountThreshold) {
                    newDoctorIncentive = multiConfig.newDoctorReward
                    breakdownItems.add(
                        BreakdownComponentItem(
                            title = "Component 3: New Doctor Activation",
                            description = "${input.newDoctorsActivated} New Prescribers Onboarded (Min: ${multiConfig.newDoctorCountThreshold})",
                            amount = newDoctorIncentive,
                            rateOrUnit = "₹${multiConfig.newDoctorReward.toInt()}"
                        )
                    )
                }

                // Component 4: Collection
                val collectionPercent = if (input.collectionTarget > 0) (input.collectionAmount / input.collectionTarget) * 100.0 else 0.0
                if (collectionPercent >= multiConfig.collectionThresholdPercent) {
                    collectionIncentive = multiConfig.collectionReward
                    breakdownItems.add(
                        BreakdownComponentItem(
                            title = "Component 4: Commercial Collection",
                            description = "${String.format("%.0f", collectionPercent)}% Payment Recovered (Min: ${multiConfig.collectionThresholdPercent.toInt()}%)",
                            amount = collectionIncentive,
                            rateOrUnit = "₹${multiConfig.collectionReward.toInt()}"
                        )
                    )
                }
            }
        }

        val additionalIncentives = coverageIncentive + newDoctorIncentive + collectionIncentive
        val finalIncentive = (baseIncentive + additionalIncentives - deductions).coerceAtLeast(0.0)

        val status = if (input.isMonthClosed) "FINAL" else "ESTIMATED"

        return IncentiveResult(
            employeeId = input.employee.id,
            employeeName = input.employee.name,
            period = input.period,
            target = target,
            actualSales = actualSales,
            achievementPercent = achievementPercent,
            applicableRuleId = rule.id,
            applicableRuleName = rule.ruleName,
            ruleVersion = rule.versionNumber,
            ruleType = rule.ruleType,
            applicableSlab = applicableSlabLabel,
            incentiveRate = incentiveRate,
            baseIncentive = baseIncentive,
            coverageIncentive = coverageIncentive,
            newDoctorIncentive = newDoctorIncentive,
            collectionIncentive = collectionIncentive,
            additionalIncentives = additionalIncentives,
            deductions = deductions,
            finalIncentive = finalIncentive,
            status = status,
            breakdownItems = breakdownItems
        )
    }

    /**
     * Converts IncentiveResult to database entity
     */
    fun toRecordEntity(result: IncentiveResult): IncentiveRecordEntity {
        val breakdownJson = JSONArray().apply {
            result.breakdownItems.forEach { item ->
                put(JSONObject().apply {
                    put("title", item.title)
                    put("description", item.description)
                    put("amount", item.amount)
                    put("rateOrUnit", item.rateOrUnit)
                })
            }
        }.toString()

        return IncentiveRecordEntity(
            id = "INC-${result.employeeId}-${result.period.replace(" ", "-").uppercase()}",
            employeeId = result.employeeId,
            employeeName = result.employeeName,
            period = result.period,
            target = result.target,
            actualSales = result.actualSales,
            achievementPercent = result.achievementPercent,
            ruleId = result.applicableRuleId,
            ruleName = result.applicableRuleName,
            ruleVersion = result.ruleVersion,
            ruleType = result.ruleType,
            applicableSlab = result.applicableSlab,
            incentiveRate = result.incentiveRate,
            baseIncentive = result.baseIncentive,
            coverageIncentive = result.coverageIncentive,
            newDoctorIncentive = result.newDoctorIncentive,
            collectionIncentive = result.collectionIncentive,
            additionalIncentives = result.additionalIncentives,
            deductions = result.deductions,
            finalIncentive = result.finalIncentive,
            status = result.status,
            breakdownJson = breakdownJson,
            calculatedAt = result.calculationTimestamp
        )
    }

    /**
     * Validates incentive rule configuration before saving/activating:
     * - Slabs do not overlap
     * - Min <= Max
     * - Non-negative values
     * - Duplicate slabs rejected
     * - At least one slab exists
     * - Effective dates valid
     */
    fun validateRule(
        ruleName: String,
        ruleType: String,
        slabs: List<IncentiveSlab>,
        effectiveFrom: String,
        effectiveTo: String
    ): List<String> {
        val errors = mutableListOf<String>()

        if (ruleName.isBlank()) {
            errors.add("Rule Name cannot be empty.")
        }

        if (effectiveFrom.isBlank() || effectiveTo.isBlank()) {
            errors.add("Rule must have valid Effective From and Effective To dates.")
        }

        if (ruleType != "MULTI_COMPONENT") {
            if (slabs.isEmpty()) {
                errors.add("Rule must have at least one incentive slab.")
            } else {
                // Check individual slab validity
                for ((index, slab) in slabs.withIndex()) {
                    if (slab.minThresholdPercent < 0) {
                        errors.add("Slab #${index + 1}: Minimum % cannot be negative.")
                    }
                    if (slab.maxThresholdPercent <= slab.minThresholdPercent) {
                        errors.add("Slab #${index + 1}: Max % (${slab.maxThresholdPercent}%) must be greater than Min % (${slab.minThresholdPercent}%).")
                    }
                    if (slab.incentivePercent < 0) {
                        errors.add("Slab #${index + 1}: Incentive Rate % cannot be negative.")
                    }
                    if (slab.fixedRewardAmount < 0) {
                        errors.add("Slab #${index + 1}: Fixed Reward cannot be negative.")
                    }
                }

                // Check for overlapping slabs
                val sorted = slabs.sortedBy { it.minThresholdPercent }
                for (i in 0 until sorted.size - 1) {
                    val current = sorted[i]
                    val next = sorted[i + 1]
                    if (current.maxThresholdPercent > next.minThresholdPercent) {
                        errors.add("Slab overlap detected: ${current.minThresholdPercent}%–${current.maxThresholdPercent}% and ${next.minThresholdPercent}%–${next.maxThresholdPercent}%.")
                    }
                }
            }
        }

        return errors
    }
}
