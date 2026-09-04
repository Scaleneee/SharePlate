package com.example.shareplate.ui.buyer.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shareplate.R
import com.example.shareplate.ui.buyer.BuyerViewModel
import com.example.shareplate.ui.buyer.navigation.BuyerBottomBar
import com.example.shareplate.ui.theme.SharePlateTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


data class Shop(

    val sellerId: String,

    val name: String,

    val address: String,

    val shortName: String,

    val latitude: Double? = null,

    val longitude: Double? = null
)


@Composable
fun BuyerHomeScreen(

    buyerViewModel: BuyerViewModel? = null,

    onShopClick: (Shop) -> Unit = {},

    onNotificationClick: () -> Unit = {},

    onHomeClick: () -> Unit = {},

    onOrderClick: () -> Unit = {},

    onActivityClick: () -> Unit = {},

    onProfileClick: () -> Unit = {}

) {

    // PREVIEW
    val isPreview =
        LocalInspectionMode.current

    // VIEW MODEL
    val actualViewModel: BuyerViewModel? =

        if (isPreview) {

            null

        } else {

            buyerViewModel ?: viewModel()
        }

    // SEARCH
    var searchText by rememberSaveable {

        mutableStateOf("")
    }

    // FAVOURITE
    var showFavouritesOnly by rememberSaveable {

        mutableStateOf(false)
    }

    // NEAR ME
    var showNearMeOnly by rememberSaveable {

        mutableStateOf(false)
    }

    // LOCATION
    val context =
        LocalContext.current


    var buyerLatitude by remember {

        mutableStateOf<Double?>(
            null
        )
    }


    var buyerLongitude by remember {

        mutableStateOf<Double?>(
            null
        )
    }


    var locationError by remember {

        mutableStateOf<String?>(
            null
        )
    }


    var isGettingLocation by remember {

        mutableStateOf(false)
    }


    val fusedLocationClient =
        remember(context) {

            LocationServices
                .getFusedLocationProviderClient(
                    context
                )
        }

    // GET CURRENT LOCATION
    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {

        locationError =
            null

        isGettingLocation =
            true


        val finePermission =
            ContextCompat
                .checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )


        val coarsePermission =
            ContextCompat
                .checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )


        if (
            finePermission !=
            PackageManager.PERMISSION_GRANTED &&

            coarsePermission !=
            PackageManager.PERMISSION_GRANTED
        ) {

            isGettingLocation =
                false

            locationError =
                "Location permission is required for Near Me."

            return
        }


        val cancellationTokenSource =
            CancellationTokenSource()


        fusedLocationClient
            .getCurrentLocation(

                Priority.PRIORITY_HIGH_ACCURACY,

                cancellationTokenSource.token
            )
            .addOnSuccessListener { location ->

                isGettingLocation =
                    false


                if (location != null) {

                    buyerLatitude =
                        location.latitude

                    buyerLongitude =
                        location.longitude

                } else {

                    locationError =
                        "Unable to get your current location."
                }
            }
            .addOnFailureListener {

                isGettingLocation =
                    false

                locationError =
                    "Unable to get your current location."
            }
    }

    // LOCATION PERMISSION
    val locationPermissionLauncher =
        rememberLauncherForActivityResult(

            contract =
                ActivityResultContracts
                    .RequestMultiplePermissions()

        ) { permissions ->


            val fineGranted =
                permissions[
                    Manifest.permission.ACCESS_FINE_LOCATION
                ] == true


            val coarseGranted =
                permissions[
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ] == true


            if (
                fineGranted ||
                coarseGranted
            ) {

                getCurrentLocation()

            } else {

                isGettingLocation =
                    false

                locationError =
                    "Location permission is required for Near Me."
            }
        }

    // VIEW MODEL STATES
    val profileState =
        actualViewModel
            ?.profile
            ?.collectAsState()


    val sellersState =
        actualViewModel
            ?.sellers
            ?.collectAsState()


    val savedSellerIdsState =
        actualViewModel
            ?.savedSellerIds
            ?.collectAsState()


    val unreadNotificationCountState =
        actualViewModel
            ?.unreadNotificationCount
            ?.collectAsState()


    val loadingState =
        actualViewModel
            ?.isLoading
            ?.collectAsState()


    val errorState =
        actualViewModel
            ?.errorMessage
            ?.collectAsState()


    val profile =
        profileState?.value


    val sellers =
        sellersState?.value
            ?: emptyList()


    val savedSellerIds =
        savedSellerIdsState?.value
            ?: emptySet()


    val unreadNotificationCount =

        if (isPreview) {

            2

        } else {

            unreadNotificationCountState
                ?.value
                ?: 0
        }


    val isLoading =

        if (isPreview) {

            false

        } else {

            loadingState
                ?.value
                ?: false
        }


    val errorMessage =
        errorState?.value

    // LOAD HOME + AUTO REFRESH NOTIFICATIONS
    LaunchedEffect(
        actualViewModel
    ) {

        if (!isPreview) {

            // FIRST LOAD
            actualViewModel
                ?.loadBuyerHome()


            // AUTO CHECK NOTIFICATIONS
            while (isActive) {

                delay(
                    3000
                )


                actualViewModel
                    ?.loadNotifications()
            }
        }
    }

    // BUYER NAME
    val buyerName =

        if (isPreview) {

            "Brian Chew"

        } else {

            profile
                ?.name
                ?.takeIf {

                    it.isNotBlank()
                }
                ?: "Buyer"
        }

    // SHOP LIST
    val shops: List<Shop> =

        if (isPreview) {

            listOf(

                Shop(

                    sellerId =
                        "preview-seller-1",

                    name =
                        "Ondo 65",

                    address =
                        "George Town, Penang",

                    shortName =
                        "O6",

                    latitude =
                        5.4141,

                    longitude =
                        100.3288
                ),


                Shop(

                    sellerId =
                        "preview-seller-2",

                    name =
                        "Mixue",

                    address =
                        "George Town, Penang",

                    shortName =
                        "MX",

                    latitude =
                        5.4164,

                    longitude =
                        100.3327
                )
            )

        } else {

            sellers.map { seller ->


                val shopName =

                    seller
                        .organisationName
                        ?.takeIf {

                            it.isNotBlank()
                        }
                        ?: seller.name


                Shop(

                    sellerId =
                        seller.userId,

                    name =
                        shopName,

                    address =
                        seller.address
                            ?: "Address not provided",

                    shortName =
                        createShortName(
                            shopName
                        ),

                    latitude =
                        seller.latitude,

                    longitude =
                        seller.longitude
                )
            }
        }


    // FILTER SHOPS
    val filteredShops =
        shops.filter { shop ->

            // SEARCH
            val matchesSearch =

                searchText.isBlank() ||

                        shop.name.contains(

                            searchText,

                            ignoreCase =
                                true

                        ) ||

                        shop.address.contains(

                            searchText,

                            ignoreCase =
                                true
                        )

            // FAVOURITE
            val matchesFavourite =

                !showFavouritesOnly ||

                        savedSellerIds.contains(
                            shop.sellerId
                        )

            // NEAR ME
            val matchesNearMe =

                if (!showNearMeOnly) {

                    true

                } else {

                    val buyerLat =
                        buyerLatitude

                    val buyerLng =
                        buyerLongitude

                    val sellerLat =
                        shop.latitude

                    val sellerLng =
                        shop.longitude


                    if (
                        buyerLat == null ||
                        buyerLng == null ||
                        sellerLat == null ||
                        sellerLng == null
                    ) {

                        false

                    } else {

                        val distanceKm =
                            calculateDistanceKm(

                                lat1 =
                                    buyerLat,

                                lon1 =
                                    buyerLng,

                                lat2 =
                                    sellerLat,

                                lon2 =
                                    sellerLng
                            )


                        distanceKm <= 5.0
                    }
                }


            matchesSearch &&
                    matchesFavourite &&
                    matchesNearMe
        }

    // SCREEN
    Scaffold(

        bottomBar = {

            BuyerBottomBar(

                selectedIndex =
                    0,

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


        LazyColumn(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        innerPadding
                    )
                    .padding(
                        horizontal = 20.dp
                    ),

            contentPadding =
                PaddingValues(
                    bottom = 20.dp
                )

        ) {


            item {


                Spacer(

                    modifier =
                        Modifier.height(
                            30.dp
                        )
                )

                // HEADER
                BuyerHeaderSection(

                    buyerName =
                        buyerName,

                    unreadNotificationCount =
                        unreadNotificationCount,

                    onNotificationClick =
                        onNotificationClick
                )


                Spacer(

                    modifier =
                        Modifier.height(
                            20.dp
                        )
                )

                // SEARCH
                BuyerSearchField(

                    searchText =
                        searchText,

                    onSearchTextChange = {

                        searchText =
                            it
                    }
                )


                Spacer(

                    modifier =
                        Modifier.height(
                            20.dp
                        )
                )

                // QUICK BUTTONS
                BuyerQuickButtons(

                    showNearMeOnly =
                        showNearMeOnly,

                    showFavouritesOnly =
                        showFavouritesOnly,

                    // NEAR ME
                    onNearMeClick = {


                        if (
                            showNearMeOnly
                        ) {

                            showNearMeOnly =
                                false

                            locationError =
                                null

                        } else {

                            showNearMeOnly =
                                true

                            showFavouritesOnly =
                                false

                            locationError =
                                null


                            val finePermission =
                                ContextCompat
                                    .checkSelfPermission(

                                        context,

                                        Manifest
                                            .permission
                                            .ACCESS_FINE_LOCATION
                                    )


                            val coarsePermission =
                                ContextCompat
                                    .checkSelfPermission(

                                        context,

                                        Manifest
                                            .permission
                                            .ACCESS_COARSE_LOCATION
                                    )


                            if (
                                finePermission ==
                                PackageManager.PERMISSION_GRANTED ||

                                coarsePermission ==
                                PackageManager.PERMISSION_GRANTED
                            ) {

                                getCurrentLocation()

                            } else {

                                isGettingLocation =
                                    true


                                locationPermissionLauncher
                                    .launch(

                                        arrayOf(

                                            Manifest
                                                .permission
                                                .ACCESS_FINE_LOCATION,

                                            Manifest
                                                .permission
                                                .ACCESS_COARSE_LOCATION
                                        )
                                    )
                            }
                        }
                    },

                    // FAVOURITE
                    onFavouriteClick = {

                        showFavouritesOnly =
                            !showFavouritesOnly


                        if (
                            showFavouritesOnly
                        ) {

                            showNearMeOnly =
                                false

                            locationError =
                                null
                        }


                        actualViewModel
                            ?.loadFavourites()
                    }
                )


                Spacer(

                    modifier =
                        Modifier.height(
                            28.dp
                        )
                )

                // TITLE
                Text(

                    text =

                        when {


                            showNearMeOnly -> {

                                "SHOPS NEAR ME"
                            }


                            showFavouritesOnly -> {

                                "FAVOURITE SHOPS"
                            }


                            searchText
                                .isNotBlank() -> {

                                "SEARCH RESULTS"
                            }


                            else -> {

                                "RECOMMENDED SHOPS"
                            }
                        },

                    style =
                        MaterialTheme
                            .typography
                            .bodyLarge
                )


                Spacer(

                    modifier =
                        Modifier.height(
                            15.dp
                        )
                )
            }

            // LOADING
            if (
                isLoading
            ) {


                item {


                    Box(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 50.dp
                                ),

                        contentAlignment =
                            Alignment.Center

                    ) {


                        CircularProgressIndicator()
                    }
                }


            } else if (
                errorMessage != null
            ) {

                // ERROR
                item {


                    Box(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 40.dp
                                ),

                        contentAlignment =
                            Alignment.Center

                    ) {


                        Text(

                            text =
                                errorMessage
                                    ?: "Something went wrong.",

                            color =
                                Color.Red,

                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium
                        )
                    }
                }


            } else if (
                filteredShops.isEmpty()
            ) {

                // EMPTY
                item {


                    Box(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 40.dp
                                ),

                        contentAlignment =
                            Alignment.Center

                    ) {


                        Text(

                            text =

                                when {


                                    showNearMeOnly &&
                                            locationError != null -> {

                                        locationError
                                            ?: "Unable to get location"
                                    }


                                    showNearMeOnly &&
                                            isGettingLocation -> {

                                        "Getting your current location..."
                                    }


                                    showNearMeOnly &&
                                            (
                                                    buyerLatitude == null ||
                                                            buyerLongitude == null
                                                    ) -> {

                                        "Getting your current location..."
                                    }


                                    showNearMeOnly -> {

                                        "No shops found within 5 km"
                                    }


                                    showFavouritesOnly -> {

                                        "No favourite shops yet"
                                    }


                                    else -> {

                                        "No shops found"
                                    }
                                },

                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium,

                            color =
                                Color.Gray
                        )
                    }
                }


            } else {

                // SHOPS
                items(

                    items =
                        filteredShops,

                    key = { shop ->

                        shop.sellerId
                    }

                ) { shop ->


                    BuyerShopItem(

                        shop =
                            shop,

                        onClick = {

                            onShopClick(
                                shop
                            )
                        }
                    )


                    HorizontalDivider(

                        color =
                            Color.LightGray
                    )
                }
            }
        }
    }
}

