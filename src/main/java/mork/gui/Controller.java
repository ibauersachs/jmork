package mork.gui;

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import mork.ExceptionHandler;
import mozilla.thunderbird.Address;
import mozilla.thunderbird.AddressBook;
import mozilla.thunderbird.AddressComparator;

public class Controller {

	private final Container parent;

	private int x = 10;
	private int y = 10;

	public Controller(Container parent) {
		this.parent = parent;
	}

	public void openFile(File morkFile) {
		System.out.println("Reading " + morkFile.getAbsolutePath());

		final List<Throwable> modelExceptions = new ArrayList<Throwable>();
		AddressBook book = new AddressBook();
		book.setExceptionHandler(new ExceptionHandler() {
			public void handle(Throwable t) {
				modelExceptions.add(t);
				// Does not rethrow, so parsing continues
			}
		});
		try {
			book.load(new FileInputStream(morkFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			String msg = "Unable to open " + morkFile.getAbsolutePath();
			JOptionPane.showMessageDialog(parent, msg, "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		} catch (RuntimeException e) {
			e.printStackTrace();
			String msg = e.getMessage();
			JOptionPane.showMessageDialog(parent, msg, "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		List<Address> addresses = new ArrayList<Address>(book.getAddresses());
		Collections.sort(addresses, new AddressComparator());
		openInternalFrame("Addresses of " + morkFile.getName(), addresses);
		openInternalFrame("Errors while parsing " + morkFile.getName(),
				modelExceptions);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void openInternalFrame(String title, Collection<?> model) {
		final JList list = new JList(model.toArray());
		list.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index,
						isSelected, cellHasFocus);
				if (value instanceof Throwable) {
					Throwable throwable = (Throwable) value;
					setText("ERROR: " + throwable.getMessage());
				} else if (value instanceof Address) {
					Address address = (Address) value;
					String msg = String.format("%s, %s (%s) <%s>", address
							.getLastName(), address.getFirstName(), address
							.getDisplayName(), address.getPrimaryEmail());
					setText(msg);
				}
				return this;
			}
		});
		list.revalidate();

		JScrollPane pane = new JScrollPane();
		pane.setViewportView(list);

		JInternalFrame frame = new JInternalFrame(title, true, true, true, true);
		frame.add(pane);
		frame.setVisible(true);
		frame.setSize(320, 240);
		frame.setLocation(x = x + 30, y = y + 30);
		parent.add(frame);
	}

}
