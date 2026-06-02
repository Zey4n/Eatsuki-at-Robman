package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Shop
import com.example.data.ShopRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

sealed interface GachaState {
    object Idle : GachaState
    object Rolling : GachaState
    data class Success(val shop: Shop) : GachaState
}

enum class ActiveTab {
    GACHA, JOURNAL, DIRECTORY
}

data class AppState(
    val currentTab: ActiveTab = ActiveTab.GACHA,
    val selectedCategory: String = "Dine", // "Dine" or "Desserts"
    val showResetConfirmation: Boolean = false,
    val showAcceptConfirmation: Boolean = false,
    val searchJournalQuery: String = "",
    val journalSortBy: JournalSort = JournalSort.DATE_DESC,
    val directorySearchQuery: String = "",
    val directorySelectedCategory: String = "All" // "All", "Dine", "Desserts"
)

enum class JournalSort {
    DATE_DESC, RATING_DESC, NAME_ASC
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ShopRepository
    
    // UI state flows
    val allShops = MutableStateFlow<List<Shop>>(emptyList())
    
    private val _appState = MutableStateFlow(AppState())
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    private val _gachaState = MutableStateFlow<GachaState>(GachaState.Idle)
    val gachaState: StateFlow<GachaState> = _gachaState.asStateFlow()

    // Transient display shop during rolling animation
    private val _rollingDisplayShop = MutableStateFlow<Shop?>(null)
    val rollingDisplayShop: StateFlow<Shop?> = _rollingDisplayShop.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ShopRepository(database.shopDao())

        viewModelScope.launch {
            // Seed database if empty
            repository.checkAndSeedDatabase()
            
            // Connect reactive shops flow
            repository.allShops.collect { shops ->
                allShops.value = shops
            }
        }
    }

    // Toggle Tab
    fun setTab(tab: ActiveTab) {
        _appState.update { it.copy(currentTab = tab) }
    }

    // Filter Category
    fun setCategory(category: String) {
        _appState.update { it.copy(selectedCategory = category) }
        // Reset raw draw if we switch categories so they don't see old success
        _gachaState.value = GachaState.Idle
        _rollingDisplayShop.value = null
    }

    // Sort or filter journal search
    fun setJournalSearch(query: String) {
        _appState.update { it.copy(searchJournalQuery = query) }
    }

    fun setJournalSort(sort: JournalSort) {
        _appState.update { it.copy(journalSortBy = sort) }
    }

    // Active pool derived helper
    fun getActivePool(category: String): List<Shop> {
        return allShops.value.filter { it.category == category && !it.visited }
    }

    // Roll Gacha with Slot-Machine deceleration animation
    fun rollGacha() {
        val category = _appState.value.selectedCategory
        val pool = getActivePool(category)
        
        if (pool.isEmpty()) {
            // Pool is empty! No unvisited shops left in this category.
            // Screen handles this by informing them they've hit 100% exploration
            return
        }

        viewModelScope.launch {
            _gachaState.value = GachaState.Rolling
            _rollingDisplayShop.value = null

            // Roll feel transitions (decelerating)
            val rollInteractions = 8
            var currentDelay = 80L
            val candidateShops = pool.shuffled()

            for (i in 0 until rollInteractions) {
                // Pick a random shop from candidates (different than last if possible)
                val randomShop = candidateShops[Random.nextInt(candidateShops.size)]
                _rollingDisplayShop.value = randomShop
                delay(currentDelay)
                currentDelay = (currentDelay * 1.3).toLong() // Slower each step
            }

            // Land on final drawn item
            val finalShop = pool[Random.nextInt(pool.size)]
            _rollingDisplayShop.value = null
            _gachaState.value = GachaState.Success(finalShop)
        }
    }

    // Decisions Matrix Actions
    fun reroll() {
        rollGacha()
    }

    fun requestAccept() {
        _appState.update { it.copy(showAcceptConfirmation = true) }
    }

    fun cancelAccept() {
        _appState.update { it.copy(showAcceptConfirmation = false) }
    }

    fun confirmAccept(shop: Shop) {
        viewModelScope.launch {
            // Record visit details
            val updatedShop = shop.copy(
                visited = true,
                visitedTimestamp = System.currentTimeMillis()
            )
            repository.updateShop(updatedShop)
            // Dimiss dialog & Reset draw state back to Idle after transition
            _appState.update { it.copy(showAcceptConfirmation = false) }
            _gachaState.value = GachaState.Idle
        }
    }

    // Update journal item rating
    fun updateShopRating(shop: Shop, rating: Double) {
        viewModelScope.launch {
            val updatedShop = shop.copy(userRating = rating)
            repository.updateShop(updatedShop)
        }
    }

    // Mall Directory specific actions
    fun setDirectorySearch(query: String) {
        _appState.update { it.copy(directorySearchQuery = query) }
    }

    fun setDirectoryCategory(category: String) {
        _appState.update { it.copy(directorySelectedCategory = category) }
    }

    fun toggleVisited(shop: Shop) {
        viewModelScope.launch {
            val updatedShop = shop.copy(
                visited = !shop.visited,
                visitedTimestamp = if (!shop.visited) System.currentTimeMillis() else null,
                userRating = if (!shop.visited) shop.userRating else null
            )
            repository.updateShop(updatedShop)
        }
    }

    // Confirm Progress Reset Dialog toggles
    fun showResetConfirmation(show: Boolean) {
        _appState.update { it.copy(showResetConfirmation = show) }
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            repository.resetAllProgress()
            _appState.update { it.copy(showResetConfirmation = false) }
            _gachaState.value = GachaState.Idle
            _rollingDisplayShop.value = null
        }
    }
}
