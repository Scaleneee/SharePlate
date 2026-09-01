package com.example.shareplate.data.entity

data class FoodItem(

    // properties
    // primary key, Id
    val foodItemId: Long = 0,

    val sellerId: Long,
    val foodName: String,
    val category: String,
    val originalPriceCent: Int, // RM 6.50 == 650 cents, double may cause calculation errors
    val bestBeforeDays: Int,
    val imageUri: String? = null, // ? means this value is optional
    val isActive: Boolean = true
)

