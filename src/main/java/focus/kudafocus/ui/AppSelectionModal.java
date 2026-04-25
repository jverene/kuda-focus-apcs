package focus.kudafocus.ui;

import focus.kudafocus.monitoring.AppMonitor;
import focus.kudafocus.monitoring.ProcessInfo;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a modal dialog for selecting applications and websites to be blocked during a focus session.
 *
 * This stage allows users to search and select from currently running processes, common
 * distracting applications, and manually enter website domains. It provides features
 * for quick selection of common distractions and automatic refreshing of the running
 * process list.
 */
public class AppSelectionModal extends Stage {

    /**
     * A map of common distracting desktop applications and their categories.
     * Only includes apps that have native macOS/Windows processes.
     */
    private static final Map<String, String> COMMON_DISTRACTIONS = createCommonDistractions();

    /**
     * A map of commonly distracting websites and their categories.
     * These are pre-filled in the website text area by "Select All Distracting".
     */
    private static final Map<String, String> COMMON_DISTRACTING_SITES = createCommonDistractingSites();

    /**
     * The monitor used to retrieve currently running processes.
     */
    private final AppMonitor appMonitor;

    /**
     * The set of currently selected application names.
     */
    private final Set<String> selectedApps;

    /**
     * The list of all available applications for selection.
     */
    private final ObservableList<String> allApps;

    /**
     * The container for displaying the list of applications in the UI.
     */
    private final VBox appListContainer;

    /**
     * The label displaying the current selection status.
     */
    private final Label statusLabel;

    /**
     * The text field for searching and filtering the application list.
     */
    private final TextField searchField;

    /**
     * The text area for entering comma-separated website domains.
     */
    private final TextArea websitesTextArea;

    /**
     * The row of quick-select chips for common distracting websites.
     */
    private final HBox siteChipsRow;

    /**
     * The theme providing the color palette for the modal.
     */
    private final Theme theme;

    /**
     * The timeline used for automatically refreshing the application list.
     */
    private Timeline refreshTimeline;

    /**
     * Indicates whether the user confirmed their selections.
     */
    private boolean confirmed;

    /**
     * Constructs an app selection modal with the default dark theme.
     *
     * @param owner The parent window for the modal.
     * @param initiallySelectedApps The list of applications initially selected.
     */
    public AppSelectionModal(Window owner, List<String> initiallySelectedApps) {
        this(owner, initiallySelectedApps, new ArrayList<>(), new DarkTheme());
    }

    /**
     * Constructs an app selection modal with the default dark theme.
     *
     * @param owner The parent window for the modal.
     * @param initiallySelectedApps The list of applications initially selected.
     * @param initiallySelectedWebsites The list of websites initially selected.
     */
    public AppSelectionModal(Window owner, List<String> initiallySelectedApps, List<String> initiallySelectedWebsites) {
        this(owner, initiallySelectedApps, initiallySelectedWebsites, new DarkTheme());
    }

    /**
     * Constructs an app selection modal with the specified theme.
     *
     * @param owner The parent window for the modal.
     * @param initiallySelectedApps The list of applications initially selected.
     * @param initiallySelectedWebsites The list of websites initially selected.
     * @param theme The theme providing the color palette for the modal.
     */
    public AppSelectionModal(Window owner, List<String> initiallySelectedApps, List<String> initiallySelectedWebsites, Theme theme) {
        this.theme = theme;
        this.appMonitor = AppMonitor.createForCurrentOS();
        this.selectedApps = new HashSet<>(initiallySelectedApps);
        this.allApps = FXCollections.observableArrayList();
        this.appListContainer = new VBox(UIConstants.SPACING_SM);
        this.statusLabel = new Label();
        this.searchField = new TextField();
        this.websitesTextArea = new TextArea();
        this.siteChipsRow = new HBox(UIConstants.SPACING_SM);
        this.confirmed = false;

        // Initialize websites text area with initial values
        if (initiallySelectedWebsites != null && !initiallySelectedWebsites.isEmpty()) {
            websitesTextArea.setText(String.join(", ", initiallySelectedWebsites));
        }

        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Select Apps & Websites to Block");
        setResizable(false);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(UIConstants.PADDING_STANDARD));
        root.setStyle("-fx-background-color: " + toRGBCode(theme.getBackgroundPrimary()) + ";" +
                      "-fx-base: " + toRGBCode(theme.getBackgroundPrimary()) + ";" +
                      "-fx-control-inner-background: " + toRGBCode(theme.getBackgroundSecondary()) + ";" +
                      "-fx-text-background-color: " + toRGBCode(theme.getTextPrimary()) + ";");

