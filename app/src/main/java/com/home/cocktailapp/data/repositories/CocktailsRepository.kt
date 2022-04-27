package com.home.cocktailapp.data.repositories

import android.util.Log
import androidx.room.withTransaction
import com.google.gson.JsonSyntaxException
import com.home.cocktailapp.api.CocktailApi
import com.home.cocktailapp.api.dto.toCocktails
import com.home.cocktailapp.data.*
import com.home.cocktailapp.data.database.CocktailDatabase
import com.home.cocktailapp.util.Resource
import com.home.cocktailapp.util.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CocktailsRepository @Inject constructor(
    private val api: CocktailApi,
    private val database: CocktailDatabase,
) {

    private val cocktailDao = database.cocktailDao()

    fun getDrinksByFilter(
        forceRefresh: Boolean,
        preferencesFlow: Flow<FilterPreferences>,
        onFetchFailed: (Throwable) -> Unit,
        onFetchSuccess: () -> Unit
    ): Flow<Resource<List<Cocktails>>> =
        networkBoundResource(
            query = {
                val drinksByQuery = preferencesFlow.flatMapLatest { filterPreference ->
                    cocktailDao.getDrinks(filterPreference.cocktailFilter)
                }
                drinksByQuery
            },
            fetch = {
                val response = api.getDrinksByQuery(preferencesFlow.first().cocktailFilter.name.lowercase())
                response.drinks
            },
            saveFetchResult = { serverCocktails ->
                if (!serverCocktails.isNullOrEmpty()) {
                    val favoritedCocktails = cocktailDao.getAllFavoritedCocktails().first()
                    val cocktails = serverCocktails.map { serverCocktail ->
                        val isFavorited = favoritedCocktails.any { favoritedCocktail ->
                            favoritedCocktail.cocktailId == serverCocktail.idDrink
                        }
                        serverCocktail.toCocktails(isFavorited)
                    }

                    val filteredDrinks = serverCocktails.map { cocktail ->
                        when (preferencesFlow.first().cocktailFilter.name.lowercase()) {
                            "latest" -> LatestCocktails(drinkId = cocktail.idDrink)
                            "popular" -> PopularCocktails(drinkId = cocktail.idDrink)
                            "randomselection" -> RandomCocktails(drinkId = cocktail.idDrink)
                            else -> {}
                        }
                    }

                    database.withTransaction {
                        cocktails.let { cocktailDao.insertCocktails(it) }
                        filteredDrinks.let {
                            cocktailDao.insertFilteredDrinks(
                                preferencesFlow.first().cocktailFilter,
                                it
                            )
                        }
                    }
                }
            },
            shouldFetch = { cachedCocktails ->
                if (forceRefresh) {
                    true
                } else {
                    val sortedCocktails = cachedCocktails.sortedBy { cocktail ->
                        cocktail.timeStamp
                    }
                    val oldestTimeStamp = sortedCocktails.firstOrNull()?.timeStamp
                    val needsRefresh = oldestTimeStamp == null ||
                            oldestTimeStamp < System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1)
                    needsRefresh
                }
            },
            onFetchFailed = { t ->
                if (t !is HttpException && t !is IOException) {
                    throw t
                }
                onFetchFailed(t)
            },
            onFetchSuccess = onFetchSuccess
        )

    fun getSearchResults(
        forceRefresh: Boolean,
        searchQuery: MutableStateFlow<String?>,
        searchQueryTypeFlow: Flow<SearchQueryTypePreferences>,
        onFetchFailed: (Throwable) -> Unit,
        onFetchSuccess: () -> Unit
    ): Flow<Resource<List<Cocktails>>> = networkBoundResource(
        query = {
            val searchedCocktails = searchQuery.flatMapLatest { query ->
                cocktailDao.getSearchResultCocktails(query.orEmpty())
            }
            searchedCocktails
        },
        fetch = {
            val response = when (searchQueryTypeFlow.first().searchQueryType) {
                SearchQueryType.SEARCH_COCKTAILS -> {
                    searchQuery.value?.let { api.searchDrinks(it) }?.drinks
                }
                SearchQueryType.SEARCH_COCKTAILS_BY_INGREDIENT -> {
                    searchQuery.value?.let { api.searchDrinksByIngredient(it) }?.drinks
                }
            }
            response
        },
        saveFetchResult = { response ->
            if (!response.isNullOrEmpty()) {
                Log.d("tag", "getSearchResults:    the search query was new")
                val favoritedCocktails = cocktailDao.getAllFavoritedCocktails().first()
                val cocktails = response.map { serverCocktail ->
                    val isFavorited = favoritedCocktails.any { favoritedCocktail ->
                        favoritedCocktail.cocktailId == serverCocktail.idDrink
                    }
                    if (searchQueryTypeFlow.first().searchQueryType == SearchQueryType.SEARCH_COCKTAILS_BY_INGREDIENT) {
                        insertCocktailById(serverCocktail.idDrink, isFavorited)
                    } else {
                        serverCocktail.toCocktails(isFavorited)
                    }
                }

                val searchResults = response.map {
                    val cocktail = it
                    searchQuery.value?.let { searchQuery ->
                        SearchResult(
                            searchQuery,
                            drinkId = cocktail.idDrink
                        )
                    }
                }
                database.withTransaction {
                    searchQuery.value?.let { cocktailDao.deleteSearchResultsForQuery(it) }
                    cocktailDao.insertCocktails(cocktails)
                    searchResults.let { results ->
                        cocktailDao.insertSearchResults(results as List<SearchResult>)
                    }
                }
            }
        },
        shouldFetch = { cachedCocktails ->
            if (forceRefresh) {
                true
            } else {
                val sortedCocktails = cachedCocktails.sortedBy { cocktail ->
                    cocktail.timeStamp
                }
                val oldestTimestamp = sortedCocktails.firstOrNull()?.timeStamp
                val needsRefresh = oldestTimestamp == null ||
                        oldestTimestamp < System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1)
                needsRefresh
            }
        },
        onFetchFailed = { t ->
            if (t !is HttpException && t !is IOException
                && t !is JsonSyntaxException
            ) {
                throw t
            }
            onFetchFailed(t)
        },
        onFetchSuccess = onFetchSuccess
    )

    fun getAllFavoritedCocktails(): Flow<List<Cocktails>> =
        cocktailDao.getAllFavoritedCocktails()

    suspend fun updateCocktail(cocktail: Cocktails) {
        cocktailDao.updateCocktail(cocktail)
    }

    suspend fun resetAllFavorites() {
        cocktailDao.resetAllFavorites()
    }

    suspend fun deleteNonFavoritedCocktailsOlderThan(timestamp: Long) {
        cocktailDao.deleteNonFavoritedCocktailsOlderThan(timestamp)
    }

    private suspend fun insertCocktailById(id: String, isFavorited: Boolean): Cocktails {
        val response = api.getCocktailByID(id).drinks
        val serverCocktail = response?.map { serverCocktail ->
            serverCocktail.toCocktails(isFavorited)
        }
        return (serverCocktail?.first() ?: serverCocktail) as Cocktails
    }
}
