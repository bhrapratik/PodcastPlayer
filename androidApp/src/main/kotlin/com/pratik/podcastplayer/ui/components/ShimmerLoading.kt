package com.pratik.podcastplayer.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

/** A slowly sweeping gradient used to fill skeleton placeholders while content loads. */
@Composable
fun rememberShimmerBrush(): Brush {
    val base = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    val highlight = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    return Brush.linearGradient(
        colors = listOf(base, highlight, base),
        start = Offset(translateAnim * 600f - 300f, 0f),
        end = Offset(translateAnim * 600f, 0f)
    )
}

@Composable
private fun ShimmerBlock(
    modifier: Modifier,
    brush: Brush,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(6.dp)
) {
    Box(modifier = modifier.background(brush = brush, shape = shape))
}

/** Placeholder matching the shape of a podcast row in [com.pratik.podcastplayer.ui.podcast.PodcastScreen]. */
@Composable
fun PodcastListItemShimmer(modifier: Modifier = Modifier) {
    val brush = rememberShimmerBrush()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShimmerBlock(
                modifier = Modifier.size(56.dp),
                brush = brush,
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                ShimmerBlock(modifier = Modifier.fillMaxWidth(0.7f).height(16.dp), brush = brush)
                Spacer(Modifier.height(8.dp))
                ShimmerBlock(modifier = Modifier.fillMaxWidth(0.4f).height(13.dp), brush = brush)
            }
        }
    }
}

/** Placeholder matching the shape of an episode row in [com.pratik.podcastplayer.ui.podcast.PodcastDetailsScreen]. */
@Composable
fun EpisodeListItemShimmer(modifier: Modifier = Modifier) {
    val brush = rememberShimmerBrush()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShimmerBlock(
                modifier = Modifier.size(44.dp),
                brush = brush,
                shape = CircleShape
            )

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                ShimmerBlock(modifier = Modifier.fillMaxWidth(0.8f).height(15.dp), brush = brush)
                Spacer(Modifier.height(8.dp))
                ShimmerBlock(modifier = Modifier.fillMaxWidth(0.35f).height(12.dp), brush = brush)
            }
        }
    }
}
