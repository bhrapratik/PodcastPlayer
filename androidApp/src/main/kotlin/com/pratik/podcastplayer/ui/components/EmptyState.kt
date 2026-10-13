package com.pratik.podcastplayer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Shared visual treatment for empty/error/placeholder states so loading
 * failures and "nothing here yet" screens look consistent across the app.
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    tone: EmptyStateTone = EmptyStateTone.Neutral
) {
    val colors = MaterialTheme.colorScheme
    val (iconTint, iconContainer) = when (tone) {
        EmptyStateTone.Neutral -> colors.onSurfaceVariant to colors.surfaceVariant
        EmptyStateTone.Error -> colors.error to colors.errorContainer
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(color = iconContainer, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(30.dp)
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = colors.onBackground,
            textAlign = TextAlign.Center
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

enum class EmptyStateTone { Neutral, Error }
