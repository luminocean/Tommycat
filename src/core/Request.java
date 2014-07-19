package core;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;


public class Request {
	private String content;
	private String uri;
	
	public Request(InputStream is) throws Exception{
		byte[] buffer = new byte[2048];
		int readedBytes = is.read(buffer);
		
		byte[] contentBytes = Arrays.copyOfRange(buffer, 0, readedBytes);
		String content = new String(contentBytes);
		
		this.content = content;
		
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
		
		return headLineParts[1];
	}
	
	
	public String toString(){
		return content;
	}
	
	public String getUri() {
		return uri;
	}
}
