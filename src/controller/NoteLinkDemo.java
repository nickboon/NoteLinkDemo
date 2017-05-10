package controller;

import javax.swing.SwingUtilities;

/**
 * NoteLink demo version, January 2014, Nick Boon (nickboon.com).
 * 
 * This demonstrates an idea for a notepad type program which indexes text
 * documents in a directory, and can open them to read or edit. If a document is
 * opened, references to other documents (ie, to their titles) are automatically
 * updated to become clickable links. This means you can have hypertext over
 * ordinary text as opposed to writing markup.
 * 
 * The titles of the texts are displayed in an index, and if they occur in other
 * document, a reference to that second document is added. All titles written in
 * either the index or in opened documents are clickable and will bring up the
 * document they refers to.
 * 
 * The idea is that if you follow a convention of writing a title at the top of
 * the text that describes what it is about, as in an encyclopaedia entry, it
 * will find instances of that title (and therefore subject, train of thought,
 * etc.) and provide a meaningful link to it. The references following the
 * titles in the index then map out the connections that arise between ideas and
 * the relative strength of each.
 * 
 * The NoteLinkDemo class contains the program's main method.
 **/
public class NoteLinkDemo {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Controller();
			}
		});
	}
}
