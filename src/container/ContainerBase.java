package container;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;

import connector.request.Request;
import connector.response.Response;
import core.LifeCycle;

/**
 * 基础的container类，包含了container里面的很多通用方法与特性
 * @author zhangh-fnst
 *
 */
public class ContainerBase implements Container, LifeCycle{
	//容器所持有的pipeline，将最终处理所有valve以及servlet调用
	protected Pipeline pipeline = new Pipeline();
	//所有子容器的列表
	protected List<Container> children = new LinkedList<Container>();
	protected String name;

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
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
}
