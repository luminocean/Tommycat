package util.os;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

import connector.response.ResponseStream;
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
	public static String getWebAppFileContent(String uri) throws Exception {
		return getFileContent( getLocationInWebApp(uri) );
	}

	private static String getLocationInWebApp(String uri) {
		String location = null;
		//uri里面是带有根目录的/的（因为是请求），所以在拼接目录的时候应该去掉请求里面的根目录
		if( uri.startsWith("/") )
			location = uri.substring(1);
		else
			location = uri;
		
		//WebRoot由于是一个目录，所以最后会带上/
		return Constants.WEB_ROOT+ location;
	}

	
	/**
	 * 判断当前web项目中是否有指定的资源
	 * @param uri
	 */
	public static boolean hasResource(String uri) {
		String fileLocation = getLocationInWebApp(uri);
		
		File file = new File(fileLocation);
		
		if( file.exists() )
			return true;
		return false;
	}
	
	
	public static void writeFileToStream(String uri, OutputStream stream) {
		String fileLocation = getLocationInWebApp(uri);
		
		File file = new File(fileLocation);
		
		//如果文件不存在则直接返回
		if( !file.exists() )
			return;
		
		//如果存在就开始把文件按字节写入输出流
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			
			byte[] buffer = new byte[2048];
			int readedChars = 0;
			do{
				readedChars = fis.read(buffer);
				if( readedChars > 0 ){
					stream.write(buffer, 0, readedChars);
				}
			}while( readedChars > 0 );
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try { fis.close(); } catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
