package com.example.countriesapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CountryDto(
    @SerializedName("name")
    val name: CountryNameDto?,

    @SerializedName("capital")
    val capital: List<String>?,

    @SerializedName("population")
    val population: Long?,

    @SerializedName("flags")
    val flags: CountryFlagsDto?,

    @SerializedName("region")
    val region: String?,

    @SerializedName("subregion")
    val subregion: String?,

    @SerializedName("cca3")
    val cca3: String?,

    @SerializedName("currencies")
    val currencies: Map<String, CurrencyDto>?,

    @SerializedName("languages")
    val languages: Map<String, String>?,

    // Новые поля для детальной информации
    @SerializedName("coatOfArms")
    val coatOfArms: CoatOfArmsDto?,

    @SerializedName("maps")
    val maps: MapsDto?,

    @SerializedName("timezones")
    val timezones: List<String>?,

    @SerializedName("continents")
    val continents: List<String>?,

    @SerializedName("area")
    val area: Double?,

    @SerializedName("borders")
    val borders: List<String>?,

    @SerializedName("startOfWeek")
    val startOfWeek: String?,

    @SerializedName("postalCode")
    val postalCode: PostalCodeDto?,

    @SerializedName("car")
    val car: CarDto?,

    @SerializedName("idd")
    val idd: IddDto?,

    @SerializedName("translations")
    val translations: Map<String, TranslationDto>?
)

data class CountryNameDto(
    @SerializedName("common")
    val common: String?,

    @SerializedName("official")
    val official: String?
)

data class CountryFlagsDto(
    @SerializedName("png")
    val png: String?,

    @SerializedName("svg")
    val svg: String?,

    @SerializedName("alt")
    val alt: String?
)

data class CurrencyDto(
    @SerializedName("name")
    val name: String?,

    @SerializedName("symbol")
    val symbol: String?
)

data class CoatOfArmsDto(
    @SerializedName("png")
    val png: String?,

    @SerializedName("svg")
    val svg: String?
)

data class MapsDto(
    @SerializedName("googleMaps")
    val googleMaps: String?,

    @SerializedName("openStreetMaps")
    val openStreetMaps: String?
)

data class PostalCodeDto(
    @SerializedName("format")
    val format: String?,

    @SerializedName("regex")
    val regex: String?
)

data class CarDto(
    @SerializedName("signs")
    val signs: List<String>?,

    @SerializedName("side")
    val side: String?
)

data class IddDto(
    @SerializedName("root")
    val root: String?,

    @SerializedName("suffixes")
    val suffixes: List<String>?
)

data class TranslationDto(
    @SerializedName("official")
    val official: String?,

    @SerializedName("common")
    val common: String?
)