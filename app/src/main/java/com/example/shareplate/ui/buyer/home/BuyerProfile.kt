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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shareplate.data.local.SessionManager
import com.example.shareplate.data.supabase.Profile
import com.example.shareplate.ui.buyer.BuyerViewModel
import com.example.shareplate.ui.buyer.navigation.BuyerBottomBar
import com.example.shareplate.ui.theme.SharePlateTheme


@Composable
fun BuyerProfileScreen(

    buyerViewModel: BuyerViewModel? = null,

    onHomeClick: () -> Unit = {},

    onOrderClick: () -> Unit = {},

    onActivityClick: () -> Unit = {},

    onProfileClick: () -> Unit = {},

    onLoggedOut: () -> Unit = {}

) {

    val isPreview = LocalInspectionMode.current


    val context = LocalContext.current


    val sessionManager = remember {

        SessionManager(
            context
        )
    }


    // VIEWMODEL
    val actualViewModel: BuyerViewModel? =

        if (isPreview) {

            null

        } else {

            buyerViewModel ?: viewModel()
        }


    // VIEWMODEL STATES
    val profileState = actualViewModel?.profile?.collectAsState()


    val loadingState = actualViewModel?.isLoading?.collectAsState()


    val errorState = actualViewModel?.errorMessage?.collectAsState()


    val successState = actualViewModel?.successMessage?.collectAsState()


    // PREVIEW PROFILE
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


    val profile =

        if (isPreview) {

            previewProfile

        } else {

            profileState?.value
        }


    val isLoading =

        if (isPreview) {

            false

        } else {

            loadingState?.value ?: false
        }


    val errorMessage =

        if (isPreview) {

            null

        } else {

            errorState?.value
        }


    val successMessage =

        if (isPreview) {

            null

        } else {

            successState?.value
        }


    // EDITING STATE
    var isEditing by remember {

        mutableStateOf(false)
    }


    var buyerName by remember {

        mutableStateOf("")
    }


    var phoneNumber by remember {

        mutableStateOf("")
    }


    var deliveryAddress by remember {

        mutableStateOf("")
    }


    // LOAD PROFILE
    LaunchedEffect(
        actualViewModel
    ) {

        if (!isPreview) {

            actualViewModel?.loadProfile()
        }
    }


    // COPY PROFILE DATA TO FORM
    LaunchedEffect(
        profile?.userId, profile?.name, profile?.phone, profile?.address
    ) {

        if (profile != null && !isEditing) {

            buyerName = profile.name


            phoneNumber = profile.phone


            deliveryAddress = profile.address ?: ""
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


        // INITIAL LOADING
        if (isLoading && profile == null) {


            Box(

                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        innerPadding
                    ),

                contentAlignment = Alignment.Center

            ) {


                CircularProgressIndicator()
            }


            return@Scaffold
        }


        // PROFILE NOT FOUND
        if (profile == null) {


            Box(

                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        innerPadding
                    )
                    .padding(
                        24.dp
                    ),

                contentAlignment = Alignment.Center

            ) {


                Text(

                    text = errorMessage ?: "Profile not found.",

                    color = MaterialTheme.colorScheme.error,

                    textAlign = TextAlign.Center
                )
            }


            return@Scaffold
        }


        // PROFILE CONTENT
        val currentProfile = profile


        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(
                    innerPadding
                )
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(

                    horizontal = 24.dp,

                    vertical = 24.dp
                ),

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

                modifier = Modifier.height(
                    24.dp
                )
            )


            // PROFILE ICON
            Icon(

                imageVector = Icons.Default.AccountCircle,

                contentDescription = "Buyer Profile",

                modifier = Modifier.size(
                    105.dp
                ),

                tint = MaterialTheme.colorScheme.primary
            )


            Spacer(

                modifier = Modifier.height(
                    10.dp
                )
            )


            // BUYER NAME
            Text(

                text = buyerName.ifBlank {

                        "Buyer"
                    },

                fontSize = 21.sp,

                fontWeight = FontWeight.Bold
            )


            Spacer(

                modifier = Modifier.height(
                    4.dp
                )
            )


            Text(

                text = "Buyer",

                fontSize = 13.sp,

                color = MaterialTheme.colorScheme.onSurfaceVariant
            )


            Spacer(

                modifier = Modifier.height(
                    24.dp
                )
            )


            // EDIT / CANCEL
            TextButton(

                onClick = {

                    if (isLoading) {

                        return@TextButton
                    }


                    actualViewModel?.clearError()


                    actualViewModel?.clearSuccessMessage()


                    if (isEditing) {


                        // Restore original data
                        buyerName = currentProfile.name


                        phoneNumber = currentProfile.phone


                        deliveryAddress = currentProfile.address ?: ""
                    }


                    isEditing = !isEditing
                },

                modifier = Modifier.align(
                    Alignment.End
                )

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

                modifier = Modifier.height(
                    4.dp
                )
            )


            // NAME
            BuyerProfileField(

                label = "Name",

                value = buyerName,

                onValueChange = {

                    buyerName = it
                },

                enabled = isEditing
            )


            Spacer(

                modifier = Modifier.height(
                    14.dp
                )
            )


            // EMAIL
            BuyerProfileField(

                label = "Email",

                value = currentProfile.email,

                onValueChange = {},

                enabled = false
            )


            Spacer(

                modifier = Modifier.height(
                    14.dp
                )
            )

            // PHONE
            BuyerProfileField(

                label = "Phone Number",

                value = phoneNumber,

                onValueChange = {

                    phoneNumber = it
                },

                enabled = isEditing
            )


            Spacer(

                modifier = Modifier.height(
                    14.dp
                )
            )


            // ADDRESS
            OutlinedTextField(

                value = deliveryAddress,

                onValueChange = {

                    deliveryAddress = it
                },

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

                modifier = Modifier.height(
                    14.dp
                )
            )


            // ROLE
            BuyerProfileField(

                label = "Role",

                value = currentProfile.role,

                onValueChange = {},

                enabled = false
            )


            // ERROR
            if (errorMessage != null) {


                Spacer(

                    modifier = Modifier.height(
                        16.dp
                    )
                )


                Text(

                    text = errorMessage,

                    modifier = Modifier.fillMaxWidth(),

                    color = MaterialTheme.colorScheme.error,

                    fontSize = 13.sp
                )
            }


            // SUCCESS
            if (successMessage != null) {


                Spacer(

                    modifier = Modifier.height(
                        16.dp
                    )
                )


                Text(

                    text = successMessage,

                    modifier = Modifier.fillMaxWidth(),

                    color = MaterialTheme.colorScheme.primary,

                    fontSize = 13.sp
                )
            }


            Spacer(

                modifier = Modifier.height(
                    24.dp
                )
            )


            // SAVE CHANGES
            if (isEditing) {


                Button(

                    onClick = {


                        actualViewModel?.clearError()


                        actualViewModel?.clearSuccessMessage()


                        // VALIDATE NAME
                        if (buyerName.trim().isEmpty()) {

                            return@Button
                        }


                        // VALIDATE PHONE
                        if (phoneNumber.trim().isEmpty()) {

                            return@Button
                        }


                        // UPDATE THROUGH VIEWMODEL
                        actualViewModel?.updateProfile(

                                name = buyerName,

                                phone = phoneNumber,

                                address = deliveryAddress,

                                onSuccess = {

                                    isEditing = false
                                })
                    },

                    enabled = !isLoading,

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            52.dp
                        )

                ) {


                    if (isLoading) {


                        CircularProgressIndicator(

                            modifier = Modifier.size(
                                22.dp
                            )
                        )


                    } else {


                        Text(

                            text = "Save Changes"
                        )
                    }
                }


                Spacer(

                    modifier = Modifier.height(
                        14.dp
                    )
                )
            }


            // LOGOUT
            OutlinedButton(

                onClick = {


                    if (isLoading) {

                        return@OutlinedButton
                    }


                    actualViewModel?.signOut(

                            onSuccess = {


                                // Clear locally remembered role
                                sessionManager.clearSession()


                                // Go back to login
                                onLoggedOut()
                            })
                },

                enabled = !isLoading,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        52.dp
                    )

            ) {


                if (isLoading && !isEditing) {


                    CircularProgressIndicator(

                        modifier = Modifier.size(
                            22.dp
                        )
                    )


                } else {


                    Text(

                        text = "Log Out"
                    )
                }
            }


            Spacer(

                modifier = Modifier.height(
                    24.dp
                )
            )
        }
    }
}

// PROFILE FIELD
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
    showBackground = true, showSystemUi = true
)
@Composable
fun BuyerProfileScreenPreview() {


    SharePlateTheme(
        dynamicColor = false
    ) {


        BuyerProfileScreen()
    }
}