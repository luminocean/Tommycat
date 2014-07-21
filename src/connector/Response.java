package connector;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import util.Constants;
import util.Logger;
import util.os.FileHelper;


public class Response {
	//直接从socket那里拿到的输出流
	private OutputStream os;
	
	private Request request;
	
	//将会交给servlet使用的输出流，它包装了OutputStream os
	private ResponseStream responseStream;
	
	
	public Response(OutputStream os, Request request) {
		this.os = os;
		this.request = request;
		
		this.responseStream = new ResponseStream(os);
	}

	/**
	 * 发送静态资源
	 */
	public void sendStaticResource() {
		//获取目标uri，如果是请求根目录则手动将其跳转到index.html上面去
		String uri = request.getUri();
		if( uri.equals("/") ){
			uri = "/index.html";
		}
		
		String fileContent = "";
		try{
			fileContent = FileHelper.getWebAppFileContent(uri);
			Logger.debug("读取请求的文件内容："+ fileContent);
			
			responseStream.write(fileContent);
			
			finishResponse();
			//writeResponse(os, fileContent);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void finishResponse() throws IOException{
		responseStream.flushAll();
	}
	
	public PrintWriter getWriter() {
		//送出去之前先开启自动flush, 防止使用方忘记flush
		//PrintWriter writer = new PrintWriter(os, true);
		
		PrintWriter writer = new PrintWriter(responseStream, false);
		
		return writer;
	}

	private void writeResponse(OutputStream os, String body) {
		writeHead(os, body.length());
		
		PrintWriter writer = new PrintWriter(os);
		writer.println(); //别忘记输出空行
		
		//向客户端回写静态资源
		writer.print(body);
		
		//如果客户端只接收一次的话，使用自动flush就会提前把内容给flush掉了，于是就会缺东西
		writer.flush();
	}

	private void writeHead(OutputStream os, int length) {
		PrintWriter writer = new PrintWriter(os);
		
		writer.println("HTTP/1.1 200 OK");
		writer.println("Content-Type: text/html;charset=utf-8");
		writer.println("Content-Length: "+length);
		
		writer.flush();
	}
	
}
