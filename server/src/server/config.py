from dotenv import load_dotenv 
from os import environ

import toml
from path import Path
from typing import Any
import logging

logger = logging.getLogger(__name__)

CONFIG: dict[str, Any] | None = None

def get_config_path() -> str:
	load_dotenv() 
	path = environ.get("CONFIG_PATH")
	if path is None:
		return "/etc/hypocrisy/hypocrisy.toml"
		# return "/home/aaa/Documents/python/hypocrisy/local.toml"
	else:
		return path

def get_logging_path() -> str:
	load_dotenv() 
	path = environ.get("LOG_PATH")
	if path is None:
		return "/var/log/hypocrisy/hypocrisy.log"
		# return "/home/aaa/Documents/python/hypocrisy/daemon.log"
	else:
		return path

def default_config():
	return {
		"database_path":"/var/lib/osmium/osmium.db",
		"host": "0.0.0.0",
		"port": 6767,
		"debug": True,
		"reset_db": True,
		"reset_log": True,
		"password": "password",
		"work_directory": "../server-workdir",
		"scratch_directory": "../scratch",

		"server_name": "devServer",
		"server_description": "development server experimental",
		"accept_pushes": True,
		"chunk_size": 524288,
		"big_file_chunk_size": 8388608,
	}

def get_config() ->  dict[str, Any]:
	global CONFIG
	if CONFIG is not None:
		logger.debug("Got config from memory")
		return CONFIG

	path = Path(get_config_path())

	cfg = None
	if path.is_file():
		try:
			f = open(path)
			cfg = toml.loads(f.read())
			f.close()
		except:
			pass
	if cfg is not None:
		CONFIG = cfg
		logger.info(f"Read config from {path}")
	else:
		CONFIG = default_config()
		try:
			f=open(path, "w")
			f.write(toml.dumps(CONFIG))
			f.close()
			logger.info(f"Written default config to {path}")
		except:
			logger.warning(f"Failed to write default config to {path}")
			pass

	return CONFIG
