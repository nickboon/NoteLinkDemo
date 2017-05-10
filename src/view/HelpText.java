package view;

public class HelpText {

	private static String text = "HELP"
			+ "\n\n"
			+ "This application, when placed in a folder with .txt files makes a list of all the"
			+ " filenames and reads all the texts searching for references to each. It then constructs"
			+ " an index from these references and displays it in a window. Existing files can be opened"
			+ " by clicking on the titles in the index, or new ones can be created by selecting \"New"
			+ " Note\" in the menu or the leftmost icon on the toolbar. Once a note is opened from the"
			+ " file, it can be edited, and the index is updated accordingly. The second icon in the"
			+ " index window saves all open notes, and its counterpart in a note window saves that note."
			+ " The third icon in the index window deletes all the notes from the directory, and its"
			+ " counterpart in a note window deletes just that note. If your system does not override the"
			+ " keys, you can also use Ctrl + N, Ctrl + S, and Ctrl + D to open save or delete notes."
			+ "\n\n"
			+ "The filename/note title is shown in the note window as the first line of text in bold"
			+ " font followed by a carriage return. References to other files are underlined and are"
			+ " clickable.If the colour mode is enabled, a colour is generated from the characters in"
			+ " the title, and that note\'s text and references to it in other notes appear in that "
			+ "colour (You can adjust the lightness of this colour for legibility in the preferences)."
			+ "\n\n"
			+ "The upshot of this is a sort of hypertext notebook. You can build up interrelated"
			+ " articles similar to Wikipedia by writing titles that are descriptive of the the content"
			+ " of the note. You can search the folder by creating a new note and giving it the term you"
			+ " are searching for as a title."
			+ "\n\n"
			+ "The underlying text of the .txt files can be overwritten by the application, but doesn\'t"
			+ " get the styling (colour, underlines or addition of titles in bold). This just happens"
			+ " in the application windows."
			+ "\n\n"
			+ "Open the index window out to see references pointing to the listed titles. The x in the "
			+ "corner of the index window closes all the windows and quits."
			+ "\n\n"
			+ "Please have a go, and let me know what you think, or if you need more info."
			+ "\n\n" + "Cheers," + "\n\n"
			+ "Nick Boon, Jan 2013, boonnick@hotmail.com.";

	public static String getText() {
		return text;
	}
}
