# Smart Pantry Manager

**Smart Pantry Manager** is a native Android application built in Java that empowers users to keep track of their home pantry items, monitor ingredient expiry dates, reduce food waste, and receive intelligent recipe suggestions based on currently available ingredients and quantities.

---

## 📱 Features

- **Pantry Management**: Add, edit, view, and delete ingredients with quantities, measurement units, and expiration dates.
- **Expiry Tracking & Visual Warnings**: Automatic expiry reminders highlighting items that are expired, expiring today, or expiring within 3 days.
- **Smart Recipe Matching**:
  - **Strict Matches**: Discovers recipes you can cook immediately with exact or converted ingredient quantities in your pantry.
  - **Almost-There Matches**: Highlights recipes missing only one ingredient and shows the exact quantity shortage needed.
- **Unit Conversion & Normalization**: Built-in normalization for singular/plural ingredient names and unit conversions across metric and imperial systems (e.g., `g` to `kg`, `ml` to `l`, `tbsp` to `ml`, `oz` to `g`).
- **Theme & Preference Customization**: Light Mode / Dark Mode toggles and Metric / Imperial unit system hints saved via `SharedPreferences`.

---

## 💾 Database Architecture & Selection

### **Chosen Database Option: SQLite (`SQLiteOpenHelper`)**

For local data persistence, this application utilizes **Android's native SQLite Database** via the `SQLiteOpenHelper` framework (`smart_pantry.db`, database version `2`).

### **Why SQLite Was Chosen**:

1. **Zero External Dependencies**: SQLite is embedded natively within the Android OS, requiring no third-party libraries or complex setup overhead.
2. **Offline-First & Reliable Local Storage**: Pantry and recipe data remain fully accessible and functional without an active internet connection or backend server.
3. **Structured Relational Querying**: Allows performant relational SQL queries (`rawQuery`, `insert`, `update`, `delete`) necessary for querying ingredient records and calculating matching availability across recipe requirements.
4. **Controlled Schema & Database Seeding**: `SQLiteOpenHelper` provides structured lifecycle callbacks (`onCreate` and `onUpgrade`), making it straightforward to seed 20 pre-configured recipes in JSON format during the initial app database creation.

---

## 🛠️ Setup & Running Instructions

### **Prerequisites**
- **Android Studio**: Jellyfish (2024.1.1) or newer.
- **Android SDK**: API Level 34 or 35.
- **Java Development Kit (JDK)**: JDK 11 or higher.
- **Device / Emulator**: Android device or Android Virtual Device (AVD) running API 28 (Android 9.0) or higher.

### **Steps to Run the App**

1. **Clone or Open the Project**:
   - Launch Android Studio.
   - Click **File > Open...** and select the project directory:
     `402413138_MAD700_PantryManagerAssignment`

2. **Gradle Sync**:
   - Wait for Android Studio to automatically sync the Gradle project dependencies (`libs.versions.toml`).
   - If prompted, accept installing any missing SDK components or build tools.

3. **Build the Project**:
   - Go to **Build > Make Project** (or press `Ctrl + F9` / `Cmd + F9`).
   - Ensure the build completes without errors.

4. **Launch on Emulator / Device**:
   - Start an Android Virtual Device (AVD) from the Device Manager or connect a physical Android device with USB Debugging enabled.
   - Select your target device from the top toolbar dropdown.
   - Click the green **Run** button (or press `Shift + F10`).

5. **First Launch Behavior**:
   - Upon first startup, SQLite initializes `smart_pantry.db` and automatically seeds 20 built-in recipes.
   - Tap **Add ingredient** to populate your pantry and view recipe suggestions under **Recipes**.

---

## ⚙️ Tech Stack & Dependencies

- **Language**: Java 11
- **UI Framework**: Android XML Layouts, Material Design (`Theme.MaterialComponents.DayNight.NoActionBar`)
- **Components**: `RecyclerView`, `CardView`, `ScrollView`, `DatePickerDialog`
- **Database**: Native SQLite (`SQLiteOpenHelper`)
- **Preferences**: `SharedPreferences` & `AppCompatDelegate` for dynamic dark mode switching
- **Build System**: Gradle Kotlin DSL (`build.gradle.kts`)
