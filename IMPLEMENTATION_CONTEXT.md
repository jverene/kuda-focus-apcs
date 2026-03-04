# KUDA FOCUS - Implementation Context for Phase 2

## Project Status
**Phase 1 Complete** ✅ - Foundation and core OOP classes are implemented
**Next: Phase 2** - Timer UI & Session Flow implementation

## What You Need to Do

Start implementing the **Circular Timer UI** and related components. The foundation is ready - now build the user interface!

### Immediate Tasks (Phase 2)
1. **CircularProgressRing.java** - Custom JavaFX circular timer control with drag interaction
2. **CircularTimerPanel.java** - Main home screen with circular timer
3. **Timer.java** - Core countdown timer logic
4. **ActiveSessionPanel.java** - Running session view
5. **DistractionOverlay.java** - Full-screen "Stay Focused" overlay
6. **SessionSummaryPanel.java** - Post-session results

---

## Project Overview

**KUDA FOCUS** is a minimalist desktop focus timer for students (APCS project).

### Core Concept
- **Circular Timer**: iPhone-style drag-to-select interface (0-180 minutes)
- **Process Monitoring**: Detects when blocked apps are opened during focus sessions
- **Focus Score**: 0-100 scoring based on distraction violations
- **Streak Tracking**: Consecutive days with score ≥80 (sessions ≥30 min)

### Technology Stack
- **Language**: Java 11
- **UI**: JavaFX 17 (code-based, no FXML)
- **Build**: Maven
- **Data**: Gson for JSON (local storage only)
- **Platform**: macOS primary, Windows supported

---

## Existing Implementation (Phase 1 ✅)

### 1. OOP Demonstration Classes
These are **already complete** and demonstrate APCS requirements:

#### **Encapsulation**: `FocusSession.java`
Location: `src/main/java/focus/kudafocus/core/FocusSession.java`
- Private fields with public getters/setters
- Private `calculateFocusScore()` method (hidden implementation)
- Manages session state, violations, and scoring
- Key methods:
  - `startViolation(appName)` - Records when blocked app opened
  - `recordDismissal()` - Logs overlay dismissal
  - `complete(duration)` - Marks session complete, calculates final score
  - `getFocusScore()` - Returns 0-100 score (calculation is hidden)

#### **Abstraction**: `AppMonitor.java` hierarchy
Location: `src/main/java/focus/kudafocus/monitoring/`
- Abstract base class: `AppMonitor.java`
- macOS implementation: `MacOSAppMonitor.java` (uses `ps aux`)
- Windows implementation: `WindowsAppMonitor.java` (uses `tasklist`)
- Abstract method: `getCurrentProcesses()` - must be implemented per OS
- Shared method: `checkForViolations(blockedApps)` - same on all platforms
- Factory method: `AppMonitor.createForCurrentOS()` - returns correct monitor

#### **Inheritance**: `BasePanel.java`
Location: `src/main/java/focus/kudafocus/ui/BasePanel.java`
- Extends JavaFX `VBox`
- Provides shared styling properties (colors, fonts, spacing)
- Protected fields accessible to subclasses
- Method: `applyStandardStyling()` - applies consistent design
- All UI panels should extend this class

### 2. Supporting Classes

#### **UIConstants.java** ✅
Location: `src/main/java/focus/kudafocus/ui/UIConstants.java`
Complete design system with:
- **Colors**: Dark theme (BACKGROUND_PRIMARY, ACCENT_COLOR, TEXT_PRIMARY, etc.)
- **Typography**: Font sizes and helper methods (getDisplayFont(), getBodyFont(), etc.)
- **Sizing**: TIMER_RING_DIAMETER (400px), WINDOW_WIDTH (800px), etc.
- **Spacing**: SPACING_XS/SM/MD/LG/XL, PADDING_STANDARD
- **Timing**: OVERLAY_REAPPEAR_SECONDS (15), MONITORING_INTERVAL_MS (2000)
- **Scoring**: SCORE_VIOLATION_PENALTY (5), SCORE_DISMISSAL_PENALTY (2), etc.
- **Animation**: ANIMATION_DURATION_MS (300), ANIMATION_FAST_MS (150)

