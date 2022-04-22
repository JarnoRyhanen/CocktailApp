package com.home.cocktailapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cocktails")
data class Cocktails(
    @PrimaryKey val cocktailId: String,
    val drinkName: String,
    val drinkInstructions: String?,
    val drinkImageUrl: String?,
    val drinkCategory: String?,

    val isAlcoholic: Boolean?,
    val isFavourited: Boolean,
    val timeStamp: Long = System.currentTimeMillis(),

    val drinkIngredient1: String?,
    val drinkIngredient2: String?,
    val drinkIngredient3: String?,
    val drinkIngredient4: String?,
    val drinkIngredient5: String?,
    val drinkIngredient6: String?,
    val drinkIngredient7: String?,
    val drinkIngredient8: String?,
    val drinkIngredient9: String?,
    val drinkIngredient10: String?,
    val drinkIngredient11: String?,
    val drinkIngredient12: String?,
    val drinkIngredient13: String?,
    val drinkIngredient14: String?,
    val drinkIngredient15: String?,

    val drinkMeasure1: String?,
    val drinkMeasure2: String?,
    val drinkMeasure3: String?,
    val drinkMeasure4: String?,
    val drinkMeasure5: String?,
    val drinkMeasure6: String?,
    val drinkMeasure7: String?,
    val drinkMeasure8: String?,
    val drinkMeasure9: String?,
    val drinkMeasure10: String?,
    val drinkMeasure11: String?,
    val drinkMeasure12: String?,
    val drinkMeasure13: String?,
    val drinkMeasure14: String?,
    val drinkMeasure15: String?
)

@Entity(tableName = "popular_cocktails")
data class PopularCocktails(
    val drinkId: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

@Entity(tableName = "random_cocktails")
data class RandomCocktails(
    val drinkId: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

@Entity(tableName = "latest_cocktails")
data class LatestCocktails(
    val drinkId: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)