package com.home.cocktailapp.api.response

import com.home.cocktailapp.api.dto.CocktailIngredientDto

data class CocktailIngredientResponse(
    val ingredients: List<CocktailIngredientDto>
)
