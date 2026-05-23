package com.example.moviecatalog.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieResponseDto(
    @SerialName("results") val results: List<MovieDto>
)

@Serializable
data class MovieDto (
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("overview") val overview: String,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("vote_average") val voteAverage: Double
)