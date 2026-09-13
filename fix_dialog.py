import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

old_dialog = r"""updateNotification\?\.let \{ notif ->
\s*AlertDialog\(
.*?
\s*containerColor = Color\(0xFF1E1E2A\),
.*?
\s*\)
\s*\}"""

new_dialog = """updateNotification?.let { notif ->
            androidx.compose.ui.window.Dialog(
                onDismissRequest = { 
                    sharedPrefs.edit().putString("LAST_SEEN_NOTIFICATION", notif.id).apply()
                    updateNotification = null 
                },
                properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(20.dp)
                    ) {
                        if (notif.imageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = notif.imageUrl,
                                contentDescription = "Update Banner",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(1.dp, Color.Black.copy(alpha=0.1f), RoundedCornerShape(16.dp)),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                            Spacer(Modifier.height(20.dp))
                        }
                        Text(notif.title, color = Color(0xFF1C1C1E), fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontFamily = InterFontFamily)
                        Spacer(Modifier.height(10.dp))
                        Text(notif.description, color = Color(0xFF3A3A3C), fontSize = 15.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, maxLines = 4, fontFamily = InterFontFamily, lineHeight = 22.sp)
                        Spacer(Modifier.height(24.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            TextButton(
                                onClick = {
                                    sharedPrefs.edit().putString("LAST_SEEN_NOTIFICATION", notif.id).apply()
                                    updateNotification = null
                                },
                                modifier = Modifier.weight(1f).height(50.dp)
                            ) {
                                Text(notif.secondaryButtonText, color = Color.Gray, fontWeight = FontWeight.SemiBold, fontFamily = InterFontFamily)
                            }
                            Button(
                                onClick = {
                                    if (notif.primaryButtonUrl.isNotEmpty()) {
                                        try {
                                            context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(notif.primaryButtonUrl)))
                                        } catch(e:Exception){}
                                    }
                                    sharedPrefs.edit().putString("LAST_SEEN_NOTIFICATION", notif.id).apply()
                                    updateNotification = null
                                },
                                modifier = Modifier.weight(1f).height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9500)),
                                shape = CircleShape
                            ) {
                                Text(notif.primaryButtonText, color = Color.White, fontWeight = FontWeight.Bold, fontFamily = InterFontFamily)
                            }
                        }
                    }
                    
                    IconButton(
                        onClick = {
                            sharedPrefs.edit().putString("LAST_SEEN_NOTIFICATION", notif.id).apply()
                            updateNotification = null
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(32.dp)
                            .background(Color.Black.copy(alpha=0.3f), CircleShape)
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close", modifier = Modifier.size(18.dp), tint = Color.White)
                    }
                }
            }
        }"""

content = re.sub(old_dialog, new_dialog, content, flags=re.DOTALL)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Updated dialog")
