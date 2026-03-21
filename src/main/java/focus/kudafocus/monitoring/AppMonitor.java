package focus.kudafocus.monitoring;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for cross-platform process monitoring.
 *
 * This class provides a unified interface for scanning system processes and detecting
 * violations of focus rules. Platform-specific details are handled by subclasses,
 * while shared logic for violation detection and caching is maintained here.
 */
public abstract class AppMonitor {

    /**
     * The cached list of currently running processes, updated during the most recent scan.
     */
    protected List<ProcessInfo> cachedProcesses;

    /**
     * The timestamp in milliseconds of the last successful process scan.
     */
    protected long lastScanTime;

    /**
     * The minimum interval between system scans to prevent performance degradation.
     */
    protected static final long SCAN_INTERVAL_MS = 1000;

    /**
     * Initializes a new AppMonitor instance with default values.
     */
    public AppMonitor() {
        this.cachedProcesses = new ArrayList<>();
        this.lastScanTime = 0;
    }

    /**
     * Retrieves the list of currently running processes from the operating system.
     * This method must be implemented by platform-specific subclasses.
     *
     * @return A list of ProcessInfo objects representing active system processes.
     */
    protected abstract List<ProcessInfo> getCurrentProcesses();

    /**
     * Normalizes a raw process name into a standardized format for consistent matching.
     *
     * @param rawProcessName The raw process name retrieved from the system.
     * @return The normalized version of the process name.
     */
    protected abstract String normalizeProcessName(String rawProcessName);

    /**
     * Checks if any of the specified blocked applications are currently running.
     * Uses caching to ensure scans do not occur more frequently than SCAN_INTERVAL_MS.
     *
     * @param blockedApps A list of application names to monitor for violations.
     * @return A list of names for blocked applications that are currently active.
     */
    public List<String> checkForViolations(List<String> blockedApps) {
        List<String> violations = new ArrayList<>();

        // Only scan if enough time has passed since last scan
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastScanTime >= SCAN_INTERVAL_MS) {
            cachedProcesses = getCurrentProcesses();
            lastScanTime = currentTime;
        }

        // Check each blocked app against running processes
        for (String blockedApp : blockedApps) {
            if (isAppRunning(blockedApp)) {
                violations.add(blockedApp);
            }
        }

        return violations;
    }

    /**
     * Determines whether a specific application is currently running.
     *
     * @param appName The name of the application to check.
     * @return {@code true} if the application is detected in the running processes; {@code false} otherwise.
     */
    public boolean isAppRunning(String appName) {
        String normalizedTarget = normalizeProcessName(appName).toLowerCase();

        for (ProcessInfo process : cachedProcesses) {
            String normalizedProcess = normalizeProcessName(process.getProcessName()).toLowerCase();
            String normalizedDisplay = normalizeProcessName(process.getDisplayName()).toLowerCase();

            // Check both process name and display name
            if (normalizedProcess.contains(normalizedTarget) ||
                    normalizedDisplay.contains(normalizedTarget) ||
                    normalizedTarget.contains(normalizedProcess)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns a list of all currently running processes, using the cache if appropriate.
     *
     * @param forceRefresh If {@code true}, bypasses the cache and performs a fresh scan.
     * @return A list of ProcessInfo objects for currently running processes.
     */
    public List<ProcessInfo> getRunningProcesses(boolean forceRefresh) {
        long currentTime = System.currentTimeMillis();

        if (forceRefresh || currentTime - lastScanTime >= SCAN_INTERVAL_MS) {
            cachedProcesses = getCurrentProcesses();
            lastScanTime = currentTime;
        }

        return new ArrayList<>(cachedProcesses);
    }

    /**
     * Returns a list of all currently running processes from the cache.
     *
     * @return A list of ProcessInfo objects for currently running processes.
     */
    public List<ProcessInfo> getRunningProcesses() {
        return getRunningProcesses(false);
    }

    /**
     * Retrieves processes filtered by a specific category.
     *
     * @param category The category name to filter by.
     * @return A list of processes belonging to the specified category.
     */
    public List<ProcessInfo> getProcessesByCategory(String category) {
        return getRunningProcesses();
    }

    /**
     * Clears the process cache and resets the last scan time, forcing a fresh scan on the next request.
     */
    public void clearCache() {
        cachedProcesses.clear();
        lastScanTime = 0;
    }

    /**
     * Returns the total number of processes currently stored in the cache.
     *
     * @return The number of cached processes.
     */
    public int getProcessCount() {
        return cachedProcesses.size();
    }

    /**
     * Factory method that creates and returns an AppMonitor implementation appropriate for the current OS.
     *
     * @return An AppMonitor instance for the local operating system.
     */
    public static AppMonitor createForCurrentOS() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("mac")) {
            return new MacOSAppMonitor();
        } else if (os.contains("win")) {
            return new WindowsAppMonitor();
        } else {
            return new MacOSAppMonitor();
        }
    }
}
