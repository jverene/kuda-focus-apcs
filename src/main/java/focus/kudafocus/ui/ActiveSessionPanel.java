package focus.kudafocus.ui;

import focus.kudafocus.core.FocusSession;
import focus.kudafocus.core.Timer;
import focus.kudafocus.monitoring.SessionMonitor;
import focus.kudafocus.ui.components.CircularProgressRing;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Displays a running focus session with a countdown timer and progress indicator.
 *
 * This panel extends {@link BasePanel} and provides a visual representation of an active
 * focus session, including remaining time, blocked applications, and session controls.
 *
 * It utilizes the {@link SessionMonitor} service to detect violations and provides
 * real-time feedback to the user through a circular progress ring and status labels.
 */
public class ActiveSessionPanel extends BasePanel {

    // ===== CALLBACK INTERFACE =====

    /**
     * Defines the contract for receiving events from an active focus session.
     */
    public interface ActiveSessionCallback {
        /**
         * Invoked when a session completes naturally after the timer reaches zero.
         *
         * @param session The focus session that has completed.
         */
        void onSessionComplete(FocusSession session);

        /**
         * Invoked when the user manually stops a session before completion.
         *
         * @param session The focus session that was stopped.
         * @param actualDuration The actual duration of the session in seconds.
         */
        void onSessionStopped(FocusSession session, int actualDuration);

        /**
         * Invoked when a blocked application is detected during an active session.
         *
         * @param appName The name of the blocked application detected.
         */
        void onViolationDetected(String appName);
    }

    // ===== COMPONENTS =====

    /**
     * The label displaying session information at the top of the panel.
     */
    private Label sessionInfoLabel;

    /**
     * The label displaying information about blocked applications and websites.
     */
    private Label blockedAppsLabel;

    /**
     * The circular progress ring representing session completion progress.
     */
    private CircularProgressRing progressRing;

    /**
     * The label displaying the remaining time in H:MM:SS format.
     */
    private Label timeLabel;

    /**
     * The button used to pause or resume the active session.
     */
    private Button pauseButton;

    /**
     * The button used to manually stop the active session.
     */
    private Button stopButton;

    /**
     * The label indicating the current status, such as when the session is paused.
     */
    private Label statusLabel;

    // ===== STATE =====

    /**
     * The focus session currently being tracked and displayed.
     */
    private FocusSession focusSession;

    /**
     * The countdown timer managing the session duration.
     */
    private Timer timer;

    /**
     * The service responsible for monitoring active processes and detecting violations.
     */
    private SessionMonitor sessionMonitor;

    /**
     * The callback object for notifying session-related events.
     */
    private ActiveSessionCallback callback;

    /**
     * Indicates whether the current session is in a paused state.
     */
    private boolean paused = false;

    // ===== CONSTRUCTORS =====

    /**
     * Constructs an active session panel for the specified session using the default dark theme.
     *
     * @param focusSession The focus session to track and display.
     */
    public ActiveSessionPanel(FocusSession focusSession) {
        this(focusSession, new DarkTheme());
    }

    /**
     * Constructs an active session panel for the specified session using a custom theme.
     *
     * @param focusSession The focus session to track and display.
     * @param theme The theme providing the color palette for the panel.
     */
    public ActiveSessionPanel(FocusSession focusSession, Theme theme) {
        super(theme);

        this.focusSession = focusSession;

        createComponents();
        layoutComponents();
        setupEventHandlers();
        initializeTimer();
    }

    // ===== INITIALIZATION METHODS =====

