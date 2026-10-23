import base64
from hashlib import sha512
import json
import os
import random
import string

import requests
from server import config

chars = list(string.ascii_lowercase + string.ascii_uppercase + string.digits)

def get_url():
	host = config.get_config()["host"]
	port = config.get_config()["port"]
	return f"http://{host}:{port}/"

def sign_request(body):
    body_size = len(body)
    nonce = "".join(random.sample(chars, 16))

    data = bytearray(config.get_config()["password"].encode("utf-8")) + nonce.encode("utf-8")
    if body_size != 0:
        data = data + body

    digest = base64.urlsafe_b64encode(sha512(data).digest())

    sign = digest.decode("utf-8")
    return {
		"RandomString": nonce,
		"MessageSignature": sign
	}

def download_file(file):
	print(file["name"])

	path = os.path.join("test", file["name"])

	dr = "/".join(path.split("/")[:-1])
	try:
		os.mkdir(dr)
	except:
		pass

	with open(path, "wb") as f:
		f.write(bytes(b"\0") * file["size"])

	for chunk in file["chunks"]:
		# print(chunk)
		hs = chunk["id"]
		bt = requests.get(f"{get_url()}/files/{hs}", headers = sign_request(""))
		if bt.status_code != 200:
			print(bt.content)
			break
		else:
			with open(path, "rb+") as f:
				f.seek(chunk["offset"])
				f.write(bt.content)


def test_get_file():
	js = json.loads(requests.get(get_url() + "/files", headers=sign_request("")).content)

	js.sort(key=lambda x: x["size"])

	for f in js:
		download_file(f)


def main():
	# print(requests.get(get_url(), headers=sign_request("")).content)
	# print(requests.get(get_url() + "/files", headers=sign_request("")).content)
	# print(requests.get(get_url() + "/files?requestThumbnails=1", headers=sign_request("")).content[:2000])
	# print(requests.get(get_url() + "/files?requestReload=1", headers=sign_request("")).content)
	# print(requests.get(get_url() + "/files?addedAfter=0000.00.00 00:00", headers=sign_request("")).content)


	test_get_file()


