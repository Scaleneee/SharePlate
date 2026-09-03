package com.example.shareplate.ui.buyer.profile

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shareplate.data.local.SessionManager
import com.example.shareplate.data.supabase.AuthRepository
import com.example.shareplate.data.supabase.Profile
import com.example.shareplate.ui.buyer.navigation.BuyerBottomBar
import com.example.shareplate.ui.buyer.order.BuyerCartStore
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun BuyerProfileScreen(
    onHomeClick: () -> Unit = {},
    onOrderClick: () -> Unit = {},
    onActivityClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLoggedOut: () -> Unit = {}
) {
    val isPreview = LocalInspectionMode.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val sessionManager = remember { SessionManager(context) }
    var profile by remember { mutableStateOf<Profile?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isEditing by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var isLoggingOut by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var buyerName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var deliveryAddress by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {

        if (isPreview) {

            val previewProfile =
                Profile(
                    userId = "preview-buyer-1",
                    name = "Brian Chew",
                    email = "brian@email.com",
                    phone = "012-3456789",
                    role = "BUYER",
                    organisationName = null,
                    address = "Petaling Jaya, Selangor",
                    profileImageUrl = null,
                    isVerified = true,
                    closingTime = null,
                    createdAt = "2026-09-03T10:00:00+08:00"
                )


            profile =
                previewProfile


            buyerName =
                previewProfile.name


            phoneNumber =
                previewProfile.phone


            deliveryAddress =
                previewProfile.address
                    ?: ""


            isLoading =
                false


            return@LaunchedEffect
        }


        try {

            isLoading = true

            errorMessage = null


            val loadedProfile =
                authRepository
                    .getCurrentProfile()


            if (loadedProfile == null) {

                errorMessage =
                    "Profile not found. Please log in again."

                return@LaunchedEffect
            }
            profile = loadedProfile
            buyerName = loadedProfile.name
            phoneNumber = loadedProfile.phone
            deliveryAddress = loadedProfile.address ?: ""


        } catch (e: Exception) {
            errorMessage = e.message ?: "Unable to load profile."

        } finally {
            isLoading = false
        }
    }


    Scaffold(

        bottomBar = {

            BuyerBottomBar(
                selectedIndex = 3,
                onHomeClick = onHomeClick,
                onOrderClick = onOrderClick,
                onActivityClick = onActivityClick,
                onProfileClick = onProfileClick
            )
        }

    ) { innerPadding ->


        when {
            // LOADING
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),

                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }


            // PROFILE NOT FOUND
            profile == null -> {


                Box(

                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),

                    contentAlignment = Alignment.Center
                ) {


                    Text(

                        text = errorMessage ?: "Profile not found.",

                        color = MaterialTheme
                                .colorScheme
                                .error,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // PROFILE CONTENT
            else -> {
                val currentProfile = profile!!

                Column(

                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // TITLE
                   Text(

                        text = "My Profile",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )


                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )

                    // PROFILE ICON
                    Icon(

                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Buyer Profile",
                        modifier = Modifier.size(105.dp),
                        tint = MaterialTheme
                                .colorScheme
                                .primary
                    )


                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    // BUYER NAME
                  Text(
                        text = buyerName.ifBlank { "Buyer" },
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )


                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )


                    Text(
                        text = "Buyer",
                        fontSize = 13.sp,
                        color = MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )


                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )

                    // EDIT / CANCEL
                    TextButton(

                        onClick = {
                            if (isSaving) {
                                return@TextButton
                            }
                            errorMessage = null
                            successMessage = null

                            if (isEditing) {
                                // Restore original values
                                buyerName = currentProfile.name
                                phoneNumber = currentProfile.phone
                                deliveryAddress = currentProfile.address ?: ""
                            }


                            isEditing = !isEditing
                        },

                        modifier = Modifier.align(Alignment.End)

                    ) {


                        Text(
                            text =
                                if (isEditing) {
                                    "Cancel"
                                } else {
                                    "Edit Profile"
                                }
                        )
                    }


                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )


                    // NAME
                     BuyerProfileField(
                        label = "Name",
                        value = buyerName,
                        onValueChange = { buyerName = it},
                        enabled = isEditing
                    )
                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )


                    // EMAIL
                  BuyerProfileField(

                        label = "Email",
                        value = currentProfile.email,
                        onValueChange = {},
                        enabled = false
                    )


                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    // PHONE NUMBER
                    BuyerProfileField(
                        label = "Phone Number",
                        value = phoneNumber,
                        onValueChange = {phoneNumber = it },
                        enabled = isEditing
                    )


                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )


                    // DELIVERY ADDRESS
                    // In your latest database this uses "address"
                    OutlinedTextField(
                        value = deliveryAddress,
                        onValueChange = { deliveryAddress = it },

                        label = {

                            Text(
                                text = "Delivery Address"
                            )
                        },

                        enabled = isEditing,
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4
                    )


                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )


                    // ROLE
                   BuyerProfileField(
                        label = "Role",
                        value = currentProfile.role,
                        onValueChange = {},
                        enabled = false
                    )

                    // ERROR MESSAGE
                    if (
                        errorMessage != null
                    ) {


                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )


                        Text(

                            text = errorMessage ?: "",

                            modifier = Modifier.fillMaxWidth(),

                            color = MaterialTheme
                                    .colorScheme
                                    .error,

                            fontSize = 13.sp
                        )
                    }

                    // SUCCESS MESSAGE
                    if (
                        successMessage != null
                    ) {


                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )


                        Text(
                            text = successMessage ?: "",
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme
                                    .colorScheme
                                    .primary,

                            fontSize = 13.sp
                        )
                    }


                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )

                    // SAVE CHANGES
                   if (isEditing) {
                        Button(
                            onClick = {
                                errorMessage = null
                                successMessage = null


                                // VALIDATE NAME
                                if (
                                    buyerName
                                        .trim()
                                        .isEmpty()
                                ) {

                                    errorMessage = "Please enter your name."

                                    return@Button
                                }

                                // VALIDATE PHONE
                               if (
                                    phoneNumber
                                        .trim()
                                        .isEmpty()
                                ) {

                                    errorMessage =
                                        "Please enter your phone number."

                                    return@Button
                                }


                                // UPDATE SUPABASE
                                coroutineScope.launch {


                                    isSaving =
                                        true


                                    try {


                                        val updatedProfile =
                                            currentProfile.copy(

                                                name =
                                                    buyerName
                                                        .trim(),

                                                phone =
                                                    phoneNumber
                                                        .trim(),

                                                address =
                                                    deliveryAddress
                                                        .trim()
                                                        .ifBlank {
                                                            null
                                                        }
                                            )


                                        authRepository
                                            .updateProfile(
                                                updatedProfile
                                            )
                                            .onSuccess {


                                                profile =
                                                    updatedProfile


                                                buyerName =
                                                    updatedProfile.name


                                                phoneNumber =
                                                    updatedProfile.phone


                                                deliveryAddress =
                                                    updatedProfile.address
                                                        ?: ""


                                                isEditing =
                                                    false


                                                successMessage =
                                                    "Profile updated successfully."
                                            }
                                            .onFailure { error ->


                                                errorMessage =
                                                    error.message
                                                        ?: "Unable to update profile."
                                            }


                                    } catch (e: Exception) {


                                        errorMessage =
                                            e.message
                                                ?: "Unable to update profile."


                                    } finally {


                                        isSaving =
                                            false
                                    }
                                }
                            },

                            enabled =
                                !isSaving,

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)

                        ) {


                            if (isSaving) {


                                CircularProgressIndicator(

                                    modifier =
                                        Modifier.size(22.dp)
                                )


                            } else {


                                Text(
                                    text =
                                        "Save Changes"
                                )
                            }
                        }


                        Spacer(
                            modifier =
                                Modifier.height(14.dp)
                        )
                    }


                    // =================================================
                    // LOG OUT
                    // =================================================

                    OutlinedButton(

                        onClick = {


                            if (isLoggingOut) {

                                return@OutlinedButton
                            }


                            coroutineScope.launch {


                                isLoggingOut =
                                    true


                                errorMessage =
                                    null


                                try {


                                    // Sign out from Supabase
                                    authRepository
                                        .signOut()


                                    // Clear local remembered role
                                    sessionManager
                                        .clearSession()


                                    // Clear buyer temporary cart
                                    BuyerCartStore
                                        .clearCart()


                                    // Navigate to login later
                                    onLoggedOut()


                                } catch (e: Exception) {


                                    errorMessage =
                                        e.message
                                            ?: "Unable to log out."


                                } finally {


                                    isLoggingOut =
                                        false
                                }
                            }
                        },

                        enabled =
                            !isLoggingOut,

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)

                    ) {


                        if (isLoggingOut) {


                            CircularProgressIndicator(

                                modifier = Modifier.size(22.dp)
                            )


                        } else {


                            Text(
                                text = "Log Out"
                            )
                        }
                    }


                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
        }
    }
}


@Composable
private fun BuyerProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean
) {


    OutlinedTextField(

        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label
            )
        },

        enabled = enabled,
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun BuyerProfileScreenPreview() {

    MaterialTheme {

        BuyerProfileScreen()
    }
}