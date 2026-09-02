package com.example.shareplate.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Donation(

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