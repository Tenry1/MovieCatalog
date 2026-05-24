package com.example.moviecatalog.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.moviecatalog.data.remote.TmdbApiService
import com.example.moviecatalog.data.repository.MovieRepositoryImpl
import com.example.moviecatalog.domain.repository.MovieRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "movie_favorites")

interface AppContainer {
    val movieRepository: MovieRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val baseUrl = "https://api.themoviedb.org/3/"

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val retrofitService: TmdbApiService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)

            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(TmdbApiService::class.java)
    }

    override val movieRepository: MovieRepository by lazy {
        MovieRepositoryImpl(retrofitService, context.dataStore)
    }
}
