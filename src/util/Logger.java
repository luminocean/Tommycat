package util;

public class Logger {
	public static void debug(String msg){
		String callingInfo = getCallingInfo();
		System.out.println("[debug] "+msg+" "+callingInfo);
	}

	/**
	 * 获取该方法的调用方信息
	 * @return
	 */
	private static String getCallingInfo() {
		StackTraceElement[] stackElements = Thread.currentThread().getStackTrace();
		
		if( stackElements.length > 2 ){
			String callingMethod = stackElements[3].getMethodName();
			String callingClass = stackElements[3].getClassName();
			
			return "("+callingClass+":"+callingMethod+")";
		}

		return "";
	}
}
