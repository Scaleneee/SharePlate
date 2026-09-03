package com.example.shareplate.navigation

object AppRoutes {

    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PROFILE = "profile"
    const val NEW_PASSWORD = "new_password"

    const val SELLER_HOME = "seller/home"
    const val SELLER_MENU = "seller/menu"
    const val SELLER_ACTIVITY = "seller/activity"
    const val SELLER_PROFILE = "seller/profile"
    const val SELLER_ADD_FOOD = "seller/add-food"
    const val SELLER_EDIT_FOOD = "seller/edit-food/{foodItemId}"


    const val BUYER_SHOP_DETAIL = "buyer_shop_detail/{sellerId}"
    const val BUYER_CART = "buyer_cart"
    const val BUYER_HOME = "buyer/home"
    const val BUYER_ACTIVITY = "buyer_activity"
    const val BUYER_PROFILE = "buyer_profile"

    fun buyerShopDetailRoute(
        sellerId: String
    ): String {

        return "buyer_shop_detail/$sellerId"
    }

    const val NGO_HOME = "ngo/home"
}
