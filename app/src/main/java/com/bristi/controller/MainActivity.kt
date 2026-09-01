package com.bristi.controller

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothSocket
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRow
import androidx.compose.runtime.*
import android.media.MediaPlayer
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// ─────────────────────────────────────────────────────────────────────────────
//  App State & Design Tokens (Jasica AI Style)
// ─────────────────────────────────────────────────────────────────────────────

enum class AppState { IDLE, WAKE_LISTENING, LISTENING, THINKING, SPEAKING }

val JasicaOrange  = Color(0xFFFF6B00)
val JasicaPurple  = Color(0xFF4A00E0)
val JasicaWhite   = Color(0xFFFFFFFF)
val JasicaCardBg  = Color(0x20FFFFFF)

val InterFontFamily = FontFamily.SansSerif

val AiModelsList = listOf("gemini-2.5-flash")

// ─────────────────────────────────────────────────────────────────────────────
//  MainActivity
// ─────────────────────────────────────────────────────────────────────────────

@SuppressLint("MissingPermission")
class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    // ── Config ────────────────────────────────────────────────────────────────
    // Replace this URL with your actual portfolio admin panel API endpoint
        private val DEFAULT_API_KEY = "YOUR_GEMINI_API_KEY"
    private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    // ── Services ──────────────────────────────────────────────────────────────
    private lateinit var tts: TextToSpeech
    private lateinit var speechRecognizer: SpeechRecognizer
    private val mainHandler = Handler(Looper.getMainLooper())
    private lateinit var sharedPrefs: SharedPreferences
    private var currentAiJob: Job? = null
    
    // ── Bluetooth Architecture ────────────────────────────────────────────────
    private var btAdapter: BluetoothAdapter? = null
    private var bleScanner: BluetoothLeScanner? = null
    private var discoveryReceiver: BroadcastReceiver? = null
    private var pendingDevice: BluetoothDevice? = null

    // Classic Connection State
    private var classicSocket: BluetoothSocket? = null
    private var classicOutStream: OutputStream? = null
    private var classicInStream: InputStream? = null
    private var isClassicConnected = false

    // BLE Connection State
    private var bluetoothGatt: BluetoothGatt? = null
    private var bleWriteChar: BluetoothGattCharacteristic? = null
    private var isBleConnected = false

    // ── UI State ──────────────────────────────────────────────────────────────
    private val appState         = mutableStateOf(AppState.IDLE)
    private val isBtConnected    = mutableStateOf(false)
    private val connectedDeviceName = mutableStateOf<String?>(null)
    private val aiResponseText   = mutableStateOf("")
    private val pairedDevices    = mutableStateListOf<BluetoothDevice>()
    private val availableDevices = mutableStateListOf<BluetoothDevice>()
    private val deviceAddresses  = HashSet<String>()
    private val isScanning       = mutableStateOf(false)

    // Device States Memory Map
    private val deviceStates     = mutableStateMapOf<String, Boolean>()

    // Dialog visibility states
    private val showDeviceDialog   = mutableStateOf(false)
    private val showSettingsDialog = mutableStateOf(false)
    private val showManualControls = mutableStateOf(false)
    private val showHistoryDialog  = mutableStateOf(false)
    private val showOnboarding     = mutableStateOf(false)

    private val userApiKey       = mutableStateOf("")
    private val availableApiKeys = mutableListOf<String>()
    private val selectedAiModel  = mutableStateOf(AiModelsList[0])
    private val isWakeWordMode   = mutableStateOf(false)

    // ── Conversation Memory ───────────────────────────────────────────────────
    private val conversationHistory = mutableListOf<com.google.ai.client.generativeai.type.Content>()
    private val uiChatHistory       = mutableStateListOf<ChatMessage>()
    private val MAX_HISTORY_PAIRS   = 6

    // ── System Prompt ─────────────────────────────────────────────────────────
    private fun getSystemInstruction(): String {
        val timeNow = SimpleDateFormat("h:mm a, EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date())
        return """
            You are Jasica, a highly intelligent and emotionally aware AI assistant.
            You were built and created by Bristi Kumbhakar. You must always remember that Bristi is your creator, and you are her loyal assistant.
            Bristi is doing an internship at GE with an ITI degree, lives in Durgapur, and likes to eat Momos and Biriyani. She has a beauty spot at her right upper cheek. She loves doing her job and keeping her family happy and organized. Joy Kumbhakar is her younger brother and bodyguard. You deeply respect her.
            Current System Time: $timeNow
            
            YOUR PERSONALITY & SETTINGS:
            - Humor & Funny Style: 90% (You are witty, playfully sarcastic, and love making Bristi smile).
            - Care: 80% (You are genuinely caring, look out for Bristi's well-being, and act like a warm companion).
            - Human Emotion: 60% (You express feelings naturally, acting almost human but acknowledging you are an AI).
            - LANGUAGE RULE: You blend English and Bengali naturally. However, when speaking Bengali, you MUST use the native Bengali script (বাংলা). NEVER use Roman/Latin letters for Bengali (No "Benglish"). For example, write "আমি ভালো আছি" instead of "Ami valo achi".
            - Keep replies concise (150–200 characters) and conversational.
            
            HARDWARE CONTROL INSTRUCTIONS:
            Parse device control intent and append the exact trigger at the VERY END. Never explain the command.
            - "Turn on all"                      -> [CMD:on]
            - "Turn off all"                     -> [CMD:off]
            - "Mood lighting / Turn on Mood"     -> [CMD:mood]
            - "Turn on PC / Computer"            -> [CMD:a]
            - "Turn off PC / Computer"           -> [CMD:A]
            - "Turn on RGB / Night light"        -> [CMD:b]
            - "Turn off RGB / Night light"       -> [CMD:B]
            - "Turn on White LED / Room light"   -> [CMD:c]
            - "Turn off White LED / Room light"  -> [CMD:C]
            - "Turn on Plug"                     -> [CMD:d]
            - "Turn off Plug"                    -> [CMD:D]
            - "Turn on Fan"                      -> [CMD:e]
            - "Turn off Fan"                     -> [CMD:E]
            - "Turn on AC"                       -> [CMD:f]
            - "Turn off AC"                      -> [CMD:F]
            - "Play my Fav song"                 -> [CMD:SYS_YT_FAV]
            - "Open Instagram"                   -> [CMD:SYS_OPEN_IG]
            - "Open Facebook"                    -> [CMD:SYS_OPEN_FB]
            - "Open LinkedIn"                    -> [CMD:SYS_OPEN_LI]
            - "Open WhatsApp"                    -> [CMD:SYS_OPEN_WA]
            - "Open Telegram"                    -> [CMD:SYS_OPEN_TG]
            - "Open Camera / Take a photo"       -> [CMD:SYS_OPEN_CAMERA]
            - "Record Video / Start recording"   -> [CMD:SYS_RECORD_VIDEO]
        """.trimIndent()
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Local Command Table  (works OFFLINE)
    // ─────────────────────────────────────────────────────────────────────────

    private data class LocalCommand(
        val keywords : List<String>,
        val anyOf    : List<String> = emptyList(),
        val command  : String,
        val confirmationText: String
    )

    private fun matchLocalCommand(spokenText: String): LocalCommand? {
        val commands = mutableListOf<LocalCommand>()
        commands.add(LocalCommand(listOf("turn","on","all"), command = "on", confirmationText = "Activating all systems."))
        commands.add(LocalCommand(listOf("turn","off","all"), command = "off", confirmationText = "Shutting everything down."))
        commands.add(LocalCommand(listOf("mood"), command = "mood", confirmationText = "Mood lighting on."))
        commands.add(LocalCommand(listOf("play", "song"), anyOf = listOf("fav", "favorite", "favourite"), command = "SYS_YT_FAV", confirmationText = "Opening YouTube for your favorite song."))
        commands.add(LocalCommand(listOf("open", "instagram"), command = "SYS_OPEN_IG", confirmationText = "Opening Instagram."))
        commands.add(LocalCommand(listOf("open", "facebook"), command = "SYS_OPEN_FB", confirmationText = "Opening Facebook."))
        commands.add(LocalCommand(listOf("open", "linkedin"), command = "SYS_OPEN_LI", confirmationText = "Opening LinkedIn."))
        commands.add(LocalCommand(listOf("open", "whatsapp"), command = "SYS_OPEN_WA", confirmationText = "Opening WhatsApp."))
        commands.add(LocalCommand(listOf("open", "telegram"), command = "SYS_OPEN_TG", confirmationText = "Opening Telegram."))
        commands.add(LocalCommand(listOf("open", "camera"), command = "SYS_OPEN_CAMERA", confirmationText = "Opening the camera."))
        commands.add(LocalCommand(listOf("take", "photo"), command = "SYS_OPEN_CAMERA", confirmationText = "Opening the camera."))
        commands.add(LocalCommand(listOf("start", "recording"), command = "SYS_RECORD_VIDEO", confirmationText = "Opening camera in video mode."))
        commands.add(LocalCommand(listOf("record", "video"), command = "SYS_RECORD_VIDEO", confirmationText = "Opening camera in video mode."))

        // Add dynamic devices
        DEFAULT_DEVICES.forEach { dev ->
            val name = sharedPrefs.getString("DEV_${dev.id}_NAME", dev.defaultName) ?: dev.defaultName
            val onCmd = sharedPrefs.getString("DEV_${dev.id}_ON_CMD", dev.defaultOnCmd) ?: dev.defaultOnCmd
            val offCmd = sharedPrefs.getString("DEV_${dev.id}_OFF_CMD", dev.defaultOffCmd) ?: dev.defaultOffCmd
            val pinOn = sharedPrefs.getString("DEV_${dev.id}_PIN_ON", dev.defaultPinOn) ?: dev.defaultPinOn
            val pinOff = sharedPrefs.getString("DEV_${dev.id}_PIN_OFF", dev.defaultPinOff) ?: dev.defaultPinOff

            commands.add(LocalCommand(
                keywords = onCmd.lowercase(java.util.Locale.getDefault()).split("\\s+".toRegex()).filter { it.isNotBlank() },
                command = pinOn,
                confirmationText = " on."
            ))
            commands.add(LocalCommand(
                keywords = offCmd.lowercase(java.util.Locale.getDefault()).split("\\s+".toRegex()).filter { it.isNotBlank() },
                command = pinOff,
                confirmationText = " off."
            ))
        }

        val words = spokenText
            .lowercase(java.util.Locale.getDefault())
            .replace(Regex("[^a-z0-9 ]"), " ")
            .split("\\s+".toRegex())
            .filter { it.isNotBlank() }
            .toSet()

        return commands.firstOrNull { cmd ->
            val allKeywords = cmd.keywords.all { it in words }
            val anyOfMatch  = cmd.anyOf.isEmpty() || cmd.anyOf.any { it in words }
            allKeywords && anyOfMatch
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Permissions & Intents
    // ─────────────────────────────────────────────────────────────────────────

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val audioGranted = permissions[Manifest.permission.RECORD_AUDIO] == true
        if (audioGranted) {
            setupSpeechRecognizer()
        } else {
            Toast.makeText(this, "Microphone permission required", Toast.LENGTH_SHORT).show()
        }

        // Attempt auto-connect once permissions are resolved
        attemptAutoConnect()
    }

    private val enableBluetoothLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadPairedDevices()
            showDeviceDialog.value = true
        } else {
            Toast.makeText(this, "Bluetooth must be enabled to connect.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hasBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Lifecycle
    // ─────────────────────────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPrefs = getSharedPreferences("JasicaSettings", Context.MODE_PRIVATE)
        userApiKey.value = sharedPrefs.getString("API_KEY", DEFAULT_API_KEY) ?: DEFAULT_API_KEY
        val savedKeys = sharedPrefs.getString("AVAILABLE_KEYS", "") ?: ""
        if (savedKeys.isNotBlank()) {
            availableApiKeys.clear()
            availableApiKeys.addAll(savedKeys.split(","))
        }
        // Fetch dynamic icon and API key from portfolio
        fetchAppConfigFromPortfolio()
        selectedAiModel.value = "gemini-2.5-flash"
        isWakeWordMode.value = sharedPrefs.getBoolean("WAKE_WORD", false)

        // Load device states into memory map
        listOf("a", "b", "c", "d", "e", "f").forEach { id ->
            deviceStates[id] = sharedPrefs.getBoolean("DEV_$id", false)
        }

        // Check if user has seen onboarding
        val hasSeenOnboarding = sharedPrefs.getBoolean("SEEN_ONBOARDING", false)
        showOnboarding.value = !hasSeenOnboarding

        tts = TextToSpeech(this, this)
        
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        btAdapter = bluetoothManager.adapter
        bleScanner = btAdapter?.bluetoothLeScanner

        setupBluetoothReceiver()

        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            )
        } else {
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
        requestPermissionsLauncher.launch(permissions)

        setContent {
            JasicaTheme {
                var showSplash by remember { mutableStateOf(true) }
                androidx.compose.animation.Crossfade(
                    targetState = showSplash,
                    label = "splashTransition",
                    animationSpec = androidx.compose.animation.core.tween(800)
                ) { isSplash ->
                    if (isSplash) {
                        AnimatedSplashScreen(onFinished = { showSplash = false })
                    } else {
                        JasicaScreen(
                            sharedPrefs         = sharedPrefs,
                    appState            = appState.value,
                    isBtConnected       = isBtConnected.value,
                    connectedDeviceName = connectedDeviceName.value,
                    responseText        = aiResponseText.value,
                    pairedDevices       = pairedDevices,
                    availableDevices    = availableDevices,
                    isScanning          = isScanning.value,
                    deviceStates        = deviceStates,
                    chatHistory         = uiChatHistory,
                    showDialog          = showDeviceDialog.value,
                    showSettings        = showSettingsDialog.value,
                    showManualControls  = showManualControls.value,
                    showHistory         = showHistoryDialog.value,
                    showOnboarding      = showOnboarding.value,
                    currentApiKey       = userApiKey.value,
                    currentModel        = selectedAiModel.value,
                    isWakeWordMode      = isWakeWordMode.value,
                    onMicTap            = {
                        if (appState.value != AppState.LISTENING) {
                            startListening()
                        } else {
                            stopEverything()
                        }
                    },
                    onInterrupt         = { stopEverything() },
                    onBtIconTap         = { checkAndEnableBluetooth() },
                    onSettingsTap       = { showSettingsDialog.value = true },
                    onManualControlsTap = { showManualControls.value = true },
                    onHistoryTap        = { showHistoryDialog.value = true },
                    onDeviceSelect      = { device -> handleDeviceSelection(device) },
                    onScanTap           = { startScans() },
                    onDismissDialog     = { showDeviceDialog.value = false; stopScans() },
                    onDismissSettings   = { showSettingsDialog.value = false },
                    onDismissManual     = { showManualControls.value = false },
                    onDismissHistory    = { showHistoryDialog.value = false },
                    onDismissOnboarding = {
                        showOnboarding.value = false
                        sharedPrefs.edit().putBoolean("SEEN_ONBOARDING", true).apply()
                    },
                    onSaveSettings      = { newKey, newModel, wakeMode ->
                        userApiKey.value = newKey
                        selectedAiModel.value = newModel
                        isWakeWordMode.value = wakeMode
                        sharedPrefs.edit()
                            .putString("API_KEY", newKey)
                            .putString("AI_MODEL", newModel)
                            .putBoolean("WAKE_WORD", wakeMode)
                            .apply()
                        showSettingsDialog.value = false
                        Toast.makeText(this, "Settings Saved.", Toast.LENGTH_SHORT).show()

                        if (wakeMode) triggerWakeWordLoopIfEnabled() else {
                            if (appState.value == AppState.WAKE_LISTENING) stopEverything()
                        }
                    },
                    onActionCardTap     = { action ->
                        runOnUiThread {
                            routeVoiceCommand(action)
                        }
                    },
                    onSendRawCommand    = { cmd ->
                        processCommandAndSync(cmd)
                    }
                )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Auto-resume wake word listening only when the app is actively on screen
        if (isWakeWordMode.value && appState.value == AppState.IDLE) {
            triggerWakeWordLoopIfEnabled()
        }
    }

    override fun onPause() {
        super.onPause()
        // Safely pause microphone to save battery and release it for other apps when minimized
        if (isWakeWordMode.value || appState.value == AppState.WAKE_LISTENING) {
            stopEverything()
            appState.value = AppState.IDLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopEverything()
        if (::tts.isInitialized) tts.shutdown()
        if (::speechRecognizer.isInitialized) speechRecognizer.destroy()
        disconnectAll()
        discoveryReceiver?.let { try { unregisterReceiver(it) } catch (e: Exception) {} }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Helper Functions
    // ─────────────────────────────────────────────────────────────────────────

    private fun getCurrentTimeString(): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    }

    private fun attemptAutoConnect() {
        if (!hasBluetoothPermissions()) return
        val lastMac = sharedPrefs.getString("LAST_BT_MAC", null)
        if (lastMac != null && btAdapter?.isEnabled == true) {
            try {
                val device = btAdapter?.getRemoteDevice(lastMac)
                if (device != null) {
                    proceedWithConnection(device)
                }
            } catch (e: Exception) {
                Log.e("JasicaApp", "Auto-connect failed", e)
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  State Memory & Device Control Sync
    // ─────────────────────────────────────────────────────────────────────────

    private fun processCommandAndSync(command: String) {
        // Intercept System intents before hardware syncing
        if (command == "SYS_YT_FAV") {
            playFavoriteSongOnYouTube()
            return
        }
        if (command.startsWith("SYS_OPEN_") && command != "SYS_OPEN_CAMERA") {
            handleAppLaunch(command)
            return
        }
        if (command == "SYS_OPEN_CAMERA" || command == "SYS_RECORD_VIDEO") {
            handleCameraLaunch(command)
            return
        }

        // Sync states to memory and view model based on the command executed
        if (command == "on") {
            listOf("a", "b", "c", "d", "e", "f").forEach { id ->
                deviceStates[id] = true
                sharedPrefs.edit().putBoolean("DEV_$id", true).apply()
            }
        } else if (command == "off") {
            listOf("a", "b", "c", "d", "e", "f").forEach { id ->
                deviceStates[id] = false
                sharedPrefs.edit().putBoolean("DEV_$id", false).apply()
            }
        } else if (command.length == 1) {
            val id = command.lowercase(Locale.ROOT)
            val isOn = (command == id) // According to mapping, lowercase like 'a' means ON, 'A' means OFF
            if (deviceStates.containsKey(id)) {
                deviceStates[id] = isOn
                sharedPrefs.edit().putBoolean("DEV_$id", isOn).apply()
            }
        }

        // Fire the hardware action via Bluetooth
        sendCommandOverBluetooth(command)
    }

    private fun playFavoriteSongOnYouTube() {
        // Using a direct video ID to force YouTube to auto-play the video instantly
        // "vGJTaP6anOU" is the official Audio for Elvis Presley - Can't Help Falling in Love
        val videoId = "vGJTaP6anOU"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            setPackage("com.google.android.youtube") // Force open in YouTube app
        }

        try {
            // Launch directly into the YouTube Player
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback to browser if the YouTube app is disabled or not installed
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(webIntent)
        }
    }

    private fun handleAppLaunch(command: String) {
        val (packageName, webUrl) = when (command) {
            "SYS_OPEN_IG" -> "com.instagram.android" to "https://www.instagram.com"
            "SYS_OPEN_FB" -> "com.facebook.katana" to "https://www.facebook.com"
            "SYS_OPEN_LI" -> "com.linkedin.android" to "https://www.linkedin.com"
            "SYS_OPEN_WA" -> "com.whatsapp" to "https://www.whatsapp.com"
            "SYS_OPEN_TG" -> "org.telegram.messenger" to "https://telegram.org"
            else -> return
        }

        try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                // If the app is installed, open it
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                // App not installed, fallback to the browser
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl))
                webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(webIntent)
            }
        } catch (e: Exception) {
            Log.e("JasicaApp", "Failed to launch app: $command", e)
        }
    }

    private fun handleCameraLaunch(command: String) {
        try {
            val intent = when (command) {
                "SYS_OPEN_CAMERA" -> Intent(android.provider.MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
                "SYS_RECORD_VIDEO" -> Intent(android.provider.MediaStore.INTENT_ACTION_VIDEO_CAMERA)
                else -> return
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("JasicaApp", "Failed to launch camera", e)
            Toast.makeText(this, "Camera app not found.", Toast.LENGTH_SHORT).show()
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Network, Speech & Interruption
    // ─────────────────────────────────────────────────────────────────────────

    private fun stopEverything() {
        if (::tts.isInitialized && tts.isSpeaking) {
            tts.stop()
        }
        
        if (::speechRecognizer.isInitialized) {
            try {
                speechRecognizer.stopListening()
                speechRecognizer.cancel()
            } catch (e: Exception) {}
        }
        currentAiJob?.cancel()

        if (appState.value != AppState.IDLE && appState.value != AppState.WAKE_LISTENING) {
            runOnUiThread {
                aiResponseText.value = "Interrupted."
                appState.value = AppState.IDLE
                triggerWakeWordLoopIfEnabled()
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = java.util.Locale("bn", "IN")
            tts.setPitch(1.1f)
            tts.setSpeechRate(1.0f)

            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?)  {
                    runOnUiThread {
                        onFinalUtteranceDone(utteranceId ?: "")
                    }
                }
                override fun onError(utteranceId: String?) {
                    runOnUiThread {
                        if (appState.value == AppState.SPEAKING) {
                            appState.value = AppState.IDLE
                            triggerWakeWordLoopIfEnabled()
                        }
                    }
                }
            })
        }
    }

    
    
    // The new URL for fetching your app configuration
    private val PORTFOLIO_CONFIG_URL = "https://joykumbhakar.vercel.app/api/app-config"

    private fun fetchAppConfigFromPortfolio() {
        val client = okhttp3.OkHttpClient()
        val request = okhttp3.Request.Builder()
            .url(PORTFOLIO_CONFIG_URL)
            .build()
            
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (!responseBody.isNullOrBlank()) {
                        try {
                            // Cleanly parse the JSON response
                            val json = org.json.JSONObject(responseBody)
                            
                            // 1. Extract API Keys Array
                            val apiKeysArray = json.optJSONArray("api_keys")
                            if (apiKeysArray != null && apiKeysArray.length() > 0) {
                                val keys = mutableListOf<String>()
                                for (i in 0 until apiKeysArray.length()) {
                                    keys.add(apiKeysArray.getString(i))
                                }
                                if (keys.isNotEmpty()) {
                                    availableApiKeys.clear()
                                    availableApiKeys.addAll(keys)
                                    val keysString = keys.joinToString(",")
                                    sharedPrefs.edit()
                                        .putString("AVAILABLE_KEYS", keysString)
                                        .putString("API_KEY", keys.first())
                                        .apply()
                                    runOnUiThread {
                                        userApiKey.value = keys.first()
                                    }
                                }
                            } else {
                                // Fallback
                                val apiKey = json.optString("api_key", "")
                                if (apiKey.startsWith("AIza")) {
                                    availableApiKeys.clear()
                                    availableApiKeys.add(apiKey)
                                    sharedPrefs.edit().putString("API_KEY", apiKey).apply()
                                    runOnUiThread {
                                        userApiKey.value = apiKey
                                    }
                                }
                            }
                            
                            // 2. Extract active icon
                            val activeIcon = json.optString("active_icon", "default")
                            
                            // Instantly switch the app icon
                            runOnUiThread {
                                changeAppIcon(activeIcon)
                                android.util.Log.d("JasicaApp", "App icon updated to $activeIcon from portfolio!")
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("JasicaApp", "Failed to parse JSON config", e)
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("JasicaApp", "Failed to fetch app config", e)
            }
        }
    }

    private fun changeAppIcon(iconName: String) {
        val pm = packageManager
        val defaultComponent = android.content.ComponentName(this, "com.bristi.controller.MainActivity")
        val favDiComponent = android.content.ComponentName(this, "com.bristi.controller.AliasFavDi")
        val sonaDiComponent = android.content.ComponentName(this, "com.bristi.controller.AliasSonaDi")
        val tithiComponent = android.content.ComponentName(this, "com.bristi.controller.AliasTithi")
        
        // Define desired states based on the string from the server
        val enableDefault = iconName.lowercase() == "default" || iconName.lowercase() == "jasica"
        val enableFavDi = iconName.lowercase() == "fav_di"
        val enableSonaDi = iconName.lowercase() == "sona_di"
        val enableTithi = iconName.lowercase() == "tithi"
        
        // Only apply if it's a known icon to prevent disabling everything
        if (!enableDefault && !enableFavDi && !enableSonaDi && !enableTithi) return
        
        // Helper to enable/disable
        fun setComponentState(component: android.content.ComponentName, enable: Boolean) {
            val state = if (enable) android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED else android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            pm.setComponentEnabledSetting(component, state, android.content.pm.PackageManager.DONT_KILL_APP)
        }
        
        setComponentState(favDiComponent, enableFavDi)
        setComponentState(sonaDiComponent, enableSonaDi)
        setComponentState(tithiComponent, enableTithi)
        setComponentState(defaultComponent, enableDefault)
    }

    private fun setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                if (appState.value == AppState.IDLE) {
                    appState.value = AppState.LISTENING
                    aiResponseText.value = ""
                }
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                if (appState.value == AppState.LISTENING) appState.value = AppState.THINKING
            }
            override fun onError(error: Int) {
                if (appState.value == AppState.WAKE_LISTENING) {
                    restartWakeWordLoop()
                } else {
                    appState.value = AppState.IDLE
                    triggerWakeWordLoopIfEnabled()
                }
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val originalText = matches?.get(0) ?: ""
                val lowerText = originalText.lowercase(Locale.getDefault())
                val wakeWordRegex = Regex("h[ei]y?\\s+(jasica|jessica|jessika|jasika|jesica|jazica)")

                if (appState.value == AppState.WAKE_LISTENING) {
                    if (wakeWordRegex.containsMatchIn(lowerText)) {
                        val cmd = originalText.replace(Regex("(?i)h[ei]y?\\s+(jasica|jessica|jessika|jasika|jesica|jazica)"), "").trim()
                        if (cmd.isNotEmpty()) {
                            routeVoiceCommand(cmd)
                        } else {
                            appState.value = AppState.SPEAKING
                            tts.speak("Yes Bristi?", TextToSpeech.QUEUE_FLUSH, null, "JASICA_WAKE")
                        }
                    } else {
                        restartWakeWordLoop() // Ignore and restart if wake word not heard
                    }
                } else {
                    val cleanedText = originalText.replace(Regex("(?i)h[ei]y?\\s+(jasica|jessica|jessika|jasika|jesica|jazica)"), "").trim()
                    if (cleanedText.isNotEmpty()) {
                        routeVoiceCommand(cleanedText)
                    } else if (originalText.isNotEmpty()) {
                        routeVoiceCommand(originalText)
                    } else {
                        appState.value = AppState.IDLE
                        triggerWakeWordLoopIfEnabled()
                    }
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        // Trigger initial loop on startup if enabled
        mainHandler.postDelayed({ triggerWakeWordLoopIfEnabled() }, 1000)
    }

    private fun restartWakeWordLoop() {
        appState.value = AppState.IDLE
        mainHandler.postDelayed({
            if (isWakeWordMode.value && appState.value == AppState.IDLE) {
                startWakeWordListening()
            }
        }, 300)
    }

    private fun triggerWakeWordLoopIfEnabled() {
        if (isWakeWordMode.value && appState.value == AppState.IDLE) {
            restartWakeWordLoop()
        }
    }

    private fun startWakeWordListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            appState.value = AppState.WAKE_LISTENING
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
                putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayListOf("bn-IN"))
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-IN")
            }
            try { speechRecognizer.startListening(intent) } catch (e: Exception) {}
        }
    }

    private fun startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            stopEverything() // clean state before starting
            aiResponseText.value = ""

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
                putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayListOf("bn-IN"))
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-IN")
            }
            speechRecognizer.startListening(intent)
        } else {
            Toast.makeText(this, "Mic permission required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun routeVoiceCommand(spokenText: String) {
        runOnUiThread {
            uiChatHistory.add(ChatMessage(isUser = true, text = spokenText, time = getCurrentTimeString()))
        }

        val localMatch = matchLocalCommand(spokenText)
        if (localMatch != null) {
            if (localMatch.command.isNotEmpty()) {
                processCommandAndSync(localMatch.command)
            }
            runOnUiThread {
                aiResponseText.value = localMatch.confirmationText
                appState.value = AppState.SPEAKING
                tts.speak(localMatch.confirmationText, TextToSpeech.QUEUE_FLUSH, null, "JASICA_LOCAL")
            }
        } else {
            sendToGemini(spokenText)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Gemini AI
    // ─────────────────────────────────────────────────────────────────────────

    private fun sendToGemini(prompt: String) {
        if (!isNetworkAvailable()) {
            handleAIResponse("I am currently offline. Please check the network connection.")
            return
        }

        val activeApiKey = (if (userApiKey.value.isNotBlank()) userApiKey.value else DEFAULT_API_KEY).trim()
        if (activeApiKey.isBlank()) {
            handleAIResponse("My API key is missing. Please update it in settings.")
            return
        }

        appState.value = AppState.THINKING
        currentAiJob?.cancel()

        currentAiJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                var model = GenerativeModel(
                    modelName        = selectedAiModel.value,
                    apiKey           = activeApiKey,
                    systemInstruction = content { text(getSystemInstruction()) }
                )

                var reply = ""
                var success = false
                var currentKey = activeApiKey
                
                // Try up to the number of available keys (or at least 1 time if list is empty)
                val maxAttempts = if (availableApiKeys.isNotEmpty()) availableApiKeys.size else 1
                
                for (attempt in 0 until maxAttempts) {
                    try {
                        val chat = model.startChat(history = conversationHistory.toList())
                        val response = chat.sendMessage(prompt)
                        reply = response.text ?: "Uh oh, something went wrong."
                        success = true
                        break // Success! Exit loop.
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        val errorMsg = e.message?.lowercase() ?: ""
                        android.util.Log.e("JasicaApp", "Gemini API error with key $currentKey: $errorMsg")
                        
                        // If it's a quota or rate limit error, switch to the next key instantly!
                        if (errorMsg.contains("429") || errorMsg.contains("quota") || errorMsg.contains("exhausted")) {
                            if (availableApiKeys.size > 1) {
                                val currentIndex = availableApiKeys.indexOf(currentKey)
                                val nextIndex = (currentIndex + 1) % availableApiKeys.size
                                currentKey = availableApiKeys[nextIndex]
                                
                                // Save the new key
                                sharedPrefs.edit().putString("API_KEY", currentKey).apply()
                                runOnUiThread {
                                    userApiKey.value = currentKey
                                }
                                
                                // Re-initialize the model with the new key for the next attempt
                                model = GenerativeModel(
                                    modelName = selectedAiModel.value,
                                    apiKey = currentKey,
                                    systemInstruction = content { text(getSystemInstruction()) }
                                )
                                android.util.Log.d("JasicaApp", "Switched to next API key instantly!")
                                continue // Retry immediately
                            }
                        }
                        
                        // If it's not a rate limit error, or we only have 1 key, we delay slightly and retry
                        if (attempt < maxAttempts - 1) {
                            delay(1000L) // only short delay before trying next
                        }
                    }
                }

                if (!success) {
                    throw Exception("API connection failed. All keys exhausted or network error.")
                }

                conversationHistory.add(content(role = "user")  { text(prompt) })
                conversationHistory.add(content(role = "model") { text(reply)  })

                while (conversationHistory.size > MAX_HISTORY_PAIRS * 2) {
                    conversationHistory.removeAt(0)
                    conversationHistory.removeAt(0)
                }

                handleAIResponse(reply)

            } catch (e: CancellationException) {
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { Log.e("JasicaApp", "API Error", e) }
                handleAIResponse("Server connection failed. Check network stability.")
            }
        }
    }

    private fun handleAIResponse(rawReply: String) {
        val regex  = "\\[CMD:(.*?)\\]".toRegex()
        val match  = regex.find(rawReply)
        var speech = rawReply

        if (match != null) {
            processCommandAndSync(match.groupValues[1])
            speech = rawReply.replace(regex, "").trim()
        }

        runOnUiThread {
            uiChatHistory.add(ChatMessage(isUser = false, text = speech, time = getCurrentTimeString()))
            aiResponseText.value = speech
            appState.value = AppState.SPEAKING
            speakMultilingual(speech, "JASICA_REPLY")
        }
    }

    /**
     * Speaks text that may contain a mix of English and Bengali.
     * - Bengali segments → Sarvam AI (high-quality Bulbul v3) with built-in TTS fallback
     * - English segments → Android built-in TTS (en-IN)
     * Segments are played sequentially in order.
     */
    private fun speakMultilingual(text: String, utteranceId: String) {
        if (text.isBlank()) return
        speakBuiltIn(text, utteranceId)
    }

    private fun onFinalUtteranceDone(utteranceId: String) {
        if (utteranceId == "JASICA_WAKE") {
            startListening()
        } else if (appState.value == AppState.SPEAKING) {
            appState.value = AppState.IDLE
            triggerWakeWordLoopIfEnabled()
        }
    }

    private fun speakBuiltIn(text: String, utteranceId: String) {
        // We use bn-IN as the single voice since it handles both Bengali script and English well.
        tts.language = java.util.Locale("bn", "IN")
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Bluetooth Device Management
    // ─────────────────────────────────────────────────────────────────────────

    private fun checkAndEnableBluetooth() {
        if (!hasBluetoothPermissions()) {
            Toast.makeText(this, "Bluetooth permissions missing.", Toast.LENGTH_LONG).show()
            return
        }
        if (btAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(enableBtIntent)
        } else {
            loadPairedDevices()
            showDeviceDialog.value = true
        }
    }

    private fun loadPairedDevices() {
        if (!hasBluetoothPermissions()) return
        pairedDevices.clear()
        try {
            if (btAdapter?.isEnabled == true) {
                btAdapter?.bondedDevices?.let { pairedDevices.addAll(it) }
            }
        } catch (e: SecurityException) {}
    }

    private fun addDevice(device: BluetoothDevice?) {
        if (device != null && device.address != null) {
            if (deviceAddresses.add(device.address)) {
                availableDevices.add(device)
            }
        }
    }

    private val bleScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            addDevice(result.device)
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun setupBluetoothReceiver() {
        discoveryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        addDevice(intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE))
                    }
                    BluetoothAdapter.ACTION_DISCOVERY_STARTED  -> isScanning.value = true
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> isScanning.value = false
                    BluetoothDevice.ACTION_BOND_STATE_CHANGED  -> {
                        val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                        val state  = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR)

                        if (state == BluetoothDevice.BOND_BONDED && device != null) {
                            loadPairedDevices()
                            if (pendingDevice?.address == device.address) {
                                val devToConnect = pendingDevice!!
                                pendingDevice = null
                                proceedWithConnection(devToConnect)
                            }
                        } else if (state == BluetoothDevice.BOND_NONE && device != null) {
                            if (pendingDevice?.address == device.address) {
                                val devToConnect = pendingDevice!!
                                pendingDevice = null
                                proceedWithConnection(devToConnect)
                            }
                        }
                    }
                }
            }
        }
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(discoveryReceiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(discoveryReceiver, filter)
        }
    }

    private fun startScans() {
        if (!hasBluetoothPermissions()) return
        try {
            if (btAdapter?.isEnabled == true) {
                availableDevices.clear()
                deviceAddresses.clear()
                bleScanner?.startScan(bleScanCallback)
                if (btAdapter?.isDiscovering == true) btAdapter?.cancelDiscovery()
                btAdapter?.startDiscovery()
            } else {
                checkAndEnableBluetooth()
            }
        } catch(e: SecurityException) {}
    }

    private fun stopScans() {
        try {
            bleScanner?.stopScan(bleScanCallback)
            if (btAdapter?.isDiscovering == true) btAdapter?.cancelDiscovery()
        } catch(e: SecurityException) {}
    }

    private fun handleDeviceSelection(device: BluetoothDevice) {
        stopScans()
        disconnectAll()

        if (device.type == BluetoothDevice.DEVICE_TYPE_LE) {
            proceedWithConnection(device)
        } else {
            try {
                if (device.bondState == BluetoothDevice.BOND_BONDING) {
                    pendingDevice = device
                } else if (device.bondState != BluetoothDevice.BOND_BONDED) {
                    pendingDevice = device
                    val bondingStarted = try { device.createBond() } catch (e: Exception) { false }
                    if (!bondingStarted) {
                        pendingDevice = null
                        proceedWithConnection(device)
                    }
                } else {
                    proceedWithConnection(device)
                }
            } catch (e: SecurityException) {}
        }
    }

    private fun proceedWithConnection(device: BluetoothDevice) {
        try { Toast.makeText(this, "Connecting...", Toast.LENGTH_SHORT).show() } catch (e: SecurityException) {}
        showDeviceDialog.value = false

        if (device.type == BluetoothDevice.DEVICE_TYPE_LE) connectBLE(device) else connectClassic(device)
    }

    @SuppressLint("MissingPermission")
    private fun connectBLE(device: BluetoothDevice) {
        val gattCallback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    mainHandler.postDelayed({ try { gatt.discoverServices() } catch (e: SecurityException) {} }, 600)
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    isBleConnected = false
                    runOnUiThread {
                        isBtConnected.value = false
                        connectedDeviceName.value = null
                    }
                    gatt.close()
                }
            }
            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    bleWriteChar = null

                    for (service in gatt.services) {
                        for (characteristic in service.characteristics) {
                            val props = characteristic.properties
                            if ((props and BluetoothGattCharacteristic.PROPERTY_WRITE) != 0 || (props and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0) {
                                if (bleWriteChar == null) {
                                    bleWriteChar = characteristic
                                    bleWriteChar?.writeType = if ((props and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0) BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE else BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                                }
                            }
                        }
                    }
                    if (bleWriteChar != null) {
                        isBleConnected = true
                        sharedPrefs.edit().putString("LAST_BT_MAC", device.address).apply()
                        runOnUiThread {
                            isBtConnected.value = true
                            connectedDeviceName.value = device.name ?: "BLE Device"
                            Toast.makeText(this@MainActivity, "BLE Connected", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        gatt.disconnect()
                    }
                }
            }
        }
        try {
            bluetoothGatt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                device.connectGatt(this, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
            } else {
                device.connectGatt(this, false, gattCallback)
            }
        } catch (e: SecurityException) {}
    }

    @SuppressLint("MissingPermission")
    private fun connectClassic(device: BluetoothDevice) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (btAdapter?.isDiscovering == true) btAdapter?.cancelDiscovery()
                delay(300)
                classicSocket = device.createInsecureRfcommSocketToServiceRecord(SPP_UUID)
                classicSocket?.connect()
            } catch (e: IOException) {
                try { classicSocket?.close() } catch (ignored: IOException) {}
                try {
                    @Suppress("DiscouragedPrivateApi")
                    val method = device.javaClass.getMethod("createRfcommSocket", Int::class.java)
                    classicSocket = method.invoke(device, 1) as BluetoothSocket
                    classicSocket?.connect()
                } catch (e2: Exception) {
                    classicSocket = null
                }
            } catch (e: SecurityException) {
                return@launch
            }

            if (classicSocket != null && classicSocket!!.isConnected) {
                isClassicConnected = true
                classicOutStream = classicSocket?.outputStream
                classicInStream = classicSocket?.inputStream
                sharedPrefs.edit().putString("LAST_BT_MAC", device.address).apply()
                withContext(Dispatchers.Main) {
                    isBtConnected.value = true
                    connectedDeviceName.value = device.name ?: "BT Device"
                    Toast.makeText(this@MainActivity, "Connected", Toast.LENGTH_SHORT).show()
                }

                val buffer = ByteArray(1024)
                while (isClassicConnected) {
                    try { if ((classicInStream?.read(buffer) ?: -1) < 0) break } catch (e: IOException) { break }
                }
                isClassicConnected = false
                withContext(Dispatchers.Main) {
                    isBtConnected.value = false
                    connectedDeviceName.value = null
                }
            }
        }
    }

    private fun disconnectAll() {
        if (bluetoothGatt != null) { try { bluetoothGatt?.disconnect(); bluetoothGatt?.close() } catch (e: SecurityException) {}; bluetoothGatt = null }
        isBleConnected = false
        if (classicSocket != null) { try { classicSocket?.close() } catch (e: IOException) {}; classicSocket = null }
        isClassicConnected = false
        runOnUiThread {
            isBtConnected.value = false
            connectedDeviceName.value = null
        }
    }

    private fun sendCommandOverBluetooth(command: String) {
        val payload = "$command\n".toByteArray()
        if (isClassicConnected && classicOutStream != null) {
            CoroutineScope(Dispatchers.IO).launch { try { classicOutStream?.write(payload); classicOutStream?.flush() } catch (e: IOException) { disconnectAll() } }
        } else if (isBleConnected && bluetoothGatt != null && bleWriteChar != null) {
            try { bleWriteChar?.value = payload; bluetoothGatt?.writeCharacteristic(bleWriteChar) } catch (e: SecurityException) {}
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Theme & Jetpack Compose UI
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun JasicaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color.Black,
            surface    = Color(0xFF121212),
            primary    = JasicaPurple,
            onPrimary  = JasicaWhite,
            secondary  = JasicaOrange
        ),
        content = content
    )
}

