package com.example.shareplate.ui.auth.login

import android.widget.Toast
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.shareplate.data.local.SessionManager
import com.example.shareplate.data.supabase.AuthRepository
import com.example.shareplate.ui.auth.component.AuthLogo
import com.example.shareplate.ui.auth.component.AuthTextField
import com.example.shareplate.ui.auth.component.AuthTitle
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: (role: String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val sessionManager = remember { SessionManager(context) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
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
            AuthTitle("Sign In to Share Plate")
            Spacer(modifier = Modifier.height(36.dp))

            AuthTextField(
                label = "Email",
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = null
                },
                keyboardType = KeyboardType.Email
            )
            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                label = "Password",
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                isPassword = !passwordVisible,
                trailing = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                }
            )

            TextButton(
                onClick = {
                    if (email.isBlank()) {
                        errorMessage = "Enter your email to reset your password."
                        return@TextButton
                    }
                    scope.launch {
                        try {
                            authRepository.resetPassword(email, "shareplate://reset")
                                .onSuccess {
                                    Toast.makeText(context, "Reset link sent to your email.", Toast.LENGTH_SHORT).show()
                                }
                                .onFailure { e ->
                                    errorMessage = e.message ?: "Could not send reset link."
                                }
                        } catch (t: Throwable) {
                            errorMessage = "Could not send reset link. Please try again."
                        }
                    }
                }
            ) {
                Text("Forgot password?")
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Please enter both email and password."
                        return@Button
                    }
                    isLoading = true
                    scope.launch {
                        val result = authRepository.signIn(email, password)
                        isLoading = false
                        result.onSuccess { profile ->
                            sessionManager.saveSession(profile.role)
                            onLoginSuccess(profile.role)
                        }.onFailure { e ->
                            errorMessage = e.message ?: "Login failed. Please try again."
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
                    Text("Sign In", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToRegister) {
                Text("Don't have an account? Sign up instead.")
            }
        }
    }
}
