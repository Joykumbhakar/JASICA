import re

# 1. Modify FloatingControlService.kt
path_fc = r"E:\Controller\app\src\main\java\com\bristi\controller\FloatingControlService.kt"
with open(path_fc, "r", encoding="utf-8") as f:
    text_fc = f.read()

# First, change onMicTap in setupFloatingView
target_setup = """                    onMicTap = {
                        playStartSound()
                        sendMicBroadcast()
                    }"""

replacement_setup = """                    onMicTap = {
                        showBottomVoiceBlob()
                    }"""
text_fc = text_fc.replace(target_setup, replacement_setup)

# Second, remove sendMicBroadcast and add showBottomVoiceBlob
target_broadcast = """    private fun sendMicBroadcast() {
        val intent = Intent("com.bristi.controller.START_MIC")
        sendBroadcast(intent)
    }"""

replacement_blob = """    private var bottomBlobView: ComposeView? = null

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
    }"""
text_fc = text_fc.replace(target_broadcast, replacement_blob)

# Remove the orb overlay in the FloatingWidgetContent
target_overlay = """            // -- Gradient mic orb overlay --------------------------------------
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
            }"""

text_fc = text_fc.replace(target_overlay, "")

# Remove onDestroy micActive dependency if any, and remove bottomBlobView
target_destroy = """    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null"""
replacement_destroy = """    override fun onDestroy() {
        super.onDestroy()
        removeBottomBlob()
        mediaPlayer?.release()
        mediaPlayer = null"""
text_fc = text_fc.replace(target_destroy, replacement_destroy)


with open(path_fc, "w", encoding="utf-8") as f:
    f.write(text_fc)


# 2. Modify MainActivity.kt to handle START_MIC_FROM_WIDGET
path_main = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path_main, "r", encoding="utf-8") as f:
    text_main = f.read()

target_oncreate = """    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)"""

replacement_oncreate = """    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra("START_MIC_FROM_WIDGET", false)) {
            mainHandler.postDelayed({
                if (appState.value != AppState.LISTENING) {
                    startListening()
                }
            }, 300)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.getBooleanExtra("START_MIC_FROM_WIDGET", false)) {
            mainHandler.postDelayed({
                if (appState.value != AppState.LISTENING) {
                    startListening()
                }
            }, 1000)
        }"""
text_main = text_main.replace(target_oncreate, replacement_oncreate)

with open(path_main, "w", encoding="utf-8") as f:
    f.write(text_main)

print("Modifications applied successfully")
