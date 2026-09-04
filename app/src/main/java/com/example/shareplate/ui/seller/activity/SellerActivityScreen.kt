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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shareplate.data.model.SellerPickupActivityItem
import com.example.shareplate.ui.seller.navigation.SellerBottomBar
import com.example.shareplate.ui.theme.SharePlateTheme
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


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
    surplusActivities: List<SellerActivityItem>,
    pickupActivities: List<SellerPickupActivityItem>,

    onMarkPickedUp: (SellerPickupActivityItem) -> Unit = {},

    onUpdateQuantity:
        (SellerActivityItem, Int) -> Unit =
        { _, _ -> },

    onHomeClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onActivityClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {

    // 0 = surplus
    // 1 = pickup
    var selectedTab by remember {
        mutableIntStateOf(0)
    }


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

                onHomeClick =
                    onHomeClick,

                onMenuClick =
                    onMenuClick,

                onActivityClick =
                    onActivityClick,

                onProfileClick =
                    onProfileClick
            )
        }

    ) { innerPadding ->


        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
        ) {


            /**
             * Tabs
             */
            TabRow(
                selectedTabIndex =
                    selectedTab
            ) {


                Tab(

                    selected =
                        selectedTab == 0,

                    onClick = {
                        selectedTab = 0
                    },

                    text = {

                        Text(
                            text =
                                "Surplus"
                        )
                    }
                )


                Tab(

                    selected =
                        selectedTab == 1,

                    onClick = {
                        selectedTab = 1
                    },

                    text = {

                        Text(
                            text =
                                "Pickups"
                        )
                    }
                )
            }


            when (selectedTab) {

                0 -> {
                    SurplusActivityList(

                        activities =
                            surplusActivities,

                        onUpdateQuantity =
                            onUpdateQuantity
                    )
                }


                1 -> {

                    PickupActivityList(
                        pickupActivities = pickupActivities,
                        onMarkPickedUp = onMarkPickedUp
                    )
                }
            }
        }
    }
}


/**
 * SURPLUS ACTIVITY LIST
 */
@Composable
fun SurplusActivityList(

    activities:
    List<SellerActivityItem>,

    onUpdateQuantity:
        (SellerActivityItem, Int) -> Unit
) {

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 20.dp
                )
    ) {

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        Text(

            text =
                "Published Surplus",

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
                "View and update your published surplus quantity.",

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

        if (
            activities.isEmpty()
        ) {

            EmptyActivity(

                title =
                    "No published surplus",

                message =
                    "Published surplus will appear here."
            )

        } else {

            LazyColumn(

                verticalArrangement =
                    Arrangement.spacedBy(
                        14.dp
                    )
            ) {

                items(

                    items =
                        activities,

                    key = {
                        it.listingId
                    }

                ) { activity ->

                    SurplusActivityCard(

                        activity =
                            activity,

                        onUpdateQuantity =
                            onUpdateQuantity
                    )
                }

                item {

                    Spacer(
                        modifier =
                            Modifier.height(
                                20.dp
                            )
                    )
                }
            }
        }
    }
}


/**
 * SURPLUS ACTIVITY CARD
 */
