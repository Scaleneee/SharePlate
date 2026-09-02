package com.example.shareplate.ui.buyer.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.shareplate.ui.theme.SharePlateTheme


@Composable
fun BuyerOrderSuccessScreen(

    pickupCode: String,

    totalPriceCent: Int,

    onViewOrderClick: () -> Unit = {},

    onHomeClick: () -> Unit = {}

) {

    val totalPrice =
        totalPriceCent / 100.0


    Scaffold { innerPadding ->


        Surface(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),

            color =
                Color(0xFFF7F7F7)

        ) {


            Column(

                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally

            ) {


                Spacer(
                    modifier =
                        Modifier.height(55.dp)
                )


                // SUCCESS ICON
                Surface(

                    modifier =
                        Modifier.size(90.dp),

                    shape =
                        CircleShape,

                    color =
                        Color(0xFFE8F5E9)

                ) {


                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {


                        Text(
                            text = "✓",
                            fontSize = 50.sp,
                            fontWeight =
                                FontWeight.Bold,
                            color =
                                Color(0xFF4CAF50)
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )


                Text(
                    text =
                        "Order Successful!",
                    fontSize = 26.sp,
                    fontWeight =
                        FontWeight.Bold
                )


                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )


                Text(
                    text =
                        "Your order has been placed successfully.",
                    fontSize = 14.sp,
                    color =
                        Color.Gray
                )


                Spacer(
                    modifier =
                        Modifier.height(32.dp)
                )


                // PICKUP CODE CARD
                Card(

                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(
                            16.dp
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
                            .padding(24.dp),

                        horizontalAlignment =
                            Alignment.CenterHorizontally

                    ) {


                        Text(
                            text =
                                "Pickup Code",
                            fontSize = 14.sp,
                            color =
                                Color.Gray
                        )


                        Spacer(
                            modifier =
                                Modifier.height(10.dp)
                        )


                        Text(
                            text =
                                pickupCode,
                            fontSize = 32.sp,
                            fontWeight =
                                FontWeight.Bold,
                            color =
                                Color(0xFF4CAF50)
                        )


                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )


                        Text(
                            text =
                                "Show this code to the seller when collecting your food.",
                            fontSize = 13.sp,
                            color =
                                Color.DarkGray
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )


                // PAYMENT SUMMARY
                Card(

                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(
                            16.dp
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
                            .padding(20.dp)

                    ) {


                        Text(
                            text =
                                "Order Summary",
                            fontSize = 17.sp,
                            fontWeight =
                                FontWeight.Bold
                        )


                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )


                        Row(

                            modifier =
                                Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement.SpaceBetween

                        ) {


                            Text(
                                text =
                                    "Order Status",
                                fontSize =
                                    14.sp,
                                color =
                                    Color.DarkGray
                            )


                            Text(
                                text =
                                    "Pending Pickup",
                                fontSize =
                                    14.sp,
                                fontWeight =
                                    FontWeight.SemiBold,
                                color =
                                    Color(0xFFFF9800)
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )


                        Row(

                            modifier =
                                Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement.SpaceBetween

                        ) {


                            Text(
                                text =
                                    "Total Payment",
                                fontSize =
                                    15.sp,
                                color =
                                    Color.DarkGray
                            )


                            Text(

                                text =
                                    "RM %.2f".format(
                                        totalPrice
                                    ),

                                fontSize =
                                    18.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    Color(0xFF4CAF50)
                            )
                        }
                    }
                }


                Spacer(
                    modifier =
                        Modifier.weight(1f)
                )


                // VIEW ORDER BUTTON
                Button(

                    onClick =
                        onViewOrderClick,

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),

                    shape =
                        RoundedCornerShape(
                            12.dp
                        ),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                Color(0xFF4CAF50)
                        )

                ) {


                    Text(
                        text =
                            "View My Order",
                        fontSize =
                            16.sp,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            Color.White
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                // HOME BUTTON
                OutlinedButton(

                    onClick =
                        onHomeClick,

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),

                    shape =
                        RoundedCornerShape(
                            12.dp
                        )

                ) {


                    Text(
                        text =
                            "Back to Home",
                        fontSize =
                            16.sp,
                        fontWeight =
                            FontWeight.SemiBold
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(15.dp)
                )
            }
        }
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun BuyerOrderSuccessScreenPreview() {


    SharePlateTheme(
        dynamicColor = false
    ) {


        BuyerOrderSuccessScreen(

            pickupCode =
                "SP4821",

            totalPriceCent =
                350

        )
    }
}