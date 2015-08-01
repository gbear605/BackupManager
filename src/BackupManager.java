
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class BackupManager implements ActionListener {

	public final float version = 0.01f;
	private static final long serialVersionUID = 1L;

	JFrame window;
	Container Pane;
	Insets insets;
	JPanel backupItems;

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
		//Pane.setLayout(null);
		//Pane.setLayout(new GridLayout(1,2));
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
		window.setVisible (true);
		window.setResizable(false);
		
		//adds control panel to pane
		Pane.add(createControlPanel());

		//handles closing
		window.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
				window.dispose();
			}
		});


	}

	//creates control panel
	private JPanel createControlPanel() 
	{
	
		//backup button
		JButton backup = new JButton();		
		backup.setIcon(getIcon("Resources/Backup.png"));
		backup.setMargin(new Insets(0, 0, 0, 0));
		backup.setBorder(null);
		int buttonSize = backup.getPreferredSize().width;
		backup.setBounds(0, insets.top + 5, buttonSize, buttonSize);
	    backup.setActionCommand("backup");
		backup.addActionListener(this);

		//revert button
		JButton revert = new JButton();
		revert.setIcon(getIcon("Resources/Revert.png"));
		revert.setMargin(new Insets(0, 0, 0, 0));
		revert.setBorder(null);
		revert.setBounds(buttonSize, insets.top + 5, buttonSize, buttonSize);
	    revert.setActionCommand("revert");
		revert.addActionListener(this);

		//add button
		JButton addItem = new JButton();
		addItem.setIcon(getIcon("Resources/AddToList.png"));
		addItem.setMargin(new Insets(0, 0, 0, 0));
		addItem.setBorder(null);
		addItem.setBounds(buttonSize * 2, insets.top + 5, buttonSize, buttonSize);
	    addItem.setActionCommand("add");
		addItem.addActionListener(this);
		
		//remove button
		JButton removeItem = new JButton();
		removeItem.setIcon(getIcon("Resources/RemoveFromList.png"));
		//removeItem.setMargin(new Insets(0, 0, 0, 0));
		//removeItem.setBorder(null);
		//removeItem.setBounds(buttonSize * 3, insets.top + 5, buttonSize, buttonSize);
	    removeItem.setActionCommand("remove");
		removeItem.addActionListener(this);
		
		//puts buttons into a container
		JPanel controls = new JPanel(new GridLayout(1, 4));
		controls.add(backup);
		controls.add(revert);
		controls.add(addItem);
		controls.add(removeItem);
		controls.setBorder(BorderFactory.createLineBorder(Color.black));       
		controls.setBounds(insets.left + 5, insets.top + 5, buttonSize * 4, buttonSize);
		controls.validate();
		Pane.validate();
		Pane.repaint();
		return controls;
		
	}
	
	//gets icons
	public Icon getIcon(String i){
		try {
			return new ImageIcon(ImageIO.read(getClass().getResource(i)));
		} catch (IOException e) {
			return null;
		}
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
        }
        if ("remove".equals(e.getActionCommand())) {
        	System.out.println(e.getActionCommand());
        }

    }
	
	public static void main(String[] args) {
		new BackupManager();
	}

}
