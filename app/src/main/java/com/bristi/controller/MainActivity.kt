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
import org.json.JSONObject
import coil.compose.AsyncImage
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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

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
        DEFAULT_DEVICES.forEach { dev ->
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
                    "হ্যাঁ বৃষ্টি, আমি লাইট অন করে দিচ্ছি। তোমার আর কিছু অন করতে লাগবে?",
                    "ঠিক আছে বস, লাইট জ্বালিয়ে দিলাম।",
                    "লাইট অন করা হয়েছে বৃষ্টি বস!",
                    "অবশ্যই বৃষ্টি, লাইট অন করে দিচ্ছি!"
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
        listOf("a", "b", "c", "d", "e", "f").forEach { id ->
            deviceStates[id] = sharedPrefs.getBoolean("DEV_$id", false)
        }

        // Check if user has seen setup
        val hasSeenCalibration = sharedPrefs.getBoolean("SEEN_CALIBRATION", false)
        val hasSeenOnboarding = sharedPrefs.getBoolean("SEEN_ONBOARDING", false)
        
        if (!hasSeenCalibration) {
            showVoiceCalibration.value = true
        } else if (!hasSeenOnboarding) {
            showOnboarding.value = true
        }

        // Set Water Reminder Default ON
        if (!sharedPrefs.contains("WATER_REMINDER")) {
            sharedPrefs.edit().putBoolean("WATER_REMINDER", true).apply()
            WaterReminderManager.scheduleNextAlarm(this)
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
        
        setComponentState(favDiComponent, enableFavDi)
        setComponentState(sonaDiComponent, enableSonaDi)
        setComponentState(tithiComponent, enableTithi)
        setComponentState(jijuDidiComponent, enableJijuDidi)
        setComponentState(qweenComponent, enableQween)
        setComponentState(thinkingComponent, enableThinking)
        setComponentState(thinking2Component, enableThinking2)
        setComponentState(defaultComponent, enableDefault)
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
                val originalText = matches?.firstOrNull() ?: ""
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
                        restartWakeWordLoop()
                    }
                } else {
                    // Regular listening mode — strip wake word prefix if user said it
                    val cleaned = originalText.replace(Regex("(?i)h[ei]y?\\s+(jasica|jessica|jessika|jasika|jesica|jazica)"), "").trim()
                    val finalText = if (cleaned.isNotEmpty()) cleaned else originalText
                    if (finalText.isNotEmpty()) {
                        routeVoiceCommand(finalText)
                    } else {
                        appState.value = AppState.IDLE
                        triggerWakeWordLoopIfEnabled()
                    }
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
            putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayListOf("bn-IN"))
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
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
            uiChatHistory.add(ChatMessage(isUser = true, text = spokenText, time = getCurrentTimeString()))
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
                okhttp3.OkHttpClient().newCall(request).execute()
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
            uiChatHistory.add(ChatMessage(isUser = false, text = reply, time = getCurrentTimeString()))
            aiResponseText.value = reply
            appState.value = AppState.SPEAKING
            speakMultilingual(reply, "JASICA_REPLY")
        }
    }



    private fun sendToGemini(prompt: String, apiKey: String) {
        if (!isNetworkAvailable()) {
            runOnUiThread {
                val msg = "I am offline right now. Please check your internet connection."
                uiChatHistory.add(ChatMessage(isUser = false, text = msg, time = getCurrentTimeString()))
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
            lifecycleScope.launch(Dispatchers.IO) { try { classicOutStream?.write(payload); classicOutStream?.flush() } catch (e: IOException) { disconnectAll() } }
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

    val haptic = LocalHapticFeedback.current
    Box(modifier = Modifier.fillMaxSize()) {
        
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
                    var showMenu by remember { mutableStateOf(false) }

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
                        
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(Color(0xFF1E1E2E))
                        ) {
                            DropdownMenuItem(
                                text = { Text("Manual Controls", color = Color.White, fontFamily = InterFontFamily) },
                                onClick = { showMenu = false; onManualControlsTap() },
                                leadingIcon = { Icon(Icons.Outlined.Home, contentDescription = null, tint = JasicaWhite) }
                            )
                            DropdownMenuItem(
                                text = { Text("Chat History", color = Color.White, fontFamily = InterFontFamily) },
                                onClick = { showMenu = false; onHistoryTap() },
                                leadingIcon = { Icon(Icons.Outlined.History, contentDescription = null, tint = JasicaWhite) }
                            )
                            DropdownMenuItem(
                                text = { Text("Arduino Code", color = Color.White, fontFamily = InterFontFamily) },
                                onClick = { showMenu = false; onArduinoCodeTap() },
                                leadingIcon = { Icon(painterResource(id = android.R.drawable.ic_menu_edit), contentDescription = null, tint = JasicaWhite) }
                            )
                            DropdownMenuItem(
                                text = { Text("Settings", color = Color.White, fontFamily = InterFontFamily) },
                                onClick = { showMenu = false; onSettingsTap() },
                                leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null, tint = JasicaWhite) }
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
            SettingsScreen(
                currentApiKey       = currentApiKey,
                currentModel        = currentModel,
                isWakeWordMode      = isWakeWordMode,
                isAdvancedAiMode    = isAdvancedAiMode,
                isOnlineModeEnabled = isOnlineModeEnabled,
                useAdminPanelKey    = useAdminPanelKey,
                sharedPrefs         = sharedPrefs,
                onDismiss           = onDismissSettings,
                onSave              = onSaveSettings
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

    // Animated glow when ON
    val glowAlpha by animateFloatAsState(
        targetValue = if (isChecked) 0.7f else 0f,
        animationSpec = tween(400),
        label = "glow"
    )
    val cardScale by animateFloatAsState(
        targetValue = if (isChecked) 1f else 0.97f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    // Per-device icon
    val deviceIcon = when (device.id) {
        "a" -> "💻"
        "b" -> "🌈"
        "c" -> "💡"
        "d" -> "🔌"
        "e" -> "🌀"
        "f" -> "❄️"
        else -> "⚙️"
    }

    Box(
        modifier = modifier
            .scale(cardScale)
            .clip(RoundedCornerShape(24.dp))
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onSendCommand(if (!isChecked) device.cmdOn else device.cmdOff)
            }
    ) {
        // Card background image
        Image(
            painter = painterResource(id = R.drawable.orangeandpurplebg),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )

        // Dark overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = if (isChecked) 0.25f else 0.55f))
        )

        // Active glow overlay
        if (isChecked) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                JasicaOrange.copy(alpha = glowAlpha * 0.4f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // Border — orange when ON, subtle when OFF
        Box(
            modifier = Modifier
                .matchParentSize()
                .border(
                    width = if (isChecked) 1.5.dp else 1.dp,
                    brush = if (isChecked)
                        Brush.linearGradient(listOf(JasicaOrange.copy(alpha = 0.9f), JasicaPurple.copy(alpha = 0.5f)))
                    else
                        Brush.linearGradient(listOf(Color.White.copy(alpha = 0.15f), Color.White.copy(alpha = 0.05f))),
                    shape = RoundedCornerShape(24.dp)
                )
        )

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            // Top row: emoji icon + status dot
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = deviceIcon, fontSize = 26.sp)
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(
                            if (isChecked) Color(0xFF00E676) else Color.White.copy(alpha = 0.25f)
                        )
                )
            }

            Spacer(Modifier.height(14.dp))

            // Device name
            Text(
                text = device.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = InterFontFamily,
                fontSize = 14.sp,
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(12.dp))

            // Custom toggle pill
            val pillColor by animateColorAsState(
                targetValue = if (isChecked)
                    Brush.linearGradient(listOf(JasicaOrange, Color(0xFFFF6B35))).let { JasicaOrange }
                else Color(0xFF2A2A3A),
                animationSpec = tween(300),
                label = "pill"
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (isChecked)
                            Brush.horizontalGradient(listOf(JasicaOrange, Color(0xFFFF6B35)))
                        else
                            Brush.horizontalGradient(listOf(Color(0xFF2A2A3A), Color(0xFF1E1E2E)))
                    )
                    .border(
                        1.dp,
                        if (isChecked) JasicaOrange.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.1f),
                        RoundedCornerShape(50)
                    )
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onSendCommand(if (!isChecked) device.cmdOn else device.cmdOff)
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isChecked) "● ON" else "○ OFF",
                        color = if (isChecked) Color.White else Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFontFamily,
                        letterSpacing = 1.sp
                    )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentApiKey: String,
    currentModel: String,
    isWakeWordMode: Boolean,
    isAdvancedAiMode: Boolean,
    isOnlineModeEnabled: Boolean,
    useAdminPanelKey: Boolean,
    sharedPrefs: SharedPreferences,
    onDismiss: () -> Unit,
    onSave: (String, String, Boolean) -> Unit
) {
    var apiKeyInput by remember { mutableStateOf(currentApiKey) }
    var selectedModel by remember { mutableStateOf(currentModel) }
    var wakeWordInput by remember { mutableStateOf(isWakeWordMode) }
    var waterReminderInput by remember { mutableStateOf(sharedPrefs.getBoolean("WATER_REMINDER", false)) }
    var advancedAiInput by remember { mutableStateOf(isAdvancedAiMode) }
    var onlineModeInput by remember { mutableStateOf(isOnlineModeEnabled) }
    var adminKeyInput by remember { mutableStateOf(useAdminPanelKey) }
    var geminiKeyInput by remember { mutableStateOf(sharedPrefs.getString("GEMINI_API_KEY", "") ?: "") }
    var showApiKey by remember { mutableStateOf(false) }
    
    val context = androidx.compose.ui.platform.LocalContext.current
    var currentTab by remember { mutableStateOf(0) }
    
    var showPasswordDialog by remember { mutableStateOf(false) }
    var passwordInput by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D12))
            .clickable(enabled = false) {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 44.dp, start = 20.dp, end = 20.dp, bottom = 0.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Settings",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = InterFontFamily
                    )
                    Text(
                        "Preferences & Configuration",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 13.sp,
                        fontFamily = InterFontFamily
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("AI & System", "Hardware Config").forEachIndexed { index, title ->
                    val isSelected = currentTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0xFF1E1E2E) else Color.Transparent)
                            .clickable { currentTab = index },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            title,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            fontFamily = InterFontFamily
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            
            // Content
            Box(modifier = Modifier.weight(1f)) {
                if (currentTab == 0) {
                    // AI & System Tab
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        
                        // Section: Core System
                        Column {
                            Text("CORE SYSTEM", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Spacer(Modifier.height(12.dp))
                            
                            // Wake Word Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { wakeWordInput = !wakeWordInput }.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Hands-Free Wake Word", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                    Text("Say 'Hey Jasica' to activate", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                                }
                                Switch(
                                    checked = wakeWordInput,
                                    onCheckedChange = { wakeWordInput = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = JasicaOrange, uncheckedThumbColor = Color.Gray, uncheckedTrackColor = Color.DarkGray)
                                )
                            }
                            
                            Divider(color = Color.White.copy(alpha = 0.05f))
                            
                            // Water Reminder Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable {
                                    if (waterReminderInput) showPasswordDialog = true
                                    else {
                                        waterReminderInput = true
                                        sharedPrefs.edit().putBoolean("WATER_REMINDER", true).apply()
                                        WaterReminderManager.scheduleNextAlarm(context)
                                    }
                                }.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Water Drinking Reminder", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                    Text("30 minute intervals", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                                }
                                Switch(
                                    checked = waterReminderInput,
                                    onCheckedChange = null,
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF00BFFF), uncheckedThumbColor = Color.Gray, uncheckedTrackColor = Color.DarkGray)
                                )
                            }
                        }

                        // ── Section: AI Mode ─────────────────────────────────
                        Column {
                            Text(
                                "JASICA ONLINE",
                                color = Color.White.copy(alpha = 0.4f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(Modifier.height(12.dp))

                            // ── Master Online Toggle ──────────────────────────
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (onlineModeInput) JasicaOrange.copy(alpha = 0.10f)
                                        else Color.White.copy(alpha = 0.04f)
                                    )
                                    .clickable {
                                        onlineModeInput = !onlineModeInput
                                        sharedPrefs.edit()
                                            .putBoolean("ONLINE_MODE_ENABLED", onlineModeInput)
                                            .putBoolean("ADVANCED_AI_MODE", onlineModeInput) // compat
                                            .apply()
                                    }
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        "⚡",
                                        fontSize = 20.sp,
                                        modifier = Modifier.padding(end = 12.dp)
                                    )
                                    Column {
                                        Text(
                                            "Turn On Jasica Online",
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            if (onlineModeInput)
                                                "AI handles open questions & smart tasks"
                                            else
                                                "Offline only — 100% free, no internet needed",
                                            color = if (onlineModeInput) JasicaOrange else Color.White.copy(alpha = 0.5f),
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                Switch(
                                    checked = onlineModeInput,
                                    onCheckedChange = { checked ->
                                        onlineModeInput = checked
                                        sharedPrefs.edit()
                                            .putBoolean("ONLINE_MODE_ENABLED", checked)
                                            .putBoolean("ADVANCED_AI_MODE", checked)
                                            .apply()
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = JasicaOrange,
                                        uncheckedThumbColor = Color.Gray,
                                        uncheckedTrackColor = Color.DarkGray
                                    )
                                )
                            }

                            // ── Expanded: Online ON — Key Source ─────────────
                            androidx.compose.animation.AnimatedVisibility(
                                visible = onlineModeInput,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column {
                                    Spacer(Modifier.height(16.dp))

                                    // Key source label
                                    Text(
                                        "API KEY SOURCE",
                                        color = Color.White.copy(alpha = 0.35f),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    // Option 1 — Admin Panel Key
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (adminKeyInput) Color(0xFF1A2A1A)
                                                else Color.White.copy(alpha = 0.04f)
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (adminKeyInput) Color(0xFF4CAF50).copy(alpha = 0.5f)
                                                        else Color.White.copy(alpha = 0.08f),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable {
                                                adminKeyInput = true
                                                sharedPrefs.edit().putBoolean("USE_ADMIN_PANEL_KEY", true).apply()
                                            }
                                            .padding(horizontal = 14.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                "🔗  Admin Panel Key",
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                "Auto-fetched from your Portfolio settings",
                                                color = Color.White.copy(alpha = 0.5f),
                                                fontSize = 11.sp
                                            )
                                            // Status indicator
                                            val adminKey = sharedPrefs.getString("API_KEY", "") ?: ""
                                            val hasAdminKey = adminKey.startsWith("AIza")
                                            Text(
                                                if (hasAdminKey) "✓ Key loaded" else "✗ No key found — set one in Admin Panel",
                                                color = if (hasAdminKey) Color(0xFF4CAF50) else Color(0xFFFF6B6B),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
                                        }
                                        RadioButton(
                                            selected = adminKeyInput,
                                            onClick = {
                                                adminKeyInput = true
                                                sharedPrefs.edit().putBoolean("USE_ADMIN_PANEL_KEY", true).apply()
                                            },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = Color(0xFF4CAF50),
                                                unselectedColor = Color.White.copy(alpha = 0.3f)
                                            )
                                        )
                                    }

                                    Spacer(Modifier.height(8.dp))

                                    // Option 2 — My Own Key
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (!adminKeyInput) Color(0xFF1A1A2A)
                                                else Color.White.copy(alpha = 0.04f)
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (!adminKeyInput) JasicaOrange.copy(alpha = 0.5f)
                                                        else Color.White.copy(alpha = 0.08f),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable {
                                                adminKeyInput = false
                                                sharedPrefs.edit().putBoolean("USE_ADMIN_PANEL_KEY", false).apply()
                                            }
                                            .padding(horizontal = 14.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                "🔑  My Own Key",
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                "Enter your personal AI API key below",
                                                color = Color.White.copy(alpha = 0.5f),
                                                fontSize = 11.sp
                                            )
                                        }
                                        RadioButton(
                                            selected = !adminKeyInput,
                                            onClick = {
                                                adminKeyInput = false
                                                sharedPrefs.edit().putBoolean("USE_ADMIN_PANEL_KEY", false).apply()
                                            },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = JasicaOrange,
                                                unselectedColor = Color.White.copy(alpha = 0.3f)
                                            )
                                        )
                                    }

                                    // ── User's Own Key Field ──────────────────
                                    androidx.compose.animation.AnimatedVisibility(
                                        visible = !adminKeyInput,
                                        enter = expandVertically() + fadeIn(),
                                        exit = shrinkVertically() + fadeOut()
                                    ) {
                                        Column(modifier = Modifier.padding(top = 12.dp)) {
                                            OutlinedTextField(
                                                value = geminiKeyInput,
                                                onValueChange = {
                                                    geminiKeyInput = it
                                                    sharedPrefs.edit().putString("GEMINI_API_KEY", it.trim()).apply()
                                                },
                                                label = { Text("AI API Key", color = Color.White.copy(alpha = 0.5f)) },
                                                placeholder = { Text("AIza...", color = Color.White.copy(alpha = 0.2f)) },
                                                textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp),
                                                singleLine = true,
                                                visualTransformation = if (showApiKey)
                                                    androidx.compose.ui.text.input.VisualTransformation.None
                                                else
                                                    androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                                trailingIcon = {
                                                    IconButton(onClick = { showApiKey = !showApiKey }) {
                                                        Text(
                                                            if (showApiKey) "👁" else "🔒",
                                                            fontSize = 16.sp
                                                        )
                                                    }
                                                },
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = JasicaOrange,
                                                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                                                    cursorColor = JasicaOrange
                                                )
                                            )
                                            Spacer(Modifier.height(6.dp))
                                            Text(
                                                "Get your free key at aistudio.google.com →",
                                                color = JasicaOrange.copy(alpha = 0.8f),
                                                fontSize = 11.sp,
                                                modifier = Modifier.clickable {
                                                    try {
                                                        val intent = android.content.Intent(
                                                            android.content.Intent.ACTION_VIEW,
                                                            android.net.Uri.parse("https://aistudio.google.com/app/apikey")
                                                        )
                                                        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                                        context.startActivity(intent)
                                                    } catch (e: Exception) {}
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // ── Offline badge (when Online Mode is OFF) ───────
                            androidx.compose.animation.AnimatedVisibility(
                                visible = !onlineModeInput,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF1A1F2E))
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "100% offline. No tokens, no limits.",
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 12.sp
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(JasicaOrange.copy(alpha = 0.15f))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            "FREE",
                                            color = JasicaOrange,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(40.dp))
                    }
                } else {
                    // Devices Tab
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        items(DEFAULT_DEVICES.size) { index ->
                            val dev = DEFAULT_DEVICES[index]
                            var name by remember { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_NAME", dev.defaultName) ?: dev.defaultName) }
                            var onCmd by remember { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_ON_CMD", dev.defaultOnCmd) ?: dev.defaultOnCmd) }
                            var offCmd by remember { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_OFF_CMD", dev.defaultOffCmd) ?: dev.defaultOffCmd) }
                            var pinOn by remember { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_PIN_ON", dev.defaultPinOn) ?: dev.defaultPinOn) }
                            var pinOff by remember { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_PIN_OFF", dev.defaultPinOff) ?: dev.defaultPinOff) }

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(JasicaOrange))
                                    Spacer(Modifier.width(8.dp))
                                    Text("DEVICE '${dev.id.uppercase()}'", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                }
                                Spacer(Modifier.height(12.dp))
                                
                                OutlinedTextField(
                                    value = name, 
                                    onValueChange = { name = it; sharedPrefs.edit().putString("DEV_${dev.id}_NAME", it).apply() },
                                    label = { Text("Display Name", color = Color.White.copy(0.5f)) },
                                    textStyle = TextStyle(color = Color.White),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JasicaOrange, unfocusedBorderColor = Color.White.copy(alpha = 0.15f))
                                )
                                Spacer(Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    OutlinedTextField(
                                        value = onCmd, 
                                        onValueChange = { onCmd = it; sharedPrefs.edit().putString("DEV_${dev.id}_ON_CMD", it).apply() },
                                        label = { Text("ON Voice Cmd", color = Color.White.copy(0.5f)) },
                                        textStyle = TextStyle(color = Color.White),
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JasicaOrange, unfocusedBorderColor = Color.White.copy(alpha = 0.15f))
                                    )
                                    OutlinedTextField(
                                        value = offCmd, 
                                        onValueChange = { offCmd = it; sharedPrefs.edit().putString("DEV_${dev.id}_OFF_CMD", it).apply() },
                                        label = { Text("OFF Voice Cmd", color = Color.White.copy(0.5f)) },
                                        textStyle = TextStyle(color = Color.White),
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JasicaOrange, unfocusedBorderColor = Color.White.copy(alpha = 0.15f))
                                    )
                                }
                                Spacer(Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    OutlinedTextField(
                                        value = pinOn, 
                                        onValueChange = { pinOn = it; sharedPrefs.edit().putString("DEV_${dev.id}_PIN_ON", it).apply() },
                                        label = { Text("ON Pin (Char)", color = Color.White.copy(0.5f)) },
                                        textStyle = TextStyle(color = Color.White),
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JasicaOrange, unfocusedBorderColor = Color.White.copy(alpha = 0.15f))
                                    )
                                    OutlinedTextField(
                                        value = pinOff, 
                                        onValueChange = { pinOff = it; sharedPrefs.edit().putString("DEV_${dev.id}_PIN_OFF", it).apply() },
                                        label = { Text("OFF Pin (Char)", color = Color.White.copy(0.5f)) },
                                        textStyle = TextStyle(color = Color.White),
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JasicaOrange, unfocusedBorderColor = Color.White.copy(alpha = 0.15f))
                                    )
                                }
                                
                                Spacer(Modifier.height(8.dp))
                                Divider(color = Color.White.copy(alpha = 0.05f))
                            }
                        }
                    }
                }
            }
            
            // Bottom Action Buttons
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) {
                    Text("CANCEL", color = Color.White.copy(alpha = 0.5f), fontFamily = InterFontFamily, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(16.dp))
                Button(
                    onClick = { onSave(apiKeyInput, selectedModel, wakeWordInput) },
                    colors = ButtonDefaults.buttonColors(containerColor = JasicaOrange),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp).padding(horizontal = 16.dp)
                ) {
                    Text("SAVE SETTINGS", color = Color.White, fontFamily = InterFontFamily, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (showPasswordDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showPasswordDialog = false
                    passwordError = false
                    passwordInput = ""
                },
                title = { Text("Enter Admin Password", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("A password is required to turn off the water reminder.", color = Color.White.copy(0.7f), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it; passwordError = false },
                            label = { Text("Password", color = Color.White.copy(0.5f)) },
                            isError = passwordError,
                            textStyle = TextStyle(color = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JasicaOrange, unfocusedBorderColor = Color.White.copy(alpha = 0.15f))
                        )
                        if (passwordError) {
                            Text("Incorrect password", color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                },
                containerColor = Color(0xFF1E1E2E),
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
                        colors = ButtonDefaults.buttonColors(containerColor = JasicaOrange),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Confirm", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        showPasswordDialog = false
                        passwordError = false
                        passwordInput = ""
                    }) {
                        Text("Cancel", color = Color.White.copy(0.5f))
                    }
                }
            )
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

    // 0 = UNO + HC-05, 1 = ESP32
    var selectedBoard by remember { mutableStateOf(0) }
    var copied by remember { mutableStateOf(false) }

    val boards = listOf("Arduino UNO + HC-05", "ESP32 Dev v1")
    val codes = listOf(ARDUINO_UNO_CODE, ESP32_CODE)
    val boardColors = listOf(
        listOf(Color(0xFF00979C), Color(0xFF005F60)),  // Arduino teal
        listOf(Color(0xFFE7352C), Color(0xFF8B1010))   // ESP32 red
    )

    LaunchedEffect(copied) {
        if (copied) {
            kotlinx.coroutines.delay(2000)
            copied = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D12))
            .clickable(enabled = false) {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 44.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Arduino Code",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = InterFontFamily
                    )
                    Text(
                        "Ready to upload firmware",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 13.sp,
                        fontFamily = InterFontFamily
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(Modifier.height(20.dp))

            // Board Selector Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.06f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                boards.forEachIndexed { index, name ->
                    val isSelected = selectedBoard == index
                    val tabColor = if (isSelected) boardColors[index] else listOf(Color.Transparent, Color.Transparent)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.horizontalGradient(tabColor))
                            .border(
                                if (isSelected) 1.dp else 0.dp,
                                Color.White.copy(alpha = if (isSelected) 0.2f else 0f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedBoard = index; copied = false }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.4f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = InterFontFamily,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Board info pill
            val boardInfo = if (selectedBoard == 0)
                "📌 HC-05 RX→Pin 2  TX→Pin 3  |  Devices: Pins 8–13"
            else
                "📌 Built-in BLE  |  Devices: GPIO 13,12,14,27,26,25"

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(boardColors[selectedBoard][0].copy(alpha = 0.15f))
                    .border(1.dp, boardColors[selectedBoard][0].copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = boardInfo,
                    color = boardColors[selectedBoard][0],
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(14.dp))

            // Code block — scrollable
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1A1A2E))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            ) {
                // Line numbers + code
                val scrollState = rememberScrollState()
                val codeLines = codes[selectedBoard].lines()
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(12.dp)
                ) {
                    // Line numbers column
                    Column(
                        modifier = Modifier.padding(end = 12.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        codeLines.forEachIndexed { i, _ ->
                            Text(
                                text = "${i + 1}",
                                color = Color.White.copy(alpha = 0.2f),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 18.sp
                            )
                        }
                    }
                    // Code column
                    Column {
                        codeLines.forEach { line ->
                            val lineColor = when {
                                line.trimStart().startsWith("//") -> Color(0xFF6A9955)
                                line.trimStart().startsWith("#") -> Color(0xFFC586C0)
                                line.contains("void ") || line.contains("const ") || line.contains("int ") -> Color(0xFF569CD6)
                                line.contains("HIGH") || line.contains("LOW") -> Color(0xFFCE9178)
                                else -> Color(0xFFD4D4D4)
                            }
                            Text(
                                text = line,
                                color = lineColor,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 18.sp,
                                softWrap = false
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Copy Button
            Button(
                onClick = {
                    clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(codes[selectedBoard]))
                    copied = true
                    Toast.makeText(context, "Code copied! Open Arduino IDE and paste.", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (copied) Color(0xFF00E676) else boardColors[selectedBoard][0]
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (copied) "✓  Copied to Clipboard!" else "⎘  Copy Full Code",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily,
                    letterSpacing = 0.5.sp
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
