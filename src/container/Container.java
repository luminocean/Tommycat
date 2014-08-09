package container;

import java.util.List;

import connector.request.Request;
import connector.response.Response;
import container.loader.Loader;
import core.LifeCycle;

public interface Container extends LifeCycle{
	void invoke(Request request, Response response);
	
	void addChild(Container child);
	Container getParent();
	void setParent(Container parent);
	
	String getName();
	
	void setBasicValve(Valve basicValve);
	
	Loader getLoader();
	void setLoader(Loader loader);
	
	/**
	 * 发现容器所属的类被修改了于是就需要通知容器重新加载
	 */
	void reload();
}
