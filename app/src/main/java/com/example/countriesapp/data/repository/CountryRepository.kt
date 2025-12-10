package com.yourname.countriesapp.data.repository

import com.yourname.countriesapp.data.local.AppDatabase
import com.yourname.countriesapp.data.local.entity.CountryEntity
import com.yourname.countriesapp.data.mapper.toCountry
import com.yourname.countriesapp.data.mapper.toCountryEntity
import com.yourname.countriesapp.data.model.Country
import com.yourname.countriesapp.data.remote.api.CountriesApi
import com.yourname.countriesapp.di.NetworkModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CountryRepository @Inject constructor(
    private val database: AppDatabase,
    private val api: CountriesApi
) {

    private val dao = database.countryDao()

    suspend fun refreshCountries() {
        try {
            val response = api.getAllCountries()
            if (response.isSuccessful) {
                val countries = response.body()?.map { it.toCountryEntity() } ?: emptyList()
                dao.deleteAll()
                dao.insertAll(countries)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAllCountries(): Flow<List<Country>> {
        return dao.getAllCountries().map { entities ->
            entities.map { it.toCountry() }
        }
    }

    fun getFavoriteCountries(): Flow<List<Country>> {
        return dao.getFavoriteCountries().map { entities ->
            entities.map { it.toCountry() }
        }
    }

    fun getCountriesByRegion(region: String): Flow<List<Country>> {
        return dao.getCountriesByRegion(region).map { entities ->
            entities.map { it.toCountry() }
        }
    }

    suspend fun toggleFavorite(countryId: String) {
        val country = dao.getCountryById(countryId)
        country?.let {
            dao.updateFavoriteStatus(countryId, !it.isFavorite)
        }
    }

    suspend fun getCountryById(id: String): Country? {
        return dao.getCountryById(id)?.toCountry()
    }
}