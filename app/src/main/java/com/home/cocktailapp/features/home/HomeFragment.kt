package com.home.cocktailapp.features.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.home.cocktailapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()



}