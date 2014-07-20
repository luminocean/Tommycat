package core;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import util.Logger;

public class ServerLauncher {
	private static final int MAX_READ_TIMES = 3;
	
	private boolean isShutdown = false;

	public static void main(String[] args) {
		try {
			new ServerLauncher().run();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void run() throws Exception {
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
			
			//开始分发请求
			dispatch(request, response);
			
			//连接结束
			socket.close();
		}
		
		serverSocket.close();
	}

	/**
	 * 分发请求，为请求找到合适的处理器，处理并发回响应
	 * @param request
	 * @param response
	 */
	private void dispatch(Request request, Response response) {
		String uri = request.getUri();
		
		//判断该请求该何去何从
		if( uri.startsWith("/servlet/") ){
			//进入servlet处理路线，将request和response传入处理器
			ServletProcessor processor = new ServletProcessor();
			processor.process(request, response);
		}else{
			//进入静态资源处理路线
			//向客户端发送响应
			response.sendStaticResource();
		}
	}
}
