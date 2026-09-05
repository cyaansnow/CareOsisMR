package com.example.ui.admin.incentive

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.core.components.CareOsisStatusChip
import com.example.core.engine.*
import com.example.data.local.entity.IncentiveRuleEntity
import com.example.data.local.entity.RegionEntity
import com.example.data.local.entity.UserAccountEntity
import com.example.ui.theme.*

/**
 * Enterprise Admin Incentive Builder Modal
 * Provides visual slab building, multi-component configurators, live test sandbox,
 * mathematical overlap validation, and version incrementing.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminIncentiveBuilderModal(
    existingRule: IncentiveRuleEntity?,
    regions: List<RegionEntity>,
    employees: List<UserAccountEntity>,
    currentAdminId: String,
    onDismiss: () -> Unit,
    onSaveRule: (IncentiveRuleEntity, Boolean) -> Unit
) {
    val isEdit = existingRule != null

    var ruleName by remember { mutableStateOf(existingRule?.ruleName ?: "Monthly Field Sales Incentive 2026") }
    var ruleType by remember { mutableStateOf(existingRule?.ruleType ?: RuleType.SLAB_BASED.name) }
    var targetSource by remember { mutableStateOf(existingRule?.targetSource ?: TargetSource.TOTAL_SALES.name) }
    var defaultTarget by remember { mutableStateOf((existingRule?.defaultTarget ?: 200000.0).toString()) }
    var targetPriority by remember { mutableStateOf(existingRule?.targetPriority ?: TargetPriority.EMPLOYEE_FIRST.name) }
    var assignmentScope by remember { mutableStateOf(existingRule?.regionId ?: "GLOBAL") }
    var priorityLevel by remember { mutableIntStateOf(existingRule?.priority ?: 4) }
    var effectiveFrom by remember { mutableStateOf(existingRule?.effectiveFrom ?: "01 Aug 2026") }
    var effectiveTo by remember { mutableStateOf(existingRule?.effectiveTo ?: "31 Dec 2026") }

    // Slabs State
    var slabs by remember {
        val initialSlabs = if (existingRule?.slabsJson?.isNotBlank() == true) {
            IncentiveCalculationEngine.parseSlabs(existingRule.slabsJson)
        } else {
            listOf(
                SlabConfig(70.0, 79.99, 2.0, 0.0, "70% - 79.99%"),
                SlabConfig(80.0, 89.99, 3.0, 0.0, "80% - 89.99%"),
                SlabConfig(90.0, 99.99, 4.0, 0.0, "90% - 99.99%"),
                SlabConfig(100.0, 1000.0, 5.0, 2500.0, "100%+ (Bonus)")
            )
        }
        mutableStateOf(initialSlabs)
    }

    // Multi-Component State
    var coverageWeight by remember { mutableStateOf("1000") }
    var coverageMinThreshold by remember { mutableStateOf("80") }
    var newDocBonus by remember { mutableStateOf("250") }
    var newDocMinThreshold by remember { mutableStateOf("5") }
    var collectionBonus by remember { mutableStateOf("1500") }
    var collectionMinThreshold by remember { mutableStateOf("90") }

    // Live Calculation Sandbox State
    var testTarget by remember { mutableStateOf("200000") }
    var testSales by remember { mutableStateOf("180000") }
    var testDocVisits by remember { mutableStateOf("14") }
    var testDocTarget by remember { mutableStateOf("15") }
    var testNewDocs by remember { mutableStateOf("6") }
    var testCollection by remember { mutableStateOf("190000") }

    // Sub-dialogs
    var showAddSlabDialog by remember { mutableStateOf(false) }
    var editingSlabIndex by remember { mutableIntStateOf(-1) }
    var saveAsNewVersionOption by remember { mutableStateOf(isEdit) }

    // Validation
    val validationErrors = remember(ruleName, slabs, ruleType) {
        val errors = mutableListOf<String>()
        if (ruleName.isBlank()) errors.add("Rule Name is mandatory.")
        if (ruleType == RuleType.SLAB_BASED.name) {
            if (slabs.isEmpty()) errors.add("At least one slab is required.")
            // Check overlaps
            val sortedSlabs = slabs.sortedBy { it.minPercent }
            for (i in 0 until sortedSlabs.size - 1) {
                val current = sortedSlabs[i]
                val next = sortedSlabs[i + 1]
                if (current.maxPercent > next.minPercent) {
                    errors.add("Slab overlap detected: [${current.minPercent}%-${current.maxPercent}%] and [${next.minPercent}%-${next.maxPercent}%]")
                }
            }
            for (s in slabs) {
                if (s.minPercent >= s.maxPercent) {
                    errors.add("Invalid range: Min (${s.minPercent}%) >= Max (${s.maxPercent}%)")
                }
            }
        }
        errors
    }

    // Live Simulated Calculation
    val simulatedResult = remember(
        ruleType, targetSource, defaultTarget, targetPriority, slabs,
        coverageWeight, coverageMinThreshold, newDocBonus, newDocMinThreshold, collectionBonus, collectionMinThreshold,
        testTarget, testSales, testDocVisits, testDocTarget, testNewDocs, testCollection
    ) {
        val targetVal = testTarget.toDoubleOrNull() ?: 200000.0
        val salesVal = testSales.toDoubleOrNull() ?: 180000.0
        val visitsDone = testDocVisits.toIntOrNull() ?: 14
        val visitsTarget = testDocTarget.toIntOrNull() ?: 15
        val newDocs = testNewDocs.toIntOrNull() ?: 6
        val colAmt = testCollection.toDoubleOrNull() ?: 190000.0

        val tempRule = IncentiveRuleEntity(
            id = existingRule?.id ?: "TEMP-RULE",
            ruleName = ruleName.ifBlank { "Live Preview Rule" },
            ruleType = ruleType,
            targetSource = targetSource,
            defaultTarget = defaultTarget.toDoubleOrNull() ?: 200000.0,
            targetPriority = targetPriority,
            slabsJson = IncentiveCalculationEngine.serializeSlabConfigs(slabs),
            componentsJson = IncentiveCalculationEngine.serializeComponents(
                listOf(
                    ComponentConfig(
                        name = "Doctor Coverage",
                        type = "DOCTOR_COVERAGE",
                        weightPercent = 0.0,
                        minThresholdPercent = coverageMinThreshold.toDoubleOrNull() ?: 80.0,
                        rewardType = "FIXED_AMOUNT",
                        rewardValue = coverageWeight.toDoubleOrNull() ?: 1000.0
                    ),
                    ComponentConfig(
                        name = "New Doctor Activation",
                        type = "NEW_DOCTOR_ACTIVATION",
                        weightPercent = 0.0,
                        minThresholdPercent = newDocMinThreshold.toDoubleOrNull() ?: 5.0,
                        rewardType = "PER_UNIT_AMOUNT",
                        rewardValue = newDocBonus.toDoubleOrNull() ?: 250.0
                    ),
                    ComponentConfig(
                        name = "Collection Milestone",
                        type = "COLLECTION_TARGET",
                        weightPercent = 0.0,
                        minThresholdPercent = collectionMinThreshold.toDoubleOrNull() ?: 90.0,
                        rewardType = "FIXED_AMOUNT",
                        rewardValue = collectionBonus.toDoubleOrNull() ?: 1500.0
                    )
                )
            ),
            regionId = assignmentScope,
            priority = priorityLevel,
            effectiveFrom = effectiveFrom,
            effectiveTo = effectiveTo,
            versionNumber = existingRule?.versionNumber ?: 1,
            status = "ACTIVE"
        )

        val dummyUser = UserAccountEntity(
            id = "SIM-MR-001",
            name = "Live Test MR",
            email = "mr@careosis.com",
            phone = "+91 99999 00000",
            role = "EMPLOYEE",
            password = "",
            status = "ACTIVE",
            assignedRegionIds = assignmentScope,
            employeeScopeMode = "ALL",
            assignedEmployeeIds = "SELF",
            permissions = "",
            monthlyTarget = targetVal,
            territoryName = "Test Beat"
        )

        val input = CalculationInput(
            employee = dummyUser,
            period = "Simulated Month",
            actualSales = salesVal,
            doctorVisitsDone = visitsDone,
            doctorVisitsTarget = visitsTarget,
            newDoctorsActivated = newDocs,
            collectionAmount = colAmt,
            collectionTarget = targetVal,
            isMonthClosed = false
        )

        IncentiveCalculationEngine.calculateIncentive(input, tempRule)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .testTag("admin_incentive_builder_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Top App Bar inside modal
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
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Tune,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (isEdit) "Configure Incentive Rule (v${existingRule?.versionNumber})" else "Create New Incentive Rule",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Configurable Financial Rules Engine",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Form & Sandbox Body
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Validation Banner
                    if (validationErrors.isNotEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = StatusErrorContainer),
                                border = BorderStroke(1.dp, StatusError.copy(alpha = 0.4f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = StatusError, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Configuration Issues Found", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = StatusError))
                                    }
                                    validationErrors.forEach { err ->
                                        Text("• $err", style = MaterialTheme.typography.bodySmall.copy(color = StatusError, fontSize = 12.sp))
                                    }
                                }
                            }
                        }
                    }

                    // 1. General Rule Settings
                    item {
                        Text("1. Rule Identity & Type", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = ruleName,
                            onValueChange = { ruleName = it },
                            label = { Text("Rule Name *") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Rule Type Selector Chips
                    item {
                        Text("Calculation Strategy:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf<Pair<String, String>>(
                                RuleType.SLAB_BASED.name to "Slab Based",
                                RuleType.PERCENTAGE_OF_SALES.name to "% of Sales",
                                RuleType.MULTI_COMPONENT.name to "Multi-Component",
                                RuleType.FIXED_AMOUNT.name to "Fixed Reward"
                            ).forEach { (typeKey, label) ->
                                FilterChip(
                                    selected = ruleType == typeKey,
                                    onClick = { ruleType = typeKey },
                                    label = { Text(label, fontSize = 12.sp) }
                                )
                            }
                        }
                    }

                    // Target Source & Target Priority
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = defaultTarget,
                                onValueChange = { defaultTarget = it },
                                label = { Text("Default Target (₹)") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = assignmentScope,
                                onValueChange = { assignmentScope = it },
                                label = { Text("Territory Scope") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    // 2. Visual Slab Builder Section (When Slab Based or Multi-Component)
                    if (ruleType == RuleType.SLAB_BASED.name || ruleType == RuleType.PERCENTAGE_OF_SALES.name) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("2. Achievement Slabs (${slabs.size})", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                TextButton(
                                    onClick = {
                                        editingSlabIndex = -1
                                        showAddSlabDialog = true
                                    }
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Slab")
                                }
                            }
                        }

                        itemsIndexed(slabs) { index, slab ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = slab.label.ifBlank { "Tier ${index + 1}" },
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "Range: ${slab.minPercent}% – ${if (slab.maxPercent > 500) "100%+" else "${slab.maxPercent}%"}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        )
                                        Text(
                                            text = "Rate: ${slab.ratePercent}% of sales + ₹${slab.fixedAmount.toInt()} bonus",
                                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary, fontSize = 11.sp)
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = {
                                                editingSlabIndex = index
                                                showAddSlabDialog = true
                                            }
                                        ) {
                                            Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                        }

                                        IconButton(
                                            onClick = {
                                                val mutable = slabs.toMutableList()
                                                mutable.removeAt(index)
                                                slabs = mutable
                                            }
                                        ) {
                                            Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = StatusError, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 3. Multi-Component Configurator (When Multi-Component)
                    if (ruleType == RuleType.MULTI_COMPONENT.name) {
                        item {
                            Text("2. Multi-Component KPI Settings", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("Component 1: Doctor Coverage Milestone", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = coverageMinThreshold,
                                            onValueChange = { coverageMinThreshold = it },
                                            label = { Text("Min Visits %") },
                                            modifier = Modifier.weight(1f)
                                        )
                                        OutlinedTextField(
                                            value = coverageWeight,
                                            onValueChange = { coverageWeight = it },
                                            label = { Text("Fixed Reward (₹)") },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    Divider()

                                    Text("Component 2: New Prescriber Activation", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = newDocMinThreshold,
                                            onValueChange = { newDocMinThreshold = it },
                                            label = { Text("Min New HCPs") },
                                            modifier = Modifier.weight(1f)
                                        )
                                        OutlinedTextField(
                                            value = newDocBonus,
                                            onValueChange = { newDocBonus = it },
                                            label = { Text("Reward / HCP (₹)") },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    Divider()

                                    Text("Component 3: Commercial Payment Collection", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = collectionMinThreshold,
                                            onValueChange = { collectionMinThreshold = it },
                                            label = { Text("Collection %") },
                                            modifier = Modifier.weight(1f)
                                        )
                                        OutlinedTextField(
                                            value = collectionBonus,
                                            onValueChange = { collectionBonus = it },
                                            label = { Text("Reward (₹)") },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 4. Live Interactive Calculation Preview Sandbox
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("live_calculation_sandbox_card"),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.Science, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Text(
                                            text = "Live Calculation Preview Sandbox",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        )
                                    }
                                    CareOsisStatusChip(
                                        text = "REALTIME",
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = ClinicalWhite
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = testSales,
                                        onValueChange = { testSales = it },
                                        label = { Text("Test Sales (₹)") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    OutlinedTextField(
                                        value = testTarget,
                                        onValueChange = { testTarget = it },
                                        label = { Text("Test Target (₹)") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Achievement: ${String.format("%.1f", simulatedResult.achievementPercent)}%",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                        )
                                        Text(
                                            text = "Matched: ${simulatedResult.applicableSlab}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "₹${String.format("%,.0f", simulatedResult.finalIncentive)}",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        )
                                        Text(
                                            text = "Calculated Payout",
                                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Versioning Option if Editing Existing Rule
                    if (isEdit) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Publish as New Version (v${(existingRule?.versionNumber ?: 1) + 1})",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "Preserves historical calculation integrity for past payroll cycles.",
                                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                                        )
                                    }
                                    Switch(
                                        checked = saveAsNewVersionOption,
                                        onCheckedChange = { saveAsNewVersionOption = it }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (validationErrors.isEmpty()) {
                                val finalRule = IncentiveRuleEntity(
                                    id = existingRule?.id ?: "RULE-${ruleType.take(4)}-${System.currentTimeMillis().toString().takeLast(6)}",
                                    ruleName = ruleName.trim(),
                                    ruleType = ruleType,
                                    targetSource = targetSource,
                                    defaultTarget = defaultTarget.toDoubleOrNull() ?: 200000.0,
                                    targetPriority = targetPriority,
                                    slabsJson = IncentiveCalculationEngine.serializeSlabConfigs(slabs),
                                    componentsJson = IncentiveCalculationEngine.serializeComponents(
                                        listOf(
                                            ComponentConfig(
                                                name = "Doctor Coverage",
                                                type = "DOCTOR_COVERAGE",
                                                weightPercent = 0.0,
                                                minThresholdPercent = coverageMinThreshold.toDoubleOrNull() ?: 80.0,
                                                rewardType = "FIXED_AMOUNT",
                                                rewardValue = coverageWeight.toDoubleOrNull() ?: 1000.0
                                            ),
                                            ComponentConfig(
                                                name = "New Doctor Activation",
                                                type = "NEW_DOCTOR_ACTIVATION",
                                                weightPercent = 0.0,
                                                minThresholdPercent = newDocMinThreshold.toDoubleOrNull() ?: 5.0,
                                                rewardType = "PER_UNIT_AMOUNT",
                                                rewardValue = newDocBonus.toDoubleOrNull() ?: 250.0
                                            ),
                                            ComponentConfig(
                                                name = "Collection Milestone",
                                                type = "COLLECTION_TARGET",
                                                weightPercent = 0.0,
                                                minThresholdPercent = collectionMinThreshold.toDoubleOrNull() ?: 90.0,
                                                rewardType = "FIXED_AMOUNT",
                                                rewardValue = collectionBonus.toDoubleOrNull() ?: 1500.0
                                            )
                                        )
                                    ),
                                    regionId = assignmentScope,
                                    priority = priorityLevel,
                                    effectiveFrom = effectiveFrom,
                                    effectiveTo = effectiveTo,
                                    versionNumber = existingRule?.versionNumber ?: 1,
                                    status = "ACTIVE",
                                    updatedAt = System.currentTimeMillis(),
                                    updatedBy = currentAdminId
                                )
                                onSaveRule(finalRule, saveAsNewVersionOption)
                            }
                        },
                        enabled = validationErrors.isEmpty(),
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isEdit) if (saveAsNewVersionOption) "Save Version v${(existingRule?.versionNumber ?: 1) + 1}" else "Update Rule" else "Activate Rule")
                    }
                }
            }
        }
    }

    // Sub-dialog: Add / Edit Slab
    if (showAddSlabDialog) {
        val editingSlab = if (editingSlabIndex in slabs.indices) slabs[editingSlabIndex] else null
        var slabMin by remember { mutableStateOf((editingSlab?.minPercent ?: 100.0).toString()) }
        var slabMax by remember { mutableStateOf((editingSlab?.maxPercent ?: 120.0).toString()) }
        var slabRate by remember { mutableStateOf((editingSlab?.ratePercent ?: 4.0).toString()) }
        var slabFixed by remember { mutableStateOf((editingSlab?.fixedAmount ?: 0.0).toString()) }
        var slabLabel by remember { mutableStateOf(editingSlab?.label ?: "") }

        AlertDialog(
            onDismissRequest = { showAddSlabDialog = false },
            title = { Text(if (editingSlab != null) "Edit Slab Tier" else "Add New Achievement Slab", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = slabMin,
                            onValueChange = { slabMin = it },
                            label = { Text("Min %") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = slabMax,
                            onValueChange = { slabMax = it },
                            label = { Text("Max %") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = slabRate,
                            onValueChange = { slabRate = it },
                            label = { Text("Rate % of Sales") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = slabFixed,
                            onValueChange = { slabFixed = it },
                            label = { Text("Fixed Bonus (₹)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = slabLabel,
                        onValueChange = { slabLabel = it },
                        label = { Text("Slab Label (e.g. 100%+ Bonus)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val min = slabMin.toDoubleOrNull() ?: 0.0
                        val max = slabMax.toDoubleOrNull() ?: 100.0
                        val rate = slabRate.toDoubleOrNull() ?: 0.0
                        val fixed = slabFixed.toDoubleOrNull() ?: 0.0
                        val label = slabLabel.ifBlank { "$min% - $max%" }

                        val newSlab = SlabConfig(min, max, rate, fixed, label)
                        val mutable = slabs.toMutableList()
                        if (editingSlabIndex in mutable.indices) {
                            mutable[editingSlabIndex] = newSlab
                        } else {
                            mutable.add(newSlab)
                        }
                        slabs = mutable.sortedBy { it.minPercent }
                        showAddSlabDialog = false
                    }
                ) {
                    Text("Done")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSlabDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
