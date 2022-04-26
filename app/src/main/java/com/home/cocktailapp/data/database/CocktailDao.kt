package com.home.cocktailapp.data.database

import androidx.room.*
import com.home.cocktailapp.data.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CocktailDao {

    fun getDrinks(cocktailFilter: CocktailFilter): Flow<List<Cocktails>> =
        when (cocktailFilter) {
            CocktailFilter.POPULAR -> getDrinksFilteredByPopularity()
            CocktailFilter.LATEST -> getDrinksFilteredByLatest()
            CocktailFilter.RANDOMSELECTION -> getDrinksFilteredByRandom()
        }

    @Query("SELECT * FROM search_results INNER JOIN cocktails on cocktailId = drinkId WHERE searchQuery = :query")
    fun getSearchResultCocktails(query: String): Flow<List<Cocktails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFilteredDrinks(filter: CocktailFilter, cocktails: List<Any>) =
        when (filter) {
            CocktailFilter.POPULAR -> {
                deletePopularCocktails()
                insertPopularCocktails(cocktails as List<PopularCocktails>)
            }
            CocktailFilter.LATEST -> {
                deleteLatestCocktails()
                insertLatestCocktails(cocktails as List<LatestCocktails>)
            }
            CocktailFilter.RANDOMSELECTION -> {
                deleteRandomizedCocktails()
                insertRandomizedCocktails(cocktails as List<RandomCocktails>)
            }
        }

    @Query("SELECT * FROM popular_cocktails INNER JOIN cocktails ON cocktailId = drinkId")
    fun getDrinksFilteredByPopularity(): Flow<List<Cocktails>>

    @Query("SELECT * FROM random_cocktails INNER JOIN cocktails ON cocktailId = drinkId")
    fun getDrinksFilteredByRandom(): Flow<List<Cocktails>>

    @Query("SELECT * FROM latest_cocktails INNER JOIN cocktails ON cocktailId = drinkId")
    fun getDrinksFilteredByLatest(): Flow<List<Cocktails>>

    @Query("SELECT * FROM cocktails WHERE isFavourited = 1")
    fun getAllFavoritedCocktails(): Flow<List<Cocktails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCocktails(cocktails: List<Cocktails>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchResults(searchResult: List<SearchResult>)

    @Update
    suspend fun updateCocktail(cocktail: Cocktails)

    @Query("UPDATE cocktails SET isFavourited = 0")
    suspend fun resetAllFavorites()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPopularCocktails(cocktails: List<PopularCocktails>)

    @Query("DELETE FROM popular_cocktails")
    suspend fun deletePopularCocktails()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRandomizedCocktails(cocktails: List<RandomCocktails>)

    @Query("DELETE FROM random_cocktails")
    suspend fun deleteRandomizedCocktails()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLatestCocktails(cocktails: List<LatestCocktails>)

    @Query("DELETE FROM search_results WHERE searchQuery = :query")
    suspend fun deleteSearchResultsForQuery(query: String)

    @Query("DELETE FROM latest_cocktails")
    suspend fun deleteLatestCocktails()

    @Query("DELETE FROM cocktails WHERE timeStamp < :timestamp AND isFavourited = 0")
    suspend fun deleteNonFavoritedCocktailsOlderThan(timestamp: Long)
}