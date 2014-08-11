package session;

import java.util.HashMap;
import java.util.Map;

public class Session {
	private String sessionId;
	private Map<String, Object> attributes = new HashMap<String, Object>();

	public Session(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public void setAttribute(String name, Object value) {
		attributes.put(name, value);
	}

	public Object getAttribute(String name) {
		return attributes.get(name);
	}
}
