package com.home.cocktailapp.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.cocktailapp.data.Cocktails
import com.home.cocktailapp.data.CocktailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CocktailsRepository
) : ViewModel() {

    private val mostPopularCocktailsFlow = MutableStateFlow<List<Cocktails>>(emptyList())
    val mostPopularCocktails: Flow<List<Cocktails>> = mostPopularCocktailsFlow

    init {
        viewModelScope.launch {
            val cocktails = repository.getMostPopularDrinks()
            mostPopularCocktailsFlow.value = cocktails
        }
    }

}