package com.bristi.controller

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

class FloatingControlService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var composeView: ComposeView
    private lateinit var params: WindowManager.LayoutParams
    private var isViewAttached = false

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
                    onDeviceTap = { cmd -> sendCommandBroadcast(cmd) },
                    onMicTap = { sendMicBroadcast() }
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 300
        }

        windowManager.addView(composeView, params)
        isViewAttached = true
    }

    private fun sendCommandBroadcast(deviceIndex: Int) {
        val intent = Intent("com.bristi.controller.SEND_QUICK_COMMAND")
        intent.putExtra("device_index", deviceIndex)
        sendBroadcast(intent)
    }
    
    private fun sendMicBroadcast() {
        val intent = Intent("com.bristi.controller.START_MIC")
        sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isViewAttached) {
            windowManager.removeView(composeView)
            isViewAttached = false
        }
    }
    
    @Composable
    fun FloatingWidgetContent(onClose: () -> Unit, onDeviceTap: (Int) -> Unit, onMicTap: () -> Unit) {
        var expanded by remember { mutableStateOf(false) }
        
        val prefs = androidx.compose.ui.platform.LocalContext.current.getSharedPreferences("JasicaSettings", Context.MODE_PRIVATE)
        val dev1 = prefs.getString("DEV_1_NAME", "Device 1") ?: "Device 1"
        val dev2 = prefs.getString("DEV_2_NAME", "Device 2") ?: "Device 2"
        val dev3 = prefs.getString("DEV_3_NAME", "Device 3") ?: "Device 3"
        val dev4 = prefs.getString("DEV_4_NAME", "Device 4") ?: "Device 4"

        Box(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        params.x = (params.x + dragAmount.x).toInt()
                        params.y = (params.y + dragAmount.y).toInt()
                        windowManager.updateViewLayout(composeView, params)
                    }
                }
        ) {
            Row(verticalAlignment = Alignment.Top) {
                // Jasica Icon (Main Toggle)
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF007AFF))
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
                
                if (expanded) {
                    Spacer(modifier = Modifier.width(8.dp))
                    // Menu Box
                    Column(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .padding(8.dp)
                            .width(140.dp)
                    ) {
                        Text("Quick Controls", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black, modifier = Modifier.padding(bottom = 8.dp))
                        
                        QuickAccessButton(dev1) { onDeviceTap(1) }
                        QuickAccessButton(dev2) { onDeviceTap(2) }
                        QuickAccessButton(dev3) { onDeviceTap(3) }
                        QuickAccessButton(dev4) { onDeviceTap(4) }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        HorizontalDivider(color = Color.LightGray.copy(alpha=0.5f))
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Mic Button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).clickable { onMicTap(); expanded = false }.padding(6.dp)
                        ) {
                            Icon(Icons.Rounded.Mic, contentDescription = "Mic", tint = Color(0xFF007AFF), modifier = Modifier.size(20.dp))
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
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF007AFF)))
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
