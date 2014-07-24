package container.context;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import util.Logger;
import util.os.FileHelper;
import connector.request.Request;
import connector.response.Response;
import container.Container;
import container.ContainerBase;
import container.Pipeline;
import container.Valve;
import core.LifeCycle;

public class StandardContext extends ContainerBase implements Context{
	//保管了请求路径和对应servlet容器之间的映射
	private Map<String, String> servletNameMap = new HashMap<String, String>();
	//子容器的映射集合
	private Map<String, Container> childrenMap = new HashMap<String, Container>();
	
	
	public StandardContext(){
		BasicContextValve basicValve = new BasicContextValve(this);
		setBasicValve(basicValve);
	}
	
	
	@Override
	public void invoke(Request request, Response response) {
		super.invoke(request, response);
	}

	@Override
	public void addChild(Container child) {
		super.addChild(child);
		childrenMap.put(child.getName(), child);
	}
	
	public void addServletMapping(String uri, String servletName){
		servletNameMap.put(uri, servletName);
	}

	
	@Override
	public Map<String, String> getServletNameMap() {
		return servletNameMap;
	}

	@Override
	public Map<String, Container> getChildrenMap() {
		return childrenMap;
	}
}
