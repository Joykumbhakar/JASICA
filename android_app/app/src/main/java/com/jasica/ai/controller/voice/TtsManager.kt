package com.jasica.ai.controller.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsManager(context: Context) {
    private val TAG = "JasicaTtsManager"
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.w(TAG, "US Language not supported, falling back to default")
                    tts?.language = Locale.getDefault()
                }
                tts?.setPitch(1.05f)
                tts?.setSpeechRate(1.1f) // Snappy and responsive
                isInitialized = true
            } else {
                Log.e(TAG, "TTS Initialization failed with status: $status")
            }
        }

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isSpeaking.value = true
            }

            override fun onDone(utteranceId: String?) {
                _isSpeaking.value = false
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _isSpeaking.value = false
            }
        })
    }

    /**
     * Non-blocking speech synthesis.
     * QUEUE_FLUSH ensures previous utterance is immediately interrupted
     * and does NOT delay future speech commands.
     */
    fun speak(text: String, interrupt: Boolean = true) {
        if (!isInitialized || tts == null) {
            Log.w(TAG, "TTS not ready yet")
            return
        }
        val queueMode = if (interrupt) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
        tts?.speak(text, queueMode, null, "JASICA_UTTERANCE_${System.currentTimeMillis()}")
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
