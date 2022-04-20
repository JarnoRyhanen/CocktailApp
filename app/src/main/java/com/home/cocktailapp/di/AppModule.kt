package com.home.cocktailapp.di

import android.app.Application
import androidx.room.Room
import com.home.cocktailapp.api.CocktailApi
import com.home.cocktailapp.data.CocktailDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(CocktailApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideCocktailApi(retrofit: Retrofit): CocktailApi =
        retrofit.create(CocktailApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(app: Application): CocktailDatabase =
        Room.databaseBuilder(app, CocktailDatabase::class.java,"cocktail_database")
            .fallbackToDestructiveMigration()
            .build()
}