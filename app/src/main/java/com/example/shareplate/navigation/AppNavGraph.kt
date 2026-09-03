package com.example.shareplate.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.shareplate.data.FoodItems
import com.example.shareplate.data.local.SessionManager
import com.example.shareplate.data.remote.SupabaseProvider
import com.example.shareplate.ui.NGO.NGOHomeScreen
import com.example.shareplate.ui.auth.login.LoginScreen
import com.example.shareplate.ui.auth.password.NewPasswordScreen
import com.example.shareplate.ui.auth.profile.ProfileScreen
import com.example.shareplate.ui.auth.register.RegisterScreen
import com.example.shareplate.ui.seller.SellerViewModel
import com.example.shareplate.ui.seller.home.SellerHomeScreen
import com.example.shareplate.ui.seller.menu.AddFoodScreen
import com.example.shareplate.ui.seller.menu.EditFoodScreen
import com.example.shareplate.ui.seller.menu.SellerMenuScreen
import io.github.jan.supabase.auth.auth
import com.example.shareplate.ui.buyer.home.BuyerActivityScreen
import com.example.shareplate.ui.buyer.home.BuyerHomeScreen
import com.example.shareplate.ui.buyer.home.BuyerOrderSuccessScreen
import com.example.shareplate.ui.buyer.home.BuyerQrCodeScreen
import com.example.shareplate.ui.buyer.home.ShopDetailScreen
import com.example.shareplate.ui.buyer.order.BuyerCartScreen
import com.example.shareplate.ui.buyer.order.BuyerCheckoutScreen
import com.example.shareplate.ui.buyer.order.BuyerPaymentScreen
import com.example.shareplate.ui.buyer.profile.BuyerProfileScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
) {
    val sellerViewModel: SellerViewModel = viewModel()

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val startDestination = remember {
        if (sessionManager.isLoggedIn()) {
            homeRouteFor(sessionManager.getRole())
        } else {
            AppRoutes.LOGIN
        }
    }

    NavHost(
        navController = navController, startDestination = startDestination
    ) {
        composable(AppRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { role ->
                    sessionManager.saveSession(role)
                    navController.navigate(homeRouteFor(role)) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(AppRoutes.REGISTER)
                }
            )
        }

        composable(AppRoutes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = { role ->
                    sessionManager.saveSession(role)
                    navController.navigate(homeRouteFor(role)) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppRoutes.PROFILE) {
            ProfileScreen(
                onLoggedOut = {
                    sessionManager.clearSession()
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(AppRoutes.SELLER_PROFILE) {
            ProfileScreen(
                onLoggedOut = {
                    sessionManager.clearSession()
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(AppRoutes.SELLER_ACTIVITY) {
            PlaceholderScreen("Seller activity is under development")
        }

        composable(AppRoutes.NEW_PASSWORD) {
            NewPasswordScreen(
                onDone = {
                    sessionManager.clearSession()
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(AppRoutes.NGO_HOME) {
            NGOHomeScreen(
                onProfileClick = {
                    navController.navigate(AppRoutes.PROFILE)
                }
            )
        }

        // seller home screen
        composable(AppRoutes.SELLER_HOME) {
            SellerHomeScreen(onHomeClick = {
                // already on home
            }, onMenuClick = {
                navController.navigate(AppRoutes.SELLER_MENU)
            }, onActivityClick = {
                navController.navigate(AppRoutes.SELLER_ACTIVITY)
            }, onProfileClick = {
                navController.navigate(AppRoutes.SELLER_PROFILE)
            })
        }
        // seller menu screen
        composable(AppRoutes.SELLER_MENU) {
            // get the food items of the seller
            val foodItems by sellerViewModel.foodItems.collectAsStateWithLifecycle()
            // get the seller id
            val sellerId = SupabaseProvider.client.auth.currentUserOrNull()?.id

            LaunchedEffect(sellerId) {
                if (sellerId != null) {
                    sellerViewModel.loadFoodItems(sellerId)
                }
            }

            SellerMenuScreen(
                foodItems = FoodItems.foodItems,
                onHomeClick = {
                    navController.navigate(AppRoutes.SELLER_HOME)
                }, onMenuClick = {
                    // already on menu
                }, onActivityClick = {
                    navController.navigate(AppRoutes.SELLER_ACTIVITY)
                }, onProfileClick = {
                    navController.navigate(AppRoutes.SELLER_PROFILE)
                }, onAddFoodClick = {
                    navController.navigate(AppRoutes.SELLER_ADD_FOOD)
                }, onEditFoodClick = { foodItemId ->
                    navController.navigate("seller/edit-food/$foodItemId")
                })
        }
        // seller add food screen
        composable(AppRoutes.SELLER_ADD_FOOD) {
            val context = LocalContext.current
            val sellerId = SupabaseProvider.client.auth.currentUserOrNull()?.id

            AddFoodScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveClick = { foodName, category, originalPrice, bestBeforeDays, imageUri, isActive ->
                    // ViewModel saves food
                    if (sellerId != null) {
                        val originalPriceCent = (originalPrice.toDouble() * 100).toInt()

                        sellerViewModel.addFood(
                            context = context,
                            sellerId = sellerId,
                            foodName = foodName,
                            category = category,
                            originalPriceCent =
                                originalPriceCent,
                            bestBeforeDays =
                                bestBeforeDays.toInt(),
                            selectedImageUri =
                                imageUri,
                            isActive = isActive,

                            onSuccess = {
                                navController.popBackStack()
                            }
                        )
                    }
                })
        }
        // seller edit food screen
        composable(
            route = AppRoutes.SELLER_EDIT_FOOD,
            arguments = listOf(
                navArgument("foodItemId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            // get the food item id from the route
            val foodItemId = backStackEntry.arguments?.getLong("foodItemId") ?: -1

            // get the food obj using the id
            val foodItem = FoodItems.getFoodItemById(foodItemId)
            FoodItems.getFoodItemById(foodItemId)?.let { foodItem ->
                // call the screen
                EditFoodScreen(
                    foodItem = foodItem,
                    onBackClick = { navController.popBackStack() },
                    onSaveClick = { foodName, category, originalPrice, bestBeforeDays, imageURI, isActive ->
                        // ViewModel saves food
                        navController.popBackStack()
                    }
                )
            }
        }

        // BUYER HOME
        composable(AppRoutes.BUYER_HOME) {

            BuyerHomeScreen(

                onShopClick = { shop ->

                    navController.navigate(
                        AppRoutes.buyerShopDetailRoute(
                            shop.sellerId
                        )
                    )
                },

                onHomeClick = {
                    // Already on home
                },

                onOrderClick = {

                    navController.navigate(
                        AppRoutes.BUYER_CART
                    ) {
                        launchSingleTop = true
                    }
                },

                onActivityClick = {

                    navController.navigate(
                        AppRoutes.BUYER_ACTIVITY
                    ) {
                        launchSingleTop = true
                    }
                },

                onProfileClick = {

                    navController.navigate(
                        AppRoutes.BUYER_PROFILE
                    ) {
                        launchSingleTop = true
                    }
                }
            )
        }

        //BUYER SHOP DETAIL
        composable(

            route =
                AppRoutes.BUYER_SHOP_DETAIL,

            arguments =
                listOf(

                    navArgument("sellerId") {

                        type =
                            NavType.StringType
                    }
                )

        ) { backStackEntry ->


            val sellerId =
                backStackEntry
                    .arguments
                    ?.getString(
                        "sellerId"
                    )
                    ?: return@composable


            ShopDetailScreen(

                sellerId =
                    sellerId,

                onBackClick = {

                    navController
                        .popBackStack()
                },

                onHomeClick = {

                    navController.navigate(
                        AppRoutes.BUYER_HOME
                    ) {
                        launchSingleTop = true
                    }
                },

                onOrderClick = {

                    navController.navigate(
                        AppRoutes.BUYER_CART
                    ) {
                        launchSingleTop = true
                    }
                },

                onActivityClick = {

                    navController.navigate(
                        AppRoutes.BUYER_ACTIVITY
                    ) {
                        launchSingleTop = true
                    }
                },

                onProfileClick = {

                    navController.navigate(
                        AppRoutes.BUYER_PROFILE
                    ) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // BUYER CART
        composable(
            AppRoutes.BUYER_CART
        ) {

            BuyerCartScreen(

                onBackClick = {
                    navController.popBackStack()
                },

                onCheckoutClick = {

                    navController.navigate(
                        AppRoutes.BUYER_CHECKOUT
                    )
                },

                onHomeClick = {

                    navController.navigate(
                        AppRoutes.BUYER_HOME
                    ) {
                        launchSingleTop = true
                    }
                },

                onOrderClick = {
                    // Already on cart
                },

                onActivityClick = {

                    navController.navigate(
                        AppRoutes.BUYER_ACTIVITY
                    ) {
                        launchSingleTop = true
                    }
                },

                onProfileClick = {

                    navController.navigate(
                        AppRoutes.BUYER_PROFILE
                    ) {
                        launchSingleTop = true
                    }
                }
            )
        }


        // BUYER CHECKOUT
        composable(
            AppRoutes.BUYER_CHECKOUT
        ) {

            BuyerCheckoutScreen(

                onBackClick = {

                    navController.popBackStack()
                },

                onContinuePaymentClick = {

                    navController.navigate(
                        AppRoutes.BUYER_PAYMENT
                    )
                }
            )
        }


        // BUYER PAYMENT
        composable(
            AppRoutes.BUYER_PAYMENT
        ) {

            BuyerPaymentScreen(

                onBackClick = {

                    navController
                        .popBackStack()
                },

                onPaymentSuccess = {
                        pickupCode,
                        totalPriceCent ->


                    navController.navigate(

                        AppRoutes
                            .buyerOrderSuccessRoute(

                                pickupCode =
                                    pickupCode,

                                totalPriceCent =
                                    totalPriceCent
                            )

                    ) {

                        popUpTo(
                            AppRoutes.BUYER_CART
                        ) {
                            inclusive = true
                        }
                    }
                }
            )
        }

    // BUYER ORDER SUCCESS
        composable(

            route =
                AppRoutes.BUYER_ORDER_SUCCESS,

            arguments =
                listOf(

                    navArgument(
                        "pickupCode"
                    ) {

                        type =
                            NavType.StringType
                    },

                    navArgument(
                        "totalPriceCent"
                    ) {

                        type =
                            NavType.IntType
                    }
                )

        ) { backStackEntry ->


            val pickupCode =
                backStackEntry
                    .arguments
                    ?.getString(
                        "pickupCode"
                    )
                    ?: ""


            val totalPriceCent =
                backStackEntry
                    .arguments
                    ?.getInt(
                        "totalPriceCent"
                    )
                    ?: 0


            BuyerOrderSuccessScreen(

                pickupCode =
                    pickupCode,

                totalPriceCent =
                    totalPriceCent,

                onViewOrderClick = {

                    navController.navigate(
                        AppRoutes.BUYER_ACTIVITY
                    )
                },

                onHomeClick = {

                    navController.navigate(
                        AppRoutes.BUYER_HOME
                    ) {

                        popUpTo(
                            AppRoutes.BUYER_HOME
                        ) {
                            inclusive = false
                        }

                        launchSingleTop =
                            true
                    }
                }
            )
        }

    // BUYER ACTIVITY
        composable(
            AppRoutes.BUYER_ACTIVITY
        ) {

            BuyerActivityScreen(

                onHomeClick = {

                    navController.navigate(
                        AppRoutes.BUYER_HOME
                    ) {
                        launchSingleTop = true
                    }
                },

                onOrderClick = {

                    navController.navigate(
                        AppRoutes.BUYER_CART
                    ) {
                        launchSingleTop = true
                    }
                },

                onActivityClick = {
                    // Already on activity
                },

                onProfileClick = {

                    navController.navigate(
                        AppRoutes.BUYER_PROFILE
                    ) {
                        launchSingleTop = true
                    }
                },

                onQrCodeClick = { orderId ->

                    navController.navigate(
                        AppRoutes.buyerQrCodeRoute(
                            orderId
                        )
                    )
                }
            )
        }


        // BUYER QR CODE
        composable(

            route =
                AppRoutes.BUYER_QR_CODE,

            arguments =
                listOf(

                    navArgument(
                        "orderId"
                    ) {

                        type =
                            NavType.LongType
                    }
                )

        ) { backStackEntry ->


            val orderId =
                backStackEntry
                    .arguments
                    ?.getLong(
                        "orderId"
                    )
                    ?: return@composable


            BuyerQrCodeScreen(

                orderId =
                    orderId,

                onBackClick = {

                    navController
                        .popBackStack()
                }
            )
        }

    // BUYER PROFILE

        composable(
            AppRoutes.BUYER_PROFILE
        ) {

            BuyerProfileScreen(

                onHomeClick = {

                    navController.navigate(
                        AppRoutes.BUYER_HOME
                    ) {
                        launchSingleTop = true
                    }
                },

                onOrderClick = {

                    navController.navigate(
                        AppRoutes.BUYER_CART
                    ) {
                        launchSingleTop = true
                    }
                },

                onActivityClick = {

                    navController.navigate(
                        AppRoutes.BUYER_ACTIVITY
                    ) {
                        launchSingleTop = true
                    }
                },

                onProfileClick = {
                    // Already on profile
                },

                onLoggedOut = {

                    navController.navigate(
                        AppRoutes.LOGIN
                    ) {

                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}


@Composable
private fun PlaceholderScreen(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text)
    }
}

private fun homeRouteFor(role: String?): String = when (role) {
    "SELLER" -> AppRoutes.SELLER_HOME
    "BUYER" -> AppRoutes.BUYER_HOME
    "NGO" -> AppRoutes.NGO_HOME
    else -> AppRoutes.LOGIN
}
