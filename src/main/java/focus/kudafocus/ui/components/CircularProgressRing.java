package focus.kudafocus.ui.components;

import focus.kudafocus.ui.UIConstants;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;

/**
 * Represents a custom circular progress ring component for time selection and display.
 *
 * This component operates in two modes. In selection mode, the user can drag around the 
 * perimeter to select a duration from 0 to 180 minutes. In progress display mode, it 
 * shows the countdown progress during an active focus session.
 *
 * Visual design includes a background ring, a progress ring in the accent color, and a 
 * selection indicator. Interaction is handled via mouse events, with trigonometry used 
 * to convert mouse positions into angles that snap to the nearest minute.
 */
public class CircularProgressRing extends Pane {

    // ===== CONSTANTS =====

    /**
     * The diameter of the ring.
     */
    private final double diameter;

    /**
     * The radius of the ring.
     */
    private final double radius;

    /**
     * The center X coordinate of the ring.
     */
    private final double centerX;

    /**
     * The center Y coordinate of the ring.
     */
    private final double centerY;

    /**
     * The stroke width of the ring.
     */
    private final double strokeWidth;

    /**
     * The number of degrees per minute of duration.
     */
    private static final double DEGREES_PER_MINUTE = 2.0;

    /**
     * The maximum duration in minutes allowed by the component.
     */
    private static final int MAX_DURATION_MINUTES = UIConstants.MAX_DURATION_MINUTES;

    // ===== STATE FIELDS =====

    /**
     * The current selection angle in degrees, ranging from 0 to 360.
     */
    private double selectionAngle = 90.0; // Default to 45 minutes (90 degrees)

    /**
     * The progress value for the countdown display, ranging from 0.0 to 1.0.
     */
    private double progress = 1.0;

    /**
     * Indicates whether the ring is in selection mode or progress display mode.
     */
    private boolean selectionMode = true;

    /**
     * Indicates whether the selection angle should snap to the nearest minute.
     */
    private boolean snapToMinutes = true;

    /**
     * The listener for handling selection changes.
     */
    private SelectionChangeListener selectionChangeListener;

    /**
     * Provides a callback interface for handling selection change events.
     */
    public interface SelectionChangeListener {
        /**
         * Notifies when the selected duration in minutes has changed.
         *
         * @param minutes The new duration in minutes.
         */
        void onSelectionChanged(int minutes);
    }

    // ===== VISUAL COMPONENTS =====

    /**
     * The background arc of the ring.
     */
    private Arc backgroundRing;

    /**
     * The progress or selection arc of the ring.
     */
    private Arc progressRing;

    /**
     * The small circle indicating the current selection position.
     */
    private Circle selectionIndicator;

    // ===== CONSTRUCTOR =====

    /**
     * Initializes a new circular progress ring with the specified diameter.
     *
     * @param diameter The diameter of the ring in pixels.
     */
    public CircularProgressRing(double diameter) {
        this.diameter = diameter;
        this.radius = diameter / 2.0;
        this.centerX = radius;
        this.centerY = radius;
        this.strokeWidth = UIConstants.TIMER_RING_STROKE_WIDTH;

        // Set pane size
        this.setPrefSize(diameter, diameter);
        this.setMinSize(diameter, diameter);
        this.setMaxSize(diameter, diameter);

        // Create visual components
        createRings();
        createSelectionIndicator();

        // Set up mouse interaction for selection mode
        setupMouseHandlers();

        // Initial update
        updateVisuals();
    }

    // ===== INITIALIZATION METHODS =====

    /**
     * Creates and initializes the background and progress rings.
     */
    private void createRings() {
        // Background ring (full circle, dark)
        backgroundRing = new Arc();
        backgroundRing.setCenterX(centerX);
        backgroundRing.setCenterY(centerY);
        backgroundRing.setRadiusX(radius - strokeWidth / 2.0);
        backgroundRing.setRadiusY(radius - strokeWidth / 2.0);
        backgroundRing.setStartAngle(90.0); // Start at top (12 o'clock)
        backgroundRing.setLength(360.0); // Full circle
        backgroundRing.setType(ArcType.OPEN);
        backgroundRing.setFill(null);
        backgroundRing.setStroke(UIConstants.BACKGROUND_SECONDARY);
        backgroundRing.setStrokeWidth(strokeWidth);

        // Progress ring (partial arc, accent color)
        progressRing = new Arc();
        progressRing.setCenterX(centerX);
        progressRing.setCenterY(centerY);
        progressRing.setRadiusX(radius - strokeWidth / 2.0);
        progressRing.setRadiusY(radius - strokeWidth / 2.0);
        progressRing.setStartAngle(90.0); // Start at top (12 o'clock)
        progressRing.setLength(-selectionAngle); // Negative = clockwise
        progressRing.setType(ArcType.OPEN);
        progressRing.setFill(null);
        progressRing.setStroke(UIConstants.ACCENT_COLOR);
        progressRing.setStrokeWidth(strokeWidth);

        // Add to pane
        this.getChildren().addAll(backgroundRing, progressRing);
    }

