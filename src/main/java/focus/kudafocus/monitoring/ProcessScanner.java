package focus.kudafocus.monitoring;

import java.util.ArrayList;
import java.util.List;

public class ProcessScanner {
    private final AppMonitor monitor;
    private List<ProcessInfo> cachedProcesses = new ArrayList<>();
    private long lastScanTime = 0;
    private final List<ProcessScanListener> listeners = new ArrayList<>();

    public interface ProcessScanListener {
    void onScan(List<ProcessInfo> processes);
    }
    public ProcessScanner(AppMonitor monitor) {
        this.monitor = monitor;
    }
    public ProcessScanner() {
        this(AppMonitor.createForCurrentOS());
    }

    public List<ProcessInfo> scan() {
    long now = System.currentTimeMillis();
    if (now - lastScanTime >= AppMonitor.SCAN_INTERVAL_MS) {
        cachedProcesses = monitor.getRunningProcesses(true);
        lastScanTime = now;
        notifyListeners();
    }
    return new ArrayList<>(cachedProcesses);
    }

    public List<ProcessInfo> getCachedProcesses() {
        return new ArrayList<>(cachedProcesses);
    }
    public void addListener(ProcessScanListener l) { listeners.add(l); }
    public void removeListener(ProcessScanListener l) { listeners.remove(l); }
    private void notifyListeners() {
    List<ProcessInfo> snapshot = getCachedProcesses();
    for (ProcessScanListener l : listeners) {
        l.onScan(snapshot);
        }
    }
}
