import java.io.*;
import java.util.ArrayList;

public class BackupFile {
//test
	String name;
	Boolean setToDelete = false;
	File fileLocation;
	ArrayList<File> backupLocations = new ArrayList<File>(1);

	public BackupFile(File _fileLocation, String _name) {
		name = _name;
		fileLocation = _fileLocation;
		backupLocations.add(new File(BackupManager.config.defaultBackupLocation));
		
		
	}

	public void setFileLocation(File _fileLocation) {
		fileLocation = _fileLocation;
	}

	public File getFileLocation() {
		return fileLocation;
	}

	public void addBackupLocation(File file) {
		backupLocations.add(new File(file.toString() + "/" + fileLocation.getName()));
	}

	public ArrayList<File> getBackupLocations() {
		return backupLocations;
	}	
}
