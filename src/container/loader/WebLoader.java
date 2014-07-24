package container.loader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import javax.servlet.Servlet;

import util.Constants;
import util.Logger;
import container.Container;
import container.Repository;

/**
 * 负责web项目所有中java类资源的加载，主要就是servlet的加载
 * @author zhangh-fnst
 *
 */
public class WebLoader implements Loader{
	private Container relatedContainer;
	private ClassLoader classLoader;

	public WebLoader(Container container) {
		this.relatedContainer = container;
	}

	@Override
	public Servlet loadServlet(String servletName) {
		if( classLoader == null )
			createClassLoader();
		
		try{
			Class servletClass = classLoader.loadClass(servletName);
			Servlet servlet = (Servlet)servletClass.newInstance();
			
			return servlet;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
		
		/**
		//所使用路径暂时是硬编码的
		try{
			URL servletPath = new URL("file", null, Constants.WEB_ROOT+ Constants.APP_CLASS_PATH);
			
			URLClassLoader classLoader = new URLClassLoader(new URL[]{servletPath});
			
			Class servletClass = classLoader.loadClass(servletName);
			
			Servlet servlet = (Servlet)servletClass.newInstance();
			
			return servlet;
			
		}catch(Exception e){
			e.printStackTrace();
		}	
		
		return null;*/
	}

	/**
	 * 根据代码repositories创建对应的classLoader
	 */
	private void createClassLoader() {
		URL[] paths = null;
		try {
			
			paths = getRepositoryPaths();
			classLoader = new URLClassLoader(paths);
			
		} catch (MalformedURLException e) {
			Logger.error("根据代码repositories创建对应的classLoader出错！");
			e.printStackTrace();
		}
		
	}

	private URL[] getRepositoryPaths() throws MalformedURLException {
		List<Repository> repoList = relatedContainer.getRepositories();
				
		URL[] urls = new URL[repoList.size()];
		
		for(int i=0; i<repoList.size(); i++){
			Repository repo = repoList.get(i);
			//在Java中使用的文件系统是以/分隔的，虽然从windows文件系统取出来是\，因此要进行转换
			//另外由于查找的目录，所以【必须】要以/结尾
			String path = repo.getPath().replace('\\', '/')+"/";
			URL url = new URL("file", null, path);
			urls[i] = url;
		}
		
		return urls;
	}
}
