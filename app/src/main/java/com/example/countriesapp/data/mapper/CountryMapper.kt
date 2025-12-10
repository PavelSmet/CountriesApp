package com.example.countriesapp.data.mapper

import com.example.countriesapp.data.local.entity.CountryEntity
import com.example.countriesapp.data.model.Country
import com.example.countriesapp.data.remote.dto.CountryDto

// DTO -> Entity
fun CountryDto.toCountryEntity(): CountryEntity {
    return CountryEntity(
        id = name.common.replace(" ", "_"),
        name = name.common,
        officialName = name.official,
        capital = capital?.firstOrNull(),
        population = population,
        region = region,
        subregion = subregion,
        flagUrl = flags.png,
        currency = currencies?.values?.firstOrNull()?.name,
        languages = languages?.values?.joinToString(", ")
    )
}

// Entity -> Domain Model
fun CountryEntity.toCountry(): Country {
    return Country(
        id = id,
        name = name,
        officialName = officialName,
        capital = capital,
        population = population,
        region = region,
        subregion = subregion,
        flagUrl = flagUrl,
        currency = currency,
        languages = languages,
        isFavorite = isFavorite
    )
}