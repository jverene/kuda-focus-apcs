package focus.kudafocus.monitoring;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;
import java.util.Locale;

/**
 * Chrome-focused website monitor for macOS.
 *
 * This monitor checks the URL of the active tab only when Google Chrome is
 * the frontmost application, then matches the host against blocked domains.
 */
public class ChromeWebsiteMonitor {

    /**
     * Checks if frontmost Chrome tab URL matches any blocked domain.
     *
     * @param blockedDomains List of blocked domains like "youtube.com"
     * @return Matched domain, or null if no match / not applicable
     */
    public String detectDistractingDomain(List<String> blockedDomains) {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (!os.contains("mac")) {
            return null;
        }

        String frontmostApp = runAppleScript(
                "tell application \"System Events\" to get name of first application process whose frontmost is true"
        );
        if (frontmostApp == null || !frontmostApp.equalsIgnoreCase("Google Chrome")) {
            return null;
        }

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

    private String extractHost(String url) {
        try {
            return new URI(url).getHost();
        } catch (Exception ignored) {
            return null;
        }
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
