package com.home.cocktailapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CocktailDao {

    @Query("SELECT * FROM most_popular_drinks INNER JOIN cocktails ON drinkId = id")
    fun getAllPopularDrinks(): Flow<List<Cocktails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCocktails(cocktails: List<Cocktails>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMostPopularDrinks(cocktails: List<Cocktails>)

    @Query("DELETE FROM most_popular_drinks")
    suspend fun deleteAllPopularDrinks()

}