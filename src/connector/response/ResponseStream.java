package connector.response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletOutputStream;

import com.sun.corba.se.impl.ior.WireObjectKeyTemplate;

/**
 * 
 * @author zhangh-fnst
 *
 */
public class ResponseStream extends OutputStream{
	private OutputStream os;
	
	private ByteArrayOutputStream buffer;
	private List<Pair> headParams = new LinkedList<Pair>();

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
		
		int bufferSize = buffer.size();
		//写首行
		writeHeadLine(writer, "HTTP/1.1 200 OK");
		//输出响应头部
		addHeadParam("Content-Type", "text/html;charset=utf-8");
		addHeadParam("Content-Length", bufferSize+"");
		//addHeadParam("Connection", "keep-alive");
		writeHeadParams(writer, headParams);

		//不可缺少的空行
		writer.println();
		
		//输出报文体
		writer.print(new String(buffer.toByteArray()));
		//一次性的flush,否则连浏览器都会出问题
		writer.flush();
	}
	
	/**
	 * 写入所有的头部参数
	 * @param writer
	 * @param headParams
	 */
	private void writeHeadParams(PrintWriter writer, List<Pair> headParams) {
		for(Pair p: headParams){
			writer.println(p.key+": "+p.value);
			System.out.println(p.key +", "+p.value);
		}
	}

	/**
	 * 写入首行
	 * @param writer
	 * @param headLine
	 */
	private void writeHeadLine(PrintWriter writer, String headLine){
		writer.println(headLine);
	}
	
	/**
	 * 添加一个头部的参数（可能被外部的类所调用）
	 * @param key
	 * @param value
	 */
	public void addHeadParam(String key, String value){
		Pair p = new Pair();
		p.key = key;
		p.value = value;
		
		headParams.add(p);
	}
	
	public void setSession(String sessionId) {
		addHeadParam("Set-Cookie", "JSESSIONID="+sessionId);
	}
	
	/**
	 * 内部使用的键值对
	 * @author luMinO
	 *
	 */
	class Pair{
		String key;
		String value;
	}
}
