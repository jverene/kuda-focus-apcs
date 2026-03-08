package focus.kudafocus.ui;

import javafx.scene.paint.Color;

/**
 * Dark mode theme for KUDA FOCUS UI.
 *
 * Default theme using colors from UIConstants.
 * Provides a dark, minimalist aesthetic suited for focus sessions.
 */
public class DarkTheme extends Theme {

    /** {@inheritDoc} */
    @Override
    public Color getBackgroundPrimary() {
        return UIConstants.BACKGROUND_PRIMARY;
    }

    /** {@inheritDoc} */
    @Override
    public Color getBackgroundSecondary() {
        return UIConstants.BACKGROUND_SECONDARY;
    }

    /** {@inheritDoc} */
    @Override
    public Color getAccentColor() {
        return UIConstants.ACCENT_COLOR;
    }

    /** {@inheritDoc} */
    @Override
    public Color getTextPrimary() {
        return UIConstants.TEXT_PRIMARY;
    }

    /** {@inheritDoc} */
    @Override
    public Color getTextSecondary() {
        return UIConstants.TEXT_SECONDARY;
    }

    /** {@inheritDoc} */
    @Override
    public Color getTextMuted() {
        return UIConstants.TEXT_MUTED;
    }

    /** {@inheritDoc} */
    @Override
    public Color getSuccessColor() {
        return UIConstants.SUCCESS_COLOR;
    }

    /** {@inheritDoc} */
    @Override
    public Color getWarningColor() {
        return UIConstants.WARNING_COLOR;
    }

    /** {@inheritDoc} */
    @Override
    public Color getErrorColor() {
        return UIConstants.DANGER_COLOR;
    }

    /** {@inheritDoc} */
    @Override
    public Color getOverlayBackground() {
        return UIConstants.OVERLAY_BACKGROUND;
    }
}
