package focus.kudafocus.ui;

import focus.kudafocus.core.FocusSession;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

/**
 * Session summary panel - displays results after a focus session completes.
 *
 * This panel demonstrates INHERITANCE - extends BasePanel for common styling.
 *
 * Components:
 * - Completion message (top)
 * - Focus score (LARGE, color-coded)
 * - Session statistics (duration, violations, etc.)
 * - Most distracting app
 * - CONTINUE button (returns to home)
 *
 * Focus Score Color Coding:
 * - 80-100: Green (SUCCESS_COLOR) - Excellent focus!
 * - 50-79: Yellow (WARNING_COLOR) - Good effort, room for improvement
 * - 0-49: Red (DANGER_COLOR) - Many distractions detected
 *
 * Learning Points:
 * - Conditional styling based on data
 * - Formatting and presenting statistics
 * - Data-driven UI updates
 */
public class SessionSummaryPanel extends BasePanel {

    // ===== CALLBACK INTERFACE =====

    /**
     * Callback interface for panel events
     */
    public interface SummaryCallback {
        /**
         * Called when user clicks CONTINUE to return to home screen
         */
        void onContinue();
    }

    // ===== COMPONENTS =====

    /**
     * Completion message label
     */
    private Label completionLabel;

    /**
     * Focus score label (large, color-coded)
     */
    private Label scoreLabel;

    /**
     * Score description label
     */
    private Label scoreDescriptionLabel;

    /**
     * Duration label
     */
    private Label durationLabel;

    /**
     * Violations label
     */
    private Label violationsLabel;

    /**
     * Dismissals label
     */
    private Label dismissalsLabel;

    /**
     * Most distracting app label
     */
    private Label mostDistractingLabel;

    /**
     * Streak update label
     */
    private Label streakUpdateLabel;

    /**
     * CONTINUE button
     */
    private Button continueButton;

    // ===== STATE =====

    /**
     * The completed focus session
     */
    private FocusSession focusSession;

    /**
     * Callback for events
     */
    private SummaryCallback callback;

    // ===== CONSTRUCTOR =====

    /**
     * Creates a session summary panel for the given completed session
     *
     * @param focusSession The completed session
     */
    public SessionSummaryPanel(FocusSession focusSession) {
        super();

        this.focusSession = focusSession;

        createComponents();
        layoutComponents();
        setupEventHandlers();
    }

    // ===== INITIALIZATION METHODS =====

    /**
     * Creates all UI components
     */
    private void createComponents() {
        // Completion message
        String completionText = focusSession.isCompleted() ? "Session Complete!" : "Session Stopped";
        completionLabel = new Label(completionText);
        completionLabel.setFont(UIConstants.getHeadingFont());
        completionLabel.setTextFill(getTextPrimaryColor());

        // Focus score (LARGE)
        int score = focusSession.getFocusScore();
        scoreLabel = new Label(String.valueOf(score));
        scoreLabel.setFont(UIConstants.getDisplayFont());
        scoreLabel.setTextAlignment(TextAlignment.CENTER);

        // Color code the score
        Color scoreColor = getScoreColor(score);
        scoreLabel.setTextFill(scoreColor);

        // Score description
        String description = getScoreDescription(score);
        scoreDescriptionLabel = new Label(description);
        scoreDescriptionLabel.setFont(UIConstants.getHeadingFont());
        scoreDescriptionLabel.setTextFill(scoreColor);
        scoreDescriptionLabel.setTextAlignment(TextAlignment.CENTER);

        // Duration
        int actualMinutes = focusSession.getActualDurationMinutes();
        int plannedMinutes = focusSession.getPlannedDurationMinutes();
        String durationText;
        if (actualMinutes == plannedMinutes) {
            durationText = String.format("Duration: %d minutes", actualMinutes);
        } else {
            durationText = String.format("Duration: %d / %d minutes", actualMinutes, plannedMinutes);
        }
        durationLabel = new Label(durationText);
        durationLabel.setFont(UIConstants.getBodyFont());
        durationLabel.setTextFill(getTextPrimaryColor());

        // Violations
        int violationCount = focusSession.getViolationCount();
        violationsLabel = new Label(String.format("Distractions: %d", violationCount));
        violationsLabel.setFont(UIConstants.getBodyFont());
        violationsLabel.setTextFill(getTextSecondaryColor());

        // Dismissals
        int dismissalCount = focusSession.getTotalDismissals();
        dismissalsLabel = new Label(String.format("Overlay dismissals: %d", dismissalCount));
        dismissalsLabel.setFont(UIConstants.getBodyFont());
        dismissalsLabel.setTextFill(getTextSecondaryColor());

        // Most distracting app
        String mostDistracting = focusSession.getMostDistractingApp();
        if (!mostDistracting.equals("None")) {
            int distractedSeconds = focusSession.getTotalDistractionSeconds();
            mostDistractingLabel = new Label(
                    String.format("Most distracting: %s (%d sec)", mostDistracting, distractedSeconds)
            );
        } else {
            mostDistractingLabel = new Label("No distractions - perfect focus! 🎯");
        }
        mostDistractingLabel.setFont(UIConstants.getBodyFont());
        mostDistractingLabel.setTextFill(getTextSecondaryColor());

        // Streak update
        boolean qualifies = focusSession.qualifiesForStreak();
        if (qualifies) {
            streakUpdateLabel = new Label("✨ This session counts toward your streak!");
            streakUpdateLabel.setTextFill(UIConstants.SUCCESS_COLOR);
        } else {
            streakUpdateLabel = new Label("Reach 80+ score and 30+ min to build your streak");
            streakUpdateLabel.setTextFill(UIConstants.TEXT_MUTED);
        }
        streakUpdateLabel.setFont(UIConstants.getSmallFont());
        streakUpdateLabel.setTextAlignment(TextAlignment.CENTER);

        // CONTINUE button
        continueButton = new Button("CONTINUE");
        continueButton.setFont(UIConstants.getHeadingFont());
        continueButton.setPrefHeight(UIConstants.BUTTON_HEIGHT);
        continueButton.setMinWidth(UIConstants.BUTTON_MIN_WIDTH * 1.5);
        continueButton.setStyle(
                "-fx-background-color: " + toRGBCode(getAccentColor()) + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 25;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-weight: bold;"
        );
    }

