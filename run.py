from flask import Flask, render_template, jsonify, Response, request
from flask_cors import CORS
import json
from imdb import IMDb

ia = IMDb()

DATASETS = [{ "id": 'imdb', "label": 'IMDb' }]
SELECTED_DATASET = ''

with open('./frontend/app/api/config.json') as f:
  CONFIG = json.load(f)

app = Flask(__name__,
            static_folder = "./frontend/app/dist",
            template_folder = "./frontend/app/dist")
cors = CORS(app, resources={r"/api/*": {"origins": "*"}})

@app.route('/')
def index():
    return render_template("index.de.html")

@app.route('/api/config/frontend')
def config():
    return Response(json.dumps(CONFIG),  mimetype='application/json')

@app.route('/api/datasets')
def datasets():
    return Response(json.dumps(DATASETS),  mimetype='application/json')

@app.route('/api/dataset/<id>')
def dataset(id):
    SELECTED_DATASET = json.loads(DATASETS)[id]
    return SELECTED_DATASET

@app.route('/api/datasets/<dataset>/concepts')
def concepts(dataset):
<<<<<<< OURS
  return ia.
=======
  return ia.get_movie_locations
>>>>>>> THEIRS
