package com.avalonnarrator.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.avalonnarrator.domain.roles.RoleDefinition
import com.avalonnarrator.domain.roles.Alignment as RoleAlignment
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HolographicRolePreviewCard(
    role: RoleDefinition,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "holo_preview")
    val cycle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "cycle",
    )

    val shimmerProgress by transition.animateFloat(
        initialValue = -1.2f,
        targetValue = 2.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer_progress",
    )

    val rotationY = (sin(cycle * PI * 2.0) * 5.5).toFloat()
    val rotationX = (cos(cycle * PI * 2.0) * 3.2).toFloat()
    val scale = 1.006f + (sin(cycle * PI * 2.0) * 0.004f).toFloat()
    val liftY = (cos(cycle * PI * 2.0) * -3.0).toFloat()
    Box(
        modifier = modifier
            .graphicsLayer {
                this.rotationY = rotationY
                this.rotationX = rotationX
                scaleX = scale
                scaleY = scale
                translationY = liftY
                cameraDistance = 12f * density
                shadowElevation = 34.dp.toPx()
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(24.dp))
                .graphicsLayer {
                    rotationZ = -2.4f
                    translationX = -6f
                    translationY = 8f
                }
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0x662D1C0B), Color(0x99412316), Color(0x5530160A)),
                    ),
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0x66FFDFA2), Color(0x66B7E4FF), Color(0x66FFDFA2)),
                    ),
                    shape = RoundedCornerShape(24.dp),
                ),
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1A120A)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFFDECB7), Color(0xFFAEDFFF), Color(0xFFF6C983)),
                        ),
                        shape = RoundedCornerShape(24.dp),
                    ),
            )
            CharacterArtwork(
                imageKey = role.imageKey,
                contentDescription = role.name,
                modifier = Modifier.fillMaxSize(),
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x26FFF7E0),
                                Color.Transparent,
                                Color(0xC7140E08),
                            ),
                        ),
                    ),
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0x224CF7FF),
                                Color(0x24F9FF92),
                                Color(0x22FFD288),
                                Color.Transparent,
                                Color.Transparent,
                            ),
                            start = Offset(x = 1500f * shimmerProgress - 760f, y = -220f),
                            end = Offset(x = 1500f * shimmerProgress + 140f, y = 1320f),
                        ),
                    ),
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color.Transparent, Color(0x40FFFFFF), Color.Transparent),
                            start = Offset(x = 1500f * shimmerProgress - 640f, y = -200f),
                            end = Offset(x = 1500f * shimmerProgress + 220f, y = 1300f),
                        ),
                    ),
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = role.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFFFFF4DB),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = if (role.alignment == RoleAlignment.GOOD) "GOOD" else "EVIL",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (role.alignment == RoleAlignment.GOOD) Color(0xFFD6EEFF) else Color(0xFFFFD3CC),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (role.alignment == RoleAlignment.GOOD) Color(0x552274A7) else Color(0x55772F25))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0x8F130D09),
                            shape = RoundedCornerShape(14.dp),
                        )
                        .padding(12.dp),
                ) {
                    Text(
                        text = role.mechanicSummary,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFFFFEFD2),
                    )
                    if (role.tags.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = role.tags.joinToString(" • ") { it.replace('_', ' ') },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFD7C7A8),
                        )
                    }
                }
            }
        }
    }
}
