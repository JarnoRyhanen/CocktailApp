package com.home.cocktailapp.data

import android.util.Log
import androidx.room.withTransaction
import com.home.cocktailapp.api.CocktailApi
import com.home.cocktailapp.util.Resource
import com.home.cocktailapp.util.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject


private const val TAG = "CocktailsRepository"

class CocktailsRepository @Inject constructor(
    private val api: CocktailApi,
    private val database: CocktailDatabase,
) {

    private val cocktailDao = database.cocktailDao()

    fun getDrinksByQuery(
        preferencesFlow: Flow<FilterPreferences>
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
                val cocktails = serverCocktails.map { serverCocktail ->
                    Cocktails(
                        cocktailId = serverCocktail.idDrink,
                        drinkName = serverCocktail.strDrink,
                        drinkInstructions = serverCocktail.strInstructions,
                        drinkImageUrl = serverCocktail.strDrinkThumb,
                        drinkCategory = serverCocktail.category,

                        isFavourited = false,
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
            }
        )
}