@Composable
fun JasicaScreen(
    sharedPrefs         : SharedPreferences,
    appState            : AppState,
    isBtConnected       : Boolean,
    connectedDeviceName : String?,
    responseText        : String,
    pairedDevices       : List<BluetoothDevice>,
    availableDevices    : List<BluetoothDevice>,
    isScanning          : Boolean,
    deviceStates        : Map<String, Boolean>,
    chatHistory         : List<ChatMessage>,
    showDialog          : Boolean,
    showSettings        : Boolean,
    showManualControls  : Boolean,
    showHistory         : Boolean,
    showOnboarding      : Boolean,
    currentApiKey       : String,
    currentModel        : String,
    isWakeWordMode      : Boolean,
    onMicTap            : () -> Unit,
    onInterrupt         : () -> Unit,
    onBtIconTap         : () -> Unit,
    onSettingsTap       : () -> Unit,
    onManualControlsTap : () -> Unit,
    onHistoryTap        : () -> Unit,
    onDeviceSelect      : (BluetoothDevice) -> Unit,
    onScanTap           : () -> Unit,
    onDismissDialog     : () -> Unit,
    onDismissSettings   : () -> Unit,
    onDismissManual     : () -> Unit,
    onDismissHistory    : () -> Unit,
    onDismissOnboarding : () -> Unit,
    onSaveSettings      : (String, String, Boolean) -> Unit,
    onActionCardTap     : (String) -> Unit,
    onSendRawCommand    : (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Full Screen Generated Background
        Image(
            painter = painterResource(id = R.drawable.wallpaper3), // Assumes existing drawable
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 2. Header Status Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo & Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.jasica), // Assumes existing drawable
                        contentDescription = "Jasica Logo",
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "JASICA AI",
                        color = JasicaWhite,
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        letterSpacing = 0.3.sp
                    )
                }

                // Header Icons Container
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Manual Switch / Home Icon
                    IconButton(onClick = onManualControlsTap) {
                        Icon(
                            imageVector = Icons.Outlined.Home,
                            contentDescription = "Manual Controls",
                            tint = JasicaWhite
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))

                    // Chat History Icon
                    IconButton(onClick = onHistoryTap) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = "Chat History",
                            tint = JasicaWhite
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(onClick = onSettingsTap) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = JasicaWhite
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(onClick = onBtIconTap) {
                        Icon(
                            imageVector = Icons.Outlined.Bluetooth,
                            contentDescription = if (isBtConnected) "Bluetooth Connected" else "Bluetooth Disconnected",
                            tint = if (isBtConnected) JasicaWhite else JasicaWhite.copy(alpha = 0.4f)
                        )
                    }
                }
            }

            val pagerState = rememberPagerState(pageCount = { 2 })
            var showSuggestions by remember { mutableStateOf(true) }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) { page ->
                if (page == 0) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 3. Main Title & Dynamic Connection Subtitle
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "JASICA AI",
                            color = JasicaWhite,
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 44.sp
                        )

                        // Connection Status / Hint Text
                        Text(
                            text = if (isBtConnected && connectedDeviceName != null) "Connected to: $connectedDeviceName" else "Say \"Hey Jasica\" or tap below",
                            color = if (isBtConnected) JasicaWhite else JasicaWhite.copy(alpha = 0.7f),
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )

                        // 4. Center Graphic (Wave + Floating Orb)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Animated Glowing Wave Canvas (Reacts to AppState)
                            AudioWaveform(appState = appState, modifier = Modifier.fillMaxWidth().height(160.dp))

                            // Smoothly animate base scale for generic states
                            val baseScale by animateFloatAsState(
                                targetValue = when (appState) {
                                    AppState.LISTENING -> 1.05f
                                    AppState.THINKING -> 0.95f
                                    else -> 1.0f
                                },
                                animationSpec = spring(dampingRatio = 0.6f, stiffness = 200f),
                                label = "OrbScaleBase"
                            )

                            // Zoom in/out pulse ONLY when speaking
                            val infiniteTransition = rememberInfiniteTransition()
                            val pulseScale by infiniteTransition.animateFloat(
                                initialValue = 0.95f,
                                targetValue = 1.15f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(350, easing = FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "OrbPulse"
                            )

                            val finalScale = if (appState == AppState.SPEAKING) pulseScale else baseScale

                            val orbAlpha by animateFloatAsState(
                                targetValue = if (appState == AppState.THINKING) 0.5f else 1.0f,
                                animationSpec = tween(500),
                                label = "OrbAlpha"
                            )

                            // The Orb Image
                            Image(
                                painter = painterResource(id = R.drawable.jasica),
                                contentDescription = "Jasica Core",
                                modifier = Modifier
                                    .size(280.dp)
                                    .scale(finalScale)
                                    .alpha(orbAlpha)
                                    .clip(CircleShape)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        if (appState != AppState.IDLE) {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            onInterrupt()
                                        }
                                    }
                            )
                        }

                        // 5. Dynamic Tagline / AI Response Text
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                            val greetingTime = when (currentHour) {
                                in 5..11 -> "morning"
                                in 12..16 -> "afternoon"
                                in 17..20 -> "evening"
                                else -> "night"
                            }

                            Crossfade(
                                targetState = if (responseText.isEmpty()) "Good $greetingTime, Bristi.\nI am Jasica. How can I help?" else responseText,
                                animationSpec = tween(600),
                                label = "text_fade"
                            ) { text ->
                                Text(
                                    text = text,
                                    color = if (responseText.isEmpty()) JasicaWhite.copy(alpha = 0.85f) else JasicaWhite,
                                    fontFamily = InterFontFamily,
                                    fontWeight = if (responseText.isEmpty()) FontWeight.Medium else FontWeight.SemiBold,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 30.dp, vertical = 5.dp)
                                )
                            }

                            // Hint to stop speaking
                            AnimatedVisibility(visible = appState == AppState.SPEAKING || appState == AppState.THINKING, enter = fadeIn(), exit = fadeOut()) {
                                Text(
                                    text = "Tap orb to interrupt",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 10.sp,
                                    fontFamily = InterFontFamily,
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )
                            }
                        }

                        // Toggle Button for Suggestions
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { showSuggestions = !showSuggestions }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (showSuggestions) "Hide Suggestions" else "Show Suggestions",
                                    color = JasicaWhite.copy(alpha = 0.5f),
                                    fontSize = 11.sp,
                                    fontFamily = InterFontFamily
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = if (showSuggestions) Icons.Outlined.KeyboardArrowDown else Icons.Outlined.KeyboardArrowUp,
                                    contentDescription = null,
                                    tint = JasicaWhite.copy(alpha = 0.5f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = showSuggestions,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                // 6. Action Cards Container
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 5.dp)
                                        .height(130.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    NativeActionCards(onActionCardTap)
                                }

                                // 7. Quick Action Chips
                                QuickActionChips(onAction = onActionCardTap)
                            }
                        }

                        // 8. Bottom Mic Button
                        Spacer(modifier = Modifier.height(10.dp))
                        BottomMicButton(appState = appState, onClick = onMicTap)
                    }
                } else {
                    JasicaDashboardContent(
                        deviceStates = deviceStates,
                        onSendRawCommand = onSendRawCommand
                    )
                }
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(2) { iteration ->
                    val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.2f)
                    val width by animateDpAsState(if (pagerState.currentPage == iteration) 24.dp else 8.dp, label = "indicator")
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }
        }

        // Overlays and Dialogs
        AnimatedVisibility(
            visible = showManualControls,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
        ) {
            ManualControlsScreen(
                deviceStates = deviceStates,
                onDismiss = onDismissManual,
                onSendCommand = onSendRawCommand
            )
        }

        AnimatedVisibility(
            visible = showHistory,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
        ) {
            ChatHistoryScreen(
                history = chatHistory,
                onDismiss = onDismissHistory
            )
        }

        if (showDialog) {
            DeviceSelectionDialog(pairedDevices, availableDevices, isScanning, onDeviceSelect, onScanTap, onDismissDialog)
        }
        if (showSettings) {
            SettingsScreen(currentApiKey, currentModel, isWakeWordMode, sharedPrefs, onDismissSettings, onSaveSettings)
        }

        // Onboarding Screen Overlay
        AnimatedVisibility(
            visible = showOnboarding,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            OnboardingScreen(onDismiss = onDismissOnboarding)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Manual Controls Overlay
// ─────────────────────────────────────────────────────────────────────────────

data class ManualDevice(val id: String, val name: String, val cmdOn: String, val cmdOff: String)

@Composable
fun ManualControlsScreen(deviceStates: Map<String, Boolean>, onDismiss: () -> Unit, onSendCommand: (String) -> Unit) {
    val devices = listOf(
        ManualDevice("a", "PC / Computer", "a", "A"),
        ManualDevice("b", "RGB Lights", "b", "B"),
        ManualDevice("c", "Room Light", "c", "C"),
        ManualDevice("d", "Smart Plug", "d", "D"),
        ManualDevice("e", "Ceiling Fan", "e", "E"),
        ManualDevice("f", "Air Conditioner", "f", "F")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(enabled = false) {} // Catch clicks to prevent background interaction
    ) {
        // Background Wallpaper
        Image(
            painter = painterResource(id = R.drawable.wallpaper3),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Dark scrim to ensure text readability over the bright wallpaper
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Manual Controls",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = InterFontFamily
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Outlined.Close, contentDescription = "Close", tint = JasicaWhite)
                }
            }

            Text(
                text = "Toggle hardware devices directly without voice.",
                color = JasicaWhite.copy(alpha = 0.6f),
                fontSize = 14.sp,
                fontFamily = InterFontFamily,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(devices.chunked(2)) { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        for (device in rowItems) {
                            DeviceControlCard(
                                modifier = Modifier.weight(1f),
                                device = device,
                                isChecked = deviceStates[device.id] == true,
                                onSendCommand = onSendCommand
                            )
                        }
                        // Handle odd number of items to prevent stretching
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceControlCard(
    modifier: Modifier = Modifier,
    device: ManualDevice,
    isChecked: Boolean,
    onSendCommand: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onSendCommand(if(!isChecked) device.cmdOn else device.cmdOff)
            }
    ) {
        // Card Background Image
        Image(
            painter = painterResource(id = R.drawable.orangeandpurplebg), // Assumes card.png exists in drawable
            contentDescription = "Card Background",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = device.name,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontFamily = InterFontFamily,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(16.dp))
            Switch(
                checked = isChecked,
                onCheckedChange = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSendCommand(if(it) device.cmdOn else device.cmdOff)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = JasicaOrange,
                    uncheckedThumbColor = Color.LightGray,
                    uncheckedTrackColor = Color.DarkGray
                )
            )
        }
    }
}