@Composable
fun SurplusActivityCard(

    activity:
    SellerActivityItem,

    onUpdateQuantity:
        (SellerActivityItem, Int) -> Unit
) {

    var isEditing by remember(
        activity.listingId
    ) {
        mutableStateOf(false)
    }


    var quantityText by remember(
        activity.listingId,
        activity.publishedQuantity
    ) {

        mutableStateOf(
            activity
                .publishedQuantity
                .toString()
        )
    }


    /**
     * Cannot edit after NGO transfer,
     * completed or cancelled.
     */
    val canEdit =
        activity.status == "ACTIVE" ||
                activity.status == "SOLD_OUT"


    /**
     * Number already reserved by buyers.
     */
    val reservedQuantity =
        activity.publishedQuantity -
                activity.availableQuantity


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
                .padding(
                    18.dp
                )
    ) {

        /**
         * Food name + status
         */
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
                        Modifier.height(
                            4.dp
                        )
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
                    Modifier.width(
                        10.dp
                    )
            )


            StatusBadge(
                status =
                    activity.status
            )
        }


        Spacer(
            modifier =
                Modifier.height(
                    18.dp
                )
        )


        /**
         * Quantity information
         */
        Row(

            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            ActivityInfo(

                title =
                    "Published",

                value =
                    activity
                        .publishedQuantity
                        .toString()
            )


            ActivityInfo(

                title =
                    "Remaining",

                value =
                    activity
                        .availableQuantity
                        .toString()
            )


            ActivityInfo(

                title =
                    "Discount",

                value =
                    "${activity.discountPercent}%"
            )
        }


        Spacer(
            modifier =
                Modifier.height(
                    18.dp
                )
        )


        Row(

            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(
                text =
                    "Current Price"
            )


            Text(

                text =
                    formatPrice(
                        activity
                            .currentPriceCent
                    ),

                fontWeight =
                    FontWeight.Bold
            )
        }


        /**
         * Edit section
         */
        if (
            isEditing
        ) {

            Spacer(
                modifier =
                    Modifier.height(
                        18.dp
                    )
            )


            OutlinedTextField(

                value =
                    quantityText,

                onValueChange = { value ->

                    if (
                        value.all {
                            it.isDigit()
                        } &&
                        value.length <= 4
                    ) {

                        quantityText =
                            value
                    }
                },

                label = {

                    Text(
                        "Published Quantity"
                    )
                },

                supportingText = {

                    Text(
                        "Minimum: $reservedQuantity " +
                                "($reservedQuantity already reserved)"
                    )
                },

                singleLine = true,

                keyboardOptions =
                    KeyboardOptions(

                        keyboardType =
                            KeyboardType.Number
                    ),

                modifier =
                    Modifier.fillMaxWidth()
            )


            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )


            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.End
            ) {

                TextButton(

                    onClick = {

                        quantityText =
                            activity
                                .publishedQuantity
                                .toString()

                        isEditing =
                            false
                    }

                ) {

                    Text(
                        "Cancel"
                    )
                }


                Spacer(
                    modifier =
                        Modifier.width(
                            8.dp
                        )
                )


                Button(

                    onClick = {

                        val newQuantity =
                            quantityText
                                .toIntOrNull()

                        if (
                            newQuantity != null
                        ) {

                            onUpdateQuantity(
                                activity,
                                newQuantity
                            )

                            isEditing =
                                false
                        }
                    },

                    enabled =
                        quantityText
                            .toIntOrNull()
                            ?.let {

                                it >=
                                        reservedQuantity

                            } == true

                ) {

                    Text(
                        "Save"
                    )
                }
            }

        } else {

            Spacer(
                modifier =
                    Modifier.height(
                        18.dp
                    )
            )


            Button(

                onClick = {

                    quantityText =
                        activity
                            .publishedQuantity
                            .toString()

                    isEditing =
                        true
                },

                enabled =
                    canEdit,

                modifier =
                    Modifier.fillMaxWidth()

            ) {

                Text(

                    text =
                        if (
                            canEdit
                        ) {

                            "Edit Quantity"

                        } else {

                            "Quantity Locked"
                        }
                )
            }
        }
    }
}


/**
 * PICKUP ACTIVITY LIST
 */
