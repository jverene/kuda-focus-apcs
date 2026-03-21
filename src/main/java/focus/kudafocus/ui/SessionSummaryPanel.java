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
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.feather.Feather;

/**
 * Represents the session summary panel that displays results after a focus session completes.
 *
 * Extends the {@link BasePanel} for common styling. This panel displays the completion status, 
 * a color-coded focus score, session statistics including duration and violations, and the most 
 * distracting application identified during the session.
 *
 * The focus score is color-coded as follows:
 * <ul>
 *   <li>80-100: Green (SUCCESS_COLOR) - Indicates excellent focus.</li>
 *   <li>50-79: Yellow (WARNING_COLOR) - Indicates good effort with room for improvement.</li>
 *   <li>0-49: Red (DANGER_COLOR) - Indicates many distractions were detected.</li>
 * </ul>
 */
public class SessionSummaryPanel extends BasePanel {

    // ===== CALLBACK INTERFACE =====

    /**
     * Provides a callback interface for handling panel events.
     */
    public interface SummaryCallback {
        /**
         * Handles the event when the user clicks CONTINUE to return to the home screen.
         */
        void onContinue();
    }

    // ===== COMPONENTS =====

    /**
     * The label for displaying the completion message.
     */
    private Label completionLabel;

    /**
     * The label for displaying the focus score.
     */
    private Label scoreLabel;

    /**
     * The label for displaying the score description.
     */
    private Label scoreDescriptionLabel;

    /**
     * The label for displaying the session duration.
     */
    private Label durationLabel;

    /**
     * The label for displaying the number of violations.
     */
    private Label violationsLabel;

    /**
     * The label for displaying the number of dismissals.
     */
    private Label dismissalsLabel;

    /**
     * The label for displaying the most distracting application.
     */
    private Label mostDistractingLabel;

    /**
     * The label for displaying streak updates.
     */
    private Label streakUpdateLabel;

    /**
     * The button used to continue to the next screen.
     */
    private Button continueButton;

    // ===== STATE =====

    /**
     * The focus session associated with this summary.
     */
    private FocusSession focusSession;

    /**
     * The current streak count.
     */
    private int currentStreak;

    /**
     * The callback for handling panel events.
     */
    private SummaryCallback callback;

    // ===== CONSTRUCTOR =====

    /**
     * Initializes a new session summary panel for the specified focus session with the default dark theme.
     *
     * @param focusSession The completed focus session to summarize.
     */
    public SessionSummaryPanel(FocusSession focusSession) {
        this(focusSession, 0, new DarkTheme());
    }

    /**
     * Initializes a new session summary panel for the specified focus session with a custom theme.
     *
     * @param focusSession The completed focus session to summarize.
     * @param theme The theme providing the color palette.
     */
    public SessionSummaryPanel(FocusSession focusSession, Theme theme) {
        this(focusSession, 0, theme);
    }

    /**
     * Initializes a new session summary panel for the specified focus session, current streak, and custom theme.
     *
     * @param focusSession The completed focus session to summarize.
     * @param currentStreak The current streak count.
     * @param theme The theme providing the color palette.
     */
    public SessionSummaryPanel(FocusSession focusSession, int currentStreak, Theme theme) {
        super(theme);

        this.focusSession = focusSession;
        this.currentStreak = currentStreak;

        createComponents();
        layoutComponents();
        setupEventHandlers();
    }

    // ===== INITIALIZATION METHODS =====

    /**
     * Creates and initializes all UI components for the panel.
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
        if (score >= 95) {
             FontIcon starIcon = new FontIcon("fth-star");
             starIcon.setIconColor(scoreColor);
             scoreDescriptionLabel.setGraphic(starIcon);
             scoreDescriptionLabel.setGraphicTextGap(8);
        }

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
            mostDistractingLabel = new Label("No distractions - perfect focus!");
            mostDistractingLabel.setGraphic(new FontIcon("fth-target"));
            mostDistractingLabel.setGraphicTextGap(8);
        }
        mostDistractingLabel.setFont(UIConstants.getBodyFont());
        mostDistractingLabel.setTextFill(getTextSecondaryColor());

        // Streak update
        boolean qualifies = focusSession.qualifiesForStreak();
        if (qualifies) {
            streakUpdateLabel = new Label(String.format("Streak: %d day%s!", currentStreak, currentStreak == 1 ? "" : "s"));
            streakUpdateLabel.setTextFill(getSuccessColor());
            streakUpdateLabel.setGraphic(new FontIcon("fth-trending-up"));
            streakUpdateLabel.setGraphicTextGap(8);
        } else {
            streakUpdateLabel = new Label("Reach 80+ score and 30+ min to build your streak");
            streakUpdateLabel.setTextFill(getTextMutedColor());
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
     * Arranges the UI components in the panel layout.
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
     * Configures the event handlers for the UI components.
     */
    private void setupEventHandlers() {
        continueButton.setOnAction(event -> handleContinue());
    }

    // ===== EVENT HANDLERS =====

    /**
     * Handles the logic when the CONTINUE button is clicked.
     */
    private void handleContinue() {
        if (callback != null) {
            callback.onContinue();
        }
    }

    // ===== UTILITY METHODS =====

    /**
     * Retrieves the color associated with a given focus score.
     *
     * @param score The focus score ranging from 0 to 100.
     * @return The {@link Color} corresponding to the score.
     */
    private Color getScoreColor(int score) {
        if (score >= UIConstants.MIN_STREAK_SCORE) {
            return getSuccessColor(); // 80-100: Green
        } else if (score >= 50) {
            return getWarningColor(); // 50-79: Yellow
        } else {
            return getErrorColor(); // 0-49: Red
        }
    }

    /**
     * Retrieves the description text for a given focus score.
     *
     * @param score The focus score ranging from 0 to 100.
     * @return A {@link String} describing the focus level.
     */
    private String getScoreDescription(int score) {
        if (score >= 95) {
            return "Exceptional Focus!";
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
     * Sets the callback for handling panel events.
     *
     * @param callback The callback implementation to receive events.
     */
    public void setCallback(SummaryCallback callback) {
        this.callback = callback;
    }

    /**
     * Retrieves the focus session summarized by this panel.
     *
     * @return The {@link FocusSession} instance.
     */
    public FocusSession getFocusSession() {
        return focusSession;
    }
}