    /**
     * Creates and configures all UI components for the panel.
     */
    private void createComponents() {
        // Session info label
        sessionInfoLabel = new Label("Focus Session");
        sessionInfoLabel.setFont(UIConstants.getHeadingFont());
        sessionInfoLabel.setTextFill(getTextPrimaryColor());

        // Blocked apps and websites info
        List<String> blockedApps = focusSession.getBlockedApps();
        List<String> blockedWebsites = focusSession.getBlockedWebsites();
        
        String blockedInfo = buildBlockedInfo(blockedApps, blockedWebsites);
        blockedAppsLabel = new Label(blockedInfo);
        blockedAppsLabel.setFont(UIConstants.getSmallFont());
        blockedAppsLabel.setTextFill(getTextSecondaryColor());
        blockedAppsLabel.setWrapText(true);
        blockedAppsLabel.setMaxWidth(UIConstants.TIMER_RING_DIAMETER);
        blockedAppsLabel.setTextAlignment(TextAlignment.CENTER);

        // Progress ring (display mode, not selectable)
        progressRing = new CircularProgressRing(UIConstants.TIMER_RING_DIAMETER);
        progressRing.setThemeColors(
                getTheme().getBackgroundSecondary(),
                getAccentColor(),
                getTextPrimaryColor()
        );
        progressRing.setSelectionMode(false); // Display mode
        progressRing.setProgress(1.0); // Start full

        // Time display
        int remainingSeconds = focusSession.getPlannedDuration();
        timeLabel = new Label(Timer.formatTime(remainingSeconds));
        timeLabel.setFont(UIConstants.getDisplayFont());
        timeLabel.setTextFill(getTextPrimaryColor());
        timeLabel.setTextAlignment(TextAlignment.CENTER);

        // Status label (hidden by default, shown when paused)
        statusLabel = new Label("Paused");
        statusLabel.setFont(UIConstants.getHeadingFont());
        statusLabel.setTextFill(getWarningColor());
        statusLabel.setVisible(false);

        // PAUSE button
        pauseButton = new Button("PAUSE");
        pauseButton.setFont(UIConstants.getBodyFont());
        pauseButton.setPrefHeight(UIConstants.BUTTON_HEIGHT);
        pauseButton.setMinWidth(UIConstants.BUTTON_MIN_WIDTH * 1.2);
        pauseButton.setStyle(
                "-fx-background-color: " + toRGBCode(getWarningColor()) + ";" +
                        "-fx-text-fill: black;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-weight: bold;"
        );        UIConstants.setupButtonAnimation(pauseButton);
        // STOP button
        stopButton = new Button("STOP");
        stopButton.setFont(UIConstants.getBodyFont());
        stopButton.setPrefHeight(UIConstants.BUTTON_HEIGHT);
        stopButton.setMinWidth(UIConstants.BUTTON_MIN_WIDTH * 1.2);
        stopButton.setStyle(
                "-fx-background-color: " + toRGBCode(getErrorColor()) + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-weight: bold;"
        );
        UIConstants.setupButtonAnimation(stopButton);
    }

    /**
     * Arranges the UI components within the panel layout.
     */
    private void layoutComponents() {
        this.getChildren().clear();

        // Top section (session info)
        VBox topSection = new VBox(UIConstants.SPACING_SM);
        topSection.setAlignment(Pos.CENTER);
        topSection.getChildren().addAll(sessionInfoLabel, blockedAppsLabel);

        // Center content (ring with time inside)
        VBox centerContent = new VBox(UIConstants.SPACING_SM);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.getChildren().addAll(timeLabel, statusLabel);

        StackPane ringStack = new StackPane();
        ringStack.getChildren().addAll(progressRing, centerContent);
        ringStack.setAlignment(Pos.CENTER);

        // Bottom section (buttons)
        HBox buttonBox = new HBox(UIConstants.SPACING_LG);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(pauseButton, stopButton);

        // Add all sections to main panel
        VBox.setMargin(topSection, new Insets(UIConstants.SPACING_LG, 0, 0, 0));
        VBox.setMargin(ringStack, new Insets(UIConstants.SPACING_XL, 0, 0, 0));
        VBox.setMargin(buttonBox, new Insets(UIConstants.SPACING_XL, 0, UIConstants.SPACING_LG, 0));

        this.getChildren().addAll(topSection, ringStack, buttonBox);
        this.setAlignment(Pos.CENTER);
    }

    /**
     * Configures event handlers for interactive UI components.
     */
    private void setupEventHandlers() {
        // PAUSE button
        pauseButton.setOnAction(event -> handlePauseResume());

        // STOP button
        stopButton.setOnAction(event -> handleStop());
    }

