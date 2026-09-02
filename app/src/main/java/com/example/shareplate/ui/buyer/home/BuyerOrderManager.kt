package com.example.shareplate.ui.buyer.order

import com.example.shareplate.data.model.CreateOrder
import com.example.shareplate.data.model.SurplusListing
import com.example.shareplate.data.remote.SupabaseProvider
import com.example.shareplate.data.repository.BuyerRepository
import io.github.jan.supabase.auth.auth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.random.Random


data class BuyerOrderResult(

    val success: Boolean,

    val message: String,

    val pickupCode: String? = null,

    val totalPriceCent: Int = 0
)


class BuyerOrderManager(

    private val repository: BuyerRepository =
        BuyerRepository()

) {

    private val supabase =
        SupabaseProvider.client


    suspend fun submitOrder(): BuyerOrderResult {

        try {

            // -----------------------------------------
            // 1. CHECK LOGGED-IN BUYER
            // -----------------------------------------

            val currentUser =
                supabase.auth
                    .currentUserOrNull()


            if (currentUser == null) {

                return BuyerOrderResult(
                    success = false,
                    message =
                        "Please log in before placing an order."
                )
            }


            val buyerId =
                currentUser.id


            // -----------------------------------------
            // 2. CHECK CART
            // -----------------------------------------

            val cartItems =
                BuyerCartStore
                    .cartItems
                    .toList()


            if (cartItems.isEmpty()) {

                return BuyerOrderResult(
                    success = false,
                    message =
                        "Your cart is empty."
                )
            }


            // -----------------------------------------
            // 3. CHECK THAT CART ONLY HAS ONE SELLER
            // -----------------------------------------

            val sellerIds =
                cartItems
                    .map {
                        it.sellerId
                    }
                    .distinct()


            if (sellerIds.size > 1) {

                return BuyerOrderResult(
                    success = false,
                    message =
                        "Please order from one shop at a time."
                )
            }


            // -----------------------------------------
            // 4. RECHECK SUPABASE LISTINGS
            // -----------------------------------------

            val validatedItems =
                mutableListOf<
                        Pair<
                                BuyerCartItem,
                                SurplusListing
                                >
                        >()


            for (cartItem in cartItems) {


                val listing =
                    repository
                        .getListingById(
                            cartItem.listingId
                        )


                if (listing == null) {

                    return BuyerOrderResult(
                        success = false,
                        message =
                            "${cartItem.foodName} is no longer available."
                    )
                }


                // Make sure this listing still belongs
                // to the same seller.
                if (
                    listing.sellerId !=
                    cartItem.sellerId
                ) {

                    return BuyerOrderResult(
                        success = false,
                        message =
                            "Unable to verify ${cartItem.foodName}."
                    )
                }


                // Listing must still be ACTIVE.
                if (
                    !listing.status.equals(
                        "ACTIVE",
                        ignoreCase = true
                    )
                ) {

                    return BuyerOrderResult(
                        success = false,
                        message =
                            "${cartItem.foodName} is no longer available."
                    )
                }


                // Check stock again from Supabase.
                if (
                    listing.availableQuantity <
                    cartItem.quantity
                ) {

                    return BuyerOrderResult(
                        success = false,
                        message =
                            "Only ${listing.availableQuantity} ${cartItem.foodName} left."
                    )
                }


                validatedItems.add(
                    cartItem to listing
                )
            }


            // -----------------------------------------
            // 5. GENERATE PICKUP CODE
            // -----------------------------------------

            val pickupCode =
                generatePickupCode()


            // -----------------------------------------
            // 6. ORDER DATE
            // -----------------------------------------

            val orderedAt =
                getCurrentDateTime()


            // -----------------------------------------
            // 7. CALCULATE TOTAL
            // -----------------------------------------

            var totalOrderPriceCent = 0


            for (
            validatedItem
            in validatedItems
            ) {

                val cartItem =
                    validatedItem.first


                val listing =
                    validatedItem.second


                val itemTotal =
                    listing.currentPriceCents *
                            cartItem.quantity


                totalOrderPriceCent +=
                    itemTotal
            }


            // -----------------------------------------
            // 8. CREATE ORDERS
            // -----------------------------------------

            for (
            validatedItem
            in validatedItems
            ) {

                val cartItem =
                    validatedItem.first


                val listing =
                    validatedItem.second


                val unitPriceCent =
                    listing.currentPriceCents


                val totalPriceCent =
                    unitPriceCent *
                            cartItem.quantity


                val newOrder =
                    CreateOrder(

                        listingId =
                            cartItem.listingId,

                        buyerId =
                            buyerId,

                        quantity =
                            cartItem.quantity,

                        unitPriceCent =
                            unitPriceCent,

                        totalPriceCent =
                            totalPriceCent,

                        orderedAt =
                            orderedAt,

                        pickupCode =
                            pickupCode,

                        status =
                            "PENDING"
                    )


                // Save into Supabase orders table.
                repository.createOrder(
                    newOrder
                )


                // Reduce available quantity.
                val quantityUpdated =
                    repository
                        .reduceListingQuantity(

                            listingId =
                                cartItem.listingId,

                            orderedQuantity =
                                cartItem.quantity
                        )


                if (!quantityUpdated) {

                    return BuyerOrderResult(
                        success = false,
                        message =
                            "Unable to update stock for ${cartItem.foodName}."
                    )
                }
            }


            // -----------------------------------------
            // 9. CLEAR CART
            // -----------------------------------------

            BuyerCartStore
                .clearCart()


            // -----------------------------------------
            // 10. SUCCESS
            // -----------------------------------------

            return BuyerOrderResult(

                success = true,

                message =
                    "Order placed successfully.",

                pickupCode =
                    pickupCode,

                totalPriceCent =
                    totalOrderPriceCent
            )


        } catch (e: Exception) {


            return BuyerOrderResult(

                success = false,

                message =
                    e.message
                        ?: "Unable to place order."
            )
        }
    }


    private fun generatePickupCode():
            String {

        val number =
            Random.nextInt(
                1000,
                9999
            )


        return "SP$number"
    }


    private fun getCurrentDateTime():
            String {

        val formatter =
            SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                Locale.US
            )


        formatter.timeZone =
            TimeZone.getTimeZone(
                "UTC"
            )


        return formatter.format(
            Date()
        )
    }
}