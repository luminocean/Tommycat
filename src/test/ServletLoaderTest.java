package test;

import java.io.File;
import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import os.ServletLoader;

public class ServletLoaderTest {

	public static void main(String[] args) {
		ServletLoader loader = new ServletLoader();
		
		Servlet servlet = loader.loadServlet("ToyServlet");
		
		try {
			servlet.service(null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
