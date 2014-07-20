package os;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import util.Constants;

public class FileHelper {
	public static String getFileContent(String fileSystemPath) throws Exception{
		StringBuilder builder = new StringBuilder();
		File file = new File(fileSystemPath);
		
		if( file.exists() ){
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			String buffer = null;
			while( (buffer=reader.readLine()) != null ){
				builder.append(buffer);
			}
			
			reader.close();
		}
		
		return builder.toString();
	}

	/**
	 * 获取本web项目里面的文件
	 * @param webFileAbsPath 以服务器根目录为根目录的文件位置
	 * @return
	 * @throws Exception
	 */
	public static String getWebAppFileContent(String webFileAbsPath) throws Exception {
		//webAppPath里面是带有根目录的/的（因为是请求），所以在拼接目录的时候应该去掉请求里面的根目录
		String relativePath = webFileAbsPath;
		if( webFileAbsPath.startsWith("/") )
			relativePath = webFileAbsPath.substring(1);
		
		//WebRoot由于是一个目录，所以最后会带上/
		return getFileContent(Constants.WEB_ROOT+ relativePath);
	}
}