    /**
     * Arranges components in the layout
     */
    private void layoutComponents() {
        this.getChildren().clear();

        // Top section (completion message)
        VBox topSection = new VBox(UIConstants.SPACING_SM);
        topSection.setAlignment(Pos.CENTER);
        topSection.getChildren().add(completionLabel);

        // Score section (large score with description)
        VBox scoreSection = new VBox(UIConstants.SPACING_SM);
        scoreSection.setAlignment(Pos.CENTER);
        scoreSection.getChildren().addAll(scoreLabel, scoreDescriptionLabel);

        // Stats section
        VBox statsSection = new VBox(UIConstants.SPACING_SM);
        statsSection.setAlignment(Pos.CENTER);
        statsSection.getChildren().addAll(
                durationLabel,
                violationsLabel,
                dismissalsLabel,
                mostDistractingLabel
        );

        // Streak section
        VBox streakSection = new VBox(UIConstants.SPACING_XS);
        streakSection.setAlignment(Pos.CENTER);
        streakSection.getChildren().add(streakUpdateLabel);

        // Button section
        VBox buttonSection = new VBox(UIConstants.SPACING_SM);
        buttonSection.setAlignment(Pos.CENTER);
        buttonSection.getChildren().add(continueButton);

        // Add all sections with spacing
        VBox.setMargin(topSection, new Insets(UIConstants.SPACING_XL, 0, 0, 0));
        VBox.setMargin(scoreSection, new Insets(UIConstants.SPACING_LG, 0, 0, 0));
        VBox.setMargin(statsSection, new Insets(UIConstants.SPACING_XL, 0, 0, 0));
        VBox.setMargin(streakSection, new Insets(UIConstants.SPACING_LG, 0, 0, 0));
        VBox.setMargin(buttonSection, new Insets(UIConstants.SPACING_XL, 0, UIConstants.SPACING_LG, 0));

        this.getChildren().addAll(
                topSection,
                scoreSection,
                statsSection,
                streakSection,
                buttonSection
        );
        this.setAlignment(Pos.CENTER);
    }

    /**
     * Sets up event handlers
     */
    private void setupEventHandlers() {
        continueButton.setOnAction(event -> handleContinue());
    }

    // ===== EVENT HANDLERS =====

    /**
     * Handles CONTINUE button click
     */
    private void handleContinue() {
        if (callback != null) {
            callback.onContinue();
        }
    }

    // ===== UTILITY METHODS =====

    /**
     * Gets the color for a given score
     *
     * @param score Focus score (0-100)
     * @return Color for the score
     */
    private Color getScoreColor(int score) {
        if (score >= UIConstants.MIN_STREAK_SCORE) {
            return UIConstants.SUCCESS_COLOR; // 80-100: Green
        } else if (score >= 50) {
            return UIConstants.WARNING_COLOR; // 50-79: Yellow
        } else {
            return UIConstants.DANGER_COLOR; // 0-49: Red
        }
    }

    /**
     * Gets a description for a given score
     *
     * @param score Focus score (0-100)
     * @return Description text
     */
    private String getScoreDescription(int score) {
        if (score >= 95) {
            return "Exceptional Focus! 🌟";
        } else if (score >= 85) {
            return "Excellent Focus!";
        } else if (score >= 70) {
            return "Good Focus";
        } else if (score >= 50) {
            return "Moderate Focus";
        } else if (score >= 30) {
            return "Many Distractions";
        } else {
            return "Very Distracted";
        }
    }

    // ===== PUBLIC METHODS =====

    /**
     * Sets the callback for panel events
     *
     * @param callback Callback to receive events
     */
    public void setCallback(SummaryCallback callback) {
        this.callback = callback;
    }

    /**
     * Gets the focus session
     *
     * @return Focus session
     */
    public FocusSession getFocusSession() {
        return focusSession;
    }
}
