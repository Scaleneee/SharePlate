package com.example.shareplate.data.repository

import android.content.Context
import android.net.Uri
import com.example.shareplate.data.model.CreateFoodItem
import com.example.shareplate.data.remote.SupabaseProvider
import com.example.shareplate.data.model.FoodItem
import com.example.shareplate.data.model.SurplusListing
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage

class SellerRepository {

    private val supabase = SupabaseProvider.client

    // FOOD ITEMS
    suspend fun getFoodItems(
        sellerId: String
    ): List<FoodItem> {

        return supabase
            .from("food_items")
            .select {
                filter {
                    eq("seller_id", sellerId)
                }
            }
            .decodeList<FoodItem>()
    }

    suspend fun getFoodItemById(
        foodItemId: Long
    ): FoodItem {

        return supabase
            .from("food_items")
            .select {
                filter {
                    eq("food_item_id", foodItemId)
                }
            }
            .decodeSingle<FoodItem>()
    }

    suspend fun addFoodItem(
        foodItem: CreateFoodItem
    ) {
        supabase
            .from("food_items")
            .insert(foodItem)
    }


    suspend fun updateFoodItem(
        foodItem: FoodItem
    ) {
        supabase
            .from("food_items")
            .update(foodItem) {

                filter {
                    eq(
                        "food_item_id",
                        foodItem.foodItemId
                    )
                }
            }
    }


    // SURPLUS LISTINGS
    suspend fun publishSurplus(
        listing: SurplusListing
    ) {
        supabase
            .from("surplus_listings")
            .insert(listing)
    }


    suspend fun getActiveListings(
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
    }

    suspend fun uploadFoodImage(
        context: Context,
        imageUri: Uri,
        fileName: String
    ): String {

        // read image from Android Uri
        val inputStream =
            context.contentResolver.openInputStream(imageUri)
                ?: throw Exception("Unable to open selected image")

        val imageBytes =
            inputStream.use {
                it.readBytes()
            }

        // get Supabase storage
        val bucket =
            supabase.storage
                .from("food-images")

        // upload image
        bucket.upload(
            path = fileName,
            data = imageBytes
        ) {
            upsert = true
        }

        // get public URL
        return bucket.publicUrl(fileName)
    }
}