package com.example.shareplate.ui.buyer.order

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
import com.example.shareplate.ui.buyer.home.navigation.BuyerBottomBar
import com.example.shareplate.ui.theme.SharePlateTheme


@Composable
fun BuyerCartScreen(

    onBackClick: () -> Unit = {},

    onCheckoutClick: () -> Unit = {},

    onHomeClick: () -> Unit = {},

    onOrderClick: () -> Unit = {},

    onActivityClick: () -> Unit = {},

    onProfileClick: () -> Unit = {}
) {

    val cartItems =
        BuyerCartStore.cartItems


    Scaffold(

        bottomBar = {

            BuyerBottomBar(
                selectedIndex = 1,
                onHomeClick = onHomeClick,
                onOrderClick = onOrderClick,
                onActivityClick = onActivityClick,
                onProfileClick = onProfileClick
            )
        }

    ) { innerPadding ->


        Surface(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),

            color =
                Color(0xFFF7F7F7)
        ) {


            Column(
                modifier =
                    Modifier.fillMaxSize()
            ) {


                // TOP BAR
                Row(

                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {


                    IconButton(
                        onClick =
                            onBackClick
                    ) {

                        Text(
                            text = "←",
                            fontSize = 26.sp,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }


                    Text(
                        text = "My Cart",
                        fontSize = 21.sp,
                        fontWeight =
                            FontWeight.Bold
                    )


                    Spacer(
                        modifier =
                            Modifier.weight(1f)
                    )


                    if (
                        cartItems.isNotEmpty()
                    ) {

                        Text(
                            text =
                                "${BuyerCartStore.getTotalQuantity()} items",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }


                HorizontalDivider(
                    color =
                        Color(0xFFE0E0E0)
                )


                if (
                    cartItems.isEmpty()
                ) {


                    EmptyCartContent(
                        onBackClick =
                            onBackClick
                    )


                } else {


                    LazyColumn(

                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),

                        contentPadding =
                            PaddingValues(
                                horizontal = 16.dp,
                                vertical = 16.dp
                            ),

                        verticalArrangement =
                            Arrangement.spacedBy(
                                12.dp
                            )
                    ) {


                        itemsIndexed(

                            items =
                                cartItems,

                            key = { _, item ->
                                item.listingId
                            }

                        ) { index, item ->


                            BuyerCartItemCard(

                                item = item,

                                onIncrease = {

                                    BuyerCartStore
                                        .increaseQuantity(
                                            index
                                        )
                                },

                                onDecrease = {

                                    BuyerCartStore
                                        .decreaseQuantity(
                                            index
                                        )
                                },

                                onRemove = {

                                    BuyerCartStore
                                        .removeItem(
                                            index
                                        )
                                }
                            )
                        }
                    }


                    BuyerCartSummary(
                        onCheckoutClick =
                            onCheckoutClick
                    )
                }
            }
        }
    }
}


@Composable
private fun EmptyCartContent(
    onBackClick: () -> Unit
) {

    Box(

        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        contentAlignment =
            Alignment.Center
    ) {


        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {


            Text(
                text = "🛒",
                fontSize = 60.sp
            )


            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )


            Text(
                text = "Your cart is empty",
                fontSize = 20.sp,
                fontWeight =
                    FontWeight.Bold
            )


            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )


            Text(
                text =
                    "Add surplus food from a shop first.",
                fontSize = 14.sp,
                color = Color.Gray
            )


            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )


            Button(

                onClick =
                    onBackClick,

                colors =
                    ButtonDefaults
                        .buttonColors(
                            containerColor =
                                Color(0xFF4CAF50)
                        ),

                shape =
                    RoundedCornerShape(
                        10.dp
                    )
            ) {


                Text(
                    text =
                        "Continue Shopping",
                    color =
                        Color.White
                )
            }
        }
    }
}


