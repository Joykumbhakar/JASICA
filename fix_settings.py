import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# 1. Change useAdminPanelKey default to false
content = re.sub(
    r'private val useAdminPanelKey\s*=\s*mutableStateOf\(true\)',
    'private val useAdminPanelKey    = mutableStateOf(false)',
    content
)
content = re.sub(
    r'useAdminPanelKey\.value\s*=\s*sharedPrefs\.getBoolean\("USE_ADMIN_PANEL_KEY",\s*true\)',
    'useAdminPanelKey.value    = sharedPrefs.getBoolean("USE_ADMIN_PANEL_KEY", false)',
    content
)

# 2. Change AppleSettingsGroup corner radius to 24.dp
# Find AppleSettingsGroup specifically
pattern = re.compile(r'(fun AppleSettingsGroup.*?\.clip\(RoundedCornerShape\()16(\.dp\)\)\s*\.background\(cardBg\)\s*\.border\(1\.dp,\s*cardBorder,\s*RoundedCornerShape\()16(\.dp\)\))', re.DOTALL)
content = pattern.sub(r'\g<1>24\g<2>24\g<3>', content)

# 3. Add Version info, Privacy Policy, Terms & Conditions to SettingsScreen
old_settings_footer = """                item {
                    Spacer(Modifier.height(40.dp))
                }
            }
        }"""
new_settings_footer = """                item {
                    Spacer(Modifier.height(30.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "JASICA Flash-2.5 v1.2.13.09.2026 (beta)",
                            color = textSecondary,
                            fontSize = 12.sp,
                            fontFamily = InterFontFamily,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Privacy Policy",
                                color = Color(0xFF007AFF),
                                fontSize = 12.sp,
                                fontFamily = InterFontFamily,
                                modifier = Modifier.clickable {
                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://jasicaai.vercel.app/privacy-policy"))
                                    context.startActivity(intent)
                                }.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                            Text("|", color = textSecondary, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp))
                            Text(
                                text = "Terms & Conditions",
                                color = Color(0xFF007AFF),
                                fontSize = 12.sp,
                                fontFamily = InterFontFamily,
                                modifier = Modifier.clickable {
                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://jasicaai.vercel.app/terms-conditions"))
                                    context.startActivity(intent)
                                }.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(40.dp))
                }
            }
        }"""
content = content.replace(old_settings_footer, new_settings_footer)

# 4. Fix Reset Devices to Default (prompt restart to apply fully)
old_reset_dialog = """showResetConfirmDialog = false
                    android.widget.Toast.makeText(context, "Devices Reset to Defaults", android.widget.Toast.LENGTH_SHORT).show()"""
new_reset_dialog = """showResetConfirmDialog = false
                    android.widget.Toast.makeText(context, "Devices Reset. Restarting app...", android.widget.Toast.LENGTH_SHORT).show()
                    context.startActivity(android.content.Intent(context, MainActivity::class.java).apply {
                        addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    })
                    Runtime.getRuntime().exit(0)"""
content = content.replace(old_reset_dialog, new_reset_dialog)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Done fixing settings.")
