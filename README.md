# CocktailApp

This app uses https://www.thecocktaildb.com/api.php api to fetch cocktails and display them on the screen

It is written in 100% Kotlin, and follows recommended android app architecture (MVVM) with repository pattern

The app uses Data binding, Kotlin coroutines, Kotlin flow, Navigation component. For network requests it uses Retrofit with Gson, for local database Room, 
for dependency injection Dagger Hilt and for loading images from the web it uses Glide
