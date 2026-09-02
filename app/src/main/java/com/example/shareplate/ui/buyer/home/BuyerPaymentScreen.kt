package com.example.shareplate.ui.buyer.order

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shareplate.ui.theme.SharePlateTheme
import kotlinx.coroutines.launch


@Composable
fun BuyerPaymentScreen(

    onBackClick: () -> Unit = {},

    onPaymentSuccess: (
        pickupCode: String,
        totalPriceCent: Int
    ) -> Unit = { _, _ -> }

) {

    val orderManager =
        remember {
            BuyerOrderManager()
        }

    val coroutineScope =
        rememberCoroutineScope()


    var selectedPaymentMethod by remember {
        mutableStateOf("")
    }


    var isSubmitting by remember {
        mutableStateOf(false)
    }


    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }


    val totalPrice =
        BuyerCartStore
            .getTotalPrice()


    Scaffold { innerPadding ->


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


                // =================================================
                // TOP BAR
                // =================================================

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
                        onClick = onBackClick,
                        enabled = !isSubmitting
                    ) {

                        Text(
                            text = "←",
                            fontSize = 26.sp,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }


                    Text(
                        text = "Payment",
                        fontSize = 21.sp,
                        fontWeight =
                            FontWeight.Bold
                    )
                }


                HorizontalDivider(
                    color =
                        Color(0xFFE0E0E0)
                )


                Column(

                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(20.dp)

                ) {


                    // =================================================
                    // TOTAL PAYMENT
                    // =================================================

                    Text(
                        text = "Total Payment",
                        fontSize = 15.sp,
                        color =
                            Color.DarkGray
                    )


                    Spacer(
                        modifier =
                            Modifier.height(5.dp)
                    )


                    Text(

                        text =
                            "RM %.2f".format(
                                totalPrice
                            ),

                        fontSize = 30.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            Color(0xFF4CAF50)
                    )


                    Spacer(
                        modifier =
                            Modifier.height(28.dp)
                    )


                    Text(
                        text =
                            "Choose Payment Method",
                        fontSize = 18.sp,
                        fontWeight =
                            FontWeight.Bold
                    )


                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )


                    // =================================================
                    // CASH ON PICKUP
                    // =================================================

                    PaymentMethodCard(

                        title =
                            "Cash on Pickup",

                        description =
                            "Pay when you collect your food.",

                        selected =
                            selectedPaymentMethod ==
                                    "CASH",

                        onClick = {

                            if (!isSubmitting) {

                                selectedPaymentMethod =
                                    "CASH"

                                errorMessage =
                                    null
                            }
                        }
                    )


                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )


                    // =================================================
                    // CARD
                    // =================================================

                    PaymentMethodCard(

                        title =
                            "Credit / Debit Card",

                        description =
                            "Pay using your bank card.",

                        selected =
                            selectedPaymentMethod ==
                                    "CARD",

                        onClick = {

                            if (!isSubmitting) {

                                selectedPaymentMethod =
                                    "CARD"

                                errorMessage =
                                    null
                            }
                        }
                    )


                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )


                    // =================================================
                    // E-WALLET
                    // =================================================

                    PaymentMethodCard(

                        title =
                            "E-Wallet",

                        description =
                            "Pay using an e-wallet.",

                        selected =
                            selectedPaymentMethod ==
                                    "EWALLET",

                        onClick = {

                            if (!isSubmitting) {

                                selectedPaymentMethod =
                                    "EWALLET"

                                errorMessage =
                                    null
                            }
                        }
                    )


                    Spacer(
                        modifier =
                            Modifier.height(22.dp)
                    )


                    // =================================================
                    // INFORMATION
                    // =================================================

                    if (
                        selectedPaymentMethod
                            .isNotEmpty()
                    ) {


                        Card(

                            modifier =
                                Modifier.fillMaxWidth(),

                            shape =
                                RoundedCornerShape(
                                    10.dp
                                ),

                            colors =
                                CardDefaults.cardColors(
                                    containerColor =
                                        Color(
                                            0xFFE8F5E9
                                        )
                                )

                        ) {


                            Text(

                                text =
                                    when (
                                        selectedPaymentMethod
                                    ) {

                                        "CASH" ->
                                            "You will pay the seller when collecting your food."

                                        "CARD" ->
                                            "Card payment selected."

                                        "EWALLET" ->
                                            "E-Wallet payment selected."

                                        else ->
                                            ""
                                    },

                                modifier =
                                    Modifier.padding(
                                        14.dp
                                    ),

                                fontSize =
                                    13.sp,

                                color =
                                    Color(
                                        0xFF2E7D32
                                    )
                            )
                        }
                    }


                    if (
                        errorMessage != null
                    ) {


                        Spacer(
                            modifier =
                                Modifier.height(
                                    18.dp
                                )
                        )


                        Text(
                            text =
                                errorMessage
                                    ?: "",
                            fontSize =
                                13.sp,
                            color =
                                Color.Red
                        )
                    }
                }


                // =================================================
                // CONFIRM BUTTON
                // =================================================

                Column(

                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.White
                        )
                        .padding(20.dp)

                ) {


                    Button(

                        onClick = {


                            if (
                                selectedPaymentMethod
                                    .isBlank()
                            ) {

                                errorMessage =
                                    "Please select a payment method."

                                return@Button
                            }


                            if (
                                BuyerCartStore
                                    .isCartEmpty()
                            ) {

                                errorMessage =
                                    "Your cart is empty."

                                return@Button
                            }


                            coroutineScope.launch {


                                isSubmitting =
                                    true

                                errorMessage =
                                    null


                                val result =
                                    orderManager
                                        .submitOrder()


                                if (
                                    result.success
                                ) {


                                    val pickupCode =
                                        result.pickupCode
                                            ?: "N/A"


                                    onPaymentSuccess(

                                        pickupCode,

                                        result
                                            .totalPriceCent
                                    )


                                } else {


                                    errorMessage =
                                        result.message
                                }


                                isSubmitting =
                                    false
                            }
                        },

                        enabled =
                            !isSubmitting &&
                                    BuyerCartStore
                                        .cartItems
                                        .isNotEmpty(),

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),

                        shape =
                            RoundedCornerShape(
                                12.dp
                            ),

                        colors =
                            ButtonDefaults
                                .buttonColors(
                                    containerColor =
                                        Color(
                                            0xFF4CAF50
                                        )
                                )
                    ) {


                        if (isSubmitting) {


                            CircularProgressIndicator(
                                color =
                                    Color.White
                            )


                        } else {


                            Text(
                                text =
                                    "Confirm Payment",
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
            }
        }
    }
}


@Composable
private fun PaymentMethodCard(

    title: String,

    description: String,

    selected: Boolean,

    onClick: () -> Unit

) {


    Card(

        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },

        shape =
            RoundedCornerShape(
                12.dp
            ),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            )

    ) {


        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            verticalAlignment =
                Alignment.CenterVertically,

            horizontalArrangement =
                Arrangement.SpaceBetween

        ) {


            Column(
                modifier =
                    Modifier.weight(1f)
            ) {


                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight =
                        FontWeight.SemiBold
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            4.dp
                        )
                )


                Text(
                    text =
                        description,
                    fontSize =
                        12.sp,
                    color =
                        Color.Gray
                )
            }


            RadioButton(

                selected =
                    selected,

                onClick =
                    onClick
            )
        }
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun BuyerPaymentScreenPreview() {


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


        BuyerPaymentScreen()
    }
}