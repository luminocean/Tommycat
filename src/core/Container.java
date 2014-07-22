package core;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import util.os.ServletLoader;
import connector.request.Request;
import connector.request.RequestFacade;
import connector.response.Response;
import connector.response.ResponseFacade;

public class Container {
	private ServletLoader servletLoader = new ServletLoader();

	public void invoke(Request request, Response response){
		//下面进行请求的分发
		String uri = request.getUri();
		//判断该请求该何去何从
		if( uri.startsWith("/servlet/") ){
			//进入servlet处理路线
			servletProcess(request, response);
		}else{
			//进入静态资源处理路线
			//向客户端发送响应
			response.sendStaticResource();
		}
	}
	
	/**
	 * 加载servlet并调用
	 * @param request
	 * @param response
	 */
	private void servletProcess(Request request, Response response){
		String uri = request.getUri();
		String servletName = getServletName(uri);
		
		//加载Servlet
		Servlet servlet = servletLoader.loadServlet(servletName);
		
		//将request和response使用facade包装，增加其安全性
		ServletRequest servletRequest = (ServletRequest) new RequestFacade(request);
		ServletResponse servletResponse = (ServletResponse) new ResponseFacade(response);
		
		//尝试调用Servlet执行
		try {
			servlet.service( servletRequest, servletResponse );
			
			//这一步非常重要！
			response.finishResponse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 从请求uri中获取servlet名称
	 * @param uri
	 * @return
	 */
	public String getServletName(String uri) {
		//这里约定servlet请求的uri为 /servlet/balabala 的格式
		String servletName = null;
		
		//首先必须要是/servlet开头的
		if( !(uri.indexOf("/servlet")==0) ){
			return null;
		}
		
		//将/servlet前缀去掉
		servletName = uri.replace("/servlet", "");
		if( servletName.startsWith("/") )
			servletName = servletName.substring(1);
		
		//由于请求的都是 /servlet/xxx 的形式，但是实际上xxx一般是在一个包里面的，因此在这里补上一个包名，默认是servlet
		servletName = "servlet."+servletName;
		
		return servletName;
	}
}
