package com.example.shareplate.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// use to insert data into Supabase
@Serializable
data class CreateFoodItem(

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
)