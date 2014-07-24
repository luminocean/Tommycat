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
	protected Loader loader;
	protected Container parent;
	protected List<Repository> repositories = new LinkedList<Repository>();
	//标志是否已经启动
	protected boolean started;

	@Override
	public void start() {
		if( started )
			Logger.warning("Container("+ this.getClass().getName() +")重复启动");
		
		//先启动所有（子）容器所依赖的类，然后再启动子容器，否则会有以来缺失的问题
		if( loader != null )
			loader.start();
		
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
	public Loader getLoader() {
		//如果自己有loader就直接返回
		if( loader != null )
			return loader;
		//否则问父容器要
		if( parent != null )
			return parent.getLoader();
		//如果没有父容器，那么只能返回空了
		return null;
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
	public void setLoader(Loader loader) {
		this.loader = loader;
	}

	@Override
	public void addRepository(String relativeRepoPath) {
		String repoPath = null;
		
		//上下文路径（此上下文非彼上下文）
		//服务器目录 + webApp目录
		String contextPath = System.getProperty("user.dir")
				.replace('\\', '/') + "/" +"WebApps/"+ name;
		contextPath = normalizeDirPostfix(contextPath);
		
		
		repoPath = contextPath;
		
		if( name == null ){
			Logger.error("Context的name为空！无法构建context路径");
			return;
		}
		
		repoPath = repoPath + relativeRepoPath;
		repoPath = normalizeDirPostfix(repoPath);
		
		
		File file = new File(repoPath);
		if( !file.exists() ){
			Logger.warning("添加的代码存放位置不存在: repoPath = "+repoPath);
			return;
		}
			
		Repository repo = new Repository(file);
		repositories.add(repo);
	}

	@Override
	public List<Repository> getRepositories() {
		List<Repository> returnList = repositories;
		
		//获取父容器的repositories加到自己这里
		if( parent != null ){
			List<Repository> parentRepos = parent.getRepositories();
			if(parentRepos != null ) 
				returnList.addAll(parentRepos);
		}
			
		return returnList;
	}
	
	/**
	 * 标准化后缀，对于目录如果结尾没有/则加上
	 * @param s
	 * @return
	 */
	private String normalizeDirPostfix(String s){
		if( !s.endsWith("/") )
			s = s+"/";
		return s;
	}

	@Override
	public void repositoryUpdateNotify() {
		Logger.info("Repository发生修改, 容器开始重新加载");
		reload();
	}

	@Override
	public void reload() {
		stop();
		start();
		Logger.info("重新加载完成");
	}
}
