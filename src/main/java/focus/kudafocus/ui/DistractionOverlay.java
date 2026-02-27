package focus.kudafocus.ui;

import focus.kudafocus.core.FocusSession;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.geometry.Rectangle2D;

/**
 * Full-screen distraction overlay that appears when blocked apps are detected.
 *
 * This overlay serves as a gentle reminder to stay focused when the user
 * opens a blocked application during a focus session.
 *
 * Features:
 * - Semi-transparent dark background (70% opacity)
 * - "Stay Focused!" message
 * - Shows remaining session time
 * - "Return to Focus" button to dismiss
 * - Always on top (blocks interaction with blocked app)
 * - Reappears every 15 seconds if app still open
 *
 * Design Pattern:
 * - Modal dialog (blocks interaction with other windows)
 * - Timer-based reappearance
 * - Records dismissals to the session for scoring
 *
 * Learning Points:
 * - JavaFX Stage and Modality
 * - Transparent/semi-transparent windows
 * - Always-on-top windows
 * - Event handling for dismissal
 */
public class DistractionOverlay {

    // ===== CALLBACK INTERFACE =====

    /**
     * Callback interface for overlay events
     */
    public interface OverlayCallback {
        /**
         * Called when user dismisses the overlay
         */
        void onDismissed();

        /**
         * Called when overlay closes and app is still running
         * (will trigger reappearance after delay)
         */
        void onAppStillRunning();
    }

    // ===== COMPONENTS =====

    /**
     * The overlay window (Stage)
     */
    private Stage overlayStage;

    /**
     * Root layout
     */
    private VBox root;

    /**
     * Main message label
     */
    private Label messageLabel;

    /**
     * App name label
     */
    private Label appNameLabel;

    /**
     * Time remaining label
     */
    private Label timeRemainingLabel;

    /**
     * Dismiss button
     */
    private Button dismissButton;

    /**
     * Warning label
     */
    private Label warningLabel;

    // ===== STATE =====

    /**
     * The focus session being tracked
     */
    private FocusSession focusSession;

    /**
     * Name of the blocked app that triggered this overlay
     */
    private String blockedAppName;

    /**
     * Callback for events
     */
    private OverlayCallback callback;

    /**
     * Whether overlay is currently showing
     */
    private boolean showing = false;

    // ===== CONSTRUCTOR =====

    /**
     * Creates a distraction overlay for a specific app and session
     *
     * @param focusSession The active focus session
     * @param blockedAppName Name of the blocked app
     */
    public DistractionOverlay(FocusSession focusSession, String blockedAppName) {
        this.focusSession = focusSession;
        this.blockedAppName = blockedAppName;

        createOverlay();
    }

    // ===== INITIALIZATION METHODS =====

    /**
     * Creates the overlay window and components
     */
    private void createOverlay() {
        // Create stage (window)
        overlayStage = new Stage();
        overlayStage.initStyle(StageStyle.UNDECORATED); // No title bar
        overlayStage.initModality(Modality.NONE);

        // Create components
        createComponents();
        layoutComponents();
        setupEventHandlers();

        // Create scene with transparent background
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        overlayStage.setScene(scene);

        // Set always on top
        overlayStage.setAlwaysOnTop(true);

        // Use full-screen sized bounds instead of JavaFX fullscreen mode to
        // avoid black-screen artifacts on dismiss on some macOS setups.
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        overlayStage.setX(bounds.getMinX());
        overlayStage.setY(bounds.getMinY());
        overlayStage.setWidth(bounds.getWidth());
        overlayStage.setHeight(bounds.getHeight());
    }

