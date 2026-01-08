package com.example.countriesapp.data.model

data class Country(
    val id: String,
    val name: String,
    val officialName: String,
    val capital: String?,
    val population: Long,
    val region: String,
    val subregion: String?,
    val flagUrl: String?,
    val currency: String?,
    val languages: String?,
    val isFavorite: Boolean = false,

    // Новые поля для детального экрана
    val coatOfArmsUrl: String? = null,
    val googleMapsUrl: String? = null,
    val timezones: String? = null,
    val continents: String? = null,
    val area: Double? = null,
    val borders: String? = null,
    val startOfWeek: String? = null,
    val carSide: String? = null,
    val iddCode: String? = null
)
{
    // Вспомогательная функция для форматирования населения
    fun formatPopulation(): String {
        return when {
            population >= 1_000_000_000 -> "${population / 1_000_000_000} млрд"
            population >= 1_000_000 -> "${population / 1_000_000} млн"
            population >= 1_000 -> "${population / 1_000} тыс"
            else -> population.toString()
        }
    }

    // Форматирование площади
    fun formatArea(): String? {
        return area?.let {
            val formatted = if (it >= 1_000_000) {
                "${"%.1f".format(it / 1_000_000)} млн км²"
            } else if (it >= 1_000) {
                "${"%.1f".format(it / 1_000)} тыс км²"
            } else {
                "${"%.0f".format(it)} км²"
            }
            formatted
        }
    }
}