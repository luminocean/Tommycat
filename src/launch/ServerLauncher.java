package launch;
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
import container.DefaultContext;
import container.DefaultWrapper;
import util.Logger;

public class ServerLauncher {
	public static void main(String[] args) {
		Connector connector = new Connector();
		//Container container = new DefaultContainer();
		
		//每个wrapper都会关联唯一的一个servlet，在创建后设置该servlet的名字以便加载该类
		DefaultWrapper wrapper = new DefaultWrapper();
		wrapper.setup("servlet.MainServlet");
		
		//将wrapper放到context里面
		DefaultContext context = new DefaultContext();
		context.addChild(wrapper);
		context.addServletMapping("/main", "servlet.MainServlet");
		
		//connector将会针对每一个请求开启一个线程调用该container实例
		connector.setContainer(context);
		
		try {
			//异步启动connector
			connector.start();
			
			//主线程暂停
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
