package com.example.moviecatalog

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moviecatalog.ui.screens.FavoritesScreen
import com.example.moviecatalog.ui.screens.HomeScreen
import com.example.moviecatalog.ui.screens.MovieDetailScreen
import com.example.moviecatalog.ui.screens.Screen
import com.example.moviecatalog.ui.theme.MovieCatalogTheme
import com.example.moviecatalog.ui.viewmodel.MovieDetailUiState
import com.example.moviecatalog.ui.viewmodel.MovieDetailViewModel
import com.example.moviecatalog.ui.viewmodel.MovieUiState
import com.example.moviecatalog.ui.viewmodel.MovieViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieCatalogTheme {
                MovieCataloApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieCataloApp() {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val movieViewModel: MovieViewModel = viewModel(factory = MovieViewModel.Factory)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Movie Catalog",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)
                )
                HorizontalDivider()

                // Item Home
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Filmes Populares") },
                    selected = currentRoute == Screen.Home.route,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                // Item Favoritos
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
                    label = { Text("Meus Favoritos") },
                    selected = currentRoute == Screen.Favorites.route,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate(Screen.Favorites.route)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                val context = androidx.compose.ui.platform.LocalContext.current

                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = when (currentRoute) {
                                Screen.Home.route -> "Catálogo de Filmes"
                                Screen.Favorites.route -> "Meus Favoritos"
                                else -> "Detalhes do Filme"
                            }
                        )
                    },
                    navigationIcon = {
                        if (currentRoute?.startsWith("movie_detail") == true) {
                            IconButton(onClick = { navController.popBackStack()}) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                            }
                        } else {
                            IconButton(onClick = { coroutineScope.launch { drawerState.open() }}) {
                                Icon(Icons.Default.Menu, contentDescription = "Abrir Menu")
                            }
                        }
                    },
                    actions = {
                        if (currentRoute?.startsWith("movie_detail") == true) {
                            IconButton(onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND

                                    putExtra(Intent.EXTRA_TEXT, "Veja este filme que encontrei no MovieCatalog!")

                                    type = "text/plain"
                                }

                                val shareIntent = Intent.createChooser(sendIntent, "Partilhar Filme")

                                context.startActivity(shareIntent)
                            }) {
                                Icon(Icons.Default.Share, contentDescription = "Partilhar")
                            }
                        }
                    },

                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                // 1. Rota Ecrã Inicial
                composable(Screen.Home.route) {
                    val uiState by movieViewModel.uiState.collectAsState()
                    HomeScreen(
                        uiState = uiState,
                        searchQuery = movieViewModel.searchQuery,
                        onSearchQueryChange = { movieViewModel.onSearchQueryChange(it) },
                        onMovieClick = { movieId ->
                            navController.navigate(Screen.MovieDetail.createRoute(movieId))
                        },
                        onFavoriteToggle = { movie ->
                            movieViewModel.toggleFavorite(movie)
                        },
                        onRetry = { movieViewModel.getPopularMovies() }
                    )
                }
                // 2. Rota Favoritos
                composable(Screen.Favorites.route) {
                    val uiState by movieViewModel.uiState.collectAsState()
                    val favoriteMovies = if (uiState is MovieUiState.Success) {
                        (uiState as MovieUiState.Success).movies.filter { it.isFavorite }
                    } else {
                        emptyList()
                    }
                    FavoritesScreen(
                        favoriteMovies = favoriteMovies,
                        onMovieClick = { movieId ->
                            navController.navigate(Screen.MovieDetail.createRoute(movieId))
                        },
                        onFavoriteToggle = { movie ->
                            movieViewModel.toggleFavorite(movie)
                        }
                    )
                }
                // 3. Rota Detalhes (Passa o id do filme como argumento)
                composable(
                    route = Screen.MovieDetail.route,
                    arguments = listOf(navArgument("movieId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val movieId = backStackEntry.arguments?.getInt("movieId") ?: -1
                    val detailViewModel: MovieDetailViewModel = viewModel(factory = MovieDetailViewModel.Factory)

                    // Dispara o carregamento dos detalhes do filme específico
                    LaunchedEffect(movieId) {
                        detailViewModel.getMovieDetails(movieId)
                    }
                    val detailUiState by detailViewModel.uiState.collectAsState()
                    MovieDetailScreen(
                        uiState = detailUiState,
                        onFavoriteToggle = { id ->
                            detailViewModel.toggleFavorite(id)
                            // Sincroniza localmente com o ViewModel da Home para a mudança refletir instantaneamente
                            if (detailUiState is MovieDetailUiState.Success) {
                                val movie = (detailUiState as MovieDetailUiState.Success).movie
                                movieViewModel.toggleFavorite(movie)
                            }
                        },
                        onRetry = { detailViewModel.getMovieDetails(movieId) }
                    )
                }
            }
        }
    }
}