package com.jasica.ai.controller.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbIncandescent
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jasica.ai.controller.bluetooth.AppBluetoothManager
import com.jasica.ai.controller.bluetooth.ConnectionState
import com.jasica.ai.controller.data.PinCommand
import com.jasica.ai.controller.ui.theme.AccentOrange
import com.jasica.ai.controller.ui.theme.AccentPurple
import com.jasica.ai.controller.ui.theme.BackgroundDark
import com.jasica.ai.controller.ui.theme.CardBorder
import com.jasica.ai.controller.ui.theme.ErrorRed
import com.jasica.ai.controller.ui.theme.GlowGreen
import com.jasica.ai.controller.ui.theme.PrimaryBlue
import com.jasica.ai.controller.ui.theme.PrimaryCyan
import com.jasica.ai.controller.ui.theme.SurfaceDark
import com.jasica.ai.controller.ui.theme.TextMuted
import com.jasica.ai.controller.ui.theme.TextPrimary
import com.jasica.ai.controller.ui.theme.TextSecondary
import com.jasica.ai.controller.ui.components.LiquidGlassSlider
import com.jasica.ai.controller.ui.components.LiquidGlassToggle
import com.jasica.ai.controller.voice.SpeechManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    bluetoothManager: AppBluetoothManager,
    speechManager: SpeechManager,
    onNavigateToSettings: () -> Unit
) {
    val connectionState by bluetoothManager.connectionState.collectAsState()
    val connectedDeviceName by bluetoothManager.connectedDeviceName.collectAsState()
    val isListening by speechManager.isListening.collectAsState()
    val lastFeedback by speechManager.lastActionFeedback.collectAsState()
    val lastTranscript by speechManager.lastTranscript.collectAsState()
    val pinList by speechManager.pinStates.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "JASICA",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            ),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            ),
                            color = PrimaryCyan
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        // Connection Badge Chip
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = when (connectionState) {
                                ConnectionState.CONNECTED -> GlowGreen.copy(alpha = 0.12f)
                                ConnectionState.CONNECTING -> AccentOrange.copy(alpha = 0.12f)
                                else -> ErrorRed.copy(alpha = 0.12f)
                            },
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                when (connectionState) {
                                    ConnectionState.CONNECTED -> GlowGreen.copy(alpha = 0.6f)
                                    ConnectionState.CONNECTING -> AccentOrange.copy(alpha = 0.6f)
                                    else -> ErrorRed.copy(alpha = 0.4f)
                                }
                            ),
                            modifier = Modifier.clickable { onNavigateToSettings() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (connectionState == ConnectionState.CONNECTED)
                                        Icons.Default.BluetoothConnected else Icons.Default.Bluetooth,
                                    contentDescription = null,
                                    tint = when (connectionState) {
                                        ConnectionState.CONNECTED -> GlowGreen
                                        ConnectionState.CONNECTING -> AccentOrange
                                        else -> ErrorRed
                                    },
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = when (connectionState) {
                                        ConnectionState.CONNECTED -> connectedDeviceName ?: "Connected"
                                        ConnectionState.CONNECTING -> "Connecting..."
                                        else -> "Offline"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (connectionState) {
                                        ConnectionState.CONNECTED -> GlowGreen
                                        ConnectionState.CONNECTING -> AccentOrange
                                        else -> TextMuted
                                    }
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .background(SurfaceDark.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = PrimaryCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Holographic Animated Globe / Voice Button
            GlowingGlobeButton(
                isListening = isListening,
                onClick = {
                    if (isListening) {
                        speechManager.stopListening()
                    } else {
                        speechManager.startListening()
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Speech Transcript & Action Feedback HUD
            Text(
                text = if (isListening) "Listening to voice input..." else if (lastTranscript.isNotEmpty()) "\"$lastTranscript\"" else "Tap Orb to Speak",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isListening) PrimaryCyan else TextPrimary,
                    letterSpacing = 0.5.sp
                ),
                textAlign = TextAlign.Center
            )

            if (lastFeedback.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceDark.copy(alpha = 0.85f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.35f)),
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Text(
                        text = lastFeedback,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PrimaryCyan,
                            fontWeight = FontWeight.SemiBold
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Macro Action Strips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MacroButton(
                    title = "ALL ON",
                    icon = Icons.Default.PowerSettingsNew,
                    accentColor = GlowGreen,
                    modifier = Modifier.weight(1f),
                    onClick = { speechManager.processVoiceCommand("all on") }
                )
                MacroButton(
                    title = "ALL OFF",
                    icon = Icons.Default.PowerSettingsNew,
                    accentColor = ErrorRed,
                    modifier = Modifier.weight(1f),
                    onClick = { speechManager.processVoiceCommand("all off") }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MacroButton(
                    title = "MOOD MODE",
                    icon = Icons.Default.NightlightRound,
                    accentColor = AccentPurple,
                    modifier = Modifier.weight(1f),
                    onClick = { speechManager.processVoiceCommand("mood") }
                )
                MacroButton(
                    title = "WATER (30M)",
                    icon = Icons.Default.LocalDrink,
                    accentColor = PrimaryCyan,
                    modifier = Modifier.weight(1f),
                    onClick = { speechManager.processVoiceCommand("remind me to drink water") }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Liquid Glass Slider Controller Section
            var sliderValue by remember { mutableFloatStateOf(70f) }
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "INTENSITY / PWM OUTPUT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "${sliderValue.toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryCyan
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LiquidGlassSlider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        valueRange = 0f..100f
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 12-Pin Control Grid Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ARDUINO RELAY MATRIX (PINS 2 - 13)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextSecondary
                    )
                )
                Text(
                    text = "Liquid Toggle",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GlowGreen
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 12-Pin Control Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(pinList, key = { it.pinNumber }) { pin ->
                    PinCard(
                        pin = pin,
                        onToggle = { speechManager.togglePinDirectly(pin) }
                    )
                }
            }
        }
    }
}

@Composable
fun GlowingGlobeButton(
    isListening: Boolean,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.28f else 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening) 700 else 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "globePulse"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "hudRotation"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(136.dp)
            .clickable(onClick = onClick)
    ) {
        // Rotating Orbital Ring
        Box(
            modifier = Modifier
                .size(134.dp)
                .rotate(rotationAngle)
                .border(
                    1.dp,
                    if (isListening) ErrorRed.copy(alpha = 0.5f) else PrimaryCyan.copy(alpha = 0.35f),
                    CircleShape
                )
        )

        // Outer glow pulsation ring
        Box(
            modifier = Modifier
                .size(118.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(
                    if (isListening) ErrorRed.copy(alpha = 0.3f)
                    else PrimaryBlue.copy(alpha = 0.22f)
                )
        )

        // Middle holographic core gradient
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(92.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = if (isListening) listOf(Color(0xFFFF3366), Color(0xFFD50000), Color(0xFF6B0000))
                        else listOf(PrimaryCyan, PrimaryBlue, Color(0xFF1E32AA))
                    )
                )
                .border(
                    2.dp,
                    if (isListening) Color(0xFFFF8A80) else Color(0xFF80D8FF),
                    CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Globe Mic Button",
                tint = TextPrimary,
                modifier = Modifier.size(44.dp)
            )
        }
    }
}