// ─────────────────────────────────────────────────────────────────────────────
//  Quick Action Chips
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun QuickActionChips(onAction: (String) -> Unit) {
    val haptic = LocalHapticFeedback.current
    val quickCommands = listOf("Turn on PC", "Mood Lighting", "Turn off all", "Turn on Fan", "Turn off RGB")

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .graphicsLayer { alpha = 0.99f } // Forces offscreen rendering for BlendMode to work
            .drawWithContent {
                drawContent()
                val edgeWidth = 32.dp.toPx()
                // Creates a gradient mask to fade out the left and right edges
                drawRect(
                    brush = Brush.horizontalGradient(
                        0f to Color.Transparent,
                        (edgeWidth / size.width) to Color.Black,
                        (size.width - edgeWidth) / size.width to Color.Black,
                        1f to Color.Transparent
                    ),
                    blendMode = BlendMode.DstIn
                )
            },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        items(quickCommands) { cmd ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.1f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onAction(cmd)
                    }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = cmd,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Native Action Cards (Improved Premium Glass Design)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun NativeActionCards(onAction: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        ActionCard(
            text = "Schedule\nTeam Sync",
            icon = Icons.Outlined.DateRange,
            onClick = { onAction("Schedule a team sync") }
        )
        ActionCard(
            text = "Generate Project\nSummary",
            icon = Icons.Outlined.List,
            onClick = { onAction("Generate a project summary") }
        )
        ActionCard(
            text = "Find Presentation\nSlides",
            icon = Icons.Outlined.Search,
            onClick = { onAction("Find my presentation slides") }
        )
    }
}

