package com.phonelookup.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phonelookup.app.ui.theme.*

/**
 * Premium neon glow button with animated pulsing border.
 */
@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    glowColor: Color = NeonCyan,
    isLoading: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "neonPulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val borderGlow = if (enabled) glowColor.copy(alpha = glowAlpha) else TextMuted

    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .height(54.dp)
            .drawBehind {
                if (enabled) {
                    // Outer glow
                    drawRoundRect(
                        color = borderGlow.copy(alpha = glowAlpha * 0.3f),
                        cornerRadius = CornerRadius(16.dp.toPx()),
                        style = Stroke(width = 6.dp.toPx())
                    )
                    // Inner glow border
                    drawRoundRect(
                        color = borderGlow,
                        cornerRadius = CornerRadius(16.dp.toPx()),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = glowColor.copy(alpha = 0.15f),
            contentColor = glowColor,
            disabledContainerColor = DarkCard,
            disabledContentColor = TextMuted
        ),
        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)
    ) {
        if (isLoading) {
            PulsingDots(color = glowColor)
        } else {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        }
    }
}

/**
 * Animated loading dots for button loading state.
 */
@Composable
fun PulsingDots(color: Color = NeonCyan) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = index * 200),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot$index"
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = color.copy(alpha = alpha),
                        shape = RoundedCornerShape(50)
                    )
            )
        }
    }
}

private val EaseInOutCubic = CubicBezierEasing(0.645f, 0.045f, 0.355f, 1f)
