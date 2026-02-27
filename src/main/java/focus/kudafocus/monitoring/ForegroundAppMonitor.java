package focus.kudafocus.monitoring;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * Reads the current frontmost application.
 */
public class ForegroundAppMonitor {

    /**
     * Gets the frontmost application display name.
     *
     * @return Frontmost app name, or null if unavailable/unsupported
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
