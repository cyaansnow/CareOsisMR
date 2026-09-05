package com.example.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.CareOsisApp
import com.example.core.components.*
import com.example.data.local.entity.IncentiveRecordEntity
import com.example.data.local.entity.MRProfileEntity
import com.example.ui.navigation.Destinations
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDashboardScreen(
    onNavigate: (String) -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val scope = rememberCoroutineScope()
    val profile by repository.getProfile().collectAsStateWithLifecycle(initialValue = null)
    val pendingSyncCount by repository.getPendingSyncCount().collectAsStateWithLifecycle(initialValue = 0)
    val followUps by repository.getPendingFollowUps().collectAsStateWithLifecycle(initialValue = emptyList())
    val trainingProgressList by repository.getAllTrainingProgress().collectAsStateWithLifecycle(initialValue = emptyList())
    val unreadNotifs by repository.getUnreadNotificationCount().collectAsStateWithLifecycle(initialValue = 0)

    val currentIncentiveRecord by repository.getIncentiveRecord(profile?.empId ?: "CO-MR-8492", "August 2026").collectAsStateWithLifecycle(initialValue = null)
    val incentiveHistory by repository.getIncentiveRecordsForEmployee(profile?.empId ?: "CO-MR-8492").collectAsStateWithLifecycle(initialValue = emptyList())

    var showIncentiveBreakdown by remember { mutableStateOf(false) }
    var showIncentiveHistory by remember { mutableStateOf(false) }
    var selectedHistoryRecord by remember { mutableStateOf<IncentiveRecordEntity?>(null) }

    val currentDateStr = remember {
        val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
        sdf.format(Date())
    }

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "CareOsis MR",
                subtitle = "Digital Operating System",
                actions = {
                    IconButton(
                        onClick = { onNavigate(Destinations.NOTIFICATIONS) },
                        modifier = Modifier.testTag("home_notification_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotifs > 0) {
                                    Badge(containerColor = GoldMetallic) {
                                        Text(unreadNotifs.toString(), color = OnGoldContainer)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = ClinicalWhite
                            )
                        }
                    }
                    IconButton(
                        onClick = { onNavigate(Destinations.PROFILE) },
                        modifier = Modifier.testTag("home_profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = ClinicalWhite
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ClinicalBackground)
                .padding(innerPadding)
                .testTag("home_dashboard_scroll")
        ) {
            // 1. Offline Sync Banner
            item {
                CareOsisOfflineBanner(
                    pendingSyncCount = pendingSyncCount,
                    onSyncClick = {
                        scope.launch {
                            repository.performSync()
                        }
                    }
                )
            }

            // 2. Greeting & MR Level Progress Header
            item {
                GreetingCard(
                    profile = profile,
                    currentDate = currentDateStr,
                    onProfileClick = { onNavigate(Destinations.PROFILE) }
                )
            }

            // 2.5 Prominent "Incentive This Month" Card
            item {
                MRIncentiveThisMonthCard(
                    record = currentIncentiveRecord,
                    onClick = { showIncentiveBreakdown = true }
                )
            }

            // 3. Today's Performance 4-Card Grid
            item {
                SectionHeader(title = "Today's Performance", subtitle = "Live daily field tracker")
                PerformanceStatGrid(
                    visitsDone = profile?.completedVisitsToday ?: 12,
                    visitsTarget = profile?.targetVisitsToday ?: 15,
                    todayOrders = 18500.0,
                    todayIncentive = 2300.0,
                    todayExpenses = 450.0,
                    onStatClick = { dest -> onNavigate(dest) }
                )
            }

            // 4. Today's Route
            item {
                SectionHeader(
                    title = "Today's Route",
                    subtitle = "3 Doctors • 2 Retailers • 1 Stockist",
                    actionText = "Full Route",
                    onActionClick = { onNavigate(Destinations.ROUTE_PLAN) }
                )
                TodayRouteCard(
                    onStartVisit = { docId -> onNavigate("start_visit/$docId") },
                    onViewRoute = { onNavigate(Destinations.ROUTE_PLAN) }
                )
            }

            // 5. Quick Actions
            item {
                SectionHeader(title = "Quick Actions", subtitle = "Essential MR Operations")
                QuickActionsGrid(onActionClick = { dest -> onNavigate(dest) })
            }

            // HQ Executive Admin Command Hub (Company Owner Telemetry)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable { onNavigate(Destinations.HQ_DASHBOARD) }
                        .testTag("home_hq_command_banner"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = GeoHeroContainer),
                    border = BorderStroke(1.dp, GeoPrimary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(GeoPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = ClinicalWhite,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Company Owner & Admin Hub",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GeoHeroOnContainer
                                    )
                                )
                                Text(
                                    text = "Live Firestore telemetry, MR attendance & visits",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = GeoHeroOnContainer.copy(alpha = 0.85f),
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Open Admin Hub",
                            tint = GeoPrimary
                        )
                    }
                }
            }

            // 6. Follow-ups
            item {
                SectionHeader(
                    title = "Follow-ups",
                    subtitle = "${followUps.size} Action items pending",
                    actionText = "View All",
                    onActionClick = { onNavigate(Destinations.FOLLOW_UPS) }
                )
                if (followUps.isEmpty()) {
                    CareOsisCard(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "You're all caught up! No overdue follow-ups.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = NeutralTextSecondary)
                        )
                    }
                } else {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        followUps.take(2).forEach { followUp ->
                            FollowUpItemCard(
                                title = followUp.personName,
                                subtitle = followUp.reason,
                                time = followUp.followUpDate,
                                priority = followUp.priority,
                                onClick = { onNavigate(Destinations.FOLLOW_UPS) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            // 7. Academy Progress Spotlight
            item {
                SectionHeader(
                    title = "MR Academy Progress",
                    subtitle = "${profile?.trainingProgressPercent ?: 78}% Trained • ${profile?.level ?: "Expert MR"}",
                    actionText = "Academy",
                    onActionClick = { onNavigate(Destinations.ACADEMY) }
                )
                AcademySpotlightCard(
                    progressList = trainingProgressList,
                    onOpenAcademy = { onNavigate(Destinations.ACADEMY) },
                    onProductClick = { pId -> onNavigate("product_detail/$pId") }
                )
            }

            // 8. Monthly Sales & Incentive Overview
            item {
                SectionHeader(
                    title = "Monthly Target Progress",
                    subtitle = "August 2026 Achievement Cycle",
                    actionText = "Breakdown",
                    onActionClick = { onNavigate(Destinations.INCENTIVES) }
                )
                MonthlyTargetCard(
                    sales = profile?.monthlySales ?: 156800.0,
                    target = profile?.monthlyTarget ?: 200000.0,
                    estimatedIncentive = profile?.currentIncentive ?: 8450.0,
                    onCardClick = { onNavigate(Destinations.INCENTIVES) }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showIncentiveBreakdown) {
        IncentiveBreakdownModal(
            record = currentIncentiveRecord,
            onDismiss = { showIncentiveBreakdown = false },
            onViewHistory = {
                showIncentiveBreakdown = false
                showIncentiveHistory = true
            }
        )
    }

    if (showIncentiveHistory) {
        IncentiveHistoryModal(
            records = incentiveHistory,
            onSelectRecord = { rec ->
                selectedHistoryRecord = rec
            },
            onDismiss = { showIncentiveHistory = false }
        )
    }

    if (selectedHistoryRecord != null) {
        IncentiveBreakdownModal(
            record = selectedHistoryRecord,
            onDismiss = { selectedHistoryRecord = null },
            onViewHistory = {
                selectedHistoryRecord = null
                showIncentiveHistory = true
            }
        )
    }
}

@Composable
fun MRIncentiveThisMonthCard(
    record: IncentiveRecordEntity?,
    onClick: () -> Unit
) {
    val finalIncentive = record?.finalIncentive ?: 8450.0
    val status = record?.status ?: "ESTIMATED"
    val target = record?.target ?: 200000.0
    val achievement = record?.achievementPercent ?: 82.0
    val rate = record?.incentiveRate ?: 3.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() }
            .testTag("mr_incentive_this_month_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (status == "FINAL") StatusSuccessContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (status == "FINAL") StatusSuccess.copy(alpha = 0.4f) else GoldMetallic.copy(alpha = 0.35f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row: Title + Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(GoldMetallic.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CurrencyRupee,
                            contentDescription = null,
                            tint = if (status == "FINAL") StatusSuccess else OnGoldContainer,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "Incentive This Month",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                when (status) {
                    "FINAL" -> {
                        CareOsisStatusChip(
                            text = "Final",
                            containerColor = StatusSuccess,
                            contentColor = ClinicalWhite
                        )
                    }
                    "PENDING_APPROVAL" -> {
                        CareOsisStatusChip(
                            text = "Pending Approval",
                            containerColor = StatusWarningContainer,
                            contentColor = StatusWarning
                        )
                    }
                    else -> {
                        CareOsisStatusChip(
                            text = "Estimated",
                            containerColor = GoldMetallicLight,
                            contentColor = OnGoldContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Figure
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "₹${String.format("%,.0f", finalIncentive)}",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (status == "FINAL") StatusSuccess else MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = if (status == "FINAL") "Final approved payout" else "Live rules engine calculation",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Breakdown",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
            Spacer(modifier = Modifier.height(12.dp))

            // 3-Metric Footer: Target, Achievement, Incentive Rate
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Target",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "₹${String.format("%,.0f", target)}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Column {
                    Text(
                        text = "Achievement",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "${String.format("%.0f", achievement)}%",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (achievement >= 80) StatusSuccess else MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Incentive Rate",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "${String.format("%.0f", rate)}%",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun GreetingCard(
    profile: MRProfileEntity?,
    currentDate: String,
    onProfileClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onProfileClick() }
            .testTag("home_greeting_card"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = GeoHeroContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Hello, ${profile?.name ?: "Aman Chhabra"}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = GeoHeroOnContainer,
                            fontSize = 22.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${profile?.territory ?: "North Delhi"} • $currentDate",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = GeoTextSecondary,
                            fontSize = 12.sp
                        )
                    )
                }

                // Level Badge with Dark Contrast Pill (Pro style from Geometric Balance)
                Surface(
                    shape = RoundedCornerShape(100),
                    color = GeoHeroOnContainer
                ) {
                    Text(
                        text = (profile?.level ?: "PRO MR").uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp,
                            color = ClinicalWhite
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Pill Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onProfileClick() },
                    shape = RoundedCornerShape(16.dp),
                    color = GeoSecondaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = GeoOnSecondaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Check-in: ${profile?.checkInTime ?: "08:45 AM"}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = GeoOnSecondaryContainer
                            )
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onProfileClick() },
                    shape = RoundedCornerShape(16.dp),
                    color = GeoSecondaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = GeoOnSecondaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${profile?.trainingProgressPercent ?: 78}% Certified",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = GeoOnSecondaryContainer
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = GeoTextPrimary,
                    fontSize = 16.sp
                )
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = GeoTextSecondary,
                        fontSize = 12.sp
                    )
                )
            }
        }
        if (actionText != null && onActionClick != null) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = GeoPrimary
                ),
                modifier = Modifier
                    .clickable { onActionClick() }
                    .padding(4.dp)
            )
        }
    }
}

