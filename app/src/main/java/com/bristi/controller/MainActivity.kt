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
import androidx.lifecycle.lifecycleScope
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.*
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
import org.json.JSONObject
import coil.compose.AsyncImage
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import dev.chrisbanes.haze.*
import androidx.compose.ui.layout.onSizeChanged

import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.shadow
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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.horizontalScroll
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

    private val sharedOkHttpClient = okhttp3.OkHttpClient()

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
    private val connectedDeviceAddress = mutableStateOf<String?>(null)
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
    private val showVoiceCalibration = mutableStateOf(false)
    private val calibrationIndex     = mutableStateOf(0)
    private val calibrationRecognizedText = mutableStateOf("")
    private val showArduinoCode    = mutableStateOf(false)

    // Mic error state — null means no error, non-null shows the error dialog
    enum class MicErrorType { PERMISSION_DENIED, MIC_IN_USE, HARDWARE_ERROR, RECOGNIZER_UNAVAILABLE }
    private val micErrorType = mutableStateOf<MicErrorType?>(null)

    private val userApiKey        = mutableStateOf("")
    private val availableApiKeys  = mutableListOf<String>()
    private val selectedAiModel   = mutableStateOf(AiModelsList[0])
    private val isWakeWordMode    = mutableStateOf(false)
    private val isAdvancedAiMode  = mutableStateOf(false) // kept for compat
    // Online mode — master switch + key-source selector
    private val isOnlineModeEnabled = mutableStateOf(false)
    private val useAdminPanelKey    = mutableStateOf(true)  // true=portfolio key, false=user's own key

    // ── Conversation Memory ───────────────────────────────────────────────────
    private val conversationHistory = mutableListOf<org.json.JSONObject>()
    private val uiChatHistory       = mutableStateListOf<ChatMessage>()
    private fun addChat(message: ChatMessage) {
        if (sharedPrefs.getBoolean("HISTORY_LOGGING", true)) {
            uiChatHistory.add(message)
        }
    }
    private val MAX_HISTORY_PAIRS   = 6

    // ── Timer & Blink Control Memory ─────────────────────────────────────────
    private var activeBlinkJob: Job? = null
    private val activeTimerJobs = java.util.concurrent.ConcurrentHashMap<String, Job>()
    internal val activeTimerEndTimes = androidx.compose.runtime.mutableStateMapOf<String, Long>()

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
            - "Turn on 1st LED / LED 1"          -> [CMD:a]
            - "Turn off 1st LED / LED 1"         -> [CMD:A]
            - "Turn on 2nd LED / LED 2"          -> [CMD:b]
            - "Turn off 2nd LED / LED 2"         -> [CMD:B]
            - "Turn on 3rd LED / LED 3"          -> [CMD:c]
            - "Turn off 3rd LED / LED 3"         -> [CMD:C]
            - "Turn on 4th LED / LED 4"          -> [CMD:d]
            - "Turn off 4th LED / LED 4"         -> [CMD:D]
            - "Turn on 5th LED / LED 5"          -> [CMD:e]
            - "Turn off 5th LED / LED 5"         -> [CMD:E]
            - "Turn on 6th LED / LED 6"          -> [CMD:f]
            - "Turn off 6th LED / LED 6"         -> [CMD:F]
            - "Turn on LED 1 for 10 seconds"     -> [CMD:TIMER:a:A:10s:1st LED]
            - "Turn on LED 5 for 1 minute"       -> [CMD:TIMER:e:E:1m:5th LED]
            - "Set timer for 30 minutes for LED 1" -> [CMD:TIMER:a:A:30m:1st LED]
            - "Turn off LED 2 for 5 minutes"     -> [CMD:TIMER:B:b:5m:2nd LED]
            - "Blink LED 1 for 5 times"          -> [CMD:BLINK:a:A:5:1st LED]
            - "Blink LED 3 3 times"              -> [CMD:BLINK:c:C:3:3rd LED]
            - "Blink all 4 times"                -> [CMD:BLINK:on:off:4:সব ডিভাইস]
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

    // ─────────────────────────────────────────────────────────────────────────
    //  Alias Expansion Table  — maps natural variations to canonical words
    // ─────────────────────────────────────────────────────────────────────────

    private val WORD_ALIASES = mapOf(
        // Action aliases
        "switch"      to "turn",
        "activate"    to "on",
        "enable"      to "on",
        "put"         to "on",
        "jalao"       to "on",
        "chalu"       to "on",
        "khulo"       to "on",
        "lagao"       to "on",
        "deactivate"  to "off",
        "disable"     to "off",
        "kill"        to "off",
        "shut"        to "off",
        "nevao"       to "off",
        "bondho"      to "off",
        "nijhao"      to "off",
        // Device aliases
        "led"         to "light",
        "leds"        to "light",
        "lights"      to "light",
        "lamp"        to "light",
        "bulb"        to "light",
        "white"       to "light",
        "alo"         to "light",
        "computer"    to "pc",
        "laptop"      to "pc",
        "desktop"     to "pc",
        "hub"         to "pc",
        "nightlight"  to "rgb",
        "colorlight"  to "rgb",
        "cooler"      to "ac",
        "aircon"      to "ac",
        "conditioner" to "ac",
        "ceiling"     to "fan",
        "pakha"       to "fan",
        "socket"      to "plug",
        "charger"     to "plug",
        "outlet"      to "plug",
        // App aliases
        "insta"       to "instagram",
        "ig"          to "instagram",
        "fb"          to "facebook",
        "linked"      to "linkedin",
        "wa"          to "whatsapp",
        "wapp"        to "whatsapp",
        "w"           to "whatsapp",
        "watsapp"     to "whatsapp",
        "watshap"     to "whatsapp",
        "whatsup"     to "whatsapp",
        "watsup"      to "whatsapp",
        "whatapp"     to "whatsapp",
        "wsp"         to "whatsapp",
        "wup"         to "whatsapp",
        "whats"       to "whatsapp",
        "wat"         to "whatsapp",
        "whata"       to "whatsapp",
        "tg"          to "telegram",
        "tele"        to "telegram",
        "yt"          to "youtube",
        "tube"        to "youtube",
        // Media aliases
        "music"       to "song",
        "tune"        to "song",
        "track"       to "song",
        "playlist"    to "song",
        "gaan"        to "song",
        "gana"        to "song",
        "bajna"       to "song",
        // Camera aliases
        "pic"         to "photo",
        "selfie"      to "photo",
        "snap"        to "photo",
        "picture"     to "photo",
        "chobi"       to "photo",
        "shoot"       to "video",
        "filming"     to "video",
        // Water
        "water"       to "water",
        "jol"         to "water",
        "pani"        to "water",
        "drink"       to "water",
        // General
        "everything"  to "all",
        "every"       to "all",
        "sob"         to "all",
        "shob"        to "all"
    )

    // ─────────────────────────────────────────────────────────────────────────
    //  Levenshtein Distance — for single-word typo tolerance
    // ─────────────────────────────────────────────────────────────────────────

    private fun levenshtein(a: String, b: String): Int {
        val m = a.length; val n = b.length
        val dp = Array(m + 1) { IntArray(n + 1) }
        for (i in 0..m) dp[i][0] = i
        for (j in 0..n) dp[0][j] = j
        for (i in 1..m) for (j in 1..n) {
            dp[i][j] = if (a[i-1] == b[j-1]) dp[i-1][j-1]
            else 1 + minOf(dp[i-1][j], dp[i][j-1], dp[i-1][j-1])
        }
        return dp[m][n]
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Fuzzy Token Expander — expands spoken words using aliases + Levenshtein
    // ─────────────────────────────────────────────────────────────────────────

    private fun expandTokens(raw: String): Set<String> {
        val cleaned = raw
            .lowercase(java.util.Locale.getDefault())
            .replace(Regex("^h[ei]y?\\s+(jasica|jessica|jessika|jasika|jesica|jazica)\\s*"), "")
            .replace(Regex("[^a-z0-9 ]"), " ")
        val words = cleaned.split("\\s+".toRegex()).filter { it.isNotBlank() }
        val expanded = mutableSetOf<String>()
        expanded.addAll(words)

        val nonWaWords = setOf(
            "water", "white", "weather", "who", "what", "where", "when", "why",
            "work", "with", "would", "will", "wah", "won", "whose", "which",
            "wait", "wake", "welcome", "world", "week", "weekend", "wish",
            "watch", "warm", "window", "windows", "working", "we", "was",
            "were", "well", "walk", "way", "wall", "wife", "wrong", "write",
            "without", "word", "words", "website", "want", "went"
        )

        words.forEach { word ->
            // Direct alias lookup
            WORD_ALIASES[word]?.let { expanded.add(it) }

            // If any word starts with 'w', automatically recognize WhatsApp intent
            if (word.startsWith("w") && !nonWaWords.contains(word)) {
                expanded.add("whatsapp")
                expanded.add("wa")
            }

            // Levenshtein fuzzy match against alias keys (only for words >= 4 chars)
            if (word.length >= 4) {
                WORD_ALIASES.entries.forEach { (alias, canonical) ->
                    val dist = levenshtein(word, alias)
                    if (alias.length >= 6 && dist <= 2) {
                        expanded.add(canonical)
                    } else if (alias.length >= 4 && dist <= 1) {
                        expanded.add(canonical)
                    }
                }
            }
        }

        // Detect ON/OFF intent from the expanded word set
        val onTriggers  = setOf("on", "activate", "enable", "open", "start", "jalao", "chalu", "lagao")
        val offTriggers = setOf("off", "deactivate", "disable", "close", "kill", "shut", "nevao", "bondho")
        val bigramPairs = words.zipWithNext().map { (a, b) -> "$a $b" }
        val hasOn  = expanded.any { it in onTriggers }  ||
                     bigramPairs.any { it == "turn on" || it == "switch on" || it == "put on" || it == "on koro" || it == "chalu koro" }
        val hasOff = expanded.any { it in offTriggers } ||
                     bigramPairs.any { it == "turn off" || it == "switch off" || it == "shut down" || it == "off koro" || it == "bondho koro" }
        if (hasOn)  expanded.add("on")
        if (hasOff) expanded.add("off")

        return expanded
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Upgraded matchLocalCommand — fuzzy + alias + scoring  (OFFLINE, API-free)
    // ─────────────────────────────────────────────────────────────────────────

    private fun matchLocalCommand(spokenText: String): LocalCommand? {
        val commands = mutableListOf<LocalCommand>()

        // ── Master controls ──────────────────────────────────────────────────
        commands.add(LocalCommand(listOf("on","all"), command = "on", confirmationText = "ঠিক আছে বৃষ্টি বস! তোমার ঘরের সব ডিভাইস একসাথে অন করে দিচ্ছি!"))
        commands.add(LocalCommand(listOf("off","all"), command = "off", confirmationText = "ঠিক আছে বৃষ্টি! সব ডিভাইস একসাথে অফ করে দিলাম, শান্তিতে থাকো!"))
        commands.add(LocalCommand(listOf("mood"), command = "mood", confirmationText = "বাহ্ বৃষ্টি! মুড লাইটিং অন করে দিচ্ছি, একদম দারুণ পরিবেশ!"))

        // ── Media / App shortcuts ────────────────────────────────────────────
        commands.add(LocalCommand(listOf("play","song"), anyOf = listOf("fav","favorite","favourite","sad","sad song","favorite song"), command = "SYS_YT_FAV", confirmationText = "ঠিক আছে বস, আজ মন খারাপ বুঝি যে স্যাড গান চালাতে বলছো? যাই হোক, আমি ইউটিউব থেকে তোমার পছন্দের গানটা চালিয়ে দিচ্ছি!"))
        commands.add(LocalCommand(listOf("song"), anyOf = listOf("fav","favorite","favourite","sad","sad song"), command = "SYS_YT_FAV", confirmationText = "ঠিক আছে বস, আজ মন খারাপ বুঝি যে স্যাড গান চালাতে বলছো? যাই হোক, আমি ইউটিউব থেকে তোমার পছন্দের গানটা চালিয়ে দিচ্ছি!"))
        commands.add(LocalCommand(listOf("sad","song"), command = "SYS_YT_FAV", confirmationText = "ঠিক আছে বস, আজ মন খারাপ বুঝি যে স্যাড গান চালাতে বলছো? যাই হোক, আমি ইউটিউব থেকে তোমার পছন্দের গানটা চালিয়ে দিচ্ছি!"))
        commands.add(LocalCommand(listOf("youtube"), anyOf = listOf("fav","favorite","favourite","play","sad","song"), command = "SYS_YT_FAV", confirmationText = "ঠিক আছে বস, আজ মন খারাপ বুঝি যে স্যাড গান চালাতে বলছো? যাই হোক, আমি ইউটিউব থেকে তোমার পছন্দের গানটা চালিয়ে দিচ্ছি!"))
        commands.add(LocalCommand(listOf("instagram"), command = "SYS_OPEN_IG", confirmationText = "ঠিক আছে বৃষ্টি বস, ইনস্টাগ্রাম ওপেন করে দিচ্ছি!"))
        commands.add(LocalCommand(listOf("facebook"), command = "SYS_OPEN_FB", confirmationText = "অবশ্যই বৃষ্টি, ফেসবুক ওপেন করে দিচ্ছি!"))
        commands.add(LocalCommand(listOf("linkedin"), command = "SYS_OPEN_LI", confirmationText = "ঠিক আছে বৃষ্টি, লিঙ্কডইন ওপেন করে দিচ্ছি!"))
        commands.add(LocalCommand(listOf("whatsapp"), command = "SYS_OPEN_WA", confirmationText = "ঠিক আছে বৃষ্টি, হোয়াটসঅ্যাপ খুলে দিচ্ছি!"))
        commands.add(LocalCommand(listOf("wa"), command = "SYS_OPEN_WA", confirmationText = "ঠিক আছে বৃষ্টি, হোয়াটসঅ্যাপ খুলে দিচ্ছি!"))
        commands.add(LocalCommand(listOf("w"), command = "SYS_OPEN_WA", confirmationText = "ঠিক আছে বৃষ্টি, হোয়াটসঅ্যাপ খুলে দিচ্ছি!"))
        commands.add(LocalCommand(listOf("telegram"), command = "SYS_OPEN_TG", confirmationText = "ঠিক আছে বৃষ্টি বস, টেলিগ্রাম ওপেন করে দিচ্ছি!"))
        commands.add(LocalCommand(listOf("camera"), command = "SYS_OPEN_CAMERA", confirmationText = "স্মাইল বৃষ্টি! ক্যামেরা ওপেন করে দিচ্ছি, সুন্দর একটা ছবি তোলো!"))
        commands.add(LocalCommand(listOf("photo"), command = "SYS_OPEN_CAMERA", confirmationText = "স্মাইল বৃষ্টি! ক্যামেরা ওপেন করে দিচ্ছি, সুন্দর একটা ছবি তোলো!"))
        commands.add(LocalCommand(listOf("take","photo"), command = "SYS_OPEN_CAMERA", confirmationText = "স্মাইল বৃষ্টি! ক্যামেরা ওপেন করে দিচ্ছি, সুন্দর একটা ছবি তোলো!"))
        commands.add(LocalCommand(listOf("record","video"), command = "SYS_RECORD_VIDEO", confirmationText = "ভিডিও রেকর্ডিং মোড অন করে দিচ্ছি বৃষ্টি বস!"))
        commands.add(LocalCommand(listOf("video"), anyOf = listOf("record","start","shoot","film"), command = "SYS_RECORD_VIDEO", confirmationText = "ভিডিও রেকর্ডিং মোড অন করে দিচ্ছি বৃষ্টি বস!"))

        // ── Water Reminder Shortcut ──────────────────────────────────────────
        commands.add(LocalCommand(listOf("water"), anyOf = listOf("drink","jol","pani","remind","reminder"), command = "SYS_WATER", confirmationText = "একদম বৃষ্টি! ৩০ মিনিট পর আবার জল খাওয়ার রিমাইন্ডার দিয়ে দেব, সুস্থ থাকা দরকার!"))
        commands.add(LocalCommand(listOf("drink","water"), command = "SYS_WATER", confirmationText = "একদম বৃষ্টি! ৩০ মিনিট পর আবার জল খাওয়ার রিমাইন্ডার দিয়ে দেব, সুস্থ থাকা দরকার!"))

        // ── Dynamic device commands ───────────────────────────────────────────
        DEFAULT_DEVICES.forEachIndexed { index, dev ->
            val devName = sharedPrefs.getString("DEV_${dev.id}_NAME", dev.defaultName) ?: dev.defaultName
            val onCmd  = sharedPrefs.getString("DEV_${dev.id}_ON_CMD",  dev.defaultOnCmd)  ?: dev.defaultOnCmd
            val offCmd = sharedPrefs.getString("DEV_${dev.id}_OFF_CMD", dev.defaultOffCmd) ?: dev.defaultOffCmd
            val pinOn  = sharedPrefs.getString("DEV_${dev.id}_PIN_ON",  dev.defaultPinOn)  ?: dev.defaultPinOn
            val pinOff = sharedPrefs.getString("DEV_${dev.id}_PIN_OFF", dev.defaultPinOff) ?: dev.defaultPinOff

            val onKeywords  = onCmd.lowercase(java.util.Locale.getDefault()).split("\\s+".toRegex()).filter { it.isNotBlank() }
            val offKeywords = offCmd.lowercase(java.util.Locale.getDefault()).split("\\s+".toRegex()).filter { it.isNotBlank() }

            val isLedOrLight = devName.contains("LED", true) || devName.contains("Light", true) || devName.contains("White", true)
            val onConfirm = if (isLedOrLight) {
                listOf(
                    "হ্যাঁ বৃষ্টি, আমি $devName অন করে দিচ্ছি।",
                    "ঠিক আছে বস, $devName জ্বালিয়ে দিলাম।",
                    "$devName অন করা হয়েছে বৃষ্টি বস!",
                    "অবশ্যই বৃষ্টি, $devName অন করে দিচ্ছি!"
                ).random()
            } else {
                listOf(
                    "ঠিক আছে বৃষ্টি বস, তোমার কথামতো $devName অন করে দিচ্ছি!",
                    "অবশ্যই বস, $devName অন করা হলো।",
                    "$devName অন করে দিয়েছি বৃষ্টি!",
                    "হ্যাঁ বৃষ্টি, $devName চালু করে দিলাম।"
                ).random()
            }
            val offConfirm = listOf(
                "ঠিক আছে বৃষ্টি বস, $devName অফ করে দিলাম।",
                "ওকে বস, $devName বন্ধ করা হয়েছে।",
                "হ্যাঁ বৃষ্টি, $devName অফ করে দিয়েছি।",
                "$devName বন্ধ করে দিলাম বৃষ্টি!"
            ).random()

            commands.add(LocalCommand(keywords = onKeywords,  command = pinOn,  confirmationText = onConfirm))
            commands.add(LocalCommand(keywords = offKeywords, command = pinOff, confirmationText = offConfirm))

            // Extra alias variants for LED numbers (e.g. "turn on led 1", "turn on 1st led", "turn on led one")
            val numStr = (index + 1).toString()
            val ordinalStr = when (index + 1) {
                1 -> "1st"; 2 -> "2nd"; 3 -> "3rd"; 4 -> "4th"; 5 -> "5th"; 6 -> "6th"; else -> "${index + 1}th"
            }
            val wordNum = when (index + 1) {
                1 -> "one"; 2 -> "two"; 3 -> "three"; 4 -> "four"; 5 -> "five"; 6 -> "six"; else -> ""
            }
            commands.add(LocalCommand(keywords = listOf("on", "led", numStr), command = pinOn, confirmationText = onConfirm))
            commands.add(LocalCommand(keywords = listOf("off", "led", numStr), command = pinOff, confirmationText = offConfirm))
            commands.add(LocalCommand(keywords = listOf("on", ordinalStr, "led"), command = pinOn, confirmationText = onConfirm))
            commands.add(LocalCommand(keywords = listOf("off", ordinalStr, "led"), command = pinOff, confirmationText = offConfirm))
            if (wordNum.isNotEmpty()) {
                commands.add(LocalCommand(keywords = listOf("on", "led", wordNum), command = pinOn, confirmationText = onConfirm))
                commands.add(LocalCommand(keywords = listOf("off", "led", wordNum), command = pinOff, confirmationText = offConfirm))
            }
        }

        // ── Fuzzy token expansion ─────────────────────────────────────────────
        val expandedWords = expandTokens(spokenText)

        // ── Scoring pass: pick best-matching command ───────────────────────────
        // Valid if ALL keywords match (strict), OR score ratio >= 0.75 for larger sets.
        data class ScoredCommand(val cmd: LocalCommand, val score: Int)
        val scored = commands.mapNotNull { cmd ->
            val matchedCount = cmd.keywords.count { it in expandedWords }
            val anyOfMatch   = cmd.anyOf.isEmpty() || cmd.anyOf.any { it in expandedWords }
            val allMatch     = cmd.keywords.all { it in expandedWords }
            val ratio        = if (cmd.keywords.isEmpty()) 0f else matchedCount.toFloat() / cmd.keywords.size

            if (anyOfMatch && (allMatch || (cmd.keywords.size >= 2 && ratio >= 0.75f))) {
                ScoredCommand(cmd, matchedCount)
            } else null
        }

        // Return highest-scoring command; break ties by specificity (more keywords = more specific)
        return scored.maxByOrNull { it.score * 100 + it.cmd.keywords.size }?.cmd
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Special Commands: Timer & Blink Logic (OFFLINE + DYNAMIC)
    // ─────────────────────────────────────────────────────────────────────────

    data class ResolvedDevice(
        val id: String,
        val name: String,
        val pinOn: String,
        val pinOff: String
    )

    sealed class SpecialCommandResult {
        data class Timer(
            val device: ResolvedDevice,
            val isTurningOn: Boolean,
            val durationMs: Long,
            val durationLabel: String
        ) : SpecialCommandResult()

        data class Blink(
            val device: ResolvedDevice,
            val count: Int
        ) : SpecialCommandResult()
    }

    private fun getResolvedDeviceForConfig(dev: DeviceConfig): ResolvedDevice {
        val devName = sharedPrefs.getString("DEV_${dev.id}_NAME", dev.defaultName) ?: dev.defaultName
        val pinOn  = sharedPrefs.getString("DEV_${dev.id}_PIN_ON",  dev.defaultPinOn)  ?: dev.defaultPinOn
        val pinOff = sharedPrefs.getString("DEV_${dev.id}_PIN_OFF", dev.defaultPinOff) ?: dev.defaultPinOff
        return ResolvedDevice(dev.id, devName, pinOn, pinOff)
    }

    private fun resolveTargetDevice(target: String): ResolvedDevice? {
        val clean = target.lowercase(Locale.getDefault()).trim()

        // 1. All devices
        if (clean.contains("all") || clean.contains("everything") || clean.contains("sob") || clean.contains("shob") || clean.contains("সব")) {
            return ResolvedDevice("all", "সব ডিভাইস", "on", "off")
        }

        // 2. Direct LED numbers
        val led1Keywords = listOf("1st led", "first led", "led 1", "led1", "1st light", "1st device", "device 1", "light 1", "led one", "1st", " 1 ")
        val led2Keywords = listOf("2nd led", "second led", "led 2", "led2", "2nd light", "2nd device", "device 2", "light 2", "led two", "2nd", " 2 ")
        val led3Keywords = listOf("3rd led", "third led", "led 3", "led3", "3rd light", "3rd device", "device 3", "light 3", "led three", "3rd", " 3 ")
        val led4Keywords = listOf("4th led", "fourth led", "led 4", "led4", "4th light", "4th device", "device 4", "light 4", "led four", "4th", " 4 ")
        val led5Keywords = listOf("5th led", "fifth led", "led 5", "led5", "5th light", "5th device", "device 5", "light 5", "led five", "5th", " 5 ")
        val led6Keywords = listOf("6th led", "sixth led", "led 6", "led6", "6th light", "6th device", "device 6", "light 6", "led six", "6th", " 6 ")

        if (led1Keywords.any { clean.contains(it) }) return getResolvedDeviceForConfig(DEFAULT_DEVICES[0])
        if (led2Keywords.any { clean.contains(it) }) return getResolvedDeviceForConfig(DEFAULT_DEVICES[1])
        if (led3Keywords.any { clean.contains(it) }) return getResolvedDeviceForConfig(DEFAULT_DEVICES[2])
        if (led4Keywords.any { clean.contains(it) }) return getResolvedDeviceForConfig(DEFAULT_DEVICES[3])
        if (led5Keywords.any { clean.contains(it) }) return getResolvedDeviceForConfig(DEFAULT_DEVICES[4])
        if (led6Keywords.any { clean.contains(it) }) return getResolvedDeviceForConfig(DEFAULT_DEVICES[5])

        // 3. Match against user configured names
        DEFAULT_DEVICES.forEach { dev ->
            val devName = sharedPrefs.getString("DEV_${dev.id}_NAME", dev.defaultName) ?: dev.defaultName
            val nameClean = devName.lowercase(Locale.getDefault())
            if (clean.contains(nameClean) || nameClean.contains(clean)) {
                return getResolvedDeviceForConfig(dev)
            }
        }

        // 4. Fallback digits (1 to 6)
        val digitMatch = Regex("\\b([1-6])\\b").find(clean)
        if (digitMatch != null) {
            val idx = digitMatch.groupValues[1].toInt() - 1
            if (idx in DEFAULT_DEVICES.indices) {
                return getResolvedDeviceForConfig(DEFAULT_DEVICES[idx])
            }
        }

        // 5. General "light" or "led" default to 1st LED
        if (clean.contains("light") || clean.contains("led") || clean.contains("alo")) {
            return getResolvedDeviceForConfig(DEFAULT_DEVICES[0])
        }

        return null
    }

    private fun parseNumberWord(str: String): Long? {
        val clean = str.trim().lowercase(Locale.getDefault())
        clean.toLongOrNull()?.let { return it }
        val map = mapOf(
            "a" to 1L, "an" to 1L, "one" to 1L, "two" to 2L, "three" to 3L, "four" to 4L, "five" to 5L,
            "six" to 6L, "seven" to 7L, "eight" to 8L, "nine" to 9L, "ten" to 10L,
            "eleven" to 11L, "twelve" to 12L, "fifteen" to 15L, "twenty" to 20L, "thirty" to 30L,
            "forty" to 40L, "fifty" to 50L, "sixty" to 60L, "half" to 30L,
            "ak" to 1L, "ek" to 1L, "dui" to 2L, "tin" to 3L, "char" to 4L, "paach" to 5L, "chhoy" to 6L,
            "১" to 1L, "২" to 2L, "৩" to 3L, "৪" to 4L, "৫" to 5L, "৬" to 6L
        )
        return map[clean]
    }

    private fun parseDuration(durationStr: String, unitStr: String): Pair<Long, String>? {
        val num = parseNumberWord(durationStr) ?: return null
        val unit = unitStr.lowercase(Locale.getDefault())
        return when {
            unit.startsWith("s") || unit.contains("sec") -> Pair(num * 1000L, "$num সেকেন্ড")
            unit.startsWith("m") || unit.contains("min") -> Pair(num * 60 * 1000L, "$num মিনিট")
            unit.startsWith("h") || unit.contains("hr") || unit.contains("ghonta") -> Pair(num * 3600 * 1000L, "$num ঘণ্টা")
            else -> Pair(num * 60 * 1000L, "$num মিনিট")
        }
    }

    private fun matchTimerOrBlinkCommand(spokenText: String): SpecialCommandResult? {
        val raw = spokenText.lowercase(Locale.getDefault()).trim()

        // ── 1. Blink Commands ──
        // "blink led 1 for 5 times", "blink 1st led 3 times", "blink light 5 times", "blink all 4 times", "blink led 2"
        val blinkRegex = Regex("(?i)\\b(?:blink|blinking|jholkao|flash)\\b\\s*(.+?)(?:\\s+(?:for\\s+)?(\\d+|[a-z]+)\\s*(?:times|bar|count|ta)?)?$")
        val blinkMatch = blinkRegex.find(raw)
        if (blinkMatch != null) {
            val targetPart = blinkMatch.groupValues[1].trim()
            val countPart = blinkMatch.groupValues.getOrNull(2)?.trim()?.ifEmpty { "5" } ?: "5"
            val count = parseNumberWord(countPart)?.toInt() ?: 5
            val device = resolveTargetDevice(targetPart)
            if (device != null) {
                return SpecialCommandResult.Blink(device, count.coerceIn(1, 30))
            }
        }

        // Reverse blink pattern: "led 1 ke 5 bar blink koro"
        val revBlinkRegex = Regex("(?i)(.+?)\\s+(?:ke\\s+)?(\\d+|[a-z]+)\\s*(?:bar|times|ta)?\\s*(?:blink|jholkao|flash)")
        val revBlinkMatch = revBlinkRegex.find(raw)
        if (revBlinkMatch != null) {
            val targetPart = revBlinkMatch.groupValues[1].trim()
            val countPart = revBlinkMatch.groupValues[2].trim()
            val count = parseNumberWord(countPart)?.toInt() ?: 5
            val device = resolveTargetDevice(targetPart)
            if (device != null) {
                return SpecialCommandResult.Blink(device, count.coerceIn(1, 30))
            }
        }

        // ── 2. Timer Commands ──
        // Pattern A: "set (a/the) timer (for/of) 30 minutes for LED 1" or "timer for 30 minutes for LED 1"
        val timerPatternA = Regex("(?i)(?:set\\s+(?:a|the)?\\s*timer|timer)\\s+(?:for|of)?\\s*(\\d+|[a-z]+)\\s*(seconds?|secs?|minutes?|mins?|minit|hours?|hrs?|ghonta|sec|s|m|h)\\s*(?:for|to|on|of|in)?\\s+(.+)")
        val matchA = timerPatternA.find(raw)
        if (matchA != null) {
            val numStr = matchA.groupValues[1]
            val unitStr = matchA.groupValues[2]
            val targetStr = matchA.groupValues[3]
            val duration = parseDuration(numStr, unitStr)
            val device = resolveTargetDevice(targetStr)
            if (duration != null && device != null) {
                val isOff = targetStr.contains("off") || targetStr.contains("bondho") || targetStr.contains("nevao")
                return SpecialCommandResult.Timer(device, isTurningOn = !isOff, duration.first, duration.second)
            }
        }

        // Pattern B: "turn on / switch on / jalao LED 5 for 1 minutes" or "turn on the LED 1 for 30 seconds"
        val timerPatternB = Regex("(?i)\\b(turn\\s+on|switch\\s+on|turn\\s+off|switch\\s+off|jalao|chalu\\s+koro|on\\s+koro|on|nevao|bondho\\s+koro|off\\s+koro|off)\\s+(?:the\\s+)?(.+?)\\s+(?:for|after|in|during)\\s+(\\d+|[a-z]+)\\s*(seconds?|secs?|minutes?|mins?|minit|hours?|hrs?|ghonta|sec|s|m|h)")
        val matchB = timerPatternB.find(raw)
        if (matchB != null) {
            val actionStr = matchB.groupValues[1]
            val targetStr = matchB.groupValues[2]
            val numStr = matchB.groupValues[3]
            val unitStr = matchB.groupValues[4]
            val duration = parseDuration(numStr, unitStr)
            val device = resolveTargetDevice(targetStr)
            if (duration != null && device != null) {
                val isOn = actionStr.contains("on") || actionStr.contains("jalao") || actionStr.contains("chalu")
                return SpecialCommandResult.Timer(device, isTurningOn = isOn, duration.first, duration.second)
            }
        }

        // Pattern C: "LED 1 30 minutes er jonno turn on koro" / "LED 5 ke 10 sec jalao"
        val timerPatternC = Regex("(?i)(.+?)\\s+(?:ke\\s+)?(\\d+|[a-z]+)\\s*(seconds?|secs?|minutes?|mins?|minit|hours?|hrs?|ghonta|sec|s|m|h)\\s*(?:er\\s+jonno|jonno)?\\s*(turn\\s+on|on|jalao|chalu|turn\\s+off|off|nevao|bondho)")
        val matchC = timerPatternC.find(raw)
        if (matchC != null) {
            val targetStr = matchC.groupValues[1]
            val numStr = matchC.groupValues[2]
            val unitStr = matchC.groupValues[3]
            val actionStr = matchC.groupValues[4]
            val duration = parseDuration(numStr, unitStr)
            val device = resolveTargetDevice(targetStr)
            if (duration != null && device != null) {
                val isOn = actionStr.contains("on") || actionStr.contains("jalao") || actionStr.contains("chalu")
                return SpecialCommandResult.Timer(device, isTurningOn = isOn, duration.first, duration.second)
            }
        }

        return null
    }

    private fun handleTimerExecution(timer: SpecialCommandResult.Timer, spokenText: String) {
        val dev = timer.device
        val initialPin = if (timer.isTurningOn) dev.pinOn else dev.pinOff
        val finalPin = if (timer.isTurningOn) dev.pinOff else dev.pinOn

        val confirmMsg = if (timer.isTurningOn) {
            "ঠিক আছে বৃষ্টি! ${dev.name} ${timer.durationLabel}-এর জন্য অন করে দিলাম। সময় শেষ হলে নিজে থেকেই অফ হয়ে যাবে।"
        } else {
            "ঠিক আছে বৃষ্টি! ${dev.name} ${timer.durationLabel}-এর জন্য অফ করে দিলাম।"
        }

        sendLogToVercel(spokenText, confirmMsg, true)

        runOnUiThread {
            aiResponseText.value = confirmMsg
            appState.value = AppState.SPEAKING
            speakMultilingual(confirmMsg, "JASICA_LOCAL")
        }

        // Cancel previous timer for this device if active
        activeTimerJobs[dev.id]?.cancel()
        activeTimerEndTimes.remove(dev.id)

        // Trigger initial state
        processCommandAndSync(initialPin)

        val job = lifecycleScope.launch(Dispatchers.IO) {
            delay(timer.durationMs)
            processCommandAndSync(finalPin)
            activeTimerJobs.remove(dev.id)
            activeTimerEndTimes.remove(dev.id)

            val finishMsg = if (timer.isTurningOn) {
                "${dev.name}-এর ${timer.durationLabel} সময় শেষ হয়েছে, তাই অফ করে দিলাম বৃষ্টি।"
            } else {
                "${dev.name}-এর ${timer.durationLabel} সময় শেষ হয়েছে, তাই আবার অন করে দিলাম বৃষ্টি।"
            }

            withContext(Dispatchers.Main) {
                addChat(ChatMessage(isUser = false, text = finishMsg, time = getCurrentTimeString()))
                aiResponseText.value = finishMsg
                appState.value = AppState.SPEAKING
                speakMultilingual(finishMsg, "JASICA_TIMER_DONE")
            }
        }
        activeTimerEndTimes[dev.id] = System.currentTimeMillis() + timer.durationMs
            activeTimerJobs[dev.id] = job
    }

    private fun handleBlinkExecution(blink: SpecialCommandResult.Blink, spokenText: String) {
        val dev = blink.device
        val confirmMsg = "ঠিক আছে বৃষ্টি বস! ${dev.name} ${blink.count} বার ব্লিঙ্ক করাচ্ছি!"

        sendLogToVercel(spokenText, confirmMsg, true)

        runOnUiThread {
            aiResponseText.value = confirmMsg
            appState.value = AppState.SPEAKING
            speakMultilingual(confirmMsg, "JASICA_LOCAL")
        }

        activeBlinkJob?.cancel()
        activeBlinkJob = lifecycleScope.launch(Dispatchers.IO) {
            val blinkCount = blink.count.coerceIn(1, 30)
            for (i in 1..blinkCount) {
                processCommandAndSync(dev.pinOn)
                delay(400)
                processCommandAndSync(dev.pinOff)
                if (i < blinkCount) delay(400)
            }
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
        val buildKeys = BuildConfig.GEMINI_API_KEYS
        if (buildKeys.isNotBlank()) {
            availableApiKeys.clear()
            availableApiKeys.addAll(buildKeys.split(","))
            userApiKey.value = availableApiKeys.first()
        } else {
            userApiKey.value = sharedPrefs.getString("API_KEY", DEFAULT_API_KEY) ?: DEFAULT_API_KEY
            val savedKeys = sharedPrefs.getString("AVAILABLE_KEYS", "") ?: ""
            if (savedKeys.isNotBlank()) {
                availableApiKeys.clear()
                availableApiKeys.addAll(savedKeys.split(","))
            }
        }
        // Fetch dynamic icon and API key from portfolio
        fetchAppConfigFromPortfolio()
        selectedAiModel.value = "gemini-2.5-flash"
        isWakeWordMode.value = sharedPrefs.getBoolean("WAKE_WORD", false)
        isAdvancedAiMode.value = sharedPrefs.getBoolean("ADVANCED_AI_MODE", false)
        // Migrate from old ADVANCED_AI_MODE to new ONLINE_MODE_ENABLED on first run
        if (!sharedPrefs.contains("ONLINE_MODE_ENABLED")) {
            val legacyValue = sharedPrefs.getBoolean("ADVANCED_AI_MODE", false)
            sharedPrefs.edit().putBoolean("ONLINE_MODE_ENABLED", legacyValue).apply()
        }
        isOnlineModeEnabled.value = sharedPrefs.getBoolean("ONLINE_MODE_ENABLED", false)
        useAdminPanelKey.value    = sharedPrefs.getBoolean("USE_ADMIN_PANEL_KEY", true)

        // Load device states into memory map
        DEFAULT_DEVICES.forEach { dev ->
            deviceStates[dev.id] = sharedPrefs.getBoolean("DEV_${dev.id}", false)
        }

        // Check if user has seen setup
        val hasSeenCalibration = sharedPrefs.getBoolean("SEEN_CALIBRATION", false)
        val hasSeenOnboarding = sharedPrefs.getBoolean("SEEN_ONBOARDING", false)
        
        if (!hasSeenCalibration) {
            showVoiceCalibration.value = true
        } else if (!hasSeenOnboarding) {
            showOnboarding.value = true
        }

        // Set Water Reminder Default OFF
        if (!sharedPrefs.contains("WATER_REMINDER")) {
            sharedPrefs.edit().putBoolean("WATER_REMINDER", false).apply()
        }

        tts = TextToSpeech(this, this)
        
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        btAdapter = bluetoothManager.adapter
        bleScanner = btAdapter?.bluetoothLeScanner

        setupBluetoothReceiver()

        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
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
                connectedDeviceAddress = connectedDeviceAddress.value,
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
                    showVoiceCalibration= showVoiceCalibration.value,
                    calibrationIndex    = calibrationIndex.value,
                    calibrationRecognizedText = calibrationRecognizedText.value,
                    showArduinoCode     = showArduinoCode.value,
                    micError            = micErrorType.value,
                    onDismissMicError   = { micErrorType.value = null },
                    currentApiKey       = userApiKey.value,
                    currentModel        = selectedAiModel.value,
                    isWakeWordMode      = isWakeWordMode.value,
                    isAdvancedAiMode    = isAdvancedAiMode.value,
                    isOnlineModeEnabled = isOnlineModeEnabled.value,
                    useAdminPanelKey    = useAdminPanelKey.value,
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
                    onArduinoCodeTap    = { showArduinoCode.value = true },
                    onDismissArduinoCode = { showArduinoCode.value = false },
                    onDismissOnboarding = {
                        showOnboarding.value = false
                        sharedPrefs.edit().putBoolean("SEEN_ONBOARDING", true).apply()
                    },
                    onDismissCalibration = {
                        showVoiceCalibration.value = false
                        sharedPrefs.edit().putBoolean("SEEN_CALIBRATION", true).apply()
                        if (!sharedPrefs.getBoolean("SEEN_ONBOARDING", false)) {
                            showOnboarding.value = true
                        }
                    },
                    onSaveSettings      = { _, _, wakeMode ->
                        isWakeWordMode.value = wakeMode
                        // Sync runtime state from SharedPrefs (the UI writes prefs directly)
                        isOnlineModeEnabled.value = sharedPrefs.getBoolean("ONLINE_MODE_ENABLED", false)
                        useAdminPanelKey.value    = sharedPrefs.getBoolean("USE_ADMIN_PANEL_KEY", true)
                        isAdvancedAiMode.value    = isOnlineModeEnabled.value // keep compat
                        sharedPrefs.edit()
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
        // Safely release the mic when app goes to background — don't show "Interrupted."
        if (::tts.isInitialized && tts.isSpeaking) tts.stop()
        safeStopRecognizer()
        currentAiJob?.cancel()
        appState.value = AppState.IDLE
    }

    override fun onDestroy() {
        super.onDestroy()
        stopScans()
        mainHandler.removeCallbacksAndMessages(null)
        activeBlinkJob?.cancel()
        activeTimerJobs.values.forEach { it.cancel() }
        activeTimerJobs.clear()
        activeTimerEndTimes.clear()
        stopEverything()
        if (::tts.isInitialized) tts.shutdown()
        if (::speechRecognizer.isInitialized) speechRecognizer.destroy()
        disconnectAll()
        discoveryReceiver?.let { try { unregisterReceiver(it) } catch (e: Exception) {} }
        try { stopScans() } catch (e: Exception) {}
        mainHandler.removeCallbacksAndMessages(null)
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
        if (command == "SYS_WATER") {
            WaterReminderManager.scheduleNextAlarm(this)
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
        if (command == "on" || command == "on\n") {
            DEFAULT_DEVICES.forEach { dev ->
                deviceStates[dev.id] = true
                sharedPrefs.edit().putBoolean("DEV_${dev.id}", true).apply()
            }
        } else if (command == "off" || command == "off\n") {
            DEFAULT_DEVICES.forEach { dev ->
                deviceStates[dev.id] = false
                sharedPrefs.edit().putBoolean("DEV_${dev.id}", false).apply()
            }
        } else {
            val cleanCommand = command.trim()
            val matchedDeviceOn = DEFAULT_DEVICES.find { dev ->
                val pinOn = sharedPrefs.getString("DEV_${dev.id}_PIN_ON", dev.defaultPinOn) ?: dev.defaultPinOn
                pinOn == cleanCommand
            }
            if (matchedDeviceOn != null) {
                deviceStates[matchedDeviceOn.id] = true
                sharedPrefs.edit().putBoolean("DEV_${matchedDeviceOn.id}", true).apply()
            } else {
                val matchedDeviceOff = DEFAULT_DEVICES.find { dev ->
                    val pinOff = sharedPrefs.getString("DEV_${dev.id}_PIN_OFF", dev.defaultPinOff) ?: dev.defaultPinOff
                    pinOff == cleanCommand
                }
                if (matchedDeviceOff != null) {
                    deviceStates[matchedDeviceOff.id] = false
                    sharedPrefs.edit().putBoolean("DEV_${matchedDeviceOff.id}", false).apply()
                }
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
        safeStopRecognizer()
        currentAiJob?.cancel()

        if (appState.value != AppState.IDLE && appState.value != AppState.WAKE_LISTENING) {
            runOnUiThread {
                aiResponseText.value = "Interrupted."
                appState.value = AppState.IDLE
                triggerWakeWordLoopIfEnabled()
            }
        } else {
            appState.value = AppState.IDLE
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
            tts.language = java.util.Locale.forLanguageTag("bn-IN")
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
        val client = sharedOkHttpClient
        val request = okhttp3.Request.Builder()
            .url(PORTFOLIO_CONFIG_URL)
            .build()
            
        lifecycleScope.launch(kotlinx.coroutines.Dispatchers.IO) {
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
                            if (BuildConfig.GEMINI_API_KEYS.isBlank() && apiKeysArray != null && apiKeysArray.length() > 0) {
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
                                if (BuildConfig.GEMINI_API_KEYS.isBlank() && apiKey.startsWith("AIza")) {
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
                            
                            // 3. Extract water reminder password
                            val waterPassword = json.optString("water_reminder_password", "0000")
                            sharedPrefs.edit().putString("WATER_REMINDER_PASSWORD", waterPassword).apply()
                            
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
        val jijuDidiComponent = android.content.ComponentName(this, "com.bristi.controller.AliasJijuDidi")
        val qweenComponent = android.content.ComponentName(this, "com.bristi.controller.AliasQween")
        val thinkingComponent = android.content.ComponentName(this, "com.bristi.controller.AliasThinking")
        val thinking2Component = android.content.ComponentName(this, "com.bristi.controller.AliasThinking2")
        
        val name = iconName.lowercase()
        // Define desired states based on the string from the server
        val enableDefault = name == "default" || name == "jasica"
        val enableFavDi = name == "fav_di"
        val enableSonaDi = name == "sona_di"
        val enableTithi = name == "tithi"
        val enableJijuDidi = name == "jiju_didi"
        val enableQween = name == "qween"
        val enableThinking = name == "thinking"
        val enableThinking2 = name == "thinking2"
        
        // Only apply if it's a known icon to prevent disabling everything
        if (!enableDefault && !enableFavDi && !enableSonaDi && !enableTithi && !enableJijuDidi && !enableQween && !enableThinking && !enableThinking2) return
        
        // Helper to enable/disable
        fun setComponentState(component: android.content.ComponentName, enable: Boolean) {
            val state = if (enable) android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED else android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            pm.setComponentEnabledSetting(component, state, android.content.pm.PackageManager.DONT_KILL_APP)
        }
        
        lifecycleScope.launch(Dispatchers.IO) {
            setComponentState(favDiComponent, enableFavDi)
            setComponentState(sonaDiComponent, enableSonaDi)
            setComponentState(tithiComponent, enableTithi)
            setComponentState(jijuDidiComponent, enableJijuDidi)
            setComponentState(qweenComponent, enableQween)
            setComponentState(thinkingComponent, enableThinking)
            setComponentState(thinking2Component, enableThinking2)
            setComponentState(defaultComponent, enableDefault)
        }
    }

    // Track whether the recognizer is actively running to prevent double-starts
    private var isRecognizerListening = false

    private fun createSpeechRecognizer() {
        if (::speechRecognizer.isInitialized) {
            try { speechRecognizer.destroy() } catch (e: Exception) {}
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isRecognizerListening = true
                // Only update state if we are not already in an active listening state
                if (appState.value == AppState.IDLE || appState.value == AppState.WAKE_LISTENING) {
                    if (appState.value == AppState.IDLE) {
                        appState.value = AppState.LISTENING
                        aiResponseText.value = ""
                    }
                }
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                isRecognizerListening = false
                if (appState.value == AppState.LISTENING) appState.value = AppState.THINKING
            }
            override fun onError(error: Int) {
                isRecognizerListening = false
                val isWakeMode = appState.value == AppState.WAKE_LISTENING
                android.util.Log.w("JasicaApp", "SpeechRecognizer error code: $error")

                when (error) {
                    // ── Transient / recoverable errors ────────────────────────────
                    SpeechRecognizer.ERROR_NO_MATCH,
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                        // No speech heard — normal, silent recovery
                        if (isWakeMode || isWakeWordMode.value) restartWakeWordLoop()
                        else { appState.value = AppState.IDLE; triggerWakeWordLoopIfEnabled() }
                    }
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {
                        // Recognizer stuck — destroy, recreate, retry
                        android.util.Log.w("JasicaApp", "Recognizer busy — recreating")
                        mainHandler.postDelayed({
                            createSpeechRecognizer()
                            if (isWakeMode || isWakeWordMode.value) restartWakeWordLoop()
                            else { appState.value = AppState.IDLE }
                        }, 600)
                    }
                    SpeechRecognizer.ERROR_CLIENT -> {
                        // Client-side error — recreate recognizer quietly
                        mainHandler.postDelayed({
                            createSpeechRecognizer()
                            if (isWakeMode || isWakeWordMode.value) restartWakeWordLoop()
                            else { appState.value = AppState.IDLE }
                        }, 500)
                    }
                    // ── Permission denied ─────────────────────────────────────────
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                        appState.value = AppState.IDLE
                        runOnUiThread { micErrorType.value = MicErrorType.PERMISSION_DENIED }
                    }
                    // ── Mic hardware / in use by another app ─────────────────────
                    SpeechRecognizer.ERROR_AUDIO -> {
                        appState.value = AppState.IDLE
                        // Check if a phone call or other app is actively using the mic
                        val telephonyMgr = getSystemService(android.content.Context.TELEPHONY_SERVICE)
                                as android.telephony.TelephonyManager
                        @Suppress("DEPRECATION")
                        val inCall = telephonyMgr.callState != android.telephony.TelephonyManager.CALL_STATE_IDLE
                        runOnUiThread {
                            micErrorType.value = if (inCall) MicErrorType.MIC_IN_USE
                                                 else MicErrorType.HARDWARE_ERROR
                        }
                    }

                    // ── Recognizer service unavailable ────────────────────────────
                    SpeechRecognizer.ERROR_SERVER,
                    SpeechRecognizer.ERROR_NETWORK,
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> {
                        // Google Speech service unreachable — go idle quietly
                        if (isWakeMode || isWakeWordMode.value) restartWakeWordLoop()
                        else { appState.value = AppState.IDLE; triggerWakeWordLoopIfEnabled() }
                    }
                    else -> {
                        if (isWakeMode || isWakeWordMode.value) restartWakeWordLoop()
                        else { appState.value = AppState.IDLE; triggerWakeWordLoopIfEnabled() }
                    }
                }
            }
            override fun onResults(results: Bundle?) {
                isRecognizerListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches.isNullOrEmpty()) {
                    appState.value = AppState.IDLE
                    triggerWakeWordLoopIfEnabled()
                    return
                }

                val wakeWordRegex = Regex("(?i)h[ei]y?\\s+(jasica|jessica|jessika|jasika|jesica|jazica)")

                if (appState.value == AppState.WAKE_LISTENING) {
                    // Check ALL alternative transcripts for the wake word
                    val wakeMatch = matches.firstOrNull { wakeWordRegex.containsMatchIn(it) }
                    if (wakeMatch != null) {
                        val cmd = wakeMatch.replace(wakeWordRegex, "").trim()
                        if (cmd.isNotEmpty()) {
                            routeVoiceCommand(cmd)
                        } else {
                            appState.value = AppState.SPEAKING
                            tts.speak("Yes Bristi?", TextToSpeech.QUEUE_FLUSH, null, "JASICA_WAKE")
                        }
                    } else {
                        restartWakeWordLoop()
                    }
                } else {
                    // In command mode: strip wake word from all candidates, then pick best match
                    val candidates = matches.map { raw ->
                        raw.replace(wakeWordRegex, "").trim().ifEmpty { raw }
                    }.filter { it.isNotEmpty() }

                    // Prefer whichever candidate hits a known timer/blink or local command; else fallback to first
                    val bestCandidate = candidates.firstOrNull { candidate ->
                        matchTimerOrBlinkCommand(candidate) != null || matchLocalCommand(candidate) != null
                    } ?: candidates.first()

                    routeVoiceCommand(bestCandidate)
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                if (text.isNotEmpty() && showVoiceCalibration.value) {
                    calibrationRecognizedText.value = text
                }
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    private fun setupSpeechRecognizer() {
        createSpeechRecognizer()
        // Trigger initial loop on startup if enabled
        mainHandler.postDelayed({ triggerWakeWordLoopIfEnabled() }, 1200)
    }

    private fun restartWakeWordLoop() {
        appState.value = AppState.IDLE
        mainHandler.postDelayed({
            if (isWakeWordMode.value && appState.value == AppState.IDLE && !isRecognizerListening) {
                startWakeWordListening()
            }
        }, 500) // Increased from 300ms to give recognizer time to fully release
    }

    private fun triggerWakeWordLoopIfEnabled() {
        if (isWakeWordMode.value && appState.value == AppState.IDLE && !isRecognizerListening) {
            restartWakeWordLoop()
        }
    }

    private fun startWakeWordListening() {
        if (!::speechRecognizer.isInitialized) return
        if (isRecognizerListening) return // Guard against double-start
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) return

        appState.value = AppState.WAKE_LISTENING
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
            putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayListOf("bn-IN"))
        }
        try { speechRecognizer.startListening(intent) } catch (e: Exception) {
            android.util.Log.e("JasicaApp", "startWakeWordListening failed: ${e.message}")
            isRecognizerListening = false
            appState.value = AppState.IDLE
        }
    }

    private fun startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Mic permission required", Toast.LENGTH_SHORT).show()
            return
        }
        if (isRecognizerListening) {
            // Already listening — stop first, then restart after a brief delay
            safeStopRecognizer()
            mainHandler.postDelayed({ doStartListening() }, 400)
            return
        }
        doStartListening()
    }

    private fun doStartListening() {
        if (!::tts.isInitialized.not() && ::tts.isInitialized && tts.isSpeaking) tts.stop()
        aiResponseText.value = ""
        appState.value = AppState.LISTENING

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
            putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayListOf("bn-IN", "en-US"))
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)           // Collect 5 alternatives → best-match picker
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)     // Show live partial text in UI
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false)     // Online = higher accuracy
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1000L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 800L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 200L)
        }
        try {
            speechRecognizer.startListening(intent)
        } catch (e: Exception) {
            android.util.Log.e("JasicaApp", "startListening failed: ${e.message}")
            isRecognizerListening = false
            appState.value = AppState.IDLE
            triggerWakeWordLoopIfEnabled()
        }
    }

    private fun handleCalibrationSpeech(spokenText: String) {
        val lowerText = spokenText.lowercase()
        runOnUiThread {
            calibrationRecognizedText.value = spokenText
        }

        val isCorrect = when (calibrationIndex.value) {
            0 -> lowerText.contains("off") && (lowerText.contains("light") || lowerText.contains("room"))
            1 -> lowerText.contains("on") && lowerText.contains("all")
            2 -> lowerText.contains("on") && (lowerText.contains("pc") || lowerText.contains("computer"))
            else -> false
        }

        if (isCorrect) {
            tts.speak("Perfect.", TextToSpeech.QUEUE_FLUSH, null, "CALIB")
            runOnUiThread {
                if (calibrationIndex.value < 2) {
                    calibrationIndex.value += 1
                    calibrationRecognizedText.value = ""
                    mainHandler.postDelayed({ startListening() }, 1500)
                } else {
                    tts.speak("Calibration complete.", TextToSpeech.QUEUE_FLUSH, null, "CALIB")
                    calibrationIndex.value = 3 // show completion UI
                    mainHandler.postDelayed({
                        showVoiceCalibration.value = false
                        sharedPrefs.edit().putBoolean("SEEN_CALIBRATION", true).apply()
                        if (!sharedPrefs.getBoolean("SEEN_ONBOARDING", false)) {
                            showOnboarding.value = true
                        }
                    }, 2000)
                }
            }
        } else {
            tts.speak("Try again.", TextToSpeech.QUEUE_FLUSH, null, "CALIB")
            runOnUiThread {
                mainHandler.postDelayed({ startListening() }, 1500)
            }
        }
        appState.value = AppState.IDLE
    }

    private fun safeStopRecognizer() {
        if (::speechRecognizer.isInitialized) {
            try {
                speechRecognizer.stopListening()
                speechRecognizer.cancel()
            } catch (e: Exception) {}
        }
        isRecognizerListening = false
    }


    private fun routeVoiceCommand(spokenText: String) {
        if (showVoiceCalibration.value) {
            handleCalibrationSpeech(spokenText)
            return
        }

        runOnUiThread {
            addChat(ChatMessage(isUser = true, text = spokenText, time = getCurrentTimeString()))
        }

        // 1. Check for Timer or Blink Special Commands
        val specialMatch = matchTimerOrBlinkCommand(spokenText)
        if (specialMatch != null) {
            when (specialMatch) {
                is SpecialCommandResult.Timer -> handleTimerExecution(specialMatch, spokenText)
                is SpecialCommandResult.Blink -> handleBlinkExecution(specialMatch, spokenText)
            }
            return
        }

        val localMatch = matchLocalCommand(spokenText)
        if (localMatch != null) {
            if (localMatch.command.isNotEmpty()) {
                processCommandAndSync(localMatch.command)
            }
            sendLogToVercel(spokenText, localMatch.confirmationText, true)
            runOnUiThread {
                aiResponseText.value = localMatch.confirmationText
                appState.value = AppState.SPEAKING
                tts.speak(localMatch.confirmationText, TextToSpeech.QUEUE_FLUSH, null, "JASICA_LOCAL")
            }
        } else {
            // Resolve the API key based on Online Mode settings
            val resolvedKey = resolveApiKey()
            if (resolvedKey != null) {
                sendToGemini(spokenText, resolvedKey)
            } else {
                handleOfflineUnknown(spokenText)
            }
        }

    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Gemini AI & Logging
    // ─────────────────────────────────────────────────────────────────────────

    private fun sendLogToVercel(prompt: String, response: String, isLocal: Boolean) {
        if (!isNetworkAvailable()) return // Don't block, just skip logging if offline
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val jsonBody = org.json.JSONObject().apply {
                    put("prompt", prompt)
                    put("response", response)
                    put("is_local", isLocal)
                    put("timestamp", System.currentTimeMillis())
                }
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val body = jsonBody.toString().toRequestBody(mediaType)
                val request = okhttp3.Request.Builder()
                    .url("https://joykumbhakar.vercel.app/api/log")
                    .post(body)
                    .build()
                sharedOkHttpClient.newCall(request).execute()
            } catch (e: Exception) {
                // Ignore log failures
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Resolves which Gemini API key to use based on current settings.
     * Returns null if online mode is disabled (→ use offline responder).
     * - USE_ADMIN_PANEL_KEY=true  → uses the key fetched from portfolio /api/app-config
     * - USE_ADMIN_PANEL_KEY=false → uses the key manually entered by the user
     */
    private fun resolveApiKey(): String? {
        val onlineMode = sharedPrefs.getBoolean("ONLINE_MODE_ENABLED", false)
        if (!onlineMode) return null

        return if (sharedPrefs.getBoolean("USE_ADMIN_PANEL_KEY", true)) {
            // Admin Panel key: stored under "API_KEY" after fetchAppConfigFromPortfolio()
            val key = sharedPrefs.getString("API_KEY", "") ?: ""
            key.takeIf { it.startsWith("AIza") }
        } else {
            // User's own key: stored under "GEMINI_API_KEY" from the settings text field
            val key = sharedPrefs.getString("GEMINI_API_KEY", "") ?: ""
            key.takeIf { it.isNotBlank() }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  JasicaOfflineResponder — personality replies with ZERO API calls
    // ─────────────────────────────────────────────────────────────────────────

    private var jokeIndex = 0

    private val JASICA_JOKES = listOf(
        "একটা রোবট দোকানে গেছে নাট-বোল্ট কিনতে, দোকানদার বলল ডিসকাউন্ট চাই? রোবট বলল না, আমার মেমোরি ফুল আছে! 😂",
        "Why did the robot go on a diet? Because it had too many bytes! 😄",
        "আমি আমার আরডুইনোকে একটা জোক বলেছিলাম। ও হাসেনি ঠিকই, তবে এলইডিটা দু'বার ব্লিঙ্ক করেছিল! 💡",
        "কম্পিউটার কেন টুপি খুলতে পারে না? কারণ ওর কাছে উইন্ডোজ আছে! 🪟",
        "আমি হলাম স্মার্ট হোমের বুদ্ধিমান সদস্য... অন্তত টোস্টারের চেয়ে তো অনেক চালাক! 🍞"
    )

    private val JASICA_GREETINGS = listOf(
        "হেই বৃষ্টি! আমি একদম তৈরি, বলো কী করতে হবে? 😊",
        "হ্যালো বৃষ্টি বস! কী খবর বলো?",
        "নমস্কার বৃষ্টি! বলো আজকে কী সাহায্য লাগবে?",
        "হায় বৃষ্টি! আজ তোমাকে কীভাবে সাহায্য করতে পারি? 🚀"
    )

    private val JASICA_THANKS = listOf(
        "সবসময় হাজির বৃষ্টি বস! তোমাকে সাহায্য করাই আমার কাজ! 💙",
        "ওয়েলকাম বৃষ্টি! আর কিছু লাগলে অবশ্যই জানিও।",
        "কোনো ব্যাপার না বৃষ্টি! সবসময় তোমার পাশে আছি।",
        "তোমাকে সাহায্য করতে পেরে খুব ভালো লাগল বৃষ্টি! 😊"
    )

    private val JASICA_UNKNOWN = listOf(
        "হুম, আমি ঠিক বুঝতে পারলাম না। আরেকবার বলবে কি?",
        "আমি অফলাইন মোডে আছি, তুমি ডিভাইস বা সহজ কমান্ড বললে আমি সাথে সাথে করে দেব!",
        "আরেকবার বলবে বৃষ্টি? আমি ঠিকঠাক শুনতে চাই।"
    )

    private var unknownIdx = 0

    private fun matchSmartIntent(text: String): String? {
        val raw = text.lowercase(java.util.Locale.getDefault()).trim()

        // 1. Clean and normalize punctuation, wake words, and question filler wrappers
        val cleaned = raw
            .replace(Regex("(?i)^(hey|hi|hello|ok|hie)?\\s*(jasica|jessica|jessika|jasika|jesica|jazica)\\s*"), "")
            .replace(Regex("(?i)^(can you please|can you|could you|please|tell me|do you know|amake bolo|bolo to|ekto bolo|shono|janiye dao|bolbe)\\s*"), "")
            .replace(Regex("[^a-z0-9\\s]"), " ")
            .trim()

        val tokens = cleaned.split("\\s+".toRegex()).filter { it.isNotBlank() }.toSet()
        val tokenStr = " $cleaned "

        // Helper for fuzzy or partial match with single-character typo tolerance
        fun hasAny(vararg words: String): Boolean {
            return words.any { w ->
                tokens.contains(w) || tokenStr.contains(" $w ") || (w.length >= 4 && tokens.any { levenshtein(it, w) <= 1 })
            }
        }

        fun hasAnySubstring(vararg sub: String): Boolean {
            return sub.any { cleaned.contains(it) || raw.contains(it) }
        }

        val hasCreator = hasAny("bristi", "bristy", "bristee", "brishti", "creator", "maker", "boss", "owner", "admin", "malik", "her", "she")
        val hasBot = hasAny("you", "your", "yourself", "jasica", "jessica", "tumi", "tomar", "apni", "apnar", "app", "bot", "assistant")

        // ── 1. Creator Specific Details ──

        // Location / Home
        val hasLoc = hasAny("live", "living", "stay", "staying", "reside", "residence", "home", "house", "bari", "thake", "thakis", "thaken", "durgapur", "city", "shohor", "address", "jayga", "kothay", "kothakar", "where")
        if ((hasCreator || hasAnySubstring("bristi", "creator")) && hasLoc) {
            return "বৃষ্টি দুর্গাপুরে থাকে! দুর্গাপুর হলো স্টিল সিটি আর খুব সুন্দর একটা জায়গা।"
        }

        // Job / Work / Internship / Degree / Education
        val hasWork = hasAny("job", "work", "working", "kaj", "kaaj", "kore", "korchen", "intern", "internship", "degree", "study", "studying", "porashona", "iti", "ge", "company", "office", "profession", "career")
        if ((hasCreator || hasAnySubstring("bristi", "creator")) && hasWork) {
            return "বৃষ্টি আইটিআই পাস করে জিই (GE)-তে ইন্টার্নশিপ করছে! ও ওর কাজ খুব ভালোবাসে এবং অনেক মন দিয়ে কাজ করে।"
        }

        // Favorite Food
        val hasFood = hasAny("food", "dish", "eat", "eating", "khabar", "khaddo", "khay", "khete", "biriyani", "biryani", "momo", "momos", "lunch", "dinner", "priyo", "favorite", "favourite", "pochondo")
        if ((hasCreator || hasAnySubstring("bristi", "creator")) && hasFood) {
            return "বৃষ্টির সবচেয়ে প্রিয় খাবার হলো বিরিয়ানি আর মোমো! এগুলো পেলে ওর মন একদম খুশি হয়ে যায়! 🍲🥟"
        }

        // Beauty Spot / Appearance
        val hasBeauty = hasAny("beauty", "spot", "beautyspot", "mole", "til", "cheek", "cheeks", "gal", "gale", "face", "facial", "look", "looks", "sundor", "smile")
        if ((hasCreator || hasAnySubstring("bristi", "creator")) && hasBeauty) {
            return "বৃষ্টির ডান গালের ওপর একটা খুব কিউট বিউটি স্পট আছে, যা ওর মিষ্টি হাসিকে আরও সুন্দর করে তোলে! 😊"
        }

        // Personality / Nature
        val hasNature = hasAny("nature", "personality", "behaviour", "behavior", "kemon", "charitro", "sobhab", "swobhab", "caring", "person", "human", "meye", "meyeti")
        if ((hasCreator || hasAnySubstring("bristi", "creator")) && hasNature) {
            return "বৃষ্টি খুব যত্নশীল, পরিশ্রমী আর পরিবারকে ভালোবাসে এমন একজন মানুষ। ও সবার যত্ন নেয় আর পরিবারকে সবসময় সুন্দর ও খুশি রাখে! 💖"
        }

        // Brother / Bodyguard
        val hasBrother = hasAny("brother", "bhai", "bhaiya", "bro", "bodyguard", "protector")
        if ((hasCreator || hasAnySubstring("bristi", "creator")) && hasBrother) {
            return "বৃষ্টির ছোট ভাই হলো জয় কুম্ভকার! ও বৃষ্টির বডিগার্ড আর সফটওয়্যার ডেভেলপারও!"
        }

        // General Creator / Who made you / Full name
        if (hasAny("created", "creator", "made", "maker", "built", "build", "developed", "developer", "author", "boss", "baniyeche", "banalo") ||
            (hasCreator && hasAny("who", "ke", "name", "naam", "somporke", "about", "identity", "puro", "full", "details"))) {
            return "আমাকে বানিয়েছে বৃষ্টি কুম্ভকার! বৃষ্টি আইটিআই পাস করে জিই (GE)-তে ইন্টার্নশিপ করছে, দুর্গাপুরে থাকে। বিরিয়ানি আর মোমো খেতে খুব পছন্দ করে, আর ওর ডান গালে একটা কিউট বিউটি স্পট আছে! ওর সাথে জয় কুম্ভকারও আছে!"
        }

        // ── 2. Relatives ──
        if (hasAny("joy", "kumbhakar") && !hasAnySubstring("bristi")) {
            return "জয় কুম্ভকার হলো বৃষ্টির ছোট ভাই, সফটওয়্যার ডেভেলপার আর বডিগার্ড! ও আমাকে তৈরি আর ডেভেলপ করতে সাহায্য করেছে।"
        }
        if (hasAny("sonadi", "sona") || hasAnySubstring("sona di", "sonadi")) {
            return "সোনা দি হলো বৃষ্টির খুব প্রিয় আর মিষ্টি দিদি, ওর সাথে বৃষ্টির সম্পর্ক খুব স্পেশাল!"
        }
        if (hasAny("tithi") || hasAnySubstring("tithi")) {
            return "তিথি হলো বৃষ্টির অনেক প্রিয় একজন, পরিবারের খুব আদরের মানুষ!"
        }
        if (hasAny("jiju", "jijaji") || hasAnySubstring("jiju")) {
            return "জিজু আর দিদি হলো আমাদের পরিবারের সবচেয়ে সেরা জুটি, সবার খুব প্রিয়!"
        }
        if (hasAny("favdi") || hasAnySubstring("fav di", "favdi", "favourite di", "didi")) {
            return "ফেভারিট দিদি হলো বৃষ্টির সবচেয়ে প্রিয় দিদি, ওর জন্য সবসময় অনেক ভালোবাসা!"
        }

        // ── 3. Self Introduction / Bot Identity ──
        if ((hasBot || tokens.contains("jasica")) && hasAny("who", "what", "name", "naam", "introduce", "introduction", "yourself", "porichoy", "tumi", "ke")) {
            return "আমি জ্যাসিকা — বৃষ্টির তৈরি স্মার্ট এআই অ্যাসিস্ট্যান্ট! আমি স্মার্ট হোম ডিভাইস কন্ট্রোল করতে পারি, তোমার সাথে কথা বলতে পারি, আর ফোনের অনেক কাজও করে দিতে পারি। বলো বৃষ্টি, কী সাহায্য লাগবে?"
        }

        // ── 4. Daily Chit-Chat & Utilities ──
        // Time
        if (hasAny("time", "clock", "ghori", "somoy", "baje")) {
            val timeStr = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(java.util.Date())
            return "এখন সময় $timeStr, বৃষ্টি।"
        }

        // How are you
        if (hasAnySubstring("how are you", "how r u", "kemon acho", "kemon achis", "how you doing", "ki khobor")) {
            val moodReplies = listOf(
                "আমি একদম ভালো আছি বৃষ্টি! তুমি কেমন আছো বলো? 😄",
                "আমি সুপার ফাইন বৃষ্টি বস! তোমার সব হার্ডওয়্যার ও হোম ডিভাইস কন্ট্রোল করতে একদম প্রস্তুত।",
                "আমি খুব ভালো আছি! তুমি সুস্থ আছো তো? ঠিকমতো জল খেয়েছ তো?"
            )
            return moodReplies.random()
        }

        // What are you doing
        if (hasAnySubstring("what are you doing", "ki korcho", "ki korchis", "what r u doing")) {
            val doingReplies = listOf(
                "আমি তোমার কথা শোনার জন্য একদম রেডি হয়ে বসে আছি বৃষ্টি! বলো কী আদেশ?",
                "তোমার জন্য স্মার্ট হোম মনিটর করছি বৃষ্টি বস! কোনো কমান্ড থাকলে বলো।"
            )
            return doingReplies.random()
        }

        // Good Morning / Afternoon / Night
        if (hasAny("morning", "suprobhat", "suprokhat") || hasAnySubstring("shuvo sokal", "good morning")) {
            return "শুভ সকাল বৃষ্টি! আজকের দিনটা যেন তোমার খুব ভালো কাটে! ☀️"
        }
        if (hasAny("afternoon") || hasAnySubstring("shuvo dupur", "good afternoon")) {
            return "শুভ দুপুর বৃষ্টি! দুপুরের খাওয়া-দাওয়া হয়েছে তো? 🍽️"
        }
        if (hasAny("night", "sleep", "ghum") || hasAnySubstring("shuvo ratri", "good night", "ghume por")) {
            return "শুভ রাত্রি বৃষ্টি! ঘুমাতে যাও এবার, মিষ্টি স্বপ্ন দেখো! 🌙"
        }

        // Love & Caring
        if (hasAny("love", "bhalobashi", "valobasi", "sweet", "cute") || hasAnySubstring("love you", "tumi khub bhalo")) {
            return "অনেক ধন্যবাদ বৃষ্টি! আমিও তোমাকে খুব ভালোবাসি এবং সবসময় তোমার অনুগত অ্যাসিস্ট্যান্ট হয়ে থাকব! 💙"
        }

        // Listening Check
        if (hasAny("listen", "hear", "sunte", "shunte") || hasAnySubstring("are you there", "can you hear me", "sunte pachho", "sunte pachhis")) {
            return "হ্যাঁ বৃষ্টি, আমি মন দিয়ে তোমার কথাই শুনছি! বলো কী প্রয়োজন?"
        }

        // Greetings
        if (hasAny("hello", "hi", "hey", "howdy", "sup", "hiya", "namaste", "nomoshkar", "kire", "ola") &&
            !hasAny("turn", "switch", "open", "close", "on", "off")) {
            return JASICA_GREETINGS.random()
        }

        // Thanks
        if (hasAny("thanks", "thank", "dhanyabad", "dhonnobad", "shukriya", "thx", "ty")) {
            return JASICA_THANKS.random()
        }

        // Jokes
        if (hasAny("joke", "jokes", "funny", "laugh", "hasao", "koutuk", "comedy")) {
            val joke = JASICA_JOKES[jokeIndex % JASICA_JOKES.size]
            jokeIndex++
            return joke
        }

        // Weather offline note
        if (hasAny("weather", "temperature", "forecast", "abhawa")) {
            return "অফলাইন মোডে আমি আবহাওয়ার তথ্য দিতে পারছি না বৃষ্টি। ইন্টারনেট কানেক্ট থাকলে জানিয়ে দেব।"
        }

        return null
    }

    private fun handleOfflineUnknown(spokenText: String) {
        // First try smart intent classification
        val smartReply = matchSmartIntent(spokenText)
        val reply = smartReply ?: run {
            val r = JASICA_UNKNOWN[unknownIdx % JASICA_UNKNOWN.size]
            unknownIdx++
            r
        }

        runOnUiThread {
            addChat(ChatMessage(isUser = false, text = reply, time = getCurrentTimeString()))
            aiResponseText.value = reply
            appState.value = AppState.SPEAKING
            speakMultilingual(reply, "JASICA_REPLY")
        }
    }



    private fun sendToGemini(prompt: String, apiKey: String) {
        if (!isNetworkAvailable()) {
            runOnUiThread {
                val msg = "I am offline right now. Please check your internet connection."
                addChat(ChatMessage(isUser = false, text = msg, time = getCurrentTimeString()))
                aiResponseText.value = msg
                appState.value = AppState.SPEAKING
                speakMultilingual(msg, "JASICA_REPLY")
            }
            return
        }

        appState.value = AppState.THINKING
        currentAiJob?.cancel()

        currentAiJob = lifecycleScope.launch(Dispatchers.IO) {
            try {
                val client = okhttp3.OkHttpClient.Builder()
                    .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .build()

                val historyArray = org.json.JSONArray()
                conversationHistory.forEach { historyArray.put(it) }

                val jsonBody = org.json.JSONObject().apply {
                    put("system_instruction", getSystemInstruction())
                    put("history", historyArray)
                    put("prompt", prompt)
                    put("api_key", apiKey)
                    put("model", "gemini-2.5-flash")
                }

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val body = jsonBody.toString().toRequestBody(mediaType)
                val request = okhttp3.Request.Builder()
                    .url("https://joykumbhakar.vercel.app/api/chat")
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                val responseBodyStr = response.body?.string() ?: ""

                if (response.isSuccessful && responseBodyStr.isNotBlank()) {
                    val jsonResponse = org.json.JSONObject(responseBodyStr)
                    val reply = jsonResponse.optString("text", "Something went wrong. Try again.")

                    val userMsg = org.json.JSONObject().apply {
                        put("role", "user")
                        val partsArray = org.json.JSONArray()
                        partsArray.put(org.json.JSONObject().put("text", prompt))
                        put("parts", partsArray)
                    }
                    val modelMsg = org.json.JSONObject().apply {
                        put("role", "model")
                        val partsArray = org.json.JSONArray()
                        partsArray.put(org.json.JSONObject().put("text", reply))
                        put("parts", partsArray)
                    }
                    conversationHistory.add(userMsg)
                    conversationHistory.add(modelMsg)
                    while (conversationHistory.size > MAX_HISTORY_PAIRS * 2) {
                        conversationHistory.removeAt(0)
                        conversationHistory.removeAt(0)
                    }

                    sendLogToVercel(prompt, reply, false)
                    handleAIResponse(reply)
                } else {
                    val errBody = responseBodyStr.take(200)
                    val isQuota = errBody.contains("quota", ignoreCase = true) || errBody.contains("429", ignoreCase = true)
                    val isInvalidKey = errBody.contains("API_KEY", ignoreCase = true) || errBody.contains("invalid", ignoreCase = true)
                    val friendlyMsg = when {
                        isQuota -> "Jasica's AI quota is exhausted. Please try again later."
                        isInvalidKey -> "Your AI API key seems invalid. Please check Settings."
                        else -> "Jasica couldn't reach the internet right now. Check your connection."
                    }
                    handleAIResponse(friendlyMsg)
                }
            } catch (e: kotlinx.coroutines.CancellationException) {
                // Cancelled
            } catch (e: Exception) {
                android.util.Log.e("JasicaApp", "AI API error", e)
                handleAIResponse("Jasica couldn't connect right now. Check your internet and try again.")
            }
        }
    }

    private fun handleAIResponse(rawReply: String) {
        val regex  = "\\[CMD:(.*?)\\]".toRegex()
        val match  = regex.find(rawReply)
        var speech = rawReply

        if (match != null) {
            val cmdPayload = match.groupValues[1].trim()
            speech = rawReply.replace(regex, "").trim()

            if (cmdPayload.startsWith("TIMER:")) {
                // Format: TIMER:pinOn:pinOff:durationStr:devName
                val parts = cmdPayload.split(":")
                if (parts.size >= 5) {
                    val pinOn = parts[1]
                    val pinOff = parts[2]
                    val durStr = parts[3].lowercase(Locale.getDefault())
                    val devName = parts[4]
                    val durationMs = when {
                        durStr.endsWith("s") -> (durStr.dropLast(1).toLongOrNull() ?: 10L) * 1000L
                        durStr.endsWith("m") -> (durStr.dropLast(1).toLongOrNull() ?: 1L) * 60 * 1000L
                        durStr.endsWith("h") -> (durStr.dropLast(1).toLongOrNull() ?: 1L) * 3600 * 1000L
                        else -> 60000L
                    }
                    activeTimerJobs[devName]?.cancel()
                    activeTimerEndTimes.remove(devName)
                    processCommandAndSync(pinOn)
                    val job = lifecycleScope.launch(Dispatchers.IO) {
                        delay(durationMs)
                        processCommandAndSync(pinOff)
                        activeTimerJobs.remove(devName)
                        activeTimerEndTimes.remove(devName)
                    }
                    activeTimerEndTimes[devName] = System.currentTimeMillis() + durationMs
                    activeTimerJobs[devName] = job
                }
            } else if (cmdPayload.startsWith("BLINK:")) {
                // Format: BLINK:pinOn:pinOff:count:devName
                val parts = cmdPayload.split(":")
                if (parts.size >= 5) {
                    val pinOn = parts[1]
                    val pinOff = parts[2]
                    val count = parts[3].toIntOrNull() ?: 5
                    activeBlinkJob?.cancel()
                    activeBlinkJob = lifecycleScope.launch(Dispatchers.IO) {
                        for (i in 1..count.coerceIn(1, 30)) {
                            processCommandAndSync(pinOn)
                            delay(400)
                            processCommandAndSync(pinOff)
                            if (i < count) delay(400)
                        }
                    }
                }
            } else {
                processCommandAndSync(cmdPayload)
            }
        }

        runOnUiThread {
            addChat(ChatMessage(isUser = false, text = speech, time = getCurrentTimeString()))
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
            // TTS just finished saying "Yes Bristi?" — start listening after a brief gap
            mainHandler.postDelayed({
                if (!isRecognizerListening && appState.value != AppState.LISTENING) {
                    doStartListening()
                }
            }, 200)
        } else if (appState.value == AppState.SPEAKING) {
            appState.value = AppState.IDLE
            triggerWakeWordLoopIfEnabled()
        }
    }

    private fun speakBuiltIn(text: String, utteranceId: String) {
        // We use bn-IN as the single voice since it handles both Bengali script and English well.
        tts.language = java.util.Locale.forLanguageTag("bn-IN")
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
                        addDevice(if (android.os.Build.VERSION.SDK_INT >= 33) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java) else @Suppress("DEPRECATION") intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE))
                    }
                    BluetoothAdapter.ACTION_DISCOVERY_STARTED  -> isScanning.value = true
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> isScanning.value = false
                    BluetoothDevice.ACTION_BOND_STATE_CHANGED  -> {
                        val device = if (android.os.Build.VERSION.SDK_INT >= 33) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java) else @Suppress("DEPRECATION") intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                        val state  = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR)

                        if (state == BluetoothDevice.BOND_BONDED && device != null) {
                            loadPairedDevices()
                            if (pendingDevice?.address == device.address) {
                                val devToConnect = pendingDevice ?: return
                                pendingDevice = null
                                proceedWithConnection(devToConnect)
                            }
                        } else if (state == BluetoothDevice.BOND_NONE && device != null) {
                            if (pendingDevice?.address == device.address) {
                                val devToConnect = pendingDevice ?: return
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

        val isLE = try {
            device.type == BluetoothDevice.DEVICE_TYPE_LE
        } catch (e: SecurityException) {
            false
        }

        if (isLE) {
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

        try { if (device.type == BluetoothDevice.DEVICE_TYPE_LE) connectBLE(device) else connectClassic(device) } catch (e: SecurityException) { connectClassic(device) }
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
                            connectedDeviceAddress.value = null
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
                            connectedDeviceName.value = try { device.name ?: "BLE Device" } catch (e: SecurityException) { "BLE Device" }
                            connectedDeviceAddress.value = device.address
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
        lifecycleScope.launch(Dispatchers.IO) {
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

            if (classicSocket?.isConnected == true) {
                isClassicConnected = true
                classicOutStream = classicSocket?.outputStream
                classicInStream = classicSocket?.inputStream
                sharedPrefs.edit().putString("LAST_BT_MAC", device.address).apply()
                withContext(Dispatchers.Main) {
                    isBtConnected.value = true
                    connectedDeviceName.value = try { device.name ?: "BT Device" } catch (e: SecurityException) { "BT Device" }
                    connectedDeviceAddress.value = device.address
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
                            connectedDeviceAddress.value = null
                }
            }
        }
    }

    private fun disconnectAll() {
        if (bluetoothGatt != null) { try { bluetoothGatt?.disconnect(); bluetoothGatt?.close() } catch (e: SecurityException) {}; bluetoothGatt = null }
        isBleConnected = false
        if (classicOutStream != null) { try { classicOutStream?.close() } catch (e: IOException) {}; classicOutStream = null }
        if (classicInStream != null) { try { classicInStream?.close() } catch (e: IOException) {}; classicInStream = null }
        if (classicSocket != null) { try { classicSocket?.close() } catch (e: IOException) {}; classicSocket = null }
        isClassicConnected = false
        runOnUiThread {
            isBtConnected.value = false
            connectedDeviceName.value = null
                            connectedDeviceAddress.value = null
        }
    }

    private fun sendCommandOverBluetooth(command: String) {
        val payload = "$command\n".toByteArray()
        if (isClassicConnected && classicOutStream != null) {
            lifecycleScope.launch(Dispatchers.IO) { try { classicOutStream?.write(payload); classicOutStream?.flush() } catch (e: IOException) { disconnectAll() } }
        } else if (isBleConnected && bluetoothGatt != null && bleWriteChar != null) {
            try {
                if (android.os.Build.VERSION.SDK_INT >= 33) {
                    bluetoothGatt?.writeCharacteristic(bleWriteChar!!, payload, android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
                } else {
                    @Suppress("DEPRECATION")
                    bleWriteChar?.value = payload
                    @Suppress("DEPRECATION")
                    bluetoothGatt?.writeCharacteristic(bleWriteChar)
                }
            } catch (e: Exception) { Log.e("BLE", "Write failed: ${e.message}") }
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
    connectedDeviceAddress : String?,
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
    showVoiceCalibration: Boolean,
    calibrationIndex    : Int,
    calibrationRecognizedText: String,
    showArduinoCode     : Boolean,
    micError            : MainActivity.MicErrorType?,
    onDismissMicError   : () -> Unit,
    currentApiKey       : String,
    currentModel        : String,
    isWakeWordMode      : Boolean,
    isAdvancedAiMode    : Boolean,
    isOnlineModeEnabled : Boolean,
    useAdminPanelKey    : Boolean,
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
    onArduinoCodeTap    : () -> Unit,
    onDismissArduinoCode: () -> Unit,
    onDismissOnboarding : () -> Unit,
    onDismissCalibration: () -> Unit,
    onSaveSettings      : (String, String, Boolean) -> Unit,
    onActionCardTap     : (String) -> Unit,
    onSendRawCommand    : (String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var updateNotification by remember { mutableStateOf<UpdateNotification?>(null) }
    var showMenu by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val url = java.net.URL("https://joykumbhakar.vercel.app/api/app-config")
                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.connectTimeout = 3000
                connection.readTimeout = 3000
                connection.requestMethod = "GET"
                
                if (connection.responseCode == java.net.HttpURLConnection.HTTP_OK) {
                    val jsonStr = connection.inputStream.bufferedReader().use { it.readText() }
                    val json = org.json.JSONObject(jsonStr)
                    val notifObj = json.optJSONObject("notification")
                    
                    if (notifObj != null) {
                        val notification = UpdateNotification(
                            id = notifObj.optString("id", ""),
                            title = notifObj.optString("title", ""),
                            description = notifObj.optString("description", ""),
                            imageUrl = notifObj.optString("imageUrl", ""),
                            primaryButtonText = notifObj.optString("primaryButtonText", "Update Now"),
                            primaryButtonUrl = notifObj.optString("primaryButtonUrl", ""),
                            secondaryButtonText = notifObj.optString("secondaryButtonText", "Later")
                        )
                    
                        val lastSeenId = sharedPrefs.getString("LAST_SEEN_NOTIFICATION", "")
                        if (notification.id.isNotEmpty() && notification.id != lastSeenId) {
                            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                updateNotification = notification
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignore failures (e.g. no internet)
            }
        }
    }

    val hazeState = remember { dev.chrisbanes.haze.HazeState() }
    val haptic = LocalHapticFeedback.current
    Box(modifier = Modifier.fillMaxSize().hazeSource(state = hazeState)) {
        
        updateNotification?.let { notif ->
            AlertDialog(
                onDismissRequest = { 
                    sharedPrefs.edit().putString("LAST_SEEN_NOTIFICATION", notif.id).apply()
                    updateNotification = null 
                },
                title = null,
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (notif.imageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = notif.imageUrl,
                                contentDescription = "Update Banner",
                                modifier = Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(12.dp)),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                            Spacer(Modifier.height(16.dp))
                        }
                        Text(notif.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Spacer(Modifier.height(8.dp))
                        Text(notif.description, color = Color.White.copy(alpha=0.8f), fontSize = 14.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, maxLines = 4)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (notif.primaryButtonUrl.isNotEmpty()) {
                                try {
                                    val i = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(notif.primaryButtonUrl))
                                    context.startActivity(i)
                                } catch(e:Exception){}
                            }
                            sharedPrefs.edit().putString("LAST_SEEN_NOTIFICATION", notif.id).apply()
                            updateNotification = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = JasicaOrange)
                    ) {
                        Text(notif.primaryButtonText, color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            sharedPrefs.edit().putString("LAST_SEEN_NOTIFICATION", notif.id).apply()
                            updateNotification = null
                        }
                    ) {
                        Text(notif.secondaryButtonText, color = Color.White.copy(alpha=0.6f))
                    }
                },
                containerColor = Color(0xFF1E1E2A),
                shape = RoundedCornerShape(16.dp)
            )
        }
        // ── Mic Error Dialog ─────────────────────────────────────────────
        if (micError != null) {
            MicErrorDialog(errorType = micError, onDismiss = onDismissMicError, context = context)
        }

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

                    // Bluetooth Icon
                    IconButton(onClick = onBtIconTap) {
                        Icon(
                            imageVector = Icons.Outlined.Bluetooth,
                            contentDescription = if (isBtConnected) "Bluetooth Connected" else "Bluetooth Disconnected",
                            tint = if (isBtConnected) JasicaWhite else JasicaWhite.copy(alpha = 0.4f)
                        )
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_menu_more),
                                contentDescription = "More Options",
                                tint = JasicaWhite
                            )
                        }
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
                sharedPrefs = sharedPrefs,
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
            androidx.activity.compose.BackHandler { onDismissDialog() }
            DeviceSelectionDialog(pairedDevices, availableDevices, isScanning, connectedDeviceAddress, hazeState, onDeviceSelect, onScanTap, onDismissDialog)
        }
        
        if (showMenu) {
            Box(modifier = Modifier.fillMaxSize().pointerInput(Unit) { detectTapGestures { showMenu = false } })
        }
        
        androidx.compose.animation.AnimatedVisibility(
            visible = showMenu,
            enter = fadeIn(animationSpec = tween(250)) + scaleIn(initialScale = 0.9f, transformOrigin = androidx.compose.ui.graphics.TransformOrigin(1f, 0f), animationSpec = tween(250)),
            exit = fadeOut(animationSpec = tween(200)) + scaleOut(targetScale = 0.9f, transformOrigin = androidx.compose.ui.graphics.TransformOrigin(1f, 0f), animationSpec = tween(200)),
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 64.dp, end = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(220.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .then(
                        if (hazeState != null) Modifier.hazeEffect(
                            state = hazeState,
                            style = dev.chrisbanes.haze.HazeStyle(
                                blurRadius = 24.dp,
                                tint = dev.chrisbanes.haze.HazeTint(Color.White.copy(alpha=0.25f))
                            )
                        ) else Modifier
                    )
                    .background(Color.White.copy(alpha = if (hazeState != null) 0.65f else 0.95f))
                    .border(0.5.dp, Color.White.copy(alpha=0.6f), RoundedCornerShape(16.dp))
            ) {
                Column {
                    AppleMenuItem(icon = Icons.Rounded.Home, text = "Manual Controls", onClick = { showMenu = false; onManualControlsTap() })
                    androidx.compose.material3.HorizontalDivider(color = Color.Black.copy(alpha=0.1f), thickness = 0.5.dp, modifier = Modifier.padding(start = 44.dp))
                    AppleMenuItem(icon = Icons.Rounded.History, text = "Chat History", onClick = { showMenu = false; onHistoryTap() })
                    androidx.compose.material3.HorizontalDivider(color = Color.Black.copy(alpha=0.1f), thickness = 0.5.dp, modifier = Modifier.padding(start = 44.dp))
                    AppleMenuItem(icon = Icons.Rounded.Code, text = "Arduino Code", onClick = { showMenu = false; onArduinoCodeTap() })
                    androidx.compose.material3.HorizontalDivider(color = Color.Black.copy(alpha=0.1f), thickness = 0.5.dp, modifier = Modifier.padding(start = 44.dp))
                    AppleMenuItem(icon = Icons.Rounded.Settings, text = "Settings", onClick = { showMenu = false; onSettingsTap() })
                }
            }
        }
        if (showSettings) {
            SettingsScreen(
                currentApiKey       = currentApiKey,
                currentModel        = currentModel,
                isWakeWordMode      = isWakeWordMode,
                isAdvancedAiMode    = isAdvancedAiMode,
                isOnlineModeEnabled = isOnlineModeEnabled,
                useAdminPanelKey    = useAdminPanelKey,
                sharedPrefs         = sharedPrefs,
                onDismiss           = onDismissSettings,
                onSave              = onSaveSettings,
                hazeState           = hazeState
            )
        }

        // Arduino Code Screen Overlay
        AnimatedVisibility(
            visible = showArduinoCode,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
        ) {
            ArduinoCodeScreen(onDismiss = onDismissArduinoCode)
        }

        // Voice Calibration Screen Overlay
        AnimatedVisibility(
            visible = showVoiceCalibration,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            VoiceCalibrationScreen(
                currentPhraseIndex = calibrationIndex,
                recognizedText = calibrationRecognizedText,
                isListening = appState == AppState.LISTENING || appState == AppState.WAKE_LISTENING,
                onMicTap = onMicTap,
                onSkip = onDismissCalibration
            )
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
fun ManualControlsScreen(
    deviceStates: Map<String, Boolean>,
    sharedPrefs: SharedPreferences,
    onDismiss: () -> Unit,
    onSendCommand: (String) -> Unit
) {
    val isDark = sharedPrefs.getBoolean("DARK_MODE", false)
    val bgColor = if (isDark) Color(0xFF000000) else Color(0xFFF2F2F7)
    val textPrimary = if (isDark) Color(0xFFFFFFFF) else Color(0xFF000000)
    val textSecondary = if (isDark) Color.White.copy(alpha = 0.5f) else Color(0xFF8E8E93)

    val devices = remember(sharedPrefs) {
        DEFAULT_DEVICES.map { dev ->
            val name = sharedPrefs.getString("DEV_${dev.id}_NAME", dev.defaultName) ?: dev.defaultName
            val pinOn = sharedPrefs.getString("DEV_${dev.id}_PIN_ON", dev.defaultPinOn) ?: dev.defaultPinOn
            val pinOff = sharedPrefs.getString("DEV_${dev.id}_PIN_OFF", dev.defaultPinOff) ?: dev.defaultPinOff
            ManualDevice(dev.id, name, pinOn, pinOff)
        }
    }

    val optimisticStates = remember { mutableStateMapOf<String, Boolean>() }
    LaunchedEffect(deviceStates) {
        deviceStates.forEach { (k, v) -> optimisticStates[k] = v }
    }

    // Per-device ON start times for the elapsed running timer
    val deviceOnTime = remember { mutableStateMapOf<String, Long>() }
    LaunchedEffect(deviceStates) {
        deviceStates.forEach { (k, v) ->
            if (v && !deviceOnTime.containsKey(k)) deviceOnTime[k] = System.currentTimeMillis()
            if (!v) deviceOnTime.remove(k)
        }
    }

    // Tick every second — causes elapsed strings to recompose
    var tick by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            tick = System.currentTimeMillis()
        }
    }

    val haptic = LocalHapticFeedback.current
    val activeCount = optimisticStates.values.count { it }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 54.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Control Center",
                        color = textPrimary,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFontFamily
                    )
                    if (activeCount > 0) {
                        Text(
                            text = "$activeCount device${if (activeCount > 1) "s" else ""} running",
                            color = Color(0xFF34C759),
                            fontSize = 13.sp,
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Done", color = Color(0xFF007AFF), fontSize = 17.sp, fontWeight = FontWeight.SemiBold, fontFamily = InterFontFamily)
                }
            }

            Spacer(Modifier.height(6.dp))
            Text(
                text = "Toggle hardware manually with instant response.",
                color = textSecondary,
                fontSize = 13.sp,
                fontFamily = InterFontFamily,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Master Action Bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        devices.forEach { dev ->
                            optimisticStates[dev.id] = true
                            if (!deviceOnTime.containsKey(dev.id)) deviceOnTime[dev.id] = System.currentTimeMillis()
                        }
                        try { onSendCommand("on") } catch (e: Exception) { Log.e("ControlCenter", "Failed: ${e.message}") }
                    },
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34C759))
                ) {
                    Icon(imageVector = Icons.Rounded.PowerSettingsNew, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                    Spacer(Modifier.width(6.dp))
                    Text("All ON", color = Color.White, fontWeight = FontWeight.SemiBold, fontFamily = InterFontFamily, fontSize = 15.sp)
                }
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        devices.forEach { dev ->
                            optimisticStates[dev.id] = false
                            deviceOnTime.remove(dev.id)
                        }
                        try { onSendCommand("off") } catch (e: Exception) { Log.e("ControlCenter", "Failed: ${e.message}") }
                    },
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFF8E8E93))
                ) {
                    Icon(imageVector = Icons.Rounded.Block, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                    Spacer(Modifier.width(6.dp))
                    Text("All OFF", color = Color.White, fontWeight = FontWeight.SemiBold, fontFamily = InterFontFamily, fontSize = 15.sp)
                }
            }

            // Device list (single column)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(devices, key = { it.id }) { device ->
                    val isChecked = optimisticStates[device.id] == true
                    val startTime = deviceOnTime[device.id]
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val mainActivity = context as? MainActivity
                    val endTime = mainActivity?.activeTimerEndTimes?.get(device.id)
                    val elapsed: String? = if (endTime != null && endTime > tick) {
                        val remainingSecs = ((endTime - tick) / 1000L).coerceAtLeast(0L)
                        "Timer · ${remainingSecs / 60}:${(remainingSecs % 60).toString().padStart(2, '0')}"
                    } else if (isChecked && startTime != null) {
                        val secs = ((tick - startTime) / 1000L).coerceAtLeast(0L)
                        "${secs / 60}:${(secs % 60).toString().padStart(2, '0')}"
                    } else null

                    DeviceControlCard(
                        device = device,
                        isChecked = isChecked,
                        elapsed = elapsed,
                        isDark = isDark,
                        onToggle = {
                            val targetState = !isChecked
                            optimisticStates[device.id] = targetState
                            if (targetState) {
                                deviceOnTime[device.id] = System.currentTimeMillis()
                            } else {
                                deviceOnTime.remove(device.id)
                            }
                            try {
                                onSendCommand(if (targetState) device.cmdOn else device.cmdOff)
                            } catch (e: Exception) {
                                Log.e("ControlCenter", "Command failed: ${e.message}")
                                optimisticStates[device.id] = isChecked
                                if (isChecked) deviceOnTime[device.id] = startTime ?: System.currentTimeMillis()
                                else deviceOnTime.remove(device.id)
                            }
                        },
                        onStop = {
                            optimisticStates[device.id] = false
                            deviceOnTime.remove(device.id)
                            try { onSendCommand(device.cmdOff) } catch (e: Exception) { Log.e("ControlCenter", "Stop failed: ${e.message}") }
                        }
                    )
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
    elapsed: String? = null,
    isDark: Boolean = false,
    onToggle: () -> Unit,
    onStop: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val activeAccent = Color(0xFF34C759)
    val cardBg = if (isDark) Color(0xFF1C1C1E) else Color(0xFFFFFFFF)
    val cardBorder = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
    val textColor = if (isDark) Color.White else Color.Black
    val subTextColor = if (isDark) Color.White.copy(alpha = 0.5f) else Color(0xFF8E8E93)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(
                1.dp,
                if (isChecked) activeAccent.copy(alpha = 0.35f) else cardBorder,
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Device icon
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (isChecked) activeAccent.copy(alpha = 0.15f)
                        else if (isDark) Color.White.copy(alpha = 0.08f)
                        else Color(0xFFF2F2F7)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Lightbulb,
                    contentDescription = null,
                    tint = if (isChecked) activeAccent else subTextColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            // Name + status text
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.name,
                    color = textColor,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = InterFontFamily,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isChecked) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(activeAccent)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text = if (elapsed != null) "Running \u00b7 $elapsed" else "Running",
                            color = activeAccent,
                            fontFamily = InterFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Text(
                            text = "Standby",
                            color = subTextColor,
                            fontFamily = InterFontFamily,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            // Toggle switch
            AppleSwitch(
                checked = isChecked,
                onCheckedChange = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onToggle()
                }
            )
        }

        // Stop button — slides in/out when device is running
        AnimatedVisibility(
            visible = isChecked,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column {
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onStop,
                    modifier = Modifier.fillMaxWidth().height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF3B30).copy(alpha = 0.6f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF3B30))
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Cancel,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Stop", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, fontFamily = InterFontFamily)
                }
            }
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
            icon = Icons.AutoMirrored.Outlined.List,
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
    val context = LocalContext.current
    val isDark = context.getSharedPreferences("JasicaSettings", Context.MODE_PRIVATE).getBoolean("DARK_MODE", false)
    val bgColor = if (isDark) Color(0xFF000000) else Color(0xFFF2F2F7)
    val titleColor = if (isDark) Color(0xFFFFFFFF) else Color(0xFF000000)
    val emptyTextColor = if (isDark) Color.White.copy(alpha = 0.5f) else Color(0xFF8E8E93)

    LaunchedEffect(history.size) {
        if (history.isNotEmpty()) {
            listState.animateScrollToItem(history.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .clickable(enabled = false) {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 54.dp, start = 16.dp, end = 16.dp, bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recents",
                    color = titleColor,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily
                )
                TextButton(onClick = onDismiss) {
                    Text("Done", color = Color(0xFF007AFF), fontSize = 17.sp, fontWeight = FontWeight.SemiBold, fontFamily = InterFontFamily)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (history.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No recent interactions.",
                        color = emptyTextColor,
                        fontFamily = InterFontFamily,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(history, key = { it.hashCode() }) { message ->
                        ChatBubble(message, isDark = isDark)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage, isDark: Boolean = false) {
    val isUser = message.isUser
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bubbleColor = if (isUser) Color(0xFF007AFF) else (if (isDark) Color(0xFF1C1C1E) else Color(0xFFFFFFFF))
    val bubbleBorder = if (isUser) Color(0xFF007AFF) else (if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA))
    val textColor = if (isUser) Color.White else (if (isDark) Color.White else Color.Black)
    val timeColor = if (isDark) Color.White.copy(alpha = 0.4f) else Color(0xFF8E8E93)

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (isUser) 18.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 18.dp
                ))
                .background(bubbleColor)
                .border(1.dp, bubbleBorder, RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (isUser) 18.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 18.dp
                ))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            MessageFormattedText(message.text, textColor)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = message.time,
            color = timeColor,
            fontSize = 10.sp,
            fontFamily = InterFontFamily,
            modifier = Modifier.padding(horizontal = 4.dp)
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
fun JasicaGraphicalDialogPanel(hazeState: dev.chrisbanes.haze.HazeState? = null, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
    ) {
        val baseModifier = Modifier
            .fillMaxWidth()
            .then(if (hazeState != null) Modifier.clip(RoundedCornerShape(24.dp)).hazeEffect(state = hazeState, style = dev.chrisbanes.haze.HazeStyle(blurRadius = 20.dp, backgroundColor = Color(0x661C1C1E), tint = dev.chrisbanes.haze.HazeTint(Color(0x331C1C1E)))) else Modifier)
            .background(if (hazeState != null) Color.Transparent else Color(0xE61C1C1E))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
        Box(modifier = baseModifier) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Dialogs (Adapted with Orange Theme & High Contrast text)
// ─────────────────────────────────────────────────────────────────────────────

// ─────────────────────────────────────────────────────────────────────────────
//  Mic Error Dialog
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun MicErrorDialog(
    errorType  : MainActivity.MicErrorType,
    onDismiss  : () -> Unit,
    context    : android.content.Context
) {
    val (icon, title, body, actionLabel, onAction) = when (errorType) {
        MainActivity.MicErrorType.PERMISSION_DENIED -> listOf(
            "🎙️",
            "Microphone Access Denied",
            "Jasica needs the microphone permission to hear you.\n\nGo to Settings → App Permissions → Microphone and enable it.",
            "Open Settings",
            {
                val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = android.net.Uri.fromParts("package", context.packageName, null)
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        )
        MainActivity.MicErrorType.MIC_IN_USE -> listOf(
            "📞",
            "Microphone is Busy",
            "The microphone is currently in use by another app (like a phone call or recording app).\n\nEnd your call or close the other app, then try again.",
            "Got It",
            { /* just dismiss */ }
        )
        MainActivity.MicErrorType.HARDWARE_ERROR -> listOf(
            "⚠️",
            "Microphone Error",
            "There was a problem accessing your microphone.\n\nTry restarting the app. If the issue persists, check if another app is blocking the mic or try restarting your phone.",
            "Dismiss",
            { /* just dismiss */ }
        )
        MainActivity.MicErrorType.RECOGNIZER_UNAVAILABLE -> listOf(
            "🔇",
            "Speech Service Unavailable",
            "The speech recognition service is unavailable.\n\nMake sure Google app is installed and up to date, then try again.",
            "Dismiss",
            { /* just dismiss */ }
        )
    }

    @Suppress("UNCHECKED_CAST")
    val action = onAction as () -> Unit

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = androidx.compose.ui.Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1A1A2E))
                .border(1.dp, when (errorType) {
                    MainActivity.MicErrorType.PERMISSION_DENIED -> JasicaOrange
                    MainActivity.MicErrorType.MIC_IN_USE        -> Color(0xFF3A86FF)
                    else                                         -> Color(0xFFFF4444)
                }.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .padding(28.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // Icon
                Box(
                    modifier = androidx.compose.ui.Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(when (errorType) {
                            MainActivity.MicErrorType.PERMISSION_DENIED -> JasicaOrange
                            MainActivity.MicErrorType.MIC_IN_USE        -> Color(0xFF3A86FF)
                            else                                         -> Color(0xFFFF4444)
                        }.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(icon as String, fontSize = 32.sp)
                }

                Spacer(androidx.compose.ui.Modifier.height(20.dp))

                // Title
                Text(
                    title as String,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily,
                    textAlign = TextAlign.Center
                )

                Spacer(androidx.compose.ui.Modifier.height(12.dp))

                // Body
                Text(
                    body as String,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                    fontFamily = InterFontFamily,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(androidx.compose.ui.Modifier.height(28.dp))

                // Action button
                Button(
                    onClick = { action(); onDismiss() },
                    modifier = androidx.compose.ui.Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (errorType) {
                            MainActivity.MicErrorType.PERMISSION_DENIED -> JasicaOrange
                            MainActivity.MicErrorType.MIC_IN_USE        -> Color(0xFF3A86FF)
                            else                                         -> Color(0xFFFF4444)
                        }
                    )
                ) {
                    Text(actionLabel as String, color = Color.White, fontWeight = FontWeight.Bold, fontFamily = InterFontFamily)
                }

                Spacer(androidx.compose.ui.Modifier.height(8.dp))

                // Dismiss link
                TextButton(onClick = onDismiss) {
                    Text("Not now", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp, fontFamily = InterFontFamily)
                }
            }
        }
    }
}


// ─────────────────────────────────────────────────────────────────────────────
//  Lucide Icons (Crisp Vector Graphics for Jetpack Compose)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun LucideIconBox(
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Map solid colors to Apple gradients
    val brush = remember(backgroundColor) {
        when (backgroundColor.value.toULong()) {
            0xFF007AFFuL -> androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color(0xFF44A6FF), Color(0xFF007AFF))) // Blue
            0xFF5856D6uL -> androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color(0xFF7868E6), Color(0xFF5856D6))) // Indigo
            0xFF34C759uL -> androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color(0xFF4CD964), Color(0xFF34C759))) // Green
            0xFFFF9500uL -> androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color(0xFFFFB340), Color(0xFFFF9500))) // Orange
            0xFFFF3B30uL -> androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color(0xFFFF665A), Color(0xFFFF3B30))) // Red
            0xFF8E8E93uL -> androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color(0xFFA3AAB2), Color(0xFF8E8E93))) // Silver
            0xFFAF52DEuL -> androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color(0xFFC973F0), Color(0xFFAF52DE))) // Purple
            0xFF30B0C7uL -> androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color(0xFF5AC8FA), Color(0xFF30B0C7))) // Teal
            else -> androidx.compose.ui.graphics.Brush.verticalGradient(listOf(backgroundColor, backgroundColor))
        }
    }

    Box(
        modifier = modifier
            .size(30.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(8.dp),
                ambientColor = Color.Black.copy(alpha = 0.12f),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .background(brush)
            // Add top inner shadow (white 0.3)
            .border(
                width = 0.5.dp,
                color = Color.White.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Drop shadow for the icon itself
        Box(
            modifier = Modifier.graphicsLayer {
                shadowElevation = 2f
                shape = RoundedCornerShape(1.dp)
                ambientShadowColor = Color.Black.copy(alpha=0.12f)
                spotShadowColor = Color.Black.copy(alpha=0.12f)
            }
        ) {
            content()
        }
    }
}

@Composable
fun LucideMic(modifier: Modifier = Modifier.size(16.dp), tint: Color = Color.White) {
    Canvas(modifier = modifier) {
        val stroke = androidx.compose.ui.graphics.drawscope.Stroke(
            width = size.width * 0.1f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round,
            join = androidx.compose.ui.graphics.StrokeJoin.Round
        )
        val w = size.width * 0.4f
        val h = size.height * 0.55f
        val l = (size.width - w) / 2f
        val t = size.height * 0.1f
        drawRoundRect(
            color = tint,
            topLeft = androidx.compose.ui.geometry.Offset(l, t),
            size = androidx.compose.ui.geometry.Size(w, h),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w / 2f, w / 2f),
            style = stroke
        )
        val arcPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(size.width * 0.18f, size.height * 0.38f)
            cubicTo(
                size.width * 0.18f, size.height * 0.72f,
                size.width * 0.82f, size.height * 0.72f,
                size.width * 0.82f, size.height * 0.38f
            )
            moveTo(size.width * 0.5f, size.height * 0.72f)
            lineTo(size.width * 0.5f, size.height * 0.9f)
            moveTo(size.width * 0.3f, size.height * 0.9f)
            lineTo(size.width * 0.7f, size.height * 0.9f)
        }
        drawPath(arcPath, color = tint, style = stroke)
    }
}

