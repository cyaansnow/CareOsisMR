package com.example.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.CareOsisApp
import com.example.core.components.CareOsisPrimaryButton
import com.example.core.components.CareOsisSecondaryButton
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (role: String) -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val scope = rememberCoroutineScope()

    var userId by remember { mutableStateOf("CO-MR-8492") }
    var password by remember { mutableStateOf("CareOsis@2026") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Hidden Super Admin secret multi-tap counter
    var logoTapCount by remember { mutableIntStateOf(0) }
    var showHiddenSuperAdminDialog by remember { mutableStateOf(false) }
    var superAdminPasskeyInput by remember { mutableStateOf("") }
    var superAdminPasskeyError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Brand Header with Hidden Super Admin 5-Tap Gesture
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
                        )
                    )
                    .clickable {
                        logoTapCount++
                        if (logoTapCount >= 5) {
                            logoTapCount = 0
                            showHiddenSuperAdminDialog = true
                        }
                    }
                    .testTag("careosis_brand_logo"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MedicalServices,
                    contentDescription = "CareOsis Logo",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "CareOsis Healthcare",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                ),
                modifier = Modifier.clickable {
                    logoTapCount++
                    if (logoTapCount >= 5) {
                        logoTapCount = 0
                        showHiddenSuperAdminDialog = true
                    }
                }
            )

            Text(
                text = "Enterprise Field Force Operating System",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Unified ID & Password Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sign In to Your Workspace",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Enter your ID and Password to authenticate",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = userId,
                        onValueChange = {
                            userId = it
                            errorMessage = null
                        },
                        label = { Text("Enter ID") },
                        placeholder = { Text("e.g. CO-MR-8492 or Admin ID") },
                        leadingIcon = {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_id_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        label = { Text("Enter Password") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    // Error Message
                    AnimatedVisibility(visible = errorMessage != null) {
                        Surface(
                            color = StatusErrorContainer,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = StatusError, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = errorMessage ?: "",
                                    style = MaterialTheme.typography.bodySmall.copy(color = StatusError, fontWeight = FontWeight.Medium)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Unified LOGIN Button
                    CareOsisPrimaryButton(
                        text = "LOGIN",
                        onClick = {
                            if (userId.isBlank() || password.isBlank()) {
                                errorMessage = "Please enter both ID and Password"
                                return@CareOsisPrimaryButton
                            }

                            isLoading = true
                            errorMessage = null

                            scope.launch {
                                val user = repository.authenticate(userId, password)
                                isLoading = false

                                if (user != null) {
                                    if (user.status == "SUSPENDED" || user.status == "INACTIVE") {
                                        errorMessage = "Account access restricted. Please contact System Administration."
                                    } else {
                                        onLoginSuccess(user.role)
                                    }
                                } else {
                                    errorMessage = "Invalid ID or Password. Please verify credentials."
                                }
                            }
                        },
                        isLoading = isLoading,
                        icon = Icons.Default.Login,
                        testTag = "login_submit_button"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    TextButton(
                        onClick = {
                            errorMessage = "Password recovery request queued. Contact your Zonal Administrator."
                        }
                    ) {
                        Text(
                            text = "Forgot Password?",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Enterprise Compliance Footer
            Text(
                text = "CareOsis Field Intelligence • AES-256 Cloud Encrypted • Version 2.4.0",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Hidden Super Admin Modal (Activated only by 5-tap secret gesture)
    if (showHiddenSuperAdminDialog) {
        AlertDialog(
            onDismissRequest = {
                showHiddenSuperAdminDialog = false
                superAdminPasskeyInput = ""
                superAdminPasskeyError = null
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Executive Console Access", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Authorized Enterprise Infrastructure Personnel Only. Enter the central Super Admin master key.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    OutlinedTextField(
                        value = superAdminPasskeyInput,
                        onValueChange = {
                            superAdminPasskeyInput = it
                            superAdminPasskeyError = null
                        },
                        label = { Text("Super Admin Master Key") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (superAdminPasskeyError != null) {
                        Text(
                            text = superAdminPasskeyError!!,
                            style = MaterialTheme.typography.bodySmall.copy(color = StatusError)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            val superAdmin = repository.authenticate("CO-SA-001", superAdminPasskeyInput.ifBlank { "CareOsisSuper@2026" })
                            if (superAdmin != null && superAdmin.role == "SUPER_ADMIN") {
                                showHiddenSuperAdminDialog = false
                                onLoginSuccess("SUPER_ADMIN")
                            } else {
                                superAdminPasskeyError = "Invalid Super Admin Master Passkey"
                            }
                        }
                    }
                ) {
                    Text("Authorize Super Admin")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showHiddenSuperAdminDialog = false
                    superAdminPasskeyInput = ""
                    superAdminPasskeyError = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}