#### **Violation.java** ✅
Location: `src/main/java/focus/kudafocus/core/Violation.java`
- Represents single distraction event
- Fields: timestamp, appName, durationSeconds, dismissCount
- Methods: `incrementDismissCount()`, `addDuration(seconds)`

#### **ProcessInfo.java** ✅
Location: `src/main/java/focus/kudafocus/monitoring/ProcessInfo.java`
- Process data model
- Fields: processName, displayName, pid, running

#### **Data Models** ✅
Location: `src/main/java/focus/kudafocus/data/models/`
- `UserPreferences.java` - User settings and app registry
- `SessionHistory.java` - Container for all sessions
- `SessionRecord.java` - Individual session storage (TODO: implement this)

---

## Design Specifications

### Circular Timer Interface

**Visual Design:**
```
┌─────────────────────────────────┐
│                                 │
│         Current Streak          │
│           🔥 14 days            │
│                                 │
│     ╭─────────────────╮        │
│   ╱                     ╲      │
│  │      1:30:00          │     │  <- Large display (64px font)
│  │                       │     │
│  │         START         │     │  <- Button in center
│   ╲                     ╱      │
│     ╰─────────────────╯        │
│                                 │
│    [Select apps to block]      │
│                                 │
└─────────────────────────────────┘
```

**Interaction:**
1. User drags cursor around circle perimeter to select duration
2. Time snaps to nearest minute (clean UX)
3. Center displays selected time in HH:MM:SS format
4. START button in center begins session
5. During session, ring depletes clockwise

**Technical Implementation:**
- Custom JavaFX control extending `Region` or `Pane`
- Use `Arc` shape for ring segments
- Mouse event handlers for drag interaction
- AnimationTimer for smooth updates
- Snap-to-minute using modulo math: `Math.round(degrees / 6.0) * 6.0` (6° per minute)

### Focus Score Algorithm

```java
Base Score: 100

Deductions:
- Per violation occurrence: -5 points (SCORE_VIOLATION_PENALTY)
- Per overlay dismissal: -2 points (SCORE_DISMISSAL_PENALTY)
- Per minute on blocked apps: -1 point (SCORE_TIME_PENALTY_PER_MINUTE)

Minimum: 0, Maximum: 100

Example:
- 3 violations: -15
- 11 dismissals: -22
- 2.75 minutes distracted: -2
- Final: 100 - 15 - 22 - 2 = 61
```

### Session Flow

```
CircularTimerPanel (home)
    ↓ [select time, select apps, press START]
ActiveSessionPanel (running)
    ↓ [timer counting down, monitoring processes]
DistractionOverlay (if blocked app detected)
    ↓ [user dismisses overlay or closes app]
ActiveSessionPanel (continues)
    ↓ [timer reaches 0:00]
SessionSummaryPanel (results)
    ↓ [press CONTINUE]
CircularTimerPanel (home)
```

---

## File Structure

