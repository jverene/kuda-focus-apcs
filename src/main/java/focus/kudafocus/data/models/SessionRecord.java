package focus.kudafocus.data.models;

import focus.kudafocus.core.Violation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a simplified session record for storage and persistence.
 * This class provides a data-oriented model of a focus session,
 * optimized for JSON serialization and historical analysis.
 */
public class SessionRecord {

    /**
     * Unique identifier for the session record.
     */
    private String id;

    /**
     * The date of the session in YYYY-MM-DD format.
     */
    private String date;

    /**
     * The date and time when the focus session started.
     */
    private LocalDateTime startTime;

    /**
     * The intended duration of the focus session in seconds.
     */
    private int plannedDuration;

    /**
     * The actual duration for which the focus session lasted in seconds.
     */
    private int actualDuration;

    /**
     * The calculated focus score representing session productivity.
     */
    private int focusScore;

    /**
     * Indicates whether the focus session was successfully completed.
     */
    private boolean completed;

    /**
     * The list of applications that were blocked during the session.
     */
    private List<String> blockedApps;

    /**
     * The list of websites that were blocked during the session.
     */
    private List<String> blockedWebsites;

    /**
     * The record of violations that occurred during the focus session.
     */
    private List<Violation> violations;

    // ===== CONSTRUCTORS =====

    /**
     * Initializes a new, empty instance of SessionRecord for JSON deserialization.
     */
    public SessionRecord() {
    }

    /**
     * Initializes a new instance of SessionRecord with all fields except blocked websites.
     *
     * @param id the unique identifier for the session record
     * @param date the session date in YYYY-MM-DD format
     * @param startTime the starting date and time of the session
     * @param plannedDuration the target duration in seconds
     * @param actualDuration the actual duration in seconds
     * @param focusScore the productivity score for the session
     * @param completed true if the session was completed; false otherwise
     * @param blockedApps the list of apps that were blocked
     * @param violations the list of focus violations recorded
     */
    public SessionRecord(String id, String date, LocalDateTime startTime, int plannedDuration,
                         int actualDuration, int focusScore, boolean completed,
                         List<String> blockedApps, List<Violation> violations) {
        this(id, date, startTime, plannedDuration, actualDuration, focusScore, completed, blockedApps, new ArrayList<>(), violations);
    }

    /**
     * Initializes a new instance of SessionRecord with a full set of session data.
     *
     * @param id the unique identifier for the session record
     * @param date the session date in YYYY-MM-DD format
     * @param startTime the starting date and time of the session
     * @param plannedDuration the target duration in seconds
     * @param actualDuration the actual duration in seconds
     * @param focusScore the productivity score for the session
     * @param completed true if the session was completed; false otherwise
     * @param blockedApps the list of apps that were blocked
     * @param blockedWebsites the list of websites that were blocked
     * @param violations the list of focus violations recorded
     */
    public SessionRecord(String id, String date, LocalDateTime startTime, int plannedDuration,
                         int actualDuration, int focusScore, boolean completed,
                         List<String> blockedApps, List<String> blockedWebsites, List<Violation> violations) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.plannedDuration = plannedDuration;
        this.actualDuration = actualDuration;
        this.focusScore = focusScore;
        this.completed = completed;
        this.blockedApps = blockedApps;
        this.blockedWebsites = blockedWebsites;
        this.violations = violations;
    }

    // ===== GETTERS AND SETTERS =====

    /**
     * Retrieves the session record identifier.
     *
     * @return the unique session ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the session record identifier.
     *
     * @param id the unique ID to be assigned
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Retrieves the date when the session occurred.
     *
     * @return the date string in YYYY-MM-DD format
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date when the session occurred.
     *
     * @param date the date string in YYYY-MM-DD format
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Retrieves the exact start time of the focus session.
     *
     * @return the local date and time of the session start
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time of the focus session.
     *
     * @param startTime the local date and time to assign as the start
     */
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Retrieves the planned session duration.
     *
     * @return the target duration in seconds
     */
    public int getPlannedDuration() {
        return plannedDuration;
    }

    /**
     * Sets the planned session duration.
     *
     * @param plannedDuration the target duration in seconds
     */
    public void setPlannedDuration(int plannedDuration) {
        this.plannedDuration = plannedDuration;
    }

    /**
     * Retrieves the actual time spent during the focus session.
     *
     * @return the actual duration in seconds
     */
    public int getActualDuration() {
        return actualDuration;
    }

    /**
     * Sets the actual duration of the focus session.
     *
     * @param actualDuration the actual duration in seconds
     */
    public void setActualDuration(int actualDuration) {
        this.actualDuration = actualDuration;
    }

    /**
     * Retrieves the productivity score for this session.
     *
     * @return the focus score
     */
    public int getFocusScore() {
        return focusScore;
    }

    /**
     * Sets the productivity score for this session.
     *
     * @param focusScore the focus score to assign
     */
    public void setFocusScore(int focusScore) {
        this.focusScore = focusScore;
    }

    /**
     * Checks if the focus session was completed without premature termination.
     *
     * @return true if the session was completed; false otherwise
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Sets whether the session was completed.
     *
     * @param completed true if the session was completed; false otherwise
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * Retrieves the list of applications that were restricted during this session.
     *
     * @return the list of blocked application names
     */
    public List<String> getBlockedApps() {
        return blockedApps;
    }

    /**
     * Sets the list of applications to be blocked.
     *
     * @param blockedApps the list of blocked application names
     */
    public void setBlockedApps(List<String> blockedApps) {
        this.blockedApps = blockedApps;
    }

    /**
     * Retrieves the list of websites that were restricted during this session.
     *
     * @return the list of blocked website URLs
     */
    public List<String> getBlockedWebsites() {
        return blockedWebsites;
    }

    /**
     * Sets the list of websites to be blocked.
     *
     * @param blockedWebsites the list of blocked website URLs
     */
    public void setBlockedWebsites(List<String> blockedWebsites) {
        this.blockedWebsites = blockedWebsites;
    }

    /**
     * Retrieves the list of violations that occurred during the session.
     *
     * @return the list of session violations
     */
    public List<Violation> getViolations() {
        return violations;
    }

    /**
     * Sets the list of violations that occurred during the session.
     *
     * @param violations the list of violations to assign
     */
    public void setViolations(List<Violation> violations) {
        this.violations = violations;
    }
}
