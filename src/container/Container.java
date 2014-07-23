package container;

import connector.request.Request;
import connector.response.Response;

public interface Container {
	void invoke(Request request, Response response);
	void addChild(Container child);
	String getName();
}
