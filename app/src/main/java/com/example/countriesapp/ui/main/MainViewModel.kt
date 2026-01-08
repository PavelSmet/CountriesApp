package com.example.countriesapp.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.countriesapp.data.model.Country
import com.example.countriesapp.data.repository.CountryRepository
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: CountryRepository
) : ViewModel() {

    // LiveData для списка стран
    private val _countries = MutableLiveData<List<Country>>()
    val countries: LiveData<List<Country>> = _countries

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _currentRegion = MutableLiveData<String>("All")
    val currentRegion: LiveData<String> = _currentRegion

    // ⭐ Флаг для прокрутки в начало
    private val _shouldScrollToTop = MutableLiveData<Boolean>(false)
    val shouldScrollToTop: LiveData<Boolean> = _shouldScrollToTop

    // ⭐ Enum для типов сортировки
    enum class SortType {
        NONE, NAME_ASC, NAME_DESC, POPULATION_ASC, POPULATION_DESC
    }

    // ⭐ Текущий тип сортировки
    private val _currentSortType = MutableLiveData<SortType>(SortType.NONE)
    val currentSortType: LiveData<SortType> = _currentSortType

    init {
        Log.d("MainViewModel", "🚀 ViewModel создан")
        _isLoading.value = false
    }

    fun startObservingCountries() {
        Log.d("MainViewModel", "👀 Начинаю слушать данные из репозитория...")

        viewModelScope.launch {
            repository.getAllCountries().collect { countriesFromDb ->
                if (!viewModelScope.isActive) return@collect

                Log.d("MainViewModel", "📥 Получено из БД: ${countriesFromDb.size} стран")

                val filteredList = if (_currentRegion.value != "All" && _currentRegion.value != null) {
                    countriesFromDb.filter { it.region.equals(_currentRegion.value, ignoreCase = true) }
                } else {
                    countriesFromDb
                }

                Log.d("MainViewModel", "🎯 Отправляю в UI: ${filteredList.size} стран")
                _countries.postValue(filteredList)
            }
        }
    }

    fun loadCountriesFromApi() {
        if (_isLoading.value == true) {
            Log.w("MainViewModel", "⚠️ Загрузка уже выполняется, пропускаю...")
            return
        }

        Log.d("MainViewModel", "🔄 Начинаю загрузку из API...")
        _isLoading.value = true

        viewModelScope.launch {
            try {
                repository.refreshCountries()
                _errorMessage.postValue(null)
                Log.d("MainViewModel", "✅ Загрузка из API завершена успешно")

            } catch (e: Exception) {
                Log.e("MainViewModel", "❌ Ошибка загрузки из API: ${e.message}")
                _errorMessage.postValue("Не удалось загрузить страны: ${e.localizedMessage}")

            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun filterByRegion(region: String) {
        Log.d("MainViewModel", "🌍 Фильтрую по региону: $region")
        _currentRegion.value = region

        viewModelScope.launch {
            repository.getAllCountries().collect { allCountries ->
                val filteredList = if (region == "All" || region.isEmpty()) {
                    allCountries
                } else {
                    allCountries.filter {
                        it.region.equals(region, ignoreCase = true)
                    }
                }
                _countries.postValue(filteredList)
            }
        }
    }

    fun toggleFavorite(countryId: String) {
        Log.d("MainViewModel", "❤️ Переключаю избранное для страны ID: $countryId")

        viewModelScope.launch {
            try {
                repository.toggleFavorite(countryId)
            } catch (e: Exception) {
                Log.e("MainViewModel", "❌ Ошибка при обновлении избранного: ${e.message}")
                _errorMessage.postValue("Не удалось обновить избранное")
            }
        }
    }

    fun searchCountries(query: String) {
        Log.d("MainViewModel", "🔍 Поиск стран по запросу: '$query'")

        viewModelScope.launch {
            val currentCountries = _countries.value ?: emptyList()

            if (query.isEmpty()) {
                filterByRegion(_currentRegion.value ?: "All")
            } else {
                val filtered = currentCountries.filter { country ->
                    country.name.contains(query, ignoreCase = true) ||
                            country.capital?.contains(query, ignoreCase = true) == true ||
                            country.region.contains(query, ignoreCase = true)
                }
                _countries.postValue(filtered)
            }
        }
    }

    fun sortByName(ascending: Boolean = true) {
        Log.d("MainViewModel", "🔀 Сортировка по имени: $ascending")

        viewModelScope.launch {
            _countries.value?.let { currentList ->
                val sorted = if (ascending) {
                    _currentSortType.value = SortType.NAME_ASC
                    currentList.sortedBy { it.name }
                } else {
                    _currentSortType.value = SortType.NAME_DESC
                    currentList.sortedByDescending { it.name }
                }
                _countries.value = sorted
                _shouldScrollToTop.value = true
            }
        }
    }

    fun sortByPopulation(ascending: Boolean = true) {
        Log.d("MainViewModel", "🔀 Сортировка по населению: $ascending")

        viewModelScope.launch {
            _countries.value?.let { currentList ->
                val sorted = if (ascending) {
                    _currentSortType.value = SortType.POPULATION_ASC
                    currentList.sortedBy { it.population }
                } else {
                    _currentSortType.value = SortType.POPULATION_DESC
                    currentList.sortedByDescending { it.population }
                }
                _countries.value = sorted
                _shouldScrollToTop.value = true
            }
        }
    }

    // ⭐⭐ ВОТ ЭТИ ДВА МЕТОДА ДОБАВЬ В КОНЕЦ КЛАССА ⭐⭐

    /**
     * Сбрасывает флаг прокрутки
     */
    fun resetScrollFlag() {
        _shouldScrollToTop.value = false
    }

    /**
     * Применяет сохраненную сортировку
     */
    fun applySavedSort() {
        _currentSortType.value?.let { sortType ->
            when (sortType) {
                SortType.NAME_ASC -> sortByName(true)
                SortType.NAME_DESC -> sortByName(false)
                SortType.POPULATION_ASC -> sortByPopulation(true)
                SortType.POPULATION_DESC -> sortByPopulation(false)
                SortType.NONE -> {
                    Log.d("MainViewModel", "📊 Нет сохраненной сортировки")
                }
            }
        }
    }
}