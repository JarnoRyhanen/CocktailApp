package com.home.cocktailapp.features.details

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
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

        val cocktail = args.cocktail

        binding.apply {
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
                            progressBar.isVisible = false
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBar.isVisible = false

                            textViewDrinkName.isVisible = !cocktail.drinkName.isNullOrEmpty()
                            textViewDrinkInstructions.isVisible = !cocktail.drinkInstructions.isNullOrEmpty()

                            ingredient1.isVisible = !cocktail.drinkIngredient1.isNullOrEmpty()
                            ingredient2.isVisible = !cocktail.drinkIngredient2.isNullOrEmpty()
                            ingredient3.isVisible = !cocktail.drinkIngredient4.isNullOrEmpty()
                            ingredient4.isVisible = !cocktail.drinkIngredient4.isNullOrEmpty()
                            ingredient5.isVisible = !cocktail.drinkIngredient5.isNullOrEmpty()
                            ingredient6.isVisible = !cocktail.drinkIngredient6.isNullOrEmpty()
                            ingredient7.isVisible = !cocktail.drinkIngredient7.isNullOrEmpty()
                            ingredient8.isVisible = !cocktail.drinkIngredient8.isNullOrEmpty()
                            ingredient9.isVisible = !cocktail.drinkIngredient9.isNullOrEmpty()
                            ingredient10.isVisible = !cocktail.drinkIngredient10.isNullOrEmpty()
                            ingredient11.isVisible = !cocktail.drinkIngredient11.isNullOrEmpty()
                            ingredient12.isVisible = !cocktail.drinkIngredient12.isNullOrEmpty()
                            ingredient13.isVisible = !cocktail.drinkIngredient13.isNullOrEmpty()
                            ingredient14.isVisible = !cocktail.drinkIngredient14.isNullOrEmpty()
                            ingredient15.isVisible = !cocktail.drinkIngredient15.isNullOrEmpty()

                            measure1.isVisible = !cocktail.drinkMeasure1.isNullOrEmpty()
                            measure2.isVisible = !cocktail.drinkMeasure2.isNullOrEmpty()
                            measure3.isVisible = !cocktail.drinkMeasure4.isNullOrEmpty()
                            measure4.isVisible = !cocktail.drinkMeasure4.isNullOrEmpty()
                            measure5.isVisible = !cocktail.drinkMeasure5.isNullOrEmpty()
                            measure6.isVisible = !cocktail.drinkMeasure6.isNullOrEmpty()
                            measure7.isVisible = !cocktail.drinkMeasure7.isNullOrEmpty()
                            measure8.isVisible = !cocktail.drinkMeasure8.isNullOrEmpty()
                            measure9.isVisible = !cocktail.drinkMeasure9.isNullOrEmpty()
                            measure10.isVisible = !cocktail.drinkMeasure10.isNullOrEmpty()
                            measure11.isVisible = !cocktail.drinkMeasure11.isNullOrEmpty()
                            measure12.isVisible = !cocktail.drinkMeasure12.isNullOrEmpty()
                            measure13.isVisible = !cocktail.drinkMeasure13.isNullOrEmpty()
                            measure14.isVisible = !cocktail.drinkMeasure14.isNullOrEmpty()
                            measure15.isVisible = !cocktail.drinkMeasure15.isNullOrEmpty()
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