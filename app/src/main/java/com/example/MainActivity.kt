package com.example

import kotlinx.coroutines.launch
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.TeacherViewModel
import com.example.ui.screens.TeacherAppScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : FragmentActivity() {
    private var deepLinkUriState by mutableStateOf<Uri?>(null)
    private var isAppUnlocked by mutableStateOf(false)
    private var authStatusMessage by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.google.firebase.FirebaseApp.initializeApp(this)
        // ساعة موثوقة تقاوم رجوع تاريخ الهاتف إلى الوراء؛ تُحدَّث بوقت الخادم كلما توفّر إنترنت
        com.example.data.security.TrustedClock.init(this)
        com.example.ui.PrintDesign.load(this)
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            try {
                com.example.data.security.TrustedClock.syncWithServer()
            } catch (e: Exception) {
            }
        }
        val appCheck = com.google.firebase.appcheck.FirebaseAppCheck.getInstance()
        if (BuildConfig.DEBUG) {
            appCheck.installAppCheckProviderFactory(
                com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory.getInstance()
            )
        } else {
            appCheck.installAppCheckProviderFactory(
                com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory.getInstance()
            )
        }
        enableEdgeToEdge()
        deepLinkUriState = intent?.data

        // Automatically prompt for biometric / device credential authentication on launch
        showBiometricPrompt()

        setContent {
            MyApplicationTheme {
                if (isAppUnlocked) {
                    val viewModel: TeacherViewModel = viewModel()
                    TeacherAppScreen(
                        viewModel = viewModel,
                        deepLinkUri = deepLinkUriState,
                        onDeepLinkConsumed = { deepLinkUriState = null }
                    )
                } else {
                    BiometricLockScreen(
                        statusMessage = authStatusMessage,
                        onRetry = { showBiometricPrompt() },
                        onExit = { finish() }
                    )
                }
            }
        }
    }

    private fun showBiometricPrompt() {
        val canAuth = BiometricManager.from(this).canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
        if (canAuth != BiometricManager.BIOMETRIC_SUCCESS) {
            isAppUnlocked = true
            authStatusMessage = null
            return
        }

        val executor = ContextCompat.getMainExecutor(this)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                isAppUnlocked = true
                authStatusMessage = null
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                isAppUnlocked = false
                authStatusMessage = "يجب التحقق من الهوية لفتح التطبيق وتأمين بيانات الطلاب ($errString)"
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                isAppUnlocked = false
                authStatusMessage = "فشل التعرف على الهوية. يرجى المحاولة مرة أخرى."
            }
        }

        val biometricPrompt = BiometricPrompt(this, executor, callback)

        // Note: Do NOT set setNegativeButtonText when setAllowedAuthenticators includes DEVICE_CREDENTIAL
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("التحقق من الهوية 🔒")
            .setSubtitle("تطبيق المعلم المساعد يحمي بيانات الطلاب الحساسة. يرجى استخدام البصمة أو الوجه أو رمز قفل الشاشة لفتح التطبيق.")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        try {
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            e.printStackTrace()
            authStatusMessage = "يرجى استخدام البصمة أو رمز قفل الشاشة لفتح التطبيق وتأمين بياناتك."
        }
    }

    override fun onResume() {
        super.onResume()
        com.example.ui.update.InAppUpdateManager.onResume(this)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == com.example.ui.update.InAppUpdateManager.IN_APP_UPDATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                // Update was cancelled or failed - user continues app normally
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        deepLinkUriState = intent.data
    }
}

@Composable
fun BiometricLockScreen(
    statusMessage: String?,
    onRetry: () -> Unit,
    onExit: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1B5E20),
                            Color(0xFF0D3B10),
                            Color(0xFF001505)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier.size(100.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "قفل الأمان",
                            tint = Color.White,
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "بوابة الحماية والأمان 🔒",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "تطبيق المعلم المساعد محمّي لمنع الوصول غير المصرح به لبيانات الطلاب ودرجاتهم",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }

                if (statusMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0x33FFCDD2),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x66EF5350)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color(0xFFFF8A80)
                            )
                            Text(
                                text = statusMessage,
                                color = Color.White,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onRetry,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "إعادة المحاولة / تأكيد الهوية 🔑",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = onExit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White.copy(alpha = 0.8f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color.White.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            text = "إغلاق التطبيق ❌",
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
