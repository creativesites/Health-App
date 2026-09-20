# Zambia Health Sanctuary 🇿🇲
> **A Zambia-First Digital Healthcare Platform Connecting Patients with Verified Healthcare Specialists**

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Release](https://img.shields.io/badge/Release-V1.0.0-emerald)](https://github.com/creativesites/Health-App/releases/latest)

---

## 📲 Download V1 Showcase APK

Get the ready-to-install debug APK directly from GitHub Releases:

👉 **[Download Latest APK (`zambia-health-v1.apk`)](https://github.com/creativesites/Health-App/releases/latest/download/zambia-health-v1.apk)**

Or browse all release assets on the [Releases Page](https://github.com/creativesites/Health-App/releases).

---

## 🌟 Overview

Zambia Health Sanctuary is engineered specifically for the Zambian healthcare landscape, blending a warm, **Calm Light aesthetic** with seamless bidirectional clinician-patient workflows:

- **Calm Light Design System**: Warm ivory canvas (`#FFFBFAF7`), deep navy typography, luminous spherical glow accents, soft pill chips, and dark matte contrast cards.
- **Transparent Specialist Cutouts**: High-resolution specialist portraits with background extraction for Doctors, Psychologists, and Occupational Therapists across Lusaka and the Copperbelt.
- **Zambia-First Economics & Payments**:
  - Full pricing in Zambian Kwacha (**ZMW**).
  - Native Mobile Money checkout simulation supporting **MTN MoMo**, **Airtel Money**, and **Zamtel Kwacha**.
  - Verified **HPCZ Accreditation** badges and clinic affiliations (UTH Lusaka, Levy Mwanawasa Hospital, CBU SOM, Arthur Davison Children's Hospital).
- **5–10 Min North Star Bidirectional Journey**:
  - Instant role switcher between **Patient** and **Specialist / Clinician**.
  - Multi-slot booking & real-time USSD push payment confirmation.
  - Role-aware two-way messaging (dynamic bubble alignment, colors, participant identities).
  - Specialist Clinical Dossier (Intake, Prescribed Care Goals, Consultation Log).
  - Direct sync from Specialist Encounter Notes to Patient Care Goals & Completed Appointment Summaries.

---

## 🏗️ Tech Stack & Architecture

- **UI Framework**: Modern Jetpack Compose with Material 3
- **Architecture**: MVI / MVVM with Clean Architecture principles and Unidirectional Data Flow (UDF)
- **Local Database**: Room SQLite database with reactive Kotlin Coroutines `Flow`
- **Dependency & State Management**: StateFlow, ViewModelScope, Service Locator
- **Testing**: JUnit4, Robolectric, Kotlinx Coroutines Test

---

## 🛠️ Building & Running Locally

### Prerequisites
- Android Studio Ladybug (or newer)
- Android SDK 36.1 (`compileSdk = 36`)
- JDK 17 or JDK 21

### Quick Start
```bash
# Clone the repository
git clone https://github.com/creativesites/Health-App.git
cd Health-App

# Run unit and regression tests
./gradlew testDebugUnitTest

# Assemble the debug APK
./gradlew assembleDebug
```

The compiled APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📜 License & Accreditation
Built for Zambia's digital healthcare future. Practitioners listed are registered with the Health Professions Council of Zambia (HPCZ).

