package connector;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import util.Logger;
import connector.request.Request;
import connector.response.Response;
import core.Container;

public class Connector {
	private static final int MAX_READ_TIMES = 3;
	private boolean isShutdown = false;
	
	private Container container;
	
	/**
	 * 连接器启动，接受用户的连接请求
	 * @throws Exception
	 */
	public void run() throws Exception {
		Logger.debug("服务器启动");
		
		//把服务器Socket绑定在本地
		ServerSocket serverSocket = new ServerSocket(8080, 1, InetAddress.getByName("127.0.0.1"));
		
		while(true){
			if( isShutdown ) break;
			//进入等待状态
			Socket socket = serverSocket.accept();
			socket.setSoTimeout(1000);	//设置read操作的阻塞时间
			
			//完成socket连接后，获取输入输出流
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			
			//根据两个流创建request和response
			Request request = new Request(is);
			Response response = new Response(os, request);
			
			//从现在开始交给container来处理后续的事情！
			container.invoke(request, response);
			
			//连接结束
			socket.close();
		}
		
		serverSocket.close();
	}

	public void setContainer(Container container) {
		this.container = container;
	}
}
