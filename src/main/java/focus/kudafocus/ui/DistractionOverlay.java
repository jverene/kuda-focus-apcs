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
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Represents a full-screen distraction overlay that appears when blocked applications are detected.
 *
 * This overlay serves as a visual reminder to the user to maintain focus when they attempt
 * to access a blocked application during an active session. It covers the primary screen
 * with a semi-transparent layer, displays the remaining session time, and provides a
 * button to return to a focused state.
 */
public class DistractionOverlay {

    // ===== CALLBACK INTERFACE =====

    /**
     * Defines the contract for receiving events from the distraction overlay.
     */
    public interface OverlayCallback {
        /**
         * Invoked when the user dismisses the overlay to return to their focus session.
         */
        void onDismissed();
    }

    // ===== COMPONENTS =====

    /**
     * The stage representing the overlay window.
     */
    private Stage overlayStage;

    /**
     * The root layout container for the overlay components.
     */
    private VBox root;

    /**
     * The label displaying the main focus reminder message.
     */
    private Label messageLabel;

    /**
     * The label displaying the name of the application that triggered the overlay.
     */
    private Label appNameLabel;

    /**
     * The label displaying the remaining session time in minutes.
     */
    private Label timeRemainingLabel;

    /**
     * The button used by the user to dismiss the overlay.
     */
    private Button dismissButton;

    /**
     * The label providing additional instructions for dismissing the overlay.
     */
    private Label warningLabel;

    // ===== STATE =====

    /**
     * The active focus session being monitored.
     */
    private FocusSession focusSession;

    /**
     * The name of the blocked application that triggered the appearance of the overlay.
     */
    private String blockedAppName;

    /**
     * The callback object for notifying overlay-related events.
     */
    private OverlayCallback callback;

    /**
     * The theme providing the color palette for the overlay.
     */
    private Theme theme;

    /**
     * Indicates whether the overlay is currently visible to the user.
     */
    private boolean showing = false;

    // ===== CONSTRUCTORS =====

    /**
     * Constructs a distraction overlay for the specified session and application using the dark theme.
     *
     * @param focusSession The active focus session.
     * @param blockedAppName The name of the application that triggered the overlay.
     */
    public DistractionOverlay(FocusSession focusSession, String blockedAppName) {
        this(focusSession, blockedAppName, new DarkTheme());
    }

    /**
     * Constructs a distraction overlay for the specified session and application using a custom theme.
     *
     * @param focusSession The active focus session.
     * @param blockedAppName The name of the application that triggered the overlay.
     * @param theme The theme providing the color palette for the overlay.
     */
    public DistractionOverlay(FocusSession focusSession, String blockedAppName, Theme theme) {
        this.focusSession = focusSession;
        this.blockedAppName = blockedAppName;
        this.theme = theme;

        createOverlay();
    }

    // ===== INITIALIZATION METHODS =====

    /**
     * Initializes the overlay stage, components, and layout.
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
     * Creates and configures the UI components for the overlay.
     */
    private void createComponents() {
        // Main message
        messageLabel = new Label("Stay Focused!");
        messageLabel.setFont(UIConstants.getTitleFont());
        messageLabel.setTextFill(theme.getTextPrimary());
        messageLabel.setTextAlignment(TextAlignment.CENTER);

        // Warning Icon
        FontIcon warningIcon = new FontIcon("fth-alert-triangle");
        warningIcon.setIconSize(48);
        warningIcon.setIconColor(theme.getWarningColor());
        messageLabel.setGraphic(warningIcon);
        messageLabel.setGraphicTextGap(15);

        // App name
        appNameLabel = new Label("You opened: " + blockedAppName);
        appNameLabel.setFont(UIConstants.getHeadingFont());
        appNameLabel.setTextFill(theme.getWarningColor());
        appNameLabel.setTextAlignment(TextAlignment.CENTER);

        // Time remaining
        int remainingMinutes = focusSession.getPlannedDurationMinutes();
        timeRemainingLabel = new Label(String.format("%d minutes remaining in your session", remainingMinutes));
        timeRemainingLabel.setFont(UIConstants.getBodyFont());
        timeRemainingLabel.setTextFill(theme.getTextSecondary());
        timeRemainingLabel.setTextAlignment(TextAlignment.CENTER);

        // Dismiss button
        dismissButton = new Button("Return to Focus");
        dismissButton.setFont(UIConstants.getHeadingFont());
        dismissButton.setPrefHeight(UIConstants.BUTTON_HEIGHT * 1.2);
        dismissButton.setMinWidth(UIConstants.BUTTON_MIN_WIDTH * 2);
        dismissButton.setStyle(
                "-fx-background-color: " + toRGBCode(theme.getAccentColor()) + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-weight: bold;"
        );

        // Warning label
        warningLabel = new Label("Close the app to continue focused work\nThis overlay will reappear in "
                + UIConstants.OVERLAY_REAPPEAR_SECONDS + " seconds");
        warningLabel.setFont(UIConstants.getSmallFont());
        warningLabel.setTextFill(theme.getTextMuted());
        warningLabel.setTextAlignment(TextAlignment.CENTER);
    }

    /**
     * Configures the layout arrangement for the overlay components.
     */
    private void layoutComponents() {
        root = new VBox(UIConstants.SPACING_XL);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(UIConstants.PADDING_STANDARD * 3));

        // Semi-transparent dark background
        root.setStyle("-fx-background-color: " + toRGBACode(theme.getOverlayBackground()) + ";");

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
     * Sets up event handlers for user interactions within the overlay.
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
     * Handles the interaction for dismissing the distraction overlay.
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
     * Displays the distraction overlay on the screen.
     */
    public void show() {
        if (!showing) {
            showing = true;
            overlayStage.show();
            overlayStage.toFront();
        }
    }

    /**
     * Hides the distraction overlay from the user's view.
     */
    public void hide() {
        if (showing) {
            showing = false;
            overlayStage.hide();
        }
    }

    /**
     * Updates the remaining session time displayed on the overlay.
     *
     * @param remainingMinutes The number of minutes remaining in the current session.
     */
    public void updateTimeRemaining(int remainingMinutes) {
        timeRemainingLabel.setText(String.format("%d minutes remaining in your session", remainingMinutes));
    }

    /**
     * Registers a callback for receiving events from the distraction overlay.
     *
     * @param callback The callback object to be notified of events.
     */
    public void setCallback(OverlayCallback callback) {
        this.callback = callback;
    }

    /**
     * Indicates whether the distraction overlay is currently visible.
     *
     * @return true if the overlay is showing, false otherwise.
     */
    public boolean isShowing() {
        return showing;
    }

    /**
     * Retrieves the name of the blocked application that triggered the overlay.
     *
     * @return The name of the blocked application.
     */
    public String getBlockedAppName() {
        return blockedAppName;
    }

    /**
     * Permanently closes the overlay stage and releases associated resources.
     */
    public void close() {
        if (overlayStage != null) {
            overlayStage.close();
        }
    }

    // ===== UTILITY METHODS =====

    /**
     * Converts a JavaFX Color to a CSS-compatible RGB string representation.
     *
     * @param color The Color object to convert.
     * @return A string representing the RGB code.
     */
    private String toRGBCode(Color color) {
        return String.format("rgb(%d, %d, %d)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    /**
     * Converts a JavaFX Color to a CSS-compatible RGBA string with an alpha channel.
     *
     * @param color The Color object to convert.
     * @return A string representing the RGBA code.
     */
    private String toRGBACode(Color color) {
        return String.format("rgba(%d, %d, %d, %.2f)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255),
                color.getOpacity());
    }
}
