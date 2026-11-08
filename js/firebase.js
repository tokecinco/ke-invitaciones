import { initializeApp } from "https://www.gstatic.com/firebasejs/10.14.1/firebase-app.js";
import { getFirestore } from "https://www.gstatic.com/firebasejs/10.14.1/firebase-firestore.js";
import { getAuth } from "https://www.gstatic.com/firebasejs/10.14.1/firebase-auth.js";

const firebaseConfig = {
  apiKey: "AIzaSyBgSdALBB356A-V11uCHSktzAR_4e_k-vQ",
  authDomain: "ke-invitaciones.firebaseapp.com",
  projectId: "ke-invitaciones",
  storageBucket: "ke-invitaciones.firebasestorage.app",
  messagingSenderId: "44260633113",
  appId: "1:44260633113:web:8e659537e7e74126589888"
};

const app = initializeApp(firebaseConfig);
export const db = getFirestore(app);
export const auth = getAuth(app);
