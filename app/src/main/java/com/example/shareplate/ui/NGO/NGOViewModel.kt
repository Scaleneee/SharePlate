package com.example.shareplate.ui.NGO

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareplate.data.repository.NGORepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NGOViewModel(
    private val repository: NGORepository = NGORepository()
) : ViewModel() {

    private val _shops = MutableStateFlow<List<FoodDonation>>(emptyList())
    val shops: StateFlow<List<FoodDonation>> = _shops.asStateFlow()

    private val _pickups = MutableStateFlow<List<NGOActivityItem>>(emptyList())
    val pickups: StateFlow<List<NGOActivityItem>> = _pickups.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadShops() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val sellers = repository.getSellers()
                val listings = repository.getActiveListings()
                val foodItems = repository.getActiveFoodItems()
                val itemsById = foodItems.associateBy { it.foodItemId }

                val shopList = sellers.map { seller ->
                    val sellerListings = listings.filter { it.sellerId == seller.userId }

                    val itemStrings = sellerListings.map { listing ->
                        val item = itemsById[listing.foodItemId]
                        "${item?.foodName ?: "Food"} - ${listing.availableQuantity}"
                    }

                    FoodDonation(
                        name = seller.organisationName ?: seller.name,
                        location = seller.address ?: "",
                        availableFood = sellerListings.sumOf { it.availableQuantity },
                        foodItems = itemStrings,
                        nearby = false,
                        liked = false
                    )
                }

                _shops.value = if (shopList.isEmpty()) sampleShops() else shopList

                _pickups.value = listings.mapNotNull { listing ->
                    val seller = sellers.find { it.userId == listing.sellerId }
                        ?: return@mapNotNull null
                    val item = itemsById[listing.foodItemId]
                    val sellerName = seller.organisationName ?: seller.name

                    NGOActivityItem(
                        name = sellerName,
                        location = seller.address ?: "",
                        shortName = sellerName.take(2).uppercase(),
                        pickupTime = "Pickup: ${formatTime(listing.pickupEndAt)}",
                        items = "${item?.foodName ?: "Food"} - ${listing.availableQuantity}",
                        done = false
                    )
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
                if (_shops.value.isEmpty()) {
                    _shops.value = sampleShops()
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

fun addPickup(donation: FoodDonation, items: List<String>) {
        val pickup = NGOActivityItem(
            name = donation.name,
            location = donation.location,
            shortName = donation.name.take(2).uppercase(),
            pickupTime = "Pickup: today",
            items = items.joinToString(", "),
            done = false
        )
        _pickups.value = listOf(pickup) + _pickups.value
    }

    private fun sampleShops(): List<FoodDonation> = listOf(
        FoodDonation(
            name = "Ondo Bakery",
            location = "George Town - 5.0 km",
            availableFood = 22,
            foodItems = listOf("Bread - 8", "Cookie pack - 4", "Sweet Donuts - 5"),
            nearby = false,
            liked = false
        ),
        FoodDonation(
            name = "The Coffee Bean & Tea Leaf",
            location = "Kuala Lumpur",
            availableFood = 18,
            foodItems = listOf("Butter Croissant - 4", "Chocolate Muffin - 3", "Chicken Sandwich - 5"),
            nearby = true,
            liked = false
        ),
        FoodDonation(
            name = "Bread History",
            location = "Subang Jaya",
            availableFood = 15,
            foodItems = listOf("Sausage Bun - 6", "Chocolate Roll - 4", "Sugar Donut - 5"),
            nearby = false,
            liked = false
        )
    )

    private fun formatTime(epochMillis: Long): String =
        SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(epochMillis))

    fun clearError() {
        _errorMessage.value = null
    }
}