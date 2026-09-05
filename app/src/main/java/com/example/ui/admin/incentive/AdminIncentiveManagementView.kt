package com.example.ui.admin.incentive

import androidx.compose.animation.*
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
import androidx.compose.material.icons.outlined.*
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
import com.example.core.components.CareOsisStatusChip
import com.example.data.local.entity.IncentiveRecordEntity
import com.example.data.local.entity.IncentiveRuleEntity
import com.example.data.local.entity.RegionEntity
import com.example.data.local.entity.UserAccountEntity
import com.example.ui.dashboard.IncentiveBreakdownModal
import com.example.ui.theme.*

/**
 * CareOsis Admin Incentive Management View
 * Features: Total Liability Overview, Monthly Approval Workflow, and Rule Versioning Engine
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminIncentiveManagementView(
    rules: List<IncentiveRuleEntity>,
    records: List<IncentiveRecordEntity>,
    employees: List<UserAccountEntity>,
    regions: List<RegionEntity>,
    canEdit: Boolean,
    currentAdminId: String,
    currentAdminName: String,
    onSaveRule: (IncentiveRuleEntity, Boolean) -> Unit,
    onDeleteRule: (String) -> Unit,
    onApproveRecord: (String) -> Unit
) {
    var selectedSection by remember { mutableIntStateOf(0) } // 0: Monthly Approvals & Liability, 1: Rule Engine & Versions
    var selectedPeriod by remember { mutableStateOf("August 2026") }
    var showRuleBuilderModal by remember { mutableStateOf(false) }
    var ruleToEdit by remember { mutableStateOf<IncentiveRuleEntity?>(null) }
    var selectedRecordForBreakdown by remember { mutableStateOf<IncentiveRecordEntity?>(null) }

    val periodRecords = remember(records, selectedPeriod) {
        records.filter { it.period.contains(selectedPeriod, ignoreCase = true) }
    }

    // Liability Analytics
    val totalLiability = remember(periodRecords) {
        periodRecords.sumOf { it.finalIncentive }
    }
    val eligibleEmployeesCount = remember(periodRecords) {
        periodRecords.count { it.finalIncentive > 0 }
    }
    val pendingApprovalsCount = remember(periodRecords) {
        periodRecords.count { it.status == "PENDING_APPROVAL" || it.status == "ESTIMATED" }
    }
    val avgIncentive = remember(periodRecords, eligibleEmployeesCount) {
        if (eligibleEmployeesCount > 0) totalLiability / eligibleEmployeesCount else 0.0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Section Tabs (Approvals vs Rules Engine)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedSection == 0,
                onClick = { selectedSection = 0 },
                label = { Text("Monthly Approvals & Liability", fontWeight = FontWeight.Bold) },
                leadingIcon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = selectedSection == 1,
                onClick = { selectedSection = 1 },
                label = { Text("Rules Engine & Versions", fontWeight = FontWeight.Bold) },
                leadingIcon = { Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.weight(1f)
            )
        }

        if (selectedSection == 0) {
            // SECTION 0: Monthly Approvals & Liability Dashboard
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header & Batch Action
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Monthly Incentive Approvals",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Cycle: $selectedPeriod",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }

                        if (pendingApprovalsCount > 0) {
                            Button(
                                onClick = {
                                    periodRecords.filter { it.status != "FINAL" }.forEach {
                                        onApproveRecord(it.id)
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                                modifier = Modifier.testTag("admin_approve_all_incentives_button")
                            ) {
                                Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Approve All ($pendingApprovalsCount)")
                            }
                        }
                    }
                }

                // Liability KPI Cards
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Total Incentive Liability ($selectedPeriod)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "₹${String.format("%,.0f", totalLiability)}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f))
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Eligible MRs", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)))
                                    Text("$eligibleEmployeesCount Reps", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer))
                                }
                                Column {
                                    Text("Avg Payout", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)))
                                    Text("₹${String.format("%,.0f", avgIncentive)}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer))
                                }
                                Column {
                                    Text("Pending Approval", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)))
                                    Text("$pendingApprovalsCount", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = if (pendingApprovalsCount > 0) StatusWarning else StatusSuccess))
                                }
                            }
                        }
                    }
                }

                // Employee Incentive Records List
                item {
                    Text(
                        text = "Field Force Incentive Statements (${periodRecords.size})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                items(periodRecords) { record ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedRecordForBreakdown = record }
                            .testTag("admin_incentive_record_${record.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column {
                                        Text(record.employeeName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                        Text("${record.employeeId} • Target: ₹${String.format("%,.0f", record.target)}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                    }
                                }

                                CareOsisStatusChip(
                                    text = record.status,
                                    containerColor = if (record.status == "FINAL") StatusSuccessContainer else GoldMetallicLight,
                                    contentColor = if (record.status == "FINAL") StatusSuccess else OnGoldContainer
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Sales: ₹${String.format("%,.0f", record.actualSales)} (${String.format("%.1f", record.achievementPercent)}%)",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                    Text(
                                        text = "Rule: ${record.ruleName} (${record.applicableSlab})",
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "₹${String.format("%,.0f", record.finalIncentive)}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (record.status == "FINAL") StatusSuccess else MaterialTheme.colorScheme.primary
                                        )
                                    )

                                    if (record.status != "FINAL") {
                                        IconButton(
                                            onClick = { onApproveRecord(record.id) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.CheckCircle,
                                                contentDescription = "Approve",
                                                tint = StatusSuccess,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
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
        } else {
            // SECTION 1: Rules Engine & Version Management
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header & Create New Rule
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Configurable Incentive Rules (${rules.size})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Live calculation logic applied to MR bookings",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }

                        if (canEdit) {
                            Button(
                                onClick = {
                                    ruleToEdit = null
                                    showRuleBuilderModal = true
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("admin_create_new_rule_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Rule")
                            }
                        }
                    }
                }

                // List of Active and Versioned Rules
                items(rules) { rule ->
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = rule.ruleName,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            text = "v${rule.versionNumber}",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }

                                CareOsisStatusChip(
                                    text = rule.status,
                                    containerColor = if (rule.status == "ACTIVE") StatusSuccessContainer else StatusNeutralContainer,
                                    contentColor = if (rule.status == "ACTIVE") StatusSuccess else StatusNeutral
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Strategy: ${rule.ruleType} • Scope: ${rule.regionId} • Effective: ${rule.effectiveFrom} to ${rule.effectiveTo}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Default Target: ₹${String.format("%,.0f", rule.defaultTarget)} • Priority: Level ${rule.priority}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary, fontSize = 11.sp)
                            )

                            if (canEdit) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            ruleToEdit = rule
                                            showRuleBuilderModal = true
                                        },
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Edit / New Version")
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    IconButton(
                                        onClick = { onDeleteRule(rule.id) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = StatusError, modifier = Modifier.size(18.dp))
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
    }

    // Modal: Admin Incentive Builder
    if (showRuleBuilderModal) {
        AdminIncentiveBuilderModal(
            existingRule = ruleToEdit,
            regions = regions,
            employees = employees,
            currentAdminId = currentAdminId,
            onDismiss = { showRuleBuilderModal = false },
            onSaveRule = { rule, createNewVersion ->
                onSaveRule(rule, createNewVersion)
                showRuleBuilderModal = false
            }
        )
    }

    // Modal: Breakdown Sheet for Selected MR Record
    if (selectedRecordForBreakdown != null) {
        IncentiveBreakdownModal(
            record = selectedRecordForBreakdown,
            onDismiss = { selectedRecordForBreakdown = null },
            onViewHistory = { /* Admin history */ },
            isAdminView = true,
            onApprove = {
                onApproveRecord(selectedRecordForBreakdown!!.id)
                selectedRecordForBreakdown = null
            }
        )
    }
}
