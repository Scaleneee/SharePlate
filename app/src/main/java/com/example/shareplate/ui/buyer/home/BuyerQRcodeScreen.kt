package com.example.shareplate.ui.buyer.order

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shareplate.data.model.FoodItem
import com.example.shareplate.data.model.Order
import com.example.shareplate.data.model.SurplusListing
import com.example.shareplate.data.model.User
import com.example.shareplate.data.model.UserRole
import com.example.shareplate.ui.buyer.BuyerOrderDetails
import com.example.shareplate.ui.buyer.BuyerViewModel
import com.example.shareplate.ui.theme.SharePlateTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun BuyerQrCodeScreen(

    orderId: Long,

    buyerViewModel: BuyerViewModel? = null,

    onBackClick: () -> Unit = {}

) {

    val isPreview = LocalInspectionMode.current


    // VIEW MODEL
    val actualViewModel: BuyerViewModel? =

        if (isPreview) {

            null

        } else {

            buyerViewModel ?: viewModel()
        }

    // VIEWMODEL STATES
    val selectedOrderState = actualViewModel?.selectedOrder?.collectAsState()


    val loadingState = actualViewModel?.isLoading?.collectAsState()


    val errorState = actualViewModel?.errorMessage?.collectAsState()

    // PREVIEW DATA
    val previewOrderDetails =

        BuyerOrderDetails(

            order = Order(

                orderId = 1,

                listingId = 1,

                buyerId = "preview-buyer",

                quantity = 2,

                unitPriceCent = 100,

                totalPriceCent = 200,

                orderedAt = "2026-09-03T10:00:00.000Z",

                pickupCode = "SP4821",

                status = "READY"
            ),

            listing = SurplusListing(

                listingId = 1,

                foodItemId = 1,

                sellerId = "preview-seller",

                publishedQuantity = 10,

                availableQuantity = 7,

                originalPriceCents = 500,

                currentDiscountPercent = 80,

                currentPriceCents = 100,

                publishedAt = System.currentTimeMillis(),

                closingAt = System.currentTimeMillis() + 7_200_000,

                pickupEndAt = System.currentTimeMillis() + 3_600_000,

                status = "ACTIVE"
            ),

            foodItem = FoodItem(

                foodItemId = 1,

                sellerId = "preview-seller",

                foodName = "Blueberry Bread",

                description = "Fresh blueberry bread",

                category = "Bakery",

                originalPriceCent = 500,

                bestBeforeDays = 3,

                allergenInformation = null,

                imageUrl = null,

                isActive = true
            ),

            seller = User(

                userId = "preview-seller",

                name = "Ondo Bakery",

                email = "seller@email.com",

                phone = null,

                role = UserRole.SELLER,

                organisationName = "Ondo Bakery",

                address = "Petaling Jaya"
            )
        )


    val orderDetails =

        if (isPreview) {

            previewOrderDetails

        } else {

            selectedOrderState?.value
        }


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


    // =================================================
    // LOAD ORDER
    // =================================================

    LaunchedEffect(
        orderId, actualViewModel
    ) {

        if (!isPreview) {

            actualViewModel?.loadOrder(
                    orderId
                )
        }
    }


    // SCREEN
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

                        text = "Pickup QR Code",

                        fontSize = 21.sp,

                        fontWeight = FontWeight.Bold
                    )
                }


                HorizontalDivider(

                    color = Color(
                        0xFFE0E0E0
                    )
                )

                // CONTENT
                when {


                    isLoading -> {


                        Box(

                            modifier = Modifier.fillMaxSize(),

                            contentAlignment = Alignment.Center

                        ) {


                            CircularProgressIndicator()
                        }
                    }


                    errorMessage != null -> {


                        Box(

                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    24.dp
                                ),

                            contentAlignment = Alignment.Center

                        ) {


                            Text(

                                text = errorMessage,

                                color = Color.Red,

                                fontSize = 14.sp
                            )
                        }
                    }


                    orderDetails == null -> {


                        Box(

                            modifier = Modifier.fillMaxSize(),

                            contentAlignment = Alignment.Center

                        ) {


                            Text(

                                text = "Order not found.",

                                color = Color.Gray
                            )
                        }
                    }


                    else -> {


                        val currentOrder = orderDetails.order


                        val listing = orderDetails.listing


                        val food = orderDetails.foodItem


                        val seller = orderDetails.seller


                        val shopName =

                            seller?.organisationName?.takeIf {

                                    it.isNotBlank()
                                } ?: seller?.name ?: "Shop"


                        Column(

                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    20.dp
                                ),

                            horizontalAlignment = Alignment.CenterHorizontally

                        ) {


                            Spacer(

                                modifier = Modifier.height(
                                    20.dp
                                )
                            )


                            Text(

                                text = "Show this code during pickup",

                                fontSize = 16.sp,

                                fontWeight = FontWeight.SemiBold
                            )


                            Spacer(

                                modifier = Modifier.height(
                                    6.dp
                                )
                            )


                            Text(

                                text = "The seller can use your pickup code to verify your order.",

                                fontSize = 12.sp,

                                color = Color.Gray
                            )


                            Spacer(

                                modifier = Modifier.height(
                                    28.dp
                                )
                            )


                            // QR AREA
                            Card(

                                modifier = Modifier.fillMaxWidth(),

                                shape = RoundedCornerShape(
                                    18.dp
                                ),

                                colors = CardDefaults.cardColors(

                                        containerColor = Color.White
                                    )

                            ) {


                                Column(

                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            28.dp
                                        ),

                                    horizontalAlignment = Alignment.CenterHorizontally

                                ) {


                                    Icon(

                                        imageVector = Icons.Outlined.QrCode2,

                                        contentDescription = "QR Code",

                                        modifier = Modifier.size(
                                            150.dp
                                        ),

                                        tint = Color.Black
                                    )


                                    Spacer(

                                        modifier = Modifier.height(
                                            18.dp
                                        )
                                    )


                                    Text(

                                        text = "Pickup Code",

                                        fontSize = 13.sp,

                                        color = Color.Gray
                                    )


                                    Spacer(

                                        modifier = Modifier.height(
                                            6.dp
                                        )
                                    )


                                    Text(

                                        text = currentOrder.pickupCode,

                                        fontSize = 28.sp,

                                        fontWeight = FontWeight.Bold,

                                        color = Color(
                                            0xFF388E3C
                                        )
                                    )


                                    Spacer(

                                        modifier = Modifier.height(
                                            8.dp
                                        )
                                    )


                                    Text(

                                        text = formatStatus(
                                            currentOrder.status
                                        ),

                                        fontSize = 13.sp,

                                        fontWeight = FontWeight.SemiBold,

                                        color = getStatusColor(
                                            currentOrder.status
                                        )
                                    )
                                }
                            }


                            Spacer(

                                modifier = Modifier.height(
                                    20.dp
                                )
                            )


                            // ORDER DETAILS
                            Card(

                                modifier = Modifier.fillMaxWidth(),

                                shape = RoundedCornerShape(
                                    14.dp
                                ),

                                colors = CardDefaults.cardColors(

                                        containerColor = Color.White
                                    )

                            ) {


                                Column(

                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            18.dp
                                        )

                                ) {


                                    Text(

                                        text = "Pickup Details",

                                        fontSize = 17.sp,

                                        fontWeight = FontWeight.Bold
                                    )


                                    Spacer(

                                        modifier = Modifier.height(
                                            15.dp
                                        )
                                    )


                                    DetailRow(

                                        label = "Shop",

                                        value = shopName
                                    )


                                    Spacer(

                                        modifier = Modifier.height(
                                            9.dp
                                        )
                                    )


                                    DetailRow(

                                        label = "Address",

                                        value = seller?.address ?: "Address not provided"
                                    )


                                    Spacer(

                                        modifier = Modifier.height(
                                            9.dp
                                        )
                                    )


                                    DetailRow(

                                        label = "Food",

                                        value = food?.foodName ?: "Food Item"
                                    )


                                    Spacer(

                                        modifier = Modifier.height(
                                            9.dp
                                        )
                                    )


                                    DetailRow(

                                        label = "Quantity",

                                        value = currentOrder.quantity.toString()
                                    )


                                    Spacer(

                                        modifier = Modifier.height(
                                            9.dp
                                        )
                                    )


                                    DetailRow(

                                        label = "Total",

                                        value = "RM %.2f".format(

                                            currentOrder.totalPriceCent / 100.0
                                        ),

                                        valueColor = Color(
                                            0xFF388E3C
                                        )
                                    )


                                    Spacer(

                                        modifier = Modifier.height(
                                            9.dp
                                        )
                                    )


                                    DetailRow(

                                        label = "Pickup",

                                        value = formatPickupTime(

                                            listing?.pickupEndAt ?: 0L
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


// DETAIL ROW
@Composable
private fun DetailRow(

    label: String,

    value: String,

    valueColor: Color = Color.DarkGray

) {

    Row(

        modifier = Modifier.fillMaxWidth(),

        horizontalArrangement = Arrangement.SpaceBetween,

        verticalAlignment = Alignment.Top

    ) {


        Text(

            text = label,

            fontSize = 13.sp,

            color = Color.Gray,

            modifier = Modifier.weight(
                0.35f
            )
        )


        Text(

            text = value,

            fontSize = 13.sp,

            fontWeight = FontWeight.Medium,

            color = valueColor,

            modifier = Modifier.weight(
                0.65f
            )
        )
    }
}


// STATUS
private fun formatStatus(

    status: String

): String {

    return when (status.uppercase()) {

        "PENDING" -> "Pending"

        "CONFIRMED" -> "Confirmed"

        "READY", "READY_FOR_PICKUP" -> "Ready for Pickup"

        "COMPLETED" -> "Completed"

        "CANCELLED" -> "Cancelled"

        else -> status.lowercase().replaceFirstChar {

                it.uppercase()
            }
    }
}


// STATUS COLOR
private fun getStatusColor(

    status: String

): Color {

    return when (status.uppercase()) {

        "PENDING" -> Color(
            0xFFFF9800
        )

        "CONFIRMED", "READY", "READY_FOR_PICKUP", "COMPLETED" -> Color(
            0xFF388E3C
        )

        "CANCELLED" -> Color.Red

        else -> Color.DarkGray
    }
}


// PICKUP TIME
private fun formatPickupTime(

    pickupEndAt: Long

): String {

    if (pickupEndAt <= 0) {

        return "Pickup time unavailable"
    }


    return try {

        val milliseconds =

            if (pickupEndAt < 100_000_000_000L) {

                pickupEndAt * 1000

            } else {

                pickupEndAt
            }


        val formatter = SimpleDateFormat(

            "dd MMM yyyy, hh:mm a",

            Locale.getDefault()
        )


        formatter.format(

            Date(
                milliseconds
            )
        )


    } catch (e: Exception) {

        "Pickup time unavailable"
    }
}

@Preview(
    showBackground = true, showSystemUi = true
)
@Composable
fun BuyerQrCodeScreenPreview() {

    SharePlateTheme(
        dynamicColor = false
    ) {

        BuyerQrCodeScreen(
            orderId = 1
        )
    }
}