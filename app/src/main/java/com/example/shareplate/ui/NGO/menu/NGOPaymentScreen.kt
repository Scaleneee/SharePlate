package com.example.shareplate.ui.NGO

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shareplate.ui.NGO.order.NGOCartItem

@Composable
fun NGOPaymentScreen(
    donation: FoodDonation,
    items: List<com.example.shareplate.ui.NGO.order.NGOCartItem>,
    onBackClick: () -> Unit,
    onCompleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text("Payment", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Total: RM0.00",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Payment method",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Text("Free Donation", fontSize = 16.sp, color = Color.DarkGray)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "No payment needed — this is a free food donation.",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onCompleteClick,
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Complete Donation", color = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NGOPaymentScreenPreview() {
    NGOPaymentScreen(
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
        onCompleteClick = {}
    )
}