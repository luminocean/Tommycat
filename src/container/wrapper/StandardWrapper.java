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
import container.loader.Loader;

public class StandardWrapper extends ContainerBase implements Wrapper {
	
	/**pipeline
	 * invoke()
	 * children
	 * 均包含在父类中*/
	
	private String servletName;
	
	public StandardWrapper(String servletName){
		this.servletName = servletName;
		
		//该wrapper的名字，上层的context会用这个名字来查找wrapper，以此来分配请求
		//wrapper名字与它关联的servlet相同，这一点请注意
		setName(servletName);
	}
	
	//重写启动方法
	@Override
	public void start() {
		//在这里加载需要使用的servlet!
		Servlet servlet = loadServlet(servletName);
			
		//配置BasicValve！否则Servlet就不会被处理了！！千万注意别忘记了！
		//其实是在配置pipeline
		BasicWrapperValve basicValve = new BasicWrapperValve(servlet);
		setBasicValve(basicValve);
		
		//启动剩余部分
		super.start();
	}

	@Override
	public void stop() {
		super.stop();
	}
	
	
	/**
	 * 加载这个wrapper所管理的servlet
	 * @param servletName
	 * @return
	 */
	private Servlet loadServlet(String servletName) {
		//获取loader用来加载servlet（这可loader很可能是从父容器那里拿过来的）
		Loader loader = getLoader();
		
		Servlet targetServlet = loader.loadServlet(servletName);
		
		return targetServlet;
	}


	@Override
	public void addChild(Container child) {
		Logger.warning("Wrapper中不可以添加子容器！");
	}
}
