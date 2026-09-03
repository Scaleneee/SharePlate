package com.example.shareplate.ui.NGO

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.shareplate.R

@Composable
fun NGOBottomBar(
    selectedIndex: Int,
    onHomeClick: () -> Unit,
    onMenuClick: () -> Unit,
    onActivityClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedIndex == 0,
            onClick = onHomeClick,
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = selectedIndex == 1,
            onClick = onMenuClick,
            icon = { Icon(Icons.Filled.List, contentDescription = "Menu") },
            label = { Text("Menu") }
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
            label = { Text("Activity") }
        )
        NavigationBarItem(
            selected = selectedIndex == 3,
            onClick = onProfileClick,
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Profile") }
        )
    }
}