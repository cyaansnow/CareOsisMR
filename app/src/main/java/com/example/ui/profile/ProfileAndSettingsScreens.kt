package com.example.ui.profile

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.CareOsisApp
import com.example.core.ai.LocalCareOsisAIEngine
import com.example.core.ai.ObjectionResolution
import com.example.core.components.*
import com.example.data.local.entity.MRProfileEntity
import com.example.ui.navigation.Destinations
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val scope = rememberCoroutineScope()
    val profile by repository.getProfile().collectAsStateWithLifecycle(initialValue = null)
    val pendingSyncCount by repository.getPendingSyncCount().collectAsStateWithLifecycle(initialValue = 0)
    var isSyncing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "MR Profile & Territory",
                subtitle = profile?.empId ?: "CareOsis MR",
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
            // Profile Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = ClinicalWhite,
                                modifier = Modifier.size(44.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = profile?.name ?: "Aman Chhabra",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "${profile?.designation ?: "Senior Medical Representative"} • ${profile?.empId ?: "CO-MR-8492"}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = EmeraldPrimary, fontWeight = FontWeight.Medium)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        CareOsisStatusChip(
                            text = "${profile?.level ?: "Expert MR"} (${profile?.trainingProgressPercent ?: 78}% Certified)",
                            containerColor = GoldContainer,
                            contentColor = OnGoldContainer
                        )
                    }
                }
            }

            // Sync Status & Trigger
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Offline Sync Engine",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = if (pendingSyncCount > 0) "$pendingSyncCount transactions waiting in queue" else "All field data synced with HQ cloud",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                            Icon(
                                imageVector = if (pendingSyncCount > 0) Icons.Default.SyncProblem else Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = if (pendingSyncCount > 0) GoldDark else EmeraldPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        CareOsisSecondaryButton(
                            text = if (isSyncing) "Synchronizing..." else "Synchronize Now with Server",
                            onClick = {
                                isSyncing = true
                                scope.launch {
                                    repository.performSync()
                                    isSyncing = false
                                }
                            },
                            icon = Icons.Default.Sync
                        )
                    }
                }
            }

            // Territory & Reporting Details
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Territory & Reporting Structure",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ProfileDetailRow("Territory / Beat", profile?.territory ?: "North Delhi (Rohini & Pitampura)")
                        ProfileDetailRow("Division", "CareOsis Core Formulations")
                        ProfileDetailRow("Reporting Manager", profile?.managerName ?: "Vikram Malhotra (Area Sales Manager)")
                        ProfileDetailRow("Mobile Number", profile?.phone ?: "+91 98765 43210")
                        ProfileDetailRow("Email", profile?.email ?: "aman.chhabra@careosis.com")
                    }
                }
            }

            // Settings & Tools Links
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        MenuRow(Icons.Default.AdminPanelSettings, "HQ Executive Admin Portal") { onNavigate(Destinations.HQ_DASHBOARD) }
                        MenuRow(Icons.Default.Settings, "Application Settings") { onNavigate(Destinations.SETTINGS) }
                        MenuRow(Icons.Default.HelpOutline, "Help & HQ Scientific Desk") { onNavigate(Destinations.HELP) }
                        MenuRow(Icons.Default.SmartToy, "AI Prescriber Objection Solver") { onNavigate(Destinations.HELP) }
                    }
                }
            }

            item {
                CareOsisPrimaryButton(
                    text = "Sign Out of Territory",
                    onClick = onLogout,
                    icon = Icons.Default.Logout,
                    testTag = "logout_button"
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
    }
}

