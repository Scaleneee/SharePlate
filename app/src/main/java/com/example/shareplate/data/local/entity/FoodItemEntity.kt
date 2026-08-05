package com.example.shareplate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_items")
data class FoodItemEntity(

    // properties
    // primary key, ID
    @PrimaryKey(autoGenerate = true)
    val foodItemID: Long = 0,

    val sellerID: Long,
    val foodName: String,
    val category: String,
    val originalPriceCent: Int, // RM 6.50 == 650 cents, double may cause calculation errors
    val imageUri: String? = null, // ? means this value is optional
    val isActive: Boolean = true
)

