package com.home.cocktailapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Cocktails::class, MostPopularDrinks::class], version = 1)
abstract class CocktailDatabase : RoomDatabase() {

    abstract fun cocktailDao(): CocktailDao
}