package com.home.cocktailapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "ingredients")
data class Ingredient(
    @PrimaryKey val ingredientId: String,
    val ingredientName: String,
    val description: String? = null
)

@Entity(tableName = "ingredient_search_result", primaryKeys = ["searchQuery","idIngredient"])
data class IngredientSearchResult(
    val searchQuery: String,
    val idIngredient: String
)

