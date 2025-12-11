# 🩺 Personal Health Buddy

An Android app built with Kotlin and Jetpack Compose that helps users monitor key health metrics, manage medical information, and quickly access nearby healthcare services.  

This project was developed as a final project for the **UNH Android Fall 2025** course.

---

## 👥 Team

- **Chimezie Anthony Onwuegbuchulem**
- **Raj Lama**
- **Nabin Kumar Bamma**

---

## 🎯 Project Overview

**Personal Health Buddy** is designed to give users a simple, centralized place to view and manage important health information.  

Key goals of the app:

- Provide quick access to **BMI**, **blood group info**, and **medication** records  
- Help users manage **emergency contacts** and key health data  
- Offer **location-aware access** to health services using Google Maps  
- Support **secure authentication** via Firebase Email/Password and Google Sign-In  
- Enhance experience with **notifications**, **profile photos**, and an **AI health chatbot**

---

## 📱 Core Features

### 1. BMI Calculation
- Users can enter **height** and **weight** to calculate their **Body Mass Index (BMI)**.
- Results are categorized (e.g., *underweight*, *normal*, *overweight*, *obese*).
- BMI and category are displayed on the dashboard so users can track changes over time.

### 2. Blood Group & Medication
- Users store their **blood group**.
- App shows which blood types they can **donate to** and **receive from** using universal blood rules.
- Users can add and manage **medication records**, helping them track ongoing treatments.

### 3. Authentication (Email/Password + Google Sign-In)
- **Firebase Authentication** used for secure sign up / sign in.
- New users:
  - Create an account with email and password.
  - A unique user ID is generated and used to store their data in **Firestore**.
- Returning users:
  - Log in with email and password or  
  - Use **Google Sign-In** for faster access.
- All user profile and health data is tied to their Firebase user ID.

### 4. Profile System
- View and update:
  - Personal info: first name, last name, DOB, gender, email, phone, address, city
  - Health info: blood group, allergies, medications
  - Emergency contacts: name, phone, relationship
- Upload / change **profile picture** via:
  - Camera  
  - Gallery  
- Profile photo is stored in **Firebase Storage** and shown on both the Profile and Home screens.
- All data is stored securely in **Firestore**.

---

## ➕ Supplemental Features

### 🗺️ Google Maps Integration
- Embedded **Google Map** inside the app.
- Centers on user location (e.g., West Haven, CT).
- Shows markers for nearby **hospitals**, **clinics**, **urgent care**, etc.
- Each marker shows:
  - Name  
  - Address  
  - Hours  
  - Contact details  
- Includes a search bar for finding specific locations.
- Built using **Google Maps SDK** and **Google Places API**.

### 🔔 Notifications
- Users receive helpful notifications such as:
  - Reminders to **check BMI**
  - Updates when **medications** are added
  - Changes to **emergency contacts**
  - Updates to **blood group** info

### 📷 Camera & Photo Support
- Users can:
  - Take a new photo using the **device camera**  
  - Choose an image from the **gallery**  
  - Remove or replace profile photos  
- Images are stored securely in **Firebase Storage** and displayed instantly in the UI.

### 📞 Emergency Contacts
- Manage multiple **emergency contacts** with:
  - Name  
  - Phone number  
  - Relationship  
- Actions supported:
  - Add  
  - Edit  
  - Update  
  - Delete  
- All contacts are stored in **Firestore**, linked to the user.

### 💬 AI Health Chatbot
- In-app AI chatbot to discuss:
  - BMI  
  - Blood group  
  - Basic health concerns and symptoms  
- Provides informational responses to support awareness (not a replacement for professional care).

### 📰 Live Health Articles
- Dedicated section for **real-time health articles**:
  - New medical discoveries
  - Disease updates
  - Fitness & nutrition tips
  - General health reminders
- Helps users stay up to date on health and wellness topics.

---

## 🧭 Navigation

The app uses **Tab Bar Navigation** with four primary tabs:

1. **Home** – dashboard, BMI, quick overview  
2. **Map** – nearby health services  
3. **Notifications** – health alerts and app updates  
4. **Profile** – personal info, health info, settings, logout  

This structure makes it easy for users to move around the app and find what they need quickly.

---

## ☁️ Firebase & Cloud Integration

### Firestore Integration
- Uses `google-services.json` for Firebase configuration.
- Firestore stores:
  - User profile data  
  - Health info  
  - Emergency contacts  
  - Other user-specific data
- Strong separation of data by unique Firebase user ID.
- Gradle dependencies added for:
  - `firebase-auth-ktx`
  - `firebase-firestore-ktx`
  - `firebase-analytics-ktx`

### Cloud Storage
- Firebase Storage holds:
  - Profile photos
  - Other user-uploaded media
- App uses Firestore + Storage together to link metadata with stored images.

---

## 🛠 Tech Stack

- **Language:** Kotlin  
- **UI:** Jetpack Compose, Material 3  
- **Architecture:** Android app with Compose-based screens and Firebase backend  
- **Backend Services:**
  - Firebase Authentication
  - Firebase Firestore
  - Firebase Storage
  - Firebase Analytics
- **Google Services:**
  - Google Maps SDK
  - Google Places API
  - Google Sign-In (`play-services-auth`)
- **Other Libraries:**
  - Coil (`coil-compose`) for image loading
  - Stream Chat UI (`io.getstream:stream-chat-android-ui-components`)
  - AndroidX Navigation Compose
  - AndroidX Biometric (for secure flows, where supported)
  - Coroutines for async operations

Gradle plugin setup (high-level):

- `com.android.application`
- `org.jetbrains.kotlin.android`
- Jetpack Compose support
- `com.google.gms.google-services`
- `kotlin-parcelize`

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio** (latest stable version)
- **Android SDK 33** (compile & target)
- A **Firebase project** with:
  - Firestore enabled  
  - Authentication enabled (Email/Password + Google)  
  - Cloud Storage enabled  
- `google-services.json` file downloaded from Firebase Console

### Setup Steps

1. **Clone the repository**

   ```bash
   git clone https://github.com/UNH-Android-Fall2025/Personal_Health_Buddy2025.git
   cd Personal_Health_Buddy2025
