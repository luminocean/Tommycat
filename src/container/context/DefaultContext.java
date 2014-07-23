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
import container.Pipeline;

public class DefaultContext implements Context {
	//保管了请求路径和对应servlet容器之间的映射
	private Map<String, String> servletNameMap = new HashMap<String, String>();
	//子容器的映射集合
	private Map<String, Container> childrenMap = new HashMap<String, Container>();
	
	private Pipeline pipeline = new Pipeline();
	
	
	public DefaultContext(){
		BasicContextValve basicValve = new BasicContextValve(this);
		pipeline.setBasicValve(basicValve);
	}
	
	@Override
	public void invoke(Request request, Response response) {
		try {
			pipeline.invoke(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addChild(Container child) {
		childrenMap.put(child.getName(), child);
	}
	
	public void addServletMapping(String uri, String servletName){
		servletNameMap.put(uri, servletName);
	}

	@Override
	public String getName() {
		return null;
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
