package com.example.assignment.ngo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.assignment.NGO.NgoViewModel
import com.example.shareplate.ui.NGO.menu.NGOActivityScreen
import com.example.shareplate.ui.NGO.menu.NGOSuccessScreen

data class FoodDonation(
    val name: String,
    val location: String,
    val availableFood: Int,
    val foodItems: List<String>,
    val nearby: Boolean,
    val liked: Boolean
)

/**
 * NGO Home Screen Function
 * */
@Composable
fun NGOHomeScreen() {
    var selectedDonation by remember { mutableStateOf<FoodDonation?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) }

    val ngoViewModel: NgoViewModel = viewModel()
    val items by ngoViewModel.foodItems.collectAsState()
    val isLoading by ngoViewModel.isLoading.collectAsState()
    val error by ngoViewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) { ngoViewModel.loadFoodItems() }
    var showSuccess by remember { mutableStateOf(false) }

    if (showSuccess) {
        NGOSuccessScreen(onBackClick = { showSuccess = false })
        return
    }


    /**
     * Screen Scaffold
     */
    Scaffold(
        // bottom navigation bar
        bottomBar = {
            NGOBottomBar(
                //selected index
                selectedIndex = selectedTab,
                onHomeClick = { selectedTab = 0 },
                onMenuClick = { selectedTab = 1 },
                onActivityClick = { selectedTab = 2 },
                onProfileClick = { selectedTab = 3 }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> Column {
                    SupabaseStatusBanner(
                        isLoading = isLoading,
                        errorMessage = error,
                        itemCount = items.size
                    )
                    DonationListScreen(
                        onDonationClick = { donation ->
                            selectedDonation = donation
                            selectedTab = 1
                        }
                    )
                }
                1 -> if (selectedDonation == null) {
                    PlaceholderScreen("Menu")
                } else {
                    ShopDetailScreen(
                        donation = selectedDonation!!,
                        onBackClick = {
                            selectedDonation = null
                            selectedTab = 0
                        },
                        onTakeAllClick = { showSuccess = true }
                    )
                }
                2 -> NGOActivityScreen()
                else -> PlaceholderScreen("Profile")
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(title, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DonationListScreen(onDonationClick: (FoodDonation) -> Unit) {
    var donations by remember {
        mutableStateOf(
            listOf(
                FoodDonation(
                    "Sunrise Bakery", "Penang - 1.2 km", 25,
                    listOf("Bread - 10", "Croissant - 8", "Muffin - 7"),
                    true, false),
                FoodDonation("Daily Bake", "Penang - 2.4 km", 18,
                    listOf("Sandwich - 6", "Bun - 7", "Cake slice - 5"),
                    true, false),
                FoodDonation("Ondo Bakery", "George Town - 5.0 km", 12,
                    listOf("Bread - 8", "Cookie pack - 4"),
                    false, false)
            )
        )
    }

    var searchText by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("All") }

    val shownDonations = donations.filter { donation ->
        val matchesSearch = donation.name.contains(searchText, ignoreCase = true)

        val matchesFilter = when (filter) {
            "Near Me" -> donation.nearby
            "Favourite" -> donation.liked
            else -> true
        }

        matchesSearch && matchesFilter
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("Good evening, Hope Orphanage", fontWeight = FontWeight.Bold)
        Text("Penang")

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Search free food") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = {
                    filter = if (filter == "Near Me") "All" else "Near Me"
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (filter == "Near Me") "Show All" else "Near Me")
            }

            Spacer(modifier = Modifier.width(10.dp))

            OutlinedButton(
                onClick = {
                    filter = if (filter == "Favourite") "All" else "Favourite"
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Favourite")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("NEARBY FREE FOOD", fontWeight = FontWeight.Bold)

        if (shownDonations.isEmpty()) {
            Text("No food donations found")
        }

        shownDonations.forEach { donation ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .clickable { onDonationClick(donation) }
            ) {
                Column(modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text(
                        donation.name, fontWeight = FontWeight.Bold
                    )

                    Text(
                        donation.location
                    )

                    Text(
                        "Total Surplus Food: ${donation.availableFood}"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { onDonationClick(donation) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("View Donation")
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        OutlinedButton(
                            onClick = {
                                donations = donations.map {
                                    if (it.name == donation.name) it.copy(liked = !it.liked) else it
                                }
                            },
                            modifier = Modifier.width(52.dp)
                        ) {
                            Text(
                                text = if (donation.liked) "♥" else "♡",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SupabaseStatusBanner(
    isLoading: Boolean,
    errorMessage: String?,
    itemCount: Int
) {
    Text(
        text = when {
            isLoading -> "Loading from Supabase…"
            errorMessage != null -> "Supabase error: $errorMessage"
            else -> "Synced $itemCount food items from Supabase"
        },
        fontSize = 13.sp,
        color = if (errorMessage != null) Color(0xFFB00020) else Color.Gray,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

/**
 * Preview Function
 */
@Preview(showBackground = true)
@Composable
fun NGOHomeScreenPreview(){
    NGOHomeScreen()
}
