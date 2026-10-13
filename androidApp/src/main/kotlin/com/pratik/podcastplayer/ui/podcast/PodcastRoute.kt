
package com.pratik.podcastplayer.ui.podcast

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pratik.podcastplayer.data.model.Podcast
import com.pratik.podcastplayer.presentation.podcast.PodcastViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PodcastRoute(
    onPodcastClick: (Podcast) -> Unit,
    viewModel: PodcastViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PodcastScreen(
        uiState = uiState,
        onSearch = viewModel::onSearchQueryChanged,
        onPodcastClick = onPodcastClick
    )
}
