package com.example.countriesapp.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.countriesapp.data.repository.CountryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: CountryRepository
) : ViewModel() {

    private val _country = MutableStateFlow<com.example.countriesapp.data.model.Country?>(null)
    val country: StateFlow<com.example.countriesapp.data.model.Country?> = _country.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadCountry(countryId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val countryData = repository.getCountryById(countryId)
                _country.value = countryData
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _country.value?.let { country ->
                repository.toggleFavorite(country.id)
                loadCountry(country.id)
            }
        }
    }
}