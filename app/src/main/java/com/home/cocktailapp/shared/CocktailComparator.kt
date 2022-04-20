package com.home.cocktailapp.shared

import androidx.recyclerview.widget.DiffUtil
import com.home.cocktailapp.data.Cocktails

class CocktailComparator : DiffUtil.ItemCallback<Cocktails>() {
    override fun areItemsTheSame(oldItem: Cocktails, newItem: Cocktails) =
        oldItem.cocktailId == newItem.cocktailId


    override fun areContentsTheSame(oldItem: Cocktails, newItem: Cocktails) =
        oldItem == newItem
}