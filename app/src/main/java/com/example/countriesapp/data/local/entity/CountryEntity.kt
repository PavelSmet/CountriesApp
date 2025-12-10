package com.example.countriesapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "countries")
data class CountryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val officialName: String,
    val capital: String?,
    val population: Long,
    val region: String,
    val subregion: String?,
    val flagUrl: String,
    val currency: String?,
    val languages: String?,
    val isFavorite: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)