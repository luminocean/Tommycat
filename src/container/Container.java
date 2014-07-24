package container;

import java.util.List;

import connector.request.Request;
import connector.response.Response;
import container.loader.Loader;
import core.LifeCycle;

public interface Container extends LifeCycle{
	void invoke(Request request, Response response);
	void addChild(Container child);
	String getName();
	void setBasicValve(Valve basicValve);
	Loader getLoader();
	void setLoader(Loader loader);
	Container getParent();
	void setParent(Container parent);
	void addRepository(String relativeRepoPath);
	List<Repository> getRepositories();
	
	/**
	 * 同时容器与它相关的repository发生了修改
	 */
	void repositoryUpdateNotify();
}
