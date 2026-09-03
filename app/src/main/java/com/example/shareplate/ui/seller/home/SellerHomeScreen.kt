package com.example.shareplate.ui.seller.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.shareplate.R
import com.example.shareplate.data.FoodItems
import com.example.shareplate.data.model.FoodItem
import com.example.shareplate.ui.seller.navigation.SellerBottomBar
import com.example.shareplate.ui.theme.SharePlateTheme

/**
 * Preview Function
 */
@Preview
@Composable
fun PreviewSellerHomeScreen() {
    SharePlateTheme {
    }
}

/**
 * Seller Home Screen function
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerHomeScreen(
    sellerName: String,
    foodItems: List<FoodItem>,
    onHomeClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onActivityClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    activeListings: Int = 0,
    awaitingPickup: Int = 0
) {
    // use to store the surplus food quantity
    val quantities = remember {
        mutableStateMapOf<Long, String>()
    }
    /**
     * Screen Scaffold
     */
    Scaffold(
        // declare the top app bar
        topBar = {
            SellerTopBar(sellerName, onNotificationClick)
        },
        // bottom navigation bar
        bottomBar = {
            SellerBottomBar(
                // selected index
                selectedIndex = 0,
                onHomeClick = onHomeClick,
                onMenuClick = onMenuClick,
                onActivityClick = onActivityClick,
                onProfileClick = onProfileClick
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // Dashboard
            item {
                SellerDashboard(
                    activeListings = activeListings,
                    awaitingPickup = awaitingPickup
                )
            }

            // space
            item {
                Spacer(modifier = Modifier.height(18.dp))
            }

            // surplus food list
            item {
                SurplusFoodList(
                    foodItems,
                    quantities = quantities,
                    onQuantityChange = { foodItemId, quantity ->
                        quantities[foodItemId] = quantity
                    },
                    onPublishClick = {}
                )
            }
        }
    }
}

/**
 * Seller Homepage Top Bar
 *  displays logo of the shop, app slogan, and the notification icon
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerTopBar(
    sellerName: String, onNotificationClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row {
                // logo image
                Image(
                    painter = painterResource(R.drawable.account_circle),
                    contentDescription = "Logo",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(Modifier.width(12.dp))
                // title beside the logo
                Column {
                    // top title
                    Text(
                        text = "Hey, $sellerName",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    // small words below the title
                    Text(
                        text = "SharePlate, share more, waste less.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = onNotificationClick
            ) {
                Icon(
                    painter = painterResource(R.drawable.notification),
                    contentDescription = "Notification",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    )
}

/**
 * Dashboard summary of seller
 */
@Composable
fun SellerDashboard(
    modifier: Modifier = Modifier,
    activeListings: Int,
    awaitingPickup: Int
) {
    // title and the dashboard
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top
    ) {
        // title
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 12.dp)
        )
        // dashboard box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            // Active Listing column
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .height(120.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // title
                Text(
                    text = "Active\nListing",
                    textAlign = TextAlign.Center, // separate to two line
                    style = MaterialTheme.typography.titleLarge
                )

                // space
                Spacer(modifier = Modifier.height(12.dp))

                // display the number of active listing
                Text(
                    text = "$activeListings",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            // Awaiting Pickup column
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .height(120.dp)
                    .width(160.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // title
                Text(
                    text = "Awaiting\nPickup",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )

                // space
                Spacer(modifier = Modifier.height(12.dp))

                // display the number of awaiting pickup
                Text(
                    text = "$awaitingPickup",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun SurplusFoodList(
    foodItems: List<FoodItem>,
    quantities: Map<Long, String>,
    onQuantityChange: (Long, String) -> Unit,
    onPublishClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {

        Text(
            text = "Today's Surplus",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        if (foodItems.isNotEmpty()) {
            foodItems.forEach { foodItem ->

                SurplusFoodRow(
                    foodItem = foodItem,

                    quantity =
                        quantities[foodItem.foodItemId] ?: "",

                    onQuantityChange = { newQuantity ->
                        onQuantityChange(
                            foodItem.foodItemId,
                            newQuantity
                        )
                    }
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }
            // if all the quantity is 0, false
            val hasSurplus = quantities.values.any {
                (it.toIntOrNull() ?: 0) > 0
            }
            Button(
                onClick = onPublishClick,
                enabled = hasSurplus, // the button enable only when there has surplus
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Publish Today's Surplus",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            // no item yet
            Text(
                text = "No items in menu yet. set up your frequently wasted menu!",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/**
 * display the food details and allow user to enter the quantity
 */
@Composable
fun SurplusFoodRow(
    foodItem: FoodItem,
    quantity: String = "",
    onQuantityChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Outlined box
    OutlinedCard(
        modifier = modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            if (foodItem.imageUrl != null) {
                // if got image
                // food image
                AsyncImage(
                    model = foodItem.imageUrl,
                    contentDescription = foodItem.foodName,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // show a default icon
                Image(
                    painter = painterResource(R.drawable.food),
                    contentDescription = "No food image",
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = modifier
                    .weight(1f)
            ) {
                // food name
                Text(
                    text = foodItem.foodName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quantity:",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.width(12.dp))
                    // quantity input box
                    Box(
                        modifier = Modifier
                            .width(30.dp)
                            .height(20.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(4.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicTextField(
                            value = quantity,
                            onValueChange = { newValue ->
                                if (
                                    newValue.all { it.isDigit() } &&
                                    newValue.length <= 3
                                ) {
                                    onQuantityChange(newValue)
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            textStyle = MaterialTheme.typography.bodySmall.copy(
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // food price
                Text(
                    // convert from cents to RM
                    text = "RM%.2f".format(
                        foodItem.originalPriceCent / 100.0
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewSurplusFoodRow() {
    SharePlateTheme() {
        SurplusFoodRow(FoodItems.foodItems.get(0), onQuantityChange = {})
    }
}