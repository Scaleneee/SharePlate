package com.example.shareplate.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.shareplate.data.FoodItems
import com.example.shareplate.data.remote.SupabaseProvider
import com.example.shareplate.ui.seller.SellerViewModel
import com.example.shareplate.ui.seller.home.SellerHomeScreen
import com.example.shareplate.ui.seller.menu.AddFoodScreen
import com.example.shareplate.ui.seller.menu.EditFoodScreen
import com.example.shareplate.ui.seller.menu.SellerMenuScreen
import io.github.jan.supabase.auth.auth

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
) {
    val sellerViewModel: SellerViewModel = viewModel()

    NavHost(
        navController = navController, startDestination = AppRoutes.SELLER_HOME
    ) {
        composable("login") {
            // waiting for the implementation of LoginScreen
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
            val foodItems by sellerViewModel.foodItems.collectAsStateWithLifecycle()

            val sellerId =
                SupabaseProvider.client.auth
                    .currentUserOrNull()
                    ?.id

            LaunchedEffect(sellerId) {
                if (sellerId != null) {
                    sellerViewModel.loadFoodItems(sellerId)
                }
            }

            SellerMenuScreen(foodItems = FoodItems.foodItems, onHomeClick = {
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
            AddFoodScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveClick = { foodName, category, originalPrice, bestBeforeDays, imageURI, isActive ->
                    // ViewModel saves food
                    navController.popBackStack()
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
    }
}
