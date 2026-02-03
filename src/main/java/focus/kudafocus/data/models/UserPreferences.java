package focus.kudafocus.data.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User preferences and app registry data model.
 * Stores persistent user settings and app categorization.
 */
public class UserPreferences {

    /**
     * Default session duration in seconds (last selected)
     */
    private int defaultDuration;

    /**
     * Last selected apps to block
     */
    private List<String> lastSelectedApps;

    /**
     * App registry mapping app names to their metadata
     */
    private Map<String, AppEntry> appRegistry;

    // ===== CONSTRUCTOR =====

    /**
     * Creates default user preferences
     */
    public UserPreferences() {
        this.defaultDuration = 1500;  // 25 minutes (Pomodoro)
        this.lastSelectedApps = new ArrayList<>();
        this.appRegistry = new HashMap<>();
        initializeDefaultApps();
    }

    /**
     * Initialize with common apps pre-registered
     */
    private void initializeDefaultApps() {
        addApp("Discord", "Discord", "Social Media", true);
        addApp("Steam", "Steam", "Gaming", true);
        addApp("Instagram", "Instagram", "Social Media", true);
        addApp("Chrome", "Google Chrome", "Browser", false);
        addApp("Safari", "Safari", "Browser", false);
        addApp("Messages", "Messages", "Social Media", true);
        addApp("Slack", "Slack", "Communication", false);
        addApp("Spotify", "Spotify", "Entertainment", false);
    }

    /**
     * Adds an app to the registry
     *
     * @param processName Process name
     * @param displayName User-friendly display name
     * @param category Category
     * @param commonlyBlocked Whether it's commonly blocked
     */
    public void addApp(String processName, String displayName, String category, boolean commonlyBlocked) {
        appRegistry.put(processName, new AppEntry(processName, displayName, category, commonlyBlocked));
    }

    // ===== GETTERS AND SETTERS =====

    public int getDefaultDuration() {
        return defaultDuration;
    }

    public void setDefaultDuration(int defaultDuration) {
        this.defaultDuration = defaultDuration;
    }

    public List<String> getLastSelectedApps() {
        return lastSelectedApps;
    }

    public void setLastSelectedApps(List<String> lastSelectedApps) {
        this.lastSelectedApps = lastSelectedApps;
    }

    public Map<String, AppEntry> getAppRegistry() {
        return appRegistry;
    }

    public void setAppRegistry(Map<String, AppEntry> appRegistry) {
        this.appRegistry = appRegistry;
    }

    /**
     * App entry in the registry
     */
    public static class AppEntry {
        private String processName;
        private String displayName;
        private String category;
        private boolean commonlyBlocked;
        private String iconPath;

        public AppEntry(String processName, String displayName, String category, boolean commonlyBlocked) {
            this.processName = processName;
            this.displayName = displayName;
            this.category = category;
            this.commonlyBlocked = commonlyBlocked;
            this.iconPath = "";
        }

        public String getProcessName() {
            return processName;
        }

        public void setProcessName(String processName) {
            this.processName = processName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public boolean isCommonlyBlocked() {
            return commonlyBlocked;
        }

        public void setCommonlyBlocked(boolean commonlyBlocked) {
            this.commonlyBlocked = commonlyBlocked;
        }

        public String getIconPath() {
            return iconPath;
        }

        public void setIconPath(String iconPath) {
            this.iconPath = iconPath;
        }
    }
}
