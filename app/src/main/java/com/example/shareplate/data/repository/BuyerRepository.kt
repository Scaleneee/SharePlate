package com.example.shareplate.data.repository

import com.example.shareplate.data.model.CreateOrder
import com.example.shareplate.data.model.FoodItem
import com.example.shareplate.data.model.Order
import com.example.shareplate.data.model.SurplusListing
import com.example.shareplate.data.model.User
import com.example.shareplate.data.remote.SupabaseProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.Serializable
import com.example.shareplate.data.model.SavedSeller

@Serializable
data class ReduceListingQuantityParams(
    val p_listing_id: Long, val p_ordered_quantity: Int
)

class BuyerRepository {

    private val supabase = SupabaseProvider.client

    // SELLER
    suspend fun getSellers(): List<User> {

        return supabase.from("users").select {

                filter {

                    eq(
                        "role", "SELLER"
                    )
                }
            }.decodeList<User>()
    }


    suspend fun getSellerById(
        sellerId: String
    ): User? {

        return supabase.from("users").select {

                filter {

                    eq(
                        "user_id", sellerId
                    )
                }
            }.decodeList<User>().firstOrNull()
    }

    // FOOD ITEM
    suspend fun getActiveFoodItems(): List<FoodItem> {

        return supabase.from("food_items").select().decodeList<FoodItem>()
    }

    // FAVOURITE / SAVED SELLERS
    // GET ALL SAVED SELLERS FOR BUYER
    suspend fun getSavedSellers(
        buyerId: String
    ): List<SavedSeller> {

        return supabase.from("saved_sellers").select {

                filter {

                    eq(
                        "buyer_id", buyerId
                    )
                }
            }.decodeList<SavedSeller>()
    }


    // CHECK WHETHER SELLER IS SAVED
    suspend fun isSellerSaved(
        buyerId: String, sellerId: String
    ): Boolean {

        val result = supabase.from("saved_sellers").select {

                filter {

                    eq(
                        "buyer_id", buyerId
                    )

                    eq(
                        "seller_id", sellerId
                    )
                }
            }.decodeList<SavedSeller>()

        return result.isNotEmpty()
    }


    // SAVE SELLER
    suspend fun saveSeller(
        buyerId: String, sellerId: String
    ) {

        val savedSeller = SavedSeller(

            buyerId = buyerId,

            sellerId = sellerId
        )


        supabase.from("saved_sellers").insert(
                savedSeller
            )
    }


    // REMOVE SAVED SELLER
    suspend fun removeSavedSeller(
        buyerId: String, sellerId: String
    ) {

        supabase.from("saved_sellers").delete {

                filter {

                    eq(
                        "buyer_id", buyerId
                    )

                    eq(
                        "seller_id", sellerId
                    )
                }
            }
    }


    // GET SAVED SELLER IDS
    suspend fun getSavedSellerIds(
        buyerId: String
    ): Set<String> {

        return getSavedSellers(
            buyerId
        ).map {

                it.sellerId
            }.toSet()
    }


    suspend fun getFoodItemsBySeller(
        sellerId: String
    ): List<FoodItem> {

        return supabase.from("food_items").select {

                filter {

                    eq(
                        "seller_id", sellerId
                    )
                }
            }.decodeList<FoodItem>()
    }


    suspend fun getFoodItemById(
        foodItemId: Long
    ): FoodItem? {

        return supabase.from("food_items").select {

                filter {

                    eq(
                        "food_item_id", foodItemId
                    )
                }
            }.decodeList<FoodItem>().firstOrNull()
    }

    // SURPLUS LISTING
    suspend fun getActiveListings(): List<SurplusListing> {

        return supabase.from("surplus_listings").select {

                filter {

                    eq(
                        "status", "ACTIVE"
                    )
                }
            }.decodeList<SurplusListing>().filter {

                it.availableQuantity > 0
            }
    }


    suspend fun getActiveListingsBySeller(
        sellerId: String
    ): List<SurplusListing> {

        return supabase.from("surplus_listings").select {

                filter {

                    eq("seller_id", sellerId)

                    eq("status", "ACTIVE")
                }
            }.decodeList<SurplusListing>().filter {

                it.availableQuantity > 0
            }
    }


    suspend fun getListingById(
        listingId: Long
    ): SurplusListing? {

        return supabase.from("surplus_listings").select {

                filter {

                    eq(
                        "listing_id", listingId
                    )
                }
            }.decodeList<SurplusListing>().firstOrNull()
    }

    // ORDER
    suspend fun createOrder(
        order: CreateOrder
    ) {

        supabase.from("orders").insert(order)
    }


    suspend fun getBuyerOrders(
        buyerId: String
    ): List<Order> {

        return supabase.from("orders").select {

                filter {

                    eq(
                        "buyer_id", buyerId
                    )
                }
            }.decodeList<Order>().sortedByDescending {

                it.orderedAt
            }
    }


    suspend fun getOrderById(
        orderId: Long
    ): Order? {

        return supabase.from("orders").select {

                filter {

                    eq(
                        "order_id", orderId
                    )
                }
            }.decodeList<Order>().firstOrNull()
    }

    // UPDATE SURPLUS QUANTITY AFTER ORDER
    suspend fun reduceListingQuantity(
        listingId: Long, orderedQuantity: Int
    ): Boolean {

        return try {

            supabase.postgrest.rpc(
                    function = "reduce_listing_quantity", parameters = ReduceListingQuantityParams(
                        p_listing_id = listingId, p_ordered_quantity = orderedQuantity
                    )
                ).decodeAs<Boolean>()

        } catch (e: Exception) {

            false
        }
    }
}