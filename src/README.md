================================================================
   TYPING SPEED TESTER — HOW TO RUN
================================================================

REQUIREMENTS:
  - Java JDK 8 or above installed on your computer
  - To check: open Command Prompt and type: java -version

----------------------------------------------------------------
METHOD 1 — Using Command Prompt (Recommended)
----------------------------------------------------------------

Step 1: Open Command Prompt (cmd)

Step 2: Navigate to the project's src folder
        cd path\to\TypingSpeedTester\src

Step 3: Compile all Java files
        javac *.java

Step 4: Run the program
        java Main

----------------------------------------------------------------
METHOD 2 — Using an IDE (VS Code / IntelliJ / Eclipse)
----------------------------------------------------------------

1. Open your IDE
2. Import/Open the TypingSpeedTester folder as a project
3. Navigate to src/Main.java
4. Right-click → "Run" or press the Run button

----------------------------------------------------------------
PROJECT FILE STRUCTURE
----------------------------------------------------------------

TypingSpeedTester/
│
├── src/
│   ├── Main.java            ← Entry point (run this)
│   ├── TypingSpeedUI.java   ← Main UI window
│   ├── TypingLogic.java     ← WPM & accuracy calculations
│   ├── FileHandler.java     ← File save/read operations
│   └── ResultsViewer.java   ← Past results popup
│
├── PROJECT_REPORT.txt       ← Full project report
├── VIVA_QA.txt              ← Viva questions & answers
└── README.txt               ← This file

After running, a file called "typing_results.csv" will be
created automatically in the same folder where you run the
program. This file stores all your test results.

================================================================
