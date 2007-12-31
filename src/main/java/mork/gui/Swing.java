package mork.gui;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.WindowConstants;

public class Swing extends JFrame {

	private final JDesktopPane desktop = new JDesktopPane();
	private final Controller controller = new Controller(desktop);

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new Swing();
	}

	public Swing() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(640, 480);
		setTitle("jMork - Simple Address Book Viewer");
		setJMenuBar(createMenu());
		setVisible(true);
		setContentPane(desktop);
	}

	private JMenuBar createMenu() {
		JMenuBar bar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		fileMenu.add(new OpenAction(controller));
		bar.add(fileMenu);
		return bar;
	}

}
