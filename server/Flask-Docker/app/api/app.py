# Import Libraries
from gevent import monkey
monkey.patch_all()

from flask import Flask, jsonify, request
from utilities import predict_pipeline
from gevent.pywsgi import WSGIServer
from collections import OrderedDict
import cv2
import os
import socket
import json
import pyrebase

with open("auth.json") as f:
    config = json.load(f)

firebase = pyrebase.initialize_app(config)
storage = firebase.storage()



# example (동영상 다운로드)
# path_on_cloud : 동영상이 저장되어있는 위치(영상이름까지 기재)
# path_on_cloud = "bjy123bjy@gmail.com/exercise/unselected/221202/VIDEO_221202_13:30_.mp4"
# path_on_cloud2 = "temp/celebrity.mp4"
# local_path : local에 동영상을 저장할 위치
# local_path = "tempDB/video/bjy123bjy.mp4"
# local_path2 = "tempDB/image/celebrity.mp4"
# 다운로드
# storage.child(path_on_cloud).download("",local_path)
# storage.child(path_on_cloud2).download("",local_path2)

# example (동영상 업로드)
# path_on_cloud : 동영상이 저장될 위치(영상이름까지 기재)
# path_on_cloud = "temp/temp.mp4"
# local_path : 올릴 동영상이 있는 위치
# local_path = "tempDB/video/temp.mp4"
# 업로드
# storage.child(path_on_cloud).put(local_path)



# Make Flask
app = Flask(__name__)

# command : <none>
@app.route('/hello',methods=['GET','POST'])
def hello():
    print("Hello!")
    print("===end===")
    return jsonify("Hello!")

# command : download_and_analyze
@app.route('/download_and_analyze',methods=['POST'])
def download():
    # Get parameters to dictionary format
    parameter_dict = request.args.to_dict()
    # If no data
    if len(parameter_dict) == 0:
        print("No parameter")
        return jsonify("ERROR 1: No parameter")

    # If have data, convert into key-value
    # parameters = ''
    keys = []
    values = []
    for key in parameter_dict.keys():
        # parameters += 'key: {}, value: {}\n'.format(key, request.args[key])
        keys.append(key)
        values.append(request.args[key])
    print('keys:', keys)
    print('values:', values)
    # print('parameters:', parameters)
    
    # Download video
    path_on_cloud = values[0]+".mp4"
    local_path = "tempDB/video/"+values[0].split('/')[-1]+".mp4"
    print('path_on_cloud:', path_on_cloud)
    print('local_path:', local_path+'\n===end===')
    storage.child(path_on_cloud).download("",local_path)

    # Analyze
    # TODO

    # Make result files (1. json file)
    file_data = OrderedDict()

    file_data["name"] = "user1"
    file_data["aa"] = "ex_aa"
    file_data["bb"] = {'x': 100, 'y': 70, 'z': 88}
    file_data["cc"] = "ex_cc"
    file_data["피드백"] = "굿"

    print(json.dumps(file_data, ensure_ascii=False, indent="\t"))

    with open('tempDB/result/'+values[0].split('/')[-1]+'.json', 'w', encoding='utf-8') as make_file:
        json.dump(file_data, make_file, ensure_ascii=False, indent="\t")
    
    # Upload json file
    storage.child(values[0].split('/')[-1].split('_')[0]+'/'+values[0].split('/')[-1]+'_result/'+values[0].split('/')[-1]+'.json').put('tempDB/result/'+values[0].split('/')[-1]+'.json')
    
    # Get result files (2. image)
    storage.child('temp/image/worst1.png').download("",'tempDB/result/'+values[0].split('/')[-1]+'_worst1.png')
    storage.child('temp/image/worst2.png').download("",'tempDB/result/'+values[0].split('/')[-1]+'_worst2.png')
    storage.child('temp/image/graph.png').download("",'tempDB/result/'+values[0].split('/')[-1]+'_graph.png')

    # Upload image files
    storage.child(values[0].split('/')[-1].split('_')[0]+'/'+values[0].split('/')[-1]+'_result/'+values[0].split('/')[-1]+'_worst1.png').put('tempDB/result/'+values[0].split('/')[-1]+'_worst1.png')
    storage.child(values[0].split('/')[-1].split('_')[0]+'/'+values[0].split('/')[-1]+'_result/'+values[0].split('/')[-1]+'_worst2.png').put('tempDB/result/'+values[0].split('/')[-1]+'_worst2.png')
    storage.child(values[0].split('/')[-1].split('_')[0]+'/'+values[0].split('/')[-1]+'_result/'+values[0].split('/')[-1]+'_graph.png').put('tempDB/result/'+values[0].split('/')[-1]+'_graph.png')

    # Remove temporary files in the volume
    os.remove('tempDB/result/'+values[0].split('/')[-1]+'.json')
    os.remove('tempDB/result/'+values[0].split('/')[-1]+'_worst1.png')
    os.remove('tempDB/result/'+values[0].split('/')[-1]+'_worst2.png')
    os.remove('tempDB/result/'+values[0].split('/')[-1]+'_graph.png')
    os.remove('tempDB/video/'+values[0].split('/')[-1]+'.mp4')
    fileName = 'temp/exercise/'+values[0].split('/')[-1]+'.mp4'
    storage.delete(fileName,token=None)

    # TODO: firebase에 저장되어있는 영상 삭제되도록 해야 함.

    # Jobs finished!
    return jsonify("RESULT")
    # # Get data in json format
    # data = request.json
    # print("Get data:", data)

    # # download temp.mp4
    # if data['fileName'] == 'temp':
    #     path_on_cloud = "temp/video/temp.mp4"
    #     local_path = "tempDB/video/temp.mp4"
    #     storage.child(path_on_cloud).download("",local_path)

    # # download celebrity.mp4
    # elif data['fileName'] == 'celebrity':
    #     path_on_cloud = "temp/image/celebrity.mp4"
    #     local_path = "tempDB/image/celebrity.mp4"
    #     storage.child(path_on_cloud).download("",local_path)

    # return jsonify("Download Complete")

