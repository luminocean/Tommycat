package container;

import java.util.HashMap;
import java.util.Map;

import util.Logger;
import util.os.FileHelper;
import connector.request.Request;
import connector.response.Response;

public class DefaultContext implements Container {
	//保管了请求路径和对应servlet容器之间的映射
	private Map<String, String> servletMap = new HashMap<String, String>();
	//子容器的映射集合
	private Map<String, Container> childrenMap = new HashMap<String, Container>();
	
	
	@Override
	public void invoke(Request request, Response response) {
		String requestUri = request.getUri();
		
		//先判断是否是静态资源
		if( FileHelper.hasResource(requestUri)){
			response.sendStaticResource();
			return;
		}
		
		String mappedServletName = servletMap.get(requestUri);
		if( mappedServletName != null ){
			Container wrapper = childrenMap.get(mappedServletName);
			if( wrapper == null )
				Logger.error("配置了uri到servlet的映射，但是找不到这个servlet所在的wrapper!");
			
			//调用查找到的wrapper，继续往下传
			wrapper.invoke(request, response);
		}else{
			Logger.debug("找不到请求uri到servlet的映射，或是任何静态资源");
			Logger.debug("请求uri："+requestUri);
		}
	}

	@Override
	public void addChild(Container child) {
		childrenMap.put(child.getName(), child);
	}
	
	public void addServletMapping(String uri, String servletName){
		servletMap.put(uri, servletName);
	}

	@Override
	public String getName() {
		return null;
	}

}
