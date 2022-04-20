package com.home.cocktailapp.features.home

import androidx.lifecycle.ViewModel
import com.home.cocktailapp.data.CocktailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CocktailsRepository
) : ViewModel() {



}