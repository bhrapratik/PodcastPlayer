package com.pratik.podcastplayer.player

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * Talks to [PlaybackService] through a [MediaController] instead of owning the [androidx.media3.exoplayer.ExoPlayer]
 * directly, so playback (and its system notification) keeps running in the foreground service
 * after the app is backgrounded or swiped from recents - not just while this object is alive.
 */
class PodcastPlayer(context: Context) {

    data class PodcastPlayerUiState(
        val isPlaying: Boolean = false,
        val isBuffering: Boolean = false,
        val currentEpisodeId: String? = null,
        val currentEpisodeTitle: String? = null,
        val currentPosition: Long = 0L,
        val duration: Long = 0L
    )

    private val _podcastPlayerUiState = MutableStateFlow(PodcastPlayerUiState())
    val podcastPlayerUiState: StateFlow<PodcastPlayerUiState> = _podcastPlayerUiState.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var progressJob: Job? = null

    private var controller: MediaController? = null

    // A command issued before the controller finished connecting (e.g. tapping play right
    // after launch) is held here and flushed once the connection completes.
    private var pendingCommand: ((MediaController) -> Unit)? = null

    private val controllerFuture: ListenableFuture<MediaController> = MediaController.Builder(
        context,
        SessionToken(context, ComponentName(context, PlaybackService::class.java))
    ).buildAsync()

    init {
        controllerFuture.addListener(
            {
                val connected = controllerFuture.get()
                controller = connected
                connected.addListener(playerListener)
                syncStateFrom(connected)

                pendingCommand?.let { command ->
                    command(connected)
                    pendingCommand = null
                }
            },
            MoreExecutors.directExecutor()
        )
    }

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            _podcastPlayerUiState.update { it.copy(isBuffering = playbackState == Player.STATE_BUFFERING) }

            if (playbackState == Player.STATE_ENDED) {
                _podcastPlayerUiState.update { it.copy(isPlaying = false) }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _podcastPlayerUiState.update { it.copy(isPlaying = isPlaying) }

            if (isPlaying) {
                startProgressUpdates()
            } else {
                progressJob?.cancel()
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            Log.e("PodcastPlayer", "Playback error: ${error.message}", error)
        }
    }

    fun play(url: String, episodeId: String, episodeTitle: String) {
        withController { mediaController ->
            if (_podcastPlayerUiState.value.currentEpisodeId == episodeId) {
                // Resume the current episode from its paused position.
                mediaController.play()
            } else {
                // Start a new episode.
                _podcastPlayerUiState.update {
                    it.copy(currentEpisodeId = episodeId, currentEpisodeTitle = episodeTitle)
                }
                mediaController.setMediaItem(MediaItem.fromUri(url))
                mediaController.prepare()
                mediaController.play()
            }
        }
    }

    fun pause() {
        withController { it.pause() }
    }

    fun resume() {
        withController { it.play() }
    }

    /** Releases this UI's connection to the playback service; playback itself is unaffected. */
    fun release() {
        progressJob?.cancel()
        controller?.removeListener(playerListener)
        controller = null
        MediaController.releaseFuture(controllerFuture)
    }

    private fun withController(action: (MediaController) -> Unit) {
        val connected = controller
        if (connected != null) {
            action(connected)
        } else {
            pendingCommand = action
        }
    }

    private fun syncStateFrom(mediaController: MediaController) {
        _podcastPlayerUiState.update {
            it.copy(
                isPlaying = mediaController.isPlaying,
                isBuffering = mediaController.playbackState == Player.STATE_BUFFERING
            )
        }

        if (mediaController.isPlaying) {
            startProgressUpdates()
        }
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                val activeController = controller
                if (activeController != null) {
                    val position = activeController.currentPosition
                    val duration = activeController.duration.takeIf { it > 0 } ?: 0L
                    _podcastPlayerUiState.update {
                        it.copy(currentPosition = position, duration = duration)
                    }
                }
                delay(500.milliseconds)
            }
        }
    }
}
