package focus.kudafocus;

import focus.kudafocus.core.FocusSession;
import focus.kudafocus.ui.ActiveSessionPanel;
import focus.kudafocus.ui.CircularTimerPanel;
import focus.kudafocus.ui.DistractionOverlay;
import focus.kudafocus.ui.SessionSummaryPanel;
import focus.kudafocus.ui.UIConstants;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

/**
 * Main entry point for KUDA FOCUS application.
 *
 * Manages the application lifecycle and scene transitions:
 * - CircularTimerPanel (home screen)
 * - ActiveSessionPanel (running session)
 * - SessionSummaryPanel (results)
 * - DistractionOverlay (appears when blocked apps detected)
 *
 * Demonstrates OOP principles:
 * - Encapsulation: FocusSession class (private fields, public methods)
 * - Abstraction: AppMonitor hierarchy (platform-specific implementations hidden)
 * - Inheritance: BasePanel hierarchy (CircularTimerPanel, ActiveSessionPanel, etc. extend BasePanel)
 *
 * This demonstrates a complete session flow with all Phase 2 components!
 */
public class Main extends Application {

    /**
     * Primary application stage (window)
     */
    private Stage primaryStage;

    /**
     * Current scene
     */
    private Scene scene;

    /**
     * Home screen panel
     */
    private CircularTimerPanel timerPanel;

    /**
     * Active session panel (running timer)
     */
    private ActiveSessionPanel activeSessionPanel;

    /**
     * Session summary panel (results)
     */
    private SessionSummaryPanel summaryPanel;

    /**
     * Distraction overlay (shown when blocked apps detected)
     */
    private DistractionOverlay distractionOverlay;

    /**
     * Current active session (if any)
     */
    private FocusSession currentSession;

