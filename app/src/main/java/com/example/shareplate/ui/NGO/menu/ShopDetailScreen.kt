package com.example.assignment.ngo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ShopDetailScreen(
    donation: FoodDonation,
    onBackClick: () -> Unit
) {
    var selectedItems by remember { mutableStateOf(listOf<String>()) }
    var accepted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Button(onClick = onBackClick) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(donation.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("FREE DONATION")

        Spacer(modifier = Modifier.height(15.dp))

        Text("Food donated today: ${donation.availableFood} items")
        Text("Location: ${donation.location}")
        Text("Available at: 9:50 PM")
        Text("Pickup: 10:00 - 10:30 PM")

        Spacer(modifier = Modifier.height(20.dp))

        Text("Select the food your NGO needs", fontWeight = FontWeight.Bold)

        donation.foodItems.forEach { item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = selectedItems.contains(item),
                    onCheckedChange = { isChecked ->
                        selectedItems = if (isChecked) {
                            selectedItems + item
                        } else {
                            selectedItems - item
                        }
                    }
                )
                Text(item)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { accepted = true },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedItems.isNotEmpty()
        ) {
            Text("Accept Donation")
        }

        if (accepted) {
            Text("Donation accepted: ${selectedItems.joinToString()}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShopDetailScreenPreview(){
    val sampleDonation = FoodDonation(
        name = "Sunrise Bakery",
        location = "Penang - 1.2 km",
        availableFood = 25,
        foodItems = listOf("Bread - 10", "Croissant - 8", "Muffin - 7"),
        nearby = true,
        liked = false
    )

    ShopDetailScreen(
        donation = sampleDonation,
        onBackClick = {}
    )
}
