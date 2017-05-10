package view;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import controller.Commands;

@SuppressWarnings("serial")
public class IndexFrame extends LinkedFrame {
	private static final int MINIMUM_WIDTH = 110;
	private static final int DEFAULT_WIDTH = 131; // 131
	private static int preferredWidth = DEFAULT_WIDTH;
	private static int maximizedHeight;
	private Menubar menubar;
	private Toolbar toolbar;
	private JScrollPane scrollPane;

	public IndexFrame() {
		// COMPONENTS
		JPanel noWrapPanel = new JPanel(new BorderLayout());
		noWrapPanel.add(textPane);
		
		scrollPane = new JScrollPane(noWrapPanel);
		add(scrollPane);
		
		//add(new JScrollPane(noWrapPanel));
		menubar = new Menubar();
		setJMenuBar(menubar);
		toolbar = new Toolbar(0);
		add(toolbar, BorderLayout.NORTH);

		toolbar.saveButton.setCommand(Commands.SAVE_ALL);
		toolbar.saveButton.setToolTipText("Save All Current Notes (Ctrl + S)");
		toolbar.deleteButton.setCommand(Commands.DELETE_ALL);
		toolbar.deleteButton
				.setToolTipText("Delete All Notes in Folder (Ctrl + D)");
		textPane.setEditable(false);
		toolbar.add(toolbar.graphButton);
		
		// FRAME
		// setExtendedState(JFrame.MAXIMIZED_BOTH);
		// double height = getSize().getHeight();
		// maximizedHeight = (int) height;

		maximizedHeight = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getMaximumWindowBounds().height;

		int preferredHeight = 0;
		if (maximizedHeight == 0) {
			preferredHeight = NoteFrame.getStaticPreferredSize().height;
		} else {
			preferredHeight = maximizedHeight;
		}
		setSize(preferredWidth, preferredHeight);
		setTitle("NoteLink");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	} // END OF CONSTRUCTOR ////////////////////////////////////////////////////

	/** Updates the list of Titles in the Index window **/
	public void updateIndexFrameText(ArrayList<String> index) {
		StringBuilder indexStringBuilder = new StringBuilder("\n Index:\n\n");
		Collections.sort(index);
		for (String title : index) {
			indexStringBuilder.append(" " + title + "\n");
		}
		final int vPos = scrollPane.getVerticalScrollBar().getValue();
		final int hPos = scrollPane.getHorizontalScrollBar().getValue();
		textPane.setText(indexStringBuilder.toString());
		
		// stop scrollbar returning to top when refereshed.
		//Alternatively could try setting index in a table?
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				scrollPane.getVerticalScrollBar().setValue(vPos);
				scrollPane.getHorizontalScrollBar().setValue(hPos);
			}
		});
	}
	
	// GETTERS AND SETTERS /////////////////////////////////////////////////////
	public Menubar getMenubar() {
		return menubar;
	}

	public void setIsIndexMaximizedAtStart(boolean setting) {
		if (setting) {
			setExtendedState(JFrame.MAXIMIZED_BOTH);
		}
	}

	public static int getMinimumWidth() {
		return MINIMUM_WIDTH;
	}

	public static int getDefaultWidth() {
		return DEFAULT_WIDTH;
	}

	public static int getPreferredHeight() {
		return maximizedHeight;
	}

	public static void setPreferredWidth(int preferredWidth) {
		IndexFrame.preferredWidth = preferredWidth;
	}

	public Toolbar getToolbar() {
		return toolbar;
	}
}