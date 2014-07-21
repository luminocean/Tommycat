package connector.response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;

/**
 * 
 * @author zhangh-fnst
 *
 */
public class ResponseStream extends OutputStream{
	private OutputStream os;
	
	private ByteArrayOutputStream buffer;

	public ResponseStream(OutputStream os) {
		this.os = os;
		
		//准备内部的字节缓冲
		buffer = new ByteArrayOutputStream();
	}

	@Override
	public void write(int b) throws IOException {
		buffer.write(b);
	}
	
	public void write(String s) throws IOException {
		byte[] bytes = s.getBytes();
		
		write(bytes);
	}

	/**
	 * 响应过程中最后需要调用的方法
	 * 将缓存中的所有数据全部输出到socket的输出流中（包括响应头）
	 * 因为只有这个时候才能知道响应体到底有多大
	 * @throws IOException 
	 */
	public void flushAll() throws IOException{
		//要注意的是输出过程中只能用这一个writer，不要一会用writer一会又用os，因为writer里面是否缓存的，而flush只能用一次，切记
		PrintWriter writer = new PrintWriter(os, false);
		//输出响应头部
		int bufferSize = buffer.size();
		writer.println("HTTP/1.1 200 OK");
		writer.println("Content-Type: text/html;charset=utf-8");
		writer.println("Content-Length: "+bufferSize);

		//不可缺少的空行
		writer.println();
		
		//输出报文体
		writer.print(new String(buffer.toByteArray()));
		//一次性的flush,否则连浏览器都会出问题
		writer.flush();
	}
}
