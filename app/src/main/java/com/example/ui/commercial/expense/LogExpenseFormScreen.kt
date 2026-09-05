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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.CareOsisApp
import com.example.core.components.*
import com.example.data.local.entity.ExpenseEntity
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Category Model with icon and default policy guideline
 */
data class ExpenseCategoryInfo(
    val name: String,
    val icon: ImageVector,
    val guideline: String,
    val maxLimitHint: String
)

val EXPENSE_CATEGORIES = listOf(
    ExpenseCategoryInfo("Fuel", Icons.Default.LocalGasStation, "₹3.50/km (Bike) • ₹8.00/km (Car)", "Odometer Bill / Fuel Receipt"),
    ExpenseCategoryInfo("Travel", Icons.Default.DirectionsTransit, "Bus, Metro, Auto or Intercity Rail", "Actual Ticket or Transit Slip"),
    ExpenseCategoryInfo("Food", Icons.Default.Restaurant, "Daily Lunch Allowance Cap: ₹250", "GST Bill / Refreshment Slip"),
    ExpenseCategoryInfo("Hotel", Icons.Default.Hotel, "HQ Prior Approval Required (>₹1500)", "Hotel Invoice & Checkout Bill"),
    ExpenseCategoryInfo("Parking", Icons.Default.LocalParking, "Hospital / Clinic Parking & Toll", "Parking Token / Toll Slip"),
    ExpenseCategoryInfo("Doctor Engagement", Icons.Default.LocalHospital, "CME / Doctor Discussion Refreshment", "Itemized Hospitality Slip"),
    ExpenseCategoryInfo("Other", Icons.Default.ReceiptLong, "Printing, Courier, Stationary", "Actual Bill Copy")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogExpenseFormScreen(
    onExpenseAdded: () -> Unit,
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val scope = rememberCoroutineScope()
    val doctors by repository.getAllDoctors().collectAsStateWithLifecycle(initialValue = emptyList())

    val todayFormatted = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()) }
    val calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
    val yesterdayFormatted = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(calendar.time) }

    var selectedDate by remember { mutableStateOf(todayFormatted) }
    var selectedCategory by remember { mutableStateOf("Fuel") }
    var amountText by remember { mutableStateOf("450") }
    var location by remember { mutableStateOf("Rohini Sector 9 & Pitampura Circuit") }
    var description by remember { mutableStateOf("") }
    var associatedDoctor by remember { mutableStateOf("None") }
    var paymentMode by remember { mutableStateOf("UPI / Digital") }
    var receiptFileName by remember { mutableStateOf<String?>("fuel_slip_rohinistation.jpg") }
    var isSaving by remember { mutableStateOf(false) }
    var isDraftSaving by remember { mutableStateOf(false) }
    var showSuccessSnackbar by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    // Mileage Calculator States (For Fuel / Travel)
    var isMileageMode by remember { mutableStateOf(false) }
    var vehicleType by remember { mutableStateOf("2-Wheeler (₹3.50/km)") }
    var distanceKmText by remember { mutableStateOf("128") }

    val activeCategoryInfo = EXPENSE_CATEGORIES.find { it.name == selectedCategory } ?: EXPENSE_CATEGORIES[0]

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "Log Daily Expense",
                subtitle = "Claim TA/DA & Field Reimbursements",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Date Selector
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "1. Expense Date",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FilterChip(
                                selected = selectedDate == todayFormatted,
                                onClick = { selectedDate = todayFormatted },
                                label = { Text("Today ($todayFormatted)") },
                                leadingIcon = {
                                    Icon(Icons.Default.Today, contentDescription = null, modifier = Modifier.size(18.dp))
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )

                            FilterChip(
                                selected = selectedDate == yesterdayFormatted,
                                onClick = { selectedDate = yesterdayFormatted },
                                label = { Text("Yesterday") },
                                leadingIcon = {
                                    Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(18.dp))
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                }
            }

            // 2. Category Selection & Policy Guidelines
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "2. Expense Category *",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                            ) {
                                Text(
                                    text = activeCategoryInfo.name,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(EXPENSE_CATEGORIES) { cat ->
                                val isSelected = selectedCategory == cat.name
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedCategory = cat.name
                                        if (cat.name == "Fuel" || cat.name == "Travel") {
                                            isMileageMode = false
                                        }
                                    },
                                    label = { Text(cat.name) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = cat.icon,
                                            contentDescription = cat.name,
                                            modifier = Modifier.size(18.dp),
                                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Category policy callout
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Policy: ${activeCategoryInfo.guideline}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Text(
                                        text = "Requirement: ${activeCategoryInfo.maxLimitHint}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Amount & Smart Mileage / Allowance Calculator
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "3. Claim Amount *",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )

                            if (selectedCategory == "Fuel" || selectedCategory == "Travel") {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { isMileageMode = !isMileageMode }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Calculate,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isMileageMode) "Direct Amount" else "KM Calculator",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Mileage Calculator expansion
                        AnimatedVisibility(visible = isMileageMode && (selectedCategory == "Fuel" || selectedCategory == "Travel")) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Auto-Calculate Reimbursement by Distance",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FilterChip(
                                        selected = vehicleType.contains("2-Wheeler"),
                                        onClick = {
                                            vehicleType = "2-Wheeler (₹3.50/km)"
                                            val km = distanceKmText.toDoubleOrNull() ?: 0.0
                                            amountText = (km * 3.50).toInt().toString()
                                        },
                                        label = { Text("Bike (₹3.5/km)") }
                                    )
                                    FilterChip(
                                        selected = vehicleType.contains("4-Wheeler"),
                                        onClick = {
                                            vehicleType = "4-Wheeler (₹8.00/km)"
                                            val km = distanceKmText.toDoubleOrNull() ?: 0.0
                                            amountText = (km * 8.00).toInt().toString()
                                        },
                                        label = { Text("Car (₹8.0/km)") }
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = distanceKmText,
                                    onValueChange = { kmStr ->
                                        distanceKmText = kmStr
                                        val km = kmStr.toDoubleOrNull() ?: 0.0
                                        val rate = if (vehicleType.contains("2-Wheeler")) 3.50 else 8.00
                                        amountText = (km * rate).toInt().toString()
                                    },
                                    label = { Text("Total Route Distance (Kilometers)") },
                                    trailingIcon = { Text("km", modifier = Modifier.padding(end = 12.dp), style = MaterialTheme.typography.bodyMedium) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }

                        if (isMileageMode) {
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        OutlinedTextField(
                            value = amountText,
                            onValueChange = {
                                amountText = it
                                validationError = null
                            },
                            label = { Text("Claim Amount (₹) *") },
                            leadingIcon = {
                                Text(
                                    text = "₹",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.padding(start = 12.dp)
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("expense_amount_input"),
                            shape = RoundedCornerShape(12.dp),
                            isError = validationError != null
                        )

                        if (validationError != null) {
                            Text(
                                text = validationError ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Presets
                        Text(
                            text = "Quick Presets:",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(150, 250, 350, 500, 1000).forEach { preset ->
                                SuggestionChip(
                                    onClick = { amountText = preset.toString() },
                                    label = { Text("₹$preset") }
                                )
                            }
                        }
                    }
                }
            }

            // 4. Territory, Location & Route
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "4. Route & Field Location",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Route / Clinic Hub / Territory") },
                            leadingIcon = {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("expense_location_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Prescriber linkage dropdown (optional)
                        if (doctors.isNotEmpty()) {
                            Text(
                                text = "Link to Doctor Call (Optional):",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                item {
                                    FilterChip(
                                        selected = associatedDoctor == "None",
                                        onClick = { associatedDoctor = "None" },
                                        label = { Text("General Territory") }
                                    )
                                }
                                items(doctors.take(4)) { doc ->
                                    FilterChip(
                                        selected = associatedDoctor == doc.name,
                                        onClick = { associatedDoctor = doc.name },
                                        label = { Text(doc.name) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Bill / Receipt Attachment & Payment Mode
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "5. Receipt Attachment & Payment",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Receipt Upload Card
                        if (receiptFileName != null) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.primaryContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Receipt,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = receiptFileName ?: "Receipt Attachment",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            )
                                            Text(
                                                text = "Attached • 248 KB (Verified)",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = StatusSuccess,
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { receiptFileName = null },
                                        modifier = Modifier.testTag("remove_receipt_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Remove receipt",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    receiptFileName = "receipt_${selectedCategory.lowercase()}_${System.currentTimeMillis().toString().takeLast(4)}.jpg"
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("attach_receipt_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Attach Bill / Receipt Photo")
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Payment mode selection
                        Text(
                            text = "Paid Via:",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("UPI / Digital", "Cash", "Corporate Card", "Personal").forEach { mode ->
                                FilterChip(
                                    selected = paymentMode == mode,
                                    onClick = { paymentMode = mode },
                                    label = { Text(mode) }
                                )
                            }
                        }
                    }
                }
            }

            // 6. Description & Notes
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "6. Description & Field Remarks",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            placeholder = { Text("e.g. Daily travel circuit for Dr. Sharma and Model Town stockist delivery") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("expense_description_input"),
                            minLines = 3,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // Action Buttons (Submit Claim + Save as Draft)
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CareOsisPrimaryButton(
                        text = "Submit Claim to HQ",
                        onClick = {
                            val parsedAmount = amountText.toDoubleOrNull()
                            if (parsedAmount == null || parsedAmount <= 0) {
                                validationError = "Please enter a valid expense amount"
                                return@CareOsisPrimaryButton
                            }

                            isSaving = true
                            scope.launch {
                                val exp = ExpenseEntity(
                                    id = "EXP-${System.currentTimeMillis().toString().takeLast(5)}",
                                    category = selectedCategory,
                                    amount = parsedAmount,
                                    date = selectedDate,
                                    location = location,
                                    description = description.ifEmpty {
                                        if (associatedDoctor != "None") "Field discussion & visit with $associatedDoctor."
                                        else "Standard field route operations in territory."
                                    },
                                    receiptPath = receiptFileName ?: "",
                                    status = "Submitted",
                                    isSynced = false
                                )
                                repository.createExpense(exp)
                                isSaving = false
                                onExpenseAdded()
                            }
                        },
                        isLoading = isSaving,
                        testTag = "submit_expense_button"
                    )

                    OutlinedButton(
                        onClick = {
                            val parsedAmount = amountText.toDoubleOrNull() ?: 100.0
                            isDraftSaving = true
                            scope.launch {
                                val exp = ExpenseEntity(
                                    id = "EXP-${System.currentTimeMillis().toString().takeLast(5)}",
                                    category = selectedCategory,
                                    amount = parsedAmount,
                                    date = selectedDate,
                                    location = location,
                                    description = description.ifEmpty { "Draft claim." },
                                    receiptPath = receiptFileName ?: "",
                                    status = "Draft",
                                    isSynced = false
                                )
                                repository.createExpense(exp)
                                isDraftSaving = false
                                onExpenseAdded()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_draft_expense_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isDraftSaving) "Saving Draft..." else "Save as Draft",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }
    }
}
