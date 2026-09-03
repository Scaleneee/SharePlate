package com.example.shareplate.ui.seller.menu

import android.net.Uri
import androidx.compose.runtime.Composable
import com.example.shareplate.data.model.FoodItem

@Composable
fun EditFoodScreen(
    foodItem: FoodItem,
    onBackClick: () -> Unit,
    onSaveClick: (
        foodName: String,
        category: String,
        originalPrice: String,
        bestBeforeDays: String,
        imageUri: Uri?,
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
        initialImageUrl = foodItem.imageUrl,
        buttonText = "Update Food",
        onBackClick = onBackClick,
        onSubmit = onSaveClick
    )
}