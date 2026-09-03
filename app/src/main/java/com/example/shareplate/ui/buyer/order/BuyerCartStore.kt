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

    val cartItems = mutableStateListOf<BuyerCartItem>()

    // ADD ITEM
    fun addItem(

        shopName: String,
        foodDeal: FoodDeal

    ): Boolean {

        // CHECK AVAILABLE STOCK
        if (
            foodDeal.availableQuantity <= 0
        ) {
            return false
        }

        // ONLY ALLOW ONE SHOP IN ONE CART
        if (
            cartItems.isNotEmpty()
        ) {

            val currentSellerId = cartItems
                    .first()
                    .sellerId


            if (
                currentSellerId != foodDeal.sellerId
            ) {

                return false
            }
        }

        // CONVERT PRICE STRING TO DOUBLE
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

        // CHECK WHETHER ITEM ALREADY EXISTS
       val existingIndex =
            cartItems
                .indexOfFirst {

                    it.listingId == foodDeal.listingId
                }


        if (
            existingIndex >= 0
        ) {

            val existingItem =
                cartItems[
                    existingIndex
                ]
            // Cannot exceed Supabase available quantity
            if (
                existingItem.quantity >=
                existingItem.availableQuantity
            ) {
                return false
            }


            cartItems[
                existingIndex
            ] =
                existingItem.copy(
                    quantity = existingItem.quantity + 1
                )


        } else {


            cartItems.add(

                BuyerCartItem(
                    listingId = foodDeal.listingId,
                    foodItemId = foodDeal.foodItemId,
                    sellerId = foodDeal.sellerId,
                    shopName = shopName,
                    foodName = foodDeal.name,
                    price = priceValue,
                    pickupTime = foodDeal.pickupTime,
                    availableQuantity = foodDeal.availableQuantity,
                    quantity = 1
                )
            )
        }
        return true
    }

    // INCREASE QUANTITY
    fun increaseQuantity(
        index: Int
    ): Boolean {

        if (
            index !in cartItems.indices
        ) {
            return false
        }

        val item = cartItems[index]
        if (
            item.quantity >= item.availableQuantity
        ) {
            return false
        }


        cartItems[index] =
            item.copy(
                quantity = item.quantity + 1
            )

        return true
    }

    // DECREASE QUANTITY
    fun decreaseQuantity(
        index: Int
    ) {

        if (
            index !in cartItems.indices
        ) {
            return
        }

        val item = cartItems[index]

        if (
            item.quantity > 1
        ) {
            cartItems[index] =
                item.copy(
                    quantity = item.quantity - 1
                )
        } else {
            cartItems.removeAt(
                index
            )
        }
    }

    // REMOVE ITEM
    fun removeItem(
        index: Int
    ) {


        if (
            index in cartItems.indices
        ) {
            cartItems.removeAt(
                index
            )
        }
    }
    // REMOVE USING LISTING ID
    fun removeItemByListingId(

        listingId: Long

    ) {


        cartItems.removeAll {

            it.listingId == listingId
        }
    }

    // CLEAR CART
    fun clearCart() {
        cartItems.clear()
    }

    // TOTAL PRICE
    fun getTotalPrice():
            Double {

        return cartItems.sumOf {

            it.price * it.quantity
        }
    }

    // TOTAL QUANTITY
    fun getTotalQuantity():
            Int {
        return cartItems.sumOf {
            it.quantity
        }
    }

    // CHECK EMPTY
    fun isCartEmpty():
            Boolean {


        return cartItems.isEmpty()
    }

    // CHECK LISTING EXISTS
    fun containsListing(
        listingId: Long
    ): Boolean {
        return cartItems.any {
            it.listingId == listingId
        }
    }

    // GET ITEM QUANTITY
    fun getQuantityForListing(
        listingId: Long
    ): Int {

        return cartItems
            .find {
                it.listingId == listingId
            }?.quantity ?: 0
    }

    // GET CURRENT SELLER
   fun getCurrentSellerId():
            String? {


        return cartItems
            .firstOrNull()
            ?.sellerId
    }

    // CHECK WHETHER ANOTHER SHOP CAN BE ADDED
    fun canAddFromSeller(

        sellerId: String

    ): Boolean {

        if (
            cartItems.isEmpty()
        ) {
            return true
        }

        return cartItems
            .first()
            .sellerId ==
                sellerId
    }
}