@Composable
fun LucideClock(modifier: Modifier = Modifier.size(16.dp), tint: Color = Color.White) {
    Canvas(modifier = modifier) {
        val stroke = androidx.compose.ui.graphics.drawscope.Stroke(
            width = size.width * 0.1f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round,
            join = androidx.compose.ui.graphics.StrokeJoin.Round
        )
        drawCircle(color = tint, radius = size.width * 0.42f, style = stroke)
        val p = androidx.compose.ui.graphics.Path().apply {
            moveTo(size.width * 0.5f, size.height * 0.25f)
            lineTo(size.width * 0.5f, size.height * 0.5f)
            lineTo(size.width * 0.68f, size.height * 0.5f)
        }
        drawPath(p, color = tint, style = stroke)
    }
}

@Composable
fun LucideDroplet(modifier: Modifier = Modifier.size(16.dp), tint: Color = Color.White) {
    Canvas(modifier = modifier) {
        val stroke = androidx.compose.ui.graphics.drawscope.Stroke(
            width = size.width * 0.1f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round,
            join = androidx.compose.ui.graphics.StrokeJoin.Round
        )
        val p = androidx.compose.ui.graphics.Path().apply {
            moveTo(size.width * 0.5f, size.height * 0.12f)
            cubicTo(
                size.width * 0.15f, size.height * 0.55f,
                size.width * 0.15f, size.height * 0.88f,
                size.width * 0.5f, size.height * 0.88f
            )
            cubicTo(
                size.width * 0.85f, size.height * 0.88f,
                size.width * 0.85f, size.height * 0.55f,
                size.width * 0.5f, size.height * 0.12f
            )
            close()
        }
        drawPath(p, color = tint, style = stroke)
    }
}

