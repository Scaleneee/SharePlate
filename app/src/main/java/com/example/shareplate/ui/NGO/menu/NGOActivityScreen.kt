package com.example.shareplate.ui.NGO

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
        horizontalArrangement = Arrangement.SpaceAround
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
    var showQr by remember { mutableStateOf(false) }
    var qrSeed by remember { mutableIntStateOf(0) }

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
                    .clickable { showQr = !showQr; if (showQr) qrSeed++ }
                    .padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!activity.done) {
                    SmallQrIcon()
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("QR code", fontSize = 9.sp, color = Color.Gray)
                } else {
                    Text(
                        text = "Show Item",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        }

        if (showQr) {
            if (activity.done) {
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
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("SCAN THE QR CODE BELOW", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, textAlign = TextAlign.Center)
                    Text("TO GET YOUR FOOD.", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(20.dp))
                    BigQrCode(seed = qrSeed)
                }
            }
        }

        HorizontalDivider(color = Color.Gray, thickness = 1.dp)
    }
}



@Composable
private fun SmallQrIcon() {
    Canvas(modifier = Modifier.size(26.dp)) {
        val cell = size.minDimension / 21.0f
        val dark = Color(0xFF2E2E2E)
        fun block(x: Int, y: Int, s: Int = 1) {
            drawRect(dark, Offset(x * cell, y * cell), Size(cell * s, cell * s))
        }
        listOf(0 to 0, 14 to 0, 0 to 14).forEach { (fx, fy) ->
            drawRect(dark, Offset(fx * cell, fy * cell), Size(cell * 7, cell * 7))
            drawRect(Color.White, Offset((fx + 1) * cell, (fy + 1) * cell), Size(cell * 5, cell * 5))
            drawRect(dark, Offset((fx + 2) * cell, (fy + 2) * cell), Size(cell * 3, cell * 3))
        }
        val pattern = listOf(
            1 to 1,2 to 1,4 to 1,6 to 1,8 to 1,11 to 1,12 to 1,
            1 to 3,5 to 3,9 to 3,12 to 3,2 to 4,4 to 4,7 to 4,10 to 4,12 to 4,
            1 to 5,3 to 5,8 to 5,11 to 5,4 to 6,6 to 6,9 to 6,12 to 6,
            1 to 8,3 to 8,7 to 8,10 to 8,12 to 8,2 to 9,5 to 9,8 to 9,11 to 9,
            4 to 10,9 to 10,12 to 10,1 to 11,6 to 11,10 to 11,3 to 12,7 to 12,11 to 12,
            1 to 13,4 to 13,8 to 13,12 to 13,2 to 16,6 to 16,10 to 16,14 to 16,
            4 to 17,9 to 17,12 to 17,3 to 18,7 to 18,11 to 18,
            1 to 19,5 to 19,8 to 19,12 to 19
        )
        pattern.forEach { (x, y) -> block(x, y) }
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

@Composable
private fun BigQrCode(seed: Int) {
    val modules = remember(seed) { generateQrModules(seed) }

    Canvas(modifier = Modifier.size(220.dp)) {
        val cell = size.minDimension / 21.0f
        val dark = Color(0xFF2E2E2E)

        fun block(x: Int, y: Int, s: Int = 1) {
            drawRect(dark, Offset(x * cell, y * cell), Size(cell * s, cell * s))
        }

        listOf(0 to 0, 14 to 0, 0 to 14).forEach { (fx, fy) ->
            drawRect(dark, Offset(fx * cell, fy * cell), Size(cell * 7, cell * 7))
            drawRect(Color.White, Offset((fx + 1) * cell, (fy + 1) * cell), Size(cell * 5, cell * 5))
            drawRect(dark, Offset((fx + 2) * cell, (fy + 2) * cell), Size(cell * 3, cell * 3))
        }

        modules.forEach { (x, y) -> block(x, y) }
    }
}

private fun generateQrModules(seed: Int): List<Pair<Int, Int>> {
    val rng = kotlin.random.Random(seed)
    val modules = mutableListOf<Pair<Int, Int>>()
    for (y in 0 until 21) {
        for (x in 0 until 21) {
            val inFinder = (x < 7 && y < 7) || (x > 13 && y < 7) || (x < 7 && y > 13)
            if (!inFinder && rng.nextBoolean()) {
                modules.add(x to y)
            }
        }
    }
    return modules
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
            shape = RoundedCornerShape(6.dp),
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
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NGOActivityScreenPreview() {
    NGOActivityScreen(pickups = emptyList())
}