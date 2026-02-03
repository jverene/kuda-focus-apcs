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

    // ===== SHARED GUI PROPERTIES =====
    // These are 'protected' so subclasses can access them

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
     * Title font for headings
     */
    protected Font titleFont;

    /**
     * Body font for regular text
     */
    protected Font bodyFont;

    /**
     * Standard padding amount
     */
    protected double standardPadding;

    /**
     * Standard spacing between elements
     */
    protected double standardSpacing;

    // ===== CONSTRUCTOR =====

    /**
     * Creates a new BasePanel with standard KUDA FOCUS styling.
     * All subclasses must call this constructor (implicitly or explicitly)
     * to initialize the shared visual properties.
     */
    public BasePanel() {
        super();
        initializeColors();
        initializeTypography();
        initializeSpacing();
        applyStandardStyling();
    }

    // ===== INITIALIZATION METHODS =====

    /**
     * Initialize color scheme from UIConstants
     */
    private void initializeColors() {
        this.primaryColor = UIConstants.BACKGROUND_PRIMARY;
        this.accentColor = UIConstants.ACCENT_COLOR;
        this.textPrimaryColor = UIConstants.TEXT_PRIMARY;
        this.textSecondaryColor = UIConstants.TEXT_SECONDARY;
    }

    /**
     * Initialize typography from UIConstants
     */
    private void initializeTypography() {
        this.titleFont = UIConstants.getTitleFont();
        this.bodyFont = UIConstants.getBodyFont();
    }

    /**
     * Initialize spacing and padding from UIConstants
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

    // ===== GETTERS FOR SUBCLASSES =====

    /**
     * Get the primary background color
     *
     * @return Primary color
     */
    protected Color getPrimaryColor() {
        return primaryColor;
    }

    /**
     * Get the accent color
     *
     * @return Accent color
     */
    protected Color getAccentColor() {
        return accentColor;
    }

    /**
     * Get the primary text color
     *
     * @return Primary text color
     */
    protected Color getTextPrimaryColor() {
        return textPrimaryColor;
    }

    /**
     * Get the secondary text color
     *
     * @return Secondary text color
     */
    protected Color getTextSecondaryColor() {
        return textSecondaryColor;
    }

    /**
     * Get the title font
     *
     * @return Title font
     */
    protected Font getTitleFont() {
        return titleFont;
    }

    /**
     * Get the body font
     *
     * @return Body font
     */
    protected Font getBodyFont() {
        return bodyFont;
    }

    /**
     * Get standard padding amount
     *
     * @return Standard padding in pixels
     */
    protected double getStandardPadding() {
        return standardPadding;
    }

    /**
     * Get standard spacing amount
     *
     * @return Standard spacing in pixels
     */
    protected double getStandardSpacing() {
        return standardSpacing;
    }
}