    /**
     * Creates and initializes the selection indicator component.
     */
    private void createSelectionIndicator() {
        selectionIndicator = new Circle(8.0); // 8px radius
        selectionIndicator.setFill(UIConstants.ACCENT_COLOR);
        selectionIndicator.setStroke(UIConstants.TEXT_PRIMARY);
        selectionIndicator.setStrokeWidth(2.0);

        // Add to pane (initially visible)
        this.getChildren().add(selectionIndicator);
    }

    /**
     * Configures the mouse event handlers for user interaction in selection mode.
     */
    private void setupMouseHandlers() {
        // Mouse pressed - start drag
        this.setOnMousePressed(event -> {
            if (selectionMode) {
                updateAngleFromMouse(event.getX(), event.getY(), false);
            }
        });

        // Mouse dragged - update angle continuously
        this.setOnMouseDragged(event -> {
            if (selectionMode) {
                updateAngleFromMouse(event.getX(), event.getY(), false);
            }
        });

        // Mouse released - snap to nearest minute
        this.setOnMouseReleased(event -> {
            if (selectionMode && snapToMinutes) {
                updateAngleFromMouse(event.getX(), event.getY(), true);
            }
        });
    }

    // ===== INTERACTION METHODS =====

    /**
     * Updates the selection angle based on the provided mouse coordinates.
     *
     * @param mouseX The X coordinate of the mouse.
     * @param mouseY The Y coordinate of the mouse.
     * @param snap Indicates whether the angle should snap to the nearest minute increment.
     */
    private void updateAngleFromMouse(double mouseX, double mouseY, boolean snap) {
        // Calculate angle from center to mouse position
        // Math.atan2 returns angle in radians (-PI to PI)
        double dx = mouseX - centerX;
        double dy = mouseY - centerY;

        // Convert to degrees (0-360)
        // atan2 gives us: 0° = right (3 o'clock), increases counter-clockwise
        // We want: 0° = top (12 o'clock), increases clockwise
        double angleRad = Math.atan2(dy, dx);
        double angleDeg = Math.toDegrees(angleRad);

        // Adjust so 0° is at top and increases clockwise
        // Subtract 90° to rotate reference from right to top
        // Add 360 and modulo to ensure positive value
        angleDeg = (angleDeg + 90.0 + 360.0) % 360.0;

        // Snap to nearest minute if requested
        if (snap && snapToMinutes) {
            // Round to nearest multiple of DEGREES_PER_MINUTE
            angleDeg = Math.round(angleDeg / DEGREES_PER_MINUTE) * DEGREES_PER_MINUTE;
        }

        // Clamp to valid range (0-360)
        angleDeg = Math.max(0.0, Math.min(360.0, angleDeg));

        // Update selection angle
        setSelectionAngle(angleDeg);
    }

    /**
     * Updates the visual appearance of the component based on its current state.
     */
    private void updateVisuals() {
        if (selectionMode) {
            // Selection mode: show angle from selection
            progressRing.setLength(-selectionAngle); // Negative = clockwise

            // Update selection indicator position
            updateIndicatorPosition();
            selectionIndicator.setVisible(true);
        } else {
            // Progress mode: show remaining progress
            double angle = progress * 360.0;
            progressRing.setLength(-angle); // Negative = clockwise

            // Hide selection indicator in progress mode
            selectionIndicator.setVisible(false);
        }
    }

