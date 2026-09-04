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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.shareplate.ui.buyer.navigation.BuyerBottomBar
import com.example.shareplate.ui.theme.SharePlateTheme


data class Shop(

    val sellerId: String,

    val name: String,

    val address: String,

    val shortName: String
)


@Composable
fun BuyerHomeScreen(

    buyerViewModel: BuyerViewModel? = null,

    onShopClick: (Shop) -> Unit = {},

    onNotificationClick: () -> Unit = {},

    onHomeClick: () -> Unit = {},

    onOrderClick: () -> Unit = {},

    onActivityClick: () -> Unit = {},

    onProfileClick: () -> Unit = {}

) {

    // PREVIEW
    val isPreview = LocalInspectionMode.current

    // VIEW MODEL
    val actualViewModel: BuyerViewModel? =

        if (isPreview) {

            null

        } else {

            buyerViewModel ?: viewModel()
        }

    // SEARCH
    var searchText by rememberSaveable { mutableStateOf("") }

    // FAVOURITE FILTER
    var showFavouritesOnly by rememberSaveable { mutableStateOf(false) }

    //NEAR ME
    var showNearMeOnly by rememberSaveable { mutableStateOf(false) }

    // VIEWMODEL STATES
    val profileState = actualViewModel?.profile?.collectAsState()
    val sellersState = actualViewModel?.sellers?.collectAsState()
    val loadingState = actualViewModel?.isLoading?.collectAsState()
    val errorState = actualViewModel?.errorMessage?.collectAsState()
    val savedSellerIdsState = actualViewModel?.savedSellerIds?.collectAsState()
    val profile = profileState?.value
    val buyerAddress = profile?.address.orEmpty()
    val sellers = sellersState?.value ?: emptyList()
    val savedSellerIds = savedSellerIdsState?.value ?: emptySet()
    val isLoading =

        if (isPreview) {

            false

        } else {

            loadingState?.value ?: false
        }


    val errorMessage = errorState?.value


    // LOAD BUYER HOME
    LaunchedEffect(
        actualViewModel
    ) {

        if (!isPreview) {

            actualViewModel?.loadBuyerHome()
        }
    }


    // BUYER NAME
    val buyerName =

        if (isPreview) {

            "Brian Chew"

        } else {

            profile?.name?.takeIf {

                it.isNotBlank()
            } ?: "Buyer"
        }


    // SHOPS
    val shops: List<Shop> =

        if (isPreview) {


            listOf(

                Shop(

                    sellerId = "preview-seller-1",

                    name = "Ondo Bakery",

                    address = "Petaling Jaya",

                    shortName = "OB"
                ),


                Shop(

                    sellerId = "preview-seller-2",

                    name = "The Coffee Bean & Tea Leaf",

                    address = "Kuala Lumpur",

                    shortName = "CB"
                ),


                Shop(

                    sellerId = "preview-seller-3",

                    name = "Bread History",

                    address = "Subang Jaya",

                    shortName = "BH"
                )
            )


        } else {


            sellers.map { seller ->


                val shopName = seller.organisationName?.takeIf {

                    it.isNotBlank()
                } ?: seller.name


                Shop(

                    sellerId = seller.userId,

                    name = shopName,

                    address = seller.address ?: "Address not provided",

                    shortName = createShortName(
                        shopName
                    )
                )
            }
        }


    // SEARCH FILTER
    val filteredShops = shops.filter { shop ->

        val matchesSearch =

            searchText.isBlank() ||

                    shop.name.contains(
                        searchText, ignoreCase = true
                    ) ||

                    shop.address.contains(
                        searchText, ignoreCase = true
                    )


        val matchesFavourite =

            !showFavouritesOnly ||

                    savedSellerIds.contains(
                        shop.sellerId
                    )


        val matchesNearMe =

            !showNearMeOnly ||

                    isNearbyAddress(
                        buyerAddress = buyerAddress, sellerAddress = shop.address
                    )


        matchesSearch && matchesFavourite && matchesNearMe
    }


    // SCREEN
    Scaffold(

        bottomBar = {

            BuyerBottomBar(

                selectedIndex = 0,

                onHomeClick = onHomeClick,

                onOrderClick = onOrderClick,

                onActivityClick = onActivityClick,

                onProfileClick = onProfileClick
            )
        }

    ) { innerPadding ->


        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .padding(
                    innerPadding
                )
                .padding(
                    horizontal = 20.dp
                ),

            contentPadding = PaddingValues(
                bottom = 20.dp
            )

        ) {


            item {


                Spacer(

                    modifier = Modifier.height(
                        30.dp
                    )
                )


                // HEADER
                BuyerHeaderSection(

                    buyerName = buyerName,

                    onNotificationClick =
                        onNotificationClick
                )


                Spacer(

                    modifier = Modifier.height(
                        20.dp
                    )
                )


                // SEARCH
                BuyerSearchField(

                    searchText = searchText,

                    onSearchTextChange = {

                        searchText = it
                    })


                Spacer(

                    modifier = Modifier.height(
                        20.dp
                    )
                )


                // QUICK BUTTONS
                BuyerQuickButtons(

                    showNearMeOnly = showNearMeOnly,

                    showFavouritesOnly = showFavouritesOnly,

                    onNearMeClick = {

                        showNearMeOnly = !showNearMeOnly

                        if (showNearMeOnly) {

                            showFavouritesOnly = false
                        }
                    },

                    onFavouriteClick = {

                        showFavouritesOnly =
                            !showFavouritesOnly

                        if (showFavouritesOnly) {

                            showNearMeOnly =
                                false
                        }

                        actualViewModel
                            ?.loadFavourites()
                    }
                )


                Spacer(

                    modifier = Modifier.height(
                        28.dp
                    )
                )


                Text(

                    text =
                        when {

                            showNearMeOnly -> {
                                "SHOPS NEAR ME"
                            }

                            showFavouritesOnly -> {
                                "FAVOURITE SHOPS"
                            }

                            searchText.isNotBlank() -> {
                                "SEARCH RESULTS"
                            }

                            else -> {
                                "RECOMMENDED SHOPS"
                            }
                        },

                    style = MaterialTheme.typography.bodyLarge
                )


                Spacer(

                    modifier = Modifier.height(
                        15.dp
                    )
                )
            }


            // LOADING
            if (isLoading) {


                item {


                    Box(

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 50.dp
                            ),

                        contentAlignment = Alignment.Center

                    ) {


                        CircularProgressIndicator()
                    }
                }


            } else if (errorMessage != null) {


                // ERROR
                item {


                    Box(

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 40.dp
                            ),

                        contentAlignment = Alignment.Center

                    ) {


                        Text(

                            text = errorMessage ?: "Something went wrong.",

                            color = Color.Red,

                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }


            } else if (filteredShops.isEmpty()) {


                // EMPTY
                item {


                    Box(

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 40.dp
                            ),

                        contentAlignment = Alignment.Center

                    ) {


                        Text(

                            text =
                                when {

                                    showNearMeOnly &&
                                            buyerAddress.isBlank() -> {

                                        "Please add your address in Profile first"
                                    }

                                    showNearMeOnly -> {

                                        "No nearby shops found"
                                    }

                                    showFavouritesOnly -> {

                                        "No favourite shops yet"
                                    }

                                    else -> {

                                        "No shops found"
                                    }
                                },

                            style =
                                MaterialTheme.typography.bodyMedium,

                            color =
                                Color.Gray
                        )
                    }
                }


            } else {


                // SHOP LIST
                items(

                    items = filteredShops,

                    key = { shop ->

                        shop.sellerId
                    }

                ) { shop ->


                    BuyerShopItem(

                        shop = shop,

                        onClick = {

                            onShopClick(
                                shop
                            )
                        })


                    HorizontalDivider(

                        color = Color.LightGray
                    )
                }
            }
        }
    }
}


