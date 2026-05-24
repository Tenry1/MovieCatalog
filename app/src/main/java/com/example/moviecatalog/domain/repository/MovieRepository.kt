package com.example.moviecatalog.domain.repository

import com.example.moviecatalog.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    suspend fun getPopularMovies(): List<Movie>
    suspend fun searchMovies(query: String): List<Movie>
    suspend fun getMovieDetails(movieId: Int): Movie

    //Favoritos
    fun getFavoriteMovies(popularAndSearchedMovies: List<Movie>): Flow<List<Movie>>
    fun isFavorite(movieId: Int): Flow<Boolean>
    suspend fun toggleFavorite(movieId: Int)
}