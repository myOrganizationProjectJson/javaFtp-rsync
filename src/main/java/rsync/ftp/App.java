package rsync.ftp;

import connectFtp.download;
import properties.properties;

public class App 
{
	
	
	   public static void main( String[] args )
	    {
		   
	    	download download =new download();
	    	download.host=properties.GetValueByKey("host");
	    	download.port=Integer.parseInt(properties.GetValueByKey("port"));
	    	download.connectTimeoutSecond=Integer.parseInt(properties.GetValueByKey("connectTimeoutSecond"));
	    	download.connectTimeoutSecond=Integer.parseInt(properties.GetValueByKey("connectTimeoutSecond"));
	    	download.dataTimeoutSecond=Integer.parseInt(properties.GetValueByKey("dataTimeoutSecond"));
	    	download.user=properties.GetValueByKey("user");
	    	download.password= properties.GetValueByKey("password");
	    	download.ftpPath=  properties.GetValueByKey("ftpPath");
	    	download.localPath=properties.GetValueByKey("localPath");
	    	
	    	download.download();
	    	
	    }
}
