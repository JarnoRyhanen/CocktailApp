package com.home.cocktailapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.home.cocktailapp.data.*

@Database(
    entities = [Cocktails::class, PopularCocktails::class,
        RandomCocktails::class, LatestCocktails::class, SearchResult::class,
        Ingredient::class, IngredientSearchResult::class],
    version = 2
)
abstract class CocktailDatabase : RoomDatabase() {

    abstract fun cocktailDao(): CocktailDao
    abstract fun ingredientDao(): IngredientDao
}