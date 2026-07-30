package com.phonelookup.app.data.repository

import android.app.DownloadManager
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.phonelookup.app.BuildConfig
import com.phonelookup.app.data.model.UpdateConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.net.Proxy
import java.util.concurrent.TimeUnit

class UpdateManager(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .proxy(Proxy.NO_PROXY)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()
    
    private val gson = Gson()
    private val updateConfigUrl = "https://raw.githubusercontent.com/studywithsunny17744-svg/Apk-password-/main/version.json"

    suspend fun fetchConfig(): UpdateConfig? {
        return withContext(Dispatchers.IO) {
            try {
                val urlWithCacheBuster = "$updateConfigUrl?t=${System.currentTimeMillis()}"
                val request = Request.Builder()
                    .url(urlWithCacheBuster)
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext null
                    val content = response.body?.string() ?: return@withContext null
                    gson.fromJson(content, UpdateConfig::class.java)
                }
            } catch (e: Exception) {
                Log.e("UpdateManager", "Config fetch error", e)
                null
            }
        }
    }

    fun isUpdateAvailable(config: UpdateConfig): Boolean {
        return config.latestVersionCode > BuildConfig.VERSION_CODE
    }

    /**
     * Downloads the APK using DownloadManager and triggers installation.
     */
    fun startUpdate(url: String) {
        if (!url.startsWith("https://")) {
            Toast.makeText(context, "Invalid update link.", Toast.LENGTH_LONG).show()
            return
        }

        val destination = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "update.apk")
        if (destination.exists()) destination.delete()

        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Mani 272 AI Update")
            .setDescription("Downloading new version...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationUri(Uri.fromFile(destination))
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            request.setRequiresCharging(false)
        }

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

        Toast.makeText(context, "Downloading update...", Toast.LENGTH_SHORT).show()

        // Register receiver to install after download
        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (downloadId == id) {
                    installApk(destination)
                    context.unregisterReceiver(this)
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }
    }

    private fun installApk(file: File) {
        try {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("UpdateManager", "Install error", e)
            Toast.makeText(context, "Installation failed. Open Downloads to install.", Toast.LENGTH_LONG).show()
        }
    }
}
