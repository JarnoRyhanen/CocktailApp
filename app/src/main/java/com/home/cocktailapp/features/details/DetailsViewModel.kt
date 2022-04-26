package com.home.cocktailapp.features.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.cocktailapp.data.Cocktails
import com.home.cocktailapp.data.CocktailsRepository
import com.home.cocktailapp.data.PreferencesManager
import com.home.cocktailapp.data.SearchQueryType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: CocktailsRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    suspend fun getCocktailById(id: String): Cocktails {
       return repository.getCocktailById(id)
    }

    suspend fun insertCocktailById(id: String) {
        repository.insertCocktailById(id)
    }

    suspend fun getPreferences(): SearchQueryType {
        return preferencesManager.searchQueryTypeFlow.first().searchQueryType
    }
}