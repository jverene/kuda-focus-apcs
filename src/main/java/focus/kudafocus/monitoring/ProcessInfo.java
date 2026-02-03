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

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public String toString() {
        return String.format("ProcessInfo{name='%s', display='%s', pid=%d}",
                processName, displayName, pid);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ProcessInfo other = (ProcessInfo) obj;
        return pid == other.pid &&
                processName.equals(other.processName);
    }

    @Override
    public int hashCode() {
        return processName.hashCode() * 31 + pid;
    }
}
