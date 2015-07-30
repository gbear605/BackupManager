import java.io.*;

public class Backup {
	
	public static void backupFiles(BackupFile[] filesToBackup)
	{
		for(BackupFile fileToBackup : filesToBackup) {
			backupFile(fileToBackup);
		}
	}
	
	public static void backupFile(BackupFile fileToBackup){
		//TODO: Actually implement backing up
	}
	
	public static void restoreFiles(BackupFile[] filesToRestore) {
		for(BackupFile fileToRestore : filesToRestore) {
			restoreFile(fileToRestore);
		}
	}
	
	public static void restoreFile(BackupFile fileToRestore) {
		//TODO: Actually implement restoration
	}

}
