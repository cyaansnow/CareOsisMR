package com.example.ui.commercial.expense

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ExpenseEntity
import com.example.ui.theme.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Data representation for category breakdown visualization
 */
data class CategorySpendingData(
    val category: String,
    val totalAmount: Double,
    val count: Int,
    val percentage: Float,
    val color: Color
)

/**
 * Color mapping for expense categories adhering to CareOsis Clinical palette
 */
fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "fuel", "fuel / mileage" -> ChartCategoryFuel
        "travel", "public transport" -> ChartCategoryTravel
        "food", "meals & daily allowance" -> ChartCategoryFood
        "hotel", "lodging / stay" -> ChartCategoryHotel
        "parking", "parking & toll" -> ChartCategoryParking
        "doctor engagement", "doctor refreshments" -> ChartCategoryDoctorEngagement
        else -> ChartCategoryOther
    }
}

/**
 * Aggregate raw expenses into category breakdown data
 */
fun calculateCategorySpending(expenses: List<ExpenseEntity>): List<CategorySpendingData> {
    if (expenses.isEmpty()) return emptyList()
    val totalSum = expenses.sumOf { it.amount }.coerceAtLeast(1.0)
    val grouped = expenses.groupBy { it.category }

    return grouped.map { (cat, list) ->
        val catTotal = list.sumOf { it.amount }
        val pct = ((catTotal / totalSum) * 100).toFloat()
        CategorySpendingData(
            category = cat,
            totalAmount = catTotal,
            count = list.size,
            percentage = pct,
            color = getCategoryColor(cat)
        )
    }.sortedByDescending { it.totalAmount }
}

/**
 * Recharts-style Interactive Donut Chart for Expense Categories
 */
