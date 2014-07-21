package core;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import connector.Request;
import connector.Response;
import connector.facade.RequestFacade;
import connector.facade.ResponseFacade;
import util.os.ServletLoader;

public class ServletProcessor {
	private ServletLoader servletLoader = new ServletLoader();

	
	public void process(Request request, Response response) {
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
