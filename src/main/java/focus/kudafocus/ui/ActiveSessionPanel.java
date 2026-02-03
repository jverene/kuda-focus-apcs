package focus.kudafocus.ui;

import focus.kudafocus.core.FocusSession;
import focus.kudafocus.core.Timer;
import focus.kudafocus.monitoring.AppMonitor;
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

import java.util.List;

/**
 * Active session panel - displays running focus session with countdown.
 *
 * This panel demonstrates INHERITANCE - extends BasePanel for common styling.
 *
 * Components:
 * - Session info label (top) - shows "Focus Session"
 * - Circular progress ring (center) - depletes as time passes
 * - Time remaining display (center of ring)
 * - PAUSE button (bottom)
 * - STOP button (bottom)
 *
 * Functionality:
 * - Countdown timer that updates every second
 * - Progress ring that depletes clockwise
 * - Process monitoring to detect blocked apps (TODO: Phase 3)
 * - Pause/resume capability
 * - Stop with confirmation
 *
 * Learning Points:
 * - Timer callback pattern (responding to timer events)
 * - JavaFX Platform.runLater (thread-safe UI updates)
 * - Managing multiple event handlers
 */
public class ActiveSessionPanel extends BasePanel {

    // ===== CALLBACK INTERFACE =====

    /**
     * Callback interface for session events
     */
    public interface ActiveSessionCallback {
        /**
         * Called when session completes naturally (timer reaches 0)
         *
         * @param session The completed session
         */
        void onSessionComplete(FocusSession session);

        /**
         * Called when user stops session early
         *
         * @param session The session that was stopped
         * @param actualDuration Actual duration in seconds
         */
        void onSessionStopped(FocusSession session, int actualDuration);

        /**
         * Called when a blocked app is detected (for showing overlay)
         *
         * @param appName Name of the blocked app
         */
        void onViolationDetected(String appName);
    }

    // ===== COMPONENTS =====

    /**
     * Session info label (top)
     */
    private Label sessionInfoLabel;

    /**
     * Blocked apps info label
     */
    private Label blockedAppsLabel;

    /**
     * Circular progress ring
     */
    private CircularProgressRing progressRing;

    /**
     * Time remaining display
     */
    private Label timeLabel;

    /**
     * PAUSE/RESUME button
     */
    private Button pauseButton;

    /**
     * STOP button
     */
    private Button stopButton;

    /**
     * Status label (shows "Paused" when paused)
     */
    private Label statusLabel;

    // ===== STATE =====

    /**
     * The focus session being tracked
     */
    private FocusSession focusSession;

    /**
     * The countdown timer
     */
    private Timer timer;

    /**
     * App monitor for detecting violations
     */
    private AppMonitor appMonitor;

    /**
     * Callback for events
     */
    private ActiveSessionCallback callback;

    /**
     * Whether session is paused
     */
    private boolean paused = false;

    // ===== CONSTRUCTOR =====

    /**
     * Creates an active session panel for the given session
     *
     * @param focusSession The session to track
     */
    public ActiveSessionPanel(FocusSession focusSession) {
        super();

        this.focusSession = focusSession;

        // Create app monitor for violation detection
        this.appMonitor = AppMonitor.createForCurrentOS();

        createComponents();
        layoutComponents();
        setupEventHandlers();
        initializeTimer();
    }

    // ===== INITIALIZATION METHODS =====

    /**
     * Creates all UI components
     */
    private void createComponents() {
        // Session info label
        sessionInfoLabel = new Label("Focus Session");
        sessionInfoLabel.setFont(UIConstants.getHeadingFont());
        sessionInfoLabel.setTextFill(getTextPrimaryColor());

        // Blocked apps info
        List<String> blockedApps = focusSession.getBlockedApps();
        String appsText = blockedApps.isEmpty() ? "No apps blocked" :
                String.format("Blocking: %s", String.join(", ", blockedApps));
        blockedAppsLabel = new Label(appsText);
        blockedAppsLabel.setFont(UIConstants.getSmallFont());
        blockedAppsLabel.setTextFill(getTextSecondaryColor());
        blockedAppsLabel.setWrapText(true);
        blockedAppsLabel.setMaxWidth(UIConstants.TIMER_RING_DIAMETER);
        blockedAppsLabel.setTextAlignment(TextAlignment.CENTER);

        // Progress ring (display mode, not selectable)
        progressRing = new CircularProgressRing(UIConstants.TIMER_RING_DIAMETER);
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
        statusLabel.setTextFill(UIConstants.WARNING_COLOR);
        statusLabel.setVisible(false);

        // PAUSE button
        pauseButton = new Button("PAUSE");
        pauseButton.setFont(UIConstants.getBodyFont());
        pauseButton.setPrefHeight(UIConstants.BUTTON_HEIGHT);
        pauseButton.setMinWidth(UIConstants.BUTTON_MIN_WIDTH * 1.2);
        pauseButton.setStyle(
                "-fx-background-color: " + toRGBCode(UIConstants.WARNING_COLOR) + ";" +
                        "-fx-text-fill: black;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-weight: bold;"
        );

        // STOP button
        stopButton = new Button("STOP");
        stopButton.setFont(UIConstants.getBodyFont());
        stopButton.setPrefHeight(UIConstants.BUTTON_HEIGHT);
        stopButton.setMinWidth(UIConstants.BUTTON_MIN_WIDTH * 1.2);
        stopButton.setStyle(
                "-fx-background-color: " + toRGBCode(UIConstants.DANGER_COLOR) + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-weight: bold;"
        );
    }

