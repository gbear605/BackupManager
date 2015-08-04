import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Backup {

	public static void backupFile(final BackupFile fileToBackup) {
		for (final File file : fileToBackup.getBackupLocations()) {
			Backup.copy(fileToBackup.getFileLocation(), file);
		}
	}

	public static void backupFiles(final BackupFile[] filesToBackup) {
		for (final BackupFile fileToBackup : filesToBackup) {
			Backup.backupFile(fileToBackup);
		}
	}

	private static void copy(final File from, final File to) {
		try {
			if (to.exists()) {
				Backup.delete(to);
			}
			if (!new File(to.getParent()).exists()) {
				new File(to.getParent()).mkdirs();
			}
			Files.copy(from.toPath(), to.toPath(), REPLACE_EXISTING, COPY_ATTRIBUTES);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (from.isDirectory()) {
			for (final File inner : from.listFiles()) {
				Backup.copy(inner, new File(to.toString()
						+ inner.toString().substring(from.toString().length(), inner.toString().length())));

			}
		}
	}

	private static void delete(final File file) {
		if (file.isDirectory()) {
			for (final File inner : file.listFiles()) {
				Backup.delete(inner);
			}
		}
		try {
			Files.delete(file.toPath());
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static void restoreFile(final BackupFile fileToRestore, final boolean replace) {
		Backup.copy(fileToRestore.getBackupLocations().get(0), fileToRestore.getFileLocation());
	}

	public static void restoreFiles(final BackupFile[] filesToRestore, final boolean replace) {
		for (final BackupFile fileToRestore : filesToRestore) {
			Backup.restoreFile(fileToRestore, replace);
		}
	}

}
