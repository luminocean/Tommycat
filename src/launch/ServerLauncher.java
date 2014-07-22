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
import core.Container;
import util.Logger;

public class ServerLauncher {
	public static void main(String[] args) {
		Connector connector = new Connector();
		Container container = new Container();
		
		//将Connector和Container关联起来
		//当Connector使用processor准备好request和response以后将会传递给Container
		connector.setContainer(container);
		
		try {
			//启动connector
			connector.start();
			
			System.out.println("输入任何字符停止服务器:");
			//使用标准输入阻塞应用，输入任何字符即可终止整个进程
			new Scanner(System.in).next();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
