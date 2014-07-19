import java.io.OutputStream;
import java.io.PrintWriter;


public class Response {
	private OutputStream os;

	public Response(OutputStream os) {
		this.os = os;
	}

	public void send() {
		//设置自动flush，这样就不用手动flush了
		PrintWriter writer = new PrintWriter(os, true); 
		//向客户端写回一条信息表示响应
		writer.println("GOT IT");
	}

}
