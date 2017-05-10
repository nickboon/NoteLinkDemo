package controller;

import static controller.NoteStates.CLOSED;
import static controller.NoteStates.SAVED;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

import view.NoteFrame;

public class Note {
	private static int keys;
	//private static String newline;
	private int key;
	private String title;
	private String text;
	private File file;
	private NoteFrame frame;
	private NoteStates noteState;
	private ReferencesMap referencesToThis;
	private String extension;
	private Controller controller;

	/**
	 * The Note is the main object of NoteLink and represents a titled hypertext
	 * displayed in the NoteFrame variable. They can be saved, deleted, opened
	 * and closed by the program's NoteMap.
	 * 
	 * As a Note is created it is assigned a key integer which is passed on to
	 * its associated frame (if opened), and is used to refer back to the Note
	 * by event handlers.
	 * 
	 * The title variable stores the definitive title for the Note. This is
	 * initially derived from the file's filename, or a default Untitled_x
	 * string if the Note is newly created by the user or program. It can be
	 * edited by the user in the frame.
	 * 
	 * The text variable stores the last saved version of the Note's text.
	 * 
	 * The file variable holds the file the Note was created from, or stores the
	 * file created when a new Note is saved.
	 * 
	 * The noteState variable stores one of the NoteStates enumeration values
	 * (NEW, NEW_UNSAVED, UNSAVED, SAVED, CLOSED). These allow the NoteMap to
	 * set a warning marker in the title bar of the frame if the note is in an
	 * unsaved state and lets the command manager methods tailor its saving and
	 * closing.
	 * 
	 * The ReferencesMap stores the titles of those Notes which contain this
	 * one's title in the text, and the number of times it occurs.
	 **/
	protected Note(String title) {
		keys++;
		key = keys;
		extension = NoteMap.getExtensionString();
		file = new File(title + extension);
		setTitle(title);
		text = "";
		noteState = CLOSED;
		referencesToThis = new ReferencesMap();
		//newline = System.getProperty("line.separator");
	} // END OF CONSTRUCTOR ////////////////////////////////////////////////////

	/**
	 * The Save method writes the text content of the frame (minus the title) to
	 * the file referred to by the file variable. The filename becomes the title
	 * plus the NoteMap.extension. When a Note is saved, the old file is deleted
	 * and replaced by a new one, so that the associated file is 'overwritten'
	 * even if the user changes the title in the frame.
	 **/
	protected void save() {
		file.delete();
		file = new File(title + extension);
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			//String[] lines = getTextNoTitle().split(newline);
			String[] lines = getTextNoTitle().split("\n");
			for (String line : lines) {
				bw.write(line);
				bw.newLine();
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, "Couldn't save note: "
					+ "\nDon't use * ^ \" / \\ [ ] :  | ? <> {}"
					+ "\nin your title.");
			System.err.println("couldn\'t write to file: " + file);
			e.printStackTrace();
		} finally {
			try {
				bw.close();
			} catch (IOException e1) {
				System.err.println("Couldn\'t close FileWriter for file "
						+ file + " in Note.save().");
				e1.printStackTrace();
			}
		}
		text = getFrame().getTextPane().getText();
		noteState = SAVED;
	}

	protected void delete() {
		if (file != null) {
			file.delete();
		}
		if (noteState != CLOSED) {
			close();
		}
	}

	protected void open() {
		noteState = SAVED;
		frame = new NoteFrame(key);
		frame.setTitle(title);
		frame.getTextPane().setText(text);
		if (controller != null) {
			controller.setNoteFrameListeners(this);
		}
		frame.toFront();
	}

	protected void close() {
		noteState = CLOSED;
		frame.dispose();
		NoteFrame.reduceOpenFrameCount();
	}

	private String getTextNoTitle() {
		String text = frame.getTextPane().getText();
		if (text.startsWith(title)) {
			text = text.replaceFirst(title, "");
			while (text.startsWith("\n")) {
				text = text.replaceFirst("\n", "");
			}
		}
		return text;
	}

	// GETTERS AND SETTERS /////////////////////////////////////////////////////
	public int getKey() {
		return key;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public NoteStates getNoteState() {
		return noteState;
	}

	public void setNoteState(NoteStates noteState) {
		this.noteState = noteState;
	}

	public NoteFrame getFrame() {
		return frame;
	}

	public ReferencesMap getReferencesToThis() {
		return referencesToThis;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}
}