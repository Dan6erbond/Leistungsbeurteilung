import requests 

url = "https://us-central1-choreapp.cloudfunctions.net/getToken"

data = {"data": {"u_id": "1234"}} 

r = requests.post(url=url, data=data)
print(r.text)
