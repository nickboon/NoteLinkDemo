package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import controller.Commands;
import controller.NoteFrameListener;

@SuppressWarnings("serial")
public class NoteFrame extends LinkedFrame {

	private static final int DEFAULT_WIDTH = 350;
	private static final int DEFAULT_HEIGHT = 400;
	private static Dimension staticPreferredSize;
	private static SimpleAttributeSet titleAttributes;
	private static int openFrameCount = 0;
	private final int key;
	private String frameTitle;
	private Toolbar toolbar;
	public NoteFrameListener noteFrameListener;

	static {
		staticPreferredSize = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	public NoteFrame(int noteKey) {
		key = noteKey;
		// style distinguishing title in text
		titleAttributes = new SimpleAttributeSet();
		titleAttributes.addAttribute(StyleConstants.CharacterConstants.Bold,
				Boolean.TRUE);

		toolbar = new Toolbar(key);
		add(toolbar, BorderLayout.NORTH);

		// FRAME //
		// frame count should probably be handled by a static Note list?
		openFrameCount++;
		setSize(new Dimension(staticPreferredSize));
		setLocation(160, 92);
		add(new JScrollPane(textPane));
		// stagger new windows
		Point point = getLocation();
		int offset = 23;
		point.x += (openFrameCount - 1) * offset;
		point.y += (openFrameCount - 1) * offset;
		setLocation(point);

		// SAVE ACCELERATOR //
		textPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK),
				"save");
		// put action in actionMap
		textPane.getActionMap().put("save", new NoteAction() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if (noteFrameListener != null) {
					noteFrameListener.noteCommandCalled(key, Commands.SAVE);
				}
			}
		});
		// NEW ACCELERATOR //
		textPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK),
				"new");
		// put action in actionMap
		textPane.getActionMap().put("new", new NoteAction() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if (noteFrameListener != null) {
					noteFrameListener.noteCommandCalled(key, Commands.NEW);
				}
			}
		});
		// DELETE ACCELERATOR //
		// nb on mac doesn't work if frame selected. Something might be
		// overriding it?
		textPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK),
				"delete");
		// put action in actionMap
		textPane.getActionMap().put("delete", new NoteAction() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if (noteFrameListener != null) {
					noteFrameListener.noteCommandCalled(key, Commands.DELETE);
				}
			}
		});
		// HELP ACCELERATOR //
		textPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "help");
		// put action in actionMap
		textPane.getActionMap().put("help", new NoteAction() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				new HelpFrame();
			}
		});
		// CLOSING EVENT //
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (noteFrameListener != null) {
					noteFrameListener.noteCommandCalled(key, Commands.CLOSE);
				}
			}
		});
		// CONTENT CHANGED EVENT //
		textPane.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
			}

			public void removeUpdate(DocumentEvent e) {
				fireNoteFrameListener();
			}

			public void insertUpdate(DocumentEvent e) {
				fireNoteFrameListener();
			}
		});
	}

	/**
	 * fireNoteFrameListener alerts the controller if the noteFrames text
	 * content is changed by the user
	 **/
	private void fireNoteFrameListener() {
		if (noteFrameListener != null) {
			noteFrameListener.noteContentChanged(key);
		}
	}

	public void updateTitle() {
		String titleText = textPane.getText();
		if (titleText.indexOf("\n") > 0) {
			// get title
			titleText = (titleText.subSequence(0, titleText.indexOf("\n"))
					.toString().trim());
			// style title in text
			StyledDocument doc = (StyledDocument) textPane.getDocument();
			doc.setCharacterAttributes(0, titleText.length(), titleAttributes,
					true);
			// assign result to frameTitle variable
			frameTitle = titleText;
		}
	}

	public static void reduceOpenFrameCount() {
		openFrameCount--;
	}

	// GETTERS AND SETTERS /////////////////////////////////////////////////////
	public void setNoteFrameListener(NoteFrameListener listener) {
		noteFrameListener = listener;
	}

	public static int getDefaultWidth() {
		return DEFAULT_WIDTH;
	}

	public static int getDefaultHeight() {
		return DEFAULT_HEIGHT;
	}

	public static void setStaticPreferredSize(int width, int height) {
		 staticPreferredSize = new Dimension(width, height);
	}
	
	public static Dimension getStaticPreferredSize() {
		return staticPreferredSize;
	}

	public static int getOpenFrameCount() {
		return openFrameCount;
	}

	public String getFrameTitle() {
		return frameTitle;
	}

	public Toolbar getToolbar() {
		return toolbar;
	}
}