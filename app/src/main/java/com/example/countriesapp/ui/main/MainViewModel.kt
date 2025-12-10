package com.example.countriesapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.countriesapp.data.model.Country
import com.example.countriesapp.data.repository.CountryRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val repository: CountryRepository
) : ViewModel() {

    private val _countries = MutableLiveData<List<Country>>()
    val countries: LiveData<List<Country>> = _countries

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _currentRegion = MutableLiveData<String>("All")
    val currentRegion: LiveData<String> = _currentRegion

    init {
        loadCountries()
    }

    fun loadCountries() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                repository.refreshCountries()
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load countries: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun observeCountries() {
        viewModelScope.launch {
            repository.getAllCountries().collect { countriesList ->
                _countries.value = countriesList
            }
        }
    }

    fun filterByRegion(region: String) {
        _currentRegion.value = region
        viewModelScope.launch {
            if (region == "All") {
                repository.getAllCountries().collect { countriesList ->
                    _countries.value = countriesList
                }
            } else {
                repository.getCountriesByRegion(region).collect { countriesList ->
                    _countries.value = countriesList
                }
            }
        }
    }

    fun toggleFavorite(countryId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(countryId)
        }
    }

    fun searchCountries(query: String) {
        viewModelScope.launch {
            val allCountries = _countries.value ?: emptyList()
            if (query.isEmpty()) {
                // Если запрос пустой, показываем все страны текущего региона
                filterByRegion(_currentRegion.value ?: "All")
            } else {
                val filtered = allCountries.filter { country ->
                    country.name.contains(query, ignoreCase = true) ||
                            country.capital?.contains(query, ignoreCase = true) == true ||
                            country.region.contains(query, ignoreCase = true)
                }
                _countries.value = filtered
            }
        }
    }
}