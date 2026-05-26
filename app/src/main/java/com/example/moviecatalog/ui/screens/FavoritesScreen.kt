package com.example.moviecatalog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.moviecatalog.domain.model.Movie

@Composable
fun FavoritesScreen (
    favoriteMovies: List<Movie>,
    onMovieClick: (Int) -> Unit,
    onFavoriteToggle: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    if (favoriteMovies.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    text = "💖",
                    style = MaterialTheme.typography.displayLarge
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Nenhum filme adicionado aos favoritos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Explora os filmes populares e clica no ícone de coração para os veres listados aqui de forma automática.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(favoriteMovies) { movie ->
                MovieCard(
                    movie = movie,
                    onClick = {
                        onMovieClick(movie.id)
                    },
                    onFavoriteToggle = {
                        onFavoriteToggle(movie)
                    }
                )
            }
        }
    }
}
