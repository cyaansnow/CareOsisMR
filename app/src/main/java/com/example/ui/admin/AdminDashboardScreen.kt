package com.example.ui.admin

import androidx.compose.animation.*
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
import androidx.compose.material.icons.outlined.*
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
import com.example.core.components.*
import com.example.data.local.entity.*
import com.example.ui.navigation.Destinations
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit = {},
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val scope = rememberCoroutineScope()

    // Current Admin User
    val currentAdmin by repository.currentUser.collectAsStateWithLifecycle(initialValue = null)
    val assignedRegionIds = remember(currentAdmin) {
        currentAdmin?.assignedRegionIds?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: listOf("DELHI_NCR")
    }

    // Local & Cloud Data Streams
    val allOrders by repository.getAllOrders().collectAsStateWithLifecycle(initialValue = emptyList())
    val allDoctors by repository.getAllDoctors().collectAsStateWithLifecycle(initialValue = emptyList())
    val allVisits by repository.getAllVisits().collectAsStateWithLifecycle(initialValue = emptyList())
    val allExpenses by repository.getAllExpenses().collectAsStateWithLifecycle(initialValue = emptyList())
    val allAttendance by repository.getAllAttendance().collectAsStateWithLifecycle(initialValue = emptyList())
    val allEmployees by repository.getAllEmployees().collectAsStateWithLifecycle(initialValue = emptyList())
    val allRegions by repository.getAllRegions().collectAsStateWithLifecycle(initialValue = emptyList())
    val incentiveRules by repository.getActiveIncentiveRules().collectAsStateWithLifecycle(initialValue = emptyList())
    val incentiveRecords by repository.getAllIncentiveRecords().collectAsStateWithLifecycle(initialValue = emptyList())
    val salaryRule by repository.getActiveSalaryRule().collectAsStateWithLifecycle(initialValue = null)

    var selectedRegionId by remember(assignedRegionIds) {
        mutableStateOf(if (assignedRegionIds.contains("GLOBAL")) "ALL" else assignedRegionIds.firstOrNull() ?: "ALL")
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    var isCloudSyncing by remember { mutableStateOf(false) }
    var syncMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showApproveOrderDialog by remember { mutableStateOf<OrderEntity?>(null) }
    var showApproveExpenseDialog by remember { mutableStateOf<ExpenseEntity?>(null) }
    var showCreateEmployeeDialog by remember { mutableStateOf(false) }
    var showEditIncentiveDialog by remember { mutableStateOf(false) }

    val tabs = listOf(
        "Overview & KPIs",
        "Field MR Force",
        "Prescriber CRM",
        "Commercial Orders",
        "Expense Claims",
        "Incentive & Salary"
    )

    // Filtered data based on Admin's assigned regions
    val myRegions = remember(allRegions, assignedRegionIds) {
        if (assignedRegionIds.contains("GLOBAL")) allRegions
        else allRegions.filter { assignedRegionIds.contains(it.id) }
    }

    val filteredEmployees = remember(allEmployees, selectedRegionId, assignedRegionIds, currentAdmin) {
        allEmployees.filter { emp ->
            val matchesRegion = if (selectedRegionId == "ALL") {
                assignedRegionIds.contains("GLOBAL") || assignedRegionIds.any { emp.assignedRegionIds.contains(it) }
            } else {
                emp.assignedRegionIds.contains(selectedRegionId)
            }

            val matchesScope = if (currentAdmin?.employeeScopeMode == "SPECIFIC_EMPLOYEES") {
                val allowedIds = currentAdmin?.assignedEmployeeIds?.split(",")?.map { it.trim() } ?: emptyList()
                allowedIds.contains(emp.id)
            } else true

            matchesRegion && matchesScope
        }
    }

    val filteredOrders = remember(allOrders, searchQuery) {
        if (searchQuery.isBlank()) allOrders
        else allOrders.filter { it.customerName.contains(searchQuery, ignoreCase = true) || it.id.contains(searchQuery, ignoreCase = true) }
    }

    val totalRevenue = remember(allOrders) { allOrders.sumOf { it.totalAmount } }
    val pendingOrders = remember(allOrders) { allOrders.filter { it.status == "Submitted" || it.status == "Pending" } }
    val pendingExpenses = remember(allExpenses) { allExpenses.filter { it.status == "Submitted" || it.status == "Pending" } }

    val canCreateEmp = currentAdmin?.canCreateEmployees ?: true
    val canEditIncentives = currentAdmin?.permissions?.contains("EDIT_INCENTIVE_RULES") ?: true

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "Regional Operations Center",
                subtitle = "${currentAdmin?.name ?: "Regional Director"} • ${currentAdmin?.territoryName ?: "CareOsis Territory"}",
                onBackClick = onBackClick,
                actions = {
                    IconButton(
                        onClick = {
                            isCloudSyncing = true
                            scope.launch {
                                delay(500)
                                repository.performSync()
                                isCloudSyncing = false
                                syncMessage = "Cloud Sync Verified: Aggregates pushed to Firebase"
                            }
                        }
                    ) {
                        if (isCloudSyncing) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary)
                        } else {
                            Icon(Icons.Default.CloudSync, contentDescription = "Sync Firebase", tint = MaterialTheme.colorScheme.primary)
                        }
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
            // Region Filter Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Text(
                        text = "Assigned Operating Territory",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = selectedRegionId == "ALL",
                                onClick = { selectedRegionId = "ALL" },
                                label = { Text("All My Zones (${myRegions.size})") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                        items(myRegions) { reg ->
                            FilterChip(
                                selected = selectedRegionId == reg.id,
                                onClick = { selectedRegionId = reg.id },
                                label = { Text(reg.name) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Sync Notification Toast
            AnimatedVisibility(visible = syncMessage != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = StatusSuccessContainer
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudDone, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = syncMessage ?: "", style = MaterialTheme.typography.bodySmall.copy(color = StatusSuccess, fontWeight = FontWeight.SemiBold))
                        }
                        IconButton(onClick = { syncMessage = null }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = StatusSuccess, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            // Tab Row
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

            // Tab Content
            when (selectedTab) {
                0 -> RegionalOverviewTab(
                    employeesCount = filteredEmployees.size,
                    doctorsCount = allDoctors.size,
                    totalSales = totalRevenue,
                    pendingOrdersCount = pendingOrders.size,
                    pendingExpensesCount = pendingExpenses.size,
                    employees = filteredEmployees,
                    onOpenTab = { selectedTab = it },
                    onCreateEmployee = { showCreateEmployeeDialog = true },
                    canCreateEmp = canCreateEmp
                )
                1 -> RegionalMRForceTab(
                    employees = filteredEmployees,
                    canCreateEmp = canCreateEmp,
                    onCreateEmployee = { showCreateEmployeeDialog = true }
                )
                2 -> PrescriberCRMTab(
                    doctors = allDoctors,
                    onNavigate = onNavigate
                )
                3 -> RegionalOrdersTab(
                    orders = filteredOrders,
                    onApproveOrder = { showApproveOrderDialog = it }
                )
                4 -> RegionalExpensesTab(
                    expenses = allExpenses,
                    onApproveExpense = { showApproveExpenseDialog = it }
                )
                5 -> com.example.ui.admin.incentive.AdminIncentiveManagementView(
                    rules = incentiveRules,
                    records = incentiveRecords,
                    employees = filteredEmployees,
                    regions = allRegions,
                    canEdit = canEditIncentives,
                    currentAdminId = currentAdmin?.id ?: "CO-ADM-101",
                    currentAdminName = currentAdmin?.name ?: "Regional Admin",
                    onSaveRule = { rule, createNewVersion ->
                        scope.launch {
                            repository.saveIncentiveRule(rule, currentAdmin?.id ?: "CO-ADM-101", createNewVersion)
                            syncMessage = if (createNewVersion) "New incentive rule v${rule.versionNumber} published!" else "Incentive rule updated!"
                        }
                    },
                    onDeleteRule = { ruleId ->
                        scope.launch {
                            repository.deleteIncentiveRule(ruleId, currentAdmin?.id ?: "CO-ADM-101")
                            syncMessage = "Incentive rule removed."
                        }
                    },
                    onApproveRecord = { recordId ->
                        scope.launch {
                            repository.approveIncentiveRecord(
                                recordId = recordId,
                                approverId = currentAdmin?.id ?: "CO-ADM-101",
                                approverName = currentAdmin?.name ?: "Regional Admin"
                            )
                            syncMessage = "Incentive finalized & approved as FINAL!"
                        }
                    }
                )
            }
        }
    }

    // Order Approval Dialog
    if (showApproveOrderDialog != null) {
        val ord = showApproveOrderDialog!!
        AlertDialog(
            onDismissRequest = { showApproveOrderDialog = null },
            title = { Text("Approve Order #${ord.id}", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Customer: ${ord.customerName} (${ord.customerType})")
                    Text("Order Value: ₹${String.format("%,.0f", ord.totalAmount)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Summary: ${ord.itemsSummary}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                    Text("Approve this commercial order to release stock from the regional CFA warehouse for immediate fulfillment.")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            repository.updateOrderStatus(ord.id, "Approved")
                            repository.performSync()
                            showApproveOrderDialog = null
                            syncMessage = "Order #${ord.id} approved and dispatched to CFA."
                        }
                    }
                ) {
                    Text("Approve & Authorize")
                }
            },
            dismissButton = {
                TextButton(onClick = { showApproveOrderDialog = null }) { Text("Cancel") }
            }
        )
    }

    // Expense Approval Dialog
    if (showApproveExpenseDialog != null) {
        val exp = showApproveExpenseDialog!!
        AlertDialog(
            onDismissRequest = { showApproveExpenseDialog = null },
            title = { Text("Approve Expense Claim #${exp.id}", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Category: ${exp.category} • Date: ${exp.date}")
                    Text("Claim Amount: ₹${String.format("%,.0f", exp.amount)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Location / Route: ${exp.location.ifEmpty { "Field Territory" }}")
                    Text("Remarks: ${exp.description}")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            repository.updateExpenseStatus(exp.id, "Approved")
                            repository.performSync()
                            showApproveExpenseDialog = null
                            syncMessage = "Expense Claim #${exp.id} approved for payroll payout."
                        }
                    }
                ) {
                    Text("Approve Expense")
                }
            },
            dismissButton = {
                TextButton(onClick = { showApproveExpenseDialog = null }) { Text("Cancel") }
            }
        )
    }

    // Create Employee Dialog (Regional Admin)
    if (showCreateEmployeeDialog) {
        CreateEmployeeModal(
            myRegions = myRegions,
            currentAdminId = currentAdmin?.id ?: "CO-ADM-101",
            onDismiss = { showCreateEmployeeDialog = false },
            onConfirm = { newEmp ->
                scope.launch {
                    repository.createUser(newEmp, currentAdmin?.id ?: "CO-ADM-101")
                    showCreateEmployeeDialog = false
                    syncMessage = "Representative ${newEmp.name} (${newEmp.id}) created and deployed."
                }
            }
        )
    }

    // Incentive Formula Editor Dialog (Regional Admin)
    if (showEditIncentiveDialog) {
        RegionalIncentiveEditorModal(
            activeRules = incentiveRules,
            onDismiss = { showEditIncentiveDialog = false },
            onSaveRule = { updatedRule ->
                scope.launch {
                    repository.publishNewIncentiveRule(updatedRule, currentAdmin?.id ?: "CO-ADM-101")
                    showEditIncentiveDialog = false
                    syncMessage = "Updated incentive formula for ${updatedRule.ruleName} published."
                }
            }
        )
    }
}

