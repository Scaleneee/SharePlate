package com.example.shareplate.ui.seller.menu

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shareplate.ui.theme.SharePlateTheme

@Preview
@Composable
fun AddFoodScreenPreview() {
    SharePlateTheme {
        AddFoodScreen({}, { foodName, category, originalPrice, bestBeforeDays, isActive -> })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodScreen(
    onBackClick: () -> Unit,
    onSaveClick: (
        foodName: String,
        category: String,
        originalPrice: String,
        bestBeforeDays: String,
        isActive: Boolean
    ) -> Unit
) {
    // use to store the user input
    var foodName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var originalPrice by remember { mutableStateOf("") }
    var bestBeforeDays by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add Food",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(12.dp)
                    )
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
                .padding(innerPadding)
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Food image
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
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
            // Food name
            OutlinedTextField(
                value = foodName,
                onValueChange = {
                    foodName = it
                },
                label = {
                    Text("Food Name")
                },
                singleLine = true,
                modifier =
                    Modifier.fillMaxWidth()
            )

            // Category
            OutlinedTextField(
                value = category,
                onValueChange = {
                    category = it
                },
                label = {
                    Text("Category")
                },
                singleLine = true,
                modifier =
                    Modifier.fillMaxWidth()
            )

            // Price
            OutlinedTextField(
                value = originalPrice,
                onValueChange = { newValue ->

                    if (
                        newValue.all {
                            it.isDigit() || it == '.'
                        }
                    ) {
                        originalPrice = newValue
                    }
                },
                label = {
                    Text("Original Price")
                },
                prefix = {
                    Text("RM ")
                },
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Decimal
                    ),
                singleLine = true,
                modifier =
                    Modifier.fillMaxWidth()
            )

            // Best before
            OutlinedTextField(
                value = bestBeforeDays,
                onValueChange = { newValue ->

                    if (
                        newValue.all {
                            it.isDigit()
                        }
                    ) {
                        bestBeforeDays = newValue
                    }
                },
                label = {
                    Text("Best Before")
                },
                suffix = {
                    Text("days")
                },
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Number
                    ),
                singleLine = true,
                modifier =
                    Modifier.fillMaxWidth()
            )

            // Active
            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.Absolute.Right
            ) {
                Text(
                    text = "Active",
                    style =
                        MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.width(12.dp))
                Switch(
                    checked = isActive,
                    onCheckedChange = {
                        isActive = it
                    }
                )
            }

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Button(
                onClick = {
                    onSaveClick(
                        foodName,
                        category,
                        originalPrice,
                        bestBeforeDays,
                        isActive
                    )
                },
                enabled =
                    foodName.isNotBlank() &&
                            category.isNotBlank() &&
                            originalPrice.toDoubleOrNull() != null &&
                            bestBeforeDays.toIntOrNull() != null,
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text("Save Food")
            }
        }
    }
}