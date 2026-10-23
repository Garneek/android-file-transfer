import json

from server import common, files
import server.database as db

def get_file_data(dt: None|str, thumbnails: bool):
	conn = db.get_conn()
	query = """SELECT 
				name, size, chunk_n, chunk_size, thumbnail, id
				FROM file WHERE is_ready = 1 AND date_ready > ?"""
	if not thumbnails:
		query = """SELECT 
				name, size, chunk_n, chunk_size, "", id
				FROM file WHERE is_ready = 1 AND date_ready > ?"""


	data = conn.cursor().execute(
		query, 
		[dt if dt is not None else "0000.00.00 00:00"]
	).fetchall()

	for i in range(len(data)):
		x = data[i]
		fid = x[5]
	
		chunks = conn.cursor().execute(
			"""SELECT hash, offset FROM chunk
				WHERE fid = ?
			""",
			[fid]
		).fetchall()

		data[i] = {
			"name": x[0],
			"size": x[1],
			"chunk-n": x[2],
			"chunk-size": x[3],
			"thumbnail": x[4],
			"chunks": [
				{
					"id": x[0],
					"offset": x[1]
				} for x in chunks
			],
		}

	return data


def get_chunk_data(chunk_hash, downloaded = True) -> None|tuple[str, int, int, str]:
	d = db.get_conn().cursor().execute(
		"""SELECT file.name, chunk.offset, chunk.size, file.id FROM 
			file INNER JOIN chunk ON file.id = chunk.fid
			WHERE chunk.hash = ? AND chunk.is_downloaded = ?
			""",
		[chunk_hash, int(downloaded)]
	).fetchone()

	return d



def add_file_data(data) -> int|None:
	try:
		json_data = json.loads(data)
		name = json_data["name"]	
		size = json_data["size"]	
		chunk_n = json_data["chunk-n"]
		chunk_size = json_data["chunk-size"]
		chunks = json_data["chunks"]


		conn = db.get_conn()

		conn.cursor().execute(
			"""INSERT INTO file 
				(name, size, chunk_n, chunk_size, thumbnail, is_ready, date_ready) 
				VALUES
				(?, ?, ?, ?, ?, ?, ?)""",
			[name, size, chunk_n, chunk_size, "", 0, common.get_not_ready_date()]
		)	
		fid = conn.cursor().execute("SELECT LAST_INSERT_ROWID()").fetchone()

		if fid is None:
			raise RuntimeError("what")
		fid = fid[0]

		for chunk in chunks:
			db.get_conn().cursor().execute(
				"""INSERT INTO chunk 
					(hash, fid, size, offset, is_downloaded) 
					VALUES
					(?, ?, 0, ?, 0)
					""",
				[chunk["id"], fid, chunk["offset"]]
			)

		conn.commit()

		files.reserve_file(name, size)

		return None


	except Exception as e:
		# print(e)
		return 400