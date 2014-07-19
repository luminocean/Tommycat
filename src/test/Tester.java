package test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

import util.Logger;

public class Tester {

	public static void main(String[] args){
		try {
			new Tester().run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void run() throws Exception{
		//获取和服务器连接的socket
		Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), 8080);
		//从socket中获取输入输出流，并将获得的流包装成字符操作类
		OutputStream os = socket.getOutputStream();
		InputStream is = socket.getInputStream();
		
		PrintWriter writer = new PrintWriter(os);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		
		writeContent(writer);
		
		while(true){
			if( reader.ready() ){
				char[] buffer = new char[1024];
				int readedChars = reader.read(buffer);
				
				char[] responseInChar = Arrays.copyOfRange(buffer, 0, readedChars);
				
				String response = new String(responseInChar);
				
				Logger.debug("获取响应："+ response);
				
				break;
			}
		}
		
		socket.close();
	}


	private void writeContent(PrintWriter writer) {
		writer.println("OPTIONS /msg.txt HTTP/1.1");
		writer.println("Host: 127.0.0.1:8080");
		writer.println("User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:30.0) Gecko/20100101 Firefox/30.0");
		writer.println("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		writer.println("Accept-Language: zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
		writer.println("Accept-Encoding: gzip, deflate");
		writer.println("Access-Control-Request-Method: GET");
		writer.println("Access-Control-Request-Headers: cache-control,if-modified-since,if-none-match");
		writer.println("Connection: keep-alive");
		
		//Header之后的空行
		writer.println();
		
		writer.flush();
	}

}
