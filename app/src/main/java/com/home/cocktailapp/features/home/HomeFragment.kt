package com.home.cocktailapp.features.home

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.home.cocktailapp.R
import com.home.cocktailapp.data.CocktailFilter
import com.home.cocktailapp.databinding.FragmentHomeBinding
import com.home.cocktailapp.shared.CocktailListAdapter
import com.home.cocktailapp.util.Resource
import com.home.cocktailapp.util.exhaustive
import com.home.cocktailapp.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentHomeBinding.bind(view)

        val cocktailAdapter = CocktailListAdapter(
            onItemClick = { cocktails ->
                val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(cocktails)
                findNavController().navigate(action)
            },
            onFavoriteClick = { cocktail ->
                viewModel.onFavoriteClick(cocktail)
            })

        binding.apply {
            recyclerView.apply {
                adapter = cocktailAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                itemAnimator?.changeDuration = 0
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.drinksByQuery.collect {
                    val result = it ?: return@collect
                    Log.d("tag", "onViewCreated: ${result.data?.size}")

                    when (result) {
                        is Resource.Loading -> {
                            buttonRetry.isVisible = false
                            textViewError.isVisible = false
                            swipeRefreshLayout.isRefreshing = true
                        }
                        is Resource.Success -> {
                            buttonRetry.isVisible = false
                            textViewError.isVisible = false
                            swipeRefreshLayout.isRefreshing = false
                            recyclerView.isVisible =
                                cocktailAdapter.itemCount > 0 || !result.data.isNullOrEmpty()
                        }
                        is Resource.Error -> {
                            swipeRefreshLayout.isRefreshing = false
                            recyclerView.isVisible = cocktailAdapter.itemCount > 0
                            
                            val noCachedResults =
                                cocktailAdapter.itemCount < 1 && result.data.isNullOrEmpty()
                            textViewError.isVisible = noCachedResults

                            viewModel.pendingScrollToTopAfterRefresh = false

                            if (viewModel.getPreferences() == CocktailFilter.RANDOMSELECTION) {
                                recyclerView.isVisible = false
                                textViewError.isVisible = true
                                buttonRetry.isVisible = true
                            }
                            val errorMessage = getString(
                                R.string.could_not_load_search_results,
                                result.error?.localizedMessage ?: R.string.unknown_error_occured
                            )
                            textViewError.text = errorMessage
                        }
                    }
                    cocktailAdapter.submitList(result.data) {
                        if (viewModel.pendingScrollToTopAfterRefresh) {
                            recyclerView.scrollToPosition(0)
                            viewModel.pendingScrollToTopAfterRefresh = false
                        }
                    }
                }
            }
            swipeRefreshLayout.setOnRefreshListener {
                viewModel.onManualRefresh()
            }
            buttonRetry.setOnClickListener {
                viewModel.onManualRefresh()
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.events.collect { event ->
                    when (event) {
                        is HomeViewModel.Event.ShowErrorMessage -> showSnackbar(
                            getString(
                                R.string.could_not_refresh,
                                event.error.localizedMessage
                                    ?: getString(R.string.unknown_error_occured)
                            )
                        )
                    }.exhaustive
                }
            }
        }
        setHasOptionsMenu(true)
    }

    override fun onStart() {
        super.onStart()
        viewModel.normalRefresh()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_popular -> {
                viewModel.onFilterItemSelected(CocktailFilter.POPULAR)
                viewModel.normalRefresh()
                true
            }
            R.id.action_latest -> {
                viewModel.onFilterItemSelected(CocktailFilter.LATEST)
                viewModel.normalRefresh()
                true
            }
            R.id.action_random -> {
                viewModel.onFilterItemSelected(CocktailFilter.RANDOMSELECTION)
                viewModel.normalRefresh()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}