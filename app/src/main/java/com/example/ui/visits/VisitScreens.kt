package com.example.ui.visits

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
import com.example.core.components.*
import com.example.data.local.entity.DoctorEntity
import com.example.data.local.entity.DoctorVisitEntity
import com.example.ui.navigation.Destinations
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartVisitScreen(
    doctorId: String,
    onProceedToReport: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val doctor by repository.getDoctorById(doctorId).collectAsStateWithLifecycle(initialValue = null)

    var selectedPurpose by remember { mutableStateOf("New Product Introduction") }
    val startTime = remember {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        sdf.format(Date())
    }

    val purposes = listOf(
        "New Product Introduction",
        "Follow-up Call",
        "Product Reminder",
        "Sample Follow-up",
        "Prescription Discussion",
        "Relationship Visit",
        "KOL Scientific Discussion"
    )

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "Start Doctor Call",
                subtitle = doctor?.name ?: "Doctor Visit",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ClinicalBackground)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    // Doctor Details Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = doctor?.name ?: "Loading...",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Text(
                                        text = "${doctor?.specialty ?: ""} • ${doctor?.clinicHospital ?: ""}",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = EmeraldLight)
                                    )
                                }
                                CareOsisStatusChip(
                                    text = "Category ${doctor?.potentialCategory ?: "A"}",
                                    containerColor = GoldContainer,
                                    contentColor = OnGoldContainer
                                )
                            }
                        }
                    }
                }

                item {
                    // Call Telemetry & Start Time
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Call Telemetry & Location",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccessTime, contentDescription = null, tint = EmeraldPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Call Started At: $startTime",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MyLocation, contentDescription = null, tint = EmeraldPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "GPS Geotag: Verified Territory Checkpoint (Rohini Sector 9)",
                                    style = MaterialTheme.typography.bodySmall.copy(color = NeutralTextSecondary)
                                )
                            }
                        }
                    }
                }

                item {
                    // Visit Purpose Selector
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Select Call Objective / Purpose *",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            purposes.forEach { purpose ->
                                val isSelected = selectedPurpose == purpose
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { selectedPurpose = purpose },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(
                                        1.5.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { selectedPurpose = purpose },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = MaterialTheme.colorScheme.primary,
                                                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = purpose,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Begin Pitching & Detailing Button
            CareOsisPrimaryButton(
                text = "Conduct Discussion & Open Report",
                onClick = { onProceedToReport(doctorId) },
                icon = Icons.Default.ArrowForward,
                testTag = "start_visit_proceed_button"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitReportScreen(
    doctorId: String,
    onVisitSubmitted: () -> Unit,
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val scope = rememberCoroutineScope()
    val doctor by repository.getDoctorById(doctorId).collectAsStateWithLifecycle(initialValue = null)
    val allProducts by repository.getAllProducts().collectAsStateWithLifecycle(initialValue = emptyList())
    val profile by repository.getProfile().collectAsStateWithLifecycle(initialValue = null)

    val selectedProducts = remember { mutableStateListOf<String>("Booster", "Metabo 3X") }
    var doctorResponse by remember { mutableStateOf("Positive") }
    var prescriptionPotential by remember { mutableStateOf("High") }
    var sampleQuantity by remember { mutableStateOf("Booster (2 packs), Metabo 3X (1 pack)") }
    var nextFollowUpDate by remember { mutableStateOf("26 Aug 2026") }
    var notes by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    val currentDateStr = remember {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        sdf.format(Date())
    }
    val startTimeStr = remember {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        sdf.format(Date())
    }

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "Complete Visit Report",
                subtitle = doctor?.name ?: "Doctor Call Summary",
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
            item {
                // Products Discussed Multi-Selection
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Products Discussed / Detailed *",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Tap to add/remove products presented to the prescriber",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(allProducts) { prod ->
                                val isSelected = selectedProducts.contains(prod.name)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        if (isSelected) selectedProducts.remove(prod.name)
                                        else selectedProducts.add(prod.name)
                                    },
                                    label = { Text(prod.name) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EmeraldPrimary,
                                        selectedLabelColor = ClinicalWhite
                                    )
                                )
                            }
                        }
                    }
                }
            }

            item {
                // Doctor Response
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Prescriber Response *",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        val responses = listOf("Positive", "Interested", "Neutral", "Needs Follow-up", "Negative")
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(responses) { resp ->
                                FilterChip(
                                    selected = doctorResponse == resp,
                                    onClick = { doctorResponse = resp },
                                    label = { Text(resp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = if (resp == "Positive" || resp == "Interested") EmeraldPrimary else GoldDark,
                                        selectedLabelColor = ClinicalWhite
                                    )
                                )
                            }
                        }
                    }
                }
            }

            item {
                // Prescription Potential
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Prescription Potential *",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("High", "Medium", "Low").forEach { level ->
                                FilterChip(
                                    selected = prescriptionPotential == level,
                                    onClick = { prescriptionPotential = level },
                                    label = { Text(level) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            item {
                // Samples Given & Next Follow-up
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Samples & Follow-up",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = sampleQuantity,
                            onValueChange = { sampleQuantity = it },
                            label = { Text("Samples Handed Over (Product & Quantity)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = nextFollowUpDate,
                            onValueChange = { nextFollowUpDate = it },
                            label = { Text("Next Follow-up Call Date") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Doctor's Key Feedback & Next Action Points") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            item {
                CareOsisPrimaryButton(
                    text = "SUBMIT VISIT REPORT (OFFLINE-FIRST)",
                    onClick = {
                        isSubmitting = true
                        scope.launch {
                            val visit = DoctorVisitEntity(
                                id = "VISIT-${System.currentTimeMillis().toString().takeLast(5)}",
                                doctorId = doctorId,
                                doctorName = doctor?.name ?: "Doctor",
                                clinicName = doctor?.clinicHospital ?: "Clinic",
                                startTime = startTimeStr,
                                endTime = startTimeStr,
                                visitDate = currentDateStr,
                                purpose = "Prescription Call & Detailing",
                                doctorResponse = doctorResponse,
                                prescriptionPotential = prescriptionPotential,
                                samplesGiven = sampleQuantity,
                                productsDiscussed = selectedProducts.joinToString(", "),
                                nextFollowUpDate = nextFollowUpDate,
                                notes = notes.ifEmpty { "Prescriber agreed to initiate CareOsis products for targeted patient groups." },
                                status = "Completed",
                                isSynced = false
                            )
                            repository.recordDoctorVisit(visit, profile?.empId ?: "CO-MR-8492")
                            onVisitSubmitted()
                        }
                    },
                    isLoading = isSubmitting,
                    testTag = "submit_visit_button"
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitHistoryScreen(
    onNavigate: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val visits by repository.getAllVisits().collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "Visit Call History",
                subtitle = "${visits.size} Field Visits Logged",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        if (visits.isEmpty()) {
            CareOsisEmptyState(
                title = "No Visits Logged",
                description = "Your completed doctor calls will appear here.",
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ClinicalBackground)
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(visits, key = { it.id }) { visit ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = visit.doctorName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                CareOsisStatusChip(
                                    text = visit.doctorResponse,
                                    containerColor = EmeraldContainer,
                                    contentColor = OnEmeraldContainer
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${visit.visitDate} • ${visit.startTime} • ${visit.clinicName}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Products: ${visit.productsDiscussed}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                            )
                            if (visit.samplesGiven.isNotEmpty()) {
                                Text(
                                    text = "Samples: ${visit.samplesGiven}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = GoldDark, fontWeight = FontWeight.Medium)
                                )
                            }
                            if (visit.notes.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Notes: ${visit.notes}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = NeutralTextSecondary)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
