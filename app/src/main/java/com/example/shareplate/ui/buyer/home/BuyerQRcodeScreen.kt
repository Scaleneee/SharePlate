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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.shareplate.data.remote.SupabaseProvider
import com.example.shareplate.data.repository.BuyerRepository
import com.example.shareplate.ui.theme.SharePlateTheme
import io.github.jan.supabase.auth.auth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class BuyerQrOrderDetails(

    val orderId: Long,
    val shopName: String,
    val shopAddress: String,
    val foodName: String,
    val quantity: Int,
    val pickupCode: String,
    val status: String,
    val pickupEndAt: Long
)


@Composable
fun BuyerQrCodeScreen(

    orderId: Long,

    onBackClick: () -> Unit = {}

) {

    val repository =
        remember {
            BuyerRepository()
        }


    val supabase = SupabaseProvider.client


    val isPreview = LocalInspectionMode.current


    var orderDetails by remember {

        mutableStateOf<
                BuyerQrOrderDetails?
                >(
            null
        )
    }


    var isLoading by remember {

        mutableStateOf(true)
    }


    var errorMessage by remember {

        mutableStateOf<String?>(null)
    }


    LaunchedEffect(
        orderId
    ) {

        // PREVIEW
        if (isPreview) {

            orderDetails =
                BuyerQrOrderDetails(

                    orderId = 1,

                    shopName = "Ondo Bakery",

                    shopAddress = "Petaling Jaya",

                    foodName = "Blueberry Bread",

                    quantity = 2,

                    pickupCode = "SP4821",

                    status = "PENDING",

                    pickupEndAt =
                        System.currentTimeMillis() +
                                3_600_000
                )


            isLoading = false

            return@LaunchedEffect
        }


        try {

            isLoading = true

            errorMessage = null

            // GET CURRENT BUYER
            val currentUser =
                supabase
                    .auth
                    .currentUserOrNull()


            if (currentUser == null) {

                errorMessage = "Please log in to view this pickup code."

                isLoading = false

                return@LaunchedEffect
            }

            // GET ORDER
            val order =
                repository
                    .getOrderById(
                        orderId
                    )


            if (order == null) {

                errorMessage = "Order not found."

                isLoading = false

                return@LaunchedEffect
            }


            // Make sure this order belongs
            // to the currently logged-in buyer.
            if (
                order.buyerId != currentUser.id
            ) {

                errorMessage = "You cannot access this order."

                isLoading = false

                return@LaunchedEffect
            }

            // GET SURPLUS LISTING
            val listing =
                repository
                    .getListingById(
                        order.listingId
                    )


            if (listing == null) {

                errorMessage = "Surplus listing not found."

                isLoading = false

                return@LaunchedEffect
            }

            // GET FOOD
             val food =
                repository
                    .getFoodItemById(
                        listing.foodItemId
                    )

            // GET SELLER
            val seller =
                repository
                    .getSellerById(
                        listing.sellerId
                    )


            val shopName =

                seller
                    ?.organisationName
                    ?.takeIf {
                        it.isNotBlank()
                    }
                    ?: seller?.name
                    ?: "Shop"


            orderDetails =
                BuyerQrOrderDetails(

                    orderId = order.orderId,
                    shopName = shopName,
                    shopAddress = seller?.address ?: "Address not provided",
                    foodName = food?.foodName ?: "Food Item",
                    quantity = order.quantity,
                    pickupCode = order.pickupCode,
                    status = order.status,
                    pickupEndAt = listing.pickupEndAt
                )


        } catch (e: Exception) {


            errorMessage = e.message ?: "Unable to load pickup information."


        } finally {


            isLoading = false
        }
    }


    Scaffold(

        topBar = {

            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White
                    )
                    .padding(
                        horizontal = 8.dp,
                        vertical = 8.dp
                    ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {


                IconButton(
                    onClick = onBackClick
                ) {


                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }


                Text(
                    text = "Pickup QR Code",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

    ) { innerPadding ->


        Surface(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),

            color = Color(0xFFF7F7F7)
        ) {


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
                            .padding(24.dp),

                        contentAlignment = Alignment.Center
                    ) {


                        Text(
                            text = errorMessage ?: "Something went wrong.",
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }
                }


                orderDetails != null -> {


                    BuyerQrCodeContent(

                        order = orderDetails!!
                    )
                }
            }
        }
    }
}


