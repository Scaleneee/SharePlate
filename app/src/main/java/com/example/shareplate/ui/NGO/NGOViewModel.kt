package com.example.shareplate.ui.NGO

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareplate.data.model.Order
import com.example.shareplate.data.remote.SupabaseProvider
import com.example.shareplate.data.repository.NGORepository
import com.example.shareplate.ui.NGO.order.NGOCartItem
import com.example.shareplate.ui.NGO.menu.NGOOrderManager
import com.example.shareplate.ui.NGO.menu.NGOOrderResult
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.copy

class NGOViewModel(
    private val repository: NGORepository = NGORepository(),
    private val orderManager: NGOOrderManager = NGOOrderManager()
) : ViewModel() {

    private val _shops = MutableStateFlow<List<FoodDonation>>(emptyList())
    val shops: StateFlow<List<FoodDonation>> = _shops.asStateFlow()

    private val _orders = MutableStateFlow<List<NGOActivityItem>>(emptyList())
    val orders: StateFlow<List<NGOActivityItem>> = _orders.asStateFlow()

    private val _orderResult = MutableStateFlow<NGOOrderResult?>(null)
    val orderResult: StateFlow<NGOOrderResult?> = _orderResult.asStateFlow()

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

                    val sellerInventory = sellerListings.map { listing ->
                        val food = itemsById[listing.foodItemId]
                        NGOCartItem(
                            listingId = listing.listingId,
                            foodItemId = listing.foodItemId,
                            sellerId = seller.userId,
                            shopName = seller.organisationName ?: seller.name,
                            foodName = food?.foodName?: "Food",
                            price = 0.0,
                            pickupTime = formatPickupTime(listing.pickupEndAt),
                            availableQuantity = listing.availableQuantity,
                            quantity = listing.availableQuantity,
                            imageUrl = food?.imageUrl
                        )
                    }
                    val itemStrings = sellerInventory.map { "${it.foodName} - ${it.availableQuantity}" }

                    FoodDonation(
                        name = seller.organisationName ?: seller.name,
                        location = seller.address ?: "",
                        availableFood = sellerInventory.sumOf { it.availableQuantity },
                        foodItems = itemStrings,
                        nearby = (seller.address ?: "").lowercase().let {
                            it.contains("penang") || it.contains("george")
                        },
                        liked = false,
                        sellerId = seller.userId,
                        inventory = sellerInventory
                    )
                }

                _shops.value = if (shopList.isEmpty()) sampleShops() else shopList
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

    fun loadOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val currentUser = SupabaseProvider.client.auth.currentUserOrNull()
                if (currentUser == null) {
                    _errorMessage.value = "Please log in to view your donations."
                    return@launch
                }
                val ngoOrders = repository.getNgoOrders(currentUser.id)
                _orders.value = ngoOrders.mapNotNull { toActivityItem(it) }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unable to load donations."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitOrder(
        onComplete: (success: Boolean, pickupCode: String, totalPriceCent: Int) -> Unit = { _, _, _ -> }
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _orderResult.value = null
            try {
                val result = orderManager.submitOrder()
                _orderResult.value = result
                if (result.success) {
                    onComplete(true, result.pickupCode ?: "", result.totalPriceCent)
                } else {
                    _errorMessage.value = result.message
                    onComplete(false, "", 0)
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unable to place donation."
                onComplete(false, "", 0)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun toActivityItem(order: Order): NGOActivityItem? {
        val listing = repository.getListingById(order.listingId) ?: return null
        val food = repository.getFoodItemById(listing.foodItemId)
        val seller = repository.getSellerById(listing.sellerId)
        val shopName = seller?.organisationName?.takeIf { it.isNotBlank() } ?: seller?.name ?: "Shop"

        return NGOActivityItem(
            name = shopName,
            location = seller?.address ?: "Address not provided",
            shortName = shopName.take(2).uppercase(),
            pickupTime = formatPickupTime(listing.pickupEndAt),
            items = "${food?.foodName ?: "Food Item"} - ${order.quantity}",
            pickupCode = order.pickupCode,
            orderedAt = order.orderedAt,
            orderId = order.orderId,
            done = order.status.uppercase() in setOf("COMPLETED", "CANCELLED")
        )
    }

    private fun sampleShops(): List<FoodDonation> = listOf(
        FoodDonation(
            name = "Ondo Bakery",
            location = "George Town - 5.0 km",
            availableFood = 22,
            foodItems = listOf("Bread - 8", "Cookie pack - 4", "Sweet Donuts - 5"),
            nearby = true,
            liked = false
        ),
        FoodDonation(
            name = "The Coffee Bean & Tea Leaf",
            location = "Kuala Lumpur",
            availableFood = 18,
            foodItems = listOf("Butter Croissant - 4", "Chocolate Muffin - 3", "Chicken Sandwich - 5"),
            nearby = false,
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

    private fun formatPickupTime(epochMillis: Long): String {
        if (epochMillis <= 0) return "Pickup time unavailable"
        val milliseconds = if (epochMillis < 100_000_000_000L) epochMillis * 1000 else epochMillis
        return "Pickup before ${SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(milliseconds))}"
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun confirmPickup(activity: NGOActivityItem) {
        _orders.value = _orders.value.map {
            if (it.orderId != 0L && it.orderId == activity.orderId) it.copy(done = true) else it
        }
        if (activity.orderId != 0L) {
            viewModelScope.launch {
                try {
                    repository.updateOrderStatus(activity.orderId, "COMPLETED")
                } catch (_: Exception) {
                }
            }
        }
    }
}
