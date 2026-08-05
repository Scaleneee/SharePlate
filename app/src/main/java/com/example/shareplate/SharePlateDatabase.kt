package com.example.shareplate

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shareplate.data.local.dao.FoodItemDao
import com.example.shareplate.data.local.dao.SurplusListingDao
import com.example.shareplate.data.local.entity.FoodItemEntity
import com.example.shareplate.data.local.entity.SurplusListingEntity

@Database(
    entities = [
        FoodItemEntity::class,
        SurplusListingEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class SharePlateDatabase : RoomDatabase() {

    // use to get a dao obj
    abstract fun foodItemDao(): FoodItemDao
    abstract fun surplusListingDao(): SurplusListingDao

    // get a database obj
    companion object {
        @Volatile
        private var INSTANCE: SharePlateDatabase? = null

        fun getDatabase(context: Context): SharePlateDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    SharePlateDatabase::class.java,
                    "shareplate_database"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}