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


    const val BUYER_HOME = "buyer_home"

    const val BUYER_NOTIFICATION = "buyer_notification"
    const val BUYER_SHOP_DETAIL = "buyer_shop_detail/{sellerId}"
    const val BUYER_CART = "buyer_cart"
    const val BUYER_CHECKOUT = "buyer_checkout"
    const val BUYER_PAYMENT = "buyer_payment"
    const val BUYER_ORDER_SUCCESS = "buyer_order_success/{pickupCode}/{totalPriceCent}"
    const val BUYER_ACTIVITY = "buyer_activity"
    const val BUYER_QR_CODE = "buyer_qr_code/{orderId}"
    const val BUYER_PROFILE = "buyer_profile"

    fun buyerShopDetailRoute(sellerId: String): String {
        return "buyer_shop_detail/$sellerId"
    }

    fun buyerOrderSuccessRoute(pickupCode: String, totalPriceCent: Int): String {
        return "buyer_order_success/$pickupCode/$totalPriceCent"
    }

    fun buyerQrCodeRoute(orderId: Long): String {
        return "buyer_qr_code/$orderId"
    }


    const val NGO_HOME = "ngo_home"
    const val NGO_CART = "ngo_cart"
    const val NGO_CHECKOUT = "ngo_checkout"
    const val NGO_PAYMENT = "ngo_payment"
    const val NGO_SUCCESS = "ngo_success/{pickupCode}"
    const val NGO_ACTIVITY = "ngo_activity"
    const val NGO_PROFILE = "ngo_profile"
    const val NGO_EDIT_PROFILE = "ngo_edit_profile"

    fun ngoSuccessRoute(pickupCode: String): String {
        return "ngo_success/$pickupCode"
    }
}
