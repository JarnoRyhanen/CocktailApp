package com.home.cocktailapp.features.search.ingredients

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.home.cocktailapp.R
import com.home.cocktailapp.databinding.FragmentSearchIngredientBinding
import com.home.cocktailapp.util.Resource
import com.home.cocktailapp.util.onQueryTextSubmit
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SearchIngredientsFragment : Fragment(R.layout.fragment_search_ingredient) {

    private val viewModel: SearchIngredientsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSearchIngredientBinding.bind(view)

        val searchAdapter = SearchIngredientsListAdapter()

        binding.apply {
            recyclerView.apply {
                adapter = searchAdapter
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.ingredient.collectLatest {
                    val result = it ?: return@collectLatest

                    when (result) {
                        is Resource.Loading -> {
                            progressBar.isVisible = true
                            buttonRetry.isVisible = false
                            textViewInstructions.isVisible = false
                            textViewError.isVisible = false
                            textViewNoResults.isVisible = false
                            recyclerView.visibility = View.INVISIBLE
                        }
                        is Resource.Success -> {
                            buttonRetry.isVisible = false
                            textViewInstructions.isVisible = false
                            textViewError.isVisible = false
                            progressBar.isVisible = false
                            recyclerView.isVisible =
                                searchAdapter.itemCount > 0 || !result.data.isNullOrEmpty()
                            val noResults =
                                searchAdapter.itemCount < 1 && result.data.isNullOrEmpty()
                            textViewNoResults.isVisible = noResults
                            textViewNoResults.text = getString(R.string.no_results_found)
                        }
                        is Resource.Error -> {
                            textViewInstructions.isVisible = false
                            textViewNoResults.isVisible = false
                            progressBar.isVisible = false
                            recyclerView.isVisible = searchAdapter.itemCount > 0

                            val noCachedResults =
                                searchAdapter.itemCount < 1 && result.data.isNullOrEmpty()
                            buttonRetry.isVisible = noCachedResults

                            textViewError.isVisible = noCachedResults
                            val errorMessage = getString(
                                R.string.could_not_load_search_results,
                                result.error?.localizedMessage ?: R.string.unknown_error_occured
                            )
                            textViewError.text = errorMessage
                        }
                    }
                    searchAdapter.submitList(result.data)
                }
            }
            textViewInstructions.isVisible = true

            buttonRetry.setOnClickListener {
                viewModel.onManualRefresh()
            }
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search_ingredients, menu)

        val searchItem = menu.findItem(R.id.action_search_ingredients)
        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextSubmit { query ->
            viewModel.updateSearchQuery(query)
            viewModel.onManualRefresh()
            searchView.clearFocus()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_search_ingredients -> true
            else -> super.onOptionsItemSelected(item)
        }
}