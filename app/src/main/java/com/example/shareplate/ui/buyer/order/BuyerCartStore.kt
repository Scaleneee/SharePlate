package com.example.shareplate.ui.buyer.order

import androidx.compose.runtime.mutableStateListOf
import com.example.shareplate.ui.buyer.home.FoodDeal


data class BuyerCartItem(

    val listingId: Long,

    val foodItemId: Long,

    val sellerId: String,

    val shopName: String,

    val foodName: String,

    val price: Double,

    val pickupTime: String,

    val availableQuantity: Int,

    val quantity: Int = 1
)


object BuyerCartStore {

    val cartItems =
        mutableStateListOf<BuyerCartItem>()


    fun addItem(
        shopName: String,
        foodDeal: FoodDeal
    ): Boolean {

        if (foodDeal.availableQuantity <= 0) {
            return false
        }


        val priceValue =
            foodDeal.price
                .replace(
                    "RM",
                    "",
                    ignoreCase = true
                )
                .trim()
                .toDoubleOrNull()
                ?: 0.0


        val existingIndex =
            cartItems.indexOfFirst { item ->

                item.listingId ==
                        foodDeal.listingId
            }


        if (existingIndex >= 0) {

            val existingItem =
                cartItems[existingIndex]


            if (
                existingItem.quantity >=
                existingItem.availableQuantity
            ) {

                return false
            }


            cartItems[existingIndex] =
                existingItem.copy(

                    quantity =
                        existingItem.quantity + 1
                )


        } else {


            cartItems.add(

                BuyerCartItem(

                    listingId =
                        foodDeal.listingId,

                    foodItemId =
                        foodDeal.foodItemId,

                    sellerId =
                        foodDeal.sellerId,

                    shopName =
                        shopName,

                    foodName =
                        foodDeal.name,

                    price =
                        priceValue,

                    pickupTime =
                        foodDeal.pickupTime,

                    availableQuantity =
                        foodDeal.availableQuantity,

                    quantity = 1
                )
            )
        }


        return true
    }


    fun increaseQuantity(
        index: Int
    ): Boolean {

        if (
            index !in cartItems.indices
        ) {
            return false
        }


        val item =
            cartItems[index]


        if (
            item.quantity >=
            item.availableQuantity
        ) {

            return false
        }


        cartItems[index] =
            item.copy(

                quantity =
                    item.quantity + 1
            )


        return true
    }


    fun decreaseQuantity(
        index: Int
    ) {

        if (
            index !in cartItems.indices
        ) {
            return
        }


        val item =
            cartItems[index]


        if (item.quantity > 1) {

            cartItems[index] =
                item.copy(

                    quantity =
                        item.quantity - 1
                )

        } else {

            cartItems.removeAt(index)
        }
    }


    fun removeItem(
        index: Int
    ) {

        if (
            index in cartItems.indices
        ) {

            cartItems.removeAt(index)
        }
    }


    fun removeItemByListingId(
        listingId: Long
    ) {

        cartItems.removeAll { item ->

            item.listingId ==
                    listingId
        }
    }


    fun clearCart() {

        cartItems.clear()
    }


    fun getTotalPrice(): Double {

        return cartItems.sumOf { item ->

            item.price *
                    item.quantity
        }
    }


    fun getTotalQuantity(): Int {

        return cartItems.sumOf { item ->

            item.quantity
        }
    }


    fun isCartEmpty(): Boolean {

        return cartItems.isEmpty()
    }


    fun containsListing(
        listingId: Long
    ): Boolean {

        return cartItems.any { item ->

            item.listingId ==
                    listingId
        }
    }


    fun getQuantityForListing(
        listingId: Long
    ): Int {

        return cartItems
            .find { item ->

                item.listingId ==
                        listingId
            }
            ?.quantity
            ?: 0
    }
}