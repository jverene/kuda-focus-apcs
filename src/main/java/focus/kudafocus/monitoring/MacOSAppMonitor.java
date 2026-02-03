package focus.kudafocus.monitoring;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * macOS-specific implementation of AppMonitor.
 * Demonstrates how ABSTRACTION allows platform-specific code
 * while maintaining a common interface.
 *
 * This class extends AppMonitor and implements the abstract methods
 * using macOS-specific system commands.
 */
public class MacOSAppMonitor extends AppMonitor {

    /**
     * Common app name mappings for macOS
     * Maps process names to user-friendly display names
     */
    private static final String[][] APP_NAME_MAPPINGS = {
            {"Google Chrome", "Chrome"},
            {"Google Chrome Helper", "Chrome"},
            {"Safari", "Safari"},
            {"Discord", "Discord"},
            {"Steam", "Steam"},
            {"Messages", "Messages"},
            {"Slack", "Slack"},
            {"Spotify", "Spotify"},
            {"Firefox", "Firefox"},
            {"Microsoft Edge", "Edge"},
            {"Visual Studio Code", "VS Code"},
            {"IntelliJ IDEA", "IntelliJ"},
            {"PyCharm", "PyCharm"}
    };

    /**
     * Creates a new macOS app monitor
     */
    public MacOSAppMonitor() {
        super();
    }

    /**
     * IMPLEMENTS ABSTRACT METHOD from AppMonitor.
     *
     * Gets currently running processes on macOS using the 'ps aux' command.
     * This is the macOS-specific implementation.
     *
     * Process:
     * 1. Execute 'ps aux' command via ProcessBuilder
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
            // Execute 'ps aux' command
            ProcessBuilder pb = new ProcessBuilder("ps", "aux");
            Process process = pb.start();

            // Read the output
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                // Skip header line
                if (firstLine) {
                    firstLine = false;
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
            System.err.println("Error getting macOS processes: " + e.getMessage());
        }

        return processes;
    }

    /**
     * IMPLEMENTS ABSTRACT METHOD from AppMonitor.
     *
     * Normalizes process names for macOS.
     * Removes file extensions, helpers, and standardizes names.
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

        // Remove common macOS process suffixes
        normalized = normalized.replace(" Helper", "");
        normalized = normalized.replace(".app", "");
        normalized = normalized.replace(".exe", "");

        // Remove path prefixes if present
        if (normalized.contains("/")) {
            int lastSlash = normalized.lastIndexOf("/");
            normalized = normalized.substring(lastSlash + 1);
        }

        // Check for known app mappings
        for (String[] mapping : APP_NAME_MAPPINGS) {
            if (normalized.equalsIgnoreCase(mapping[0])) {
                return mapping[1];
            }
        }

        return normalized.trim();
    }

    /**
     * Parses a single line from 'ps aux' output into a ProcessInfo object.
     *
     * ps aux format (columns):
     * USER  PID  %CPU %MEM    VSZ   RSS  TT  STAT STARTED      TIME COMMAND
     *
     * Example line:
     * hjiang  1234  0.5  2.1 1234567 123456 ??  S    3:45PM   1:23.45 /Applications/Discord.app/Contents/MacOS/Discord
     *
     * @param line Line from ps aux output
     * @return ProcessInfo object or null if line couldn't be parsed
     */
    private ProcessInfo parseProcessLine(String line) {
        try {
            // Split by whitespace
            String[] parts = line.trim().split("\\s+");

            if (parts.length < 11) {
                return null;  // Invalid line
            }

            // Extract PID (column 1, 0-indexed)
            int pid = Integer.parseInt(parts[1]);

            // Extract command (column 10+, 0-indexed)
            // Command may contain spaces, so join remaining parts
            StringBuilder commandBuilder = new StringBuilder();
            for (int i = 10; i < parts.length; i++) {
                if (i > 10) commandBuilder.append(" ");
                commandBuilder.append(parts[i]);
            }
            String command = commandBuilder.toString();

            // Extract process name from command
            String processName = extractProcessName(command);

            // Skip system processes and grep itself
            if (isSystemProcess(processName) || processName.equals("ps") || processName.equals("grep")) {
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
     * Extracts the process name from a command string.
     *
     * @param command Full command string
     * @return Process name
     */
    private String extractProcessName(String command) {
        // Remove command-line arguments
        int spaceIndex = command.indexOf(' ');
        if (spaceIndex > 0) {
            command = command.substring(0, spaceIndex);
        }

        // Get just the filename from the path
        if (command.contains("/")) {
            int lastSlash = command.lastIndexOf('/');
            command = command.substring(lastSlash + 1);
        }

        // Extract app name from .app bundle path
        if (command.endsWith(".app")) {
            // Remove .app extension
            command = command.substring(0, command.length() - 4);
        }

        // Special handling for macOS app bundles
        // E.g., "/Applications/Discord.app/Contents/MacOS/Discord" -> "Discord"
        if (command.contains("/Contents/MacOS/")) {
            int macosIndex = command.indexOf("/Contents/MacOS/");
            command = command.substring(macosIndex + "/Contents/MacOS/".length());
        }

        return command;
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

        // macOS system processes to ignore
        return lower.startsWith("kernel") ||
                lower.startsWith("launchd") ||
                lower.startsWith("com.apple") ||
                lower.startsWith("system") ||
                lower.startsWith("usr/") ||
                lower.startsWith("/system/") ||
                lower.equals("ps") ||
                lower.equals("grep") ||
                lower.equals("bash") ||
                lower.equals("sh") ||
                lower.equals("java");  // Don't detect ourselves!
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
        return "MacOSAppMonitor{processes=" + getProcessCount() + "}";
    }
}
