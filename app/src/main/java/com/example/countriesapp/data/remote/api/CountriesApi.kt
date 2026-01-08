package com.example.countriesapp.data.remote.api

import com.example.countriesapp.data.remote.dto.CountryDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CountriesApi {

    // Основной запрос для списка стран (минимум полей для скорости)
    @GET("v3.1/all")
    suspend fun getAllCountries(
        @Query("fields") fields: String = "name,capital,population,flags,region,subregion,cca3,currencies,languages"
    ): Response<List<CountryDto>>

    // Запрос для детальной информации о стране (все поля)
    @GET("v3.1/alpha/{code}")
    suspend fun getCountryByCode(
        @Path("code") code: String,
        @Query("fields") fields: String = "name,capital,population,flags,region,subregion,cca3,currencies,languages,coatOfArms,maps,timezones,continents,area,borders,startOfWeek,postalCode,car,idd,translations"
    ): Response<List<CountryDto>>
}