package com.example.countriesapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.countriesapp.data.model.Country
import com.example.countriesapp.data.repository.CountryRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailViewModel @Inject constructor(
    private val repository: CountryRepository
) : ViewModel() {

    private val _country = MutableLiveData<Country?>()
    val country: LiveData<Country?> = _country

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadCountry(countryId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val loadedCountry = repository.getCountryById(countryId)
                _country.value = loadedCountry
            } catch (e: Exception) {
                _country.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _country.value?.let { country ->
                repository.toggleFavorite(country.id)
                // Обновляем текущее состояние страны
                val updatedCountry = repository.getCountryById(country.id)
                _country.value = updatedCountry
            }
        }
    }
}