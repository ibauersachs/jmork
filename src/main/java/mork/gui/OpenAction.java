package mork.gui;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class OpenAction extends AbstractAction {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;
	private final Controller controller;

	public OpenAction(Controller controller) {
		super("Open...");
		this.controller = controller;
	}

	public void actionPerformed(ActionEvent e) {
		ProfileLocator locator = new ProfileLocator();
		String result = locator.locateFirstThunderbirdAddressbookPath();

		JFileChooser chooser = new JFileChooser();
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);

		if (result != null) {
			// We found a Thunderbird profile in the user app data
			chooser.setCurrentDirectory(new File(result));
		}

		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File f) {
				if (f.isDirectory())
					return true;
				return f.getName().endsWith("mab");
			}

			@Override
			public String getDescription() {
				return "Mozilla Addressbook (*.mab)";
			}
		};
		chooser.setFileFilter(filter);
		chooser.setFileHidingEnabled(false);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			System.out.println("You chose to open this file: "
					+ chooser.getSelectedFile().getName());
			controller.openFile(chooser.getSelectedFile());
		}
	}

}
