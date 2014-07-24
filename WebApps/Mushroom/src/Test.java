import java.io.File;


public class Test {

	public static void main(String[] args) {
		System.out.println(System.getProperty("user.dir"));
		System.out.println(File.separator);
		
		File f = new File(System.getProperty("user.dir")+File.separator+"index.html");
		
		System.out.println(f.exists());
		System.out.println(f.getPath());
	}

}
