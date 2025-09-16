package com.example.text2cash.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
) : ViewModel() {
    val selectedLanguage = userPreferences.selectedLanguage.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        "en",
    )

    val selectedCurrency = userPreferences.selectedCurrency.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        "USD",
    )

    fun onLanguageChanged(language: String) {
        viewModelScope.launch {
            userPreferences.saveSelectedLanguage(language)
        }
    }

    internal fun onCurrencyChanged(currency: String) {
        viewModelScope.launch {
            userPreferences.saveSelectedCurrency(currency)
        }
    }
}