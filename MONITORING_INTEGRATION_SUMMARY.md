# Monitoring Backend Integration - Complete Solution

## Summary

The monitoring backend (AppMonitor, ForegroundAppMonitor, ChromeWebsiteMonitor) has been **deeply integrated** into the active session violation-tracking loop through a unified `SessionMonitor` service.

### Problems Solved

1. **Isolated Monitors** → **Unified Service**
   - Before: Monitors lived independently; only frontmost app was checked
   - After: SessionMonitor owns all monitors and polls on a schedule
   - Result: Background apps are now detected via AppMonitor.checkForViolations()

2. **Hardcoded Website List** → **User-Configurable**
   - Before: BLOCKED_WEBSITE_DOMAINS was hardcoded in ActiveSessionPanel
   - After: Users select domains in AppSelectionModal, persisted in UserPreferences
   - Result: Websites are first-class session data like apps

3. **UI Logic Handles Detection** → **Service Handles Detection**
   - Before: ActiveSessionPanel.checkForViolations() checked monitors every tick
   - After: SessionMonitor polls monitors, updates FocusSession, UI subscribes to callbacks
   - Result: Clean separation of concerns

---

## Architecture

### SessionMonitor Service
**File**: `src/main/java/focus/kudafocus/monitoring/SessionMonitor.java`

**Responsibilities**:
- Owns AppMonitor, ChromeWebsiteMonitor, JavaFX Timeline
- Polls all running processes every 1 second → calls AppMonitor.checkForViolations()
- Polls Chrome tab every 5 seconds → calls ChromeWebsiteMonitor.detectDistractingDomain()
- Calls FocusSession.startViolation() / addViolationDuration() / endCurrentViolation()
- Invokes callback only when overlay re-trigger cadence allows (2s minimum)

**Lifecycle**:
```
ActiveSessionPanel.constructor
  → SessionMonitor monitor = new SessionMonitor(session, callback)
ActiveSessionPanel.initializeTimer()
  → timer.start()
  → monitor.start()  // Creates Timeline, begins polling
ActiveSessionPanel.cleanup()
  → timer.cancel()
  → monitor.stop()   // Cancels Timeline
```

### Data Model Changes
**FocusSession.java**:
- New field: `List<String> blockedWebsites`
- Overloaded constructor: `FocusSession(duration, apps, websites)`
- Getter: `getBlockedWebsites()`
- Setter: `setBlockedWebsites()`

**UserPreferences.java**:
- New field: `List<String> lastSelectedWebsites`
- Auto-persisted to `~/.kudafocus/preferences.json`

**SessionRecord.java**:
- New field: `List<String> blockedWebsites`
- Full serialization support (Gson)

### UI Changes
**AppSelectionModal.java**:
- Added TextArea for comma-separated domain input
- New method: `getSelectedWebsites()` parses and normalizes domains
- Overloaded constructor: `AppSelectionModal(owner, apps, websites)`
- Websites normalized to lowercase (e.g., "YouTube.com" → "youtube.com")

**CircularTimerPanel.java**:
- New field: `List<String> selectedWebsites`
- Updated callback: `onStartSession(minutes, apps, websites)`
- Methods: `setSelectedWebsites()`, `getSelectedWebsites()`
- Status label shows both apps and sites counts

**Main.java**:
- Updated `handleStartSession()` to create FocusSession with websites
- Updated `handleSelectApps()` to retrieve/store websites from modal
- Updated `showHomeScreen()` to restore websites from UserPreferences

**ActiveSessionPanel.java** (Refactored):
- Removed: ChromeWebsiteMonitor, ForegroundAppMonitor fields
- Removed: hardcoded BLOCKED_WEBSITE_DOMAINS, all violation checking logic
- Added: SessionMonitor field
- Timer callback: UI updates only (no monitoring logic)
- SessionMonitor callback: shows overlay on detection
- Cleanup: stops monitor, cancels timer

---

## Workflow

### User Selects Blocked Apps/Websites
```
User clicks "Select apps to block"
  → Main.handleSelectApps()
  → AppSelectionModal(timerPanel.getSelectedApps(), getSelectedWebsites())
  → User checks apps, enters website domains (comma-separated)
  → User clicks "Confirm"
  → Modal returns apps & websites
  → CircularTimerPanel.setSelectedApps() + setSelectedWebsites()
  → UserPreferences.lastSelectedApps + lastSelectedWebsites
  → PreferencesStore.save()
```

### Session Starts
```
User clicks "START"
  → CircularTimerPanel.onStartSession(45, ["Discord", "Steam"], ["youtube.com"])
  → Main.handleStartSession()
  → FocusSession(2700, ["Discord", "Steam"], ["youtube.com"])
  → ActiveSessionPanel.initializeTimer()
    └─ Timer.start()
    └─ SessionMonitor.start()
       ├─ Create Timeline
       ├─ Every 1s: poll AppMonitor.checkForViolations()
       └─ Every 5s: poll ChromeWebsiteMonitor.detectDistractingDomain()
```

