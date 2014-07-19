package logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import util.Constants;

public class FileHelper {
	public static String getFileContent(String filePath) throws Exception{
		StringBuilder builder = new StringBuilder();
		File file = new File(filePath);
		
		if( file.exists() ){
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			String buffer = null;
			while( (buffer=reader.readLine()) != null ){
				builder.append(buffer);
			}
		}
		
		return builder.toString();
	}

	public static String getWebAppFileContent(String webAppPath) throws Exception {
		return getFileContent(Constants.WEB_ROOT+"/"+ webAppPath);
	}
}
