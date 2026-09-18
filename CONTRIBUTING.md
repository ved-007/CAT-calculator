# Contributing to CAT On-Screen Calculator

Thank you for your interest in contributing to the **CAT On-Screen Calculator** project! Whether you want to report a bug, suggest an enhancement, or fix an issue, this guide covers everything you need to get started.

---

## 🛠️ Prerequisites

To build and run the project from source, you will need:
- **Java Development Kit (JDK) 17 or higher** (JDK 21 or 24 recommended).
  - Verify with: `javac -version` and `java -version`.
- **Git** installed on your system.

---

## 🚀 Getting Started (Step-by-Step)

### 1. Fork and Clone the Repository
1. Navigate to the repository: [https://github.com/ved-007/CAT-calculator](https://github.com/ved-007/CAT-calculator)
2. Click the **Fork** button (top right) to create a copy in your own GitHub account.
3. Clone your fork locally:
   ```powershell
   git clone https://github.com/<your-username>/CAT-calculator.git
   cd CAT-calculator
   ```

### 2. Project Architecture Overview

The source code is organized cleanly under `src/com/catcalculator/`:

| Package / File | Description |
| :--- | :--- |
| `src/.../model/CalculatorEngine.java` | Core calculation logic, memory registers (`MC`, `MR`, `MS`, `M+`, `M-`), operator precedence, and 8-digit precision. |
| `src/.../input/MouseOnlyFilter.java` | Global AWT `KeyEventDispatcher` that intercepts and blocks keyboard input to enforce the strict CAT mouse-only exam rule. |
| `src/.../ui/CalculatorFrame.java` | Main floating window, window controls, and always-on-top management. |
| `src/.../ui/TitleBar.java` | Draggable custom blue header with vector-rendered icons (Pin, Options, Minimize, Close). |
| `src/.../ui/CalculatorPanel.java` | Dual display layout and the 5x6 button grid. |
| `src/.../ui/CatButton.java` | Custom-rendered buttons matching official CAT colors, fonts, and 3D tactile press effects. |
| `src/.../test/EngineTestRunner.java` | Headless test suite verifying arithmetic, unary operations, memory, precedence, and error states. |

---

## 💻 Building and Testing Locally

### Compile All Code
```powershell
if (!(Test-Path bin)) { New-Item -ItemType Directory -Path bin }
javac -encoding UTF-8 -d bin (Get-ChildItem -Path src -Recurse -Filter *.java).FullName
```

### Run the Automated Tests
Always run the test suite to ensure no regressions:
```powershell
java -cp bin com.catcalculator.test.EngineTestRunner
```

### Run the Application Locally
```powershell
java -cp bin com.catcalculator.Main
```

### Package into Executable JAR
```powershell
jar --create --file CatCalculator.jar --main-class com.catcalculator.Main -C bin com
```

---

## 🔄 Making Changes & Submitting a Pull Request (PR)

1. **Create a new branch** for your fix or feature:
   ```powershell
   git checkout -b fix-operator-bug
   ```

2. **Make your changes**:
   - If modifying math logic, update or add test cases in `EngineTestRunner.java`.
   - Ensure all tests pass (`java -cp bin com.catcalculator.test.EngineTestRunner`).

3. **Commit your changes**:
   ```powershell
   git add .
   git commit -m "Fix: Correct modulo precedence issue in CalculatorEngine"
   ```

4. **Push to your fork**:
   ```powershell
   git push -u origin fix-operator-bug
   ```

5. **Open a Pull Request**:
   - Go to [https://github.com/ved-007/CAT-calculator](https://github.com/ved-007/CAT-calculator).
   - GitHub will display a banner saying *"Compare & pull request"*.
   - Click it, describe what you fixed or changed, and submit the PR!

---

## 📦 For Maintainers: Releasing an Updated Version

Once a Pull Request is reviewed and merged into `main`:

1. Pull the latest `main` branch locally:
   ```powershell
   git pull origin main
   ```
2. Recompile and package the updated `CatCalculator.jar`:
   ```powershell
   javac -encoding UTF-8 -d bin (Get-ChildItem -Path src -Recurse -Filter *.java).FullName
   jar --create --file CatCalculator.jar --main-class com.catcalculator.Main -C bin com
   ```
3. Rebuild the standalone Windows `.exe` bundle using `jpackage`:
   ```powershell
   Remove-Item -Recurse -Force dist
   jpackage --name "CAT-Calculator" --input . --main-jar CatCalculator.jar --main-class com.catcalculator.Main --type app-image --dest dist
   Compress-Archive -Path dist\CAT-Calculator\* -DestinationPath CAT-Calculator-Windows-x64.zip -Force
   ```
4. Create a new release (e.g. `v1.0.1` or `v1.1.0`) on GitHub and attach the updated `CAT-Calculator-Windows-x64.zip` and `CatCalculator.jar`.
