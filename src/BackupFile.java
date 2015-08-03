import java.io.*;
import java.util.ArrayList;

public class BackupFile {

	String name;
	Boolean setToDelete = false;
	File fileLocation;
	ArrayList<String> backupLocations = new ArrayList<String>(1);

	public BackupFile(File _fileLocation, String _name) {
		name = _name;
		fileLocation = _fileLocation;
		backupLocations.add(Configuration.defaultBackupLocation);
		
	}

	public void setFileLocation(File _fileLocation) {
		fileLocation = _fileLocation;
	}

	public File getFileLocation() {
		return fileLocation;
	}

	public void setBackupLocation() {

	}

	public File getBackupLocation() {
		return null;
	}	
}
