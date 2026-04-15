package focus.kudafocus.ui;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Centralizes UI design constants for the KUDA FOCUS application.
 *
 * This class provides a single source for colors, fonts, sizing, and spacing to ensure 
 * a consistent visual experience throughout the user interface.
 */
public class UIConstants {

    // ===== COLOR SCHEME =====

    /**
     * The primary background color, providing a dark minimalist aesthetic.
     */
    public static final Color BACKGROUND_PRIMARY = Color.rgb(26, 26, 26);

    /**
     * The secondary background color, typically used for panels and cards.
     */
    public static final Color BACKGROUND_SECONDARY = Color.rgb(35, 35, 35);

    /**
     * The accent color used for interactive elements and the progress ring.
     */
    public static final Color ACCENT_COLOR = Color.rgb(88, 166, 255);

    /**
     * The primary text color.
     */
    public static final Color TEXT_PRIMARY = Color.rgb(255, 255, 255);

    /**
     * The secondary text color.
     */
    public static final Color TEXT_SECONDARY = Color.rgb(180, 180, 180);

    /**
     * The muted text color, used for less important information.
     */
    public static final Color TEXT_MUTED = Color.rgb(120, 120, 120);

    /**
     * The success color, used for high focus scores.
     */
    public static final Color SUCCESS_COLOR = Color.rgb(76, 217, 100);

    /**
     * The warning color, used for medium focus scores.
     */
    public static final Color WARNING_COLOR = Color.rgb(255, 204, 0);

    /**
     * The error/danger color, used for low focus scores.
     */
    public static final Color DANGER_COLOR = Color.rgb(255, 69, 58);

    /**
     * The overlay background color, which is semi-transparent.
     */
    public static final Color OVERLAY_BACKGROUND = Color.rgb(0, 0, 0, 0.7);

    // ===== TYPOGRAPHY =====

    /**
     * The title font size for large headings.
     */
    public static final double FONT_SIZE_TITLE = 48.0;

    /**
     * The large display font size, used for the timer.
     */
    public static final double FONT_SIZE_DISPLAY = 64.0;

    /**
     * The font size for headings.
     */
    public static final double FONT_SIZE_HEADING = 28.0;

    /**
     * The font size for body text.
     */
    public static final double FONT_SIZE_BODY = 16.0;

    /**
     * The font size for small labels and hints.
     */
    public static final double FONT_SIZE_SMALL = 14.0;

    /**
     * The font size for tiny text such as footnotes.
     */
    public static final double FONT_SIZE_TINY = 12.0;

    /**
     * Retrieves the display font used for the large timer display.
     *
     * @return The display {@link Font}.
     */
    public static Font getDisplayFont() {
        return Font.font("System", FontWeight.LIGHT, FONT_SIZE_DISPLAY);
    }

    /**
     * Retrieves the title font.
     *
     * @return The title {@link Font}.
     */
    public static Font getTitleFont() {
        return Font.font("System", FontWeight.BOLD, FONT_SIZE_TITLE);
    }

    /**
     * Retrieves the heading font.
     *
     * @return The heading {@link Font}.
     */
    public static Font getHeadingFont() {
        return Font.font("System", FontWeight.SEMI_BOLD, FONT_SIZE_HEADING);
    }

    /**
     * Retrieves the body font.
     *
     * @return The body {@link Font}.
     */
    public static Font getBodyFont() {
        return Font.font("System", FontWeight.NORMAL, FONT_SIZE_BODY);
    }

    /**
     * Retrieves the small font.
     *
     * @return The small {@link Font}.
     */
    public static Font getSmallFont() {
        return Font.font("System", FontWeight.NORMAL, FONT_SIZE_SMALL);
    }

    /**
     * Retrieves the tiny font.
     *
     * @return The tiny {@link Font}.
     */
    public static Font getTinyFont() {
        return Font.font("System", FontWeight.NORMAL, FONT_SIZE_TINY);
    }

    // ===== SIZING =====

    /**
     * The standard window width.
     */
    public static final double WINDOW_WIDTH = 800.0;

    /**
     * The standard window height.
     */
    public static final double WINDOW_HEIGHT = 600.0;

    /**
     * The diameter of the circular timer ring.
     */
    public static final double TIMER_RING_DIAMETER = 400.0;

    /**
     * The stroke width of the timer ring.
     */
    public static final double TIMER_RING_STROKE_WIDTH = 12.0;

