package com.example.moviecatalog.ui.screens

sealed class Screen(val route: String) {
    object Home: Screen("home")

    object Favorites: Screen("favorites")

    object MovieDetail: Screen("movie_detail/{movieId}") {
        fun createRoute(movieId: Int) = "movie_detail/$movieId"
    }
}