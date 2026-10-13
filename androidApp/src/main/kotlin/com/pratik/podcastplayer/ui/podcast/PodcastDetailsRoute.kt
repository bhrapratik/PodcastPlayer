package com.pratik.podcastplayer.ui.podcast

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pratik.podcastplayer.data.model.Podcast
import com.pratik.podcastplayer.player.PodcastPlayer
import com.pratik.podcastplayer.presentation.podcast.PodcastViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun PodcastDetailsRoute(
    podcast: Podcast,
    onBack: () -> Unit,
    viewModel: PodcastViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val podcastPlayer: PodcastPlayer = koinInject()

    val podcastPlayerUiState by podcastPlayer.podcastPlayerUiState.collectAsState()




    LaunchedEffect(podcast.feedUrl) {
        viewModel.loadEpisodes(podcast.feedUrl)
    }

    PodcastDetailsScreen(
        podcast = podcast,
        episodes = uiState.episodes,
        isLoadingEpisodes = uiState.isLoadingEpisodes,
        episodeError = uiState.episodeError,
        onBack = onBack,
        playingEpisodeId = if (podcastPlayerUiState.isPlaying) podcastPlayerUiState.currentEpisodeId else null,
        onPlayClick = { episode ->
            if (podcastPlayerUiState.currentEpisodeId == episode.id && podcastPlayerUiState.isPlaying) {
                podcastPlayer.pause()
            } else {
                episode.audioUrl?.let { audioUrl ->
                    podcastPlayer.play(audioUrl, episode.id, episode.title)
                }
            }
        }
    )
}
