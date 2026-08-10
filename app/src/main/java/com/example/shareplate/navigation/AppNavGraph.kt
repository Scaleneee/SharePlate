package com.example.shareplate.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shareplate.ui.seller.home.SellerHomeScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
){
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            // waiting for the implementation of LoginScreen
            /*
            LoginScreen(
                onSellerLogin = {
                    navController.navigate("seller_home")
                },
                onBuyerLogin = {
                    navController.navigate("buyer_home")
                },
                onNgoLogin = {
                    navController.navigate("ngo_home")
                }
            )
            */
        }

        // seller homepage
        composable("seller_home") {
            SellerHomeScreen(
                onMenuClick = {
                    navController.navigate("seller_menu")
                }
            )
        }
        composable("seller_menu") {
            // waiting for implementation
            /*
            FrequentlyWastedMenu(
                onBack = {
                    navController.popBackStack()
                }
            )
             */

        }
    }
}