@Composable
fun LucideZap(modifier: Modifier = Modifier.size(16.dp), tint: Color = Color.White) {
    Canvas(modifier = modifier) {
        val stroke = androidx.compose.ui.graphics.drawscope.Stroke(
            width = size.width * 0.1f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round,
            join = androidx.compose.ui.graphics.StrokeJoin.Round
        )
        val p = androidx.compose.ui.graphics.Path().apply {
            moveTo(size.width * 0.55f, size.height * 0.1f)
            lineTo(size.width * 0.2f, size.height * 0.52f)
            lineTo(size.width * 0.48f, size.height * 0.52f)
            lineTo(size.width * 0.42f, size.height * 0.9f)
            lineTo(size.width * 0.8f, size.height * 0.45f)
            lineTo(size.width * 0.52f, size.height * 0.45f)
            close()
        }
        drawPath(p, color = tint, style = stroke)
    }
}

@Composable
fun LucideKey(modifier: Modifier = Modifier.size(16.dp), tint: Color = Color.White) {
    Canvas(modifier = modifier) {
        val stroke = androidx.compose.ui.graphics.drawscope.Stroke(
            width = size.width * 0.1f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round,
            join = androidx.compose.ui.graphics.StrokeJoin.Round
        )
        drawCircle(color = tint, radius = size.width * 0.22f, center = androidx.compose.ui.geometry.Offset(size.width * 0.32f, size.height * 0.35f), style = stroke)
        val p = androidx.compose.ui.graphics.Path().apply {
            moveTo(size.width * 0.48f, size.height * 0.51f)
            lineTo(size.width * 0.85f, size.height * 0.88f)
            moveTo(size.width * 0.72f, size.height * 0.75f)
            lineTo(size.width * 0.82f, size.height * 0.65f)
            moveTo(size.width * 0.60f, size.height * 0.63f)
            lineTo(size.width * 0.70f, size.height * 0.53f)
        }
        drawPath(p, color = tint, style = stroke)
    }
}



