package com.pratik.podcastplayer.presentation.podcast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pratik.podcastplayer.data.model.Podcast
import com.pratik.podcastplayer.data.model.PodcastEpisode
import com.pratik.podcastplayer.data.repository.PodcastRepository
import com.pratik.podcastplayer.network.toUserMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds

private data class PodcastSearchState(
    val podcasts: List<Podcast> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

private data class EpisodeState(
    val episodes: List<PodcastEpisode> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class PodcastViewModel(
    private val repository: PodcastRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("technology")
    private val selectedFeedUrl = MutableStateFlow<String?>(null)

    private val searchState = searchQuery
        .map { it.trim() }
        .debounce(300.milliseconds)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            repository.searchPodcasts(query)
                .map { podcasts ->
                    PodcastSearchState(podcasts = podcasts)
                }
                .onStart {
                    emit(PodcastSearchState(isLoading = true))
                }
                .catch { exception ->
                    if (exception is CancellationException) throw exception

                    emit(
                        PodcastSearchState(
                            error = exception.toUserMessage
                        )
                    )
                }
        }

    private val episodeState = selectedFeedUrl
        .flatMapLatest { feedUrl ->
            if (feedUrl.isNullOrBlank()) {
                flowOf(EpisodeState())
            } else {
                repository.getEpisodes(feedUrl)
                    .map { episodes ->
                        EpisodeState(episodes = episodes)
                    }
                    .onStart {
                        emit(EpisodeState(isLoading = true))
                    }
                    .catch { exception ->
                        if (exception is CancellationException) throw exception

                        emit(
                            EpisodeState(
                                error = exception.toUserMessage
                            )
                        )
                    }
            }
        }

    val uiState: StateFlow<PodcastUiState> = combine(
        searchState,
        episodeState
    ) { search, episodes ->
        PodcastUiState(
            podcasts = search.podcasts,
            isLoading = search.isLoading,
            error = search.error,
            episodes = episodes.episodes,
            isLoadingEpisodes = episodes.isLoading,
            episodeError = episodes.error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PodcastUiState(isLoading = true)
    )

    fun onSearchQueryChanged(newQuery: String) {
        searchQuery.value = newQuery.ifBlank { "technology" }
    }

    fun loadEpisodes(feedUrl: String?) {
        selectedFeedUrl.value = feedUrl
    }
}
