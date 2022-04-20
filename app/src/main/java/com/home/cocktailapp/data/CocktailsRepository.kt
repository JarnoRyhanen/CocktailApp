package com.home.cocktailapp.data

import com.home.cocktailapp.api.CocktailApi
import javax.inject.Inject


class CocktailsRepository @Inject constructor(
    private val api: CocktailApi,
    private val database: CocktailDatabase,
) {

    private val cocktailDao = database.cocktailDao()


}