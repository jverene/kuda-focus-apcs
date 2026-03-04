package focus.kudafocus.monitoring;

import focus.kudafocus.core.FocusSession;
import focus.kudafocus.monitoring.ForegroundAppMonitor;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.List;
import java.util.Locale;

/**
 * Monitors app and website usage during an active focus session.
 *
 * Integrates AppMonitor, ForegroundAppMonitor, and ChromeWebsiteMonitor
 * into a unified polling service that updates the FocusSession with violations.
 *
 * Design:
 * - Service owns the monitors and session reference
 * - Runs on a Timeline to poll monitors at regular intervals
 * - Maintains current violation state and enforces overlay re-trigger cadence
 * - Invokes callback when violations start/end
 *
 * This decouples violation detection from the UI layer (ActiveSessionPanel),
 * allowing the session to be the source of truth for monitoring state.
 */
public class SessionMonitor {

    /**
     * Callback interface for monitoring events
     */
    public interface SessionMonitorCallback {
        /**
         * Called when a violation is detected
         *
         * @param appName Name of the blocked app/website that triggered violation
         */
        void onViolationDetected(String appName);

        /**
         * Called when a violation ends
         */
        void onViolationEnded();
    }

    // ===== MONITORING INTERVALS =====

    /**
     * How often to check running processes (for blocked apps)
     */
    private static final int APP_CHECK_INTERVAL_SECONDS = 1;

    /**
     * How often to check Chrome active tab (for blocked websites)
     */
    private static final int WEBSITE_CHECK_INTERVAL_SECONDS = 5;

    /**
     * Minimum gap before re-triggering overlay for same violation
     */
    private static final int OVERLAY_RETRIGGER_INTERVAL_SECONDS = 2;

    // ===== MONITORS =====

    /**
     * Process monitor (platform-specific)
     */
    private final AppMonitor appMonitor;

    /**
     * Foreground application monitor (for checking which app is frontmost)
     */
    private final ForegroundAppMonitor foregroundMonitor;

    /**
     * Chrome website monitor
     */
    private final ChromeWebsiteMonitor websiteMonitor;

    /**
     * The session being monitored
     */
    private final FocusSession session;

    /**
     * Callback for violations
     */
    private final SessionMonitorCallback callback;

    // ===== STATE =====

    /**
     * Timeline that drives monitoring
     */
    private Timeline monitoringTimeline;

    /**
     * Elapsed seconds since session started (synced with check)
     */
    private int elapsedSeconds = 0;

    /**
     * Last time overlay was triggered for current violation type
     */
    private int lastAppOverlayTriggerSecond = -OVERLAY_RETRIGGER_INTERVAL_SECONDS;
    private int lastWebsiteOverlayTriggerSecond = -OVERLAY_RETRIGGER_INTERVAL_SECONDS;

    /**
     * Whether this monitor is currently running
     */
    private boolean running = false;

    // ===== CONSTRUCTOR =====

    /**
     * Creates a session monitor for the given session
     *
     * @param session The focus session to monitor
     * @param callback Callback for violation events
     */
    public SessionMonitor(FocusSession session, SessionMonitorCallback callback) {
        this(session, callback,
             AppMonitor.createForCurrentOS(),
             new ForegroundAppMonitor(),
             new ChromeWebsiteMonitor());
    }

    /*
     * Package-private constructor for testing with custom monitors.
     */
    SessionMonitor(FocusSession session,
                   SessionMonitorCallback callback,
                   AppMonitor appMonitor,
                   ForegroundAppMonitor foregroundMonitor,
                   ChromeWebsiteMonitor websiteMonitor) {
        this.session = session;
        this.callback = callback;
        this.appMonitor = appMonitor;
        this.foregroundMonitor = foregroundMonitor;
        this.websiteMonitor = websiteMonitor;
    }

    // ===== LIFECYCLE METHODS =====

    /**
     * Starts the monitoring timeline
     */
    public void start() {
        if (running) {
            return;
        }

        running = true;
        elapsedSeconds = 0;

        // Create a timeline that ticks every second
        monitoringTimeline = new Timeline(
            new KeyFrame(Duration.seconds(1), event -> onTimerTick())
        );
        monitoringTimeline.setCycleCount(Timeline.INDEFINITE);
        monitoringTimeline.play();

        System.out.println("[SessionMonitor] Started monitoring session");
    }

    /**
     * Stops the monitoring timeline
     */
    public void stop() {
        if (!running || monitoringTimeline == null) {
            return;
        }

        running = false;
        monitoringTimeline.stop();
        monitoringTimeline = null;

        // End any active violation
        if (session.hasActiveViolation()) {
            session.endCurrentViolation();
            if (callback != null) {
                callback.onViolationEnded();
            }
        }

        System.out.println("[SessionMonitor] Stopped monitoring session");
    }

    // ===== TIMER TICK HANDLER =====

    /**
     * Called every second while monitoring is running
     */
    private void onTimerTick() {
        elapsedSeconds++;

        // Track app violations
        checkAppViolations();

        // Track website violations (less frequently)
        if (elapsedSeconds % WEBSITE_CHECK_INTERVAL_SECONDS == 0) {
            checkWebsiteViolations();
        }

        // If no violation detected and none active, we're good
        // If something was active, it will be ended in the check methods
    }

