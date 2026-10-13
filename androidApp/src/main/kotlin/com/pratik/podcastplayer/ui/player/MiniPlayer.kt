package com.pratik.podcastplayer.ui.player

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun MiniPlayer(
    episodeTitle: String,
    isPlaying: Boolean,
    isBuffering: Boolean,
    currentPosition: Long,
    duration: Long,
    onPlayPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    val targetProgress = if (duration > 0L) {
        (currentPosition.toFloat() / duration).coerceIn(0f, 1f)
    } else {
        0f
    }
    val progress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 300),
        label = "miniPlayerProgress"
    )

    val statusText = when {
        isBuffering -> "Loading…"
        duration > 0L -> {
            val left = formatTime(duration - currentPosition)
            if (isPlaying) "Now playing · $left left" else "Paused · $left left"
        }
        isPlaying -> "Now playing"
        else -> "Paused"
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        color = colors.surfaceContainerHigh,
        tonalElevation = 3.dp,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 10.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Artwork placeholder
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(colors.primaryContainer, colors.tertiaryContainer)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                    tint = colors.onPrimaryContainer
                )
            }

            Spacer(Modifier.width(12.dp))

            // Episode info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = episodeTitle,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = if (isPlaying) Modifier.basicMarquee() else Modifier
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isPlaying && !isBuffering) colors.primary else colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.width(10.dp))

            // Play / Pause wrapped in a progress ring
            Box(
                modifier = Modifier.size(52.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isBuffering) {
                    CircularProgressIndicator(
                        modifier = Modifier.fillMaxSize(),
                        color = colors.primary,
                        strokeWidth = 3.dp,
                        trackColor = colors.outlineVariant,
                        strokeCap = StrokeCap.Round
                    )
                } else {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        color = colors.primary,
                        strokeWidth = 3.dp,
                        trackColor = colors.outlineVariant,
                        strokeCap = StrokeCap.Round
                    )
                }

                FilledIconButton(
                    onClick = onPlayPauseClick,
                    enabled = !isBuffering,
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape
                ) {
                    Crossfade(targetState = isPlaying, label = "playPauseIcon") { playing ->
                        Icon(
                            imageVector = if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (playing) "Pause" else "Play",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}