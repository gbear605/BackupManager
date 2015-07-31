import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class BackupManager extends Component {

	public final float version = 0.01f;
	private static final long serialVersionUID = 1L;

	
	//button icons
	Image backupIcon, revertIcon, addIcon, removeIcon;
	
	JFrame window;
	Container Pane;
	Insets insets;
	JButton revert, backup, addItem, removeItem;
	JPanel backupItems;

	
	private BackupFile[] Files;

	public BackupManager() {
		
		//sets the window's appearance based on the OS used
		try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
		catch (ClassNotFoundException e) {}
		catch (InstantiationException e) {}
		catch (IllegalAccessException e) {}
		catch (UnsupportedLookAndFeelException e) {}
		
		//loads icons
	    try {
			backupIcon = ImageIO.read(getClass().getResource("Resources/Backup.png"));
			revertIcon = ImageIO.read(getClass().getResource("Resources/Revert.png"));

		} catch (IOException ex) {

		}
		
		//Makes the window and gives it an icon
		window = new JFrame("Backup Manager " + version);
		window.setSize(500, 300);
		window.setLocationRelativeTo(null);
		Pane = window.getContentPane();
		insets = Pane.getInsets();
		Pane.setLayout(null);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
		window.setVisible (true);
		window.setResizable(false);
		
		//revert button
		revert = new JButton();
		revert.setIcon(new ImageIcon(revertIcon));
		revert.setMargin(new Insets(0, 0, 0, 0));
		revert.setBorder(null);
		revert.setBounds(insets.left + 5, insets.top + 5, revert.getPreferredSize().width, revert.getPreferredSize().height);
		Pane.add(revert);
		
		//backup button
		backup = new JButton();
		backup.setIcon(new ImageIcon(backupIcon));
		backup.setMargin(new Insets(0, 0, 0, 0));
		backup.setBorder(null);
		backup.setBounds(insets.left + 5 + revert.getPreferredSize().width, insets.top + 5, backup.getPreferredSize().width, revert.getPreferredSize().height);
		//Pane.add(backup);
		
		backupItems = new JPanel();

		backupItems.setBounds(insets.left + 5, insets.top + 50, 100, 100);
		backupItems.add(backup);

		Pane.add(backupItems);
		revert.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){

		}});
		
		
		
		window.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
				window.dispose();
			}
		});


	}

	public static void main(String[] args) {
		new BackupManager();
	}

}
