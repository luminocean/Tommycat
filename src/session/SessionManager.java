package session;

import java.util.HashMap;
import java.util.Random;

import core.LifeCycle;

public class SessionManager implements LifeCycle{
	private HashMap<String, Session> sessions = new HashMap<String, Session>();

	private Random rand = new Random();
	
	/**
	 * 根据提供的session找出本管理器管理中的session对象
	 * @param sessionId
	 * @return
	 */
	public Session findSession(String sessionId) {
		return sessions.get(sessionId);
	}

	/**
	 * 创建一个全新的session
	 * @return
	 */
	public Session createSession() {
		String sessionId = generateSessionId();
		
		Session session = new Session(sessionId);
		sessions.put(sessionId, session);
		
		return session;
	}
	
	/**
	 * 随机生成sessionid的工具函数
	 * @return
	 */
	private String generateSessionId() {
		char[] sessionId = new char[32];
		
		for(int i=0; i<sessionId.length; i++){
			int randIndex = rand.nextInt(26);
			int charCode = randIndex+65;
			char randChar = (char)charCode;
			sessionId[i] = randChar;
		}
		
		return new String(sessionId);
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
}
