package focus.kudafocus;

import focus.kudafocus.core.FocusSession;
import focus.kudafocus.core.StreakTracker;
import focus.kudafocus.data.models.UserPreferences;
import focus.kudafocus.data.storage.PreferencesStore;
import focus.kudafocus.ui.ActiveSessionPanel;
import focus.kudafocus.ui.AppSelectionModal;
import focus.kudafocus.ui.CircularTimerPanel;
import focus.kudafocus.ui.DistractionOverlay;
import focus.kudafocus.ui.SessionSummaryPanel;
import focus.kudafocus.ui.DarkTheme;
import focus.kudafocus.ui.LightTheme;
import focus.kudafocus.ui.Theme;
import focus.kudafocus.ui.UIConstants;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * The main entry point for the KUDA FOCUS application.
 *
 * This class manages the application lifecycle and handles transitions between different
 * screens, including the home screen, active session view, results summary, and
 * distraction overlays. It serves as the central controller for the application's
 * primary user interface and session flow.
 */
public class Main extends Application {

    /**
     * The primary application stage.
     */
    private Stage primaryStage;

    /**
     * The current scene being displayed.
     */
    private Scene scene;

    /**
     * The home screen panel containing the circular timer.
     */
    private CircularTimerPanel timerPanel;

    /**
     * The current theme applied to the application.
     */
    private Theme currentTheme = new DarkTheme();

    /**
     * Toggles between light and dark mode and refreshes the home screen.
     *
     * @param enable true to enable light mode, false to revert to dark mode
     */
    private void toggleLightMode(boolean enable) {
        currentTheme = enable ? new LightTheme() : new DarkTheme();
        showHomeScreen();
    }

    /**
     * The active session panel shown during a running timer.
     */
    private ActiveSessionPanel activeSessionPanel;

    /**
     * The session summary panel shown after a session completes or stops.
     */
    private SessionSummaryPanel summaryPanel;

    /**
     * The distraction overlay displayed when blocked applications are detected.
     */
    private DistractionOverlay distractionOverlay;

    /**
     * The current active focus session.
     */
    private FocusSession currentSession;

    /**
     * The store for persisting and loading user preferences.
     */
    private PreferencesStore preferencesStore;

    /**
     * The loaded preferences for the current user.
     */
    private UserPreferences userPreferences;

    /**
     * The tracker for maintaining user focus streaks.
     */
    private StreakTracker streakTracker;

