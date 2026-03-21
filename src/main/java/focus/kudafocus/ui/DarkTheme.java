package focus.kudafocus.ui;

import javafx.scene.paint.Color;

/**
 * Represents the dark mode theme for the KUDA FOCUS user interface.
 *
 * This theme provides a dark, minimalist aesthetic designed to minimize distractions
 * during focus sessions. It utilizes color constants defined in {@link UIConstants}
 * to ensure consistency across the application.
 */
public class DarkTheme extends Theme {

    /**
     * Constructs a new DarkTheme instance.
     */
    public DarkTheme() {
        super();
    }

    /**
     * Retrieves the primary background color for the dark theme.
     *
     * @return The primary background color.
     */
    @Override
    public Color getBackgroundPrimary() {
        return UIConstants.BACKGROUND_PRIMARY;
    }

    /**
     * Retrieves the secondary background color for the dark theme.
     *
     * @return The secondary background color.
     */
    @Override
    public Color getBackgroundSecondary() {
        return UIConstants.BACKGROUND_SECONDARY;
    }

    /**
     * Retrieves the accent color for the dark theme.
     *
     * @return The accent color.
     */
    @Override
    public Color getAccentColor() {
        return UIConstants.ACCENT_COLOR;
    }

    /**
     * Retrieves the primary text color for the dark theme.
     *
     * @return The primary text color.
     */
    @Override
    public Color getTextPrimary() {
        return UIConstants.TEXT_PRIMARY;
    }

    /**
     * Retrieves the secondary text color for the dark theme.
     *
     * @return The secondary text color.
     */
    @Override
    public Color getTextSecondary() {
        return UIConstants.TEXT_SECONDARY;
    }

    /**
     * Retrieves the muted text color for the dark theme.
     *
     * @return The muted text color.
     */
    @Override
    public Color getTextMuted() {
        return UIConstants.TEXT_MUTED;
    }

    /**
     * Retrieves the success indicator color for the dark theme.
     *
     * @return The success color.
     */
    @Override
    public Color getSuccessColor() {
        return UIConstants.SUCCESS_COLOR;
    }

    /**
     * Retrieves the warning indicator color for the dark theme.
     *
     * @return The warning color.
     */
    @Override
    public Color getWarningColor() {
        return UIConstants.WARNING_COLOR;
    }

    /**
     * Retrieves the error indicator color for the dark theme.
     *
     * @return The error color.
     */
    @Override
    public Color getErrorColor() {
        return UIConstants.DANGER_COLOR;
    }

    /**
     * Retrieves the overlay background color for the dark theme.
     *
     * @return The overlay background color.
     */
    @Override
    public Color getOverlayBackground() {
        return UIConstants.OVERLAY_BACKGROUND;
    }
}
