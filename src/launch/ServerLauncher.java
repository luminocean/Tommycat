package launch;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import connector.Connector;
import connector.request.Request;
import connector.response.Response;
import container.Container;
import container.context.StandardContext;
import container.wrapper.StandardWrapper;
import util.Constants;
import util.Logger;

public class ServerLauncher {
	public static void main(String[] args) {
		Connector connector = new Connector();

		//每个wrapper都会关联唯一的一个servlet，在创建后设置该servlet的名字以便加载该类
		StandardWrapper wrapper1 = new StandardWrapper("servlet.MainServlet");
		StandardWrapper wrapper2 = new StandardWrapper("servlet.ToyServlet");
		
		//WebApps目录下现在有的context文件夹名
		//实际上应该有好多个，这里暂时放一个
		String contextName = scanContext();
		if( contextName == null ){
			Logger.error("WebApps下没有检测到任何context，请确认是否配置了web项目！");
			System.exit(0);
		}
		
		//将wrapper放到context里面，并配置映射
		StandardContext context = new StandardContext();
		context.setContextPath("/" + contextName); //设置context的特有属性
		context.setDocBase(contextName);
		context.addChild(wrapper1);
		context.addChild(wrapper2);
		context.addServletMapping("/main", "servlet.MainServlet");
		context.addServletMapping("/toy", "servlet.ToyServlet");
		
		//添加代码存放点，只需要填写相对路径，也不用考虑要不要加后缀分隔符的问题
		//在这里添加的项目里面的servlet被编译后被eclipse默认放到了bin文件夹下，因此这里写的是bean
		//如果是真实的web项目，那么应该是/build/classes
		context.addRepository(Constants.WEB_APP_CLASS_PATH);
		
		//connector将会针对每一个请求开启一个线程调用该container实例
		connector.setContainer(context);
		
		try {
			//异步启动connector
			connector.start();
			//启动context，connector是不会去启动context的
			context.start();
			
			//主线程暂停
			System.in.read();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检索WebApps根目录下的web项目，（暂时）返回第一个
	 * @return
	 */
	private static String scanContext() {
		File webApps = new File(Constants.WEB_ROOT);
		
		File[] files = webApps.listFiles();
		for(File file: files){
			if( file.isDirectory() ){
				return file.getName();
			}
		}
		
		return null;
	}
}
