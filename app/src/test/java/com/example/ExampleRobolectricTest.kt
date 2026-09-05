package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.calculations.IncentiveCalculator
import com.example.core.calculations.OrderCalculator
import com.example.core.calculations.TrainingProgressCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("CareOsis MR", appName)
    }

    @Test
    fun `test order calculation engine`() {
        val items = listOf(
            Triple(10, 450.0, 320.0), // 10 units of Booster
            Triple(5, 290.0, 210.0)   // 5 units of Calci Fizz
        )
        val result = OrderCalculator.calculateOrder(items, overallDiscountPercent = 5.0)

        // subtotal = 3200 + 1050 = 4250
        assertEquals(4250.0, result.subtotal, 0.01)
        // 5% discount = 212.5
        assertEquals(212.5, result.discountAmount, 0.01)
        // taxable = 4037.5
        assertEquals(4037.5, result.taxableAmount, 0.01)
        // 12% GST = 484.5
        assertEquals(484.5, result.gstAmount, 0.01)
        // total = 4522.0
        assertEquals(4522.0, result.totalAmount, 0.01)
    }

    @Test
    fun `test incentive calculator`() {
        val incentive = IncentiveCalculator.calculateIncentive(
            monthlySales = 220000.0,
            monthlyTarget = 200000.0,
            focusProductSales = 80000.0,
            doctorCoveragePercent = 90.0,
            collectionPercent = 92.0
        )
        // 110% achievement tier (5.0%)
        assertTrue(incentive.achievementPercent >= 100.0)
        assertTrue(incentive.totalEstimatedIncentive > 15000.0)
    }

    @Test
    fun `test training progress calculator`() {
        val score = TrainingProgressCalculator.calculateProductProgress(
            dossierRead = true,
            videoWatched = true,
            quizScore = 100
        )
        assertEquals(100, score)

        val (level, descriptor) = TrainingProgressCalculator.calculateMrLevel(85)
        assertEquals("Expert MR", level)
    }
}