@Composable
private fun PerformanceStatGrid(
    visitsDone: Int,
    visitsTarget: Int,
    todayOrders: Double,
    todayIncentive: Double,
    todayExpenses: Double,
    onStatClick: (String) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CareOsisStatCard(
                title = "Doctor Visits",
                value = "$visitsDone / $visitsTarget",
                subtitle = "${((visitsDone.toFloat() / visitsTarget) * 100).toInt()}% of daily target",
                icon = Icons.Default.MedicalServices,
                accentColor = GeoPrimary,
                modifier = Modifier.weight(1f),
                onClick = { onStatClick(Destinations.VISITS) }
            )
            CareOsisStatCard(
                title = "Today's Orders",
                value = "₹${todayOrders.toInt()}",
                subtitle = "2 Orders Booked",
                icon = Icons.Default.ShoppingCart,
                accentColor = GeoSecondary,
                modifier = Modifier.weight(1f),
                onClick = { onStatClick(Destinations.ORDERS) }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CareOsisStatCard(
                title = "Est. Incentive",
                value = "₹${todayIncentive.toInt()}",
                subtitle = "Today's contribution",
                icon = Icons.Default.MonetizationOn,
                accentColor = Color(0xFF4F378B),
                modifier = Modifier.weight(1f),
                onClick = { onStatClick(Destinations.INCENTIVES) }
            )
            CareOsisStatCard(
                title = "Field Expenses",
                value = "₹${todayExpenses.toInt()}",
                subtitle = "Fuel & Refreshment",
                icon = Icons.Default.ReceiptLong,
                accentColor = GeoSecondary,
                modifier = Modifier.weight(1f),
                onClick = { onStatClick(Destinations.EXPENSES) }
            )
        }
    }
}

