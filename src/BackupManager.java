
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;

import com.google.gson.Gson;

public class BackupManager implements ActionListener {

	public final float version = 0.01f;
	public static Gson gson = new Gson();
	
	public static File configFile;
	public static Configuration config;
	
	JFrame window;
	Container Pane;
	Insets insets;
	JPanel backupItems;
	BackupFile[] backups = new BackupFile[50];

	public BackupManager() {
		
		//sets the window's appearance based on the OS used
		try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
		catch (ClassNotFoundException e) {}
		catch (InstantiationException e) {}
		catch (IllegalAccessException e) {}
		catch (UnsupportedLookAndFeelException e) {}
		
		//Makes the window and gives it an icon
		window = new JFrame("Backup Manager " + version);
		window.setSize(500, 300);
		window.setLocationRelativeTo(null);
		Pane = window.getContentPane();
		insets = Pane.getInsets();
		Pane.setLayout(new GridLayout(3,1));
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
		window.setResizable(false);
		
		//TEST add item to list
		
		Pane.add(createControlPanel());
		Pane.add(createItemList());
		window.pack();

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

	public JPanel createItemList()
	{
		JPanel listPanel = new JPanel();
		listPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		return listPanel;
	}
	
	public void createAddBackupWindow()
	{
		JFrame addWindow = new JFrame("Add backup");
		Container addPane = new Container();
		JPanel addPanel = new JPanel();
		
		//initialise window and disable main window
		addWindow.setSize(500, 300);
		addWindow.setLocationRelativeTo(null);
		addWindow.setVisible(true);
		window.setEnabled(false);
		
		//initialise container
		addPane.setLayout(new GridLayout(3,1));
		addPane = addWindow.getContentPane();

		//add border and buttons
		addPanel.setBorder(BorderFactory.createLineBorder(Color.black));		
		addPanel.add(new JButton("test"));
		
		//add border with buttons to main container
		addPane.add(addPanel);

		//close this window and return focus back to the main
		addWindow.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				window.setEnabled(true);
				addWindow.dispose();
			}
		});
	}
	
	//creates control panel
	public JPanel createControlPanel() 
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
		JPanel controls = new JPanel();
		controls.setLayout(new GridLayout(1, 4));
		controls.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		controls.add(backup);
		controls.add(revert);
		controls.add(add);
		controls.add(remove);


		return controls;	
	}
	

	
	//add logic for buttons in here
    public void actionPerformed(ActionEvent e) {
        if ("backup".equals(e.getActionCommand())) {
        	System.out.println(e.getActionCommand());
        }
        if ("revert".equals(e.getActionCommand())) {
        	System.out.println(e.getActionCommand());
        }
        if ("add".equals(e.getActionCommand())) {
        	System.out.println(e.getActionCommand());
        	createAddBackupWindow();
        }
        if ("remove".equals(e.getActionCommand())) {
        	System.out.println(e.getActionCommand());
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