// HEADER
@Composable
fun BuyerHeaderSection(

    buyerName: String,

    onNotificationClick: () -> Unit

) {


    Row(

        modifier = Modifier.fillMaxWidth(),

        verticalAlignment = Alignment.CenterVertically,

        horizontalArrangement = Arrangement.SpaceBetween

    ) {


        Column {


            Text(

                text = "Hey, $buyerName",

                fontSize = 21.sp,

                fontWeight = FontWeight.SemiBold
            )


            Spacer(

                modifier = Modifier.height(
                    4.dp
                )
            )


            Text(

                text = "Discover food, save money, reduce waste.",

                fontSize = 12.sp,

                color = Color.Gray
            )
        }


        IconButton(

            onClick = onNotificationClick

        ) {


            Icon(

                painter = painterResource(
                    R.drawable.notification
                ),

                contentDescription = "Notification"
            )
        }
    }
}


// SEARCH FIELD
@Composable
fun BuyerSearchField(

    searchText: String,

    onSearchTextChange: (String) -> Unit

) {


    OutlinedTextField(

        value = searchText,

        onValueChange = onSearchTextChange,

        modifier = Modifier.fillMaxWidth(),

        placeholder = {


            Text(

                text = "Search...",

                style = MaterialTheme.typography.bodyMedium
            )
        },

        leadingIcon = {


            Icon(

                painter = painterResource(
                    R.drawable.search
                ),

                contentDescription = "Search"
            )
        },

        singleLine = true,

        shape = RoundedCornerShape(
            10.dp
        )
    )
}


