package focus.kudafocus.ui;

import javafx.scene.paint.Color;

/**
 * Abstract theme class for KUDA FOCUS UI.
 *
 * Defines the color palette that all themes must provide.
 * Subclasses implement each accessor to supply a cohesive set of colors,
 * enabling runtime theme switching (e.g., dark mode vs. light mode).
 *
 * Subclasses: DarkTheme, LightTheme
 */
public abstract class Theme {

    /**
     * Primary background color for the main panel area
     *
     * @return Primary background color
     */
    public abstract Color getBackgroundPrimary();

    /**
     * Secondary background color for nested panels and cards
     *
     * @return Secondary background color
     */
    public abstract Color getBackgroundSecondary();

    /**
     * Accent color for interactive elements and highlights
     *
     * @return Accent color
     */
    public abstract Color getAccentColor();

    /**
     * Primary text color for headings and important content
     *
     * @return Primary text color
     */
    public abstract Color getTextPrimary();

    /**
     * Secondary text color for less prominent content
     *
     * @return Secondary text color
     */
    public abstract Color getTextSecondary();

    /**
     * Muted text color for hints and disabled content
     *
     * @return Muted text color
     */
    public abstract Color getTextMuted();

    /**
     * Success color for positive indicators (e.g., high focus scores)
     *
     * @return Success color
     */
    public abstract Color getSuccessColor();

    /**
     * Warning color for caution indicators (e.g., medium focus scores)
     *
     * @return Warning color
     */
    public abstract Color getWarningColor();

    /**
     * Error color for negative indicators (e.g., low focus scores)
     *
     * @return Error color
     */
    public abstract Color getErrorColor();

    /**
     * Overlay background color (typically semi-transparent)
     *
     * @return Overlay background color
     */
    public abstract Color getOverlayBackground();
}
