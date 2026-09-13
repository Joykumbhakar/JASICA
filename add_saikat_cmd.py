import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# Add command
saikat_cmd = """
        commands.add(LocalCommand(listOf("play", "saikat", "song"), anyOf = listOf("saikat", "saikat's"), command = "SYS_YT_SAIKAT", confirmationText = "??? ???, ??? ?????? ????? ??? ??? ?????????? ???? ????!"))
"""
content = content.replace('commands.add(LocalCommand(listOf("play","song"), anyOf = listOf("fav","favorite","favourite","sad","sad song","favorite song"), command = "SYS_YT_FAV", confirmationText = "??? ???, ??? ????? ????? ??? ???? ????, ???? ??? ???? ??? ?? ??? ????? ???? ???!"))', saikat_cmd + '        commands.add(LocalCommand(listOf("play","song"), anyOf = listOf("fav","favorite","favourite","sad","sad song","favorite song"), command = "SYS_YT_FAV", confirmationText = "??? ???, ??? ????? ????? ??? ???? ????, ???? ??? ???? ??? ?? ??? ????? ???? ???!"))')

# Add SYS_YT_SAIKAT interceptor
saikat_interceptor = """
        if (command == "SYS_YT_SAIKAT") {
            playSaikatSongOnYouTube()
            return
        }
"""
content = content.replace('        if (command == "SYS_YT_FAV") {\n            playFavoriteSongOnYouTube()\n            return\n        }', saikat_interceptor + '        if (command == "SYS_YT_FAV") {\n            playFavoriteSongOnYouTube()\n            return\n        }')

# Add function
saikat_func = """
    private fun playSaikatSongOnYouTube() {
        val videoId = "lhV2bCBo-8k"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            setPackage("com.google.android.youtube") // Force open in YouTube app
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
    }
"""
content = content.replace('    private fun playFavoriteSongOnYouTube() {', saikat_func + '\n    private fun playFavoriteSongOnYouTube() {')

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Added Saikat cmd")
