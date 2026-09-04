package com.example.shareplate.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Combined pickup activity displayed to seller.
 *
 * BUYER:
 * pickupId = order_id
 *
 * NGO:
 * pickupId = pickup_id
 */
data class SellerPickupActivityItem(

    val pickupId: Long,

    val donationId: Long? = null,

    val receiverName: String,

    // BUYER or NGO
    val pickupType: String,

    val foodName: String,

    val quantity: Int,

    val pickupCode: String,

    val pickupTime: String,

    val status: String
)


/**
 * orders table
 */
@Serializable
data class SellerOrderRow(

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


/**
 * donations table
 */
@Serializable
data class SellerDonationRow(

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


/**
 * pickups table
 */
@Serializable
data class SellerNgoPickupRow(

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
data class UpdatePickupStatus(

    val status: String
)


@Serializable
data class UpdateNgoPickupCollected(

    val status: String,

    @SerialName("collected_at")
    val collectedAt: String
)