package container.context;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import session.SessionManager;
import util.Logger;
import util.os.FileHelper;
import connector.request.Request;
import connector.response.Response;
import container.Container;
import container.ContainerBase;
import container.Pipeline;
import container.Repository;
import container.Valve;
import container.loader.Loader;
import container.loader.WebLoader;
import core.LifeCycle;

public class StandardContext extends ContainerBase implements Context {
	//Context负责管理的Session管理器
	private SessionManager sessionManager;
	
	//用于将来映射到该context所使用的路径，形如 /mushroom/
	private String contextPath;
	//该context真正对应的文件系统的文件夹名字,形如Mushroom，里面放置的是所有的web项目文件
	private String docBase;
	
	//其他组件真正要请求的repo列表，由下面的relativeRepoPaths String列表转化而来
	private List<Repository> repositories = new LinkedList<Repository>();
	//相对与context目录的代码存放路径，用于临时存放add进来的相对路径，在用户获取repositories的时候一次性进行转化
	private List<String> relativeRepoPaths = new LinkedList<String>();
	
	/**
	 * pipeline invoke() children 均包含在父类中
	 */

	// 保管了请求路径和对应servlet容器之间的映射
	private Map<String, String> servletNameMap = new HashMap<String, String>();
	// 子容器的映射集合
	private Map<String, Container> childrenMap = new HashMap<String, Container>();

	/**
	 * 主要配置了专门由context使用的的basicContextValve和WebLoader
	 */
	public StandardContext() {
		BasicContextValve basicValve = new BasicContextValve(this);
		setBasicValve(basicValve);

		// 在Context里面配置一个Loader
		Loader loader = new WebLoader(this);
		super.setLoader(loader);
		
		this.sessionManager = new SessionManager();
	}

	@Override
	public void start() {
		// 先启动所所依赖的类，然后再启动子容器，否则会有依赖缺失的问题
		if (loader != null)
			loader.start();
		else
			Logger.error("没有找到Loader类！将无法加载servlet等资源！");
		
		
		if( sessionManager != null )
			sessionManager.start();
		else{
			Logger.error("没有找到SessionManager类！将无法处理Session相关功能！");
		}
		

		super.start();
	}

	@Override
	public void addChild(Container child) {
		super.addChild(child);
		// 添加子容器名映射，方便查找
		childrenMap.put(child.getName(), child);
	}

	public void addServletMapping(String uri, String servletName) {
		servletNameMap.put(uri, servletName);
	}

	@Override
	public Map<String, String> getServletNameMap() {
		return servletNameMap;
	}

	@Override
	public Map<String, Container> getChildrenMap() {
		return childrenMap;
	}

	@Override
	public void addRepository(String relativeRepoPath) {
		relativeRepoPaths.add(relativeRepoPath);
	}

	@Override
	public List<Repository> getRepositories() {
		if( contextPath == null || docBase == null ){
			Logger.error("contextPath 或者 docBase未设置，无法构建出代码Repository列表！");
			return null;
		}
		
		repositories.clear();
		
		for( String relativeRepoPath: relativeRepoPaths ){
			String repoPath = null;

			// 上下文路径（此上下文非彼上下文）
			// 服务器目录 + webApp目录 + context目录
			String absoluteContextPath = System.getProperty("user.dir").replace('\\', '/')
					+ "/" + "WebApps/" + docBase;
			absoluteContextPath = normalizeDirPostfix(absoluteContextPath);

			//context的绝对路径加上相对repo路径就可以拼出repo的绝对路径
			repoPath = absoluteContextPath + relativeRepoPath;
			repoPath = normalizeDirPostfix(repoPath);

			File file = new File(repoPath);
			if (!file.exists()) {
				Logger.warning("添加的代码存放位置不存在: repoPath = " + repoPath);
				return null;
			}

			Repository repo = new Repository(file);
			repositories.add(repo);
		}
		
		return repositories;
	}

	@Override
	public void repositoryUpdateNotify() {
		Logger.info("Repository发生修改, 容器开始重新加载");
		reload();
	}
	
	public void setPath(String contexPath) {
		this.contextPath = contexPath;
		
		if( !contexPath.endsWith("/") ){
			this.contextPath = contexPath+"/";
		}
	}

	public void setDocBase(String docBase) {
		this.docBase = docBase;
	}
	
	@Override
	public SessionManager getSessionManager() {
		return sessionManager;
	}

	/**
	 * 标准化后缀，对于目录如果结尾没有/则加上
	 * 
	 * @param s
	 * @return
	 */
	private String normalizeDirPostfix(String s) {
		if (!s.endsWith("/"))
			s = s + "/";
		return s;
	}
}
