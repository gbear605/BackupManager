
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;

import com.google.gson.Gson;

public class BackupManager implements ActionListener {

	public final float version = 0.01f;
	public static Gson gson = new Gson();
	
	public static File configFile;
	public static Configuration config;
	
	
	JFrame window;
	Container Pane;
	JPanel backupItems;
	ArrayList<BackupFile> backups = new ArrayList<BackupFile>();
	private JLabel fileChooserOutput;


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
		
		
		//ADD SAVE LOADING METHOD CALL HERE		


		
		createGUI();

		//handles closing
		window.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println(e.getWindow());
				System.exit(0);
				window.dispose();
			}
		});
		window.setVisible (true);


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
		Box listBox = Box.createVerticalBox();
		int count = 0;

		listBox.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(5, 5, 5, 5), new LineBorder(Color.BLACK)));
		listBox.add(createListHeader());

		for(BackupFile file : backups)
		{
			
			listBox.add(createListItem(file.name, file.fileLocation.toString(), count));
			count++;
		}
		
		return listBox;
	}
	
	//creates the header for the list
	public Component createListHeader()
	{
		Box headerBox = Box.createHorizontalBox();
		JLabel headerName = new JLabel("Backup name"),
				headerPath = new JLabel("Backup path"),
				headerRemove = new JLabel("Remove");
		
		//set border
		headerBox.setBorder(new LineBorder(Color.black));

		//initialises labels
		headerRemove.setBorder(new EmptyBorder(5, 5, 5, 5));
		headerName.setPreferredSize(new Dimension(106, 0));
		headerName.setBorder(new EmptyBorder(5, 10, 5, 0));
		headerPath.setPreferredSize(new Dimension(150, 0));
		headerPath.setBorder(new EmptyBorder(5, 0, 5, 0));

		//adds labels to box
		headerBox.add(headerName);
		headerBox.add(headerPath);
		headerBox.add(Box.createHorizontalGlue());
		headerBox.add(headerRemove);
		
		return headerBox;
	}
	
	//creates an item for the list
	public Component createListItem(String _name, String _fileLocation, int count)
	{
		JLabel name = new JLabel(_name);
	    Box createBox = Box.createHorizontalBox();
	    JCheckBox setDelete = new JCheckBox();
	    JButton chooseBackup = new JButton("back");

	    //sets up the name label
	    name.setBorder(new EmptyBorder(5, 5, 5, 0));
	    name.setPreferredSize(new Dimension(100, 0));
	    
	    setDelete.addItemListener(new ItemListener() {
	        public void itemStateChanged(ItemEvent e) {
	        	if (setDelete.isSelected())
	        	{
	        		backups.get(count).setToDelete = true;
	        	} else {
		        	backups.get(count).setToDelete = false;
	        	}
	          }
	        });
	    
	    //adds to the box and makes sure everything has space
		createBox.add(name);
		createBox.add(new JLabel(_fileLocation));
	    createBox.add(Box.createHorizontalGlue());
	    chooseBackup.addItemListener(new ItemListener() {
	        public void itemStateChanged(ItemEvent e) {
	        	backups.get(count).backupLocations.add(createFileChooserWindow());
	          }
	        });
	    createBox.add(chooseBackup);
	    
	    createBox.add(new JButton("rev"));
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


		    	  
		    	  backups.add(new BackupFile(new File(createFileChooserWindow()), nameInput.getText()));
		    	  nameWindow.dispose();
		        }
		      });
		cancelName.addActionListener(new ActionListener() {
		      @Override
			public void actionPerformed(ActionEvent e) {
					window.setEnabled(true);
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
	
	//creates a filechooser
	public String createFileChooserWindow()
	{
		JFrame fileWindow = new JFrame("Find file to backup");
		JFileChooser fileChooser = new JFileChooser();
		String filePath;
		JLabel test = new JLabel();
		
		//initialise window and disable main window
		fileWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE );
		fileWindow.setSize(500, 300);
		fileWindow.setResizable(false);
		fileWindow.setLocationRelativeTo(null);
		fileWindow.setVisible(true);
		window.setEnabled(false);	
		window.toFront();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setApproveButtonText("Backup");
		
  		//add file chooser to the window
  		fileWindow.add(fileChooser);
		
		//handles the buttons on the file chooser

		fileChooser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand() == "ApproveSelection")
				{
					fileChooserOutput.setText(fileChooser.getSelectedFile().getAbsolutePath());
					createGUI();
					
				    window.setEnabled(true);
			 		fileWindow.dispose();
				}
				else if (e.getActionCommand() == "CancelSelection")
				{
					fileChooserOutput.setText(null);
	        	  	window.setEnabled(true);
	        	  	fileWindow.dispose();
				}
				
	        }
		});
		
		
		fileWindow.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				window.setEnabled(true);
			}
		});
		return test.getText();
	}
	
	//creates control panel
	public JToolBar createControlPanel() 
	{	
		//backup button
		JButton backup = new JButton("backup");		
	    backup.setActionCommand("backup");
		backup.addActionListener(this);
		
		//revert button
		JButton revert = new JButton("revert");		
	    revert.setActionCommand("revert");
		revert.addActionListener(this);
		
		//add button
		JButton add = new JButton("add");		
	    add.setActionCommand("add");
		add.addActionListener(this);
		
		//remove button
		JButton remove = new JButton("remove");		
		remove.setActionCommand("remove");
		remove.addActionListener(this);

		//puts buttons into a container
		JToolBar controls = new JToolBar();
		controls.setLayout(new GridLayout(1, 4));
		controls.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		controls.add(backup);
		controls.add(revert);
		controls.add(add);
		controls.add(remove);
		return controls;	
	}
	
	//add logic for buttons in here
    @Override
	public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "backup") {
        	System.out.println(e.getActionCommand());
        }
        else if (e.getActionCommand() == "revert") {
        	System.out.println(e.getActionCommand());
        }
        else if (e.getActionCommand() == "add") {
        	createAddBackupNameWindow();
        }
        else if (e.getActionCommand() == "remove") {
        	if (backups.size() > 0){
        		for (int i = backups.size() - 1; i == 0; i-- )
        		{
        			if (backups.get(i).setToDelete)
        			{
        				backups.remove(i);

        			}
        		}
				createGUI();
        	} else {
        		createErrorWindow("Nothing to delete");
        	}
        }
    }
    
    public static Configuration readConfig() {
    	String text = "";
    	try {
			text = new String(Files.readAllBytes(configFile.toPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return gson.fromJson(text,Configuration.class);
    }
    
    public static void setConfig() {
    	String json = gson.toJson(new Configuration()); 
    	try {
			FileWriter file = new FileWriter(configFile.getAbsolutePath());
			file.write(json);
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
	public static void main(String[] args) {

		String workingDirectory;
		String OS = (System.getProperty("os.name")).toUpperCase();
		if (OS.contains("WIN")) {
		    workingDirectory = System.getenv("AppData");
		} else {
		    workingDirectory = System.getProperty("user.home");
		    workingDirectory += "/Library/Application Support";
		}
		workingDirectory += "/BackupManager";

		configFile = new File(workingDirectory);
		//config = readConfig();
		
		new BackupManager();
	}

}