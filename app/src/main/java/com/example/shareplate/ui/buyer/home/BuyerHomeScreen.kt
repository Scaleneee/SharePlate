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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shareplate.R
import com.example.shareplate.ui.theme.SharePlateTheme


// Shop data
data class Shop(
    val name: String,
    val address: String,
    val shortName: String
)


@Composable
fun BuyerHomeScreen(
    onShopClick: (Shop) -> Unit = {}
) {

    var searchText by rememberSaveable {
        mutableStateOf("")
    }

    val shops = listOf(
        Shop(
            name = "Ondo Bakery",
            address = "Petaling Jaya",
            shortName = "OB"
        ),
        Shop(
            name = "The Coffee Bean & Tea Leaf",
            address = "Kuala Lumpur",
            shortName = "CB"
        ),
        Shop(
            name = "Break History",
            address = "Subang Jaya",
            shortName = "BH"
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {

        item {

            Spacer(modifier = Modifier.height(30.dp))

            BuyerHeaderSection()

            Spacer(modifier = Modifier.height(20.dp))

            BuyerSearchField(
                searchText = searchText,
                onSearchTextChange = {
                    searchText = it
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            BuyerQuickButtons()

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "RECOMMENDED SHOPS",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(15.dp))
        }

        items(shops) { shop ->

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


@Composable
fun BuyerHeaderSection() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Column {

            Text(
                text = "Hey, Brian Chew",
                fontSize = 21.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Discover food, save money, reduce waste.",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        IconButton(
            onClick = {
                // Notification function later
            }
        ) {

            Icon(
                painter = painterResource(R.drawable.notification),
                contentDescription = "Notification"
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
        onValueChange = onSearchTextChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {

            Text(
                text = "Search...",
                fontSize = 13.sp
            )
        },
        leadingIcon = {

            Icon(
                painter = painterResource(R.drawable.search),
                contentDescription = "Search"
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(10.dp)
    )
}


@Composable
fun BuyerQuickButtons() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        OutlinedButton(
            onClick = {
                // Nearby function later
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
        ) {

            Icon(
                painter = painterResource(R.drawable.location_on),
                contentDescription = "Nearby"
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text("Near Me")
        }


        OutlinedButton(
            onClick = {
                // Favourite function later
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
        ) {

            Icon(
                painter = painterResource(R.drawable.favourite),
                contentDescription = "Favourite"
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text("Favourite")
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
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            modifier = Modifier
                .size(65.dp)
                .clip(CircleShape),
            color = Color(0xFFFFF4D6)
        ) {

            Box(
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = shop.shortName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD99B00)
                )
            }
        }

        Spacer(modifier = Modifier.width(15.dp))

        Column {

            Text(
                text = shop.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = shop.address,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun BuyerHomeScreenPreview() {

    SharePlateTheme(
        dynamicColor = false
    ) {

        BuyerHomeScreen()
    }
}