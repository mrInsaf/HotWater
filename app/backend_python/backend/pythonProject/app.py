from flask import Flask, jsonify
from backend import *

app = Flask(__name__)


@app.route('/select_all', methods=['GET'])
def get_data():
    all_data = select_all()
    data = {'message': all_data}
    return jsonify(data)


if __name__ == '__main__':
    app.run(debug=True)
