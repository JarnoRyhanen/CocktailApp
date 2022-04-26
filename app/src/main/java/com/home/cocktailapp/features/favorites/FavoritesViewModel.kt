package com.home.cocktailapp.features.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.cocktailapp.data.Cocktails
import com.home.cocktailapp.data.repositories.CocktailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: CocktailsRepository
) : ViewModel() {

    val favorites = repository.getAllFavoritedCocktails()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun onFavoriteClick(cocktail: Cocktails) {
        val currentlyFavorited = cocktail.isFavourited
        val updatedCocktail = cocktail.copy(isFavourited = !currentlyFavorited)
        viewModelScope.launch {
            repository.updateCocktail(updatedCocktail)
        }
    }

    fun onDeleteAllFavorites() {
        viewModelScope.launch {
            repository.resetAllFavorites()
        }
    }
}