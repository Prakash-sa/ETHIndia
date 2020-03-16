import flask
import json
import requests
import os
import subprocess
import ipfshttpclient
import base64
from PIL import Image
from flask_ngrok import run_with_ngrok
import uu
from datetime import date
from flask_cors import CORS
import time
app = flask.Flask(__name__)
CORS(app)
run_with_ngrok(app)

def upload_file(file_name):
    url = 'https://kfs1.moibit.io/moibit/v0/writefile'
    headers = {"api_key":"12D3KooWHKy1gQesCpXkhXwGCMktUeWRMLnhXFj3sdMURoWoufJb",
    "api_secret": "08011240db4674dd3d2ffc226dd4f8bb5d58efd8469d8ba1d6d9a05ad2ff33194ff9c59f6f970cc42dfc61b622a42c14ca822c825c50c1beb1e99decc35d16eebc5011f4"}

    files={"content-type": "multipart/form-data",'file':open(file_name,'rb')}
    response = requests.post(url,headers=headers, files=files)
    l = response.json()
    print((l['data'])['Hash'])
    return (l['data'])['Hash']

def download_file(hash,result_file):
    url = 'https://kfs1.moibit.io/moibit/v0/readfilebyhash'
    payload = {'hash': hash}
    headers = {"api_key":"12D3KooWHKy1gQesCpXkhXwGCMktUeWRMLnhXFj3sdMURoWoufJb",
    "api_secret": "08011240db4674dd3d2ffc226dd4f8bb5d58efd8469d8ba1d6d9a05ad2ff33194ff9c59f6f970cc42dfc61b622a42c14ca822c825c50c1beb1e99decc35d16eebc5011f4"}
    
    r = requests.post(url, data=json.dumps(payload), headers=headers)
    rnfile='ranfile'
    with open(rnfile, 'wb') as file_handler:
        file_handler.write(r.content)

    Image.open(rnfile).save(result_file,'JPEG')
    os.remove(rnfile)
    return "True"

def download_file_video(hash,result_file):
    url = 'https://kfs1.moibit.io/moibit/v0/readfilebyhash'
    payload = {'hash': 'bafybeicp2jqnre4zspsqhmzkijgpei4wyo3yepy7umqxxklfjaecnxvoiq'}
    headers = {"api_key":"12D3KooWHKy1gQesCpXkhXwGCMktUeWRMLnhXFj3sdMURoWoufJb",
    "api_secret": "08011240db4674dd3d2ffc226dd4f8bb5d58efd8469d8ba1d6d9a05ad2ff33194ff9c59f6f970cc42dfc61b622a42c14ca822c825c50c1beb1e99decc35d16eebc5011f4"}
 
    r = requests.post(url, data=json.dumps(payload), headers=headers)
    with open(result_file, 'wb') as file_handler:
        file_handler.write(r.content)
    return "True"

def remove(filename):
    url = 'https://kfs1.moibit.io/moibit/v0/remove'

    headers = {"api_key":"12D3KooWHKy1gQesCpXkhXwGCMktUeWRMLnhXFj3sdMURoWoufJb",
    "api_secret": "08011240db4674dd3d2ffc226dd4f8bb5d58efd8469d8ba1d6d9a05ad2ff33194ff9c59f6f970cc42dfc61b622a42c14ca822c825c50c1beb1e99decc35d16eebc5011f4"}

    payload = {'path': filename+'.txt'}

    response = requests.post(url,headers=headers, data = json.dumps(payload))
    print(response.text)
    return "True"

def remove_image(filename):
    url = 'https://kfs1.moibit.io/moibit/v0/remove'

    headers = {"api_key":"12D3KooWHKy1gQesCpXkhXwGCMktUeWRMLnhXFj3sdMURoWoufJb",
    "api_secret": "08011240db4674dd3d2ffc226dd4f8bb5d58efd8469d8ba1d6d9a05ad2ff33194ff9c59f6f970cc42dfc61b622a42c14ca822c825c50c1beb1e99decc35d16eebc5011f4"}

    payload = {'path': filename}

    response = requests.post(url,headers=headers, data = json.dumps(payload))
    print(response.text)
    return "True"

    
