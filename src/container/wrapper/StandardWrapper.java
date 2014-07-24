package container.wrapper;

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
import container.Container;
import container.ContainerBase;
import container.Pipeline;

public class StandardWrapper extends ContainerBase implements Wrapper {
	/**
	 * 给当前的wrapper配置pipeline，包括BasicValve的配置
	 * @param servletName
	 */
	public void setup(String servletName){
		Servlet servlet = loadServlet(servletName);
		
		//配置BasicValve！否则Servlet就不会被处理了！！千万注意别忘记了！
		BasicWrapperValve basicValve = new BasicWrapperValve(servlet);
		setBasicValve(basicValve);
		
		//该wrapper的名字，上层的context会用这个名字来查找wrapper，以此来分配请求
		//wrapper名字与它关联的servlet相同，这一点请注意
		setName(servletName);
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


	@Override
	public void addChild(Container child) {
		Logger.warning("Wrapper中不可以添加子容器！");
	}
}