@Composable
private fun BuyerQrCodeContent(

    order: BuyerQrOrderDetails

) {


    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(22.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        Text(
            text = "Show this code to the seller",
            fontSize = 20.sp,
            fontWeight =FontWeight.Bold
        )


        Spacer(
            modifier = Modifier.height(7.dp)
        )


        Text(
            text = "The seller will use this code when you collect your food.",
            fontSize = 13.sp,
            color = Color.Gray
        )


        Spacer(
            modifier =Modifier.height(28.dp)
        )

        // QR CARD
        Card(

            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(18.dp),

            colors =
                CardDefaults.cardColors(
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


                Surface(

                    modifier =
                        Modifier.size(180.dp),

                    shape =
                        RoundedCornerShape(14.dp),

                    color =
                        Color(0xFFF5F5F5)
                ) {


                    Box(
                        contentAlignment = Alignment.Center
                    ) {


                        Icon(

                            imageVector = Icons.Outlined.QrCode2,

                            contentDescription = "Pickup QR Code",

                            modifier = Modifier.size(145.dp),

                            tint = Color.Black
                        )
                    }
                }


                Spacer(
                    modifier = Modifier.height(22.dp)
                )


                Text(
                    text = "Pickup Code",
                    fontSize = 13.sp,
                    color = Color.Gray
                )


                Spacer(
                    modifier = Modifier.height(6.dp)
                )


                Text(

                    text = order.pickupCode,

                    fontSize = 30.sp,

                    fontWeight = FontWeight.Bold,

                    color = Color(0xFF4CAF50)
                )
            }
        }


        Spacer(
            modifier = Modifier.height(18.dp)
        )


        // ORDER INFORMATION
        Card(

            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(14.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor = Color.White
                )
        ) {


            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {


                Text(
                    text = "Pickup Details",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )


                Spacer(
                    modifier = Modifier.height(14.dp)
                )


                BuyerQrInformationRow(

                    label = "Shop",

                    value = order.shopName
                )


                Spacer(
                    modifier = Modifier.height(9.dp)
                )


                BuyerQrInformationRow(

                    label = "Address",

                    value = order.shopAddress
                )


                Spacer(
                    modifier = Modifier.height(9.dp)
                )


                BuyerQrInformationRow(

                    label = "Food",

                    value = order.foodName
                )


                Spacer(
                    modifier = Modifier.height(9.dp)
                )


                BuyerQrInformationRow(

                    label = "Quantity",

                    value = order.quantity.toString()
                )


                Spacer(
                    modifier = Modifier.height(9.dp)
                )


                BuyerQrInformationRow(

                    label = "Status",

                    value = formatQrOrderStatus(order.status)
                )


                Spacer(
                    modifier = Modifier.height(9.dp)
                )


                BuyerQrInformationRow(

                    label = "Pickup Before",

                    value = formatQrPickupTime(order.pickupEndAt)
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(18.dp)
        )


        Surface(

            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(10.dp),

            color =
                Color(0xFFE8F5E9)
        ) {


            Text(

                text = "Please do not share your pickup code with other people.",

                modifier =
                    Modifier.padding(14.dp),

                fontSize = 12.sp,

                color =
                    Color(0xFF2E7D32)
            )
        }
    }
}


@Composable
private fun BuyerQrInformationRow(

    label: String,

    value: String

) {


    Row(

        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement = Arrangement.SpaceBetween,

        verticalAlignment = Alignment.Top
    ) {


        Text(
            text = label,
            fontSize = 13.sp,
            color = Color.Gray
        )


        Text(

            text = value,

            modifier =
                Modifier.padding(
                    start = 20.dp
                ),

            fontSize = 13.sp,

            fontWeight = FontWeight.Medium
        )
    }
}


private fun formatQrOrderStatus(
    status: String
): String {


    return when (
        status.uppercase()
    ) {
        "PENDING" -> "Pending"

        "CONFIRMED" -> "Confirmed"

        "READY",
        "READY_FOR_PICKUP" -> "Ready for Pickup"

        "COMPLETED" -> "Completed"

        "CANCELLED" -> "Cancelled"


        else ->
            status
    }
}


private fun formatQrPickupTime(
    pickupEndAt: Long
): String {


    if (
        pickupEndAt <= 0
    ) {

        return "Not available"
    }


    return try {


        val milliseconds =

            if (
                pickupEndAt < 100_000_000_000L
            ) {

                pickupEndAt * 1000

            } else {

                pickupEndAt
            }


        val formatter =
            SimpleDateFormat(
                "dd MMM yyyy, hh:mm a",
                Locale.getDefault()
            )

        formatter.format(
            Date(
                milliseconds
            )
        )
    } catch (e: Exception) {
        "Not available"
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true
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