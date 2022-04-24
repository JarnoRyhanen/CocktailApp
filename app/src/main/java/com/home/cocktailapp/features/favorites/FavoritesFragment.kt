package com.home.cocktailapp.features.favorites

import android.os.Bundle
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
import com.home.cocktailapp.databinding.FragmentFavoritesBinding
import com.home.cocktailapp.features.home.HomeFragmentDirections
import com.home.cocktailapp.shared.CocktailListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private val viewModel: FavoritesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentFavoritesBinding.bind(view)

        val favoritesAdapter = CocktailListAdapter(
            onItemClick = { cocktails ->
                val action = FavoritesFragmentDirections.actionFavoritesFragmentToDetailsFragment(cocktails)
                findNavController().navigate(action)
            },
            onFavoriteClick = { cocktail ->
                viewModel.onFavoriteClick(cocktail)
            }
        )

        binding.apply {
            recyclerView.apply {
                adapter = favoritesAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.favorites.collect {
                    val favorites = it ?: return@collect

                    favoritesAdapter.submitList(favorites)
                    textViewFavorites.isVisible = favorites.isEmpty()
                    recyclerView.isVisible = favorites.isNotEmpty()
                }
            }
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_favorites, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_delete_all_favorites -> {
                viewModel.onDeleteAllFavorites()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

}