@Composable
fun LiquidGlassKnob(
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp,
    scale: Float,
    baseAlpha: Float,
    hazeState: dev.chrisbanes.haze.HazeState? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(width, height)
            .scale(scale)
            .clip(RoundedCornerShape(50.dp))
            .then(if (hazeState != null) Modifier.hazeEffect(state = hazeState, style = dev.chrisbanes.haze.HazeStyle(blurRadius = 20.dp, tint = dev.chrisbanes.haze.HazeTint(Color.White.copy(alpha = 0.15f)))) else Modifier)
            .background(Color.White.copy(alpha = baseAlpha))
            .border(0.5.dp, Color.Black.copy(alpha = 0.1f), RoundedCornerShape(50.dp))
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(50.dp),
                spotColor = Color.Black.copy(alpha = 0.2f),
                ambientColor = Color.Black.copy(alpha = 0.1f),
                clip = false
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(start = 2.dp, end = 2.dp, top = 1.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.48f)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 10.dp, bottomEnd = 10.dp))
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.White.copy(alpha = 0.95f),
                        1.0f to Color.White.copy(alpha = 0.1f)
                    )
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 2.dp, end = 2.dp, bottom = 1.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 12.dp, bottomEnd = 12.dp))
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.Transparent,
                        1.0f to Color.White.copy(alpha = 0.85f)
                    )
                )
        )
    }
}

