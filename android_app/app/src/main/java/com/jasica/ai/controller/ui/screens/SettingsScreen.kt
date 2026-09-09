package com.jasica.ai.controller.ui.screens

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jasica.ai.controller.bluetooth.AppBluetoothManager
import com.jasica.ai.controller.bluetooth.BluetoothDeviceItem
import com.jasica.ai.controller.bluetooth.ConnectionState
import com.jasica.ai.controller.data.CommandPreferences
import com.jasica.ai.controller.data.PinCommand
import com.jasica.ai.controller.ui.theme.AccentOrange
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
import com.jasica.ai.controller.voice.SpeechManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    bluetoothManager: AppBluetoothManager,
    speechManager: SpeechManager,
    commandPreferences: CommandPreferences,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val isBtEnabled by bluetoothManager.isBluetoothEnabled.collectAsState()
    val connectionState by bluetoothManager.connectionState.collectAsState()
    val connectedDeviceName by bluetoothManager.connectedDeviceName.collectAsState()
    val pairedDevices by bluetoothManager.pairedDevices.collectAsState()
    val discoveredDevices by bluetoothManager.discoveredDevices.collectAsState()
    val isScanning by bluetoothManager.isScanning.collectAsState()
    val statusMsg by bluetoothManager.statusMessage.collectAsState()

    var pinList by remember { mutableStateOf(commandPreferences.loadPinList()) }
    var editingPin by remember { mutableStateOf<PinCommand?>(null) }

    val enableBtLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            bluetoothManager.updateBtState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings & Device Mapping",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. BLUETOOTH STATUS & CONTROLS
            item {
                Text(
                    text = "BLUETOOTH CONNECTION",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isBtEnabled) Icons.Default.BluetoothConnected else Icons.Default.Bluetooth,
                                    contentDescription = null,
                                    tint = if (isBtEnabled) PrimaryCyan else ErrorRed,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (isBtEnabled) "Bluetooth is ON" else "Bluetooth is OFF",
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = when (connectionState) {
                                            ConnectionState.CONNECTED -> "Connected to ${connectedDeviceName ?: "Device"}"
                                            ConnectionState.CONNECTING -> "Connecting..."
                                            else -> "Not connected"
                                        },
                                        fontSize = 12.sp,
                                        color = when (connectionState) {
                                            ConnectionState.CONNECTED -> GlowGreen
                                            ConnectionState.CONNECTING -> AccentOrange
                                            else -> TextMuted
                                        }
                                    )
                                }
                            }

                            if (!isBtEnabled) {
                                Button(
                                    onClick = {
                                        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                                        enableBtLauncher.launch(intent)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                ) {
                                    Text("Turn ON")
                                }
                            } else if (connectionState == ConnectionState.CONNECTED) {
                                OutlinedButton(
                                    onClick = { bluetoothManager.disconnect() },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
                                ) {
                                    Text("Disconnect")
                                }
                            }
                        }

                        if (statusMsg.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = statusMsg,
                                fontSize = 12.sp,
                                color = PrimaryCyan
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Scan & Refresh Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (isScanning) bluetoothManager.stopDiscovery()
                                    else bluetoothManager.startDiscovery()
                                },
                                enabled = isBtEnabled,
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                modifier = Modifier.weight(1f)
                            ) {
                                if (isScanning) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = TextPrimary,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Scanning...")
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.BluetoothSearching,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Scan Devices")
                                }
                            }

                            OutlinedButton(
                                onClick = { bluetoothManager.refreshPairedDevices() },
                                enabled = isBtEnabled,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                            ) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
                            }
                        }
                    }
                }
            }

            // 2. PAIRED DEVICES LIST
            item {
                Text(
                    text = "PAIRED DEVICES (HC-05 / ESP32)",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp)
                )
            }

            if (pairedDevices.isEmpty()) {
                item {
                    Text(
                        text = if (isBtEnabled) "No paired devices found. Please pair in Android Settings or scan below." else "Turn on Bluetooth to view devices.",
                        fontSize = 13.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            } else {
                items(pairedDevices) { device ->
                    DeviceItemCard(
                        device = device,
                        isConnected = connectionState == ConnectionState.CONNECTED && connectedDeviceName == device.name,
                        isConnecting = connectionState == ConnectionState.CONNECTING,
                        onConnect = { bluetoothManager.connectToDevice(device.address) }
                    )
                }
            }

            // 3. DISCOVERED DEVICES LIST
            if (discoveredDevices.isNotEmpty()) {
                item {
                    Text(
                        text = "DISCOVERED DEVICES",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp)
                    )
                }
                items(discoveredDevices) { device ->
                    DeviceItemCard(
                        device = device,
                        isConnected = false,
                        isConnecting = false,
                        onConnect = { bluetoothManager.connectToDevice(device.address) }
                    )
                }
            }

            // 4. CUSTOM VOICE COMMAND MAPPING HEADER
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CUSTOM VOICE COMMANDS (PINS 2-13)",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp)
                    )

                    TextButton(
                        onClick = {
                            pinList = commandPreferences.resetToDefaults()
                            speechManager.reloadCommandList()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset Defaults", fontSize = 12.sp, color = AccentOrange)
                    }
                }
            }

            // 5. LIST OF EDITABLE PIN COMMANDS
            items(pinList) { pin ->
                CommandMappingCard(
                    pin = pin,
                    onEdit = { editingPin = pin }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // EDIT COMMAND DIALOG
    editingPin?.let { targetPin ->
        EditCommandDialog(
            pin = targetPin,
            onDismiss = { editingPin = null },
            onSave = { updatedPin ->
                val updatedList = pinList.map { if (it.pinNumber == updatedPin.pinNumber) updatedPin else it }
                pinList = updatedList
                commandPreferences.savePinList(updatedList)
                speechManager.reloadCommandList()
                editingPin = null
            }
        )
    }
}

@Composable
fun DeviceItemCard(
    device: BluetoothDeviceItem,
    isConnected: Boolean,
    isConnecting: Boolean,
    onConnect: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isConnected) GlowGreen else CardBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onConnect)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Text(
                    text = device.address,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            if (isConnected) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GlowGreen.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "CONNECTED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlowGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            } else {
                Button(
                    onClick = onConnect,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Connect", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun CommandMappingCard(
    pin: PinCommand,
    onEdit: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = PrimaryCyan.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "PIN ${pin.pinNumber}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryCyan,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = pin.label,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "ON: \"${pin.onPhrase}\" -> '${pin.onChar}'",
                    fontSize = 12.sp,
                    color = GlowGreen
                )
                Text(
                    text = "OFF: \"${pin.offPhrase}\" -> '${pin.offChar}'",
                    fontSize = 12.sp,
                    color = ErrorRed
                )
            }

            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Command",
                    tint = PrimaryCyan
                )
            }
        }
    }
}

@Composable
fun EditCommandDialog(
    pin: PinCommand,
    onDismiss: () -> Unit,
    onSave: (PinCommand) -> Unit
) {
    var label by remember { mutableStateOf(pin.label) }
    var onPhrase by remember { mutableStateOf(pin.onPhrase) }
    var offPhrase by remember { mutableStateOf(pin.offPhrase) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Pin ${pin.pinNumber} Commands",
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Device Name / Label") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryCyan,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = onPhrase,
                    onValueChange = { onPhrase = it },
                    label = { Text("Voice Phrase for ON (sends '${pin.onChar}')") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GlowGreen,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = offPhrase,
                    onValueChange = { offPhrase = it },
                    label = { Text("Voice Phrase for OFF (sends '${pin.offChar}')") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ErrorRed,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        pin.copy(
                            label = label.trim(),
                            onPhrase = onPhrase.trim(),
                            offPhrase = offPhrase.trim()
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        },
        containerColor = SurfaceDark
    )
}