// QUICK BUTTONS
@Composable
fun BuyerQuickButtons(

    showNearMeOnly: Boolean,

    showFavouritesOnly: Boolean,

    onNearMeClick: () -> Unit,

    onFavouriteClick: () -> Unit

) {


    Row(

        modifier = Modifier.fillMaxWidth(),

        horizontalArrangement = Arrangement.spacedBy(
            12.dp
        )

    ) {


        OutlinedButton(

            onClick = onFavouriteClick,

            modifier = Modifier.weight(1f),

            shape = RoundedCornerShape(10.dp)

        ) {


            Icon(

                painter = painterResource(
                    R.drawable.location_on
                ),

                contentDescription = "Nearby"
            )


            Spacer(

                modifier = Modifier.width(
                    6.dp
                )
            )


            Text(
                text =

                    if (showNearMeOnly) {

                        "All Shops"

                    } else {

                        "Near Me"
                    }
            )
        }


        OutlinedButton(

            onClick = onNearMeClick,

            modifier = Modifier.weight(
                1f
            ),

            shape = RoundedCornerShape(
                10.dp
            )

        ) {


            Icon(

                painter = painterResource(
                    R.drawable.favourite
                ),

                contentDescription = "Favourite"
            )


            Spacer(

                modifier = Modifier.width(
                    6.dp
                )
            )


            Text(

                text =

                    if (showFavouritesOnly) {

                        "All Shops"

                    } else {

                        "Favourite"
                    }
            )
        }
    }
}


// SHOP ITEM
@Composable
fun BuyerShopItem(

    shop: Shop,

    onClick: () -> Unit

) {


    Row(

        modifier = Modifier
            .fillMaxWidth()
            .clickable {

                onClick()
            }
            .padding(
                vertical = 16.dp
            ),

        verticalAlignment = Alignment.CenterVertically

    ) {


        Surface(

            modifier = Modifier
                .size(
                    65.dp
                )
                .clip(
                    CircleShape
                ),

            color = Color(
                0xFFFFF4D6
            )

        ) {


            Box(

                contentAlignment = Alignment.Center

            ) {


                Text(

                    text = shop.shortName,

                    fontSize = 18.sp,

                    fontWeight = FontWeight.Bold,

                    color = Color(
                        0xFFD99B00
                    )
                )
            }
        }


        Spacer(

            modifier = Modifier.width(
                15.dp
            )
        )


        Column {


            Text(

                text = shop.name,

                fontSize = 15.sp,

                fontWeight = FontWeight.Medium
            )


            Spacer(

                modifier = Modifier.height(
                    4.dp
                )
            )


            Text(

                text = shop.address,

                fontSize = 12.sp,

                color = Color.Gray
            )
        }
    }
}


// CREATE SHORT SHOP NAME
private fun createShortName(

    shopName: String

): String {


    val words = shopName.trim().split(" ").filter {

        it.isNotBlank()
    }


    return when {


        words.isEmpty() -> {

            "SP"
        }


        words.size == 1 -> {

            words.first().take(2).uppercase()
        }


        else -> {

            "${words[0].first()}${words[1].first()}".uppercase()
        }
    }
}

private fun isNearbyAddress(
    buyerAddress: String, sellerAddress: String
): Boolean {

    if (buyerAddress.isBlank() || sellerAddress.isBlank()) {
        return false
    }

    val buyerParts = buyerAddress.lowercase().split(",").map {
            it.trim()
        }.filter {
            it.length >= 4 && !it.all(Char::isDigit)
        }

    val sellerParts = sellerAddress.lowercase().split(",").map {
            it.trim()
        }

    return buyerParts.any { buyerPart ->

        sellerParts.any { sellerPart ->

            sellerPart.contains(
                buyerPart
            ) ||

                    buyerPart.contains(
                        sellerPart
                    )
        }
    }
}


@Preview(
    showBackground = true, showSystemUi = true
)
@Composable
fun BuyerHomeScreenPreview() {


    SharePlateTheme(
        dynamicColor = false
    ) {


        BuyerHomeScreen()
    }
}