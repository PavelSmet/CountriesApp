package com.example.countriesapp.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.countriesapp.data.model.Country
import com.example.countriesapp.data.repository.CountryRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoritesViewModel @Inject constructor(
    private val repository: CountryRepository
) : ViewModel() {

    private val _favorites = MutableLiveData<List<Country>>()
    val favorites: LiveData<List<Country>> = _favorites

    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> = _isEmpty

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            repository.getFavoriteCountries().collect { favoriteCountries ->
                _favorites.value = favoriteCountries
                _isEmpty.value = favoriteCountries.isEmpty()
            }
        }
    }

    fun removeFromFavorites(countryId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(countryId)
        }
    }
}