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
import core.ServletProcessor;
import util.Logger;

public class ServerLauncher {
	public static void main(String[] args) {
		Connector connector = new Connector();
		
		try {
			connector.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
