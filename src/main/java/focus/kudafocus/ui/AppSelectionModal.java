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
 * Modal dialog for selecting blocked apps and websites.
 *
 * Allows users to:
 * - Select from running processes and common distracting apps
 * - Optionally input blocked websites (comma-separated domains)
 * - View status of selections
 * - Auto-refresh running processes while modal is open
 */
public class AppSelectionModal extends Stage {

    private static final Map<String, String> COMMON_DISTRACTIONS = createCommonDistractions();

    private final AppMonitor appMonitor;
    private final Set<String> selectedApps;
    private final ObservableList<String> allApps;
    private final VBox appListContainer;
    private final Label statusLabel;
    private final TextField searchField;
    private final TextArea websitesTextArea;
    private Timeline refreshTimeline;

    private boolean confirmed;

    public AppSelectionModal(Window owner, List<String> initiallySelectedApps) {
        this(owner, initiallySelectedApps, new ArrayList<>());
    }

    public AppSelectionModal(Window owner, List<String> initiallySelectedApps, List<String> initiallySelectedWebsites) {
        this.appMonitor = AppMonitor.createForCurrentOS();
        this.selectedApps = new HashSet<>(initiallySelectedApps);
        this.allApps = FXCollections.observableArrayList();
        this.appListContainer = new VBox(UIConstants.SPACING_SM);
        this.statusLabel = new Label();
        this.searchField = new TextField();
        this.websitesTextArea = new TextArea();
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
        root.setStyle("-fx-background-color: " + toRGBCode(UIConstants.BACKGROUND_PRIMARY) + ";");

        VBox content = new VBox(UIConstants.SPACING_MD);
        content.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Select apps and websites to block");
        titleLabel.setFont(UIConstants.getHeadingFont());
        titleLabel.setTextFill(UIConstants.TEXT_PRIMARY);

        // Apps section
        Label appsLabel = new Label("Applications:");
        appsLabel.setFont(UIConstants.getBodyFont());
        appsLabel.setTextFill(UIConstants.TEXT_PRIMARY);

        searchField.setPromptText("Search running apps...");
        searchField.setFont(UIConstants.getBodyFont());
        searchField.setStyle(
                "-fx-background-color: " + toRGBCode(UIConstants.BACKGROUND_SECONDARY) + ";" +
                        "-fx-text-fill: " + toRGBCode(UIConstants.TEXT_PRIMARY) + ";" +
                        "-fx-prompt-text-fill: " + toRGBCode(UIConstants.TEXT_MUTED) + ";"
        );

        statusLabel.setFont(UIConstants.getSmallFont());
        statusLabel.setTextFill(UIConstants.TEXT_SECONDARY);

        ScrollPane scrollPane = new ScrollPane(appListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(200);
        scrollPane.setStyle("-fx-background: " + toRGBCode(UIConstants.BACKGROUND_PRIMARY) + ";");

        HBox quickActionRow = new HBox(UIConstants.SPACING_SM);
        quickActionRow.setAlignment(Pos.CENTER_LEFT);

        Button selectAllDistractingButton = new Button("Select All Distracting");
        selectAllDistractingButton.setFont(UIConstants.getSmallFont());
        selectAllDistractingButton.setOnAction(event -> {
            selectedApps.addAll(COMMON_DISTRACTIONS.keySet());
            renderAppList(searchField.getText());
            updateStatusLabel();
        });

        Button clearAllButton = new Button("Clear All");
        clearAllButton.setFont(UIConstants.getSmallFont());
        clearAllButton.setOnAction(event -> {
            selectedApps.clear();
            renderAppList(searchField.getText());
            updateStatusLabel();
        });
        Button refreshButton = new Button("Refresh Apps");
        refreshButton.setFont(UIConstants.getSmallFont());
        refreshButton.setOnAction(event -> refreshAvailableAppsAndRender());
        quickActionRow.getChildren().addAll(selectAllDistractingButton, clearAllButton, refreshButton);

        // Websites section
        Label sitesLabel = new Label("Websites (comma-separated):");
        sitesLabel.setFont(UIConstants.getBodyFont());
        sitesLabel.setTextFill(UIConstants.TEXT_PRIMARY);

        websitesTextArea.setPromptText("e.g., youtube.com, instagram.com, reddit.com");
        websitesTextArea.setFont(UIConstants.getSmallFont());
        websitesTextArea.setWrapText(true);
        websitesTextArea.setPrefRowCount(3);
        websitesTextArea.setStyle(
                "-fx-control-inner-background: " + toRGBCode(UIConstants.BACKGROUND_SECONDARY) + ";" +
                        "-fx-text-fill: " + toRGBCode(UIConstants.TEXT_PRIMARY) + ";" +
                        "-fx-font-family: monospace;"
        );

        HBox buttonRow = new HBox(UIConstants.SPACING_MD);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> close());

        Button confirmButton = new Button("Confirm");
        confirmButton.setStyle(
                "-fx-background-color: " + toRGBCode(UIConstants.ACCENT_COLOR) + ";" +
                        "-fx-text-fill: white;"
        );
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

    public boolean isConfirmed() {
        return confirmed;
    }

    public List<String> getSelectedApps() {
        return selectedApps.stream()
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
    }

    /**
     * Gets the list of selected blocked websites
     *
     * @return List of website domains (parsed from text area)
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

    private void refreshAvailableAppsAndRender() {
        loadAvailableApps();
        renderAppList(searchField.getText());
    }

    private void startAutoRefresh() {
        stopAutoRefresh();
        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> refreshAvailableAppsAndRender()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    private void stopAutoRefresh() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
            refreshTimeline = null;
        }
    }

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
            appRow.setStyle("-fx-background-color: " + toRGBCode(UIConstants.BACKGROUND_SECONDARY) + "; -fx-background-radius: 8;");

            CheckBox checkBox = new CheckBox(appName);
            checkBox.setFont(UIConstants.getBodyFont());
            checkBox.setTextFill(UIConstants.TEXT_PRIMARY);
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
            categoryLabel.setTextFill(UIConstants.TEXT_MUTED);
            categoryLabel.setStyle(
                    "-fx-background-color: " + toRGBCode(UIConstants.BACKGROUND_PRIMARY) + ";" +
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
            emptyLabel.setTextFill(UIConstants.TEXT_MUTED);
            appListContainer.getChildren().add(emptyLabel);
        }
    }

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

    private String getCategory(String appName) {
        String category = COMMON_DISTRACTIONS.get(appName);
        if (category != null) {
            return category;
        }
        return "Running App";
    }

    private String toRGBCode(javafx.scene.paint.Color color) {
        return String.format("rgb(%d, %d, %d)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private static Map<String, String> createCommonDistractions() {
        Map<String, String> apps = new LinkedHashMap<>();
        apps.put("Discord", "Social");
        apps.put("Instagram", "Social");
        apps.put("Steam", "Gaming");
        apps.put("Messages", "Messaging");
        apps.put("YouTube", "Video");
        apps.put("TikTok", "Video");
        apps.put("X", "Social");
        apps.put("Reddit", "Social");
        return apps;
    }
}
