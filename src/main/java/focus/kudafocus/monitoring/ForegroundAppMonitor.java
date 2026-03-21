package focus.kudafocus.monitoring;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * A monitor that identifies the application currently in the foreground.
 *
 * This class provides methods to retrieve the name of the application that has
 * the current focus on the user's desktop.
 */
public class ForegroundAppMonitor {

    /**
     * Retrieves the display name of the application currently in the foreground.
     * Currently supported only on macOS.
     *
     * @return The name of the frontmost application, or {@code null} if unavailable or unsupported.
     */
    public String getFrontmostApplication() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (!os.contains("mac")) {
            return null;
        }

        return runAppleScript(
                "tell application \"System Events\" to get name of first application process whose frontmost is true"
        );
    }

    /**
     * Executes an AppleScript command and returns its output.
     *
     * @param script The AppleScript command to execute.
     * @return The trimmed output of the script, or {@code null} if execution fails.
     */
    private String runAppleScript(String script) {
        try {
            Process process = new ProcessBuilder("osascript", "-e", script).start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String output = reader.readLine();
                process.waitFor();
                return output == null ? null : output.trim();
            }
        } catch (Exception ignored) {
            return null;
        }
    }
}
