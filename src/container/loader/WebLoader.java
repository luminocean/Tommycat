package container.loader;

import java.net.URL;
import java.net.URLClassLoader;

import javax.servlet.Servlet;

import util.Constants;
import container.Container;

/**
 * 负责web项目所有中java类资源的加载，主要就是servlet的加载
 * @author zhangh-fnst
 *
 */
public class WebLoader implements Loader{
	private Container relatedContainer;

	public WebLoader(Container container) {
		this.relatedContainer = container;
	}

	@Override
	public Servlet loadServlet(String servletName) {
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
		
		return null;
	}
}
