package com.home.cocktailapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Cocktails::class, PopularCocktails::class,
    RandomCocktails::class, LatestCocktails::class, SearchResult::class, SearchResultByIngredient::class],
    version = 3)
abstract class CocktailDatabase : RoomDatabase() {

    abstract fun cocktailDao(): CocktailDao
}