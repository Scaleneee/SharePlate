package com.example.shareplate.ui.buyer.home

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shareplate.R
import com.example.shareplate.ui.buyer.BuyerViewModel
import com.example.shareplate.ui.theme.SharePlateTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

data class BuyerOrderDisplay(

    val orderId: Long,
    val listingId: Long,
    val foodName: String,
    val shopName: String,
    val shopAddress: String,
    val quantity: Int,
    val totalPriceCent: Int,
    val pickupCode: String,
    val status: String,
    val orderedAt: String,
    val pickupEndAt: Long
)

//bottom bar
@Composable
fun BuyerActivityBottomBar(
    selectedIndex: Int,
    onHomeClick: () -> Unit,
    onOrderClick: () -> Unit,
    onActivityClick: () -> Unit,
    onProfileClick: () -> Unit
) {

    NavigationBar(
        containerColor = Color.White

    ) {
        NavigationBarItem(

            selected = selectedIndex == 0,
            onClick = onHomeClick,
            icon = {
                Icon(
                    painter = painterResource(
                        R.drawable.home
                    ),
                    contentDescription = "Home"
                )
            },

            label = {
                Text(
                    text = "Home",
                    style = MaterialTheme.typography.bodySmall
                )
            })

        NavigationBarItem(
            selected = selectedIndex == 1,
            onClick = onOrderClick,
            icon = {
                Icon(
                    painter = painterResource(
                            R.drawable.bakery_menu
                        ),
                    contentDescription = "Order"
                )
            },

            label = {
                Text(
                    text = "Order",
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall
                )
            }
        )


        NavigationBarItem(
            selected = selectedIndex == 2,
            onClick = onActivityClick,
            icon = {
                Icon(
                    painter = painterResource(
                        R.drawable.history
                    ),
                    contentDescription = "Activity"
                )
            },

            label = {
                Text(
                    text = "Activity",
                    style = MaterialTheme.typography.bodySmall
                )
            })

        NavigationBarItem(
            selected = selectedIndex == 3,
            onClick = onProfileClick,
            icon = {
                Icon(
                    painter = painterResource(
                            R.drawable.person
                        ),

                    contentDescription = "Profile"
                )
            },

            label = {
                Text(
                    text = "Profile",
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall
                )
            }
        )
    }
}


