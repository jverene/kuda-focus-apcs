package focus.kudafocus.ui;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * UI design constants for KUDA FOCUS application.
 * Centralizes all colors, fonts, sizes, and spacing for consistent minimalist design.
 */
public class UIConstants {

    // ===== COLOR SCHEME =====

    /**
     * Primary background color - dark for minimalist aesthetic
     */
    public static final Color BACKGROUND_PRIMARY = Color.rgb(26, 26, 26);

    /**
     * Secondary background color - slightly lighter for panels
     */
    public static final Color BACKGROUND_SECONDARY = Color.rgb(35, 35, 35);

    /**
     * Accent color for interactive elements and progress ring
     */
    public static final Color ACCENT_COLOR = Color.rgb(88, 166, 255);

    /**
     * Text color - primary (white)
     */
    public static final Color TEXT_PRIMARY = Color.rgb(255, 255, 255);

    /**
     * Text color - secondary (light gray)
     */
    public static final Color TEXT_SECONDARY = Color.rgb(180, 180, 180);

    /**
     * Text color - muted (dark gray for less important info)
     */
    public static final Color TEXT_MUTED = Color.rgb(120, 120, 120);

    /**
     * Success color (for high focus scores)
     */
    public static final Color SUCCESS_COLOR = Color.rgb(76, 217, 100);

    /**
     * Warning color (for medium focus scores)
     */
    public static final Color WARNING_COLOR = Color.rgb(255, 204, 0);

    /**
     * Error/danger color (for low focus scores)
     */
    public static final Color DANGER_COLOR = Color.rgb(255, 69, 58);

    /**
     * Overlay background color (semi-transparent dark)
     */
    public static final Color OVERLAY_BACKGROUND = Color.rgb(0, 0, 0, 0.7);

    // ===== TYPOGRAPHY =====

    /**
     * Title font size (large headings)
     */
    public static final double FONT_SIZE_TITLE = 48.0;

    /**
     * Large display font size (for timer display)
     */
    public static final double FONT_SIZE_DISPLAY = 64.0;

    /**
     * Heading font size
     */
    public static final double FONT_SIZE_HEADING = 28.0;

    /**
     * Body text font size
     */
    public static final double FONT_SIZE_BODY = 16.0;

    /**
     * Small text font size (labels, hints)
     */
    public static final double FONT_SIZE_SMALL = 14.0;

    /**
     * Tiny text font size (footnotes)
     */
    public static final double FONT_SIZE_TINY = 12.0;

    /**
     * Get display font (for large timer numbers)
     */
    public static Font getDisplayFont() {
        return Font.font("System", FontWeight.LIGHT, FONT_SIZE_DISPLAY);
    }

    /**
     * Get title font
     */
    public static Font getTitleFont() {
        return Font.font("System", FontWeight.BOLD, FONT_SIZE_TITLE);
    }

    /**
     * Get heading font
     */
    public static Font getHeadingFont() {
        return Font.font("System", FontWeight.SEMI_BOLD, FONT_SIZE_HEADING);
    }

    /**
     * Get body font
     */
    public static Font getBodyFont() {
        return Font.font("System", FontWeight.NORMAL, FONT_SIZE_BODY);
    }

    /**
     * Get small font
     */
    public static Font getSmallFont() {
        return Font.font("System", FontWeight.NORMAL, FONT_SIZE_SMALL);
    }

    /**
     * Get tiny font
     */
    public static Font getTinyFont() {
        return Font.font("System", FontWeight.NORMAL, FONT_SIZE_TINY);
    }

    // ===== SIZING =====

    /**
     * Standard window width
     */
    public static final double WINDOW_WIDTH = 800.0;

    /**
     * Standard window height
     */
    public static final double WINDOW_HEIGHT = 600.0;

    /**
     * Circular timer ring diameter
     */
    public static final double TIMER_RING_DIAMETER = 400.0;

    /**
     * Timer ring stroke width
     */
    public static final double TIMER_RING_STROKE_WIDTH = 12.0;

    /**
     * Button height
     */
    public static final double BUTTON_HEIGHT = 50.0;

    /**
     * Button minimum width
     */
    public static final double BUTTON_MIN_WIDTH = 120.0;

    // ===== SPACING =====

    /**
     * Extra small spacing
     */
    public static final double SPACING_XS = 4.0;

    /**
     * Small spacing
     */
    public static final double SPACING_SM = 8.0;

    /**
     * Medium spacing (standard)
     */
    public static final double SPACING_MD = 16.0;

    /**
     * Large spacing
     */
    public static final double SPACING_LG = 24.0;

    /**
     * Extra large spacing
     */
    public static final double SPACING_XL = 32.0;

    /**
     * Standard padding
     */
    public static final double PADDING_STANDARD = 20.0;

    // ===== TIMING =====

    /**
     * Maximum focus session duration in minutes (3 hours)
     */
    public static final int MAX_DURATION_MINUTES = 180;

    /**
     * Minimum qualifying session duration for streaks (30 minutes)
     */
    public static final int MIN_STREAK_DURATION_MINUTES = 30;

    /**
     * Minimum focus score for streak qualification
     */
    public static final int MIN_STREAK_SCORE = 80;

    /**
     * Overlay reappearance interval in seconds
     */
    public static final int OVERLAY_REAPPEAR_SECONDS = 15;

    /**
     * Timer update interval in milliseconds
     */
    public static final int TIMER_UPDATE_INTERVAL_MS = 100;

    /**
     * Process monitoring interval in milliseconds
     */
    public static final int MONITORING_INTERVAL_MS = 2000;

    // ===== FOCUS SCORE CONSTANTS =====

    /**
     * Base focus score
     */
    public static final int SCORE_BASE = 100;

    /**
     * Penalty per violation occurrence
     */
    public static final int SCORE_VIOLATION_PENALTY = 5;

    /**
     * Penalty per overlay dismissal
     */
    public static final int SCORE_DISMISSAL_PENALTY = 2;

    /**
     * Penalty per minute spent on blocked apps
     */
    public static final int SCORE_TIME_PENALTY_PER_MINUTE = 1;

    // ===== ANIMATION =====

    /**
     * Standard animation duration in milliseconds
     */
    public static final int ANIMATION_DURATION_MS = 300;

    /**
     * Fast animation duration in milliseconds
     */
    public static final int ANIMATION_FAST_MS = 150;

    /**
     * Slow animation duration in milliseconds
     */
    public static final int ANIMATION_SLOW_MS = 600;

    // Private constructor to prevent instantiation
    private UIConstants() {
        throw new AssertionError("Cannot instantiate UIConstants");
    }
}