// -------------------------------------------------------------
// TAB 1: Regional Overview & KPIs
// -------------------------------------------------------------
@Composable
private fun RegionalOverviewTab(
    employeesCount: Int,
    doctorsCount: Int,
    totalSales: Double,
    pendingOrdersCount: Int,
    pendingExpensesCount: Int,
    employees: List<UserAccountEntity>,
    onOpenTab: (Int) -> Unit,
    onCreateEmployee: () -> Unit,
    canCreateEmp: Boolean
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
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiCard(
                    title = "Field MRs",
                    value = "$employeesCount Active",
                    subtitle = "100% Present",
                    icon = Icons.Default.Groups,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    onClick = { onOpenTab(1) }
                )
                KpiCard(
                    title = "Prescribers",
                    value = "$doctorsCount Doctors",
                    subtitle = "Covered Zone",
                    icon = Icons.Default.MedicalServices,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f),
                    onClick = { onOpenTab(2) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiCard(
                    title = "Territory Booking",
                    value = "₹${String.format("%,.0f", totalSales)}",
                    subtitle = "$pendingOrdersCount Pending CFA",
                    icon = Icons.Default.ShoppingCart,
                    color = StatusSuccess,
                    modifier = Modifier.weight(1f),
                    onClick = { onOpenTab(3) }
                )
                KpiCard(
                    title = "Pending Expenses",
                    value = "$pendingExpensesCount Claims",
                    subtitle = "Needs Approval",
                    icon = Icons.Default.ReceiptLong,
                    color = StatusWarning,
                    modifier = Modifier.weight(1f),
                    onClick = { onOpenTab(4) }
                )
            }
        }

        // Action card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Territory Field Actions",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (canCreateEmp) {
                            Button(
                                onClick = onCreateEmployee,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("admin_add_mr_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Onboard MR")
                            }
                        }
                        OutlinedButton(
                            onClick = { onOpenTab(5) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Incentive Logic")
                        }
                    }
                }
            }
        }

        // Active Reps List
        item {
            Text(
                text = "Field Force Roster & Realtime Performance",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(employees) { emp ->
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = emp.name,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${emp.id} • ${emp.territoryName}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Text(
                                text = "Monthly Target: ₹${String.format("%,.0f", emp.monthlyTarget)}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary, fontSize = 11.sp)
                            )
                        }
                    }
                    CareOsisStatusChip(
                        text = "ON FIELD",
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

// -------------------------------------------------------------
// TAB 2: Field MR Force
// -------------------------------------------------------------
@Composable
private fun RegionalMRForceTab(
    employees: List<UserAccountEntity>,
    canCreateEmp: Boolean,
    onCreateEmployee: () -> Unit
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
                        text = "Territory Representatives (${employees.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Manage and monitor field staff in your assigned zone.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
                if (canCreateEmp) {
                    Button(
                        onClick = onCreateEmployee,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add MR", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        items(employees) { emp ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
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
                                text = emp.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${emp.id} • ${emp.designation}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                        CareOsisStatusChip(
                            text = emp.status,
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
                            Text("Mobile", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            Text(emp.phone, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                        }
                        Column {
                            Text("Region", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            Text(emp.assignedRegionIds, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                        }
                        Column {
                            Text("Target", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            Text("₹${String.format("%,.0f", emp.monthlyTarget)}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
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
// TAB 3: Prescriber CRM
// -------------------------------------------------------------
@Composable
private fun PrescriberCRMTab(
    doctors: List<DoctorEntity>,
    onNavigate: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Prescribing HCPs in Controlled Territory (${doctors.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(doctors) { doc ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate("doctor_detail/${doc.id}") },
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
                    Column {
                        Text(doc.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text("${doc.specialty} • ${doc.clinicHospital}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        Text("Category ${doc.potentialCategory} • Priority: ${doc.priority} • Last Visited: ${doc.lastVisitDate.ifEmpty { "Pending" }}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary, fontSize = 11.sp))
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// -------------------------------------------------------------
// TAB 4: Commercial Orders
// -------------------------------------------------------------
@Composable
private fun RegionalOrdersTab(
    orders: List<OrderEntity>,
    onApproveOrder: (OrderEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Commercial Stockist & Retailer Orders (${orders.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(orders) { ord ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
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
                            Text("Order #${ord.id}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text("${ord.customerName} (${ord.customerType})", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        }
                        CareOsisStatusChip(
                            text = ord.status,
                            containerColor = if (ord.status == "Approved" || ord.status == "Dispatched") StatusSuccessContainer else StatusWarningContainer,
                            contentColor = if (ord.status == "Approved" || ord.status == "Dispatched") StatusSuccess else StatusWarning
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Products: ${ord.itemsSummary}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface))
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Net Amount: ₹${String.format("%,.0f", ord.totalAmount)}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        )

                        if (ord.status == "Submitted" || ord.status == "Pending") {
                            Button(
                                onClick = { onApproveOrder(ord) },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Approve Order")
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

// -------------------------------------------------------------
// TAB 5: Expense Claims
// -------------------------------------------------------------
@Composable
private fun RegionalExpensesTab(
    expenses: List<ExpenseEntity>,
    onApproveExpense: (ExpenseEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "MR Field Expense Claims (${expenses.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(expenses) { exp ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
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
                            Text(exp.category, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text("${exp.date} • ${exp.location.ifEmpty { "Field Territory" }}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        }
                        CareOsisStatusChip(
                            text = exp.status,
                            containerColor = if (exp.status == "Approved") StatusSuccessContainer else StatusWarningContainer,
                            contentColor = if (exp.status == "Approved") StatusSuccess else StatusWarning
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(exp.description, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Claim: ₹${String.format("%,.0f", exp.amount)}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        )

                        if (exp.status == "Submitted" || exp.status == "Pending") {
                            Button(
                                onClick = { onApproveExpense(exp) },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Approve Claim")
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

// -------------------------------------------------------------
// TAB 6: Incentive & Salary Logic
// -------------------------------------------------------------
@Composable
private fun RegionalIncentiveAndSalaryTab(
    rules: List<IncentiveRuleEntity>,
    salaryRule: SalaryRuleEntity?,
    canEdit: Boolean,
    employees: List<UserAccountEntity>,
    onOpenEditor: () -> Unit
) {
    var sampleSalesInput by remember { mutableStateOf("180000") }
    var sampleTargetInput by remember { mutableStateOf("200000") }

    val salesVal = sampleSalesInput.toDoubleOrNull() ?: 0.0
    val targetVal = sampleTargetInput.toDoubleOrNull() ?: 200000.0
    val achievementPercent = if (targetVal > 0) (salesVal / targetVal) * 100.0 else 0.0

    val matchingRule = rules.firstOrNull { rule ->
        achievementPercent >= rule.minThresholdPercent && achievementPercent < rule.maxThresholdPercent
    } ?: rules.lastOrNull()

    val calculatedIncentive = if (matchingRule != null) {
        (salesVal * (matchingRule.incentivePercent / 100.0)) + matchingRule.fixedRewardAmount
    } else 0.0

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
                        text = "Regional Incentive & Salary Rules",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Live calculation logic applied to MR bookings and payouts.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
                if (canEdit) {
                    Button(
                        onClick = onOpenEditor,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Customize Rules", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        // Incentive Simulation Calculator Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Interactive Payout Simulator",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = sampleSalesInput,
                            onValueChange = { sampleSalesInput = it },
                            label = { Text("Simulated Sales (₹)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = sampleTargetInput,
                            onValueChange = { sampleTargetInput = it },
                            label = { Text("Monthly Target (₹)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Achievement: ${String.format("%.1f", achievementPercent)}%",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "Applied Tier: ${matchingRule?.ruleName ?: "Standard"}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "₹${String.format("%,.0f", calculatedIncentive)}",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                            Text(
                                text = "Calculated Payout",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                            )
                        }
                    }
                }
            }
        }

        // Active Rules Breakdown
        item {
            Text(
                text = "Active Tier Rules for Your Region",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(rules) { rule ->
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
                        Text(rule.ruleName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text("Range: ${rule.minThresholdPercent.toInt()}% - ${if (rule.maxThresholdPercent > 200) "100%+" else "${rule.maxThresholdPercent.toInt()}%"}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        Text(rule.formulaDescription, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary, fontSize = 11.sp))
                    }
                    Text(
                        text = "${rule.incentivePercent}%",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(subtitle, style = MaterialTheme.typography.bodySmall.copy(color = color, fontSize = 11.sp))
        }
    }
}

// -------------------------------------------------------------
// MODALS
// -------------------------------------------------------------

@Composable
private fun CreateEmployeeModal(
    myRegions: List<RegionEntity>,
    currentAdminId: String,
    onDismiss: () -> Unit,
    onConfirm: (UserAccountEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var empId by remember { mutableStateOf("CO-MR-${(8000..8999).random()}") }
    var phone by remember { mutableStateOf("") }
    var territory by remember { mutableStateOf("Connaught Place & Central Zone") }
    var target by remember { mutableStateOf("200000") }
    var baseSalary by remember { mutableStateOf("35000") }
    var selectedRegion by remember { mutableStateOf(myRegions.firstOrNull()?.id ?: "DELHI_NCR") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Onboard Medical Representative", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item { OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("MR Full Name *") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(value = empId, onValueChange = { empId = it }, label = { Text("Employee ID *") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Contact Number *") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(value = territory, onValueChange = { territory = it }, label = { Text("Assigned Territory / Beat") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(value = target, onValueChange = { target = it }, label = { Text("Monthly Sales Target (₹)") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(value = baseSalary, onValueChange = { baseSalary = it }, label = { Text("Fixed Base Salary (₹)") }, modifier = Modifier.fillMaxWidth()) }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val newEmp = UserAccountEntity(
                            id = empId.trim(),
                            name = name.trim(),
                            email = "${empId.lowercase()}@careosis.com",
                            phone = phone.ifBlank { "+91 98765 00000" },
                            role = "EMPLOYEE",
                            password = "CareOsis@2026",
                            status = "ACTIVE",
                            assignedRegionIds = selectedRegion,
                            employeeScopeMode = "ALL_IN_REGION",
                            assignedEmployeeIds = "SELF",
                            permissions = "FIELD_OPERATIONS,LOG_VISITS,SUBMIT_ORDERS,CLAIM_EXPENSES,VIEW_TRAINING",
                            baseSalary = baseSalary.toDoubleOrNull() ?: 35000.0,
                            fixedAllowance = 8000.0,
                            travelAllowance = 5000.0,
                            otherAllowance = 2000.0,
                            deductions = 1500.0,
                            monthlyTarget = target.toDoubleOrNull() ?: 200000.0,
                            reportingAdminId = currentAdminId,
                            designation = "Medical Representative",
                            territoryName = territory,
                            joiningDate = "20 Aug 2026"
                        )
                        onConfirm(newEmp)
                    }
                }
            ) {
                Text("Deploy Representative")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun RegionalIncentiveEditorModal(
    activeRules: List<IncentiveRuleEntity>,
    onDismiss: () -> Unit,
    onSaveRule: (IncentiveRuleEntity) -> Unit
) {
    var selectedRule by remember { mutableStateOf(activeRules.firstOrNull()) }
    var percentageRate by remember(selectedRule) { mutableStateOf((selectedRule?.incentivePercent ?: 3.0).toString()) }
    var fixedReward by remember(selectedRule) { mutableStateOf((selectedRule?.fixedRewardAmount ?: 2500.0).toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Customize Regional Incentive Formula", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Select Tier to Adjust:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(activeRules) { rule ->
                        FilterChip(
                            selected = selectedRule?.id == rule.id,
                            onClick = { selectedRule = rule },
                            label = { Text(rule.ruleName) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = percentageRate,
                    onValueChange = { percentageRate = it },
                    label = { Text("Incentive Rate %") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = fixedReward,
                    onValueChange = { fixedReward = it },
                    label = { Text("Fixed Reward Bonus (₹)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedRule != null) {
                        val rate = percentageRate.toDoubleOrNull() ?: selectedRule!!.incentivePercent
                        val fixed = fixedReward.toDoubleOrNull() ?: selectedRule!!.fixedRewardAmount
                        val updated = selectedRule!!.copy(
                            id = "INC-${selectedRule!!.id}-${System.currentTimeMillis().toString().takeLast(3)}",
                            incentivePercent = rate,
                            fixedRewardAmount = fixed,
                            versionNumber = selectedRule!!.versionNumber + 1,
                            formulaDescription = "$rate% of sales + ₹${fixed.toInt()} achievement bonus",
                            status = "ACTIVE"
                        )
                        onSaveRule(updated)
                    }
                }
            ) {
                Text("Apply & Publish")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
