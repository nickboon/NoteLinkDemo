package view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import controller.Commands;
import controller.MenubarListener;
import controller.PreferencesController;

@SuppressWarnings("serial")
public class Menubar extends JMenuBar {
	private static final Color BACKGROUND = new Color(240, 240, 240);
	public MenubarListener menubarListener;
	@SuppressWarnings("unused")
	private HelpFrame helpFrame;

	public Menubar() {

		
		setBackground(BACKGROUND);

		JMenu optionsMenu = new JMenu("Options");
		add(optionsMenu);

		JMenuItem newNoteItem = new JMenuItem("New Note");
		JMenuItem preferencesItem = new JMenuItem("Preferences...");
		JMenuItem helpItem = new JMenuItem("Help");
		JMenuItem saveItem = new JMenuItem("Save All");
		JMenuItem deleteAllItem = new JMenuItem("Delete All");
		JMenuItem closeAllItem = new JMenuItem("Close All");
		JMenuItem exitItem = new JMenuItem("Exit");

		optionsMenu.add(newNoteItem);
		optionsMenu.addSeparator();
		optionsMenu.add(helpItem);
		optionsMenu.add(preferencesItem);
		optionsMenu.addSeparator();
		optionsMenu.add(saveItem);
		optionsMenu.add(deleteAllItem);
		optionsMenu.add(closeAllItem);
		optionsMenu.addSeparator();
		optionsMenu.add(exitItem);

		// ACCELERATORS //
		newNoteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				ActionEvent.CTRL_MASK));

		helpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));

		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				ActionEvent.CTRL_MASK));

		deleteAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
				ActionEvent.CTRL_MASK));

		closeAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
				ActionEvent.CTRL_MASK));

		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				ActionEvent.CTRL_MASK));

		// MNEMONICS //
		optionsMenu.setMnemonic(KeyEvent.VK_O);
		newNoteItem.setMnemonic(KeyEvent.VK_N);
		preferencesItem.setMnemonic(KeyEvent.VK_P);
		helpItem.setMnemonic(KeyEvent.VK_H);
		saveItem.setMnemonic(KeyEvent.VK_S);
		deleteAllItem.setMnemonic(KeyEvent.VK_D);
		closeAllItem.setMnemonic(KeyEvent.VK_C);
		exitItem.setMnemonic(KeyEvent.VK_X);

		// LISTENERS //
		newNoteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (menubarListener != null) {
					menubarListener.noteCommandCalled(0, Commands.NEW);
				}
			}
		});
		helpItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				helpFrame = new HelpFrame();
			}
		});
		preferencesItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PreferencesController.getPreferencesDialogue().setVisible(true);
			}
		});
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (menubarListener != null) {
					menubarListener.noteCommandCalled(0, Commands.SAVE_ALL);
				}
			}
		});
		deleteAllItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (menubarListener != null) {
					menubarListener.noteCommandCalled(0, Commands.DELETE_ALL);
				}
			}
		});
		closeAllItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (menubarListener != null) {
					menubarListener.noteCommandCalled(0, Commands.CLOSE_ALL);
				}
			}
		});
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int goAhead = JOptionPane.showConfirmDialog(null,
						"Close all windows and quit?", null,
						JOptionPane.OK_CANCEL_OPTION);
				if (goAhead == JOptionPane.OK_OPTION) {
					System.exit(0);
				}
			}
		});
	} // END OF CONSTRUCTOR ////////////////////////////////////////////////////

	public void setMenubarListener(MenubarListener listener) {
		menubarListener = listener;
	}
}
