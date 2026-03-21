package focus.kudafocus.data.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import focus.kudafocus.data.models.UserPreferences;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Handles the persistence of user preferences to local JSON storage.
 * This class manages the directory creation and file I/O operations
 * required to save and load application settings.
 */
public class PreferencesStore {

    /**
     * The name of the application's hidden configuration directory.
     */
    private static final String APP_DIR_NAME = ".kudafocus";

    /**
     * The name of the JSON file where preferences are stored.
     */
    private static final String PREFERENCES_FILE_NAME = "preferences.json";

    /**
     * The Gson instance used for JSON serialization and deserialization.
     */
    private final Gson gson;

    /**
     * The filesystem path where the preferences file is located.
     */
    private final Path preferencesPath;

    /**
     * Initializes a new instance of PreferencesStore.
     * Configures the storage path within the user's home directory.
     */
    public PreferencesStore() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        Path appDir = Paths.get(System.getProperty("user.home"), APP_DIR_NAME);
        this.preferencesPath = appDir.resolve(PREFERENCES_FILE_NAME);
    }

    /**
     * Loads the user preferences from the local storage file.
     * If the file does not exist or an error occurs during loading,
     * a new instance with default preferences is returned.
     *
     * @return the loaded user preferences, or defaults if loading fails
     */
    public UserPreferences load() {
        if (!Files.exists(preferencesPath)) {
            return new UserPreferences();
        }

        try (Reader reader = Files.newBufferedReader(preferencesPath)) {
            UserPreferences preferences = gson.fromJson(reader, UserPreferences.class);
            return preferences != null ? preferences : new UserPreferences();
        } catch (Exception e) {
            return new UserPreferences();
        }
    }

    /**
     * Persists the provided user preferences to the local storage file.
     * Creates the necessary application directories if they do not exist.
     *
     * @param preferences the user preferences to be saved
     */
    public void save(UserPreferences preferences) {
        try {
            Files.createDirectories(preferencesPath.getParent());
            try (Writer writer = Files.newBufferedWriter(preferencesPath)) {
                gson.toJson(preferences, writer);
            }
        } catch (IOException e) {
            // Silently fail if save is unsuccessful
        }
    }
}
