package com.home.cocktailapp.features.details

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.home.cocktailapp.R
import com.home.cocktailapp.databinding.FragmentDetailsBinding

class DetailsFragment : Fragment(R.layout.fragment_details) {

    private val args by navArgs<DetailsFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDetailsBinding.bind(view)

        binding.apply {
            val cocktail = args.cocktail

            Glide.with(this@DetailsFragment)
                .load(cocktail.drinkImageUrl)
                .fitCenter()
                .error(R.drawable.image_not_found)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .into(imageView)

            textViewDrinkName.text = cocktail.drinkName
            textViewDrinkInstructions.text = cocktail.drinkInstructions

            ingredient1.text = cocktail.drinkIngredient1
            ingredient2.text = cocktail.drinkIngredient2
            ingredient3.text = cocktail.drinkIngredient3
            ingredient4.text = cocktail.drinkIngredient4
            ingredient5.text = cocktail.drinkIngredient5
            ingredient6.text = cocktail.drinkIngredient6
            ingredient7.text = cocktail.drinkIngredient7
            ingredient8.text = cocktail.drinkIngredient8
            ingredient9.text = cocktail.drinkIngredient9
            ingredient10.text = cocktail.drinkIngredient10
            ingredient11.text = cocktail.drinkIngredient11
            ingredient12.text = cocktail.drinkIngredient12
            ingredient13.text = cocktail.drinkIngredient13
            ingredient14.text = cocktail.drinkIngredient14
            ingredient15.text = cocktail.drinkIngredient15

            measure1.text = cocktail.drinkMeasure1
            measure2.text = cocktail.drinkMeasure2
            measure3.text = cocktail.drinkMeasure3
            measure4.text = cocktail.drinkMeasure4
            measure5.text = cocktail.drinkMeasure5
            measure6.text = cocktail.drinkMeasure6
            measure7.text = cocktail.drinkMeasure7
            measure8.text = cocktail.drinkMeasure8
            measure9.text = cocktail.drinkMeasure9
            measure10.text = cocktail.drinkMeasure10
            measure11.text = cocktail.drinkMeasure11
            measure12.text = cocktail.drinkMeasure12
            measure13.text = cocktail.drinkMeasure13
            measure14.text = cocktail.drinkMeasure14
            measure15.text = cocktail.drinkMeasure15
        }
    }
}