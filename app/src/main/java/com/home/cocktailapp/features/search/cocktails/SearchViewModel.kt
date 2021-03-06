package com.home.cocktailapp.features.search.cocktails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.cocktailapp.data.Cocktails
import com.home.cocktailapp.data.PreferencesManager
import com.home.cocktailapp.data.SearchQueryType
import com.home.cocktailapp.data.repositories.CocktailsRepository
import com.home.cocktailapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: CocktailsRepository,
    private val preferencesManager: PreferencesManager,
) : ViewModel() {

    private val searchQuery = MutableStateFlow<String?>(null)

    val hasCurrentQuery = searchQuery.map { it != null }

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
                    }
                ) {
                    pendingScrollToTopAfterRefresh = true
                }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    var refreshInProgress = false
    var pendingScrollToTopAfterNewQuery = false
    var newQueryInProgress = false

    fun onSearchQuerySubmit(query: String) {
        searchQuery.value = query
        newQueryInProgress = true
        pendingScrollToTopAfterNewQuery = true
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
        FORCE,
    }

    sealed class Event {
        data class ShowErrorMessage(val error: Throwable) : Event()
    }
}