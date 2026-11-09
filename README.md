# Tokito

**Autonomous Zero-Trust Cyber Sentinel & Multi-Platform Defense System**

> **Zero-Cloud • Zero-Trust • 100% On-Device AI Inference**  
> Target Environments: **Android (API 26+) • Windows 10/11 • Linux (Ubuntu / Debian / Arch)**  
> Developed for the **HackWithAditya Hackathon**

---

Tokito is an autonomous, on-device cyber defense ecosystem and endpoint threat sentry engineered to protect everyday users and technical professionals from modern social engineering, financial fraud, smishing, vishing, and endpoint compromise.

Unlike conventional mobile and desktop security solutions that exfiltrate private messages, browsing history, and personal credentials to centralized corporate servers, Tokito enforces an uncompromising **Zero-Cloud, Zero-Trust Architecture**. All natural language inference, heuristic scanning, and threat deconstructions execute 100% locally on the host device using an embedded Google Gemma 2B language model (via MediaPipe GenAI and local runtimes) paired with ultra-low-latency rule engines.

---

## Current Features (Implemented)

Tokito currently provides a comprehensive personal assistance and endpoint protection suite running entirely on-device:

- **Today Commitment Dashboard**: Ambiently captures notifications via Android's `NotificationListenerService`, extracts actionable tasks and commitments using on-device models, prioritizes urgent items first, and provides 1-tap replies, calendar sync, and automated reminder scheduling.
- **On-Device AI Assistant (Ask)**: Powered by local Gemma 2B inference via MediaPipe GenAI (with rule-based fallback). Answers personal questions from saved facts ("what is my branch?"), executes local automations (flashlight toggle, launching applications), schedules alarms, and answers general queries without network dependencies.
- **Voice Agent (Local STT & TTS)**: Real-time interactive voice interface. Transcribes speech locally with streaming feedback and speaks responses aloud with low-latency on-device Text-To-Speech.
- **Personal Knowledge Library**: Searchable, encrypted on-device repository tracking all extracted notification events, commitments, user-taught memories, and execution logs.
- **LinkGuard Interstitial Router**: Intercepts HTTP/HTTPS links across the OS as a browsable handler, performs instant local URL hygiene checks and optional Google Safe Browsing verification, and forwards clean links directly to the user's browser.
- **Hardware-Sealed Identity Vault & Autofill**: Scans and parses Indian identity documents (Aadhaar, PAN cards, etc.) using on-device camera vision and ML Kit OCR. Cryptographically seals credentials using Google Tink (`AES256_GCM`) backed by Android Keystore / hardware TEE. Securely autofills form fields across third-party apps gated behind strict biometric verification (`BiometricPrompt`).
- **Zero-Knowledge Encrypted Backup**: Client-side database backups encrypted using AES-GCM with a user-derived PBKDF2 master password. The Node.js/Express backend stores only ciphertext in MongoDB GridFS and never has access to keys or plaintexts.

---

## Upcoming Cyber Defense Roadmap (Technical Specification)

Based on the Tokito Technical Specification (HackWithAditya submission), Tokito is expanding into a multi-platform, air-gapped threat defense ecosystem with the following core security capabilities:

### 1. Autonomous AI Call Agent & Vishing Screener (Tokito Call Guardian)
*Addresses the surge in extortionary "Digital Arrest", fake police/CBI, customs seizure, and telecom KYC coercion calls.*
- **Silent Interception**: Registers as an Android Telecom `CallScreeningService` / `InCallService`. Automatically intercepts incoming calls from unknown, unsaved, or suspicious numbers in the background without ringing the phone or alerting the user.
- **Conversational Voice Challenge**: An interactive on-device AI speaks to the caller via local TTS:
  > *"Hello, I am Tokito, the on-device automated security screener for this recipient. Please state your full name and the exact reason for your call."*
- **Real-Time Speech-to-Text & Threat Heuristics**: Transcribes caller audio via low-latency STT and streams the text into local Gemma 2B and NLP rule engines. Evaluates coercion metrics, aggressive urgency, and vishing keywords (*arrest warrant*, *narcotics*, *customs seizure*, *illegal parcel*, *CBI*, *cyber cell*, *immediate fine*, *transfer money*, *verification OTP*).
- **Autonomous Disposition**:
  - **Spam / Fraud**: Terminates the call, records threat reasons and full audio transcripts to the local database, notifies the user with high-priority threat indicators, and blacklists the number.
  - **Legitimate Call** (e.g., food delivery driver, verified business): Alerts user with instant context (*"Food delivery at your door — ringing through"*) and bridges the audio ringtone.

