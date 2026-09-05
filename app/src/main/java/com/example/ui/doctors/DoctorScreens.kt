package com.example.ui.doctors

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
import com.example.ui.navigation.Destinations
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorListScreen(
    onNavigate: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val doctors by repository.getAllDoctors().collectAsStateWithLifecycle(initialValue = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredDoctors = remember(doctors, searchQuery, selectedCategory) {
        doctors.filter { doc ->
            val matchesQuery = doc.name.contains(searchQuery, ignoreCase = true) ||
                    doc.specialty.contains(searchQuery, ignoreCase = true) ||
                    doc.clinicHospital.contains(searchQuery, ignoreCase = true)
            val matchesCategory = if (selectedCategory == "All") true else doc.potentialCategory == selectedCategory
            matchesQuery && matchesCategory
        }
    }

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "Doctor CRM",
                subtitle = "${doctors.size} Prescribers in Territory",
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigate(Destinations.ADD_DOCTOR) },
                containerColor = EmeraldPrimary,
                contentColor = ClinicalWhite,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_doctor_fab")
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add Doctor")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ClinicalBackground)
                .padding(innerPadding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("doctor_search_bar"),
                placeholder = { Text("Search doctor, specialty, clinic...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EmeraldPrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmeraldPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Category Filter Chips (All, Category A, Category B, Category C)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categories = listOf("All", "A", "B", "C")
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = { Text(if (cat == "All") "All Categories" else "Category $cat") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = ClinicalWhite,
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }

            if (filteredDoctors.isEmpty()) {
                CareOsisEmptyState(
                    title = "No Doctors Found",
                    description = if (searchQuery.isNotEmpty()) "No prescribers match '$searchQuery'" else "Add doctors in your territory to begin logging visits.",
                    actionButtonText = "Add New Doctor",
                    onActionClick = { onNavigate(Destinations.ADD_DOCTOR) },
                    icon = Icons.Default.PersonSearch
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(top = 6.dp, bottom = 80.dp)
                ) {
                    items(filteredDoctors, key = { it.id }) { doctor ->
                        DoctorItemCard(
                            doctor = doctor,
                            onCardClick = { onNavigate("doctor_detail/${doctor.id}") },
                            onStartVisit = { onNavigate("start_visit/${doctor.id}") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DoctorItemCard(
    doctor: DoctorEntity,
    onCardClick: () -> Unit,
    onStartVisit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("doctor_card_${doctor.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = doctor.name.take(4).replace("Dr.", "").trim().take(2).uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = doctor.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${doctor.specialty} • ${doctor.qualification}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = EmeraldLight,
                                fontWeight = FontWeight.Medium
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                CareOsisStatusChip(
                    text = "Cat ${doctor.potentialCategory}",
                    containerColor = when (doctor.potentialCategory) {
                        "A" -> GoldContainer
                        "B" -> EmeraldContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    contentColor = when (doctor.potentialCategory) {
                        "A" -> OnGoldContainer
                        "B" -> OnEmeraldContainer
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = doctor.clinicHospital,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Visiting: ${doctor.preferredVisitingTime}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (doctor.lastVisitDate.isNotEmpty()) "Last Visited: ${doctor.lastVisitDate}" else "No recorded visits yet",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                )

                Button(
                    onClick = onStartVisit,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Start Visit", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDetailScreen(
    doctorId: String,
    onNavigate: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val doctor by repository.getDoctorById(doctorId).collectAsStateWithLifecycle(initialValue = null)
    val visits by repository.getVisitsForDoctor(doctorId).collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = doctor?.name ?: "Doctor Profile",
                subtitle = doctor?.specialty,
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        if (doctor == null) {
            CareOsisLoadingState(message = "Loading prescriber profile...")
        } else {
            val doc = doctor!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ClinicalBackground)
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    // Profile Header Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = doc.name,
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Text(
                                        text = "${doc.specialty} • ${doc.qualification}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = EmeraldPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                                CareOsisStatusChip(
                                    text = "Category ${doc.potentialCategory}",
                                    containerColor = GoldContainer,
                                    contentColor = OnGoldContainer
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(14.dp))

                            // Contact info
                            InfoRow(icon = Icons.Default.Business, label = "Clinic / Hospital", value = doc.clinicHospital)
                            InfoRow(icon = Icons.Default.Place, label = "Address", value = doc.address)
                            InfoRow(icon = Icons.Default.Phone, label = "Phone", value = doc.phone)
                            InfoRow(icon = Icons.Default.AccessTime, label = "Preferred Visiting Hours", value = doc.preferredVisitingTime)
                            if (doc.birthday.isNotEmpty()) {
                                InfoRow(icon = Icons.Default.Cake, label = "Birthday", value = doc.birthday)
                            }
                        }
                    }
                }

                item {
                    // Start Visit Action Banner
                    CareOsisPrimaryButton(
                        text = "Conduct Doctor Visit Call",
                        onClick = { onNavigate("start_visit/${doc.id}") },
                        icon = Icons.Default.PlayArrow,
                        testTag = "detail_start_visit_button"
                    )
                }

                item {
                    // Notes / Key Clinical Strategy
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "MR Strategy & Prescriber Notes",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = doc.notes.ifEmpty { "High potential KOL. Pitch Booster and Metabo 3X with clinical data." },
                                style = MaterialTheme.typography.bodyMedium.copy(color = NeutralTextSecondary)
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Visit History (${visits.size} Recorded)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                if (visits.isEmpty()) {
                    item {
                        CareOsisEmptyState(
                            title = "No Visits Recorded",
                            description = "You haven't logged any calls with ${doc.name} yet.",
                            actionButtonText = "Log First Visit",
                            onActionClick = { onNavigate("start_visit/${doc.id}") }
                        )
                    }
                } else {
                    items(visits) { visit ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${visit.visitDate} • ${visit.startTime}",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    CareOsisStatusChip(
                                        text = visit.doctorResponse,
                                        containerColor = EmeraldContainer,
                                        contentColor = OnEmeraldContainer
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Purpose: ${visit.purpose}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = EmeraldLight, fontWeight = FontWeight.Medium)
                                )
                                if (visit.productsDiscussed.isNotEmpty()) {
                                    Text(
                                        text = "Products Discussed: ${visit.productsDiscussed}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                if (visit.samplesGiven.isNotEmpty()) {
                                    Text(
                                        text = "Samples: ${visit.samplesGiven}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = GoldDark, fontWeight = FontWeight.Medium)
                                    )
                                }
                                if (visit.notes.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = visit.notes,
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
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

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = EmeraldPrimary,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDoctorScreen(
    onDoctorAdded: () -> Unit,
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var specialty by remember { mutableStateOf("Consultant Physician") }
    var qualification by remember { mutableStateOf("MBBS, MD (Medicine)") }
    var clinicHospital by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var preferredTime by remember { mutableStateOf("10:00 AM - 01:00 PM") }
    var category by remember { mutableStateOf("A") }
    var priority by remember { mutableStateOf("High") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "Add Prescriber",
                subtitle = "New Doctor Registration",
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Doctor Demographics",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it; errorMessage = null },
                            label = { Text("Doctor Name * (e.g. Dr. Ramesh Gupta)") },
                            modifier = Modifier.fillMaxWidth().testTag("add_doctor_name_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = specialty,
                            onValueChange = { specialty = it },
                            label = { Text("Specialty * (e.g. Cardiologist, Ortho)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = qualification,
                            onValueChange = { qualification = it },
                            label = { Text("Qualification (e.g. MD, DM, MS, DNB)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Clinic & Practice Information",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = clinicHospital,
                            onValueChange = { clinicHospital = it },
                            label = { Text("Clinic / Hospital Name *") },
                            modifier = Modifier.fillMaxWidth().testTag("add_doctor_clinic_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Address & Locality *") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone Number") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = preferredTime,
                            onValueChange = { preferredTime = it },
                            label = { Text("Preferred Visiting Hours") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Targeting & Strategic Category",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("A", "B", "C").forEach { cat ->
                                FilterChip(
                                    selected = category == cat,
                                    onClick = { category = cat },
                                    label = { Text("Category $cat") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Strategic MR Notes / Key Products to Pitch") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            if (errorMessage != null) {
                item {
                    Text(
                        text = errorMessage ?: "",
                        color = StatusError,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            item {
                CareOsisPrimaryButton(
                    text = "Save Doctor to Local CRM",
                    onClick = {
                        if (name.isBlank() || clinicHospital.isBlank()) {
                            errorMessage = "Please enter Doctor Name and Clinic Name"
                        } else {
                            isSaving = true
                            scope.launch {
                                val newDoc = DoctorEntity(
                                    id = "DOC-${System.currentTimeMillis().toString().takeLast(4)}",
                                    name = if (!name.startsWith("Dr.")) "Dr. $name" else name,
                                    specialty = specialty,
                                    qualification = qualification,
                                    clinicHospital = clinicHospital,
                                    address = address.ifEmpty { "Local Field Territory" },
                                    phone = phone.ifEmpty { "+91 98000 00000" },
                                    email = email,
                                    preferredVisitingTime = preferredTime,
                                    potentialCategory = category,
                                    priority = priority,
                                    notes = notes,
                                    isSynced = false
                                )
                                repository.insertDoctor(newDoc)
                                onDoctorAdded()
                            }
                        }
                    },
                    isLoading = isSaving,
                    testTag = "save_doctor_button"
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