@Composable
private fun MenuRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), modifier = Modifier.weight(1f))
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val scope = rememberCoroutineScope()
    val notifications by repository.getAllNotifications().collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "Field Notifications",
                subtitle = "${notifications.size} Updates & Alerts",
                onBackClick = onBackClick,
                actions = {
                    TextButton(onClick = {
                        scope.launch { repository.markAllNotificationsAsRead() }
                    }) {
                        Text("Mark All Read", color = ClinicalWhite, style = MaterialTheme.typography.labelSmall)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(notifications, key = { it.id }) { notif ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope.launch { repository.markNotificationAsRead(notif.id) }
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (notif.isRead) MaterialTheme.colorScheme.surface else EmeraldContainer.copy(alpha = 0.35f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = notif.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            CareOsisStatusChip(
                                text = notif.type,
                                containerColor = if (notif.type == "DISPATCH") EmeraldContainer else GoldContainer,
                                contentColor = if (notif.type == "DISPATCH") OnEmeraldContainer else OnGoldContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = notif.message, style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = notif.timeFormatted,
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    var autoSyncEnabled by remember { mutableStateOf(true) }
    var biometricEnabled by remember { mutableStateOf(true) }
    var offlineMediaCache by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "Application Settings",
                subtitle = "Preferences & Storage",
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
                        Text(text = "Data & Connectivity", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(10.dp))

                        SettingToggle("Auto-Sync on Network Available", "Uploads doctor visits and orders automatically in background.", autoSyncEnabled) {
                            autoSyncEnabled = it
                        }
                        SettingToggle("Preload Masterclass Media", "Saves all 19 product dossiers and video visualizers locally.", offlineMediaCache) {
                            offlineMediaCache = it
                        }
                        SettingToggle("Biometric Screen Lock", "Require fingerprint / face auth before accessing CRM data.", biometricEnabled) {
                            biometricEnabled = it
                        }
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
                        Text(text = "App Build & Database", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Version: 1.0.0 (Enterprise MR Release)", style = MaterialTheme.typography.bodySmall)
                        Text(text = "Database: Room 2.6.1 SQLite Offline-First", style = MaterialTheme.typography.bodySmall)
                        Text(text = "Security: AES-256 Room SQLCipher Ready", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingToggle(title: String, desc: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            Text(text = desc, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary, checkedTrackColor = EmeraldContainer)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(
    onBackClick: () -> Unit
) {
    val aiEngine = remember { LocalCareOsisAIEngine() }
    val scope = rememberCoroutineScope()

    var doctorObjectionInput by remember { mutableStateOf("Doctor says Booster is too expensive compared to standard tablets.") }
    var aiResolution by remember { mutableStateOf<ObjectionResolution?>(null) }
    var isThinking by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "HQ Support & AI Desk",
                subtitle = "Scientific & Field Assistance",
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
            // AI Doctor Objection Solver Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.5.dp, GoldMetallic)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(GoldContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.SmartToy, contentDescription = null, tint = GoldDark, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "AI Doctor Objection Solver",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                                )
                                Text(
                                    text = "Instant clinical counters approved by CareOsis Medical Affairs",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = doctorObjectionInput,
                            onValueChange = { doctorObjectionInput = it },
                            label = { Text("What objection did the doctor raise?") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CareOsisPrimaryButton(
                            text = if (isThinking) "Generating Counter-Pitch..." else "Generate AI Clinical Counter-Pitch",
                            onClick = {
                                isThinking = true
                                scope.launch {
                                    aiResolution = aiEngine.resolveDoctorObjection("Booster", doctorObjectionInput)
                                    isThinking = false
                                }
                            },
                            isLoading = isThinking,
                            icon = Icons.Default.Psychology
                        )

                        if (aiResolution != null) {
                            val res = aiResolution!!
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = EmeraldContainer
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "Suggested Pitch to Doctor:",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldDark)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "\"${res.suggestedPitch}\"",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium, color = OnEmeraldContainer)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Scientific Evidence: ${res.scientificCounterPoint}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = EmeraldDark)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Recommended Follow-up: ${res.followUpAction}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = GoldDark, fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // HQ Emergency Contacts
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "HQ Emergency Contacts", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(10.dp))
                        ProfileDetailRow("Medical Affairs Desk", "+91 11 4000 8801")
                        ProfileDetailRow("Supply Chain & Dispatch", "+91 11 4000 8802")
                        ProfileDetailRow("Incentive & Payroll Desk", "+91 11 4000 8803")
                    }
                }
            }
        }
    }
}
