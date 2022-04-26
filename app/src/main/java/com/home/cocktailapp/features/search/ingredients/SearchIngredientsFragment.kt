package com.home.cocktailapp.features.search.ingredients

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.home.cocktailapp.R
import com.home.cocktailapp.databinding.FragmentSearchIngredientBinding
import com.home.cocktailapp.util.onQueryTextSubmit
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchIngredientsFragment : Fragment(R.layout.fragment_search_ingredient) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSearchIngredientBinding.bind(view)

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search_ingredients, menu)

        val searchItem = menu.findItem(R.id.action_search_ingredients)
        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextSubmit { query ->
            searchView.clearFocus()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_search_ingredients -> true
            else -> super.onOptionsItemSelected(item)
        }
}