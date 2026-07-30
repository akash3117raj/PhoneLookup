package com.phonelookup.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import com.phonelookup.app.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phonelookup.app.data.local.SessionManager
import com.phonelookup.app.ui.components.*
import com.phonelookup.app.ui.theme.*
import com.phonelookup.app.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    sessionManager: SessionManager,
    onLoginSuccess: (isAdmin: Boolean) -> Unit
) {
    val viewModel = remember { LoginViewModel(sessionManager) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var licenseKey by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onLoginSuccess(uiState.isAdmin)
    }

    AnimatedGradientBackground {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Background Glow Effects
            Box(
                modifier = Modifier
                    .size(400.dp)
                    .offset(x = 150.dp, y = (-250).dp)
                    .background(NeonPurple.copy(0.08f), CircleShape)
                    .blur(120.dp)
            )
            Box(
                modifier = Modifier
                    .size(350.dp)
                    .offset(x = (-150).dp, y = 250.dp)
                    .background(NeonCyan.copy(0.08f), CircleShape)
                    .blur(100.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp)
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Professional Logo Header
                FadeSlideIn(delayMs = 100) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .drawBehind {
                                    drawCircle(
                                        brush = Brush.sweepGradient(listOf(NeonCyan, NeonPurple, NeonCyan)),
                                        radius = size.width / 2 + 6.dp.toPx(),
                                        alpha = 0.2f
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.app_logo_main),
                                contentDescription = "App Logo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        Text(
                            text = "MANI 272 AI",
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            letterSpacing = 5.sp
                        )
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Shield, 
                                null, 
                                tint = NeonCyan, 
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ENCRYPTED ACCESS PORTAL",
                                fontSize = 11.sp,
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 3.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(56.dp))

                // Premium Glass Authorization Card
                FadeSlideIn(delayMs = 300) {
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Fingerprint, null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "IDENTITY VERIFICATION",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = TextSecondary,
                                letterSpacing = 2.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))

                        OutlinedTextField(
                            value = licenseKey,
                            onValueChange = {
                                licenseKey = it
                                viewModel.clearError()
                            },
                            label = { 
                                Text(
                                    "ENTER LICENSE KEY", 
                                    fontSize = 10.sp, 
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ) 
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = NeonCyan
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = !uiState.isLoading,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    viewModel.login(licenseKey)
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextSecondary,
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = GlassBorder,
                                cursorColor = NeonCyan,
                                focusedContainerColor = Color.Black.copy(0.4f),
                                unfocusedContainerColor = Color.Black.copy(0.2f)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )

                        // Status Messages (Error/Processing)
                        AnimatedVisibility(visible = uiState.error != null || uiState.message != null) {
                            Column {
                                Spacer(modifier = Modifier.height(20.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (uiState.error != null) NeonRed.copy(0.1f) else NeonCyan.copy(0.1f), 
                                            RoundedCornerShape(10.dp)
                                        )
                                        .border(
                                            1.dp, 
                                            if (uiState.error != null) NeonRed.copy(0.3f) else NeonCyan.copy(0.3f), 
                                            RoundedCornerShape(10.dp)
                                        )
                                        .padding(14.dp)
                                ) {
                                    Text(
                                        text = (uiState.error ?: uiState.message ?: "").uppercase(),
                                        color = if (uiState.error != null) NeonRed else NeonCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth(),
                                        letterSpacing = 1.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        NeonButton(
                            text = if (uiState.isLoading) "VERIFYING..." else "SECURE LOGIN",
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.login(licenseKey)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            isLoading = uiState.isLoading,
                            enabled = licenseKey.isNotBlank() && !uiState.isLoading,
                            glowColor = NeonCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Professional AI Footer
                FadeSlideIn(delayMs = 500) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "RSA-4096 BIT ENCRYPTION ENABLED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = TextMuted.copy(alpha = 0.5f),
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "POWERED BY MANI 272 AI",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonCyan.copy(alpha = 0.7f),
                            letterSpacing = 4.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
