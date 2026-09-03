package com.example.shareplate.ui.seller

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareplate.data.repository.SellerRepository
import com.example.shareplate.data.model.CreateFoodItem
import com.example.shareplate.data.model.FoodItem
import com.example.shareplate.data.model.SurplusListing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SellerViewModel(
    private val repository: SellerRepository = SellerRepository()
) : ViewModel() {

    // food items
    private val _foodItems = MutableStateFlow<List<FoodItem>>(emptyList())

    // view only food items
    val foodItems: StateFlow<List<FoodItem>> = _foodItems.asStateFlow()

    // LOADING STATE
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ERROR MESSAGE
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // seller name
    private val _sellerName = MutableStateFlow("Seller")
    val sellerName: StateFlow<String> = _sellerName

    init {
        fetchSellerName()
    }

    private fun fetchSellerName() {
        viewModelScope.launch {
            try {
                val user = repository.getCurrentSeller()
                _sellerName.value = user?.organisationName ?: "Seller"
            } catch (e: Exception) {
                // keep default "Seller", optionally log e
                _errorMessage.value = e.message
            }
        }
    }

    // LOAD FOOD ITEMS
    fun loadFoodItems(
        sellerId: String
    ) {
        viewModelScope.launch {

            _isLoading.value = true
            _errorMessage.value = null

            try {

                _foodItems.value = repository.getFoodItems(sellerId)

            } catch (e: Exception) {

                _errorMessage.value = e.message

            } finally {

                _isLoading.value = false
            }
        }
    }


    // ADD FOOD
    fun addFood(
        context: Context,
        sellerId: String,
        foodName: String,
        category: String,
        originalPriceCent: Int,
        bestBeforeDays: Int,
        selectedImageUri: Uri?,
        isActive: Boolean,
        onSuccess: () -> Unit = {}
    ) {

        viewModelScope.launch {

            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Image URL after uploading to Supabase
                var imageUrl: String? = null

                if (selectedImageUri != null) {

                    val fileName = "${sellerId}_${System.currentTimeMillis()}.jpg"

                    imageUrl = repository.uploadFoodImage(
                        context = context, imageUri = selectedImageUri, fileName = fileName
                    )
                }

                val foodItem = CreateFoodItem(
                    sellerId = sellerId,
                    foodName = foodName,
                    category = category,
                    originalPriceCent = originalPriceCent,
                    bestBeforeDays = bestBeforeDays,
                    imageUrl = imageUrl,
                    isActive = isActive
                )

                repository.addFoodItem(foodItem)

                // reload menu after adding
                loadFoodItems(sellerId)

                onSuccess()

            } catch (e: Exception) {
                Log.e(
                    "AddFood",
                    "Failed to add food: ${e.message}",
                    e
                )

                _errorMessage.value =
                    e.message ?: "Failed to add food"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // UPDATE FOOD
    fun updateFood(
        context: Context, foodItem: FoodItem, selectedImageUri: Uri?, onSuccess: () -> Unit = {}
    ) {

        viewModelScope.launch {

            _isLoading.value = true
            _errorMessage.value = null

            try {

                var imageUrl = foodItem.imageUrl

                // only upload if seller chose a new image
                if (selectedImageUri != null) {

                    val fileName = "${foodItem.sellerId}_${System.currentTimeMillis()}.jpg"

                    imageUrl = repository.uploadFoodImage(
                        context = context, imageUri = selectedImageUri, fileName = fileName
                    )
                }

                val updatedFood = foodItem.copy(
                    imageUrl = imageUrl
                )

                repository.updateFoodItem(
                    updatedFood
                )

                loadFoodItems(
                    foodItem.sellerId
                )

                onSuccess()

            } catch (e: Exception) {

                _errorMessage.value = e.message

            } finally {

                _isLoading.value = false
            }
        }
    }

    // PUBLISH SURPLUS
    fun publishSurplus(
        listing: SurplusListing, onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                repository.publishSurplus(
                    listing
                )
                onSuccess()

            } catch (e: Exception) {
                _errorMessage.value = e.message

            } finally {
                _isLoading.value = false
            }
        }
    }

    // CLEAR ERROR
    fun clearError() {
        _errorMessage.value = null
    }
}