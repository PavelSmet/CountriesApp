package com.example.countriesapp.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.countriesapp.data.repository.CountryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: CountryRepository
) : ViewModel() {

    private val _favoriteCountries = MutableStateFlow<List<com.example.countriesapp.data.model.Country>>(emptyList())
    val favoriteCountries: StateFlow<List<com.example.countriesapp.data.model.Country>> = _favoriteCountries.asStateFlow()

    private val _isLoading = MutableStateFlow(true) // начинаем с true
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            _isLoading.value = true  // ВКЛючаем

            repository.getFavoriteCountries().collect { favorites ->
                _favoriteCountries.value = favorites

                // ВЫКЛючаем после первого значения
                if (_isLoading.value) {
                    _isLoading.value = false
                }
            }
        }
    }



    fun toggleFavorite(countryId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(countryId)
        }
    }
}