        VBox content = new VBox(UIConstants.SPACING_MD);
        content.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Select apps and websites to block");
        titleLabel.setFont(UIConstants.getHeadingFont());
        titleLabel.setTextFill(theme.getTextPrimary());

        // Apps section
        Label appsLabel = new Label("Applications:");
        appsLabel.setFont(UIConstants.getBodyFont());
        appsLabel.setTextFill(theme.getTextPrimary());

        searchField.setPromptText("Search running apps...");
        searchField.setFont(UIConstants.getBodyFont());
        searchField.setStyle(
                "-fx-background-color: " + toRGBCode(theme.getBackgroundSecondary()) + ";" +
                        "-fx-text-fill: " + toRGBCode(theme.getTextPrimary()) + ";" +
                        "-fx-prompt-text-fill: " + toRGBCode(theme.getTextMuted()) + ";"
        );

        statusLabel.setFont(UIConstants.getSmallFont());
        statusLabel.setTextFill(theme.getTextSecondary());

        ScrollPane scrollPane = new ScrollPane(appListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(200);
        scrollPane.setStyle("-fx-background: " + toRGBCode(theme.getBackgroundPrimary()) + ";");

        HBox quickActionRow = new HBox(UIConstants.SPACING_SM);
        quickActionRow.setAlignment(Pos.CENTER_LEFT);

        Button selectAllDistractingButton = new Button("Select All Distracting");
        selectAllDistractingButton.setFont(UIConstants.getSmallFont());
        UIConstants.setupButtonAnimation(selectAllDistractingButton);
        selectAllDistractingButton.setOnAction(event -> {
            selectedApps.addAll(COMMON_DISTRACTIONS.keySet());
            // Activate all site chips
            for (javafx.scene.Node node : siteChipsRow.getChildren()) {
                if (node instanceof Button) {
                    node.setUserData(true);
                    updateChipStyle((Button) node, true);
                }
            }
            syncSitesFromChips();
            renderAppList(searchField.getText());
        });

        Button clearAllButton = new Button("Clear All");
        clearAllButton.setFont(UIConstants.getSmallFont());
        UIConstants.setupButtonAnimation(clearAllButton);
        clearAllButton.setOnAction(event -> {
            selectedApps.clear();
            // Deactivate all site chips
            for (javafx.scene.Node node : siteChipsRow.getChildren()) {
                if (node instanceof Button) {
                    node.setUserData(false);
                    updateChipStyle((Button) node, false);
                }
            }
            websitesTextArea.clear();
            renderAppList(searchField.getText());
            updateStatusLabel();
        });
        Button refreshButton = new Button("Refresh Apps");
        refreshButton.setFont(UIConstants.getSmallFont());
        UIConstants.setupButtonAnimation(refreshButton);
        refreshButton.setOnAction(event -> refreshAvailableAppsAndRender());
        quickActionRow.getChildren().addAll(selectAllDistractingButton, clearAllButton, refreshButton);

        // Websites section
        Label sitesLabel = new Label("Websites (comma-separated):");
        sitesLabel.setFont(UIConstants.getBodyFont());
        sitesLabel.setTextFill(theme.getTextPrimary());

        // Quick-select chips for common distracting sites
        siteChipsRow.setAlignment(Pos.CENTER_LEFT);
        Set<String> initiallySelectedSites = new HashSet<>(initiallySelectedWebsites != null ? initiallySelectedWebsites : new ArrayList<>());
        for (Map.Entry<String, String> entry : COMMON_DISTRACTING_SITES.entrySet()) {
            String domain = entry.getKey();
            Button chip = new Button(domain);
            chip.setFont(UIConstants.getTinyFont());
            boolean active = initiallySelectedSites.contains(domain);
            updateChipStyle(chip, active);
            chip.setUserData(active);
            chip.setOnAction(e -> {
                boolean nowActive = !(boolean) chip.getUserData();
                chip.setUserData(nowActive);
                updateChipStyle(chip, nowActive);
                syncSitesFromChips();
            });
            siteChipsRow.getChildren().add(chip);
        }

        websitesTextArea.setPromptText("e.g., youtube.com, instagram.com, reddit.com");
        websitesTextArea.setFont(UIConstants.getSmallFont());
        websitesTextArea.setWrapText(true);
        websitesTextArea.setPrefRowCount(3);
        websitesTextArea.setStyle(
                "-fx-control-inner-background: " + toRGBCode(theme.getBackgroundSecondary()) + ";" +
                        "-fx-text-fill: " + toRGBCode(theme.getTextPrimary()) + ";" +
                        "-fx-font-family: monospace;"
        );

        HBox buttonRow = new HBox(UIConstants.SPACING_MD);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        Button cancelButton = new Button("Cancel");
        UIConstants.setupButtonAnimation(cancelButton);
        cancelButton.setOnAction(event -> close());

        Button confirmButton = new Button("Confirm");
        confirmButton.setStyle(
                "-fx-background-color: " + toRGBCode(theme.getAccentColor()) + ";" +
                        "-fx-text-fill: white;"
        );
        UIConstants.setupButtonAnimation(confirmButton);
        confirmButton.setOnAction(event -> {
            confirmed = true;
            close();
        });

        buttonRow.getChildren().addAll(cancelButton, confirmButton);

        content.getChildren().addAll(
                titleLabel,
                appsLabel,
                searchField,
                quickActionRow,
                statusLabel,
                scrollPane,
                new Separator(),
                sitesLabel,
                siteChipsRow,
                websitesTextArea
        );
        content.getChildren().addAll(buttonRow);
        root.setCenter(content);

        refreshAvailableAppsAndRender();
        updateStatusLabel();

        searchField.textProperty().addListener((obs, oldValue, newValue) -> renderAppList(newValue));

        setScene(new Scene(root, 580, 700));
        setOnShown(event -> startAutoRefresh());
        setOnHidden(event -> stopAutoRefresh());
    }

