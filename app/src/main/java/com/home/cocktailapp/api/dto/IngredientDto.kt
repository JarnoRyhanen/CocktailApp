package com.home.cocktailapp.api.dto

import com.home.cocktailapp.data.Ingredient

data class IngredientDto(
    val idIngredient: String,
    val strIngredient: String,
    val strDescription: String?
)

fun IngredientDto.toIngredient(): Ingredient {
   return Ingredient(
        ingredientId = idIngredient,
        description = strDescription,
        ingredientName = strIngredient
    )
}
