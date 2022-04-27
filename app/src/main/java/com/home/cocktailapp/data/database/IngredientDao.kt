package com.home.cocktailapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.home.cocktailapp.data.Ingredient
import com.home.cocktailapp.data.IngredientSearchResult
import com.home.cocktailapp.data.SearchResult
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {

    @Query("SELECT * FROM ingredient_search_result INNER JOIN ingredients on " +
            "ingredientId = idIngredient WHERE searchQuery = :query")
    fun getIngredientByQuery(query: String): Flow<List<Ingredient>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: List<Ingredient>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchIngredientResults(searchResult: List<IngredientSearchResult>)

    @Query("DELETE FROM ingredient_search_result WHERE searchQuery = :query")
    suspend fun deleteSearchResultForQuery(query: String)

}