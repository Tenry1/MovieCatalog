package com.example.moviecatalog.domain.model

import com.example.moviecatalog.data.model.MovieDto

fun MovieDto.toDomainModel(isFavorite: Boolean = false): Movie {
    return Movie(
        id = this.id,
        title = this.title,
        overview = this.overview,
        posterPath = this.posterPath,
        backdropPath = this.backdropPath,
        releaseDate = this.releaseDate ?: "",
        voteAverage = this.voteAverage,
        isFavorite = isFavorite
    )
}