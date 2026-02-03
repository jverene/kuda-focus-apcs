package focus.kudafocus.core;

import focus.kudafocus.ui.UIConstants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a focus session demonstrating ENCAPSULATION in OOP.
 *
 * Encapsulation Demonstration:
 * - All fields are PRIVATE - cannot be accessed directly from outside
 * - Public getter/setter methods provide controlled access
 * - Focus score calculation is HIDDEN (private method)
 * - Internal state protected from invalid modifications
 *
 * This design ensures that:
 * - Session data integrity is maintained
 * - Score calculation logic is hidden from external code
 * - Changes to scoring algorithm don't affect other classes
 * - Only valid operations can be performed on session data
 *
 * Benefits of Encapsulation:
 * - Data hiding: Internal details are hidden
 * - Controlled access: Only through public methods
 * - Flexibility: Can change internal implementation without breaking external code
 * - Validation: Can validate data before accepting changes
 */
public class FocusSession {

    // ===== PRIVATE FIELDS (Encapsulation) =====
    // These fields are completely hidden from external classes

    /**
     * Unique identifier for this session
     */
    private String sessionId;

    /**
     * When the session started
     */
    private LocalDateTime startTime;

    /**
     * Planned duration in seconds
     */
    private int plannedDuration;

    /**
     * Actual duration in seconds (may differ if session was abandoned)
     */
    private int actualDuration;

    /**
     * List of all violations (distractions) during this session
     */
    private List<Violation> violations;

    /**
     * Calculated focus score (0-100)
     */
    private int focusScore;

    /**
     * Whether the session was completed successfully
     */
    private boolean completed;

    /**
     * List of apps that were blocked during this session
     */
    private List<String> blockedApps;

    /**
     * Current violation being tracked (if any)
     */
    private Violation currentViolation;

    // ===== CONSTRUCTORS =====

    /**
     * Creates a new focus session with specified duration and blocked apps.
     *
     * @param plannedDurationSeconds Duration in seconds
     * @param blockedApps List of app names to block
     */
    public FocusSession(int plannedDurationSeconds, List<String> blockedApps) {
        this.sessionId = UUID.randomUUID().toString();
        this.startTime = LocalDateTime.now();
        this.plannedDuration = plannedDurationSeconds;
        this.actualDuration = 0;
        this.violations = new ArrayList<>();
        this.blockedApps = new ArrayList<>(blockedApps);
        this.focusScore = UIConstants.SCORE_BASE; // Start with perfect score
        this.completed = false;
        this.currentViolation = null;
    }

    /**
     * Creates a session from stored data (for deserialization)
     */
    public FocusSession(String sessionId, LocalDateTime startTime, int plannedDuration,
                        int actualDuration, List<Violation> violations, int focusScore,
                        boolean completed, List<String> blockedApps) {
        this.sessionId = sessionId;
        this.startTime = startTime;
        this.plannedDuration = plannedDuration;
        this.actualDuration = actualDuration;
        this.violations = violations;
        this.focusScore = focusScore;
        this.completed = completed;
        this.blockedApps = blockedApps;
        this.currentViolation = null;
    }

    // ===== PUBLIC METHODS (Controlled Access) =====

    /**
     * Records the start of a new violation
     *
     * @param appName Name of the blocked app that was opened
     */
    public void startViolation(String appName) {
        // If there's already a current violation for a different app, end it first
        if (currentViolation != null && !currentViolation.getAppName().equals(appName)) {
            endCurrentViolation();
        }

        // Start new violation if not already tracking this app
        if (currentViolation == null) {
            currentViolation = new Violation(appName);
            violations.add(currentViolation);
        }
    }

    /**
     * Records an overlay dismissal for the current violation
     */
    public void recordDismissal() {
        if (currentViolation != null) {
            currentViolation.incrementDismissCount();
        }
    }

    /**
     * Adds time to the current violation duration
     *
     * @param seconds Number of seconds to add
     */
    public void addViolationDuration(int seconds) {
        if (currentViolation != null) {
            currentViolation.addDuration(seconds);
        }
    }

    /**
     * Ends the current violation (when user stops using blocked app)
     */
    public void endCurrentViolation() {
        currentViolation = null;
        recalculateFocusScore();
    }

    /**
     * Marks the session as completed and calculates final score
     *
     * @param actualDurationSeconds Actual session duration
     */
    public void complete(int actualDurationSeconds) {
        this.actualDuration = actualDurationSeconds;
        this.completed = true;
        if (currentViolation != null) {
            endCurrentViolation();
        }
        recalculateFocusScore();
    }

    /**
     * Marks the session as abandoned (not completed)
     *
     * @param actualDurationSeconds How long they lasted before quitting
     */
    public void abandon(int actualDurationSeconds) {
        this.actualDuration = actualDurationSeconds;
        this.completed = false;
        if (currentViolation != null) {
            endCurrentViolation();
        }
        recalculateFocusScore();
    }

    /**
     * Checks if this session qualifies for streak counting.
     * Qualification criteria:
     * - Duration >= 30 minutes
     * - Focus score >= 80
     * - Successfully completed
     *
     * @return true if session qualifies for streak
     */
    public boolean qualifiesForStreak() {
        int durationMinutes = actualDuration / 60;
        return completed &&
                durationMinutes >= UIConstants.MIN_STREAK_DURATION_MINUTES &&
                focusScore >= UIConstants.MIN_STREAK_SCORE;
    }

