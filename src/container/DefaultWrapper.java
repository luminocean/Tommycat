package container;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import sun.rmi.runtime.Log;
import util.Logger;
import util.os.ServletLoader;
import connector.request.Request;
import connector.response.Response;
import container.valve.BasicWrapperValve;

public class DefaultWrapper implements Container {
	//该wrapper的名字，上层的context会用这个名字来查找wrapper，以此来分配请求
	private String name;
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
		
		//wrapper名字与它关联的servlet相同，这一点请注意
		name = servletName;
	}

	/**
	 * 加载这个wrapper所管理的servlet
	 * @param servletName
	 * @return
	 */
	private Servlet loadServlet(String servletName) {
		Servlet targetServlet = ServletLoader.loadServlet(servletName);
		
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

	@Override
	public void addChild(Container child) {
		Logger.warning("Wrapper中不可以添加子容器！");
	}

	@Override
	public String getName() {
		return name;
	}
}