@Composable
fun PickupActivityList(
    pickupActivities: List<SellerPickupActivityItem>,
    onMarkPickedUp: (SellerPickupActivityItem) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "Pickup Activity",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "Track buyer and NGO pickups.",
            style = MaterialTheme.typography.bodyMedium,
            color =
                MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        if (pickupActivities.isEmpty()) {

            EmptyActivity(
                title = "No pickup activity",
                message =
                    "Buyer and NGO pickups will appear here."
            )

        } else {

            LazyColumn(
                verticalArrangement =
                    Arrangement.spacedBy(14.dp)
            ) {

                items(
                    items = pickupActivities,
                    key = {
                        "${it.pickupType}-${it.pickupId}"
                    }
                ) { pickup ->

                    PickupActivityCard(

                        pickup = pickup,

                        onMarkPickedUp = {

                            onMarkPickedUp(
                                pickup
                            )
                        }
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


/**
 * PICKUP ACTIVITY CARD
 */
@Composable
fun PickupActivityCard(
    pickup: SellerPickupActivityItem,
    onMarkPickedUp: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color =
                    MaterialTheme
                        .colorScheme
                        .outlineVariant,
                shape =
                    RoundedCornerShape(18.dp)
            )
            .padding(18.dp)
    ) {

        // Receiver + status
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
                        pickup.receiverName,
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
                        pickup.pickupType,
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

            StatusBadge(
                status =
                    pickup.status
            )
        }

        Spacer(
            modifier =
                Modifier.height(18.dp)
        )

        // Food
        Text(
            text =
                pickup.foodName,
            style =
                MaterialTheme
                    .typography
                    .bodyLarge,
            fontWeight =
                FontWeight.SemiBold
        )

        Spacer(
            modifier =
                Modifier.height(4.dp)
        )

        Text(
            text =
                "Quantity: ${pickup.quantity}",
            style =
                MaterialTheme
                    .typography
                    .bodyMedium
        )

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        // Pickup information
        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Column {

                Text(
                    text =
                        "Pickup Code",
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )

                Text(
                    text =
                        pickup.pickupCode,
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.Bold
                )
            }

            Column(
                horizontalAlignment =
                    Alignment.End
            ) {

                Text(
                    text =
                        if (
                            pickup.pickupType ==
                            "BUYER"
                        ) {
                            "Pickup Before"
                        } else {
                            "Pickup Time"
                        }
                )

                Text(
                    text =
                        formatActivityTime(
                            pickup.pickupTime
                        ),
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    fontWeight =
                        FontWeight.SemiBold
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(18.dp)
        )

        // PICKED UP BUTTON
        Button(
            onClick = {
                onMarkPickedUp()
            },

            // cannot click again after collected
            enabled =
                pickup.status != "COLLECTED" &&
                        pickup.status != "CANCELLED" &&
                        pickup.status != "MISSED",

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text(
                text =
                    if (
                        pickup.status ==
                        "COLLECTED"
                    ) {
                        "Picked Up"
                    } else {
                        "Mark as Picked Up"
                    }
            )
        }
    }
}


/**
 * STATUS
 */
@Composable
fun StatusBadge(

    status: String
) {

    val text =
        when (status) {

            "ACTIVE" ->
                "Active"

            "SOLD_OUT" ->
                "Sold Out"

            "TRANSFERRED_TO_NGO" ->
                "NGO"

            "COMPLETED" ->
                "Completed"

            "RESERVED" ->
                "Reserved"

            "READY_FOR_PICKUP" ->
                "Ready"

            "SCHEDULED" ->
                "Scheduled"

            "ON_THE_WAY" ->
                "On The Way"

            "COLLECTED" ->
                "Collected"

            "MISSED" ->
                "Missed"

            "CANCELLED" ->
                "Cancelled"

            else ->
                status
                    .replace(
                        "_",
                        " "
                    )
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

                    horizontal =
                        10.dp,

                    vertical =
                        5.dp
                )
    ) {


        Text(

            text = text,

            style =
                MaterialTheme
                    .typography
                    .labelMedium,

            fontWeight =
                FontWeight.Bold
        )
    }
}


/**
 * SMALL INFORMATION COLUMN
 */
@Composable
fun ActivityInfo(

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
                Modifier.height(
                    3.dp
                )
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


/**
 * EMPTY SCREEN
 */
@Composable
fun EmptyActivity(

    title: String,

    message: String
) {

    Box(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical =
                        80.dp
                ),

        contentAlignment =
            Alignment.Center
    ) {


        Column(

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {


            Text(

                text = title,

                style =
                    MaterialTheme
                        .typography
                        .titleMedium,

                fontWeight =
                    FontWeight.Bold
            )


            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )


            Text(

                text = message,

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



fun formatActivityTime(
    value: String
): String {

    return try {

        val localTime =
            OffsetDateTime
                .parse(value)
                .atZoneSameInstant(
                    ZoneId.systemDefault()
                )

        localTime.format(
            DateTimeFormatter.ofPattern(
                "dd MMM, h:mm a"
            )
        )

    } catch (e: Exception) {

        value
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

            surplusActivities =
                listOf(

                    SellerActivityItem(

                        listingId = 1,

                        foodName =
                            "Blueberry Bread",

                        publishedQuantity =
                            10,

                        availableQuantity =
                            6,

                        discountPercent =
                            70,

                        currentPriceCent =
                            165,

                        status =
                            "ACTIVE",

                        publishedTime =
                            "Today, 4:00 PM"
                    )
                ),

            pickupActivities =
                listOf(

                    SellerPickupActivityItem(

                        pickupId = 1,

                        receiverName =
                            "Brian",

                        pickupType =
                            "BUYER",

                        foodName =
                            "Blueberry Bread",

                        quantity =
                            2,

                        pickupCode =
                            "B823",

                        pickupTime =
                            "Today, 8:30 PM",

                        status =
                            "READY_FOR_PICKUP"
                    ),

                    SellerPickupActivityItem(

                        pickupId = 2,

                        receiverName =
                            "Food Aid Penang",

                        pickupType =
                            "NGO",

                        foodName =
                            "Chocolate Croissant",

                        quantity =
                            5,

                        pickupCode =
                            "N572",

                        pickupTime =
                            "Today, 10:15 PM",

                        status =
                            "SCHEDULED"
                    )
                )
        )
    }
}