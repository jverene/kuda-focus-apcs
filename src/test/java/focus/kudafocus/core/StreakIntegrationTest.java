package focus.kudafocus.core;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test demonstrating complete streak workflow
 */
class StreakIntegrationTest {

    @Test
    void testCompleteStreakWorkflow() {
        // Clean up any existing data
        StreakTracker tracker = new StreakTracker();
        tracker.resetStreak();
        
        System.out.println("\n=== STREAK INTEGRATION TEST ===\n");
        
        // Session 1: Short session, doesn't qualify
        System.out.println("Session 1: 15 minutes, perfect focus");
        FocusSession session1 = new FocusSession(900, new ArrayList<>(), new ArrayList<>());
        session1.complete(900);
        
        System.out.println("  Duration: " + session1.getActualDurationMinutes() + " min");
        System.out.println("  Score: " + session1.getFocusScore());
        System.out.println("  Qualifies: " + session1.qualifiesForStreak());
        
        tracker.recordSession(session1.qualifiesForStreak());
        System.out.println("  Streak after: " + tracker.getCurrentStreak() + " days\n");
        
        assertFalse(session1.qualifiesForStreak(), "15-min session should not qualify (too short)");
        assertEquals(0, tracker.getCurrentStreak(), "Streak should remain 0");
        
        // Session 2: Perfect 30-minute session, qualifies!
        System.out.println("Session 2: 30 minutes, perfect focus");
        FocusSession session2 = new FocusSession(1800, new ArrayList<>(), new ArrayList<>());
        session2.complete(1800);
        
        System.out.println("  Duration: " + session2.getActualDurationMinutes() + " min");
        System.out.println("  Score: " + session2.getFocusScore());
        System.out.println("  Qualifies: " + session2.qualifiesForStreak());
        
        tracker.recordSession(session2.qualifiesForStreak());
        System.out.println("  Streak after: " + tracker.getCurrentStreak() + " days\n");
        
        assertTrue(session2.qualifiesForStreak(), "30-min perfect session should qualify");
        assertEquals(1, tracker.getCurrentStreak(), "Streak should be 1");
        
        // Session 3: Another qualifying session same day
        System.out.println("Session 3: 45 minutes, same day");
        FocusSession session3 = new FocusSession(2700, new ArrayList<>(), new ArrayList<>());
        session3.complete(2700);
        
        System.out.println("  Duration: " + session3.getActualDurationMinutes() + " min");
        System.out.println("  Score: " + session3.getFocusScore());
        System.out.println("  Qualifies: " + session3.qualifiesForStreak());
        
        tracker.recordSession(session3.qualifiesForStreak());
        System.out.println("  Streak after: " + tracker.getCurrentStreak() + " days\n");
        
        assertTrue(session3.qualifiesForStreak(), "45-min perfect session should qualify");
        assertEquals(1, tracker.getCurrentStreak(), "Streak should still be 1 (same day)");
        
        // Session 4: With distractions, but score is still 80+
        System.out.println("Session 4: 30 minutes with minimal distractions");
        FocusSession session4 = new FocusSession(1800, Arrays.asList("Discord"), new ArrayList<>());
        
        // Simulate one violation: open Discord for 5 seconds, dismiss twice
        session4.startViolation("Discord");
        session4.recordDismissal();
        session4.recordDismissal();
        session4.addViolationDuration(5);
        session4.endCurrentViolation();
        
        session4.complete(1800);
        
        System.out.println("  Duration: " + session4.getActualDurationMinutes() + " min");
        System.out.println("  Score: " + session4.getFocusScore());
        System.out.println("  Violations: " + session4.getViolationCount());
        System.out.println("  Dismissals: " + session4.getTotalDismissals());
        System.out.println("  Qualifies: " + session4.qualifiesForStreak());
        
        tracker.recordSession(session4.qualifiesForStreak());
        System.out.println("  Streak after: " + tracker.getCurrentStreak() + " days\n");
        
        // Score calculation: 100 - 5 (1 violation) - 4 (2 dismissals) - 0 (< 1 min) = 91
        assertEquals(91, session4.getFocusScore(), "Score should be 91");
        assertTrue(session4.qualifiesForStreak(), "Score 91 should still qualify");
        assertEquals(1, tracker.getCurrentStreak(), "Streak unchanged (same day)");
        
        // Session 5: Poor performance, doesn't qualify
        System.out.println("Session 5: 30 minutes with many distractions");
        FocusSession session5 = new FocusSession(1800, Arrays.asList("Discord", "Twitter"), new ArrayList<>());
        
        // Multiple violations with many dismissals
        for (int i = 0; i < 8; i++) {
            session5.startViolation(i % 2 == 0 ? "Discord" : "Twitter");
            for (int j = 0; j < 3; j++) {
                session5.recordDismissal();
            }
            session5.addViolationDuration(30);
            session5.endCurrentViolation();
        }
        
        session5.complete(1800);
        
        System.out.println("  Duration: " + session5.getActualDurationMinutes() + " min");
        System.out.println("  Score: " + session5.getFocusScore());
        System.out.println("  Violations: " + session5.getViolationCount());
        System.out.println("  Dismissals: " + session5.getTotalDismissals());
        System.out.println("  Qualifies: " + session5.qualifiesForStreak());
        
        tracker.recordSession(session5.qualifiesForStreak());
        System.out.println("  Streak after: " + tracker.getCurrentStreak() + " days\n");
        
        assertFalse(session5.qualifiesForStreak(), "Poor session should not qualify");
        assertEquals(1, tracker.getCurrentStreak(), "Streak unchanged (didn't qualify)");
        
        // Verify persistence
        System.out.println("Testing persistence...");
        StreakTracker reloadedTracker = new StreakTracker();
        System.out.println("  Reloaded streak: " + reloadedTracker.getCurrentStreak() + " days");
        assertEquals(1, reloadedTracker.getCurrentStreak(), "Streak should persist");
        
        System.out.println("\n=== STREAK TEST COMPLETE ===\n");
        
        // Clean up
        tracker.resetStreak();
    }
}
