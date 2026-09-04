package com.example.shareplate.ui.buyer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareplate.data.model.FoodItem
import com.example.shareplate.data.model.Order
import com.example.shareplate.data.model.SurplusListing
import com.example.shareplate.data.model.User
import com.example.shareplate.data.remote.SupabaseProvider
import com.example.shareplate.data.repository.BuyerRepository
import com.example.shareplate.data.supabase.AuthRepository
import com.example.shareplate.data.supabase.Profile
import com.example.shareplate.ui.buyer.home.FoodDeal
import com.example.shareplate.ui.buyer.order.BuyerCartItem
import com.example.shareplate.ui.buyer.order.BuyerCartStore
import com.example.shareplate.ui.buyer.order.BuyerOrderManager
import com.example.shareplate.ui.buyer.order.BuyerOrderResult
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.shareplate.data.model.Notification

data class BuyerOrderDetails(

    val order: Order,

    val listing: SurplusListing?,

    val foodItem: FoodItem?,

    val seller: User?
)


class BuyerViewModel(

    private val repository: BuyerRepository = BuyerRepository(),

    private val authRepository: AuthRepository = AuthRepository(),

    private val orderManager: BuyerOrderManager = BuyerOrderManager()

) : ViewModel() {


    // BUYER PROFILE
    private val _profile = MutableStateFlow<Profile?>(null)

    val profile: StateFlow<Profile?> = _profile.asStateFlow()


    // SELLERS / SHOPS
    private val _sellers = MutableStateFlow<List<User>>(
        emptyList()
    )

    val sellers: StateFlow<List<User>> = _sellers.asStateFlow()

    //save/favourite sellers
    private val _savedSellerIds = MutableStateFlow<Set<String>>(emptySet())

    val savedSellerIds: StateFlow<Set<String>> = _savedSellerIds.asStateFlow()

    // NOTIFICATIONS
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())

    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()
    private val _unreadNotificationCount = MutableStateFlow(0)

    val unreadNotificationCount: StateFlow<Int> = _unreadNotificationCount.asStateFlow()

    // SELECTED SELLER
    private val _selectedSeller = MutableStateFlow<User?>(null)

    val selectedSeller: StateFlow<User?> = _selectedSeller.asStateFlow()

    // FOOD ITEMS
    private val _foodItems = MutableStateFlow<List<FoodItem>>(
        emptyList()
    )

    val foodItems: StateFlow<List<FoodItem>> = _foodItems.asStateFlow()

    // SURPLUS LISTINGS
    private val _listings = MutableStateFlow<List<SurplusListing>>(
        emptyList()
    )

    val listings: StateFlow<List<SurplusListing>> = _listings.asStateFlow()

    // BUYER ORDERS
    private val _orders = MutableStateFlow<List<BuyerOrderDetails>>(
        emptyList()
    )

    val orders: StateFlow<List<BuyerOrderDetails>> = _orders.asStateFlow()

    // SELECTED ORDER
    private val _selectedOrder = MutableStateFlow<BuyerOrderDetails?>(
        null
    )

    val selectedOrder: StateFlow<BuyerOrderDetails?> = _selectedOrder.asStateFlow()

    // ORDER RESULT
    private val _orderResult = MutableStateFlow<BuyerOrderResult?>(null)

    val orderResult: StateFlow<BuyerOrderResult?> = _orderResult.asStateFlow()

    private val _pickupNote = MutableStateFlow("")

    val pickupNote: StateFlow<String> = _pickupNote.asStateFlow()

    // LOADING
    private val _isLoading = MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ERROR
    private val _errorMessage = MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // SUCCESS MESSAGE
    private val _successMessage = MutableStateFlow<String?>(null)

    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    // CART
    val cartItems: List<BuyerCartItem>
        get() = BuyerCartStore.cartItems


    // LOAD BUYER HOME
    fun loadBuyerHome() {

        viewModelScope.launch {

            _isLoading.value = true

            _errorMessage.value = null


            try {

                // GET CURRENT BUYER
                _profile.value = authRepository.getCurrentProfile()


                // GET SELLERS / SHOPS
                _sellers.value = repository.getSellers()

                val currentUser = SupabaseProvider.client.auth.currentUserOrNull()


                if (currentUser != null) {

                    // LOAD FAVOURITES
                    _savedSellerIds.value = repository.getSavedSellerIds(
                        currentUser.id
                    )


                    // LOAD NOTIFICATIONS
                    val notificationList = repository.getNotifications(
                        currentUser.id
                    )

                    _notifications.value = notificationList

                    _unreadNotificationCount.value = notificationList.count {
                        !it.isRead
                    }
                }


            } catch (e: Exception) {

                _errorMessage.value = e.message ?: "Unable to load Buyer Home."

            } finally {

                _isLoading.value = false
            }
        }
    }

    // LOAD FAVOURITES
    fun loadFavourites() {

        viewModelScope.launch {

            try {
                val currentUser = SupabaseProvider.client.auth.currentUserOrNull() ?: return@launch
                _savedSellerIds.value = repository.getSavedSellerIds(currentUser.id)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unable to load favourites."
            }
        }
    }

    // CHECK FAVOURITE
    fun isFavourite(
        sellerId: String
    ): Boolean {

        return _savedSellerIds.value.contains(
            sellerId
        )
    }

    // TOGGLE FAVOURITE
    fun toggleFavourite(

        sellerId: String,

        onResult: (
            success: Boolean, message: String
        ) -> Unit = { _, _ -> }

    ) {

        viewModelScope.launch {

            try {

                val currentUser = SupabaseProvider.client.auth.currentUserOrNull()


                if (currentUser == null) {

                    onResult(
                        false, "Please log in first."
                    )

                    return@launch
                }


                val buyerId = currentUser.id


                val currentlySaved = _savedSellerIds.value.contains(
                    sellerId
                )


                if (currentlySaved) {

                    // REMOVE FAVOURITE
                    repository.removeSavedSeller(
                        buyerId = buyerId, sellerId = sellerId
                    )


                    _savedSellerIds.value = _savedSellerIds.value - sellerId


                    onResult(
                        true, "Removed from favourites."
                    )

                } else {

                    // ADD FAVOURITE
                    repository.saveSeller(
                        buyerId = buyerId, sellerId = sellerId
                    )


                    _savedSellerIds.value = _savedSellerIds.value + sellerId


                    onResult(
                        true, "Added to favourites."
                    )
                }


            } catch (e: Exception) {

                _errorMessage.value = e.message ?: "Unable to update favourite."

                onResult(
                    false, e.message ?: "Unable to update favourite."
                )
            }
        }
    }

    // LOAD NOTIFICATIONS
    fun loadNotifications() {

        viewModelScope.launch {

            try {

                val currentUser = SupabaseProvider.client.auth.currentUserOrNull() ?: return@launch


                val result = repository.getNotifications(
                    currentUser.id
                )


                _notifications.value = result


                _unreadNotificationCount.value = result.count {
                    !it.isRead
                }


            } catch (e: Exception) {

                _errorMessage.value = e.message ?: "Unable to load notifications."
            }
        }
    }

    // MARK NOTIFICATION AS READ
    fun markNotificationAsRead(
        notificationId: Long
    ) {

        viewModelScope.launch {

            try {

                val currentUser = SupabaseProvider.client.auth.currentUserOrNull() ?: return@launch


                repository.markNotificationAsRead(
                    notificationId = notificationId,

                    userId = currentUser.id
                )


                _notifications.value = _notifications.value.map { notification ->

                    if (notification.notificationId == notificationId) {

                        notification.copy(
                            isRead = true
                        )

                    } else {

                        notification
                    }
                }


                _unreadNotificationCount.value = _notifications.value.count {
                    !it.isRead
                }


            } catch (e: Exception) {

                _errorMessage.value = e.message ?: "Unable to update notification."
            }
        }
    }

    // MARK ALL NOTIFICATIONS AS READ
    fun markAllNotificationsAsRead() {

        viewModelScope.launch {

            try {

                val currentUser = SupabaseProvider.client.auth.currentUserOrNull() ?: return@launch


                repository.markAllNotificationsAsRead(
                    currentUser.id
                )


                _notifications.value = _notifications.value.map {
                    it.copy(
                        isRead = true
                    )
                }


                _unreadNotificationCount.value = 0


            } catch (e: Exception) {

                _errorMessage.value = e.message ?: "Unable to update notifications."
            }
        }
    }

    // LOAD PROFILE
    fun loadProfile() {

        viewModelScope.launch {

            _isLoading.value = true

            _errorMessage.value = null


            try {

                val currentProfile = authRepository.getCurrentProfile()


                if (currentProfile == null) {

                    _errorMessage.value = "Profile not found."

                    return@launch
                }


                _profile.value = currentProfile


            } catch (e: Exception) {

                _errorMessage.value = e.message ?: "Unable to load profile."

            } finally {

                _isLoading.value = false
            }
        }
    }

    // UPDATE PROFILE
    fun updateProfile(

        name: String,

        phone: String,

        address: String,

        onSuccess: () -> Unit = {}

    ) {

        viewModelScope.launch {

            _isLoading.value = true

            _errorMessage.value = null

            _successMessage.value = null


            try {

                val currentProfile = _profile.value ?: authRepository.getCurrentProfile()


                if (currentProfile == null) {

                    _errorMessage.value = "Profile not found."

                    return@launch
                }


                val updatedProfile = currentProfile.copy(

                    name = name.trim(),

                    phone = phone.trim(),

                    address = address.trim().ifBlank {
                        null
                    })


                val result = authRepository.updateProfile(
                    updatedProfile
                )


                result.onSuccess {

                    _profile.value = it

                    _successMessage.value = "Profile updated successfully."

                    onSuccess()

                }.onFailure {

                    _errorMessage.value = it.message ?: "Unable to update profile."
                }


            } catch (e: Exception) {

                _errorMessage.value = e.message ?: "Unable to update profile."

            } finally {

                _isLoading.value = false
            }
        }
    }

    // LOAD SHOP
    fun loadShop(

        sellerId: String

    ) {

        viewModelScope.launch {

            _isLoading.value = true

            _errorMessage.value = null


            try {

                val seller = repository.getSellerById(
                    sellerId
                )


                if (seller == null) {

                    _errorMessage.value = "Seller not found."

                    return@launch
                }


                _selectedSeller.value = seller


                _foodItems.value = repository.getFoodItemsBySeller(
                    sellerId
                )


                _listings.value = repository.getActiveListingsBySeller(
                    sellerId
                )


            } catch (e: Exception) {

                _errorMessage.value = e.message ?: "Unable to load shop."

            } finally {

                _isLoading.value = false
            }
        }
    }

    // REFRESH SHOP
    fun refreshShop() {

        val sellerId = _selectedSeller.value?.userId ?: return


        loadShop(
            sellerId
        )
    }

    // ADD TO CART
    fun addToCart(

        shopName: String,

        foodDeal: FoodDeal

    ): Boolean {

        return BuyerCartStore.addItem(

            shopName = shopName,

            foodDeal = foodDeal
        )
    }


    // CHECK SELLER
    fun canAddFromSeller(

        sellerId: String

    ): Boolean {

        return BuyerCartStore.canAddFromSeller(
            sellerId
        )
    }

    // CART ITEM QUANTITY
    fun getQuantityForListing(

        listingId: Long

    ): Int {

        return BuyerCartStore.getQuantityForListing(
            listingId
        )
    }

    // INCREASE QUANTITY
    fun increaseCartQuantity(

        index: Int

    ): Boolean {

        return BuyerCartStore.increaseQuantity(
            index
        )
    }

    // DECREASE QUANTITY
    fun decreaseCartQuantity(

        index: Int

    ) {

        BuyerCartStore.decreaseQuantity(
            index
        )
    }

    // REMOVE CART ITEM
    fun removeCartItem(

        index: Int

    ) {

        BuyerCartStore.removeItem(
            index
        )
    }

    // CLEAR CART
    fun clearCart() {

        BuyerCartStore.clearCart()
    }

    // CART TOTAL
    fun getCartTotal(): Double {

        return BuyerCartStore.getTotalPrice()
    }

    // CART TOTAL QUANTITY
    fun getCartQuantity(): Int {

        return BuyerCartStore.getTotalQuantity()
    }

    // CHECK CART EMPTY
    fun isCartEmpty(): Boolean {

        return BuyerCartStore.isCartEmpty()
    }

    // SUBMIT ORDER
    fun submitOrder(

        paymentMethod: String,

        onSuccess: (
            pickupCode: String, totalPriceCent: Int
        ) -> Unit = { _, _ -> }

    ) {

        viewModelScope.launch {

            _isLoading.value = true

            _errorMessage.value = null

            _orderResult.value = null


            try {

                val result = orderManager.submitOrder(

                    pickupNote = _pickupNote.value,

                    paymentMethod = paymentMethod
                )


                _orderResult.value = result


                if (result.success) {

                    _successMessage.value = result.message

                    clearPickupNote()

                    onSuccess(

                        result.pickupCode ?: "",

                        result.totalPriceCent
                    )

                } else {

                    _errorMessage.value = result.message
                }


            } catch (e: Exception) {

                _errorMessage.value = e.message ?: "Unable to place order."

            } finally {

                _isLoading.value = false
            }
        }
    }

    // LOAD ORDERS
    fun loadOrders(
        showLoading: Boolean = true
    ) {

        viewModelScope.launch {

            if (showLoading) {
                _isLoading.value = true
            }

            _errorMessage.value = null

            try {

                val currentUser =
                    SupabaseProvider
                        .client
                        .auth
                        .currentUserOrNull()
                        ?: return@launch


                val buyerOrders =
                    repository
                        .getBuyerOrders(
                            currentUser.id
                        )


                val details =
                    mutableListOf<BuyerOrderDetails>()


                for (order in buyerOrders) {

                    val listing =
                        repository
                            .getListingById(
                                order.listingId
                            )

                    val foodItem =
                        if (listing != null) {
                            repository.getFoodItemById(
                                listing.foodItemId
                            )
                        } else {
                            null
                        }

                    val seller =
                        if (listing != null) {
                            repository.getSellerById(
                                listing.sellerId
                            )
                        } else {
                            null
                        }


                    details.add(

                        BuyerOrderDetails(
                            order = order,
                            listing = listing,
                            foodItem = foodItem,
                            seller = seller
                        )
                    )
                }


                _orders.value =
                    details


            } catch (e: Exception) {

                _errorMessage.value =
                    e.message
                        ?: "Unable to load orders."

            } finally {

                if (showLoading) {
                    _isLoading.value = false
                }
            }
        }
    }

    // LOAD ONE ORDER
    fun loadOrder(

        orderId: Long

    ) {

        viewModelScope.launch {

            _isLoading.value = true

            _errorMessage.value = null


            try {

                val currentUser = SupabaseProvider.client.auth.currentUserOrNull()


                if (currentUser == null) {

                    _errorMessage.value = "Please log in first."

                    return@launch
                }


                val order = repository.getOrderById(
                    orderId
                )


                if (order == null) {

                    _errorMessage.value = "Order not found."

                    return@launch
                }


                if (order.buyerId != currentUser.id) {

                    _errorMessage.value = "Unable to access this order."

                    return@launch
                }


                val listing = repository.getListingById(
                    order.listingId
                )


                val foodItem =

                    if (listing != null) {

                        repository.getFoodItemById(
                            listing.foodItemId
                        )

                    } else {

                        null
                    }


                val seller =

                    if (listing != null) {

                        repository.getSellerById(
                            listing.sellerId
                        )

                    } else {

                        null
                    }


                _selectedOrder.value = BuyerOrderDetails(

                    order = order,

                    listing = listing,

                    foodItem = foodItem,

                    seller = seller
                )


            } catch (e: Exception) {

                _errorMessage.value = e.message ?: "Unable to load order."

            } finally {

                _isLoading.value = false
            }
        }
    }

    fun updatePickupNote(
        note: String
    ) {

        _pickupNote.value = note
    }


    fun clearPickupNote() {

        _pickupNote.value = ""
    }

    // SIGN OUT
    fun signOut(

        onSuccess: () -> Unit = {}

    ) {

        viewModelScope.launch {

            _isLoading.value = true

            _errorMessage.value = null


            try {

                authRepository.signOut()

                BuyerCartStore.clearCart()

                _profile.value = null

                _sellers.value = emptyList()

                _savedSellerIds.value = emptySet()

                _notifications.value = emptyList()

                _unreadNotificationCount.value = 0

                _selectedSeller.value = null

                _foodItems.value = emptyList()

                _listings.value = emptyList()

                _orders.value = emptyList()

                _selectedOrder.value = null

                _orderResult.value = null


                onSuccess()


            } catch (e: Exception) {

                _errorMessage.value = e.message ?: "Unable to log out."

            } finally {

                _isLoading.value = false
            }
        }
    }

    // CLEAR ERROR
    fun clearError() {

        _errorMessage.value = null
    }

    // CLEAR SUCCESS
    fun clearSuccessMessage() {

        _successMessage.value = null
    }

    // CLEAR ORDER RESULT
    fun clearOrderResult() {

        _orderResult.value = null
    }
}