from dotenv import load_dotenv, dotenv_values 
import os
from os import environ

import toml
from path import Path
from typing import Any, Self
import logging

logger = logging.getLogger(__name__)


CONFIG: dict[str, Any] | None = None
SECRET_KEY: str | None = None

def get_config_path() -> str:
	load_dotenv() 
	path = environ.get("CONFIG_PATH")
	print(path)
	if path is None:
		return "/etc/hypocrisy/hypocrisy.toml"
		# return "/home/aaa/Documents/python/hypocrisy/local.toml"
	else:
		return path

def get_logging_path() -> str:
	load_dotenv() 
	path = environ.get("LOG_PATH")
	print(path)
	if path is None:
		return "/var/log/hypocrisy/hypocrisy.log"
		# return "/home/aaa/Documents/python/hypocrisy/daemon.log"
	else:
		return path

def default_config():
	return {
		"database_path":"/var/lib/hypocrisy/hypocrisy.db",
		"host": "0.0.0.0",
		"port": 6767,
		"debug": False,
		"reset_db": False,
		"reset_log": False
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

def get_secret():
	global SECRET_KEY
	if SECRET_KEY is not None:
		return SECRET_KEY

	path = Path(get_config()["secret_path"])
	if path.is_file():
		try:
			f = open(path)
			SECRET_KEY = f.read()
			f.close()
			os.system(f"chmod 600 {path}")
		except Exception as e:
			logger.critical(f"Cannot read secret value from path {path} error: {e}")
			raise e

	else:
		try:
			f = open(path, "w")
			SECRET_KEY = os.urandom(128).hex()
			f.write(SECRET_KEY)
			f.close()
			logger.info(f"Written new secret key to path {path}")
		except Exception as e:
			logger.critical(f"Cannot write secret to path {path} error: {e}")
			raise e
	return SECRET_KEY

