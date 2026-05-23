package com.example.moviecatalog.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val overview: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val voteAverage: Double,
    val isFavorite: Boolean = false
) {
    val fullPosterUrl: String
        get() = if (posterPath != null)
            "https://image.tmdb.org/t/p/w500$posterPath"
        else
            ""

    val fullBackdropUrl: String
        get() = if (backdropPath != null)
            "https://image.tmdb.org/t/p/w780$backdropPath"
        else
            ""
}