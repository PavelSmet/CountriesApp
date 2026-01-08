package com.example.countriesapp.data.repository

import android.util.Log
import com.example.countriesapp.data.local.AppDatabase
import com.example.countriesapp.data.local.entity.CountryEntity
import com.example.countriesapp.data.model.Country
import com.example.countriesapp.data.remote.api.CountriesApi
import com.example.countriesapp.data.remote.dto.CountryDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import retrofit2.Response
import java.util.UUID

class CountryRepository(
    private val database: AppDatabase,
    private val api: CountriesApi
) {

    /**
     * Получает все страны из базы данных
     */
    fun getAllCountries(): Flow<List<Country>> {
        Log.d("CountryRepository", "📂 getAllCountries() вызван")

        return database.countryDao().getAllCountries()
            .map { entities ->
                entities.map { entity ->
                    Country(
                        id = entity.id,
                        name = entity.name,
                        officialName = entity.officialName,
                        capital = entity.capital,
                        population = entity.population,
                        region = entity.region,
                        subregion = entity.subregion,
                        flagUrl = entity.flagUrl,
                        currency = entity.currency,
                        languages = entity.languages,
                        isFavorite = entity.isFavorite
                    )
                }
            }
    }

    /**
     * Получает страны по региону
     */
    fun getCountriesByRegion(region: String): Flow<List<Country>> {
        Log.d("CountryRepository", "🌍 getCountriesByRegion($region) вызван")

        return database.countryDao().getCountriesByRegion(region)
            .map { entities ->
                entities.map { entity ->
                    Country(
                        id = entity.id,
                        name = entity.name,
                        officialName = entity.officialName,
                        capital = entity.capital,
                        population = entity.population,
                        region = entity.region,
                        subregion = entity.subregion,
                        flagUrl = entity.flagUrl,
                        currency = entity.currency,
                        languages = entity.languages,
                        isFavorite = entity.isFavorite
                    )
                }
            }
    }

    /**
     * Загружает свежие данные из API
     */
    suspend fun refreshCountries() {
        try {
            Log.d("CountryRepository", "🔄 Начинаю загрузку стран из API")

            val response = api.getAllCountries()
            Log.d("CountryRepository", "📡 Ответ API получен. Код: ${response.code()}")
            Log.d("CountryRepository", "📡 Успешен? ${response.isSuccessful}")
            Log.d("CountryRepository", "📡 Сообщение: ${response.message()}")

            if (response.isSuccessful) {
                val apiResponse = response.body() ?: emptyList()
                Log.d("CountryRepository", "✅ Получено стран из API: ${apiResponse.size}")

                if (apiResponse.isEmpty()) {
                    Log.w("CountryRepository", "⚠️ API вернул пустой список!")
                } else {
                    // Выведите первые 3 страны для проверки
                    apiResponse.take(3).forEachIndexed { index, dto ->
                        Log.d("CountryRepository", "   ${index + 1}. ${dto.name?.common ?: "No name"} (${dto.cca3})")
                    }
                }

                // 1. Получаем текущие избранные из БД
                val allCountries = database.countryDao().getAllCountries().first()
                val currentFavorites = allCountries
                    .filter { it.isFavorite }
                    .associateBy { it.id }

                Log.d("CountryRepository", "📊 Избранных в БД: ${currentFavorites.size}")

                val entities = apiResponse.map { dto ->
                    val currency = dto.currencies?.values?.firstOrNull()?.name
                    val languages = dto.languages?.values?.joinToString(", ")

                    CountryEntity(
                        id = dto.cca3 ?: "unknown_${UUID.randomUUID()}",
                        name = dto.name?.common ?: "Unknown",
                        officialName = dto.name?.official ?: "",
                        capital = dto.capital?.firstOrNull(),
                        population = dto.population ?: 0L,
                        region = dto.region ?: "Unknown",
                        subregion = dto.subregion,
                        flagUrl = dto.flags?.png ?: "",
                        currency = currency,
                        languages = languages,
                        isFavorite = currentFavorites[dto.cca3]?.isFavorite ?: false
                    )
                }

                Log.d("CountryRepository", "💾 Сохраняю ${entities.size} стран в Room")
                database.countryDao().insertAll(entities)
                Log.d("CountryRepository", "✅ Данные сохранены в Room")

            } else {
                Log.e("CountryRepository", "❌ Ошибка API: ${response.errorBody()?.string()}")
            }

        } catch (e: Exception) {
            Log.e("CountryRepository", "❌ Исключение при загрузке: ${e.message}", e)
            throw e
        }
    }

    /**
     * Переключает статус "избранное"
     */
    suspend fun toggleFavorite(countryId: String) {
        Log.d("CountryRepository", "❤️ toggleFavorite($countryId)")

        try {
            val country = database.countryDao().getCountryById(countryId)
            country?.let {
                val newFavoriteStatus = !it.isFavorite
                database.countryDao().updateFavoriteStatus(countryId, newFavoriteStatus)
                Log.d("CountryRepository", "✅ Избранное обновлено: $countryId → $newFavoriteStatus")
            } ?: run {
                Log.e("CountryRepository", "❌ Страна с ID $countryId не найдена")
            }
        } catch (e: Exception) {
            Log.e("CountryRepository", "❌ Ошибка: ${e.message}", e)
            throw e
        }
    }

    /**
     * Получает избранные страны
     */
    fun getFavoriteCountries(): Flow<List<Country>> {
        Log.d("CountryRepository", "⭐ getFavoriteCountries() вызван")

        return database.countryDao().getFavoriteCountries()
            .map { entities ->
                entities.map { entity ->
                    Country(
                        id = entity.id,
                        name = entity.name,
                        officialName = entity.officialName,
                        capital = entity.capital,
                        population = entity.population,
                        region = entity.region,
                        subregion = entity.subregion,
                        flagUrl = entity.flagUrl,
                        currency = entity.currency,
                        languages = entity.languages,
                        isFavorite = entity.isFavorite
                    )
                }
            }
    }

    /**
     * Получает страну по ID
     */
    suspend fun getCountryById(id: String): Country? {
        Log.d("CountryRepository", "🔍 getCountryById($id) вызван")

        return try {
            database.countryDao().getCountryById(id)?.let { entity ->
                Country(
                    id = entity.id,
                    name = entity.name,
                    officialName = entity.officialName,
                    capital = entity.capital,
                    population = entity.population,
                    region = entity.region,
                    subregion = entity.subregion,
                    flagUrl = entity.flagUrl,
                    currency = entity.currency,
                    languages = entity.languages,
                    isFavorite = entity.isFavorite
                )
            }
        } catch (e: Exception) {
            Log.e("CountryRepository", "❌ Ошибка: ${e.message}")
            null
        }
    }

    /**
     * Получает детальную информацию о стране
     */
    suspend fun getCountryDetails(countryCode: String): Country? {
        Log.d("CountryRepository", "🔍 getCountryDetails($countryCode) вызван")

        return try {
            val response = api.getCountryByCode(countryCode)
            if (response.isSuccessful) {
                val countryDto = response.body()?.firstOrNull()
                countryDto?.let { dto ->
                    // Используем mapper
                    val currency = dto.currencies?.values?.firstOrNull()?.name
                    val languages = dto.languages?.values?.joinToString(", ")
                    val timezones = dto.timezones?.joinToString(", ")
                    val continents = dto.continents?.joinToString(", ")
                    val borders = dto.borders?.joinToString(", ")

                    Country(
                        id = dto.cca3 ?: "unknown",
                        name = dto.name?.common ?: "Unknown",
                        officialName = dto.name?.official ?: "",
                        capital = dto.capital?.firstOrNull(),
                        population = dto.population ?: 0L,
                        region = dto.region ?: "Unknown",
                        subregion = dto.subregion,
                        flagUrl = dto.flags?.png ?: "",
                        currency = currency,
                        languages = languages,
                        isFavorite = false, // временно
                        coatOfArmsUrl = dto.coatOfArms?.png,
                        googleMapsUrl = dto.maps?.googleMaps,
                        timezones = timezones,
                        continents = continents,
                        area = dto.area,
                        borders = borders
                    )
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("CountryRepository", "❌ Ошибка: ${e.message}")
            null
        }
    }

    /**
     * Получает статус избранного
     */
    suspend fun getFavoriteStatus(countryId: String): Boolean {
        return try {
            database.countryDao().getCountryById(countryId)?.isFavorite ?: false
        } catch (e: Exception) {
            false
        }
    }
}