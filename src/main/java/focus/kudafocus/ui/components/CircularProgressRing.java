package focus.kudafocus.ui.components;

import focus.kudafocus.ui.UIConstants;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;

/**
 * Custom circular progress ring component for time selection and display.
 *
 * This component serves two purposes:
 * 1. TIME SELECTION MODE: User drags around the perimeter to select duration (0-180 min)
 * 2. PROGRESS DISPLAY MODE: Shows countdown progress during active session
 *
 * Visual Design:
 * - Background ring (dark gray) - full circle
 * - Progress ring (accent color) - partial arc based on progress/selection
 * - Selection indicator (small circle) - shows drag position in selection mode
 *
 * Interaction:
 * - Mouse drag around perimeter updates selection angle
 * - Angle snaps to nearest minute for clean UX (6 degrees per minute)
 * - 0 degrees = 12 o'clock (top), increases clockwise
 * - 360 degrees (full circle) = 180 minutes (max duration)
 *
 * Key Concepts (for APCS):
 * - Custom JavaFX components (extending Pane)
 * - Mouse event handling
 * - Trigonometry (converting mouse position to angle)
 * - Properties and data binding
 */
public class CircularProgressRing extends Pane {

    // ===== CONSTANTS =====

    /**
     * Diameter of the ring
     */
    private final double diameter;

    /**
     * Radius of the ring (half diameter)
     */
    private final double radius;

    /**
     * Center X coordinate
     */
    private final double centerX;

    /**
     * Center Y coordinate
     */
    private final double centerY;

    /**
     * Stroke width of the ring
     */
    private final double strokeWidth;

    /**
     * Degrees per minute (360 degrees / 180 minutes = 2 degrees per minute)
     * Actually, we'll use 2 degrees per minute for smoother control
     */
    private static final double DEGREES_PER_MINUTE = 2.0;

    /**
     * Maximum duration in minutes
     */
    private static final int MAX_DURATION_MINUTES = UIConstants.MAX_DURATION_MINUTES;

    // ===== STATE FIELDS =====

    /**
     * Current selection angle in degrees (0-360)
     * 0 = top (12 o'clock), increases clockwise
     */
    private double selectionAngle = 90.0; // Default to 45 minutes (90 degrees)

    /**
     * Progress value for countdown display (0.0 to 1.0)
     * 0.0 = complete, 1.0 = full time remaining
     */
    private double progress = 1.0;

    /**
     * Whether the ring is in selection mode (draggable) or display mode (countdown)
     */
    private boolean selectionMode = true;

    /**
     * Whether to snap angle to nearest minute
     */
    private boolean snapToMinutes = true;

    /**
     * Callback for selection changes
     */
    private SelectionChangeListener selectionChangeListener;

    /**
     * Callback interface for selection changes
     */
    public interface SelectionChangeListener {
        void onSelectionChanged(int minutes);
    }

    // ===== VISUAL COMPONENTS =====

    /**
     * Background ring (always full circle, dark gray)
     */
    private Arc backgroundRing;

    /**
     * Progress/selection ring (partial arc, accent color)
     */
    private Arc progressRing;

    /**
     * Selection indicator (small circle on perimeter, only visible in selection mode)
     */
    private Circle selectionIndicator;

    // ===== CONSTRUCTOR =====

    /**
     * Creates a circular progress ring with specified diameter
     *
     * @param diameter Diameter of the ring in pixels
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
     * Creates the background and progress rings
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
     * Creates the selection indicator (small circle on perimeter)
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
     * Sets up mouse event handlers for drag interaction
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
     * Updates the selection angle based on mouse position.
     * Uses trigonometry to convert mouse coordinates to angle.
     *
     * @param mouseX Mouse X coordinate
     * @param mouseY Mouse Y coordinate
     * @param snap Whether to snap to nearest minute
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
     * Updates the visual appearance based on current state
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
     * Updates the position of the selection indicator on the perimeter
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
     * Sets the selection angle in degrees (0-360)
     *
     * @param angle Angle in degrees
     */
    public void setSelectionAngle(double angle) {
        this.selectionAngle = Math.max(0.0, Math.min(360.0, angle));
        updateVisuals();
        notifySelectionChanged();
    }

    /**
     * Sets the listener for selection changes
     *
     * @param listener Listener to be notified when selection changes
     */
    public void setSelectionChangeListener(SelectionChangeListener listener) {
        this.selectionChangeListener = listener;
    }

    /**
     * Notifies the listener of selection changes
     */
    private void notifySelectionChanged() {
        if (selectionChangeListener != null) {
            selectionChangeListener.onSelectionChanged(getSelectedMinutes());
        }
    }

    /**
     * Gets the selection angle in degrees
     *
     * @return Angle in degrees (0-360)
     */
    public double getSelectionAngle() {
        return selectionAngle;
    }

    /**
     * Sets the selected duration in minutes.
     * Converts minutes to angle (2 degrees per minute).
     *
     * @param minutes Duration in minutes (0-180)
     */
    public void setSelectedMinutes(int minutes) {
        minutes = Math.max(0, Math.min(MAX_DURATION_MINUTES, minutes));
        setSelectionAngle(minutes * DEGREES_PER_MINUTE);
    }

    /**
     * Gets the selected duration in minutes.
     * Converts angle to minutes (2 degrees per minute).
     *
     * @return Duration in minutes (0-180)
     */
    public int getSelectedMinutes() {
        return (int) Math.round(selectionAngle / DEGREES_PER_MINUTE);
    }

    /**
     * Sets the progress value (for countdown display mode)
     *
     * @param progress Progress from 0.0 (empty) to 1.0 (full)
     */
    public void setProgress(double progress) {
        this.progress = Math.max(0.0, Math.min(1.0, progress));
        updateVisuals();
    }

    /**
     * Gets the current progress value
     *
     * @return Progress (0.0 to 1.0)
     */
    public double getProgress() {
        return progress;
    }

    /**
     * Enables or disables selection mode.
     * When disabled, the ring shows progress instead of selection.
     *
     * @param enabled true for selection mode, false for progress display mode
     */
    public void setSelectionMode(boolean enabled) {
        this.selectionMode = enabled;

        // Enable/disable mouse interaction
        this.setMouseTransparent(!enabled);

        updateVisuals();
    }

    /**
     * Checks if selection mode is enabled
     *
     * @return true if in selection mode
     */
    public boolean isSelectionMode() {
        return selectionMode;
    }

    /**
     * Sets whether to snap angle to nearest minute
     *
     * @param snap true to snap to minutes
     */
    public void setSnapToMinutes(boolean snap) {
        this.snapToMinutes = snap;
    }

    /**
     * Sets the color of the progress ring
     *
     * @param color Ring color
     */
    public void setRingColor(Color color) {
        progressRing.setStroke(color);
        selectionIndicator.setFill(color);
    }

    /**
     * Resets the ring to initial state (45 minutes, selection mode)
     */
    public void reset() {
        setSelectionMode(true);
        setSelectedMinutes(45); // Default 45 minutes
        setProgress(1.0);
    }
}
