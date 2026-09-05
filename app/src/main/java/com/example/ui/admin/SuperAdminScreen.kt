package com.example.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.CareOsisApp
import com.example.core.components.CareOsisPrimaryButton
import com.example.core.components.CareOsisSecondaryButton
import com.example.core.components.CareOsisStatusChip
import com.example.core.components.CareOsisTopBar
import com.example.data.local.entity.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperAdminScreen(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val scope = rememberCoroutineScope()

    val currentUser by repository.currentUser.collectAsStateWithLifecycle(initialValue = null)
    val allAdmins by repository.getAllAdmins().collectAsStateWithLifecycle(initialValue = emptyList())
    val allEmployees by repository.getAllEmployees().collectAsStateWithLifecycle(initialValue = emptyList())
    val allRegions by repository.getAllRegions().collectAsStateWithLifecycle(initialValue = emptyList())
    val incentiveRules by repository.getAllIncentiveRules().collectAsStateWithLifecycle(initialValue = emptyList())
    val activeSalaryRule by repository.getActiveSalaryRule().collectAsStateWithLifecycle(initialValue = null)
    val auditLogs by repository.getAllAuditLogs().collectAsStateWithLifecycle(initialValue = emptyList())

    val allOrders by repository.getAllOrders().collectAsStateWithLifecycle(initialValue = emptyList())
    val allDoctors by repository.getAllDoctors().collectAsStateWithLifecycle(initialValue = emptyList())
    val allExpenses by repository.getAllExpenses().collectAsStateWithLifecycle(initialValue = emptyList())

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Global Hub", "Admins & Roles", "Regions", "Incentive Engine", "Salary Rules", "Audit Logs")

    var showCreateAdminDialog by remember { mutableStateOf(false) }
    var showCreateRegionDialog by remember { mutableStateOf(false) }
    var showCreateIncentiveDialog by remember { mutableStateOf(false) }
    var showCreateEmployeeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "CareOsis Super Admin Control",
                subtitle = "Global Enterprise Command Center • ${currentUser?.name ?: "HQ Authority"}",
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout Super Admin",
                            tint = StatusError
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            // Security Warning & Verification Badge
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = GeoHeroContainer,
                border = BorderStroke(1.dp, GeoPrimary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = GeoHeroOnContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Authenticated: Role == SUPER_ADMIN (Global Authority)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GeoHeroOnContainer
                            )
                        )
                    }
                    CareOsisStatusChip(
                        text = "RESTRICTED",
                        containerColor = StatusWarningContainer,
                        contentColor = StatusWarning
                    )
                }
            }

            // Tab Navigation Scrollable Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 16.dp,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    )
                }
            }

            // Content per Tab
            when (selectedTab) {
                0 -> GlobalOverviewTab(
                    adminsCount = allAdmins.size,
                    employeesCount = allEmployees.size,
                    regionsCount = allRegions.size,
                    doctorsCount = allDoctors.size,
                    ordersCount = allOrders.size,
                    totalSales = allOrders.sumOf { it.totalAmount },
                    expensesCount = allExpenses.size,
                    totalExpenses = allExpenses.sumOf { it.amount },
                    regions = allRegions,
                    onOpenTab = { selectedTab = it },
                    onCreateAdmin = { showCreateAdminDialog = true },
                    onCreateRegion = { showCreateRegionDialog = true }
                )
                1 -> AdminManagementTab(
                    admins = allAdmins,
                    allRegions = allRegions,
                    allEmployees = allEmployees,
                    onCreateAdmin = { showCreateAdminDialog = true },
                    onUpdateScope = { adminId, regions, perms, canCreateEmp, scopeMode, empIds ->
                        scope.launch {
                            repository.updateAdminScope(
                                adminId = adminId,
                                regionIds = regions,
                                permissions = perms,
                                canCreateEmployees = canCreateEmp,
                                scopeMode = scopeMode,
                                assignedEmployeeIds = empIds,
                                actorId = currentUser?.id ?: "CO-SA-001"
                            )
                        }
                    },
                    onToggleStatus = { adminId, newStatus ->
                        scope.launch {
                            repository.updateUserStatus(
                                id = adminId,
                                status = newStatus,
                                actorId = currentUser?.id ?: "CO-SA-001"
                            )
                        }
                    }
                )
                2 -> RegionManagementTab(
                    regions = allRegions,
                    employees = allEmployees,
                    onCreateRegion = { showCreateRegionDialog = true }
                )
                3 -> IncentiveEngineTab(
                    rules = incentiveRules,
                    employeesCount = allEmployees.size,
                    onPublishRule = { showCreateIncentiveDialog = true }
                )
                4 -> SalaryRulesTab(
                    activeRule = activeSalaryRule,
                    allEmployees = allEmployees,
                    onUpdateRule = { newRule ->
                        scope.launch {
                            repository.updateSalaryRule(newRule, currentUser?.id ?: "CO-SA-001")
                        }
                    }
                )
                5 -> AuditLogsTab(logs = auditLogs)
            }
        }
    }

    // Dialogs
    if (showCreateAdminDialog) {
        CreateAdminDialog(
            allRegions = allRegions,
            allEmployees = allEmployees,
            onDismiss = { showCreateAdminDialog = false },
            onConfirm = { newAdmin ->
                scope.launch {
                    repository.createUser(newAdmin, currentUser?.id ?: "CO-SA-001")
                    showCreateAdminDialog = false
                }
            }
        )
    }

    if (showCreateRegionDialog) {
        CreateRegionDialog(
            onDismiss = { showCreateRegionDialog = false },
            onConfirm = { newRegion ->
                scope.launch {
                    repository.createRegion(newRegion, currentUser?.id ?: "CO-SA-001")
                    showCreateRegionDialog = false
                }
            }
        )
    }

    if (showCreateIncentiveDialog) {
        CreateIncentiveRuleDialog(
            currentVersion = incentiveRules.maxOfOrNull { it.versionNumber } ?: 1,
            employeesCount = allEmployees.size,
            onDismiss = { showCreateIncentiveDialog = false },
            onConfirm = { newRule ->
                scope.launch {
                    repository.publishNewIncentiveRule(newRule, currentUser?.id ?: "CO-SA-001")
                    showCreateIncentiveDialog = false
                }
            }
        )
    }
}

