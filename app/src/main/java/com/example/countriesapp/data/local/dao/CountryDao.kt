package com.example.countriesapp.data.local.dao

import androidx.room.*
import com.example.countriesapp.data.local.entity.CountryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CountryDao {
    @Query("SELECT * FROM countries ORDER BY name")
    fun getAllCountries(): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries WHERE isFavorite = 1 ORDER BY name")
    fun getFavoriteCountries(): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries WHERE region = :region ORDER BY name")
    fun getCountriesByRegion(region: String): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries WHERE id = :id")
    suspend fun getCountryById(id: String): CountryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountry(country: CountryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(countries: List<CountryEntity>)

    @Update
    suspend fun updateCountry(country: CountryEntity)

    @Query("UPDATE countries SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean)

    @Query("DELETE FROM countries")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM countries")
    suspend fun getCount(): Int

    @Query("DELETE FROM countries WHERE id NOT IN (:ids)")
    suspend fun deleteExcept(ids: List<String>)
}