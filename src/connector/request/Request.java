package connector.request;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import util.Logger;


public class Request{
	private static final int BUFFER_SIZE = 2048;
	//请求里面的全部内容的字符串，包括首行以、请求头部以及报文体
	private String content;
	
	//请求首行对象
	private RequestLine requestLine;
	//请求头部对象
	private RequestHeader requestHeader;
	
	public Request(InputStream is) throws Exception{
		//读取首行
		String requestLineStr = readRequestLine(is);
		Logger.debug("**"+requestLineStr);
		
		requestLine = new RequestLine(requestLineStr);
		
		//读取报文头
		String headerStr = readHeader(is);
		Logger.debug("**"+headerStr);
		
		requestHeader = new RequestHeader(headerStr);

		/**
		 * 请先把这个请求做完了再测试！否则浏览器连续发送请求的时候它会认为前一个连接失败了于是新开了一个！
		 */
	}
	
	
	private String readRequestLine(InputStream is) throws SocketTimeoutException, IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		//先把前端所有的空字符给清理掉
		int c = 0;
		do{
			c = is.read();
		}while( c==' ' || c==10 || c==13 );
		
		//读取socket流直到读到换行符为止
		//13号是CR 10号是LF，这俩都要排除(在java中这两个都被认为是换行符，其中\n是10)
		while( c != -1 && c != 13){
			char peek = (char)c;
			baos.write(c);
			c = is.read();
		}
		
		if( c == 13 ){
			//把剩下那个LF也去掉
			c = is.read();
			assert c==10:"请求里面CF后面没有跟LF！";
		}
		
		byte[] bytes = baos.toByteArray();
		String requestLine = new String(bytes);
		
		return requestLine;
	}
	
	private String readHeader(InputStream is) throws SocketTimeoutException, IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		//预警标志，连续两个CRLF表示头部结束
		//因此如果在CRLF中读到了LF之后又读到了CR,就意味着读取结束，可以返回了
		boolean flag = false;
		
		int c = is.read();
		while( c != -1 ){
			//读到LF，预警
			if( c == 10 ) flag = true;
			else if( c == 13 ){
				//又读到了CR，读取结束
				if(flag) break;
				//只是换行，预警解除
				else flag = false;
			}else{
				flag = false;
			}
			baos.write(c);
			c = is.read();
		}
		
		byte[] bytes = baos.toByteArray();
		String headerStr = new String(bytes);
		
		return headerStr;
	}

	
	public String getHeaderParam(String key){
		String value = requestHeader.getHeaderParam(key);
		
		return value;
	}
	
	public boolean isKeepAlive() {
		return requestHeader.isKeepAlive();
	}
	
	public Map<String, String> getParamMap() {
		return requestLine.getParamMap();
	}

	public Map<String, Object> getAttributeMap() {
		return requestLine.getAttributeMap();
	}
	
	
	public String toString(){
		return content;
	}
	
	public String getUri() {
		return requestLine.getUri();
	}
}
