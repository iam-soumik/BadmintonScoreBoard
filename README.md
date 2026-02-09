# Badminton Scoreboard App

Native Android (Java) application for managing badminton tournament scoreboards.

This project supports **two modes of distribution from the same codebase**:

* **Tournament Mode** – Full version, no expiry (used by organizer)
* **Restricted Mode** – Time-limited version shared with external users

---

## 🔊 Enabling Audio in BlueStacks

If audio / text‑to‑speech is not working in **BlueStacks**, follow these steps:

1. Login with Gmail in BlueStacks and open **Google Play Store**
2. Download the **Google** app
3. Download **Google Text‑To‑Speech** app from Play Store
4. Go to:

    * Home → System Apps → Settings → Accessibility
    * Text‑To‑Speech Output
    * Select **Google Text‑To‑Speech Engine**
5. Set default language and tap **Install Voice Data**
6. Download a voice sample and select it

---

## 🔀 Build Variants (Core Concept)

The app uses **Android Product Flavors** to generate two APKs:

| Flavor       | Purpose                  | Restriction         |
| ------------ | ------------------------ | ------------------- |
| `tournament` | Organizer / official use | ❌ No restriction    |
| `restricted` | Shared APK               | ✅ Date‑based expiry |

Generated APKs:

* `badminton-scoreboard-tournament-release.apk`
* `badminton-scoreboard-restricted-release.apk`

---

## 🧱 Build Configuration

### Product Flavors

```gradle
flavorDimensions "mode"

productFlavors {
    tournament {
        dimension "mode"
        buildConfigField "boolean", "RESTRICTED_BUILD", "false"
    }
    restricted {
        dimension "mode"
        buildConfigField "boolean", "RESTRICTED_BUILD", "true"
    }
}
```

This generates the following flag at build time:

```java
BuildConfig.RESTRICTED_BUILD
```

---

## 🔐 Restriction Logic (How It Works)

### Entry Gate in `MainActivity`

```java
if (!BuildConfig.RESTRICTED_BUILD) {
    setContentView(R.layout.activity_main);
    initUI();
    return;
}

if (isUnlocked()) {
    setContentView(R.layout.activity_main);
    initUI();
} else {
    checkExpiryUsingServerTime();
}
```

### Behavior

* **Tournament build** → UI loads immediately
* **Restricted build**:

    * If unlocked → UI loads
    * If not unlocked → expiry check is enforced

---

## ⏳ Expiry Check (Restricted Build Only)

* Primary source: **Server time** (HTTPS header)
* Fallback: **Device time**

Example expiry:

```java
expiry.set(2025, Calendar.APRIL, 9, 23, 59, 59);
```

If expired, the app shows a blocking dialog and exits.

---

## 🚫 Expiry Dialog

```java
App Expired
The App is no longer working. Need to update the app
```

---

## 🔓 Hidden Admin Unlock (Restricted Build)

A secret unlock mechanism exists for administrative override. This section is **for internal use only**.

### How to Trigger

* Long-press the **Tournament Edition** input field
* Repeat **5 times**

This opens a hidden PIN dialog.

### Unlock Logic

```java
private static final String PIN_HASH = "8899";

if (PIN_HASH.equals(input.getText().toString())) {
    getSharedPreferences("app_prefs", MODE_PRIVATE)
        .edit()
        .putBoolean("unlocked", true)
        .apply();

    recreate();
}
```

### Notes

* PIN is stored as a constant for simplicity
* Can be replaced later with hash / server validation
* Unlock state is persisted using `SharedPreferences`

---

## 🔁 Unlock Behavior Summary

| Build      | Locked | Result          |
| ---------- | ------ | --------------- |
| Tournament | —      | Always works    |
| Restricted | Yes    | Expiry enforced |
| Restricted | No     | Works normally  |

---

## 📦 APK Naming

APKs are automatically renamed during build:

```gradle
badminton-scoreboard-${variant.flavorName}-${variant.buildType.name}.apk
```

---

## 🧪 How to Run / Set Up

1. Clone the repository
2. Open in **Android Studio**
3. Sync Gradle
4. Select Build Variant:

    * `tournamentRelease` for official use
    * `restrictedRelease` for sharing
5. Build APK

---

## 🧠 Notes & Limitations

* Not Play Store dependent
* No device‑binding
* Designed for controlled sharing
* Not intended as DRM or anti‑tamper protection

---

## 👤 Maintainer

**Soumik Maity**
