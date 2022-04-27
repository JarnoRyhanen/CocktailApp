package com.home.cocktailapp.features.search.ingredients

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.cocktailapp.data.repositories.IngredientRepository
import com.home.cocktailapp.features.search.cocktails.SearchViewModel
import com.home.cocktailapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchIngredientsViewModel @Inject constructor(
    private val repository: IngredientRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")

    private val refreshTriggerChannel = Channel<SearchViewModel.Refresh>()
    private val refreshTrigger = refreshTriggerChannel.receiveAsFlow()

    val ingredient = refreshTrigger.flatMapLatest {
        repository.getIngredients(
            it == SearchViewModel.Refresh.FORCE,
            searchQuery
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun updateSearchQuery(query: String) {
        Log.d("viewmodel", "updateSearchQuery: ${ingredient.value}")
        searchQuery.value = query
    }

    fun onManualRefresh() {
        if (ingredient.value !is Resource.Loading) {
            viewModelScope.launch {
                refreshTriggerChannel.send(SearchViewModel.Refresh.FORCE)
            }
        }
    }
}