### Violation Detected
```
SessionMonitor.onTimerTick() @ 1s interval
  → AppMonitor.checkForViolations(["Discord", "Steam"])
  → Returns ["Discord"] (running in background!)
  → SessionMonitor.startViolationIfChanged("Discord")
  → FocusSession.startViolation("Discord")
  → FocusSession.addViolationDuration(1)
  → Check overlay cadence: elapsed - lastAppOverlayTrigger >= 2s?
    └─ YES → callback.onViolationDetected("Discord")
    └─ ActiveSessionPanel.callback → Main.handleViolationDetected()
    └─ Show DistractionOverlay
```

### Website Violation Detected
```
SessionMonitor.onTimerTick() @ 5s interval
  → ChromeWebsiteMonitor.detectDistractingDomain(["youtube.com"])
  → Returns "youtube.com" (if Chrome is frontmost and tab matches)
  → Create violation name: "Website: youtube.com"
  → SessionMonitor.startViolationIfChanged("Website: youtube.com")
  → FocusSession.startViolation("Website: youtube.com")
  → FocusSession.addViolationDuration(5)
  → Check overlay cadence → callback → show overlay
```

### Session Ends
```
Timer reaches 0
  → Timer.onComplete()
  → Main.handleSessionComplete()
  → SessionMonitor.stop() (in ActiveSessionPanel.cleanup())
  → Show SessionSummaryPanel
  → Display violations, focus score, etc.
```

---

## Tests

**File**: `src/test/java/focus/kudafocus/monitoring/SessionMonitorTest.java`

**13 Test Cases**:
1. ✅ Session initialization
2. ✅ Violation creation
3. ✅ Violation duration tracking
4. ✅ Violation ending
5. ✅ Multiple violations
6. ✅ Violation switching (auto-ends previous)
7. ✅ Focus score calculation
8. ✅ Website violations
9. ✅ Session completion
10. ✅ Session abandonment
11. ✅ Streak qualification
12. ✅ Most distracting app detection
13. ✅ Defensive copying (getters return copies, not originals)

**Run Tests**: `mvn test`

---

## Key Design Decisions

### 1. SessionMonitor owns monitors, not ActiveSessionPanel
**Why**: Decouples UI from detection logic. Panel now just shows overlays; service handles all polling.

### 2. Website list is user-configurable, not hardcoded
**Why**: OOP principle: data should be in the data model (session), not scattered in code.

### 3. AppMonitor.checkForViolations() checks ALL processes
**Why**: Catches background distractions that only checking frontmost would miss.

### 4. Overlay cadence is enforced in SessionMonitor, not panel
**Why**: Business logic belongs in service layer. UI just responds to callbacks.

### 5. Websites are stored alongside apps in FocusSession
**Why**: Symmetry and consistency. Both are "blocked items" from focus perspective.

---

## Integration Checklist

- [x] SessionMonitor service created
- [x] FocusSession extended with blockedWebsites field
- [x] SessionRecord extended with blockedWebsites field
- [x] UserPreferences extended with lastSelectedWebsites field
- [x] AppSelectionModal extended for website input
- [x] CircularTimerPanel updated to handle websites
- [x] Main.java updated to pass websites through callback chain
- [x] ActiveSessionPanel refactored to use SessionMonitor
- [x] Unit tests created (13 passing tests)
- [x] Build clean, no errors
- [x] Documentation updated

---

## Verification

```bash
cd /Users/hjiang/Developer/kudafocus
mvn clean
mvn compile  # 24 files compile successfully
mvn test     # All 13 tests pass
```

---

## Future Enhancements

1. **Multi-browser support**: Add Firefox, Safari website monitors
2. **Background monitoring**: Track violations when paused (with user opt-in)
3. **Smart categorization**: Automatically categorize detected apps
4. **Advanced filtering**: Regex patterns for website blocking (not just exact domain)
5. **Real-time stats UI**: Show live violation count during session
6. **Violation replay**: View detailed timeline of violations after session

---

## Files Modified

```
src/main/java/focus/kudafocus/
├── core/
│   ├── FocusSession.java             (+blockedWebsites)
├── data/models/
│   ├── SessionRecord.java            (+blockedWebsites)
│   └── UserPreferences.java          (+lastSelectedWebsites)
├── monitoring/
│   └── SessionMonitor.java           (NEW - service for unified polling)
└── ui/
    ├── ActiveSessionPanel.java       (refactored - uses SessionMonitor)
    ├── AppSelectionModal.java        (extended - website input)
    ├── CircularTimerPanel.java       (enhanced - website support)
    └── Main.java                     (updated - website integration)

src/test/java/focus/kudafocus/monitoring/
└── SessionMonitorTest.java           (NEW - 13 unit tests)
```

**Total Changes**: 
- 8 files modified/created
- 7 git commits
- 0 breaking changes (backwards compatible)
- All tests passing
