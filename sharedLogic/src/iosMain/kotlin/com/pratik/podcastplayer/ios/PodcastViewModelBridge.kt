package com.pratik.podcastplayer.ios

import com.pratik.podcastplayer.data.repository.PodcastRepository
import com.pratik.podcastplayer.presentation.podcast.PodcastUiState
import com.pratik.podcastplayer.presentation.podcast.PodcastViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.mp.KoinPlatform

/**
 * Swift can't consume a Kotlin `StateFlow` directly, so this wraps [PodcastViewModel] with a
 * plain callback instead. One instance is shared for the app's lifetime from Swift, mirroring
 * the single shared `PodcastViewModel` instance Android's `koinViewModel()` resolves across the
 * Discover and Details screens.
 */
class PodcastViewModelBridge {
    private val viewModel = PodcastViewModel(KoinPlatform.getKoin().get<PodcastRepository>())
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    /** Starts forwarding every [PodcastUiState] emission to [onChange] until the app exits. */
    fun start(onChange: (PodcastUiState) -> Unit) {
        viewModel.uiState.onEach { onChange(it) }.launchIn(scope)
    }

    fun onSearchQueryChanged(query: String) {
        viewModel.onSearchQueryChanged(query)
    }

    fun loadEpisodes(feedUrl: String?) {
        viewModel.loadEpisodes(feedUrl)
    }
}
