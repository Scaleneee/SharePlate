package com.example.shareplate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surplus_listings")
data class SurplusListingEntity(
    // properties
    // primary key, Id
    @PrimaryKey(autoGenerate = true)
    val listingId: Long = 0,


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