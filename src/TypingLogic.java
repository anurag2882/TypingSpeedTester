import java.util.ArrayList;
import java.util.Arrays;

public class TypingLogic {

    // ─── Sentence Banks by Difficulty ────────────────────────────────────────

    private static final String[] EASY_SENTENCES = {
        "The cat sat on the mat.",
        "I love to eat pizza and pasta.",
        "The sun is bright and the sky is blue.",
        "She sells sea shells by the sea shore.",
        "My dog likes to run in the park.",
        "Java is a popular programming language.",
        "Reading books is a good habit.",
        "Apples and oranges are healthy fruits.",
        "The quick fox jumped over the fence.",
        "Learning to code is fun and rewarding."
    };

    private static final String[] MEDIUM_SENTENCES = {
        "The quick brown fox jumps over the lazy dog.",
        "To be or not to be, that is the question.",
        "A journey of a thousand miles begins with a single step.",
        "Practice makes a man perfect in all walks of life.",
        "Success is not the key to happiness; happiness is the key to success.",
        "All that glitters is not gold, but it still catches the eye.",
        "The best time to plant a tree was twenty years ago.",
        "In the middle of every difficulty lies opportunity.",
        "Computer science is not just about coding and algorithms.",
        "Hard work and dedication are the keys to achieving your goals."
    };

    private static final String[] HARD_SENTENCES = {
        "Programming requires logical thinking, patience, and persistent problem-solving skills.",
        "Quantum computing leverages superposition and entanglement to process information exponentially faster.",
        "The Byzantine Fault Tolerance algorithm ensures consensus even when nodes behave maliciously.",
        "Polymorphism, encapsulation, inheritance, and abstraction are the four pillars of object-oriented programming.",
        "Artificial intelligence and machine learning are transforming industries at an unprecedented pace.",
        "Asynchronous programming models improve application responsiveness by avoiding blocking operations.",
        "The time complexity of quicksort is O(n log n) on average, but O(n^2) in the worst case.",
        "Distributed systems must handle network partitions, latency, and node failures gracefully.",
        "Cryptographic hash functions produce fixed-size digests that are computationally infeasible to reverse.",
        "Microservices architecture decomposes monolithic applications into independently deployable, loosely coupled services."
    };

    // Currently selected sentence for the test
    private String targetSentence;

    // Track start time (milliseconds)
    private long startTime;

    // Difficulty level: "Easy", "Medium", "Hard"
    private String difficulty;

    /**
     * Constructor — sets default difficulty to Medium
     */
    public TypingLogic() {
        this.difficulty = "Medium";
    }

    /**
     * Set the difficulty level and pick a random sentence for that level.
     * @param difficulty "Easy", "Medium", or "Hard"
     * @return The randomly selected sentence
     */
    public String getRandomSentence(String difficulty) {
        this.difficulty = difficulty;
        String[] pool;

        switch (difficulty) {
            case "Easy":   pool = EASY_SENTENCES;   break;
            case "Hard":   pool = HARD_SENTENCES;   break;
            default:       pool = MEDIUM_SENTENCES; break;
        }

        // Pick a random index
        int randomIndex = (int) (Math.random() * pool.length);
        targetSentence = pool[randomIndex];
        return targetSentence;
    }

    /**
     * Record the start time of the test.
     */
    public void startTimer() {
        startTime = System.currentTimeMillis();
    }

    /**
     * Calculate elapsed time in seconds.
     * @return Time in seconds since startTimer() was called
     */
    public double getElapsedSeconds() {
        long endTime = System.currentTimeMillis();
        return (endTime - startTime) / 1000.0;
    }

    /**
     * Calculate Words Per Minute.
     * Formula: WPM = (number of words typed) / (time in minutes)
     *
     * @param typedText The text the user typed
     * @param elapsedSeconds Time taken in seconds
     * @return WPM as a double
     */
    public double calculateWPM(String typedText, double elapsedSeconds) {
        if (elapsedSeconds <= 0) return 0;

        // Count words by splitting on spaces
        String trimmed = typedText.trim();
        if (trimmed.isEmpty()) return 0;

        int wordCount = trimmed.split("\\s+").length;
        double minutes = elapsedSeconds / 60.0;

        return wordCount / minutes;
    }

    /**
     * Calculate Accuracy.
     * Formula: Accuracy = (correct characters / total characters in target) × 100
     *
     * @param typedText The text the user typed
     * @return Accuracy as a percentage (0.0 to 100.0)
     */
    public double calculateAccuracy(String typedText) {
        if (targetSentence == null || targetSentence.isEmpty()) return 0;

        int totalChars = targetSentence.length();
        int correctChars = 0;

        // Compare character by character up to the length of the shorter string
        int compareLength = Math.min(typedText.length(), totalChars);
        for (int i = 0; i < compareLength; i++) {
            if (typedText.charAt(i) == targetSentence.charAt(i)) {
                correctChars++;
            }
        }

        return ((double) correctChars / totalChars) * 100.0;
    }

    /**
     * Count how many characters are correct (for live highlighting).
     * @param typedText Text typed so far
     * @return Number of correct characters from the start
     */
    public int countCorrectChars(String typedText) {
        int correct = 0;
        int len = Math.min(typedText.length(), targetSentence.length());
        for (int i = 0; i < len; i++) {
            if (typedText.charAt(i) == targetSentence.charAt(i)) {
                correct++;
            } else {
                break; // Stop at first mistake (for streak counting)
            }
        }
        return correct;
    }

    /**
     * Get the current target sentence.
     */
    public String getTargetSentence() {
        return targetSentence;
    }

    /**
     * Get the current difficulty.
     */
    public String getDifficulty() {
        return difficulty;
    }
}
