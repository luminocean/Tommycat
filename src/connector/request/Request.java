package connector.request;
import java.io.BufferedReader;
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
	//请求里面的全部内容，包括首行以及请求头部
	private String content;
	
	//请求首行对象
	private RequestLine requestLine;
	
	public Request(InputStream is) throws Exception{
		content = loadRequest(is);
		
		String requestLineStr = getRequestLine(content);
		requestLine = new RequestLine(requestLineStr);
	}
	
	/**
	 * 从请求报文中获取请求首行
	 * @param content
	 * @return
	 * @throws Exception 
	 */
	private String getRequestLine(String content) throws Exception {
		int firstCRLFPos = content.indexOf("\n");

		if( firstCRLFPos >= 0 ){
			String requestLine = content.substring(0, firstCRLFPos);
			return requestLine;
		}else{
			throw new Exception("请求中首行解析异常！");
		}
	}

	/**
	 * 从输入流中加载整个请求的内容
	 * @param is
	 * @return
	 */
	private String loadRequest(InputStream is) {
		StringBuilder builder = new StringBuilder();
		
		//这里表明目前一个http请求最大的大小为2048个字节
		//同时之所以使用字节的方式而不是一个个char读进来是考虑到对中文的支持，按照字节读中文就铁定会出问题的
		byte[] buffer = new byte[BUFFER_SIZE];
		
		int readedBytes = 0;
		do{
			try{
				readedBytes = is.read(buffer);
			}catch(SocketTimeoutException e){
				//如果运气不好数据正好是BUFFER_SIZE的正数倍，那么只能等到超时了
				Logger.debug("socket读取数据超时，结束读取");
				break;
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			byte[] bytes = Arrays.copyOfRange(buffer, 0, readedBytes);
			String contentPart = new String(bytes);
			
			builder.append(contentPart);
		}while( readedBytes == BUFFER_SIZE ); //如果上一次读取时读满的，那么极有可能下面还有一次
		
		String content = builder.toString();
		
		return content;
	}
	
	public String toString(){
		return content;
	}
	
	public String getUri() {
		return requestLine.getUri();
	}
}
