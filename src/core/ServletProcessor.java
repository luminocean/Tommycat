package core;

import api.Servlet;
import os.ServletLoader;

public class ServletProcessor {
	private ServletLoader servletLoader = new ServletLoader();

	public void process(Request request, Response response) {
		String uri = request.getUri();
		
		String servletName = getServletName(uri);
		
		Servlet servlet = servletLoader.loadServlet(servletName);
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
		if( uri.indexOf("/servlet")==0 ){
			servletName = uri.replace("/servlet", "");
			if( servletName.startsWith("/") )
				servletName = servletName.substring(1);
			return servletName;
		}
		
		return servletName;
	}
}
