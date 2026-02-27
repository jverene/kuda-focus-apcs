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
 * Persists user preferences to local JSON storage.
 */
public class PreferencesStore {

    private static final String APP_DIR_NAME = ".kudafocus";
    private static final String PREFERENCES_FILE_NAME = "preferences.json";

    private final Gson gson;
    private final Path preferencesPath;

    public PreferencesStore() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        Path appDir = Paths.get(System.getProperty("user.home"), APP_DIR_NAME);
        this.preferencesPath = appDir.resolve(PREFERENCES_FILE_NAME);
    }

    public UserPreferences load() {
        if (!Files.exists(preferencesPath)) {
            return new UserPreferences();
        }

        try (Reader reader = Files.newBufferedReader(preferencesPath)) {
            UserPreferences preferences = gson.fromJson(reader, UserPreferences.class);
            return preferences != null ? preferences : new UserPreferences();
        } catch (Exception e) {
            System.err.println("Failed to load preferences, using defaults: " + e.getMessage());
            return new UserPreferences();
        }
    }

    public void save(UserPreferences preferences) {
        try {
            Files.createDirectories(preferencesPath.getParent());
            try (Writer writer = Files.newBufferedWriter(preferencesPath)) {
                gson.toJson(preferences, writer);
            }
        } catch (IOException e) {
            System.err.println("Failed to save preferences: " + e.getMessage());
        }
    }
}
