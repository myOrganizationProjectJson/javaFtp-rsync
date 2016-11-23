package ftpClieent;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

public class FtpManage {
	private static Logger logger = Logger.getLogger(FtpManage.class);
	private FTPClient ftp;
	
	public Map<String,FTPFile> FtpFilesInfo = new HashMap();
	public List<String> fileList = new ArrayList<String>();
	public List<String> dirList = new ArrayList<String>();
	
	public FtpManage() {
		ftp = new FTPClient();
	}

	public FtpManage(int defaultTimeoutSecond, int connectTimeoutSecond, int dataTimeoutSecond) {
		ftp = new FTPClient();

		ftp.setDefaultTimeout(defaultTimeoutSecond * 1000);
		ftp.setConnectTimeout(connectTimeoutSecond * 1000);
		ftp.setDataTimeout(dataTimeoutSecond * 1000);
	}

	/**
	 * 连接到FTP服务器
	 * 
	 * @param host
	 *            FTP server address or name
	 * @param port
	 *            FTP server port
	 * @param user
	 *            user name
	 * @param password
	 *            user password
	 * @param isTextMode
	 *            text / binary mode switch
	 * @throws IOException
	 *             on I/O errors
	 */
	public void connect(String host, int port, String user, String password) throws IOException {
		// Connect to server.
		try {
			ftp.connect(host, port);
		} catch (UnknownHostException ex) {
			throw new IOException("Can't find FTP server '" + host + "'");
		}

		// Check rsponse after connection attempt.
		int reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			throw new IOException("Can't connect to server '" + host + "'");
		}

		// Login.
		if (!ftp.login(user, password)) {
			throw new IOException("Can't login to server '" + host + "'");
		}

