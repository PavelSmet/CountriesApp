package com.example.countriesapp.data.model

data class Country(
    val id: String,
    val name: String,
    val officialName: String,
    val capital: String?,
    val population: Long,
    val region: String,
    val subregion: String?,
    val flagUrl: String,
    val currency: String?,
    val languages: String?,
    val isFavorite: Boolean
) {
    companion object {
        fun formatPopulation(population: Long): String {
            return when {
                population >= 1_000_000 -> "${population / 1_000_000}M"
                population >= 1_000 -> "${population / 1_000}K"
                else -> population.toString()
            }
        }
    }
}