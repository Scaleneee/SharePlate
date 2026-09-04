package com.example.shareplate.ui.seller

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareplate.data.repository.SellerRepository
import com.example.shareplate.data.model.CreateFoodItem
import com.example.shareplate.data.model.CreateSurplusListing
import com.example.shareplate.data.model.FoodItem
import com.example.shareplate.util.PriceCalculator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

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

    // seller closing time
    private val _closingTime = MutableStateFlow<String?>(null)
    val closingTime: StateFlow<String?> = _closingTime.asStateFlow()

    private var smartPricingJob: Job? = null

    // fetch seller name and seller closing time
    fun fetchSellerName() {
        viewModelScope.launch {
            try {
                val user = repository.getCurrentSeller()
                _sellerName.value = user?.organisationName ?: "Seller"
                _closingTime.value = user?.closingTime
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

    fun deleteFood(
        foodItemId: Long,
        sellerId: String
    ) {
        viewModelScope.launch {

            try {

                val deletedFood =
                    repository.deleteFoodItem(foodItemId)

                loadFoodItems(sellerId)

            } catch (e: Exception) {

                Log.e(
                    "DeleteFood",
                    "DELETE FAILED: ${e.message}",
                    e
                )

                _errorMessage.value =
                    e.message ?: "Failed to delete food"
            }
        }
    }

    fun publishTodaySurplus(
        foodItem: FoodItem,
        quantity: Int,
        closingTime: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {

            _isLoading.value = true
            _errorMessage.value = null

            try {

                if (quantity <= 0) {
                    throw Exception(
                        "Quantity must be more than 0"
                    )
                }

                val zone =
                    ZoneId.systemDefault()

                val today =
                    java.time.LocalDate.now(zone)

                val sellerClosingTime =
                    java.time.LocalTime.parse(closingTime)

                val closingAt =
                    ZonedDateTime.of(
                        today,
                        sellerClosingTime,
                        zone
                    )

                val currentTime =
                    ZonedDateTime.now(zone)

                // NGO transfer starts 10 minutes before closing
                val transferAt =
                    closingAt.minusMinutes(10)

                if (currentTime >= transferAt) {
                    throw Exception(
                        "Too late to publish this food for buyers"
                    )
                }

                // calculate current discount
                val discountPercent =
                    when {

                        currentTime >= closingAt.minusMinutes(30) ->
                            80

                        currentTime >= closingAt.minusHours(1) ->
                            70

                        else ->
                            60
                    }

                val currentPriceCent =
                    PriceCalculator.calculateDiscountedPrice(
                        originalPriceCent =
                            foodItem.originalPriceCent,
                        discountPercent =
                            discountPercent
                    )

                val listing =
                    CreateSurplusListing(

                        foodItemId =
                            foodItem.foodItemId,

                        sellerId =
                            foodItem.sellerId,

                        publishedQuantity =
                            quantity,

                        availableQuantity =
                            quantity,

                        originalPriceCent =
                            foodItem.originalPriceCent,

                        currentDiscountPercent =
                            discountPercent,

                        currentPriceCent =
                            currentPriceCent,

                        publishedAt =
                            currentTime
                                .toOffsetDateTime()
                                .toString(),

                        closingAt =
                            closingAt
                                .toOffsetDateTime()
                                .toString(),

                        pickupEndAt =
                            closingAt
                                .plusMinutes(30)
                                .toOffsetDateTime()
                                .toString(),

                        status =
                            "ACTIVE"
                    )

                repository.publishSurplus(
                    listing
                )

                onSuccess()

            } catch (e: Exception) {

                Log.e(
                    "PublishSurplus",
                    "Failed to publish surplus",
                    e
                )

                _errorMessage.value =
                    e.message ?: "Failed to publish surplus"

            } finally {

                _isLoading.value = false
            }
        }
    }

    private suspend fun updateSmartPricing(
        sellerId: String
    ) {

        val listings =
            repository.getActiveListings(
                sellerId
            )

        val currentTime =
            OffsetDateTime.now()

        listings.forEach { listing ->

            val closingAt =
                OffsetDateTime.parse(
                    listing.closingAt
                )

            val transferAt =
                closingAt.minusMinutes(10)

            // NGO transfer will be implemented later
            if (
                currentTime.isBefore(
                    transferAt
                )
            ) {

                val newDiscount =
                    when {

                        currentTime >=
                                closingAt.minusMinutes(30) -> {

                            80
                        }

                        currentTime >=
                                closingAt.minusHours(1) -> {

                            70
                        }

                        else -> {

                            60
                        }
                    }

                // don't repeatedly update same price
                if (
                    newDiscount !=
                    listing.currentDiscountPercent
                ) {

                    val newPriceCent =
                        PriceCalculator
                            .calculateDiscountedPrice(
                                originalPriceCent =
                                    listing.originalPriceCents,

                                discountPercent =
                                    newDiscount
                            )

                    repository.updateListingPrice(
                        listingId =
                            listing.listingId,

                        discountPercent =
                            newDiscount,

                        currentPriceCent =
                            newPriceCent
                    )

                    Log.d(
                        "SmartPricing",
                        "Listing ${listing.listingId} updated: " +
                                "$newDiscount% off, " +
                                "$newPriceCent cents"
                    )
                }
            }
        }
    }

    fun refreshSmartPricing(
        sellerId: String
    ) {

        viewModelScope.launch {

            try {

                updateSmartPricing(
                    sellerId
                )

            } catch (e: Exception) {

                Log.e(
                    "SmartPricing",
                    "Failed to refresh pricing",
                    e
                )

                _errorMessage.value =
                    e.message
                        ?: "Failed to refresh smart pricing"
            }
        }
    }

    fun startSmartPricing(
        sellerId: String
    ) {

        smartPricingJob?.cancel()

        smartPricingJob =
            viewModelScope.launch {

                while (isActive) {

                    try {

                        updateSmartPricing(
                            sellerId
                        )

                    } catch (e: Exception) {

                        Log.e(
                            "SmartPricing",
                            "Automatic pricing failed",
                            e
                        )
                    }

                    // check every minute
                    delay(60_000)
                }
            }
    }

    fun stopSmartPricing() {

        smartPricingJob?.cancel()

        smartPricingJob = null
    }

    // CLEAR ERROR
    fun clearError() {
        _errorMessage.value = null
    }
}