### 2. ScanGuard 2.0 (Phishing, Typosquatting & QR/UPI Fraud Scanner)
*Sub-millisecond on-device link evaluation and financial deep-link auditing without third-party telemetry.*
- **0 ms Latency On-Device Evaluation**: Analyzes destination URLs completely offline before any socket connection is initiated.
- **Hard Red Flag Detection**:
  - **Userinfo `@` Obfuscation**: Catches credential-harvesting deception (e.g., `https://sbi.co.in@evil-server.xyz/login`).
  - **Raw IP Destinations**: Flags URLs resolving directly to naked IPs (`http://192.168.1.1/kyc.apk`).
  - **Punycode / IDN Homographs**: Detects Cyrillic and Greek lookalike character replacements (`xn--...`).
  - **Levenshtein Brand Typosquatting**: Measures string distance against 25+ protected Indian and global brand roots (SBI, HDFC, ICICI, Axis Bank, Paytm, PhonePe, UIDAI, IRCTC, Amazon, etc.).
- **UPI & QR Code Deep-Link Inspector**: Decodes `upi://pay` query parameters (`pa` = Payee VPA, `pn` = Payee Name, `am` = Amount, `mode` = QR/Intent).
- **Reverse-Charge Alert**: Automatically flags deceptive "Collect Requests" where the user is tricked into thinking they are receiving cashback or funds when the intent actually authorizes an outbound debit.

### 3. Autonomous Smishing & Social Engineering Shield
- Hooks into SMS, WhatsApp, Telegram, and Signal notifications via `NotificationListenerService`.
- Deconstructs scam patterns: Electricity bill disconnection threats, fake Telegram/YouTube task earnings fraud, and courier package delivery APK scams.
- **Multilingual Code-Mixing**: Natively parses Indian code-mixed languages (Hinglish, Telugu-English such as *"25th lopu fee kattali, marchipovaddu"*), neutralizing regional evasion tactics.
- **OTP Intercept Guardian**: Triggers immediate alerts when an incoming SMS contains an authentication OTP while an active phone call or screen-sharing session is open.

### 4. Device Hardening, Permission & Trojan Watchdog
- **Accessibility Trojan Watchdog**: Continuously monitors applications with `BIND_ACCESSIBILITY_SERVICE` privileges; flags any non-system app attempting to read active window text or automate UI clicks.
- **Screen Overlay Protection**: Detects apps holding `SYSTEM_ALERT_WINDOW` ("Draw over other apps") to prevent transparent clickjacking over banking login forms.
- **Privacy Exposure Score (0–100)**: Combinatorial audit of dangerous permission clusters (e.g., Camera + Microphone + SMS + Background Location on utility apps).
- **Display Shield (`FLAG_SECURE`)**: Enforces screenshot blocking, spyware protection, and prevents thumbnail caching in the Recent Apps switcher across all sensitive views.

### 5. Zero-Trust Clipboard Sentry
- **Crypto-Clipper Defense**: Real-time clipboard regex monitoring for Bitcoin (`bc1`, `1`, `3`), Ethereum (`0x`), Solana, and Tron wallet addresses, warning if malware replaces copied addresses.
- **UPI Handle Sentry**: Validates integrity of copied UPI VPAs.
- **Memory Hygiene Flush**: Automatically warns and purges sensitive credentials (passwords, Aadhaar, PAN) remaining on the system clipboard for longer than 60 seconds.

### 6. Network Sentinel (Wi-Fi & Radio Layer Sentry)
- **Evil-Twin Detection**: Alerts on unencrypted open Wi-Fi networks mimicking SSIDs of known secure networks.
- **ARP Gateway Poisoning Watchdog**: Detects Man-In-The-Middle (MITM) sniffing by comparing gateway MAC addresses against ARP tables.
- **Cellular Radio Downgrade Warning**: Detects forced downgrades from 4G/5G to unencrypted 2G GSM (signaling IMSI-catchers / rogue portable SMS blasters).

