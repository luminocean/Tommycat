import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class ServerLauncher {

	public static void main(String[] args) {
		try {
			new ServerLauncher().run();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void run() throws Exception {
		ServerSocket serverSocket;

		serverSocket = new ServerSocket(8080, 1, InetAddress.getByName("127.0.0.1"));
		Socket socket = serverSocket.accept();

		InputStream is = socket.getInputStream();
		OutputStream os = socket.getOutputStream();
		
		PrintWriter writer = new PrintWriter(os, true);

		byte[] buffer = new byte[1024];
		
		int readBytes = 0;

		while( true ){
			readBytes = is.read(buffer);
			
			if( readBytes == -1 )
				break;

			byte[] contentInByte = Arrays.copyOfRange(buffer, 0, readBytes);
			
			String content = new String(contentInByte);
			System.out.println("读取消息："+ content);
			
			writer.println("GOT IT");
			
			Thread.sleep(1000);
		}

		serverSocket.close();
	}
}
