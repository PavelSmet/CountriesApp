package com.yourname.countriesapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CountryDto(
    @SerializedName("name") val name: CountryNameDto,
    @SerializedName("capital") val capital: List<String>?,
    @SerializedName("population") val population: Long,
    @SerializedName("region") val region: String,
    @SerializedName("subregion") val subregion: String?,
    @SerializedName("flags") val flags: CountryFlagsDto,
    @SerializedName("currencies") val currencies: Map<String, CurrencyDto>?,
    @SerializedName("languages") val languages: Map<String, String>?
)

data class CountryNameDto(
    @SerializedName("common") val common: String,
    @SerializedName("official") val official: String
)

data class CountryFlagsDto(
    @SerializedName("png") val png: String,
    @SerializedName("svg") val svg: String
)

data class CurrencyDto(
    @SerializedName("name") val name: String,
    @SerializedName("symbol") val symbol: String?
)