import java.io.File;
import java.util.ArrayList;

public class BackupFile {
	// test
	String name;
	File fileLocation;
	ArrayList<File> backupLocations = new ArrayList<File>(1);

	public BackupFile(final File _fileLocation, final String _name) {
		name = _name;
		fileLocation = _fileLocation;
		backupLocations.add(new File(BackupManager.config.defaultBackupLocation + name 
				+ File.separator + _fileLocation.getName()));

	}

	public void addBackupLocation(final File file) {
		backupLocations.add(new File(file.toString() + "/" + fileLocation.getName()));
	}

	public ArrayList<File> getBackupLocations() {
		return backupLocations;
	}

	public File getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(final File _fileLocation) {
		fileLocation = _fileLocation;
	}
}
