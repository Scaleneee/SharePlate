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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.shareplate.R
import com.example.shareplate.data.repository.BuyerRepository
import com.example.shareplate.ui.buyer.home.navigation.BuyerBottomBar
import com.example.shareplate.ui.theme.SharePlateTheme


data class Shop(
    val sellerId: String,
    val name: String,
    val address: String,
    val shortName: String
)


@Composable
fun BuyerHomeScreen(
    onShopClick: (Shop) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onOrderClick: () -> Unit = {},
    onActivityClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {

    val repository = remember {
        BuyerRepository()
    }

    val isPreview = LocalInspectionMode.current

    var searchText by rememberSaveable {
        mutableStateOf("")
    }

    var shops by remember {
        mutableStateOf<List<Shop>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }


    LaunchedEffect(Unit) {

        if (isPreview) {

            shops = listOf(
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

            isLoading = false

            return@LaunchedEffect
        }


        try {

            isLoading = true

            errorMessage = null


            val sellers =
                repository.getSellers()


            shops = sellers.map { seller ->

                val shopName =
                    seller.organisationName
                        ?.takeIf {
                            it.isNotBlank()
                        }
                        ?: seller.name


                Shop(
                    sellerId = seller.userId,
                    name = shopName,
                    address = seller.address
                        ?: "Address not provided",
                    shortName = createShortName(
                        shopName
                    )
                )
            }


        } catch (e: Exception) {

            errorMessage =
                e.message
                    ?: "Unable to load shops."

        } finally {

            isLoading = false
        }
    }


    val filteredShops = if (
        searchText.isBlank()
    ) {

        shops

    } else {

        shops.filter { shop ->

            shop.name.contains(
                searchText,
                ignoreCase = true
            ) ||
                    shop.address.contains(
                        searchText,
                        ignoreCase = true
                    )
        }
    }


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
                .padding(innerPadding)
                .padding(horizontal = 20.dp),

            contentPadding = PaddingValues(
                bottom = 20.dp
            )
        ) {


            item {

                Spacer(
                    modifier = Modifier.height(30.dp)
                )


                BuyerHeaderSection()


                Spacer(
                    modifier = Modifier.height(20.dp)
                )


                BuyerSearchField(
                    searchText = searchText,
                    onSearchTextChange = {
                        searchText = it
                    }
                )


                Spacer(
                    modifier = Modifier.height(20.dp)
                )


                BuyerQuickButtons()


                Spacer(
                    modifier = Modifier.height(28.dp)
                )


                Text(
                    text = if (
                        searchText.isBlank()
                    ) {
                        "RECOMMENDED SHOPS"
                    } else {
                        "SEARCH RESULTS"
                    },
                    style =
                        MaterialTheme.typography.bodyLarge
                )


                Spacer(
                    modifier = Modifier.height(15.dp)
                )
            }


            if (isLoading) {

                item {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        CircularProgressIndicator()
                    }
                }

            } else if (
                errorMessage != null
            ) {

                item {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text =
                                errorMessage
                                    ?: "Something went wrong.",
                            color = Color.Red,
                            style =
                                MaterialTheme.typography.bodyMedium
                        )
                    }
                }

            } else if (
                filteredShops.isEmpty()
            ) {

                item {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text = "No shops found",
                            style =
                                MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }

            } else {

                items(
                    items = filteredShops,
                    key = { shop ->
                        shop.sellerId
                    }
                ) { shop ->


                    BuyerShopItem(
                        shop = shop,
                        onClick = {
                            onShopClick(shop)
                        }
                    )


                    HorizontalDivider(
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}


@Composable
fun BuyerHeaderSection() {

    Row(
        modifier =
            Modifier.fillMaxWidth(),
        verticalAlignment =
            Alignment.CenterVertically,
        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {


        Column {

            Text(
                text = "Hey, Brian Chew",
                fontSize = 21.sp,
                fontWeight =
                    FontWeight.SemiBold
            )


            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )


            Text(
                text =
                    "Discover food, save money, reduce waste.",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }


        IconButton(
            onClick = {}
        ) {

            Icon(
                painter = painterResource(
                    R.drawable.notification
                ),
                contentDescription =
                    "Notification"
            )
        }
    }
}


@Composable
fun BuyerSearchField(
    searchText: String,
    onSearchTextChange: (String) -> Unit
) {

    OutlinedTextField(

        value = searchText,

        onValueChange =
            onSearchTextChange,

        modifier =
            Modifier.fillMaxWidth(),

        placeholder = {

            Text(
                text = "Search...",
                style =
                    MaterialTheme.typography.bodyMedium
            )
        },

        leadingIcon = {

            Icon(
                painter = painterResource(
                    R.drawable.search
                ),
                contentDescription =
                    "Search"
            )
        },

        singleLine = true,

        shape =
            RoundedCornerShape(10.dp)
    )
}


@Composable
fun BuyerQuickButtons() {

    Row(
        modifier =
            Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {


        OutlinedButton(
            onClick = {},
            modifier =
                Modifier.weight(1f),
            shape =
                RoundedCornerShape(10.dp)
        ) {

            Icon(
                painter = painterResource(
                    R.drawable.location_on
                ),
                contentDescription =
                    "Nearby"
            )


            Spacer(
                modifier =
                    Modifier.width(6.dp)
            )


            Text(
                text = "Near Me"
            )
        }


        OutlinedButton(
            onClick = {},
            modifier =
                Modifier.weight(1f),
            shape =
                RoundedCornerShape(10.dp)
        ) {

            Icon(
                painter = painterResource(
                    R.drawable.favourite
                ),
                contentDescription =
                    "Favourite"
            )


            Spacer(
                modifier =
                    Modifier.width(6.dp)
            )


            Text(
                text = "Favourite"
            )
        }
    }
}


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
            .padding(vertical = 16.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {


        Surface(
            modifier = Modifier
                .size(65.dp)
                .clip(CircleShape),
            color =
                Color(0xFFFFF4D6)
        ) {

            Box(
                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = shop.shortName,
                    fontSize = 18.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        Color(0xFFD99B00)
                )
            }
        }


        Spacer(
            modifier =
                Modifier.width(15.dp)
        )


        Column {

            Text(
                text = shop.name,
                fontSize = 15.sp,
                fontWeight =
                    FontWeight.Medium
            )


            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )


            Text(
                text = shop.address,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}


private fun createShortName(
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

        words.isEmpty() -> {
            "SP"
        }

        words.size == 1 -> {
            words.first()
                .take(2)
                .uppercase()
        }

        else -> {

            "${words[0].first()}${words[1].first()}"
                .uppercase()
        }
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun BuyerHomeScreenPreview() {

    SharePlateTheme(
        dynamicColor = false
    ) {

        BuyerHomeScreen()
    }
}