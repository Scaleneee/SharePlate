package com.example.shareplate.ui.seller.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shareplate.data.entity.FoodItem
import com.example.shareplate.ui.seller.home.foodItems
import com.example.shareplate.ui.theme.SharePlateTheme

@Composable
@Preview
fun SellerMenuScreenPreview() {
    SharePlateTheme {
        SellerMenuScreen(foodItems, {}, {})
    }
}

@Composable
fun SellerMenuScreen(
    foodItems: List<FoodItem>,
    onAddFoodClick: () -> Unit,
    onEditFoodClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddFoodClick,
                text = {
                    Text("Add Food")
                },
                icon = {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Food"
                    )
                }
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // title
            item {
                Text(
                    text = "Frequently Wasted Menu",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            // show all foods inside the frequently wasted menu
            items(
                items = foodItems,
                key = {it.foodItemId}
            ) { item ->
                FoodMenuRow(
                    foodItem = item,
                    onEditClick = {onEditFoodClick(item.foodItemId)},
                )
            }
        }
    }
}