// -------------------------------------------------------------
// TAB 1: Global Overview
// -------------------------------------------------------------
@Composable
private fun GlobalOverviewTab(
    adminsCount: Int,
    employeesCount: Int,
    regionsCount: Int,
    doctorsCount: Int,
    ordersCount: Int,
    totalSales: Double,
    expensesCount: Int,
    totalExpenses: Double,
    regions: List<RegionEntity>,
    onOpenTab: (Int) -> Unit,
    onCreateAdmin: () -> Unit,
    onCreateRegion: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "CareOsis Healthcare Platform Architecture",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                text = "Hierarchical governance across all divisions, territories, and field personnel.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        // Global KPI Cards Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlobalKpiCard(
                    title = "Super Admins",
                    value = "1 Global",
                    subtitle = "Central HQ",
                    icon = Icons.Default.AdminPanelSettings,
                    containerColor = GeoHeroContainer,
                    contentColor = GeoHeroOnContainer,
                    modifier = Modifier.weight(1f)
                )
                GlobalKpiCard(
                    title = "Regional Admins",
                    value = "$adminsCount Active",
                    subtitle = "Zonal Heads",
                    icon = Icons.Default.ManageAccounts,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    onClick = { onOpenTab(1) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlobalKpiCard(
                    title = "Operating Regions",
                    value = "$regionsCount Zones",
                    subtitle = "NCR, MUM, BLR+",
                    icon = Icons.Default.Map,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    onClick = { onOpenTab(2) }
                )
                GlobalKpiCard(
                    title = "Field MR Force",
                    value = "$employeesCount Reps",
                    subtitle = "100% Deployed",
                    icon = Icons.Default.Groups,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlobalKpiCard(
                    title = "Commercial Sales",
                    value = "₹${String.format("%,.0f", totalSales)}",
                    subtitle = "$ordersCount Orders Logged",
                    icon = Icons.Default.ShoppingBag,
                    containerColor = StatusSuccessContainer,
                    contentColor = StatusSuccess,
                    modifier = Modifier.weight(1f)
                )
                GlobalKpiCard(
                    title = "Field Expenses",
                    value = "₹${String.format("%,.0f", totalExpenses)}",
                    subtitle = "$expensesCount Claims Audited",
                    icon = Icons.Default.ReceiptLong,
                    containerColor = StatusWarningContainer,
                    contentColor = StatusWarning,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Global Administration Actions
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Executive Provisioning Actions",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onCreateAdmin,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("super_admin_create_admin_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Create Admin", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        }

                        OutlinedButton(
                            onClick = onCreateRegion,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("super_admin_create_region_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.AddLocationAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Region", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }

        // Regions Overview List
        item {
            Text(
                text = "Live Operating Territories",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }

        items(regions) { region ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = region.name,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "HQ: ${region.headquarters} • Code: ${region.code}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Target: ₹${String.format("%,.0f", region.monthlyTarget)} | ${region.activeMRCount} MRs | ${region.doctorCount} Doctors",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                    CareOsisStatusChip(
                        text = region.status,
                        containerColor = StatusSuccessContainer,
                        contentColor = StatusSuccess
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun GlobalKpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = contentColor.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = contentColor.copy(alpha = 0.7f)
                )
            )
        }
    }
}

// -------------------------------------------------------------
// TAB 2: Admin Management (Super Admin only)
// -------------------------------------------------------------
@Composable
private fun AdminManagementTab(
    admins: List<UserAccountEntity>,
    allRegions: List<RegionEntity>,
    allEmployees: List<UserAccountEntity>,
    onCreateAdmin: () -> Unit,
    onUpdateScope: (adminId: String, regions: String, perms: String, canCreateEmp: Boolean, scopeMode: String, empIds: String) -> Unit,
    onToggleStatus: (adminId: String, newStatus: String) -> Unit
) {
    var editingAdmin by remember { mutableStateOf<UserAccountEntity?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Regional Administrators",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Text(
                        text = "Assign regions, employee scopes, and granular permissions.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                Button(
                    onClick = onCreateAdmin,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("admin_tab_create_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Admin", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        items(admins) { admin ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ManageAccounts,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = admin.name,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = "${admin.id} • ${admin.designation}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }

                        CareOsisStatusChip(
                            text = admin.status,
                            containerColor = if (admin.status == "ACTIVE") StatusSuccessContainer else StatusErrorContainer,
                            contentColor = if (admin.status == "ACTIVE") StatusSuccess else StatusError
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Region Scope
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Map,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Assigned Regions: ${admin.assignedRegionIds.replace(",", ", ")}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Employee Scope Mode
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Groups,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (admin.employeeScopeMode == "ALL_IN_REGION")
                                "Employee Access: All employees in assigned regions"
                            else "Employee Access: Limited to assigned IDs (${admin.assignedEmployeeIds})",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Can Create Employees Permission
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (admin.canCreateEmployees) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (admin.canCreateEmployees) StatusSuccess else StatusError,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (admin.canCreateEmployees) "Permission: Can Create & Onboard MRs" else "Permission: Cannot Create MRs",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { editingAdmin = admin },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit Scope & Perms", style = MaterialTheme.typography.bodySmall)
                        }

                        Button(
                            onClick = {
                                val nextStatus = if (admin.status == "ACTIVE") "SUSPENDED" else "ACTIVE"
                                onToggleStatus(admin.id, nextStatus)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (admin.status == "ACTIVE") StatusErrorContainer else StatusSuccessContainer,
                                contentColor = if (admin.status == "ACTIVE") StatusError else StatusSuccess
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (admin.status == "ACTIVE") "Suspend" else "Activate",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Edit Scope Dialog
    if (editingAdmin != null) {
        EditAdminScopeDialog(
            admin = editingAdmin!!,
            allRegions = allRegions,
            allEmployees = allEmployees,
            onDismiss = { editingAdmin = null },
            onConfirm = { updatedRegions, updatedPerms, canCreateEmp, scopeMode, assignedEmpIds ->
                onUpdateScope(editingAdmin!!.id, updatedRegions, updatedPerms, canCreateEmp, scopeMode, assignedEmpIds)
                editingAdmin = null
            }
        )
    }
}

// -------------------------------------------------------------
// TAB 3: Region Management
// -------------------------------------------------------------
@Composable
private fun RegionManagementTab(
    regions: List<RegionEntity>,
    employees: List<UserAccountEntity>,
    onCreateRegion: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Company Regional Hierarchy",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Text(
                        text = "Data isolation boundaries across CareOsis operations.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                Button(
                    onClick = onCreateRegion,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AddLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Region", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        items(regions) { region ->
            val regionEmps = employees.filter { it.assignedRegionIds.contains(region.id) }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = region.name,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "Code: ${region.code} • State: ${region.state} • HQ: ${region.headquarters}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                        CareOsisStatusChip(
                            text = region.status,
                            containerColor = StatusSuccessContainer,
                            contentColor = StatusSuccess
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Target P.M.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Text(
                                text = "₹${String.format("%,.0f", region.monthlyTarget)}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            )
                        }
                        Column {
                            Text(
                                text = "Active MRs",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Text(
                                text = "${regionEmps.size} Reps Assigned",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Column {
                            Text(
                                text = "Doctors",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Text(
                                text = "${region.doctorCount} Listed",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// -------------------------------------------------------------
// TAB 4: Incentive Engine (Configurable Calculation Rules & Versioning)
// -------------------------------------------------------------
@Composable
private fun IncentiveEngineTab(
    rules: List<IncentiveRuleEntity>,
    employeesCount: Int,
    onPublishRule: () -> Unit
) {
    val activeRules = rules.filter { it.status == "ACTIVE" }
    val archivedRules = rules.filter { it.status == "ARCHIVED" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Dynamic Incentive Engine",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Text(
                        text = "Configure tier rates, booster rewards, and publish new rule versions.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                Button(
                    onClick = onPublishRule,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("super_admin_new_incentive_rule_btn")
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Rule", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        item {
            // Active Rule Version Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Active Calculation Engine (Version ${activeRules.firstOrNull()?.versionNumber ?: 1})",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                        CareOsisStatusChip(
                            text = "LIVE IN PRODUCTION",
                            containerColor = StatusSuccessContainer,
                            contentColor = StatusSuccess
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Effective: ${activeRules.firstOrNull()?.effectiveFrom ?: "01-08-2026"} to ${activeRules.firstOrNull()?.effectiveTo ?: "31-12-2026"} • Applies to all CareOsis applications",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                    )
                }
            }
        }

        item {
            Text(
                text = "Configured Achievement Tiers",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }

        items(activeRules) { rule ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = rule.ruleName,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "Range: ${rule.minThresholdPercent.toInt()}% – ${if (rule.maxThresholdPercent > 200) "100%+" else "${rule.maxThresholdPercent.toInt()}%"} Target Achievement",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = rule.formulaDescription,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${rule.incentivePercent}%",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        if (rule.fixedRewardAmount > 0) {
                            Text(
                                text = "+ ₹${String.format("%,.0f", rule.fixedRewardAmount)} Bonus",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = StatusSuccess
                                )
                            )
                        }
                    }
                }
            }
        }

        if (archivedRules.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Historical / Archived Rule Versions",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            items(archivedRules) { rule ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${rule.ruleName} (v${rule.versionNumber})",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "Effective: ${rule.effectiveFrom} to ${rule.effectiveTo}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                        CareOsisStatusChip(text = "ARCHIVED", containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// -------------------------------------------------------------
// TAB 5: Salary Rules
// -------------------------------------------------------------
@Composable
private fun SalaryRulesTab(
    activeRule: SalaryRuleEntity?,
    allEmployees: List<UserAccountEntity>,
    onUpdateRule: (SalaryRuleEntity) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var baseSalaryInput by remember(activeRule) { mutableStateOf((activeRule?.baseSalary ?: 35000.0).toString()) }
    var fixedAllowanceInput by remember(activeRule) { mutableStateOf((activeRule?.fixedAllowance ?: 8000.0).toString()) }
    var travelAllowancePerKmInput by remember(activeRule) { mutableStateOf((activeRule?.travelAllowancePerKm ?: 4.5).toString()) }
    var dailyAllowancePerDayInput by remember(activeRule) { mutableStateOf((activeRule?.dailyAllowancePerDay ?: 350.0).toString()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Configurable Compensation & Salary Policy",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                text = "Global formula for base earnings, daily travel allowances, and fixed allowances.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Standard Field Compensation Formula",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        CareOsisStatusChip(
                            text = "v${activeRule?.versionNumber ?: 1} ACTIVE",
                            containerColor = StatusSuccessContainer,
                            contentColor = StatusSuccess
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (!isEditing) {
                        SalaryComponentRow("Base Salary Benchmark", "₹${String.format("%,.0f", activeRule?.baseSalary ?: 35000.0)}")
                        SalaryComponentRow("Fixed Monthly Allowance", "₹${String.format("%,.0f", activeRule?.fixedAllowance ?: 8000.0)}")
                        SalaryComponentRow("Travel Allowance Rate", "₹${activeRule?.travelAllowancePerKm ?: 4.5} per km (Verified GPS)")
                        SalaryComponentRow("Daily Allowance (DA)", "₹${activeRule?.dailyAllowancePerDay ?: 350.0} per on-field day")
                        SalaryComponentRow("PF & Statutory Deduction", "${activeRule?.deductionPfPercent ?: 12.0}% on Basic")

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { isEditing = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Salary Parameters")
                        }
                    } else {
                        OutlinedTextField(
                            value = baseSalaryInput,
                            onValueChange = { baseSalaryInput = it },
                            label = { Text("Base Salary Benchmark (₹)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = fixedAllowanceInput,
                            onValueChange = { fixedAllowanceInput = it },
                            label = { Text("Fixed Allowance (₹)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = travelAllowancePerKmInput,
                            onValueChange = { travelAllowancePerKmInput = it },
                            label = { Text("Travel Allowance per km (₹)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = dailyAllowancePerDayInput,
                            onValueChange = { dailyAllowancePerDayInput = it },
                            label = { Text("Daily Allowance per day (₹)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { isEditing = false },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Cancel")
                            }

                            Button(
                                onClick = {
                                    val updated = (activeRule ?: SalaryRuleEntity(id = "SAL-R1-GLOBAL", ruleName = "CareOsis Salary Policy")).copy(
                                        baseSalary = baseSalaryInput.toDoubleOrNull() ?: 35000.0,
                                        fixedAllowance = fixedAllowanceInput.toDoubleOrNull() ?: 8000.0,
                                        travelAllowancePerKm = travelAllowancePerKmInput.toDoubleOrNull() ?: 4.5,
                                        dailyAllowancePerDay = dailyAllowancePerDayInput.toDoubleOrNull() ?: 350.0,
                                        versionNumber = (activeRule?.versionNumber ?: 1) + 1,
                                        updatedAt = System.currentTimeMillis()
                                    )
                                    onUpdateRule(updated)
                                    isEditing = false
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Save & Publish v${(activeRule?.versionNumber ?: 1) + 1}")
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SalaryComponentRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface))
    }
}

// -------------------------------------------------------------
// TAB 6: Immutable Audit Logs
// -------------------------------------------------------------
@Composable
private fun AuditLogsTab(logs: List<AuditLogEntity>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Immutable Enterprise Audit Trail",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                text = "Cryptographically ordered log of all administrative, scope, and rule mutations.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        items(logs) { log ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CareOsisStatusChip(
                            text = log.action,
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = log.formattedDate,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = log.targetEntity,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    )
                    Text(
                        text = "Actor: ${log.userName} (${log.userId}) [${log.userRole}]",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                    )
                    if (log.newValue.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Mutation: ${log.newValue}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// -------------------------------------------------------------
// DIALOGS
// -------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateAdminDialog(
    allRegions: List<RegionEntity>,
    allEmployees: List<UserAccountEntity>,
    onDismiss: () -> Unit,
    onConfirm: (UserAccountEntity) -> Unit
) {
    var adminName by remember { mutableStateOf("") }
    var adminId by remember { mutableStateOf("CO-ADM-${(100..999).random()}") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("AdminPass@2026") }
    var designation by remember { mutableStateOf("Regional Operations Manager") }
    var selectedRegions = remember { mutableStateListOf<String>("DELHI_NCR") }
    var canCreateEmployees by remember { mutableStateOf(true) }
    var scopeMode by remember { mutableStateOf("ALL_IN_REGION") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create Regional Administrator", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = adminName,
                        onValueChange = { adminName = it },
                        label = { Text("Admin Full Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
                item {
                    OutlinedTextField(
                        value = adminId,
                        onValueChange = { adminId = it },
                        label = { Text("Admin ID *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
                item {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
                item {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Temporary Password *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
                item {
                    Text("Assign Controlled Regions:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(allRegions) { reg ->
                            val isSelected = selectedRegions.contains(reg.id)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) {
                                        if (selectedRegions.size > 1) selectedRegions.remove(reg.id)
                                    } else {
                                        selectedRegions.add(reg.id)
                                    }
                                },
                                label = { Text(reg.name) }
                            )
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Grant Permission to Create MRs:", style = MaterialTheme.typography.bodySmall)
                        Switch(
                            checked = canCreateEmployees,
                            onCheckedChange = { canCreateEmployees = it }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (adminName.isNotBlank() && adminId.isNotBlank()) {
                        val newAdmin = UserAccountEntity(
                            id = adminId.trim(),
                            name = adminName.trim(),
                            email = email.ifBlank { "${adminId.lowercase()}@careosis.com" },
                            phone = phone.ifBlank { "+91 98000 00000" },
                            role = "ADMIN",
                            password = password,
                            status = "ACTIVE",
                            assignedRegionIds = selectedRegions.joinToString(","),
                            employeeScopeMode = scopeMode,
                            assignedEmployeeIds = "ALL",
                            permissions = "VIEW_EMPLOYEES,VIEW_DOCTORS,CREATE_DOCTOR,VIEW_ORDERS,APPROVE_ORDER,VIEW_EXPENSES,APPROVE_EXPENSE,VIEW_REPORTS,VIEW_INCENTIVES" + (if (canCreateEmployees) ",CREATE_EMPLOYEE,EDIT_EMPLOYEE" else ""),
                            canCreateEmployees = canCreateEmployees,
                            designation = designation,
                            territoryName = selectedRegions.joinToString(", "),
                            joiningDate = "20 Aug 2026"
                        )
                        onConfirm(newAdmin)
                    }
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Provision Admin")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun EditAdminScopeDialog(
    admin: UserAccountEntity,
    allRegions: List<RegionEntity>,
    allEmployees: List<UserAccountEntity>,
    onDismiss: () -> Unit,
    onConfirm: (regions: String, perms: String, canCreateEmp: Boolean, scopeMode: String, assignedEmpIds: String) -> Unit
) {
    val selectedRegions = remember { mutableStateListOf(*admin.assignedRegionIds.split(",").toTypedArray()) }
    var canCreateEmp by remember { mutableStateOf(admin.canCreateEmployees) }
    var scopeMode by remember { mutableStateOf(admin.employeeScopeMode) }
    var canEditIncentives by remember { mutableStateOf(admin.permissions.contains("EDIT_INCENTIVE_RULES")) }
    var canViewSalary by remember { mutableStateOf(admin.permissions.contains("VIEW_SALARY")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Scope: ${admin.name}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Text("Assigned Operational Regions:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(allRegions) { reg ->
                            val isSelected = selectedRegions.contains(reg.id)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) {
                                        if (selectedRegions.size > 1) selectedRegions.remove(reg.id)
                                    } else {
                                        selectedRegions.add(reg.id)
                                    }
                                },
                                label = { Text(reg.name) }
                            )
                        }
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Permission: Create & Onboard MRs", style = MaterialTheme.typography.bodySmall)
                        Switch(checked = canCreateEmp, onCheckedChange = { canCreateEmp = it })
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Permission: Edit Regional Incentive Rules", style = MaterialTheme.typography.bodySmall)
                        Switch(checked = canEditIncentives, onCheckedChange = { canEditIncentives = it })
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Permission: View Confidential Salary", style = MaterialTheme.typography.bodySmall)
                        Switch(checked = canViewSalary, onCheckedChange = { canViewSalary = it })
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val permsList = mutableListOf("VIEW_EMPLOYEES", "VIEW_DOCTORS", "CREATE_DOCTOR", "VIEW_ORDERS", "APPROVE_ORDER", "VIEW_EXPENSES", "APPROVE_EXPENSE", "VIEW_REPORTS", "VIEW_INCENTIVES")
                    if (canCreateEmp) permsList.addAll(listOf("CREATE_EMPLOYEE", "EDIT_EMPLOYEE"))
                    if (canEditIncentives) permsList.add("EDIT_INCENTIVE_RULES")
                    if (canViewSalary) permsList.add("VIEW_SALARY")

                    onConfirm(
                        selectedRegions.joinToString(","),
                        permsList.joinToString(","),
                        canCreateEmp,
                        scopeMode,
                        admin.assignedEmployeeIds
                    )
                }
            ) {
                Text("Save Scope")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun CreateRegionDialog(
    onDismiss: () -> Unit,
    onConfirm: (RegionEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var hq by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("2000000") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Operating Region", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Region Name * (e.g. Pune City)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = state, onValueChange = { state = it }, label = { Text("State *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Region Code * (e.g. MH-PUN)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = hq, onValueChange = { hq = it }, label = { Text("Headquarters Area *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = target, onValueChange = { target = it }, label = { Text("Monthly Revenue Target (₹)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && code.isNotBlank()) {
                        val regionId = code.uppercase().replace("-", "_")
                        onConfirm(
                            RegionEntity(
                                id = regionId,
                                name = name.trim(),
                                state = state.trim(),
                                code = code.trim(),
                                headquarters = hq.trim(),
                                monthlyTarget = target.toDoubleOrNull() ?: 2000000.0
                            )
                        )
                    }
                }
            ) {
                Text("Create Region")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun CreateIncentiveRuleDialog(
    currentVersion: Int,
    employeesCount: Int,
    onDismiss: () -> Unit,
    onConfirm: (IncentiveRuleEntity) -> Unit
) {
    var ruleName by remember { mutableStateOf("Tier Boost Rule") }
    var minPercent by remember { mutableStateOf("100") }
    var maxPercent by remember { mutableStateOf("200") }
    var incentivePercent by remember { mutableStateOf("6.0") }
    var fixedReward by remember { mutableStateOf("6000") }

    val nextVersion = currentVersion + 1

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Publish Incentive Engine v$nextVersion", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    color = StatusWarningContainer,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Impact Notice: Publishing this version will activate dynamic calculations for $employeesCount field representatives across all regions.",
                        style = MaterialTheme.typography.bodySmall.copy(color = StatusWarning, fontSize = 11.sp),
                        modifier = Modifier.padding(10.dp)
                    )
                }
                OutlinedTextField(value = ruleName, onValueChange = { ruleName = it }, label = { Text("Tier Name *") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = minPercent, onValueChange = { minPercent = it }, label = { Text("Min %") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = maxPercent, onValueChange = { maxPercent = it }, label = { Text("Max %") }, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = incentivePercent, onValueChange = { incentivePercent = it }, label = { Text("Incentive Rate %") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = fixedReward, onValueChange = { fixedReward = it }, label = { Text("Bonus ₹") }, modifier = Modifier.weight(1f))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        IncentiveRuleEntity(
                            id = "INC-R$nextVersion-${System.currentTimeMillis().toString().takeLast(4)}",
                            ruleName = ruleName,
                            ruleType = "SALES_TIER",
                            minThresholdPercent = minPercent.toDoubleOrNull() ?: 100.0,
                            maxThresholdPercent = maxPercent.toDoubleOrNull() ?: 200.0,
                            incentivePercent = incentivePercent.toDoubleOrNull() ?: 5.0,
                            fixedRewardAmount = fixedReward.toDoubleOrNull() ?: 5000.0,
                            versionNumber = nextVersion,
                            effectiveFrom = "01-09-2026",
                            effectiveTo = "31-12-2026",
                            status = "ACTIVE",
                            formulaDescription = "$incentivePercent% of sales + ₹$fixedReward achievement bonus"
                        )
                    )
                }
            ) {
                Text("Confirm & Publish v$nextVersion")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
