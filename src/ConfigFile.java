import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

public class ConfigFile {
	private File file;
	private Configuration config;
	public String fileSearchLocation;
	
	public String getFileSearchLocation() {
		return fileSearchLocation;
	}
	public void setFileSearchLocation(String fileSearchLocation) {
		this.fileSearchLocation = fileSearchLocation;
	}
	public Configuration getConfig() {
		return config;
	}
	public void setConfig(Configuration config) {
		this.config = config;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public File getParent() {
		return file.getParentFile();
	}
	public String getAbsolutePathString() {
		return file.getAbsolutePath();
	}
	public Path getAbsolutePath() {
		return file.toPath();
	}
	public String getAbsoluteParentPathString() {
		return getParent().getAbsolutePath();
	}
	public Path getAbsoluteParentPath() {
		return getParent().toPath();
	}
	
	public String getDefaultBackupLocation() {
		return config.defaultBackupLocation;
	}
	public void setDefaultBackupLocation(String defaultBackupLocation) {
		config.defaultBackupLocation = defaultBackupLocation;
	}
	public void setGenericDefaultBackupLocation() {
		fileSearchLocation = System.getProperty("user.home");
		setDefaultBackupLocation(fileSearchLocation 
				+ File.separator + "Backups" + File.separator);
	}
	
	public void setBackups(ArrayList<BackupFile> backups) {
		config.backups = backups;
	}
	public ArrayList<BackupFile> getBackups() {
		return config.backups;
	}
	
	
}
