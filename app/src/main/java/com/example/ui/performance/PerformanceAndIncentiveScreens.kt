package com.example.ui.performance

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.CareOsisApp
import com.example.core.components.*
import com.example.ui.navigation.Destinations
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceScreen(
    onNavigate: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val profile by repository.getProfile().collectAsStateWithLifecycle(initialValue = null)

    val target = profile?.monthlyTarget ?: 200000.0
    val sales = profile?.monthlySales ?: 156800.0
    val achievementPercent = if (target > 0) ((sales / target) * 100).toInt() else 0

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "Field Performance",
                subtitle = "Monthly Analytics & KPIs",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ClinicalBackground)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Main Target Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldPrimary)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(text = "Sales Target vs Actual", style = MaterialTheme.typography.bodySmall.copy(color = GoldLight))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "₹${sales.toInt()} / ₹${target.toInt()}",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = ClinicalWhite
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        CareOsisProgressBar(
                            progressPercent = achievementPercent,
                            progressColor = GoldMetallic,
                            trackColor = ClinicalWhite.copy(alpha = 0.25f),
                            barHeight = 10.dp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$achievementPercent% Target Achieved • ₹${(target - sales).toInt()} Remaining",
                            style = MaterialTheme.typography.bodySmall.copy(color = ClinicalWhite.copy(alpha = 0.9f))
                        )
                    }
                }
            }

            // Key Metrics 4-Box Grid
            item {
                Text(text = "Key Operational KPIs", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CareOsisStatCard(
                        title = "Call Average",
                        value = "14.2 / day",
                        subtitle = "Target: 12 calls",
                        icon = Icons.Default.PhoneInTalk,
                        accentColor = EmeraldPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    CareOsisStatCard(
                        title = "Doctor Coverage",
                        value = "92%",
                        subtitle = "118 of 128 listed",
                        icon = Icons.Default.PeopleAlt,
                        accentColor = Color(0xFF2563EB),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CareOsisStatCard(
                        title = "POB Value",
                        value = "₹68,500",
                        subtitle = "Per Order Booking",
                        icon = Icons.Default.ShoppingCartCheckout,
                        accentColor = GoldDark,
                        modifier = Modifier.weight(1f)
                    )
                    CareOsisStatCard(
                        title = "Strike Rate",
                        value = "74%",
                        subtitle = "Order conversion",
                        icon = Icons.Default.TrendingUp,
                        accentColor = Color(0xFF7C3AED),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Product Wise Contribution
            item {
                Text(text = "Top Product Revenue Drivers", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }

            val topProducts = listOf(
                Pair("Booster", "₹48,200 (31%)"),
                Pair("Metabo 3X", "₹36,400 (23%)"),
                Pair("Calci Fizz", "₹24,800 (16%)"),
                Pair("Immun-X", "₹18,500 (12%)"),
                Pair("Other Formulations", "₹28,900 (18%)")
            )

            items(topProducts) { (prod, rev) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = prod, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        Text(text = rev, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = EmeraldPrimary))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncentiveScreen(
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val profile by repository.getProfile().collectAsStateWithLifecycle(initialValue = null)
    val activeRules by repository.getActiveIncentiveRules().collectAsStateWithLifecycle(initialValue = emptyList())
    val currentRecord by repository.getIncentiveRecord(profile?.empId ?: "CO-MR-8492", "August 2026").collectAsStateWithLifecycle(initialValue = null)

    var simulatedSales by remember { mutableFloatStateOf(164000f) }
    var target by remember { mutableFloatStateOf(200000f) }
    var newDoctorsCount by remember { mutableIntStateOf(6) }
    var doctorVisitsDone by remember { mutableIntStateOf(14) }

    val activeRule = remember(activeRules, profile) {
        val dummyUser = com.example.data.local.entity.UserAccountEntity(
            id = profile?.empId ?: "CO-MR-8492",
            name = profile?.name ?: "Aman Chhabra",
            email = "mr@careosis.com",
            phone = "+91 98765 43210",
            role = "EMPLOYEE",
            password = "",
            status = "ACTIVE",
            assignedRegionIds = profile?.territory ?: "DELHI_NCR",
            employeeScopeMode = "ALL",
            assignedEmployeeIds = "SELF",
            permissions = ""
        )
        com.example.core.engine.IncentiveCalculationEngine.resolveApplicableRule(dummyUser, activeRules)
            ?: activeRules.firstOrNull()
    }

    val simulatedCalculation = remember(simulatedSales, target, newDoctorsCount, doctorVisitsDone, activeRule, profile) {
        val dummyUser = com.example.data.local.entity.UserAccountEntity(
            id = profile?.empId ?: "CO-MR-8492",
            name = profile?.name ?: "Aman Chhabra",
            email = "mr@careosis.com",
            phone = "+91 98765 43210",
            role = "EMPLOYEE",
            password = "",
            status = "ACTIVE",
            assignedRegionIds = profile?.territory ?: "DELHI_NCR",
            employeeScopeMode = "ALL",
            assignedEmployeeIds = "SELF",
            permissions = "",
            monthlyTarget = target.toDouble()
        )
        val input = com.example.core.engine.CalculationInput(
            employee = dummyUser,
            period = "August 2026",
            actualSales = simulatedSales.toDouble(),
            doctorVisitsDone = doctorVisitsDone,
            doctorVisitsTarget = 15,
            newDoctorsActivated = newDoctorsCount,
            collectionAmount = simulatedSales.toDouble() * 0.95,
            collectionTarget = target.toDouble(),
            isMonthClosed = false
        )
        if (activeRule != null) {
            com.example.core.engine.IncentiveCalculationEngine.calculateIncentive(input, activeRule)
        } else {
            null
        }
    }

    val finalIncentive = simulatedCalculation?.finalIncentive ?: 8450.0
    val achievementPercent = if (target > 0) ((simulatedSales / target) * 100).toDouble() else 0.0

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "Incentive Calculator",
                subtitle = "Central Rules Engine Simulator",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ClinicalBackground)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Projected Payout Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldPrimary)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Projected Incentive (Live Engine)", style = MaterialTheme.typography.bodySmall.copy(color = GoldLight))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "₹${String.format("%,.0f", finalIncentive)}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldMetallic
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Achievement: ${String.format("%.1f", achievementPercent)}% • ${simulatedCalculation?.applicableSlab ?: "80%–89.99%"}",
                            style = MaterialTheme.typography.bodySmall.copy(color = ClinicalWhite.copy(alpha = 0.9f))
                        )
                    }
                }
            }

            // Interactive Simulator Sliders
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Simulate Monthly Performance",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Sales Slider
                        Text(text = "Monthly Sales: ₹${simulatedSales.toInt()}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                        Slider(
                            value = simulatedSales,
                            onValueChange = { simulatedSales = it },
                            valueRange = 50000f..300000f,
                            steps = 25,
                            colors = SliderDefaults.colors(thumbColor = EmeraldPrimary, activeTrackColor = EmeraldPrimary)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // New Doctors Added
                        Text(text = "New Prescribers Activated: $newDoctorsCount", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                        Slider(
                            value = newDoctorsCount.toFloat(),
                            onValueChange = { newDoctorsCount = it.toInt() },
                            valueRange = 0f..12f,
                            steps = 12,
                            colors = SliderDefaults.colors(thumbColor = GoldDark, activeTrackColor = GoldDark)
                        )
                    }
                }
            }

            // Active Policy Slabs Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Active Incentive Policy: ${activeRule?.ruleName ?: "Standard Tier Rule"}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (activeRule != null && activeRule.slabsJson.isNotBlank()) {
                            val parsedSlabs = com.example.core.engine.IncentiveCalculationEngine.parseSlabs(activeRule.slabsJson)
                            parsedSlabs.forEach { slab ->
                                SlabRow(
                                    slab = slab.label.ifBlank { "${slab.minPercent}% – ${if (slab.maxPercent > 200) "100%+" else "${slab.maxPercent}%"}" },
                                    payout = "${slab.ratePercent}% Rate" + if (slab.fixedAmount > 0) " + ₹${slab.fixedAmount.toInt()} Bonus" else ""
                                )
                            }
                        } else {
                            SlabRow("70% - 79.99% Target", "2% of sales")
                            SlabRow("80% - 89.99% Target", "3% of sales")
                            SlabRow("90% - 99.99% Target", "4% of sales")
                            SlabRow("100%+ Super Achievement", "5% of sales + ₹2,500 Bonus")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SlabRow(slab: String, payout: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = slab, style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
        Text(text = payout, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamificationScreen(
    onNavigate: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val achievements by repository.getAllAchievements().collectAsStateWithLifecycle(initialValue = emptyList())
    val profile by repository.getProfile().collectAsStateWithLifecycle(initialValue = null)

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "Badges & Milestones",
                subtitle = "${profile?.level ?: "Expert MR"} • CareOsis Elite",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ClinicalBackground)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(achievements, key = { it.id }) { ach ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = if (ach.isUnlocked) BorderStroke(1.dp, GoldMetallic.copy(alpha = 0.5f)) else null,
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (ach.isUnlocked) GoldContainer else MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (ach.isUnlocked) Icons.Default.WorkspacePremium else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (ach.isUnlocked) GoldDark else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = ach.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                CareOsisStatusChip(
                                    text = "${ach.progress}/${ach.maxProgress}",
                                    containerColor = if (ach.isUnlocked) GoldContainer else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (ach.isUnlocked) OnGoldContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = ach.description,
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val leaderboard by repository.getLeaderboard().collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "National Leaderboard",
                subtitle = "Field Force Champions",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ClinicalBackground)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(leaderboard) { ranker ->
                val isTop3 = ranker.rank <= 3
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = if (isTop3) BorderStroke(1.dp, GoldMetallic.copy(alpha = 0.6f)) else null,
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    when (ranker.rank) {
                                        1 -> GoldMetallic
                                        2 -> Color(0xFFC0C0C0)
                                        3 -> Color(0xFFCD7F32)
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#${ranker.rank}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isTop3) EmeraldDark else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = ranker.mrName,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${ranker.territory} • ${ranker.visitsCount} Visits",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${ranker.points} Pts",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                            )
                            Text(
                                text = "${ranker.trainingPercent}% Trained",
                                style = MaterialTheme.typography.labelSmall.copy(color = GoldDark, fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                }
            }
        }
    }
}
