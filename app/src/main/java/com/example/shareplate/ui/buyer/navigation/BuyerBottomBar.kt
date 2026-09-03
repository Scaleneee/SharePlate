package com.example.shareplate.ui.buyer.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.shareplate.R

@Composable
fun BuyerBottomBar(
    selectedIndex: Int,
    onHomeClick: () -> Unit,
    onOrderClick: () -> Unit,
    onActivityClick: () -> Unit,
    onProfileClick: () -> Unit
) {

    NavigationBar {

        NavigationBarItem(
            selected = selectedIndex == 0,
            onClick = {
                onHomeClick()
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
                    style = MaterialTheme.typography.bodySmall
                )
            }
        )

        NavigationBarItem(
            selected = selectedIndex == 1,
            onClick = {
                onOrderClick()
            },
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
            onClick = {
                onActivityClick()
            },
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
            onClick = {
                onProfileClick()
            },
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

