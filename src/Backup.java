import java.io.*;

public class Backup {
	
	public void backupFiles(BackupFile[] filesToBackup)
	{
		for(BackupFile fileToBackup : filesToBackup) {
			backupFile(fileToBackup);
		}
	}
	
	public void backupFile(BackupFile fileToBackup){
		//TODO: Actually implement backing up
	}
	
	public void restoreFiles(BackupFile[] filesToRestore) {
		for(BackupFile fileToRestore : filesToRestore) {
			restoreFile(fileToRestore);
		}
	}
	
	public void restoreFile(BackupFile fileToRestore) {
		//TODO: Actually implement restoration
	}

}
