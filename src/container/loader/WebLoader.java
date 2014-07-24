package container.loader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import javax.servlet.Servlet;

import util.Logger;
import container.Container;
import container.Repository;
import core.LifeCycle;

/**
 * 负责web项目所有中java类资源的加载，主要就是servlet的加载
 * @author zhangh-fnst
 *
 */
public class WebLoader implements Loader{
	private Container relatedContainer;
	private ClassLoader classLoader;
	
	private List<Repository> repoList;
	
	private Thread watcher;

	public WebLoader(Container container) {
		this.relatedContainer = container;
	}

	@Override
	public void start() {
		//从关联容器获取repo的列表
		repoList = relatedContainer.getRepositories();
		//创建所管理的classLoader
		classLoader = createClassLoader();
		
		//开始监视repo的变化
		startWatchingRepositories();
	}
	
	@Override
	public Servlet loadServlet(String servletName) {
		try{
			Class servletClass = classLoader.loadClass(servletName);
			Servlet servlet = (Servlet)servletClass.newInstance();
			
			return servlet;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * 根据代码repositories创建对应的classLoader
	 */
	private ClassLoader createClassLoader() {
		URL[] paths = null;
		try {
			paths = getRepositoryPaths();
			ClassLoader classLoader = new URLClassLoader(paths);
			return classLoader;
		} catch (MalformedURLException e) {
			Logger.error("根据代码repositories创建对应的classLoader出错！");
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * 获取URL形式的repo路径
	 * @return
	 * @throws MalformedURLException
	 */
	private URL[] getRepositoryPaths() throws MalformedURLException {
		URL[] urls = new URL[repoList.size()];
		for(int i=0; i<repoList.size(); i++){
			Repository repo = repoList.get(i);
			//在Java中使用的文件系统是以/分隔的，虽然从windows文件系统取出来是\，因此要进行转换
			//另外由于查找的目录，所以【必须】要以/结尾
			String path = repo.getPath().replace('\\', '/');
			if( !path.endsWith("/") )
				path = path + "/";
			
			//可以注意一下URL new的方式
			URL url = new URL("file", null, path);
			
			urls[i] = url;
		}
		
		return urls;
	}
	
	
	/**
	 * 监视repo的变化，如果发现了变化则通知container
	 */
	private void startWatchingRepositories() {
		if( watcher != null )
			return;
		
		//内嵌一个线程开始监视
		Thread t = new Thread(new Runnable() {
			public void run() {
				//进入无限循环
				while(true){try{
					long t = System.currentTimeMillis();
					Thread.sleep(5000);
					
					for(Repository r: repoList){
						if( r.getModifiedTime() > t ){
							relatedContainer.repositoryUpdateNotify();
							break;
						}
					}
				}catch(Exception e){}}
			}
		});
		
		watcher = t;
		watcher.start();
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
	}
}
