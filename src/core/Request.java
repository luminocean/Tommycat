package core;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Arrays;

import util.Logger;


public class Request {
	private static final int BUFFER_SIZE = 2048;
	private String content;
	private String uri;
	
	public Request(InputStream is) throws Exception{
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
			}
			
			byte[] bytes = Arrays.copyOfRange(buffer, 0, readedBytes);
			String contentPart = new String(bytes);
			
			builder.append(contentPart);
		}while( readedBytes == BUFFER_SIZE ); //如果上一次读取时读满的，那么极有可能下面还有一次
		
		
		this.content = builder.toString();
		
		uri = getURI(content);
	}
	
	/**
	 * 解析首行的URI
	 * @param content
	 * @return
	 * @throws Exception
	 */
	private String getURI(String content) throws Exception{
		String[] headLineParts = content.split(" ");
		if( headLineParts.length<3 )
			throw new Exception("首行解析失败！");
		
		String targetURI = headLineParts[1];
		
		if( targetURI.indexOf("/") == 0 ){
			if( targetURI.length() > 1)
				targetURI = targetURI.substring(1);
			else
				targetURI = "";
		}
		
		return targetURI;
	}
	
	
	public String toString(){
		return content;
	}
	
	public String getUri() {
		return uri;
	}
}
