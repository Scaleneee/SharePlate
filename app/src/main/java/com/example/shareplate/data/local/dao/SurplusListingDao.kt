package com.example.shareplate.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.shareplate.data.local.entity.SurplusListingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SurplusListingDao {

    // insert new surplus into the database
    @Insert
    suspend fun insertSurplusListing(surplusListing: SurplusListingEntity): Long

    // get all listings by seller
    @Query(
        """
        SELECT * from surplus_listings
        WHERE sellerID = :sellerID
        ORDER BY publishedAt DESC
    """
    )
    fun getListingsBySeller(
        sellerID: Long
    ): Flow<List<SurplusListingEntity>>

    // get all active listings by seller
    @Query(
        """
            SELECT * from surplus_listings
            WHERE sellerID = :sellerID
            AND
            status = "SELLING"
            ORDER BY publishedAt DESC
        """
    )
    fun getActiveListingsBySeller(
        sellerID: Long
    ): Flow<List<SurplusListingEntity>>

    // get listing by its ID
    @Query(
        """
            SELECT * from surplus_listings
            WHERE listingID = :listingID
            LIMIT 1
        """
    )
    fun getListingByID(
        listingID: Long
    ): SurplusListingEntity?

    // update the whole listing
    @Update
    suspend fun updateSurplusListing(surplusListing: SurplusListingEntity)

    // update the current discount and current rescue price
    // use @Query instead of @Update, because @Update only can update the whole entity
    @Query(
        """
        UPDATE surplus_listings
        SET currentDiscountPercent = :discountPercent,
            currentPriceCents = :currentPriceCents
        WHERE listingID = :listingID
        """
    )
    suspend fun updatePrice(
        listingID: Long,
        discountPercent: Int,
        currentPriceCents: Int
    )

    // update listing status
    @Query(
        """
            UPDATE surplus_listings
            SET status = :newStatus
            WHERE listingID = :listingID
        """
    )
    suspend fun updateStatus(
        listingID: Long,
        newStatus: String
    )

    // transfer unsold food to the NGO feed
    @Query(
        """
            UPDATE surplus_listings
            SET status = "TRANSFERRED_TO_NGO"
            WHERE listingID = :listingID
            AND
            availableQuantity > 0
        """
    )
    suspend fun transferToNgo(
        listingID: Long
    )

    // cancel a listing
    @Query(
        """
            UPDATE surplus_listings
            SET status = "CANCELLED"
            WHERE listingID = :listingID
        """
    )
    suspend fun cancelListing(
        listingID: Long
    )
}