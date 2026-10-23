import base64
import os
import random

from server import config
from PIL import Image
import pathlib

IMAGE_THUMBNAIL_SIZE = 128
IMAGE_THUMBNAIL_Q = 75
class ImageT:
	def __init__(self):
		pass

	def is_this(self, ext:str) -> bool:
		return ext in [".png", "bmp", "jpg", "webp", "jpeg"]

	def thumbnail(self, path):
		sc_path = os.path.join(config.get_config()["scratch_directory"], f"{random.randrange(100)}tmp.jpg")
		with Image.open(path) as im:
			im.thumbnail((IMAGE_THUMBNAIL_SIZE, IMAGE_THUMBNAIL_SIZE))
			im = im.convert('RGB')
			im.save(sc_path, optimize=True, quality=IMAGE_THUMBNAIL_Q)

		with open(sc_path, "rb") as f:
			return base64.urlsafe_b64encode(f.read()).decode("utf-8")

thumbnailers = [ImageT()]

def get_thumbnail(thpath):
	ext = pathlib.Path(thpath).suffix

	for t in thumbnailers:
		if t.is_this(ext):
			return t.thumbnail(thpath)

	return ""
