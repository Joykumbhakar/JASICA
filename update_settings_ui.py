import re
file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Remove UI for API Key and Model in SettingsScreen
# First, let's find the exact block for the AI tab
old_ai_tab = """                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Text("API KEY", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontFamily = InterFontFamily)
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = apiKeyInput,
                            onValueChange = { apiKeyInput = it },
                            singleLine = true,
                            textStyle = TextStyle(color = Color.White, fontFamily = InterFontFamily, fontSize = 14.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                                cursorColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(24.dp))

                        Text("AI MODEL", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontFamily = InterFontFamily)
                        Spacer(Modifier.height(8.dp))

                        AiModelsList.forEach { modelName ->
                            val isSelected = selectedModel == modelName
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color.White.copy(alpha = 0.2f) else Color.Transparent)
                                    .border(1.dp, if (isSelected) Color.White else Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .clickable { selectedModel = modelName }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (isSelected) Color.White else Color.Transparent))
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = modelName,
                                    color = Color.White,
                                    fontFamily = InterFontFamily,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Row("""

new_ai_tab = """                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Spacer(Modifier.height(16.dp))

                        Row("""

content = content.replace(old_ai_tab, new_ai_tab)

# Also force model to gemini-2.5-flash in onSave
content = content.replace('onClick = { onSave(apiKeyInput, selectedModel, wakeWordInput) }', 'onClick = { onSave(apiKeyInput, "gemini-2.5-flash", wakeWordInput) }')

# Hardcode in onCreate
old_model_init = 'userSelectedModel.value = sharedPrefs.getString("AI_MODEL", AiModelsList[0]) ?: AiModelsList[0]'
new_model_init = 'userSelectedModel.value = "gemini-2.5-flash"'
# Wait, check if AiModelsList[0] is used or "gemini-2.5-flash"
content = re.sub(r'userSelectedModel\.value = sharedPrefs\.getString\("AI_MODEL",.*?\)', 'userSelectedModel.value = "gemini-2.5-flash"', content)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated Settings UI")
