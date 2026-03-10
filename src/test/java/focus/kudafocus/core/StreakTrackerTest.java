package focus.kudafocus.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StreakTracker
 *
 * Tests:
 * - New streak initialization
 * - Recording qualifying sessions
 * - Consecutive day tracking
 * - Streak breaking logic
 * - Same-day multiple sessions
 * - Persistence (save/load)
 */
class StreakTrackerTest {

    private StreakTracker tracker;
    private Path testDataFile;

    @BeforeEach
    void setUp() {
        // Each test gets a fresh tracker
        tracker = new StreakTracker();
        
        // Set up test data file path
        String userHome = System.getProperty("user.home");
        Path dataDir = Paths.get(userHome, ".kudafocus");
        testDataFile = dataDir.resolve("streak_data.json");
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up test data file after each test
        if (Files.exists(testDataFile)) {
            Files.delete(testDataFile);
        }
    }

    @Test
    void testNewTrackerStartsAtZero() {
        assertEquals(0, tracker.getCurrentStreak(), "New tracker should start at 0");
        assertNull(tracker.getLastQualifyingDate(), "New tracker should have no last qualifying date");
    }

    @Test
    void testFirstQualifyingSession() {
        tracker.recordSession(true);
        
        assertEquals(1, tracker.getCurrentStreak(), "First qualifying session should set streak to 1");
        assertEquals(LocalDate.now(), tracker.getLastQualifyingDate(), "Last qualifying date should be today");
    }

    @Test
    void testNonQualifyingSessionDoesNothing() {
        tracker.recordSession(false);
        
        assertEquals(0, tracker.getCurrentStreak(), "Non-qualifying session should not affect streak");
        assertNull(tracker.getLastQualifyingDate(), "Non-qualifying session should not set date");
    }

    @Test
    void testMultipleSessionsSameDay() {
        // Record first qualifying session
        tracker.recordSession(true);
        assertEquals(1, tracker.getCurrentStreak(), "First session should set streak to 1");
        
        // Record second qualifying session same day
        tracker.recordSession(true);
        assertEquals(1, tracker.getCurrentStreak(), "Second session same day should not increment");
        
        // Record third qualifying session same day
        tracker.recordSession(true);
        assertEquals(1, tracker.getCurrentStreak(), "Multiple sessions same day should only count once");
    }

    @Test
    void testResetStreak() {
        tracker.recordSession(true);
        assertEquals(1, tracker.getCurrentStreak(), "Streak should be 1 after recording");
        
        tracker.resetStreak();
        assertEquals(0, tracker.getCurrentStreak(), "Reset should set streak to 0");
        assertNull(tracker.getLastQualifyingDate(), "Reset should clear last qualifying date");
    }

    @Test
    void testPersistence() {
        // Record a session
        tracker.recordSession(true);
        assertEquals(1, tracker.getCurrentStreak());
        
        // Create a new tracker (simulates app restart)
        StreakTracker newTracker = new StreakTracker();
        
        // Should load the saved data
        assertEquals(1, newTracker.getCurrentStreak(), "New tracker should load saved streak");
        assertEquals(LocalDate.now(), newTracker.getLastQualifyingDate(), "New tracker should load saved date");
    }

    @Test
    void testPersistenceAcrossMultipleSessions() {
        // Day 1: Record a session
        tracker.recordSession(true);
        assertEquals(1, tracker.getCurrentStreak());
        
        // Simulate another session same day
        tracker.recordSession(true);
        assertEquals(1, tracker.getCurrentStreak());
        
        // Restart app
        StreakTracker newTracker = new StreakTracker();
        assertEquals(1, newTracker.getCurrentStreak(), "Streak should persist after app restart");
    }

    @Test
    void testLoadingCorruptedDataHandlesGracefully() throws IOException {
        // Write invalid JSON to the file
        Files.createDirectories(testDataFile.getParent());
        Files.writeString(testDataFile, "{invalid json}");
        
        // Should handle gracefully and start at 0
        StreakTracker newTracker = new StreakTracker();
        assertEquals(0, newTracker.getCurrentStreak(), "Corrupted data should result in streak of 0");
    }

    @Test
    void testNonQualifyingAfterQualifyingSession() {
        tracker.recordSession(true);
        assertEquals(1, tracker.getCurrentStreak());
        
        // Non-qualifying session should not affect existing streak
        tracker.recordSession(false);
        assertEquals(1, tracker.getCurrentStreak(), "Non-qualifying session should not change streak");
    }
}
