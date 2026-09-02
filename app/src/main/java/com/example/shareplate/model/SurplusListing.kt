package com.example.shareplate.model

data class SurplusListing(
    // properties
    // primary key, Id
    val listingId: Long,

    val foodItemId: Long,
    val sellerId: Long,

    // initially published quantity
    val publishedQuantity: Int,

    // remains quantity
    val availableQuantity: Int,

    // original price of the food
    val originalPriceCents: Int,

    // discount percentage (from 60% -> 70% -> 80%)
    val currentDiscountPercent: Int,

    // discounted price
    val currentPriceCents: Int,

    // date and time when the seller published it
    // store using currentTimeMills()
    val publishedAt: Long,

    // date and time when the store close
    val closingAt: Long,

    // status of the listing, "SELLING", "DONATING", "SOLD_OUT"
    val status: String = "SELLING"
)