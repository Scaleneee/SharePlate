package com.example.shareplate.ui.buyer.order

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shareplate.ui.buyer.BuyerViewModel
import com.example.shareplate.ui.theme.SharePlateTheme


@Composable
fun BuyerCheckoutScreen(

    buyerViewModel: BuyerViewModel? = null,

    onBackClick: () -> Unit = {},

    onContinuePaymentClick: () -> Unit = {}

) {

    val isPreview = LocalInspectionMode.current


    val actualViewModel: BuyerViewModel? =

        if (isPreview) {

            null

        } else {

            buyerViewModel ?: viewModel()
        }


    // PICKUP NOTE
    var pickupNote by remember {

        mutableStateOf("")
    }

    // PREVIEW CART
    val previewCartItems = listOf(

        BuyerCartItem(

            listingId = 1,

            foodItemId = 1,

            sellerId = "preview-seller-1",

            shopName = "Ondo Bakery",

            foodName = "Blueberry Bread",

            price = 1.00,

            pickupTime = "Pickup before 8:00 PM",

            availableQuantity = 7,

            quantity = 2
        ),


        BuyerCartItem(

            listingId = 2,

            foodItemId = 2,

            sellerId = "preview-seller-1",

            shopName = "Ondo Bakery",

            foodName = "Sausage Bread",

            price = 1.50,

            pickupTime = "Pickup before 8:00 PM",

            availableQuantity = 5,

            quantity = 1
        )
    )


    // CART ITEMS
    val cartItems =

        if (isPreview) {

            previewCartItems

        } else {

            actualViewModel?.cartItems ?: emptyList()
        }


    // TOTAL QUANTITY
    val totalQuantity =

        if (isPreview) {

            previewCartItems.sumOf {

                it.quantity
            }

        } else {

            actualViewModel?.getCartQuantity() ?: 0
        }


    // TOTAL PRICE
    val totalPrice =

        if (isPreview) {

            previewCartItems.sumOf {

                it.price * it.quantity
            }

        } else {

            actualViewModel?.getCartTotal() ?: 0.0
        }


    // FIRST CART ITEM
    val firstItem = cartItems.firstOrNull()


    Scaffold { innerPadding ->


        Surface(

            modifier = Modifier
                .fillMaxSize()
                .padding(
                    innerPadding
                ),

            color = Color(
                0xFFF7F7F7
            )

        ) {


            Column(

                modifier = Modifier.fillMaxSize()

            ) {


                // =================================================
                // TOP BAR
                // =================================================

                Row(

                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.White
                        )
                        .padding(

                            horizontal = 16.dp,

                            vertical = 12.dp
                        ),

                    verticalAlignment = Alignment.CenterVertically

                ) {


                    IconButton(

                        onClick = onBackClick

                    ) {


                        Text(

                            text = "←",

                            fontSize = 26.sp,

                            fontWeight = FontWeight.Bold
                        )
                    }


                    Text(

                        text = "Checkout",

                        fontSize = 21.sp,

                        fontWeight = FontWeight.Bold
                    )
                }


                HorizontalDivider(

                    color = Color(
                        0xFFE0E0E0
                    )
                )


                // CHECKOUT CONTENT
                LazyColumn(

                    modifier = Modifier
                        .weight(
                            1f
                        )
                        .fillMaxWidth()
                        .padding(

                            horizontal = 16.dp
                        )

                ) {


                    item {


                        Spacer(

                            modifier = Modifier.height(
                                20.dp
                            )
                        )


                        // PICKUP DETAILS
                        Text(

                            text = "Pickup Details",

                            fontSize = 18.sp,

                            fontWeight = FontWeight.Bold
                        )


                        Spacer(

                            modifier = Modifier.height(
                                12.dp
                            )
                        )


                        PickupInformationCard(

                            item = firstItem
                        )


                        Spacer(

                            modifier = Modifier.height(
                                24.dp
                            )
                        )


                        // ORDER TITLE
                        Text(

                            text = "Your Order",

                            fontSize = 18.sp,

                            fontWeight = FontWeight.Bold
                        )


                        Spacer(

                            modifier = Modifier.height(
                                12.dp
                            )
                        )
                    }


                    // CART ITEMS
                    items(

                        items = cartItems,

                        key = {

                            it.listingId
                        }

                    ) { item ->


                        CheckoutFoodItem(

                            item = item
                        )


                        Spacer(

                            modifier = Modifier.height(
                                10.dp
                            )
                        )
                    }


                    item {


                        Spacer(

                            modifier = Modifier.height(
                                14.dp
                            )
                        )


                        // PICKUP NOTE
                        Text(

                            text = "Pickup Note",

                            fontSize = 18.sp,

                            fontWeight = FontWeight.Bold
                        )


                        Spacer(

                            modifier = Modifier.height(
                                10.dp
                            )
                        )


                        OutlinedTextField(

                            value = pickupNote,

                            onValueChange = {

                                pickupNote = it
                            },

                            modifier = Modifier.fillMaxWidth(),

                            placeholder = {


                                Text(

                                    text = "Example: I will arrive around 6:30 PM"
                                )
                            },

                            minLines = 3,

                            shape = RoundedCornerShape(
                                10.dp
                            )
                        )


                        Spacer(

                            modifier = Modifier.height(
                                20.dp
                            )
                        )
                    }
                }


                // BOTTOM SUMMARY
                CheckoutBottomSection(

                    totalQuantity = totalQuantity,

                    totalPrice = totalPrice,

                    isCartEmpty = cartItems.isEmpty(),

                    onContinuePaymentClick = onContinuePaymentClick
                )
            }
        }
    }
}


