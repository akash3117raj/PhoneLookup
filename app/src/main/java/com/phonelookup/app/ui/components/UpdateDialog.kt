package com.phonelookup.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.phonelookup.app.data.model.UpdateConfig
import com.phonelookup.app.ui.theme.*

@Composable
fun UpdateDialog(
    config: UpdateConfig,
    onUpdate: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = { if (!config.isForceUpdate) onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = !config.isForceUpdate,
            dismissOnClickOutside = !config.isForceUpdate
        )
    ) {
        BaseUpdateDialogContent(
            title = "UPDATE REQUIRED",
            subtitle = "New Version: v${config.latestVersionName}",
            message = config.updateNotes,
            buttonText = "UPGRADE NOW",
            icon = Icons.Default.SystemUpdate,
            iconColor = NeonCyan,
            onButtonClick = onUpdate,
            showDismiss = !config.isForceUpdate,
            onDismiss = onDismiss
        )
    }
}

@Composable
fun MaintenanceDialog(
    message: String
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        BaseUpdateDialogContent(
            title = "SYSTEM MAINTENANCE",
            subtitle = "Under Construction",
            message = message,
            buttonText = "EXIT APP",
            icon = Icons.Default.Engineering,
            iconColor = NeonOrange,
            onButtonClick = { android.os.Process.killProcess(android.os.Process.myPid()) },
            showDismiss = false
        )
    }
}

@Composable
fun AppDisabledDialog() {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        BaseUpdateDialogContent(
            title = "ACCESS RESTRICTED",
            subtitle = "Application Disabled",
            message = "This version of the application has been disabled by the administrator.",
            buttonText = "EXIT",
            icon = Icons.Default.Error,
            iconColor = NeonRed,
            onButtonClick = { android.os.Process.killProcess(android.os.Process.myPid()) },
            showDismiss = false
        )
    }
}

@Composable
private fun BaseUpdateDialogContent(
    title: String,
    subtitle: String,
    message: String,
    buttonText: String,
    icon: ImageVector,
    iconColor: Color,
    onButtonClick: () -> Unit,
    showDismiss: Boolean = true,
    onDismiss: () -> Unit = {}
) {
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(56.dp)
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                letterSpacing = 2.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = iconColor,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = message,
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            NeonButton(
                text = buttonText,
                onClick = onButtonClick,
                modifier = Modifier.fillMaxWidth(),
                glowColor = iconColor
            )
            
            if (showDismiss) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("LATER", color = TextMuted, letterSpacing = 1.sp)
                }
            }
        }
    }
}
