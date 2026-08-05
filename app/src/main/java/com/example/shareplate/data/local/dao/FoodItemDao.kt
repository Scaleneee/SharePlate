package com.example.shareplate.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.shareplate.data.local.entity.FoodItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodItemDao {

    // add new food item to db
    @Insert
    suspend fun insertFoodItem(foodItem: FoodItemEntity): Long

    // get all food items by seller
    @Query(
        """
        SELECT * FROM food_items
        WHERE sellerID = :sellerID
        AND isActive = 1
        ORDER BY foodName ASC
        """
    )
    fun getFoodItemsBySeller(
        sellerID: Long
    ): Flow<List<FoodItemEntity>>

    // get a single food items obj by its id
    @Query(
        """
            SELECT * from food_items
            WHERE foodItemID = :foodItemID
            LIMIT 1
        """
    )
    suspend fun getFoodItemByID(
        foodItemID: Long
    )

    // update the whole food item obj
    @Update
    suspend fun updateFoodItem(foodItem: FoodItemEntity)

    // hide or restore the food item
    // set the isActive
    @Query(
        """
            UPDATE food_items
            SET isActive = :isActive
            WHERE foodItemID = :foodItemID
        """
    )
    suspend fun updateFoodItemStatus(
        foodItemID: Long,
        isActive: Boolean
    )

    // delete a food item obj
    @Delete
    suspend fun deleteFoodItem(foodItem: FoodItemEntity)

    // delete by ID
    @Query(
        """
            DELETE FROM food_items
            WHERE foodItemID = :foodItemID
        """
    )
    suspend fun deleteFoodItemByID(
        foodItemID: Long
    )

    // delete all food items obj belong to one seller

    @Query(
        """
            DELETE FROM food_items
            WHERE sellerID = :sellerID
        """
    )
    suspend fun deleteAllFoodItemsBySeller(
        sellerID: Long
    )
}