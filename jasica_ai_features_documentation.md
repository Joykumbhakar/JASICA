# JASICA AI - App Features Documentation

This document outlines all the features and capabilities present in the **JASICA AI** Android application.

## 1. AI Voice Assistant (Jasica)
The core feature of the application is a highly intelligent, emotionally aware AI assistant with a distinct personality.
* **Powered by Google Gemini**: Integrates with the `gemini-2.5-flash` API for natural language understanding and generation.
* **Custom Persona**: Designed specifically to be humorous, caring, and loyal to its creator (Bristi).
* **Bilingual Text-to-Speech (TTS)**: Speaks and understands both English and Bengali natively using pure Bengali script (`বাংলা`) for authentic pronunciation.
* **Smart Offline Persona & Fuzzy Matching (API-less Chit-Chat)**: Zero-latency local responses without internet equipped with Levenshtein fuzzy distance matching, filler stripping, and typo tolerance:
  * **Common Greetings**: "হেই বৃষ্টি! আমি একদম তৈরি, বলো কী করতে হবে? 😊", "শুভ সকাল", "শুভ রাত্রি"
  * **Self Introductions**: "আমি জ্যাসিকা — বৃষ্টির তৈরি স্মার্ট এআই অ্যাসিস্ট্যান্ট!..."
  * **Creator Identity & Details**: Full awareness of Bristi Kumbhakar (ITI degree, GE internship, Durgapur residence, Biryani & Momos favorites, cheek beauty spot).
  * **Creator Relatives**: Joy Kumbhakar (developer & bodyguard), Sona Di, Tithi, Jiju Didi, Fav Di.
  * **Daily Chit-Chat**: "কেমন আছো?", "কী করছো?", Time inquiries, jokes, love/appreciation.
  * **Native Bengali Hardware Confirmations**:
    * Light/LED: *"হ্যাঁ বৃষ্টি, আমি লাইট অন করে দিচ্ছি। তোমার আর কিছু অন করতে লাগবে?"*
    * Custom Devices: *"ঠিক আছে বৃষ্টি বস, তোমার কথামতো [Device] অন করে দিচ্ছি!"*
    * All On / All Off / Mood lighting confirmations in native Bengali.
  * **Emotion-Aware Media Playback**: *"ঠিক আছে বস, আজ মন খারাপ বুঝি যে স্যাড গান চালাতে বলছো? যাই হোক, আমি ইউটিউব থেকে তোমার পছন্দের গানটা চালিয়ে দিচ্ছি!"*
* **Wake Word Mode**: Allows the user to activate the assistant hands-free by listening in the background.
* **Conversation Memory**: Maintains the context of the conversation by storing up to 6 recent interactions (chat history).
* **Voice Calibration**: Includes a voice calibration flow on first launch to ensure optimal speech recognition.

## 2. Smart Home & Hardware Control
The app acts as a remote control for hardware systems (like Arduino or ESP32) using Bluetooth.
* **Dual Bluetooth Support**: Supports both Bluetooth Classic and Bluetooth Low Energy (BLE) connections.
* **Local Voice Commands (Offline)**: Includes an offline command table that can parse hardware intents without needing an internet connection.
* **6 Default Connected Devices**: 
  * PC Hub
  * RGB / Night Light
  * Room Light / White LED
  * Plug
  * Fan
  * AC Unit
* **Master Controls**: Commands like "Turn on all" or "Turn off all" to control multiple devices at once.
* **Mood Preset**: "Mood lighting" command for special configurations.
* **Manual Control Panel**: A UI dialog allowing the user to manually toggle the state of each connected device.
* **Arduino Code Generator**: A built-in feature that provides or displays the Arduino code needed for the hardware setup.

## 3. System & App Shortcuts
Jasica can interact with the Android OS to launch external applications and services via voice commands.
* **Social Media Launcher**: Voice commands to instantly open apps like:
  * Instagram
  * Facebook
  * LinkedIn
  * **WhatsApp**: Fast-triggerable by saying "WhatsApp", "wa", "wapp", "watsup", or any word starting with the letter **'W'** (e.g., "W", "Open W", "WA").
  * Telegram
* **Camera Integration**: Commands to "Open Camera", "Take a photo", or "Start recording video".
* **Media Playback**: "Play my fav song" automatically launches a specific YouTube video/audio stream.

## 4. Health & Wellness (Water Reminder)
A background service dedicated to keeping the user hydrated.
* **30-Minute Intervals**: Automatically schedules a recurring alarm every 30 minutes.
* **Full-Screen Lock Screen Activity**: Wakes up the device and shows a full-screen interactive UI (with animated water waves and a floating drop) even if the phone is locked.
* **Alarms & Vibration**: Plays a ringtone and vibrates the device until the user acknowledges the reminder by tapping the "I DRANK IT" button.

## 5. UI, UX, and Customization
* **Animated Voice UI**: Uses Lottie animations and dynamic visuals (like color blurring and pulsing) to indicate the AI's current state (Idle, Listening, Thinking, Speaking).
* **Dynamic App Icons**: The app defines multiple alias icons in the manifest (e.g., `AliasFavDi`, `AliasSonaDi`, `AliasQween`, `AliasThinking`), allowing the app icon to be changed programmatically.
* **Settings & Preferences**: A dedicated settings dialog to customize:
  * Gemini API Keys
  * Selected AI Model
  * Wake Word toggle status
  * Device naming and custom ON/OFF voice triggers for hardware.
* **Conversation History Viewer**: A UI panel where users can review past conversations with the AI.
* **Apple Design System Voice Setup Screen**: A voice calibration & training flow adhering to Apple Human Interface Guidelines (Cupertino HIG):
  * Deep Space Black OLED surface with Siri Aurora ambient mesh glow.
  * Segmented stepper pill progress bar with Apple Green/Blue animated transitions.
  * Frosted glass squircle card (`28.dp` radius) with hairline gradient border.
  * Dynamic real-time soundwave bar visualizer and transcription pill.
  * Multi-layer concentric Siri-pulsing tactile microphone button.
  * Elegant success badge with glowing Apple Green checkmark and full-width "Continue" primary pill button.
* **Onboarding Flow**: A welcome tutorial/onboarding screen for first-time users.
* **Remote Configuration**: Automatically fetches updated app configurations and API keys from a remote portfolio API (`https://joykumbhakar.vercel.app/api/app-config`).
