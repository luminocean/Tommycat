import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;


public class ClientLauncher {

	public static void main(String[] args){
		try {
			new ClientLauncher().run();
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
		
		PrintWriter writer = new PrintWriter(os, true);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		
		//开始循环发送多条消息并获取响应
		for(int i=0; i<5; i++){
			writer.println("msg"+i);
			
			while(true){
				if( reader.ready() ){
					char[] buffer = new char[1024];
					int readedChars = reader.read(buffer);
					
					char[] responseInChar = Arrays.copyOfRange(buffer, 0, readedChars);
					
					String response = new String(responseInChar);
					
					System.out.println("获取响应："+ response);
					
					break;
				}
			}
			
			//发送消息之间的间隔
			Thread.sleep(2000);
		}
		
		socket.close();
	}

}
