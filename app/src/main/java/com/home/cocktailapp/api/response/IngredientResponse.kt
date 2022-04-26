package com.home.cocktailapp.api.response

import com.home.cocktailapp.api.dto.IngredientDto

data class IngredientResponse(
    val ingredients: List<IngredientDto>
)
