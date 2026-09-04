package com.example.shareplate.data.repository

import com.example.shareplate.data.model.CreateOrder
import com.example.shareplate.data.model.FoodItem
import com.example.shareplate.data.model.Order
import com.example.shareplate.data.model.SurplusListing
import com.example.shareplate.data.model.User
import com.example.shareplate.data.remote.SupabaseProvider
import io.github.jan.supabase.postgrest.from
import kotlin.collections.copy

class NGORepository {

    private val supabase =
        SupabaseProvider.client

    // GET ALL SELLERS
    suspend fun getSellers(): List<User> {
        return supabase
            .from("users")
            .select {
                filter {
                    eq("role", "SELLER")
                }
            }
            .decodeList<User>()
    }

    // GET ONE SELLER
    suspend fun getSellerById(sellerId: String): User? {
        return supabase
            .from("users")
            .select {
                filter {
                    eq("user_id", sellerId)
                }
            }
            .decodeList<User>()
            .firstOrNull()
    }

    // GET ALL FOOD ITEMS
    suspend fun getActiveFoodItems(): List<FoodItem> {
        return supabase
            .from("food_items")
            .select { }
            .decodeList<FoodItem>()
    }

    // GET FOOD ITEMS FOR ONE SELLER
    suspend fun getFoodItemsBySeller(sellerId: String): List<FoodItem> {
        return supabase
            .from("food_items")
            .select {
                filter {
                    eq("seller_id", sellerId)
                }
            }
            .decodeList<FoodItem>()
    }

    // GET ONE FOOD ITEM
    suspend fun getFoodItemById(foodItemId: Long): FoodItem? {
        return supabase
            .from("food_items")
            .select {
                filter {
                    eq("food_item_id", foodItemId)
                }
            }
            .decodeList<FoodItem>()
            .firstOrNull()
    }

    // GET ALL ACTIVE SURPLUS LISTINGS
    /**
     * Get food that has been transferred
     * from sellers to NGOs.
     */
    suspend fun getActiveListings():
            List<SurplusListing> {

        return supabase
            .from("surplus_listings")
            .select {

                filter {

                    eq(
                        "status",
                        "TRANSFERRED_TO_NGO"
                    )
                }
            }
            .decodeList<SurplusListing>()
            .filter {

                it.availableQuantity > 0
            }
    }

    // GET ACTIVE SURPLUS LISTINGS FROM ONE SELLER
    suspend fun getActiveListingsBySeller(
        sellerId: String
    ): List<SurplusListing> {

        return supabase
            .from("surplus_listings")
            .select {

                filter {

                    eq(
                        "seller_id",
                        sellerId
                    )

                    eq(
                        "status",
                        "TRANSFERRED_TO_NGO"
                    )
                }
            }
            .decodeList<SurplusListing>()
            .filter {

                it.availableQuantity > 0
            }
    }

    // GET ONE SURPLUS LISTING
    suspend fun getListingById(listingId: Long): SurplusListing? {
        return supabase
            .from("surplus_listings")
            .select {
                filter {
                    eq("listing_id", listingId)
                }
            }
            .decodeList<SurplusListing>()
            .firstOrNull()
    }

    // CREATE ORDER
    suspend fun createOrder(order: CreateOrder) {
        supabase
            .from("orders")
            .insert(order)
    }

    // GET ORDERS FOR ONE NGO USER
    suspend fun getNgoOrders(buyerId: String): List<Order> {
        return supabase
            .from("orders")
            .select {
                filter {
                    eq("buyer_id", buyerId)
                }
            }
            .decodeList<Order>()
            .sortedByDescending { it.orderedAt }
    }

    // GET ONE ORDER
    suspend fun getOrderById(orderId: Long): Order? {
        return supabase
            .from("orders")
            .select {
                filter {
                    eq("order_id", orderId)
                }
            }
            .decodeList<Order>()
            .firstOrNull()
    }

    // REDUCE SURPLUS AFTER DONATION
    suspend fun reduceListingQuantity(
        listingId: Long,
        orderedQuantity: Int
    ): Boolean {
        val listing = getListingById(listingId) ?: return false

        if (listing.status != "ACTIVE") {
            return false
        }

        if (listing.availableQuantity < orderedQuantity) {
            return false
        }

        val newQuantity = listing.availableQuantity - orderedQuantity
        val newStatus = if (newQuantity == 0) "SOLD_OUT" else "ACTIVE"

        supabase
            .from("surplus_listings")
            .update(
                listing.copy(
                    availableQuantity = newQuantity,
                    status = newStatus
                )
            ) {
                filter {
                    eq("listing_id", listingId)
                }
            }

        return true
    }

    // UPDATE ORDER STATUS
    suspend fun updateOrderStatus(orderId: Long, status: String){
        val order = getOrderById(orderId)?: return

        supabase
            .from("orders")
            .update(order.copy(status = status)){
                filter {
                    eq("order_id", orderId)
                }
            }
    }
}

