import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class BackupManager extends Component {
	
	public final float version = 0.01f;

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JFrame window;
	
	private BackupFile[] Files;
	
    public BackupManager() {
        window = new JFrame("Backup Manager " + version);
        
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
