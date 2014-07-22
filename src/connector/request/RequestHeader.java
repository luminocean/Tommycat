package connector.request;

import java.util.HashMap;
import java.util.Map;

public class RequestHeader {
	private Map<String, String> headerMap = new HashMap<String, String>();

	public RequestHeader(String headerStr) throws Exception {
		String[] lines = headerStr.split("\n");
		
		for(String line: lines){
			int dividorPos = line.indexOf(":");
			
			if( dividorPos == -1 )
				throw new Exception("请求头部里面的行有问题: "+line);
			
			String key = line.substring(0, dividorPos);
			String value = line.substring(dividorPos+1).trim();
			
			headerMap.put(key, value);
		}
	}
	
	public boolean isKeepAlive() {
		String s = headerMap.get("Connection");
		
		return s.equals("keep-alive");
	}
	
	public String getHeaderParam(String key){
		return headerMap.get(key);
	}

}
