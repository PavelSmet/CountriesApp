package com.yourname.countriesapp.data.remote.api

import com.yourname.countriesapp.data.remote.dto.CountryDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CountriesApi {
    @GET("v3.1/all")
    suspend fun getAllCountries(): Response<List<CountryDto>>

    @GET("v3.1/name/{name}")
    suspend fun searchCountries(@Path("name") name: String): Response<List<CountryDto>>

    @GET("v3.1/region/{region}")
    suspend fun getCountriesByRegion(@Path("region") region: String): Response<List<CountryDto>>

    @GET("v3.1/alpha/{code}")
    suspend fun getCountryByCode(@Path("code") code: String): Response<List<CountryDto>>
}