### 7. 1-Click Forensic Dossier & Cybercrime Complaint Generator
- Consolidates intercepted scam evidence: raw headers, timestamps, sender numbers, SMS bodies, extracted URLs, decoded UPI VPAs, and automated screenshots.
- Generates a standardized, cryptographically hashed PDF complaint dossier matching evidence requirements for:
  - National Cyber Crime Reporting Portal ([cybercrime.gov.in](https://cybercrime.gov.in))
  - Indian Cybercrime Helpline (`1930`)
  - Global CERT incident formats.

### 8. Hardware-Backed Identity Vault & Verhoeff OCR
- On-device document scanner featuring automated perspective correction and edge detection.
- Mathematical **Dihedral Group $D_5$ Verhoeff algorithm** validation for 12-digit Aadhaar check digits, rejecting optical errors and counterfeit identity numbers.
- Google Tink `AES256_GCM` encryption sealed inside Android Keystore hardware TEE / StrongBox.
- Biometric step-up verification required prior to any autofill release.

### 9. Emergency Duress Protocol & Panic Wipe
- **Duress PIN / Gesture**: Unlocking with an emergency PIN or discrete multi-finger tap pattern silently initiates cryptographic zeroization.
- **Instant Key Shredding**: Destroys the Tink Keystore master key, rendering vaults, call logs, and documents permanently unrecoverable.
- **Decoy UI**: Seamlessly renders an innocent, empty state with no error dialogs to protect victims from physical coercion.

### 10. WhatsApp-Grade Zero-Knowledge Cloud Backup (Streaming STREAM)
- Key derivation using `PBKDF2-HMAC-SHA256` (600,000 iterations + 16-byte cryptographically secure random salt).
- Streaming `STREAM AES-GCM` encryption in 1 MiB chunks with authenticated nonces, chunk counters, and terminal flags.
- Zero-knowledge storage on MongoDB GridFS via Node.js/Express; server never accesses keys or plaintexts.

---

## Multi-Platform Architecture

```
                    ┌────────────────────────────────────────────────────────┐
                    │                   TOKITO SHARED CORE                   │
                    │  • Gemma 2B Engine (MediaPipe / ONNX)                  │
                    │  • ScanGuard Heuristics (Levenshtein / TLD)            │
                    │  • Zero-Knowledge Crypto (Tink AES-GCM / PBKDF2)       │
                    └────────────────────────────────────────────────────────┘
                                   │                   │
         ┌─────────────────────────┴─────────┐         └─────────────────────────┐
         ▼                                   ▼                                   ▼
┌─────────────────────────┐     ┌─────────────────────────┐     ┌─────────────────────────┐
│      ANDROID NODE       │     │      WINDOWS NODE       │     │       LINUX NODE        │
│  (Kotlin / Compose)     │     │  (Compose Multiplatform)│     │  (Systemd + GTK / CLI)  │
│ • Telecom Call Screener │     │ • Tray security sentry  │     │ • Headless daemon       │
│ • Notification sentry   │     │ • Win32 clipboard hook  │     │ • D-Bus notification mon│
│ • Accessibility monitor │     │ • URL browser intercept │     │ • Socket & DNS sentry   │
│ • TEE / StrongBox vault │     │ • DPAPI sealed vault    │     │ • TPM 2.0 / libsecret   │
└─────────────────────────┘     └─────────────────────────┘     └─────────────────────────┘
```

---

## Security Guarantees & Threat Model

| Threat Vector | Potential Impact | Tokito Mitigation |
| :--- | :--- | :--- |
| **Eavesdropping / Wiretapping** | Man-in-the-Middle network surveillance | Zero telemetry; all AI processing on localhost; backup uses AES-256-GCM. |
| **Cloud Provider Breach** | Theft of stored backups from server | Zero-Knowledge architecture: only ciphertext is stored; server holds no encryption keys. |
| **Malicious Accessibility Apps** | Theft of screen data & banking credentials | Background monitoring warns of non-system accessibility services and active overlay windows. |
| **Physical Phone Theft** | Unauthorized browsing of sensitive ID cards | Hardware Keystore TEE sealing; `BiometricPrompt` gate; `FLAG_SECURE` screen shielding. |
| **Smishing / Fraudulent Links** | Account takeover, credential theft | Local ScanGuard URL deconstruction and Gemma 2B psychological urgency classifier. |
| **Vishing / Coercion Calls** | Digital arrest extortion, panic money transfers | Autonomous Call Agent intercepts unknown callers, analyzes speech via local STT, and flags fraud. |

---

## Technology Stack

| Component | Android Mobile | Desktop (Windows / Linux) | Cloud Backend |
| :--- | :--- | :--- | :--- |
| **Language** | Kotlin 2.4 | Kotlin / Java 17+ | JavaScript / Node.js 22 |
| **Framework** | Jetpack Compose, Material 3 | Compose Multiplatform / GTK | Express 5 |
| **On-Device AI** | Google MediaPipe GenAI (Gemma 2B) | ONNX Runtime / MediaPipe Native | None (*Zero Cloud AI*) |
| **Vision & OCR** | Google ML Kit Document Scanner | Tesseract OCR / Native Vision | None |
| **Local Database** | Room DB (SQLite with WAL) | SQLite / SQLCipher | MongoDB GridFS (Ciphertext) |
| **Cryptography** | Google Tink (AES256_GCM), Keystore | Windows DPAPI / Linux TPM 2.0 | Node crypto (SHA-256) |
| **Audio Processing**| Android SpeechRecognizer & TTS | PortAudio / Piper TTS / Whisper | None |

---

## Project Structure

```
Tokito/
├── app/                               # Native Android application
│   ├── src/main/java/com/owlcoders/tokito/
│   │   ├── TokitoApp.kt               # Application entrypoint & LLM pre-warming
│   │   ├── MainActivity.kt            # Compose entrypoint & root routing
│   │   ├── account/                   # Firebase authentication & profile logic
│   │   ├── automation/                # Intent dispatcher & whitelisted device actions
│   │   ├── backup/                    # Client-side AES-GCM streaming encryption
│   │   ├── db/                        # Room database entities & DAOs
│   │   ├── documents/                 # ML Kit ID scanner & OCR parsing (IdParser)
│   │   ├── security/                  # Vault, AppLock, LinkGuard URL safety checker
│   │   ├── services/                  # NotificationCapture, TokitoAutofill, TokitoAccessibility, STT/TTS
│   │   └── ui/                        # Jetpack Compose screens, components, TokitoColors theme
│   └── src/test/                      # Unit tests (crypto, link scanner, notification filter)
├── backend/                           # Zero-knowledge Node.js / Express sync server
│   ├── src/server.js                  # Express API streaming ciphertext to MongoDB GridFS
│   └── test/api.test.js               # E2EE backup integration tests
└── branding/tokito-logo/              # Identity branding, icons, adaptive PNGs & SVGs
```

---

## Building and Installation

### Prerequisites
- **JDK 17+**
- **Android SDK** (Compile SDK: 37, Min SDK: 26)
- **Node.js 22+** (for optional backend deployment)
- Android physical device or emulator running Android 8.0+

### Step-by-Step Setup

1. **Clone or Navigate to the Workspace**:
   ```bash
   cd Tokito
   ```

2. **Configure Local Environment**:
   Create `local.properties` in the project root:
   ```properties
   sdk.dir=C:\\Users\\<USER>\\AppData\\Local\\Android\\Sdk
   FIREBASE_API_KEY=...
   FIREBASE_APP_ID=...
   FIREBASE_PROJECT_ID=...
   GOOGLE_WEB_CLIENT_ID=...      # optional: enables Google Sign-In
   BACKEND_URL=...               # optional: enables zero-knowledge cloud backup
   SAFE_BROWSING_API_KEY=...     # optional: enables online Safe Browsing link verification
   ```
   *(Note: Debug builds without Firebase keys provide a "Skip" option on the sign-in screen).*

3. **Provision On-Device Gemma 2B Model (Optional)**:
   Push the quantized Gemma model to device storage for local LLM inference:
   ```bash
   adb push gemma-1.1-2b-it-cpu-int4.bin /data/local/tmp/gemma.bin
   ```
   *(Tokito automatically falls back to its deterministic rule-based engine if the weights file is absent).*

4. **Build and Run the Android App**:
   ```bash
   # Windows PowerShell
   .\gradlew.bat :app:installDebug

   # Run Unit Tests
   .\gradlew.bat :app:testDebugUnitTest
   ```

5. **Run the Zero-Knowledge Backend (Optional)**:
   ```bash
   cd backend
   npm install
   npm run dev
   ```

---

## Verification & Test Plan

Each core capability includes defined verification methods and pass criteria:

| Area | Test Method | Pass Criteria |
| :--- | :--- | :--- |
| **Call Guardian** | Replay recorded sample calls ("digital arrest" extortion scripts vs. delivery calls) | Scam calls are terminated, blacklisted, and logged with transcript & IOCs. Legitimate calls ring through with context. |
| **ScanGuard** | Run test set of URLs and UPI/QR payloads (userinfo `@`, raw IPs, punycode, typosquats, collect requests) | All red flags detected; reverse-charge alert fires on collect requests; genuine links pass cleanly. |
| **Smishing Shield** | Feed English, Hinglish, and Telugu-English scam messages mixed with normal messages | Scams flagged with reasoning; normal messages untouched; OTP alert triggers during active phone calls. |
| **Device Sentry** | Install test apps holding accessibility & overlay permissions | Non-system accessibility apps flagged; Privacy Exposure Score reflects risk; `FLAG_SECURE` blocks screenshots. |
| **Clipboard Sentry**| Simulate crypto address / UPI ID swap; leave credentials on clipboard > 60s | Alteration alert raised; memory hygiene warning and auto-flush trigger at 60s. |
| **Zero-Cloud Audit**| Run scenarios in airplane mode while inspecting network traffic | No voice, message, or URL data leaves the device; only user-initiated encrypted backup connects. |

---

## Submission Details

- **Project**: Tokito
- **Event**: HackWithAditya Hackathon
- **Author**: Team Owl Coders (Nandi Vardhan, Lead Developer)
- **License**: Private / Hackathon Submission
