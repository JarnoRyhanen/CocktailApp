package com.home.cocktailapp.data

import androidx.room.withTransaction
import com.google.gson.JsonSyntaxException
import com.home.cocktailapp.api.CocktailApi
import com.home.cocktailapp.util.Resource
import com.home.cocktailapp.util.exhaustive
import com.home.cocktailapp.util.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "CocktailsRepository"

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

                val favoritedCocktails = cocktailDao.getAllFavoritedCocktails().first()

                val cocktails = serverCocktails.map { serverCocktail ->

                    val isFavorited = favoritedCocktails.any { favoritedCocktail ->
                        favoritedCocktail.cocktailId == serverCocktail.idDrink
                    }

                    Cocktails(
                        cocktailId = serverCocktail.idDrink,
                        drinkName = serverCocktail.strDrink,
                        drinkInstructions = serverCocktail.strInstructions,
                        drinkImageUrl = serverCocktail.strDrinkThumb,
                        drinkCategory = serverCocktail.category,

                        isFavourited = isFavorited,
                        isAlcoholic = serverCocktail.strAlcoholic == "Alcoholic",

                        drinkIngredient1 = serverCocktail.strIngredient1,
                        drinkIngredient2 = serverCocktail.strIngredient2,
                        drinkIngredient3 = serverCocktail.strIngredient3,
                        drinkIngredient4 = serverCocktail.strIngredient4,
                        drinkIngredient5 = serverCocktail.strIngredient5,
                        drinkIngredient6 = serverCocktail.strIngredient6,
                        drinkIngredient7 = serverCocktail.strIngredient7,
                        drinkIngredient8 = serverCocktail.strIngredient8,
                        drinkIngredient9 = serverCocktail.strIngredient9,
                        drinkIngredient10 = serverCocktail.strIngredient10,
                        drinkIngredient11 = serverCocktail.strIngredient11,
                        drinkIngredient12 = serverCocktail.strIngredient12,
                        drinkIngredient13 = serverCocktail.strIngredient13,
                        drinkIngredient14 = serverCocktail.strIngredient14,
                        drinkIngredient15 = serverCocktail.strIngredient15,

                        drinkMeasure1 = serverCocktail.strMeasure1,
                        drinkMeasure2 = serverCocktail.strMeasure2,
                        drinkMeasure3 = serverCocktail.strMeasure3,
                        drinkMeasure4 = serverCocktail.strMeasure4,
                        drinkMeasure5 = serverCocktail.strMeasure5,
                        drinkMeasure6 = serverCocktail.strMeasure6,
                        drinkMeasure7 = serverCocktail.strMeasure7,
                        drinkMeasure8 = serverCocktail.strMeasure8,
                        drinkMeasure9 = serverCocktail.strMeasure9,
                        drinkMeasure10 = serverCocktail.strMeasure10,
                        drinkMeasure11 = serverCocktail.strMeasure11,
                        drinkMeasure12 = serverCocktail.strMeasure12,
                        drinkMeasure13 = serverCocktail.strMeasure13,
                        drinkMeasure14 = serverCocktail.strMeasure14,
                        drinkMeasure15 = serverCocktail.strMeasure15
                    )
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
                    cocktailDao.insertCocktails(cocktails)
                    cocktailDao.insertFilteredDrinks(
                        preferencesFlow.first().cocktailFilter,
                        filteredDrinks
                    )
                }
            },
            shouldFetch =  { cachedCocktails ->
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
        searchQuery: MutableStateFlow<String>,
        searchQueryTypeFlow: Flow<SearchQueryTypePreferences>,
        onFetchFailed: (Throwable) -> Unit,
        onFetchSuccess: () -> Unit
    ): Flow<Resource<List<Cocktails>>> = networkBoundResource(
        query = {
            val searchedCocktails = searchQuery.flatMapLatest { query ->
                cocktailDao.getSearchResultCocktails(query)
            }
            searchedCocktails
        },
        fetch = {
            when (searchQueryTypeFlow.first().searchQueryType) {
                SearchQueryType.SEARCH_COCKTAILS -> {
                    val response = api.searchDrinks(searchQuery.value)
                    response.drinks
                }
                SearchQueryType.SEARCH_COCKTAILS_BY_INGREDIENT -> {
                    val response = api.searchDrinksByIngredient(searchQuery.value)
                    response.drinks
                }
            }.exhaustive
        },
        saveFetchResult = { response -> // list<CocktailDto>

            val favoritedCocktails = cocktailDao.getAllFavoritedCocktails().first()

            var cocktails: List<Cocktails> = emptyList()

            when (searchQueryTypeFlow.first().searchQueryType) {
                SearchQueryType.SEARCH_COCKTAILS -> {
                    cocktails = response.map { serverCocktail ->
                        val isFavorited = favoritedCocktails.any { favoritedCocktail ->
                            favoritedCocktail.cocktailId == serverCocktail.idDrink
                        }
                        Cocktails(
                            cocktailId = serverCocktail.idDrink,
                            drinkName = serverCocktail.strDrink,
                            drinkInstructions = serverCocktail.strInstructions,
                            drinkImageUrl = serverCocktail.strDrinkThumb,
                            drinkCategory = serverCocktail.category,

                            isFavourited = isFavorited,
                            isAlcoholic = serverCocktail.strAlcoholic == "Alcoholic",

                            drinkIngredient1 = serverCocktail.strIngredient1,
                            drinkIngredient2 = serverCocktail.strIngredient2,
                            drinkIngredient3 = serverCocktail.strIngredient3,
                            drinkIngredient4 = serverCocktail.strIngredient4,
                            drinkIngredient5 = serverCocktail.strIngredient5,
                            drinkIngredient6 = serverCocktail.strIngredient6,
                            drinkIngredient7 = serverCocktail.strIngredient7,
                            drinkIngredient8 = serverCocktail.strIngredient8,
                            drinkIngredient9 = serverCocktail.strIngredient9,
                            drinkIngredient10 = serverCocktail.strIngredient10,
                            drinkIngredient11 = serverCocktail.strIngredient11,
                            drinkIngredient12 = serverCocktail.strIngredient12,
                            drinkIngredient13 = serverCocktail.strIngredient13,
                            drinkIngredient14 = serverCocktail.strIngredient14,
                            drinkIngredient15 = serverCocktail.strIngredient15,

                            drinkMeasure1 = serverCocktail.strMeasure1,
                            drinkMeasure2 = serverCocktail.strMeasure2,
                            drinkMeasure3 = serverCocktail.strMeasure3,
                            drinkMeasure4 = serverCocktail.strMeasure4,
                            drinkMeasure5 = serverCocktail.strMeasure5,
                            drinkMeasure6 = serverCocktail.strMeasure6,
                            drinkMeasure7 = serverCocktail.strMeasure7,
                            drinkMeasure8 = serverCocktail.strMeasure8,
                            drinkMeasure9 = serverCocktail.strMeasure9,
                            drinkMeasure10 = serverCocktail.strMeasure10,
                            drinkMeasure11 = serverCocktail.strMeasure11,
                            drinkMeasure12 = serverCocktail.strMeasure12,
                            drinkMeasure13 = serverCocktail.strMeasure13,
                            drinkMeasure14 = serverCocktail.strMeasure14,
                            drinkMeasure15 = serverCocktail.strMeasure15
                        )
                    }
                }
                SearchQueryType.SEARCH_COCKTAILS_BY_INGREDIENT -> {
                    cocktails = response.map { serverCocktail ->
                        val isFavorited = favoritedCocktails.any { favoritedCocktail ->
                            favoritedCocktail.cocktailId == serverCocktail.idDrink
                        }
                        Cocktails(
                            cocktailId = serverCocktail.idDrink,
                            drinkName = serverCocktail.strDrink,
                            drinkInstructions = serverCocktail.strInstructions,
                            drinkImageUrl = serverCocktail.strDrinkThumb,
                            drinkCategory = serverCocktail.category,

                            isFavourited = isFavorited,
                            isAlcoholic = serverCocktail.strAlcoholic == "Alcoholic",

                            drinkIngredient1 = serverCocktail.strIngredient1,
                            drinkIngredient2 = serverCocktail.strIngredient2,
                            drinkIngredient3 = serverCocktail.strIngredient3,
                            drinkIngredient4 = serverCocktail.strIngredient4,
                            drinkIngredient5 = serverCocktail.strIngredient5,
                            drinkIngredient6 = serverCocktail.strIngredient6,
                            drinkIngredient7 = serverCocktail.strIngredient7,
                            drinkIngredient8 = serverCocktail.strIngredient8,
                            drinkIngredient9 = serverCocktail.strIngredient9,
                            drinkIngredient10 = serverCocktail.strIngredient10,
                            drinkIngredient11 = serverCocktail.strIngredient11,
                            drinkIngredient12 = serverCocktail.strIngredient12,
                            drinkIngredient13 = serverCocktail.strIngredient13,
                            drinkIngredient14 = serverCocktail.strIngredient14,
                            drinkIngredient15 = serverCocktail.strIngredient15,

                            drinkMeasure1 = serverCocktail.strMeasure1,
                            drinkMeasure2 = serverCocktail.strMeasure2,
                            drinkMeasure3 = serverCocktail.strMeasure3,
                            drinkMeasure4 = serverCocktail.strMeasure4,
                            drinkMeasure5 = serverCocktail.strMeasure5,
                            drinkMeasure6 = serverCocktail.strMeasure6,
                            drinkMeasure7 = serverCocktail.strMeasure7,
                            drinkMeasure8 = serverCocktail.strMeasure8,
                            drinkMeasure9 = serverCocktail.strMeasure9,
                            drinkMeasure10 = serverCocktail.strMeasure10,
                            drinkMeasure11 = serverCocktail.strMeasure11,
                            drinkMeasure12 = serverCocktail.strMeasure12,
                            drinkMeasure13 = serverCocktail.strMeasure13,
                            drinkMeasure14 = serverCocktail.strMeasure14,
                            drinkMeasure15 = serverCocktail.strMeasure15
                        )
                    }
                }
            }
            val searchResults = response.map { cocktail ->
                SearchResult(searchQuery.value, drinkId = cocktail.idDrink)
            }
            database.withTransaction {
                cocktailDao.deleteSearchResultsForQuery(searchQuery.value)
                cocktailDao.insertCocktails(cocktails)
                cocktailDao.insertSearchResults(searchResults)
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
                && t !is NullPointerException && t !is JsonSyntaxException
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
}
