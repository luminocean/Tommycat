package os;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.Servlet;

import util.Constants;
import util.Logger;

public class ServletLoader {

	public Servlet loadServlet(String servletName) {
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

	/**
	 * 判断是否存在有个servlet的class文件
	 * @param servletName
	 * @return
	 */
	private boolean isServletClassExists(String servletName) {
		File file = new File(Constants.WEB_ROOT+Constants.APP_CLASS_PATH+servletName+".class");
		
		return file.exists();
	}

}
