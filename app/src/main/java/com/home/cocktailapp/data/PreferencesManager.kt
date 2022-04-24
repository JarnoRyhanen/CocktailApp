package com.home.cocktailapp.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

enum class CocktailFilter {
    POPULAR, LATEST, RANDOMSELECTION
}

enum class SearchQueryType {
    SEARCH_COCKTAILS, SEARCH_COCKTAILS_BY_INGREDIENT
}

private const val TAG = "PreferencesManager"

data class FilterPreferences(val cocktailFilter: CocktailFilter)
data class SearchQueryTypePreferences(val searchQueryType: SearchQueryType)

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext context: Context
){
    private val dataStore = context.createDataStore("user_preferences")

    val filterPreferencesFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val cocktailFilter = CocktailFilter.valueOf(
                preferences[PreferencesKeys.COCKTAIL_FILTER] ?: CocktailFilter.POPULAR.name
            )
            FilterPreferences(cocktailFilter)
        }

    val searchQueryTypeFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val searchQueryType = SearchQueryType.valueOf(
                preferences[PreferencesKeys.SEARCH_QUERY_TYPE] ?: SearchQueryType.SEARCH_COCKTAILS.name
            )
            SearchQueryTypePreferences(searchQueryType)
        }


    suspend fun updateCocktailFilter(cocktailFilter: CocktailFilter) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.COCKTAIL_FILTER] = cocktailFilter.name
        }
    }
    suspend fun updateSearchQueryType(searchQueryType: SearchQueryType) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SEARCH_QUERY_TYPE] = searchQueryType.name
        }
    }

    private object PreferencesKeys {
        val COCKTAIL_FILTER = preferencesKey<String>("cocktail_filter")
        val SEARCH_QUERY_TYPE = preferencesKey<String>("search_query_type")
    }
}