package com.jasica.ai.controller.voice

import android.content.Context
import android.content.Intent
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

    fun processVoiceCommand(input: String) {
        val cleanInput = input.lowercase().trim()
        val currentPins = _pinStates.value.toMutableList()

        // 1. Check for Macro Commands
        if (cleanInput.contains("all on") || cleanInput.contains("turn on everything") || cleanInput == "on") {
            bluetoothManager.sendCommand("on")
            currentPins.forEach { it.isStateOn = true }
            _pinStates.value = currentPins
            val msg = "All devices turned ON"
            _lastActionFeedback.value = msg
            ttsManager.speak(msg, interrupt = true)
            return
        }

        if (cleanInput.contains("all off") || cleanInput.contains("turn off everything") || cleanInput == "off") {
            bluetoothManager.sendCommand("off")
            currentPins.forEach { it.isStateOn = false }
            _pinStates.value = currentPins
            val msg = "All devices turned OFF"
            _lastActionFeedback.value = msg
            ttsManager.speak(msg, interrupt = true)
            return
        }

        if (cleanInput.contains("mood") || cleanInput.contains("stark tower")) {
            bluetoothManager.sendCommand("mood")
            val msg = "Mood mode activated"
            _lastActionFeedback.value = msg
            ttsManager.speak(msg, interrupt = true)
            return
        }

        if (cleanInput.contains("status") || cleanInput.contains("system status")) {
            bluetoothManager.sendCommand("status")
            val msg = "Requesting system status"
            _lastActionFeedback.value = msg
            ttsManager.speak(msg, interrupt = true)
            return
        }

        // Feature: Water Reminder
        if (cleanInput.contains("water") || cleanInput.contains("drink") || cleanInput.contains("remind me to drink water")) {
            alarmScheduler.scheduleWaterReminder(30)
            val msg = "Water reminder set for 30 minutes"
            _lastActionFeedback.value = msg
            ttsManager.speak(msg, interrupt = true)
            return
        }

        // 2. Check Custom & Default Pin Phrases
        for (i in currentPins.indices) {
            val pin = currentPins[i]
            val onTarget = pin.onPhrase.lowercase().trim()
            val offTarget = pin.offPhrase.lowercase().trim()

            // Match ON command
            if (cleanInput == onTarget || cleanInput.contains(onTarget) || 
                (cleanInput.contains("turn on") && cleanInput.contains(pin.label.lowercase()))) {
                bluetoothManager.sendChar(pin.onChar)
                currentPins[i] = pin.copy(isStateOn = true)
                _pinStates.value = currentPins
                val msg = "${pin.label} turned ON"
                _lastActionFeedback.value = msg
                ttsManager.speak(msg, interrupt = true)
                return
            }

            // Match OFF command
            if (cleanInput == offTarget || cleanInput.contains(offTarget) ||
                (cleanInput.contains("turn off") && cleanInput.contains(pin.label.lowercase()))) {
                bluetoothManager.sendChar(pin.offChar)
                currentPins[i] = pin.copy(isStateOn = false)
                _pinStates.value = currentPins
                val msg = "${pin.label} turned OFF"
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
                val msg = "${target.label} turned ON"
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
                val msg = "${target.label} turned OFF"
                _lastActionFeedback.value = msg
                ttsManager.speak(msg, interrupt = true)
                return
            }
        }

        val unrecMsg = "Command not recognized: \"$input\""
        _lastActionFeedback.value = unrecMsg
        ttsManager.speak("Sorry, I didn't recognize that command", interrupt = true)
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