// =================================================
// PICKUP INFORMATION
// =================================================

@Composable
private fun PickupInformationCard(

    item: BuyerCartItem?

) {


    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(
            12.dp
        ),

        colors = CardDefaults.cardColors(

                containerColor = Color.White
            )

    ) {


        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    16.dp
                )

        ) {


            Text(

                text = item?.shopName ?: "Shop",

                fontSize = 16.sp,

                fontWeight = FontWeight.SemiBold
            )


            Spacer(

                modifier = Modifier.height(
                    8.dp
                )
            )


            Text(

                text = "Self Pickup",

                fontSize = 13.sp,

                color = Color.DarkGray
            )


            Spacer(

                modifier = Modifier.height(
                    5.dp
                )
            )


            Text(

                text = item?.pickupTime ?: "Pickup time unavailable",

                fontSize = 13.sp,

                color = Color(
                    0xFF388E3C
                )
            )
        }
    }
}


// FOOD ITEM
@Composable
private fun CheckoutFoodItem(

    item: BuyerCartItem

) {


    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(
            12.dp
        ),

        colors = CardDefaults.cardColors(

                containerColor = Color.White
            )

    ) {


        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    15.dp
                )

        ) {


            Text(

                text = item.foodName,

                fontSize = 15.sp,

                fontWeight = FontWeight.SemiBold
            )


            Spacer(

                modifier = Modifier.height(
                    6.dp
                )
            )


            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween

            ) {


                Text(

                    text = "RM %.2f × ${item.quantity}".format(
                            item.price
                        ),

                    fontSize = 13.sp,

                    color = Color.DarkGray
                )


                Text(

                    text = "RM %.2f".format(

                        item.price * item.quantity
                    ),

                    fontSize = 14.sp,

                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


// BOTTOM SECTION
@Composable
private fun CheckoutBottomSection(

    totalQuantity: Int,

    totalPrice: Double,

    isCartEmpty: Boolean,

    onContinuePaymentClick: () -> Unit

) {


    Column(

        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White
            )
            .padding(
                20.dp
            )

    ) {


        // TOTAL ITEMS
        Row(

            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement = Arrangement.SpaceBetween

        ) {


            Text(

                text = "Total Items",

                fontSize = 14.sp,

                color = Color.DarkGray
            )


            Text(

                text = totalQuantity.toString(),

                fontSize = 14.sp,

                fontWeight = FontWeight.Medium
            )
        }


        Spacer(

            modifier = Modifier.height(
                10.dp
            )
        )


        // =================================================
        // TOTAL PAYMENT
        // =================================================

        Row(

            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement = Arrangement.SpaceBetween

        ) {


            Text(

                text = "Total Payment",

                fontSize = 18.sp,

                fontWeight = FontWeight.Bold
            )


            Text(

                text = "RM %.2f".format(
                    totalPrice
                ),

                fontSize = 20.sp,

                fontWeight = FontWeight.Bold,

                color = Color(
                    0xFF4CAF50
                )
            )
        }


        Spacer(

            modifier = Modifier.height(
                16.dp
            )
        )


        // CONTINUE
        Button(

            onClick = onContinuePaymentClick,

            enabled = !isCartEmpty,

            modifier = Modifier
                .fillMaxWidth()
                .height(
                    52.dp
                ),

            shape = RoundedCornerShape(
                12.dp
            ),

            colors = ButtonDefaults.buttonColors(

                    containerColor = Color(
                        0xFF4CAF50
                    )
                )

        ) {


            Text(

                text = "Continue to Payment",

                fontSize = 16.sp,

                fontWeight = FontWeight.Bold,

                color = Color.White
            )
        }
    }
}

@Preview(
    showBackground = true, showSystemUi = true
)
@Composable
fun BuyerCheckoutScreenPreview() {


    SharePlateTheme(
        dynamicColor = false
    ) {


        BuyerCheckoutScreen()
    }
}