package com.example.assignment.data.repository

import com.example.shareplate.data.model.FoodItem
import com.example.shareplate.data.model.SurplusListing
import com.example.shareplate.data.model.User
import com.example.shareplate.data.remote.SupabaseProvider
import io.github.jan.supabase.postgrest.from

class NGORepository {

    private val supabase = SupabaseProvider.client

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

    // GET ALL ACTIVE FOOD ITEMS
    suspend fun getActiveFoodItems(): List<FoodItem> {
        return supabase
            .from("food_items")
            .select {
                filter {
                    eq("is_active", true)
                }
            }
            .decodeList<FoodItem>()
    }

    // GET ACTIVE FOOD ITEMS FROM ONE SELLER
    suspend fun getFoodItemsBySeller(sellerId: String): List<FoodItem> {
        return supabase
            .from("food_items")
            .select {
                filter {
                    eq("seller_id", sellerId)
                    eq("is_active", true)
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
    suspend fun getActiveListings(): List<SurplusListing> {
        return supabase
            .from("surplus_listings")
            .select {
                filter {
                    eq("status", "ACTIVE")
                }
            }
            .decodeList<SurplusListing>()
            .filter { it.availableQuantity > 0 }
    }

    // GET ACTIVE SURPLUS LISTINGS FROM ONE SELLER
    suspend fun getActiveListingsBySeller(sellerId: String): List<SurplusListing> {
        return supabase
            .from("surplus_listings")
            .select {
                filter {
                    eq("seller_id", sellerId)
                    eq("status", "ACTIVE")
                }
            }
            .decodeList<SurplusListing>()
            .filter { it.availableQuantity > 0 }
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
}