```
kudafocus/
├── pom.xml                                    ✅ Complete
├── README.md                                  ✅ Complete
├── .gitignore                                 ✅ Complete
│
├── src/main/java/focus/kudafocus/
│   ├── Main.java                              ✅ Complete (placeholder)
│   │
│   ├── ui/
│   │   ├── UIConstants.java                   ✅ Complete
│   │   ├── BasePanel.java                     ✅ Complete
│   │   ├── CircularTimerPanel.java            ⏸️  TODO - Main home screen
│   │   ├── AppSelectionModal.java             ⏸️  TODO - App picker dialog
│   │   ├── ActiveSessionPanel.java            ⏸️  TODO - Running session
│   │   ├── DistractionOverlay.java            ⏸️  TODO - Full-screen overlay
│   │   ├── SessionSummaryPanel.java           ⏸️  TODO - Results screen
│   │   ├── DashboardPanel.java                ⏸️  TODO (Phase 5)
│   │   └── components/
│   │       ├── CircularProgressRing.java      ⏸️  TODO - Custom control
│   │       ├── AppListItem.java               ⏸️  TODO (Phase 4)
│   │       ├── CalendarWeekView.java          ⏸️  TODO (Phase 5)
│   │       └── FocusScoreGraph.java           ⏸️  TODO (Phase 5)
│   │
│   ├── core/
│   │   ├── FocusSession.java                  ✅ Complete
│   │   ├── Violation.java                     ✅ Complete
│   │   ├── Timer.java                         ⏸️  TODO - Countdown logic
│   │   └── FocusScoreCalculator.java          ⏸️  TODO (Phase 3)
│   │
│   ├── monitoring/
│   │   ├── AppMonitor.java                    ✅ Complete
│   │   ├── MacOSAppMonitor.java               ✅ Complete
│   │   ├── WindowsAppMonitor.java             ✅ Complete
│   │   ├── ProcessInfo.java                   ✅ Complete
│   │   ├── ProcessScanner.java                ⏸️  TODO (Phase 3)
│   │   └── ViolationDetector.java             ⏸️  TODO (Phase 3)
│   │
│   ├── analytics/                             ⏸️  TODO (Phase 5)
│   │   ├── StreakCalculator.java
│   │   ├── StatisticsManager.java
│   │   └── PersonalBests.java
│   │
│   └── data/
│       ├── models/
│       │   ├── UserPreferences.java           ✅ Complete
│       │   ├── SessionHistory.java            ✅ Complete
│       │   ├── AppRegistry.java               ⏸️  TODO
│       │   └── SessionRecord.java             ⏸️  TODO
│       └── storage/
│           ├── DataStore.java                 ⏸️  TODO (Phase 4)
│           ├── PreferencesStore.java          ⏸️  TODO (Phase 4)
│           └── SessionStore.java              ⏸️  TODO (Phase 4)
│
└── src/main/resources/
    ├── styles/application.css                 ✅ Complete
    └── data/
        ├── demo-preferences.json              ✅ Complete
        └── demo-sessions.json                 ✅ Complete
```

---

## Build & Run Commands

```bash
# Navigate to project
cd /Users/hjiang/Developer/kudafocus

# Clean build
mvn clean install

# Run application
mvn javafx:run

# Run tests
mvn test

# Package JAR
mvn package
```

---

## Key Design Patterns to Follow

### 1. Extend BasePanel for All UI
```java
public class CircularTimerPanel extends BasePanel {
    public CircularTimerPanel() {
        super();  // Initializes colors, fonts, spacing
        buildUI();
    }

    private void buildUI() {
        // Access inherited properties:
        // - primaryColor, accentColor
        // - titleFont, bodyFont
        // - standardPadding, standardSpacing
    }
}
```

### 2. Use UIConstants Throughout
```java
// Colors
setFill(UIConstants.ACCENT_COLOR);
setTextFill(UIConstants.TEXT_PRIMARY);

// Fonts
label.setFont(UIConstants.getDisplayFont());

// Sizing
circle.setRadius(UIConstants.TIMER_RING_DIAMETER / 2);

// Spacing
vbox.setSpacing(UIConstants.SPACING_LG);

// Timing
timer.schedule(task, UIConstants.TIMER_UPDATE_INTERVAL_MS);
```

### 3. Session Lifecycle
```java
// Creating a session
List<String> blockedApps = Arrays.asList("Discord", "Steam");
FocusSession session = new FocusSession(1500, blockedApps); // 25 min

// During session - record violation
session.startViolation("Discord");
session.recordDismissal();  // User dismissed overlay
session.addViolationDuration(30);  // 30 seconds distracted
session.endCurrentViolation();

// Completing session
session.complete(1480);  // Actually lasted 24:40
int score = session.getFocusScore();  // Get final score (0-100)
```

### 4. Process Monitoring
```java
// Create monitor for current OS
AppMonitor monitor = AppMonitor.createForCurrentOS();

// Check for violations
List<String> blockedApps = Arrays.asList("Discord", "Steam");
List<String> violations = monitor.checkForViolations(blockedApps);

if (!violations.isEmpty()) {
    // Show overlay
}
```

---

## Priority Implementation Order (Phase 2)

### 1. **Timer.java** (Core Logic)
Location: `src/main/java/focus/kudafocus/core/Timer.java`

**Purpose**: Countdown timer with second-precision

