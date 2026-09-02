package com.example.shareplate.ui.seller.menu

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.shareplate.ui.theme.SharePlateTheme
import java.net.URI

@Preview
@Composable
fun AddFoodScreenPreview() {
    SharePlateTheme {
        AddFoodScreen({}, {foodName, category, originalPrice, bestBeforeDays, imageURI, isActive -> })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodScreen(
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
        title = "Add Food",
        buttonText = "Save Food",
        onBackClick = onBackClick,
        onSubmit = onSaveClick
    )
}