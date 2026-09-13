import urllib.request
import re

req = urllib.request.Request('https://www.youtube.com/results?search_query=Dekhechi+Rupsagore+moner+manus', headers={'User-Agent': 'Mozilla/5.0'})
try:
    html = urllib.request.urlopen(req).read().decode('utf-8')
    match = re.search(r'"videoId":"([^"]+)"', html)
    if match:
        print(match.group(1))
    else:
        print("Not found")
except Exception as e:
    print(e)
