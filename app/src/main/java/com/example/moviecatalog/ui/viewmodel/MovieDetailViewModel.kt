package com.example.moviecatalog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.moviecatalog.MovieApplication
import com.example.moviecatalog.domain.model.Movie
import com.example.moviecatalog.domain.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

sealed interface MovieDetailUiState {
    data class Success(val movie: Movie) : MovieDetailUiState
    object Error : MovieDetailUiState
    object Loading : MovieDetailUiState
}

class MovieDetailViewModel(private val movieRepository: MovieRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<MovieDetailUiState> (MovieDetailUiState.Loading)

    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    fun getMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _uiState.value = MovieDetailUiState.Loading

            try {
                val movie = movieRepository.getMovieDetails(movieId)

                _uiState.value = MovieDetailUiState.Success(movie)
            } catch (e: IOException) {
                _uiState.value = MovieDetailUiState.Error
            } catch (e: HttpException) {
                _uiState.value = MovieDetailUiState.Error
            }
        }
    }

    fun toggleFavorite(movieId: Int) {
        viewModelScope.launch {
            movieRepository.toggleFavorite(movieId)

            val currentState = _uiState.value

            if(currentState is MovieDetailUiState.Success) {
                val updatedMovie = currentState.movie.copy(isFavorite = !currentState.movie.isFavorite)

                _uiState.value = MovieDetailUiState.Success(updatedMovie)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MovieApplication)

                val movieRepository = application.container.movieRepository

                MovieDetailViewModel(movieRepository = movieRepository)
            }
        }
    }
}