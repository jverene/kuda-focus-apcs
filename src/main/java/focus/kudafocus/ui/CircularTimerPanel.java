package focus.kudafocus.ui;

import focus.kudafocus.ui.components.CircularProgressRing;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.feather.Feather;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the home screen panel featuring a circular timer interface for session duration selection.
 *
 * This panel extends {@link BasePanel} and allows users to interactively select a focus
 * session duration by dragging around a circular progress ring. It also displays the current
 * streak, provides options for selecting blocked applications, and allows toggling between
 * light and dark themes.
 */
public class CircularTimerPanel extends BasePanel {

    // ===== CALLBACK INTERFACE =====

    /**
     * Defines the contract for receiving events and interactions from the circular timer panel.
     */
    public interface CircularTimerCallback {
        /**
         * Invoked when the user initiates a focus session by clicking the start button.
         *
         * @param durationMinutes The selected session duration in minutes.
         * @param blockedApps The list of application names to be blocked.
         * @param blockedWebsites The list of website domains to be blocked.
         */
        void onStartSession(int durationMinutes, List<String> blockedApps, List<String> blockedWebsites);

        /**
         * Invoked when the user requests to select applications or websites to block.
         */
        void onSelectApps();

        /**
         * Invoked when the user toggles the light mode setting.
         *
         * @param enable true if light mode should be enabled, false for dark mode.
         */
        void onToggleLightMode(boolean enable);
    }

    // ===== COMPONENTS =====

    /**
     * The label displaying the user's current focus streak.
     */
    private Label streakLabel;

    /**
     * The circular progress ring used for session duration selection.
     */
    private CircularProgressRing progressRing;

    /**
     * The label displaying the currently selected duration in a time format.
     */
    private Label timeLabel;

    /**
     * The button used to start the focus session.
     */
    private Button startButton;

    /**
     * The button used to open the application selection interface.
     */
    private Button selectAppsButton;

    /**
     * The label displaying the status of selected applications and websites.
     */
    private Label appsStatusLabel;

    /**
     * The button used to toggle between light and dark themes.
     */
    private Button lightModeButton;

    // ===== STATE =====

    /**
     * The current number of consecutive days the user has maintained their streak.
     */
    private int currentStreak = 0;

    /**
     * The list of application names currently selected to be blocked.
     */
    private List<String> selectedApps = new ArrayList<>();

    /**
     * The list of website domains currently selected to be blocked.
     */
    private List<String> selectedWebsites = new ArrayList<>();

    /**
     * The callback object for notifying user interactions and panel events.
     */
    private CircularTimerCallback callback;

    /**
     * Indicates whether light mode is currently the active theme setting.
     */
    private boolean lightModeEnabled = false;

    // ===== CONSTRUCTORS =====

    /**
     * Constructs a circular timer panel using the default dark theme.
     */
    public CircularTimerPanel() {
        super();
        createComponents();
        layoutComponents();
        setupEventHandlers();
        updateTimeDisplay();
    }

    /**
     * Constructs a circular timer panel with the specified theme.
     *
     * @param theme The theme providing the color palette for the panel.
     */
    public CircularTimerPanel(Theme theme) {
        super(theme);
        lightModeEnabled = (theme instanceof LightTheme);
        createComponents();
        layoutComponents();
        setupEventHandlers();
        updateTimeDisplay();
    }

    // ===== INITIALIZATION METHODS =====