@Composable
fun MacroButton(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.45f)),
        modifier = modifier
            .height(48.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(accentColor.copy(alpha = 0.12f), Color.Transparent)
                    )
                )
                .padding(horizontal = 10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun PinCard(
    pin: PinCommand,
    onToggle: () -> Unit
) {
    val isStateOn = pin.isStateOn
    val cardBorderColor by animateColorAsState(
        targetValue = if (isStateOn) GlowGreen.copy(alpha = 0.8f) else CardBorder,
        label = "borderColor"
    )
    val cardBgColor by animateColorAsState(
        targetValue = if (isStateOn) SurfaceDark else Color(0xFF10121A),
        label = "bgColor"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorderColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isStateOn) Brush.verticalGradient(
                        listOf(GlowGreen.copy(alpha = 0.12f), Color.Transparent)
                    ) else Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Transparent)
                    )
                )
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pin Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isStateOn) GlowGreen.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)
                ) {
                    Text(
                        text = "PIN ${pin.pinNumber}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isStateOn) GlowGreen else TextMuted,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }

                // Command Char Tag
                Text(
                    text = "Char: '${if (isStateOn) pin.onChar else pin.offChar}'",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Label
            Text(
                text = pin.label,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Switch & State Indicator with LiquidGlassToggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isStateOn) Icons.Default.WbSunny else Icons.Default.WbIncandescent,
                        contentDescription = null,
                        tint = if (isStateOn) GlowGreen else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (isStateOn) "ON" else "OFF",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp,
                        color = if (isStateOn) GlowGreen else TextMuted
                    )
                }

                // Exact Liquid Glass Toggle Component
                LiquidGlassToggle(
                    checked = isStateOn,
                    onCheckedChange = { onToggle() }
                )
            }
        }
    }
}
