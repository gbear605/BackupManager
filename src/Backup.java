import java.io.*;
import java.nio.file.Files;

import static java.nio.file.StandardCopyOption.*;

public class Backup {

	public static void backupFiles(BackupFile[] filesToBackup)
	{
		for(BackupFile fileToBackup : filesToBackup) {
			backupFile(fileToBackup);
		}
	}

	public static void backupFile(BackupFile fileToBackup){
		for(File file : fileToBackup.getBackupLocations()) {
			copy(fileToBackup.getFileLocation(), file);
		}
	}
	
	public static void restoreFiles(BackupFile[] filesToRestore, boolean replace) {
		for(BackupFile fileToRestore : filesToRestore) {
			restoreFile(fileToRestore, replace);
		}
	}

	public static void restoreFile(BackupFile fileToRestore, boolean replace) {
		copy(fileToRestore.getBackupLocations().get(0),fileToRestore.getFileLocation());
	}
	
	private static void copy(File from, File to){	
		try {
			if(to.exists()) {
				delete(to);
			}
			if(!new File(to.getParent()).exists()) {
				new File(to.getParent()).mkdirs();
			}
			Files.copy(from.toPath(), to.toPath(), REPLACE_EXISTING, COPY_ATTRIBUTES);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(from.isDirectory()) {
			for(File inner : from.listFiles()) {
				copy(inner,new File(to.toString() + inner.toString().substring(from.toString().length(), inner.toString().length())));
				
			}
		}
	}
	
	private static void delete(File file) {
		if(file.isDirectory()) {
			for(File inner : file.listFiles()) {
				delete(inner);
			}
		}
		try {
			Files.delete(file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
