import base64
from hashlib import md5
import json
import math
import os

import requests

from server import common, config


def get_url():
	host = config.get_config()["host"]
	port = config.get_config()["port"]
	return f"http://{host}:{port}"

def test_get_file():
	# sends request with signature in the header -> response is the file list
	js = json.loads(requests.get(get_url() + "/files", headers=common.sign_request("")).content)

	js.sort(key=lambda x: x["size"])

	# download all files
	for f in js:
		download_file(f)

def download_file(file):
	print(file["name"])

	# downloads to test/
	path = os.path.join("test", file["name"])

	# create any directories missing in the path
	try:
		dr = "/".join(path.split("/")[:-1])
		os.mkdir(dr)
	except:
		pass

	# reserve file size
	with open(path, "wb") as f:
		f.write(bytes(b"\0") * file["size"])

	# download each chunk
	for chunk in file["chunks"]:
		hs = chunk["id"]
		bt = requests.get(f"{get_url()}/files/{hs}", headers = common.sign_request(""))

		if bt.status_code != 200:
			print(bt.content)
			break
		else:
			# overwrite with offset
			with open(path, "rb+") as f:
				f.seek(chunk["offset"])
				f.write(bt.content)

def test_upload_file():
	print("uploading")

	fname = "upload.mp3"
	# upload test file path
	path = os.path.join("test", fname)

	# get some metadata about the file
	size = os.path.getsize(path)
	chunk_size = config.get_config()["chunk_size"]
	chunk_n = math.ceil(size / chunk_size)

	chunks = []

	# get hashes of chunks
	f = open(path, "rb")
	for i in range(chunk_n):
		bt = f.read(chunk_size)
		hs = base64.urlsafe_b64encode(
			md5(
				bytearray(bt) + 
				fname.encode("utf-8") + 
				str(chunk_size * i).encode("utf-8")
			).digest()
		).decode("utf-8")

		chunks.append(
			{
				"offset": chunk_size * i,
				"id": hs
			}
		)
	f.close()

	# prepare request
	body = json.dumps({
		"name": fname,
		"size": size, 
		"chunk-n": chunk_n, 
		"chunk-size": chunk_size,
		"chunks": chunks
	}).encode("utf-8")

	print("r1")
	# send and assert correct
	r = requests.post(f"{get_url()}/file", data=body, headers=common.sign_request(body))
	if r.status_code != 200:
		print(r.content)
		return

	print("chunks")
	# upload file chunk by chunk
	for c in chunks:
		hs = c["id"]
		offset = c["offset"]

		with open(path, "rb") as f:
			f.seek(offset)
			body = f.read(chunk_size)

		r = requests.post(f"{get_url()}/files/{hs}", data=body, headers=common.sign_request(body))
		if r.status_code != 200:
			print(r.content)
			return




def main():
	# some manual endpoint tests
	# print(requests.get(get_url(), headers=sign_request("")).content)
	# print(requests.get(get_url() + "/files", headers=sign_request("")).content)
	# print(requests.get(get_url() + "/files?requestThumbnails=1", headers=sign_request("")).content[:2000])
	# print(requests.get(get_url() + "/files?requestReload=1", headers=sign_request("")).content)
	# print(requests.get(get_url() + "/files?addedAfter=0000.00.00 00:00", headers=sign_request("")).content)

	test_get_file()

	test_upload_file()
	pass