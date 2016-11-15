package environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import common.Log;

public class FileUtil {
	private static String ZIP_SOURCE_FOLDER = "";
	private static List<String> listOfFileToZip;

	/**
	 * Find files with extension
	 * 
	 * @param dirName
	 *            - directory name
	 * @param extension
	 *            - file extension
	 * @return - list of files.
	 */
	public static File[] findFilesWithExtension(String dirName,
			final String extension) {
		File dir = new File(dirName);
		return dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.endsWith("." + extension);
			}
		});
	}

	/**
	 * Download file from FTP
	 * 
	 * @param server
	 *            - FTP server
	 * @param portNumber
	 *            - port
	 * @param userName
	 *            - FTP user name
	 * @param password
	 *            - FTP password
	 * @param remoteFile
	 *            - full path of file to download
	 * @param saveAs
	 *            - full path to store file locally.
	 */
	public static void downloadFileFromFTP(String server, String portNumber,
			String userName, String password, String remoteFile, String saveAs) {
		int port = Integer.parseInt(portNumber);
		FTPClient ftpClient = new FTPClient();
		try {
			Log.info("Logging to FTP: " + server);
			Log.info("FTP port: " + port);
			Log.info("FTP User name: " + userName);
			Log.info("FTP password: " + password);
			Log.info("Downloading file: " + remoteFile);
			Log.info("Saving locally as: " + saveAs);
			ftpClient.connect(server, port);
			ftpClient.login(userName, password);
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			// APPROACH #1: using retrieveFile(String, OutputStream)
			File downloadFile = new File(saveAs);
			OutputStream outputStream = new BufferedOutputStream(
					new FileOutputStream(downloadFile));
			boolean success = ftpClient.retrieveFile(remoteFile, outputStream);
			outputStream.close();

			if (success) {
				Log.info("File downloaded successfully.");
			} else {
				Log.info("Failed to download file.");
			}
			// APPROACH #2: using InputStream retrieveFileStream(String)
			// String remoteFile2 = "/test/song.mp3";
			// File downloadFile2 = new File("D:/Downloads/song.mp3");
			// OutputStream outputStream2 = new BufferedOutputStream(
			// new FileOutputStream(downloadFile2));
			// InputStream inputStream =
			// ftpClient.retrieveFileStream(remoteFile2);
			// byte[] bytesArray = new byte[4096];
			// int bytesRead = -1;
			// while ((bytesRead = inputStream.read(bytesArray)) != -1) {
			// outputStream2.write(bytesArray, 0, bytesRead);
			// }
			// success = ftpClient.completePendingCommand();
			// if (success) {
			// System.out.println("File #2 has been downloaded successfully.");
			// }
			// outputStream2.close();
			// inputStream.close();

		} catch (IOException exception) {
			Log.error("Error: " + exception.getMessage());
		} finally {
			try {
				if (ftpClient.isConnected()) {
					ftpClient.logout();
					ftpClient.disconnect();
				}
			} catch (IOException exception) {
				Log.error("Error: " + exception.getMessage());
			}
		}
	}

	/**
	 * Upload file to FTP
	 * 
	 * @param server
	 *            - server name to upload file
	 * @param portNumber
	 *            - ftp port number
	 * @param userName
	 *            - user name to connect to ftp
	 * @param password
	 *            - password
	 * @param fileToUpload
	 *            - file to upload
	 * @param uploadAs
	 *            - filename to upload as
	 */
	public static void uploadFileToFTP(String server, String portNumber,
			String userName, String password, String fileToUpload,
			String uploadAs) {
		int port = Integer.parseInt(portNumber);
		FTPClient ftpClient = new FTPClient();
		try {
			Log.info("Logging to FTP: " + server);
			Log.info("FTP port: " + port);
			Log.info("FTP User name: " + userName);
			Log.info("FTP password: " + password);
			Log.info("Uploading file: " + fileToUpload);
			
			ftpClient.connect(server, port);
			ftpClient.login(userName, password);
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			
			// APPROACH #1: using retrieveFile(String, OutputStream)
			File uploadFile = new File(fileToUpload);
			InputStream inputStream = new BufferedInputStream(
					new FileInputStream(uploadFile));
			boolean success = ftpClient.storeFile(uploadAs, inputStream);
			inputStream.close();
			if (success) {
				Log.info("File uploaded successfully.");
			} else {
				Log.info("Failed to upload file.");
			}
		} catch (IOException exception) {
			Log.error("Error: " + exception.getMessage());
		} finally {
			try {
				if (ftpClient.isConnected()) {
					ftpClient.logout();
					ftpClient.disconnect();
				}
			} catch (IOException exception) {
				Log.error("Error: " + exception.getMessage());
			}
		}
	}

	/**
	 * Delete files
	 * 
	 * @param files
	 *            - list of files
	 * @throws IOException
	 */
	public static void deleteFiles(File[] files) throws IOException {
		for (File file : files) {
			Log.info("Deleting file: " + file.getAbsolutePath());
			FileUtils.forceDelete(file);
		}
	}

	/**
	 * Replace file content in give file with new content
	 * 
	 * @param filePath
	 *            - file to replace content in.
	 * @param sourceString
	 *            - content to replace
	 * @param targetString
	 *            - new content at source string
	 * @return
	 * @throws IOException
	 */
	public static void replaceFileContent(String filePath, String sourceString,
			String targetString) throws IOException {

		Log.info("Replacing file content " + sourceString + " with "
				+ targetString + " in file " + filePath);
		File file = new File(filePath);
		// We are using the canWrite() method to check whether we can
		// modified file content.
		if (file.canWrite()) {
			Log.info("File is writable!");
		} else {
			Log.info("File is in read only mode!");
		}
		// Now make our file writable
		file.setWritable(true);
		if (file.canWrite()) {
			Log.info("Changed file to writable!");
		}
		String fileContents = FileUtils.readFileToString(file);
		String newFileContencts = fileContents.replace(sourceString,
				targetString);
		FileUtils.writeStringToFile(file, newFileContencts);
	}

	/**
	 * Append given content to file
	 * 
	 * @param filePath
	 *            - file to append content to.
	 * @param data
	 *            - content to append
	 * @throws IOException
	 */
	public static void appendFileContent(String filePath, String data)
			throws IOException {
		Log.info("Appending file content " + data + " to file " + filePath);
		File file = new File(filePath);
		// We are using the canWrite() method to check whether we can
		// modified file content.
		if (file.canWrite()) {
			Log.info("File is writable!");
		} else {
			Log.info("File is in read only mode!");
		}
		// Now make our file writable
		file.setWritable(true);
		if (file.canWrite()) {
			Log.info("Changed file to writable!");
		}
		FileUtils.writeStringToFile(file, data);
	}

	/**
	 * 
	 * @param filePath
	 *            : Path of the File whose data is to be appended
	 * @param data
	 *            : Data to be appended on the new line of the file
	 * @throws IOException
	 *             : throws I/O exceptions
	 */

	public static void appendFileContentOnNLIfNotExists(String filePath,
			String data) throws IOException {
		Log.info("Appending file content " + data + " to file " + filePath);
		File file = new File(filePath);
		// We are using the canWrite() method to check whether we can
		// modified file content.
		if (file.canWrite()) {
			Log.info("File is writable!");
		} else {
			Log.info("File is in read only mode!");
		}
		// Now make our file writable
		file.setWritable(true);
		if (file.canWrite()) {
			Log.info("Changed file to writable!");
		}
		String fileContents = FileUtils.readFileToString(file);
		Boolean contentExists = fileContents.contains(data);
		if (!contentExists) {
			FileUtils.writeStringToFile(file, "\n");
			FileUtils.writeStringToFile(file, data);

			Log.info("File is updated!");
		}

	}

	/**
	 * get file content in give file with new content
	 * 
	 * @param filePath
	 *            - file to replace content in.
	 * @return
	 * @throws IOException
	 */
	public static String getFileContent(String filePath) throws IOException {
		String fileContents = "";
		Log.info("getting file content from file " + filePath);
		File file = new File(filePath);
		// We are using the canWrite() method to check whether we can
		// modified file content.
		if (file.canRead()) {
			Log.info("File is Readable!");
		} else {
			Log.info("File is NOT in read only mode!");
			file.setReadable(true);
			Log.info("Changed file to Readable!");
		}
		fileContents = FileUtils.readFileToString(file);
		return fileContents;
	}
	
	/**
	 * 
	 * @param fileName
	 *              - Mention with Path or WithoutPath
	 * @param data
	 *              - Data which will be added to file
	 * @param format
	 *               - like UTF-8 , ..
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	
	
	public static void createFileFromContent(String fileName,String data,String format) 
			throws FileNotFoundException, UnsupportedEncodingException {
		
		PrintWriter writer = new PrintWriter(fileName, format);
		writer.println(data);
		writer.close();
		
	}

	
	
	
	/**
	 * get last modified file.
	 * 
	 * @param dirPath
	 *            - directory path.
	 * @return last modified file from the directory
	 */
	public static String getLastModifiedFilePath(String dirPath) {
		File fileName = new File(dirPath);
		File[] files = fileName.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isFile();
			}
		});
		long lastModified = Long.MIN_VALUE;
		File lastModifiedFile = null;
		for (File file : files) {
			if (file.lastModified() > lastModified) {
				lastModifiedFile = file;
				lastModified = file.lastModified();
			}
		}
		return lastModifiedFile.getAbsolutePath();
	}

	/**
	 * get last modified file.
	 * 
	 * @param dirPath
	 *            - directory path.
	 * @return last modified file from the directory
	 */
	public static File getLastModifiedFile(String dirPath) {
		File fileName = new File(dirPath);
		File[] files = fileName.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isFile();
			}
		});
		long lastModified = Long.MIN_VALUE;
		File lastModifiedFile = null;
		for (File file : files) {
			if (file.lastModified() > lastModified) {
				lastModifiedFile = file;
				lastModified = file.lastModified();
			}
		}
		return lastModifiedFile;
	}

	/**
	 * Unzip files
	 * 
	 * @param zipFile
	 * @param extractTo
	 * @throws IOException
	 */
	public static void unzipFile(String zipFile, String extractTo)
			throws IOException {

		byte[] buffer = new byte[1024 * 5];

		File folder = new File(extractTo);
		if (!folder.exists()) {
			folder.mkdir();
		}

		ZipInputStream ziStream = new ZipInputStream(new FileInputStream(
				zipFile));
		ZipEntry zEntity = ziStream.getNextEntry();

		while (zEntity != null) {
			if (zEntity.isDirectory()) {
				zEntity = ziStream.getNextEntry();
				continue;
			}

			String fileName = zEntity.getName();
			File newFile = new File(extractTo + File.separator + fileName);
			new File(newFile.getParent()).mkdirs();
			FileOutputStream foStream = new FileOutputStream(newFile);
			int len;
			while ((len = ziStream.read(buffer)) > 0) {
				foStream.write(buffer, 0, len);
			}

			foStream.close();
			zEntity = ziStream.getNextEntry();
		}
		ziStream.closeEntry();
		ziStream.close();
	}

	/**
	 * Gets files from the sourceFolder and makes a zip in the destination
	 * folder.
	 * 
	 * @param sourceFolder
	 * @param outputZipFolder
	 * 
	 */
	public static void zipFile(String sourceFolder, String outputZipFolder,
			String outputZipFileName) {
		boolean isFoldersValidated = false;
		File zipDestinationFolder = null;
		if (new File(sourceFolder).isDirectory()) {
			zipDestinationFolder = new File(outputZipFolder);
			if (!zipDestinationFolder.isDirectory()) {
				if (zipDestinationFolder.mkdirs()) {
					Log.info("Destination path created successfully.");
				}
			}
			isFoldersValidated = true;
		} else {
			isFoldersValidated = false;
		}

		if (isFoldersValidated) {
			Log.info("Provided paths have been located.");
			setZipSourceFolder(sourceFolder);
			listOfFileToZip = new ArrayList<String>();
			generateFileStructure(new File(sourceFolder));
			zipFiles(zipDestinationFolder.getAbsolutePath() + File.separator
					+ outputZipFileName);
			Log.info("Creating zipfile.");
		} else {
			Log.info("Provided paths could not be found.");
		}
	}

	/**
	 * Get the source folder path for current file in use.
	 */
	private static String getZipSourceFolder() {
		return ZIP_SOURCE_FOLDER;
	}

	/**
	 * Set the source folder path for current file in use.
	 */
	private static void setZipSourceFolder(String sourceFolder) {
		ZIP_SOURCE_FOLDER = sourceFolder;
	}

	/**
	 * Traverse a directory and get all files, and add the file into fileList
	 * 
	 * @param node
	 *            file or directory
	 */
	private static void generateFileStructure(File node) {

		// add file only
		if (node.isFile()) {
			listOfFileToZip.add(getRelativeFilepath(node.getAbsoluteFile()
					.toString()));
		}
		if (node.isDirectory()) {
			String[] subNote = node.list();
			for (String filename : subNote) {
				generateFileStructure(new File(node, filename));
			}
		}
	}

	/**
	 * Format the file path for zip
	 * 
	 * @param file
	 *            file path
	 * @return Formatted file path
	 */
	private static String getRelativeFilepath(String file) {
		return file.substring(getZipSourceFolder().length() + 1, file.length());
	}

	/**
	 * Zip the list of files from the <code>sourceFolder</code>
	 * 
	 * @param zipFileFullPath
	 *            output ZIP file location
	 */
	private static void zipFiles(String zipFileFullPath) {

		byte[] buffer = new byte[1024];

		try {

			FileOutputStream fos = new FileOutputStream(zipFileFullPath);
			ZipOutputStream zos = new ZipOutputStream(fos);

			System.out.println("Output to Zip : " + zipFileFullPath);

			for (String file : FileUtil.listOfFileToZip) {

				System.out.println("File Added : " + file);
				ZipEntry ze = new ZipEntry(file);
				zos.putNextEntry(ze);

				FileInputStream in = new FileInputStream(ZIP_SOURCE_FOLDER
						+ File.separator + file);

				int len;
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}

				in.close();
			}

			zos.closeEntry();
			zos.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	
}
