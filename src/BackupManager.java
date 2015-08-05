
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;

import com.google.gson.Gson;

public class BackupManager implements ActionListener {

	// function ADD = add new item based off path and name
	// function EDIT = edit existing item's input
	// function ADD_OUTPUT = add new output to existing item
	// function SET = set default path
	// function OPEN = open backups file
	// function SAVE = save backups file
	public enum fileChoice {
		ADD, EDIT, ADD_OUTPUT, SET, OPEN, SAVE;
	}

	public static Gson gson = new Gson();

	// config is the interpreted form of configFile
	public static File configFile;
	public static Configuration config;
	public ArrayList<Boolean> checkboxStates = new ArrayList<Boolean>();
	public static String fileSearchLocation;
	public static String defaultLocation;

	public static String OS;

	public static void main(final String[] args) {
		BackupManager.OS = System.getProperty("os.name").toUpperCase();
		//if it has been opened via a file
		if(args.length > 0) {
			configFile = takeInInput(args[0]);
			openConfig();
		} 
		//if it has been opened via the .exe
		else {
			config = new Configuration();
			setDefaultLocations();
		}
		new BackupManager();
	}
	
	public static File takeInInput(String inputString) {
		File input = new File(inputString);
		File inputLocal = new File(new File(System.getProperty("java.class.path")).getAbsoluteFile().getParentFile().toString()
				+ File.separator + inputString);
		System.out.println(inputLocal + "\n" + input);
		if(input.exists()) {
			return input;
		}
		if(inputLocal.exists()) {
			return inputLocal;
		} else {
			System.out.println(inputString + " does not exist");
			return null;
		}
	}

	public static void openConfig() {
		String text = "{}";
		try {
			text = new String(Files.readAllBytes(configFile.toPath()));
		} catch (final IOException e) {
			e.printStackTrace();
		}
		config = gson.fromJson(text, Configuration.class);
	}
	
