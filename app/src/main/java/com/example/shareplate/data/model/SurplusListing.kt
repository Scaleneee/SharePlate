package com.example.shareplate.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SurplusListing(

    // primary key
    @SerialName("listing_id")
    val listingId: Long,

    @SerialName("food_item_id")
    val foodItemId: Long,

    @SerialName("seller_id")
    val sellerId: String,

    // initially published quantity
    @SerialName("published_quantity")
    val publishedQuantity: Int,

    // remaining quantity
    @SerialName("available_quantity")
    val availableQuantity: Int,

    // original price of the food
    @SerialName("original_price_cents")
    val originalPriceCents: Int,

    // discount percentage: 60 -> 70 -> 80
    @SerialName("current_discount_percent")
    val currentDiscountPercent: Int,

    // discounted price
    @SerialName("current_price_cents")
    val currentPriceCents: Int,

    // date and time when seller published it
    @SerialName("published_at")
    @Serializable(with = FlexibleTimestampSerializer::class)
    val publishedAt: Long,

    // date and time when store closes
    @SerialName("closing_at")
    val closingAt: String,

    // last allowed pickup time
    @SerialName("pickup_end_at")
    @Serializable(with = FlexibleTimestampSerializer::class)
    val pickupEndAt: Long,

    // listing status
    val status: String = "ACTIVE",

    @SerialName("created_at")
    val createdAt: String? = null
)