//activity screen
@Composable
fun BuyerActivityScreen(

    buyerViewModel: BuyerViewModel? = null,
    onHomeClick: () -> Unit = {},
    onOrderClick: () -> Unit = {},
    onActivityClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onQrCodeClick: (Long) -> Unit = {}
) {

    val isPreview = LocalInspectionMode.current

    //view model
    val actualViewModel: BuyerViewModel? =

        if (isPreview) {
            null
        } else {
            buyerViewModel ?: viewModel()
        }


    //tab
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

   //view model state
    val ordersState = actualViewModel?.orders?.collectAsState()
    val loadingState = actualViewModel?.isLoading?.collectAsState()
    val errorState = actualViewModel?.errorMessage?.collectAsState()

    val isLoading =
        if (isPreview) {
            false
        } else {
            loadingState
                ?.value
                ?: false
        }

    val errorMessage =
        if (isPreview) {
            null
        } else {
            errorState
                ?.value
        }


    //preview order
    val previewOrders =
        listOf(
            BuyerOrderDisplay(
                orderId = 1,
                listingId = 1,
                foodName = "Blueberry Bread",
                shopName = "Ondo Bakery",
                shopAddress = "Petaling Jaya",
                quantity = 2,
                totalPriceCent = 200,
                pickupCode = "SP4821",
                status = "PENDING",
                orderedAt = "2026-09-02T10:00:00.000Z",
                pickupEndAt = System.currentTimeMillis() + 3_600_000
            ),

            BuyerOrderDisplay(
                orderId = 2,
                listingId = 2,
                foodName = "Sausage Bread",
                shopName = "Ondo Bakery",
                shopAddress = "Petaling Jaya",
                quantity = 1,
                totalPriceCent = 150,
                pickupCode = "SP4821",
                status = "COMPLETED",
                orderedAt = "2026-09-01T10:00:00.000Z",
                pickupEndAt = System.currentTimeMillis()
            )
        )


   //convert view model order to display order
    val orders: List<BuyerOrderDisplay> =
        if (isPreview) {
            previewOrders
        } else {

            ordersState
                ?.value
                ?.map { details ->
                    val order = details.order
                    val listing = details.listing
                    val food = details.foodItem
                    val seller = details.seller
                    val shopName = seller?.organisationName?.takeIf {
                                it.isNotBlank()
                            } ?: seller?.name ?: "Shop"

                    BuyerOrderDisplay(
                        orderId = order.orderId,
                        listingId = order.listingId,
                        foodName = food?.foodName ?: "Food Item",
                        shopName = shopName,
                        shopAddress = seller?.address ?: "Address not provided",
                        quantity = order.quantity,
                        totalPriceCent = order.totalPriceCent,
                        pickupCode = order.pickupCode,
                        status = order.status,
                        orderedAt = order.orderedAt,
                        pickupEndAt = listing?.pickupEndAt ?: 0L
                    )
                } ?: emptyList()
        }


    // load orders
    LaunchedEffect(
        actualViewModel
    ) {

        if (!isPreview) {

            // FIRST LOAD
            actualViewModel
                ?.loadOrders()


            // AUTO REFRESH
            while (isActive) {

                delay(3000)

                actualViewModel
                    ?.loadOrders(
                        showLoading = false
                    )
            }
        }
    }


    //active order
    val activeOrders =
        orders.filter { order ->
            isActiveOrder(
                order.status
            )
        }


    //history order
    val historyOrders =
        orders.filter { order ->
            !isActiveOrder(
                order.status
            )
        }


    Scaffold(
        bottomBar = {
            BuyerActivityBottomBar(
                selectedIndex = 2,
                onHomeClick = onHomeClick,
                onOrderClick = onOrderClick,
                onActivityClick = onActivityClick,
                onProfileClick = onProfileClick
            )
        }

    ) { innerPadding ->


        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 22.dp)

        ) {
            Spacer(
                modifier = Modifier.height(28.dp)
            )


            Text(
                text = "My Orders",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )


            Spacer(
                modifier = Modifier.height(22.dp)
            )


            //active/histoty tab
            BuyerActivityTabs(

                selectedTab = selectedTab,
                onTabSelected = {
                    selectedTab = it
                })

            Spacer(
                modifier = Modifier.height(20.dp)
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
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }
                }


                selectedTab == 0 -> {
                    BuyerOrderList(
                        orders = activeOrders,
                        emptyMessage = "No active orders",
                        showQrCode = true,
                        onQrCodeClick = onQrCodeClick
                    )
                }

                else -> {
                    BuyerOrderList(
                        orders = historyOrders,
                        emptyMessage = "No order history",
                        showQrCode = false,
                        onQrCodeClick = onQrCodeClick
                    )
                }
            }
        }
    }
}


//active/ history tab
@Composable
fun BuyerActivityTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit

) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {


        //active
        Column(

            modifier = Modifier.clickable {
                onTabSelected(
                    0
                )
            },
            horizontalAlignment = Alignment.CenterHorizontally

        ) {


            Text(
                text = "Active",
                fontSize = 16.sp,
                fontWeight =
                    if (selectedTab == 0) {
                        FontWeight.SemiBold
                    } else {
                        FontWeight.Normal
                    },

                color =
                    if (selectedTab == 0) {
                        Color.DarkGray
                    } else {
                        Color.Gray
                    }
            )


            Spacer(
                modifier = Modifier.height(5.dp)
            )


            if (selectedTab == 0) {
                HorizontalDivider(
                    modifier = Modifier.width(60.dp),
                    thickness = 2.dp,
                    color = Color(0xFF4CAF50)
                )
            }
        }


     //history
        Column(

            modifier = Modifier.clickable {

                    onTabSelected(
                        1
                    )
                },

            horizontalAlignment = Alignment.CenterHorizontally

        ) {


            Text(
                text = "History",
                fontSize = 16.sp,
                fontWeight =

                    if (selectedTab == 1) {
                        FontWeight.SemiBold
                    } else {
                        FontWeight.Normal
                    },
                color =
                    if (selectedTab == 1) {
                        Color.DarkGray
                    } else {
                        Color.Gray
                    }
            )


            Spacer(

                modifier =
                    Modifier.height(
                        5.dp
                    )
            )


            if (
                selectedTab == 1
            ) {


                HorizontalDivider(

                    modifier =
                        Modifier.width(
                            60.dp
                        ),

                    thickness =
                        2.dp,

                    color =
                        Color(
                            0xFF4CAF50
                        )
                )
            }
        }
    }
}


