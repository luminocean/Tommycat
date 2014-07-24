package container.loader;

import javax.servlet.Servlet;

import core.LifeCycle;

public interface Loader extends LifeCycle{
	Servlet loadServlet(String servletName);
}
