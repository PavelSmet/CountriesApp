package com.example.countriesapp.data.mapper

import com.example.countriesapp.data.local.entity.CountryEntity
import com.example.countriesapp.data.model.Country
import com.example.countriesapp.data.remote.dto.CountryDto
import java.util.UUID

fun CountryDto.toCountryEntity(): CountryEntity {
    val currency = this.currencies?.values?.firstOrNull()?.name
    val languages = this.languages?.values?.joinToString(", ")

    return CountryEntity(
        id = this.cca3 ?: "unknown_${UUID.randomUUID()}",
        name = this.name?.common ?: "Unknown",
        officialName = this.name?.official ?: "",
        capital = this.capital?.firstOrNull(),
        population = this.population ?: 0L,
        region = this.region ?: "Unknown",
        subregion = this.subregion,
        flagUrl = this.flags?.png ?: "",
        currency = currency,
        languages = languages,
        isFavorite = false
    )
}

fun CountryDto.toCountry(): Country {
    val currency = this.currencies?.values?.firstOrNull()?.name
    val languages = this.languages?.values?.joinToString(", ")
    val timezones = this.timezones?.joinToString(", ")
    val continents = this.continents?.joinToString(", ")
    val borders = this.borders?.joinToString(", ")
    val carSide = this.car?.side
    val iddCode = this.idd?.let { "${it.root}${it.suffixes?.firstOrNull() ?: ""}" }

    return Country(
        id = this.cca3 ?: "unknown",
        name = this.name?.common ?: "Unknown",
        officialName = this.name?.official ?: "",
        capital = this.capital?.firstOrNull(),
        population = this.population ?: 0L,
        region = this.region ?: "Unknown",
        subregion = this.subregion,
        flagUrl = this.flags?.png ?: "",
        currency = currency,
        languages = languages,
        isFavorite = false,
        coatOfArmsUrl = this.coatOfArms?.png,
        googleMapsUrl = this.maps?.googleMaps,
        timezones = timezones,
        continents = continents,
        area = this.area,
        borders = borders,
        startOfWeek = this.startOfWeek,
        carSide = carSide,
        iddCode = iddCode
    )
}

fun CountryEntity.toCountry(): Country {
    return Country(
        id = this.id,
        name = this.name,
        officialName = this.officialName,
        capital = this.capital,
        population = this.population,
        region = this.region,
        subregion = this.subregion,
        flagUrl = this.flagUrl,
        currency = this.currency,
        languages = this.languages,
        isFavorite = this.isFavorite
    )
}