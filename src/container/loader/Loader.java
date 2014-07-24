package container.loader;

import javax.servlet.Servlet;

public interface Loader {
	Servlet loadServlet(String servletName);
}
