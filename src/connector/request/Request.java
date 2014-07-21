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
	//请求里面的全部内容的字符串，包括首行以、请求头部以及报文体
	private String content;
	
	//请求首行对象
	private RequestLine requestLine;
	//请求头部对象
	private RequestHeader requestHeader;
	
	public Request(InputStream is) throws Exception{
		//从输入流中读取请求报文的全部内容
		content = loadRequest(is);
		//解析出首行
		String requestLineStr = getRequestLine(content);
		requestLine = new RequestLine(requestLineStr);
		//解析请求头部
		String headerStr = getRequestHeader(content);
		requestHeader = new RequestHeader(headerStr);
	}
	
	
	/**
	 * 从请求报文字符串中获取头部
	 * @param content
	 * @return
	 */
	private String getRequestHeader(String content) {
		int startPos = content.indexOf("\n")+1;
		
		int endPos = content.indexOf("\n\n");
		//如果没有连续两个换行，即没有空白行，那么就直接去掉首行返回
		if( endPos == -1 ){
			String headerString = content.substring(startPos);
			return headerString.trim();
		}
		
		String headerString = content.substring(startPos, endPos);
		
		return headerString.trim();
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
				//如果运气不好数据正好是BUFFER_SIZE的整数倍，那么只能等到超时了
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
	
	public String getHeaderParam(String key){
		String value = requestHeader.getHeaderParam(key);
		
		return value;
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
