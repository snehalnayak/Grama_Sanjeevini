# Grama Sanjeevini – Rural Pharmacy Network

## 📌 Project Overview

Grama Sanjeevini is an Android healthcare application developed using Kotlin, Jetpack Compose, and Firebase Firestore. The application connects multiple rural medical stores into a single searchable platform where users can quickly find medicine availability from nearby pharmacies.

The system helps villagers search medicines within a selected radius (10–20 km), reducing unnecessary travel and improving emergency healthcare accessibility.

Pharmacists can securely log in, manage inventory, add medicines, and mark emergency medicines such as Insulin and Anti Snake Venom.

---

# 🚀 Features

## 👤 User Features
- Search medicines across nearby pharmacies
- Radius-based search (10 km / 20 km)
- View:
  - Shop Name
  - Address
  - Distance
  - Availability
- Emergency medicine identification with red badge

## 🏥 Pharmacist Features
- Secure login using username and password
- Add medicines to inventory
- Update medicine availability
- Store pharmacy details in Firebase Firestore

## 🚨 Emergency Medicine Support
Special highlighting for emergency medicines:
- Insulin
- Snake Venom Antidote
- Adrenaline Injection
- Oxygen Cylinder

---

# 🛠 Technologies Used

## Frontend
- Kotlin
- Jetpack Compose
- Material 3 UI

## Backend & Database
- Firebase Firestore

## Architecture
- MVVM Architecture
- Repository Pattern

## Development Tools
- Android Studio
- Gemini AI Assistant

---

# 📂 Firebase Firestore Structure

```text
pharmacists
   └── pharmacyDocument
        ├── email
        ├── password
        ├── shopName
        ├── address
        ├── distance
        └── medicines
              └── medicineDocument
                    ├── medicineName
                    ├── emergency
                    ├── available
                    ├── shopName
                    ├── address
                    └── distance
