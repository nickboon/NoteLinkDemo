package view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JToolBar;

import controller.CommandButton;
import controller.Commands;
import controller.ToolbarListener;

@SuppressWarnings("serial")
public class Toolbar extends JToolBar implements ActionListener{
	private static final Color BACKGROUND = new Color(240, 240, 240);
	public ToolbarListener toolbarListener;
	protected CommandButton saveButton;
	protected CommandButton deleteButton;
	protected CommandButton graphButton;
	private CommandButton newButton;
	private int key;
	
	public Toolbar(int key) {
		
		this.key = key;
		
		newButton = new CommandButton();
		newButton.setIcon(ViewUtils.createIcon(this, "/icons/file.png"));
		newButton.setToolTipText("New Note (Ctrl + N)");
		// newButton.setBorderPainted(false);

		saveButton = new CommandButton();
		saveButton.setIcon(ViewUtils.createIcon(this, "/icons/save.png"));
		saveButton.setToolTipText("Save Note (Ctrl + S)");
		// saveButton.setBorderPainted(false);

		deleteButton = new CommandButton();
		deleteButton.setIcon(ViewUtils.createIcon(this, "/icons/action_delete.png"));
		deleteButton.setToolTipText("Delete Note From Folder (Ctrl + D)");
		// deleteButton.setBorderPainted(false);

		graphButton = new CommandButton();
		graphButton.setIcon(ViewUtils.createIcon(this, "/icons/graph3.png"));
		graphButton.setToolTipText("Open/Refresh Graph (Ctrl + G)");
		// deleteButton.setBorderPainted(false);

		
		// setBorder(BorderFactory.createEtchedBorder());
		//setBackground(panel.getBackground());
		setBackground(BACKGROUND);
		setFloatable(false);
		add(newButton);
		add(saveButton);
		add(deleteButton);


		newButton.addActionListener(this);
		newButton.setCommand(Commands.NEW);
		saveButton.addActionListener(this);
		saveButton.setCommand(Commands.SAVE);
		deleteButton.addActionListener(this);
		deleteButton.setCommand(Commands.DELETE);
		graphButton.addActionListener(this);
		graphButton.setCommand(Commands.GRAPH);
	} // END OF CONSTRUCTOR ////////////////////////////////////////////////////

	public void actionPerformed(ActionEvent e) {
		if (toolbarListener != null) {
			Commands command = ((CommandButton)e.getSource()).getCommand();
			toolbarListener.noteCommandCalled(key, command);
		}
	}

	// GETTERS AND SETTERS /////////////////////////////////////////////////////
	public void setToolbarListener(ToolbarListener listener) {
		toolbarListener = listener;
	}

}