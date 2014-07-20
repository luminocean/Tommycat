package os;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class InTimeCompiler {

	public void compile(File javaFile) throws IOException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);  
		
        Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects(javaFile);  
        
        CompilationTask task = compiler.getTask(null, fileManager, null, null, null, fileObjects);  
        
        task.call();  
        
        fileManager.close();  
	}
	
}
