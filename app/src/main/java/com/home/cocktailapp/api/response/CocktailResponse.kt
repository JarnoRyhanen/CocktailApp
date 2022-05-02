package com.home.cocktailapp.api.response

import com.home.cocktailapp.api.dto.CocktailDto

data class CocktailResponse(
    val drinks: List<CocktailDto>?
)