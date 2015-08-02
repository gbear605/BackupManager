import java.io.*;

public class BackupFile {

	String name;
	String fileLocation;
	String[] backupLocations;

	public BackupFile(String _fileLocation, String _name) {
		name = _name;
		fileLocation = _fileLocation;
		
	}

	public void setFileLocation(String _fileLocation) {
		fileLocation = _fileLocation;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setBackupLocation() {

	}

	public File getBackupLocation() {
		return null;
	}	
}
