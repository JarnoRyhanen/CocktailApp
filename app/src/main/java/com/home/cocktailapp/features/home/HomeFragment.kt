package com.home.cocktailapp.features.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.home.cocktailapp.R
import com.home.cocktailapp.databinding.FragmentHomeBinding
import com.home.cocktailapp.shared.CocktailListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentHomeBinding.bind(view)

        val cocktailAdapter = CocktailListAdapter()

        binding.apply {
            recyclerView.apply {
                adapter = cocktailAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.mostPopularCocktails.collect { cocktails ->
                    cocktailAdapter.submitList(cocktails)
                }
            }
        }
    }
}