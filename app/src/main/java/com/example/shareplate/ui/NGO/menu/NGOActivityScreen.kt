package com.example.shareplate.ui.NGO

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NGOActivityItem(
    val name: String,
    val location: String,
    val shortName: String,
    val pickupTime: String,
    val items: String,
    val done: Boolean
)

@Composable
fun NGOActivityScreen(pickups: List<NGOActivityItem>) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 26.dp)
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        NGOActivityTabs(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        val shown = if (selectedTab == 0) {
            pickups.filter { !it.done }
        } else {
            pickups.filter { it.done }
        }

        if (shown.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (selectedTab == 0) "No active pickups" else "No donation history",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            shown.forEach { activity ->
                NGOActivityRow(activity = activity)
            }
        }
    }
}

@Composable
fun NGOActivityTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceAround
    ) {
        Column(
            modifier = Modifier.clickable { onTabSelected(0) },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Active",
                fontSize = 16.sp,
                color = if (selectedTab == 0) Color.DarkGray else Color.Gray
            )
            Spacer(modifier = Modifier.height(3.dp))
            if (selectedTab == 0) {
                HorizontalDivider(
                    modifier = Modifier.width(48.dp),
                    thickness = 1.dp,
                    color = Color.DarkGray
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
                color = if (selectedTab == 1) Color.DarkGray else Color.Gray
            )
            Spacer(modifier = Modifier.height(3.dp))
            if (selectedTab == 1) {
                HorizontalDivider(
                    modifier = Modifier.width(52.dp),
                    thickness = 1.dp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun NGOActivityRow(activity: NGOActivityItem) {
    var showItem by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NGODonationLogo(shortName = activity.shortName)

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Pick up at", fontSize = 12.sp, color = Color.DarkGray)
                Text(activity.name, fontSize = 12.sp, color = Color.DarkGray)
                Text(activity.location, fontSize = 12.sp, color = Color.DarkGray)
            }

            Column(
                modifier = Modifier
                    .clickable { showItem = !showItem }
                    .padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (activity.done) "Collected" else "Upcoming",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (activity.done) Color(0xFF2E7D32) else Color(0xFFD99B00)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Show Item",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2E7D32)
                )
            }
        }

        if (showItem) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                activity.items.split(",").map { it.trim() }.forEach { raw ->
                    val parts = raw.split(" - ")
                    val name = parts.getOrElse(0) { raw }
                    val surplus = parts.getOrNull(1)?.trim()?.toIntOrNull() ?: 0
                    NGOItemDetailRow(name = name, surplus = surplus)
                }
            }
        }

        HorizontalDivider(color = Color.Gray, thickness = 1.dp)
    }
}

@Composable
private fun NGOItemDetailRow(name: String, surplus: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(width = 64.dp, height = 60.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
            color = Color(0xFFFFF4D6)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = name.take(2).uppercase(),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD99B00)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(name, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Best Before: 2 days • Surplus Food: $surplus",
                fontSize = 10.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text("FREE", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
        }
    }
}

@Composable
fun NGODonationLogo(shortName: String) {
    Surface(
        modifier = Modifier.size(68.dp),
        shape = CircleShape,
        color = Color(0xFFFFF4D6)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = shortName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD99B00)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NGOActivityScreenPreview() {
    NGOActivityScreen(
        pickups = listOf(
            NGOActivityItem("Ondo Bakery", "George Town - 5.0 km", "OB", "Pickup: 9:00 PM", "Blueberry Bread - 7", false),
            NGOActivityItem("Ondo Bakery", "George Town - 5.0 km", "OB", "Pickup: 8:00 PM", "Sweet Donuts - 5", true)
        )
    )
}