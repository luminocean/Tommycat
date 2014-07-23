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
import container.DefaultContainer;
import container.DefaultWrapper;
import util.Logger;

public class ServerLauncher {
	public static void main(String[] args) {
		Connector connector = new Connector();
		//Container container = new DefaultContainer();
		
		//每个wrapper都会关联唯一的一个servlet，所以直接用构造函数把servlet名字传进去就是了
		DefaultWrapper wrapper = new DefaultWrapper();

		/*
		 * 在这里先简单粗暴的配置一个servlet！在connector完成后会调用到这个wrapper的invoke方法
		 * 
		 * 在这行存在的时候，所有的请求都会被引导到用户的servlet上面去！
		 */
		wrapper.setup("servlet.MainServlet");
		
		//将Connector和Container关联起来
		//当Connector使用processor准备好request和response以后将会传递给Container
		connector.setContainer(wrapper);
		
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