    /**
     * Creates and configures all UI components for the panel.
     */
    private void createComponents() {
        // Streak label (top)
        streakLabel = new Label("0 days");
        streakLabel.setFont(UIConstants.getHeadingFont());
        streakLabel.setTextFill(getTextPrimaryColor());
        FontIcon streakIcon = new FontIcon("fth-trending-up");
        streakIcon.setIconColor(getTextPrimaryColor());
        streakLabel.setGraphic(streakIcon);
        streakLabel.setGraphicTextGap(8);

        // Circular progress ring
        progressRing = new CircularProgressRing(UIConstants.TIMER_RING_DIAMETER);
        progressRing.setThemeColors(
                getTheme().getBackgroundSecondary(),
                getAccentColor(),
                getTextPrimaryColor()
        );
        progressRing.setSelectionMode(true);
        progressRing.setSelectedMinutes(45); // Default 45 minutes

        // Time display (large, center of ring)
        timeLabel = new Label("0:45:00");
        timeLabel.setFont(UIConstants.getDisplayFont());
        timeLabel.setTextFill(getTextPrimaryColor());
        timeLabel.setTextAlignment(TextAlignment.CENTER);

        // START button
        startButton = new Button("START");
        startButton.setFont(UIConstants.getHeadingFont());
        startButton.setPrefHeight(UIConstants.BUTTON_HEIGHT);
        startButton.setMinWidth(UIConstants.BUTTON_MIN_WIDTH * 1.5);
        startButton.setStyle(
                "-fx-background-color: " + toRGBCode(getAccentColor()) + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 25;" +
                        "-fx-cursor: hand;"
        );
        UIConstants.setupButtonAnimation(startButton);

        // App selection button
        selectAppsButton = new Button("Select apps to block");
        selectAppsButton.setFont(UIConstants.getBodyFont());
        selectAppsButton.setPrefHeight(UIConstants.BUTTON_HEIGHT);
        selectAppsButton.setMinWidth(UIConstants.BUTTON_MIN_WIDTH * 2);
        selectAppsButton.setStyle(
                "-fx-background-color: " + toRGBCode(getTheme().getBackgroundSecondary()) + ";" +
                        "-fx-text-fill: " + toRGBCode(getTextPrimaryColor()) + ";" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        );
        UIConstants.setupButtonAnimation(selectAppsButton);

        // Apps status label
        appsStatusLabel = new Label("No apps selected");
        appsStatusLabel.setFont(UIConstants.getSmallFont());
        appsStatusLabel.setTextFill(getTextSecondaryColor());

        // Light mode toggle button
        lightModeButton = new Button(lightModeEnabled ? "Dark Mode" : "Light Mode");
        lightModeButton.setFont(UIConstants.getSmallFont());
        lightModeButton.setStyle(
                "-fx-background-color: " + toRGBCode(getTheme().getBackgroundSecondary()) + ";" +
                        "-fx-text-fill: " + toRGBCode(getTextSecondaryColor()) + ";" +
                        "-fx-background-radius: 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 6 14;"
        );
        UIConstants.setupButtonAnimation(lightModeButton);
    }

    /**
     * Arranges the UI components within the panel layout.
     */
    private void layoutComponents() {
        // Clear any existing children
        this.getChildren().clear();

        // Create center content (ring with time and button inside)
        StackPane ringStack = new StackPane();

        // VBox for time label and start button (centered in ring)
        VBox centerContent = new VBox(UIConstants.SPACING_MD);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.getChildren().addAll(timeLabel, startButton);

        // IMPORTANT: Let mouse events pass through to the ring underneath
        // pickOnBounds=false means only actual child nodes intercept clicks, not empty space
        centerContent.setPickOnBounds(false);
        // Let clicks pass through the time label to the ring
        timeLabel.setMouseTransparent(true);

        // Stack ring and center content
        ringStack.getChildren().addAll(progressRing, centerContent);
        ringStack.setAlignment(Pos.CENTER);

        // Create bottom section with app selection
        VBox bottomSection = new VBox(UIConstants.SPACING_SM);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.getChildren().addAll(selectAppsButton, appsStatusLabel);

        // Top bar with streak on the left and light mode toggle on the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topBar = new HBox(streakLabel, spacer, lightModeButton);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(0));

        // Add all sections to main panel
        VBox.setMargin(topBar, new Insets(UIConstants.SPACING_LG, 0, 0, 0));
        VBox.setMargin(ringStack, new Insets(UIConstants.SPACING_XL, 0, 0, 0));
        VBox.setMargin(bottomSection, new Insets(UIConstants.SPACING_XL, 0, UIConstants.SPACING_LG, 0));

        this.getChildren().addAll(topBar, ringStack, bottomSection);
        this.setAlignment(Pos.CENTER);
    }

    /**
     * Configures event handlers for interactive UI components.
     */
    private void setupEventHandlers() {
        // Update time display when ring selection changes (via callback, not overwriting handlers)
        progressRing.setSelectionChangeListener(minutes -> updateTimeDisplay());

        // START button - begin session
        startButton.setOnAction(event -> handleStartSession());

        // App selection button
        selectAppsButton.setOnAction(event -> handleSelectApps());

        // Light mode toggle button
        lightModeButton.setOnAction(event -> handleToggleLightMode());
    }

    // ===== EVENT HANDLERS =====

