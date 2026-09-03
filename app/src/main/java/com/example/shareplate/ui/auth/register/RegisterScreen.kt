package com.example.shareplate.ui.auth.register

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.shareplate.data.model.UserRole
import com.example.shareplate.data.local.SessionManager
import com.example.shareplate.data.supabase.AuthRepository
import com.example.shareplate.ui.auth.component.AuthLogo
import com.example.shareplate.ui.auth.component.AuthTextField
import com.example.shareplate.ui.auth.component.AuthTitle
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onRegisterSuccess: (role: String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val sessionManager = remember { SessionManager(context) }

    var selectedRole by remember { mutableStateOf(UserRole.SELLER) }
    var roleMenuExpanded by remember { mutableStateOf(false) }

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var businessName by remember { mutableStateOf("") }
    var businessAddress by remember { mutableStateOf("") }
    var deliveryAddress by remember { mutableStateOf("") }
    var organizationName by remember { mutableStateOf("") }
    var organizationRegNo by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            AuthLogo()
            Spacer(modifier = Modifier.height(20.dp))
            AuthTitle("Sign Up to Share Plate")
            Spacer(modifier = Modifier.height(32.dp))

            AuthTextField("Full Name", fullName, { fullName = it; errorMessage = null })
            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField("Email", email, { email = it; errorMessage = null }, keyboardType = KeyboardType.Email)
            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField("Phone Number", phoneNumber, { phoneNumber = it; errorMessage = null }, keyboardType = KeyboardType.Phone)
            Spacer(modifier = Modifier.height(16.dp))

            // Role selector (dropdown, as in the design)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "WHAT WAS YOUR ROLES:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { roleMenuExpanded = true },
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedRole.name.capitalizeWord(),
                                modifier = Modifier.weight(1f)
                            )
                            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Select role")
                        }
                    }
                    DropdownMenu(
                        expanded = roleMenuExpanded,
                        onDismissRequest = { roleMenuExpanded = false }
                    ) {
                        UserRole.entries.forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role.name.capitalizeWord()) },
                                onClick = {
                                    selectedRole = role
                                    roleMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedRole) {
                UserRole.SELLER -> {
                    AuthTextField("Business / Shop Name", businessName, { businessName = it })
                    Spacer(modifier = Modifier.height(16.dp))
                    AuthTextField("Business Address", businessAddress, { businessAddress = it })
                    Spacer(modifier = Modifier.height(16.dp))
                }
                UserRole.BUYER -> {
                    AuthTextField("Delivery Address", deliveryAddress, { deliveryAddress = it })
                    Spacer(modifier = Modifier.height(16.dp))
                }
                UserRole.NGO -> {
                    AuthTextField("Organization Name", organizationName, { organizationName = it })
                    Spacer(modifier = Modifier.height(16.dp))
                    AuthTextField("Registration Number", organizationRegNo, { organizationRegNo = it })
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            AuthTextField("Password", password, { password = it; errorMessage = null }, isPassword = true)
            Spacer(modifier = Modifier.height(16.dp))
            AuthTextField("Confirm Password", confirmPassword, { confirmPassword = it; errorMessage = null }, isPassword = true)

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val validationError = validateRegistration(fullName, email, phoneNumber, password, confirmPassword)
                    if (validationError != null) {
                        errorMessage = validationError
                        return@Button
                    }

                    isLoading = true
                    scope.launch {
                        val result = authRepository.signUp(
                            email = email.trim(),
                            password = password,
                            fullName = fullName.trim(),
                            phoneNumber = phoneNumber.trim(),
                            role = selectedRole.name,
                            businessName = businessName.ifBlank { null },
                            businessAddress = businessAddress.ifBlank { null },
                            deliveryAddress = deliveryAddress.ifBlank { null },
                            organizationName = organizationName.ifBlank { null },
                            organizationRegNo = organizationRegNo.ifBlank { null }
                        )
                        isLoading = false
                        result.onSuccess { profile ->
                            sessionManager.saveSession(profile.role)
                            onRegisterSuccess(profile.role)
                        }.onFailure { e ->
                            val raw = e.message ?: ""
                            errorMessage = if (raw.contains("already", ignoreCase = true) || raw.contains("registered", ignoreCase = true)) {
                                "That email is already registered. Please sign in instead."
                            } else {
                                "Could not create an account. Please try again."
                            }
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Sign Up", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToLogin, modifier = Modifier.fillMaxWidth()) {
                Text("Already have an account? Sign in instead.")
            }
        }
    }
}

private fun validateRegistration(
    fullName: String,
    email: String,
    phoneNumber: String,
    password: String,
    confirmPassword: String
): String? {
    return when {
        fullName.isBlank() -> "Please enter your full name."
        email.isBlank() || !email.contains("@") -> "Please enter a valid email."
        phoneNumber.isBlank() -> "Please enter your phone number."
        password.length < 6 -> "Password must be at least 6 characters."
        password != confirmPassword -> "Passwords do not match."
        else -> null
    }
}

private fun String.capitalizeWord(): String =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
