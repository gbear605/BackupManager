import java.io.*;
import java.util.ArrayList;

public class BackupFile {

	String name;
	Boolean setToDelete = false;
	String fileLocation;
	ArrayList<String> backupLocations = new ArrayList<String>(1);

	public BackupFile(String _fileLocation, String _name) {
		name = _name;
		fileLocation = _fileLocation;
		backupLocations.add(Configuration.defaultBackupLocation);
		
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
