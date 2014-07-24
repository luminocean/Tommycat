package container;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 包装了所有代码存放点的信息
 * @author zhangh-fnst
 *
 */
public class Repository {
	//包装的核心文件
	private File file;
	
	public Repository(File file){
		assert file!=null && file.exists():"传入Repository的文件居然不存在！没有检查？！";
		
		this.file = file;
	}

	public String getPath() {
		return file.getPath();
	}

	public long getModifiedTime() {
		long latestTime = 0;
		
		Queue<File> fileQueue = new LinkedList<File>();
		fileQueue.add(file);
		
		while( fileQueue.size() > 0 ){
			File f = fileQueue.poll();
			
			if( f.isDirectory() ){
				File[] subFiles = f.listFiles();
				for( File s: subFiles ){
					if( s.lastModified() > latestTime )
						latestTime  = s.lastModified();
							
					fileQueue.add(s);
				}
			}
		}
		
		return latestTime;
	}
}
