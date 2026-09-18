# CAT On-Screen Calculator (Windows Desktop)

A native Windows desktop application that authenticates the official on-screen virtual calculator used in the CAT (Common Admission Test) examination.

Designed for CAT aspirants who want to build accurate mouse-clicking muscle memory while practicing with mock tests, sectional tests, or PDFs.

---

## Features

- **Floating & Draggable:** Undecorated window styled with the authentic CAT blue titlebar (`#2f6ea5`) that can be positioned anywhere on your screen.
- **Always on Top (Pin 📌):** Stays floating on top of your browser mock test tabs (AIMCATs, SIMCATs, CL, Cracku, etc.) and PDFs.
- **Strict Mouse-Only Mode:** Keyboard typing is blocked by default to faithfully simulate actual exam conditions.
- **Interactive Options Menu (`...`):**
  - Toggle Always on Top
  - Toggle Strict Mouse-Only Mode
  - Adjust Window Opacity (100%, 90%, 80%, 70%) for translucent overlays
  - Center Window
  - Reset Calculator State
- **Authentic Mathematical Engine:**
  - Memory registers: `MC`, `MR`, `MS`, `M+`, `M-` with active `M` display badge.
  - Operations: `+`, `-`, `*`, `/`, `%` with CAT operator precedence.
  - Unary functions: Square Root (`√`), Reciprocal (`1/x`), Negate (`+/-`).
  - Editing: Backspace (`←`), Clear (`C`).
  - Standard 8-digit precision and error handling (`Math Error`).

---

## How to Download & Run (For Friends & Aspirants)

### Option 1: Standalone Version (Zero Setup Required — Recommended)
*No Java or dependencies needed!*

1. Go to the **[Releases](../../releases)** tab.
2. Download **`CAT-Calculator-Windows-x64.zip`**.
3. Right-click the downloaded `.zip` file and select **Extract All...**.
4. Open the extracted folder and double-click **`CAT-Calculator.exe`**.
5. (Optional) Right-click `CAT-Calculator.exe` → **Show more options** → **Send to** → **Desktop (create shortcut)**.

### Option 2: Lightweight Executable JAR (If Java is installed)
1. Download **`CatCalculator.jar`** from the **[Releases](../../releases)** tab.
2. Double-click **`CatCalculator.jar`** to launch directly.

---

## Building From Source

Prerequisites:
- Java JDK 17 or higher (tested on JDK 24)

Clone the repository and run:
```powershell
# Compile source files
javac -encoding UTF-8 -d bin (Get-ChildItem -Path src -Recurse -Filter *.java).FullName

# Package JAR
jar --create --file CatCalculator.jar --main-class com.catcalculator.Main -C bin com

# Run application
java -jar CatCalculator.jar
```

Or build a standalone Windows `.exe` using `jpackage`:
```powershell
jpackage --name "CAT-Calculator" --input . --main-jar CatCalculator.jar --main-class com.catcalculator.Main --type app-image --dest dist
```

---

## License
MIT License
