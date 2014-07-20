package os;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.Servlet;

import util.Constants;

public class ServletLoader {

	public Servlet loadServlet(String servletName) {
		if( isServletClassExists(servletName) ){
			if( isServletSourceExists(servletName) ){
				compileInTime(servletName);
			}else{
				return null;
			}
		}
		
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

	
	private void compileInTime(String servletName) {
		
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

	/**
	 * 判断是否存在servlet的源文件，有的话就可以在没有class文件的时候把它编译了
	 * @param servletName
	 * @return
	 */
	private boolean isServletSourceExists(String servletName) {
		File file = new File(Constants.WEB_ROOT+Constants.APP_SRC_PATH+servletName+".java");
		
		return file.exists();
	}
}
