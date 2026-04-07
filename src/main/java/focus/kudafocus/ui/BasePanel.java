package focus.kudafocus.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Represents the base panel class providing common styling and layout properties for all UI panels.
 *
 * This abstract class serves as the foundation for all specialized panels in the KUDA FOCUS
 * application, ensuring a consistent visual design and behavior. It demonstrates the use of
 * inheritance to facilitate code reuse and maintainability across the UI layer.
 */
public abstract class BasePanel extends VBox {

    // ===== THEME =====

    /**
     * The active theme providing the color palette for the panel.
     */
    protected Theme theme;

    // ===== COLOR PROPERTIES =====

    /**
     * The primary background color for the panel.
     */
    protected Color primaryColor;

    /**
     * The accent color for interactive elements within the panel.
     */
    protected Color accentColor;

    /**
     * The primary text color used for headings and prominent content.
     */
    protected Color textPrimaryColor;

    /**
     * The secondary text color used for less prominent text.
     */
    protected Color textSecondaryColor;

    /**
     * The muted text color used for hints and disabled content.
     */
    protected Color textMutedColor;

    /**
     * The color indicating success or positive status.
     */
    protected Color successColor;

    /**
     * The color indicating a warning or caution state.
     */
    protected Color warningColor;

    /**
     * The color indicating an error or negative status.
     */
    protected Color errorColor;

    /**
     * The semi-transparent color used for overlay backgrounds.
     */
    protected Color overlayBackgroundColor;

    // ===== TYPOGRAPHY PROPERTIES =====

    /**
     * The font used for panel titles and headings.
     */
    protected Font titleFont;

    /**
     * The font used for regular body text.
     */
    protected Font bodyFont;

    // ===== LAYOUT PROPERTIES =====

    /**
     * The standard padding amount in pixels applied to the panel.
     */
    protected double standardPadding;

    /**
     * The standard spacing in pixels between elements within the panel.
     */
    protected double standardSpacing;

    // ===== CONSTRUCTORS =====

    /**
     * Constructs a new BasePanel using the default dark theme.
     */
    public BasePanel() {
        this(new DarkTheme());
    }

    /**
     * Constructs a new BasePanel with the specified theme.
     *
     * @param theme The theme providing the color palette and visual properties.
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
     * Initializes the color scheme based on the active theme.
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
     * Initializes the typography settings using defined constants.
     */
    private void initializeTypography() {
        this.titleFont = UIConstants.getTitleFont();
        this.bodyFont = UIConstants.getBodyFont();
    }

    /**
     * Initializes the layout spacing and padding using defined constants.
     */
    private void initializeSpacing() {
        this.standardPadding = UIConstants.PADDING_STANDARD;
        this.standardSpacing = UIConstants.SPACING_MD;
    }

    // ===== SHARED STYLING METHODS =====

    /**
     * Applies standard styling to the panel, including background, spacing, and alignment.
     */
    protected void applyStandardStyling() {
        // Set background color
        this.setStyle("-fx-background-color: " + toRGBCode(primaryColor) + ";" +
                      "-fx-base: " + toRGBCode(primaryColor) + ";" +
                      "-fx-control-inner-background: " + toRGBCode(getTheme().getBackgroundSecondary()) + ";" +
                      "-fx-text-background-color: " + toRGBCode(getTextPrimaryColor()) + ";");

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
     * Converts a JavaFX Color to a CSS-compatible RGB string.
     *
     * @param color The Color to convert.
     * @return A CSS RGB string representation.
     */
    protected String toRGBCode(Color color) {
        return String.format("rgb(%d, %d, %d)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    /**
     * Converts a JavaFX Color to a CSS-compatible RGBA string with an alpha channel.
     *
     * @param color The Color to convert.
     * @return A CSS RGBA string representation.
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
     * Retrieves the active theme used by the panel.
     *
     * @return The current theme.
     */
    protected Theme getTheme() {
        return theme;
    }

    /**
     * Retrieves the primary background color.
     *
     * @return The primary background color.
     */
    protected Color getPrimaryColor() {
        return primaryColor;
    }

    /**
     * Retrieves the accent color used for interactive elements.
     *
     * @return The accent color.
     */
    protected Color getAccentColor() {
        return accentColor;
    }

    /**
     * Retrieves the primary text color.
     *
     * @return The primary text color.
     */
    protected Color getTextPrimaryColor() {
        return textPrimaryColor;
    }

    /**
     * Retrieves the secondary text color.
     *
     * @return The secondary text color.
     */
    protected Color getTextSecondaryColor() {
        return textSecondaryColor;
    }

    /**
     * Retrieves the muted text color.
     *
     * @return The muted text color.
     */
    protected Color getTextMutedColor() {
        return textMutedColor;
    }

    /**
     * Retrieves the color indicating success.
     *
     * @return The success color.
     */
    protected Color getSuccessColor() {
        return successColor;
    }

    /**
     * Retrieves the color indicating a warning.
     *
     * @return The warning color.
     */
    protected Color getWarningColor() {
        return warningColor;
    }

    /**
     * Retrieves the color indicating an error.
     *
     * @return The error color.
     */
    protected Color getErrorColor() {
        return errorColor;
    }

    /**
     * Retrieves the color used for overlay backgrounds.
     *
     * @return The overlay background color.
     */
    protected Color getOverlayBackgroundColor() {
        return overlayBackgroundColor;
    }

    /**
     * Retrieves the font used for panel titles.
     *
     * @return The title font.
     */
    protected Font getTitleFont() {
        return titleFont;
    }

    /**
     * Retrieves the font used for body text.
     *
     * @return The body font.
     */
    protected Font getBodyFont() {
        return bodyFont;
    }

    /**
     * Retrieves the standard padding amount used by the panel.
     *
     * @return The standard padding in pixels.
     */
    protected double getStandardPadding() {
        return standardPadding;
    }

    /**
     * Retrieves the standard spacing amount used by the panel.
     *
     * @return The standard spacing in pixels.
     */
    protected double getStandardSpacing() {
        return standardSpacing;
    }
}
