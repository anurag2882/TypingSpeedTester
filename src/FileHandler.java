import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class FileHandler {

    // The file where results are saved
    private static final String RESULTS_FILE = "typing_results.csv";

    // Date-time formatter for display
    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    /**
     * Save a single test result to the CSV file.
     * Appends to the file so previous results are not lost.
     *
     * @param wpm      Words Per Minute
     * @param accuracy Accuracy percentage
     * @param timeSecs Time taken in seconds
     * @param difficulty Difficulty level used
     */
    public static void saveResult(double wpm, double accuracy, double timeSecs, String difficulty) {
        // Get current date and time
        String dateTime = LocalDateTime.now().format(FORMATTER);

        // Try-with-resources automatically closes the file even if an error occurs
        try (FileWriter fw = new FileWriter(RESULTS_FILE, true); // 'true' = append mode
             BufferedWriter bw = new BufferedWriter(fw)) {

            // Check if file is new (empty) and write header
            File file = new File(RESULTS_FILE);
            if (file.length() == 0) {
                bw.write("Date & Time,WPM,Accuracy (%),Time (sec),Difficulty");
                bw.newLine();
            }

            // Format: DateTime, WPM (2 decimals), Accuracy (2 decimals), Time, Difficulty
            String line = String.format("%s,%.2f,%.2f,%.1f,%s",
                dateTime, wpm, accuracy, timeSecs, difficulty);
            bw.write(line);
            bw.newLine();

        } catch (IOException e) {
            System.err.println("Error saving result: " + e.getMessage());
        }
    }

    /**
     * Read all past results from the CSV file.
     * Returns them as a list of string arrays (each row = one result).
     *
     * @return ArrayList of String[] where each array is [DateTime, WPM, Accuracy, Time, Difficulty]
     */
    public static ArrayList<String[]> loadResults() {
        ArrayList<String[]> results = new ArrayList<>();
        File file = new File(RESULTS_FILE);

        // If file doesn't exist yet, return empty list
        if (!file.exists()) {
            return results;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                // Skip the header row
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                if (!line.trim().isEmpty()) {
                    // Split by comma to get individual fields
                    String[] parts = line.split(",");
                    results.add(parts);
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading results: " + e.getMessage());
        }

        return results;
    }

    /**
     * Get the total number of tests recorded.
     * @return Count of past results
     */
    public static int getTotalTests() {
        return loadResults().size();
    }

    /**
     * Get the best WPM ever recorded.
     * @return Best WPM as a double
     */
    public static double getBestWPM() {
        ArrayList<String[]> results = loadResults();
        double best = 0;
        for (String[] row : results) {
            if (row.length >= 2) {
                try {
                    double wpm = Double.parseDouble(row[1]);
                    if (wpm > best) best = wpm;
                } catch (NumberFormatException ignored) {}
            }
        }
        return best;
    }

    /**
     * Check whether the results file exists.
     * @return true if the file exists
     */
    public static boolean resultsFileExists() {
        return new File(RESULTS_FILE).exists();
    }
}
