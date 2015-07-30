import java.io.*;

public class BackupFile {

	String name;
	File fileLocation;
	File backupLocation;
	String[] backupLocations;

	public BackupFile(File file, File backupLocation) {
		this.fileLocation = file;
		this.backupLocation = backupLocation;
	}

	public void setFileLocation(File file) {
		this.fileLocation = file;
	}

	public File getFileLocation() {
		return this.fileLocation;
	}

	public void setBackupLocation(File backupLocation) {
		this.backupLocation = backupLocation;
	}

	public File getBackupLocation() {
		return this.backupLocation;
	}	
}
