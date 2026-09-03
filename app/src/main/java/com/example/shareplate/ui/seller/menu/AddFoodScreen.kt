package com.example.shareplate.ui.seller.menu

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.shareplate.ui.theme.SharePlateTheme

@Preview
@Composable
fun AddFoodScreenPreview() {
    SharePlateTheme {
        AddFoodScreen({}, {foodName, category, originalPrice, bestBeforeDays, imageURI, isActive -> })
    }
}

@Composable
fun AddFoodScreen(
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
        title = "Add Food",
        buttonText = "Save Food",
        onBackClick = onBackClick,
        onSubmit = onSaveClick
    )
}