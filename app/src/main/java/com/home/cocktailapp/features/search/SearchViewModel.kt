package com.home.cocktailapp.features.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.cocktailapp.data.Cocktails
import com.home.cocktailapp.data.CocktailsRepository
import com.home.cocktailapp.data.PreferencesManager
import com.home.cocktailapp.data.SearchQueryType
import com.home.cocktailapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SearchViewModel"

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: CocktailsRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")

    var pendingScrollToTopAfterRefresh = false

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    private val refreshTriggerChannel = Channel<Refresh>()
    private val refreshTrigger = refreshTriggerChannel.receiveAsFlow()

    private val searchQueryTypeFlow = preferencesManager.searchQueryTypeFlow

    val searchResults = refreshTrigger.flatMapLatest { refresh ->
        repository.getSearchResults(
            refresh == Refresh.FORCE,
            searchQuery,
            searchQueryTypeFlow,
            onFetchFailed = { t ->
                viewModelScope.launch { eventChannel.send(Event.ShowErrorMessage(t)) }
            },
            onFetchSuccess = {
                pendingScrollToTopAfterRefresh = true
            })
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun onSearchQuerySubmit(query: String) {
        searchQuery.value = query
    }

    fun onNormalRefresh() {
        if (searchResults.value !is Resource.Loading) {
            viewModelScope.launch {
                refreshTriggerChannel.send(Refresh.NORMAL)
            }
        }
    }

    fun onManualRefresh() {
        if (searchResults.value !is Resource.Loading) {
            viewModelScope.launch {
                refreshTriggerChannel.send(Refresh.FORCE)
            }
        }
    }

    fun onSearchQueryTypeSelected(searchQueryType: SearchQueryType) {
        viewModelScope.launch {
            preferencesManager.updateSearchQueryType(searchQueryType)
            Log.d(TAG, "onSearchQueryTypeSelected: ")
        }
    }

    fun onFavoriteClick(cocktail: Cocktails) {
        val currentlyFavorited = cocktail.isFavourited
        val updatedCocktail = cocktail.copy(isFavourited = !currentlyFavorited)
        viewModelScope.launch {
            repository.updateCocktail(updatedCocktail)
        }
    }

    enum class Refresh {
        FORCE, NORMAL
    }

    sealed class Event {
        data class ShowErrorMessage(val error: Throwable) : Event()
    }
}