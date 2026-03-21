package focus.kudafocus.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Tracks user's streak of consecutive days with qualifying focus sessions.
 * Persists streak data to a JSON file in the user's home directory.
 *
 * A session qualifies for the streak if:
 * - Duration >= 30 minutes
 * - Focus score >= 80
 * - Session completed successfully
 *
 * The streak increments by 1 for each qualifying session on a new day.
 * It resets to 0 if a day is missed (no qualifying session).
 */
public class StreakTracker {
    /**
     * The name of the directory where application data is stored.
     */
    private static final String DATA_DIR = ".kudafocus";

    /**
     * The name of the file where streak data is persisted.
     */
    private static final String STREAK_FILE = "streak_data.json";

    /**
     * The current number of consecutive days with qualifying focus sessions.
     */
    private int currentStreak;

    /**
     * The date when the last qualifying focus session occurred.
     */
    private LocalDate lastQualifyingDate;

    /**
     * The path to the file where streak data is stored.
     */
    private final Path dataFilePath;

    /**
     * The GSON instance used for JSON serialization and deserialization.
     */
    private final Gson gson;

    /**
     * Creates a new StreakTracker and loads existing streak data.
     */
    public StreakTracker() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        // Set up data directory path
        String userHome = System.getProperty("user.home");
        Path dataDir = Paths.get(userHome, DATA_DIR);
        this.dataFilePath = dataDir.resolve(STREAK_FILE);

        // Initialize with loaded data
        loadStreak();
    }

    /**
     * Loads streak data from the persistence file.
     * Initializes the streak to 0 if no data exists or an error occurs.
     */
    private void loadStreak() {
        try {
            if (Files.exists(dataFilePath)) {
                String content = Files.readString(dataFilePath, StandardCharsets.UTF_8);
                StreakData data = gson.fromJson(content, StreakData.class);
                this.currentStreak = data.currentStreak;
                this.lastQualifyingDate = LocalDate.parse(data.lastQualifyingDate);
            } else {
                this.currentStreak = 0;
                this.lastQualifyingDate = null;
            }
        } catch (Exception e) {
            System.err.println("Error loading streak data: " + e.getMessage());
            this.currentStreak = 0;
            this.lastQualifyingDate = null;
        }
    }

    /**
     * Saves the current streak data to the persistence file.
     * Creates the data directory if it does not exist.
     */
    private void saveStreak() {
        try {
            // Create directory if it doesn't exist
            Path dataDir = dataFilePath.getParent();
            Files.createDirectories(dataDir);

            // Create and save data object
            StreakData data = new StreakData(
                    currentStreak,
                    lastQualifyingDate != null ? lastQualifyingDate.toString() : null
            );

            String json = gson.toJson(data);
            Files.writeString(dataFilePath, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Error saving streak data: " + e.getMessage());
        }
    }

    /**
     * Gets the current streak count.
     *
     * @return Number of consecutive days with qualifying sessions
     */
    public int getCurrentStreak() {
        return currentStreak;
    }

    /**
     * Gets the date of the last qualifying session.
     *
     * @return LocalDate of last qualifying session, or null if no sessions
     */
    public LocalDate getLastQualifyingDate() {
        return lastQualifyingDate;
    }

    /**
     * Updates the streak based on a newly completed session.
     *
     * Streak logic:
     * - If today is the same as lastQualifyingDate: no change (already counted today)
     * - If today is exactly 1 day after lastQualifyingDate: increment streak
     * - If today is more than 1 day after: reset to 1 (streak broken)
     * - If no previous data: set to 1
     *
     * @param qualifiesForStreak true if the completed session qualifies for streak
     */
    public void recordSession(boolean qualifiesForStreak) {
        if (!qualifiesForStreak) {
            return; // Session doesn't qualify, no update needed
        }

        LocalDate today = LocalDate.now();

        // If this is the first qualifying session or same day, handle appropriately
        if (lastQualifyingDate == null) {
            // First qualifying session ever
            currentStreak = 1;
            lastQualifyingDate = today;
        } else if (lastQualifyingDate.equals(today)) {
            // Already counted a qualifying session today, no change
            // (user can do multiple sessions per day, but it only counts once)
            return;
        } else if (ChronoUnit.DAYS.between(lastQualifyingDate, today) == 1) {
            // Consecutive day! Increment the streak
            currentStreak++;
            lastQualifyingDate = today;
        } else {
            // Streak broken (more than 1 day gap)
            currentStreak = 1;
            lastQualifyingDate = today;
        }

        // Save updated data
        saveStreak();
    }

    /**
     * Resets the current streak to zero and clears the last qualifying date.
     */
    public void resetStreak() {
        currentStreak = 0;
        lastQualifyingDate = null;
        saveStreak();
    }

    /**
     * Represents the data structure used for streak persistence.
     */
    private static class StreakData {
        /**
         * The current streak count.
         */
        int currentStreak;

        /**
         * The ISO-8601 string representation of the last qualifying date.
         */
        String lastQualifyingDate; // ISO-8601 format

        /**
         * Creates a new StreakData instance.
         *
         * @param currentStreak The current streak count
         * @param lastQualifyingDate The date of the last qualifying session as a string
         */
        StreakData(int currentStreak, String lastQualifyingDate) {
            this.currentStreak = currentStreak;
            this.lastQualifyingDate = lastQualifyingDate;
        }
    }
}
