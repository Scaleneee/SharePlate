package com.example.shareplate.ui.NGO

import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shareplate.data.local.NgoLocalStore
import com.example.shareplate.data.supabase.AuthRepository
import com.example.shareplate.ui.NGO.order.NGOCartItem
import java.util.Calendar

data class FoodDonation(
    val name: String,
    val location: String,
    val availableFood: Int,
    val foodItems: List<String>,
    val nearby: Boolean,
    val liked: Boolean,
    val sellerId: String = "",
    val inventory: List<NGOCartItem> = emptyList()
)

@Composable
fun NGOHomeScreen(
    onAcceptDonation: (FoodDonation, List<NGOCartItem>) -> Unit = { _, _ -> },
    onMenuClick: () -> Unit = {},
    onActivityClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var selectedDonationId by rememberSaveable { mutableStateOf<String?>(null) }

    val ngoViewModel: NGOViewModel = viewModel()
    val shops by ngoViewModel.shops.collectAsState()
    val isLoading by ngoViewModel.isLoading.collectAsState()
    val error by ngoViewModel.errorMessage.collectAsState()

    val selectedDonation = selectedDonationId?.let { id -> shops.find { it.name == id } }

    LaunchedEffect(Unit) { ngoViewModel.loadShops() }

    Scaffold(
        bottomBar = {
            NGOBottomBar(
                selectedIndex = selectedTab,
                onHomeClick = { selectedTab = 0 },
                onMenuClick = onMenuClick,
                onActivityClick = onActivityClick,
                onProfileClick = onProfileClick
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> Column {
                    DonationListScreen(
                        donations = shops,
                        onDonationClick = { donation ->
                            selectedDonationId = donation.name
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
                            selectedDonationId = null
                            selectedTab = 0
                        },
                        onAcceptClick = { items ->
                            onAcceptDonation(selectedDonation!!, items)
                        }
                    )
                }
                2 -> onActivityClick()
                else -> onProfileClick()
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
private fun SupabaseStatusBanner(
    isLoading: Boolean,
    errorMessage: String?,
    itemCount: Int
) {
    Text(
        text = when {
            isLoading -> "Loading from Supabase…"
            errorMessage != null -> "Supabase error: $errorMessage"
            else -> "Synced $itemCount shops"
        },
        fontSize = 13.sp,
        color = if (errorMessage != null) Color(0xFFB00020) else Color.Gray,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
fun DonationListScreen(
    donations: List<FoodDonation>,
    onDonationClick: (FoodDonation) -> Unit
) {
    val context = LocalContext.current
    val store = remember { NgoLocalStore(context) }

    var userName by remember { mutableStateOf("NGO User") }
    LaunchedEffect(Unit) {
        val profile = AuthRepository().getCurrentProfile()
        userName = profile?.name?.takeIf { it.isNotBlank() }
            ?: profile?.organisationName
            ?: "NGO User"
    }

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 0..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        else -> "Good evening"
    }

    var localDonations by remember(donations) {
        mutableStateOf(donations.map { it.copy(liked = store.getFavourites().contains(it.name)) })
    }

    var searchText by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("All") }

    val shownDonations = localDonations.filter { donation ->
        val matchesSearch = if (searchText.isBlank()) {
            true
        } else {
            donation.foodItems.any { it.contains(searchText, ignoreCase = true) } ||
                donation.inventory.any { it.foodName.contains(searchText, ignoreCase = true) }
        }

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
        Text("$greeting, $userName", fontWeight = FontWeight.Bold)
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

        Text("RECOMMENDED SHOPS", fontWeight = FontWeight.Bold)

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
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(donation.name, fontWeight = FontWeight.Bold)
                    Text(donation.location)
                    Text("Total Surplus Food: ${donation.availableFood}")

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { onDonationClick(donation) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("View Donation")
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        OutlinedButton(
                            onClick = {
                                val newLiked = !donation.liked
                                localDonations = localDonations.map {
                                    if (it.name == donation.name) it.copy(liked = newLiked) else it
                                }
                                store.setFavourite(donation.name, newLiked)
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

@Preview(showBackground = true)
@Composable
fun NGOHomeScreenPreview(){
    NGOHomeScreen()
}
