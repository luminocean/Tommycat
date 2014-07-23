package container;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import util.os.ServletLoader;
import connector.request.Request;
import connector.request.RequestFacade;
import connector.response.Response;
import connector.response.ResponseFacade;
import container.valve.BasicWrapperValve;

public class DefaultWrapper implements Container {
	private Pipeline pipeline = new Pipeline();
	
	/**
	 * 给当前的wrapper配置pipeline，包括BasicValve的配置
	 * @param servletName
	 */
	public void setup(String servletName){
		Servlet servlet = loadServlet(servletName);
		
		//配置BasicValve！否则Servlet就不会被处理了！！千万注意别忘记了！
		BasicWrapperValve basicValve = new BasicWrapperValve(servlet);
		pipeline.setBasicValve(basicValve);
	}

	/**
	 * 加载这个wrapper所管理的servlet
	 * @param servletName
	 * @return
	 */
	private Servlet loadServlet(String servletName) {
		ServletLoader loader = new ServletLoader();
		Servlet targetServlet = loader.loadServlet(servletName);
		
		return targetServlet;
	}

	/**
	 * Connector调用！
	 */
	@Override
	public void invoke(Request request, Response response) {
		try {
			//调用pipeline完成业务链处理
			pipeline.invoke(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