    /**
     * Checks whether the user confirmed their selections in the modal.
     *
     * @return true if selections were confirmed, false otherwise.
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * Retrieves the list of currently selected applications.
     *
     * @return A sorted list of selected application names.
     */
    public List<String> getSelectedApps() {
        return selectedApps.stream()
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the list of selected blocked websites.
     *
     * @return A list of normalized website domains.
     */
    public List<String> getSelectedWebsites() {
        String text = websitesTextArea.getText().trim();
        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        // Parse comma-separated domains and normalize them
        return Arrays.stream(text.split(","))
                .map(String::trim)
                .filter(domain -> !domain.isEmpty())
                .map(domain -> domain.toLowerCase(Locale.ROOT))
                .collect(Collectors.toList());
    }

    /**
     * Loads available applications by combining common distractions and running processes.
     */
    private void loadAvailableApps() {
        Set<String> appSet = new HashSet<>(COMMON_DISTRACTIONS.keySet());
        // Keep already-selected apps visible even when currently not running.
        appSet.addAll(selectedApps);
        for (ProcessInfo process : appMonitor.getRunningProcesses(true)) {
            String display = process.getDisplayName();
            if (display != null && !display.isBlank()) {
                appSet.add(display.trim());
            }
        }

        List<String> sortedApps = appSet.stream()
                .sorted(Comparator
                        .comparing((String app) -> !COMMON_DISTRACTIONS.containsKey(app))
                        .thenComparing(String::compareToIgnoreCase))
                .collect(Collectors.toList());
        allApps.setAll(sortedApps);
    }

    /**
     * Refreshes the list of available applications and updates the UI.
     */
    private void refreshAvailableAppsAndRender() {
        loadAvailableApps();
        renderAppList(searchField.getText());
    }

    /**
     * Starts the automatic refresh cycle for the application list.
     */
    private void startAutoRefresh() {
        stopAutoRefresh();
        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> refreshAvailableAppsAndRender()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    /**
     * Stops the automatic refresh cycle for the application list.
     */
    private void stopAutoRefresh() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
            refreshTimeline = null;
        }
    }

