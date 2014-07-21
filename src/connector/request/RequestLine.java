package connector.request;

public class RequestLine {
	private String methodString;
	private String uri; //此uri就是http请求里面带的uri，没有变化！
	private String protocal;
	
	public RequestLine(String requestLineStr) throws Exception {
		String[] parts = requestLineStr.split(" ");
		if( parts.length != 3 )
			throw new Exception("请求首行不足三部分: "+ requestLineStr);
		
		methodString = parts[0];
		uri = parts[1];
		protocal = parts[2];
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
}
