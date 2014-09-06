package connector.request;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.smartcardio.ATR;

public class RequestLine {
	private String methodString;
	private String uri; // 此uri就是http请求里面带的uri，没有变化！
	private String protocal;
	
	private Map<String, String> paramMap = new HashMap<String, String>(); //请求参数中的参数
	private Map<String, Object> attributeMap  = new HashMap<String, Object>();

	public RequestLine(String requestLineStr) throws Exception {
		String[] parts = requestLineStr.split(" ");
		if (parts.length != 3)
			throw new Exception("请求首行不足三部分:" + requestLineStr);

		methodString = parts[0];
		protocal = parts[2];

		// 获得请求行中的第二部分，即请求的uri以及get参数
		String requestPath = parts[1];
		parseRequestPath(requestPath);
		
		//把参数映射复制到属性映射里面去
		copyMap(paramMap, attributeMap);
	}


	private void copyMap(Map<String, String> srcMap,
			Map<String, Object> destMap) {
		Iterator<Entry<String, String>> iter = srcMap.entrySet().iterator();
		
		while( iter.hasNext() ){
			Entry<String, String> entry = iter.next();
			
			attributeMap.put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * 解析请求的路径字符串，从中获得请求的uri以及参数Map
	 * @param requestPath
	 * @throws Exception 
	 */
	private void parseRequestPath(String requestPath) throws Exception {
		// 获取请求分隔符?的位置
		int dividorPos = requestPath.indexOf("?");
		// 如果没有分隔符，表示没有参数
		if (dividorPos == -1) {
			uri = requestPath;
			return;
		}

		String[] parts = requestPath.split("\\?");

		uri = parts[0];
		
		String paramsStr = parts[1];
		paramMap = getParamMap(paramsStr);
	}

	/**
	 * 从参数字符串(xx=1&yy=2的形式)中解析出参数映射
	 * @param str
	 * @return
	 * @throws Exception 
	 */
	private Map<String, String> getParamMap(String str) throws Exception {
		HashMap<String, String> paramStrMap = new HashMap<String, String>();
		
		String[] pairStrs = str.split("&");
		for(String pairStr: pairStrs){
			String[] parts = pairStr.split("=");
			if( parts.length != 2 )
				throw new Exception("get参数异常："+pairStr);
			
			String key = parts[0];
			String value = parts[1];
			
			paramStrMap.put(key, value);
		}
		
		return paramStrMap;
	}

	public String getMethodString() {
		return methodString;
	}

	public String getUri() {
		return uri;
	}

	public String getProtocal() {
		return protocal;
	}

	public Map<String, String> getParamMap() {
		return paramMap;
	}

	public Map<String, Object> getAttributeMap() {
		return attributeMap;
	}
	
}
