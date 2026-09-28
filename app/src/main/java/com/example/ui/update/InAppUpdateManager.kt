package com.example.ui.update

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoText
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

/**
 * Manager for Google Play In-App Updates.
 * - Checks Google Play once per app launch session.
 * - Shows an optional update dialog (FLEXIBLE update).
 * - Notifies user when update is downloaded to complete installation.
 * - Handles offline / Google Play errors gracefully without interrupting the user.
 * - Zero server / Firebase / database dependencies.
 */
object InAppUpdateManager {
    private const val TAG = "InAppUpdateManager"
    const val IN_APP_UPDATE_REQUEST_CODE = 8901

    private var appUpdateManager: AppUpdateManager? = null
    private var cachedAppUpdateInfo: AppUpdateInfo? = null
    private var hasCheckedInThisSession = false

    var showUpdatePromptDialog by mutableStateOf(false)
        private set

    var isUpdateDownloadedState by mutableStateOf(false)
        private set

    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            isUpdateDownloadedState = true
            showUpdatePromptDialog = false
        }
    }

    /**
     * Checks Google Play for updates once per session.
     * Safe to call multiple times; will only execute on first call per session.
     */
    fun checkForUpdatesOncePerSession(context: Context) {
        if (hasCheckedInThisSession) {
            return
        }
        hasCheckedInThisSession = true

        try {
            val manager = appUpdateManager ?: AppUpdateManagerFactory.create(context.applicationContext).also {
                appUpdateManager = it
                it.registerListener(installStateUpdatedListener)
            }

            manager.appUpdateInfo.addOnSuccessListener { info ->
                cachedAppUpdateInfo = info

                // Check if update is already downloaded and pending install
                if (info.installStatus() == InstallStatus.DOWNLOADED) {
                    isUpdateDownloadedState = true
                    return@addOnSuccessListener
                }

                // Check if update is available
                if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    showUpdatePromptDialog = true
                } else if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    // Update in progress, ensure listener is active
                    if (info.installStatus() == InstallStatus.DOWNLOADED) {
                        isUpdateDownloadedState = true
                    }
                }
            }.addOnFailureListener { e ->
                // Graceful fallback: do nothing, allow user to work offline / normally
                Log.d(TAG, "In-app update check ignored/failed gracefully: ${e.message}")
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error initializing update check: ${e.message}")
        }
    }

    /**
     * Start the flexible in-app update flow.
     * If Flexible is not allowed or fails, fallback to opening the Play Store page.
     */
    fun startFlexibleUpdate(activity: Activity) {
        showUpdatePromptDialog = false
        val manager = appUpdateManager ?: AppUpdateManagerFactory.create(activity.applicationContext).also {
            appUpdateManager = it
            it.registerListener(installStateUpdatedListener)
        }
        val info = cachedAppUpdateInfo

        if (info != null && info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
            try {
                val options = AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
                manager.startUpdateFlowForResult(info, activity, options, IN_APP_UPDATE_REQUEST_CODE)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start in-app update flow, falling back to Play Store", e)
                openPlayStore(activity)
            }
        } else {
            // Flexible update not directly allowed or info is null -> open Play Store
            openPlayStore(activity)
        }
    }

    /**
     * User dismissed the update dialog for this session.
     */
    fun dismissUpdateDialog() {
        showUpdatePromptDialog = false
    }

    /**
     * Complete the update (restarts the app to install).
     */
    fun completeUpdate() {
        try {
            appUpdateManager?.completeUpdate()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to complete update", e)
        }
    }

    /**
     * Checks if a downloaded update is pending when returning to the app.
     */
    fun onResume(context: Context) {
        try {
            appUpdateManager?.appUpdateInfo?.addOnSuccessListener { info ->
                if (info.installStatus() == InstallStatus.DOWNLOADED) {
                    isUpdateDownloadedState = true
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error in onResume update check: ${e.message}")
        }
    }

    /**
     * Fallback to open Google Play Store page directly.
     */
    fun openPlayStore(context: Context) {
        val packageName = context.packageName
        try {
            val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(marketIntent)
        } catch (e: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        }
    }
}

/**
 * In-App Update Prompt Dialog (Arabic UI).
 */
@Composable
fun InAppUpdateDialog(
    onUpdateNow: () -> Unit,
    onLater: () -> Unit
) {
    Dialog(
        onDismissRequest = onLater,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = BentoPrimary.copy(alpha = 0.12f),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.SystemUpdate,
                            contentDescription = "تحديث جديد",
                            tint = BentoPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Text(
                    text = "يتوفر إصدار جديد",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = BentoText,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "يتوفر إصدار جديد من تطبيق دفتر المعلم. ننصح بالتحديث للحصول على أحدث التحسينات والإصلاحات.",
                    fontSize = 13.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onLater,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "لاحقًا",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Button(
                        onClick = onUpdateNow,
                        modifier = Modifier
                            .weight(1.2f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BentoPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "تحديث الآن",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Floating / Top Banner when Flexible Update has finished downloading.
 */
@Composable
fun UpdateDownloadedNotificationCard(
    onInstallNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DownloadDone,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "تم تنزيل التحديث الجديد.",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
                Text(
                    text = "جاهز للتثبيت لتطبيق أحدث الميزات والإصلاحات",
                    fontSize = 10.5.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }

            Button(
                onClick = onInstallNow,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF1B5E20)
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "تثبيت الآن",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
            }
        }
    }
}
