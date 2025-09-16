package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {

    companion object {
        val LANGUAGE = stringPreferencesKey("language")
        val CURRENCY = stringPreferencesKey("currency")
    }

    val selectedLanguage = context.dataStore.data.map { it[LANGUAGE] ?: "en" }

    val selectedCurrency = context.dataStore.data.map { it[CURRENCY] ?: "USD" }

    suspend fun saveSelectedLanguage(language: String) {
        context.dataStore.edit { it[LANGUAGE] = language }
    }

    // TODO: Use currency: Currency?
    suspend fun saveSelectedCurrency(currency: String) {
        context.dataStore.edit { it[CURRENCY] = currency }
    }
}