**Required Features:**
- Constructor: `Timer(int durationSeconds, TimerCallback callback)`
- Methods:
  - `start()` - Begin countdown
  - `pause()` - Pause timer
  - `resume()` - Resume timer
  - `stop()` - Stop and reset
  - `getRemainingSeconds()` - Get current time left
  - `getElapsedSeconds()` - Get time elapsed
  - `isRunning()` - Check if active
- Callback interface: `onTick(remaining)`, `onComplete()`
- Use `ScheduledExecutorService` or JavaFX `Timeline`

### 2. **CircularProgressRing.java** (Custom Control)
Location: `src/main/java/focus/kudafocus/ui/components/CircularProgressRing.java`

**Purpose**: Circular ring that can be dragged to select time and shows progress

**Required Features:**
- Extends `Region` or `Pane`
- Properties:
  - `progress` (0.0 to 1.0)
  - `selectionAngle` (0 to 360 degrees)
  - `snapToMinutes` (boolean)
- Mouse handlers:
  - `onMousePressed` - Start drag
  - `onMouseDragged` - Update angle
  - `onMouseReleased` - Snap to minute
- Visual:
  - Background ring (dark gray)
  - Progress ring (accent color, depletes during session)
  - Selection indicator (small circle on perimeter)
- Use JavaFX `Arc` shape

### 3. **CircularTimerPanel.java** (Home Screen)
Location: `src/main/java/focus/kudafocus/ui/CircularTimerPanel.java`

**Purpose**: Main home screen with circular timer interface

**Required Components:**
- Extends `BasePanel`
- Streak display (Label at top: "🔥 X days")
- CircularProgressRing (center, 400px diameter)
- Time display (Label in center, 64px font)
- START button (Button in center, below time)
- "Select apps to block" button (bottom)

**Interactions:**
- Drag ring → update time display
- Click START → transition to ActiveSessionPanel
- Click "Select apps" → open AppSelectionModal (stub for now)

### 4. **ActiveSessionPanel.java** (Running Session)
Location: `src/main/java/focus/kudafocus/ui/ActiveSessionPanel.java`

**Purpose**: Shows running session with countdown

**Required Components:**
- Extends `BasePanel`
- CircularProgressRing (depleting)
- Time remaining (Label, large)
- PAUSE button
- STOP button (with confirmation)

**Logic:**
- Integrate with Timer class
- Update ring progress on each tick
- Monitor for violations (integrate AppMonitor)
- Show DistractionOverlay when violation detected

### 5. **DistractionOverlay.java** (Full-Screen Overlay)
Location: `src/main/java/focus/kudafocus/ui/DistractionOverlay.java`

**Purpose**: Full-screen overlay when blocked app detected

**Required Components:**
- Extends `Stage` or `Scene` (overlay window)
- Semi-transparent background (70% opacity black)
- "Stay Focused!" message (large, centered)
- Time remaining (display)
- "Return to Focus" button
- Sets `alwaysOnTop(true)`

**Logic:**
- Reappears every 15 seconds if app still open
- Records dismissal to session
- Tracks violation duration

### 6. **SessionSummaryPanel.java** (Results)
Location: `src/main/java/focus/kudafocus/ui/SessionSummaryPanel.java`

**Purpose**: Shows session results after completion

**Required Components:**
- Extends `BasePanel`
- Focus score (LARGE number, color-coded)
- Session duration
- Violation count
- Most distracting app
- CONTINUE button → return home

**Logic:**
- Get data from FocusSession object
- Color-code score:
  - 80-100: SUCCESS_COLOR (green)
  - 50-79: WARNING_COLOR (yellow)
  - 0-49: DANGER_COLOR (red)

---

## Testing Strategy

After implementing each component:

1. **Unit Test** (if applicable):
   - Timer.java → TimerTest.java

2. **Visual Test**:
   - Run `mvn javafx:run`
   - Manually test UI interactions

3. **Integration Test**:
   - Test full flow: home → session → overlay → summary → home

---

## Common Pitfalls to Avoid

1. **Don't use JPanel/JFrame** - This is JavaFX, not Swing!
   - Use `Pane`, `VBox`, `HBox`, `Region`, etc.

