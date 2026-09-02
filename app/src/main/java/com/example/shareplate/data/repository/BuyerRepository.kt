package com.example.shareplate.data.repository

import com.example.shareplate.data.model.CreateOrder
import com.example.shareplate.data.model.FoodItem
import com.example.shareplate.data.model.Order
import com.example.shareplate.data.model.SurplusListing
import com.example.shareplate.data.model.User
import com.example.shareplate.data.remote.SupabaseProvider
import io.github.jan.supabase.postgrest.from


class BuyerRepository {

    private val supabase =
        SupabaseProvider.client

    // SELLER
    suspend fun getSellers(): List<User> {

        return supabase
            .from("users")
            .select {

                filter {

                    eq(
                        "role",
                        "SELLER"
                    )
                }
            }
            .decodeList<User>()
    }


    suspend fun getSellerById(
        sellerId: String
    ): User? {

        return supabase
            .from("users")
            .select {

                filter {

                    eq(
                        "user_id",
                        sellerId
                    )
                }
            }
            .decodeList<User>()
            .firstOrNull()
    }

    // FOOD ITEM
    suspend fun getActiveFoodItems(): List<FoodItem> {

        return supabase
            .from("food_items")
            .select {

                filter {

                    eq(
                        "is_active",
                        true
                    )
                }
            }
            .decodeList<FoodItem>()
    }


    suspend fun getFoodItemsBySeller(
        sellerId: String
    ): List<FoodItem> {

        return supabase
            .from("food_items")
            .select {

                filter {

                    eq(
                        "seller_id",
                        sellerId
                    )

                    eq(
                        "is_active",
                        true
                    )
                }
            }
            .decodeList<FoodItem>()
    }


    suspend fun getFoodItemById(
        foodItemId: Long
    ): FoodItem? {

        return supabase
            .from("food_items")
            .select {

                filter {

                    eq(
                        "food_item_id",
                        foodItemId
                    )
                }
            }
            .decodeList<FoodItem>()
            .firstOrNull()
    }

    // SURPLUS LISTING
    suspend fun getActiveListings():
            List<SurplusListing> {

        return supabase
            .from("surplus_listings")
            .select {

                filter {

                    eq(
                        "status",
                        "ACTIVE"
                    )
                }
            }
            .decodeList<SurplusListing>()
            .filter {

                it.availableQuantity > 0
            }
    }


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
                        "ACTIVE"
                    )
                }
            }
            .decodeList<SurplusListing>()
            .filter {

                it.availableQuantity > 0
            }
    }


    suspend fun getListingById(
        listingId: Long
    ): SurplusListing? {

        return supabase
            .from("surplus_listings")
            .select {

                filter {

                    eq(
                        "listing_id",
                        listingId
                    )
                }
            }
            .decodeList<SurplusListing>()
            .firstOrNull()
    }

    // ORDER
    suspend fun createOrder(
        order: CreateOrder
    ) {

        supabase
            .from("orders")
            .insert(order)
    }


    suspend fun getBuyerOrders(
        buyerId: String
    ): List<Order> {

        return supabase
            .from("orders")
            .select {

                filter {

                    eq(
                        "buyer_id",
                        buyerId
                    )
                }
            }
            .decodeList<Order>()
            .sortedByDescending {

                it.orderedAt
            }
    }


    suspend fun getOrderById(
        orderId: Long
    ): Order? {

        return supabase
            .from("orders")
            .select {

                filter {

                    eq(
                        "order_id",
                        orderId
                    )
                }
            }
            .decodeList<Order>()
            .firstOrNull()
    }

    // UPDATE SURPLUS QUANTITY AFTER ORDER
    suspend fun reduceListingQuantity(
        listingId: Long,
        orderedQuantity: Int
    ): Boolean {

        val listing =
            getListingById(
                listingId
            )
                ?: return false


        if (
            listing.status != "ACTIVE"
        ) {

            return false
        }


        if (
            listing.availableQuantity <
            orderedQuantity
        ) {

            return false
        }


        val newQuantity =
            listing.availableQuantity -
                    orderedQuantity


        val newStatus =

            if (newQuantity == 0) {

                "SOLD_OUT"

            } else {

                "ACTIVE"
            }


        val updatedListing =
            listing.copy(

                availableQuantity =
                    newQuantity,

                status =
                    newStatus
            )


        supabase
            .from("surplus_listings")
            .update(
                updatedListing
            ) {

                filter {

                    eq(
                        "listing_id",
                        listingId
                    )
                }
            }


        return true
    }
}