package com.home.cocktailapp.features.search.ingredients

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.home.cocktailapp.data.Ingredient
import com.home.cocktailapp.databinding.RecyclerViewIngredientBinding

class SearchIngredientsListAdapter : ListAdapter<Ingredient,
        SearchIngredientsListAdapter.SearchIngredientsViewHolder>(IngredientComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchIngredientsViewHolder {
        val binding = RecyclerViewIngredientBinding.inflate(
            LayoutInflater.from(
                parent.context
            ), parent, false
        )
        return SearchIngredientsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchIngredientsViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    class SearchIngredientsViewHolder(private val binding: RecyclerViewIngredientBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ingredient: Ingredient) {
            binding.apply {
             ingredientName.text = ingredient.ingredientName
                if (!ingredient.description.isNullOrEmpty()) {
                    ingredientDescription.text = ingredient.description
                }else{
                    ingredientDescriptionNotFound.text = String.format("No description found for \n ${ingredient.ingredientName}")
                }
            }
        }
    }

    class IngredientComparator : DiffUtil.ItemCallback<Ingredient>() {
        override fun areItemsTheSame(oldItem: Ingredient, newItem: Ingredient) =
            oldItem.ingredientId == newItem.ingredientId

        override fun areContentsTheSame(oldItem: Ingredient, newItem: Ingredient) =
            oldItem == newItem
    }
}