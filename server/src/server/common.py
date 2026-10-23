import base64
import datetime
from hashlib import sha512
import random
import string

from server import config

chars = list(string.ascii_lowercase + string.ascii_uppercase + string.digits)

def get_current_date():
	return datetime.datetime.now().strftime("%Y.%m.%d %H:%M")

def get_not_ready_date():
	return "9999.99.99 99:99"	

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