    /**
     * Gets the name of the most distracting app (most total time)
     *
     * @return App name, or "None" if no violations
     */
    public String getMostDistractingApp() {
        if (violations.isEmpty()) {
            return "None";
        }

        String mostDistracting = violations.get(0).getAppName();
        int maxDuration = violations.get(0).getDurationSeconds();

        for (Violation v : violations) {
            if (v.getDurationSeconds() > maxDuration) {
                maxDuration = v.getDurationSeconds();
                mostDistracting = v.getAppName();
            }
        }

        return mostDistracting;
    }

    /**
     * Gets total number of distraction occurrences
     *
     * @return Number of violations
     */
    public int getViolationCount() {
        return violations.size();
    }

    /**
     * Gets total number of overlay dismissals across all violations
     *
     * @return Total dismissals
     */
    public int getTotalDismissals() {
        int total = 0;
        for (Violation v : violations) {
            total += v.getDismissCount();
        }
        return total;
    }

    /**
     * Gets total time spent on blocked apps in seconds
     *
     * @return Total distraction time in seconds
     */
    public int getTotalDistractionSeconds() {
        int total = 0;
        for (Violation v : violations) {
            total += v.getDurationSeconds();
        }
        return total;
    }

    // ===== PRIVATE METHODS (Hidden Implementation) =====

    /**
     * PRIVATE METHOD - Demonstrates encapsulation!
     *
     * Calculates the focus score based on violations.
     * This formula is completely hidden from external code.
     * External classes can only ACCESS the score via getFocusScore(),
     * they cannot see HOW it's calculated.
     *
     * Formula:
     * - Base: 100 points
     * - Violation penalty: -5 per occurrence
     * - Dismissal penalty: -2 per dismissal
     * - Time penalty: -1 per minute on blocked apps
     * - Minimum: 0 points
     */
    private void recalculateFocusScore() {
        int score = UIConstants.SCORE_BASE;

        // Deduct for each violation occurrence
        score -= violations.size() * UIConstants.SCORE_VIOLATION_PENALTY;

        // Deduct for each overlay dismissal
        score -= getTotalDismissals() * UIConstants.SCORE_DISMISSAL_PENALTY;

        // Deduct for time spent distracted (per minute)
        int totalMinutes = getTotalDistractionSeconds() / 60;
        score -= totalMinutes * UIConstants.SCORE_TIME_PENALTY_PER_MINUTE;

        // Ensure score stays within 0-100 range
        this.focusScore = Math.max(0, Math.min(100, score));
    }

    // ===== GETTERS (Public Access to Private Data) =====

    /**
     * Get session ID
     *
     * @return Session ID
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Get start time
     *
     * @return Start time
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Get date portion of start time
     *
     * @return Date string (YYYY-MM-DD)
     */
    public String getDate() {
        return startTime.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * Get planned duration in seconds
     *
     * @return Planned duration
     */
    public int getPlannedDuration() {
        return plannedDuration;
    }

    /**
     * Get planned duration in minutes
     *
     * @return Planned duration in minutes
     */
    public int getPlannedDurationMinutes() {
        return plannedDuration / 60;
    }

    /**
     * Get actual duration in seconds
     *
     * @return Actual duration
     */
    public int getActualDuration() {
        return actualDuration;
    }

    /**
     * Get actual duration in minutes
     *
     * @return Actual duration in minutes
     */
    public int getActualDurationMinutes() {
        return actualDuration / 60;
    }

    /**
     * Get list of violations (defensive copy to protect internal state)
     *
     * @return Copy of violations list
     */
    public List<Violation> getViolations() {
        return new ArrayList<>(violations);
    }

    /**
     * Get focus score (0-100)
     * NOTE: External code can GET the score but cannot see HOW it's calculated
     *
     * @return Focus score
     */
    public int getFocusScore() {
        return focusScore;
    }

    /**
     * Check if session was completed
     *
     * @return true if completed, false if abandoned
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Get list of blocked apps for this session
     *
     * @return Copy of blocked apps list
     */
    public List<String> getBlockedApps() {
        return new ArrayList<>(blockedApps);
    }

    /**
     * Check if there's a current active violation
     *
     * @return true if currently violated
     */
    public boolean hasActiveViolation() {
        return currentViolation != null;
    }

    /**
     * Get the current active violation, if any
     *
     * @return Current violation or null
     */
    public Violation getCurrentViolation() {
        return currentViolation;
    }

    // ===== SETTERS (for deserialization) =====

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setPlannedDuration(int plannedDuration) {
        this.plannedDuration = plannedDuration;
    }

    public void setActualDuration(int actualDuration) {
        this.actualDuration = actualDuration;
    }

    public void setViolations(List<Violation> violations) {
        this.violations = violations;
    }

    public void setFocusScore(int focusScore) {
        this.focusScore = focusScore;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setBlockedApps(List<String> blockedApps) {
        this.blockedApps = blockedApps;
    }

    @Override
    public String toString() {
        return String.format("FocusSession{id='%s', start=%s, duration=%d/%d min, score=%d, violations=%d, completed=%b}",
                sessionId, startTime, actualDuration / 60, plannedDuration / 60,
                focusScore, violations.size(), completed);
    }
}
