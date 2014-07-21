package launch;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

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
		//当Connector准备好request和response以后将会传递给Container
		connector.setContainer(container);
		
		try {
			connector.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
