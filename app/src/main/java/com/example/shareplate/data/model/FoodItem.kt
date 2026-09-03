package com.example.shareplate.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FoodItem(

    // properties
    // primary key, Id
    @SerialName("food_item_id")
    val foodItemId: Long,

    @SerialName("seller_id")
    val sellerId: String,

    @SerialName("food_name")
    val foodName: String,

    val category: String,

    @SerialName("original_price_cent")
    val originalPriceCent: Int,

    @SerialName("best_before_days")
    val bestBeforeDays: Int,

    @SerialName("image_url")
    val imageUrl: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null
)
