package com.example.shareplate.ui.seller.activity

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shareplate.ui.seller.navigation.SellerBottomBar
import com.example.shareplate.ui.theme.SharePlateTheme


/**
 * UI model for seller activity.
 *
 * Later this data will come from surplus_listings.
 */
data class SellerActivityItem(
    val listingId: Long,
    val foodName: String,
    val publishedQuantity: Int,
    val availableQuantity: Int,
    val discountPercent: Int,
    val currentPriceCent: Int,
    val status: String,
    val publishedTime: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerActivityScreen(

    activities: List<SellerActivityItem>,

    onHomeClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onActivityClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {

    Scaffold(

        topBar = {

            TopAppBar(
                title = {

                    Text(
                        text = "Activity",
                        style =
                            MaterialTheme
                                .typography
                                .headlineSmall,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            )
        },

        bottomBar = {

            SellerBottomBar(
                selectedIndex = 2,
                onHomeClick = onHomeClick,
                onMenuClick = onMenuClick,
                onActivityClick = onActivityClick,
                onProfileClick = onProfileClick
            )
        }

    ) { innerPadding ->

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(
                        horizontal = 20.dp
                    )
        ) {

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Text(
                text = "Surplus Activity",
                style =
                    MaterialTheme
                        .typography
                        .titleLarge,
                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text =
                    "Track today's published surplus and previous listings.",
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )


            if (activities.isEmpty()) {

                EmptyActivity()

            } else {

                LazyColumn(
                    verticalArrangement =
                        Arrangement.spacedBy(
                            14.dp
                        )
                ) {

                    items(
                        items = activities,
                        key = {
                            it.listingId
                        }
                    ) { activity ->

                        ActivityCard(
                            activity = activity
                        )
                    }

                    item {

                        Spacer(
                            modifier =
                                Modifier.height(20.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ActivityCard(
    activity: SellerActivityItem
) {

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color =
                        MaterialTheme
                            .colorScheme
                            .outlineVariant,
                    shape =
                        RoundedCornerShape(
                            18.dp
                        )
                )
                .padding(18.dp)
    ) {

        // Food name + status
        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text =
                        activity.foodName,
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(
                    text =
                        activity.publishedTime,
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )
            }

            Spacer(
                modifier =
                    Modifier.width(12.dp)
            )

            ActivityStatus(
                status =
                    activity.status
            )
        }


        Spacer(
            modifier =
                Modifier.height(18.dp)
        )


        // Quantity
        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            ActivityInformation(
                title = "Published",
                value =
                    activity
                        .publishedQuantity
                        .toString()
            )

            ActivityInformation(
                title = "Remaining",
                value =
                    activity
                        .availableQuantity
                        .toString()
            )

            ActivityInformation(
                title = "Discount",
                value =
                    "${activity.discountPercent}%"
            )
        }


        Spacer(
            modifier =
                Modifier.height(18.dp)
        )


        // Current price
        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text =
                    "Current Price",
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )

            Text(
                text =
                    formatPrice(
                        activity
                            .currentPriceCent
                    ),
                style =
                    MaterialTheme
                        .typography
                        .titleMedium,
                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}


@Composable
fun ActivityInformation(
    title: String,
    value: String
) {

    Column(
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text = value,
            style =
                MaterialTheme
                    .typography
                    .titleMedium,
            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(3.dp)
        )

        Text(
            text = title,
            style =
                MaterialTheme
                    .typography
                    .bodySmall,
            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )
    }
}


@Composable
fun ActivityStatus(
    status: String
) {

    val displayText =
        when (status) {

            "ACTIVE" ->
                "Active"

            "SOLD_OUT" ->
                "Sold Out"

            "TRANSFERRED_TO_NGO" ->
                "NGO"

            "COMPLETED" ->
                "Completed"

            "CANCELLED" ->
                "Cancelled"

            else ->
                status
        }


    Box(
        modifier =
            Modifier
                .border(
                    width = 1.dp,
                    color =
                        MaterialTheme
                            .colorScheme
                            .outline,
                    shape =
                        RoundedCornerShape(
                            50.dp
                        )
                )
                .padding(
                    horizontal = 12.dp,
                    vertical = 6.dp
                ),
        contentAlignment =
            Alignment.Center
    ) {

        Text(
            text = displayText,
            style =
                MaterialTheme
                    .typography
                    .labelMedium,
            fontWeight =
                FontWeight.Bold
        )
    }
}


@Composable
fun EmptyActivity() {

    Box(
        modifier =
            Modifier.fillMaxSize(),
        contentAlignment =
            Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text =
                    "No activity yet",
                style =
                    MaterialTheme
                        .typography
                        .titleMedium,
                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    "Published surplus will appear here.",
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )
        }
    }
}


fun formatPrice(
    priceCent: Int
): String {

    return String.format(
        "RM %.2f",
        priceCent / 100.0
    )
}


@Preview(
    showBackground = true
)
@Composable
fun PreviewSellerActivityScreen() {

    SharePlateTheme {

        SellerActivityScreen(

            activities =
                listOf(

                    SellerActivityItem(
                        listingId = 1,
                        foodName =
                            "Blueberry Bread",
                        publishedQuantity = 10,
                        availableQuantity = 6,
                        discountPercent = 60,
                        currentPriceCent = 220,
                        status = "ACTIVE",
                        publishedTime =
                            "Today, 8:05 PM"
                    ),

                    SellerActivityItem(
                        listingId = 2,
                        foodName =
                            "Chocolate Croissant",
                        publishedQuantity = 8,
                        availableQuantity = 0,
                        discountPercent = 80,
                        currentPriceCent = 130,
                        status = "SOLD_OUT",
                        publishedTime =
                            "Today, 8:10 PM"
                    ),

                    SellerActivityItem(
                        listingId = 3,
                        foodName =
                            "Chicken Sandwich",
                        publishedQuantity = 5,
                        availableQuantity = 2,
                        discountPercent = 80,
                        currentPriceCent = 160,
                        status =
                            "TRANSFERRED_TO_NGO",
                        publishedTime =
                            "Yesterday, 9:50 PM"
                    )
                )
        )
    }
}