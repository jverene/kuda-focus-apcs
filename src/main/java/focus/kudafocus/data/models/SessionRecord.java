package focus.kudafocus.data.models;

import focus.kudafocus.core.Violation;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Session record for storage.
 * This is a simplified version of FocusSession for JSON serialization.
 */
public class SessionRecord {

    private String id;
    private String date;  // YYYY-MM-DD format
    private LocalDateTime startTime;
    private int plannedDuration;
    private int actualDuration;
    private int focusScore;
    private boolean completed;
    private List<String> blockedApps;
    private List<Violation> violations;

    // ===== CONSTRUCTORS =====

    /**
     * Default constructor for Gson
     */
    public SessionRecord() {
    }

    /**
     * Creates a session record from all fields
     */
    public SessionRecord(String id, String date, LocalDateTime startTime, int plannedDuration,
                         int actualDuration, int focusScore, boolean completed,
                         List<String> blockedApps, List<Violation> violations) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.plannedDuration = plannedDuration;
        this.actualDuration = actualDuration;
        this.focusScore = focusScore;
        this.completed = completed;
        this.blockedApps = blockedApps;
        this.violations = violations;
    }

    // ===== GETTERS AND SETTERS =====

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public int getPlannedDuration() {
        return plannedDuration;
    }

    public void setPlannedDuration(int plannedDuration) {
        this.plannedDuration = plannedDuration;
    }

    public int getActualDuration() {
        return actualDuration;
    }

    public void setActualDuration(int actualDuration) {
        this.actualDuration = actualDuration;
    }

    public int getFocusScore() {
        return focusScore;
    }

    public void setFocusScore(int focusScore) {
        this.focusScore = focusScore;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public List<String> getBlockedApps() {
        return blockedApps;
    }

    public void setBlockedApps(List<String> blockedApps) {
        this.blockedApps = blockedApps;
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public void setViolations(List<Violation> violations) {
        this.violations = violations;
    }
}
