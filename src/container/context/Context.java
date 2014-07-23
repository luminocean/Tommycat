package container.context;

import java.util.Map;

import container.Container;

public interface Context extends Container{
	Map<String, String> getServletNameMap();
	Map<String, Container> getChildrenMap();
}
