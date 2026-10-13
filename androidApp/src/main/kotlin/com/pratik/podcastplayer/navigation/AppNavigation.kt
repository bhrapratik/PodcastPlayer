package com.pratik.podcastplayer.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pratik.podcastplayer.data.model.Podcast
import com.pratik.podcastplayer.player.PodcastPlayer
import com.pratik.podcastplayer.ui.components.EmptyState
import com.pratik.podcastplayer.ui.player.MiniPlayer
import com.pratik.podcastplayer.ui.podcast.PodcastDetailsRoute
import com.pratik.podcastplayer.ui.podcast.PodcastRoute
import com.pratik.podcastplayer.ui.profile.ProfileRoute
import org.koin.compose.koinInject

private enum class AppTab(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    Discover("discover", "Discover", Icons.Default.Explore),
    Profile("profile", "Profile", Icons.Default.PersonOutline)
}

private const val DETAILS_ROUTE = "podcastDetails/{podcastId}"

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val podcastPlayer: PodcastPlayer = koinInject()

    val podcastPlayerUiState by podcastPlayer.podcastPlayerUiState.collectAsState()


    var selectedPodcast by remember {
        mutableStateOf<Podcast?>(null)
    }

    val backStackEntry by
    navController.currentBackStackEntryAsState()

    val currentRoute = backStackEntry?.destination?.route
    val isDetailsScreen = currentRoute == DETAILS_ROUTE


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Column {
                if (podcastPlayerUiState.currentEpisodeId != null && podcastPlayerUiState.currentEpisodeTitle != null) {
                    MiniPlayer(
                        episodeTitle = podcastPlayerUiState.currentEpisodeTitle!!,
                        isPlaying = podcastPlayerUiState.isPlaying,
                        isBuffering = podcastPlayerUiState.isBuffering,
                        currentPosition = podcastPlayerUiState.currentPosition,
                        duration = podcastPlayerUiState.duration,
                        onPlayPauseClick = {
                            if (podcastPlayerUiState.isPlaying) {
                                podcastPlayer.pause()
                            } else {
                                podcastPlayer.resume()
                            }
                        }
                    )
                }

                if (!isDetailsScreen) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp
                    ) {
                        AppTab.entries.forEach { tab ->
                            NavigationBarItem(
                                selected = currentRoute == tab.route,
                                onClick = {
                                    navController.navigate(tab.route) {
                                        popUpTo(
                                            navController.graph.findStartDestination().id
                                        ) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = null
                                    )
                                },
                                label = { Text(tab.label) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppTab.Discover.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppTab.Discover.route) {
                PodcastRoute(
                    onPodcastClick = { podcast ->
                        selectedPodcast = podcast
                        navController.navigate(
                            "podcastDetails/${podcast.id}"
                        )
                    }
                )
            }

            composable(AppTab.Profile.route) {
                ProfileRoute()
            }

            composable(
                route = DETAILS_ROUTE,
                arguments = listOf(
                    navArgument("podcastId") {
                        type = NavType.LongType
                    }
                )
            ) { entry ->
                val podcastId =
                    entry.arguments?.getLong("podcastId")

                val podcast = selectedPodcast
                    ?.takeIf { it.id == podcastId }

                if (podcast != null) {
                    PodcastDetailsRoute(
                        podcast = podcast,
                        onBack = { navController.popBackStack() },
                    )
                } else {
                    PlaceholderScreen(
                        icon = Icons.Default.ErrorOutline,
                        title = "Podcast unavailable",
                        message = "Please return to Discover and select it again."
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(
    icon: ImageVector,
    title: String,
    message: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        EmptyState(
            icon = icon,
            title = title,
            message = message
        )
    }
}
