package focus.kudafocus.monitoring;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for process monitoring demonstrating ABSTRACTION in OOP.
 *
 * Abstraction Demonstration:
 * - Defines WHAT monitoring should do (interface)
 * - Hides HOW it's implemented (platform-specific details)
 * - Abstract method: getCurrentProcesses() - must be implemented by subclasses
 * - Concrete method: checkForViolations() - shared across all platforms
 *
 * This design allows:
 * - Platform-independent code to use AppMonitor without knowing the OS
 * - Easy addition of new platforms (Linux, etc.) by extending this class
 * - Shared violation detection logic (no code duplication)
 * - Runtime polymorphism (can swap implementations at runtime)
 *
 * Subclasses:
 * - MacOSAppMonitor: Uses 'ps aux' command
 * - WindowsAppMonitor: Uses 'tasklist' command
 *
 * Benefits of Abstraction:
 * - Separation of interface and implementation
 * - Code reuse through shared methods
 * - Platform independence
 * - Easy to extend for new platforms
 */
public abstract class AppMonitor {

    /**
     * Cached list of currently running processes
     * Updated each time getCurrentProcesses() is called
     */
    protected List<ProcessInfo> cachedProcesses;

    /**
     * Last time processes were scanned
     */
    protected long lastScanTime;

    /**
     * Minimum interval between scans in milliseconds
     * Prevents excessive system calls
     */
    protected static final long SCAN_INTERVAL_MS = 1000;

    // ===== CONSTRUCTOR =====

    /**
     * Creates a new AppMonitor
     */
    public AppMonitor() {
        this.cachedProcesses = new ArrayList<>();
        this.lastScanTime = 0;
    }

    // ===== ABSTRACT METHODS =====
    // These MUST be implemented by platform-specific subclasses

    /**
     * ABSTRACT METHOD - Platform-specific implementation required!
     *
     * Gets the list of currently running processes on this OS.
     * Each platform implements this differently:
     * - macOS: Parse output of 'ps aux' command
     * - Windows: Parse output of 'tasklist' command
     * - Linux: Read from /proc or use 'ps' command
     *
     * This method is PROTECTED, meaning subclasses must implement it,
     * but external code doesn't call it directly.
     *
     * @return List of running processes
     */
    protected abstract List<ProcessInfo> getCurrentProcesses();

    /**
     * ABSTRACT METHOD - Platform-specific process name extraction.
     *
     * Converts raw process names to normalized form.
     * For example:
     * - macOS might see "Google Chrome Helper"
     * - Windows might see "chrome.exe"
     * - Both should normalize to "Chrome"
     *
     * @param rawProcessName Raw process name from system
     * @return Normalized process name
     */
    protected abstract String normalizeProcessName(String rawProcessName);

    // ===== CONCRETE METHODS =====
    // These are SHARED across all platforms (no need to override)

    /**
     * SHARED METHOD - Works the same on all platforms!
     *
     * Checks if any blocked apps are currently running.
     * This method uses the abstract getCurrentProcesses() method,
     * but the violation detection logic is the same regardless of OS.
     *
     * @param blockedApps List of app names to check for
     * @return List of blocked apps that are currently running
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
     * SHARED METHOD - Checks if a specific app is currently running.
     *
     * @param appName Name of app to check
     * @return true if app is running
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
     * SHARED METHOD - Gets all currently running processes.
     * Uses caching to avoid excessive system calls.
     *
     * @param forceRefresh If true, force a new scan regardless of interval
     * @return List of running processes
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
     * SHARED METHOD - Gets all currently running processes (uses cache).
     *
     * @return List of running processes
     */
    public List<ProcessInfo> getRunningProcesses() {
        return getRunningProcesses(false);
    }

    /**
     * SHARED METHOD - Filters processes by category.
     *
     * @param category Category to filter by (e.g., "browser", "game")
     * @return List of processes in that category
     */
    public List<ProcessInfo> getProcessesByCategory(String category) {
        // This could be extended to use an app registry for categorization
        // For now, just return all processes
        return getRunningProcesses();
    }

    /**
     * SHARED METHOD - Clears the process cache.
     * Forces a fresh scan on next call.
     */
    public void clearCache() {
        cachedProcesses.clear();
        lastScanTime = 0;
    }

    /**
     * SHARED METHOD - Gets the number of currently cached processes.
     *
     * @return Number of processes
     */
    public int getProcessCount() {
        return cachedProcesses.size();
    }

    /**
     * FACTORY METHOD - Creates the appropriate AppMonitor for this OS.
     * This demonstrates polymorphism - returns different subclass based on OS.
     *
     * @return AppMonitor instance for current operating system
     */
    public static AppMonitor createForCurrentOS() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("mac")) {
            return new MacOSAppMonitor();
        } else if (os.contains("win")) {
            return new WindowsAppMonitor();
        } else {
            // Default to macOS for unsupported systems
            System.err.println("Warning: Unsupported OS '" + os + "', using macOS monitor");
            return new MacOSAppMonitor();
        }
    }
}
