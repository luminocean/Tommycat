package container;

import java.io.File;

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
}
