package com.example.shareplate.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.shareplate.data.local.NgoLocalStore
import com.example.shareplate.data.local.SessionManager
import com.example.shareplate.data.remote.SupabaseProvider
import com.example.shareplate.ui.NGO.NGOActivityScreen
import com.example.shareplate.ui.NGO.NGOHomeScreen
import com.example.shareplate.ui.NGO.order.NGOCartStore
import com.example.shareplate.ui.NGO.NGOCartScreen
import com.example.shareplate.ui.NGO.NGOCheckoutScreen
import com.example.shareplate.ui.NGO.NGOPaymentScreen
import com.example.shareplate.ui.NGO.NGOSuccessScreen
import com.example.shareplate.ui.NGO.NGOProfileScreen
import com.example.shareplate.ui.NGO.NGOViewModel
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
import com.example.shareplate.ui.buyer.home.ShopDetailScreen
import com.example.shareplate.ui.buyer.order.BuyerCartScreen
import com.example.shareplate.ui.buyer.order.BuyerCheckoutScreen
import com.example.shareplate.ui.buyer.order.BuyerPaymentScreen
import com.example.shareplate.ui.buyer.order.BuyerQrCodeScreen
import com.example.shareplate.ui.buyer.profile.BuyerProfileScreen
import io.github.jan.supabase.auth.status.SessionStatus
import com.example.shareplate.ui.buyer.BuyerViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
) {
    val context = LocalContext.current

    val sessionManager = remember {
        SessionManager(context)
    }

    // Supabase authentication state
    val sessionStatus by SupabaseProvider.client.auth.sessionStatus.collectAsStateWithLifecycle()

    // Wait for Supabase to restore saved session
    if (sessionStatus == SessionStatus.Initializing) {

        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        return
    }

    // Create SellerViewModel AFTER Supabase finished restoring session
    val sellerViewModel: SellerViewModel = viewModel()
    //BuyerViewModel
    val buyerViewModel: BuyerViewModel = viewModel()

    // Decide startup screen using Supabase Auth
    val startDestination = when (sessionStatus) {

        is SessionStatus.Authenticated -> {
            homeRouteFor(
                sessionManager.getRole()
            )
        }

        else -> {
            AppRoutes.LOGIN
        }
    }

    val errorMessage by sellerViewModel.errorMessage.collectAsStateWithLifecycle()

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->

            Toast.makeText(
                context, message, Toast.LENGTH_LONG
            ).show()

            sellerViewModel.clearError()
        }
    }

    NavHost(
        navController = navController, startDestination = startDestination
    ) {
        composable(AppRoutes.LOGIN) {
            LoginScreen(onLoginSuccess = { role ->
                sessionManager.saveSession(role)
                navController.navigate(homeRouteFor(role)) {
                    popUpTo(AppRoutes.LOGIN) { inclusive = true }
                }
            }, onNavigateToRegister = {
                navController.navigate(AppRoutes.REGISTER)
            })
        }

        composable(AppRoutes.REGISTER) {
            RegisterScreen(onRegisterSuccess = { role ->
                sessionManager.saveSession(role)
                navController.navigate(homeRouteFor(role)) {
                    popUpTo(AppRoutes.LOGIN) { inclusive = true }
                }
            }, onNavigateToLogin = {
                navController.popBackStack()
            })
        }

        composable(AppRoutes.PROFILE) {
            ProfileScreen(
                onLoggedOut = {
                    sessionManager.clearSession()
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(0)
                    }
                })
        }

        composable(AppRoutes.SELLER_PROFILE) {
            ProfileScreen(
                onLoggedOut = {
                    sessionManager.clearSession()
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(0)
                    }
                })
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
                })
        }

        // NGO home screen
        composable(AppRoutes.NGO_HOME) {
            NGOHomeScreen(onAcceptDonation = { donation, items ->
                NGOCartStore.donation = donation
                NGOCartStore.cartItems.clear()
                NGOCartStore.cartItems.addAll(items)
                navController.navigate(AppRoutes.NGO_CART)
            }, onActivityClick = {
                navController.navigate(AppRoutes.NGO_ACTIVITY) {
                    launchSingleTop = true
                }
            }, onProfileClick = {
                navController.navigate(AppRoutes.NGO_PROFILE)
            })
        }

        // NGO activity screen
        composable(AppRoutes.NGO_ACTIVITY) {
            val ngoViewModel: NGOViewModel = viewModel()
            NGOActivityScreen(ngoViewModel = ngoViewModel, onHomeClick = {
                navController.navigate(AppRoutes.NGO_HOME) {
                    launchSingleTop = true
                }
            }, onMenuClick = {
                navController.navigate(AppRoutes.NGO_HOME) {
                    launchSingleTop = true
                }
            }, onActivityClick = {
                // already on activity
            }, onProfileClick = {
                navController.navigate(AppRoutes.NGO_PROFILE) {
                    launchSingleTop = true
                }
            })
        }

        // NGO cart screen
        composable(AppRoutes.NGO_CART) {
            NGOCartScreen(
                onBackClick = { navController.popBackStack() },
                onCheckoutClick = { navController.navigate(AppRoutes.NGO_CHECKOUT) })
        }

        // NGO checkout screen
        composable(AppRoutes.NGO_CHECKOUT) {
            NGOCheckoutScreen(
                donation = NGOCartStore.donation ?: return@composable,
                items = NGOCartStore.cartItems,
                onBackClick = { navController.popBackStack() },
                onProceedClick = { navController.navigate(AppRoutes.NGO_PAYMENT) })
        }

        // NGO payment screen
        composable(AppRoutes.NGO_PAYMENT) {
            val ngoViewModel: NGOViewModel = viewModel()
            NGOPaymentScreen(
                donation = NGOCartStore.donation ?: return@composable,
                items = NGOCartStore.cartItems,
                onBackClick = { navController.popBackStack() },
                onCompleteClick = {
                    val donation = NGOCartStore.donation
                    val orderItems = NGOCartStore.cartItems.toList()
                    ngoViewModel.submitOrder { _, pickupCode, _ ->
                        val code = pickupCode.ifBlank { "ND${(1000..9999).random()}" }
                        if (donation != null) {
                            val itemsText =
                                orderItems.joinToString(", ") { "${it.foodName} - ${it.quantity}" }
                            NgoLocalStore(context).addOrder(
                                "${donation.name}|${donation.location}|${
                                    donation.name.take(2).uppercase()
                                }|Pickup today|${itemsText}|$code|${System.currentTimeMillis()}|false"
                            )
                        }
                        navController.navigate(AppRoutes.ngoSuccessRoute(code)) {
                            popUpTo(AppRoutes.NGO_CART) { inclusive = true }
                        }
                    }
                })
        }

        // NGO successful screen
        composable(
            route = AppRoutes.NGO_SUCCESS, arguments = listOf(
            navArgument("pickupCode") {
                type = NavType.StringType
            })) { backStackEntry ->
            val pickupCode = backStackEntry.arguments?.getString("pickupCode") ?: ""
            NGOSuccessScreen(
                pickupCode = pickupCode, onBackClick = {
                    navController.navigate(AppRoutes.NGO_HOME) {
                        popUpTo(AppRoutes.NGO_HOME)
                    }
                })
        }

        // NGO profile screen
        composable(AppRoutes.NGO_PROFILE) {
            NGOProfileScreen(onHomeClick = {
                navController.navigate(AppRoutes.NGO_HOME) {
                    launchSingleTop = true
                }
            }, onMenuClick = {
                navController.navigate(AppRoutes.NGO_HOME) {
                    launchSingleTop = true
                }
            }, onActivityClick = {
                navController.navigate(AppRoutes.NGO_ACTIVITY) {
                    launchSingleTop = true
                }
            }, onProfileClick = {
                // already on profile
            }, onLogout = {
                navController.navigate(AppRoutes.LOGIN) {
                    popUpTo(0) { inclusive = true }
                }
            })
        }
        // seller home screen
        composable(AppRoutes.SELLER_HOME) {

            val sellerName by sellerViewModel.sellerName.collectAsStateWithLifecycle()

            val foodItems by sellerViewModel.foodItems.collectAsStateWithLifecycle()

            val sellerId = SupabaseProvider.client.auth.currentUserOrNull()?.id

            LaunchedEffect(sellerId) {
                if (sellerId != null) {
                    // load seller name
                    sellerViewModel.fetchSellerName()
                    sellerViewModel.loadFoodItems(sellerId)
                }
            }

            SellerHomeScreen(
                sellerName = sellerName, foodItems = foodItems,

                onHomeClick = {
                    // already home
                },

                onMenuClick = {
                    navController.navigate(
                        AppRoutes.SELLER_MENU
                    )
                },

                onActivityClick = {
                    navController.navigate(
                        AppRoutes.SELLER_ACTIVITY
                    )
                },

                onProfileClick = {
                    navController.navigate(
                        AppRoutes.SELLER_PROFILE
                    )
                })
        }
        // seller menu screen
        composable(AppRoutes.SELLER_MENU) {
            // get the food items of the seller
            val foodItems by sellerViewModel.foodItems.collectAsStateWithLifecycle()
            // get the seller id
            val sellerId = SupabaseProvider.client.auth.currentUserOrNull()?.id
            val errorMessage by sellerViewModel.errorMessage.collectAsStateWithLifecycle()

            LaunchedEffect(errorMessage) {
                errorMessage?.let { message ->

                    Toast.makeText(
                        context, message, Toast.LENGTH_LONG
                    ).show()

                    sellerViewModel.clearError()
                }
            }

            SellerMenuScreen(foodItems = foodItems, onHomeClick = {
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
            }, onDeleteClick = { foodItemId ->

                val foodOwnerId = foodItems.find {
                    it.foodItemId == foodItemId
                }?.sellerId

                if (sellerId != null) {
                    sellerViewModel.deleteFood(
                        foodItemId = foodItemId, sellerId = sellerId
                    )
                }
            })
        }

        // seller add food screen
        composable(AppRoutes.SELLER_ADD_FOOD) {

            val context = LocalContext.current

            val sellerId = SupabaseProvider.client.auth.currentUserOrNull()?.id

            // Observe error from SellerViewModel
            val errorMessage by sellerViewModel.errorMessage.collectAsStateWithLifecycle()

            // Show error when ViewModel reports one
            LaunchedEffect(errorMessage) {
                errorMessage?.let { message ->

                    Toast.makeText(
                        context, message, Toast.LENGTH_LONG
                    ).show()

                    sellerViewModel.clearError()
                }
            }

            AddFoodScreen(
                onBackClick = {
                    navController.popBackStack()
                },

                onSaveClick = { foodName, category, originalPrice, bestBeforeDays, imageUri, isActive ->

                    if (sellerId != null) {

                        val originalPriceCent = (originalPrice.toDouble() * 100).toInt()

                        sellerViewModel.addFood(
                            context = context,
                            sellerId = sellerId,
                            foodName = foodName,
                            category = category,
                            originalPriceCent = originalPriceCent,
                            bestBeforeDays = bestBeforeDays.toInt(),
                            selectedImageUri = imageUri,
                            isActive = isActive,

                            onSuccess = {

                                Toast.makeText(
                                    context, "Food added successfully", Toast.LENGTH_SHORT
                                ).show()

                                navController.popBackStack()
                            })

                    } else {

                        Toast.makeText(
                            context, "Error: Supabase user session is null", Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }
        // seller edit food screen
        composable(
            route = AppRoutes.SELLER_EDIT_FOOD, arguments = listOf(
                navArgument("foodItemId") {
                    type = NavType.LongType
                })
        ) { backStackEntry ->

            val context = LocalContext.current

            // get food item id from route
            val foodItemId = backStackEntry.arguments?.getLong("foodItemId") ?: return@composable

            // get food items from ViewModel
            val foodItems by sellerViewModel.foodItems.collectAsStateWithLifecycle()

            // find the selected food
            val foodItem = foodItems.find {
                it.foodItemId == foodItemId
            }

            if (foodItem != null) {

                EditFoodScreen(
                    foodItem = foodItem,

                    onBackClick = {
                        navController.popBackStack()
                    },

                    onSaveClick = { foodName, category, originalPrice, bestBeforeDays, imageUri, isActive ->

                        // convert RM to cent
                        val originalPriceCent = (originalPrice.toDouble() * 100).toInt()

                        // copy old food item with new values
                        val updatedFoodItem = foodItem.copy(
                            foodName = foodName,
                            category = category,
                            originalPriceCent = originalPriceCent,
                            bestBeforeDays = bestBeforeDays.toInt(),
                        )

                        // update Supabase
                        sellerViewModel.updateFood(
                            context = context,
                            foodItem = updatedFoodItem,
                            selectedImageUri = imageUri,

                            onSuccess = {
                                navController.popBackStack()
                            })
                    })
            }
        }

        // BUYER HOME
        composable(AppRoutes.BUYER_HOME) {

            BuyerHomeScreen(

                buyerViewModel = buyerViewModel,

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
                })
        }

        //BUYER SHOP DETAIL
        composable(

            route = AppRoutes.BUYER_SHOP_DETAIL,

            arguments = listOf(

                navArgument("sellerId") {

                    type = NavType.StringType
                })

        ) { backStackEntry ->


            val sellerId = backStackEntry.arguments?.getString(
                    "sellerId"
                ) ?: return@composable


            ShopDetailScreen(

                sellerId = sellerId,
                buyerViewModel = buyerViewModel,

                onBackClick = {

                    navController.popBackStack()
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
                })
        }

        // BUYER CART
        composable(
            AppRoutes.BUYER_CART
        ) {

            BuyerCartScreen(

                buyerViewModel = buyerViewModel,

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
                })
        }


        // BUYER CHECKOUT
        composable(
            AppRoutes.BUYER_CHECKOUT
        ) {

            BuyerCheckoutScreen(

                buyerViewModel = buyerViewModel,

                onBackClick = {

                    navController.popBackStack()
                },

                onContinuePaymentClick = {

                    navController.navigate(
                        AppRoutes.BUYER_PAYMENT
                    )
                })
        }


        // BUYER PAYMENT
        composable(
            AppRoutes.BUYER_PAYMENT
        ) {

            BuyerPaymentScreen(

                buyerViewModel = buyerViewModel,

                onBackClick = {

                    navController.popBackStack()
                },

                onPaymentSuccess = { pickupCode, totalPriceCent ->


                    navController.navigate(

                        AppRoutes.buyerOrderSuccessRoute(

                                pickupCode = pickupCode,

                                totalPriceCent = totalPriceCent
                            )

                    ) {

                        popUpTo(
                            AppRoutes.BUYER_CART
                        ) {
                            inclusive = true
                        }
                    }
                })
        }

        // BUYER ORDER SUCCESS
        composable(

            route = AppRoutes.BUYER_ORDER_SUCCESS,

            arguments = listOf(

                navArgument(
                    "pickupCode"
                ) {

                    type = NavType.StringType
                },

                navArgument(
                    "totalPriceCent"
                ) {

                    type = NavType.IntType
                })

        ) { backStackEntry ->


            val pickupCode = backStackEntry.arguments?.getString(
                    "pickupCode"
                ) ?: ""


            val totalPriceCent = backStackEntry.arguments?.getInt(
                    "totalPriceCent"
                ) ?: 0


            BuyerOrderSuccessScreen(

                pickupCode = pickupCode,

                totalPriceCent = totalPriceCent,

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

                        launchSingleTop = true
                    }
                })
        }

        // BUYER ACTIVITY
        composable(
            AppRoutes.BUYER_ACTIVITY
        ) {

            BuyerActivityScreen(

                buyerViewModel = buyerViewModel,

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
                })
        }


        // BUYER QR CODE
        composable(

            route = AppRoutes.BUYER_QR_CODE,

            arguments = listOf(

                navArgument(
                    "orderId"
                ) {

                    type = NavType.LongType
                })

        ) { backStackEntry ->


            val orderId = backStackEntry.arguments?.getLong(
                    "orderId"
                ) ?: return@composable


            BuyerQrCodeScreen(

                buyerViewModel = buyerViewModel,

                orderId = orderId,

                onBackClick = {

                    navController.popBackStack()
                })
        }

        // BUYER PROFILE

        composable(
            AppRoutes.BUYER_PROFILE
        ) {

            BuyerProfileScreen(

                buyerViewModel = buyerViewModel,

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
                })
        }
    }
}


@Composable
private fun PlaceholderScreen(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
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
