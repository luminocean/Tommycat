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
	private Context context;
	private Map<String, String> servletNameMap;
	private Map<String, Container> childrenMap;
	
	public BasicContextValve(Context context) {
		this.context = context;
		this.servletNameMap = context.getServletNameMap();
		this.childrenMap = context.getChildrenMap();
	}

	@Override
	public void invoke(Request request, Response response,
			ValveContext valveContext) throws ServletException, IOException {
		String requestUri = request.getUri();
		
		//在把request往下传之前先设置好session manager
		request.setSessionManager(context.getSessionManager());
		
		//先判断是否是静态资源
		String pathInContext = getPathInContext(requestUri);
		//如果context里面的相对路径为空，表示可能访问的时候没有context路径直接访问根了，这个时候直接等于请求目录
		if( pathInContext == null ){
			pathInContext = requestUri;
		}
		if( FileHelper.hasResource(context.getDocBase(), pathInContext)){
			response.sendStaticResource();
			return;
		}
		
		//添加请求的context检查，实际上这一步应该是把请求传给context的container做的
		//只是现在顶层的container就是context所以只能先这么干，等到添加了再上层的container再修改
		String requestContextPath = getContextString(requestUri);
		if( request == null || !context.getContextPath().equals(requestContextPath)){
			Logger.debug("请求的context路径与现在装载的context路径不符，请检查请求的uri与配置的web上下文是否一致");
			Logger.debug("请求的context路径:" + requestContextPath);
			return;
		}
		
		String mappedServletName = getMappedServletName(requestUri);
		
		if( mappedServletName != null ){
			Container wrapper = childrenMap.get(mappedServletName);
			if( wrapper == null ){
				Logger.error("配置了uri到servlet的映射，但是找不到这个servlet所在的wrapper!");
			}else{
				//调用查找到的wrapper，继续往下传
				wrapper.invoke(request, response);
			}
				
		}else{
			Logger.debug("找不到请求uri到servlet的映射或任何静态资源");
			Logger.debug("请求uri："+requestUri);
		}
		
	}

	/**
	 * 从原始请求中获取相对于context的根路径，如 xx:8080/mushoom[/index.html]
	 * @param requestUri
	 * @return
	 */
	private String getPathInContext(String requestUri) {
		String[] parts = requestUri.split("/");
		if( parts.length > 2 ){
			return "/"+parts[2];
		}else{
			return null;
		}
	}

	/**
	 * 从原始请求中获取context的路径字符串，比如xxx:8080/mushroom/main里面的/mushroom部分
	 * @param requestUri
	 * @return
	 */
	private String getContextString(String requestUri) {
		String[] parts = requestUri.split("/");
		if( parts.length > 0 ){
			return "/"+parts[1];
		}else{
			return null;
		}
	}

	/**
	 * 从原始请求URI中获取sevlet在项目中相对于根的位置，比如xxx:8080/mushroom/main里面的/main部分
	 * @param requestUri
	 * @return
	 */
	private String getMappedServletName(String requestUri) {
		String servletRootUri = requestUri.substring(requestUri.lastIndexOf("/"));
		
		return servletNameMap.get(servletRootUri);
	}
}
