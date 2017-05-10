package controller;

import java.awt.Color;

import view.ColourGenerator;

public class NoteFrameController {
	private NoteMap noteMap;
	private Index index;
	
	NoteFrameController(NoteMap noteMap, Index index) {
		this.noteMap = noteMap;
		this.index = index;
	} // END OF CONSTRUCTOR ////////////////////////////////////////////////////
	
	protected void update() {
		for (Note note : noteMap.values()) {
			if (note.getNoteState() != NoteStates.CLOSED) {

				note.getFrame().update(index); // to update links in frame
				note.getFrame().updateTitle();
				noteMap.synchTitles(note);

				if (Controller.getColourMode()) {
					note.getFrame().setColoured(true);
					Color textColour = ColourGenerator
							.generateColourFromTitle(note.getTitle());
					note.getFrame().setTextColour(textColour);
				} else {
					note.getFrame().setColoured(false);
					note.getFrame().setTextColour(Color.black);
				}
			}
		}
	}
}
