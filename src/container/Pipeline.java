package container;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import util.Logger;
import connector.request.Request;
import connector.response.Response;
import container.wrapper.BasicWrapperValve;

public class Pipeline {
	private Valve basicValve;
	private List<Valve> valves = new ArrayList<Valve>();

	public void setBasicValve(Valve basicValve) {
		this.basicValve = basicValve;
		
		if( valves.size() == 0 )
			valves.add(basicValve);
		else{
			//替换最后一个valve，也即basic的valve
			valves.set(valves.size()-1, basicValve);
		}
	}
	
	public void invoke(Request request, Response response) 
			throws ServletException, IOException {
		if( basicValve == null )
			Logger.info("Wrapper的BasicValve未配置，Servlet将不会执行");
		
		//交给ValveContext去调用，实现valve之间的调用链
		new ValveContext(valves).invokeNext(request, response);
	}
}
