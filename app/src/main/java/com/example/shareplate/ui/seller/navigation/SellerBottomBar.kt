package com.example.shareplate.ui.seller.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.shareplate.R

/**
 * Seller Bottom Navigation Bar
 *  displays the navigation selection for users
 *  home, menu, history, profile
 *  allow users navigate to another page
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerBottomBar(
    selectedIndex: Int,
    onHomeClick: () -> Unit,
    onMenuClick: () -> Unit,
    onActivityClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar {
        // homepage navigation
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
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        )
        // frequently wasted menu navigation
        NavigationBarItem(
            selected = selectedIndex == 1,
            onClick = {
                onMenuClick()
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
                Text("Activity")
            }
        )

        // profile navigation
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
                Text("Profile")
            }
        )
    }
}
