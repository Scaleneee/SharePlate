package com.example.shareplate.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.assignment.ngo.NGOHomeScreen
import com.example.shareplate.data.FoodItems
import com.example.shareplate.data.local.SessionManager
import com.example.shareplate.data.model.FoodItem
import com.example.shareplate.ui.auth.login.LoginScreen
import com.example.shareplate.ui.auth.password.NewPasswordScreen
import com.example.shareplate.ui.auth.profile.ProfileScreen
import com.example.shareplate.ui.auth.register.RegisterScreen
import com.example.shareplate.ui.buyer.home.BuyerActivityScreen
import com.example.shareplate.ui.buyer.home.BuyerHomeScreen
import com.example.shareplate.ui.seller.home.SellerHomeScreen
import com.example.shareplate.ui.seller.menu.AddFoodScreen
import com.example.shareplate.ui.seller.menu.EditFoodScreen
import com.example.shareplate.ui.seller.menu.SellerMenuScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
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
        navController = navController,
        startDestination = startDestination
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

        // ---------------- Seller ----------------
        composable(AppRoutes.SELLER_HOME) {
            SellerHomeScreen(
                sellerName = "Brian Chew",
                onHomeClick = {},
                onMenuClick = {
                    navController.navigate(AppRoutes.SELLER_MENU)
                },
                onActivityClick = {
                    navController.navigate(AppRoutes.SELLER_ACTIVITY)
                },
                onProfileClick = {
                    navController.navigate(AppRoutes.SELLER_PROFILE)
                },
                onNotificationClick = {}
            )
        }

        composable(AppRoutes.SELLER_MENU) {
            SellerMenuScreen(
                onHomeClick = {
                    navController.navigate(AppRoutes.SELLER_HOME)
                },
                onMenuClick = {},
                onActivityClick = {
                    navController.navigate(AppRoutes.SELLER_ACTIVITY)
                },
                onProfileClick = {
                    navController.navigate(AppRoutes.SELLER_PROFILE)
                },
                foodItems = FoodItems.foodItems,
                onAddFoodClick = {
                    navController.navigate(AppRoutes.SELLER_ADD_FOOD)
                },
                onEditFoodClick = { foodItemId ->
                    navController.navigate("seller/edit-food/$foodItemId")
                }
            )
        }

        composable(AppRoutes.SELLER_ACTIVITY) {
            PlaceholderScreen("Seller activity is under development")
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

        composable(AppRoutes.SELLER_ADD_FOOD) {
            AddFoodScreen(
                onBackClick = { navController.popBackStack() },
                onSaveClick = { _, _, _, _, _, _ ->
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = AppRoutes.SELLER_EDIT_FOOD,
            arguments = listOf(
                navArgument("foodItemId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val foodItemId = backStackEntry.arguments?.getLong("foodItemId") ?: -1L
            val foodItem: FoodItem? = FoodItems.getFoodItemById(foodItemId)
            if (foodItem != null) {
                EditFoodScreen(
                    foodItem = foodItem,
                    onBackClick = { navController.popBackStack() },
                    onSaveClick = { _, _, _, _, _, _ ->
                        navController.popBackStack()
                    }
                )
            }
        }

        // ---------------- Buyer ----------------
        composable(AppRoutes.BUYER_HOME) {
            BuyerHomeScreen(
                onHomeClick = {},
                onOrderClick = {},
                onActivityClick = {
                    navController.navigate(AppRoutes.BUYER_ACTIVITY)
                },
                onProfileClick = {
                    navController.navigate(AppRoutes.PROFILE)
                }
            )
        }

        composable(AppRoutes.BUYER_ACTIVITY) {
            BuyerActivityScreen(
                onHomeClick = {
                    navController.navigate(AppRoutes.BUYER_HOME)
                },
                onOrderClick = {},
                onActivityClick = {},
                onProfileClick = {
                    navController.navigate(AppRoutes.PROFILE)
                },
                onQrCodeClick = {}
            )
        }

        // ---------------- NGO ----------------
        composable(AppRoutes.NGO_HOME) {
            NGOHomeScreen(
                onProfileClick = {
                    navController.navigate(AppRoutes.PROFILE)
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
