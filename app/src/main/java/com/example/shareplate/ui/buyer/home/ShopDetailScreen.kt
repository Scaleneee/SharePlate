package com.example.shareplate.ui.buyer.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shareplate.data.model.User
import com.example.shareplate.data.repository.BuyerRepository
import com.example.shareplate.ui.buyer.navigation.BuyerBottomBar
import com.example.shareplate.ui.theme.SharePlateTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.shareplate.ui.buyer.order.BuyerCartStore


data class FoodDeal(

    val listingId: Long,

    val foodItemId: Long,

    val sellerId: String,

    val name: String,

    val description: String,

    val price: String,

    val pickupTime: String,

    val availableQuantity: Int,

    val discountPercent: Int
)


@Composable
fun ShopDetailScreen(
    sellerId: String,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onOrderClick: () -> Unit = {},
    onActivityClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {

    val repository =
        remember {
            BuyerRepository()
        }

    val isPreview =
        LocalInspectionMode.current


    var seller by remember(
        sellerId
    ) {
        mutableStateOf<User?>(null)
    }


    var foodDeals by remember(
        sellerId
    ) {
        mutableStateOf<List<FoodDeal>>(
            emptyList()
        )
    }


    var isLoading by remember(
        sellerId
    ) {
        mutableStateOf(true)
    }


    var errorMessage by remember(
        sellerId
    ) {
        mutableStateOf<String?>(null)
    }


    LaunchedEffect(
        sellerId
    ) {

        if (isPreview) {

            seller = User(
                userId =
                    "preview-seller-1",
                name =
                    "Ondo Bakery",
                email =
                    "preview@email.com",
                role =
                    "SELLER",
                organisationName =
                    "Ondo Bakery",
                address =
                    "Petaling Jaya"
            )


            foodDeals = listOf(

                FoodDeal(
                    listingId = 1,
                    foodItemId = 1,
                    sellerId =
                        "preview-seller-1",
                    name =
                        "Blueberry Bread",
                    description =
                        "Best Before: 3 days • Surplus Food: 7",
                    price =
                        "RM 1.00",
                    pickupTime =
                        "Pickup today",
                    availableQuantity = 7,
                    discountPercent = 80
                ),

                FoodDeal(
                    listingId = 2,
                    foodItemId = 2,
                    sellerId =
                        "preview-seller-1",
                    name =
                        "Sausage Bread",
                    description =
                        "Best Before: 3 days • Surplus Food: 5",
                    price =
                        "RM 1.50",
                    pickupTime =
                        "Pickup today",
                    availableQuantity = 5,
                    discountPercent = 70
                )
            )


            isLoading = false

            return@LaunchedEffect
        }


        try {

            isLoading = true

            errorMessage = null


            val sellerResult =
                repository.getSellerById(
                    sellerId
                )


            if (sellerResult == null) {

                errorMessage =
                    "Seller not found."

                isLoading = false

                return@LaunchedEffect
            }


            seller =
                sellerResult


            val foodItems =
                repository
                    .getFoodItemsBySeller(
                        sellerId
                    )


            val listings =
                repository
                    .getActiveListingsBySeller(
                        sellerId
                    )


            val foodItemMap =
                foodItems.associateBy {
                    it.foodItemId
                }


            foodDeals =
                listings
                    .sortedByDescending {
                        it.publishedAt
                    }
                    .mapNotNull { listing ->


                        val food =
                            foodItemMap[
                                listing.foodItemId
                            ]


                        if (food == null) {

                            null

                        } else {


                            FoodDeal(

                                listingId =
                                    listing.listingId,

                                foodItemId =
                                    food.foodItemId,

                                sellerId =
                                    listing.sellerId,

                                name =
                                    food.foodName,

                                description =
                                    buildFoodDescription(
                                        foodDescription =
                                            food.description,
                                        bestBeforeDays =
                                            food.bestBeforeDays,
                                        availableQuantity =
                                            listing.availableQuantity
                                    ),

                                price =
                                    formatPrice(
                                        listing.currentPriceCents
                                    ),

                                pickupTime =
                                    formatPickupTime(
                                        listing.pickupEndAt
                                    ),

                                availableQuantity =
                                    listing.availableQuantity,

                                discountPercent =
                                    listing.currentDiscountPercent
                            )
                        }
                    }


        } catch (e: Exception) {

            errorMessage =
                e.message
                    ?: "Unable to load shop information."

        } finally {

            isLoading = false
        }
    }


    Scaffold(

        bottomBar = {

            BuyerBottomBar(
                selectedIndex = 0,
                onHomeClick =
                    onHomeClick,
                onOrderClick =
                    onOrderClick,
                onActivityClick =
                    onActivityClick,
                onProfileClick =
                    onProfileClick
            )
        }

    ) { innerPadding ->


        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    horizontal = 20.dp
                )
        ) {


            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )


            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {


                IconButton(
                    onClick =
                        onBackClick
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.ArrowBack,
                        contentDescription =
                            "Back"
                    )
                }


                Text(
                    text =
                        "Shop Details",
                    fontSize = 20.sp,
                    fontWeight =
                        FontWeight.SemiBold
                )


                Spacer(
                    modifier =
                        Modifier.weight(1f)
                )


                TextButton(
                    onClick =
                        onOrderClick
                ) {

                    Text(
                        text = "Cart",
                        color =
                            Color(0xFF4CAF50),
                        fontWeight =
                            FontWeight.SemiBold
                    )
                }
            }


            when {

                isLoading -> {

                    Box(
                        modifier =
                            Modifier.fillMaxSize(),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        CircularProgressIndicator()
                    }
                }


                errorMessage != null -> {

                    Box(
                        modifier =
                            Modifier.fillMaxSize(),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text =
                                errorMessage
                                    ?: "Something went wrong.",
                            color = Color.Red
                        )
                    }
                }


                seller == null -> {

                    Box(
                        modifier =
                            Modifier.fillMaxSize(),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text =
                                "Seller not found."
                        )
                    }
                }


                else -> {

                    val currentSeller =
                        seller!!


                    val shopName =
                        currentSeller
                            .organisationName
                            ?.takeIf {
                                it.isNotBlank()
                            }
                            ?: currentSeller.name


                    Spacer(
                        modifier =
                            Modifier.height(18.dp)
                    )


                    Row(

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 8.dp
                            ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {


                        Surface(

                            modifier =
                                Modifier.size(
                                    82.dp
                                ),

                            shape =
                                CircleShape,

                            color =
                                Color(0xFFFFF4D6)
                        ) {


                            Box(
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Text(
                                    text =
                                        createShopShortName(
                                            shopName
                                        ),
                                    fontSize = 22.sp,
                                    fontWeight =
                                        FontWeight.Bold,
                                    color =
                                        Color(0xFFD99B00)
                                )
                            }
                        }


                        Spacer(
                            modifier =
                                Modifier.width(
                                    16.dp
                                )
                        )


                        Column(
                            modifier =
                                Modifier.weight(
                                    1f
                                )
                        ) {


                            Text(
                                text =
                                    shopName,
                                fontSize =
                                    18.sp,
                                fontWeight =
                                    FontWeight.SemiBold
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(
                                        4.dp
                                    )
                            )


                            Text(
                                text =
                                    currentSeller.address
                                        ?: "Address not provided",
                                fontSize =
                                    12.sp,
                                color =
                                    Color.Gray
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(
                                        6.dp
                                    )
                            )


                            Text(
                                text =
                                    if (
                                        currentSeller.closingTime
                                            .isNullOrBlank()
                                    ) {

                                        "Save surplus food and reduce food waste."

                                    } else {

                                        "Closing time: ${currentSeller.closingTime}"
                                    },
                                fontSize =
                                    11.sp,
                                color =
                                    Color.Gray
                            )
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.height(
                                18.dp
                            )
                    )


                    HorizontalDivider(
                        color =
                            Color.LightGray
                    )


                    if (
                        foodDeals.isEmpty()
                    ) {


                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    top = 50.dp
                                ),
                            contentAlignment =
                                Alignment.TopCenter
                        ) {

                            Text(
                                text =
                                    "No surplus food available right now.",
                                fontSize =
                                    14.sp,
                                color =
                                    Color.Gray
                            )
                        }


                    } else {


                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                bottom = 20.dp
                            )
                        ) {

                            items(
                                items = foodDeals,
                                key = {
                                    it.listingId
                                }
                            ) { food ->

                                FoodDealRow(
                                    food = food,
                                    onAddClick = {

                                        BuyerCartStore.addItem(
                                            shopName = shopName,
                                            foodDeal = food
                                        )
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
        }
    }
}


@Composable
private fun FoodDealRow(

    food: FoodDeal,

    onAddClick: () -> Unit
) {


    Row(

        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 14.dp
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {


        Surface(

            modifier = Modifier.size(width = 76.dp, height = 70.dp),
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


        Column(modifier = Modifier.weight(1f)
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


            Row(

                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {


                Column {

                    Text(
                        text = food.price,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF388E3C)
                    )


                    if (
                        food.discountPercent > 0
                    ) {

                        Text(
                            text = "${food.discountPercent}% OFF",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }


                Button(
                    onClick = onAddClick,
                    enabled = food.availableQuantity > 0,
                    modifier = Modifier.height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
                ) {


                    Text(
                        text = "Add",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}


private fun buildFoodDescription(

    foodDescription: String?,
    bestBeforeDays: Int,
    availableQuantity: Int
): String {


    val foodInformation = "Best Before: $bestBeforeDays days • Surplus Food: $availableQuantity"


    return if (foodDescription.isNullOrBlank()) {

        foodInformation

    } else {

        "$foodDescription • $foodInformation"
    }
}


private fun formatPrice(
    priceCent: Int
): String {

    val price = priceCent / 100.0


    return String.format(Locale.getDefault(),
        "RM %.2f",
        price
    )
}


private fun formatPickupTime(
    pickupEndAt: Long
): String {


    if (
        pickupEndAt <= 0
    ) {

        return "Pickup time not available"
    }


    return try {


        val milliseconds =

            if (
                pickupEndAt < 100_000_000_000L
            ) {

                pickupEndAt * 1000

            } else {

                pickupEndAt
            }


        val dateFormat =
            SimpleDateFormat(
                "dd MMM, hh:mm a",
                Locale.getDefault()
            )


        "Pickup before ${
            dateFormat.format(
                Date(milliseconds)
            )
        }"


    } catch (e: Exception) {

        "Pickup available"
    }
}


private fun createShopShortName(
    shopName: String
): String {


    val words = shopName
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
fun ShopDetailScreenPreview() {

    SharePlateTheme(
        dynamicColor = false
    ) {

        ShopDetailScreen(
            sellerId =
                "preview-seller-1"
        )
    }
}