		// Set data transfer mode.
		ftp.setFileType(FTP.BINARY_FILE_TYPE);
		ftp.enterLocalPassiveMode();
		System.out.println("连接ftp ...");
	}

	/**
	 * 从FTP下载文件
	 * 
	 * @param ftpFileName
	 *            server file name (with absolute path)
	 * @param localFile
	 *            local file to download into
	 * @return 
	 * @throws IOException 
	 * @throws Exception
	 */
	public boolean download(String ftpFileName, File localFile) throws IOException {
		// Download.
		OutputStream out = null;
		try {
			
			// Get file info.
			FTPFile[] fileInfoArray = ftp.listFiles(ftpFileName);
			if (fileInfoArray == null) {
				throw new FileNotFoundException("File " + ftpFileName + " was not found on FTP server.");
			}
			
			// Check file size.
			FTPFile fileInfo = fileInfoArray[0];
			long size = fileInfo.getSize();
			if (size > Integer.MAX_VALUE) {
				throw new IOException("File " + ftpFileName + " is too large.");
			}

			// Download file.
			out = new BufferedOutputStream(new FileOutputStream(localFile));
			if (!ftp.retrieveFile(ftpFileName, out)) {
				throw new IOException("Error loading file " + ftpFileName
						+ " from FTP server. Check FTP permissions and path.");
			}
			
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ex) {
					throw ex;
				}
				
			}
		}
	     return true;
	}
	
	/**
	 * 从FTP下载文件
	 * 
	 * @param ftpFileName
	 *            server file name (with absolute path)
	 * @param localFile
	 *            local file to download into
	 * @throws IOException 
	 * @throws Exception
	 */
	public boolean additionalDownload(String ftpFileName, File localFile) throws IOException {
		// Download.
		OutputStream out = null;
		try {
			
			//System.out.println("============ftpFileName============="+ftpFileName);
			FTPFile[] fileInfoArray = null;
			try {
				fileInfoArray = ftp.listFiles(ftpFileName);
				
			} catch (Exception e) {
				System.out.println("File " + ftpFileName + " was not found on FTP server.");
				return false;
			}
			
			
			if(fileInfoArray.length > 0) {
				System.out.println(new Date()+"---------------"+ftpFileName);
				// Check file size.
				FTPFile fileInfo = fileInfoArray[0];
				long size = fileInfo.getSize();
				if (size > Integer.MAX_VALUE) {
					throw new IOException("File " + ftpFileName + " is too large.");
				}

				// Download file.
				out = new BufferedOutputStream(new FileOutputStream(localFile));
				if (!ftp.retrieveFile(ftpFileName, out)) {
					throw new IOException("Error loading file " + ftpFileName
							+ " from FTP server. Check FTP permissions and path.");
				}
				
				return true;
			}
			
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ex) {
				}
			}
		}
		return false;
	}

	/**
	 * 获取FTP的文件列表
	 * 
	 * @param filePath
	 *            absolute path on the server
	 * @return files relative names list
	 * @throws IOException
	 *             on I/O errors
	 */
	public List<String> list(String filePath) throws IOException {
		

		// Use passive mode to pass firewalls.
		FTPFile[] ftpFiles;
		ftpFiles = ftp.listFiles(filePath);
	
		int size = (ftpFiles == null) ? 0 : ftpFiles.length;
		for (int i = 0; i < size; i++) {
			FTPFile ftpFile = ftpFiles[i];
			String name=filePath+ftpFile.getName();
			name=new String(name.getBytes("iso-8859-1"),"GBK");
			if (ftpFile.isFile()) {
				fileList.add(name);
				FtpFilesInfo.put(name, ftpFile);
				//System.out.println(filePath+ftpFile.getName()+"文件大小....."+ftpFile.getSize());
			}else if (ftpFile.isDirectory()){
				//list(name+"/");
				dirList.add(filePath+ftpFile.getName()+"/");
				System.out.println(filePath);
			}
			
		}
		
		return fileList;
	}
	
	public List<String> listDir(String filePath) throws IOException {
		List<String> fileList = new ArrayList<String>();
		
		// Use passive mode to pass firewalls.
		FTPFile[] ftpFiles;
		ftpFiles = ftp.listFiles(filePath);
		int size = (ftpFiles == null) ? 0 : ftpFiles.length;
		for (int i = size-1; i >=0; i--) {
			FTPFile ftpFile = ftpFiles[i];
			if (ftpFile.isDirectory()) {
				fileList.add(ftpFile.getName());
				//System.out.println(filePath+ftpFile.getName()+"文件大小....."+ftpFile.getSize());
			}
		}
		
		return fileList;
	}

	/**
	 * 关闭FTP
	 * 
	 * @throws IOException
	 *             on I/O errors
	 */
	public void disconnect() {

		if (ftp.isConnected()) {
			try {
				ftp.logout();
				ftp.disconnect();
			} catch (IOException ex) {
				logger.error(ex, ex);
				if (ftp.isConnected()) {
					try {
						ftp.disconnect();
					} catch (IOException e) {
						logger.error(e, e);
					}
				}
			}
		}
	}

	/**
	 * 生成路径
	 * 
	 * @param ftpPath
	 *            file path on the server
	 * @param localFile
	 *            local file
	 * @return full name of the file on the FTP server
	 */
	public String makeFTPFileName(String ftpPath, File localFile) {
		if (ftpPath == "") {
			return localFile.getName();
		} else {
			String path = ftpPath.trim();
			if (path.charAt(path.length() - 1) != '/') {
				path = path + "/";
			}

			return path + localFile.getName();
		}
	}

	/**
	 * 设定FTP根目录
	 * 
	 * @param dir
	 *            new working directory
	 * @return true, if working directory changed
	 * @throws IOException
	 */
	public boolean setWorkingDirectory(String dir) throws IOException {
		return ftp.changeWorkingDirectory(dir);
	}

	/**
	 * 获取文件输出流
	 * 
	 * @param ftpFileName
	 *            file name on ftp server
	 * @param out
	 *            OutputStream
	 * @throws IOException
	 */
	public void getFile(String ftpFileName, OutputStream out) throws IOException {
		try {
			// Use passive mode to pass firewalls.

			// Get file info.
			FTPFile[] fileInfoArray = ftp.listFiles(ftpFileName);
			if (fileInfoArray == null) {
				throw new FileNotFoundException("File '" + ftpFileName + "' was not found on FTP server.");
			}

			// Check file size.
			FTPFile fileInfo = fileInfoArray[0];
			long size = fileInfo.getSize();
			if (size > Integer.MAX_VALUE) {
				throw new IOException("File '" + ftpFileName + "' is too large.");
			}

			// Download file.
			if (!ftp.retrieveFile(ftpFileName, out)) {
				throw new IOException("Error loading file '" + ftpFileName
						+ "' from FTP server. Check FTP permissions and path.");
			}
			
			out.flush();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ex) {
					logger.error(ex, ex);
					throw ex;
				}
			}
		}
	}

	/**
	 * return stream
	 * 
	 * @param fileName
	 * @return
	 */
	public String readFile(String fileName) {
		String result = "";
		InputStream ins = null;
		BufferedReader reader = null;
		try {
			ins = ftp.retrieveFileStream(fileName);

			// byte []b = new byte[ins.available()];
			// ins.read(b);
			reader = new BufferedReader(new InputStreamReader(ins));
			String inLine = reader.readLine();
			while (inLine != null) {
				inLine = reader.readLine();
			}
			reader.close();
			if (ins != null) {
				ins.close();
			}
			// 主动调用一次getReply()把接下来的226消费掉. 这样做是可以解决这个返回null问题
			ftp.getReply();
		} catch (IOException e) {
			e.printStackTrace();

			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e1) {
					logger.error(e, e);
				}
			}
			if (null != ins) {
				try {
					ins.close();
				} catch (IOException e1) {
					logger.error(e, e);
				}
			}

		}
		return result;
	}
	

	
}