    /**
     * Initializes and starts the session timer and monitor.
     */
    private void initializeTimer() {
        int durationSeconds = focusSession.getPlannedDuration();

        // Create timer with callback (only for UI updates, not violation detection)
        timer = new Timer(durationSeconds, new Timer.TimerCallback() {
            @Override
            public void onTick(int remainingSeconds) {
                // Update UI only
                updateTimerDisplay(remainingSeconds);
            }

            @Override
            public void onComplete() {
                // Timer finished naturally
                handleTimerComplete();
            }
        });

        // Create and start the session monitor for violation detection
        sessionMonitor = new SessionMonitor(focusSession, new SessionMonitor.SessionMonitorCallback() {
            @Override
            public void onViolationDetected(String appName) {
                if (callback != null) {
                    callback.onViolationDetected(appName);
                }
            }

            @Override
            public void onViolationEnded() {
                // Violation ended - overlay will disappear naturally
            }
        });

        // Start the timer and monitor
        timer.start();
        sessionMonitor.start();
    }

    // ===== EVENT HANDLERS =====

    /**
     * Handles the interaction for pausing and resuming the session.
     */
    private void handlePauseResume() {
        if (paused) {
            // Resume
            timer.resume();
            paused = false;
            pauseButton.setText("PAUSE");
            statusLabel.setVisible(false);
        } else {
            // Pause
            timer.pause();
            paused = true;
            pauseButton.setText("RESUME");
            statusLabel.setVisible(true);
        }
    }

    /**
     * Handles the interaction for stopping the session manually.
     */
    private void handleStop() {
        // Stop timer
        timer.stop();

        // Calculate actual duration
        int actualDuration = timer.getElapsedSeconds();

        // Notify callback
        if (callback != null) {
            callback.onSessionStopped(focusSession, actualDuration);
        }
    }

    /**
     * Finalizes the session state when the timer completes naturally.
     */
    private void handleTimerComplete() {
        // Notify callback
        if (callback != null) {
            callback.onSessionComplete(focusSession);
        }
    }

    /**
     * Updates the time display label and progress ring based on remaining seconds.
     *
     * @param remainingSeconds The number of seconds remaining in the session.
     */
    private void updateTimerDisplay(int remainingSeconds) {
        // Update time label
        timeLabel.setText(Timer.formatTime(remainingSeconds));

        // Update progress ring (depleting)
        double progress = timer.getRemainingProgress();
        progressRing.setProgress(progress);

        // Change ring color based on remaining time
        if (progress < 0.1) {
            // Less than 10% - red
            progressRing.setRingColor(getErrorColor());
        } else if (progress < 0.25) {
            // Less than 25% - yellow
            progressRing.setRingColor(getWarningColor());
        } else {
            // Normal - accent color
            progressRing.setRingColor(getAccentColor());
        }
    }

    // ===== HELPER METHODS =====

    /**
     * Constructs a descriptive string summarizing the blocked applications and websites.
     *
     * @param blockedApps The list of blocked application names.
     * @param blockedWebsites The list of blocked website domains.
     * @return A formatted string describing the blocked items.
     */
    private String buildBlockedInfo(List<String> blockedApps, List<String> blockedWebsites) {
        List<String> parts = new ArrayList<>();
        
        if (!blockedApps.isEmpty()) {
            parts.add("Apps: " + String.join(", ", blockedApps));
        }
        if (!blockedWebsites.isEmpty()) {
            parts.add("Sites: " + String.join(", ", blockedWebsites));
        }
        
        if (parts.isEmpty()) {
            return "No apps or sites blocked";
        }
        
        return "Blocking: " + String.join(" | ", parts);
    }

    // ===== PUBLIC METHODS =====

    /**
     * Registers a callback for receiving session lifecycle events.
     *
     * @param callback The callback to be notified of session events.
     */
    public void setCallback(ActiveSessionCallback callback) {
        this.callback = callback;
    }

    /**
     * Retrieves the focus session currently being tracked.
     *
     * @return The active focus session.
     */
    public FocusSession getFocusSession() {
        return focusSession;
    }

    /**
     * Retrieves the countdown timer managing the session duration.
     *
     * @return The session timer.
     */
    public Timer getTimer() {
        return timer;
    }

    /**
     * Performs necessary cleanup operations when the panel is closed.
     */
    public void cleanup() {
        if (timer != null) {
            timer.cancel();
        }
        if (sessionMonitor != null) {
            sessionMonitor.stop();
        }
    }
}
