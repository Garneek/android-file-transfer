import time
import atexit
import logging
import os
import sqlite3
import threading
from typing import Any, Dict

from flask import Flask, flash, redirect, render_template, request, send_from_directory, session, url_for
from flask import current_app, g

import server.database as database
import server.config as config

logger = logging.getLogger(__name__)

def get_db():
    if 'db' not in g:
        g.db = database.get_conn()
        g.db.row_factory = sqlite3.Row

    return g.db

def close_db(e=None):
    database.get_conn().close()

def create_app():
    logging.basicConfig(filename=config.get_logging_path(), level=logging.INFO)
    config.get_config()
    logger.debug('Loaded config')

    cfg = config.get_config()

    app = Flask(__name__, instance_relative_config=True)
    app.config.from_mapping(
        DATABASE=os.path.join(cfg["database_path"]),
        SECRET_KEY="TEMP",

    )



    os.makedirs(app.instance_path, exist_ok=True)

    # app.register_blueprint(auth.bp)
    
    atexit.register(close_db)
    
    @app.route("/")
    # @login_required
    def index():
        # return "test"
        if g.user is None:
            return redirect("/auth/login")
        else:
            return render_template("index.html")

    @app.before_request
    def load_logged_in_user():
        g.user = session.get('username')

    return app

def run_app():
    cfg = config.get_config()

    if cfg["reset_log"]:
        os.system(f'rm {config.get_logging_path()}')
    if cfg["reset_db"]:
        os.system(f'rm {cfg["database_path"]}')

    app = create_app()
    app.run(host=cfg["host"], port=cfg["port"], debug=cfg["debug"])

def main():
    run_app()