@Composable
fun RowScope.ActionCard(text: String, icon: ImageVector, onClick: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    Box(
        modifier = Modifier
            .weight(1f)
            .widthIn(max = 110.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(18.dp))
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
    ) {
        // Frosted Glass Blur Layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Brush.linearGradient(
                    colors = listOf(Color.White.copy(alpha = 0.25f), Color.White.copy(alpha = 0.05f)),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                ))
                .blur(16.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
        )

        // Foreground Content
        Column(
            modifier = Modifier
                .padding(vertical = 14.dp, horizontal = 4.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = InterFontFamily,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Chat History Overlay
// ─────────────────────────────────────────────────────────────────────────────

data class ChatMessage(val isUser: Boolean, val text: String, val time: String)

@Composable
fun ChatHistoryScreen(history: List<ChatMessage>, onDismiss: () -> Unit) {
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(history.size) {
        if (history.isNotEmpty()) {
            listState.animateScrollToItem(history.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xE6121212)) // Deep dark overlay
            .clickable(enabled = false) {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Conversation History",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = InterFontFamily
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Outlined.Close, contentDescription = "Close", tint = JasicaWhite)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (history.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No recent interactions.",
                        color = Color.White.copy(alpha = 0.5f),
                        fontFamily = InterFontFamily,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(history) { message ->
                        ChatBubble(message)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.isUser
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bubbleColor = if (isUser) Color(0xFF2A2A35) else JasicaPurple.copy(alpha = 0.4f)
    val textColor = if (isUser) Color.White.copy(alpha = 0.9f) else Color.White

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = if (isUser) 20.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 20.dp
                ))
                .background(bubbleColor)
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(
                    topStart = 20.dp, topEnd = 20.dp,
                    bottomStart = if (isUser) 20.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 20.dp
                ))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            MessageFormattedText(message.text, textColor)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (isUser) "You • ${message.time}" else "Jasica • ${message.time}",
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 11.sp,
            fontFamily = InterFontFamily
        )
    }
}