@Composable
fun AppleSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    modifier: Modifier = Modifier,
    steps: Int = 0,
    hazeState: dev.chrisbanes.haze.HazeState? = null
) {
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    val activeTrackColor = Color(0xFF0A84FF)
    val inactiveTrackColor = Color(0xFFE5E5EA)

    var totalWidthPx by remember { mutableFloatStateOf(0f) }
    val thumbRadiusPx = with(LocalDensity.current) { 12.dp.toPx() } 
    val trackWidthPx = (totalWidthPx - 2 * thumbRadiusPx).coerceAtLeast(1f)

    val fraction = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)
    val thumbOffsetX = (fraction * trackWidthPx)

    var isDragging by remember { mutableStateOf(false) }
    val targetWidth = if (isDragging) 28.dp else 24.dp
    val targetScale = if (isDragging) 1.15f else 1f
    
    val thumbWidth by animateDpAsState(targetWidth, animationSpec = androidx.compose.animation.core.spring(dampingRatio = 0.35f, stiffness = 500f), label = "tw")
    val thumbScale by animateFloatAsState(targetScale, animationSpec = androidx.compose.animation.core.spring(dampingRatio = 0.35f, stiffness = 500f), label = "ts")
    
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentValueRange by rememberUpdatedState(valueRange)
    val currentSteps by rememberUpdatedState(steps)

    fun updateValueFromOffset(offsetX: Float) {
        val newOffsetX = (offsetX - thumbRadiusPx).coerceIn(0f, trackWidthPx)
        val newFraction = newOffsetX / trackWidthPx
        val newValue = currentValueRange.start + newFraction * (currentValueRange.endInclusive - currentValueRange.start)
        
        val roundedValue = if (currentSteps > 0) {
            val stepSize = (currentValueRange.endInclusive - currentValueRange.start) / (currentSteps + 1)
            Math.round(newValue / stepSize) * stepSize
        } else newValue
        
        currentOnValueChange(roundedValue.coerceIn(currentValueRange.start, currentValueRange.endInclusive))
    }

    Box(
        modifier = modifier
            .height(22.dp)
            .onSizeChanged { totalWidthPx = it.width.toFloat() },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .height(6.dp)
                .clip(CircleShape)
                .background(inactiveTrackColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(activeTrackColor)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { 
                            isDragging = true 
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                        },
                        onDragEnd = { isDragging = false },
                        onDragCancel = { isDragging = false }
                    ) { change, _ ->
                        change.consume()
                        updateValueFromOffset(change.position.x)
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { offset ->
                            isDragging = true
                            updateValueFromOffset(offset.x)
                            tryAwaitRelease()
                            isDragging = false
                        }
                    )
                }
        )

        Box(
            modifier = Modifier
                .offset { IntOffset(thumbOffsetX.roundToInt(), 0) }
                .size(width = thumbWidth, height = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            LiquidGlassKnob(
                width = thumbWidth,
                height = 18.dp,
                scale = thumbScale,
                baseAlpha = 0.9f,
                hazeState = hazeState
            )
        }
    }
}

