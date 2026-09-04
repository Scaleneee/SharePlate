package com.example.shareplate.ui.NGO

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shareplate.data.local.NgoLocalStore
import kotlin.math.absoluteValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class NGOActivityItem(
    val name: String,
    val location: String,
    val shortName: String,
    val pickupTime: String,
    val items: String,
    val pickupCode: String = "",
    val orderedAt: String = "",
    val orderId: Long = 0,
    val done: Boolean
)

@Composable
fun NGOActivityScreen(
    ngoViewModel: NGOViewModel? = null,
    onHomeClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onActivityClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val actualViewModel = if (isPreview) null else ngoViewModel ?: viewModel()
    val ordersState = actualViewModel?.orders?.collectAsState()
    val loadingState = actualViewModel?.isLoading?.collectAsState()
    val errorState = actualViewModel?.errorMessage?.collectAsState()

    LaunchedEffect(actualViewModel) {
        if (!isPreview) actualViewModel?.loadOrders()
    }

    val isLoading = if (isPreview) false else loadingState?.value ?: false
    val errorMessage = errorState?.value
    val supabaseOrders = if (isPreview) emptyList() else ordersState?.value ?: emptyList()

    val ngoStore = remember { NgoLocalStore(context) }

    val orders = if (isPreview) {
        previewOrders
    } else {
        val localOrders = ngoStore.getOrders().map { parseOrder(it) }
        (supabaseOrders + localOrders).distinctBy { it.pickupCode }
    }

    val activeOrders = orders.filter { !it.done }
    val historyOrders = orders.filter { it.done }

    Scaffold(
        bottomBar = {
            NGOBottomBar(
                selectedIndex = 2,
                onHomeClick = onHomeClick,
                onMenuClick = onMenuClick,
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
            Spacer(modifier = Modifier.height(28.dp))
            Text("My Orders", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(22.dp))

            NGOActivityTabs(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
            Spacer(modifier = Modifier.height(20.dp))

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                errorMessage != null && orders.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(errorMessage, color = Color.Red, fontSize = 14.sp)
                    }
                }

                selectedTab == 0 -> {
                    NGOOrderList(
                        orders = activeOrders,
                        emptyMessage = "No active orders",
                        onConfirmPickup = {
                            activity -> actualViewModel?.confirmPickup(activity)
                            ngoStore.markOrderDone(activity.pickupCode)
                            selectedTab = 1
                        }
                    )
                }

                else -> {
                    NGOOrderList(
                        orders = historyOrders,
                        emptyMessage = "No order history",
                        onConfirmPickup = {
                            activity -> actualViewModel?.confirmPickup(activity)
                            ngoStore.markOrderDone(activity.pickupCode)
                            selectedTab = 1
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NGOActivityTabs(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            modifier = Modifier.clickable { onTabSelected(0) },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Active",
                fontSize = 16.sp,
                fontWeight = if (selectedTab == 0) FontWeight.SemiBold else FontWeight.Normal,
                color = if (selectedTab == 0) Color.DarkGray else Color.Gray
            )
            Spacer(modifier = Modifier.height(5.dp))
            if (selectedTab == 0) {
                HorizontalDivider(
                    modifier = Modifier.width(60.dp),
                    thickness = 2.dp,
                    color = Color(0xFF4CAF50)
                )
            }
        }

        Column(
            modifier = Modifier.clickable { onTabSelected(1) },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "History",
                fontSize = 16.sp,
                fontWeight = if (selectedTab == 1) FontWeight.SemiBold else FontWeight.Normal,
                color = if (selectedTab == 1) Color.DarkGray else Color.Gray
            )
            Spacer(modifier = Modifier.height(5.dp))
            if (selectedTab == 1) {
                HorizontalDivider(
                    modifier = Modifier.width(60.dp),
                    thickness = 2.dp,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Composable
private fun NGOOrderList(
    orders: List<NGOActivityItem>,
    emptyMessage: String,
    onConfirmPickup: (NGOActivityItem) -> Unit
) {
    if (orders.isEmpty()) {
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
            contentPadding = PaddingValues(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(orders) { activity ->
                NGOOrderCard(activity = activity, onConfirmPickup = onConfirmPickup)
            }
        }
    }
}

@Composable
private fun NGOOrderCard(
    activity: NGOActivityItem,
    onConfirmPickup: (NGOActivityItem) -> Unit
) {

    val code = activity.pickupCode.ifBlank { pickupCodeFor(activity) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NGODonationLogo(shortName = activity.shortName)
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(activity.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(activity.location, fontSize = 11.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFE0E0E0))
            Spacer(modifier = Modifier.height(12.dp))

            Text(activity.items, fontSize = 15.sp, fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(6.dp))
            OrderInfoRow("Quantity", orderQuantity(activity.items).toString())
            Spacer(modifier = Modifier.height(6.dp))
            OrderInfoRow("Total", "RM0.00", valueColor = Color(0xFF388E3C), bold = true)
            Spacer(modifier = Modifier.height(6.dp))
            OrderInfoRow(
                "Status",
                if (activity.done) "Completed" else "Pending",
                valueColor = if (activity.done) Color(0xFF388E3C) else Color(0xFFFF9800),
                bold = true
            )
            Spacer(modifier = Modifier.height(6.dp))
            OrderInfoRow("Ordered", formatOrderDate(activity.orderedAt))

            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFE8F5E9)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Pickup Code", fontSize = 11.sp, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(code, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF388E3C))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(activity.pickupTime, fontSize = 11.sp, color = Color.DarkGray)
                }
            }

            if (!activity.done) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { onConfirmPickup(activity) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Confirm Pickup", color = Color.White)
                }
            }
        }
    }
}


@Composable
private fun OrderInfoRow(
    label: String,
    value: String,
    valueColor: Color = Color.Unspecified,
    bold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = Color.Gray)
        Text(
            value,
            fontSize = 12.sp,
            fontWeight = if (bold) FontWeight.SemiBold else FontWeight.Normal,
            color = valueColor
        )
    }
}

