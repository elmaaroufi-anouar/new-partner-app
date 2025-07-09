package com.done.app

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.done.app.util.BatteryLevelReceiver
import com.done.core.presentation.core.ui.theme.DoneTheme
import com.done.partner.data.dto.status.StatusDto
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import com.done.core.data.services.language.LanguageServiceImpl
import com.done.core.presentation.core.ui.components.OnResumeCompose

class MainActivity : ComponentActivity() {

    private val viewModel by viewModel<CoreViewModel>()
    private var batteryReceiver: BatteryLevelReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.requestFeature(Window.FEATURE_NO_TITLE)
        enableEdgeToEdge(
            SystemBarStyle.light(
                scrim = Color.Transparent.toArgb(), darkScrim = Color.Transparent.toArgb()
            )
        )

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.state.isCheckingLogIn
            }
        }

        registerBatteryReceiver()

        val data = intent.getStringExtra("entity_data")
        val type = intent.getStringExtra("entity_data")
        var orderId: String? = null
        var orderStatus: StatusDto? = null
        val json = Json { ignoreUnknownKeys = true }

        if (data != null && type == "order") {
            try {
                val deliveryData = json.decodeFromString<JsonObject>(data)
                orderId = deliveryData["id"]?.jsonPrimitive?.content
                orderStatus = deliveryData["status"]?.toString()?.let {
                    json.decodeFromString<StatusDto>(it)
                }
                println("Pushy orderId: $orderId")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        setContent {
            DoneTheme {
                val state = viewModel.state
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    var hasPermissions by rememberSaveable { mutableStateOf(true) }

                    if (
                        state.activateFCM == true
                        && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                    ) {
                        OnResumeCompose {
                            if (!Environment.isExternalStorageManager()) {
                                startActivity(
                                    Intent(
                                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                                        "package:${BuildConfig.APPLICATION_ID}".toUri()
                                    )
                                )
                            } else {
                                hasPermissions = true
                            }
                        }
                    } else {
                        hasPermissions = true
                    }
                    if (hasPermissions) {
                        // Compose App initialization
                        App(
                            isLoggedIn = state.isLoggedIn,
                            orderId = orderId,
                            orderStatus = orderStatus?.value,
                        )
                    }
                }
            }
        }

        setMaxNotificationVolume()
        acquireWakeLock()
    }

    private fun setMaxNotificationVolume() {
        try {
            val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION)
            audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, maxVolume, 0)
        } catch (_: Exception) {
        }
    }

    private fun registerBatteryReceiver() {
        batteryReceiver = BatteryLevelReceiver()
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        batteryReceiver?.let {
            unregisterReceiver(it)
        }
    }

    private fun acquireWakeLock() {
        try {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK, "PartnerApp::LocationWakeLock"
            )
        } catch (e: Exception) {
            Log.e("WakeLock", "Failed to acquire wake lock", e)
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(
            LanguageServiceImpl(base).setCurrentLanguageToBaseContext()
        )
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    PartnerApplication()
}