    /**
     * Renders the application list in the UI, filtered by the specified query.
     *
     * @param query The search query used to filter applications.
     */
    private void renderAppList(String query) {
        appListContainer.getChildren().clear();
        String normalizedQuery = query == null ? "" : query.toLowerCase(Locale.ROOT).trim();

        List<String> filtered = allApps.stream()
                .filter(app -> normalizedQuery.isEmpty() || app.toLowerCase(Locale.ROOT).contains(normalizedQuery))
                .collect(Collectors.toList());

        for (String appName : filtered) {
            HBox appRow = new HBox(UIConstants.SPACING_MD);
            appRow.setAlignment(Pos.CENTER_LEFT);
            appRow.setPadding(new Insets(UIConstants.SPACING_SM));
            appRow.setStyle("-fx-background-color: " + toRGBCode(theme.getBackgroundSecondary()) + "; -fx-background-radius: 8;");

            CheckBox checkBox = new CheckBox(appName);
            checkBox.setFont(UIConstants.getBodyFont());
            checkBox.setTextFill(theme.getTextPrimary());
            checkBox.setSelected(selectedApps.contains(appName));
            checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    selectedApps.add(appName);
                } else {
                    selectedApps.remove(appName);
                }
                updateStatusLabel();
            });

            Label categoryLabel = new Label(getCategory(appName));
            categoryLabel.setFont(UIConstants.getTinyFont());
            categoryLabel.setTextFill(theme.getTextMuted());
            categoryLabel.setStyle(
                    "-fx-background-color: " + toRGBCode(theme.getBackgroundPrimary()) + ";" +
                            "-fx-padding: 3 8 3 8;" +
                            "-fx-background-radius: 10;"
            );

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            appRow.getChildren().addAll(checkBox, spacer, categoryLabel);
            appListContainer.getChildren().add(appRow);
        }

        if (filtered.isEmpty()) {
            Label emptyLabel = new Label("No apps match your search.");
            emptyLabel.setFont(UIConstants.getSmallFont());
            emptyLabel.setTextFill(theme.getTextMuted());
            appListContainer.getChildren().add(emptyLabel);
        }
    }

    /**
     * Updates the status label with the count of currently selected apps and websites.
     */
    private void updateStatusLabel() {
        int appCount = selectedApps.size();
        List<String> websites = getSelectedWebsites();
        int siteCount = websites.size();

        // Build status message
        String appStatus = appCount == 0 ? "No apps" :
                String.format("%d app%s", appCount, appCount == 1 ? "" : "s");
        String siteStatus = siteCount == 0 ? "No sites" :
                String.format("%d site%s", siteCount, siteCount == 1 ? "" : "s");

        statusLabel.setText(appStatus + " | " + siteStatus + " selected");
    }

    /**
     * Determines the category for the specified application.
     *
     * @param appName The name of the application.
     * @return The category name, or "Running App" if unknown.
     */
    private String getCategory(String appName) {
        String category = COMMON_DISTRACTIONS.get(appName);
        if (category != null) {
            return category;
        }
        return "Running App";
    }

    /**
     * Converts a JavaFX Color to a CSS-compatible RGB string.
     *
     * @param color The Color to convert.
     * @return A string representing the RGB code.
     */
    private String toRGBCode(javafx.scene.paint.Color color) {
        return String.format("rgb(%d, %d, %d)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    /**
     * Creates and returns a map of common distracting applications and their categories.
     *
     * @return A map containing common distracting applications.
     */
    private static Map<String, String> createCommonDistractions() {
        Map<String, String> apps = new LinkedHashMap<>();
        apps.put("Discord", "Social");
        apps.put("Steam", "Gaming");
        apps.put("Messages", "Messaging");
        apps.put("Spotify", "Entertainment");
        apps.put("Slack", "Communication");
        return apps;
    }

    /**
     * Creates and returns a map of commonly distracting websites and their categories.
     *
     * @return A map containing common distracting website domains.
     */
    private static Map<String, String> createCommonDistractingSites() {
        Map<String, String> sites = new LinkedHashMap<>();
        sites.put("youtube.com", "Video");
        sites.put("instagram.com", "Social");
        sites.put("tiktok.com", "Video");
        sites.put("x.com", "Social");
        sites.put("reddit.com", "Social");
        sites.put("twitter.com", "Social");
        return sites;
    }

    /**
     * Updates the visual style of a website chip button to reflect its active state.
     *
     * @param chip The chip button to style.
     * @param active Whether the chip is in the selected state.
     */
    private void updateChipStyle(Button chip, boolean active) {
        if (active) {
            chip.setStyle(
                    "-fx-background-color: " + toRGBCode(theme.getAccentColor()) + ";" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 12;" +
                            "-fx-padding: 4 10 4 10;" +
                            "-fx-cursor: hand;"
            );
        } else {
            chip.setStyle(
                    "-fx-background-color: " + toRGBCode(theme.getBackgroundSecondary()) + ";" +
                            "-fx-text-fill: " + toRGBCode(theme.getTextSecondary()) + ";" +
                            "-fx-background-radius: 12;" +
                            "-fx-padding: 4 10 4 10;" +
                            "-fx-cursor: hand;"
            );
        }
    }

    /**
     * Syncs the website text area with the current state of all site chips.
     * Active chip domains appear first, followed by any manually typed domains.
     */
    private void syncSitesFromChips() {
        LinkedHashSet<String> domains = new LinkedHashSet<>();
        // Collect only active chips, preserving COMMON_DISTRACTING_SITES order
        for (javafx.scene.Node node : siteChipsRow.getChildren()) {
            if (node instanceof Button) {
                Button chip = (Button) node;
                if (Boolean.TRUE.equals(chip.getUserData())) {
                    domains.add(chip.getText());
                }
            }
        }
        // Append any manually typed domains that aren't already included
        for (String manual : getSelectedWebsites()) {
            if (!COMMON_DISTRACTING_SITES.containsKey(manual)) {
                domains.add(manual);
            }
        }
        websitesTextArea.setText(String.join(", ", domains));
        updateStatusLabel();
    }
}
