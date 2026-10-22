from server.config import get_config

import sqlite3
import logging

logger = logging.getLogger(__name__)

CURSOR: None|sqlite3.Cursor = None
CONN: None|sqlite3.Connection = None

table_create_query = {
	"User":"CREATE TABLE User(login, password)",
}


def create_table(table_name: str):
	cur = get_cursor()
	cur.execute(table_create_query[table_name])
	logging.info(f"Creating table {table_name}")

def ensure_current():
	cur = get_cursor()
	for table in table_create_query.keys():
		res = cur.execute(f"SELECT name FROM sqlite_master WHERE name='{table}'")
		if res.fetchone() is None:
			create_table(table)

	logging.info(f"Ensured the database tables are created")


def get_conn() -> sqlite3.Connection:
	global CURSOR, CONN
	if CONN is not None:
		return CONN
	path = get_config()["database_path"]
	CONN = sqlite3.connect(path, check_same_thread=False, detect_types=sqlite3.PARSE_DECLTYPES)

	CURSOR = CONN.cursor()

	ensure_current()
	return CONN

def get_cursor() -> sqlite3.Cursor:
	global CURSOR, CONN
	if CURSOR is not None:
		return CURSOR
	
	CONN = get_conn()
	CURSOR = CONN.cursor()
	return CURSOR