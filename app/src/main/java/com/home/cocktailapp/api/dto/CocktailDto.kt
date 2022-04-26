package com.home.cocktailapp.api.dto

import com.home.cocktailapp.data.Cocktails

data class CocktailDto(
    val idDrink: String,
    val strDrink: String,
    val strInstructions: String,
    val strDrinkThumb: String,
    val category: String,
    val strAlcoholic: String,

    val strIngredient1: String,
    val strIngredient2: String?,
    val strIngredient3: String?,
    val strIngredient4: String?,
    val strIngredient5: String?,
    val strIngredient6: String?,
    val strIngredient7: String?,
    val strIngredient8: String?,
    val strIngredient9: String?,
    val strIngredient10: String?,
    val strIngredient11: String?,
    val strIngredient12: String?,
    val strIngredient13: String?,
    val strIngredient14: String?,
    val strIngredient15: String?,

    val strMeasure1: String,
    val strMeasure2: String?,
    val strMeasure3: String?,
    val strMeasure4: String?,
    val strMeasure5: String?,
    val strMeasure6: String?,
    val strMeasure7: String?,
    val strMeasure8: String?,
    val strMeasure9: String?,
    val strMeasure10: String?,
    val strMeasure11: String?,
    val strMeasure12: String?,
    val strMeasure13: String?,
    val strMeasure14: String?,
    val strMeasure15: String?
)

fun CocktailDto.toCocktails(isFavorited: Boolean): Cocktails {

   return Cocktails(
        cocktailId = idDrink,
        drinkName = strDrink,
        drinkInstructions = strInstructions,
        drinkImageUrl = strDrinkThumb,
        drinkCategory = category,
        isFavourited = isFavorited,
        isAlcoholic = strAlcoholic == "Alcoholic",

        drinkIngredient1 = strIngredient1,
        drinkIngredient2 = strIngredient2,
        drinkIngredient3 = strIngredient3,
        drinkIngredient4 = strIngredient4,
        drinkIngredient5 = strIngredient5,
        drinkIngredient6 = strIngredient6,
        drinkIngredient7 = strIngredient7,
        drinkIngredient8 = strIngredient8,
        drinkIngredient9 = strIngredient9,
        drinkIngredient10 = strIngredient10,
        drinkIngredient11 = strIngredient11,
        drinkIngredient12 = strIngredient12,
        drinkIngredient13 = strIngredient13,
        drinkIngredient14 = strIngredient14,
        drinkIngredient15 = strIngredient15,

        drinkMeasure1 = strMeasure1,
        drinkMeasure2 = strMeasure2,
        drinkMeasure3 = strMeasure3,
        drinkMeasure4 = strMeasure4,
        drinkMeasure5 = strMeasure5,
        drinkMeasure6 = strMeasure6,
        drinkMeasure7 = strMeasure7,
        drinkMeasure8 = strMeasure8,
        drinkMeasure9 = strMeasure9,
        drinkMeasure10 = strMeasure10,
        drinkMeasure11 = strMeasure11,
        drinkMeasure12 = strMeasure12,
        drinkMeasure13 = strMeasure13,
        drinkMeasure14 = strMeasure14,
        drinkMeasure15 = strMeasure15
    )
}