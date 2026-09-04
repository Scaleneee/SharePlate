package com.example.shareplate.ui.NGO

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shareplate.ui.NGO.order.NGOCartItem

@Composable
fun NGOCheckoutScreen(
    donation: FoodDonation,
    items: List<com.example.shareplate.ui.NGO.order.NGOCartItem>,
    onBackClick: () -> Unit,
    onProceedClick: () -> Unit
) {
    var pickupNote by remember { mutableStateOf("") }

    Scaffold { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = Color(0xFFF7F7F7)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                    Text("Checkout", fontSize = 21.sp, fontWeight = FontWeight.Bold)
                }

                HorizontalDivider(color = Color(0xFFE0E0E0))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text("Pickup Details", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        PickupInformationCard(donation = donation, firstItem = items.firstOrNull())
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Your Order", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    items(items) { item ->
                        CheckoutFoodItem(item = item)
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Pickup Note", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = pickupNote,
                            onValueChange = { pickupNote = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Example: I will arrive around 6:30 PM") },
                            minLines = 3,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }

                CheckoutBottomSection(items = items, onContinuePaymentClick = onProceedClick)
            }
        }
    }
}

@Composable
private fun PickupInformationCard(donation: FoodDonation, firstItem: com.example.shareplate.ui.NGO.order.NGOCartItem?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(firstItem?.shopName ?: donation.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Self Pickup", fontSize = 13.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(5.dp))
            Text(firstItem?.pickupTime ?: "Pickup today", fontSize = 13.sp, color = Color(0xFF388E3C))
        }
    }
}

@Composable
private fun CheckoutFoodItem(item: com.example.shareplate.ui.NGO.order.NGOCartItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Text(item.foodName, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("RM %.2f × ${item.quantity}".format(item.price), fontSize = 13.sp, color = Color.DarkGray)
                Text("RM %.2f".format(item.price * item.quantity), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CheckoutBottomSection(
    items: List<com.example.shareplate.ui.NGO.order.NGOCartItem>,
    onContinuePaymentClick: () -> Unit
) {
    val totalItems = items.sumOf { it.quantity }
    val totalPrice = items.sumOf { it.price * it.quantity }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(20.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total Items", fontSize = 14.sp, color = Color.DarkGray)
            Text(totalItems.toString(), fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total Payment", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("RM %.2f".format(totalPrice), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onContinuePaymentClick,
            enabled = items.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Continue to Payment", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NGOCheckoutScreenPreview() {
    NGOCheckoutScreen(
        donation = FoodDonation("Ondo Bakery", "George Town - 5.0 km", 17, listOf("Blueberry Bread - 7"), false, false),
        items = listOf(
            NGOCartItem(
                listingId = 1,
                foodItemId = 1,
                sellerId = "seller-1",
                shopName = "Ondo Bakery",
                foodName = "Blueberry Bread",
                price = 0.0,
                pickupTime = "Pickup today",
                availableQuantity = 7,
                quantity = 2
            )
        ),
        onBackClick = {},
        onProceedClick = {}
    )
}