package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {
    @Query("SELECT * FROM shops ORDER BY name ASC")
    fun getAllShopsFlow(): Flow<List<Shop>>

    @Query("SELECT * FROM shops WHERE id = :id")
    suspend fun getShopById(id: Int): Shop?

    @Query("SELECT COUNT(*) FROM shops")
    suspend fun getCount(): Int

    @Query("SELECT * FROM shops")
    suspend fun getAllShops(): List<Shop>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(shops: List<Shop>)

    @Update
    suspend fun updateShop(shop: Shop)

    @Query("UPDATE shops SET visited = 0, userRating = NULL, visitedTimestamp = NULL")
    suspend fun resetAllProgress()
}
