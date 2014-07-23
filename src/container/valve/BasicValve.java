package container.valve;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import connector.request.Request;
import connector.request.RequestFacade;
import connector.response.Response;
import connector.response.ResponseFacade;

/**
 * 基础valve，职责就是最终调用servlet完成服务，在所有其他的valve之后调用
 * @author zhangh-fnst
 *
 */
public class BasicValve implements Valve{
	private Servlet servlet;

	public BasicValve(Servlet servlet) {
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
		
		//这一步非常重要！
		response.finishResponse();
	}
}