2. **Don't forget to extend BasePanel** - All UI panels should extend it

3. **Use UIConstants** - Don't hardcode colors, fonts, or sizes

4. **JavaFX Threading** - UI updates must be on JavaFX Application Thread
   - Use `Platform.runLater(() -> { /* UI update */ })`

5. **Timer Threading** - Use JavaFX `Timeline` or `AnimationTimer` for UI updates
   - Or use `ScheduledExecutorService` with `Platform.runLater`

---

## Example Code Snippets

### Creating a Panel
```java
public class CircularTimerPanel extends BasePanel {
    private CircularProgressRing ring;
    private Label timeLabel;
    private Button startButton;

    public CircularTimerPanel() {
        super();  // Initialize base styling
        createComponents();
        layoutComponents();
        setupEventHandlers();
    }

    private void createComponents() {
        ring = new CircularProgressRing(UIConstants.TIMER_RING_DIAMETER);
        timeLabel = new Label("0:25:00");
        timeLabel.setFont(UIConstants.getDisplayFont());
        timeLabel.setTextFill(getTextPrimaryColor());

        startButton = new Button("START");
        startButton.setFont(UIConstants.getBodyFont());
        // ... more setup
    }

    private void layoutComponents() {
        this.getChildren().addAll(ring, timeLabel, startButton);
        // ... layout logic
    }

    private void setupEventHandlers() {
        startButton.setOnAction(e -> handleStart());
        // ... more handlers
    }
}
```

### Using Timer
```java
Timer timer = new Timer(1500, new Timer.Callback() {
    @Override
    public void onTick(int remaining) {
        Platform.runLater(() -> updateUI(remaining));
    }

    @Override
    public void onComplete() {
        Platform.runLater(() -> showSummary());
    }
});

timer.start();
```

---

## Monitoring Integration (Enhanced Phase 2)

### SessionMonitor Service
Location: `src/main/java/focus/kudafocus/monitoring/SessionMonitor.java`

**Purpose**: Centralizes violation detection for apps and websites into a single service that updates the FocusSession in real-time.

**Key Features**:
- Polls all running processes using `AppMonitor.checkForViolations()` every 1 second
- Checks Chrome active tab for blocked websites every 5 seconds  
- Maintains violation state in FocusSession (start/end/duration tracking)
- Enforces overlay re-trigger cadence (minimum 2 seconds between same-violation overlays)
- Lifecycle: `start()` creates Timeline, `stop()` cancels it

### Website Blocking (New)
**User-selectable blocked websites** (no longer hardcoded):

1. **Data Model**: `FocusSession` now includes `blockedWebsites` field
   - Constructor: `FocusSession(duration, blockedApps, blockedWebsites)`
   - Storage: `SessionRecord` and `UserPreferences.lastSelectedWebsites`

2. **UI Selection**: `AppSelectionModal` extended with website input
   - TextArea for comma-separated domains
   - Getter: `getSelectedWebsites()`

3. **Tests**: `SessionMonitorTest` with 13 test cases
   - Violation lifecycle, duration tracking
   - Session completion/abandonment, focus score
   - Streak qualification, most distracting app

---

## Questions to Ask Me

If anything is unclear, ask me:
1. Should the circular timer be draggable during an active session? (I think NO - only on home screen)
2. Should PAUSE keep the monitoring active or stop it?
3. For the overlay, should we darken the background or make it fully opaque?
4. Do you want haptic feedback (system beep) when overlay appears?

---

## Next Steps for You

**Start with these files in this order:**

1. **Timer.java** - Get the core countdown logic working first
2. **CircularProgressRing.java** - Build the custom circular control
3. **CircularTimerPanel.java** - Assemble the home screen
4. **Update Main.java** - Change from placeholder to show CircularTimerPanel
5. **ActiveSessionPanel.java** - Running session view
6. **DistractionOverlay.java** - Overlay window
7. **SessionSummaryPanel.java** - Results screen

**Success Criteria:**
- User can select time by dragging circle
- User can start a session
- Timer counts down and updates UI
- Session completes and shows summary
- Summary displays correct data from FocusSession

Good luck! The foundation is solid - now make it come alive! 🚀
