package com.example.shareplate.ui.NGO

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shareplate.ui.NGO.order.NGOCartItem
import com.example.shareplate.ui.NGO.order.NGOCartStore

@Composable
fun NGOCartScreen(
    onBackClick: () -> Unit,
    onCheckoutClick: () -> Unit
) {
    val cartItems = NGOCartStore.cartItems

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
                    IconButton(onClick = onBackClick) {
                        Text(
                            "←", fontSize = 26.sp, fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        "My Cart", fontSize = 21.sp, fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    if (cartItems.isNotEmpty()) {
                        Text("${NGOCartStore.getTotalQuantity()} items"
                            , fontSize = 13.sp
                            , color = Color.Gray)
                    }
                }

                HorizontalDivider(color = Color(0xFFE0E0E0))

                if (cartItems.isEmpty()) {
                    EmptyCartContent(onBackClick = onBackClick)
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(
                            horizontal = 16.dp
                            , vertical = 16.dp
                        ),

                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(cartItems) { index, item ->
                            NGOCartItemCard(
                                item = item,
                                onIncrease = { NGOCartStore.increaseQuantity(index) },
                                onDecrease = { NGOCartStore.decreaseQuantity(index) },
                                onRemove = { NGOCartStore.removeItem(index) }
                            )
                        }
                    }
                    NGOCartSummary(onCheckoutClick = onCheckoutClick)
                }
            }
        }
    }
}

@Composable
private fun EmptyCartContent(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🛒", fontSize = 60.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Text("Your cart is empty", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))

            Text("Add surplus food from a shop first.", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onBackClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Continue Shopping", color = Color.White)
            }
        }
    }
}

@Composable
private fun NGOCartItemCard(
    item: NGOCartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp))
        {
            Text(item.shopName, fontSize = 13.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(4.dp))

            Text(item.foodName, fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black)

            Spacer(modifier = Modifier.height(5.dp))

            Text(item.pickupTime, fontSize = 12.sp, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(5.dp))

            Text("${item.availableQuantity} available", fontSize = 12.sp, color = Color(0xFF388E3C))

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("RM %.2f".format(item.price),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(onClick = onDecrease
                        , modifier = Modifier.size(42.dp)
                        , contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("−", fontSize = 19.sp)
                    }
                    Text(
                        item.quantity.toString()
                        , modifier = Modifier.padding(horizontal = 14.dp)
                        , fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    OutlinedButton(
                        onClick = onIncrease,
                        enabled = item.quantity < item.availableQuantity,
                        modifier = Modifier.size(42.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("+", fontSize = 19.sp)
                    }
                }
            }

            if (item.quantity >= item.availableQuantity) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Maximum available quantity reached"
                    , fontSize = 11.sp
                    , color = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = Color(0xFFEAEAEA))

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Subtotal", fontSize = 14.sp, color = Color.Gray)
                Text("RM %.2f".format(item.price * item.quantity)
                    , fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onRemove,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Remove", color = Color.Red)
            }
        }
    }
}

@Composable
private fun NGOCartSummary(onCheckoutClick: () -> Unit) {
    Column(
        modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)
        .padding(20.dp)
    ) {
        Text(
            "Order Summary",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

            Text(
                "Total Items", fontSize = 15.sp, color = Color.DarkGray
            )

            Text(
                NGOCartStore.getTotalQuantity().toString(),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total", fontSize = 18.sp, fontWeight = FontWeight.Bold)

            Text("RM %.2f".format(NGOCartStore.getTotalPrice()),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50))
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = onCheckoutClick,
            enabled = !NGOCartStore.isCartEmpty(),
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Proceed to Checkout",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NGOCartScreenPreview() {
    NGOCartScreen(onBackClick = {}, onCheckoutClick = {})
}