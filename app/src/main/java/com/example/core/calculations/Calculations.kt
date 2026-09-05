package com.example.core.calculations

/**
 * Pure calculation engines for CareOsis MR App.
 * Isolated from UI to allow reuse across Android, Web, and Backend services.
 */

data class OrderCalculationResult(
    val subtotal: Double,
    val discountAmount: Double,
    val taxableAmount: Double,
    val gstAmount: Double,
    val totalAmount: Double,
    val totalItemsCount: Int,
    val retailerMarginPercent: Double,
    val estimatedDoctorCommission: Double
)

object OrderCalculator {
    const val DEFAULT_GST_RATE = 0.12 // 12% pharmaceutical GST

    fun calculateItemTotal(quantity: Int, ratePerUnit: Double, discountPercent: Double = 0.0): Double {
        val gross = quantity * ratePerUnit
        val discount = gross * (discountPercent.coerceIn(0.0, 100.0) / 100.0)
        return (gross - discount).coerceAtLeast(0.0)
    }

    fun calculateOrder(
        items: List<Triple<Int, Double, Double>>, // quantity, mrp, ratePerUnit
        overallDiscountPercent: Double = 0.0,
        gstRate: Double = DEFAULT_GST_RATE
    ): OrderCalculationResult {
        var subtotal = 0.0
        var totalMrp = 0.0
        var totalQuantity = 0

        for ((qty, mrp, rate) in items) {
            subtotal += qty * rate
            totalMrp += qty * mrp
            totalQuantity += qty
        }

        val discountAmount = subtotal * (overallDiscountPercent.coerceIn(0.0, 100.0) / 100.0)
        val taxableAmount = subtotal - discountAmount
        val gstAmount = taxableAmount * gstRate
        val totalAmount = taxableAmount + gstAmount

        // Retailer Margin is difference between MRP and Unit Rate relative to MRP
        val retailerMarginPercent = if (totalMrp > 0) {
            ((totalMrp - subtotal) / totalMrp) * 100.0
        } else {
            0.0
        }

        // Standard 10% prescription commission estimation on net sales
        val estimatedDoctorCommission = taxableAmount * 0.10

        return OrderCalculationResult(
            subtotal = subtotal,
            discountAmount = discountAmount,
            taxableAmount = taxableAmount,
            gstAmount = gstAmount,
            totalAmount = totalAmount,
            totalItemsCount = totalQuantity,
            retailerMarginPercent = retailerMarginPercent,
            estimatedDoctorCommission = estimatedDoctorCommission
        )
    }
}

data class IncentiveBreakdown(
    val monthlySales: Double,
    val monthlyTarget: Double,
    val achievementPercent: Double,
    val baseIncentivePercent: Double,
    val baseIncentiveAmount: Double,
    val focusProductBonus: Double,
    val doctorCoverageBonus: Double,
    val collectionIncentive: Double,
    val superAchieverBonus: Double,
    val totalEstimatedIncentive: Double,
    val payoutStatus: String
)

object IncentiveCalculator {
    fun calculateIncentive(
        monthlySales: Double,
        monthlyTarget: Double,
        focusProductSales: Double = 0.0,
        doctorCoveragePercent: Double = 80.0,
        collectionPercent: Double = 85.0
    ): IncentiveBreakdown {
        val achievementPercent = if (monthlyTarget > 0) {
            (monthlySales / monthlyTarget) * 100.0
        } else {
            0.0
        }

        // Tiered Base Incentive
        val baseIncentivePercent = when {
            achievementPercent < 50.0 -> 0.0
            achievementPercent < 80.0 -> 2.0
            achievementPercent < 100.0 -> 3.5
            achievementPercent < 120.0 -> 5.0
            else -> 6.5
        }

        val baseIncentiveAmount = monthlySales * (baseIncentivePercent / 100.0)

        // Focus Product Bonus: 4% extra on focus products (Booster, Metabo 3X, Calci Fizz)
        val focusProductBonus = focusProductSales * 0.04

        // Doctor Coverage Milestone: Target > 85% coverage grants ₹2,500 bonus
        val doctorCoverageBonus = if (doctorCoveragePercent >= 85.0) 2500.0 else if (doctorCoveragePercent >= 70.0) 1000.0 else 0.0

        // Collection Incentive: >90% on-time collection gives ₹1,500 bonus
        val collectionIncentive = if (collectionPercent >= 90.0) 1500.0 else if (collectionPercent >= 80.0) 750.0 else 0.0

        // Super Achiever Booster (> 100% target gives flat ₹5,000 extra)
        val superAchieverBonus = if (achievementPercent >= 100.0) 5000.0 else 0.0

        val totalEstimatedIncentive = baseIncentiveAmount + focusProductBonus + doctorCoverageBonus + collectionIncentive + superAchieverBonus

        val payoutStatus = when {
            achievementPercent >= 100.0 -> "Eligible for Diamond Tier Payout"
            achievementPercent >= 80.0 -> "Eligible for Gold Tier Payout"
            achievementPercent >= 50.0 -> "Eligible for Standard Payout"
            else -> "Target Milestone Pending"
        }

        return IncentiveBreakdown(
            monthlySales = monthlySales,
            monthlyTarget = monthlyTarget,
            achievementPercent = achievementPercent,
            baseIncentivePercent = baseIncentivePercent,
            baseIncentiveAmount = baseIncentiveAmount,
            focusProductBonus = focusProductBonus,
            doctorCoverageBonus = doctorCoverageBonus,
            collectionIncentive = collectionIncentive,
            superAchieverBonus = superAchieverBonus,
            totalEstimatedIncentive = totalEstimatedIncentive,
            payoutStatus = payoutStatus
        )
    }
}

object TrainingProgressCalculator {
    fun calculateMrLevel(overallTrainingPercent: Int): Pair<String, String> {
        return when {
            overallTrainingPercent < 10 -> "Newbie" to "Getting Started"
            overallTrainingPercent < 25 -> "Beginner" to "Foundation Phase"
            overallTrainingPercent < 50 -> "Intermediate" to "Field Ready"
            overallTrainingPercent < 90 -> "Expert MR" to "High Performer"
            overallTrainingPercent < 100 -> "Advanced MR" to "Elite Representative"
            else -> "CareOsis Master MR" to "Master Field Champion"
        }
    }

    fun calculateProductProgress(dossierRead: Boolean, videoWatched: Boolean, quizScore: Int): Int {
        var score = 0
        if (dossierRead) score += 40
        if (videoWatched) score += 20
        score += (quizScore.coerceIn(0, 100) * 0.40).toInt()
        return score.coerceIn(0, 100)
    }
}

object ExpenseCalculator {
    fun calculateDailyTotal(expenses: List<Double>): Double = expenses.sum()
    fun calculateCategoryTotals(expenses: List<Pair<String, Double>>): Map<String, Double> {
        return expenses.groupBy({ it.first }, { it.second }).mapValues { it.value.sum() }
    }
}
