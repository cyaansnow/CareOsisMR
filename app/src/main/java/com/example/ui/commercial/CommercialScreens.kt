package com.example.ui.commercial

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.CareOsisApp
import com.example.core.calculations.OrderCalculationResult
import com.example.core.calculations.OrderCalculator
import com.example.core.components.*
import com.example.data.local.entity.*
import com.example.ui.navigation.Destinations
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    onNavigate: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val scope = rememberCoroutineScope()
    val orders by repository.getAllOrders().collectAsStateWithLifecycle(initialValue = emptyList())
    var selectedFilter by remember { mutableStateOf("All") }
    var isSyncingAll by remember { mutableStateOf(false) }

    val filteredOrders = remember(orders, selectedFilter) {
        when (selectedFilter) {
            "Draft" -> orders.filter { it.status == "Draft" }
            "Pending HQ" -> orders.filter { it.status == "Submitted" || it.status == "Pending" }
            "Approved" -> orders.filter { it.status == "Approved" }
            "Dispatched" -> orders.filter { it.status == "Dispatched" || it.status == "Delivered" }
            else -> orders
        }
    }

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "Commercial Orders",
                subtitle = "${orders.size} Orders • ${orders.count { it.status == "Submitted" }} Pending HQ",
                onBackClick = onBackClick,
                actions = {
                    IconButton(
                        onClick = {
                            isSyncingAll = true
                            scope.launch {
                                repository.performSync()
                                isSyncingAll = false
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isSyncingAll) Icons.Default.Sync else Icons.Default.CloudUpload,
                            contentDescription = "Sync with HQ Server",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavigate(Destinations.CREATE_ORDER) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.AddShoppingCart, contentDescription = null) },
                text = { Text("Book New Order", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("create_order_fab")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            // Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Pending HQ", "Draft", "Approved", "Dispatched").forEach { filter ->
                    item {
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            if (filteredOrders.isEmpty()) {
                CareOsisEmptyState(
                    title = "No $selectedFilter Orders Found",
                    description = "Book primary and secondary pharmacy orders with offline instant calculation and HQ synchronization.",
                    actionButtonText = "Book Commercial Order",
                    onActionClick = { onNavigate(Destinations.CREATE_ORDER) },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 88.dp, top = 4.dp)
                ) {
                    items(filteredOrders, key = { it.id }) { order ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigate("order_submission/${order.id}") }
                                .testTag("order_card_${order.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = order.customerName,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            )
                                        }
                                        Text(
                                            text = "#${order.id} • ${order.customerType} • ${order.orderDate}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                    CareOsisStatusChip(
                                        text = when (order.status) {
                                            "Submitted" -> "Pending HQ"
                                            else -> order.status
                                        },
                                        containerColor = when (order.status) {
                                            "Dispatched", "Delivered" -> MaterialTheme.colorScheme.primaryContainer
                                            "Approved" -> StatusSuccessContainer
                                            "Draft" -> MaterialTheme.colorScheme.surfaceVariant
                                            else -> StatusWarningContainer
                                        },
                                        contentColor = when (order.status) {
                                            "Dispatched", "Delivered" -> MaterialTheme.colorScheme.onPrimaryContainer
                                            "Approved" -> StatusSuccess
                                            "Draft" -> MaterialTheme.colorScheme.onSurfaceVariant
                                            else -> StatusWarning
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = order.itemsSummary,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "₹${order.totalAmount.toInt()}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrderScreen(
    onOrderCreated: () -> Unit,
    onNavigate: (String) -> Unit = {},
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val scope = rememberCoroutineScope()
    val products by repository.getAllProducts().collectAsStateWithLifecycle(initialValue = emptyList())
    val stockists by repository.getAllStockists().collectAsStateWithLifecycle(initialValue = emptyList())
    val retailers by repository.getAllRetailers().collectAsStateWithLifecycle(initialValue = emptyList())
    val profile by repository.getProfile().collectAsStateWithLifecycle(initialValue = null)

    var customerType by remember { mutableStateOf("Retailer") }
    var selectedCustomerName by remember { mutableStateOf("Apollo Pharmacy Sector 9") }
    var selectedCfaDepot by remember { mutableStateOf("Central Depot (Ambala HQ)") }
    var paymentTerms by remember { mutableStateOf("30 Days Credit") }
    var discountPercentText by remember { mutableStateOf("5.0") }
    var orderNotes by remember { mutableStateOf("") }

    val quantities = remember { mutableStateMapOf<String, Int>() }
    var isSubmitting by remember { mutableStateOf(false) }
    var isSavingDraft by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var generatedOrderId by remember { mutableStateOf("") }
    var successDialogTitle by remember { mutableStateOf("") }
    var successDialogMessage by remember { mutableStateOf("") }

    val totalItemsCount = remember(quantities) { quantities.values.sum() }

    val orderCalculation: OrderCalculationResult = remember(quantities, discountPercentText, products) {
        val calcItems = products.mapNotNull { prod ->
            val qty = quantities[prod.id] ?: 0
            if (qty > 0) Triple(qty, prod.mrp, prod.retailerRate) else null
        }
        val disc = discountPercentText.toDoubleOrNull() ?: 0.0
        OrderCalculator.calculateOrder(calcItems, overallDiscountPercent = disc)
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onOrderCreated()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = StatusSuccess,
                    modifier = Modifier.size(40.dp)
                )
            },
            title = {
                Text(
                    text = successDialogTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column {
                    Text(
                        text = successDialogMessage,
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Order ID: $generatedOrderId", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Total Amount: ₹${orderCalculation.totalAmount.toInt()}", fontSize = 12.sp)
                            Text("Depot: $selectedCfaDepot", fontSize = 12.sp)
                            Text("Sync Queue: Transmitted to Cloud Server", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        if (generatedOrderId.isNotEmpty()) {
                            onNavigate("order_submission/$generatedOrderId")
                        } else {
                            onOrderCreated()
                        }
                    }
                ) {
                    Text("Review Order Summary")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        onOrderCreated()
                    }
                ) {
                    Text("Orders List")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "Book Commercial Order",
                subtitle = "Commercial Order Entry & HQ Dispatch",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            // Sticky Action Panel with Save Draft & Send to HQ
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "$totalItemsCount units selected",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Text(
                                text = "₹${orderCalculation.totalAmount.toInt()}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 1. SAVE DRAFT LOCALLY
                            OutlinedButton(
                                onClick = {
                                    isSavingDraft = true
                                    scope.launch {
                                        val orderId = "DFT-${System.currentTimeMillis().toString().takeLast(5)}"
                                        generatedOrderId = orderId
                                        val summaryList = products.mapNotNull { p ->
                                            val q = quantities[p.id] ?: 0
                                            if (q > 0) "${p.name} ($q)" else null
                                        }
                                        val order = OrderEntity(
                                            id = orderId,
                                            customerId = "CUST-DFT",
                                            customerName = selectedCustomerName.ifEmpty { "General Chemist" },
                                            customerType = customerType,
                                            mrId = profile?.empId ?: "CO-MR-8492",
                                            orderDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                                            subtotal = orderCalculation.subtotal,
                                            discountPercent = discountPercentText.toDoubleOrNull() ?: 5.0,
                                            discountAmount = orderCalculation.discountAmount,
                                            gstAmount = orderCalculation.gstAmount,
                                            totalAmount = orderCalculation.totalAmount,
                                            itemsSummary = summaryList.joinToString(", ").ifEmpty { "Standard Formulations" },
                                            status = "Draft",
                                            notes = orderNotes.ifEmpty { "Local draft order." },
                                            isSynced = false
                                        )
                                        val orderItems = products.mapNotNull { p ->
                                            val q = quantities[p.id] ?: 0
                                            if (q > 0) {
                                                OrderItemEntity(
                                                    orderId = orderId,
                                                    productId = p.id,
                                                    productName = p.name,
                                                    quantity = q,
                                                    mrp = p.mrp,
                                                    unitRate = p.retailerRate,
                                                    discountPercent = discountPercentText.toDoubleOrNull() ?: 5.0,
                                                    totalAmount = p.retailerRate * q
                                                )
                                            } else null
                                        }
                                        repository.createOrder(order, orderItems)
                                        isSavingDraft = false
                                        successDialogTitle = "Order Saved as Draft"
                                        successDialogMessage = "Order draft stored in offline database. You can review and transmit to HQ at any time."
                                        showSuccessDialog = true
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                enabled = !isSavingDraft && !isSubmitting
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save Draft", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }

                            // 2. SEND ORDER TO HQ
                            Button(
                                onClick = {
                                    isSubmitting = true
                                    scope.launch {
                                        val orderId = "ORD-${System.currentTimeMillis().toString().takeLast(5)}"
                                        generatedOrderId = orderId
                                        val summaryList = products.mapNotNull { p ->
                                            val q = quantities[p.id] ?: 0
                                            if (q > 0) "${p.name} ($q)" else null
                                        }
                                        val order = OrderEntity(
                                            id = orderId,
                                            customerId = "CUST-01",
                                            customerName = selectedCustomerName.ifEmpty { "Apollo Pharmacy Sector 9" },
                                            customerType = customerType,
                                            mrId = profile?.empId ?: "CO-MR-8492",
                                            orderDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                                            subtotal = orderCalculation.subtotal,
                                            discountPercent = discountPercentText.toDoubleOrNull() ?: 5.0,
                                            discountAmount = orderCalculation.discountAmount,
                                            gstAmount = orderCalculation.gstAmount,
                                            totalAmount = orderCalculation.totalAmount,
                                            itemsSummary = summaryList.joinToString(", ").ifEmpty { "General Formulations" },
                                            status = "Submitted",
                                            notes = orderNotes.ifEmpty { "Dispatched request via $selectedCfaDepot • Terms: $paymentTerms" },
                                            isSynced = false
                                        )
                                        val orderItems = products.mapNotNull { p ->
                                            val q = quantities[p.id] ?: 0
                                            if (q > 0) {
                                                OrderItemEntity(
                                                    orderId = orderId,
                                                    productId = p.id,
                                                    productName = p.name,
                                                    quantity = q,
                                                    mrp = p.mrp,
                                                    unitRate = p.retailerRate,
                                                    discountPercent = discountPercentText.toDoubleOrNull() ?: 5.0,
                                                    totalAmount = p.retailerRate * q
                                                )
                                            } else null
                                        }
                                        repository.createOrder(order, orderItems)
                                        repository.performSync()
                                        isSubmitting = false
                                        successDialogTitle = "Order Sent to HQ"
                                        successDialogMessage = "Order successfully booked and transmitted to CareOsis Central Supply HQ for CFA verification and dispatch."
                                        showSuccessDialog = true
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                enabled = !isSavingDraft && !isSubmitting,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.testTag("send_to_hq_button")
                            ) {
                                if (isSubmitting) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Send to HQ", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            // Customer & Channel Details Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Customer & Channel Selection",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Retailer", "Stockist").forEach { type ->
                                FilterChip(
                                    selected = customerType == type,
                                    onClick = { customerType = type },
                                    label = { Text(type) },
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = selectedCustomerName,
                            onValueChange = { selectedCustomerName = it },
                            label = { Text("Customer / Chemist Name *") },
                            modifier = Modifier.fillMaxWidth().testTag("order_customer_name_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = selectedCfaDepot,
                            onValueChange = { selectedCfaDepot = it },
                            label = { Text("Fulfilling CFA Warehouse / Depot") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = discountPercentText,
                                onValueChange = { discountPercentText = it },
                                label = { Text("Scheme Discount (%)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = paymentTerms,
                                onValueChange = { paymentTerms = it },
                                label = { Text("Payment Terms") },
                                modifier = Modifier.weight(1.2f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }

            // Products & Quantities Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Products & Quantities",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "${products.size} Formulations",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }

            items(products, key = { it.id }) { product ->
                val qty = quantities[product.id] ?: 0
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (qty > 0) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (qty > 0) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
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
                                text = product.name,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "Rate: ₹${product.retailerRate.toInt()} • MRP: ₹${product.mrp.toInt()}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary)
                            )
                        }

                        // Quantity Stepper
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    if (qty > 0) quantities[product.id] = qty - 1
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RemoveCircleOutline,
                                    contentDescription = "Decrease",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "$qty",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            IconButton(
                                onClick = {
                                    quantities[product.id] = qty + 1
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddCircle,
                                    contentDescription = "Increase",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // Calculation Breakdown Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Commercial Bill Summary",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        SummaryRow("Subtotal (${totalItemsCount} units)", "₹${orderCalculation.subtotal.toInt()}")
                        SummaryRow("Scheme Discount (${discountPercentText}%)", "-₹${orderCalculation.discountAmount.toInt()}", isDiscount = true)
                        SummaryRow("Pharma GST (12%)", "+₹${orderCalculation.gstAmount.toInt()}")
                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Grand Total Payable",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "₹${orderCalculation.totalAmount.toInt()}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }

            // Special Delivery Instructions
            item {
                OutlinedTextField(
                    value = orderNotes,
                    onValueChange = { orderNotes = it },
                    label = { Text("Special Dispatch & Delivery Instructions") },
                    placeholder = { Text("e.g. Urgent stock replenishment before Monday") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: String,
    onNavigate: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val scope = rememberCoroutineScope()
    val order by repository.getOrderById(orderId).collectAsStateWithLifecycle(initialValue = null)
    val orderItems by repository.getOrderItems(orderId).collectAsStateWithLifecycle(initialValue = emptyList())
    var isActionInProgress by remember { mutableStateOf(false) }
    var showInvoiceDialog by remember { mutableStateOf(false) }

    if (showInvoiceDialog && order != null) {
        AlertDialog(
            onDismissRequest = { showInvoiceDialog = false },
            title = { Text("GST Proforma Tax Invoice", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("CareOsis Life Sciences Ltd. - Tax Invoice", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text("Invoice No: INV-${order!!.id}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    Text("Billed to: ${order!!.customerName} (${order!!.customerType})", fontSize = 12.sp)
                    Text("Date: ${order!!.orderDate}", fontSize = 12.sp)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text("Items Summary: ${order!!.itemsSummary}", fontSize = 12.sp)
                    Text("Subtotal: ₹${order!!.subtotal.toInt()}", fontSize = 12.sp)
                    Text("Discount: -₹${order!!.discountAmount.toInt()}", fontSize = 12.sp, color = StatusSuccess)
                    Text("CGST + SGST (12%): ₹${order!!.gstAmount.toInt()}", fontSize = 12.sp)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text("Total Amount: ₹${order!!.totalAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            },
            confirmButton = {
                Button(onClick = { showInvoiceDialog = false }) {
                    Text("Done")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "Order Details",
                subtitle = "#$orderId",
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = { showInvoiceDialog = true }) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = "View Invoice", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }
            )
        }
    ) { innerPadding ->
        if (order == null) {
            CareOsisLoadingState(message = "Retrieving commercial order...", modifier = Modifier.padding(innerPadding))
        } else {
            val ord = order!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Status Tracking Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Order Lifecycle Status",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                CareOsisStatusChip(
                                    text = ord.status,
                                    containerColor = when (ord.status) {
                                        "Dispatched", "Delivered" -> MaterialTheme.colorScheme.primaryContainer
                                        "Approved" -> StatusSuccessContainer
                                        "Draft" -> MaterialTheme.colorScheme.surfaceVariant
                                        else -> StatusWarningContainer
                                    },
                                    contentColor = when (ord.status) {
                                        "Dispatched", "Delivered" -> MaterialTheme.colorScheme.onPrimaryContainer
                                        "Approved" -> StatusSuccess
                                        "Draft" -> MaterialTheme.colorScheme.onSurfaceVariant
                                        else -> StatusWarning
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            // Step progress
                            val steps = listOf("Draft", "Submitted", "Approved", "Dispatched", "Delivered")
                            val currentStepIndex = steps.indexOf(ord.status).let { if (it == -1) 1 else it }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                steps.forEachIndexed { index, step ->
                                    val isPassed = index <= currentStepIndex
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isPassed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isPassed) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = step,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                fontWeight = if (isPassed) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isPassed) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Customer & Bill Details
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Billed Customer",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = ord.customerName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(
                                text = "${ord.customerType} • Booked on ${ord.orderDate} by MR (${ord.mrId})",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            if (ord.notes.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Remarks: ${ord.notes}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                                )
                            }
                        }
                    }
                }

                // Financial Breakdown
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Financial Breakdown",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            SummaryRow("Subtotal", "₹${ord.subtotal.toInt()}")
                            SummaryRow("Scheme Discount (${ord.discountPercent}%)", "-₹${ord.discountAmount.toInt()}", isDiscount = true)
                            SummaryRow("Pharma GST (12%)", "+₹${ord.gstAmount.toInt()}")
                            Spacer(modifier = Modifier.height(6.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Grand Total",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = "₹${ord.totalAmount.toInt()}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    }
                }

                // Action Buttons
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Re-send to HQ
                        CareOsisPrimaryButton(
                            text = if (ord.status == "Draft") "TRANSMIT ORDER TO HQ" else "RE-SYNC / SEND REQUEST TO HQ",
                            onClick = {
                                isActionInProgress = true
                                scope.launch {
                                    repository.sendOrderToHq(ord.id)
                                    repository.performSync()
                                    isActionInProgress = false
                                }
                            },
                            isLoading = isActionInProgress,
                            icon = Icons.Default.CloudUpload,
                            testTag = "resend_order_hq_button"
                        )

                        // HQ Fast-Approval (Executive action)
                        if (ord.status != "Approved" && ord.status != "Dispatched") {
                            CareOsisSecondaryButton(
                                text = "Approve Order (HQ Manager Mode)",
                                onClick = {
                                    scope.launch {
                                        repository.updateOrderStatus(ord.id, "Approved")
                                        repository.performSync()
                                    }
                                },
                                icon = Icons.Default.Verified
                            )
                        }

                        OutlinedButton(
                            onClick = { showInvoiceDialog = true },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("View / Print GST Invoice")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, isDiscount: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = if (isDiscount) StatusSuccess else MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockistListScreen(
    onNavigate: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val stockists by repository.getAllStockists().collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "Stockist Channel",
                subtitle = "${stockists.size} Active Distributors",
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(stockists, key = { it.id }) { stockist ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stockist.companyName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            CareOsisStatusChip(
                                text = stockist.status,
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Text(
                            text = "Contact: ${stockist.contactPerson} • ${stockist.phone}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stockist.address,
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Outstanding Due", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                Text(text = "₹${stockist.outstandingAmount.toInt()}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = StatusWarning))
                            }
                            Column {
                                Text(text = "Credit Limit", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                Text(text = "₹${stockist.creditLimit.toInt()}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RetailerListScreen(
    onNavigate: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val retailers by repository.getAllRetailers().collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "Retail Chemist Network",
                subtitle = "${retailers.size} Pharmacy Outlets",
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(retailers, key = { it.id }) { retailer ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = retailer.shopName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            CareOsisStatusChip(
                                text = "Chemist",
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Text(
                            text = "${retailer.ownerName} • ${retailer.phone}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Stocked: ${retailer.productsStocked}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = retailer.address,
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(
    onNavigate: (String) -> Unit,
    onBackClick: () -> Unit
) {
    com.example.ui.commercial.expense.ExpenseManagementScreen(
        onNavigate = onNavigate,
        onBackClick = onBackClick
    )
}

@Composable
fun AddExpenseScreen(
    onExpenseAdded: () -> Unit,
    onBackClick: () -> Unit
) {
    com.example.ui.commercial.expense.LogExpenseFormScreen(
        onExpenseAdded = onExpenseAdded,
        onBackClick = onBackClick
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val scope = rememberCoroutineScope()
    val profile by repository.getProfile().collectAsStateWithLifecycle(initialValue = null)
    val attendanceList by repository.getAllAttendance().collectAsStateWithLifecycle(initialValue = emptyList())

    val isCheckedIn = profile?.isCheckedInToday ?: true
    val checkInTime = profile?.checkInTime ?: "08:45 AM"

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "Field Attendance",
                subtitle = "Daily Check-in & GPS Geotag",
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCheckedIn) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isCheckedIn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isCheckedIn) Icons.Default.CheckCircle else Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = if (isCheckedIn) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = if (isCheckedIn) "Currently Checked-In" else "Not Checked-In",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = if (isCheckedIn) "Checked in at $checkInTime (GPS Geotagged: Rohini Sec-9)" else "Tap below to mark today's field attendance.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    val now = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                                    val today = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                                    val empId = profile?.empId ?: "CO-MR-8492"

                                    if (isCheckedIn) {
                                        repository.markAttendance(
                                            AttendanceEntity(
                                                id = "ATT-${System.currentTimeMillis().toString().takeLast(4)}",
                                                date = today,
                                                checkInTime = checkInTime,
                                                checkOutTime = now,
                                                workingHours = "8h 30m",
                                                visitsCompleted = profile?.completedVisitsToday ?: 12,
                                                status = "Completed",
                                                checkInLocation = "Rohini Sec-9",
                                                isSynced = false
                                            ),
                                            empId
                                        )
                                    } else {
                                        repository.markAttendance(
                                            AttendanceEntity(
                                                id = "ATT-${System.currentTimeMillis().toString().takeLast(4)}",
                                                date = today,
                                                checkInTime = now,
                                                checkOutTime = "",
                                                workingHours = "Active",
                                                visitsCompleted = 0,
                                                status = "Present",
                                                checkInLocation = "Rohini Sec-9",
                                                isSynced = false
                                            ),
                                            empId
                                        )
                                    }
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCheckedIn) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text(
                                text = if (isCheckedIn) "Check-Out for the Day" else "Check-In for the Day",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Recent Attendance Logs",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                )
            }

            items(attendanceList) { att ->
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
                        Column {
                            Text(
                                text = att.date,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            )
                            Text(
                                text = "In: ${att.checkInTime} • Out: ${att.checkOutTime.ifEmpty { "Active" }}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                        CareOsisStatusChip(
                            text = att.status,
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutePlanScreen(
    onNavigate: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val routes by repository.getAllRoutes().collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "Beat & Route Plan",
                subtitle = "Optimized Field Itinerary",
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(routes, key = { it.id }) { route ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = route.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            CareOsisStatusChip(
                                text = "${route.doctorCount} Doctors",
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${route.date} • ${route.doctorCount} Doctors, ${route.retailerCount} Retailers, ${route.stockistCount} Stockists",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Planned Sequence of Calls:",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        route.stopsListText.split(";").forEach { stop ->
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stop.trim(),
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowUpScreen(
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val scope = rememberCoroutineScope()
    val followUps by repository.getAllFollowUps().collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "Follow-ups & Action Items",
                subtitle = "${followUps.size} Prescriber & Retailer Tasks",
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(followUps, key = { it.id }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.personName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            CareOsisStatusChip(
                                text = item.priority,
                                containerColor = if (item.priority == "High") StatusErrorContainer else StatusWarningContainer,
                                contentColor = if (item.priority == "High") StatusError else StatusWarning
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.reason,
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Due: ${item.followUpDate} • Priority: ${item.priority}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Status: ${item.status}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (item.status == "Completed") StatusSuccess else StatusWarning,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            if (item.status != "Completed") {
                                TextButton(
                                    onClick = {
                                        scope.launch {
                                            repository.updateFollowUp(item.copy(status = "Completed"))
                                        }
                                    }
                                ) {
                                    Text(
                                        "Mark Completed",
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HqExecutiveDashboardScreen(
    onNavigate: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val scope = rememberCoroutineScope()
    val orders by repository.getAllOrders().collectAsStateWithLifecycle(initialValue = emptyList())
    val doctors by repository.getAllDoctors().collectAsStateWithLifecycle(initialValue = emptyList())
    val expenses by repository.getAllExpenses().collectAsStateWithLifecycle(initialValue = emptyList())
    val attendance by repository.getAllAttendance().collectAsStateWithLifecycle(initialValue = emptyList())
    val pendingSyncCount by repository.getPendingSyncCount().collectAsStateWithLifecycle(initialValue = 0)

    val totalCommercialVolume = remember(orders) { orders.sumOf { it.totalAmount } }
    val pendingHqOrders = remember(orders) { orders.count { it.status == "Submitted" || it.status == "Pending" } }
    val totalExpensesClaimed = remember(expenses) { expenses.sumOf { it.amount } }

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "HQ Executive Admin Portal",
                subtitle = "Company Owner & Business Command Center",
                onBackClick = onBackClick,
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                repository.performSync()
                            }
                        }
                    ) {
                        Icon(Icons.Default.CloudSync, contentDescription = "Sync All Field Data", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Enterprise Overview Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "CareOsis Central HQ",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                                Text(
                                    text = "North Zone Division • 14 Field Officers Active",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                                )
                            }
                            CareOsisStatusChip(
                                text = "HQ Live",
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Commercial Revenue", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onPrimaryContainer))
                                Text("₹${totalCommercialVolume.toInt()}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer))
                            }
                            Column {
                                Text("Pending HQ Orders", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onPrimaryContainer))
                                Text("$pendingHqOrders Orders", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer))
                            }
                        }
                    }
                }
            }

            // Key Operations Metrics Grid
            item {
                Text(
                    text = "Real-Time Field Telemetry",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CareOsisStatCard(
                        title = "Prescribers",
                        value = "${doctors.size}",
                        subtitle = "Covered Doctors",
                        icon = Icons.Default.MedicalServices,
                        accentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    CareOsisStatCard(
                        title = "MR Attendance",
                        value = "${attendance.size} Logs",
                        subtitle = "Geotagged Logs",
                        icon = Icons.Default.AccessTime,
                        accentColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CareOsisStatCard(
                        title = "Field Expenses",
                        value = "₹${totalExpensesClaimed.toInt()}",
                        subtitle = "Claims Logged",
                        icon = Icons.Default.Receipt,
                        accentColor = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f)
                    )
                    CareOsisStatCard(
                        title = "Sync Queue",
                        value = "$pendingSyncCount",
                        subtitle = "Pending HQ Sync",
                        icon = Icons.Default.CloudQueue,
                        accentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Quick Executive Actions
            item {
                Text(
                    text = "Executive Management Actions",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { onNavigate(Destinations.ORDERS) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Review Commercial Orders & Dispatch Queue", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onNavigate(Destinations.EXPENSES) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.AttachMoney, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Audit Field Allowance & Fuel Claims")
                    }

                    OutlinedButton(
                        onClick = { onNavigate(Destinations.ATTENDANCE) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View MR Geo-Tagged Field Attendance")
                    }
                }
            }
        }
    }
}