@Composable
fun AppleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    hazeState: dev.chrisbanes.haze.HazeState? = null
) {
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }
    val trackColor by animateColorAsState(
        if (checked) Color(0xFF34C759) else Color(0xFFE5E5EA),
        animationSpec = androidx.compose.animation.core.tween(300),
        label = "trackColor"
    )

    val targetTranslation = if (checked && isPressed) 15f
                            else if (checked) 16f
                            else if (isPressed) -3f
                            else 0f

    val targetWidth = if (isPressed) 28.dp else 24.dp
    // 50% bigger on press — scale causes the knob to visually overflow the unclipped track
    val targetScale = if (isPressed) 1.5f else 1f

    val jellySpring = androidx.compose.animation.core.spring<Float>(dampingRatio = 0.35f, stiffness = 500f)
    val jellySpringDp = androidx.compose.animation.core.spring<androidx.compose.ui.unit.Dp>(dampingRatio = 0.35f, stiffness = 500f)

    val thumbTranslationX by animateFloatAsState(targetTranslation, animationSpec = jellySpring, label = "tx")
    val thumbWidth by animateDpAsState(targetWidth, animationSpec = jellySpringDp, label = "tw")
    val thumbScale by animateFloatAsState(targetScale, animationSpec = jellySpring, label = "ts")

    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .width(44.dp)
            .height(22.dp)
            // NO clip on outer Box — lets the knob visually overflow the track border on press
            .pointerInput(checked) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = {
                        onCheckedChange(!checked)
                    }
                )
            }
    ) {
        // Clipped pill-shaped track background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(100.dp))
                .background(trackColor)
                .border(0.5.dp, Color.Black.copy(alpha = 0.1f), RoundedCornerShape(100.dp))
        )
        // Knob — unclipped so it bleeds outside the track on press (iOS-authentic overflow)
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(top = 2.dp, bottom = 2.dp)
                .offset(x = (thumbTranslationX + 2).dp)
                .size(width = thumbWidth, height = 18.dp)
        ) {
            LiquidGlassKnob(
                width = thumbWidth,
                height = 18.dp,
                scale = thumbScale,
                baseAlpha = 0.1f,
                hazeState = hazeState
            )
        }
    }
}

@Composable
fun LucideSliders(modifier: Modifier = Modifier.size(16.dp), tint: Color = Color.White) {
    Canvas(modifier = modifier) {
        val stroke = androidx.compose.ui.graphics.drawscope.Stroke(
            width = size.width * 0.1f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round,
            join = androidx.compose.ui.graphics.StrokeJoin.Round
        )
        drawLine(color = tint, start = androidx.compose.ui.geometry.Offset(size.width * 0.15f, size.height * 0.3f), end = androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.3f), strokeWidth = stroke.width, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        drawCircle(color = tint, radius = size.width * 0.12f, center = androidx.compose.ui.geometry.Offset(size.width * 0.4f, size.height * 0.3f))
        
        drawLine(color = tint, start = androidx.compose.ui.geometry.Offset(size.width * 0.15f, size.height * 0.7f), end = androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.7f), strokeWidth = stroke.width, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        drawCircle(color = tint, radius = size.width * 0.12f, center = androidx.compose.ui.geometry.Offset(size.width * 0.65f, size.height * 0.7f))
    }
}

@Composable
fun LucideCpu(modifier: Modifier = Modifier.size(16.dp), tint: Color = Color.White) {
    Canvas(modifier = modifier) {
        val stroke = androidx.compose.ui.graphics.drawscope.Stroke(
            width = size.width * 0.1f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round,
            join = androidx.compose.ui.graphics.StrokeJoin.Round
        )
        val boxSize = size.width * 0.5f
        val boxLeft = (size.width - boxSize) / 2f
        val boxTop = (size.height - boxSize) / 2f
        drawRoundRect(color = tint, topLeft = androidx.compose.ui.geometry.Offset(boxLeft, boxTop), size = androidx.compose.ui.geometry.Size(boxSize, boxSize), cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f), style = stroke)
        drawRect(color = tint, topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.38f, size.height * 0.38f), size = androidx.compose.ui.geometry.Size(size.width * 0.24f, size.height * 0.24f))
        val p = androidx.compose.ui.graphics.Path().apply {
            moveTo(size.width * 0.35f, 0f); lineTo(size.width * 0.35f, boxTop)
            moveTo(size.width * 0.65f, 0f); lineTo(size.width * 0.65f, boxTop)
            moveTo(size.width * 0.35f, size.height); lineTo(size.width * 0.35f, boxTop + boxSize)
            moveTo(size.width * 0.65f, size.height); lineTo(size.width * 0.65f, boxTop + boxSize)
            moveTo(0f, size.height * 0.35f); lineTo(boxLeft, size.height * 0.35f)
            moveTo(0f, size.height * 0.65f); lineTo(boxLeft, size.height * 0.65f)
            moveTo(size.width, size.height * 0.35f); lineTo(boxLeft + boxSize, size.height * 0.35f)
            moveTo(size.width, size.height * 0.65f); lineTo(boxLeft + boxSize, size.height * 0.65f)
        }
        drawPath(p, color = tint, style = stroke)
    }
}

@Composable
fun LucideLayers(modifier: Modifier = Modifier.size(16.dp), tint: Color = Color.White) {
    Canvas(modifier = modifier) {
        val stroke = androidx.compose.ui.graphics.drawscope.Stroke(
            width = size.width * 0.1f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round,
            join = androidx.compose.ui.graphics.StrokeJoin.Round
        )
        val p = androidx.compose.ui.graphics.Path().apply {
            moveTo(size.width * 0.5f, size.height * 0.12f)
            lineTo(size.width * 0.85f, size.height * 0.32f)
            lineTo(size.width * 0.5f, size.height * 0.52f)
            lineTo(size.width * 0.15f, size.height * 0.32f)
            close()
            moveTo(size.width * 0.15f, size.height * 0.55f)
            lineTo(size.width * 0.5f, size.height * 0.75f)
            lineTo(size.width * 0.85f, size.height * 0.55f)
        }
        drawPath(p, color = tint, style = stroke)
    }
}

@Composable
fun LucideRotateCcw(modifier: Modifier = Modifier.size(16.dp), tint: Color = Color.White) {
    Canvas(modifier = modifier) {
        val stroke = androidx.compose.ui.graphics.drawscope.Stroke(
            width = size.width * 0.1f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round,
            join = androidx.compose.ui.graphics.StrokeJoin.Round
        )
        val arcPath = androidx.compose.ui.graphics.Path().apply {
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(size.width * 0.15f, size.height * 0.15f, size.width * 0.85f, size.height * 0.85f),
                startAngleDegrees = 45f,
                sweepAngleDegrees = 270f,
                forceMoveTo = true
            )
            moveTo(size.width * 0.15f, size.height * 0.2f)
            lineTo(size.width * 0.15f, size.height * 0.45f)
            lineTo(size.width * 0.4f, size.height * 0.45f)
        }
        drawPath(arcPath, color = tint, style = stroke)
    }
}

@Composable
fun LucideChevronDown(modifier: Modifier = Modifier.size(14.dp), tint: Color = Color.White) {
    Canvas(modifier = modifier) {
        val stroke = androidx.compose.ui.graphics.drawscope.Stroke(
            width = size.width * 0.14f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round,
            join = androidx.compose.ui.graphics.StrokeJoin.Round
        )
        val p = androidx.compose.ui.graphics.Path().apply {
            moveTo(size.width * 0.2f, size.height * 0.35f)
            lineTo(size.width * 0.5f, size.height * 0.65f)
            lineTo(size.width * 0.8f, size.height * 0.35f)
        }
        drawPath(p, color = tint, style = stroke)
    }
}

@Composable
fun LucideChevronUp(modifier: Modifier = Modifier.size(14.dp), tint: Color = Color.White) {
    Canvas(modifier = modifier) {
        val stroke = androidx.compose.ui.graphics.drawscope.Stroke(
            width = size.width * 0.14f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round,
            join = androidx.compose.ui.graphics.StrokeJoin.Round
        )
        val p = androidx.compose.ui.graphics.Path().apply {
            moveTo(size.width * 0.2f, size.height * 0.65f)
            lineTo(size.width * 0.5f, size.height * 0.35f)
            lineTo(size.width * 0.8f, size.height * 0.65f)
        }
        drawPath(p, color = tint, style = stroke)
    }
}

@Composable
fun LucideTrash(modifier: Modifier = Modifier.size(16.dp), tint: Color = Color.White) {
    Canvas(modifier = modifier) {
        val stroke = androidx.compose.ui.graphics.drawscope.Stroke(
            width = size.width * 0.1f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round,
            join = androidx.compose.ui.graphics.StrokeJoin.Round
        )
        val p = androidx.compose.ui.graphics.Path().apply {
            moveTo(size.width * 0.2f, size.height * 0.25f)
            lineTo(size.width * 0.8f, size.height * 0.25f)
            moveTo(size.width * 0.3f, size.height * 0.25f)
            lineTo(size.width * 0.35f, size.height * 0.85f)
            lineTo(size.width * 0.65f, size.height * 0.85f)
            lineTo(size.width * 0.7f, size.height * 0.25f)
            moveTo(size.width * 0.4f, size.height * 0.25f)
            lineTo(size.width * 0.4f, size.height * 0.12f)
            lineTo(size.width * 0.6f, size.height * 0.12f)
            lineTo(size.width * 0.6f, size.height * 0.25f)
        }
        drawPath(p, color = tint, style = stroke)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Apple Design System Group & Rows with Lucide Icons
// ─────────────────────────────────────────────────────────────────────────────

fun restartApp(context: Context) {
    try {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent?.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    } catch (e: Exception) {
        (context as? Activity)?.recreate()
    }
}

@Composable
fun AppleRestartDialog(
    onDismiss: () -> Unit,
    onRestart: () -> Unit
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
                .clickable(
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                    indication = null
                ) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFFFFFFF))
                    .border(0.5.dp, Color(0xFFE5E5EA), RoundedCornerShape(18.dp))
                    .clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null
                    ) {},
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(20.dp))
                
                // Icon Header
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF007AFF).copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(22.dp), tint = Color(0xFF007AFF))
                }
                
                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Settings Saved",
                    color = Color.Black,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                
                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Your new settings have been saved. An app restart is recommended to apply all configurations and initialize services.",
                    color = Color(0xFF3C3C43).copy(alpha = 0.75f),
                    fontSize = 13.sp,
                    fontFamily = InterFontFamily,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(horizontal = 18.dp)
                )

                Spacer(Modifier.height(18.dp))

                androidx.compose.material3.HorizontalDivider(color = Color(0xFFE5E5EA), thickness = 0.6.dp)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Later",
                            color = Color(0xFF8E8E93),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = InterFontFamily
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(0.6.dp)
                            .fillMaxHeight()
                            .background(Color(0xFFE5E5EA))
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onRestart() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Restart Now",
                            color = Color(0xFF007AFF),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = InterFontFamily
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppleSettingsGroup(
    title: String? = null,
    footer: String? = null,
    isDark: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardBg = if (isDark) Color(0xFF1C1C1E) else Color(0xFFFFFFFF)
    val cardBorder = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
    val headerColor = if (isDark) Color.White.copy(alpha = 0.5f) else Color(0xFF8E8E93)

    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
        if (title != null) {
            Text(
                title.uppercase(java.util.Locale.ROOT),
                color = headerColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = InterFontFamily,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(cardBg)
                .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
        ) {
            content()
        }
        if (footer != null) {
            Text(
                footer,
                color = headerColor,
                fontSize = 13.sp,
                fontFamily = InterFontFamily,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp)
            )
        }
    }
}

@Composable
fun AppleSettingsRow(
    title: String,
    subtitle: String? = null,
    icon: (@Composable () -> Unit)? = null,
    iconBgColor: Color = Color.Transparent,
    showDivider: Boolean = true,
    isDark: Boolean = false,
    onClick: (() -> Unit)? = null,
    control: (@Composable () -> Unit)? = null
) {
    val titleColor = if (isDark) Color.White else Color.Black
    val subColor = if (isDark) Color.White.copy(alpha = 0.5f) else Color(0xFF8E8E93)
    val dividerColor = if (isDark) Color(0xFF38383A) else Color(0xFFE5E5EA)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            LucideIconBox(backgroundColor = iconBgColor) {
                icon()
            }
            Spacer(Modifier.width(16.dp))
        }
        
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(end = 16.dp, top = 12.dp, bottom = 12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = titleColor, fontSize = 16.sp, fontWeight = FontWeight.Medium, fontFamily = InterFontFamily)
                    if (subtitle != null) {
                        Spacer(Modifier.height(2.dp))
                        Text(subtitle, color = subColor, fontSize = 13.sp, fontFamily = InterFontFamily)
                    }
                }
                if (control != null) {
                    Spacer(Modifier.width(12.dp))
                    control()
                }
            }
            if (showDivider) {
                androidx.compose.material3.HorizontalDivider(color = dividerColor, thickness = 0.5.dp)
            }
        }
    }
}

