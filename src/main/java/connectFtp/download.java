package connectFtp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.ftp.FTPFile;


import ftpClieent.FtpManage;
import printLog.log;

public class download {
	
   public String host;
   public int port;
   public String user;
   public String password;
   public static String ftpPath;
   public String localPath;
   public int defaultTimeoutSecond;
   public int connectTimeoutSecond;
   public int dataTimeoutSecond;
   
	public List<String> downloadFile(String ftpPath){
		log.info("download Run");
		FtpManage FtpManage=new FtpManage(defaultTimeoutSecond,connectTimeoutSecond,dataTimeoutSecond);
		List<String> dirList=null;
		try {
			
			FtpManage.connect(host, port, user, password);
			List<String> fileList=FtpManage.list(ftpPath);
			dirList=FtpManage.dirList;
			Map<String,FTPFile> FtpFilesInfo =FtpManage.FtpFilesInfo;
			List<String> downLoad =  new ArrayList<String>();
			
			//if(dirList.size() >0 ){
				/**
				 * 创建本地目录
				 */
				//for(String dir:dirList){
					File curFileDir=new File(localPath+ftpPath);
					if (!curFileDir.exists()) {
						curFileDir.mkdirs();
					}
					
				//}
			
			//}
			
			/**
			 * 
			 */
			
			if(fileList.size() >0 ){
				for(String file:fileList){
					File localFile= new File(localPath+file);
					if( !localFile.exists() 
							||( localFile.lastModified() !=FtpFilesInfo.get(file).getTimestamp().getTimeInMillis() ) 
							||( localFile.length() != FtpFilesInfo.get(file).getSize() ) ){
						downLoad.add(file);
					}else{
						log.info("已经存在,不在下载 :"+localFile);
					}
				    	
				}
			}
			
			if(downLoad.size() >0 ){
				
				for(String down:downLoad){
					
					File localFile=new File(localPath+down);
			
					String[] downDir = down.split("/");
					
					log.info(downDir[downDir.length-1]);
					
			        log.info(down.replace(downDir[downDir.length-1], FtpFilesInfo.get(down).getName()));
			        
			        
					if(FtpManage.download(down.replace(downDir[downDir.length-1], FtpFilesInfo.get(down).getName()), localFile)){
						
						localFile.setLastModified(FtpFilesInfo.get(down).getTimestamp().getTimeInMillis());
						log.info("download success: "+down);
						 
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return dirList;
	
	}
	
   public void download(String d){
	   
	   List<String> downloaded=downloadFile(d);
	   
	   for(String down:downloaded){
		   
		   System.out.println(down+"--子目录");
		   download(down);
		   
	   }
	   
   }
   
   public void download(){
	   log.info("FTP START ...");
	   download(ftpPath);
	   log.info("FTP END");
   }
   
    public static void main( String[] args )
    {
    
    	
    	
    }
    
}
