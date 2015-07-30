import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class BackupManager extends Component {

	public final float version = 0.01f;
	private static final long serialVersionUID = 1L;

	private JFrame window;
	Container Pane;
	Insets insets;

	private BackupFile[] Files;

	public BackupManager() {
		
		window = new JFrame("Backup Manager " + version);
		window.setSize(500, 300);
		window.setLocationRelativeTo(null);
		Pane = window.getContentPane();
		insets = Pane.getInsets();
		Pane.setLayout(null);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
		window.setVisible (true);
		window.setResizable(false);

		window.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		window.setVisible(true);
	}

	public static void main(String[] args) {
		new BackupManager();
	}

}
