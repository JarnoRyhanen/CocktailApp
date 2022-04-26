package com.home.cocktailapp.features.search

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.home.cocktailapp.R
import com.home.cocktailapp.data.SearchQueryType
import com.home.cocktailapp.databinding.FragmentSearchBinding
import com.home.cocktailapp.shared.CocktailListAdapter
import com.home.cocktailapp.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment() : Fragment(R.layout.fragment_search) {

    private val viewModel: SearchViewModel by viewModels()

    private var currentBinding: FragmentSearchBinding? = null
    private val binding get() = currentBinding!!

    private lateinit var searchAdapter: CocktailListAdapter

    private var openFragment: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentBinding = FragmentSearchBinding.bind(view)

        searchAdapter = CocktailListAdapter(
            onItemClick = { cocktails ->
                lifecycleScope.launch {
                    val searchQueryType = viewModel.getPreferences()
                    if (searchQueryType == SearchQueryType.SEARCH_COCKTAILS_BY_INGREDIENT) {
                        if (viewModel.insertCocktailById(cocktails.cocktailId)) openFragment = true
                    } else {
                        openFragment = true
                    }
                }
                if (openFragment) {
                    val action =
                        SearchFragmentDirections.actionSearchFragmentToDetailsFragment(cocktails)
                    findNavController().navigate(action)
                    openFragment = false
                }
            },
            onFavoriteClick = { cocktail ->
                viewModel.onFavoriteClick(cocktail)
            })

        binding.apply {
            recyclerView.apply {
                adapter = searchAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                itemAnimator?.changeDuration = 0
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.searchResults.collectLatest {
                    val result = it ?: return@collectLatest
                    Log.d("tag", "onViewCreated: ${result.data?.size}")
                    when (result) {
                        is Resource.Loading -> {
                            textViewError.isVisible = false
                            textViewNoResults.isVisible = false
                            swipeRefreshLayout.isRefreshing = true
                            recyclerView.showIfOrInvisible {
                                !viewModel.newQueryInProgress && searchAdapter.itemCount > 0
                            }

                            viewModel.refreshInProgress = true
                            viewModel.pendingScrollToTopAfterRefresh = true
                        }
                        is Resource.Success -> {
                            textViewError.isVisible = false
                            swipeRefreshLayout.isRefreshing = false
                            recyclerView.isVisible =
                                searchAdapter.itemCount > 0 || !result.data.isNullOrEmpty()
                            val noResults =
                                searchAdapter.itemCount < 1 && result.data.isNullOrEmpty()
                            textViewNoResults.isVisible = noResults
                            textViewNoResults.text = getString(R.string.no_results_found)

                            viewModel.refreshInProgress = false
                            viewModel.newQueryInProgress = false
                        }
                        is Resource.Error -> {
                            swipeRefreshLayout.isRefreshing = false
                            textViewNoResults.isVisible = false
                            recyclerView.isVisible = searchAdapter.itemCount > 0

                            val noCachedResults =
                                searchAdapter.itemCount < 1 && result.data.isNullOrEmpty()
                            textViewError.isVisible = noCachedResults

                            val errorMessage = getString(
                                R.string.could_not_load_search_results,
                                result.error?.localizedMessage ?: R.string.unknown_error_occured
                            )
                            textViewError.text = errorMessage

                            if (viewModel.refreshInProgress) {
                                showSnackbar(errorMessage)
                            }
                            viewModel.pendingScrollToTopAfterRefresh = false
                            viewModel.refreshInProgress = false
                            viewModel.newQueryInProgress = false
                        }
                    }
                    searchAdapter.submitList(result.data) {
                        if (viewModel.pendingScrollToTopAfterNewQuery) {
                            recyclerView.scrollToPosition(0)
                            viewModel.pendingScrollToTopAfterNewQuery = false
                        }
                        if (viewModel.pendingScrollToTopAfterRefresh) {
                            recyclerView.scrollToPosition(0)
                            viewModel.pendingScrollToTopAfterRefresh = false
                        }
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.hasCurrentQuery.collect { hasCurrentQuery ->
                    textViewInstructions.isVisible = !hasCurrentQuery
                    swipeRefreshLayout.isEnabled = hasCurrentQuery
                    if (!hasCurrentQuery) {
                        recyclerView.isVisible = false
                    }
                }
            }

            swipeRefreshLayout.setOnRefreshListener {
                viewModel.onManualRefresh()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    is SearchViewModel.Event.ShowErrorMessage -> showSnackbar(
                        getString(
                            R.string.could_not_refresh,
                            event.error.localizedMessage
                                ?: getString(R.string.unknown_error_occured)
                        )
                    )
                }
            }
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search_cocktails, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.onQueryTextSubmit { query ->
            viewModel.onSearchQuerySubmit(query.replace(" ", ""))
            viewModel.onManualRefresh()
            searchView.clearFocus()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_search_cocktail -> {
                viewModel.onSearchQueryTypeSelected(SearchQueryType.SEARCH_COCKTAILS)
                true
            }
            R.id.action_search_ingredient -> {
                true
            }
            R.id.action_search_cocktail_by_ingredient -> {
                viewModel.onSearchQueryTypeSelected(SearchQueryType.SEARCH_COCKTAILS_BY_INGREDIENT)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }.exhaustive

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.adapter = null
        currentBinding = null
    }
}