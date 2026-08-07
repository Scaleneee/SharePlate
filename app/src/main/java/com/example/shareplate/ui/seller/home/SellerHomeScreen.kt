package com.example.shareplate.ui.seller.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shareplate.R

/**
 * Seller Home Screen function
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun SellerHomeScreen(
    sellerName: String = "Bread History",
    onAddSurplusClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onoNotificationClick: () -> Unit = {}
) {
    var selectedNavigationItem by remember {
        mutableIntStateOf(0)
    }

    /**
     * Screen Scaffold
     */
    Scaffold(
        // declare the top app bar
        topBar = {
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
                        onClick = onoNotificationClick
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.notification),
                            contentDescription = "Notification",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                modifier = Modifier.padding(8.dp)
            )
        },
        // bottom navigation bar
        bottomBar = {
            NavigationBar {
                // homepage navigation
                NavigationBarItem(
                    selected = selectedNavigationItem == 0,
                    onClick = {
                        selectedNavigationItem = 0
                    },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.home),
                            contentDescription = "Home"
                        )
                    },
                    label = {
                        Text(
                            text = "Home",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
                // frequently wasted menu navigation
                NavigationBarItem(
                    selected = selectedNavigationItem == 1,
                    onClick = {
                        selectedNavigationItem = 1
                    },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.bakery_menu),
                            contentDescription = "Menu"
                        )
                    },
                    label = {
                        Text(
                            text = "Menu",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )

                // history navigation
                NavigationBarItem(
                    selected = selectedNavigationItem == 2,
                    onClick = {
                        selectedNavigationItem = 2
                        onHistoryClick()
                    },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.history),
                            contentDescription = "History"
                        )
                    },
                    label = {
                        Text("History")
                    }
                )

                // profile navigation
                NavigationBarItem(
                    selected = selectedNavigationItem == 3,
                    onClick = {
                        selectedNavigationItem = 3
                        onProfileClick()
                    },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.person),
                            contentDescription = "Profile"
                        )
                    },
                    label = {
                        Text("Profile")
                    }
                )
            }
        }
    ) { innerpadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(innerpadding)
        ) { }
    }
}