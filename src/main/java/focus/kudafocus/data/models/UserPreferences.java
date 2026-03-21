package focus.kudafocus.data.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the user preferences and application registry data model.
 * This class stores persistent user settings, last selected focus targets,
 * and categorization metadata for applications.
 */
public class UserPreferences {

    /**
     * The default duration for focus sessions in seconds.
     */
    private int defaultDuration;

    /**
     * The list of applications that were last selected to be blocked.
     */
    private List<String> lastSelectedApps;

    /**
     * The list of websites that were last selected to be blocked.
     */
    private List<String> lastSelectedWebsites;

    /**
     * The registry mapping application process names to their respective metadata.
     */
    private Map<String, AppEntry> appRegistry;

    // ===== CONSTRUCTOR =====

    /**
     * Initializes a new instance of UserPreferences with default values.
     * Default duration is set to 25 minutes (1500 seconds).
     */
    public UserPreferences() {
        this.defaultDuration = 1500;  // 25 minutes (Pomodoro)
        this.lastSelectedApps = new ArrayList<>();
        this.lastSelectedWebsites = new ArrayList<>();
        this.appRegistry = new HashMap<>();
        initializeDefaultApps();
    }

    /**
     * Populates the application registry with a set of predefined common applications.
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
     * Registers a new application in the application registry.
     *
     * @param processName the name of the application process
     * @param displayName the user-friendly name to display in the UI
     * @param category the functional category of the application
     * @param commonlyBlocked true if the application is typically blocked by users
     */
    public void addApp(String processName, String displayName, String category, boolean commonlyBlocked) {
        appRegistry.put(processName, new AppEntry(processName, displayName, category, commonlyBlocked));
    }

    // ===== GETTERS AND SETTERS =====

    /**
     * Retrieves the default duration for new focus sessions.
     *
     * @return the default duration in seconds
     */
    public int getDefaultDuration() {
        return defaultDuration;
    }

    /**
     * Sets the default duration for new focus sessions.
     *
     * @param defaultDuration the default duration in seconds
     */
    public void setDefaultDuration(int defaultDuration) {
        this.defaultDuration = defaultDuration;
    }

    /**
     * Retrieves the list of applications that were last selected for blocking.
     *
     * @return the list of process names for last selected apps
     */
    public List<String> getLastSelectedApps() {
        return lastSelectedApps;
    }

    /**
     * Sets the list of applications that were last selected for blocking.
     *
     * @param lastSelectedApps the list of process names to store
     */
    public void setLastSelectedApps(List<String> lastSelectedApps) {
        this.lastSelectedApps = lastSelectedApps;
    }

    /**
     * Retrieves the list of websites that were last selected for blocking.
     *
     * @return the list of URLs for last selected websites
     */
    public List<String> getLastSelectedWebsites() {
        return lastSelectedWebsites;
    }

    /**
     * Sets the list of websites that were last selected for blocking.
     *
     * @param lastSelectedWebsites the list of URLs to store
     */
    public void setLastSelectedWebsites(List<String> lastSelectedWebsites) {
        this.lastSelectedWebsites = lastSelectedWebsites;
    }

    /**
     * Retrieves the complete application registry.
     *
     * @return a map of process names to application entries
     */
    public Map<String, AppEntry> getAppRegistry() {
        return appRegistry;
    }

    /**
     * Sets the application registry.
     *
     * @param appRegistry the map of application entries to assign
     */
    public void setAppRegistry(Map<String, AppEntry> appRegistry) {
        this.appRegistry = appRegistry;
    }

    /**
     * Represents a single application entry within the registry.
     * Contains metadata used for display and categorization.
     */
    public static class AppEntry {
        /**
         * The name of the application process.
         */
        private String processName;

        /**
         * The human-readable name of the application.
         */
        private String displayName;

        /**
         * The functional category assigned to the application.
         */
        private String category;

        /**
         * Indicates if the application is frequently blocked by users.
         */
        private boolean commonlyBlocked;

        /**
         * The filesystem path to the application's icon.
         */
        private String iconPath;

        /**
         * Initializes a new instance of AppEntry with specified metadata.
         *
         * @param processName the name of the application process
         * @param displayName the user-friendly name for display
         * @param category the functional category
         * @param commonlyBlocked true if the application is frequently blocked
         */
        public AppEntry(String processName, String displayName, String category, boolean commonlyBlocked) {
            this.processName = processName;
            this.displayName = displayName;
            this.category = category;
            this.commonlyBlocked = commonlyBlocked;
            this.iconPath = "";
        }

        /**
         * Retrieves the process name of the application.
         *
         * @return the process name
         */
        public String getProcessName() {
            return processName;
        }

        /**
         * Sets the process name of the application.
         *
         * @param processName the process name to assign
         */
        public void setProcessName(String processName) {
            this.processName = processName;
        }

        /**
         * Retrieves the display name of the application.
         *
         * @return the display name
         */
        public String getDisplayName() {
            return displayName;
        }

        /**
         * Sets the display name of the application.
         *
         * @param displayName the display name to assign
         */
        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        /**
         * Retrieves the category of the application.
         *
         * @return the category name
         */
        public String getCategory() {
            return category;
        }

        /**
         * Sets the category of the application.
         *
         * @param category the category name to assign
         */
        public void setCategory(String category) {
            this.category = category;
        }

        /**
         * Checks if the application is commonly blocked.
         *
         * @return true if commonly blocked; false otherwise
         */
        public boolean isCommonlyBlocked() {
            return commonlyBlocked;
        }

        /**
         * Sets whether the application is commonly blocked.
         *
         * @param commonlyBlocked true if commonly blocked; false otherwise
         */
        public void setCommonlyBlocked(boolean commonlyBlocked) {
            this.commonlyBlocked = commonlyBlocked;
        }

        /**
         * Retrieves the path to the application's icon asset.
         *
         * @return the icon file path
         */
        public String getIconPath() {
            return iconPath;
        }

        /**
         * Sets the path to the application's icon asset.
         *
         * @param iconPath the icon file path to assign
         */
        public void setIconPath(String iconPath) {
            this.iconPath = iconPath;
        }
    }
}
