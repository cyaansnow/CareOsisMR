package com.example

import com.example.data.local.entity.IncentiveRuleEntity
import com.example.data.local.entity.UserAccountEntity
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testDynamicIncentiveCalculation_tierProgression() {
        val rules = listOf(
            IncentiveRuleEntity(
                id = "R1",
                ruleName = "Tier 1",
                ruleType = "SALES_TIER",
                minThresholdPercent = 0.0,
                maxThresholdPercent = 50.0,
                incentivePercent = 0.0,
                fixedRewardAmount = 0.0
            ),
            IncentiveRuleEntity(
                id = "R2",
                ruleName = "Tier 2",
                ruleType = "SALES_TIER",
                minThresholdPercent = 50.0,
                maxThresholdPercent = 70.0,
                incentivePercent = 1.0,
                fixedRewardAmount = 500.0
            ),
            IncentiveRuleEntity(
                id = "R3",
                ruleName = "Tier 3",
                ruleType = "SALES_TIER",
                minThresholdPercent = 70.0,
                maxThresholdPercent = 90.0,
                incentivePercent = 2.0,
                fixedRewardAmount = 1200.0
            ),
            IncentiveRuleEntity(
                id = "R4",
                ruleName = "Tier 4",
                ruleType = "SALES_TIER",
                minThresholdPercent = 90.0,
                maxThresholdPercent = 100.0,
                incentivePercent = 3.0,
                fixedRewardAmount = 2500.0
            ),
            IncentiveRuleEntity(
                id = "R5",
                ruleName = "Tier 5 Super Achiever",
                ruleType = "SALES_TIER",
                minThresholdPercent = 100.0,
                maxThresholdPercent = 500.0,
                incentivePercent = 5.0,
                fixedRewardAmount = 5000.0
            )
        )

        val target = 200000.0

        // Test 40% achievement -> 0%
        val sales40 = 80000.0
        val ach40 = (sales40 / target) * 100.0
        val rule40 = rules.first { ach40 >= it.minThresholdPercent && ach40 < it.maxThresholdPercent }
        val inc40 = (sales40 * (rule40.incentivePercent / 100.0)) + rule40.fixedRewardAmount
        assertEquals(0.0, inc40, 0.01)

        // Test 60% achievement -> 1% + 500 = 1200 + 500 = 1700
        val sales60 = 120000.0
        val ach60 = (sales60 / target) * 100.0
        val rule60 = rules.first { ach60 >= it.minThresholdPercent && ach60 < it.maxThresholdPercent }
        val inc60 = (sales60 * (rule60.incentivePercent / 100.0)) + rule60.fixedRewardAmount
        assertEquals(1700.0, inc60, 0.01)

        // Test 95% achievement -> 3% + 2500 = 5700 + 2500 = 8200
        val sales95 = 190000.0
        val ach95 = (sales95 / target) * 100.0
        val rule95 = rules.first { ach95 >= it.minThresholdPercent && ach95 < it.maxThresholdPercent }
        val inc95 = (sales95 * (rule95.incentivePercent / 100.0)) + rule95.fixedRewardAmount
        assertEquals(8200.0, inc95, 0.01)

        // Test 110% achievement -> 5% + 5000 = 11000 + 5000 = 16000
        val sales110 = 220000.0
        val ach110 = (sales110 / target) * 100.0
        val rule110 = rules.first { ach110 >= it.minThresholdPercent && ach110 < it.maxThresholdPercent }
        val inc110 = (sales110 * (rule110.incentivePercent / 100.0)) + rule110.fixedRewardAmount
        assertEquals(16000.0, inc110, 0.01)
    }

    @Test
    fun testRegionalScoping_isolation() {
        val adminNorth = UserAccountEntity(
            id = "CO-ADM-101",
            name = "Rajesh Verma",
            role = "ADMIN",
            password = "pwd",
            assignedRegionIds = "DELHI_NCR,NOIDA",
            employeeScopeMode = "ALL_IN_REGION"
        )

        val empDelhi = UserAccountEntity(
            id = "CO-MR-8492",
            name = "Aman",
            role = "EMPLOYEE",
            password = "pwd",
            assignedRegionIds = "DELHI_NCR"
        )

        val empGurgaon = UserAccountEntity(
            id = "CO-MR-8493",
            name = "Rohan",
            role = "EMPLOYEE",
            password = "pwd",
            assignedRegionIds = "GURGAON"
        )

        val adminRegions = adminNorth.assignedRegionIds.split(",")

        assertTrue(adminRegions.contains(empDelhi.assignedRegionIds))
        assertFalse(adminRegions.contains(empGurgaon.assignedRegionIds))
    }
}

