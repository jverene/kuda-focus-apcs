package focus.kudafocus.data.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for all session records.
 * This is what gets serialized to sessions.json.
 */
public class SessionHistory {

    /**
     * List of all completed/abandoned sessions
     */
    private List<SessionRecord> sessions;

    // ===== CONSTRUCTORS =====

    /**
     * Creates empty session history
     */
    public SessionHistory() {
        this.sessions = new ArrayList<>();
    }

    /**
     * Creates session history with existing sessions
     *
     * @param sessions List of sessions
     */
    public SessionHistory(List<SessionRecord> sessions) {
        this.sessions = sessions;
    }

    // ===== METHODS =====

    /**
     * Adds a session to history
     *
     * @param session Session to add
     */
    public void addSession(SessionRecord session) {
        sessions.add(session);
    }

    /**
     * Gets number of sessions
     *
     * @return Number of sessions
     */
    public int getCount() {
        return sessions.size();
    }

    // ===== GETTERS AND SETTERS =====

    public List<SessionRecord> getSessions() {
        return sessions;
    }

    public void setSessions(List<SessionRecord> sessions) {
        this.sessions = sessions;
    }
}
