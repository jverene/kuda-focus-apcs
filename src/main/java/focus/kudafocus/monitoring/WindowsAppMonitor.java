package focus.kudafocus.monitoring;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Windows-specific implementation of AppMonitor.
 * Demonstrates how the same abstract interface can have
 * completely different platform-specific implementations.
 *
 * This class uses Windows 'tasklist' command instead of macOS 'ps aux'.
 * External code doesn't need to know which OS it's running on!
 */
public class WindowsAppMonitor extends AppMonitor {

    /**
     * Common app name mappings for Windows
     * Maps process names to user-friendly display names
     */
    private static final String[][] APP_NAME_MAPPINGS = {
            {"chrome.exe", "Chrome"},
            {"firefox.exe", "Firefox"},
            {"msedge.exe", "Edge"},
            {"Discord.exe", "Discord"},
            {"steam.exe", "Steam"},
            {"Slack.exe", "Slack"},
            {"spotify.exe", "Spotify"},
            {"Code.exe", "VS Code"},
            {"idea64.exe", "IntelliJ"},
            {"pycharm64.exe", "PyCharm"}
    };

    /**
     * Creates a new Windows app monitor
     */
    public WindowsAppMonitor() {
        super();
    }

    /**
     * IMPLEMENTS ABSTRACT METHOD from AppMonitor.
     *
     * Gets currently running processes on Windows using 'tasklist' command.
     * This is the Windows-specific implementation.
     *
     * Process:
     * 1. Execute 'tasklist' command via ProcessBuilder
     * 2. Parse the output line by line
     * 3. Extract process names and PIDs
     * 4. Create ProcessInfo objects
     * 5. Return the list
     *
     * @return List of currently running processes
     */
    @Override
    protected List<ProcessInfo> getCurrentProcesses() {
        List<ProcessInfo> processes = new ArrayList<>();
        Set<String> seenProcesses = new HashSet<>();

        try {
            // Execute 'tasklist' command
            ProcessBuilder pb = new ProcessBuilder("tasklist");
            Process process = pb.start();

            // Read the output
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            int lineCount = 0;

            while ((line = reader.readLine()) != null) {
                lineCount++;

                // Skip header lines (first 3 lines)
                if (lineCount <= 3) {
                    continue;
                }

                // Parse process line
                ProcessInfo processInfo = parseProcessLine(line);
                if (processInfo != null) {
                    // Avoid duplicates
                    String key = processInfo.getProcessName().toLowerCase();
                    if (!seenProcesses.contains(key)) {
                        processes.add(processInfo);
                        seenProcesses.add(key);
                    }
                }
            }

            // Wait for command to complete
            process.waitFor();
            reader.close();

        } catch (Exception e) {
            System.err.println("Error getting Windows processes: " + e.getMessage());
        }

        return processes;
    }

    /**
     * IMPLEMENTS ABSTRACT METHOD from AppMonitor.
     *
     * Normalizes process names for Windows.
     * Removes .exe extension and standardizes names.
     *
     * @param rawProcessName Raw process name from system
     * @return Normalized process name
     */
    @Override
    protected String normalizeProcessName(String rawProcessName) {
        if (rawProcessName == null || rawProcessName.isEmpty()) {
            return rawProcessName;
        }

        String normalized = rawProcessName;

        // Remove .exe extension
        if (normalized.toLowerCase().endsWith(".exe")) {
            normalized = normalized.substring(0, normalized.length() - 4);
        }

        // Remove path prefixes if present
        if (normalized.contains("\\")) {
            int lastBackslash = normalized.lastIndexOf("\\");
            normalized = normalized.substring(lastBackslash + 1);
        }

        // Check for known app mappings
        for (String[] mapping : APP_NAME_MAPPINGS) {
            if ((rawProcessName + ".exe").equalsIgnoreCase(mapping[0]) ||
                    rawProcessName.equalsIgnoreCase(mapping[0])) {
                return mapping[1];
            }
        }

        return normalized.trim();
    }

    /**
     * Parses a single line from 'tasklist' output into a ProcessInfo object.
     *
     * tasklist format:
     * Image Name                     PID Session Name        Session#    Mem Usage
     * ========================= ======== ================ =========== ============
     * Discord.exe                   1234 Console                    1     12,345 K
     *
     * @param line Line from tasklist output
     * @return ProcessInfo object or null if line couldn't be parsed
     */
    private ProcessInfo parseProcessLine(String line) {
        try {
            // Split by whitespace, keeping multiple spaces as delimiters
            String[] parts = line.trim().split("\\s+");

            if (parts.length < 2) {
                return null;  // Invalid line
            }

            // Extract process name (first column)
            String processName = parts[0];

            // Extract PID (second column)
            int pid;
            try {
                pid = Integer.parseInt(parts[1].replace(",", ""));
            } catch (NumberFormatException e) {
                return null;  // Not a valid PID
            }

            // Skip system processes
            if (isSystemProcess(processName)) {
                return null;
            }

            // Create ProcessInfo
            String displayName = normalizeProcessName(processName);
            return new ProcessInfo(processName, displayName, pid);

        } catch (Exception e) {
            // Silently skip lines that can't be parsed
            return null;
        }
    }

    /**
     * Checks if a process is a system process that should be ignored.
     *
     * @param processName Process name to check
     * @return true if system process
     */
    private boolean isSystemProcess(String processName) {
        if (processName == null || processName.isEmpty()) {
            return true;
        }

        // Convert to lowercase for case-insensitive comparison
        String lower = processName.toLowerCase();

        // Windows system processes to ignore
        return lower.startsWith("system") ||
                lower.startsWith("svchost") ||
                lower.startsWith("csrss") ||
                lower.startsWith("smss") ||
                lower.startsWith("lsass") ||
                lower.startsWith("services") ||
                lower.startsWith("winlogon") ||
                lower.startsWith("dwm") ||
                lower.equals("tasklist.exe") ||
                lower.equals("cmd.exe") ||
                lower.equals("conhost.exe") ||
                lower.equals("java.exe") ||  // Don't detect ourselves!
                lower.equals("javaw.exe");
    }

    /**
     * Gets the list of user-facing applications (filters out system processes).
     *
     * @return List of user applications
     */
    public List<ProcessInfo> getUserApplications() {
        List<ProcessInfo> allProcesses = getCurrentProcesses();
        List<ProcessInfo> userApps = new ArrayList<>();

        for (ProcessInfo process : allProcesses) {
            if (!isSystemProcess(process.getProcessName())) {
                userApps.add(process);
            }
        }

        return userApps;
    }

    @Override
    public String toString() {
        return "WindowsAppMonitor{processes=" + getProcessCount() + "}";
    }
}
