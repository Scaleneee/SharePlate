package com.example.shareplate.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pickup(

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