package container;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import connector.request.Request;
import connector.response.Response;

/**
 * 主要作用就是让valve之间可以形成调用链
 * @author zhangh-fnst
 *
 */
public class ValveContext {
	private List<Valve> valves;
	
	public ValveContext(List<Valve> valves){
		this.valves = valves;
	}
	
	//当前正在处理第几个valve
	private int currentPos = 0;
	
	public void invokeNext(Request request, Response response) 
			throws ServletException, IOException {
		int pos = currentPos++;
		//调用完所有的valve就返回了
		if( pos >= valves.size() )
			return;
		
		Valve currentValve = valves.get(pos);
		currentValve.invoke(request, response, this);
	}
}
