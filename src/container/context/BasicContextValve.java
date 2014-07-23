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
import container.Valve;
import container.ValveContext;

public class BasicContextValve implements Valve{
	private Map<String, String> servletNameMap;
	private Map<String, Container> childrenMap;
	
	public BasicContextValve(Context context) {
		this.servletNameMap = context.getServletNameMap();
		this.childrenMap = context.getChildrenMap();
	}

	@Override
	public void invoke(Request request, Response response,
			ValveContext valveContext) throws ServletException, IOException {
		String requestUri = request.getUri();
		
		//先判断是否是静态资源
		if( FileHelper.hasResource(requestUri)){
			response.sendStaticResource();
			return;
		}
		
		String mappedServletName = servletNameMap.get(requestUri);
		if( mappedServletName != null ){
			Container wrapper = childrenMap.get(mappedServletName);
			if( wrapper == null )
				Logger.error("配置了uri到servlet的映射，但是找不到这个servlet所在的wrapper!");
			else
				//调用查找到的wrapper，继续往下传
				wrapper.invoke(request, response);
		}else{
			Logger.debug("找不到请求uri到servlet的映射，或是任何静态资源");
			Logger.debug("请求uri："+requestUri);
		}
		
	}
}
