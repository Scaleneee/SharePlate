package com.example.shareplate.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateSurplusListing(

    @SerialName("food_item_id")
    val foodItemId: Long,

    @SerialName("seller_id")
    val sellerId: String,

    @SerialName("published_quantity")
    val publishedQuantity: Int,

    @SerialName("available_quantity")
    val availableQuantity: Int,

    @SerialName("original_price_cents")
    val originalPriceCent: Int,

    @SerialName("current_discount_percent")
    val currentDiscountPercent: Int,

    @SerialName("current_price_cents")
    val currentPriceCent: Int,

    @SerialName("published_at")
    val publishedAt: String,

    @SerialName("closing_at")
    val closingAt: String,

    @SerialName("pickup_end_at")
    val pickupEndAt: String,

    val status: String
)