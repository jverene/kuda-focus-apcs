package focus.kudafocus.core;

import java.time.LocalDateTime;

/**
 * Represents a single distraction violation during a focus session.
 *
 * A violation occurs when the user opens a blocked application during
 * an active focus session. Each violation tracks:
 * - When it occurred (timestamp)
 * - Which app was opened (appName)
 * - How long the user was distracted (durationSeconds)
 * - How many times they dismissed the overlay (dismissCount)
 *
 * This data is used to calculate the focus score and provide detailed
 * session analytics.
 */
public class Violation {

    /**
     * When the violation occurred
     */
    private LocalDateTime timestamp;

    /**
     * Name of the blocked app that was opened
     */
    private String appName;

    /**
     * How long the user spent on the blocked app (in seconds)
     */
    private int durationSeconds;

    /**
     * Number of times the user dismissed the overlay for this violation
     */
    private int dismissCount;

    // ===== CONSTRUCTORS =====

    /**
     * Creates a new violation that just started
     *
     * @param appName Name of the blocked app
     */
    public Violation(String appName) {
        this.timestamp = LocalDateTime.now();
        this.appName = appName;
        this.durationSeconds = 0;
        this.dismissCount = 0;
    }

    /**
     * Creates a violation with all fields (used for deserialization)
     *
     * @param timestamp When the violation occurred
     * @param appName Name of the blocked app
     * @param durationSeconds Total time spent distracted
     * @param dismissCount Number of overlay dismissals
     */
    public Violation(LocalDateTime timestamp, String appName, int durationSeconds, int dismissCount) {
        this.timestamp = timestamp;
        this.appName = appName;
        this.durationSeconds = durationSeconds;
        this.dismissCount = dismissCount;
    }

    // ===== METHODS =====

    /**
     * Records an overlay dismissal for this violation
     */
    public void incrementDismissCount() {
        this.dismissCount++;
    }

    /**
     * Adds time to the duration of this violation
     *
     * @param seconds Number of seconds to add
     */
    public void addDuration(int seconds) {
        this.durationSeconds += seconds;
    }

    // ===== GETTERS =====

    /**
     * Get the timestamp when this violation occurred
     *
     * @return Violation timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Get the name of the blocked app
     *
     * @return App name
     */
    public String getAppName() {
        return appName;
    }

    /**
     * Get total duration spent on the blocked app
     *
     * @return Duration in seconds
     */
    public int getDurationSeconds() {
        return durationSeconds;
    }

    /**
     * Get duration in minutes (rounded down)
     *
     * @return Duration in minutes
     */
    public int getDurationMinutes() {
        return durationSeconds / 60;
    }

    /**
     * Get number of overlay dismissals
     *
     * @return Dismiss count
     */
    public int getDismissCount() {
        return dismissCount;
    }

    // ===== SETTERS (for deserialization) =====

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public void setDismissCount(int dismissCount) {
        this.dismissCount = dismissCount;
    }

    @Override
    public String toString() {
        return String.format("Violation{app='%s', duration=%ds, dismissals=%d, time=%s}",
                appName, durationSeconds, dismissCount, timestamp);
    }
}