@Composable
fun RechartsCategoryDonutChart(
    categoryData: List<CategorySpendingData>,
    totalAmount: Double,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var animationProgress by remember { mutableStateOf(0f) }
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(categoryData) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("expense_recharts_donut_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Category Spending Distribution",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "Interactive breakdown of claimed amounts",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                if (selectedCategory != null) {
                    TextButton(
                        onClick = { onCategorySelected(null) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Reset View",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Donut Chart Canvas with Centered Overlay
            Box(
                modifier = Modifier
                    .size(230.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(categoryData) {
                            detectTapGestures { offset ->
                                val center = Offset(size.width / 2f, size.height / 2f)
                                val touchAngle = (Math.toDegrees(
                                    atan2(offset.y - center.y, offset.x - center.x).toDouble()
                                ) + 360) % 360

                                var currentAngle = 270.0 // Start at top
                                for (data in categoryData) {
                                    val sweep = (data.percentage / 100f) * 360f
                                    val startAngle = currentAngle % 360
                                    val endAngle = (currentAngle + sweep) % 360

                                    val isInside = if (startAngle < endAngle) {
                                        touchAngle in startAngle..endAngle
                                    } else {
                                        touchAngle >= startAngle || touchAngle <= endAngle
                                    }

                                    if (isInside) {
                                        if (selectedCategory == data.category) {
                                            onCategorySelected(null)
                                        } else {
                                            onCategorySelected(data.category)
                                        }
                                        break
                                    }
                                    currentAngle += sweep
                                }
                            }
                        }
                ) {
                    val strokeWidth = 32.dp.toPx()
                    val selectedStrokeWidth = 38.dp.toPx()
                    val chartSize = size.minDimension - selectedStrokeWidth
                    val topLeft = Offset(
                        (size.width - chartSize) / 2f,
                        (size.height - chartSize) / 2f
                    )

                    var startAngle = -90f // Start from 12 o'clock

                    if (categoryData.isEmpty()) {
                        drawArc(
                            color = Color(0xFFE0E5E3),
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = Size(chartSize, chartSize),
                            style = Stroke(width = strokeWidth)
                        )
                    } else {
                        categoryData.forEach { item ->
                            val sweepAngle = (item.percentage / 100f) * 360f * animatedProgress.value
                            val isSelected = selectedCategory == item.category
                            val currentStroke = if (isSelected) selectedStrokeWidth else strokeWidth

                            drawArc(
                                color = if (selectedCategory == null || isSelected) item.color else item.color.copy(alpha = 0.35f),
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                topLeft = topLeft,
                                size = Size(chartSize, chartSize),
                                style = Stroke(width = currentStroke, cap = StrokeCap.Round)
                            )
                            startAngle += sweepAngle
                        }
                    }
                }

                // Centered Information Badge
                val displayData = categoryData.find { it.category == selectedCategory }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    if (displayData != null) {
                        Text(
                            text = displayData.category,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = displayData.color,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "₹${displayData.totalAmount.toInt()}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "${"%.1f".format(displayData.percentage)}% of total",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    } else {
                        Text(
                            text = "Total Spent",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Text(
                            text = "₹${totalAmount.toInt()}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "${categoryData.size} Categories",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Interactive Category Legend & Metrics
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categoryData.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEach { item ->
                            val isSelected = selectedCategory == item.category
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        if (selectedCategory == item.category) {
                                            onCategorySelected(null)
                                        } else {
                                            onCategorySelected(item.category)
                                        }
                                    },
                                color = if (isSelected) item.color.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, item.color) else null,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(item.color)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = item.category,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Text(
                                        text = "₹${item.totalAmount.toInt()}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) item.color else MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Recharts-style Daily Spending Trend Bar Chart
 */
@Composable
fun RechartsDailyTrendBarChart(
    expenses: List<ExpenseEntity>,
    modifier: Modifier = Modifier
) {
    val dailyMap = expenses.groupBy { it.date }
        .mapValues { entry -> entry.value.sumOf { it.amount } }
        .toList()
        .takeLast(7)

    val maxAmount = dailyMap.maxOfOrNull { it.second }?.coerceAtLeast(500.0) ?: 1000.0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("expense_recharts_trend_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Daily Expense Velocity",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "Expenditure across recent field days",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "7 Days",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (dailyMap.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No recent daily expenditure data",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    dailyMap.forEach { (dateStr, amount) ->
                        val ratio = (amount / maxAmount).toFloat().coerceIn(0.08f, 1f)
                        val shortDate = dateStr.replace(" 2026", "").replace(" 2025", "")

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Text(
                                text = "₹${amount.toInt()}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            Box(
                                modifier = Modifier
                                    .width(26.dp)
                                    .fillMaxHeight(ratio * 0.75f)
                                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.primary,
                                                MaterialTheme.colorScheme.primaryContainer
                                            )
                                        )
                                    )
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = shortDate,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Budget & Policy Allowance Utilization Progress Card
 */
@Composable
fun ExpensePolicyUtilizationCard(
    totalSpent: Double,
    monthlyBudget: Double = 15000.0,
    modifier: Modifier = Modifier
) {
    val usedRatio = (totalSpent / monthlyBudget).toFloat().coerceIn(0f, 1f)
    val remainingBudget = (monthlyBudget - totalSpent).coerceAtLeast(0.0)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("expense_policy_utilization_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = "Allowance Budget",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Monthly Field Policy Cap",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "HQ Standard MR TA / DA Allowance",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (usedRatio < 0.85f) StatusSuccessContainer else StatusWarningContainer
                ) {
                    Text(
                        text = if (usedRatio < 0.85f) "Within Limit" else "High Spend",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (usedRatio < 0.85f) StatusSuccess else StatusWarning
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { usedRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Claimed / Logged",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Text(
                        text = "₹${totalSpent.toInt()} (${"%.1f".format(usedRatio * 100)}%)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Remaining Balance",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Text(
                        text = "₹${remainingBudget.toInt()} of ₹${monthlyBudget.toInt()}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }
    }
}