    /**
     * The standard button height.
     */
    public static final double BUTTON_HEIGHT = 50.0;

    /**
     * The minimum button width.
     */
    public static final double BUTTON_MIN_WIDTH = 120.0;

    // ===== SPACING =====

    /**
     * Extra small spacing increment.
     */
    public static final double SPACING_XS = 4.0;

    /**
     * Small spacing increment.
     */
    public static final double SPACING_SM = 8.0;

    /**
     * Medium/standard spacing increment.
     */
    public static final double SPACING_MD = 16.0;

    /**
     * Large spacing increment.
     */
    public static final double SPACING_LG = 24.0;

    /**
     * Extra large spacing increment.
     */
    public static final double SPACING_XL = 32.0;

    /**
     * The standard padding size.
     */
    public static final double PADDING_STANDARD = 20.0;

    // ===== TIMING =====

    /**
     * The maximum focus session duration in minutes (3 hours).
     */
    public static final int MAX_DURATION_MINUTES = 180;

    /**
     * The minimum duration required for a session to qualify for a streak.
     */
    public static final int MIN_STREAK_DURATION_MINUTES = 30;

    /**
     * The minimum focus score required to qualify for a streak.
     */
    public static final int MIN_STREAK_SCORE = 80;

    /**
     * The interval at which the overlay reappears.
     */
    public static final int OVERLAY_REAPPEAR_SECONDS = 15;

    /**
     * The interval for timer updates in milliseconds.
     */
    public static final int TIMER_UPDATE_INTERVAL_MS = 100;

    /**
     * The interval for process monitoring in milliseconds.
     */
    public static final int MONITORING_INTERVAL_MS = 2000;

    // ===== FOCUS SCORE CONSTANTS =====

    /**
     * The base focus score from which penalties are subtracted.
     */
    public static final int SCORE_BASE = 100;

    /**
     * The penalty points deducted for each violation.
     */
    public static final int SCORE_VIOLATION_PENALTY = 5;

    /**
     * The penalty points deducted for each overlay dismissal.
     */
    public static final int SCORE_DISMISSAL_PENALTY = 2;

    /**
     * The penalty points deducted for each minute spent on blocked apps.
     */
    public static final int SCORE_TIME_PENALTY_PER_MINUTE = 1;

    // ===== ANIMATION =====

    /**
     * The standard animation duration in milliseconds.
     */
    public static final int ANIMATION_DURATION_MS = 300;

    /**
     * The duration for fast animations in milliseconds.
     */
    public static final int ANIMATION_FAST_MS = 150;

    /**
     * The duration for slow animations in milliseconds.
     */
    public static final int ANIMATION_SLOW_MS = 600;

    /**
     * Applies a smooth scale animation effect to buttons on hover and press.
     *
     * @param button The button to animate.
     */
    public static void setupButtonAnimation(javafx.scene.control.Button button) {
        javafx.animation.TranslateTransition translateTransition = new javafx.animation.TranslateTransition(javafx.util.Duration.millis(120), button);
        // We use integer values for offsets strictly to avoid subpixel text "smearing" or "jittering".
        // No effects (like DropShadow) or Opacity changes, as those disable JavaFX's LCD subpixel anti-aliasing for Text.

        button.setOnMouseEntered(e -> {
            translateTransition.stop();
            translateTransition.setDuration(javafx.util.Duration.millis(120));
            translateTransition.setToY(-2); // Lift up exactly 2 integer pixels
            translateTransition.play();
        });

        button.setOnMouseExited(e -> {
            translateTransition.stop();
            translateTransition.setDuration(javafx.util.Duration.millis(120));
            translateTransition.setToY(0); // Return to baseline exactly
            translateTransition.play();
        });

        button.setOnMousePressed(e -> {
            translateTransition.stop();
            // Push button down
            translateTransition.setDuration(javafx.util.Duration.millis(50));
            translateTransition.setToY(1); // slightly depressed
            translateTransition.play();
        });

        button.setOnMouseReleased(e -> {
            translateTransition.stop();
            translateTransition.setDuration(javafx.util.Duration.millis(120));
            if (button.isHover()) {
                translateTransition.setToY(-2);
            } else {
                translateTransition.setToY(0);
            }
            translateTransition.play();
        });
    }

    /**
     * Private constructor to prevent instantiation.
     *
     * @throws AssertionError if instantiation is attempted.
     */
    private UIConstants() {
        throw new AssertionError("Cannot instantiate UIConstants");
    }
}
