# ⌨️ Typing Speed Tester

A Java Swing desktop application that measures typing speed (WPM) and accuracy in real time, with a persistent results history.

## Features

- **Three difficulty levels** (Easy / Medium / Hard), each with its own bank of practice sentences
- **Live timer** that starts automatically on your first keystroke
- **Real-time WPM & accuracy calculation** once you hit Finish
- **Color-coded accuracy feedback** (green / orange / red based on performance)
- **Persistent history** — every result is saved to a local CSV file
- **History viewer** — a dedicated dialog showing all past attempts in a table, plus your total test count and best WPM
- **Restart flow** to immediately retry with a new sentence
- Custom dark-themed UI built entirely with Java Swing (no external UI libraries)

## Tech Stack

- **Java** (core logic)
- **Java Swing** (GUI — JFrame, JDialog, JTable, custom-styled components)
- **File I/O** — CSV-based persistence, no external database

## Project Structure

```
TypingSpeedTester/
│
├── src/
│   ├── Main.java            ← Entry point (run this)
│   ├── TypingSpeedUI.java   ← Main application window & UI logic
│   ├── TypingLogic.java     ← Sentence selection, WPM & accuracy calculations
│   ├── FileHandler.java     ← Saves/reads results to/from typing_results.csv
│   └── ResultsViewer.java   ← Popup dialog showing past results & stats
│
├── typing_results.csv       ← Auto-generated results log (created on first run)
└── README.md
```

## How It Works

1. Choose a difficulty level (Easy, Medium, or Hard) from the dropdown.
2. Click **Start** — a random sentence is loaded for that difficulty.
3. Start typing in the input box; the timer begins on your first keystroke.
4. Click **Finish** to stop the timer and see your WPM, accuracy, and time taken.
5. Your result is automatically appended to `typing_results.csv`.
6. Click **History** at any time to view all past attempts, along with your total test count and personal best WPM.
7. Click **Restart** to try again with a new sentence.

## Requirements

- Java JDK 8 or above
- Check your version: `java -version`

## Running the Project

**Using Command Prompt / Terminal:**
```bash
cd path/to/TypingSpeedTester/src
javac *.java
java Main
```

**Using an IDE (VS Code / IntelliJ / Eclipse):**
1. Open/import the `TypingSpeedTester` folder as a project.
2. Navigate to `src/Main.java`.
3. Run the file directly.

## Author

Anurag Mishra