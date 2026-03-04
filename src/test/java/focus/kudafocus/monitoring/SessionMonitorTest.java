package focus.kudafocus.monitoring;

import focus.kudafocus.core.FocusSession;
import focus.kudafocus.monitoring.AppMonitor;
import focus.kudafocus.monitoring.ChromeWebsiteMonitor;
import focus.kudafocus.monitoring.ForegroundAppMonitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SessionMonitor violation tracking logic.
 *
 * Tests core session monitor functionality:
 * - Violation state transitions
 * - Session updates when violations are detected
 * - Callback invocations
 */
public class SessionMonitorTest {

    private FocusSession session;
    private SessionMonitor.SessionMonitorCallback mockCallback;
    private List<String> callbackInvocations;

    @BeforeEach
    public void setUp() {
        // Create a session with blocked apps
        session = new FocusSession(3600, Arrays.asList("Discord", "Steam"), Arrays.asList("youtube.com"));

        // Track callback invocations
        callbackInvocations = new ArrayList<>();
        mockCallback = new SessionMonitor.SessionMonitorCallback() {
            @Override
            public void onViolationDetected(String appName) {
                callbackInvocations.add("detected:" + appName);
            }

            @Override
            public void onViolationEnded() {
                callbackInvocations.add("ended");
            }
        };
    }

    @Test
    public void testSessionInitialization() {
        assertFalse(session.hasActiveViolation(), "Session should start with no active violation");
        assertEquals(2, session.getBlockedApps().size(), "Session should have 2 blocked apps");
        assertEquals(1, session.getBlockedWebsites().size(), "Session should have 1 blocked website");
    }

    @Test
    public void testViolationCreation() {
        session.startViolation("Discord");

        assertTrue(session.hasActiveViolation(), "Session should have active violation");
        assertEquals("Discord", session.getCurrentViolation().getAppName(), "Violation app name should match");
    }

    @Test
    public void testViolationDurationTracking() {
        session.startViolation("Discord");
        session.addViolationDuration(5);
        session.addViolationDuration(3);

        assertEquals(8, session.getCurrentViolation().getDurationSeconds(), "Violation duration should be 8 seconds");
    }

    @Test
    public void testViolationEnding() {
        session.startViolation("Discord");
        session.addViolationDuration(10);
        session.endCurrentViolation();

        assertFalse(session.hasActiveViolation(), "Session should have no active violation after ending");
        assertEquals(1, session.getViolationCount(), "Session should have 1 recorded violation");
    }

    @Test
    public void testMultipleViolations() {
        // First violation
        session.startViolation("Discord");
        session.addViolationDuration(10);
        session.endCurrentViolation();

        // Second violation
        session.startViolation("Steam");
        session.addViolationDuration(5);
        session.endCurrentViolation();

        assertEquals(2, session.getViolationCount(), "Session should have 2 violations");
        List<String> appNames = new ArrayList<>();
        for (var v : session.getViolations()) {
            appNames.add(v.getAppName());
        }
        assertTrue(appNames.contains("Discord") && appNames.contains("Steam"), "Violations should include both apps");
    }

    @Test
    public void testViolationSwitching() {
        // Start with Discord
        session.startViolation("Discord");
        session.addViolationDuration(5);

        // Switch to Steam
        session.startViolation("Steam");
        assertEquals("Steam", session.getCurrentViolation().getAppName(), "Current violation should be Steam");
        assertEquals(2, session.getViolationCount(), "Should have 2 violations (Discord ended, Steam started)");

        session.endCurrentViolation();
    }

    @Test
    public void testFocusScoreCalculation() {
        // Perfect session - no violations
        int scoreBeforeViolations = session.getFocusScore();
        assertEquals(100, scoreBeforeViolations, "Session with no violations should have perfect score");

        // Add a violation
        session.startViolation("Discord");
        session.addViolationDuration(120); // 2 minutes
        session.endCurrentViolation();

        int scoreAfterViolation = session.getFocusScore();
        assertTrue(scoreAfterViolation < 100, "Score should decrease after violation");
        assertTrue(scoreAfterViolation >= 0, "Score should not go below 0");
    }

