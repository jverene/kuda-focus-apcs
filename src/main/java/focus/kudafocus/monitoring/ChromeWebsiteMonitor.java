package focus.kudafocus.monitoring;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;
import java.util.Locale;

/**
 * A monitor for tracking active website usage within Google Chrome on macOS.
 *
 * This monitor identifies the URL of the active tab when Google Chrome is the frontmost
 * application and checks it against a list of blocked domains to detect distractions.
 */
public class ChromeWebsiteMonitor {

    /**
     * Detects if the frontmost Google Chrome tab is displaying a distracting domain.
     * This check is only performed if the operating system is macOS and Chrome is the
     * active application with a visible window.
     *
     * @param blockedDomains A list of domains to monitor for violations (e.g., "youtube.com").
     * @return The matched domain name if a violation is detected; {@code null} otherwise.
     */
    public String detectDistractingDomain(List<String> blockedDomains) {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (!os.contains("mac")) {
            return null;
        }

        // Check if Chrome is the frontmost application
        String frontmostApp = runAppleScript(
                "tell application \"System Events\" to get name of first application process whose frontmost is true"
        );
        if (frontmostApp == null || !frontmostApp.equalsIgnoreCase("Google Chrome")) {
            return null;
        }

        // Verify Chrome window is actually visible (not minimized)
        String chromeVisible = runAppleScript(
                "tell application \"Google Chrome\" to return (count of windows) > 0"
        );
        if (chromeVisible == null || !chromeVisible.equalsIgnoreCase("true")) {
            return null;
        }

        // Get the URL of the active tab
        String currentUrl = runAppleScript(
                "tell application \"Google Chrome\" to get URL of active tab of front window"
        );
        if (currentUrl == null || currentUrl.isBlank()) {
            return null;
        }

        String host = extractHost(currentUrl);
        if (host == null) {
            return null;
        }

        String normalizedHost = host.toLowerCase(Locale.ROOT);
        for (String domain : blockedDomains) {
            String normalizedDomain = domain.toLowerCase(Locale.ROOT);
            if (normalizedHost.equals(normalizedDomain) || normalizedHost.endsWith("." + normalizedDomain)) {
                return domain;
            }
        }
        return null;
    }

    /**
     * Extracts the host portion of a given URL.
     *
     * @param url The full URL string.
     * @return The extracted host name, or {@code null} if the URL is invalid.
     */
    private String extractHost(String url) {
        try {
            return new URI(url).getHost();
        } catch (Exception ignored) {
            return null;
        }
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
