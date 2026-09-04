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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onLoggedOut: () -> Unit,
    onHome: () -> Unit = {},
    onMenu: () -> Unit = {},
    onActivity: () -> Unit = {}
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

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var organisationName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val loaded = authRepository.getCurrentProfile()
            profile = loaded
            loaded?.let {
                name = it.name
                phone = it.phone
                organisationName = it.organisationName ?: ""
                address = it.address ?: ""
            }
        } catch (t: Throwable) {
            errorMessage = t.message ?: "Could not load profile. Please log in again."
            profile = null
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
            Text(errorMessage ?: "Profile not found. Please log in again.")
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth()
            ) {
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

                ProfileField("Name", name, { name = it }, isEditing)
                Spacer(modifier = Modifier.height(12.dp))

                ProfileField("Email", currentProfile.email, {}, editable = false)
                Spacer(modifier = Modifier.height(12.dp))

                ProfileField("Phone", phone, { phone = it }, isEditing)
                Spacer(modifier = Modifier.height(12.dp))

                when (currentProfile.role) {
                    "SELLER" -> {
                        ProfileField("Business / Shop Name", organisationName, { organisationName = it }, isEditing)
                        Spacer(modifier = Modifier.height(12.dp))
                        ProfileField("Business Address", address, { address = it }, isEditing)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    "BUYER" -> {
                        ProfileField("Delivery Address", address, { address = it }, isEditing)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    "NGO" -> {
                        ProfileField("Organization Name", organisationName, { organisationName = it }, isEditing)
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
                                name = name.trim(),
                                phone = phone.trim(),
                                organisationName = organisationName.ifBlank { null },
                                address = address.ifBlank { null }
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

        NavigationBar {
            NavigationBarItem(
                selected = false,
                onClick = onHome,
                icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                label = { Text("Home") }
            )
            NavigationBarItem(
                selected = false,
                onClick = onMenu,
                icon = { Icon(Icons.Filled.List, contentDescription = "Menu") },
                label = { Text("Menu") }
            )
            NavigationBarItem(
                selected = false,
                onClick = onActivity,
                icon = { Icon(Icons.Filled.History, contentDescription = "Activity") },
                label = { Text("Activity") }
            )
            NavigationBarItem(
                selected = true,
                onClick = {},
                icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                label = { Text("Profile") }
            )
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
