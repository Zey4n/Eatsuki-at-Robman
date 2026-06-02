package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "shops")
data class Shop(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String, // "Dine" or "Desserts"
    val level: String,
    val priceRange: String,
    val description: String,
    val baseRating: Double,
    val visited: Boolean = false,
    val userRating: Double? = null, // Custom user rating out of 5.0 or 10.0
    val visitedTimestamp: Long? = null
) : Serializable