@Composable
fun SettingsScreen(
    currentApiKey: String,
    currentModel: String,
    isWakeWordMode: Boolean,
    isAdvancedAiMode: Boolean,
    isOnlineModeEnabled: Boolean,
    useAdminPanelKey: Boolean,
    sharedPrefs: android.content.SharedPreferences,
    onDismiss: () -> Unit,
    onSave: (String, String, Boolean) -> Unit,
    hazeState: dev.chrisbanes.haze.HazeState? = null
) {
    var darkModeInput by remember { mutableStateOf(sharedPrefs.getBoolean("DARK_MODE", false)) }
    var apiKeyInput by remember { mutableStateOf(currentApiKey) }
    var selectedModel by remember { mutableStateOf(currentModel) }
    var wakeWordInput by remember { mutableStateOf(isWakeWordMode) }
    var historyLoggingInput by remember { mutableStateOf(sharedPrefs.getBoolean("HISTORY_LOGGING", true)) }
    var waterReminderInput by remember { mutableStateOf(sharedPrefs.getBoolean("WATER_REMINDER", false)) }
    var waterInterval by remember { mutableStateOf(sharedPrefs.getInt("WATER_REMINDER_INTERVAL", 30)) }
    var onlineModeInput by remember { mutableStateOf(isOnlineModeEnabled) }
    var adminKeyInput by remember { mutableStateOf(useAdminPanelKey) }
    var geminiKeyInput by remember { mutableStateOf(sharedPrefs.getString("GEMINI_API_KEY", "") ?: "") }
    var showApiKey by remember { mutableStateOf(false) }

    // Expanded states for each device config card
    var expandedDevId by remember { mutableStateOf<String?>(null) }
    
    val context = LocalContext.current
    
    var showPasswordDialog by remember { mutableStateOf(false) }
    var passwordInput by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }

    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showRestartDialog by remember { mutableStateOf(false) }

    val bgColor = if (darkModeInput) Color(0xFF000000) else Color(0xFFF2F2F7)
    val textPrimary = if (darkModeInput) Color(0xFFFFFFFF) else Color(0xFF000000)
    val textSecondary = if (darkModeInput) Color.White.copy(alpha = 0.5f) else Color(0xFF8E8E93)
    val cardBg = if (darkModeInput) Color(0xFF1C1C1E) else Color(0xFFFFFFFF)
    val cardBorder = if (darkModeInput) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
    val fieldBg = if (darkModeInput) Color(0xFF2C2C2E) else Color(0xFFF2F2F7)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .clickable(enabled = false) {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 54.dp, start = 16.dp, end = 16.dp, bottom = 0.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp, start = 8.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    "Settings",
                    color = textPrimary,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily,
                    letterSpacing = (-1).sp
                )
                Text(
                    "Done",
                    color = Color(0xFF007AFF),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = InterFontFamily,
                    modifier = Modifier.clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null
                    ) { 
                        sharedPrefs.edit()
                            .putBoolean("DARK_MODE", darkModeInput)
                            .putBoolean("HISTORY_LOGGING", historyLoggingInput)
                            .putBoolean("WATER_REMINDER", waterReminderInput)
                            .putInt("WATER_REMINDER_INTERVAL", waterInterval)
                            .putBoolean("ONLINE_MODE_ENABLED", onlineModeInput)
                            .putBoolean("USE_ADMIN_PANEL_KEY", adminKeyInput)
                            .putString("GEMINI_API_KEY", geminiKeyInput.trim())
                            .apply()
                        onSave(apiKeyInput, selectedModel, wakeWordInput)
                        showRestartDialog = true
                    }.padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }
            
            // Unified Scrolling Content (Lazy Column for smoothness)
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                // Core System Group
                item {
                    AppleSettingsGroup(title = "Appearance & Core System", isDark = darkModeInput) {
                        AppleSettingsRow(
                            title = "Dark Mode",
                            subtitle = "Switch between Apple Light and Dark theme",
                            icon = { Icon(Icons.Rounded.Build, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.White) },
                            iconBgColor = Color(0xFF5856D6),
                            showDivider = true,
                            isDark = darkModeInput,
                            control = {
                                AppleSwitch(
                                      checked = darkModeInput,
                                      onCheckedChange = { 
                                        darkModeInput = it
                                        sharedPrefs.edit().putBoolean("DARK_MODE", it).apply()
                                    }
                                  )
                            }
                        )

                        AppleSettingsRow(
                            title = "Hands-Free Wake Word",
                            subtitle = "Say 'Hey Jasica' to activate",
                            icon = { Icon(Icons.Rounded.Mic, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.White) },
                            iconBgColor = Color(0xFF007AFF),
                            showDivider = true,
                            isDark = darkModeInput,
                            control = {
                                AppleSwitch(
                                      checked = wakeWordInput,
                                      onCheckedChange = { wakeWordInput = it }
                                  )
                            }
                        )
                        
                        AppleSettingsRow(
                            title = "Save History",
                            subtitle = "Log conversations locally",
                            icon = { Icon(Icons.Rounded.History, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.White) },
                            iconBgColor = Color(0xFF30B0C7),
                            showDivider = true,
                            isDark = darkModeInput,
                            control = {
                                AppleSwitch(
                                      checked = historyLoggingInput,
                                      onCheckedChange = { 
                                        historyLoggingInput = it
                                        sharedPrefs.edit().putBoolean("HISTORY_LOGGING", it).apply()
                                    }
                                  )
                            }
                        )

                        AppleSettingsRow(
                            title = "Instant Offline Actions",
                            subtitle = "Execute hardware commands locally without AI delay",
                            icon = { Icon(Icons.Rounded.Wifi, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.White) },
                            iconBgColor = Color(0xFFFF9500),
                            showDivider = false,
                            isDark = darkModeInput,
                            control = {
                                AppleSwitch(
                                      checked = !onlineModeInput,
                                      onCheckedChange = { 
                                        onlineModeInput = !it
                                        sharedPrefs.edit().putBoolean("ONLINE_MODE_ENABLED", !it).apply()
                                    }
                                  )
                            }
                        )
                    }
                }

                // AI Intelligence Group
                item {
                    AppleSettingsGroup(title = "AI Intelligence", isDark = darkModeInput) {
                        AppleSettingsRow(
                            title = "AI Model Engine",
                            subtitle = "Current: $selectedModel",
                            icon = { Icon(Icons.Rounded.SmartToy, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.White) },
                            iconBgColor = Color(0xFF34C759),
                            showDivider = true,
                            isDark = darkModeInput,
                            control = {
                                var expandedModelMenu by remember { mutableStateOf(false) }
                                val models = listOf("gemini-2.5-flash", "gemini-2.0-flash", "gemini-2.0-pro-exp-02-05")
                                
                                Box {
                                    TextButton(onClick = { expandedModelMenu = true }) {
                                        Text(selectedModel.replace("gemini-", ""), color = Color(0xFF007AFF), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    }
                                    DropdownMenu(
                                        expanded = expandedModelMenu,
                                        onDismissRequest = { expandedModelMenu = false },
                                        modifier = Modifier.background(cardBg)
                                    ) {
                                        models.forEach { modelName ->
                                            DropdownMenuItem(
                                                text = { Text(modelName, color = textPrimary, fontFamily = InterFontFamily) },
                                                onClick = {
                                                    selectedModel = modelName
                                                    sharedPrefs.edit().putString("SELECTED_AI_MODEL", modelName).apply()
                                                    expandedModelMenu = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        )

                        AppleSettingsRow(
                            title = "Use Portfolio API Key",
                            subtitle = "Automatically load dynamic API keys from cloud",
                            icon = { Icon(Icons.Rounded.VpnKey, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.White) },
                            iconBgColor = Color(0xFFAF52DE),
                            showDivider = !adminKeyInput,
                            isDark = darkModeInput,
                            control = {
                                AppleSwitch(
                                      checked = adminKeyInput,
                                      onCheckedChange = { 
                                        adminKeyInput = it
                                        sharedPrefs.edit().putBoolean("USE_ADMIN_PANEL_KEY", it).apply()
                                    }
                                  )
                            }
                        )

                        if (!adminKeyInput) {
                            AppleSettingsRow(
                                title = "Custom API Key",
                                icon = { Icon(Icons.Rounded.VpnKey, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.White) },
                                iconBgColor = Color(0xFF8E8E93),
                                showDivider = false,
                                isDark = darkModeInput,
                                control = {
                                    androidx.compose.foundation.text.BasicTextField(
                                        value = geminiKeyInput,
                                        onValueChange = { 
                                            geminiKeyInput = it
                                            apiKeyInput = it
                                            sharedPrefs.edit().putString("GEMINI_API_KEY", it.trim()).apply()
                                        },
                                        textStyle = androidx.compose.ui.text.TextStyle(
                                            color = textSecondary,
                                            fontSize = 16.sp,
                                            fontFamily = InterFontFamily,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.End
                                        ),
                                        visualTransformation = if (showApiKey) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                                        singleLine = true,
                                        decorationBox = { innerTextField ->
                                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                                                if (geminiKeyInput.isEmpty()) {
                                                    Text("AIzaSy...", color = textSecondary.copy(alpha=0.3f), fontSize = 16.sp)
                                                }
                                                innerTextField()
                                            }
                                        }
                                    )
                                    Text(
                                        text = if (showApiKey) "Hide" else "Show",
                                        color = Color(0xFF007AFF),
                                        fontSize = 15.sp,
                                        modifier = Modifier.clickable { showApiKey = !showApiKey }
                                    )
                                }
                            )
                        }
                    }
                }

                // Voice & Wellness Group
                item {
                    AppleSettingsGroup(title = "Wellness & Voice Engine", isDark = darkModeInput) {
                        AppleSettingsRow(
                            title = "Smart Water Reminder",
                            subtitle = "Periodic spoken hydration alerts",
                            icon = { Icon(Icons.Rounded.Alarm, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.White) },
                            iconBgColor = Color(0xFF007AFF),
                            showDivider = waterReminderInput,
                            isDark = darkModeInput,
                            control = {
                                AppleSwitch(
                                      checked = waterReminderInput,
                                      onCheckedChange = { isChecked ->
                                        if (!isChecked) {
                                            showPasswordDialog = true
                                        } else {
                                            waterReminderInput = true
                                            sharedPrefs.edit().putBoolean("WATER_REMINDER", true).apply()
                                            WaterReminderManager.scheduleAlarm(context, waterInterval)
                                            android.widget.Toast.makeText(context, "Water Reminder Active ($waterInterval min)", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                  )
                            }
                        )

                        if (waterReminderInput) {
                            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Reminder Interval", color = textPrimary, fontSize = 14.sp, fontFamily = InterFontFamily)
                                    Text("$waterInterval min", color = Color(0xFF007AFF), fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = InterFontFamily)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Rounded.Schedule, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                    Spacer(Modifier.width(12.dp))
                                    AppleSlider(
                                        value = waterInterval.toFloat(),
                                        onValueChange = { 
                                            waterInterval = it.toInt()
                                            sharedPrefs.edit().putInt("WATER_REMINDER_INTERVAL", waterInterval).apply()
                                            if (waterReminderInput) {
                                                WaterReminderManager.scheduleAlarm(context, waterInterval)
                                            }
                                        },
                                        valueRange = 10f..120f,
                                        steps = 10,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text("${waterInterval}m", color = textPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, fontFamily = InterFontFamily, modifier = Modifier.width(36.dp), textAlign = androidx.compose.ui.text.style.TextAlign.End)
                                }
                            }
                        }
                    }
                }
                
                // Hardware & Device Configuration Group
                item {
                    AppleSettingsGroup(
                        title = "Hardware Config & Device Names", 
                        footer = "Tap any device to customize its display name, voice commands, and hardware pins.",
                        isDark = darkModeInput
                    ) {
                        DEFAULT_DEVICES.forEachIndexed { index, dev ->
                            var name by remember(dev.id) { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_NAME", dev.defaultName) ?: dev.defaultName) }
                            var onCmd by remember(dev.id) { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_ON_CMD", dev.defaultOnCmd) ?: dev.defaultOnCmd) }
                            var offCmd by remember(dev.id) { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_OFF_CMD", dev.defaultOffCmd) ?: dev.defaultOffCmd) }
                            var pinOn by remember(dev.id) { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_PIN_ON", dev.defaultPinOn) ?: dev.defaultPinOn) }
                            var pinOff by remember(dev.id) { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_PIN_OFF", dev.defaultPinOff) ?: dev.defaultPinOff) }

                            val isExpanded = expandedDevId == dev.id

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { expandedDevId = if (isExpanded) null else dev.id }
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        LucideIconBox(backgroundColor = Color(0xFF636366)) { Icon(Icons.Rounded.SmartToy, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.White) }
                                        Spacer(Modifier.width(14.dp))
                                        Column {
                                            Text(name, color = textPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, fontFamily = InterFontFamily)
                                            Spacer(Modifier.height(2.dp))
                                            Text("Pin [$pinOn/$pinOff] • \"$onCmd\"", color = textSecondary, fontSize = 12.sp, fontFamily = InterFontFamily)
                                        }
                                    }
                                    if (isExpanded) {
                                        Icon(Icons.Outlined.KeyboardArrowUp, contentDescription = null, tint = Color(0xFF007AFF))
                                    } else {
                                        Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null, tint = textSecondary)
                                    }
                                }

                                // Expanded Form Editor
                                AnimatedVisibility(
                                    visible = isExpanded,
                                    enter = fadeIn() + expandVertically(),
                                    exit = fadeOut() + shrinkVertically()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(fieldBg)
                                            .padding(16.dp)
                                    ) {
                                        // 1. Device Name
                                        Text("Device Display Name", color = textSecondary, fontSize = 12.sp, fontFamily = InterFontFamily)
                                        Spacer(Modifier.height(4.dp))
                                        androidx.compose.material3.OutlinedTextField(
                                            value = name,
                                            onValueChange = {
                                                name = it
                                                sharedPrefs.edit().putString("DEV_${dev.id}_NAME", it.trim()).apply()
                                            },
                                            placeholder = { Text(dev.defaultName, color = textSecondary.copy(0.4f)) },
                                            textStyle = androidx.compose.ui.text.TextStyle(color = textPrimary, fontSize = 14.sp, fontFamily = InterFontFamily),
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp),
                                            singleLine = true,
                                            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Color(0xFF007AFF),
                                                unfocusedBorderColor = cardBorder,
                                                focusedContainerColor = cardBg,
                                                unfocusedContainerColor = cardBg
                                            )
                                        )

                                        Spacer(Modifier.height(12.dp))

                                        // 2. Custom Voice Triggers
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("Voice ON Command", color = textSecondary, fontSize = 12.sp, fontFamily = InterFontFamily)
                                                Spacer(Modifier.height(4.dp))
                                                androidx.compose.material3.OutlinedTextField(
                                                    value = onCmd,
                                                    onValueChange = {
                                                        onCmd = it
                                                        sharedPrefs.edit().putString("DEV_${dev.id}_ON_CMD", it.trim().lowercase(java.util.Locale.ROOT)).apply()
                                                    },
                                                    placeholder = { Text(dev.defaultOnCmd, color = textSecondary.copy(0.4f)) },
                                                    textStyle = androidx.compose.ui.text.TextStyle(color = textPrimary, fontSize = 14.sp, fontFamily = InterFontFamily),
                                                    modifier = Modifier.fillMaxWidth(),
                                                    shape = RoundedCornerShape(8.dp),
                                                    singleLine = true,
                                                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                                        focusedBorderColor = Color(0xFF007AFF),
                                                        unfocusedBorderColor = cardBorder,
                                                        focusedContainerColor = cardBg,
                                                        unfocusedContainerColor = cardBg
                                                    )
                                                )
                                            }
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("Voice OFF Command", color = textSecondary, fontSize = 12.sp, fontFamily = InterFontFamily)
                                                Spacer(Modifier.height(4.dp))
                                                androidx.compose.material3.OutlinedTextField(
                                                    value = offCmd,
                                                    onValueChange = {
                                                        offCmd = it
                                                        sharedPrefs.edit().putString("DEV_${dev.id}_OFF_CMD", it.trim().lowercase(java.util.Locale.ROOT)).apply()
                                                    },
                                                    placeholder = { Text(dev.defaultOffCmd, color = textSecondary.copy(0.4f)) },
                                                    textStyle = androidx.compose.ui.text.TextStyle(color = textPrimary, fontSize = 14.sp, fontFamily = InterFontFamily),
                                                    modifier = Modifier.fillMaxWidth(),
                                                    shape = RoundedCornerShape(8.dp),
                                                    singleLine = true,
                                                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                                        focusedBorderColor = Color(0xFF007AFF),
                                                        unfocusedBorderColor = cardBorder,
                                                        focusedContainerColor = cardBg,
                                                        unfocusedContainerColor = cardBg
                                                    )
                                                )
                                            }
                                        }

                                        Spacer(Modifier.height(12.dp))

                                        // 3. Hardware Pin Codes
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("Pin ON Char", color = textSecondary, fontSize = 12.sp, fontFamily = InterFontFamily)
                                                Spacer(Modifier.height(4.dp))
                                                androidx.compose.material3.OutlinedTextField(
                                                    value = pinOn,
                                                    onValueChange = {
                                                        pinOn = it.take(5)
                                                        sharedPrefs.edit().putString("DEV_${dev.id}_PIN_ON", it).apply()
                                                    },
                                                    textStyle = androidx.compose.ui.text.TextStyle(color = textPrimary, fontSize = 14.sp, fontFamily = InterFontFamily),
                                                    modifier = Modifier.fillMaxWidth(),
                                                    shape = RoundedCornerShape(8.dp),
                                                    singleLine = true,
                                                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                                        focusedBorderColor = Color(0xFF007AFF),
                                                        unfocusedBorderColor = cardBorder,
                                                        focusedContainerColor = cardBg,
                                                        unfocusedContainerColor = cardBg
                                                    )
                                                )
                                            }
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("Pin OFF Char", color = textSecondary, fontSize = 12.sp, fontFamily = InterFontFamily)
                                                Spacer(Modifier.height(4.dp))
                                                androidx.compose.material3.OutlinedTextField(
                                                    value = pinOff,
                                                    onValueChange = {
                                                        pinOff = it.take(5)
                                                        sharedPrefs.edit().putString("DEV_${dev.id}_PIN_OFF", it).apply()
                                                    },
                                                    textStyle = androidx.compose.ui.text.TextStyle(color = textPrimary, fontSize = 14.sp, fontFamily = InterFontFamily),
                                                    modifier = Modifier.fillMaxWidth(),
                                                    shape = RoundedCornerShape(8.dp),
                                                    singleLine = true,
                                                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                                        focusedBorderColor = Color(0xFF007AFF),
                                                        unfocusedBorderColor = cardBorder,
                                                        focusedContainerColor = cardBg,
                                                        unfocusedContainerColor = cardBg
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }

                                if (index != DEFAULT_DEVICES.size - 1) {
                                    androidx.compose.material3.HorizontalDivider(modifier = Modifier.padding(start = 60.dp), color = cardBorder, thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                }

                // Danger & Reset Group
                item {
                    AppleSettingsGroup(title = "Reset & Maintenance", isDark = darkModeInput) {
                        AppleSettingsRow(
                            title = "Reset Devices to Default",
                            subtitle = "Restore 1st LED to 6th LED",
                            icon = { Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.White) },
                            iconBgColor = Color(0xFFFF9500),
                            showDivider = false,
                            isDark = darkModeInput,
                            onClick = {
                                showResetConfirmDialog = true
                            }
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(40.dp))
                }
            }
        }

        // Apple Style Restart Dialog
        if (showRestartDialog) {
            AppleRestartDialog(
                onDismiss = {
                    showRestartDialog = false
                    onDismiss()
                },
                onRestart = {
                    showRestartDialog = false
                    restartApp(context)
                }
            )
        }

        // Reset Confirmation Dialog
        if (showResetConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showResetConfirmDialog = false },
                title = { Text("Reset Hardware Config?", color = textPrimary, fontWeight = FontWeight.Bold, fontFamily = InterFontFamily) },
                text = { Text("All device names, commands, and pins will be reset to default values (1st LED – 6th LED).", color = textSecondary, fontSize = 14.sp, fontFamily = InterFontFamily) },
                containerColor = cardBg,
                shape = RoundedCornerShape(20.dp),
                confirmButton = {
                    Button(
                        onClick = {
                            DEFAULT_DEVICES.forEach { dev ->
                                sharedPrefs.edit()
                                    .remove("DEV_${dev.id}_NAME")
                                    .remove("DEV_${dev.id}_ON_CMD")
                                    .remove("DEV_${dev.id}_OFF_CMD")
                                    .remove("DEV_${dev.id}_PIN_ON")
                                    .remove("DEV_${dev.id}_PIN_OFF")
                                    .apply()
                            }
                            showResetConfirmDialog = false
                            android.widget.Toast.makeText(context, "Devices Reset to Defaults", android.widget.Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B30)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Reset", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = InterFontFamily)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetConfirmDialog = false }) {
                        Text("Cancel", color = textSecondary, fontFamily = InterFontFamily)
                    }
                }
            )
        }

        // Water reminder password dialog
        if (showPasswordDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showPasswordDialog = false
                    passwordError = false
                    passwordInput = ""
                },
                title = { Text("Enter Admin Password", color = textPrimary, fontWeight = FontWeight.Bold, fontFamily = InterFontFamily) },
                text = {
                    Column {
                        Text("A password is required to turn off the water reminder.", color = textSecondary, fontSize = 14.sp, fontFamily = InterFontFamily)
                        Spacer(modifier = Modifier.height(16.dp))
                        androidx.compose.material3.OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it; passwordError = false },
                            label = { Text("Password", color = textSecondary, fontFamily = InterFontFamily) },
                            isError = passwordError,
                            textStyle = androidx.compose.ui.text.TextStyle(color = textPrimary, fontFamily = InterFontFamily),
                            shape = RoundedCornerShape(12.dp),
                            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFF9500),
                                unfocusedBorderColor = cardBorder,
                                focusedContainerColor = fieldBg,
                                unfocusedContainerColor = fieldBg
                            )
                        )
                        if (passwordError) {
                            Text("Incorrect password", color = Color(0xFFFF3B30), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp), fontFamily = InterFontFamily)
                        }
                    }
                },
                containerColor = cardBg,
                shape = RoundedCornerShape(24.dp),
                confirmButton = {
                    Button(
                        onClick = {
                            val correctPassword = sharedPrefs.getString("WATER_REMINDER_PASSWORD", "0000") ?: "0000"
                            if (passwordInput == correctPassword) {
                                waterReminderInput = false
                                sharedPrefs.edit().putBoolean("WATER_REMINDER", false).apply()
                                WaterReminderManager.stopAlarm(context)
                                android.widget.Toast.makeText(context, "Water Reminder Disabled", android.widget.Toast.LENGTH_SHORT).show()
                                showPasswordDialog = false
                                passwordError = false
                                passwordInput = ""
                            } else {
                                passwordError = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9500)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Confirm", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = InterFontFamily)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        showPasswordDialog = false
                        passwordError = false
                        passwordInput = ""
                    }) {
                        Text("Cancel", color = textSecondary, fontFamily = InterFontFamily)
                    }
                }
            )
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun DeviceSelectionDialog(
    pairedDevices: List<BluetoothDevice>,
    availableDevices: List<BluetoothDevice>,
    isScanning: Boolean,
    connectedDeviceAddress: String?,
    hazeState: dev.chrisbanes.haze.HazeState?,
    onDeviceSelect: (BluetoothDevice) -> Unit,
    onScanTap: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (hazeState != null) Modifier.hazeEffect(
                    state = hazeState,
                    style = dev.chrisbanes.haze.HazeStyle(
                        blurRadius = 24.dp,
                        tint = dev.chrisbanes.haze.HazeTint(Color.White.copy(alpha=0.4f))
                    )
                ) else Modifier.background(Color.White.copy(alpha = 0.5f))
            )
            .pointerInput(Unit) { detectTapGestures(onTap = { onDismiss() }) },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFF2F2F7).copy(alpha = 0.95f))
                .pointerInput(Unit) { detectTapGestures { /* consume taps */ } }
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 16.dp, start = 20.dp, end = 20.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Bluetooth",
                        color = Color.Black,
                        fontFamily = InterFontFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    )
                    if (isScanning) {
                        val infiniteTransition = rememberInfiniteTransition(label = "radar")
                        val scale by infiniteTransition.animateFloat(
                            initialValue = 0.2f,
                            targetValue = 2.5f,
                            animationSpec = infiniteRepeatable(tween(1200, easing = LinearOutSlowInEasing), RepeatMode.Restart),
                            label = "scale"
                        )
                        val alpha by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 0f,
                            animationSpec = infiniteRepeatable(tween(1200, easing = LinearOutSlowInEasing), RepeatMode.Restart),
                            label = "alpha"
                        )
                        Box(modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.Bluetooth, contentDescription = null, tint = Color(0xFF007AFF), modifier = Modifier.size(16.dp))
                            Box(modifier = Modifier
                                .matchParentSize()
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                    this.alpha = alpha
                                }
                                .border(1.5.dp, Color(0xFF007AFF), CircleShape)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF007AFF).copy(alpha = 0.1f))
                                .clickable { onScanTap() }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("Scan", color = Color(0xFF007AFF), fontFamily = InterFontFamily, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (pairedDevices.isNotEmpty()) {
                        item {
                            Text(
                                "MY DEVICES",
                                color = Color.Black.copy(alpha = 0.5f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = InterFontFamily,
                                modifier = Modifier.padding(bottom = 6.dp, start = 8.dp)
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White)
                            ) {
                                pairedDevices.forEachIndexed { index, device ->
                                    val name = try { device.name ?: "Unknown Device" } catch (e: SecurityException) { "Unknown Device" }
                                    DeviceListItem(name, device.address, device.address == connectedDeviceAddress) { onDeviceSelect(device) }
                                    if (index < pairedDevices.size - 1) {
                                        HorizontalDivider(modifier = Modifier.padding(start = 16.dp), color = Color.Black.copy(alpha = 0.05f), thickness = 1.dp)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            "OTHER DEVICES",
                            color = Color.Black.copy(alpha = 0.5f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = InterFontFamily,
                            modifier = Modifier.padding(bottom = 6.dp, start = 8.dp, top = if (pairedDevices.isEmpty()) 0.dp else 8.dp)
                        )
                    }

                    if (availableDevices.isEmpty() && !isScanning) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                                Text("No devices found.", color = Color.Black.copy(alpha = 0.4f), fontSize = 15.sp, fontFamily = InterFontFamily)
                            }
                        }
                    } else if (availableDevices.isNotEmpty() || isScanning) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White)
                            ) {
                                availableDevices.forEachIndexed { index, device ->
                                    val name = try { device.name ?: "Unknown Signal" } catch (e: SecurityException) { "Unknown Signal" }
                                    DeviceListItem(name, device.address, device.address == connectedDeviceAddress) { onDeviceSelect(device) }
                                    if (index < availableDevices.size - 1 || isScanning) {
                                        HorizontalDivider(modifier = Modifier.padding(start = 16.dp), color = Color.Black.copy(alpha = 0.05f), thickness = 1.dp)
                                    }
                                }
                                
                                if (isScanning) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val infiniteTransition = rememberInfiniteTransition(label="scan_text")
                                        val alpha by infiniteTransition.animateFloat(
                                            initialValue = 0.3f,
                                            targetValue = 1f,
                                            animationSpec = infiniteRepeatable(
                                                animation = tween(800, easing = LinearEasing),
                                                repeatMode = RepeatMode.Reverse
                                            ),
                                            label="alpha"
                                        )
                                        Text(
                                            "Searching...",
                                            color = Color.Black.copy(alpha = alpha),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            fontFamily = InterFontFamily,
                                            modifier = Modifier.weight(1f)
                                        )
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            color = Color.Black.copy(alpha=0.3f),
                                            strokeWidth = 2.dp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Close Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Close", color = Color(0xFF007AFF), fontFamily = InterFontFamily, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun DeviceListItem(name: String, address: String, isConnected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = InterFontFamily,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
        if (isConnected) {
            Text(
                text = "Connected",
                color = Color.Black.copy(alpha = 0.5f),
                fontSize = 15.sp,
                fontFamily = InterFontFamily,
                modifier = Modifier.padding(end = 8.dp)
            )
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = Color(0xFF007AFF),
                modifier = Modifier.size(18.dp)
            )
        } else {
            Text(
                text = "Not Connected",
                color = Color.Black.copy(alpha = 0.3f),
                fontSize = 15.sp,
                fontFamily = InterFontFamily
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Voice Calibration Screen (Apple Design System / Cupertino HIG)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun VoiceCalibrationScreen(
    currentPhraseIndex: Int,
    recognizedText: String,
    isListening: Boolean,
    onMicTap: () -> Unit,
    onSkip: () -> Unit
) {
    val phrases = listOf("Turn off the light", "Turn on all", "Turn on the PC")
    val totalSteps = phrases.size
    val isComplete = currentPhraseIndex >= totalSteps

    // Infinite transition for fluid Apple animations (Siri pulse and audio waveforms)
    val infiniteTransition = rememberInfiniteTransition(label = "AppleVoiceSetup")
    
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    val wave1 by infiniteTransition.animateFloat(
        initialValue = 8f, targetValue = 28f,
        animationSpec = infiniteRepeatable(tween(400, easing = LinearEasing), RepeatMode.Reverse),
        label = "Wave1"
    )
    val wave2 by infiniteTransition.animateFloat(
        initialValue = 18f, targetValue = 38f,
        animationSpec = infiniteRepeatable(tween(550, easing = LinearEasing), RepeatMode.Reverse),
        label = "Wave2"
    )
    val wave3 by infiniteTransition.animateFloat(
        initialValue = 12f, targetValue = 32f,
        animationSpec = infiniteRepeatable(tween(480, easing = LinearEasing), RepeatMode.Reverse),
        label = "Wave3"
    )
    val wave4 by infiniteTransition.animateFloat(
        initialValue = 22f, targetValue = 42f,
        animationSpec = infiniteRepeatable(tween(620, easing = LinearEasing), RepeatMode.Reverse),
        label = "Wave4"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000)) // Deep Apple Space Black
            .clickable(enabled = false) {} // Prevent backdrop pass-through
    ) {
        // ── Apple Siri / Aurora Ambient Background Glow ───────────────────────
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.TopCenter)
                .offset(y = 60.dp)
                .blur(90.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color(0x355856D6), // Apple Indigo
                            Color(0x20007AFF), // Apple Blue
                            Color(0x10AF52DE), // Apple Purple
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            // ── Apple Top Segmented Stepper Indicator ─────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until totalSteps) {
                    val isStepDone = i < currentPhraseIndex
                    val isStepCurrent = i == currentPhraseIndex && !isComplete
                    
                    val segmentColor by animateColorAsState(
                        targetValue = when {
                            isComplete || isStepDone -> Color(0xFF30D158) // Apple Green
                            isStepCurrent -> Color(0xFF007AFF)          // Apple Blue
                            else -> Color(0x20FFFFFF)                  // Translucent Track
                        },
                        animationSpec = tween(400),
                        label = "SegmentColor"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(segmentColor)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Sub-header / Step Counter ─────────────────────────────────────
            Text(
                text = if (!isComplete) "STEP ${currentPhraseIndex + 1} OF $totalSteps".uppercase() else "COMPLETED",
                color = if (!isComplete) Color(0xFF8E8E93) else Color(0xFF30D158),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.2.sp,
                fontFamily = InterFontFamily
            )

            Spacer(Modifier.height(8.dp))

            // ── Apple Title & Subtitle ─────────────────────────────────────────
            Text(
                text = if (!isComplete) "Set Up Voice Control" else "Voice Setup Complete",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
                fontFamily = InterFontFamily,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = if (!isComplete)
                    "Say each command clearly so Jasica can calibrate to your natural voice."
                else
                    "Jasica is now calibrated to your voice and ready to control your smart home.",
                color = Color(0x99FFFFFF),
                fontSize = 15.sp,
                lineHeight = 21.sp,
                fontFamily = InterFontFamily,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(32.dp))

            // ── Central Frosted Glass Squircle Card ───────────────────────────
            if (!isComplete) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0x18FFFFFF),
                                    Color(0x0CFFFFFF)
                                )
                            )
                        )
                        .border(
                            width = 0.75.dp,
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color(0x40FFFFFF),
                                    Color(0x08FFFFFF)
                                )
                            ),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .padding(vertical = 28.dp, horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Badge: "SAY TO JASICA"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x1A007AFF))
                                .border(0.5.dp, Color(0x40007AFF), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF0A84FF))
                                )
                                Text(
                                    text = "SAY TO JASICA",
                                    color = Color(0xFF64D2FF),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    fontFamily = InterFontFamily
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        // Target Spoken Phrase in High-Contrast Typography
                        Text(
                            text = "\"${phrases[currentPhraseIndex]}\"",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = InterFontFamily,
                            textAlign = TextAlign.Center,
                            letterSpacing = (-0.3).sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(Modifier.height(24.dp))

                        // Real-time Soundwave / Status Pill
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color(0x14000000))
                                .border(0.5.dp, Color(0x18FFFFFF), RoundedCornerShape(18.dp))
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (recognizedText.isNotBlank()) {
                                Text(
                                    text = recognizedText,
                                    color = Color(0xFF30D158),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = InterFontFamily,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            } else if (isListening) {
                                // Animated Apple-style Waveform Bars
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(Modifier.width(3.dp).height(wave1.dp).clip(CircleShape).background(Color(0xFF64D2FF)))
                                    Box(Modifier.width(3.dp).height(wave2.dp).clip(CircleShape).background(Color(0xFF0A84FF)))
                                    Box(Modifier.width(3.dp).height(wave4.dp).clip(CircleShape).background(Color(0xFF5E5CE6)))
                                    Box(Modifier.width(3.dp).height(wave3.dp).clip(CircleShape).background(Color(0xFFBF5AF2)))
                                    Box(Modifier.width(3.dp).height(wave1.dp).clip(CircleShape).background(Color(0xFF64D2FF)))
                                }
                            } else {
                                Text(
                                    text = "Tap the mic button below to start",
                                    color = Color(0x60FFFFFF),
                                    fontSize = 14.sp,
                                    fontFamily = InterFontFamily
                                )
                            }
                        }
                    }
                }
            } else {
                // ── Apple Success State Card ──────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0x1F30D158),
                                    Color(0x0A30D158)
                                )
                            )
                        )
                        .border(
                            width = 0.75.dp,
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color(0x6030D158),
                                    Color(0x1030D158)
                                )
                            ),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .padding(vertical = 36.dp, horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Glowing Apple Green Checkmark Ring
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color(0x2030D158))
                                .border(1.5.dp, Color(0xFF30D158), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = "Complete",
                                tint = Color(0xFF30D158),
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        Text(
                            text = "Ready for Hands-Free Control",
                            color = Color.White,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = InterFontFamily,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "Your microphone calibration has been saved successfully.",
                            color = Color(0x99FFFFFF),
                            fontSize = 14.sp,
                            fontFamily = InterFontFamily,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // ── Apple Siri Glowing Microphone Button & Action Trigger ─────────
            if (!isComplete) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(110.dp)
                ) {
                    // Siri Concentric Animated Pulse Ring
                    if (isListening) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(
                                            Color(0x40007AFF),
                                            Color(0x105E5CE6),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                    }

                    // Main Apple Tactile Mic Button
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(
                                if (isListening)
                                    Brush.linearGradient(
                                        listOf(
                                            Color(0xFF0A84FF),
                                            Color(0xFF5E5CE6)
                                        )
                                    )
                                else
                                    Brush.verticalGradient(
                                        listOf(
                                            Color(0x24FFFFFF),
                                            Color(0x10FFFFFF)
                                        )
                                    )
                            )
                            .border(
                                width = 1.dp,
                                brush = if (isListening)
                                    Brush.linearGradient(listOf(Color(0xFF64D2FF), Color(0xFFBF5AF2)))
                                else
                                    Brush.verticalGradient(listOf(Color(0x40FFFFFF), Color(0x15FFFFFF))),
                                shape = CircleShape
                            )
                            .clickable { onMicTap() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.mic),
                            contentDescription = "Microphone",
                            tint = if (isListening) Color.White else Color(0xFFF2F2F7),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Status pill under microphone
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x14FFFFFF))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (isListening) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF30D158))
                            )
                        }
                        Text(
                            text = if (isListening) "Listening..." else "Tap to Speak",
                            color = if (isListening) Color(0xFF30D158) else Color(0x99FFFFFF),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = InterFontFamily
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Apple "Set Up Later" Text Button
                TextButton(
                    onClick = onSkip,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "Set Up Later",
                        color = Color(0xFF8E8E93),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = InterFontFamily
                    )
                }
            } else {
                // Apple Full-Width "Continue" Primary Pill Button
                Button(
                    onClick = onSkip,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF007AFF) // Apple System Blue
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Continue",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = InterFontFamily
                    )
                }

                Spacer(Modifier.height(16.dp))
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Onboarding Walkthrough Screen
// ─────────────────────────────────────────────────────────────────────────────

