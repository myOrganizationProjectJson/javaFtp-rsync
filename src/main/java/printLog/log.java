package printLog;

import org.apache.log4j.Logger;

import ftpClieent.FtpManage;

public class log {
	private static Logger logger = Logger.getLogger(FtpManage.class);
	
	public static void info(String X){
		
        System.out.println(X);
        logger.info(X);
        
	}
	
	
	public static void warn(String X){
		
		System.out.println(X);
		logger.warn(X);
		
	}
	
	

}
