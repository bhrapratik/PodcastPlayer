package com.pratik.podcastplayer.player

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.pratik.podcastplayer.MainActivity

/**
 * Hosts the real [ExoPlayer] + [MediaSession] as a foreground service so playback (and its
 * notification) survives the app being swiped away from recents, not just activity rotation.
 * The UI never talks to this service directly - it connects a [androidx.media3.session.MediaController]
 * to it from [PodcastPlayer].
 */
class PlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()

        val player = ExoPlayer.Builder(this).build()

        val sessionActivity = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(sessionActivity)
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        // Nothing was ever loaded for this session: no reason to keep the service alive.
        // Otherwise keep it running (even if paused) so playback can resume from the notification.
        val player = mediaSession?.player
        if (player == null || player.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
