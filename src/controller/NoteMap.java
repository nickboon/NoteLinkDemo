package controller;

import static controller.NoteStates.CLOSED;
import static controller.NoteStates.NEW;
import static controller.NoteStates.NEW_UNSAVED;
import static controller.NoteStates.SAVED;
import static controller.NoteStates.UNSAVED;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import view.NoteFrame;

/**
 * The NoteMap class contains all the Note objects in the program, and is used
 * to iterate through them or retrieve them by key. It's methods manage the
 * creation, opening, closing, saving and deletion of the Notes it contains.
 * 
 * The COPY_SUFFIX is added to duplicate titles to avoid accidental overwriting
 * if one of the duplicates is saved.
 * 
 * The extension variable stores the extension the NoteMap reads and writes.
 * 
 * The hiddenFiles Set groups titles that have been changed in the open frame
 * from their saved version. Files with these title are then not reopened by the
 * NoteMap.update method, so they don't appear twice in the Index. If the open
 * file is deleted, the title is removed from hiddenFiles and the previous
 * version can restored to the NoteMap.
 * 
 * The noteDirectory is the directory accessed by the NoteMap when creating and
 * saving the files associated with each Note.
 **/
@SuppressWarnings("serial")
public class NoteMap extends HashMap<Integer, Note> {
	private static final String COPY_SUFFIX = " copy";
	private static final String UNSAVED_MARKER = "*";
	private static String extension;
	private File noteDirectory;
	private String noteDirectoryPath;
	private HashSet<String> hiddenFiles;
	private Index index;
	private Controller controller;

	protected NoteMap(Controller controller) {
		extension = ".txt";
		noteDirectoryPath = System.getProperty("user.dir");
		noteDirectory = new File(noteDirectoryPath);
		hiddenFiles = new HashSet<String>();
		this.controller = controller;
	} // END OF CONSTRUCTOR ////////////////////////////////////////////////////

	// UPDATE //////////////////////////////////////////////////////////////////

	protected void update() {
		// update NoteMap
		synchWithDirectory();
		// update Notes
		updateNotes();
	}

	/**
	 * The synchWithDirectory method is called by the update method. It searches
	 * the noteDirectory for files note represented in the NoteMap. If it finds
	 * any, it creates a new Note and adds it to the map.
	 **/
	private void synchWithDirectory() { // if there are files in the directory
		// but not in the fileList
		File[] textFiles = getFilesInNoteDirectory();
		HashSet<String> textFileSet = new HashSet<String>();
		for (File file : textFiles) {
			textFileSet.add(file.getName());
		}
		if (textFiles.length != 0) {
			for (File file : textFiles) {
				// the same file objects in hiddenFiles and textFiles give
				// different paths
				// and cannot be compared directly, so compare File.getName()s
				if (!hiddenFiles.contains(file.getName())) {
					String title = Utils.removeFileExtension(file.getName());
					if (!index.contains(title)) {
						createNoteFromFile(file);
					}
				}
			}
		}
	}

	private void updateNotes() {
		for (Note note : values()) {
			note.getReferencesToThis().update(note.getTitle(), this);
		}
	}

