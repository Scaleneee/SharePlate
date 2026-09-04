package com.example.shareplate.data.repository

import android.content.Context
import android.net.Uri
import com.example.shareplate.data.model.CreateFoodItem
import com.example.shareplate.data.model.CreateSurplusListing
import com.example.shareplate.data.remote.SupabaseProvider
import com.example.shareplate.data.model.FoodItem
import com.example.shareplate.data.model.SellerDonationRow
import com.example.shareplate.data.model.SellerNgoPickupRow
import com.example.shareplate.data.model.SellerOrderRow
import com.example.shareplate.data.model.SellerPickupActivityItem
import com.example.shareplate.data.model.SurplusListing
import com.example.shareplate.data.model.UpdateSurplusPrice
import com.example.shareplate.data.model.User
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage

class SellerRepository {

    private val supabase = SupabaseProvider.client

    // FOOD ITEMS
    suspend fun getFoodItems(
        sellerId: String
    ): List<FoodItem> {

        return supabase
            .from("food_items")
            .select {
                filter {
                    eq("seller_id", sellerId)
                }
            }
            .decodeList<FoodItem>()
    }

    suspend fun getFoodItemById(
        foodItemId: Long
    ): FoodItem {

        return supabase
            .from("food_items")
            .select {
                filter {
                    eq("food_item_id", foodItemId)
                }
            }
            .decodeSingle<FoodItem>()
    }

    suspend fun addFoodItem(
        foodItem: CreateFoodItem
    ) {
        supabase
            .from("food_items")
            .insert(foodItem)
    }

    suspend fun deleteFoodItem(
        foodItemId: Long
    ) {
        supabase
            .from("food_items")
            .delete {
                filter {
                    eq("food_item_id", foodItemId)
                }
            }
    }

    suspend fun updateFoodItem(
        foodItem: FoodItem
    ) {
        supabase
            .from("food_items")
            .update(foodItem) {

                filter {
                    eq(
                        "food_item_id",
                        foodItem.foodItemId
                    )
                }
            }
    }


    // SURPLUS LISTINGS
    suspend fun publishSurplus(
        listing: CreateSurplusListing
    ) {
        supabase
            .from("surplus_listings")
            .insert(listing)
    }

    /**
     * Get buyer + NGO pickup activities
     * belonging to the current seller.
     */
    suspend fun getSellerPickupActivities(
        sellerId: String
    ): List<SellerPickupActivityItem> {

        val pickupActivities =
            mutableListOf<SellerPickupActivityItem>()


        /**
         * Get ALL listings belonging to seller.
         *
         * Don't only get ACTIVE because activity
         * also needs old/completed listings.
         */
        val sellerListings =
            supabase
                .from("surplus_listings")
                .select {

                    filter {
                        eq(
                            "seller_id",
                            sellerId
                        )
                    }
                }
                .decodeList<SurplusListing>()


        /**
         * =========================================
         * BUYER PICKUPS
         * =========================================
         *
         * Seller listing
         *      ↓
         * orders
         */
        sellerListings.forEach { listing ->

            val foodItem =
                try {

                    getFoodItemById(
                        listing.foodItemId
                    )

                } catch (e: Exception) {

                    null
                }


            val orders =
                supabase
                    .from("orders")
                    .select {

                        filter {

                            eq(
                                "listing_id",
                                listing.listingId
                            )
                        }
                    }
                    .decodeList<SellerOrderRow>()


            orders.forEach { order ->

                pickupActivities.add(

                    SellerPickupActivityItem(

                        // for BUYER this stores order_id
                        pickupId =
                            order.orderId,

                        donationId =
                            null,

                        // Buyer name is not stored
                        // directly in orders.
                        receiverName =
                            "Buyer",

                        pickupType =
                            "BUYER",

                        foodName =
                            foodItem?.foodName
                                ?: "Food Item",

                        quantity =
                            order.quantity,

                        pickupCode =
                            order.pickupCode,

                        /**
                         * orders table does not contain
                         * a selected pickup time.
                         *
                         * Use the listing pickup deadline.
                         */
                        pickupTime =
                            listing.pickupEndAt,

                        status =
                            order.status
                    )
                )
            }
        }


        /**
         * =========================================
         * NGO PICKUPS
         * =========================================
         *
         * seller
         *   ↓
         * donations
         *   ↓
         * pickups
         */
        val donations =
            supabase
                .from("donations")
                .select {

                    filter {

                        eq(
                            "seller_id",
                            sellerId
                        )
                    }
                }
                .decodeList<SellerDonationRow>()


        donations.forEach { donation ->

            val listing =
                sellerListings.find {

                    it.listingId ==
                            donation.listingId
                }


            val foodItem =
                if (listing != null) {

                    try {

                        getFoodItemById(
                            listing.foodItemId
                        )

                    } catch (e: Exception) {

                        null
                    }

                } else {

                    null
                }


            val pickups =
                supabase
                    .from("pickups")
                    .select {

                        filter {

                            eq(
                                "donation_id",
                                donation.donationId
                            )
                        }
                    }
                    .decodeList<SellerNgoPickupRow>()


            pickups.forEach { pickup ->

                pickupActivities.add(

                    SellerPickupActivityItem(

                        // actual pickup_id
                        pickupId =
                            pickup.pickupId,

                        donationId =
                            donation.donationId,

                        receiverName =
                            pickup.collectorName,

                        pickupType =
                            "NGO",

                        foodName =
                            foodItem?.foodName
                                ?: "Donated Food",

                        quantity =
                            donation.donationQuantity,

                        pickupCode =
                            pickup.pickupCode,

                        pickupTime =
                            pickup.selectedPickupAt,

                        status =
                            pickup.status
                    )
                )
            }
        }


        return pickupActivities
    }
    suspend fun updateListingPrice(
        listingId: Long,
        discountPercent: Int,
        currentPriceCent: Int
    ) {

        val updateData =
            UpdateSurplusPrice(
                currentDiscountPercent =
                    discountPercent,

                currentPriceCent =
                    currentPriceCent
            )

        supabase
            .from("surplus_listings")
            .update(updateData) {

                filter {

                    eq(
                        "listing_id",
                        listingId
                    )
                }
            }
    }

    suspend fun getActiveListings(
        sellerId: String
    ): List<SurplusListing> {
        return supabase
            .from("surplus_listings")
            .select {

                filter {

                    eq(
                        "seller_id",
                        sellerId
                    )

                    eq(
                        "status",
                        "ACTIVE"
                    )
                }
            }
            .decodeList<SurplusListing>()
    }

    suspend fun uploadFoodImage(
        context: Context,
        imageUri: Uri,
        fileName: String
    ): String {

        // read image from Android Uri
        val inputStream =
            context.contentResolver.openInputStream(imageUri)
                ?: throw Exception("Unable to open selected image")

        val imageBytes =
            inputStream.use {
                it.readBytes()
            }

        // get Supabase storage
        val bucket =
            supabase.storage
                .from("food-images")

        // upload image
        bucket.upload(
            path = fileName,
            data = imageBytes
        ) {
            upsert = true
        }

        // get public URL
        return bucket.publicUrl(fileName)
    }

    suspend fun getCurrentSeller(): User? {
        val uid = supabase.auth.currentUserOrNull()?.id ?: return null
        return supabase.from("users")
            .select {
                filter { eq("user_id", uid) }
            }
            .decodeSingleOrNull<User>()
    }
}