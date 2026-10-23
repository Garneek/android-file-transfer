from hashlib import sha512

import atexit
import logging
import os
import functools
import base64
import json

from flask import Flask, abort, request, g

import server.database as database
import server.config as config
import server.files as files
import server.db_operations as dbops

# TODO:
# [ ] - ADD video/audio thumbnails via another endpoint
# [ ] - make it so server also signs the messaage

logger = logging.getLogger(__name__)

VERSION = "0.0.1"

nonces = []

def main():
    cfg = config.get_config()

    if cfg["reset_log"]:
        os.system(f'rm {config.get_logging_path()}')
    if cfg["reset_db"]:
        os.system(f'rm {cfg["database_path"]}')

    logging.basicConfig(filename=config.get_logging_path(), level=logging.INFO)
    logger.debug('Loaded config')
    files.find_new_files()

    run_app()
    
def run_app():
    cfg = config.get_config()
    app = create_app()
    app.run(host=cfg["host"], port=cfg["port"], debug=cfg["debug"])

def close_db():
    database.get_conn().close()

def authenticate(view):
    @functools.wraps(view)
    def wrapped_view(**kwargs):
        global nonces
        body = request.data
        body_size = len(body)
        signature = request.headers.get("MessageSignature")
        nonce = request.headers.get("RandomString")

        if signature is None or nonce is None:
            abort(401)
        if nonce is None or nonce in nonces:
            abort(401)
        nonces.append(nonce)

        data = bytearray(config.get_config()["password"].encode("utf-8")) + nonce.encode("utf-8")

        if body_size != 0:
            data = data + body

        digest = base64.urlsafe_b64encode(sha512(data).digest())

        if signature != digest.decode("utf-8"):
            abort(401)

        return view(**kwargs)

    return wrapped_view

def create_app():
    cfg = config.get_config()

    app = Flask(__name__, instance_relative_config=True)
    app.config.from_mapping(
        DATABASE=os.path.join(cfg["database_path"])
    )

    os.makedirs(app.instance_path, exist_ok=True)

    atexit.register(close_db)
    
    @app.get("/")
    @authenticate
    def index():
        return json.dumps(
            {
                "name": cfg["server_name"],
                "description": cfg["server_description"],
                "host": cfg["host"],
                "port": cfg["port"],
                "version": VERSION,
                "accepts-pushes": cfg["accept_pushes"],
                "chunk-size": cfg["chunk_size"]
            }
        )

    @app.get("/files")
    @authenticate
    def get_files():
        dt = request.args.get("addedAfter")
        if dt is not None:
            logging.info(f"Requested files after {dt}")
        thumbnails = request.args.get("requestThumbnails") is not None
        rq_reload = request.args.get("requestReload") is not None
        if rq_reload:
            logging.info("Requested reload")
            files.find_new_files()


        return json.dumps(dbops.get_file_data(dt, thumbnails))

    @app.route('/files/<chunk_hash>',  methods=['GET', 'POST'])
    @authenticate
    def file_chunk(chunk_hash):
        if request.method == "GET":
            res = files.get_chunk_bytes(chunk_hash)
            if res is None:
                abort(404)
            return res
        else:
            data = request.data
            try:

                result = files.try_fill_file(chunk_hash, data)
                if result is not None:
                    abort(result)
            except:
                abort(500)

        return ""

    @app.post('/file')
    @authenticate
    def post_file():
        try:
            data = request.json
            result = dbops.add_file_data(data)
            if result is not None:
                abort(result)
            return ""
        except:
            abort(500)

    return app