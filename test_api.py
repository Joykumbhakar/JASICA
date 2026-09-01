import urllib.request
try:
    with urllib.request.urlopen("https://joykumbhakar.vercel.app/api/get-gemini-key") as response:
        print(response.read().decode("utf-8"))
except Exception as e:
    print(f"Error: {e}")
