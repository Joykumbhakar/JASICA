package com.bristi.controller

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kotlinx.coroutines.delay

class FloatingControlService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var composeView: ComposeView
    private lateinit var params: WindowManager.LayoutParams
    private var isViewAttached = false
    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        startForegroundServiceNotification()
        setupFloatingView()
    }

    private fun startForegroundServiceNotification() {
        val channelId = "floating_control_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Quick Access", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, channelId)
                .setContentTitle("Jasica Quick Access Active")
                .setContentText("Tap the floating icon to control devices.")
                .setSmallIcon(R.drawable.ic_ai_waves)
                .build()
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
                .setContentTitle("Jasica Quick Access Active")
                .setContentText("Tap the floating icon to control devices.")
                .setSmallIcon(R.drawable.ic_ai_waves)
                .build()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1003, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(1003, notification)
        }
    }

    private fun setupFloatingView() {
        composeView = ComposeView(this).apply {
            setContent {
                FloatingWidgetContent(
                    onClose = { stopSelf() },
                    onDeviceTap = { idx -> sendCommandBroadcast(idx) },
                    onMicTap = {
                        showBottomVoiceBlob()
                    }
                )
            }
        }

        val lifecycleOwner = MyLifecycleOwner()
        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        composeView.setViewTreeLifecycleOwner(lifecycleOwner)
        composeView.setViewTreeViewModelStoreOwner(object : ViewModelStoreOwner {
            override val viewModelStore = ViewModelStore()
        })
        composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 80
            y = 300
        }

        windowManager.addView(composeView, params)
        isViewAttached = true
    }

    private fun playStartSound() {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(this, R.raw.start)
            mediaPlayer?.setOnCompletionListener { it.release(); mediaPlayer = null }
            mediaPlayer?.start()
        } catch (e: Exception) {
            android.util.Log.e("FloatingService", "Could not play sound: ${e.message}")
        }
    }

    private fun sendCommandBroadcast(deviceIndex: Int) {
        val prefs = getSharedPreferences("JasicaSettings", Context.MODE_PRIVATE)
        val devId = "dev$deviceIndex"
        val isOn = JasicaBluetoothManager.deviceStates[devId] ?: prefs.getBoolean("DEV_$devId", false)

        val defaultPinOn = when(deviceIndex) { 1->"A"; 2->"B"; 3->"C"; 4->"D"; 5->"E"; 6->"F"; else->"A" }
        val defaultPinOff = when(deviceIndex) { 1->"a"; 2->"b"; 3->"c"; 4->"d"; 5->"e"; 6->"f"; else->"a" }

        val pinOn = prefs.getString("DEV_${devId}_PIN_ON", defaultPinOn) ?: defaultPinOn
        val pinOff = prefs.getString("DEV_${devId}_PIN_OFF", defaultPinOff) ?: defaultPinOff

        val command = if (isOn) pinOff else pinOn
        JasicaBluetoothManager.sendRawCommand(command)

        // Update state optimistically
        JasicaBluetoothManager.deviceStates[devId] = !isOn
        prefs.edit().putBoolean("DEV_$devId", !isOn).apply()
    }

    private var bottomBlobView: ComposeView? = null

    private fun showBottomVoiceBlob() {
        if (bottomBlobView != null) return
        
        playStartSound()
        
        bottomBlobView = ComposeView(this).apply {
            setContent {
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { visible = true }
                
                val transition = updateTransition(targetState = visible, label = "blob_transition")
                val offsetY by transition.animateFloat(
                    transitionSpec = { tween(400, easing = FastOutSlowInEasing) },
                    label = "offsetY"
                ) { if (it) 0f else 300f }
                
                val alpha by transition.animateFloat(
                    transitionSpec = { tween(300) },
                    label = "alpha"
                ) { if (it) 1f else 0f }

                // Continuous blob animation
                val infiniteTransition = rememberInfiniteTransition()
                val scaleX by infiniteTransition.animateFloat(
                    initialValue = 1f, targetValue = 1.1f,
                    animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse)
                )
                val scaleY by infiniteTransition.animateFloat(
                    initialValue = 1f, targetValue = 0.9f,
                    animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .graphicsLayer {
                            translationY = offsetY
                            this.alpha = alpha
                        },
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                            .graphicsLayer {
                                this.scaleX = scaleX
                                this.scaleY = scaleY
                            }
                            .size(width = 180.dp, height = 100.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(Color(0xFF007AFF), Color(0xFF5E5CE6), Color(0xFFC643FC))
                                ),
                                shape = RoundedCornerShape(50.dp)
                            )
                            .clickable {
                                // Launch MainActivity to handle Voice Command
                                val intent = Intent(this@FloatingControlService, MainActivity::class.java).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                    putExtra("START_MIC_FROM_WIDGET", true)
                                }
                                startActivity(intent)
                                
                                // Dismiss blob
                                visible = false
                                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                    removeBottomBlob()
                                }, 400)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Mic, contentDescription = "Tap to speak", tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                }
            }
        }
        
        val lifecycleOwner = MyLifecycleOwner()
        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        bottomBlobView?.setViewTreeLifecycleOwner(lifecycleOwner)
        bottomBlobView?.setViewTreeViewModelStoreOwner(object : ViewModelStoreOwner {
            override val viewModelStore = ViewModelStore()
        })
        bottomBlobView?.setViewTreeSavedStateRegistryOwner(lifecycleOwner)

        val blobParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            200 * resources.displayMetrics.density.toInt(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        }
        
        windowManager.addView(bottomBlobView, blobParams)
    }
    
    private fun removeBottomBlob() {
        bottomBlobView?.let {
            if (it.isAttachedToWindow) {
                windowManager.removeView(it)
            }
        }
        bottomBlobView = null
    }

    override fun onDestroy() {
        super.onDestroy()
        removeBottomBlob()
        mediaPlayer?.release()
        mediaPlayer = null
        if (isViewAttached) {
            windowManager.removeView(composeView)
            isViewAttached = false
        }
    }

    @Composable
    fun FloatingWidgetContent(
        onClose: () -> Unit,
        onDeviceTap: (Int) -> Unit,
        onMicTap: () -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }
        var micActive by remember { mutableStateOf(false) }

        val prefs = androidx.compose.ui.platform.LocalContext.current
            .getSharedPreferences("JasicaSettings", Context.MODE_PRIVATE)
        val dev1 = prefs.getString("DEV_1_NAME", "Device 1") ?: "Device 1"
        val dev2 = prefs.getString("DEV_2_NAME", "Device 2") ?: "Device 2"
        val dev3 = prefs.getString("DEV_3_NAME", "Device 3") ?: "Device 3"
        val dev4 = prefs.getString("DEV_4_NAME", "Device 4") ?: "Device 4"

        // Mic orb animations
        val infiniteTransition = rememberInfiniteTransition(label = "orbPulse")
        val orbRing1 by infiniteTransition.animateFloat(
            initialValue = 0.7f, targetValue = 1.4f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ), label = "ring1"
        )
        val orbRing2 by infiniteTransition.animateFloat(
            initialValue = 0.7f, targetValue = 1.4f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, delayMillis = 333, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ), label = "ring2"
        )
        val orbRing3 by infiniteTransition.animateFloat(
            initialValue = 0.7f, targetValue = 1.4f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, delayMillis = 666, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ), label = "ring3"
        )

        // Auto-dismiss mic orb after 4s
        LaunchedEffect(micActive) {
            if (micActive) {
                delay(4000)
                micActive = false
            }
        }

        Box(
            modifier = Modifier.pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    params.x = (params.x + dragAmount.x).toInt()
                    params.y = (params.y + dragAmount.y).toInt()
                    windowManager.updateViewLayout(composeView, params)
                }
            }
        ) {
            // ── Gradient mic orb overlay ──────────────────────────────────────
            AnimatedVisibility(
                visible = micActive,
                enter = fadeIn(tween(300)) + scaleIn(tween(300, easing = FastOutSlowInEasing)),
                exit  = fadeOut(tween(400)) + scaleOut(tween(400))
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    // Ripple rings
                    listOf(orbRing1, orbRing2, orbRing3).forEach { scale ->
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .scale(scale)
                                .background(
                                    brush = Brush.radialGradient(
                                        listOf(
                                            Color(0xFF007AFF).copy(alpha = (1.4f - scale).coerceIn(0f, 0.4f)),
                                            Color(0xFF5E5CE6).copy(alpha = 0f)
                                        )
                                    ),
                                    shape = CircleShape
                                )
                        )
                    }
                    // Central mic orb
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    listOf(Color(0xFF5E5CE6), Color(0xFF007AFF))
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.Mic,
                            contentDescription = "Listening",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            // ── Main widget row ───────────────────────────────────────────────
            Row(verticalAlignment = Alignment.Top) {

                // Main Jasica orb button
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                listOf(Color(0xFF338FFF), Color(0xFF007AFF))
                            )
                        )
                        .clickable { expanded = !expanded },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_ai_waves),
                        contentDescription = "Quick Access",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Expandable menu
                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn(tween(200)) + slideInHorizontally(
                        initialOffsetX = { -it / 2 },
                        animationSpec = tween(250, easing = FastOutSlowInEasing)
                    ),
                    exit = fadeOut(tween(150)) + slideOutHorizontally(
                        targetOffsetX = { -it / 2 },
                        animationSpec = tween(200)
                    )
                ) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(18.dp))
                            .padding(horizontal = 8.dp, vertical = 10.dp)
                            .width(152.dp)
                    ) {
                        // Header row: title + close button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Quick Controls",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.Black
                            )
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE5E5EA))
                                    .clickable { onClose() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Rounded.Close,
                                    contentDescription = "Exit",
                                    tint = Color(0xFF636366),
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        QuickAccessButton(dev1) { onDeviceTap(1); expanded = false }
                        QuickAccessButton(dev2) { onDeviceTap(2); expanded = false }
                        QuickAccessButton(dev3) { onDeviceTap(3); expanded = false }
                        QuickAccessButton(dev4) { onDeviceTap(4); expanded = false }

                        Spacer(Modifier.height(4.dp))
                        HorizontalDivider(color = Color(0xFFE5E5EA))
                        Spacer(Modifier.height(4.dp))

                        // Voice Command row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (micActive)
                                        Brush.horizontalGradient(listOf(Color(0xFF007AFF).copy(0.15f), Color(0xFF5E5CE6).copy(0.15f)))
                                    else
                                        Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                                )
                                .clickable {
                                    micActive = true
                                    expanded = false
                                    onMicTap()
                                }
                                .padding(6.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Mic,
                                contentDescription = "Mic",
                                tint = Color(0xFF007AFF),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Voice Command", fontSize = 12.sp, color = Color.Black)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun QuickAccessButton(name: String, onClick: () -> Unit) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { onClick() }
                .padding(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(listOf(Color(0xFF007AFF), Color(0xFF5E5CE6)))
                    )
            )
            Spacer(Modifier.width(8.dp))
            Text(name, fontSize = 12.sp, color = Color.Black, maxLines = 1)
        }
    }

    private class MyLifecycleOwner : LifecycleOwner, SavedStateRegistryOwner {
        private val lifecycleRegistry = LifecycleRegistry(this)
        private val savedStateRegistryController = SavedStateRegistryController.create(this)

        override val lifecycle: Lifecycle get() = lifecycleRegistry
        override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

        fun handleLifecycleEvent(event: Lifecycle.Event) = lifecycleRegistry.handleLifecycleEvent(event)
        fun performRestore(savedState: android.os.Bundle?) = savedStateRegistryController.performRestore(savedState)
    }
}
