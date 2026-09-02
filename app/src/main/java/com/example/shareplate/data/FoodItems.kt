package com.example.shareplate.data

import com.example.shareplate.model.FoodItem

object FoodItems {
    /**
     * temporary test data
     */
    val foodItems = listOf(
        FoodItem(
            foodItemId = 1,
            sellerId = 1,
            foodName = "Blue Berry Bread",
            category = "Bread",
            originalPriceCent = 550,
            bestBeforeDays = 2,
            imageUri = null,
            isActive = true
        ),

        FoodItem(
            foodItemId = 2,
            sellerId = 1,
            foodName = "Chocolate Croissant",
            category = "Pastry",
            originalPriceCent = 650,
            bestBeforeDays = 1,
            imageUri = null,
            isActive = true
        ),

        FoodItem(
            foodItemId = 3,
            sellerId = 1,
            foodName = "Chicken Sandwich",
            category = "Sandwich",
            originalPriceCent = 800,
            bestBeforeDays = 1,
            imageUri = null,
            isActive = true
        )
    )

    fun getFoodItemById(foodItemId: Long): FoodItem? {
        return foodItems.find { item -> item.foodItemId ==  foodItemId}
    }
}
