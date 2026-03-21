package focus.kudafocus.monitoring;

import java.util.ArrayList;
import java.util.List;

/**
 * Scans and caches running processes using a platform-specific monitor.
 * Provides a listener mechanism to notify when a new scan has occurred.
 * This class ensures that scans are not performed more frequently than
 * the defined interval in AppMonitor.
 */
public class ProcessScanner {
    /**
     * The platform-specific monitor used to retrieve running processes.
     */
    private final AppMonitor monitor;

    /**
     * The list of processes captured during the most recent scan.
     */
    private List<ProcessInfo> cachedProcesses = new ArrayList<>();

    /**
     * The timestamp of the last successful scan in milliseconds.
     */
    private long lastScanTime = 0;

    /**
     * The list of listeners to be notified upon completion of a scan.
     */
    private final List<ProcessScanListener> listeners = new ArrayList<>();

    /**
     * Listener interface for receiving notifications when a process scan is completed.
     */
    public interface ProcessScanListener {
        /**
         * Called when a new set of processes has been scanned.
         *
         * @param processes The list of currently running processes.
         */
        void onScan(List<ProcessInfo> processes);
    }

    /**
     * Creates a new ProcessScanner with a specific AppMonitor.
     *
     * @param monitor The AppMonitor implementation to use for scanning.
     */
    public ProcessScanner(AppMonitor monitor) {
        this.monitor = monitor;
    }

    /**
     * Creates a new ProcessScanner using the default monitor for the current operating system.
     */
    public ProcessScanner() {
        this(AppMonitor.createForCurrentOS());
    }

    /**
     * Performs a scan of running processes if the scan interval has elapsed.
     * If a new scan is performed, listeners are notified.
     *
     * @return A list of the most recently scanned processes.
     */
    public List<ProcessInfo> scan() {
        long now = System.currentTimeMillis();
        if (now - lastScanTime >= AppMonitor.SCAN_INTERVAL_MS) {
            cachedProcesses = monitor.getRunningProcesses(true);
            lastScanTime = now;
            notifyListeners();
        }
        return new ArrayList<>(cachedProcesses);
    }

    /**
     * Retrieves the list of processes from the most recent scan without triggering a new scan.
     *
     * @return A list of cached processes.
     */
    public List<ProcessInfo> getCachedProcesses() {
        return new ArrayList<>(cachedProcesses);
    }

    /**
     * Adds a listener to be notified of scan events.
     *
     * @param l The listener to add.
     */
    public void addListener(ProcessScanListener l) {
        listeners.add(l);
    }

    /**
     * Removes a listener from the notification list.
     *
     * @param l The listener to remove.
     */
    public void removeListener(ProcessScanListener l) {
        listeners.remove(l);
    }

    /**
     * Notifies all registered listeners that a new scan has been completed.
     */
    private void notifyListeners() {
        List<ProcessInfo> snapshot = getCachedProcesses();
        for (ProcessScanListener l : listeners) {
            l.onScan(snapshot);
        }
    }
}
