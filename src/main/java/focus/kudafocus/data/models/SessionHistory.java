package focus.kudafocus.data.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a container for all session records within the application.
 * This class serves as the primary data structure for serializing and
 * deserializing session history to and from JSON storage.
 */
public class SessionHistory {

    /**
     * The list of all completed or abandoned focus sessions.
     */
    private List<SessionRecord> sessions;

    // ===== CONSTRUCTORS =====

    /**
     * Initializes a new instance of SessionHistory with an empty list of sessions.
     */
    public SessionHistory() {
        this.sessions = new ArrayList<>();
    }

    /**
     * Initializes a new instance of SessionHistory with the specified list of sessions.
     *
     * @param sessions the list of existing session records to include in the history
     */
    public SessionHistory(List<SessionRecord> sessions) {
        this.sessions = sessions;
    }

    // ===== METHODS =====

    /**
     * Appends a session record to the session history.
     *
     * @param session the session record to be added
     */
    public void addSession(SessionRecord session) {
        sessions.add(session);
    }

    /**
     * Retrieves the total number of sessions stored in the history.
     *
     * @return the number of session records
     */
    public int getCount() {
        return sessions.size();
    }

    // ===== GETTERS AND SETTERS =====

    /**
     * Retrieves the list of all session records.
     *
     * @return the list of session records
     */
    public List<SessionRecord> getSessions() {
        return sessions;
    }

    /**
     * Sets the list of session records.
     *
     * @param sessions the list of session records to set
     */
    public void setSessions(List<SessionRecord> sessions) {
        this.sessions = sessions;
    }
}
