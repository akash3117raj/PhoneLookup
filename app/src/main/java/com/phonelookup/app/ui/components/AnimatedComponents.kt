package com.phonelookup.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phonelookup.app.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Animated gradient background with slowly shifting colors.
 */
@Composable
fun AnimatedGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bgGradient")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientOffset"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DarkBackground,
                        DarkSurface.copy(alpha = 0.5f + offset * 0.3f),
                        DarkBackground,
                        DarkCard.copy(alpha = 0.3f + (1f - offset) * 0.2f)
                    )
                )
            ),
        content = content
    )
}

/**
 * Fade-in + slide-up entrance animation for screen content.
 */
@Composable
fun FadeSlideIn(
    visible: Boolean = true,
    delayMs: Int = 0,
    content: @Composable () -> Unit
) {
    var show by remember { mutableStateOf(false) }

    LaunchedEffect(visible) {
        if (visible) {
            delay(delayMs.toLong())
            show = true
        }
    }

    AnimatedVisibility(
        visible = show,
        enter = fadeIn(
            animationSpec = tween(500, easing = EaseOutCubic)
        ) + slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = tween(500, easing = EaseOutCubic)
        )
    ) {
        content()
    }
}

/**
 * Animated success toast that shows "Copied!" with a fade-in/out.
 */
@Composable
fun CopiedToast(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(tween(200)) + scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(200)
        ),
        exit = fadeOut(tween(400)) + scaleOut(
            targetScale = 0.8f,
            animationSpec = tween(400)
        )
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(NeonGreen.copy(alpha = 0.2f), NeonCyan.copy(alpha = 0.2f))
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = "✅ Copied Successfully!",
                color = NeonGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Pulsing logo animation for splash screen.
 */
@Composable
fun PulsingLogo(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logoPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoScale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoAlpha"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .alpha(alpha),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

private val EaseOutCubic = CubicBezierEasing(0.215f, 0.61f, 0.355f, 1f)
private val EaseInOutCubic = CubicBezierEasing(0.645f, 0.045f, 0.355f, 1f)
