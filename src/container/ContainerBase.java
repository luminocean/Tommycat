package container;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletException;
import util.Logger;
import connector.request.Request;
import connector.response.Response;
import container.loader.Loader;

/**
 * 基础的container类，包含了container里面的很多通用方法与特性
 * @author zhangh-fnst
 *
 */
public class ContainerBase implements Container{
	//容器所持有的pipeline，将最终处理所有valve以及servlet调用
	protected Pipeline pipeline = new Pipeline();
	//所有子容器的列表
	protected List<Container> children = new LinkedList<Container>();
	protected String name;
	protected Container parent;
	
	protected Loader loader;

	//标志是否已经启动
	protected boolean started;

	@Override
	public void start() {
		if( started )
			Logger.warning("Container("+ this.getClass().getName() +")重复启动");
		
		//所谓容器的启动其实就是启动其所有的子容器(当然这只是基容器的行为)
		for( Container c : children ){
			c.start();
		}
		
		started = true;
	}

	@Override
	public void stop() {
		if( !started )
			Logger.warning("Container("+ this.getClass().getName() +")重复关闭");
		
		for( Container c : children ){
			c.stop();
		}
		
		started = false;
	}

	@Override
	public void invoke(Request request, Response response) {
		try {
			pipeline.invoke(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addChild(Container child) {
		//在这里设置父子关系
		child.setParent(this);
		
		children.add(child);
	}

	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setBasicValve(Valve basicValve) {
		pipeline.setBasicValve(basicValve);
	}

	

	@Override
	public Container getParent() {
		if( parent != null )
			return parent;
		
		return null;
	}

	@Override
	public void setParent(Container parent) {
		this.parent = parent;
	}


	@Override
	public void reload() {
		stop();
		start();
		Logger.info("重新加载完成");
	}

	@Override
	public Loader getLoader() {
		if( loader == null ){
			return parent.getLoader();
		}else{
			return loader;
		}
	}

	@Override
	public void setLoader(Loader loader) {
		this.loader = loader;
	}
}