@Composable
private fun TodayRouteCard(
    onStartVisit: (String) -> Unit,
    onViewRoute: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onViewRoute() }
            .testTag("today_route_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = GeoSurface),
        border = BorderStroke(1.dp, GeoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(GeoPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = ClinicalWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Rohini Sec-9 → Pitampura Circuit",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = GeoTextPrimary
                        )
                    )
                }
                CareOsisStatusChip(
                    text = "IN-PROGRESS",
                    containerColor = GeoHeroContainer,
                    contentColor = GeoHeroOnContainer
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Next Stop Spotlight
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = GeoSurfaceWhite,
                border = BorderStroke(1.dp, GeoBorderLight)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Dr. Rajesh Sharma",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = GeoTextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            CareOsisStatusChip(
                                text = "CAT A",
                                containerColor = GeoSurfaceVariant,
                                contentColor = GeoHeroOnContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "09:30 AM • Follow-up & Booster Trial",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = GeoTextSecondary,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Button(
                        onClick = { onStartVisit("DOC-101") },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text("Start Visit", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionsGrid(
    onActionClick: (String) -> Unit
) {
    val actions = listOf(
        Triple("Visit Doctor", Icons.Default.PersonAddAlt1, Destinations.DOCTOR_LIST),
        Triple("New Order", Icons.Default.AddShoppingCart, Destinations.CREATE_ORDER),
        Triple("Add Expense", Icons.Default.PostAdd, Destinations.ADD_EXPENSE),
        Triple("MR Academy", Icons.Default.School, Destinations.ACADEMY),
        Triple("Attendance", Icons.Default.CheckCircleOutline, Destinations.ATTENDANCE),
        Triple("Route Plan", Icons.Default.AltRoute, Destinations.ROUTE_PLAN)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        actions.take(3).forEach { (label, icon, route) ->
            QuickActionButton(
                label = label,
                icon = icon,
                modifier = Modifier.weight(1f),
                onClick = { onActionClick(route) }
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        actions.drop(3).forEach { (label, icon, route) ->
            QuickActionButton(
                label = label,
                icon = icon,
                modifier = Modifier.weight(1f),
                onClick = { onActionClick(route) }
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag("quick_action_${label.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GeoSurface),
        border = BorderStroke(1.dp, GeoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(GeoSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GeoPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    color = GeoTextPrimary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun FollowUpItemCard(
    title: String,
    subtitle: String,
    time: String,
    priority: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GeoSurfaceWhite),
        border = BorderStroke(1.dp, GeoBorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(GeoSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    tint = GeoHeroOnContainer,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = GeoTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    CareOsisStatusChip(
                        text = priority.uppercase(),
                        containerColor = if (priority == "High") GeoErrorContainer else GeoSurfaceVariant,
                        contentColor = if (priority == "High") GeoError else GeoTextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = GeoTextSecondary,
                        fontSize = 12.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = time,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GeoPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = GeoTextSecondary
            )
        }
    }
}

@Composable
private fun AcademySpotlightCard(
    progressList: List<com.example.data.local.entity.TrainingProgressEntity>,
    onOpenAcademy: () -> Unit,
    onProductClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onOpenAcademy() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = GeoSurface),
        border = BorderStroke(1.dp, GeoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "CareOsis Core Masterclasses",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = GeoTextPrimary
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            progressList.take(3).forEach { item ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = GeoSurfaceWhite,
                    border = BorderStroke(1.dp, GeoBorderLight),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onProductClick(item.productId) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.productName,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = GeoTextPrimary
                            )
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CareOsisProgressBar(
                                progressPercent = item.completionPercentage,
                                modifier = Modifier.width(80.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${item.completionPercentage}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GeoPrimary
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthlyTargetCard(
    sales: Double,
    target: Double,
    estimatedIncentive: Double,
    onCardClick: () -> Unit
) {
    val achievementPercent = if (target > 0) ((sales / target) * 100).toInt() else 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onCardClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = GeoHeroContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Monthly Target",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = GeoTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "₹${sales.toInt()}",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = GeoHeroOnContainer
                        )
                    )
                    Text(
                        text = "Target: ₹${target.toInt()}",
                        style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(100),
                    color = GeoHeroOnContainer
                ) {
                    Text(
                        text = "$achievementPercent%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = ClinicalWhite
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            CareOsisProgressBar(
                progressPercent = achievementPercent,
                progressColor = GeoPrimary,
                trackColor = GeoSurfaceWhite.copy(alpha = 0.6f),
                barHeight = 8.dp
            )

            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Projected Incentive:",
                    style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary)
                )
                Text(
                    text = "+₹${estimatedIncentive.toInt()}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = GeoSuccess
                    )
                )
            }
        }
    }
}
