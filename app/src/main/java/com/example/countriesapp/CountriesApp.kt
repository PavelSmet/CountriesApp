package com.example.countriesapp

import android.app.Application
import androidx.room.Room
import com.example.countriesapp.data.local.AppDatabase
import com.example.countriesapp.data.remote.api.CountriesApi
import com.example.countriesapp.data.repository.CountryRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class CountriesApp : Application() {

    // Ленивая инициализация Retrofit с правильным baseUrl
    private val retrofit: Retrofit by lazy {
        // Добавляем логирование для отладки
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl("https://restcountries.com/") // ТОЛЬКО базовый URL
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API
    val countriesApi: CountriesApi by lazy {
        retrofit.create(CountriesApi::class.java)
    }

    // База данных
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "countries_database"
        )
            .fallbackToDestructiveMigration() // Удаляет и создает заново при миграциях
            .build()
    }

    // Репозиторий
    val repository: CountryRepository by lazy {
        CountryRepository(database, countriesApi)
    }

    companion object {
        // Более безопасный способ
        private var instance: CountriesApp? = null

        fun getInstance(): CountriesApp {
            return instance ?: throw IllegalStateException("Application not initialized")
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}