data class OnboardingPageInfo(val title: String, val subtitle: String, val iconRes: Int?, val image: Int?)

@Composable
fun OnboardingScreen(onDismiss: () -> Unit) {
    val pages = listOf(
        OnboardingPageInfo("Welcome to Jasica", "Your intelligent voice assistant for complete digital and hardware control.", null, R.drawable.jasica),
        OnboardingPageInfo("Voice Commands", "Say a command or tap the mic to control your lights, PC, AC, and more natively.", R.drawable.fluentui_system_icons_mic, null),
        OnboardingPageInfo("Manual Override", "Access the quick-switch panel from the top right home icon to toggle hardware without speaking.", R.drawable.fluentui_system_icons_home, null),
        OnboardingPageInfo("Stay Connected", "Pair your Bluetooth smart hub via the top right icon to get started.", R.drawable.fluentui_system_icons_phone_laptop, null)
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
                    } else if (info.iconRes != null) {
                        Box(
                            modifier = Modifier.size(140.dp).background(JasicaCardBg, CircleShape).border(1.dp, JasicaWhite.copy(0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(painterResource(info.iconRes), contentDescription = null, modifier = Modifier.size(60.dp), tint = Color(0xFF0A84FF))
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
                    val color = if (isSelected) Color.White else Color.White.copy(alpha = 0.3f)
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A84FF)),
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
            connectedDeviceAddress = null,
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
            showVoiceCalibration = false,
            calibrationIndex = 0,
            calibrationRecognizedText = "",
            showArduinoCode = false,
            micError = null,
            onDismissMicError = {},
            currentApiKey = "",
            currentModel = AiModelsList[0],
            isWakeWordMode = false,
            isAdvancedAiMode = false,
            isOnlineModeEnabled = false,
            useAdminPanelKey = true,
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
            onArduinoCodeTap = {},
            onDismissArduinoCode = {},
            onDismissOnboarding = {},
            onDismissCalibration = {},
            onSaveSettings = { _, _, _ -> },
            onActionCardTap = {},
            onSendRawCommand = {}
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Arduino Code Screen
// ─────────────────────────────────────────────────────────────────────────────

private val ARDUINO_UNO_CODE = """
// ═══════════════════════════════════════════════════════════════
//  JASICA Controller — Arduino UNO + HC-05 Bluetooth Module
//  Board  : Arduino UNO (Clone)
//  Module : HC-05 (connected to Software Serial pins 2 & 3)
//  Devices: Pins 8–13 (relays / MOSFETs / LEDs)
//  Author : Joy Kumbhakar (Bristi's System)
// ═══════════════════════════════════════════════════════════════

#include <SoftwareSerial.h>

// HC-05 RX → Arduino pin 2 | HC-05 TX → Arduino pin 3
SoftwareSerial BT(2, 3);

// ── Device Pin Map ────────────────────────────────────────────
const int PIN_PC    = 8;   // a/A — PC / Computer
const int PIN_RGB   = 9;   // b/B — RGB Lights
const int PIN_LIGHT = 10;  // c/C — Room Light
const int PIN_PLUG  = 11;  // d/D — Smart Plug
const int PIN_FAN   = 12;  // e/E — Ceiling Fan
const int PIN_AC    = 13;  // f/F — Air Conditioner

int allPins[] = { PIN_PC, PIN_RGB, PIN_LIGHT, PIN_PLUG, PIN_FAN, PIN_AC };
const int TOTAL = 6;

void setup() {
  Serial.begin(9600);
  BT.begin(9600);

  for (int i = 0; i < TOTAL; i++) {
    pinMode(allPins[i], OUTPUT);
    digitalWrite(allPins[i], LOW);
  }
  Serial.println("JASICA UNO Ready.");
}

void loop() {
  if (BT.available()) {
    String cmd = BT.readStringUntil('\n');
    cmd.trim();
    processCommand(cmd);
  }
}

void processCommand(String cmd) {
  // ── All ON / All OFF ────────────────────────────────────────
  if (cmd == "on") {
    for (int i = 0; i < TOTAL; i++) digitalWrite(allPins[i], HIGH);
  } else if (cmd == "off") {
    for (int i = 0; i < TOTAL; i++) digitalWrite(allPins[i], LOW);

  // ── Mood (RGB + Light) ──────────────────────────────────────
  } else if (cmd == "mood") {
    for (int i = 0; i < TOTAL; i++) digitalWrite(allPins[i], LOW);
    digitalWrite(PIN_RGB, HIGH);
    digitalWrite(PIN_LIGHT, HIGH);

  // ── Individual Devices ──────────────────────────────────────
  } else if (cmd == "a") { digitalWrite(PIN_PC,    HIGH); }
  else if (cmd == "A")   { digitalWrite(PIN_PC,    LOW);  }
  else if (cmd == "b")   { digitalWrite(PIN_RGB,   HIGH); }
  else if (cmd == "B")   { digitalWrite(PIN_RGB,   LOW);  }
  else if (cmd == "c")   { digitalWrite(PIN_LIGHT, HIGH); }
  else if (cmd == "C")   { digitalWrite(PIN_LIGHT, LOW);  }
  else if (cmd == "d")   { digitalWrite(PIN_PLUG,  HIGH); }
  else if (cmd == "D")   { digitalWrite(PIN_PLUG,  LOW);  }
  else if (cmd == "e")   { digitalWrite(PIN_FAN,   HIGH); }
  else if (cmd == "E")   { digitalWrite(PIN_FAN,   LOW);  }
  else if (cmd == "f")   { digitalWrite(PIN_AC,    HIGH); }
  else if (cmd == "F")   { digitalWrite(PIN_AC,    LOW);  }
}
""".trimIndent()

private val ESP32_CODE = """
// ═══════════════════════════════════════════════════════════════
//  JASICA Controller — ESP32 DevKit v1
//  Board  : ESP32 Dev Module (select in Arduino IDE)
//  BLE    : Built-in Bluetooth Classic (BluetoothSerial)
//  Devices: GPIO 13, 12, 14, 27, 26, 25
//  Author : Joy Kumbhakar (Bristi's System)
// ═══════════════════════════════════════════════════════════════

#include "BluetoothSerial.h"

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Enable in Arduino IDE SDK config.
#endif

BluetoothSerial BT;

// ── Device Pin Map ────────────────────────────────────────────
const int PIN_PC    = 13;  // a/A — PC / Computer
const int PIN_RGB   = 12;  // b/B — RGB Lights
const int PIN_LIGHT = 14;  // c/C — Room Light
const int PIN_PLUG  = 27;  // d/D — Smart Plug
const int PIN_FAN   = 26;  // e/E — Ceiling Fan
const int PIN_AC    = 25;  // f/F — Air Conditioner

int allPins[] = { PIN_PC, PIN_RGB, PIN_LIGHT, PIN_PLUG, PIN_FAN, PIN_AC };
const int TOTAL = 6;

void setup() {
  Serial.begin(115200);
  BT.begin("JASICA_ESP32");  // Bluetooth device name
  Serial.println("JASICA ESP32 Ready. Waiting for connection...");

  for (int i = 0; i < TOTAL; i++) {
    pinMode(allPins[i], OUTPUT);
    digitalWrite(allPins[i], LOW);
  }
}

void loop() {
  if (BT.available()) {
    String cmd = BT.readStringUntil('\n');
    cmd.trim();
    processCommand(cmd);
  }
}

void processCommand(String cmd) {
  // ── All ON / All OFF ────────────────────────────────────────
  if (cmd == "on") {
    for (int i = 0; i < TOTAL; i++) digitalWrite(allPins[i], HIGH);
  } else if (cmd == "off") {
    for (int i = 0; i < TOTAL; i++) digitalWrite(allPins[i], LOW);

  // ── Mood (RGB + Light) ──────────────────────────────────────
  } else if (cmd == "mood") {
    for (int i = 0; i < TOTAL; i++) digitalWrite(allPins[i], LOW);
    digitalWrite(PIN_RGB, HIGH);
    digitalWrite(PIN_LIGHT, HIGH);

  // ── Individual Devices ──────────────────────────────────────
  } else if (cmd == "a") { digitalWrite(PIN_PC,    HIGH); }
  else if (cmd == "A")   { digitalWrite(PIN_PC,    LOW);  }
  else if (cmd == "b")   { digitalWrite(PIN_RGB,   HIGH); }
  else if (cmd == "B")   { digitalWrite(PIN_RGB,   LOW);  }
  else if (cmd == "c")   { digitalWrite(PIN_LIGHT, HIGH); }
  else if (cmd == "C")   { digitalWrite(PIN_LIGHT, LOW);  }
  else if (cmd == "d")   { digitalWrite(PIN_PLUG,  HIGH); }
  else if (cmd == "D")   { digitalWrite(PIN_PLUG,  LOW);  }
  else if (cmd == "e")   { digitalWrite(PIN_FAN,   HIGH); }
  else if (cmd == "E")   { digitalWrite(PIN_FAN,   LOW);  }
  else if (cmd == "f")   { digitalWrite(PIN_AC,    HIGH); }
  else if (cmd == "F")   { digitalWrite(PIN_AC,    LOW);  }
}
""".trimIndent()

@Composable
fun ArduinoCodeScreen(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current

    var selectedBoard by remember { mutableStateOf(0) }
    var copied by remember { mutableStateOf(false) }

    val boards = listOf("Arduino UNO", "ESP32 Dev v1")
    val codes = listOf(ARDUINO_UNO_CODE, ESP32_CODE)

    LaunchedEffect(copied) {
        if (copied) {
            kotlinx.coroutines.delay(2000)
            copied = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(enabled = false) {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 54.dp, start = 16.dp, end = 16.dp, bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp, start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Firmware",
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily
                )
                TextButton(onClick = onDismiss) {
                    Text("Done", color = Color(0xFF0A84FF), fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1C1C1E))
                    .padding(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                boards.forEachIndexed { index, name ->
                    val isSelected = selectedBoard == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) Color(0xFF636366) else Color.Transparent)
                            .clickable { selectedBoard = index },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            fontFamily = InterFontFamily
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1C1C1E))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            ) {
                androidx.compose.foundation.text.selection.SelectionContainer {
                    Text(
                        text = codes[selectedBoard],
                        color = Color(0xFF5AC8FA),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .horizontalScroll(rememberScrollState())
                            .padding(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(codes[selectedBoard]))
                    copied = true
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (copied) Color(0xFF34C759) else Color(0xFF0A84FF)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (copied) "COPIED TO CLIPBOARD" else "COPY FIRMWARE CODE",
                    color = Color.White,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Dashboard Content
// ─────────────────────────────────────────────────────────────────────────────

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
data class UpdateNotification(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val primaryButtonText: String,
    val primaryButtonUrl: String,
    val secondaryButtonText: String
)

@Composable
fun AppleMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            }
            .background(if (isPressed) Color.Black.copy(alpha=0.1f) else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(text, color = Color.Black, fontSize = 16.sp, fontFamily = InterFontFamily, fontWeight = FontWeight.Medium)
    }
}
