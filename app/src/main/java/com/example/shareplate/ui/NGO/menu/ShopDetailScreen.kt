package com.example.shareplate.ui.NGO

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class MenuFoodItem(
    val name: String,
    val surplus: Int
)

@Composable
fun ShopDetailScreen(
    donation: FoodDonation,
    onBackClick: () -> Unit,
    onAcceptClick: (List<String>) -> Unit
) {
    val items = donation.foodItems.map { raw ->
        val parts = raw.split(" - ")
        val name = parts.getOrElse(0) { raw }
        val surplus = parts.getOrNull(1)?.trim()?.toIntOrNull() ?: 0
        MenuFoodItem(name = name, surplus = surplus)
    }

    var selectedItems by remember { mutableStateOf(emptyList<String>()) }
    val allSelected = items.isNotEmpty() && selectedItems.size == items.size
    val totalSurplus = donation.availableFood

fun toggle(item: MenuFoodItem) {
        val full = "${item.name} - ${item.surplus}"
        selectedItems = if (selectedItems.contains(full)) {
            selectedItems - full
        } else {
            selectedItems + full
        }
    }

    fun toggleAll() {
        selectedItems = if (allSelected) emptyList() else items.map { "${it.name} - ${it.surplus}" }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("NGO Order", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(82.dp),
                shape = CircleShape,
                color = Color(0xFFFFF4D6)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = donation.name.take(2).uppercase(),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD99B00)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(donation.name, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(2.dp))
                Text("Today's Surplus Food: $totalSurplus", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "Surplus available for consumption, don't miss the food!",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = Color.LightGray)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { toggleAll() }
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = allSelected,
                onCheckedChange = { toggleAll() }
            )
            Text("Select All", fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }

        HorizontalDivider(color = Color.LightGray)

        if (items.isEmpty()) {
            Spacer(modifier = Modifier.height(40.dp))
            Text("No surplus food available", color = Color.Gray)
        } else {
            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { toggle(item) }
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selectedItems.contains("${item.name} - ${item.surplus}"),
                        onCheckedChange = { toggle(item) }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        modifier = Modifier.size(width = 76.dp, height = 68.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFFF4D6)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = item.name.take(2).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD99B00)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(3.dp))
                        Text("Best Before: 2 days • Surplus Food: ${item.surplus}", fontSize = 10.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("FREE", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                HorizontalDivider(color = Color.LightGray)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Total Surplus Food: $totalSurplus",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Selected: ${selectedItems.size} items",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { onAcceptClick(selectedItems) },
            enabled = selectedItems.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Accept Donation")
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NGOOrderPreview() {
    ShopDetailScreen(
        donation = FoodDonation("Ondo Bakery", "George Town - 5.0 km", 17, listOf("Blueberry Bread - 7", "Sausage Bread - 5", "Sweet Donuts - 5"), false, false),
        onBackClick = {},
        onAcceptClick = {}
    )
}