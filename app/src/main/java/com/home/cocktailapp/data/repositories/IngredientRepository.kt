package com.home.cocktailapp.data.repositories

import androidx.room.withTransaction
import com.home.cocktailapp.api.CocktailApi
import com.home.cocktailapp.api.dto.toIngredient
import com.home.cocktailapp.data.Ingredient
import com.home.cocktailapp.data.IngredientSearchResult
import com.home.cocktailapp.data.database.CocktailDatabase
import com.home.cocktailapp.util.Resource
import com.home.cocktailapp.util.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class IngredientRepository @Inject constructor(
    private val api: CocktailApi,
    private val database: CocktailDatabase
) {

    private val ingredientDao = database.ingredientDao()

    fun getIngredients(
        forceRefresh: Boolean,
        searchQuery: MutableStateFlow<String>
    ): Flow<Resource<List<Ingredient>>> =
        networkBoundResource(
            query = {
                val ingredient = searchQuery.flatMapLatest {
                    ingredientDao.getIngredientByQuery(it)
                }
                ingredient
            },
            fetch = {
                val response = api.searchIngredientInfo(searchQuery.value)
                response.ingredients
            },
            saveFetchResult = { serverIngredient ->
                if (!serverIngredient.isNullOrEmpty()) {
                    val ingredient = serverIngredient.map {
                        it.toIngredient()
                    }
                    val searchResult = serverIngredient.map {
                        IngredientSearchResult(
                            searchQuery.value, idIngredient = it.idIngredient
                        )
                    }
                    database.withTransaction {
                        ingredientDao.deleteSearchResultForQuery(searchQuery.value)
                        ingredientDao.insertIngredient(ingredient)
                        ingredientDao.insertSearchIngredientResults(searchResult)
                    }
                }
            },
            shouldFetch = {
                forceRefresh
            }
        )
}