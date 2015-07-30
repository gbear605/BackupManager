import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.awt.Container;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

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
