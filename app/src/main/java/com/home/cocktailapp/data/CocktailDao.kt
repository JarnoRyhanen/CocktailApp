package com.home.cocktailapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.home.cocktailapp.features.home.HomeViewModel
import kotlinx.coroutines.flow.Flow

@Dao
interface CocktailDao {

    fun getDrinks(cocktailFilter: CocktailFilter): Flow<List<Cocktails>> =
        when (cocktailFilter) {
            CocktailFilter.POPULAR -> getDrinksFilteredByPopularity()
            CocktailFilter.LATEST -> getDrinksFilteredByLatest()
            CocktailFilter.RANDOMSELECTION -> getDrinksFilteredByRandom()
        }

    @Query("SELECT * FROM popular_cocktails INNER JOIN cocktails ON cocktailId = drinkId")
    fun getDrinksFilteredByPopularity(): Flow<List<Cocktails>>

    @Query("SELECT * FROM random_cocktails INNER JOIN cocktails ON cocktailId = drinkId")
    fun getDrinksFilteredByRandom(): Flow<List<Cocktails>>

    @Query("SELECT * FROM latest_cocktails INNER JOIN cocktails ON cocktailId = drinkId")
    fun getDrinksFilteredByLatest(): Flow<List<Cocktails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCocktails(cocktails: List<Cocktails>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPopularCocktails(cocktails: List<PopularCocktails>)

    @Query("DELETE FROM popular_cocktails")
    suspend fun deletePopularDrinks()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRandomizedCocktails(cocktails: List<RandomCocktails>)

    @Query("DELETE FROM random_cocktails")
    suspend fun deleteRandomizedCocktails()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLatestCocktails(cocktails: List<LatestCocktails>)

    @Query("DELETE FROM latest_cocktails")
    suspend fun deleteLatestCocktails()
}