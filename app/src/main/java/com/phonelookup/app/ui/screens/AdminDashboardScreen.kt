package com.phonelookup.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phonelookup.app.ui.components.*
import com.phonelookup.app.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import android.util.Base64

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onLogout: () -> Unit
) {
    var versionCode by remember { mutableStateOf("2") }
    var versionName by remember { mutableStateOf("2.0") }
    var updateUrl by remember { mutableStateOf("") }
    var updateNotes by remember { mutableStateOf("New Update Available!") }
    var githubToken by remember { mutableStateOf("") } // GitHub Personal Access Token
    
    var isForceUpdate by remember { mutableStateOf(true) }
    var isMaintenance by remember { mutableStateOf(false) }
    var isAppEnabled by remember { mutableStateOf(true) }
    
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    AnimatedGradientBackground {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text("ADMIN CLOUD CONTROL", fontSize = 18.sp, fontWeight = FontWeight.Black) },
                    actions = {
                        IconButton(onClick = onLogout) {
                            Icon(Icons.AutoMirrored.Filled.Logout, null, tint = NeonRed)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Text("CLOUD SETTINGS", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    
                    AdminField("GITHUB TOKEN (Optional for Direct Save)", githubToken) { githubToken = it }
                    
                    HorizontalDivider(Modifier.padding(vertical = 16.dp), color = GlassHighlight)
                    
                    AdminField("LATEST VERSION CODE", versionCode) { versionCode = it }
                    AdminField("VERSION NAME", versionName) { versionName = it }
                    AdminField("DIRECT APK LINK", updateUrl) { updateUrl = it }
                    AdminField("UPDATE MESSAGE", updateNotes) { updateNotes = it }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    AdminToggle("FORCE UPDATE", isForceUpdate) { isForceUpdate = it }
                    AdminToggle("MAINTENANCE", isMaintenance) { isMaintenance = it }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    NeonButton(
                        text = "PUSH UPDATE TO ALL USERS",
                        onClick = {
                            val json = JSONObject().apply {
                                put("latest_version_code", versionCode.toIntOrNull() ?: 1)
                                put("latest_version_name", versionName)
                                put("update_url", updateUrl)
                                put("update_notes", updateNotes)
                                put("is_force_update", isForceUpdate)
                                put("maintenance_mode", isMaintenance)
                                put("is_app_enabled", isAppEnabled)
                                put("maintenance_message", "System Update in Progress")
                            }.toString()

                            if (githubToken.isBlank()) {
                                scope.launch { snackbarHostState.showSnackbar("Enter GitHub Token for Direct Save") }
                            } else {
                                scope.launch {
                                    val success = saveToGithub(githubToken, json)
                                    snackbarHostState.showSnackbar(if(success) "✅ UPDATE PUBLISHED!" else "❌ FAILED TO SAVE")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        glowColor = NeonCyan
                    )
                }
            }
        }
    }
}

suspend fun saveToGithub(token: String, content: String): Boolean = withContext(Dispatchers.IO) {
    try {
        val client = OkHttpClient()
        val repo = "studywithsunny17744-svg/Apk-password-"
        val path = "version.json"
        val url = "https://api.github.com/repos/${repo}/contents/${path}"

        // 1. Get current file SHA (Required by GitHub to update existing files)
        val getRequest = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $token")
            .header("Accept", "application/vnd.github+json")
            .build()
        
        var sha: String? = null
        client.newCall(getRequest).execute().use { response ->
            if (response.isSuccessful) {
                val jsonResponse = JSONObject(response.body?.string() ?: "{}")
                sha = jsonResponse.optString("sha")
            }
        }

        // 2. Update file using PUT
        val updateBody = JSONObject().apply {
            put("message", "Admin Update: ${System.currentTimeMillis()}")
            put("content", Base64.encodeToString(content.toByteArray(), Base64.NO_WRAP))
            if (sha != null) {
                put("sha", sha)
            }
        }

        val putRequest = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $token")
            .header("Accept", "application/vnd.github+json")
            .put(updateBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(putRequest).execute().use { response ->
            if (!response.isSuccessful) {
                android.util.Log.e("AdminPanel", "Save failed: ${response.code} - ${response.body?.string()}")
            }
            response.isSuccessful
        }
    } catch (e: Exception) {
        android.util.Log.e("AdminPanel", "Error saving to GitHub", e)
        false
    }
}

@Composable
private fun AdminField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(label, fontSize = 10.sp, color = TextMuted)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextSecondary,
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = GlassBorder,
                focusedContainerColor = Color.Black.copy(0.3f)
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
private fun AdminToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextPrimary, fontSize = 14.sp)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
