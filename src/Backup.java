import java.io.*;
import java.nio.file.Files;

import static java.nio.file.StandardCopyOption.*;

public class Backup {

	public static void backupFiles(BackupFile[] filesToBackup, boolean replace)
	{
		for(BackupFile fileToBackup : filesToBackup) {
			backupFile(fileToBackup, replace);
		}
	}

	public static void backupFile(BackupFile fileToBackup, boolean replace){
		try {
			if(replace) {
				Files.copy(fileToBackup.getFileLocation().toPath(), fileToBackup.getBackupLocation().toPath(), REPLACE_EXISTING, COPY_ATTRIBUTES);
			}
			else {
				Files.copy(fileToBackup.getFileLocation().toPath(), fileToBackup.getBackupLocation().toPath(), COPY_ATTRIBUTES);
			}		} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public static void restoreFiles(BackupFile[] filesToRestore, boolean replace) {
		for(BackupFile fileToRestore : filesToRestore) {
			restoreFile(fileToRestore, replace);
		}
	}

	public static void restoreFile(BackupFile fileToRestore, boolean replace) {
		try {
			if(replace) {
				Files.copy(fileToRestore.getBackupLocation().toPath(), fileToRestore.getFileLocation().toPath(), REPLACE_EXISTING, COPY_ATTRIBUTES);
			}
			else {
				Files.copy(fileToRestore.getBackupLocation().toPath(), fileToRestore.getFileLocation().toPath(), COPY_ATTRIBUTES);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
