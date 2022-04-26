package com.home.cocktailapp.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.cocktailapp.data.CocktailFilter
import com.home.cocktailapp.data.Cocktails
import com.home.cocktailapp.data.repositories.CocktailsRepository
import com.home.cocktailapp.data.PreferencesManager
import com.home.cocktailapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CocktailsRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val preferencesFlow = preferencesManager.filterPreferencesFlow

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    private val refreshTriggerChannel = Channel<Refresh>()
    private val refreshTrigger = refreshTriggerChannel.receiveAsFlow()

    var pendingScrollToTopAfterRefresh = false

    val drinksByQuery = refreshTrigger.flatMapLatest { refresh ->
        repository.getDrinksByFilter(
            refresh == Refresh.FORCE,
            preferencesFlow,
            onFetchFailed = { t ->
                viewModelScope.launch { eventChannel.send((Event.ShowErrorMessage(t))) }
            },
            onFetchSuccess = {
                pendingScrollToTopAfterRefresh = true
            })
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun onFilterItemSelected(cocktailFilter: CocktailFilter) = viewModelScope.launch {
        preferencesManager.updateCocktailFilter(cocktailFilter)
    }

    init {
        viewModelScope.launch {
            repository.deleteNonFavoritedCocktailsOlderThan(
                System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
            )
        }
    }

    fun normalRefresh() {
        if (drinksByQuery.value !is Resource.Loading) {
            viewModelScope.launch {
                refreshTriggerChannel.send(Refresh.NORMAL)
            }
        }
    }

    fun onManualRefresh() {
        if (drinksByQuery.value !is Resource.Loading) {
            viewModelScope.launch {
                refreshTriggerChannel.send(Refresh.FORCE)
            }
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