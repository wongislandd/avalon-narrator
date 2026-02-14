package com.avalonnarrator.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import avalon_narrator.composeapp.generated.resources.Res
import avalon_narrator.composeapp.generated.resources.assassin
import avalon_narrator.composeapp.generated.resources.brute
import avalon_narrator.composeapp.generated.resources.cleric
import avalon_narrator.composeapp.generated.resources.evil_lancelot
import avalon_narrator.composeapp.generated.resources.evil_messenger
import avalon_narrator.composeapp.generated.resources.evil_rogue
import avalon_narrator.composeapp.generated.resources.evil_sorcerer
import avalon_narrator.composeapp.generated.resources.good_lancelot
import avalon_narrator.composeapp.generated.resources.good_rogue
import avalon_narrator.composeapp.generated.resources.good_sorcerer
import avalon_narrator.composeapp.generated.resources.junior_messenger
import avalon_narrator.composeapp.generated.resources.loyal_servant_of_arthor
import avalon_narrator.composeapp.generated.resources.lunatic
import avalon_narrator.composeapp.generated.resources.merlin
import avalon_narrator.composeapp.generated.resources.minion
import avalon_narrator.composeapp.generated.resources.mordred
import avalon_narrator.composeapp.generated.resources.morgana
import avalon_narrator.composeapp.generated.resources.oberon
import avalon_narrator.composeapp.generated.resources.percival
import avalon_narrator.composeapp.generated.resources.revealer
import avalon_narrator.composeapp.generated.resources.senior_messenger
import avalon_narrator.composeapp.generated.resources.trickster
import avalon_narrator.composeapp.generated.resources.troublemaker
import avalon_narrator.composeapp.generated.resources.untrustworthy_servant
import com.avalonnarrator.domain.roles.RoleDefinition
import kotlinx.coroutines.withTimeoutOrNull
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun RoleCard(
    role: RoleDefinition,
    selected: Boolean,
    onToggle: () -> Unit,
    onPreviewStart: () -> Unit,
    onPreviewEnd: () -> Unit,
    showQuantityControls: Boolean = false,
    quantity: Int? = null,
    onDecreaseQuantity: (() -> Unit)? = null,
    onIncreaseQuantity: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val infinite = rememberInfiniteTransition(label = "selected_glow")
    val shimmerProgress = infinite.animateFloat(
        initialValue = -1.2f,
        targetValue = 2.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer_progress",
    ).value
    val shimmerShift = -940f + shimmerProgress * 760f

    val borderBrush = remember(selected, shimmerProgress) {
        if (selected) {
            Brush.linearGradient(
                colors = listOf(Color(0xFFFFE8AA), Color(0xFFD99E3D), Color(0xFFFFE8AA)),
                start = Offset(shimmerShift - 220f, 0f),
                end = Offset(shimmerShift + 220f, 300f),
            )
        } else {
            Brush.linearGradient(listOf(Color(0xFF4A3A22), Color(0xFF6D5631)))
        }
    }

    Box(
        modifier = modifier
            .pointerInput(role.id) {
                detectTapGestures(
                    onTap = { onToggle() },
                    onPress = {
                        val releasedBeforeLongPress = withTimeoutOrNull(viewConfiguration.longPressTimeoutMillis.toLong()) {
                            tryAwaitRelease()
                        } ?: false

                        if (!releasedBeforeLongPress) {
                            onPreviewStart()
                            tryAwaitRelease()
                            onPreviewEnd()
                        }
                    },
                )
            }
            .size(width = 148.dp, height = 168.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(width = if (selected) 3.dp else 1.dp, brush = borderBrush, shape = RoundedCornerShape(18.dp)),
    ) {
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
                        colors = listOf(Color.Transparent, Color.Transparent, Color(0xCC130E08)),
                    ),
                ),
        )

        Text(
            text = role.name,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xD9130E08)),
                    ),
                )
                .padding(start = 10.dp, end = 10.dp, bottom = 12.dp, top = 16.dp),
            color = Color(0xFFFFF3D6),
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )

        if (showQuantityControls && quantity != null && onDecreaseQuantity != null && onIncreaseQuantity != null) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xCC20140C),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(7.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    QuantityControlButton(symbol = "-", onClick = onDecreaseQuantity)
                    Text(
                        text = quantity.toString(),
                        color = Color(0xFFFFF3D6),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 6.dp),
                    )
                    QuantityControlButton(symbol = "+", onClick = onIncreaseQuantity)
                }
            }
        }
    }
}

@Composable
private fun QuantityControlButton(
    symbol: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(20.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, Color(0x99D8C28D), RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = symbol,
            color = Color(0xFFFFEDCA),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
        )
    }
}

@Composable
fun SelectedRoleMiniCard(
    role: RoleDefinition,
    onClick: (() -> Unit)?,
    label: String = role.name,
    modifier: Modifier = Modifier,
) {
    val tapModifier = if (onClick != null) {
        Modifier.pointerInput(role.id) {
            detectTapGestures(onTap = { onClick() })
        }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .size(width = 92.dp, height = 68.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xB3E8C16E), RoundedCornerShape(12.dp))
            .then(tapModifier),
    ) {
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
                        colors = listOf(Color.Transparent, Color(0xC0150F09)),
                    ),
                ),
        )
        Text(
            text = label,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 5.dp),
            color = Color(0xFFFFF3D6),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}

@Composable
fun CharacterArtwork(
    imageKey: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val drawable = remember(imageKey) { drawableForImageKey(imageKey) }
    Image(
        painter = painterResource(drawable),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop,
    )
}

private fun drawableForImageKey(imageKey: String): DrawableResource = when (imageKey) {
    "assassin" -> Res.drawable.assassin
    "brute" -> Res.drawable.brute
    "cleric" -> Res.drawable.cleric
    "evil_lancelot" -> Res.drawable.evil_lancelot
    "evil_messenger" -> Res.drawable.evil_messenger
    "evil_rogue" -> Res.drawable.evil_rogue
    "evil_sorcerer" -> Res.drawable.evil_sorcerer
    "good_lancelot" -> Res.drawable.good_lancelot
    "good_rogue" -> Res.drawable.good_rogue
    "good_sorcerer" -> Res.drawable.good_sorcerer
    "junior_messenger" -> Res.drawable.junior_messenger
    "lancelot_evil" -> Res.drawable.evil_lancelot
    "lancelot_good" -> Res.drawable.good_lancelot
    "loyal_servant" -> Res.drawable.loyal_servant_of_arthor
    "loyal_servant_of_arthor" -> Res.drawable.loyal_servant_of_arthor
    "lunatic" -> Res.drawable.lunatic
    "merlin" -> Res.drawable.merlin
    "minion" -> Res.drawable.minion
    "mordred" -> Res.drawable.mordred
    "morgana" -> Res.drawable.morgana
    "oberon" -> Res.drawable.oberon
    "percival" -> Res.drawable.percival
    "revealer" -> Res.drawable.revealer
    "rogue_evil" -> Res.drawable.evil_rogue
    "rogue_good" -> Res.drawable.good_rogue
    "senior_messenger" -> Res.drawable.senior_messenger
    "sorcerer_evil" -> Res.drawable.evil_sorcerer
    "sorcerer_good" -> Res.drawable.good_sorcerer
    "trickster" -> Res.drawable.trickster
    "troublemaker" -> Res.drawable.troublemaker
    "untrustworthy_servant" -> Res.drawable.untrustworthy_servant
    else -> Res.drawable.loyal_servant_of_arthor
}
