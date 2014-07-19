package core;
import java.io.OutputStream;
import java.io.PrintWriter;

import util.Constants;
import util.Logger;
import logic.FileHelper;


public class Response {
	private OutputStream os;
	private Request request;

	public Response(OutputStream os, Request request) {
		this.os = os;
		this.request = request;
	}

	public void send() {
		String uri = request.getUri();
		
		try{
			String fileContent = FileHelper.getWebAppFileContent(uri);
			
			Logger.debug("读取的文件内容："+ fileContent);
			
			PrintWriter writer = new PrintWriter(os, true);
			
			//向客户端回写静态资源
			writer.print(fileContent);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
