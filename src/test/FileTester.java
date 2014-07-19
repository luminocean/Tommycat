package test;

import util.Constants;
import logic.FileHelper;

public class FileTester {
	public static void main(String[] args) throws Exception{
		String content = FileHelper.getFileContent(Constants.WEB_ROOT+"/msg.txt");
		
		System.out.println(content);
	}
}
