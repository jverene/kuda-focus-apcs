package focus.kudafocus.ui;

import javafx.scene.paint.Color;

/**
 * Represents the light mode theme for the KUDA FOCUS user interface.
 *
 * This theme provides a bright, high-contrast color palette for users who prefer
 * light backgrounds. It ensures optimal readability and maintains a clear visual
 * hierarchy throughout the application components.
 */
public class LightTheme extends Theme {

    /**
     * Constructs a new LightTheme instance.
     */
    public LightTheme() {
        super();
    }

    /**
     * Retrieves the primary background color for the light theme.
     *
     * @return The primary background color.
     */
    @Override
    public Color getBackgroundPrimary() {
        return Color.rgb(245, 245, 245);
    }

    /**
     * Retrieves the secondary background color for the light theme.
     *
     * @return The secondary background color.
     */
    @Override
    public Color getBackgroundSecondary() {
        return Color.rgb(255, 255, 255);
    }

    /**
     * Retrieves the accent color for the light theme.
     *
     * @return The accent color.
     */
    @Override
    public Color getAccentColor() {
        return Color.rgb(88, 166, 255);
    }

    /**
     * Retrieves the primary text color for the light theme.
     *
     * @return The primary text color.
     */
    @Override
    public Color getTextPrimary() {
        return Color.rgb(30, 30, 30);
    }

    /**
     * Retrieves the secondary text color for the light theme.
     *
     * @return The secondary text color.
     */
    @Override
    public Color getTextSecondary() {
        return Color.rgb(100, 100, 100);
    }

    /**
     * Retrieves the muted text color for the light theme.
     *
     * @return The muted text color.
     */
    @Override
    public Color getTextMuted() {
        return Color.rgb(180, 180, 180);
    }

    /**
     * Retrieves the success indicator color for the light theme.
     *
     * @return The success color.
     */
    @Override
    public Color getSuccessColor() {
        return Color.rgb(76, 217, 100);
    }

    /**
     * Retrieves the warning indicator color for the light theme.
     *
     * @return The warning color.
     */
    @Override
    public Color getWarningColor() {
        return Color.rgb(255, 204, 0);
    }

    /**
     * Retrieves the error indicator color for the light theme.
     *
     * @return The error color.
     */
    @Override
    public Color getErrorColor() {
        return Color.rgb(255, 59, 48);
    }

    /**
     * Retrieves the overlay background color for the light theme.
     *
     * @return The overlay background color.
     */
    @Override
    public Color getOverlayBackground() {
        return Color.rgb(255, 255, 255, 0.7);
    }
}
