package container.context;

import java.util.List;
import java.util.Map;

import session.SessionManager;
import container.Container;
import container.Repository;
import container.loader.Loader;

public interface Context extends Container{
	Map<String, String> getServletNameMap();
	Map<String, Container> getChildrenMap();
	
	void addRepository(String relativeRepoPath);
	List<Repository> getRepositories();
	/**
	 * 同时容器与它相关的repository发生了修改
	 */
	void repositoryUpdateNotify();
	
	SessionManager getSessionManager();
}