    /**
     * Arranges components in the layout
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
     * Sets up event handlers for buttons
     */
    private void setupEventHandlers() {
        // PAUSE button
        pauseButton.setOnAction(event -> handlePauseResume());

        // STOP button
        stopButton.setOnAction(event -> handleStop());
    }

    /**
     * Initializes and starts the countdown timer
     */
    private void initializeTimer() {
        int durationSeconds = focusSession.getPlannedDuration();

        // Create timer with callback
        timer = new Timer(durationSeconds, new Timer.TimerCallback() {
            @Override
            public void onTick(int remainingSeconds) {
                // This runs on JavaFX thread (Timeline handles it)
                updateTimerDisplay(remainingSeconds);
                checkForViolations();
            }

            @Override
            public void onComplete() {
                // Timer finished naturally
                handleTimerComplete();
            }
        });

        // Start the timer
        timer.start();
    }

    // ===== EVENT HANDLERS =====

    /**
     * Handles PAUSE/RESUME button click
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
     * Handles STOP button click
     */
    private void handleStop() {
        // TODO: Show confirmation dialog
        System.out.println("Stopping session early...");

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
     * Handles timer completion
     */
    private void handleTimerComplete() {
        System.out.println("Session complete!");

        // Session completed naturally with full duration
        int actualDuration = focusSession.getPlannedDuration();

        // Notify callback
        if (callback != null) {
            callback.onSessionComplete(focusSession);
        }
    }

    /**
     * Updates the timer display and progress ring
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
            progressRing.setRingColor(UIConstants.DANGER_COLOR);
        } else if (progress < 0.25) {
            // Less than 25% - yellow
            progressRing.setRingColor(UIConstants.WARNING_COLOR);
        } else {
            // Normal - accent color
            progressRing.setRingColor(UIConstants.ACCENT_COLOR);
        }
    }

    /**
     * Checks for violations (blocked apps running)
     * TODO: Implement fully in Phase 3
     */
    private void checkForViolations() {
        // Only check if not paused
        if (paused) {
            return;
        }

        // Check every few seconds (not every tick)
        if (timer.getElapsedSeconds() % 2 != 0) {
            return; // Only check every 2 seconds
        }

        // Get blocked apps
        List<String> blockedApps = focusSession.getBlockedApps();
        if (blockedApps.isEmpty()) {
            return; // No apps to block
        }

        // Check for violations
        List<String> violations = appMonitor.checkForViolations(blockedApps);

        if (!violations.isEmpty()) {
            // Violation detected
            String appName = violations.get(0); // First violation

            // Record in session
            focusSession.startViolation(appName);

            // Notify callback to show overlay
            if (callback != null) {
                callback.onViolationDetected(appName);
            }
        } else {
            // No violations - end current violation if any
            if (focusSession.hasActiveViolation()) {
                focusSession.endCurrentViolation();
            }
        }
    }

    // ===== PUBLIC METHODS =====

    /**
     * Sets the callback for session events
     *
     * @param callback Callback to receive events
     */
    public void setCallback(ActiveSessionCallback callback) {
        this.callback = callback;
    }

    /**
     * Gets the focus session being tracked
     *
     * @return Focus session
     */
    public FocusSession getFocusSession() {
        return focusSession;
    }

    /**
     * Gets the timer
     *
     * @return Timer
     */
    public Timer getTimer() {
        return timer;
    }

    /**
     * Cleans up resources when panel is closed
     */
    public void cleanup() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