// ORDER LIST
@Composable
private fun BuyerOrderList(

    orders: List<BuyerOrderDisplay>,
    emptyMessage: String,
    showQrCode: Boolean,
    onQrCodeClick: (Long) -> Unit

) {

    if (
        orders.isEmpty()
    ) {


        Box(

            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp),

            contentAlignment = Alignment.TopCenter

        ) {


            Text(
                text = emptyMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }


    } else {


        LazyColumn(

            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                bottom = 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {


            items(
                items = orders,
                key = {
                    it.orderId
                }
            ) { order ->


                BuyerOrderCard(
                    order = order,
                    showQrCode = showQrCode,
                    onQrCodeClick = onQrCodeClick
                )
            }
        }
    }
}


//order card
@Composable
private fun BuyerOrderCard(

    order: BuyerOrderDisplay,
    showQrCode: Boolean,
    onQrCodeClick: (Long) -> Unit

) {

    Card(

        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
                containerColor = Color.White
            )

    ) {


        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)

        ) {

            //shop
            Row(

                modifier = Modifier.fillMaxWidth(),

                verticalAlignment = Alignment.CenterVertically

            ) {


                BuyerActivityShopLogo(

                    shopName = order.shopName
                )


                Spacer(
                    modifier = Modifier.width(14.dp)
                )


                Column(

                    modifier = Modifier.weight(
                        1f
                    )

                ) {


                    Text(

                        text = order.shopName,

                        fontSize = 16.sp,

                        fontWeight = FontWeight.SemiBold
                    )


                    Spacer(

                        modifier = Modifier.height(3.dp)
                    )


                    Text(

                        text = order.shopAddress,

                        fontSize = 11.sp,

                        color = Color.Gray
                    )
                }


               //qr code
                if (showQrCode) {


                    BuyerQRCode(

                        pickupCode = order.pickupCode,
                        onClick = {
                            onQrCodeClick(
                                order.orderId
                            )
                        }
                    )
                }
            }


            Spacer(

                modifier =
                    Modifier.height(14.dp)
            )


            HorizontalDivider(

                color =
                    Color(0xFFE0E0E0)
            )


            Spacer(

                modifier = Modifier.height(12.dp)
            )


            // FOOD
            Text(

                text = order.foodName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )


            Spacer(
                modifier = Modifier.height(6.dp)
            )


            OrderInfoRow(

                label = "Quantity",

                value = order.quantity.toString()
            )


            Spacer(

                modifier = Modifier.height(6.dp)
            )


            OrderInfoRow(

                label = "Total",

                value = "RM %.2f".format(

                    order.totalPriceCent / 100.0
                ),

                valueColor = Color(
                    0xFF388E3C
                ),

                bold = true
            )


            Spacer(

                modifier =
                    Modifier.height(6.dp)
            )


            OrderInfoRow(

                label = "Status",

                value =
                    formatOrderStatus(
                        order.status
                    ),

                valueColor =
                    getOrderStatusColor(
                        order.status
                    ),

                bold = true
            )


            Spacer(

                modifier =
                    Modifier.height(6.dp)
            )


            OrderInfoRow(

                label = "Ordered",

                value = formatOrderDate(
                        order.orderedAt
                    )
            )


            // PICKUP CODE
            if (showQrCode) {


                Spacer(

                    modifier =
                        Modifier.height(
                            12.dp
                        )
                )


                Surface(

                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(
                            8.dp
                        ),

                    color =
                        Color(
                            0xFFE8F5E9
                        )

                ) {


                    Column(

                        modifier =
                            Modifier.padding(
                                12.dp
                            )

                    ) {


                        Text(

                            text =
                                "Pickup Code",

                            fontSize =
                                11.sp,

                            color =
                                Color.DarkGray
                        )


                        Spacer(

                            modifier =
                                Modifier.height(
                                    3.dp
                                )
                        )


                        Text(

                            text =
                                order.pickupCode,

                            fontSize =
                                18.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                Color(
                                    0xFF388E3C
                                )
                        )


                        Spacer(

                            modifier =
                                Modifier.height(
                                    4.dp
                                )
                        )


                        Text(

                            text =
                                formatPickupTime(
                                    order.pickupEndAt
                                ),

                            fontSize =
                                11.sp,

                            color =
                                Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}


// INFO ROW
@Composable
private fun OrderInfoRow(

    label: String,

    value: String,

    valueColor: Color =
        Color.Unspecified,

    bold: Boolean =
        false

) {

    Row(

        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.SpaceBetween

    ) {


        Text(

            text =
                label,

            fontSize =
                12.sp,

            color =
                Color.Gray
        )


        Text(

            text =
                value,

            fontSize =
                12.sp,

            fontWeight =

                if (bold) {

                    FontWeight.SemiBold

                } else {

                    FontWeight.Normal
                },

            color =
                valueColor
        )
    }
}


// SHOP LOGO

@Composable
fun BuyerActivityShopLogo(

    shopName: String

) {

    Surface(

        modifier =
            Modifier.size(
                62.dp
            ),

        shape =
            CircleShape,

        color =
            Color(
                0xFFFFF4D6
            )

    ) {


        Box(

            contentAlignment =
                Alignment.Center

        ) {


            Text(

                text =
                    createShopInitials(
                        shopName
                    ),

                fontSize =
                    18.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    Color(
                        0xFFD99B00
                    )
            )
        }
    }
}


// QR BUTTON

@Composable
fun BuyerQRCode(

    pickupCode: String,

    onClick: () -> Unit

) {

    Column(

        modifier = Modifier
            .clickable {

                onClick()
            }
            .padding(
                5.dp
            ),

        horizontalAlignment =
            Alignment.CenterHorizontally

    ) {


        Icon(

            imageVector =
                Icons.Outlined.QrCode2,

            contentDescription =
                "QR Code",

            modifier =
                Modifier.size(
                    30.dp
                ),

            tint =
                Color.DarkGray
        )


        Spacer(

            modifier =
                Modifier.height(
                    2.dp
                )
        )


        Text(

            text =
                "QR code",

            fontSize =
                8.sp,

            color =
                Color.Gray
        )


        Text(

            text =
                pickupCode,

            fontSize =
                8.sp,

            fontWeight =
                FontWeight.SemiBold,

            color =
                Color(
                    0xFF388E3C
                )
        )
    }
}


// =================================================
// ACTIVE STATUS
// =================================================

private fun isActiveOrder(

    status: String

): Boolean {

    return when (
        status.uppercase()
    ) {

        "PENDING",
        "CONFIRMED",
        "READY",
        "READY_FOR_PICKUP" ->
            true

        else ->
            false
    }
}


// STATUS TEXT
private fun formatOrderStatus(

    status: String

): String {

    return when (
        status.uppercase()
    ) {

        "PENDING" ->
            "Pending"

        "CONFIRMED" ->
            "Confirmed"

        "READY",
        "READY_FOR_PICKUP" ->
            "Ready for Pickup"

        "COMPLETED" ->
            "Completed"

        "CANCELLED" ->
            "Cancelled"

        else ->
            status
                .lowercase()
                .replaceFirstChar {

                    it.uppercase()
                }
    }
}


// STATUS COLOR
private fun getOrderStatusColor(

    status: String

): Color {

    return when (
        status.uppercase()
    ) {

        "PENDING" ->
            Color(
                0xFFFF9800
            )

        "CONFIRMED",
        "READY",
        "READY_FOR_PICKUP" ->
            Color(
                0xFF4CAF50
            )

        "COMPLETED" ->
            Color(
                0xFF388E3C
            )

        "CANCELLED" ->
            Color.Red

        else ->
            Color.DarkGray
    }
}


// SHOP INITIALS
private fun createShopInitials(

    shopName: String

): String {

    val words =
        shopName
            .trim()
            .split(" ")
            .filter {

                it.isNotBlank()
            }


    return when {

        words.isEmpty() ->
            "SP"

        words.size == 1 ->
            words[0]
                .take(2)
                .uppercase()

        else ->
            "${words[0].first()}${words[1].first()}"
                .uppercase()
    }
}


//order date
private fun formatOrderDate(

    orderedAt: String

): String {

    return try {
        val inputFormat =
            SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                Locale.US
            )

        val outputFormat =
            SimpleDateFormat(
                "dd MMM yyyy, hh:mm a",
                Locale.getDefault()
            )

        val date =
            inputFormat.parse(
                orderedAt
            )

        if (date != null) {
            outputFormat.format(
                date
            )
        } else {
            orderedAt
        }
    } catch (e: Exception) {
        orderedAt
    }
}


//pickup time
private fun formatPickupTime(

    pickupEndAt: Long

): String {

    if (
        pickupEndAt <= 0
    ) {
        return "Pickup time unavailable"
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

                "dd MMM, hh:mm a",

                Locale.getDefault()
            )


        "Pickup before ${
            formatter.format(
                Date(milliseconds)
            )
        }"


    } catch (e: Exception) {

        "Pickup time available"
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun BuyerActivityScreenPreview() {

    SharePlateTheme(
        dynamicColor = false
    ) {

        BuyerActivityScreen()
    }
}