    /*
     * Package-private helper for unit tests to simulate a single tick
     */
    void tickOnce() {
        onTimerTick();
    }

    // ===== VIOLATION CHECKING =====

    /**
     * Checks if any blocked apps are currently running
     */
    private void checkAppViolations() {
        List<String> blockedApps = session.getBlockedApps();
        if (blockedApps.isEmpty()) {
            clearAppViolationIfActive();
            return;
        }

        // Determine the current frontmost application via foreground monitor
        String frontApp = foregroundMonitor.getFrontmostApplication();
        System.out.println("[SessionMonitor] frontmost app = " + frontApp);

        String matchedApp = null;
        if (frontApp != null && !frontApp.isBlank()) {
            String normalizedFront = frontApp.toLowerCase(Locale.ROOT);
            for (String app : blockedApps) {
                if (normalizedFront.contains(app.toLowerCase(Locale.ROOT))) {
                    matchedApp = app;
                    break;
                }
            }
        }

        if (matchedApp != null) {
            // Found violation in foreground app
            startViolationIfChanged(matchedApp, true);
            session.addViolationDuration(APP_CHECK_INTERVAL_SECONDS);

            // Trigger overlay if cadence allows
            if (elapsedSeconds - lastAppOverlayTriggerSecond >= OVERLAY_RETRIGGER_INTERVAL_SECONDS) {
                lastAppOverlayTriggerSecond = elapsedSeconds;
                if (callback != null) {
                    callback.onViolationDetected(matchedApp);
                }
            }
        } else {
            clearAppViolationIfActive();
        }
    }

    /**
     * Checks if blocked websites are currently active in Chrome
     */
    private void checkWebsiteViolations() {
        List<String> blockedWebsites = session.getBlockedWebsites();
        if (blockedWebsites.isEmpty()) {
            clearWebsiteViolationIfActive();
            return;
        }

        // Use ChromeWebsiteMonitor to check active tab
        String matchedDomain = websiteMonitor.detectDistractingDomain(blockedWebsites);

        System.out.println("[SessionMonitor] Checking websites: " + blockedWebsites + " -> matched: " + matchedDomain);

        if (matchedDomain != null) {
            // Found violation
            String violationName = "Website: " + matchedDomain;
            startViolationIfChanged(violationName, false);
            session.addViolationDuration(WEBSITE_CHECK_INTERVAL_SECONDS);

            // Trigger overlay if cadence allows
            if (elapsedSeconds - lastWebsiteOverlayTriggerSecond >= OVERLAY_RETRIGGER_INTERVAL_SECONDS) {
                lastWebsiteOverlayTriggerSecond = elapsedSeconds;
                if (callback != null) {
                    callback.onViolationDetected(violationName);
                }
            }
        } else {
            clearWebsiteViolationIfActive();
        }
    }

    // ===== VIOLATION STATE MANAGEMENT =====

    /**
     * Starts a violation if it's different from the current one
     */
    private void startViolationIfChanged(String appName, boolean isApp) {
        if (!session.hasActiveViolation()) {
            session.startViolation(appName);
            resetOverlayCadence(isApp);
            return;
        }

        String currentName = session.getCurrentViolation().getAppName();
        if (!appName.equals(currentName)) {
            session.startViolation(appName);
            resetOverlayCadence(isApp);
        }
    }

    /**
     * Resets the overlay trigger cadence for the given violation type
     */
    private void resetOverlayCadence(boolean isApp) {
        if (isApp) {
            lastAppOverlayTriggerSecond = elapsedSeconds - OVERLAY_RETRIGGER_INTERVAL_SECONDS;
        } else {
            lastWebsiteOverlayTriggerSecond = elapsedSeconds - OVERLAY_RETRIGGER_INTERVAL_SECONDS;
        }
    }

    /**
     * Clears app violation if currently active
     */
    private void clearAppViolationIfActive() {
        if (!session.hasActiveViolation()) {
            return;
        }

        String currentName = session.getCurrentViolation().getAppName();
        if (currentName != null && !currentName.startsWith("Website: ")) {
            session.endCurrentViolation();
            if (callback != null) {
                callback.onViolationEnded();
            }
            // Reset cadence
            lastAppOverlayTriggerSecond = elapsedSeconds - OVERLAY_RETRIGGER_INTERVAL_SECONDS;
        }
    }

    /**
     * Clears website violation if currently active
     */
    private void clearWebsiteViolationIfActive() {
        if (!session.hasActiveViolation()) {
            return;
        }

        String currentName = session.getCurrentViolation().getAppName();
        if (currentName != null && currentName.startsWith("Website: ")) {
            session.endCurrentViolation();
            if (callback != null) {
                callback.onViolationEnded();
            }
            // Reset cadence
            lastWebsiteOverlayTriggerSecond = elapsedSeconds - OVERLAY_RETRIGGER_INTERVAL_SECONDS;
        }
    }

    // ===== GETTERS =====

    /**
     * Gets the elapsed time in seconds
     *
     * @return Elapsed seconds
     */
    public int getElapsedSeconds() {
        return elapsedSeconds;
    }

    /**
     * Checks if monitor is currently running
     *
     * @return true if monitoring
     */
    public boolean isRunning() {
        return running;
    }
}