    /**
     * Handles the interaction for starting a new focus session.
     */
    private void handleStartSession() {
        int minutes = progressRing.getSelectedMinutes();

        // Validate duration (must be at least 1 minute)
        if (minutes < 1) {
            return;
        }

        // Notify callback
        if (callback != null) {
            callback.onStartSession(minutes, new ArrayList<>(selectedApps), new ArrayList<>(selectedWebsites));
        }
    }

    /**
     * Handles the interaction for toggling the light mode theme.
     */
    private void handleToggleLightMode() {
        lightModeEnabled = !lightModeEnabled;
        if (callback != null) {
            callback.onToggleLightMode(lightModeEnabled);
        }
    }

    /**
     * Handles the interaction for opening the application selection interface.
     */
    private void handleSelectApps() {
        if (callback != null) {
            callback.onSelectApps();
        }
    }

    /**
     * Updates the time display label based on the current ring selection.
     */
    private void updateTimeDisplay() {
        int minutes = progressRing.getSelectedMinutes();
        int hours = minutes / 60;
        int mins = minutes % 60;

        // Format as H:MM:SS (seconds always 00 for selection)
        String timeText = String.format("%d:%02d:00", hours, mins);
        timeLabel.setText(timeText);
    }

    // ===== PUBLIC METHODS =====

    /**
     * Registers a callback for receiving panel interactions and events.
     *
     * @param callback The callback to be notified of panel events.
     */
    public void setCallback(CircularTimerCallback callback) {
        this.callback = callback;
    }

    /**
     * Updates the streak display with the specified number of days.
     *
     * @param streakDays The number of consecutive days maintained in the streak.
     */
    public void setStreak(int streakDays) {
        this.currentStreak = streakDays;

        if (streakDays == 0) {
            streakLabel.setText("Start your streak!");
        } else {
            streakLabel.setText(String.format("%d day%s", streakDays, streakDays == 1 ? "" : "s"));
        }
    }

    /**
     * Updates the list of selected applications to be blocked.
     *
     * @param apps The list of application names to block.
     */
    public void setSelectedApps(List<String> apps) {
        this.selectedApps = new ArrayList<>(apps);
        updateStatusLabel();
    }

    /**
     * Retrieves the list of currently selected applications.
     *
     * @return A list of selected application names.
     */
    public List<String> getSelectedApps() {
        return new ArrayList<>(selectedApps);
    }

    /**
     * Updates the list of selected website domains to be blocked.
     *
     * @param websites The list of website domains to block.
     */
    public void setSelectedWebsites(List<String> websites) {
        this.selectedWebsites = new ArrayList<>(websites);
        updateStatusLabel();
    }

    /**
     * Retrieves the list of currently selected websites.
     *
     * @return A list of selected website domains.
     */
    public List<String> getSelectedWebsites() {
        return new ArrayList<>(selectedWebsites);
    }

    /**
     * Updates the status label based on the current selection of apps and websites.
     */
    private void updateStatusLabel() {
        String status;
        
        if (selectedApps.isEmpty() && selectedWebsites.isEmpty()) {
            status = "No apps or sites selected";
            appsStatusLabel.setTextFill(getTextSecondaryColor());
        } else {
            StringBuilder sb = new StringBuilder();
            if (!selectedApps.isEmpty()) {
                sb.append(selectedApps.size()).append(" app").append(selectedApps.size() == 1 ? "" : "s");
            }
            if (!selectedWebsites.isEmpty()) {
                if (sb.length() > 0) sb.append(" | ");
                sb.append(selectedWebsites.size()).append(" site").append(selectedWebsites.size() == 1 ? "" : "s");
            }
            status = sb.append(" selected").toString();
            appsStatusLabel.setTextFill(getAccentColor());
        }
        
        appsStatusLabel.setText(status);
    }

    /**
     * Sets the selected session duration in minutes and updates the display.
     *
     * @param minutes The desired duration in minutes.
     */
    public void setSelectedMinutes(int minutes) {
        progressRing.setSelectedMinutes(minutes);
        updateTimeDisplay();
    }

    /**
     * Retrieves the currently selected session duration in minutes.
     *
     * @return The selected duration in minutes.
     */
    public int getSelectedMinutes() {
        return progressRing.getSelectedMinutes();
    }

    /**
     * Resets the panel to its default state, including duration and display.
     */
    public void reset() {
        progressRing.reset();
        setSelectedMinutes(45); // Default 45 minutes
        updateTimeDisplay();
    }
}
