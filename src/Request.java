import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;


public class Request {
	private String content;
	
	public Request(InputStream is) throws IOException{
		byte[] buffer = new byte[2048];
		int readedBytes = is.read(buffer);
		
		byte[] contentBytes = Arrays.copyOfRange(buffer, 0, readedBytes);
		String content = new String(contentBytes);
		
		this.content = content;
	}
	
	public String toString(){
		return content;
	}
}