@app.route('/store',methods = ['GET','POST'])
def recieve_crappy():
    try:
        lan = flask.request.get_json(force=True)
        lan1 = lan['image']
        name = lan['name']
        print(lan1[:40])
        imgdata = base64.b64decode(lan1)
        filename = name 
        with open(filename, 'wb') as f:
            f.write(imgdata)
        #os.system('python examples/tfci.py compress b2018-gdn-128-4'+' '+name)
        #hash_file = client.add(name+'.tfci')
        hash_file = upload_file(name)
        print(hash_file)
        #os.remove(name)
        #os.remove(name+'.tfci')
        fil = open(lan['user']+'.txt','a+')
        fil.write('\n'+hash_file+'.'+name)
        fil.close()
        os.remove(name)
        return 'True'
    except:
        return 'No file found'

@app.route('/all_images',methods = ['GET','POST'])
def send_files_name():
    try:
        lan = flask.request.get_json(force = True)
        user = lan['user']
        with open(user+'.txt') as f:
            lines = f.readlines()
        if len(lines) == 0:
            return 'No filename for this user'
        else:
            files = {'image_list' : lines}
            r = json.dumps(files)
            return r
    except:
        return 'No filename for this user'

@app.route('/send',methods = ['GET','POST'])
def send_crappy():
    lan = flask.request.get_json(force=True)
    image = lan['image']
    user = lan['user']
    with open(user+'.txt') as f:
        lines = f.readlines()
    print(lines)
    for line in lines:
        if image in line:
            y = line[:59]
            break
    print(y,image)
    #client.get(y)
    download_file(y,image)
    #os.rename(y,image+'.tfci')
    #os.system(' python examples/tfci.py decompress'+' '+image+'.tfci')
    #os.rename(image+'.tfci.png',image)
    with open(image,'rb') as img_file:
        my_string = base64.b64encode(img_file.read())
    os.remove(image)
    #os.remove(image+'.tfci')
    return my_string
    
@app.route('/delete',methods = ['GET','POST'])
def delete_image():
    lan = flask.request.get_json(force = True)
    user = lan['user']
    image = lan['image']
    if user!='public':
        with open(user+".txt", "r") as f:
            lines = f.readlines()
        with open(user+".txt", "w") as f:
            for line in lines:
                if image not in line:
                    f.write(line)
        if '.mp4' in image:
            remove(image)
        else:
            remove_image(image)
        return 'True'
    else:
        with open(user+".txt", "r") as f:
            lines = f.readlines()
        with open(user+".txt", "w") as f:
            for line in lines:
                if image not in line:
                    f.write(line)
        return 'True'

@app.route('/delete_public',methods = ['GET','POST'])
def delete_public():
    lan = flask.request.get_json(force = True)
    user = lan['user']
    image = lan['image']
    with open(user+".txt", "r") as f:
        lines = f.readlines()
    with open(user+".txt", "w") as f:
        for line in lines:
            if image not in line:
                f.write(line)
    return 'True'

@app.route('/video',methods = ['GET','POST'])
def recieve_video():
    lan = flask.request.get_json(force = True)
    user = lan['user']
    vid_name = lan['name']
    vid = lan['image']
    #res = client.add_json(vid)
    res = upload_file(vid)
    print(res)
    fil = open(user+'.txt','a+')
    fil.write('\n'+res+'.'+vid_name+'\n')
    fil.close()
    return 'True'

@app.route('/videosend',methods = ['GET','POST'])
def send_vid():
    lan = flask.request.get_json(force = True)
    user = lan['user']
    vid_name = lan['image']
    print(vid_name)
    with open(user+'.txt') as f:
        lines = f.readlines()
    print(lines)
    for line in lines:
        if vid_name in line:
            print(line[:59])
            #p = client.get_json(line[:59])
            p = download_file_video(line[:59],vid_name)
            print(p)
            break
    print(p)
    return p

@app.route('/live_stream_record_start',methods = ['GET','POST'])
def live_stream_start():
    lan = flask.request.get_json(force = True)
    channel_name = lan['channel_name']
    user = lan['user']
    os.system('./start '+channel_name)

    today = str(date.today())
    print(today)
    s = ''
    for c in today:
        if c!='-':
            s+=c
    s = s[:len(s)-2]
    s+='12'
    for files in os.listdir(s):
        print(files)
        fil = files
    print(fil)
    var = str(int(time.time()*1000))+'.mp4'
    for files in os.listdir(s[:]+'/'+fil):
        if 'mp4' in files:
            #uu.encode(s+'/'+fil+'/'+files,'video.txt')
            uu.encode(s+'/'+fil+'/'+files,var+'.txt')
            break
    #res = client.add('video.txt')
    res = upload_file(var+'.txt')
    hash_file = res
    fil = open(user+'.txt','a+')
    #fil.write('\n'+hash_file+'.'+str(int(time.time()*1000))+'.mp4')
    fil.write('\n'+hash_file+'.'+var)
    fil.close()
    os.system('rm -rf '+ s)
    return 'True'

    # r = subprocess.Popen(['./start', channel_name])
    # d[channel_name] = r.pid

