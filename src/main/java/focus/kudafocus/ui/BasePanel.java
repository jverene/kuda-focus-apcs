package focus.kudafocus.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Base panel class demonstrating INHERITANCE in OOP.
 *
 * All UI panels in KUDA FOCUS extend this base class to inherit
 * common styling properties and methods. This ensures consistent
 * visual design across the application while allowing specialized
 * panels to add their own unique functionality.
 *
 * Inheritance Benefits:
 * - Code reuse: Common styling logic written once
 * - Consistency: All panels share same design language
 * - Maintainability: Changes to base styling affect all panels
 * - Extensibility: Easy to add new panel types
 *
 * Subclasses: CircularTimerPanel, DashboardPanel, SessionSummaryPanel, etc.
 */
public abstract class BasePanel extends VBox {

    // ===== THEME =====

    /**
     * Active theme providing the color palette
     */
    protected Theme theme;

    // ===== COLOR PROPERTIES =====

    /**
     * Primary background color for the panel
     */
    protected Color primaryColor;

    /**
     * Accent color for interactive elements
     */
    protected Color accentColor;

    /**
     * Primary text color
     */
    protected Color textPrimaryColor;

    /**
     * Secondary text color (for less prominent text)
     */
    protected Color textSecondaryColor;

    /**
     * Muted text color (for hints and disabled content)
     */
    protected Color textMutedColor;

    /**
     * Success color (for positive indicators)
     */
    protected Color successColor;

    /**
     * Warning color (for caution indicators)
     */
    protected Color warningColor;

    /**
     * Error color (for negative indicators)
     */
    protected Color errorColor;

    /**
     * Overlay background color (semi-transparent)
     */
    protected Color overlayBackgroundColor;

    // ===== TYPOGRAPHY PROPERTIES =====

    /**
     * Title font for headings
     */
    protected Font titleFont;

    /**
     * Body font for regular text
     */
    protected Font bodyFont;

    // ===== LAYOUT PROPERTIES =====

    /**
     * Standard padding amount
     */
    protected double standardPadding;

    /**
     * Standard spacing between elements
     */
    protected double standardSpacing;

    // ===== CONSTRUCTORS =====

    /**
     * Creates a new BasePanel with the default dark theme.
     * Delegates to {@link #BasePanel(Theme)} with a {@link DarkTheme}.
     */
    public BasePanel() {
        this(new DarkTheme());
    }

    /**
     * Creates a new BasePanel with the given theme.
     * Initializes all shared visual properties from the theme and UIConstants.
     *
     * @param theme Theme providing the color palette
     */
    public BasePanel(Theme theme) {
        super();
        this.theme = theme;
        initializeColors();
        initializeTypography();
        initializeSpacing();
        applyStandardStyling();
    }

    // ===== INITIALIZATION METHODS =====

    /**
     * Initializes the color scheme from the active theme
     */
    private void initializeColors() {
        this.primaryColor = theme.getBackgroundPrimary();
        this.accentColor = theme.getAccentColor();
        this.textPrimaryColor = theme.getTextPrimary();
        this.textSecondaryColor = theme.getTextSecondary();
        this.textMutedColor = theme.getTextMuted();
        this.successColor = theme.getSuccessColor();
        this.warningColor = theme.getWarningColor();
        this.errorColor = theme.getErrorColor();
        this.overlayBackgroundColor = theme.getOverlayBackground();
    }

    /**
     * Initializes typography from UIConstants
     */
    private void initializeTypography() {
        this.titleFont = UIConstants.getTitleFont();
        this.bodyFont = UIConstants.getBodyFont();
    }

    /**
     * Initializes spacing and padding from UIConstants
     */
    private void initializeSpacing() {
        this.standardPadding = UIConstants.PADDING_STANDARD;
        this.standardSpacing = UIConstants.SPACING_MD;
    }

    // ===== SHARED STYLING METHODS =====

    /**
     * Applies standard styling to this panel.
     * This method is called automatically during construction.
     * Subclasses can override to customize, but should call super.applyStandardStyling()
     * to maintain base styling.
     */
    protected void applyStandardStyling() {
        // Set background color
        this.setStyle("-fx-background-color: " + toRGBCode(primaryColor) + ";");

        // Set default spacing between child nodes
        this.setSpacing(standardSpacing);

        // Set default padding
        this.setPadding(new Insets(standardPadding));

        // Center align by default
        this.setAlignment(Pos.CENTER);

        // Fill available width
        this.setFillWidth(true);
    }

    /**
     * Converts JavaFX Color to CSS RGB code
     *
     * @param color The Color to convert
     * @return CSS RGB string (e.g., "rgb(26, 26, 26)")
     */
    protected String toRGBCode(Color color) {
        return String.format("rgb(%d, %d, %d)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    /**
     * Converts JavaFX Color to CSS RGBA code with alpha channel
     *
     * @param color The Color to convert
     * @return CSS RGBA string (e.g., "rgba(0, 0, 0, 0.7)")
     */
    protected String toRGBACode(Color color) {
        return String.format("rgba(%d, %d, %d, %.2f)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255),
                color.getOpacity());
    }

    // ===== GETTERS =====

    /**
     * Gets the active theme
     *
     * @return Active theme
     */
    protected Theme getTheme() {
        return theme;
    }

    /**
     * Gets the primary background color
     *
     * @return Primary color
     */
    protected Color getPrimaryColor() {
        return primaryColor;
    }

    /**
     * Gets the accent color
     *
     * @return Accent color
     */
    protected Color getAccentColor() {
        return accentColor;
    }

    /**
     * Gets the primary text color
     *
     * @return Primary text color
     */
    protected Color getTextPrimaryColor() {
        return textPrimaryColor;
    }

    /**
     * Gets the secondary text color
     *
     * @return Secondary text color
     */
    protected Color getTextSecondaryColor() {
        return textSecondaryColor;
    }

    /**
     * Gets the muted text color
     *
     * @return Muted text color
     */
    protected Color getTextMutedColor() {
        return textMutedColor;
    }

    /**
     * Gets the success color
     *
     * @return Success color
     */
    protected Color getSuccessColor() {
        return successColor;
    }

    /**
     * Gets the warning color
     *
     * @return Warning color
     */
    protected Color getWarningColor() {
        return warningColor;
    }

    /**
     * Gets the error color
     *
     * @return Error color
     */
    protected Color getErrorColor() {
        return errorColor;
    }

    /**
     * Gets the overlay background color
     *
     * @return Overlay background color
     */
    protected Color getOverlayBackgroundColor() {
        return overlayBackgroundColor;
    }

    /**
     * Gets the title font
     *
     * @return Title font
     */
    protected Font getTitleFont() {
        return titleFont;
    }

    /**
     * Gets the body font
     *
     * @return Body font
     */
    protected Font getBodyFont() {
        return bodyFont;
    }

    /**
     * Gets the standard padding amount
     *
     * @return Standard padding in pixels
     */
    protected double getStandardPadding() {
        return standardPadding;
    }

    /**
     * Gets the standard spacing amount
     *
     * @return Standard spacing in pixels
     */
    protected double getStandardSpacing() {
        return standardSpacing;
    }
}
