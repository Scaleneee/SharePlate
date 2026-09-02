package com.example.shareplate.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Order(

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