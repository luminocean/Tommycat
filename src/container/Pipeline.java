package container;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import util.Logger;
import connector.request.Request;
import connector.response.Response;
import container.valve.BasicWrapperValve;
import container.valve.Valve;
import container.valve.ValveContext;

public class Pipeline {
	private BasicWrapperValve basicValve;
	private List<Valve> valves = new ArrayList<Valve>();

	public void setBasicValve(BasicWrapperValve basicValve) {
		this.basicValve = basicValve;
		valves.add(basicValve);
	}
	
	public void invoke(Request request, Response response) 
			throws ServletException, IOException {
		if( basicValve == null )
			Logger.info("Wrapper的BasicValve未配置，Servlet将不会执行");
		
		//交给ValveContext去调用，实现valve之间的调用链
		new ValveContext(valves).invokeNext(request, response);
	}
}
