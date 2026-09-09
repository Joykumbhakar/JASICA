package com.jasica.ai.controller

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jasica.ai.controller.bluetooth.AppBluetoothManager
import com.jasica.ai.controller.data.CommandPreferences
import com.jasica.ai.controller.ui.screens.MainScreen
import com.jasica.ai.controller.ui.screens.SettingsScreen
import com.jasica.ai.controller.ui.theme.JasicaControllerTheme
import com.jasica.ai.controller.voice.SpeechManager
import com.jasica.ai.controller.voice.TtsManager

class MainActivity : ComponentActivity() {

    private lateinit var bluetoothManager: AppBluetoothManager
    private lateinit var ttsManager: TtsManager
    private lateinit var commandPreferences: CommandPreferences
    private lateinit var speechManager: SpeechManager

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val recordAudioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false
        val bluetoothGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (permissions[Manifest.permission.BLUETOOTH_CONNECT] ?: false) &&
            (permissions[Manifest.permission.BLUETOOTH_SCAN] ?: false)
        } else {
            true
        }

        if (bluetoothGranted) {
            bluetoothManager.updateBtState()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Core Subsystems
        bluetoothManager = AppBluetoothManager(this)
        ttsManager = TtsManager(this)
        commandPreferences = CommandPreferences(this)
        speechManager = SpeechManager(this, bluetoothManager, ttsManager, commandPreferences)

        checkAndRequestPermissions()

        setContent {
            JasicaControllerTheme {
                JasicaApp(
                    bluetoothManager = bluetoothManager,
                    speechManager = speechManager,
                    commandPreferences = commandPreferences
                )
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    override fun onResume() {
        super.onResume()
        bluetoothManager.updateBtState()
    }

    override fun onDestroy() {
        super.onDestroy()
        speechManager.cleanup()
        ttsManager.shutdown()
        bluetoothManager.cleanup()
    }
}

@Composable
fun JasicaApp(
    bluetoothManager: AppBluetoothManager,
    speechManager: SpeechManager,
    commandPreferences: CommandPreferences
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                bluetoothManager = bluetoothManager,
                speechManager = speechManager,
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        composable("settings") {
            SettingsScreen(
                bluetoothManager = bluetoothManager,
                speechManager = speechManager,
                commandPreferences = commandPreferences,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
