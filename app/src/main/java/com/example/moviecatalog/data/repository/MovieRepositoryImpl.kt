package com.example.moviecatalog.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.example.moviecatalog.data.remote.TmdbApiService
import com.example.moviecatalog.domain.model.Movie
import com.example.moviecatalog.domain.model.toDomainModel
import com.example.moviecatalog.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class MovieRepositoryImpl (
    private val apiService: TmdbApiService,
    private val dataStore: DataStore<Preferences>
) : MovieRepository {
    private val apiKey = ""

    private companion object {
        val FAVORITE_IDS_KEY = stringSetPreferencesKey("favorite_movie_ids")
    }

    private val favoriteIdsFlow: Flow<Set<String>> = dataStore.data
        .map { preferences ->
            preferences[FAVORITE_IDS_KEY] ?: emptySet()
        }

    override suspend fun getPopularMovies(): List<Movie> {
        val response = apiService.getPopularMovies(apiKey)
        val favIds = favoriteIdsFlow.first()

        return response.results.map { dto ->
            dto.toDomainModel(isFavorite = favIds.contains(dto.id.toString()))
        }
    }

    override suspend fun searchMovies(query: String): List<Movie> {
        if (query.isBlank())
            return  emptyList()

        val response = apiService.searchMovies(apiKey, query)
        val favIds = favoriteIdsFlow.first()

        return response.results.map { dto ->
            dto.toDomainModel(isFavorite = favIds.contains(dto.id.toString()))
        }
    }

    override suspend fun getMovieDetails(movieId: Int): Movie {
        val dto = apiService.getMovieDetails(movieId, apiKey)
        val favIds = favoriteIdsFlow.first()

        return dto.toDomainModel(isFavorite = favIds.contains(movieId.toString()))
    }

    override fun getFavoriteMovies(popularAndSearchedMovies: List<Movie>): Flow<List<Movie>> {
        return favoriteIdsFlow.map { favIds -> popularAndSearchedMovies.filter { movie ->
            favIds.contains(movie.id.toString())
        } }
    }

    override fun isFavorite(movieId: Int): Flow<Boolean> {
        return  favoriteIdsFlow.map { favIds ->
            favIds.contains(movieId.toString())
        }
    }

    override suspend fun toggleFavorite(movieId: Int) {
        dataStore.edit { preferences ->
            val currentFavs = preferences[FAVORITE_IDS_KEY] ?: emptySet()
            val newFavs = currentFavs.toMutableSet()
            val idString = movieId.toString()

            if (newFavs.contains(idString)) {
                newFavs.remove(idString)
            } else {
                newFavs.add(idString)
            }

            preferences[FAVORITE_IDS_KEY] = newFavs
        }
    }
}