package com.home.cocktailapp.shared

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.home.cocktailapp.data.Cocktails
import com.home.cocktailapp.databinding.RecyclerviewItemDrinkBinding

class CocktailListAdapter(
    private val onItemClick: (Cocktails) -> Unit,
    private val onFavoriteClick: (Cocktails) -> Unit
) : ListAdapter<Cocktails, CocktailViewHolder>(CocktailComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailViewHolder {
        val binding =
            RecyclerviewItemDrinkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CocktailViewHolder(binding,
            onItemClick = { position ->
                val cocktail = getItem(position)
                if (cocktail != null) {
                    onItemClick(cocktail)
                }
            }, onFavoriteClick = { position ->
                val cocktail = getItem(position)
                if (cocktail != null) {
                    onFavoriteClick(cocktail)
                }
            })
    }

    override fun onBindViewHolder(holder: CocktailViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }
}