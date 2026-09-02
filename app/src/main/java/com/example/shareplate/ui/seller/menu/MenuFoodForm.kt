package com.example.shareplate.ui.seller.menu

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

/**
 * reusable food information form for add food and edit food
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuFoodForm(
    // add food or edit food
    title: String,
    // these parameters use for edit food
    initialFoodName: String = "",
    initialCategory: String = "",
    initialPrice: String = "",
    initialBestBeforeDays: String = "",
    initialImageUri: String? = null,
    initialIsActive: Boolean = true,
    buttonText: String,
    onBackClick: () -> Unit,
    onSubmit: (
        foodName: String,
        category: String,
        originalPrice: String,
        bestBeforeDays: String,
        imageURI: String?,
        isActive: Boolean
    ) -> Unit
) {
    // use to store the user input
    var foodName by remember(initialFoodName) {
        mutableStateOf(initialFoodName)
    }

    var category by remember(initialCategory) {
        mutableStateOf(initialCategory)
    }

    var price by remember(initialPrice) {
        mutableStateOf(initialPrice)
    }

    var bestBeforeDays by remember(initialBestBeforeDays) {
        mutableStateOf(initialBestBeforeDays)
    }

    var imageUri by remember(initialImageUri) {
        mutableStateOf(initialImageUri)
    }

    var isActive by remember(initialIsActive) {
        mutableStateOf(initialIsActive)
    }

    val photoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            imageUri = uri?.toString()
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(title)
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Food image
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                onClick = {
                    // ask user to select an image
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        // if user selected an image
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Selected food image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                    } else {
                        // if no image selected
                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector =
                                    Icons.Default.AddPhotoAlternate,
                                contentDescription =
                                    "Add Food Image",
                                modifier =
                                    Modifier.size(40.dp)
                            )
                            Spacer(
                                modifier =
                                    Modifier.height(8.dp)
                            )
                            Text("Add Food Image")
                        }
                    }
                }
            }

            OutlinedTextField(
                value = foodName,
                onValueChange = {
                    foodName = it
                },
                label = {
                    Text("Food Name")
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = category,
                onValueChange = {
                    category = it
                },
                label = {
                    Text("Category")
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = price,
                onValueChange = { newValue ->
                    // validate the price
                    if (
                        newValue.matches(
                            Regex("""^\d*\.?\d{0,2}$""")
                        )
                    ) {
                        price = newValue
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                singleLine = true,
                label = {
                    Text("Original Price")
                },
                prefix = {
                    Text("RM ")
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = bestBeforeDays,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) {
                        bestBeforeDays = it
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true,
                label = {
                    Text("Best Before")
                },
                suffix = {
                    Text("days")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.Absolute.Right
            ) {
                Text("Active")
                Spacer(Modifier.width(12.dp))
                Switch(
                    checked = isActive,
                    onCheckedChange = {
                        isActive = it
                    }
                )
            }
            // to decide whether the button enable or not
            val isFormValid =
                foodName.isNotBlank() &&
                        category.isNotBlank() &&
                        price.toDoubleOrNull() != null &&
                        price.toDouble() > 0 &&
                        bestBeforeDays.toIntOrNull() != null &&
                        bestBeforeDays.toInt() > 0

            Button(
                onClick = {
                    onSubmit(
                        foodName,
                        category,
                        price,
                        bestBeforeDays,
                        imageUri,
                        isActive
                    )
                },
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(buttonText)
            }
        }
    }
}