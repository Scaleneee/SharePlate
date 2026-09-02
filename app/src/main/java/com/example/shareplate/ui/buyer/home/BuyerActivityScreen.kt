package com.example.shareplate.ui.buyer.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shareplate.R
import com.example.shareplate.ui.theme.SharePlateTheme

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
                    painter = painterResource(R.drawable.home),
                    contentDescription = "Home"
                )
            },
            label = {
                Text(
                    text = "Home",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        )

        NavigationBarItem(
            selected = selectedIndex == 1,
            onClick = onOrderClick,
            icon = {
                Icon(
                    painter = painterResource(R.drawable.bakery_menu),
                    contentDescription = "Order"
                )
            },
            label = {
                Text(
                    text = "Order",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        )

        NavigationBarItem(
            selected = selectedIndex == 2,
            onClick = onActivityClick,
            icon = {
                Icon(
                    painter = painterResource(R.drawable.history),
                    contentDescription = "Activity"
                )
            },
            label = {
                Text(
                    text = "Activity",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        )

        NavigationBarItem(
            selected = selectedIndex == 3,
            onClick = onProfileClick,
            icon = {
                Icon(
                    painter = painterResource(R.drawable.person),
                    contentDescription = "Profile"
                )
            },
            label = {
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        )
    }
}

@Composable
fun BuyerActivityScreen(
    onHomeClick: () -> Unit = {},
    onOrderClick: () -> Unit = {},
    onActivityClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onQrCodeClick: () -> Unit = {}
) {

    var selectedTab by rememberSaveable {
        mutableIntStateOf(0)
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
                .padding(horizontal = 26.dp)
        ) {

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            BuyerActivityTabs(
                selectedTab = selectedTab,
                onTabSelected = {
                    selectedTab = it
                }
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            if (selectedTab == 0) {

                BuyerActiveSection(
                    onQrCodeClick = onQrCodeClick
                )

            } else {

                BuyerHistorySection()
            }
        }
    }
}


@Composable
fun BuyerActivityTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {

        Column(
            modifier = Modifier
                .clickable {
                    onTabSelected(0)
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Active",
                fontSize = 16.sp,
                color = if (selectedTab == 0) {
                    Color.DarkGray
                } else {
                    Color.Gray
                }
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            if (selectedTab == 0) {

                HorizontalDivider(
                    modifier = Modifier.width(48.dp),
                    thickness = 1.dp,
                    color = Color.DarkGray
                )
            }
        }


        Column(
            modifier = Modifier
                .clickable {
                    onTabSelected(1)
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "History",
                fontSize = 16.sp,
                color = if (selectedTab == 1) {
                    Color.DarkGray
                } else {
                    Color.Gray
                }
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

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
fun BuyerActiveSection(
    onQrCodeClick: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            BuyerActivityShopLogo()

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Pick up at",
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )

                Text(
                    text = "OndoBakery, Jalan67, 30400,",
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )

                Text(
                    text = "Georgetown",
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )
            }

            BuyerQRCode(
                onClick = onQrCodeClick
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        HorizontalDivider(
            color = Color.Gray,
            thickness = 1.dp
        )
    }
}


@Composable
fun BuyerActivityShopLogo() {

    Surface(
        modifier = Modifier.size(68.dp),
        shape = CircleShape,
        color = Color(0xFFFFF4D6)
    ) {

        Box(
            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "65°",
                    fontSize = 20.sp,
                    color = Color(0xFFD99B00)
                )

                Text(
                    text = "ONDO",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFD99B00)
                )
            }
        }
    }
}


@Composable
fun BuyerQRCode(
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = Icons.Outlined.QrCode2,
            contentDescription = "QR Code",
            modifier = Modifier.size(30.dp),
            tint = Color.DarkGray
        )

        Spacer(
            modifier = Modifier.height(2.dp)
        )

        Text(
            text = "QR code",
            fontSize = 8.sp,
            color = Color.Gray
        )
    }
}


@Composable
fun BuyerHistorySection() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = "No order history",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
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
