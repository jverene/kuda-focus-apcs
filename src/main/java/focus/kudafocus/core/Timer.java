package focus.kudafocus.core;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Countdown timer for focus sessions.
 *
 * This class provides second-precision countdown functionality with callbacks
 * for UI updates. It uses JavaFX Timeline for smooth, thread-safe updates
 * that work well with the JavaFX Application Thread.
 *
 * Key Concepts (for APCS):
 * - Callback pattern: Allows other code to respond to timer events
 * - Encapsulation: Timer state is private, controlled through methods
 * - Event-driven programming: UI updates triggered by timer ticks
 */
public class Timer {

    /**
     * Callback interface for timer events.
     * Classes that want to respond to timer updates implement this interface.
     *
     * This demonstrates the OBSERVER PATTERN - the timer notifies observers
     * when something interesting happens (tick or completion).
     */
    public interface TimerCallback {
        /**
         * Called every second while timer is running
         *
         * @param remainingSeconds Time left in seconds
         */
        void onTick(int remainingSeconds);

        /**
         * Called when timer reaches zero
         */
        void onComplete();
    }

    // ===== PRIVATE FIELDS =====

    /**
     * Total duration of the timer in seconds
     */
    private final int totalDuration;

    /**
     * Current remaining time in seconds
     */
    private int remainingSeconds;

    /**
     * Time that has elapsed in seconds
     */
    private int elapsedSeconds;

    /**
     * JavaFX Timeline that handles the countdown
     * Timeline is better than ScheduledExecutorService for JavaFX because
     * it automatically runs on the JavaFX Application Thread
     */
    private Timeline timeline;

    /**
     * Callback to notify about timer events
     */
    private final TimerCallback callback;

    /**
     * Whether the timer is currently running
     */
    private boolean running;

    /**
     * Whether the timer has been paused
     */
    private boolean paused;

    // ===== CONSTRUCTOR =====

    /**
     * Creates a new timer with specified duration and callback.
     *
     * @param durationSeconds Total duration in seconds
     * @param callback Callback for timer events
     */
    public Timer(int durationSeconds, TimerCallback callback) {
        this.totalDuration = durationSeconds;
        this.remainingSeconds = durationSeconds;
        this.elapsedSeconds = 0;
        this.callback = callback;
        this.running = false;
        this.paused = false;

        // Create the JavaFX Timeline
        // KeyFrame.onFinished runs every 1 second (1000ms)
        this.timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> tick())
        );

        // Set to run indefinitely (we'll stop it manually when done)
        this.timeline.setCycleCount(Timeline.INDEFINITE);
    }

    // ===== PUBLIC METHODS =====

    /**
     * Starts the countdown timer.
     * If already running, this does nothing.
     */
    public void start() {
        if (running) {
            return; // Already running
        }

        running = true;
        paused = false;
        timeline.play();
    }

    /**
     * Pauses the timer.
     * Timer can be resumed later from the same point.
     */
    public void pause() {
        if (!running || paused) {
            return; // Not running or already paused
        }

        paused = true;
        timeline.pause();
    }

    /**
     * Resumes the timer after being paused.
     */
    public void resume() {
        if (!running || !paused) {
            return; // Not paused
        }

        paused = false;
        timeline.play();
    }

    /**
     * Stops the timer completely and resets to initial duration.
     * Cannot be resumed - would need to call start() again.
     */
    public void stop() {
        running = false;
        paused = false;
        timeline.stop();

        // Reset to initial state
        remainingSeconds = totalDuration;
        elapsedSeconds = 0;
    }

    /**
     * Cancels the timer without resetting (for cleanup).
     * After calling this, the timer object should not be reused.
     */
    public void cancel() {
        running = false;
        paused = false;
        timeline.stop();
    }

    // ===== PRIVATE METHODS =====

    /**
     * Called every second by the Timeline.
     * This is the heart of the timer - it decrements time and
     * notifies the callback.
     */
    private void tick() {
        // Decrease remaining time
        remainingSeconds--;
        elapsedSeconds++;

        // Notify callback about the tick
        if (callback != null) {
            callback.onTick(remainingSeconds);
        }

        // Check if timer is complete
        if (remainingSeconds <= 0) {
            running = false;
            timeline.stop();

            // Notify callback about completion
            if (callback != null) {
                callback.onComplete();
            }
        }
    }

    // ===== GETTERS =====

    /**
     * Get remaining time in seconds
     *
     * @return Seconds remaining
     */
    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    /**
     * Get elapsed time in seconds
     *
     * @return Seconds elapsed since start
     */
    public int getElapsedSeconds() {
        return elapsedSeconds;
    }

    /**
     * Get total duration in seconds
     *
     * @return Total timer duration
     */
    public int getTotalDuration() {
        return totalDuration;
    }

    /**
     * Check if timer is currently running
     *
     * @return true if running (not paused or stopped)
     */
    public boolean isRunning() {
        return running && !paused;
    }

    /**
     * Check if timer is paused
     *
     * @return true if paused
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Get progress as a decimal between 0.0 (start) and 1.0 (complete)
     * Useful for progress bars and circular timers.
     *
     * @return Progress value (0.0 to 1.0)
     */
    public double getProgress() {
        return (double) elapsedSeconds / totalDuration;
    }

    /**
     * Get remaining progress as a decimal between 1.0 (start) and 0.0 (complete)
     * Useful for countdown displays.
     *
     * @return Remaining progress (1.0 to 0.0)
     */
    public double getRemainingProgress() {
        return (double) remainingSeconds / totalDuration;
    }

    // ===== UTILITY METHODS =====

    /**
     * Formats seconds into HH:MM:SS string
     *
     * @param seconds Time in seconds
     * @return Formatted time string
     */
    public static String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;

        return String.format("%d:%02d:%02d", hours, minutes, secs);
    }

    /**
     * Formats this timer's remaining time
     *
     * @return Formatted time string (HH:MM:SS)
     */
    public String getFormattedTime() {
        return formatTime(remainingSeconds);
    }

    @Override
    public String toString() {
        return String.format("Timer{remaining=%s, elapsed=%s, running=%b, paused=%b}",
                formatTime(remainingSeconds),
                formatTime(elapsedSeconds),
                running,
                paused);
    }
}
