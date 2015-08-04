
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.gson.Gson;

public class BackupManager implements ActionListener {

	public final String version = "0.1";
	public static Gson gson = new Gson();
	
	// config is the interpreted form of configFile
	public static File configFile;
	public static Configuration config; 
	
	public static String fileSearchLocation;
	public static String defaultLocation; 
	
	public static String OS;
	
	JFrame window;
	Container Pane;
	JPanel backupItems;
	Boolean selectAll = false;

	public BackupManager() {
		
		//sets the window's appearance based on the OS used
		try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
		catch (ClassNotFoundException e) {}
		catch (InstantiationException e) {}
		catch (IllegalAccessException e) {}
		catch (UnsupportedLookAndFeelException e) {}
		
		//Makes the window and gives it an icon
		window = new JFrame("Backup Manager " + version);
		window.setSize(700, 500);
		window.setLocationRelativeTo(null);
		Pane = window.getContentPane();
		Pane.setLayout(new GridBagLayout());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
		window.setResizable(false);
		
		osSpecificOperations();

		setDefaultLocations();
		readConfig();
		
		createGUI();

		//handles closing
		window.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
				window.dispose();
			}
		});
		window.setVisible (true);


	}
	
	public void osSpecificOperations() {
	    if(OS.contains("MAC")) {
	    	System.setProperty("apple.laf.useScreenMenuBar", "true");
	        System.setProperty(
	            "com.apple.mrj.application.apple.menu.about.name", "Name");
	    }
	}
	
	//main draw method for refreshing the GUI
	public void createGUI()
	{
		Component[] comps = Pane.getComponents();
		GridBagConstraints gridBag = new GridBagConstraints();	
		
		if (comps.length == 2)
		{
			Pane.remove(comps[1]);
			//backup list
			gridBag.fill = GridBagConstraints.BOTH;
			gridBag.weighty = 1;
			gridBag.gridy = 1;
			Pane.add(createItemList(0), gridBag);
		} else {
			
			//adds control panel and backup list to main window
			//control panel
			gridBag.fill = GridBagConstraints.HORIZONTAL;
			gridBag.weightx = 1;
			gridBag.gridx = 0;
			gridBag.gridy = 0;
			Pane.add(createControlPanel(), gridBag);
			
			//backup list
			gridBag.fill = GridBagConstraints.BOTH;
			gridBag.weighty = 1;
			gridBag.gridy = 1;
			Pane.add(createItemList(0), gridBag);

		}

		window.revalidate();
	}
	
	//creates and populates the list
	public Component createItemList(int offset)
	{
		Box listBox = Box.createVerticalBox(), boxBox = Box.createVerticalBox();
		int count = 0;

		boxBox.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(5, 5, 5, 5), new LineBorder(Color.BLACK)));
		boxBox.add(createListHeader());

		for(BackupFile file : config.backups)
		{
			listBox.add(createListItem(file.name, file.fileLocation.toString(), count));
			count++;
		}
		
		JScrollPane outputScroll = new JScrollPane(listBox);
		outputScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		boxBox.add(outputScroll);
		return boxBox;
	}
	
	//creates the header for the list
	public Component createListHeader()
	{
		Box headerBox = Box.createHorizontalBox();
		JLabel headerName = new JLabel("Backup name"),
				headerPath = new JLabel("Backup path");
		JCheckBox headerCheckBox = new JCheckBox();
		
		//set border
		headerBox.setBorder(new LineBorder(Color.black));

		//initialises labels
		headerName.setPreferredSize(new Dimension(106, 0));
		headerName.setBorder(new EmptyBorder(5, 10, 5, 0));
		headerPath.setPreferredSize(new Dimension(529, 0));
		headerPath.setBorder(new EmptyBorder(5, 0, 5, 0));
		headerCheckBox.setSelected(selectAll);
		headerCheckBox.addItemListener(new ItemListener() {
	        public void itemStateChanged(ItemEvent e) {
	        	for(int i = config.checkboxStates.size() - 1; i >= 0; i-- ){
	        		config.checkboxStates.set(i, headerCheckBox.isSelected());
		        	selectAll = headerCheckBox.isSelected();
		        	createGUI();
	        	}
	          }
	        });
		
		//adds labels to box
		headerBox.add(headerName);
		headerBox.add(headerPath);

		headerBox.add(headerCheckBox);
		headerBox.add(Box.createHorizontalGlue());
		
		return headerBox;
	}
	
	//creates an item for the list
	public Component createListItem(String _name, String _fileLocation, int count)
	{
		JLabel name = new JLabel(_name);
	    Box createBox = Box.createHorizontalBox();
	    JCheckBox setDelete = new JCheckBox();
	    JButton chooseInput = new JButton("in"), chooseOutputs = new JButton("out");

	    //sets up the name label
	    name.setBorder(new EmptyBorder(5, 5, 5, 0));
	    name.setPreferredSize(new Dimension(100, 0));
	    
	    setDelete.setSelected(config.checkboxStates.get(count));
	    setDelete.addItemListener(new ItemListener() {
	        public void itemStateChanged(ItemEvent e) {
	        	if (setDelete.isSelected())
	        	{
	        		config.checkboxStates.set(count, true);
	        	} else {
	        		config.checkboxStates.set(count, false);
	        	}
	          }
	        });
	    
	    //adds to the box and makes sure everything has spaces
		createBox.add(name);
		createBox.add(new JLabel(_fileLocation));
	    createBox.add(Box.createHorizontalGlue());
	    chooseInput.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	createFileChooserWindow(2, _name, count);
	          }
	        });
	    createBox.add(chooseInput);
	    chooseOutputs.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	createItemOutputs(count);
	        	//createFileChooserWindow(3, _name, count);
	          }
	        });
	    createBox.add(chooseOutputs);
	    createBox.add(setDelete);
	    createBox.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(5, 5, 0, 5), new BevelBorder(1, Color.black, Color.gray)));
	    

		return createBox;
	}
	
	//creates error pop-up with specified message
	public void createErrorWindow(String error)
	{
		JFrame errorWindow = new JFrame("Error");
		Container errorPane = new Container();
		JButton errorButton = new JButton("Okay");
		JLabel errorMessage = new JLabel(error);
		
		//initialise window and disable main window
		errorWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE );
		errorWindow.setSize(250, 70);
		errorWindow.setResizable(false);
		errorWindow.setLocationRelativeTo(null);
		window.setEnabled(false);		
		errorPane = errorWindow.getContentPane();
		errorPane.setLayout(new BoxLayout(errorPane, BoxLayout.PAGE_AXIS));
		
		//initialises buttons
		errorMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
		errorButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		errorButton.addActionListener(new ActionListener() {
		      @Override
			public void actionPerformed(ActionEvent e) {
		    	  	errorWindow.dispose();
		  			window.setEnabled(true);
		  			window.toFront();
		        }
		      });
		
		//adds buttons to pane
		errorPane.add(errorMessage);
		errorPane.add(errorButton);

		errorWindow.setVisible(true);
		
		//close this window and return focus back to the main
		errorWindow.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				window.setEnabled(true);
			}
		});
	}
	
	//creates an item for the output list
	public void createItemOutputs(int id)
	{
		JFrame outputWindow = new JFrame("Add new outputs");
		Container outputPane = new Container();
		JButton outputAdd = new JButton("Add");
		Box listBox = Box.createVerticalBox();


		//initialise window and disable main window
		outputWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE );
		outputWindow.setSize(500, 200);
		outputWindow.setResizable(false);
		outputWindow.setLocationRelativeTo(null);
		window.setEnabled(false);			
		outputPane = outputWindow.getContentPane();
		outputPane.setLayout(new BoxLayout(outputPane, BoxLayout.PAGE_AXIS));	
		
		outputAdd.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	createFileChooserWindow(3, config.backups.get(id).name, id);
	        	outputWindow.dispose();
	        		        	
	          }
	        });
		

		outputAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
		outputPane.add(outputAdd);

		
		for (int i = 0; i < config.backups.get(id).backupLocations.size(); i++)
		{
			Box itemBox = Box.createHorizontalBox();
			JLabel labelBox = new JLabel(config.backups.get(id).getBackupLocations().get(i).getPath());
			JButton removeButton = new JButton("Remove");
			
			int count = i;
			itemBox.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(2, 2, 0, 2), new BevelBorder(1, Color.black, Color.gray)));
			labelBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
			itemBox.add(labelBox);
			itemBox.add(Box.createHorizontalGlue());
			removeButton.addActionListener(new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	config.backups.get(id).backupLocations.remove(count);
		        	outputWindow.dispose();
		        	window.toFront();
		        	createItemOutputs(id);		        	
		          }
		        });
			itemBox.add(removeButton);
			listBox.add(itemBox);
		}
		
		JScrollPane listPane = new JScrollPane(listBox);
		listPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		outputPane.add(listPane);

		
		outputWindow.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				window.setEnabled(true);
			}
		});
		outputWindow.setVisible(true);
		
	}
		
	//creates a window for naming the new backup
	public void createAddBackupNameWindow()
	{
		JFrame nameWindow = new JFrame("Name backup");
		Container namePane = new Container();
		JButton setName, cancelName;
		JTextPane nameInput = new JTextPane();
		
		//initialise window and disable main window
		nameWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE );
		nameWindow.setSize(250, 70);
		nameWindow.setResizable(false);
		nameWindow.setLocationRelativeTo(null);
		nameWindow.setVisible(true);
		window.setEnabled(false);		
		namePane = nameWindow.getContentPane();
		
		//layout manager
		namePane.setLayout(new GridBagLayout());
		GridBagConstraints gridBag = new GridBagConstraints();
		
		//add components
		//Done button
		gridBag.fill = GridBagConstraints.HORIZONTAL;
		gridBag.gridx = 0;
		gridBag.gridy = 1;
		gridBag.weightx = 0.5;
		setName = new JButton("Done");
		namePane.add(setName, gridBag);

		//Cancel button
		gridBag.gridx = 1;
		gridBag.gridy = 1;
		gridBag.weightx = 0.5;
		cancelName = new JButton("Cancel");
		namePane.add(cancelName, gridBag);
		
		//Text Pane
		gridBag.gridwidth = 2;
		gridBag.gridx = 0;
		gridBag.gridy = 0;
		gridBag.weightx = 0;
		nameInput.setBorder(BorderFactory.createLineBorder(Color.black));
		namePane.add(nameInput, gridBag);
		
		//handles the button inputs
		setName.addActionListener(new ActionListener() {
		      @Override
			public void actionPerformed(ActionEvent e) {
		    	  createFileChooserWindow(1, nameInput.getText(), 0);
		    	  nameWindow.dispose();
		        }
		      });
		cancelName.addActionListener(new ActionListener() {
		      @Override
			public void actionPerformed(ActionEvent e) {
		    	  nameWindow.dispose();
		    	  window.setEnabled(true);
		    	  window.toFront();
		        }
		      });

		//close this window and return focus back to the main
		nameWindow.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				window.setEnabled(true);
			}
		});
	}
	
	//creates a file chooser
	public void createFileChooserWindow(int function, String fileName, int itemID)
	{
		JFrame fileWindow = new JFrame("null");
		JFileChooser fileChooser = new JFileChooser();
		
		window.setEnabled(false);	
		window.toFront();
		
		int choice = -1;
		fileChooser.setCurrentDirectory(new File(fileSearchLocation));

		if (function == 4){
			fileChooser.setDialogTitle("Choose default directory");
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			choice = fileChooser.showSaveDialog(fileWindow);
		} else if (function == 5){
				FileFilter bkpmFile = new FileNameExtensionFilter(
					    "bkpm files", "bkpm");
				fileChooser.setDialogTitle("Open backup file");
				fileChooser.addChoosableFileFilter(bkpmFile);
				fileChooser.setFileFilter(bkpmFile);
				choice = fileChooser.showOpenDialog(fileWindow);
		} else{
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			if (function == 1){
				fileChooser.setDialogTitle("Add new file or folder to backup");
				choice = fileChooser.showOpenDialog(fileWindow);
			} else if (function == 2){
				fileChooser.setDialogTitle("Change file or folder to backup");
				choice = fileChooser.showOpenDialog(fileWindow);
			} else if (function == 3){
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setDialogTitle("Add folder to backup to");
				choice = fileChooser.showOpenDialog(fileWindow);
			} else if (function == 6){
				FileFilter bkpmFile = new FileNameExtensionFilter(
					    "bkpm files", "bkpm");
				fileChooser.setDialogTitle("Save backup file");
				fileChooser.addChoosableFileFilter(bkpmFile);
				fileChooser.setFileFilter(bkpmFile);
				fileChooser.setApproveButtonText("Backup");
				choice = fileChooser.showSaveDialog(fileWindow);
			}
		}
	    
		window.setEnabled(true);
		window.toFront();

		//handles the buttons on the file chooser
		if(choice == JFileChooser.APPROVE_OPTION) {
			createOrEditListItem(function, fileChooser.getSelectedFile(), fileName, itemID);
		} else if (choice == JFileChooser.CANCEL_OPTION) {
		} else if(choice == JFileChooser.ERROR_OPTION) {
			System.out.println("An error occured");
		}
	}
	
	//changes the list based on the function
	public void createOrEditListItem(int function, File file, String name, int id)
	{
		//function 1 = add new item based off path and name
		//function 2 = edit existing item's input
		//function 3 = add new output to existing item
		//function 4 = set default path
		//function 5 = open backups file
		//function 6 = save backups file
		
		if (function == 1){
			config.backups.add(new BackupFile(file, name));
			config.checkboxStates.add(false);
		} else if (function == 2){
			config.backups.get(id).setFileLocation(file);
		} else if (function == 3){
			config.backups.get(id).addBackupLocation(file);
	    	createItemOutputs(id);
		} else if (function == 4){
			config.defaultBackupLocation = file.getAbsolutePath();
        	createSettingsWindow();
		} else if (function == 5){
			configFile = file.getAbsoluteFile();
			readConfig();
		} else if (function == 6){
			setConfig();
		}
		
		
		createGUI();
	}
	
	//creates settings window
	public void createSettingsWindow()
	{
		JFrame settingsWindow = new JFrame("Settings");
		Container settingsPane = new Container();
		GridBagConstraints gridBag = new GridBagConstraints();	
		Box padding = Box.createHorizontalBox();
		
		//initialise window and disable main window
		settingsWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE );
		settingsWindow.setSize(500, 100);
		settingsWindow.setResizable(false);
		settingsWindow.setLocationRelativeTo(null);
		window.setEnabled(false);		
		settingsPane = settingsWindow.getContentPane();
		settingsPane.setLayout(new GridBagLayout());
		
		//adds default path selector
		//file path label

		padding.add(new JLabel("Default path :"));
		//file path textbox
		JTextPane filePath = new JTextPane();
		filePath.setText(config.defaultBackupLocation);
		gridBag.weightx = 1;
		gridBag.gridx = 1;
		gridBag.gridwidth = 3;
		padding.add(filePath);
		//set path button
		JButton addFilePath = new JButton("Browse"), addDone = new JButton("Done");
		addFilePath.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	settingsWindow.dispose();
	        	
	        	createFileChooserWindow(4, null, 0);
	        	filePath.setText(config.defaultBackupLocation);

	        		        	
	          }
	        });
		gridBag.weightx = 0;
		gridBag.gridx = 4;
		gridBag.gridwidth = 1;
		padding.add(addFilePath);
		
		//adds done button
		addDone.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	settingsWindow.dispose();
				window.setEnabled(true);
				window.toFront();
				String path = filePath.getText();
				if(!path.substring(path.length()-1, path.length()).equals(File.separator)) {
					config.defaultBackupLocation = path + File.separator;
				}
				else { 
					config.defaultBackupLocation = path;
				}
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
		
		
		//close this window and return focus back to the main
		settingsWindow.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				window.setEnabled(true);
			}
		});
		settingsWindow.setVisible(true);
	}
	
	//creates control panel
	public JToolBar createControlPanel() 
	{	
		JToolBar controls = new JToolBar();
		GridBagConstraints gridBag = new GridBagConstraints();	
		JMenuBar menuBar = new JMenuBar();
		JMenu menuFile = new JMenu("File");
		JMenuItem fileOpen = new JMenuItem("Open..."),
				fileSave = new JMenuItem("Save"),
				fileSaveAs = new JMenuItem("Save As..."),
				fileSettings = new JMenuItem("Settings");

		controls.setFloatable(false);
		controls.setLayout(new GridBagLayout());
		controls.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		//menu
		//open button
		gridBag.fill = GridBagConstraints.HORIZONTAL;
		gridBag.weightx = 1;
		gridBag.gridx = 0;
		gridBag.gridy = 0;
		gridBag.gridwidth = 4;
		fileOpen.setActionCommand("fileOpen");
		fileOpen.addActionListener(this);
		menuFile.add(fileOpen);
		//save button
		fileSave.setActionCommand("fileSave");
		fileSave.addActionListener(this);
		menuFile.add(fileSave);
		//save as button
		fileSaveAs.setActionCommand("fileSaveAs");
		fileSaveAs.addActionListener(this);
		menuFile.add(fileSaveAs);
		menuFile.addSeparator();
		//settings button
		fileSettings.setActionCommand("fileSettings");
		fileSettings.addActionListener(this);
		menuFile.add(fileSettings);
		menuBar.add(menuFile);
		if(OS.contains("MAC")) {
			window.setJMenuBar(menuBar);
		} else {
			controls.add(menuBar, gridBag);
		}
		
		//backup button
		JButton backup = new JButton("backup");		
	    backup.setActionCommand("backup");
		backup.addActionListener(this);
		gridBag.weightx = 1;
		gridBag.gridx = 0;
		gridBag.gridy = 1;
		gridBag.gridwidth = 1;
		controls.add(backup, gridBag);
		
		//revert button
		JButton revert = new JButton("revert");		
	    revert.setActionCommand("revert");
		revert.addActionListener(this);
		gridBag.gridx = 1;
		controls.add(revert, gridBag);
		
		//add button
		JButton add = new JButton("add");		
	    add.setActionCommand("add");
		add.addActionListener(this);
		gridBag.gridx = 2;
		controls.add(add, gridBag);
		
		//remove button
		JButton remove = new JButton("remove");		
		remove.setActionCommand("remove");
		remove.addActionListener(this);
		gridBag.gridx = 3;
		controls.add(remove, gridBag);	

		return controls;	
	}
	
	
	//add logic for buttons in here
    @Override
	public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "backup") {
        	if (config.checkboxStates.size() > 0 && config.checkboxStates.contains(true)){
        		for (int i = config.checkboxStates.size() - 1; i >= 0; i-- )
        		{
        			if (config.checkboxStates.get(i))
        			{
        				Backup.backupFile(config.backups.get(i));
        			}
        		}
        	} else {
        		createErrorWindow("Nothing to backup");
        	}
        }
        else if (e.getActionCommand() == "revert") {
        	if (config.checkboxStates.size() > 0 && config.checkboxStates.contains(true)){
        		for (int i = config.checkboxStates.size() - 1; i == 0; i-- )
        		{
        			if (config.checkboxStates.get(i))
        			{
        				Backup.restoreFile(config.backups.get(i), true);
        			}
        		}
        	} else {
        		createErrorWindow("Nothing to revert");
        	}
        }
        else if (e.getActionCommand() == "add") {
        	createAddBackupNameWindow();
        }
        else if (e.getActionCommand() == "remove") {
        	if (config.checkboxStates.size() > 0 && config.checkboxStates.contains(true)){
        		for (int i = config.checkboxStates.size() - 1; i >= 0; i-- )
        		{
        			if (config.checkboxStates.get(i))
        			{
        				config.backups.remove(i);
        				config.checkboxStates.remove(i);
        			}
        		}
				createGUI();
        	} else {
        		createErrorWindow("Nothing to delete");
        	}
        }
        
        //menu items
        if (e.getActionCommand() == "fileOpen") {
        	createFileChooserWindow(5, null, 0);
        } else if (e.getActionCommand() == "fileSave") {
        	setConfig();
        } else if (e.getActionCommand() == "fileSaveAs") {
        	createFileChooserWindow(6, null, 0);
        	System.out.println(e.getActionCommand());
        } else if (e.getActionCommand() == "fileSettings") {
        	createSettingsWindow();
        }
        
    }
    
    public static void readConfig() {
    	String text = "{}";
    	if(configFile.exists()) {
    		
	    	try {
				text = new String(Files.readAllBytes(configFile.toPath()));
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	config = gson.fromJson(text,Configuration.class);
    	} else {
    		//TODO: create a panel to ask the user for the default backup location
    		String defaultBackupLocation = fileSearchLocation + File.separator + "Backups";

    		config = new Configuration();
    		config.defaultBackupLocation = defaultBackupLocation;	
    		setConfig();

    	}
    }
    
    public static void setConfig() {
    	try {
    		File settingsFile = new File(configFile.getAbsolutePath());
    		File cParent = new File(configFile.getParent());
    		if(!cParent.exists()) {
    			Files.createDirectory(cParent.toPath());
    		}
    		if(!settingsFile.exists()) {
    			Files.createFile(settingsFile.toPath());
    		}
			FileWriter file = new FileWriter(settingsFile);
			file.write(gson.toJson(config));
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void setDefaultLocations() {
		String settingsLocation;
		if (OS.contains("WIN")) {
		    settingsLocation = System.getenv("AppData");
		    fileSearchLocation = "C:\\Users";
		} else {
		    settingsLocation = System.getProperty("user.home");
		    fileSearchLocation = settingsLocation;
		    if(OS.contains("MAC OS X")) {
			    settingsLocation += "/Library/Application Support";
		    }
		}
		configFile = new File(settingsLocation + File.separator 
				             + "BackupManager" + File.separator 
				             + "settings.bkpm");
    }
    
	public static void main(String[] args) {
		OS = (System.getProperty("os.name")).toUpperCase();

		new BackupManager();
	}

}