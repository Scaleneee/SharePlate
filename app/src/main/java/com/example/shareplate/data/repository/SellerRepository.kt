package com.example.shareplate.data.repository

import android.content.Context
import android.net.Uri
import com.example.shareplate.data.model.CreateFoodItem
import com.example.shareplate.data.model.CreateSurplusListing
import com.example.shareplate.data.remote.SupabaseProvider
import com.example.shareplate.data.model.FoodItem
import com.example.shareplate.data.model.SellerPickupActivityItem
import com.example.shareplate.data.model.SurplusListing
import com.example.shareplate.data.model.User
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime


@Serializable
private data class UpdateSurplusQuantityPayload(

    @SerialName("published_quantity")
    val publishedQuantity: Int,

    @SerialName("available_quantity")
    val availableQuantity: Int,

    val status: String
)

@Serializable
private data class SellerOrderRow(
    @SerialName("order_id")
    val orderId: Long,

    @SerialName("listing_id")
    val listingId: Long,

    @SerialName("buyer_id")
    val buyerId: String,

    val quantity: Int,

    @SerialName("unit_price_cent")
    val unitPriceCent: Int,

    @SerialName("total_price_cent")
    val totalPriceCent: Int,

    @SerialName("ordered_at")
    val orderedAt: String,

    @SerialName("pickup_code")
    val pickupCode: String,

    val status: String
)

@Serializable
private data class SellerDonationRow(
    @SerialName("donation_id")
    val donationId: Long,

    @SerialName("listing_id")
    val listingId: Long,

    @SerialName("seller_id")
    val sellerId: String,

    @SerialName("ngo_id")
    val ngoId: String? = null,

    @SerialName("donation_quantity")
    val donationQuantity: Int,

    @SerialName("available_at")
    val availableAt: String,

    @SerialName("pickup_start_at")
    val pickupStartAt: String,

    @SerialName("pickup_end_at")
    val pickupEndAt: String,

    val status: String,

    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
private data class SellerNgoPickupRow(
    @SerialName("pickup_id")
    val pickupId: Long,

    @SerialName("donation_id")
    val donationId: Long,

    @SerialName("ngo_id")
    val ngoId: String,

    @SerialName("selected_pickup_at")
    val selectedPickupAt: String,

    @SerialName("collector_name")
    val collectorName: String,

    @SerialName("pickup_code")
    val pickupCode: String,

    val status: String,

    @SerialName("collected_at")
    val collectedAt: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
private data class UpdateStatusPayload(
    val status: String
)

@Serializable
private data class UpdateNgoPickupCollectedPayload(
    val status: String,

    @SerialName("collected_at")
    val collectedAt: String
)

@Serializable
private data class UpdateSurplusPricePayload(
    @SerialName("current_discount_percent")
    val currentDiscountPercent: Int,

    @SerialName("current_price_cent")
    val currentPriceCent: Int
)

class SellerRepository {

    private val supabase = SupabaseProvider.client

    /**
     * Get all surplus listings published by seller.
     */
    suspend fun getSellerSurplusListings(
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
                }
            }
            .decodeList<SurplusListing>()
            .sortedByDescending {
                it.publishedAt
            }
    }


    /**
     * Update already-published surplus quantity.
     *
     * We update:
     * - published_quantity
     * - available_quantity
     * - status
     */
    suspend fun updatePublishedSurplusQuantity(
        listingId: Long,
        publishedQuantity: Int,
        availableQuantity: Int,
        status: String
    ) {

        val updateData =
            UpdateSurplusQuantityPayload(

                publishedQuantity =
                    publishedQuantity,

                availableQuantity =
                    availableQuantity,

                status =
                    status
            )

        supabase
            .from("surplus_listings")
            .update(
                updateData
            ) {

                filter {

                    eq(
                        "listing_id",
                        listingId
                    )
                }
            }
    }

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


        return pickupActivities.sortedByDescending { it.pickupTime }
    }

    /**
     * Buyer picked up order.
     *
     * orders.status:
     * READY_FOR_PICKUP -> COLLECTED
     */
    suspend fun markBuyerOrderCollected(
        orderId: Long
    ) {

        val updateData =
            UpdateStatusPayload(
                status = "COLLECTED"
            )


        supabase
            .from("orders")
            .update(updateData) {

                filter {

                    eq(
                        "order_id",
                        orderId
                    )
                }
            }
    }

    /**
     * NGO collected donation.
     */
    suspend fun markNgoPickupCollected(
        pickupId: Long,
        donationId: Long
    ) {

        val collectedTime =
            OffsetDateTime
                .now()
                .toString()


        /**
         * Update pickup
         */
        val pickupUpdate =
            UpdateNgoPickupCollectedPayload(

                status =
                    "COLLECTED",

                collectedAt =
                    collectedTime
            )


        supabase
            .from("pickups")
            .update(pickupUpdate) {

                filter {

                    eq(
                        "pickup_id",
                        pickupId
                    )
                }
            }


        /**
         * Also update the donation.
         */
        val donationUpdate =
            UpdateStatusPayload(
                status = "COLLECTED"
            )


        supabase
            .from("donations")
            .update(donationUpdate) {

                filter {

                    eq(
                        "donation_id",
                        donationId
                    )
                }
            }
    }
    suspend fun updateListingPrice(
        listingId: Long,
        discountPercent: Int,
        currentPriceCent: Int
    ) {

        val updateData =
            UpdateSurplusPricePayload(
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