@Composable
fun NGODonationLogo(shortName: String) {
    Surface(
        modifier = Modifier.size(62.dp),
        shape = CircleShape,
        color = Color(0xFFFFF4D6)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = shortName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD99B00))
        }
    }
}

private fun orderQuantity(items: String): Int =
    items.split(",").mapNotNull { it.trim().split(" - ").getOrNull(1)?.trim()?.toIntOrNull() }.sum()

private fun formatOrderDate(value: String): String {
    if (value.isBlank()) return "Just now"

    value.toLongOrNull()?.let { millis ->
        return try {
            SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(millis))
        } catch (e: Exception) {
            value
        }
    }

    return try {
        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val output = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val date = input.parse(value)
        if (date != null) output.format(date) else value
    } catch (e: Exception) {
        value
    }
}

private fun parseOrder(raw: String): NGOActivityItem {
    val parts = raw.split("|")
    val hasCode = parts.size >= 7
    val hasOrderedAt = parts.size >= 8
    return NGOActivityItem(
        name = parts.getOrElse(0) { "" },
        location = parts.getOrElse(1) { "" },
        shortName = parts.getOrElse(2) { "" },
        pickupTime = parts.getOrElse(3) { "" },
        items = parts.getOrElse(4) { "" },
        pickupCode = if (hasCode) parts.getOrElse(5) { "" } else "",
        orderedAt = if (hasOrderedAt) parts.getOrElse(6) { "" } else "",
        done = when {
            hasOrderedAt -> parts.getOrNull(7)?.equals("true") ?: false
            hasCode -> parts.getOrNull(6)?.equals("true") ?: false
            else -> parts.getOrNull(5)?.equals("true") ?: false
        }
    )
}

private fun pickupCodeFor(activity: NGOActivityItem): String {
    val n = (activity.name.hashCode().absoluteValue % 9000) + 1000
    return "SP$n"
}

private val previewOrders = listOf(
    NGOActivityItem(
        name = "Ondo Bakery",
        location = "George Town - 5.0 km",
        shortName = "OB",
        pickupTime = "Pickup before 04 Sept, 02:11 am",
        items = "Blueberry Bread - 7, Sausage Bread - 5",
        done = false
    ),
    NGOActivityItem(
        name = "Ondo Bakery",
        location = "George Town - 5.0 km",
        shortName = "OB",
        pickupTime = "Pickup yesterday",
        items = "Sweet Donuts - 5",
        done = true
    )
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NGOActivityScreenPreview() {
    NGOActivityScreen()
}