@app.route('/vid_download',methods = ['GET','POST'])
def vid():
    lan = flask.request.get_json(force = True)
    user = lan['user']
    name = lan['name']
    image = lan['image']
    with open(name,'wb') as fh:
        fh.write(base64.b64decode(image))
    #uu.encode(name,'video.txt')
    uu.encode(name,name+'.txt')
    #res = client.add('video.txt')
    #res = upload_file('video.txt')
    res = upload_file(name+'.txt')
    hash_file = res
    fil = open(user+'.txt','a+')
    fil.write('\n'+hash_file+'.'+name)
    fil.close()
    os.remove(name)
    return 'True'

@app.route('/live_stream_record_download',methods = ['GET','POST'])
def live_stream_download():
    lan = flask.request.get_json(force = True)
    user = lan['user']
    name = lan['name']
    with open(user+'.txt') as f:
        lines = f.readlines()
    for line in lines:
        if name in line:
            #client.get(line[:59])
            download_file_video(line[:59],name)
            break
    #uu.decode('video.txt','video-copy.mp4')
    uu.decode(name+'.txt','video-copy.mp4')
    with open('video-copy.mp4','rb') as image:
        image_reader = image.read()
        image_encode = base64.encodestring(image_reader)
    return image_encode

@app.route('/qrcode',methods = ['GET','POST'])
def qr_code_scanner():
    lan = flask.request.get_json(force=True)
    user1 = lan['user1']
    user2 = lan['user2']
    name = lan['name']
    with open(user1+'.txt') as f:
        lines = f.readlines()
    for files in lines:
        if name in files:
            fil = files
            break
    t = open(user2+'.txt','a+')
    t.write(fil+'\n')
    t.close()
    return 'True'

@app.route('/add_files',methods = ['GET','POST'])
def add_file():
    lan = flask.request.get_json(force=True)
    users = lan['users']
    name = lan['name']
    user1 = lan['user1']
    with open(user1+'.txt') as f:
        lines = f.readlines()
    for files in lines:
        if name in files:
            fil = files
            break

    print(users)
    for user in users:
        print(user)
        try:
            t = open(user+'.txt','a+')
            t.write(fil+'\n')
            t.close()
        except:
            pass
    return 'True'

@app.route('/make_public',methods = ['GET','POST'])
def public():
    lan = flask.request.get_json(force=True)
    name = lan['name']
    user = lan['user']
    with open(user+'.txt') as f:
        lines = f.readlines()
    for files in lines:
        if name in files:
            fil = files
            break
    t = open('public.txt','a+')
    t.write(fil+'\n')
    t.close()
    return 'True'

@app.route('/public_images',methods = ['GET','POST'])
def images_public():
    try:
        with open('public.txt') as f:
            lines = f.readlines()
        try:
            if len(lines) == 0:
                    return 'No filename for this user'
            else:
                files = {'image_list' : lines}
                r = json.dumps(files)
                return r
        except:
            return 'No filename for this user'
    except:
        return 'No public images'

@app.route('/public_images_download',methods = ['GET','POST'])
def images_public_download():
    lan = flask.request.get_json(force = True)
    image = lan['image']
    with open('public.txt') as f:
        lines = f.readlines()
    print(lines)
    for line in lines:
        if image in line:
            y = line[:59]
            break
    download_file(y,image)
    #os.rename(y,image+'.tfci')
    #os.system(' python examples/tfci.py decompress'+' '+image+'.tfci')
    #os.rename(image+'.tfci.png',image)
    with open(image,'rb') as img_file:
        my_string = base64.b64encode(img_file.read())
    os.remove(image)
    #os.remove(image+'.tfci')
    return my_string

app.run()

# if __name__ == '__main__':
#     app.debug = True
#     app.run(host='0.0.0.0', port = 5000)
