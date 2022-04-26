package com.home.cocktailapp.features.search.ingredients

import androidx.lifecycle.ViewModel
import com.home.cocktailapp.data.repositories.CocktailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchIngredientsViewModel @Inject constructor(
    private val repository: CocktailsRepository
) : ViewModel() {

}