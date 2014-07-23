package container;

import java.io.IOException;

import javax.servlet.ServletException;

import connector.request.Request;
import connector.response.Response;

public interface Valve {
	void invoke(Request request, Response response, ValveContext valveContext) 
			throws ServletException, IOException;
}
