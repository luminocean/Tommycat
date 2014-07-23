package container.wrapper;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import connector.request.Request;
import connector.request.RequestFacade;
import connector.response.Response;
import connector.response.ResponseFacade;
import container.Valve;
import container.ValveContext;

/**
 * Wrapper的基础valve，职责就是最终调用servlet完成服务，在所有其他的valve之后调用
 * @author zhangh-fnst
 *
 */
public class BasicWrapperValve implements Valve{
	private Servlet servlet;

	public BasicWrapperValve(Servlet servlet) {
		this.servlet = servlet;
	}

	/**
	 * 最终调用servlet完成核心功能的方法！
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@Override
	public void invoke(Request request, Response response,
			ValveContext valveContext) throws ServletException, IOException {
		//在传递给Servlet之前将其包装
		ServletRequest sreq = new RequestFacade(request);
		ServletResponse sres = new ResponseFacade(response);
		
		servlet.service(sreq, sres);
		
		//说实话在BasicValve里面写这一句话没什么意义，毕竟是最后一个valve了
		valveContext.invokeNext(request, response);
	}
}
