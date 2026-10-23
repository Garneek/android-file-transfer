from server import db_operations
import base64
from hashlib import md5
import math
import os

from server import common, config, database, thumbnailer


def list_files_recursive(path='.'):
    return [os.path.join(dirpath,f) for (dirpath, _, filenames) in os.walk(path) for f in filenames]

def register_new_file(path):
	wd = config.get_config()["work_directory"]
	wdpath = os.path.join(wd, path)
	size = os.path.getsize(wdpath)

	chunk_size = config.get_config()["chunk_size"] if size < 1024**3 else config.get_config()["big_file_chunk_size"]
	chunk_n = math.ceil(size / chunk_size)

	conn = database.get_conn()
	conn.cursor().execute(
		"""INSERT INTO file 
		(name, size, chunk_n, chunk_size, thumbnail, is_ready, date_ready) 
		VALUES
		(?, ?, ?, ?, ?, ?, ?)""",
		[path, size, chunk_n, chunk_size, thumbnailer.get_thumbnail(wdpath), 0, common.get_not_ready_date()]
	)
	conn.commit()
	fid = conn.cursor().execute("SELECT LAST_INSERT_ROWID()").fetchone()

	if fid is None:
		raise RuntimeError("what")

	fid = fid[0]

	f = open(wdpath, "rb")
	for i in range(chunk_n):
		bt = f.read(chunk_size)
		hs = base64.urlsafe_b64encode(md5(bt).digest()).decode("utf-8")

		conn.cursor().execute(
			"""INSERT INTO chunk 
			(hash, fid, size, offset, is_downloaded)
			VALUES 
			(?, ?, ?, ?, ?)
			""",
			[hs, fid, len(bt), i * chunk_size, 1]
		)

	conn.commit()
	conn.cursor().execute(
		"UPDATE file SET is_ready = 1, date_ready = ? WHERE id = ?",
		[common.get_current_date(), fid]
	)
	conn.commit()

def find_new_files():
	path = config.get_config()["work_directory"]
	files = list_files_recursive(path)
	files = [x[len(path) + 1:] for x in files]

	names = database.get_conn().cursor().execute(
		"SELECT name FROM file"
	).fetchall()
	names = [x[0] for x in names]

	files = list(filter(lambda x: x not in names, files))

	for file in files:
		register_new_file(file)


def get_chunk_bytes(chunk_hash) -> bytes|None:
	d = db_operations.get_chunk_data(chunk_hash)
	if d is None:
		return None

	fname, offset, size, _ = d

	b = None
	path = os.path.join(config.get_config()["work_directory"], fname)
	with open(path, "rb") as f:
		f.seek(offset)
		b = f.read(size)

	return b

def try_fill_file(chunk_hash: str, data: bytes) -> int|None:
	d = db_operations.get_chunk_data(chunk_hash, downloaded=False)
	if d is None:
		return 404

	fname, offset, _, fid = d
	hs = base64.urlsafe_b64encode(md5(data).digest()).decode("utf-8")

	if hs != chunk_hash:
		return 400

	path = os.path.join(config.get_config()["work_directory"], fname)
	size = 0

	with open(path, "rb+") as f:
		f.seek(offset)
		size = f.write(data)


	conn = database.get_conn()
	conn.cursor().execute(
		"UPDATE chunk SET is_downloaded = 1, size = ? WHERE hash = ?",
		[size, chunk_hash]
	)
	conn.commit()
	

	if conn.execute(
		"SELECT hash FROM chunk WHERE fid = ? AND is_downloaded = 0",
		[fid]
	).fetchone() is None:
		conn.execute(
			"UPDATE file SET is_ready = 1, date_ready = ?, thumbnail = ? WHERE id = ?",
			[common.get_current_date(), thumbnailer.get_thumbnail(path), fid]
		)
		conn.commit()

	return None


def reserve_file(fname, size):
	path = os.path.join(config.get_config()["work_directory"], fname)

	bt = bytes(b"\0") * size

	with open(path, "wb") as f:
		f.write(bt)