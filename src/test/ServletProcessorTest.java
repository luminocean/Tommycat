package test;

import core.ServletProcessor;

public class ServletProcessorTest {

	public static void main(String[] args) {
		ServletProcessor processor = new ServletProcessor();
		
		String result = processor.getServletName("/servlet/sara");
		System.out.println(result);
		
		result = processor.getServletName("/servlet/");
		System.out.println(result);
		
		result = processor.getServletName("/servlet");
		System.out.println(result);
	}

}
