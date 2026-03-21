package focus.kudafocus.ui;

import javafx.scene.paint.Color;

/**
 * Represents an abstract theme for the KUDA FOCUS user interface.
 *
 * Defines the color palette that all themes must provide. Subclasses implement each 
 * accessor to supply a cohesive set of colors, enabling runtime theme switching 
 * between different visual styles such as dark mode and light mode.
 */
public abstract class Theme {

    /**
     * Retrieves the primary background color for the main panel area.
     *
     * @return The primary background {@link Color}.
     */
    public abstract Color getBackgroundPrimary();

    /**
     * Retrieves the secondary background color for nested panels and cards.
     *
     * @return The secondary background {@link Color}.
     */
    public abstract Color getBackgroundSecondary();

    /**
     * Retrieves the accent color used for interactive elements and highlights.
     *
     * @return The accent {@link Color}.
     */
    public abstract Color getAccentColor();

    /**
     * Retrieves the primary text color for headings and important content.
     *
     * @return The primary text {@link Color}.
     */
    public abstract Color getTextPrimary();

    /**
     * Retrieves the secondary text color for less prominent content.
     *
     * @return The secondary text {@link Color}.
     */
    public abstract Color getTextSecondary();

    /**
     * Retrieves the muted text color for hints and disabled content.
     *
     * @return The muted text {@link Color}.
     */
    public abstract Color getTextMuted();

    /**
     * Retrieves the success color for positive indicators, such as high focus scores.
     *
     * @return The success {@link Color}.
     */
    public abstract Color getSuccessColor();

    /**
     * Retrieves the warning color for caution indicators, such as medium focus scores.
     *
     * @return The warning {@link Color}.
     */
    public abstract Color getWarningColor();

    /**
     * Retrieves the error color for negative indicators, such as low focus scores.
     *
     * @return The error {@link Color}.
     */
    public abstract Color getErrorColor();

    /**
     * Retrieves the overlay background color, which is typically semi-transparent.
     *
     * @return The overlay background {@link Color}.
     */
    public abstract Color getOverlayBackground();
}