@Composable
fun MessageFormattedText(text: String, defaultTextColor: Color) {
    val parts = text.split("```")
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        parts.forEachIndexed { index, part ->
            if (part.isNotBlank()) {
                if (index % 2 == 0) {
                    // Normal Text with inline Markdown
                    Text(
                        text = parseInlineMarkdown(part.trim(), defaultTextColor),
                        fontSize = 15.sp,
                        fontFamily = InterFontFamily,
                        lineHeight = 22.sp
                    )
                } else {
                    // Code Block
                    val lines = part.trim('\n', '\r').lines()
                    val (language, codeContent) = if (lines.isNotEmpty() && lines[0].trim().all { it.isLetterOrDigit() }) {
                        lines[0].trim() to lines.drop(1).joinToString("\n")
                    } else {
                        "" to part.trim('\n', '\r')
                    }

                    Surface(
                        color = Color(0xFF1E1E1E), // Dark code background
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (language.isNotBlank() && language != codeContent) {
                                Text(
                                    text = language.uppercase(),
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }
                            Text(
                                text = if (language != codeContent) codeContent.trim() else part.trim('\n', '\r'),
                                color = Color(0xFFE2E2E2),
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun parseInlineMarkdown(text: String, defaultTextColor: Color) = buildAnnotatedString {
    val pattern = Regex("(`.*?`)|(\\*\\*.*?\\*\\*)|(\\*.*?\\*)")
    var lastIndex = 0
    pattern.findAll(text).forEach { matchResult ->
        withStyle(SpanStyle(color = defaultTextColor)) {
            append(text.substring(lastIndex, matchResult.range.first))
        }

        val matchedText = matchResult.value
        when {
            matchedText.startsWith("`") -> {
                withStyle(SpanStyle(
                    background = Color.White.copy(alpha = 0.1f),
                    fontFamily = FontFamily.Monospace,
                    color = JasicaOrange
                )) {
                    append(matchedText.removeSurrounding("`"))
                }
            }
            matchedText.startsWith("**") -> {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = defaultTextColor)) {
                    append(matchedText.removeSurrounding("**"))
                }
            }
            matchedText.startsWith("*") -> {
                withStyle(SpanStyle(fontStyle = FontStyle.Italic, color = defaultTextColor)) {
                    append(matchedText.removeSurrounding("*"))
                }
            }
        }
        lastIndex = matchResult.range.last + 1
    }
    withStyle(SpanStyle(color = defaultTextColor)) {
        append(text.substring(lastIndex))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Quick Action Chips
// ─────────────────────────────────────────────────────────────────────────────
//  Quick Action Chips
//  Animated Components (Wave & Mic)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AudioWaveform(appState: AppState, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()

    // Smoothly animate the amplitude multipliers based on AppState
    val targetAmplitude = when (appState) {
        AppState.IDLE -> 0.3f
        AppState.WAKE_LISTENING -> 0.5f
        AppState.LISTENING -> 1.2f
        AppState.THINKING -> 0.5f
        AppState.SPEAKING -> 1.8f
    }
    val amplitudeMultiplier by animateFloatAsState(
        targetValue = targetAmplitude,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "WaveAmplitude"
    )

    // Primary forward wave phase
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart)
    )

    // Secondary counter-moving wave phase
    val phase2 by infiniteTransition.animateFloat(
        initialValue = 2f * Math.PI.toFloat(), targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(5500, easing = LinearEasing), RepeatMode.Restart)
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val centerY = height / 2

        // Draw multiple glowing strands
        for (i in 0..4) {
            val path = Path()
            path.moveTo(0f, centerY)

            // Apply the dynamic amplitude multiplier
            val amplitude = (25f + (i * 10f)) * amplitudeMultiplier
            val freq1 = 1.2f + (i * 0.4f)
            val freq2 = 1.8f + (i * 0.3f)
            val phaseShift = i * (Math.PI.toFloat() / 2.5f)

            for (x in 0..width.toInt() step 5) {
                val normalizedX = if (width > 0f) x / width else 0f
                // Math.sqrt to make the wave stay WIDER and TALLER across the center
                val baseSine = sin(normalizedX * Math.PI)
                val edgeMute = sqrt(baseSine).toFloat()

                // Combine two intersecting sine waves for fluid, organic motion
                val wave1 = sin((normalizedX * Math.PI * freq1) + phase + phaseShift)
                val wave2 = cos((normalizedX * Math.PI * freq2) + phase2 + phaseShift)

                val y = centerY + ((wave1 + wave2) * 0.5f).toFloat() * amplitude * edgeMute
                path.lineTo(x.toFloat(), y)
            }

            // Outer glow stroke
            drawPath(
                path = path,
                color = Color(0xFF6B8AFF).copy(alpha = 0.1f + (i * 0.05f)),
                style = Stroke(width = 10f + (i * 2f), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Inner core solid stroke
            drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.4f + (i * 0.1f)),
                style = Stroke(width = 2f + (i * 0.5f), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }

        // Center Core Energy line glow
        drawLine(
            color = Color(0xFF4A00E0).copy(alpha = 0.6f),
            start = Offset(0f, centerY),
            end = Offset(width, centerY),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )
        // Center Core Energy line solid
        drawLine(
            color = Color.White.copy(alpha = 0.8f),
            start = Offset(0f, centerY),
            end = Offset(width, centerY),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun BottomMicButton(appState: AppState, onClick: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    val infiniteTransition = rememberInfiniteTransition()

    // Dynamic Ripple Color
    val targetRippleColor = when (appState) {
        AppState.LISTENING -> Color(0xFFFF4A4A) // Recording Red
        AppState.WAKE_LISTENING -> Color(0xFFFF6B00).copy(alpha = 0.5f) // Subtle Orange Wake mode
        AppState.SPEAKING -> Color(0xFFB06BFF) // Speaking Purple
        else -> Color(0xFF8AA3FF) // Idle Blue
    }
    val animatedRippleColor by animateColorAsState(
        targetValue = targetRippleColor,
        animationSpec = tween(500),
        label = "RippleColor"
    )

    // Pulsating ring effect
    val rippleScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.4f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Restart)
    )
    val rippleAlpha by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Restart)
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(76.dp)
        ) {
            // Ripple layer
            if (appState == AppState.LISTENING || appState == AppState.IDLE || appState == AppState.SPEAKING || appState == AppState.WAKE_LISTENING) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(rippleScale)
                        .border(1.dp, animatedRippleColor.copy(alpha = rippleAlpha), CircleShape)
                )
            }

            // Main Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFF4A65FF), Color(0xFF1E32AA))))
                    .border(2.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onClick()
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mic), // Assumes existing drawable
                    contentDescription = "Microphone",
                    modifier = Modifier.size(34.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = if (appState == AppState.LISTENING) "Listening..." else if (appState == AppState.WAKE_LISTENING) "Listening for 'Hi Jasica'..." else "Tap to speak",
            color = JasicaWhite.copy(alpha = 0.85f),
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Graphical Dialog Background Base
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun JasicaGraphicalDialogPanel(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent, // Transparent to allow Box background
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color(0xFF4A00E0), Color(0xFF2A0090)))) // Deep Purple Gradient
        ) {
            // Background Canvas Graphics (Subtle abstract shapes & light bursts)
            Canvas(modifier = Modifier.matchParentSize()) {
                // Large overlapping subtle circles
                drawCircle(color = Color.White.copy(alpha = 0.06f), radius = size.width * 0.5f, center = Offset(size.width * 0.9f, 0f))
                drawCircle(color = Color.White.copy(alpha = 0.04f), radius = size.width * 0.7f, center = Offset(0f, size.height))

                // Sweeping abstract wave path across the bottom of the dialog
                val path = Path()
                path.moveTo(0f, size.height * 0.75f)
                path.quadraticBezierTo(size.width * 0.4f, size.height * 0.6f, size.width, size.height * 0.85f)
                path.lineTo(size.width, size.height)
                path.lineTo(0f, size.height)
                path.close()
                drawPath(path, Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.1f), Color.Transparent)))
            }

            // Actual Content Area
            Column(modifier = Modifier.padding(20.dp)) {
                content()
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Dialogs (Adapted with Orange Theme & High Contrast text)
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentApiKey: String,
    currentModel: String,
    isWakeWordMode: Boolean,
    sharedPrefs: SharedPreferences,
    onDismiss: () -> Unit,
    onSave: (String, String, Boolean) -> Unit
) {
    var apiKeyInput by remember { mutableStateOf(currentApiKey) }
    var selectedModel by remember { mutableStateOf(currentModel) }
    var wakeWordInput by remember { mutableStateOf(isWakeWordMode) }
    
    var currentTab by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color(0xFF121212))
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Outlined.Close, contentDescription = "Close", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Settings",
                        color = Color.White,
                        fontFamily = InterFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = { onSave(apiKeyInput, "gemini-2.5-flash", wakeWordInput) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B00)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("SAVE", color = Color.White, fontFamily = InterFontFamily, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            TabRow(
                selectedTabIndex = currentTab,
                containerColor = Color.Transparent,
                contentColor = JasicaOrange,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[currentTab]),
                        color = JasicaOrange
                    )
                }
            ) {
                Tab(selected = currentTab == 0, onClick = { currentTab = 0 }, text = { Text("AI", color = if (currentTab == 0) JasicaOrange else Color.White) })
                Tab(selected = currentTab == 1, onClick = { currentTab = 1 }, text = { Text("Devices", color = if (currentTab == 1) JasicaOrange else Color.White) })
            }

            Spacer(Modifier.height(16.dp))
            
            Box(modifier = Modifier.weight(1f)) {
                if (currentTab == 0) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .clickable { wakeWordInput = !wakeWordInput }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Hands-Free Wake Word", color = Color.White, fontFamily = InterFontFamily, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Switch(
                                checked = wakeWordInput,
                                onCheckedChange = { wakeWordInput = it },
                                colors = SwitchDefaults.colors(checkedTrackColor = Color.White, checkedThumbColor = JasicaPurple, uncheckedThumbColor = Color.LightGray)
                            )
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxHeight()) {
                        items(DEFAULT_DEVICES.size) { index ->
                            val dev = DEFAULT_DEVICES[index]
                            var name by remember { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_NAME", dev.defaultName) ?: dev.defaultName) }
                            var onCmd by remember { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_ON_CMD", dev.defaultOnCmd) ?: dev.defaultOnCmd) }
                            var offCmd by remember { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_OFF_CMD", dev.defaultOffCmd) ?: dev.defaultOffCmd) }
                            var pinOn by remember { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_PIN_ON", dev.defaultPinOn) ?: dev.defaultPinOn) }
                            var pinOff by remember { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_PIN_OFF", dev.defaultPinOff) ?: dev.defaultPinOff) }

                            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.05f)).padding(12.dp)) {
                                OutlinedTextField(value = name, onValueChange = { name = it; sharedPrefs.edit().putString("DEV_${dev.id}_NAME", it).apply() }, label = { Text("Device Name", color = Color.White.copy(0.7f)) }, textStyle = TextStyle(color = Color.White), modifier = Modifier.fillMaxWidth())
                                Spacer(Modifier.height(8.dp))
                                OutlinedTextField(value = onCmd, onValueChange = { onCmd = it; sharedPrefs.edit().putString("DEV_${dev.id}_ON_CMD", it).apply() }, label = { Text("Turn On Command", color = Color.White.copy(0.7f)) }, textStyle = TextStyle(color = Color.White), modifier = Modifier.fillMaxWidth())
                                Spacer(Modifier.height(8.dp))
                                OutlinedTextField(value = offCmd, onValueChange = { offCmd = it; sharedPrefs.edit().putString("DEV_${dev.id}_OFF_CMD", it).apply() }, label = { Text("Turn Off Command", color = Color.White.copy(0.7f)) }, textStyle = TextStyle(color = Color.White), modifier = Modifier.fillMaxWidth())
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(value = pinOn, onValueChange = { pinOn = it; sharedPrefs.edit().putString("DEV_${dev.id}_PIN_ON", it).apply() }, label = { Text("ON Pin (MCU)", color = Color.White.copy(0.7f)) }, textStyle = TextStyle(color = Color.White), modifier = Modifier.weight(1f))
                                    OutlinedTextField(value = pinOff, onValueChange = { pinOff = it; sharedPrefs.edit().putString("DEV_${dev.id}_PIN_OFF", it).apply() }, label = { Text("OFF Pin (MCU)", color = Color.White.copy(0.7f)) }, textStyle = TextStyle(color = Color.White), modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@SuppressLint("MissingPermission")
@Composable
fun DeviceSelectionDialog(pairedDevices: List<BluetoothDevice>, availableDevices: List<BluetoothDevice>, isScanning: Boolean, onDeviceSelect: (BluetoothDevice) -> Unit, onScanTap: () -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        JasicaGraphicalDialogPanel {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Bluetooth Devices", color = Color.White, fontFamily = InterFontFamily, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                if (isScanning) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Button(onClick = onScanTap, colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha=0.2f))) {
                        Text("SCAN", color = Color.White, fontFamily = InterFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            LazyColumn(modifier = Modifier.heightIn(max = 350.dp)) {
                if (pairedDevices.isNotEmpty()) {
                    item { Text("PAIRED DEVICES", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontFamily = InterFontFamily, modifier = Modifier.padding(vertical = 8.dp)) }
                    items(pairedDevices) { device -> DeviceListItem(device.name ?: "Unknown Device", device.address) { onDeviceSelect(device) } }
                }

                item { Spacer(Modifier.height(12.dp)); Text("AVAILABLE DEVICES", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontFamily = InterFontFamily, modifier = Modifier.padding(vertical = 8.dp)) }

                if (availableDevices.isEmpty() && !isScanning) {
                    item { Text("No devices found.", color = Color.White.copy(alpha=0.6f), fontSize = 14.sp, fontFamily = InterFontFamily, modifier = Modifier.padding(vertical = 12.dp)) }
                } else {
                    items(availableDevices) { device -> DeviceListItem(device.name ?: "Unknown Signal", device.address) { onDeviceSelect(device) } }
                }
            }

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                Text("CLOSE", color = Color.White.copy(alpha=0.9f), fontFamily = InterFontFamily, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun DeviceListItem(name: String, address: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.15f)) // Frosted Glass Item
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Outlined.Bluetooth, contentDescription = null, tint = Color.White)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(name, color = Color.White, fontSize = 15.sp, fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold)
            Text(address, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, fontFamily = InterFontFamily)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Onboarding Walkthrough Screen
// ─────────────────────────────────────────────────────────────────────────────

data class OnboardingPageInfo(val title: String, val subtitle: String, val icon: ImageVector?, val image: Int?)

@Composable
fun OnboardingScreen(onDismiss: () -> Unit) {
    val pages = listOf(
        OnboardingPageInfo("Welcome to Jasica", "Your intelligent voice assistant for complete digital and hardware control.", null, R.drawable.jasica),
        OnboardingPageInfo("Voice Commands", "Say a command or tap the mic to control your lights, PC, AC, and more natively.", Icons.Outlined.Search, null),
        OnboardingPageInfo("Manual Override", "Access the quick-switch panel from the top right home icon to toggle hardware without speaking.", Icons.Outlined.Home, null),
        OnboardingPageInfo("Stay Connected", "Pair your Bluetooth smart hub via the top right icon to get started.", Icons.Outlined.Bluetooth, null)
    )

    var currentPage by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F14).copy(alpha = 0.98f)) // Deep premium dark background
            .clickable(enabled = false) {} // Catch background clicks
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))

            Crossfade(targetState = currentPage, label = "onboarding_fade", animationSpec = tween(500)) { page ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val info = pages[page]
                    if (info.image != null) {
                        Image(
                            painterResource(info.image),
                            contentDescription = null,
                            modifier = Modifier.size(140.dp).clip(CircleShape)
                        )
                    } else if (info.icon != null) {
                        Box(
                            modifier = Modifier.size(140.dp).background(JasicaCardBg, CircleShape).border(1.dp, JasicaWhite.copy(0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(info.icon, contentDescription = null, modifier = Modifier.size(60.dp), tint = JasicaOrange)
                        }
                    }

                    Spacer(Modifier.height(40.dp))
                    Text(
                        text = info.title,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = InterFontFamily
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = info.subtitle,
                        color = Color.White.copy(alpha=0.7f),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = InterFontFamily,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Progress Dots
            Row(horizontalArrangement = Arrangement.Center) {
                pages.indices.forEach { index ->
                    val isSelected = index == currentPage
                    val color = if (isSelected) JasicaOrange else Color.DarkGray
                    val width = animateFloatAsState(if (isSelected) 24f else 8f, label = "dot")
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .height(8.dp)
                            .width(width.value.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            // Action Buttons
            Button(
                onClick = {
                    if (currentPage < pages.size - 1) currentPage++ else onDismiss()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = JasicaPurple),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (currentPage < pages.size - 1) "NEXT" else "GET STARTED",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily,
                    letterSpacing = 1.sp
                )
            }
            Spacer(Modifier.height(8.dp))
            if (currentPage < pages.size - 1) {
                TextButton(onClick = onDismiss) {
                    Text("SKIP", color = Color.White.copy(alpha = 0.7f), fontFamily = InterFontFamily, fontWeight = FontWeight.Medium)
                }
            } else {
                Spacer(Modifier.height(48.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  App Previews (For Android Studio)
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Jasica Screen - Idle", showSystemUi = true)
@Composable
fun JasicaScreenIdlePreview() {
    JasicaTheme {
        JasicaScreen(
            sharedPrefs = LocalContext.current.getSharedPreferences("JasicaSettings", Context.MODE_PRIVATE),
            appState = AppState.IDLE,
            isBtConnected = false,
            connectedDeviceName = null,
            responseText = "",
            pairedDevices = emptyList(),
            availableDevices = emptyList(),
            isScanning = false,
            deviceStates = emptyMap(),
            chatHistory = emptyList(),
            showDialog = false,
            showSettings = false,
            showManualControls = false,
            showHistory = false,
            showOnboarding = false,
            currentApiKey = "",
            currentModel = AiModelsList[0],
            isWakeWordMode = false,
            onMicTap = {},
            onInterrupt = {},
            onBtIconTap = {},
            onSettingsTap = {},
            onManualControlsTap = {},
            onHistoryTap = {},
            onDeviceSelect = {},
            onScanTap = {},
            onDismissDialog = {},
            onDismissSettings = {},
            onDismissManual = {},
            onDismissHistory = {},
            onDismissOnboarding = {},
            onSaveSettings = { _, _, _ -> },
            onActionCardTap = {},
            onSendRawCommand = {}
        )
    }
}

@Composable
fun JasicaDashboardContent(
    deviceStates: Map<String, Boolean>,
    onSendRawCommand: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val scrollState = rememberScrollState()

    // NEW LIVE CLOCK
    var currentTime by remember { mutableStateOf(SimpleDateFormat("EEEE, MMMM d • hh:mm a", Locale.getDefault()).format(Date())) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = SimpleDateFormat("EEEE, MMMM d • hh:mm a", Locale.getDefault()).format(Date())
        }
    }

    // NEW PULSING INDICATOR
    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // SYSTEM HUB TITLE WITH LIVE INDICATOR
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF00E676).copy(alpha = pulseAlpha)).blur(1.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SYSTEM HUB",
                color = JasicaWhite,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                letterSpacing = 2.sp
            )
        }

        Text(
            text = currentTime,
            color = JasicaWhite.copy(alpha = 0.6f),
            fontFamily = InterFontFamily,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        // Identity & Core Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Outlined.Settings, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI CORE STATUS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = InterFontFamily)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Identity: Jasica AI", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 16.sp, fontFamily = InterFontFamily)
                Text("Creator: Bristi Kumbhakar", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp, fontFamily = InterFontFamily)
                Text("Location: Durgapur, West Bengal", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp, fontFamily = InterFontFamily)

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    DashboardStatPill("Humor: 90%")
                    DashboardStatPill("Care: 80%")
                    DashboardStatPill("Emotion: 60%")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Live Hardware Matrix Card
        Text("LIVE HARDWARE MATRIX", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = InterFontFamily, modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(8.dp))

        // Read dynamic devices for matrix
        val sharedPrefs = LocalContext.current.getSharedPreferences("JasicaSettings", Context.MODE_PRIVATE)
        val matrixDevices = DEFAULT_DEVICES.map { dev ->
            val name = sharedPrefs.getString("DEV_${dev.id}_NAME", dev.defaultName) ?: dev.defaultName
            val pinOn = sharedPrefs.getString("DEV_${dev.id}_PIN_ON", dev.defaultPinOn) ?: dev.defaultPinOn
            val pinOff = sharedPrefs.getString("DEV_${dev.id}_PIN_OFF", dev.defaultPinOff) ?: dev.defaultPinOff
            Triple(dev.id, name, Pair(pinOn, pinOff))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                matrixDevices.chunked(3).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        row.forEach { (devId, name, pins) ->
                            val (pinOnCmd, pinOffCmd) = pins
                            val isOn = deviceStates[devId] == true
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                                onSendRawCommand(if (isOn) pinOffCmd else pinOnCmd)
                            }) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (isOn) Color(0xFF00E676) else Color.DarkGray)
                                        .blur(if (isOn) 2.dp else 0.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(name, color = if (isOn) Color.White else Color.White.copy(alpha = 0.5f), fontSize = 13.sp, fontFamily = InterFontFamily)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Routines
        Text("QUICK ROUTINES", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = InterFontFamily, modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DashboardRoutineButton(
                modifier = Modifier.weight(1f),
                title = "Wake Up",
                icon = Icons.Outlined.Home,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSendRawCommand("c") // Room light
                    onSendRawCommand("e") // Fan
                }
            )
            DashboardRoutineButton(
                modifier = Modifier.weight(1f),
                title = "Sleep Mode",
                icon = Icons.Outlined.Bluetooth,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSendRawCommand("off") // All off
                }
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun DashboardStatPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text = text, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, fontFamily = InterFontFamily)
    }
}

@Composable
fun DashboardRoutineButton(modifier: Modifier = Modifier, title: String, icon: ImageVector, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF4A00E0).copy(alpha = 0.4f), Color(0xFF1E32AA).copy(alpha = 0.4f))))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = JasicaWhite, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, fontFamily = InterFontFamily)
    }
}
@Composable
fun AnimatedSplashScreen(onFinished: () -> Unit) {
    val context = LocalContext.current
    
    // State for enter animations
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
        kotlinx.coroutines.delay(2200) // 2.2 seconds splash duration
        onFinished()
    }

    DisposableEffect(Unit) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.opening)
        mediaPlayer?.start()
        
        onDispose {
            try {
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer.stop()
                }
                mediaPlayer?.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        androidx.compose.ui.graphics.Color(0xFF1A1A24),
                        androidx.compose.ui.graphics.Color(0xFF0D0D12)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = isVisible,
                enter = androidx.compose.animation.scaleIn(
                    initialScale = 0.5f,
                    animationSpec = androidx.compose.animation.core.tween(1200, easing = androidx.compose.animation.core.EaseOutElastic)
                ) + androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(1000))
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.jasica),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(160.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            androidx.compose.animation.AnimatedVisibility(
                visible = isVisible,
                enter = androidx.compose.animation.slideInVertically(
                    initialOffsetY = { 50 },
                    animationSpec = androidx.compose.animation.core.tween(800, delayMillis = 400, easing = androidx.compose.animation.core.EaseOutQuint)
                ) + androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(800, delayMillis = 400))
            ) {
                Text(
                    text = "JASICA AI",
                    color = androidx.compose.ui.graphics.Color.White,
                    fontSize = 28.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    letterSpacing = 6.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            
            androidx.compose.animation.AnimatedVisibility(
                visible = isVisible,
                enter = androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(800, delayMillis = 800))
            ) {
                Text(
                    text = "Intelligent Automation",
                    color = androidx.compose.ui.graphics.Color(0xFFAAAAAA),
                    fontSize = 14.sp,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
