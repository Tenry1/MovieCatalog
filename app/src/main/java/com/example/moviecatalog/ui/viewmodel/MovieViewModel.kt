package com.example.moviecatalog.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import retrofit2.HttpException
import com.example.moviecatalog.MovieApplication
import com.example.moviecatalog.domain.model.Movie
import com.example.moviecatalog.domain.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.IOException

sealed interface MovieUiState {
    data class Success(val movies: List<Movie>) : MovieUiState

    object Error: MovieUiState
    object Loading : MovieUiState
}

class MovieViewModel(private val movieRepository: MovieRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<MovieUiState> (MovieUiState.Loading)

    val uiState: StateFlow<MovieUiState> = _uiState.asStateFlow()

    var searchQuery by mutableStateOf("")
        private set

    init {
        getPopularMovies()
    }

    fun getPopularMovies() {
        viewModelScope.launch {
            _uiState.value = MovieUiState.Loading

            try {
                val movies = movieRepository.getPopularMovies()
                _uiState.value = MovieUiState.Success(movies)
            } catch (e: IOException) {
                // Erro sem internet
                _uiState.value = MovieUiState.Error
            } catch (e: HttpException) {
                // Erro de resposta da API
                _uiState.value = MovieUiState.Error
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query

        if(query.isBlank()) {
            getPopularMovies()
        } else {
            searchMovies(query)
        }
    }

    private fun searchMovies(query: String) {
        viewModelScope.launch {
            _uiState.value = MovieUiState.Loading

            try {
                val movies = movieRepository.searchMovies(query)
                _uiState.value = MovieUiState.Success(movies)
            } catch (e: IOException) {
                _uiState.value = MovieUiState.Error
            } catch (e: HttpException) {
                _uiState.value = MovieUiState.Error
            }
        }
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            movieRepository.toggleFavorite(movie.id)

            val currentState = _uiState.value

            if (currentState is MovieUiState.Success) {
                val updatedMovies = currentState.movies.map {
                    if (it.id == movie.id)
                        it.copy(isFavorite = !it.isFavorite)
                    else
                        it
                }

                _uiState.value = MovieUiState.Success(updatedMovies)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MovieApplication)

                val movieRepository = application.container.movieRepository

                MovieViewModel(movieRepository = movieRepository)
            }
        }
    }
}