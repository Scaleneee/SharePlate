package com.example.shareplate.ui.seller.menu

import androidx.compose.runtime.Composable
import com.example.shareplate.model.FoodItem

@Composable
fun EditFoodScreen(
    foodItem: FoodItem,
    onBackClick: () -> Unit,
    onSaveClick: (
        foodName: String,
        category: String,
        originalPrice: String,
        bestBeforeDays: String,
        imageURI: String?,
        isActive: Boolean
    ) -> Unit
) {
    // call the menu food form
    MenuFoodForm(
        // set the title
        title = "Edit Food",
        // set the food info
        initialFoodName = foodItem.foodName,
        initialCategory = foodItem.category,
        initialPrice = "%.2f".format(
            foodItem.originalPriceCent / 100.0
        ),
        initialBestBeforeDays = foodItem.bestBeforeDays.toString(),
        initialImageUri = foodItem.imageUri,
        initialIsActive = foodItem.isActive,
        buttonText = "Update Food",
        onBackClick = onBackClick,
        onSubmit = onSaveClick
    )
}