package com.example.ui.commercial.expense

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
import com.example.data.local.entity.ExpenseEntity
import com.example.ui.navigation.Destinations
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseManagementScreen(
    onNavigate: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val scope = rememberCoroutineScope()
    val expenses by repository.getAllExpenses().collectAsStateWithLifecycle(initialValue = emptyList())

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedStatusFilter by remember { mutableStateOf("All") }
    var selectedChartCategory by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var expenseToDelete by remember { mutableStateOf<ExpenseEntity?>(null) }

    // Summary calculations
    val totalExpenseSum = remember(expenses) { expenses.sumOf { it.amount } }
    val approvedSum = remember(expenses) { expenses.filter { it.status == "Approved" }.sumOf { it.amount } }
    val pendingSum = remember(expenses) { expenses.filter { it.status == "Submitted" || it.status == "Pending" }.sumOf { it.amount } }
    val categoryBreakdown = remember(expenses) { calculateCategorySpending(expenses) }

    val filteredExpenses = remember(expenses, selectedStatusFilter, selectedChartCategory, searchQuery) {
        expenses.filter { exp ->
            val matchesStatus = when (selectedStatusFilter) {
                "Approved" -> exp.status == "Approved"
                "Pending" -> exp.status == "Submitted" || exp.status == "Pending"
                "Draft" -> exp.status == "Draft"
                "Rejected" -> exp.status == "Rejected"
                else -> true
            }
            val matchesCategory = selectedChartCategory == null || exp.category.equals(selectedChartCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isEmpty() ||
                    exp.category.contains(searchQuery, ignoreCase = true) ||
                    exp.location.contains(searchQuery, ignoreCase = true) ||
                    exp.description.contains(searchQuery, ignoreCase = true)

            matchesStatus && matchesCategory && matchesSearch
        }
    }

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "Field Expenses",
                subtitle = "₹${totalExpenseSum.toInt()} Total Claims Logged",
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavigate(Destinations.ADD_EXPENSE) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.PostAdd, contentDescription = null) },
                text = { Text("Log Expense", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("add_expense_fab")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            // Segmented Navigation Tabs
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)) }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PieChart, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Summary & Charts", fontWeight = FontWeight.SemiBold)
                        }
                    },
                    modifier = Modifier.testTag("expense_summary_tab")
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Claims (${expenses.size})", fontWeight = FontWeight.SemiBold)
                        }
                    },
                    modifier = Modifier.testTag("expense_claims_tab")
                )
            }

            if (expenses.isEmpty()) {
                CareOsisEmptyState(
                    title = "No Field Expenses Logged",
                    description = "Log your daily route fuel, meals, parking, and transit expenses for automated HQ reimbursement.",
                    actionButtonText = "Log First Expense",
                    onActionClick = { onNavigate(Destinations.ADD_EXPENSE) },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                when (selectedTabIndex) {
                    0 -> {
                        // SUMMARY VIEW (RECHARTS VISUALIZATION)
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            // Top KPI Metrics
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    ExpenseMetricCard(
                                        title = "Approved",
                                        amount = "₹${approvedSum.toInt()}",
                                        count = "${expenses.count { it.status == "Approved" }} Claims",
                                        accentColor = StatusSuccess,
                                        icon = Icons.Default.CheckCircle,
                                        modifier = Modifier.weight(1f)
                                    )
                                    ExpenseMetricCard(
                                        title = "Pending Approval",
                                        amount = "₹${pendingSum.toInt()}",
                                        count = "${expenses.count { it.status == "Submitted" || it.status == "Pending" }} Claims",
                                        accentColor = StatusWarning,
                                        icon = Icons.Default.HourglassTop,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            // 1. Interactive Recharts Category Spending Donut Chart
                            item {
                                RechartsCategoryDonutChart(
                                    categoryData = categoryBreakdown,
                                    totalAmount = totalExpenseSum,
                                    selectedCategory = selectedChartCategory,
                                    onCategorySelected = { selectedChartCategory = it }
                                )
                            }

                            // 2. Policy Utilization Progress Card
                            item {
                                ExpensePolicyUtilizationCard(
                                    totalSpent = totalExpenseSum,
                                    monthlyBudget = 15000.0
                                )
                            }

                            // 3. Daily Spending Trend Bar Chart
                            item {
                                RechartsDailyTrendBarChart(expenses = expenses)
                            }

                            // 4. Itemized Category Breakdown Cards
                            item {
                                Text(
                                    text = "Detailed Category Breakdown",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }

                            items(categoryBreakdown) { item ->
                                CategoryBreakdownDetailCard(
                                    item = item,
                                    isSelected = selectedChartCategory == item.category,
                                    onClick = {
                                        selectedChartCategory = if (selectedChartCategory == item.category) null else item.category
                                    }
                                )
                            }
                        }
                    }

                    1 -> {
                        // CLAIMS & AUDIT TRAIL VIEW
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            // Search bar
                            item {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder = { Text("Search by category, route or notes...") },
                                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                    trailingIcon = {
                                        if (searchQuery.isNotEmpty()) {
                                            IconButton(onClick = { searchQuery = "" }) {
                                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("expense_search_input"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                        focusedContainerColor = MaterialTheme.colorScheme.surface
                                    )
                                )
                            }

                            // Status Filters
                            item {
                                val filters = listOf("All", "Approved", "Pending", "Draft")
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(filters) { f ->
                                        FilterChip(
                                            selected = selectedStatusFilter == f,
                                            onClick = { selectedStatusFilter = f },
                                            label = { Text(f) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        )
                                    }
                                }
                            }

                            if (filteredExpenses.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 40.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No matching expense claims found",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            } else {
                                items(filteredExpenses, key = { it.id }) { expense ->
                                    ExpenseClaimItemCard(
                                        expense = expense,
                                        onDelete = { expenseToDelete = expense }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Delete Confirmation Dialog
        if (expenseToDelete != null) {
            AlertDialog(
                onDismissRequest = { expenseToDelete = null },
                icon = { Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                title = { Text("Delete Expense Claim?") },
                text = {
                    Text("Are you sure you want to delete claim #${expenseToDelete?.id} (${expenseToDelete?.category} - ₹${expenseToDelete?.amount?.toInt()})?")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val id = expenseToDelete?.id ?: ""
                            scope.launch {
                                repository.deleteExpense(id)
                                expenseToDelete = null
                            }
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { expenseToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

/**
 * Top KPI Metric Card
 */
@Composable
private fun ExpenseMetricCard(
    title: String,
    amount: String,
    count: String,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = amount,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = count,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            )
        }
    }
}

/**
 * Category Breakdown Detail Row Card
 */
@Composable
private fun CategoryBreakdownDetailCard(
    item: CategorySpendingData,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) item.color.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) item.color else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(item.color)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = item.category,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Text(
                    text = "₹${item.totalAmount.toInt()}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Percentage Bar
            LinearProgressIndicator(
                progress = { (item.percentage / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = item.color,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${item.count} claims logged",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = "${"%.1f".format(item.percentage)}% of total",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

/**
 * Itemized Expense Claim Card
 */
@Composable
private fun ExpenseClaimItemCard(
    expense: ExpenseEntity,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(getCategoryColor(expense.category))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = expense.category,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                CareOsisStatusChip(
                    text = expense.status,
                    containerColor = when (expense.status) {
                        "Approved" -> StatusSuccessContainer
                        "Draft" -> MaterialTheme.colorScheme.surfaceVariant
                        "Rejected" -> StatusErrorContainer
                        else -> StatusWarningContainer
                    },
                    contentColor = when (expense.status) {
                        "Approved" -> StatusSuccess
                        "Draft" -> MaterialTheme.colorScheme.onSurfaceVariant
                        "Rejected" -> StatusError
                        else -> StatusWarning
                    }
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = expense.date,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                if (expense.location.isNotEmpty()) {
                    Text(text = " • ", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = expense.location,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (expense.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = expense.description,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                )
            }

            if (expense.receiptPath.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Receipt attached",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Receipt Verified (${expense.receiptPath})",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Claim Amount",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "₹${expense.amount.toInt()}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                if (expense.status == "Draft" || expense.status == "Submitted") {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
