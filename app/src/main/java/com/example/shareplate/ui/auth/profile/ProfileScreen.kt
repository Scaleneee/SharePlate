package com.example.shareplate.ui.auth.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.shareplate.data.local.SessionManager
import com.example.shareplate.data.supabase.AuthRepository
import com.example.shareplate.data.supabase.Profile
import com.example.shareplate.ui.auth.component.AuthLogo
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onLoggedOut: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val sessionManager = remember { SessionManager(context) }

    var profile by remember { mutableStateOf<Profile?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isEditing by remember { mutableStateOf(false) }
    var saveMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var businessName by remember { mutableStateOf("") }
    var businessAddress by remember { mutableStateOf("") }
    var deliveryAddress by remember { mutableStateOf("") }
    var organizationName by remember { mutableStateOf("") }
    var organizationRegNo by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val loaded = authRepository.getCurrentProfile()
        profile = loaded
        loaded?.let {
            fullName = it.fullName
            phoneNumber = it.phoneNumber
            businessName = it.businessName ?: ""
            businessAddress = it.businessAddress ?: ""
            deliveryAddress = it.deliveryAddress ?: ""
            organizationName = it.organizationName ?: ""
            organizationRegNo = it.organizationRegNo ?: ""
        }
        isLoading = false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val currentProfile = profile
    if (currentProfile == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Profile not found. Please log in again.")
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth()
        ) {
            AuthLogo(modifier = Modifier.align(Alignment.CenterHorizontally))

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Profile",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { isEditing = !isEditing }) {
                    Text(if (isEditing) "Cancel" else "Edit")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Profile picture",
                    modifier = Modifier.size(96.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = currentProfile.role.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            ProfileField("Full Name", fullName, { fullName = it }, isEditing)
            Spacer(modifier = Modifier.height(12.dp))

            ProfileField("Email", currentProfile.email, {}, editable = false)
            Spacer(modifier = Modifier.height(12.dp))

            ProfileField("Phone Number", phoneNumber, { phoneNumber = it }, isEditing)
            Spacer(modifier = Modifier.height(12.dp))

            when (currentProfile.role) {
                "SELLER" -> {
                    ProfileField("Business Name", businessName, { businessName = it }, isEditing)
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileField("Business Address", businessAddress, { businessAddress = it }, isEditing)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                "BUYER" -> {
                    ProfileField("Delivery Address", deliveryAddress, { deliveryAddress = it }, isEditing)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                "NGO" -> {
                    ProfileField("Organization Name", organizationName, { organizationName = it }, isEditing)
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileField("Registration Number", organizationRegNo, { organizationRegNo = it }, isEditing)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (saveMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = saveMessage ?: "",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isEditing) {
                Button(
                    onClick = {
                        val updated = currentProfile.copy(
                            fullName = fullName.trim(),
                            phoneNumber = phoneNumber.trim(),
                            businessName = businessName.ifBlank { null },
                            businessAddress = businessAddress.ifBlank { null },
                            deliveryAddress = deliveryAddress.ifBlank { null },
                            organizationName = organizationName.ifBlank { null },
                            organizationRegNo = organizationRegNo.ifBlank { null }
                        )
                        scope.launch {
                            authRepository.updateProfile(updated)
                                .onSuccess {
                                    profile = updated
                                    isEditing = false
                                    saveMessage = "Profile updated successfully."
                                }
                                .onFailure { e ->
                                    errorMessage = e.message ?: "Could not update profile."
                                }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        authRepository.signOut()
                        sessionManager.clearSession()
                        onLoggedOut()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log Out")
            }
        }
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    editable: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        enabled = editable,
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}
