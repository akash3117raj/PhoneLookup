package com.phonelookup.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import com.phonelookup.app.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phonelookup.app.data.local.SessionManager
import com.phonelookup.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    sessionManager: SessionManager,
    onNavigateToLogin: () -> Unit,
    onNavigateToDashboard: () -> Unit
) {
    var phase by remember { mutableIntStateOf(0) }

    // Animate through phases: 0=enter, 1=visible, 2=exit
    LaunchedEffect(Unit) {
        delay(200)
        phase = 1  // show content
        delay(1800) // display duration
        phase = 2  // trigger exit

        // Navigate based on session
        if (sessionManager.isLoggedIn && !sessionManager.authToken.isNullOrEmpty()) {
            onNavigateToDashboard()
        } else {
            onNavigateToLogin()
        }
    }

    val alphaAnim by animateFloatAsState(
        targetValue = if (phase >= 1) 1f else 0f,
        animationSpec = tween(600),
        label = "splashAlpha"
    )
    val scaleAnim by animateFloatAsState(
        targetValue = when (phase) {
            0 -> 0.7f
            1 -> 1f
            else -> 1.1f
        },
        animationSpec = tween(600, easing = EaseOutBack),
        label = "splashScale"
    )

    // Animated background loading
    val infiniteTransition = rememberInfiniteTransition(label = "splashBg")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alphaAnim)
                .scale(scaleAnim)
        ) {
            // App icon
            Box(
                modifier = Modifier
                    .size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo_main),
                    contentDescription = "App Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "MANI 272 AI",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "ADVANCED DATA SYSTEMS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                letterSpacing = 6.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Loading bar
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(3.dp)
                    .background(
                        DarkCard,
                        shape = RoundedCornerShape(2.dp)
                    )
            ) {
                val loadingOffset by infiniteTransition.animateFloat(
                    initialValue = -1f,
                    targetValue = 2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = LinearEasing)
                    ),
                    label = "loadingBar"
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.4f)
                        .offset(x = (loadingOffset * 120).dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, NeonCyan, Color.Transparent)
                            ),
                            shape = RoundedCornerShape(2.dp)
                        )
                )
            }
        }

        // Powered by footer
        Text(
            text = "POWERED BY MANI 272 AI",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .alpha(alphaAnim * 0.5f),
            color = NeonCyan,
            fontSize = 11.sp,
            letterSpacing = 4.sp,
            fontWeight = FontWeight.Black
        )
    }
}

private val EaseOutBack = CubicBezierEasing(0.175f, 0.885f, 0.32f, 1.275f)