@Composable
private fun BuyerCartItemCard(

    item: BuyerCartItem,

    onIncrease: () -> Unit,

    onDecrease: () -> Unit,

    onRemove: () -> Unit
) {


    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                14.dp
            ),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            )
    ) {


        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {


            // SHOP NAME
            Text(
                text =
                    item.shopName,
                fontSize = 13.sp,
                color = Color.Gray
            )


            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )


            // FOOD NAME
            Text(
                text =
                    item.foodName,
                fontSize = 18.sp,
                fontWeight =
                    FontWeight.Bold,
                color =
                    Color.Black
            )


            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )


            // PICKUP
            Text(
                text =
                    item.pickupTime,
                fontSize = 12.sp,
                color =
                    Color.DarkGray
            )


            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )


            // STOCK
            Text(
                text =
                    "${item.availableQuantity} available",
                fontSize = 12.sp,
                color =
                    Color(0xFF388E3C)
            )


            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )


            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically,

                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {


                // PRICE
                Text(
                    text =
                        "RM %.2f".format(
                            item.price
                        ),
                    fontSize = 17.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        Color(0xFF4CAF50)
                )


                // QUANTITY
                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {


                    OutlinedButton(

                        onClick =
                            onDecrease,

                        modifier =
                            Modifier.size(
                                42.dp
                            ),

                        contentPadding =
                            PaddingValues(0.dp)
                    ) {


                        Text(
                            text = "−",
                            fontSize =
                                19.sp
                        )
                    }


                    Text(

                        text =
                            item.quantity
                                .toString(),

                        modifier =
                            Modifier.padding(
                                horizontal =
                                    14.dp
                            ),

                        fontSize =
                            16.sp,

                        fontWeight =
                            FontWeight.Bold
                    )


                    OutlinedButton(

                        onClick =
                            onIncrease,

                        enabled =
                            item.quantity <
                                    item.availableQuantity,

                        modifier =
                            Modifier.size(
                                42.dp
                            ),

                        contentPadding =
                            PaddingValues(0.dp)
                    ) {


                        Text(
                            text = "+",
                            fontSize =
                                19.sp
                        )
                    }
                }
            }


            if (
                item.quantity >=
                item.availableQuantity
            ) {


                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )


                Text(
                    text =
                        "Maximum available quantity reached",
                    fontSize =
                        11.sp,
                    color =
                        Color.Red
                )
            }


            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )


            HorizontalDivider(
                color =
                    Color(0xFFEAEAEA)
            )


            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )


            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {


                Text(
                    text = "Subtotal",
                    fontSize =
                        14.sp,
                    color =
                        Color.Gray
                )


                Text(

                    text =
                        "RM %.2f".format(
                            item.price *
                                    item.quantity
                        ),

                    fontSize =
                        15.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }


            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )


            OutlinedButton(

                onClick =
                    onRemove,

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(
                        10.dp
                    )
            ) {


                Text(
                    text =
                        "Remove",
                    color =
                        Color.Red
                )
            }
        }
    }
}


@Composable
private fun BuyerCartSummary(

    onCheckoutClick: () -> Unit
) {


    Column(

        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(20.dp)
    ) {


        Text(
            text =
                "Order Summary",
            fontSize =
                18.sp,
            fontWeight =
                FontWeight.Bold
        )


        Spacer(
            modifier =
                Modifier.height(14.dp)
        )


        Row(

            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {


            Text(
                text =
                    "Total Items",
                fontSize =
                    15.sp,
                color =
                    Color.DarkGray
            )


            Text(
                text =
                    BuyerCartStore
                        .getTotalQuantity()
                        .toString(),
                fontSize =
                    15.sp,
                fontWeight =
                    FontWeight.Medium
            )
        }


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        Row(

            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {


            Text(
                text =
                    "Total",
                fontSize =
                    18.sp,
                fontWeight =
                    FontWeight.Bold
            )


            Text(

                text =
                    "RM %.2f".format(
                        BuyerCartStore
                            .getTotalPrice()
                    ),

                fontSize =
                    20.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    Color(0xFF4CAF50)
            )
        }


        Spacer(
            modifier =
                Modifier.height(18.dp)
        )


        Button(

            onClick =
                onCheckoutClick,

            enabled =
                !BuyerCartStore
                    .isCartEmpty(),

            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),

            shape =
                RoundedCornerShape(
                    12.dp
                ),

            colors =
                ButtonDefaults
                    .buttonColors(
                        containerColor =
                            Color(0xFF4CAF50)
                    )
        ) {


            Text(
                text =
                    "Proceed to Checkout",
                fontSize =
                    16.sp,
                fontWeight =
                    FontWeight.Bold,
                color =
                    Color.White
            )
        }
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun BuyerCartScreenPreview() {


    if (
        BuyerCartStore
            .cartItems
            .isEmpty()
    ) {


        BuyerCartStore
            .cartItems
            .addAll(

                listOf(

                    BuyerCartItem(

                        listingId = 1,

                        foodItemId = 1,

                        sellerId =
                            "preview-seller-1",

                        shopName =
                            "Ondo Bakery",

                        foodName =
                            "Blueberry Bread",

                        price = 1.00,

                        pickupTime =
                            "Pickup before 8:00 PM",

                        availableQuantity = 7,

                        quantity = 2
                    ),


                    BuyerCartItem(

                        listingId = 2,

                        foodItemId = 2,

                        sellerId =
                            "preview-seller-1",

                        shopName =
                            "Ondo Bakery",

                        foodName =
                            "Sausage Bread",

                        price = 1.50,

                        pickupTime =
                            "Pickup before 8:00 PM",

                        availableQuantity = 5,

                        quantity = 1
                    )
                )
            )
    }


    SharePlateTheme(
        dynamicColor = false
    ) {


        BuyerCartScreen()
    }
}