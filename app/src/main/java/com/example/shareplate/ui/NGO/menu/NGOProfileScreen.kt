package com.example.shareplate.ui.NGO

import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shareplate.data.remote.SupabaseProvider
import com.example.shareplate.data.local.SessionManager
import com.example.shareplate.data.supabase.AuthRepository
import com.example.shareplate.data.supabase.Profile
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

@Composable
fun NGOProfileScreen(
    onHomeClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onActivityClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val sessionManager = remember { SessionManager(context) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var organisation by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var edit by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        authRepository.getCurrentProfile()?.let { p ->
            name = p.name
            email = p.email
            phone = p.phone ?: ""
            organisation = p.organisationName ?: ""
            address = p.address ?: ""
        }
        isLoading = false
    }

    Scaffold(
        bottomBar = {
            NGOBottomBar(
                selectedIndex = 3,
                onHomeClick = onHomeClick,
                onMenuClick = onMenuClick,
                onActivityClick = onActivityClick,
                onProfileClick = onProfileClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("My Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(24.dp))

            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "NGO Profile",
                modifier = Modifier.size(105.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text(name.ifBlank { "NGO User" }, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("NGO", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(
                onClick = { edit = !edit },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(if (edit) "Cancel" else "Edit Profile")
            }

            Spacer(modifier = Modifier.height(4.dp))

            NGOProfileField(label = "Name", value = name, onValueChange = { name = it }, enabled = edit)
            Spacer(modifier = Modifier.height(14.dp))
            NGOProfileField(label = "Email", value = email, onValueChange = {}, enabled = false)
            Spacer(modifier = Modifier.height(14.dp))
            NGOProfileField(label = "Phone Number", value = phone, onValueChange = { phone = it }, enabled = edit)
            Spacer(modifier = Modifier.height(14.dp))
            NGOProfileField(label = "Organisation", value = organisation, onValueChange = { organisation = it }, enabled = edit)
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                enabled = edit,
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(14.dp))

            NGOProfileField(label = "Role", value = "NGO", onValueChange = {}, enabled = false)

            if (message != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(message ?: "", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (edit) {
                Button(
                    onClick = {
                        scope.launch {
                            val userId = SupabaseProvider.client.auth.currentUserOrNull()?.id
                            if (userId == null) {
                                message = "Please log in to save."
                                return@launch
                            }
                            val base = authRepository.getCurrentProfile()
                            val updated = Profile(
                                userId = userId,
                                name = name.trim(),
                                email = base?.email ?: email,
                                phone = phone.trim(),
                                role = base?.role ?: "NGO",
                                organisationName = organisation.ifBlank { null },
                                address = address.ifBlank { null }
                            )
                            authRepository.updateProfile(updated)
                                .onSuccess {
                                    message = "Profile updated."
                                    edit = false
                                }
                                .onFailure {
                                    message = "Could not update profile."
                                }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Save Changes")
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        authRepository.signOut()
                        sessionManager.clearSession()
                        onLogout()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Log Out")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun NGOProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        enabled = enabled,
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NGOProfileScreenPreview() {
    NGOProfileScreen()
}
