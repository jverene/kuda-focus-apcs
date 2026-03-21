package focus.kudafocus.monitoring;

/**
 * Represents information about a running process.
 * This is a simple data class used by AppMonitor implementations.
 */
public class ProcessInfo {

    /**
     * Process name (as it appears in system process list)
     */
    private String processName;

    /**
     * User-friendly display name (if different from process name)
     */
    private String displayName;

    /**
     * Process ID (PID)
     */
    private int pid;

    /**
     * Whether this process is currently running
     */
    private boolean running;

    // ===== CONSTRUCTORS =====

    /**
     * Creates a new ProcessInfo with process name and PID
     *
     * @param processName Process name
     * @param pid Process ID
     */
    public ProcessInfo(String processName, int pid) {
        this.processName = processName;
        this.displayName = processName;  // Default to process name
        this.pid = pid;
        this.running = true;
    }

    /**
     * Creates a new ProcessInfo with all fields
     *
     * @param processName Process name
     * @param displayName Display name
     * @param pid Process ID
     */
    public ProcessInfo(String processName, String displayName, int pid) {
        this.processName = processName;
        this.displayName = displayName;
        this.pid = pid;
        this.running = true;
    }

    // ===== GETTERS =====

    /**
     * Get process name
     *
     * @return Process name
     */
    public String getProcessName() {
        return processName;
    }

    /**
     * Get display name
     *
     * @return Display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get process ID
     *
     * @return PID
     */
    public int getPid() {
        return pid;
    }

    /**
     * Check if process is running
     *
     * @return true if running
     */
    public boolean isRunning() {
        return running;
    }

    // ===== SETTERS =====

    /**
     * Updates the process name.
     *
     * @param processName The new process name.
     */
    public void setProcessName(String processName) {
        this.processName = processName;
    }

    /**
     * Updates the display name.
     *
     * @param displayName The new display name.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Updates the process ID.
     *
     * @param pid The new process ID.
     */
    public void setPid(int pid) {
        this.pid = pid;
    }

    /**
     * Updates the running status of the process.
     *
     * @param running {@code true} if the process is running; {@code false} otherwise.
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * Returns a string representation of the process information.
     *
     * @return A formatted string containing process details.
     */
    @Override
    public String toString() {
        return String.format("ProcessInfo{name='%s', display='%s', pid=%d}",
                processName, displayName, pid);
    }

    /**
     * Compares this process info with another object for equality.
     *
     * @param obj The object to compare with.
     * @return {@code true} if both objects represent the same process; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ProcessInfo other = (ProcessInfo) obj;
        return pid == other.pid &&
                processName.equals(other.processName);
    }

    /**
     * Returns the hash code for this process info.
     *
     * @return The calculated hash code.
     */
    @Override
    public int hashCode() {
        return processName.hashCode() * 31 + pid;
    }
}
