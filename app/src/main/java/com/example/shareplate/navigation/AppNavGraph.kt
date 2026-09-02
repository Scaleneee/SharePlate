package com.example.shareplate.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.shareplate.ui.seller.home.SellerHomeScreen
import com.example.shareplate.ui.seller.home.foodItems
import com.example.shareplate.ui.seller.menu.AddFoodScreen
import com.example.shareplate.ui.seller.menu.SellerMenuScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
){
    NavHost(
        navController = navController,
        startDestination = AppRoutes.SELLER_HOME
    ) {
        composable("login") {
            // waiting for the implementation of LoginScreen
        }

        // seller home screen
        composable(AppRoutes.SELLER_HOME) {
            SellerHomeScreen(
                onHomeClick = {
                    // already on home
                },
                onMenuClick = {
                    navController.navigate(AppRoutes.SELLER_MENU)
                },
                onActivityClick = {
                    navController.navigate(AppRoutes.SELLER_ACTIVITY)
                },
                onProfileClick = {
                    navController.navigate(AppRoutes.SELLER_PROFILE)
                }
            )
        }
        // seller menu screen
        composable(AppRoutes.SELLER_MENU) {
            SellerMenuScreen(
                foodItems = foodItems,
                onHomeClick = {
                    navController.navigate(AppRoutes.SELLER_HOME)
                },
                onMenuClick = {
                    // already on menu
                },
                onActivityClick = {
                    navController.navigate(AppRoutes.SELLER_ACTIVITY)
                },
                onProfileClick = {
                    navController.navigate(AppRoutes.SELLER_PROFILE)
                },
                onAddFoodClick = {
                    navController.navigate(AppRoutes.SELLER_ADD_FOOD)
                },
                onEditFoodClick = { foodItemId ->
                    navController.navigate("seller/edit-food/$foodItemId")
                }
            )
        }
        // seller add food screen
        composable(AppRoutes.SELLER_ADD_FOOD) {
            AddFoodScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveClick = { foodName, category, originalPrice, bestBeforeDays, isActive ->
                    // ViewModel saves food
                    navController.popBackStack()
                }
            )
        }
    }
}
