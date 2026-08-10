package com.example.shareplate.ui.buyer.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shareplate.ui.theme.SharePlateTheme

data class FoodDeal(
    val name: String,
    val description: String,
    val price: String,
    val pickupTime: String
)

data class ShopDetailData(
    val id: Int,
    val name: String,
    val address: String,
    val shortName: String,
    val description: String,
    val foodDeals: List<FoodDeal>
)

val shopDetailList = listOf(

    ShopDetailData(
        id = 1,
        name = "Ondo Bakery",
        address = "Petaling Jaya",
        shortName = "OB",
        description = "Promote zero hunger, don't waste the food!",
        foodDeals = listOf(

            FoodDeal(
                name = "Blueberry Bread",
                description = "Best Before: 3 days • Surplus Food: 7",
                price = "RM 1.00",
                pickupTime = "Pickup today"
            ),

            FoodDeal(
                name = "Sausage Bread",
                description = "Best Before: 3 days • Surplus Food: 5",
                price = "RM 1.50",
                pickupTime = "Pickup today"
            ),

            FoodDeal(
                name = "Sweet Donuts",
                description = "Best Before: 1 day • Surplus Food: 5",
                price = "RM 0.50",
                pickupTime = "Pickup today"
            )
        )
    ),

    ShopDetailData(
        id = 2,
        name = "The Coffee Bean & Tea Leaf",
        address = "Kuala Lumpur",
        shortName = "CB",
        description = "Enjoy good food while reducing food waste!",
        foodDeals = listOf(

            FoodDeal(
                name = "Butter Croissant",
                description = "Best Before: 1 day • Surplus Food: 4",
                price = "RM 3.00",
                pickupTime = "Pickup today"
            ),

            FoodDeal(
                name = "Chocolate Muffin",
                description = "Best Before: 1 day • Surplus Food: 3",
                price = "RM 2.50",
                pickupTime = "Pickup today"
            ),

            FoodDeal(
                name = "Chicken Sandwich",
                description = "Best Before: 1 day • Surplus Food: 5",
                price = "RM 4.00",
                pickupTime = "Pickup today"
            )
        )
    ),

    ShopDetailData(
        id = 3,
        name = "Bread History",
        address = "Subang Jaya",
        shortName = "BH",
        description = "Save delicious food before it goes to waste!",
        foodDeals = listOf(

            FoodDeal(
                name = "Sausage Bun",
                description = "Best Before: 2 days • Surplus Food: 6",
                price = "RM 1.50",
                pickupTime = "Pickup today"
            ),

            FoodDeal(
                name = "Chocolate Roll",
                description = "Best Before: 2 days • Surplus Food: 4",
                price = "RM 2.00",
                pickupTime = "Pickup today"
            ),

            FoodDeal(
                name = "Sugar Donut",
                description = "Best Before: 1 day • Surplus Food: 8",
                price = "RM 1.00",
                pickupTime = "Pickup today"
            )
        )
    )
)

@Composable
fun ShopDetailScreen(
    shopId: Int,
    onBackClick: () -> Unit = {}
) {

    val selectedShop = shopDetailList.find { shop ->
        shop.id == shopId
    } ?: shopDetailList.first()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBackClick
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Text(
                text = "Shop Details",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                modifier = Modifier.size(82.dp),
                shape = CircleShape,
                color = Color(0xFFFFF4D6)
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = selectedShop.shortName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD99B00)
                    )
                }
            }

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = selectedShop.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = selectedShop.address,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = selectedShop.description,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        HorizontalDivider(
            color = Color.LightGray
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {

            items(
                items = selectedShop.foodDeals
            ) { food ->

                FoodDealRow(
                    food = food
                )

                HorizontalDivider(
                    color = Color.LightGray
                )
            }
        }
    }
}

@Composable
private fun FoodDealRow(
    food: FoodDeal
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            modifier = Modifier.size(
                width = 76.dp,
                height = 70.dp
            ),
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFFFFF4D6)
        ) {

            Box(
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = food.name
                        .take(2)
                        .uppercase(),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD99B00)
                )
            }
        }

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = food.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = food.description,
                fontSize = 10.sp,
                color = Color.Gray
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = food.pickupTime,
                fontSize = 10.sp,
                color = Color.Gray
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = food.price,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ShopDetailScreenPreview() {

    SharePlateTheme(
        dynamicColor = false
    ) {

        ShopDetailScreen(
            shopId = 1
        )
    }
}