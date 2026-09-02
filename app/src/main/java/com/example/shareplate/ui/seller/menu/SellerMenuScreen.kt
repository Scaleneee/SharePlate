package com.example.shareplate.ui.seller.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shareplate.data.entity.FoodItem
import com.example.shareplate.ui.seller.home.foodItems
import com.example.shareplate.ui.seller.navigation.SellerBottomBar
import com.example.shareplate.ui.theme.SharePlateTheme

@Composable
@Preview
fun SellerMenuScreenPreview() {
    SharePlateTheme {
        SellerMenuScreen({}, {}, {}, {}, foodItems, {}, {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerMenuScreen(
    // for navigation bar
    onHomeClick: () -> Unit,
    onMenuClick: () -> Unit,
    onActivityClick: () -> Unit,
    onProfileClick: () -> Unit,
    foodItems: List<FoodItem>,
    onAddFoodClick: () -> Unit,
    onEditFoodClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddFoodClick,
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Food"
                )
            }
        },
        bottomBar = {
            SellerBottomBar(
                selectedIndex = 1,
                onHomeClick = onHomeClick,
                onMenuClick = onMenuClick,
                onActivityClick = onActivityClick,
                onProfileClick = onProfileClick
            )
        },
        topBar = {
            // title
            TopAppBar(
                title = {
                    Text(
                        text = "Frequently Wasted Menu",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            )
        }

    ) { innerPadding ->
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // show all foods inside the frequently wasted menu
            items(
                items = foodItems,
                key = { it.foodItemId }
            ) { item ->
                FoodMenuRow(
                    foodItem = item,
                    onEditClick = { onEditFoodClick(item.foodItemId) },
                )
            }
        }
    }
}
