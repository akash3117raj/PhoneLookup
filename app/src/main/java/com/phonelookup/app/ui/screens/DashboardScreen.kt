package com.phonelookup.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import com.phonelookup.app.R
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.phonelookup.app.data.local.SessionManager
import com.phonelookup.app.ui.components.*
import com.phonelookup.app.ui.theme.*
import com.phonelookup.app.ui.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    sessionManager: SessionManager,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    var phoneNumber by remember { mutableStateOf("") }

    AnimatedGradientBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // ── AI Top Bar ────────────────────────────────────────
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .drawBehind {
                                        drawCircle(
                                            Brush.radialGradient(
                                                colors = listOf(NeonCyan.copy(0.2f), Color.Transparent)
                                            ),
                                            radius = size.width * 0.8f
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.app_logo_main),
                                    contentDescription = "App Logo",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Mani 272",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    "AI Powered Search",
                                    fontSize = 10.sp,
                                    color = NeonCyan,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            sessionManager.clearSession()
                            onLogout()
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Logout",
                                tint = NeonRed
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )

                // ── Content ──────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 24.dp)
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // AI Tab Selection
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkCard.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(4.dp)
                    ) {
                        TabRow(
                            selectedTabIndex = uiState.selectedTab,
                            containerColor = Color.Transparent,
                            contentColor = NeonCyan,
                            indicator = { tabPositions ->
                                Box(
                                    Modifier
                                        .tabIndicatorOffset(tabPositions[uiState.selectedTab])
                                        .fillMaxHeight()
                                        .padding(horizontal = 4.dp, vertical = 4.dp)
                                        .background(
                                            Brush.horizontalGradient(listOf(NeonCyan.copy(0.2f), NeonPurple.copy(0.2f))),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .border(1.dp, NeonCyan.copy(0.3f), RoundedCornerShape(8.dp))
                                )
                            },
                            divider = {}
                        ) {
                            Tab(
                                selected = uiState.selectedTab == 0,
                                onClick = { 
                                    viewModel.setTab(0)
                                    phoneNumber = ""
                                },
                                text = { Text("Mobile", fontSize = 14.sp, fontWeight = FontWeight.SemiBold) }
                            )
                            Tab(
                                selected = uiState.selectedTab == 1,
                                onClick = { 
                                    viewModel.setTab(1)
                                    phoneNumber = ""
                                },
                                text = { Text("Aadhar/Family", fontSize = 14.sp, fontWeight = FontWeight.SemiBold) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Search Card (Futuristic Look)
                    FadeSlideIn(delayMs = 100) {
                        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp, 20.dp)
                                        .background(NeonCyan, RoundedCornerShape(2.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = if (uiState.selectedTab == 0) "PHONE LOOKUP" else "IDENTITY SEARCH",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary,
                                    letterSpacing = 2.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = {
                                    phoneNumber = it
                                    viewModel.clearError()
                                },
                                placeholder = {
                                    Text(
                                        if (uiState.selectedTab == 0) "Enter 10-digit number..." else "Enter Aadhar number...",
                                        color = TextMuted,
                                        fontSize = 14.sp
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        if (uiState.selectedTab == 0) Icons.Default.PhoneAndroid else Icons.Default.Fingerprint,
                                        contentDescription = null,
                                        tint = NeonCyan
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Phone,
                                    imeAction = ImeAction.Search
                                ),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        focusManager.clearFocus()
                                        viewModel.lookupPhone(phoneNumber)
                                    }
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextSecondary,
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = GlassBorder,
                                    cursorColor = NeonCyan,
                                    focusedContainerColor = Color.Black.copy(alpha = 0.4f),
                                    unfocusedContainerColor = Color.Black.copy(alpha = 0.2f)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            NeonButton(
                                text = "ANALYZE DATA",
                                onClick = {
                                    focusManager.clearFocus()
                                    viewModel.lookupPhone(phoneNumber)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                isLoading = uiState.isLoading,
                                enabled = phoneNumber.isNotBlank(),
                                glowColor = NeonCyan
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Result display
                    AnimatedVisibility(
                        visible = uiState.result != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut()
                    ) {
                        FadeSlideIn(delayMs = 0) {
                            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "DATA ANALYSIS",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = NeonCyan,
                                        letterSpacing = 3.sp
                                    )
                                    
                                    uiState.result?.let { result ->
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    if (result.valid) NeonGreen.copy(0.1f) else NeonRed.copy(0.1f),
                                                    RoundedCornerShape(6.dp)
                                                )
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = if (result.valid) "VERIFIED" else "FAILED",
                                                color = if (result.valid) NeonGreen else NeonRed,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                uiState.result?.let { result ->
                                    ResultRow("IDENTIFIER", result.number)
                                    ResultRow("ENTITY NAME", result.name)
                                    ResultRow("NETWORK", result.carrier)
                                    
                                    // CLICKABLE MAP LOCATION
                                    ResultRow(
                                        label = "GEOLOCATION",
                                        value = result.location,
                                        isClickable = result.location.isNotBlank(),
                                        onClick = {
                                            if (result.location.isNotBlank()) {
                                                val uri = Uri.parse("geo:0,0?q=${Uri.encode(result.location)}")
                                                val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                                                mapIntent.setPackage("com.google.android.apps.maps")
                                                context.startActivity(mapIntent)
                                            }
                                        }
                                    )
                                    
                                    ResultRow("INFRASTRUCTURE", result.lineType)
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                NeonButton(
                                    text = "COPY REPORT",
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(uiState.formattedResult))
                                        viewModel.onCopied()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    glowColor = NeonGreen
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                    
                    Text(
                        text = "POWERED BY MANI 272 AI",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = TextMuted.copy(alpha = 0.4f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp
                    )
                }
            }

            CopiedToast(
                visible = uiState.showCopied,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp)
            )
        }
    }
}

@Composable
private fun ResultRow(
    label: String, 
    value: String, 
    isClickable: Boolean = false,
    onClick: () -> Unit = {}
) {
    if (value.isBlank()) return

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = TextMuted,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 15.sp,
            color = if (isClickable) NeonCyan else TextPrimary,
            fontWeight = FontWeight.SemiBold,
            textDecoration = if (isClickable) TextDecoration.Underline else TextDecoration.None,
            modifier = Modifier
                .fillMaxWidth()
                .then(if (isClickable) Modifier.clickable { onClick() } else Modifier)
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = GlassHighlight, thickness = 0.5.dp)
    }
}
