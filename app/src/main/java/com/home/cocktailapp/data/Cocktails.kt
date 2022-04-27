package com.home.cocktailapp.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "cocktails")
@Parcelize
data class Cocktails(
    @PrimaryKey val cocktailId: String,
    val drinkName: String?,
    val drinkInstructions: String? = null,
    val drinkImageUrl: String?,
    val drinkCategory: String? = null,

    val isAlcoholic: Boolean? = null,
    val isFavourited: Boolean,
    val timeStamp: Long = System.currentTimeMillis(),

    val drinkIngredient1: String? = null,
    val drinkIngredient2: String? = null,
    val drinkIngredient3: String? = null,
    val drinkIngredient4: String? = null,
    val drinkIngredient5: String? = null,
    val drinkIngredient6: String? = null,
    val drinkIngredient7: String? = null,
    val drinkIngredient8: String? = null,
    val drinkIngredient9: String? = null,
    val drinkIngredient10: String? = null,
    val drinkIngredient11: String? = null,
    val drinkIngredient12: String? = null,
    val drinkIngredient13: String? = null,
    val drinkIngredient14: String? = null,
    val drinkIngredient15: String? = null,

    val drinkMeasure1: String? = null,
    val drinkMeasure2: String? = null,
    val drinkMeasure3: String? = null,
    val drinkMeasure4: String? = null,
    val drinkMeasure5: String? = null,
    val drinkMeasure6: String? = null,
    val drinkMeasure7: String? = null,
    val drinkMeasure8: String? = null,
    val drinkMeasure9: String? = null,
    val drinkMeasure10: String? = null,
    val drinkMeasure11: String? = null,
    val drinkMeasure12: String? = null,
    val drinkMeasure13: String? = null,
    val drinkMeasure14: String? = null,
    val drinkMeasure15: String? = null
) : Parcelable

@Entity(tableName = "search_results", primaryKeys = ["searchQuery","drinkId"])
data class SearchResult(
    val searchQuery: String,
    val drinkId: String
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