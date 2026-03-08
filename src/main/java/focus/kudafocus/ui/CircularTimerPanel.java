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

import java.util.ArrayList;
import java.util.List;

/**
 * Home screen panel with circular timer interface.
 *
 * This panel demonstrates INHERITANCE - it extends BasePanel to inherit
 * common styling and layout properties.
 *
 * Components:
 * - Streak display (top)
 * - Circular progress ring (center) - drag to select time
 * - Time display (center of ring)
 * - START button (center of ring, below time)
 * - App selection button (bottom)
 *
 * User Interaction Flow:
 * 1. User drags around ring perimeter to select duration (0-180 minutes)
 * 2. Time display updates in real-time as they drag
 * 3. User clicks "Select apps to block" to choose blocked apps (TODO Phase 3)
 * 4. User clicks START to begin session
 * 5. Panel transitions to ActiveSessionPanel
 */
public class CircularTimerPanel extends BasePanel {

    // ===== CALLBACK INTERFACE =====

    /**
     * Callback interface for panel events.
     * This allows Main.java (or parent controller) to respond to user actions.
     */
    public interface CircularTimerCallback {
        /**
         * Called when user clicks START button
         *
         * @param durationMinutes Selected duration in minutes
         * @param blockedApps List of app names to block
         * @param blockedWebsites List of website domains to block
         */
        void onStartSession(int durationMinutes, List<String> blockedApps, List<String> blockedWebsites);

        /**
         * Called when user wants to select apps to block
         */
        void onSelectApps();

        /**
         * Called when user toggles the light mode button
         *
         * @param enable true to enable light mode, false to revert to dark mode
         */
        void onToggleLightMode(boolean enable);
    }

    // ===== COMPONENTS =====

    /**
     * Streak display label (top)
     */
    private Label streakLabel;

    /**
     * Circular progress ring (for time selection)
     */
    private CircularProgressRing progressRing;

    /**
     * Time display label (center of ring)
     */
    private Label timeLabel;

    /**
     * START button (center of ring, below time)
     */
    private Button startButton;

    /**
     * App selection button (bottom)
     */
    private Button selectAppsButton;

    /**
     * Status label showing selected apps count
     */
    private Label appsStatusLabel;

    /**
     * Light mode toggle button (top-right corner)
     */
    private Button lightModeButton;

    // ===== STATE =====

    /**
     * Current streak count (days)
     */
    private int currentStreak = 0;

    /**
     * List of currently selected apps to block
     */
    private List<String> selectedApps = new ArrayList<>();

    /**
     * List of currently selected websites to block
     */
    private List<String> selectedWebsites = new ArrayList<>();

    /**
     * Callback for events
     */
    private CircularTimerCallback callback;

    /**
     * Whether light mode is currently active
     */
    private boolean lightModeEnabled = false;

    // ===== CONSTRUCTORS =====

    /**
     * Creates the circular timer panel with the default dark theme
     */
    public CircularTimerPanel() {
        super();
        createComponents();
        layoutComponents();
        setupEventHandlers();
        updateTimeDisplay();
    }

    /**
     * Creates the circular timer panel with a custom theme
     *
     * @param theme Theme providing the color palette
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
     * Creates all UI components
     */
    private void createComponents() {
        // Streak label (top)
        streakLabel = new Label("🔥 0 days");
        streakLabel.setFont(UIConstants.getHeadingFont());
        streakLabel.setTextFill(getTextPrimaryColor());

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
    }

    /**
     * Arranges components in the layout
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
     * Sets up event handlers for user interactions
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
     * Handles START button click
     */
    private void handleStartSession() {
        int minutes = progressRing.getSelectedMinutes();

        // Validate duration (must be at least 1 minute)
        if (minutes < 1) {
            // Could show error message here
            System.out.println("Please select at least 1 minute");
            return;
        }

        // Notify callback
        if (callback != null) {
            callback.onStartSession(minutes, new ArrayList<>(selectedApps), new ArrayList<>(selectedWebsites));
        }
    }

    /**
     * Handles light mode toggle button click
     */
    private void handleToggleLightMode() {
        lightModeEnabled = !lightModeEnabled;
        if (callback != null) {
            callback.onToggleLightMode(lightModeEnabled);
        }
    }

    /**
     * Handles app selection button click
     */
    private void handleSelectApps() {
        if (callback != null) {
            callback.onSelectApps();
        }
    }

    /**
     * Updates the time display label based on ring selection
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
     * Sets the callback for panel events
     *
     * @param callback Callback to receive events
     */
    public void setCallback(CircularTimerCallback callback) {
        this.callback = callback;
    }

    /**
     * Updates the streak display
     *
     * @param streakDays Number of consecutive days
     */
    public void setStreak(int streakDays) {
        this.currentStreak = streakDays;

        if (streakDays == 0) {
            streakLabel.setText("Start your streak!");
        } else {
            streakLabel.setText(String.format("🔥 %d day%s", streakDays, streakDays == 1 ? "" : "s"));
        }
    }

    /**
     * Updates the selected apps list
     *
     * @param apps List of app names
     */
    public void setSelectedApps(List<String> apps) {
        this.selectedApps = new ArrayList<>(apps);
        updateStatusLabel();
    }

    /**
     * Gets the currently selected apps
     *
     * @return List of app names
     */
    public List<String> getSelectedApps() {
        return new ArrayList<>(selectedApps);
    }

    /**
     * Updates the selected websites list
     *
     * @param websites List of website domains
     */
    public void setSelectedWebsites(List<String> websites) {
        this.selectedWebsites = new ArrayList<>(websites);
        updateStatusLabel();
    }

    /**
     * Gets the currently selected websites
     *
     * @return List of website domains
     */
    public List<String> getSelectedWebsites() {
        return new ArrayList<>(selectedWebsites);
    }

    /**
     * Updates the status label based on selected apps and websites
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
     * Sets the selected duration in minutes
     *
     * @param minutes Duration in minutes
     */
    public void setSelectedMinutes(int minutes) {
        progressRing.setSelectedMinutes(minutes);
        updateTimeDisplay();
    }

    /**
     * Gets the selected duration in minutes
     *
     * @return Duration in minutes
     */
    public int getSelectedMinutes() {
        return progressRing.getSelectedMinutes();
    }

    /**
     * Resets the panel to default state
     */
    public void reset() {
        progressRing.reset();
        setSelectedMinutes(45); // Default 45 minutes
        updateTimeDisplay();
    }
}
