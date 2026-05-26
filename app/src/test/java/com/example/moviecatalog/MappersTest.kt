package com.example.moviecatalog

import com.example.moviecatalog.data.model.MovieDto
import com.example.moviecatalog.domain.model.toDomainModel
import org.junit.Assert.assertEquals
import org.junit.Test

class MappersTest {
    @Test
    fun movieDto_toDomainModel_convertsCorrectly() {
        val dto = MovieDto(
            id = 999,
            title = "Filme de Teste",
            overview = "Sinopse de teste",
            posterPath = "/poster.jpg",
            backdropPath = "backdrop.jpg",
            releaseDate = "2026-01-01",
            voteAverage = 9.2
        )

        val movie = dto.toDomainModel(isFavorite = true)

        assertEquals(999, movie.id)
        assertEquals("Filme de Teste", movie.title)
        assertEquals("Sinopse de teste", movie.overview)
        assertEquals(9.2, movie.voteAverage, 0.01)
        assertEquals(true, movie.isFavorite)

        assertEquals("https://image.tmdb.org/t/p/w500/poster.jpg", movie.fullPosterUrl)
        assertEquals("https://image.tmdb.org/t/p/w780/backdrop.jpg", movie.fullBackdropUrl)
    }
}