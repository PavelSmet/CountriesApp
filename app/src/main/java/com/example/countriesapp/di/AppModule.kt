package com.example.countriesapp.di

import com.example.countriesapp.data.local.AppDatabase
import com.example.countriesapp.data.remote.api.CountriesApi
import com.example.countriesapp.data.repository.CountryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCountriesApi(): CountriesApi {
        return NetworkModule.countriesApi
    }

    @Provides
    @Singleton
    fun provideDatabase(app: android.app.Application): AppDatabase {
        return AppDatabase.getDatabase(app)
    }

    @Provides
    @Singleton
    fun provideRepository(
        api: CountriesApi,
        database: AppDatabase
    ): CountryRepository {
        return CountryRepository(database, api)
    }
}