    @Test
    public void testWebsiteViolation() {
        session.startViolation("Website: youtube.com");
        session.addViolationDuration(30);

        assertTrue(session.hasActiveViolation(), "Session should have website violation");
        assertTrue(session.getCurrentViolation().getAppName().contains("youtube"), "Violation should be for youtube");
    }

    @Test
    public void testSessionCompletion() {
        session.startViolation("Discord");
        session.addViolationDuration(60);
        session.endCurrentViolation();

        session.complete(1800); // 30 minutes

        assertTrue(session.isCompleted(), "Session should be marked as completed");
        assertEquals(1800, session.getActualDuration(), "Actual duration should be set");
    }

    @Test
    public void testSessionAbandonment() {
        session.startViolation("Steam");
        session.addViolationDuration(30);
        session.endCurrentViolation();

        session.abandon(900); // Abandoned after 15 minutes

        assertFalse(session.isCompleted(), "Session should be marked as abandoned");
        assertEquals(900, session.getActualDuration(), "Actual duration should be set");
    }

    @Test
    public void testStreakQualification() {
        // Create a 45-minute session
        FocusSession session45 = new FocusSession(2700, new ArrayList<>(), new ArrayList<>());

        // Complete with no violations (score = 100)
        session45.complete(2700);

        assertTrue(session45.qualifiesForStreak(), "45-min perfect session should qualify for streak");

        // Create a 20-minute session
        FocusSession session20 = new FocusSession(1200, new ArrayList<>(), new ArrayList<>());
        session20.complete(1200);

        assertFalse(session20.qualifiesForStreak(), "20-min session should not qualify (too short)");
    }

    @Test
    public void testMostDistractingApp() {
        // First violation: 60 seconds
        session.startViolation("Discord");
        session.addViolationDuration(60);
        session.endCurrentViolation();

        // Second violation: 30 seconds
        session.startViolation("Steam");
        session.addViolationDuration(30);
        session.endCurrentViolation();

        assertEquals("Discord", session.getMostDistractingApp(), "Discord should be most distracting (60 > 30)");
    }

    @Test
    public void testGettersCopiesData() {
        List<String> apps = session.getBlockedApps();
        apps.clear(); // Try to modify the returned list

        assertEquals(2, session.getBlockedApps().size(), "Original list should not be affected");

        List<String> websites = session.getBlockedWebsites();
        websites.clear();

        assertEquals(1, session.getBlockedWebsites().size(), "Original websites should not be affected");
    }

    // ==== ADDITIONAL SESSIONMONITOR TESTS ====

    /**
     * Simple stub that allows controlling the reported frontmost application.
     */
    private static class StubForeground extends ForegroundAppMonitor {
        private String front;
        void setFront(String front) { this.front = front; }
        @Override
        public String getFrontmostApplication() { return front; }
    }

    @Test
    public void testMonitorDetectsOnlyFrontmostApp() {
        StubForeground fg = new StubForeground();
        fg.setFront("Discord");
        SessionMonitor monitor = new SessionMonitor(session, mockCallback,
                AppMonitor.createForCurrentOS(), fg, new ChromeWebsiteMonitor());
        monitor.tickOnce();
        assertEquals(1, callbackInvocations.size(), "Should detect Discord when frontmost");
        assertTrue(callbackInvocations.get(0).contains("Discord"));
    }

    @Test
    public void testMonitorIgnoresBackgroundBlockedApp() {
        StubForeground fg = new StubForeground();
        fg.setFront("Messages"); // not in blocked list
        SessionMonitor monitor = new SessionMonitor(session, mockCallback,
                AppMonitor.createForCurrentOS(), fg, new ChromeWebsiteMonitor());
        monitor.tickOnce();
        assertTrue(callbackInvocations.isEmpty(), "No violation when blocked app not frontmost");
    }
}

