import re

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "r", encoding="utf-8") as f:
    text = f.read()

# 1. Add hazeState to JasicaScreen
text = text.replace(
    'val haptic = LocalHapticFeedback.current\n    Box(modifier = Modifier.fillMaxSize()) {',
    'val hazeState = remember { dev.chrisbanes.haze.HazeState() }\n    val haptic = LocalHapticFeedback.current\n    Box(modifier = Modifier.fillMaxSize().haze(state = hazeState)) {'
)

# 2. Add hazeState to DeviceSelectionDialog call
text = text.replace(
    'DeviceSelectionDialog(pairedDevices, availableDevices, isScanning, onDeviceSelect, onScanTap, onDismissDialog)',
    'androidx.activity.compose.BackHandler { onDismissDialog() }\n            DeviceSelectionDialog(pairedDevices, availableDevices, isScanning, hazeState, onDeviceSelect, onScanTap, onDismissDialog)'
)

# 3. Modify DeviceSelectionDialog signature and implementation
old_dialog = """@SuppressLint("MissingPermission")
@Composable
fun DeviceSelectionDialog(pairedDevices: List<BluetoothDevice>, availableDevices: List<BluetoothDevice>, isScanning: Boolean, onDeviceSelect: (BluetoothDevice) -> Unit, onScanTap: () -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        JasicaGraphicalDialogPanel {"""

new_dialog = """@SuppressLint("MissingPermission")
@Composable
fun DeviceSelectionDialog(pairedDevices: List<BluetoothDevice>, availableDevices: List<BluetoothDevice>, isScanning: Boolean, hazeState: dev.chrisbanes.haze.HazeState?, onDeviceSelect: (BluetoothDevice) -> Unit, onScanTap: () -> Unit, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures(onTap = { onDismiss() }) },
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.padding(24.dp).pointerInput(Unit) { detectTapGestures { /* consume */ } }) {
            JasicaGraphicalDialogPanel(hazeState = hazeState) {"""

text = text.replace(old_dialog, new_dialog)

# We need to replace the end of DeviceSelectionDialog.
text = re.sub(
    r'(items\(availableDevices.*?\}\n\s*\}\n\s*\}\n\s*\}\n)\s*\}',
    r'\1        }\n    }',
    text,
    flags=re.DOTALL
)

# 4. Modify JasicaGraphicalDialogPanel
old_panel = """@Composable
fun JasicaGraphicalDialogPanel(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xE61C1C1E))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
        ) {"""

new_panel = """@Composable
fun JasicaGraphicalDialogPanel(hazeState: dev.chrisbanes.haze.HazeState? = null, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
    ) {
        val baseModifier = Modifier
            .fillMaxWidth()
            .then(if (hazeState != null) Modifier.hazeChild(state = hazeState, shape = RoundedCornerShape(24.dp), style = dev.chrisbanes.haze.HazeStyle(blurRadius = 20.dp, backgroundColor = Color(0x661C1C1E), tint = dev.chrisbanes.haze.HazeTint(Color(0x331C1C1E)))) else Modifier)
            .background(if (hazeState != null) Color.Transparent else Color(0xE61C1C1E))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
        Box(modifier = baseModifier) {"""

text = text.replace(old_panel, new_panel)

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "w", encoding="utf-8") as f:
    f.write(text)
