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

        val cocktailAdapter = CocktailListAdapter()

        binding.apply {
            recyclerView.apply {
                adapter = cocktailAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.drinksByQuery.collect {
                    val result = it ?: return@collect
                    swipeRefreshLayout.isRefreshing = result is Resource.Loading
                    recyclerView.isVisible = !result.data.isNullOrEmpty()
                    textViewError.isVisible = result.error != null && result.data.isNullOrEmpty()
                    textViewError.text = getString(
                        R.string.could_not_refresh,
                        result.error?.localizedMessage ?: getString(R.string.unknown_error_occured)
                    )
                    buttonRetry.isVisible = result.error != null && result.data.isNullOrEmpty()

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