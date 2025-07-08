# 📦 ETHIndia – Decentralized Media Storage & Compression App

A decentralized Android app that leverages **Moibit** for secure image and video storage, with built-in media compression using a Flask-based ML backend. Users can capture, compress, upload, and retrieve media directly from decentralized storage—and even record video calls that are persistently stored.

### 🔗 Demo
Watch a quick walkthrough on YouTube: [ETHIndia Demo](https://youtu.be/eMeY1kKHXfc)

---

## 🚀 Features

- 🔐 **User Authentication** – Sign up and log in to your personal decentralized media vault.
- 📷 **Image Upload** – Upload images directly from camera or gallery.
- 🧠 **Smart Compression** – Images are automatically compressed via a Flask API with an ML model before upload, reducing storage cost.
- 🗃️ **Storage Usage Dashboard** – View uploaded images and storage consumption through a visual gallery.
- 🔁 **Media Management** – Download original/compressed versions or delete media from your decentralized store.
- 🎥 **Video Call & Record** – Make in-app video calls and record sessions; recordings are stored securely on Moibit.
- 🌐 **Decentralized Infrastructure** – Uses Moibit (built on IPFS) to ensure permanent, censorship-resistant storage.

---

## 🧱 Architecture

- **Frontend:** Android (Java)  
- **Backend:** Flask API  
- **Decentralized Storage:** Moibit (IPFS-based)  
- **ML Compression:** Image quality reduction model served via Flask  
- **APIs Used:** Agora API (for video calling), Moibit API (for decentralized storage)  

---

## 🛠️ Tech Stack

| Layer         | Technologies                            |
|---------------|-----------------------------------------|
| Mobile App    | Java, Android SDK                       |
| Backend API   | Flask, Python                           |
| ML/Compression| PIL, OpenCV, Flask                      |
| Storage       | Moibit API, IPFS                        |
| Video Calling | Agora SDK                               |

---

## 🧪 Setup Instructions

1. **Backend Setup**
   ```bash
   cd server/
   pip install -r requirements.txt
   python app.py

2. **App**

- First you have to Login or Create User to user the app storage.
![alt text](https://github.com/Prakash-sa/HackathonPune/blob/master/1.jpg)
- After you Login you can Upload Images to your Storage. To Optimise the storage we have make API with flask to run ML Model to convert your High Quality Image to Low Quality and Upload it to the decentralised system.
- From Gallery you can get the knowledge of your storage usage and what images you have uploaded to the system.
- You can Delete the images. You can Download Compressed and Original Images from the App.
- You can upload images from Gallery and Camera from App.
![alt text](https://github.com/Prakash-sa/HackathonPune/blob/master/2.jpg)
- You can make video calls with our app and can record it. Recording is stored in decentralised storage system

---

🏆 Achievements
🥈 2nd place – ETHIndia Regional Hackathon

🛡️ Secured and optimized decentralized image uploads via on-device + server-side compression.

🔄 Reduced image sizes before upload by ~70% without major quality loss.

---

🤝 Contributors
Prakash Saini(sainiprakash525@gmail.com)