    /**
     * Application entry point
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * JavaFX start method - sets up the UI
     *
     * @param primaryStage Main application window
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Set up window
        primaryStage.setTitle("KUDA FOCUS - Minimalist Focus Timer");

        // Create home screen
        showHomeScreen();

        // Show window
        primaryStage.show();

        System.out.println("\n========================================");
        System.out.println("KUDA FOCUS - Minimalist Focus Timer");
        System.out.println("========================================");
        System.out.println("✓ Phase 2: Timer UI & Session Flow COMPLETE");
        System.out.println("\nImplemented Components:");
        System.out.println("  • Timer.java - Countdown logic with JavaFX Timeline");
        System.out.println("  • CircularProgressRing.java - Drag-to-select circular control");
        System.out.println("  • CircularTimerPanel.java - Home screen");
        System.out.println("  • ActiveSessionPanel.java - Running session view");
        System.out.println("  • DistractionOverlay.java - Full-screen overlay");
        System.out.println("  • SessionSummaryPanel.java - Results screen");
        System.out.println("\nOOP Demonstrations:");
        System.out.println("  • Encapsulation: FocusSession (private fields, hidden scoring logic)");
        System.out.println("  • Abstraction: AppMonitor (platform details hidden)");
        System.out.println("  • Inheritance: All panels extend BasePanel");
        System.out.println("\nHow to Use:");
        System.out.println("  1. Drag around the circle to select time (0-180 min)");
        System.out.println("  2. Click START to begin focus session");
        System.out.println("  3. Timer will count down with live progress ring");
        System.out.println("  4. PAUSE/RESUME or STOP session anytime");
        System.out.println("  5. View results with focus score and statistics");
        System.out.println("========================================\n");
    }

    // ===== SCREEN NAVIGATION METHODS =====

    /**
     * Shows the home screen (circular timer panel)
     */
    private void showHomeScreen() {
        // Clean up previous panels
        if (activeSessionPanel != null) {
            activeSessionPanel.cleanup();
            activeSessionPanel = null;
        }
        if (distractionOverlay != null) {
            distractionOverlay.close();
            distractionOverlay = null;
        }

        // Create new timer panel
        timerPanel = new CircularTimerPanel();

        // Set up callback for panel events
        timerPanel.setCallback(new CircularTimerPanel.CircularTimerCallback() {
            @Override
            public void onStartSession(int durationMinutes, List<String> blockedApps) {
                handleStartSession(durationMinutes, blockedApps);
            }

            @Override
            public void onSelectApps() {
                handleSelectApps();
            }
        });

        // Set initial streak (TODO: load from data store in Phase 4)
        timerPanel.setStreak(0);

        // Create scene if not exists, or update root
        if (scene == null) {
            scene = new Scene(timerPanel, UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
            primaryStage.setScene(scene);
        } else {
            scene.setRoot(timerPanel);
        }
    }

    /**
     * Shows the active session screen (running timer)
     */
    private void showActiveSession(FocusSession session) {
        System.out.println("\n=== STARTING FOCUS SESSION ===");
        System.out.println("Duration: " + session.getPlannedDurationMinutes() + " minutes");
        System.out.println("Blocked apps: " + (session.getBlockedApps().isEmpty() ? "None" : session.getBlockedApps()));

        // Create active session panel
        activeSessionPanel = new ActiveSessionPanel(session);

        // Set up callback for session events
        activeSessionPanel.setCallback(new ActiveSessionPanel.ActiveSessionCallback() {
            @Override
            public void onSessionComplete(FocusSession completedSession) {
                handleSessionComplete(completedSession);
            }

            @Override
            public void onSessionStopped(FocusSession stoppedSession, int actualDuration) {
                handleSessionStopped(stoppedSession, actualDuration);
            }

            @Override
            public void onViolationDetected(String appName) {
                handleViolationDetected(appName);
            }
        });

        // Update scene
        scene.setRoot(activeSessionPanel);
    }

    /**
     * Shows the session summary screen (results)
     */
    private void showSessionSummary(FocusSession session) {
        System.out.println("\n=== SESSION SUMMARY ===");
        System.out.println("Focus Score: " + session.getFocusScore());
        System.out.println("Duration: " + session.getActualDurationMinutes() + " / " + session.getPlannedDurationMinutes() + " minutes");
        System.out.println("Violations: " + session.getViolationCount());
        System.out.println("Dismissals: " + session.getTotalDismissals());
        System.out.println("Most Distracting: " + session.getMostDistractingApp());
        System.out.println("Qualifies for Streak: " + (session.qualifiesForStreak() ? "YES" : "NO"));

        // Clean up active session panel
        if (activeSessionPanel != null) {
            activeSessionPanel.cleanup();
            activeSessionPanel = null;
        }

        // Create summary panel
        summaryPanel = new SessionSummaryPanel(session);

        // Set up callback
        summaryPanel.setCallback(new SessionSummaryPanel.SummaryCallback() {
            @Override
            public void onContinue() {
                handleContinueFromSummary();
            }
        });

        // Update scene
        scene.setRoot(summaryPanel);
    }

    /**
     * Shows the distraction overlay (when blocked app detected)
     */
    private void showDistractionOverlay(String appName) {
        if (distractionOverlay == null && currentSession != null) {
            System.out.println("⚠️  VIOLATION DETECTED: " + appName);

            // Create overlay
            distractionOverlay = new DistractionOverlay(currentSession, appName);

            // Set up callback
            distractionOverlay.setCallback(new DistractionOverlay.OverlayCallback() {
                @Override
                public void onDismissed() {
                    System.out.println("Overlay dismissed by user");
                }

                @Override
                public void onAppStillRunning() {
                    System.out.println("App still running - overlay will reappear");
                }
            });

            // Show overlay
            distractionOverlay.show();
        }
    }

    // ===== EVENT HANDLERS =====

    /**
     * Handles START button click from home screen
     */
    private void handleStartSession(int durationMinutes, List<String> blockedApps) {
        // Create new session
        int durationSeconds = durationMinutes * 60;
        currentSession = new FocusSession(durationSeconds, blockedApps);

        // Show active session screen
        showActiveSession(currentSession);
    }

    /**
     * Handles app selection request
     */
    private void handleSelectApps() {
        System.out.println("Opening app selection modal...");
        System.out.println("TODO: Implement AppSelectionModal in Phase 3");

        // For now, set some demo apps
        java.util.List<String> demoApps = java.util.Arrays.asList("Discord", "Steam", "Instagram");
        timerPanel.setSelectedApps(demoApps);
    }

    /**
     * Handles session completion (timer reached 0)
     */
    private void handleSessionComplete(FocusSession session) {
        // Mark session as complete with full duration
        session.complete(session.getPlannedDuration());

        // Show summary screen
        showSessionSummary(session);
    }

    /**
     * Handles session stopped early (user clicked STOP)
     */
    private void handleSessionStopped(FocusSession session, int actualDuration) {
        // Mark session as abandoned
        session.abandon(actualDuration);

        // Show summary screen
        showSessionSummary(session);
    }

    /**
     * Handles violation detection (blocked app opened)
     */
    private void handleViolationDetected(String appName) {
        // Show distraction overlay
        showDistractionOverlay(appName);
    }

    /**
     * Handles CONTINUE button from summary screen
     */
    private void handleContinueFromSummary() {
        // Return to home screen
        currentSession = null;
        showHomeScreen();
    }
}

