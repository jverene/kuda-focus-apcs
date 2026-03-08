package focus.kudafocus.ui;

import javafx.scene.paint.Color;

/**
 * Light mode theme for KUDA FOCUS UI.
 *
 * Provides a bright color palette for users who prefer light backgrounds.
 * All colors are chosen to maintain readability and visual hierarchy.
 */
public class LightTheme extends Theme {

    /** {@inheritDoc} */
    @Override
    public Color getBackgroundPrimary() {
        return Color.rgb(245, 245, 245);
    }

    /** {@inheritDoc} */
    @Override
    public Color getBackgroundSecondary() {
        return Color.rgb(255, 255, 255);
    }

    /** {@inheritDoc} */
    @Override
    public Color getAccentColor() {
        return Color.rgb(88, 166, 255);
    }

    /** {@inheritDoc} */
    @Override
    public Color getTextPrimary() {
        return Color.rgb(30, 30, 30);
    }

    /** {@inheritDoc} */
    @Override
    public Color getTextSecondary() {
        return Color.rgb(100, 100, 100);
    }

    /** {@inheritDoc} */
    @Override
    public Color getTextMuted() {
        return Color.rgb(180, 180, 180);
    }

    /** {@inheritDoc} */
    @Override
    public Color getSuccessColor() {
        return Color.rgb(76, 217, 100);
    }

    /** {@inheritDoc} */
    @Override
    public Color getWarningColor() {
        return Color.rgb(255, 204, 0);
    }

    /** {@inheritDoc} */
    @Override
    public Color getErrorColor() {
        return Color.rgb(255, 59, 48);
    }

    /** {@inheritDoc} */
    @Override
    public Color getOverlayBackground() {
        return Color.rgb(255, 255, 255, 0.7);
    }
}