    /**
     * Updates the position of the selection indicator on the perimeter of the ring.
     */
    private void updateIndicatorPosition() {
        // Convert selection angle to position on circle perimeter
        // Remember: 0° = top, increases clockwise
        // For trig functions: 0° = right, so subtract 90°
        double angleRad = Math.toRadians(selectionAngle - 90.0);

        double indicatorX = centerX + Math.cos(angleRad) * (radius - strokeWidth / 2.0);
        double indicatorY = centerY + Math.sin(angleRad) * (radius - strokeWidth / 2.0);

        selectionIndicator.setCenterX(indicatorX);
        selectionIndicator.setCenterY(indicatorY);
    }

    // ===== PUBLIC METHODS =====

    /**
     * Sets the selection angle in degrees.
     *
     * @param angle The angle in degrees, ranging from 0 to 360.
     */
    public void setSelectionAngle(double angle) {
        this.selectionAngle = Math.max(0.0, Math.min(360.0, angle));
        updateVisuals();
        notifySelectionChanged();
    }

    /**
     * Sets the listener for selection change events.
     *
     * @param listener The listener to be notified when the selection changes.
     */
    public void setSelectionChangeListener(SelectionChangeListener listener) {
        this.selectionChangeListener = listener;
    }

    /**
     * Notifies the registered listener that the selection has changed.
     */
    private void notifySelectionChanged() {
        if (selectionChangeListener != null) {
            selectionChangeListener.onSelectionChanged(getSelectedMinutes());
        }
    }

    /**
     * Retrieves the current selection angle in degrees.
     *
     * @return The selection angle in degrees.
     */
    public double getSelectionAngle() {
        return selectionAngle;
    }

    /**
     * Sets the selected duration in minutes and updates the selection angle.
     *
     * @param minutes The duration in minutes, ranging from 0 to 180.
     */
    public void setSelectedMinutes(int minutes) {
        minutes = Math.max(0, Math.min(MAX_DURATION_MINUTES, minutes));
        setSelectionAngle(minutes * DEGREES_PER_MINUTE);
    }

    /**
     * Retrieves the selected duration in minutes.
     *
     * @return The duration in minutes.
     */
    public int getSelectedMinutes() {
        return (int) Math.round(selectionAngle / DEGREES_PER_MINUTE);
    }

    /**
     * Sets the progress value for the countdown display mode.
     *
     * @param progress The progress value from 0.0 to 1.0.
     */
    public void setProgress(double progress) {
        this.progress = Math.max(0.0, Math.min(1.0, progress));
        updateVisuals();
    }

    /**
     * Retrieves the current progress value.
     *
     * @return The progress value from 0.0 to 1.0.
     */
    public double getProgress() {
        return progress;
    }

    /**
     * Enables or disables selection mode.
     *
     * @param enabled {@code true} to enable selection mode; {@code false} for progress display mode.
     */
    public void setSelectionMode(boolean enabled) {
        this.selectionMode = enabled;

        // Enable/disable mouse interaction
        this.setMouseTransparent(!enabled);

        updateVisuals();
    }

    /**
     * Indicates whether the component is currently in selection mode.
     *
     * @return {@code true} if in selection mode; {@code false} otherwise.
     */
    public boolean isSelectionMode() {
        return selectionMode;
    }

    /**
     * Configures whether the selection angle should snap to the nearest minute.
     *
     * @param snap {@code true} to enable snapping; {@code false} to disable.
     */
    public void setSnapToMinutes(boolean snap) {
        this.snapToMinutes = snap;
    }

    /**
     * Sets the color of the progress ring.
     *
     * @param color The {@link Color} for the ring.
     */
    public void setRingColor(Color color) {
        progressRing.setStroke(color);
        selectionIndicator.setFill(color);
    }

    /**
     * Updates the ring colors to match a theme.
     *
     * @param backgroundRingColor The {@link Color} for the background ring track.
     * @param accentColor The {@link Color} for the progress arc and indicator fill.
     * @param textColor The {@link Color} for the indicator stroke.
     */
    public void setThemeColors(Color backgroundRingColor, Color accentColor, Color textColor) {
        backgroundRing.setStroke(backgroundRingColor);
        progressRing.setStroke(accentColor);
        selectionIndicator.setFill(accentColor);
        selectionIndicator.setStroke(textColor);
    }

    /**
     * Resets the ring to its initial state, which is 45 minutes in selection mode.
     */
    public void reset() {
        setSelectionMode(true);
        setSelectedMinutes(45); // Default 45 minutes
        setProgress(1.0);
    }
}
