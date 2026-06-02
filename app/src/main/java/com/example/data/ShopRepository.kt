package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShopRepository(private val shopDao: ShopDao) {

    val allShops: Flow<List<Shop>> = shopDao.getAllShopsFlow()

    suspend fun checkAndSeedDatabase() {
        withContext(Dispatchers.IO) {
            val existing = shopDao.getAllShops()
            val existingNames = existing.map { it.name }.toSet()
            val missing = InitialShops.list.filter { it.name !in existingNames }
            if (missing.isNotEmpty()) {
                shopDao.insertAll(missing)
            }
        }
    }

    suspend fun updateShop(shop: Shop) {
        withContext(Dispatchers.IO) {
            shopDao.updateShop(shop)
        }
    }

    suspend fun resetAllProgress() {
        withContext(Dispatchers.IO) {
            shopDao.resetAllProgress()
        }
    }
}