# command : clean
@app.route('/clean',methods=['DELETE'])
def clean():
    return jsonify("Clean Complete")

# command : upload
@app.route('/upload',methods=['POST'])
def upload():
    # Get data in json format
    data = request.json
    print("Get data (upload):", data)

    # upload temp.mp4
    if data['fileName'] == 'temp':
        path_on_cloud = "temp/video/temp.mp4"
        local_path = "tempDB/video/temp.mp4"
        storage.child(path_on_cloud).put(local_path)

    # upload celebrity.mp4
    elif data['fileName'] == 'celebrity':
        path_on_cloud = "temp/image/celebrity.mp4"
        local_path = "tempDB/image/celebrity.mp4"
        storage.child(path_on_cloud).put(local_path)

    return jsonify("Upload Complete")



# For the "~/predict" command, execute the following function:
@app.route('/predict',methods=['POST','GET'])
def predict():
    # Get data in json format
    data = request.json
    print("Get data (predict):", data)

    # Exception 1 : There is no data
    try:
        sample = data['data']
    except KeyError:
        return jsonify({'error': 'No data sent'})

    # predict the result
    sample = [sample]
    predictions = predict_pipeline(sample)

    # Exception 2 : There is wrong input data
    try:
        result = jsonify(predictions[0])
    except TypeError as e:
        return jsonify({'error': str(e)+'aa'})

    # Return result
    return result


# Run Flask server
if __name__ == "__main__":
    thisIP = socket.gethostbyname(socket.gethostname())
    print("This PC IP:", thisIP)
    # app.run(host=thisIP, debug=True)
    # app.run(host='localhost', debug=True)
    http_server = WSGIServer((thisIP,5000),app)
    http_server.serve_forever()
