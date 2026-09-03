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
import androidx.compose.runtime.collectAsState
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
fun BuyerPaymentScreen(

    buyerViewModel: BuyerViewModel? = null,

    onBackClick: () -> Unit = {},

    onPaymentSuccess: (
        pickupCode: String, totalPriceCent: Int
    ) -> Unit = { _, _ -> }

) {

    val isPreview = LocalInspectionMode.current


    val actualViewModel: BuyerViewModel? =

        if (isPreview) {

            null

        } else {

            buyerViewModel ?: viewModel()
        }


    // PAYMENT METHOD
    var selectedPaymentMethod by remember {

        mutableStateOf(
            "CASH"
        )
    }


    // VIEWMODEL STATES
    val loadingState = actualViewModel?.isLoading?.collectAsState()


    val errorState = actualViewModel?.errorMessage?.collectAsState()


    val isLoading =

        if (isPreview) {

            false

        } else {

            loadingState?.value ?: false
        }


    val errorMessage =

        if (isPreview) {

            null

        } else {

            errorState?.value
        }


    // PREVIEW TOTAL
    val totalPrice =

        if (isPreview) {

            3.50

        } else {

            actualViewModel?.getCartTotal() ?: 0.0
        }


    val isCartEmpty =

        if (isPreview) {

            false

        } else {

            actualViewModel?.isCartEmpty() ?: true
        }


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


                // TOP BAR
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

                        text = "Payment",

                        fontSize = 21.sp,

                        fontWeight = FontWeight.Bold
                    )
                }


                HorizontalDivider(

                    color = Color(
                        0xFFE0E0E0
                    )
                )


                Column(

                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            20.dp
                        )

                ) {


                    // TOTAL
                    Text(

                        text = "Total Payment",

                        fontSize = 15.sp,

                        color = Color.DarkGray
                    )


                    Spacer(

                        modifier = Modifier.height(
                            6.dp
                        )
                    )


                    Text(

                        text = "RM %.2f".format(
                            totalPrice
                        ),

                        fontSize = 28.sp,

                        fontWeight = FontWeight.Bold,

                        color = Color(
                            0xFF4CAF50
                        )
                    )


                    Spacer(

                        modifier = Modifier.height(
                            28.dp
                        )
                    )


                    // PAYMENT METHOD TITLE
                    Text(

                        text = "Select Payment Method",

                        fontSize = 18.sp,

                        fontWeight = FontWeight.Bold
                    )


                    Spacer(

                        modifier = Modifier.height(
                            14.dp
                        )
                    )


                    // CASH
                    PaymentMethodCard(

                        title = "Cash",

                        subtitle = "Pay during pickup",

                        selected = selectedPaymentMethod == "CASH",

                        onClick = {

                            selectedPaymentMethod = "CASH"
                        })


                    Spacer(

                        modifier = Modifier.height(
                            12.dp
                        )
                    )


                    // CARD
                    PaymentMethodCard(

                        title = "Credit / Debit Card",

                        subtitle = "Card payment",

                        selected = selectedPaymentMethod == "CARD",

                        onClick = {

                            selectedPaymentMethod = "CARD"
                        })


                    Spacer(

                        modifier = Modifier.height(
                            12.dp
                        )
                    )


                    // EWALLET
                    PaymentMethodCard(

                        title = "E-Wallet",

                        subtitle = "Pay using e-wallet",

                        selected = selectedPaymentMethod == "EWALLET",

                        onClick = {

                            selectedPaymentMethod = "EWALLET"
                        })


                    Spacer(

                        modifier = Modifier.height(
                            24.dp
                        )
                    )

                    // ERROR
                    if (errorMessage != null) {


                        Text(

                            text = errorMessage,

                            fontSize = 13.sp,

                            color = Color.Red
                        )


                        Spacer(

                            modifier = Modifier.height(
                                12.dp
                            )
                        )
                    }


                    Spacer(

                        modifier = Modifier.weight(
                            1f
                        )
                    )

                    // CONFIRM PAYMENT
                    Button(

                        onClick = {


                            actualViewModel?.submitOrder(

                                    onSuccess = { pickupCode, totalPriceCent ->


                                        onPaymentSuccess(

                                            pickupCode,

                                            totalPriceCent
                                        )
                                    })
                        },

                        enabled = !isLoading && !isCartEmpty,

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


                        if (isLoading) {


                            CircularProgressIndicator(

                                modifier = Modifier.height(
                                    22.dp
                                ),

                                strokeWidth = 2.dp,

                                color = Color.White
                            )


                        } else {


                            Text(

                                text = "Confirm Payment",

                                fontSize = 16.sp,

                                fontWeight = FontWeight.Bold,

                                color = Color.White
                            )
                        }
                    }


                    Spacer(

                        modifier = Modifier.height(
                            8.dp
                        )
                    )


                    Text(

                        text =

                            when (selectedPaymentMethod) {

                                "CASH" -> "Cash payment will be made during pickup."

                                "CARD" -> "Card payment is simulated in this prototype."

                                "EWALLET" -> "E-Wallet payment is simulated in this prototype."

                                else -> ""
                            },

                        modifier = Modifier.fillMaxWidth(),

                        fontSize = 11.sp,

                        color = Color.Gray
                    )
                }
            }
        }
    }
}


// PAYMENT METHOD CARD
@Composable
private fun PaymentMethodCard(

    title: String,

    subtitle: String,

    selected: Boolean,

    onClick: () -> Unit

) {


    Card(

        modifier = Modifier
            .fillMaxWidth()
            .clickable {

                onClick()
            },

        shape = RoundedCornerShape(
            12.dp
        ),

        colors = CardDefaults.cardColors(

                containerColor = Color.White
            )

    ) {


        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    16.dp
                ),

            verticalAlignment = Alignment.CenterVertically,

            horizontalArrangement = Arrangement.SpaceBetween

        ) {


            Column(

                modifier = Modifier.weight(
                    1f
                )

            ) {


                Text(

                    text = title,

                    fontSize = 16.sp,

                    fontWeight = FontWeight.SemiBold
                )


                Spacer(

                    modifier = Modifier.height(
                        4.dp
                    )
                )


                Text(

                    text = subtitle,

                    fontSize = 12.sp,

                    color = Color.Gray
                )
            }


            RadioButton(

                selected = selected,

                onClick = onClick
            )
        }
    }
}

@Preview(
    showBackground = true, showSystemUi = true
)
@Composable
fun BuyerPaymentScreenPreview() {


    SharePlateTheme(
        dynamicColor = false
    ) {
        BuyerPaymentScreen()
    }
}