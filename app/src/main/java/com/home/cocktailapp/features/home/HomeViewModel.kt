package com.home.cocktailapp.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.cocktailapp.data.CocktailFilter
import com.home.cocktailapp.data.CocktailsRepository
import com.home.cocktailapp.data.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CocktailsRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val preferencesFlow = preferencesManager.preferencesFlow

    val drinksByQuery = repository.getDrinksByQuery(preferencesFlow)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun onFilterItemSelected(cocktailFilter: CocktailFilter) = viewModelScope.launch {
        preferencesManager.updateCocktailFilter(cocktailFilter)
    }
}