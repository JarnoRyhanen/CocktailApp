package com.home.cocktailapp.api

import com.home.cocktailapp.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface CocktailApi {

    companion object {
        const val BASE_URL = "https://the-cocktail-db.p.rapidapi.com/"
        const val API_KEY = BuildConfig.COCKTAIL_API_KEY
    }

    //  returns full drink details
    @Headers("X-RapidAPI-Host: the-cocktail-db.p.rapidapi.com", "X-RapidAPI-Key: $API_KEY")
    @GET("{query}.php")
    suspend fun getDrinksByQuery(
        @Path("query") query: String
    ): CocktailResponse

    // search drinks returns full drink details
    @Headers("X-RapidAPI-Host: the-cocktail-db.p.rapidapi.com", "X-RapidAPI-Key: $API_KEY")
    @GET("search.php")
    suspend fun searchDrinks(
        @Query("s") query: String
    ): CocktailResponse

    // search ingredient info returns full ingredient details
    @Headers("X-RapidAPI-Host: the-cocktail-db.p.rapidapi.com", "X-RapidAPI-Key: $API_KEY")
    @GET("search.php")
    suspend fun searchIngredientInfo(
        @Query("i") query: String
    ) : CocktailIngredientResponse

    // search drinks by ingredient returns only 3 drink values: strDrink, strDrinkThumb and idDrink
    //todo make new response class and data class if necessary
    @Headers("X-RapidAPI-Host: the-cocktail-db.p.rapidapi.com", "X-RapidAPI-Key: $API_KEY")
    @GET("filter.php")
    suspend fun searchDrinksByIngredient(
        @Query("i") query: String
    ): CocktailResponse

}