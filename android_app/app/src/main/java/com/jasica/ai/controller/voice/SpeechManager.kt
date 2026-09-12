package com.jasica.ai.controller.voice

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.jasica.ai.controller.bluetooth.AppBluetoothManager
import com.jasica.ai.controller.alarm.AlarmScheduler
import com.jasica.ai.controller.data.CommandPreferences
import com.jasica.ai.controller.data.PinCommand
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class SpeechManager(
    private val context: Context,
    private val bluetoothManager: AppBluetoothManager,
    private val ttsManager: TtsManager,
    private val commandPreferences: CommandPreferences
) {
    private val TAG = "JasicaSpeechManager"
    private var speechRecognizer: SpeechRecognizer? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private val alarmScheduler = AlarmScheduler(context)

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _lastTranscript = MutableStateFlow("")
    val lastTranscript: StateFlow<String> = _lastTranscript.asStateFlow()

    private val _lastActionFeedback = MutableStateFlow("")
    val lastActionFeedback: StateFlow<String> = _lastActionFeedback.asStateFlow()

    private val _pinStates = MutableStateFlow<List<PinCommand>>(commandPreferences.loadPinList())
    val pinStates: StateFlow<List<PinCommand>> = _pinStates.asStateFlow()

    init {
        mainHandler.post {
            initRecognizer()
        }
    }

    private fun initRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(createRecognitionListener())
            }
        } else {
            Log.e(TAG, "SpeechRecognizer is not available on this device.")
        }
    }

    fun reloadCommandList() {
        _pinStates.value = commandPreferences.loadPinList()
    }

    fun startListening() {
        mainHandler.post {
            // Instantly stop TTS so microphone doesn't pick up speaker output
            ttsManager.stop()

            if (speechRecognizer == null) {
                initRecognizer()
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }

            try {
                speechRecognizer?.startListening(intent)
                _isListening.value = true
                _lastActionFeedback.value = "Listening..."
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start listening", e)
                _isListening.value = false
            }
        }
    }

    fun stopListening() {
        mainHandler.post {
            try {
                speechRecognizer?.stopListening()
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping recognizer", e)
            } finally {
                _isListening.value = false
            }
        }
    }

    private fun createRecognitionListener() = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            _isListening.value = true
            _lastActionFeedback.value = "Ready! Say a command..."
        }

        override fun onBeginningOfSpeech() {
            _isListening.value = true
        }

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            _isListening.value = false
        }

        override fun onError(error: Int) {
            _isListening.value = false
            val errorMsg = when (error) {
                SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Try again."
                SpeechRecognizer.ERROR_NETWORK -> "Network error."
                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error."
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input detected."
                else -> "Speech error (code: $error)"
            }
            _lastActionFeedback.value = errorMsg
        }

        override fun onResults(results: Bundle?) {
            _isListening.value = false
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                val transcript = matches[0].lowercase().trim()
                _lastTranscript.value = transcript
                processVoiceCommand(transcript)
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                _lastTranscript.value = matches[0]
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

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

    private fun matchSmartIntent(cleanInput: String): String? {
        val raw = cleanInput.lowercase().trim()

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

    fun processVoiceCommand(input: String) {
        val cleanInput = input.lowercase().trim()
        val currentPins = _pinStates.value.toMutableList()

        // 1. Check for Macro Commands
        if (cleanInput.contains("all on") || cleanInput.contains("turn on everything") || cleanInput == "on" || cleanInput.contains("sob on") || cleanInput.contains("shob on")) {
            bluetoothManager.sendCommand("on")
            currentPins.forEach { it.isStateOn = true }
            _pinStates.value = currentPins
            val msg = "ঠিক আছে বৃষ্টি বস! তোমার ঘরের সব ডিভাইস একসাথে অন করে দিচ্ছি!"
            _lastActionFeedback.value = msg
            ttsManager.speak(msg, interrupt = true)
            return
        }

        if (cleanInput.contains("all off") || cleanInput.contains("turn off everything") || cleanInput == "off" || cleanInput.contains("sob off") || cleanInput.contains("shob off")) {
            bluetoothManager.sendCommand("off")
            currentPins.forEach { it.isStateOn = false }
            _pinStates.value = currentPins
            val msg = "ঠিক আছে বৃষ্টি! সব ডিভাইস একসাথে অফ করে দিলাম, শান্তিতে থাকো!"
            _lastActionFeedback.value = msg
            ttsManager.speak(msg, interrupt = true)
            return
        }

        if (cleanInput.contains("mood") || cleanInput.contains("stark tower")) {
            bluetoothManager.sendCommand("mood")
            val msg = "বাহ্ বৃষ্টি! মুড লাইটিং অন করে দিচ্ছি, একদম দারুণ পরিবেশ!"
            _lastActionFeedback.value = msg
            ttsManager.speak(msg, interrupt = true)
            return
        }

        if (cleanInput.contains("status") || cleanInput.contains("system status")) {
            bluetoothManager.sendCommand("status")
            val msg = "সিস্টেম স্ট্যাটাস চেক করছি..."
            _lastActionFeedback.value = msg
            ttsManager.speak(msg, interrupt = true)
            return
        }

        // Sad song / Favorite song
        if (cleanInput.contains("sad song") || cleanInput.contains("sad gaan") || (cleanInput.contains("song") && cleanInput.contains("sad"))) {
            val msg = "ঠিক আছে বস, আজ মন খারাপ বুঝি যে স্যাড গান চালাতে বলছো? যাই হোক, আমি ইউটিউব থেকে তোমার পছন্দের গানটা চালিয়ে দিচ্ছি!"
            _lastActionFeedback.value = msg
            ttsManager.speak(msg, interrupt = true)
            return
        }

        if (cleanInput.contains("play song") || cleanInput.contains("favorite song") || cleanInput.contains("fav song") || cleanInput.contains("gaan chalao")) {
            val msg = "অবশ্যই বৃষ্টি! তোমার পছন্দের গানটা চালিয়ে দিচ্ছি, ইনজয় করো! 🎵"
            _lastActionFeedback.value = msg
            ttsManager.speak(msg, interrupt = true)
            return
        }

        // Feature: Water Reminder
        if (Regex("\\b(?:water|drink|jol|pani)\\b").containsMatchIn(cleanInput) || cleanInput.contains("remind me to drink water")) {
            alarmScheduler.scheduleWaterReminder(30)
            val msg = "একদম বৃষ্টি! ৩০ মিনিট পর আবার জল খাওয়ার রিমাইন্ডার দিয়ে দেব, সুস্থ থাকা দরকার!"
            _lastActionFeedback.value = msg
            ttsManager.speak(msg, interrupt = true)
            return
        }

        // WhatsApp Launch Shortcut (Any word starting with 'W' / 'w' or WhatsApp keywords)
        val waTokens = cleanInput.split("\\s+".toRegex()).filter { it.isNotBlank() }
        val nonWaWords = setOf(
            "water", "white", "weather", "who", "what", "where", "when", "why",
            "work", "with", "would", "will", "wah", "won", "whose", "which",
            "wait", "wake", "welcome", "world", "week", "weekend", "wish",
            "watch", "warm", "window", "windows", "working", "we", "was",
            "were", "well", "walk", "way", "wall", "wife", "wrong", "write",
            "without", "word", "words", "website", "want", "went"
        )
        val isWaIntent = waTokens.any { w ->
            w == "w" || w == "wa" || w == "wapp" || w == "whatsapp" || w == "watsapp" || w == "watshap" || w == "whatsup" || w == "watsup" || w == "whatapp" || w == "wsp" || w == "wup" ||
            (w.startsWith("w") && !nonWaWords.contains(w))
        } || cleanInput.contains("whatsapp") || Regex("\\bopen w\\b").containsMatchIn(cleanInput)

        if (isWaIntent) {
            val msg = "ঠিক আছে বৃষ্টি, হোয়াটসঅ্যাপ খুলে দিচ্ছি!"
            _lastActionFeedback.value = msg
            ttsManager.speak(msg, interrupt = true)
            launchAppPackage("com.whatsapp", "https://www.whatsapp.com")
            return
        }

        // Social Media & App Launchers
        if (Regex("\\b(?:instagram|insta|ig)\\b").containsMatchIn(cleanInput)) {
            val msg = "ঠিক আছে বৃষ্টি বস, ইনস্টাগ্রাম ওপেন করে দিচ্ছি!"
            _lastActionFeedback.value = msg
            ttsManager.speak(msg, interrupt = true)
            launchAppPackage("com.instagram.android", "https://www.instagram.com")
            return
        }

        if (Regex("\\b(?:facebook|fb)\\b").containsMatchIn(cleanInput)) {
            val msg = "অবশ্যই বৃষ্টি, ফেসবুক ওপেন করে দিচ্ছি!"
            _lastActionFeedback.value = msg
            ttsManager.speak(msg, interrupt = true)
            launchAppPackage("com.facebook.katana", "https://www.facebook.com")
            return
        }

        if (Regex("\\b(?:linkedin)\\b").containsMatchIn(cleanInput)) {
            val msg = "ঠিক আছে বৃষ্টি, লিঙ্কডইন ওপেন করে দিচ্ছি!"
            _lastActionFeedback.value = msg
            ttsManager.speak(msg, interrupt = true)
            launchAppPackage("com.linkedin.android", "https://www.linkedin.com")
            return
        }

        if (Regex("\\b(?:telegram|tg)\\b").containsMatchIn(cleanInput)) {
            val msg = "ঠিক আছে বৃষ্টি বস, টেলিগ্রাম ওপেন করে দিচ্ছি!"
            _lastActionFeedback.value = msg
            ttsManager.speak(msg, interrupt = true)
            launchAppPackage("org.telegram.messenger", "https://telegram.org")
            return
        }

        // 2. Check Custom & Default Pin Phrases
        for (i in currentPins.indices) {
            val pin = currentPins[i]
            val onTarget = pin.onPhrase.lowercase().trim()
            val offTarget = pin.offPhrase.lowercase().trim()
            val isLedOrLight = pin.label.contains("LED", true) || pin.label.contains("Light", true) || pin.label.contains("White", true)

            // Match ON command
            val onTargetPattern = "\\b" + Regex.escape(onTarget) + "\\b"
            val labelPattern = "\\b" + Regex.escape(pin.label.lowercase()) + "\\b"
            if (cleanInput == onTarget || Regex(onTargetPattern).containsMatchIn(cleanInput) || 
                (Regex("\\bturn on\\b").containsMatchIn(cleanInput) && Regex(labelPattern).containsMatchIn(cleanInput)) ||
                (Regex("\\bon\\b").containsMatchIn(cleanInput) && Regex(labelPattern).containsMatchIn(cleanInput))) {
                bluetoothManager.sendChar(pin.onChar)
                currentPins[i] = pin.copy(isStateOn = true)
                _pinStates.value = currentPins
                val msg = if (isLedOrLight) {
                    listOf(
                        "হ্যাঁ বৃষ্টি, আমি লাইট অন করে দিচ্ছি। তোমার আর কিছু অন করতে লাগবে?",
                        "ঠিক আছে বস, লাইট জ্বালিয়ে দিলাম।",
                        "লাইট অন করা হয়েছে বৃষ্টি বস!",
                        "অবশ্যই বৃষ্টি, লাইট অন করে দিচ্ছি!"
                    ).random()
                } else {
                    listOf(
                        "ঠিক আছে বৃষ্টি বস, তোমার কথামতো ${pin.label} অন করে দিচ্ছি!",
                        "অবশ্যই বস, ${pin.label} অন করা হলো।",
                        "${pin.label} অন করে দিয়েছি বৃষ্টি!",
                        "হ্যাঁ বৃষ্টি, ${pin.label} চালু করে দিলাম।"
                    ).random()
                }
                _lastActionFeedback.value = msg
                ttsManager.speak(msg, interrupt = true)
                return
            }

            // Match OFF command
            val offTargetPattern = "\\b" + Regex.escape(offTarget) + "\\b"
            if (cleanInput == offTarget || Regex(offTargetPattern).containsMatchIn(cleanInput) ||
                (Regex("\\bturn off\\b").containsMatchIn(cleanInput) && Regex(labelPattern).containsMatchIn(cleanInput)) ||
                (Regex("\\boff\\b").containsMatchIn(cleanInput) && Regex(labelPattern).containsMatchIn(cleanInput))) {
                bluetoothManager.sendChar(pin.offChar)
                currentPins[i] = pin.copy(isStateOn = false)
                _pinStates.value = currentPins
                val msg = listOf(
                    "ঠিক আছে বৃষ্টি বস, ${pin.label} অফ করে দিলাম।",
                    "ওকে বস, ${pin.label} বন্ধ করা হয়েছে।",
                    "হ্যাঁ বৃষ্টি, ${pin.label} অফ করে দিয়েছি।",
                    "${pin.label} বন্ধ করে দিলাম বৃষ্টি!"
                ).random()
                _lastActionFeedback.value = msg
                ttsManager.speak(msg, interrupt = true)
                return
            }
        }

        // 3. Fallback for generic patterns: "turn on pin X" or "turn on led X"
        val regexOn = Regex("""turn on (?:pin|led|device)?\s*(\d+)""")
        val matchOn = regexOn.find(cleanInput)
        if (matchOn != null) {
            val pinNum = matchOn.groupValues[1].toIntOrNull()
            val target = currentPins.find { it.pinNumber == pinNum }
            if (target != null) {
                bluetoothManager.sendChar(target.onChar)
                target.isStateOn = true
                _pinStates.value = currentPins
                val msg = listOf(
                    "হ্যাঁ বৃষ্টি, আমি লাইট অন করে দিচ্ছি। তোমার আর কিছু অন করতে লাগবে?",
                    "ঠিক আছে বস, লাইট জ্বালিয়ে দিলাম।",
                    "লাইট অন করা হয়েছে বৃষ্টি বস!",
                    "অবশ্যই বৃষ্টি, লাইট অন করে দিচ্ছি!"
                ).random()
                _lastActionFeedback.value = msg
                ttsManager.speak(msg, interrupt = true)
                return
            }
        }

        val regexOff = Regex("""turn off (?:pin|led|device)?\s*(\d+)""")
        val matchOff = regexOff.find(cleanInput)
        if (matchOff != null) {
            val pinNum = matchOff.groupValues[1].toIntOrNull()
            val target = currentPins.find { it.pinNumber == pinNum }
            if (target != null) {
                bluetoothManager.sendChar(target.offChar)
                target.isStateOn = false
                _pinStates.value = currentPins
                val msg = listOf(
                    "ঠিক আছে বৃষ্টি বস, ${target.label} অফ করে দিলাম।",
                    "ওকে বস, ${target.label} বন্ধ করা হয়েছে।",
                    "হ্যাঁ বৃষ্টি, ${target.label} অফ করে দিয়েছি।",
                    "${target.label} বন্ধ করে দিলাম বৃষ্টি!"
                ).random()
                _lastActionFeedback.value = msg
                ttsManager.speak(msg, interrupt = true)
                return
            }
        }

        // 4. Smart Intent (Offline Conversational Chit-chat & Introductions)
        val smartReply = matchSmartIntent(cleanInput)
        if (smartReply != null) {
            _lastActionFeedback.value = smartReply
            ttsManager.speak(smartReply, interrupt = true)
            return
        }

        val unrecMsg = "হুম, আমি ঠিক বুঝতে পারলাম না। আরেকবার বলবে কি?"
        _lastActionFeedback.value = unrecMsg
        ttsManager.speak(unrecMsg, interrupt = true)
    }

    fun togglePinDirectly(pin: PinCommand) {
        val currentPins = _pinStates.value.toMutableList()
        val index = currentPins.indexOfFirst { it.pinNumber == pin.pinNumber }
        if (index != -1) {
            val newState = !pin.isStateOn
            val charToSend = if (newState) pin.onChar else pin.offChar
            bluetoothManager.sendChar(charToSend)
            currentPins[index] = pin.copy(isStateOn = newState)
            _pinStates.value = currentPins
            val msg = "${pin.label} turned ${if (newState) "ON" else "OFF"}"
            _lastActionFeedback.value = msg
            ttsManager.speak(msg, interrupt = true)
        }
    }

    private fun launchAppPackage(packageName: String, fallbackUrl: String) {
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } else {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webIntent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch app: $packageName", e)
        }
    }

    fun cleanup() {
        mainHandler.post {
            try {
                speechRecognizer?.destroy()
            } catch (e: Exception) {
                Log.e(TAG, "Error destroying speech recognizer", e)
            } finally {
                speechRecognizer = null
            }
        }
    }
}
