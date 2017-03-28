package properties;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static SessionManager instance;

    private Session currentSession;

    private Map<String, Session> sessions;

    private SessionManager() {
    }

    public static SessionManager getIntance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public Session getSessionById(String sessionId) {
        if (sessions.containsKey(sessionId)) {
            return sessions.get(sessionId);
        }
        return null;
    }

    public Session getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(Session newCurrentSession) {
        if (newCurrentSession != null) {
            String newCurrentId = newCurrentSession.getId();
            if (!sessions.containsKey(newCurrentId)) {
                save(newCurrentSession);
            }
            //это не первый старт программы, сохраняем инфу о текущей сессии
            if (currentSession != null) {
                PropertyManager.getIntance().saveProperty(PropertyConstants.CURRENT_SESSION, newCurrentId);
            }
            currentSession = newCurrentSession;
        }
    }

    public void save(Session session) {
        if (session != null) {
            if (sessions == null) {
                sessions = new HashMap<String, Session>();
            }
            if (!sessions.containsKey(session.getId())) {
                sessions.put(session.getId(), session);
            }
            PropertyManager.getIntance().saveProperty(session);
        }
    }

    public void remove(Session session) {
        if (session != null) {
            if (sessions.containsKey(session.getId())) {
                sessions.remove(session.getId());
            }
        }
    }

    public Collection<Session> getSessions() {
        Collection<Session> sessionsValues = null;
        if (sessions == null) {
            //Try to initialize sessions in loading properties process
            PropertyManager.getIntance();
        }
        if (sessions != null) {
            sessionsValues = sessions.values();
        }
        return sessionsValues;
    }

    public void setSessions(final Map<String, Session> sessions) {
        this.sessions = sessions;
    }
}
