package com.phonelookup.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.phonelookup.app.data.model.UpdateConfig
import com.phonelookup.app.data.repository.UpdateManager
import com.phonelookup.app.ui.components.AppDisabledDialog
import com.phonelookup.app.ui.components.MaintenanceDialog
import com.phonelookup.app.ui.components.UpdateDialog
import com.phonelookup.app.ui.navigation.AppNavigation
import com.phonelookup.app.ui.theme.PhoneLookupTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as PhoneLookupApp
        val updateManager = UpdateManager(this)

        setContent {
            PhoneLookupTheme {
                var updateConfig by remember { mutableStateOf<UpdateConfig?>(null) }
                var showUpdateDialog by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    val config = updateManager.fetchConfig()
                    updateConfig = config
                    if (config != null && updateManager.isUpdateAvailable(config)) {
                        showUpdateDialog = true
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(sessionManager = app.sessionManager)

                    updateConfig?.let { config ->
                        // 1. Check if App is disabled globally
                        if (!config.isAppEnabled) {
                            AppDisabledDialog()
                        } 
                        // 2. Check for Maintenance Mode
                        else if (config.isMaintenanceMode) {
                            MaintenanceDialog(message = config.maintenanceMessage)
                        } 
                        // 3. Check for Updates
                        else if (showUpdateDialog) {
                            UpdateDialog(
                                config = config,
                                onUpdate = { updateManager.startUpdate(config.updateUrl) },
                                onDismiss = { showUpdateDialog = false }
                            )
                        }
                    }
                }
            }
        }
    }
}