	protected File[] getFilesInNoteDirectory() {
		File[] files = noteDirectory.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(extension);
			}
		});
		return files;
	}

	private void createNoteFromFile(File file) {
		String title = Utils.removeFileExtension(file.getName());
		Note note = new Note(title);
		note.setController(controller);
		String text = readFile(file);
		note.setText(title + "\n\n" + text);
		note.setFile(file);
		this.put(note.getKey(), note);
	}

	private String readFile(File file) {
		StringBuilder sb = new StringBuilder();
		String nextLine = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			while ((nextLine = br.readLine()) != null) {
				sb.append(nextLine);
				sb.append("\n");
			}
		} catch (FileNotFoundException e) {
			System.err.println("File could not be found: " + file);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("File could not be read: " + file);
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				System.err.println("Reader could not be closed: " + br);
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * The synchTitles method updates the Note title if it is changed in the
	 * frame.
	 **/
	public void synchTitles(Note note) {
		String frameTitle = note.getFrame().getFrameTitle();
		String noteTitle = note.getTitle();
		// if frame title has now changed
		if (!noteTitle.equals(frameTitle)) {
			// prevent duplicates
			if (index.contains(frameTitle)) {
				note.getFrame()
						.getTextPane()
						.setText(
								note.getFrame()
										.getTextPane()
										.getText()
										.replaceFirst(frameTitle,
												frameTitle + COPY_SUFFIX));
			}
			hiddenFiles.add(note.getFile().getName());
			// if title has altered in frame, change title in note to match
			note.setTitle(frameTitle);
		}
		displayTitle(note);
	}

	/**
	 * The displayTitle method is called by the synchTitle method. If the
	 * content of a frame is changed by the user it adds a marker to the title
	 * displayed in the frames title bar.
	 **/
	private void displayTitle(Note note) {
		// display title in frame titlebar with saved/unsaved marker
		boolean b = (note.getNoteState().equals(UNSAVED) || note.getNoteState()
				.equals(NEW_UNSAVED));
		String marker = (b ? UNSAVED_MARKER : "");
		note.getFrame().setTitle(marker + note.getTitle());
	}

	// MANAGE COMMANDS METHODS ////////////////////////////////////////////////
	/**
	 * The newNote method creates a new Note and opens a frame for it. It is
	 * given a default title of Untitled_x, where x is the lowest integer not
	 * already in use by an untitled Note in the map. It is called by the
	 * controller when it receives notified of an event that carries a NEW
	 * command from the GUI, or if the program starts and there are no files
	 * read from the noteDirectory.
	 **/
	protected void newNote() {
		String title = "Untitled_" + getUntitledFrameNumber();
		Note note = new Note(title);
		note.setController(controller);
		note.setText(title + "\n\n");
		note.open();
		note.setNoteState(NEW);
		this.put(note.getKey(), note);
	}

	private int getUntitledFrameNumber() {
		// get names of untitled files
		ArrayList<String> untitles = new ArrayList<String>();
		ArrayList<String> titlesToMatch = new ArrayList<String>();
		titlesToMatch.addAll(index);
		for (Note note : getNewUnsavedNotes()) {
			titlesToMatch.add(note.getTitle());
		}
		for (String title : titlesToMatch) {
			Pattern pattern = Pattern.compile("\\AUntitled_\\d+\\z");
			Matcher matcher = pattern.matcher(title);
			if (matcher.find()) {
				untitles.add(matcher.group());
			}
		}
		// make integer list of number suffices
		ArrayList<Integer> suffixList = new ArrayList<Integer>();
		for (String untitled : untitles) {
			Integer suffix = Integer.parseInt(untitled.substring(untitled
					.indexOf("_") + 1));
			suffixList.add(suffix);
		}
		// find lowest unused number and assign it to untitledFrameNumber
		int i = 1;
		for (i = 1; i <= suffixList.size(); i++) {
			if (suffixList.contains(i)) {
				continue;
			}
			break;
		}
		return i;
	}

	private ArrayList<Note> getNewUnsavedNotes() {
		ArrayList<Note> notes = new ArrayList<Note>();
		for (Note note : values()) {
			if (note.getNoteState().equals(NEW)
					|| note.getNoteState().equals(NEW_UNSAVED)) {
				notes.add(note);
			}
		}
		return notes;
	}

	public void manageSavingOfNote(int key) {
		get(key).save();
	}

	public void manageClosingOfNote(int key) {
		Note note = get(key);
		if (!note.getNoteState().equals(SAVED)) {
			// (if unsaved, new, or new_unsaved)
			int confirmClose = JOptionPane
					.showConfirmDialog(null, "Close without saving?", null,
							JOptionPane.OK_CANCEL_OPTION);
			if (confirmClose == JOptionPane.OK_OPTION) {
				// clear it from the noteList and index
				if (hiddenFiles.contains(note.getFile().getName())) {
					hiddenFiles.remove(note.getFile().getName());
				}
				remove(note.getKey());
				note.close();
			}
		} else {
			// (if saved)
			note.close();
		}
	}

	public void manageDeletionOfNote(int key) {
		Note note = get(key);
		int confirmDelete = JOptionPane.showConfirmDialog(null,
				"Close " + note.getTitle() + " and delete file from folder?",
				null, JOptionPane.OK_CANCEL_OPTION);
		if (confirmDelete == JOptionPane.OK_OPTION) {
			note.delete();
			remove(note.getKey());
			if (hiddenFiles.contains(note.getFile().getName())) {
				hiddenFiles.remove(note.getFile().getName());
			}
		}
	}

	public void manageSaveAllNotes() {
		for (Note note : values()) {
			if (note.getNoteState() != CLOSED) {
				note.save();
			}
		}
	}

	public void manageCloseAllNotes() {
		String message = "Close all windows?";
		int unsavedNoteNumber = 0;
		for (Note note : values()) {
			switch (note.getNoteState()) {
			case NEW:
			case NEW_UNSAVED:
			case UNSAVED:
				unsavedNoteNumber++;
				break;
			default:
				break;
			}
		}
		if (unsavedNoteNumber == 1) {
			message = "One of the open Notes hasn\'t been saved."
					+ "\nClose all windows and discard changes?";
		} else if (unsavedNoteNumber > 1) {
			message = "There are " + unsavedNoteNumber
					+ " open Notes that havn\'t been saved."
					+ "\nClose all windows and discard changes?";
		}
		// if there are no open frames
		if (NoteFrame.getOpenFrameCount() == 0) {
			System.err.println("No no open frames to close");
			// if all open frames are saved
		} else if (unsavedNoteNumber < 1) {
			for (Note note : values()) {
				if (!note.getNoteState().equals(CLOSED)) {
					note.close();
				}
			}
			// if there are unsaved open frames
		} else {
			int confirmClose = JOptionPane.showConfirmDialog(null, message,
					null, JOptionPane.OK_CANCEL_OPTION);
			if (confirmClose == JOptionPane.OK_OPTION) {
				for (Note note : values()) {
					if (note.getNoteState().equals(UNSAVED)) {
						// if unsaved with a previous, hidden file
						if (hiddenFiles.contains(note.getFile().getName())) {
							hiddenFiles.remove(note.getFile().getName());
							note.close();
						}
					} else {
						// if it is new or new_unsaved
						// try to remove it from index
						hiddenFiles.add(note.getTitle());
						index.remove(note.getTitle());
						note.delete();
					}
				}
			}
		}
	}

	public void manageDeleteAllNotes() {
		int confirmDelete = JOptionPane.showConfirmDialog(null,
				"Delete every " + extension + " file in the folder?", null,
				JOptionPane.OK_CANCEL_OPTION);
		if (confirmDelete == JOptionPane.OK_OPTION) {
			for (Note note : values()) {
				note.delete();
				// need to find a way to delete notes from noteList
				// if they are discarded new or new_unsaved frames
			}
			clear();
		}
	}

	/**
	 * The openClickedNote method is called when a reference to the Note in a
	 * frame is clicked by the user. If it is already open, it is brought to the
	 * top of the GUI z index.
	 **/
	public void openClickedNote(String link) {
		for (Note note : values()) {
			if (link.equals(note.getTitle())) {
				if (note.getNoteState() == CLOSED) {
					note.open();
				} else {
					note.getFrame().toFront();
				}
				break;
			}
		}
	}

	public void changeStateToUnsaved(int key) {
		Note note = get(key);
		if (note.getNoteState().equals(NEW)) {
			note.setNoteState(NEW_UNSAVED);
		} else if (note.getNoteState().equals(SAVED)) {
			note.setNoteState(UNSAVED);
		}
	}

	// GETTERS AND SETTERS //////////////////////////////////////////////
	public static String getExtensionString() {
		return extension;
	}

	public static void setExtension(String extension) {
		NoteMap.extension = extension;
	}

	public void setIndex(Index index) {
		this.index = index;
	}

	public HashSet<String> getHiddenFiles() {
		return hiddenFiles;
	}
}
