file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

content = content.replace('// "ReBHEyAd2zk" is the official Audio for Elvis Presley - Can\'t Help Falling in Love', '// "ReBHEyAd2zk" is the video for Dekhechi Rupsagore Moner Manush')

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Updated comment")
