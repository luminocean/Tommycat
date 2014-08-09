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
import util.Logger;

public class ServerLauncher {
	public static void main(String[] args) {
		Connector connector = new Connector();

		//每个wrapper都会关联唯一的一个servlet，在创建后设置该servlet的名字以便加载该类
		StandardWrapper wrapper1 = new StandardWrapper("servlet.MainServlet");
		StandardWrapper wrapper2 = new StandardWrapper("servlet.ToyServlet");
		
		//将wrapper放到context里面，并配置映射
		StandardContext context = new StandardContext();
		//设置context名字，很重要，用来拼接代码查找目录的
		context.setPath("/mushroom");
		context.setDocBase("Mushroom");
		context.addChild(wrapper1);
		context.addChild(wrapper2);
		context.addServletMapping("/main", "servlet.MainServlet");
		context.addServletMapping("/toy", "servlet.ToyServlet");
		
		//添加代码存放点，只需要填写相对路径，也不用考虑要不要加后缀分隔符的问题
		String repoStr = "bin";
		context.addRepository(repoStr);
		
		
		//connector将会针对每一个请求开启一个线程调用该container实例
		connector.setContainer(context);
		
		try {
			//异步启动connector
			connector.start();
			//启动context，connector是不会去启动context的
			context.start();
			
			//主线程暂停
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