	public static void saveConfig() {
		try {
			final File settingsFile = new File(configFile.getAbsolutePath());
			final File cParent = new File(configFile.getParent());
			if (!cParent.exists()) {
				Files.createDirectory(cParent.toPath());
			}
			if (!settingsFile.exists()) {
				Files.createFile(settingsFile.toPath());
			}
			final FileWriter file = new FileWriter(settingsFile);
			file.write(gson.toJson(config));
			file.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static void setDefaultLocations() {
		if (OS.contains("WIN")) {
			fileSearchLocation = "C:\\Users";
		} else {
			fileSearchLocation = System.getProperty("user.home");
		}
		config.defaultBackupLocation = fileSearchLocation 
				+ File.separator + "Backups" + File.separator;
	}

	public final String version = "1.0";

	JFrame window;
	Container Pane;
	JPanel backupItems;
	Boolean selectAll = false;

	public BackupManager() {

		// sets the window's appearance based on the OS used
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final ClassNotFoundException e) {
		} catch (final InstantiationException e) {
		} catch (final IllegalAccessException e) {
		} catch (final UnsupportedLookAndFeelException e) {
		}

		// Makes the window and gives it an icon
		window = new JFrame("Backup Manager " + version);
		window.setSize(700, 500);
		window.setLocationRelativeTo(null);
		Pane = window.getContentPane();
		Pane.setLayout(new GridBagLayout());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);

		osSpecificOperations();

		createGUI();

		// handles closing
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				System.exit(0);
				window.dispose();
			}
		});
		window.setVisible(true);
		if (configFile == null){
			createSettingsWindow();
		}
	}

	// add logic for buttons in here
	@Override
	public void actionPerformed(final ActionEvent e) {
		String function = e.getActionCommand();
		boolean isSelected = checkboxStates.size() > 0 && checkboxStates.contains(true);
		switch(function){
		//toolbar items
		case "backup": 
			if (isSelected) {
				for (int i = checkboxStates.size() - 1; i >= 0; i--) {
					if (checkboxStates.get(i)) {
						Backup.backupFile(BackupManager.config.backups.get(i));
					}
				}
			} else {
				createErrorWindow("Nothing to backup");
			}
			break;
		case "revert":
			if (isSelected) {
				for (int i = checkboxStates.size() - 1; i == 0; i--) {
					if (checkboxStates.get(i)) {
						Backup.restoreFile(config.backups.get(i), true);
					}
				}
			} else {
				createErrorWindow("Nothing to revert");
			}
			break;
		case "add" : createAddBackupNameWindow();
			break;
		case "remove" :
			if (isSelected) {
				for (int i = checkboxStates.size() - 1; i >= 0; i--) {
					if (checkboxStates.get(i)) {
						config.backups.remove(i);
						checkboxStates.remove(i);
					}
				}
				createGUI();
			} else {
				createErrorWindow("Nothing to delete");
			}
			break;
		//menu items
		case "fileOpen" : createFileChooserWindow(fileChoice.OPEN, null, 0);
		break;
		case "fileSave" : 
			if (BackupManager.configFile != null && BackupManager.configFile.exists()) {
				BackupManager.saveConfig();
			} else {
				createFileChooserWindow(fileChoice.SAVE, null, 0);
			}
			break;
		case "fileSaveAs" : createFileChooserWindow(fileChoice.SAVE, null, 0);
		break;
		case "fileSettings" : createSettingsWindow();
		break;
		}

	}

	// creates a window for naming the new backup
	public void createAddBackupNameWindow() {
		final JFrame nameWindow = new JFrame("Name backup");
		Container namePane = new Container();
		JButton setName, cancelName;
		final JTextPane nameInput = new JTextPane();

		// initialise window and disable main window
		nameWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		nameWindow.setSize(250, 70);
		nameWindow.setResizable(false);
		nameWindow.setLocationRelativeTo(null);
		nameWindow.setVisible(true);
		window.setEnabled(false);
		namePane = nameWindow.getContentPane();

		// layout manager
		namePane.setLayout(new GridBagLayout());
		final GridBagConstraints gridBag = new GridBagConstraints();

		// add components
		// Done button
		gridBag.fill = GridBagConstraints.HORIZONTAL;
		gridBag.gridx = 0;
		gridBag.gridy = 1;
		gridBag.weightx = 0.5;
		setName = new JButton("Done");
		namePane.add(setName, gridBag);

		// Cancel button
		gridBag.gridx = 1;
		gridBag.gridy = 1;
		gridBag.weightx = 0.5;
		cancelName = new JButton("Cancel");
		namePane.add(cancelName, gridBag);

		// Text Pane
		gridBag.gridwidth = 2;
		gridBag.gridx = 0;
		gridBag.gridy = 0;
		gridBag.weightx = 0;
		nameInput.setBorder(BorderFactory.createLineBorder(Color.black));
		namePane.add(nameInput, gridBag);

		// handles the button inputs
		setName.addActionListener(e -> {
			createFileChooserWindow(fileChoice.ADD, nameInput.getText(), 0);
			nameWindow.dispose();
		});
		cancelName.addActionListener(e -> {
			nameWindow.dispose();
			window.setEnabled(true);
			window.toFront();
		});

		// close this window and return focus back to the main
		nameWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				window.setEnabled(true);
			}
		});
	}

	// creates control panel
	public JToolBar createControlPanel() {
		final JToolBar controls = new JToolBar();
		final GridBagConstraints gridBag = new GridBagConstraints();
		final JMenuBar menuBar = new JMenuBar();
		final JMenu menuFile = new JMenu("File");
		final JMenuItem fileOpen = new JMenuItem("Open..."), fileSave = new JMenuItem("Save"),
				fileSaveAs = new JMenuItem("Save As..."), fileSettings = new JMenuItem("Settings");

		controls.setFloatable(false);
		controls.setLayout(new GridBagLayout());
		controls.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		// menu
		// open button
		gridBag.fill = GridBagConstraints.HORIZONTAL;
		gridBag.weightx = 1;
		gridBag.gridx = 0;
		gridBag.gridy = 0;
		gridBag.gridwidth = 4;
		fileOpen.setActionCommand("fileOpen");
		fileOpen.addActionListener(this);
		menuFile.add(fileOpen);
		// save button
		fileSave.setActionCommand("fileSave");
		fileSave.addActionListener(this);
		menuFile.add(fileSave);
		// save as button
		fileSaveAs.setActionCommand("fileSaveAs");
		fileSaveAs.addActionListener(this);
		menuFile.add(fileSaveAs);
		menuFile.addSeparator();
		// settings button
		fileSettings.setActionCommand("fileSettings");
		fileSettings.addActionListener(this);
		menuFile.add(fileSettings);
		menuBar.add(menuFile);
		if (BackupManager.OS.contains("MAC")) {
			window.setJMenuBar(menuBar);
		} else {
			controls.add(menuBar, gridBag);
		}

		// backup button
		final JButton backup = new JButton("backup");
		backup.setActionCommand("backup");
		backup.addActionListener(this);
		gridBag.weightx = 1;
		gridBag.gridx = 0;
		gridBag.gridy = 1;
		gridBag.gridwidth = 1;
		controls.add(backup, gridBag);

		// revert button
		final JButton revert = new JButton("revert");
		revert.setActionCommand("revert");
		revert.addActionListener(this);
		gridBag.gridx = 1;
		controls.add(revert, gridBag);

		// add button
		final JButton add = new JButton("add");
		add.setActionCommand("add");
		add.addActionListener(this);
		gridBag.gridx = 2;
		controls.add(add, gridBag);

		// remove button
		final JButton remove = new JButton("remove");
		remove.setActionCommand("remove");
		remove.addActionListener(this);
		gridBag.gridx = 3;
		controls.add(remove, gridBag);

		return controls;
	}

	// creates error pop-up with specified message
	public void createErrorWindow(final String error) {
		final JFrame errorWindow = new JFrame("Error");
		Container errorPane = new Container();
		final JButton errorButton = new JButton("Okay");
		final JLabel errorMessage = new JLabel(error);

		// initialise window and disable main window
		errorWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		errorWindow.setSize(250, 70);
		errorWindow.setResizable(false);
		errorWindow.setLocationRelativeTo(null);
		window.setEnabled(false);
		errorPane = errorWindow.getContentPane();
		errorPane.setLayout(new BoxLayout(errorPane, BoxLayout.PAGE_AXIS));

		// initialises buttons
		errorMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
		errorButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		errorButton.addActionListener(e -> {
			errorWindow.dispose();
			window.setEnabled(true);
			window.toFront();
		});

		// adds buttons to pane
		errorPane.add(errorMessage);
		errorPane.add(errorButton);

		errorWindow.setVisible(true);

		// close this window and return focus back to the main
		errorWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				window.setEnabled(true);
			}
		});
	}

	// creates a file chooser
	public void createFileChooserWindow(final fileChoice function, final String fileName, final int itemID) {
		final JFileChooser fileChooser = new JFileChooser();

		window.setEnabled(false);
		window.toFront();

		int choice = -1;
		fileChooser.setCurrentDirectory(new File(BackupManager.fileSearchLocation));

		String dialogTitle = "error";

		switch (function) {
		case ADD:
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			dialogTitle = "Add file or folder to backup";
			break;
		case EDIT:
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			dialogTitle = "Change file or folder to backup";
			break;
		case ADD_OUTPUT:
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			dialogTitle = "Add folder to backup to";
			break;
		case SET:
			dialogTitle = "Choose default directory";
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			break;
		case OPEN:
			FileFilter bkpmFile = new FileNameExtensionFilter("bkpm files", "bkpm");
			dialogTitle = "Open backup file";
			fileChooser.addChoosableFileFilter(bkpmFile);
			fileChooser.setFileFilter(bkpmFile);
			break;
		case SAVE:
			bkpmFile = new FileNameExtensionFilter("bkpm files", "bkpm");
			dialogTitle = "Save backup file";
			fileChooser.addChoosableFileFilter(bkpmFile);
			fileChooser.setFileFilter(bkpmFile);
			break;
		}
		fileChooser.setDialogTitle(dialogTitle);

		switch (function) {
		case ADD:
		case EDIT:
		case ADD_OUTPUT:
		case OPEN:
		case SET:
			choice = fileChooser.showOpenDialog(new JFrame("null"));
			break;
		case SAVE:
			choice = fileChooser.showSaveDialog(new JFrame("null"));
			break;
		}
		
		window.setEnabled(true);
		window.toFront();
		// handles the buttons on the file chooser
		if (choice == JFileChooser.APPROVE_OPTION) {
			createOrEditListItem(function, fileChooser.getSelectedFile(), fileName, itemID);
		} else if (choice == JFileChooser.CANCEL_OPTION) {
		} else if (choice == JFileChooser.ERROR_OPTION) {
			System.out.println("An error occured");
		}
	}

	// main draw method for refreshing the GUI
	public void createGUI() {
		final Component[] comps = Pane.getComponents();
		final GridBagConstraints gridBag = new GridBagConstraints();

		if (comps.length == 2) {
			Pane.remove(comps[1]);
			// backup list
			gridBag.fill = GridBagConstraints.BOTH;
			gridBag.weighty = 1;
			gridBag.gridy = 1;
			Pane.add(createItemList(0), gridBag);
		} else {

			// adds control panel and backup list to main window
			// control panel
			gridBag.fill = GridBagConstraints.HORIZONTAL;
			gridBag.weightx = 1;
			gridBag.gridx = 0;
			gridBag.gridy = 0;
			Pane.add(createControlPanel(), gridBag);

			// backup list
			gridBag.fill = GridBagConstraints.BOTH;
			gridBag.weighty = 1;
			gridBag.gridy = 1;
			Pane.add(createItemList(0), gridBag);

		}

		window.revalidate();
	}

	// creates and populates the list
	public Component createItemList(final int offset) {
		final Box listBox = Box.createVerticalBox(), boxBox = Box.createVerticalBox();
		int count = 0;

		boxBox.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(5, 5, 5, 5), new LineBorder(Color.BLACK)));
		boxBox.add(createListHeader());

		for (final BackupFile file : BackupManager.config.backups) {
			listBox.add(createListItem(file.name, file.fileLocation.toString(), count));
			count++;
		}

		final JScrollPane outputScroll = new JScrollPane(listBox);
		outputScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		boxBox.add(outputScroll);
		return boxBox;
	}

	// creates an item for the output list
	public void createItemOutputs(final int id) {
		final JFrame outputWindow = new JFrame("Add new outputs");
		Container outputPane = new Container();
		final JButton outputAdd = new JButton("Add");
		final Box listBox = Box.createVerticalBox();

		// initialise window and disable main window
		outputWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		outputWindow.setSize(500, 200);
		outputWindow.setResizable(false);
		outputWindow.setLocationRelativeTo(null);
		window.setEnabled(false);
		outputPane = outputWindow.getContentPane();
		outputPane.setLayout(new BoxLayout(outputPane, BoxLayout.PAGE_AXIS));

		outputAdd.addActionListener(e -> {
			createFileChooserWindow(fileChoice.ADD_OUTPUT, BackupManager.config.backups.get(id).name, id);
			outputWindow.dispose();

		});

		outputAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
		outputPane.add(outputAdd);

		for (int i = 0; i < BackupManager.config.backups.get(id).backupLocations.size(); i++) {
			final Box itemBox = Box.createHorizontalBox();
			final JLabel labelBox = new JLabel(
					BackupManager.config.backups.get(id).getBackupLocations().get(i).getPath());
			final JButton removeButton = new JButton("Remove");

			final int count = i;
			itemBox.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(2, 2, 0, 2),
					new BevelBorder(1, Color.black, Color.gray)));
			labelBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
			itemBox.add(labelBox);
			itemBox.add(Box.createHorizontalGlue());
			removeButton.addActionListener(e -> {
				BackupManager.config.backups.get(id).backupLocations.remove(count);
				outputWindow.dispose();
				window.toFront();
				createItemOutputs(id);
			});
			itemBox.add(removeButton);
			listBox.add(itemBox);
		}

		final JScrollPane listPane = new JScrollPane(listBox);
		listPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		outputPane.add(listPane);

		outputWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				window.setEnabled(true);
			}
		});
		outputWindow.setVisible(true);

	}

	// creates the header for the list
	public Component createListHeader() {
		final Box headerBox = Box.createHorizontalBox();
		final JLabel headerName = new JLabel("Backup name"), headerPath = new JLabel("Backup path");
		final JCheckBox headerCheckBox = new JCheckBox();

		// set border
		headerBox.setBorder(new LineBorder(Color.black));

		// initialises labels
		headerName.setPreferredSize(new Dimension(106, 0));
		headerName.setBorder(new EmptyBorder(5, 10, 5, 0));
		headerPath.setPreferredSize(new Dimension(529, 0));
		headerPath.setBorder(new EmptyBorder(5, 0, 5, 0));
		headerCheckBox.setSelected(selectAll);
		headerCheckBox.addItemListener(e -> {
			for (int i = checkboxStates.size() - 1; i >= 0; i--) {
				checkboxStates.set(i, headerCheckBox.isSelected());
				selectAll = headerCheckBox.isSelected();
				createGUI();
			}
		});

		// adds labels to box
		headerBox.add(headerName);
		headerBox.add(headerPath);

		headerBox.add(headerCheckBox);
		headerBox.add(Box.createHorizontalGlue());

		return headerBox;
	}

	// creates an item for the list
	public Component createListItem(final String _name, final String _fileLocation, final int count) {
		final JLabel name = new JLabel(_name);
		final Box createBox = Box.createHorizontalBox();
		final JCheckBox setDelete = new JCheckBox();
		final JButton chooseInput = new JButton("in"), chooseOutputs = new JButton("out");

		// sets up the name label
		name.setBorder(new EmptyBorder(5, 5, 5, 0));
		name.setPreferredSize(new Dimension(100, 0));

		setDelete.setSelected(checkboxStates.get(count));
		setDelete.addItemListener(e -> {
			if (setDelete.isSelected()) {
				checkboxStates.set(count, true);
			} else {
				checkboxStates.set(count, false);
			}
		});

		// adds to the box and makes sure everything has spaces
		createBox.add(name);
		createBox.add(new JLabel(_fileLocation));
		createBox.add(Box.createHorizontalGlue());
		chooseInput.addActionListener(e -> createFileChooserWindow(fileChoice.EDIT, _name, count));
		createBox.add(chooseInput);
		chooseOutputs.addActionListener(e -> createItemOutputs(count));
		createBox.add(chooseOutputs);
		createBox.add(setDelete);
		createBox.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(5, 5, 0, 5),
				new BevelBorder(1, Color.black, Color.gray)));

		return createBox;
	}

	// changes the list based on the function
	public void createOrEditListItem(final fileChoice function, final File file, String name, final int id) {
		switch (function) {
		case ADD:
			config.backups.add(new BackupFile(file, name));
			checkboxStates.add(false);
			break;
		case EDIT:
			config.backups.get(id).setFileLocation(file);
			break;
		case ADD_OUTPUT:
			config.backups.get(id).addBackupLocation(file);
			createItemOutputs(id);
			break;
		case SET:
			config.defaultBackupLocation = file.getAbsolutePath();
			createSettingsWindow();
			break;
		case OPEN:
			configFile = file.getAbsoluteFile();
			openConfig();
			break;
		case SAVE:
			configFile = file.getAbsoluteFile();
			name = configFile.getName();
			if (name.length() < 4 || !name.substring(name.length() - 5, name.length()).equals(".bkpm")) {
				configFile = new File(file.getParentFile() + File.separator + name + ".bkpm");
			}
			saveConfig();
			break;
		}

		createGUI();
	}

	// creates settings window
	public void createSettingsWindow() {
		final JFrame settingsWindow = new JFrame("Settings");
		Container settingsPane = new Container();
		final GridBagConstraints gridBag = new GridBagConstraints();
		final Box padding = Box.createHorizontalBox();

		// initialise window and disable main window
		settingsWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		settingsWindow.setSize(500, 100);
		settingsWindow.setResizable(false);
		settingsWindow.setLocationRelativeTo(null);
		window.setEnabled(false);
		settingsPane = settingsWindow.getContentPane();
		settingsPane.setLayout(new GridBagLayout());

		// adds default path selector
		// file path label

		padding.add(new JLabel("Default path :"));
		// file path textbox
		final JTextPane filePath = new JTextPane();
		filePath.setText(config.defaultBackupLocation);
		gridBag.weightx = 1;
		gridBag.gridx = 1;
		gridBag.gridwidth = 3;
		padding.add(filePath);
		// set path button
		final JButton addFilePath = new JButton("Browse"), addDone = new JButton("Done");
		addFilePath.addActionListener(e -> {
			settingsWindow.dispose();

			createFileChooserWindow(fileChoice.SET, null, 0);
			filePath.setText(config.defaultBackupLocation);

		});
		gridBag.weightx = 0;
		gridBag.gridx = 4;
		gridBag.gridwidth = 1;
		padding.add(addFilePath);

		// adds done button
		addDone.addActionListener(e -> {
			settingsWindow.dispose();
			window.setEnabled(true);
			window.toFront();
			final String path = filePath.getText();
			if (!path.substring(path.length() - 1, path.length()).equals(File.separator)) {
				config.defaultBackupLocation = path + File.separator;
			} else {
				config.defaultBackupLocation = path;
			}
		});
		gridBag.weightx = 0;
		gridBag.gridy = 1;
		padding.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		padding.add(addFilePath);
		gridBag.fill = GridBagConstraints.HORIZONTAL;
		gridBag.weightx = 1;
		gridBag.gridx = 0;
		gridBag.gridy = 0;
		gridBag.gridwidth = 4;
		settingsPane.add(padding, gridBag);
		gridBag.gridx = 3;
		gridBag.gridy = 1;
		gridBag.gridwidth = 1;
		settingsPane.add(addDone, gridBag);

		// close this window and return focus back to the main
		settingsWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				window.setEnabled(true);
			}
		});
		settingsWindow.setVisible(true);
	}

	public void osSpecificOperations() {
		if (BackupManager.OS.contains("MAC")) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Name");
		}
	}

}