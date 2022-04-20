package com.home.cocktailapp.shared

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.home.cocktailapp.data.Cocktails
import com.home.cocktailapp.databinding.RecyclerviewItemDrinkBinding

class CocktailListAdapter : ListAdapter<Cocktails, CocktailViewHolder>(CocktailComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailViewHolder {
        val binding =
            RecyclerviewItemDrinkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CocktailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CocktailViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }
}