    /**
     * Launches the application.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initializes the application and sets up the primary stage.
     *
     * @param primaryStage The main application window.
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.preferencesStore = new PreferencesStore();
        this.userPreferences = preferencesStore.load();
        this.streakTracker = new StreakTracker();

        primaryStage.setTitle("KUDA FOCUS - Minimalist Focus Timer");
        showHomeScreen();
        primaryStage.show();
    }

    // ===== SCREEN NAVIGATION METHODS =====

    /**
     * Displays the home screen panel.
     */
    private void showHomeScreen() {
        if (activeSessionPanel != null) {
            activeSessionPanel.cleanup();
            activeSessionPanel = null;
        }
        if (distractionOverlay != null) {
            distractionOverlay.close();
            distractionOverlay = null;
        }

        timerPanel = new CircularTimerPanel(currentTheme);

        timerPanel.setCallback(new CircularTimerPanel.CircularTimerCallback() {
            @Override
            public void onStartSession(int durationMinutes, List<String> blockedApps, List<String> blockedWebsites) {
                handleStartSession(durationMinutes, blockedApps, blockedWebsites);
            }

            @Override
            public void onSelectApps() {
                handleSelectApps();
            }

            @Override
            public void onToggleLightMode(boolean enable) {
                toggleLightMode(enable);
            }
        });

        timerPanel.setStreak(streakTracker.getCurrentStreak());
        timerPanel.setSelectedApps(userPreferences.getLastSelectedApps());
        timerPanel.setSelectedWebsites(new ArrayList<>());

        if (scene == null) {
            scene = new Scene(timerPanel, UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
            primaryStage.setScene(scene);
        } else {
            scene.setRoot(timerPanel);
        }
    }

    /**
     * Displays the active session screen.
     *
     * @param session The focus session to display.
     */
    private void showActiveSession(FocusSession session) {
        activeSessionPanel = new ActiveSessionPanel(session, currentTheme);

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

        scene.setRoot(activeSessionPanel);
    }

    /**
     * Displays the session summary screen.
     *
     * @param session The completed or stopped focus session.
     */
    private void showSessionSummary(FocusSession session) {
        if (session.qualifiesForStreak()) {
            streakTracker.recordSession(true);
        }

        if (activeSessionPanel != null) {
            activeSessionPanel.cleanup();
            activeSessionPanel = null;
        }

        summaryPanel = new SessionSummaryPanel(session, streakTracker.getCurrentStreak(), currentTheme);

        summaryPanel.setCallback(new SessionSummaryPanel.SummaryCallback() {
            @Override
            public void onContinue() {
                handleContinueFromSummary();
            }
        });

        scene.setRoot(summaryPanel);
    }

    /**
     * Displays the distraction overlay when a violation occurs.
     *
     * @param appName The name of the application or website that triggered the violation.
     */
    private void showDistractionOverlay(String appName) {
        if (currentSession == null) {
            return;
        }

        if (distractionOverlay != null) {
            distractionOverlay.close();
        }

        distractionOverlay = new DistractionOverlay(currentSession, appName, currentTheme);

        distractionOverlay.setCallback(new DistractionOverlay.OverlayCallback() {
            @Override
            public void onDismissed() {
                // Overlay dismissed by user
            }
        });

        distractionOverlay.show();
    }

    // ===== EVENT HANDLERS =====

    /**
     * Handles the start of a new focus session.
     *
     * @param durationMinutes The planned duration of the session in minutes.
     * @param blockedApps The list of applications to block during the session.
     * @param blockedWebsites The list of websites to block during the session.
     */
    private void handleStartSession(int durationMinutes, List<String> blockedApps, List<String> blockedWebsites) {
        int durationSeconds = durationMinutes * 60;
        currentSession = new FocusSession(durationSeconds, blockedApps, blockedWebsites);
        showActiveSession(currentSession);
    }

    /**
     * Opens the selection modal for blocked applications and websites.
     */
    private void handleSelectApps() {
        AppSelectionModal modal = new AppSelectionModal(
                primaryStage,
                timerPanel.getSelectedApps(),
                timerPanel.getSelectedWebsites(),
                currentTheme
        );
        modal.showAndWait();

        if (modal.isConfirmed()) {
            List<String> selectedApps = modal.getSelectedApps();
            List<String> selectedWebsites = modal.getSelectedWebsites();
            
            timerPanel.setSelectedApps(selectedApps);
            timerPanel.setSelectedWebsites(selectedWebsites);
            
            userPreferences.setLastSelectedApps(selectedApps);
            userPreferences.setLastSelectedWebsites(selectedWebsites);
            preferencesStore.save(userPreferences);
        }
    }

    /**
     * Handles the completion of a focus session when the timer reaches zero.
     *
     * @param session The completed focus session.
     */
    private void handleSessionComplete(FocusSession session) {
        session.complete(session.getPlannedDuration());
        showSessionSummary(session);
    }

    /**
     * Handles the premature termination of a focus session by the user.
     *
     * @param session The abandoned focus session.
     * @param actualDuration The actual duration spent in the session in seconds.
     */
    private void handleSessionStopped(FocusSession session, int actualDuration) {
        session.abandon(actualDuration);
        showSessionSummary(session);
    }

    /**
     * Handles the detection of a blocked application during an active session.
     *
     * @param appName The name of the detected application.
     */
    private void handleViolationDetected(String appName) {
        showDistractionOverlay(appName);
    }

    /**
     * Returns the user to the home screen from the session summary.
     */
    private void handleContinueFromSummary() {
        currentSession = null;
        showHomeScreen();
    }
}