    /**
     * Creates all UI components
     */
    private void createComponents() {
        // Main message
        messageLabel = new Label("⚠️ Stay Focused!");
        messageLabel.setFont(UIConstants.getTitleFont());
        messageLabel.setTextFill(UIConstants.TEXT_PRIMARY);
        messageLabel.setTextAlignment(TextAlignment.CENTER);

        // App name
        appNameLabel = new Label("You opened: " + blockedAppName);
        appNameLabel.setFont(UIConstants.getHeadingFont());
        appNameLabel.setTextFill(UIConstants.WARNING_COLOR);
        appNameLabel.setTextAlignment(TextAlignment.CENTER);

        // Time remaining
        int remainingMinutes = focusSession.getPlannedDurationMinutes();
        timeRemainingLabel = new Label(String.format("%d minutes remaining in your session", remainingMinutes));
        timeRemainingLabel.setFont(UIConstants.getBodyFont());
        timeRemainingLabel.setTextFill(UIConstants.TEXT_SECONDARY);
        timeRemainingLabel.setTextAlignment(TextAlignment.CENTER);

        // Dismiss button
        dismissButton = new Button("Return to Focus");
        dismissButton.setFont(UIConstants.getHeadingFont());
        dismissButton.setPrefHeight(UIConstants.BUTTON_HEIGHT * 1.2);
        dismissButton.setMinWidth(UIConstants.BUTTON_MIN_WIDTH * 2);
        dismissButton.setStyle(
                "-fx-background-color: " + toRGBCode(UIConstants.ACCENT_COLOR) + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-weight: bold;"
        );

        // Warning label
        warningLabel = new Label("Close the app to continue focused work\nThis overlay will reappear in 15 seconds");
        warningLabel.setFont(UIConstants.getSmallFont());
        warningLabel.setTextFill(UIConstants.TEXT_MUTED);
        warningLabel.setTextAlignment(TextAlignment.CENTER);
    }

    /**
     * Arranges components in the layout
     */
    private void layoutComponents() {
        root = new VBox(UIConstants.SPACING_XL);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(UIConstants.PADDING_STANDARD * 3));

        // Semi-transparent dark background
        root.setStyle("-fx-background-color: " + toRGBACode(UIConstants.OVERLAY_BACKGROUND) + ";");

        // Content container
        VBox content = new VBox(UIConstants.SPACING_LG);
        content.setAlignment(Pos.CENTER);
        content.getChildren().addAll(
                messageLabel,
                appNameLabel,
                timeRemainingLabel,
                dismissButton,
                warningLabel
        );

        root.getChildren().add(content);
    }

    /**
     * Sets up event handlers
     */
    private void setupEventHandlers() {
        // Dismiss button
        dismissButton.setOnAction(event -> handleDismiss());

        // When overlay is closed
        overlayStage.setOnHiding(event -> {
            showing = false;
        });
    }

    // ===== EVENT HANDLERS =====

    /**
     * Handles dismiss button click
     */
    private void handleDismiss() {
        // Record dismissal in session
        focusSession.recordDismissal();

        // Close overlay to fully release the stage and avoid stale fullscreen
        // rendering artifacts on some systems.
        close();

        // Notify callback
        if (callback != null) {
            callback.onDismissed();
        }
    }

    // ===== PUBLIC METHODS =====

    /**
     * Shows the overlay
     */
    public void show() {
        if (!showing) {
            showing = true;
            overlayStage.show();
            overlayStage.toFront();
        }
    }

    /**
     * Hides the overlay
     */
    public void hide() {
        if (showing) {
            showing = false;
            overlayStage.hide();
        }
    }

    /**
     * Updates the time remaining display
     *
     * @param remainingMinutes Minutes remaining in session
     */
    public void updateTimeRemaining(int remainingMinutes) {
        timeRemainingLabel.setText(String.format("%d minutes remaining in your session", remainingMinutes));
    }

    /**
     * Sets the callback for overlay events
     *
     * @param callback Callback to receive events
     */
    public void setCallback(OverlayCallback callback) {
        this.callback = callback;
    }

    /**
     * Checks if overlay is currently showing
     *
     * @return true if showing
     */
    public boolean isShowing() {
        return showing;
    }

    /**
     * Gets the blocked app name
     *
     * @return App name
     */
    public String getBlockedAppName() {
        return blockedAppName;
    }

    /**
     * Closes the overlay permanently (cleanup)
     */
    public void close() {
        if (overlayStage != null) {
            overlayStage.close();
        }
    }

    // ===== UTILITY METHODS =====

    /**
     * Converts JavaFX Color to RGB string
     */
    private String toRGBCode(Color color) {
        return String.format("rgb(%d, %d, %d)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    /**
     * Converts JavaFX Color to RGBA string with alpha channel
     */
    private String toRGBACode(Color color) {
        return String.format("rgba(%d, %d, %d, %.2f)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255),
                color.getOpacity());
    }
}
