package com.example.shareplate.ui.NGO.order

import androidx.compose.runtime.mutableStateListOf
import com.example.shareplate.ui.NGO.FoodDonation

data class NGOCartItem(
    val listingId: Long,
    val foodItemId: Long,
    val sellerId: String,
    val shopName: String,
    val foodName: String,
    val price: Double,
    val pickupTime: String,
    val availableQuantity: Int,
    val quantity: Int = 1,
    val imageUrl: String? = null
)

object NGOCartStore {
    var donation: FoodDonation? = null
    var rawItems: List<String> = emptyList()
    val cartItems = mutableStateListOf<NGOCartItem>()
    fun set(donation: FoodDonation, items: List<String>) {
        this.donation = donation
        this.rawItems = items
        cartItems.clear()
        items.forEachIndexed { index, raw ->
            val parts = raw.split(" - ")
            val name = parts.getOrElse(0) { raw }
            val qty = parts.getOrNull(1)?.trim()?.toIntOrNull() ?: 0
            cartItems.add(
                NGOCartItem(
                index.toLong(),
                index.toLong(),
                donation.name,
                donation.name,
                name, 0.0,
                "Pickup today",
                qty,
                qty)
            )
        }
    }
    fun increaseQuantity(index: Int): Boolean {
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

        cartItems[index] = item.copy(quantity = item.quantity + 1)
        return true
    }
    fun decreaseQuantity(index: Int) {
        if (
            index !in cartItems.indices
            ) {
            return
        }
        val item = cartItems[index]
        if (
            item.quantity > 1
            ) cartItems[index] = item.copy(quantity = item.quantity - 1)

        else cartItems.removeAt(index)
    }
    fun removeItem(index: Int) { if (index in cartItems.indices) cartItems.removeAt(index) }

    fun clearCart() { cartItems.clear() }
    fun getTotalPrice(): Double = cartItems.sumOf { it.price * it.quantity }
    fun getTotalQuantity(): Int = cartItems.sumOf { it.quantity }
    fun isCartEmpty(): Boolean = cartItems.isEmpty()
}