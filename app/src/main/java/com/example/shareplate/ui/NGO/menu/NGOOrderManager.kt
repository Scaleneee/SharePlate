package com.example.shareplate.ui.NGO.menu

import com.example.shareplate.data.model.CreateOrder
import com.example.shareplate.data.model.SurplusListing
import com.example.shareplate.data.remote.SupabaseProvider
import com.example.shareplate.data.repository.NGORepository
import com.example.shareplate.ui.NGO.order.NGOCartItem
import com.example.shareplate.ui.NGO.order.NGOCartStore
import io.github.jan.supabase.auth.auth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.random.Random

data class NGOOrderResult(
    val success: Boolean,
    val message: String,
    val pickupCode: String? = null,
    val totalPriceCent: Int = 0
)

class NGOOrderManager(
    private val repository: NGORepository = NGORepository()
) {

    private val supabase = SupabaseProvider.client

    suspend fun submitOrder(): NGOOrderResult {
        try {

            // 1. Check logged-in NGO user
            val currentUser = supabase.auth.currentUserOrNull()
                ?: return NGOOrderResult(
                    success = false,
                    message = "Please log in before placing a donation."
                )

            val ngoId = currentUser.id

            // 2. Check cart
            val cartItems = NGOCartStore.cartItems.toList()
            if (cartItems.isEmpty()) {
                return NGOOrderResult(false, "Your cart is empty.")
            }

            // 3. Only one shop per cart
            val sellerIds = cartItems.map { it.sellerId }.distinct()
            if (sellerIds.size > 1) {
                return NGOOrderResult(false, "Please donate from one shop at a time.")
            }

            // 4. Re-validate listings from Supabase
            val validatedItems = mutableListOf<Pair<NGOCartItem, SurplusListing>>()
            for (cartItem in cartItems) {

                val listing = repository.getListingById(cartItem.listingId)
                    ?: return NGOOrderResult(false, "${cartItem.foodName} is no longer available.")

                if (listing.sellerId != cartItem.sellerId) {
                    return NGOOrderResult(false, "Unable to verify ${cartItem.foodName}.")
                }

                if (!listing.status.equals("ACTIVE", ignoreCase = true)) {
                    return NGOOrderResult(false, "${cartItem.foodName} is no longer available.")
                }

                if (listing.availableQuantity < cartItem.quantity) {
                    return NGOOrderResult(
                        false,
                        "Only ${listing.availableQuantity} ${cartItem.foodName} left."
                    )
                }

                validatedItems.add(cartItem to listing)
            }

            // 5. Pickup code + order date
            val pickupCode = generatePickupCode()
            val orderedAt = getCurrentDateTime()

            // 6. Create free orders (RM0.00) and reduce stock
            for ((cartItem, listing) in validatedItems) {

                val newOrder = CreateOrder(
                    listingId = cartItem.listingId,
                    buyerId = ngoId,
                    quantity = cartItem.quantity,
                    unitPriceCent = 0,
                    totalPriceCent = 0,
                    orderedAt = orderedAt,
                    pickupCode = pickupCode,
                    pickupNote = "",
                    paymentMethod = "",
                    status = "PENDING"
                )

                repository.createOrder(newOrder)

                val updated = repository.reduceListingQuantity(
                    listingId = cartItem.listingId,
                    orderedQuantity = cartItem.quantity
                )

                if (!updated) {
                    return NGOOrderResult(false, "Unable to update stock for ${cartItem.foodName}.")
                }
            }

            // 7. Clear cart + donation
            NGOCartStore.clearCart()
            NGOCartStore.donation = null

            // 8. Success
            return NGOOrderResult(
                success = true,
                message = "Donation placed successfully.",
                pickupCode = pickupCode,
                totalPriceCent = 0
            )

        } catch (e: Exception) {
            return NGOOrderResult(
                success = false,
                message = e.message ?: "Unable to place donation."
            )
        }
    }

    private fun generatePickupCode(): String {
        return "ND${Random.nextInt(1000, 9999)}"
    }

    private fun getCurrentDateTime(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return formatter.format(Date())
    }
}
