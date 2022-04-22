package com.home.cocktailapp.shared

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.home.cocktailapp.R
import com.home.cocktailapp.data.Cocktails
import com.home.cocktailapp.databinding.RecyclerviewItemDrinkBinding

class CocktailViewHolder(
    private val binding: RecyclerviewItemDrinkBinding,
    private val onItemClick: (Int) -> Unit,
    private val onFavoriteClick: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(cocktail: Cocktails) {
        binding.apply {
            Glide.with(itemView)
                .load(cocktail.drinkImageUrl)
                .error(R.drawable.image_not_found)
                .into(textViewDrinkImage)

            textViewDrinkName.text = cocktail.drinkName
            imageViewFavorite.setImageResource(
                when (cocktail.isFavourited) {
                    true -> R.drawable.ic_favorite_full
                    else -> R.drawable.ic_favorite_empty
                }
            )
        }
    }

    init {
        binding.apply {
            root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(position)
                }
            }
            imageViewFavorite.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onFavoriteClick(position)
                }
            }
        }
    }
}