// HEADER
@Composable
fun BuyerHeaderSection(

    buyerName: String,

    unreadNotificationCount: Int,

    onNotificationClick: () -> Unit

) {


    Row(

        modifier =
            Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.CenterVertically,

        horizontalArrangement =
            Arrangement.SpaceBetween

    ) {


        Column {


            Text(

                text =
                    "Hey, $buyerName",

                fontSize =
                    21.sp,

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
                    "Discover food, save money, reduce waste.",

                fontSize =
                    12.sp,

                color =
                    Color.Gray
            )
        }

        // NOTIFICATION BELL + BADGE
        Box {


            IconButton(

                onClick =
                    onNotificationClick

            ) {


                Icon(

                    painter =
                        painterResource(
                            R.drawable.notification
                        ),

                    contentDescription =
                        "Notification"
                )
            }


            if (
                unreadNotificationCount > 0
            ) {


                Box(

                    modifier =
                        Modifier
                            .align(
                                Alignment.TopEnd
                            )
                            .size(
                                18.dp
                            )
                            .background(

                                color =
                                    Color.Red,

                                shape =
                                    CircleShape
                            ),

                    contentAlignment =
                        Alignment.Center

                ) {


                    Text(

                        text =

                            if (
                                unreadNotificationCount > 9
                            ) {

                                "9+"

                            } else {

                                unreadNotificationCount
                                    .toString()
                            },

                        color =
                            Color.White,

                        fontSize =
                            9.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}

// SEARCH
@Composable
fun BuyerSearchField(

    searchText: String,

    onSearchTextChange: (String) -> Unit

) {


    OutlinedTextField(

        value =
            searchText,

        onValueChange =
            onSearchTextChange,

        modifier =
            Modifier.fillMaxWidth(),

        placeholder = {


            Text(

                text =
                    "Search...",

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )
        },

        leadingIcon = {


            Icon(

                painter =
                    painterResource(
                        R.drawable.search
                    ),

                contentDescription =
                    "Search"
            )
        },

        singleLine =
            true,

        shape =
            RoundedCornerShape(
                10.dp
            )
    )
}

// QUICK BUTTONS
@Composable
fun BuyerQuickButtons(

    showNearMeOnly: Boolean,

    showFavouritesOnly: Boolean,

    onNearMeClick: () -> Unit,

    onFavouriteClick: () -> Unit

) {


    Row(

        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.spacedBy(
                12.dp
            )

    ) {

        // NEAR ME
        OutlinedButton(

            onClick =
                onNearMeClick,

            modifier =
                Modifier.weight(
                    1f
                ),

            shape =
                RoundedCornerShape(
                    10.dp
                )

        ) {


            Icon(

                painter =
                    painterResource(
                        R.drawable.location_on
                    ),

                contentDescription =
                    "Near Me",

                tint =

                    if (
                        showNearMeOnly
                    ) {

                        MaterialTheme
                            .colorScheme
                            .primary

                    } else {

                        Color.Gray
                    }
            )


            Spacer(

                modifier =
                    Modifier.width(
                        6.dp
                    )
            )


            Text(

                text =
                    "Near Me"
            )
        }

        // FAVOURITE
        OutlinedButton(

            onClick =
                onFavouriteClick,

            modifier =
                Modifier.weight(
                    1f
                ),

            shape =
                RoundedCornerShape(
                    10.dp
                )

        ) {


            Icon(

                painter =
                    painterResource(
                        R.drawable.favourite
                    ),

                contentDescription =
                    "Favourite",

                tint =

                    if (
                        showFavouritesOnly
                    ) {

                        MaterialTheme
                            .colorScheme
                            .primary

                    } else {

                        Color.Gray
                    }
            )


            Spacer(

                modifier =
                    Modifier.width(
                        6.dp
                    )
            )


            Text(

                text =
                    "Favourite"
            )
        }
    }
}

// SHOP ITEM
@Composable
fun BuyerShopItem(

    shop: Shop,

    onClick: () -> Unit

) {


    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {

                    onClick()
                }
                .padding(
                    vertical = 16.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically

    ) {


        Surface(

            modifier =
                Modifier
                    .size(
                        65.dp
                    )
                    .clip(
                        CircleShape
                    ),

            color =
                Color(
                    0xFFFFF4D6
                )

        ) {


            Box(

                contentAlignment =
                    Alignment.Center

            ) {


                Text(

                    text =
                        shop.shortName,

                    fontSize =
                        18.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        Color(
                            0xFFD99B00
                        )
                )
            }
        }


        Spacer(

            modifier =
                Modifier.width(
                    15.dp
                )
        )


        Column {


            Text(

                text =
                    shop.name,

                fontSize =
                    15.sp,

                fontWeight =
                    FontWeight.Medium
            )


            Spacer(

                modifier =
                    Modifier.height(
                        4.dp
                    )
            )


            Text(

                text =
                    shop.address,

                fontSize =
                    12.sp,

                color =
                    Color.Gray
            )
        }
    }
}

// SHORT SHOP NAME
private fun createShortName(

    shopName: String

): String {


    val words =
        shopName
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

            words
                .first()
                .take(2)
                .uppercase()
        }


        else -> {

            "${words[0].first()}${words[1].first()}"
                .uppercase()
        }
    }
}

// CALCULATE DISTANCE
private fun calculateDistanceKm(

    lat1: Double,

    lon1: Double,

    lat2: Double,

    lon2: Double

): Double {


    val results =
        FloatArray(
            1
        )


    Location.distanceBetween(

        lat1,
        lon1,

        lat2,
        lon2,

        results
    )


    return results[0] /
            1000.0
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun BuyerHomeScreenPreview() {


    SharePlateTheme(
        dynamicColor = false
    ) {


        BuyerHomeScreen()
    }
}