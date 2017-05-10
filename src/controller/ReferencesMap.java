package controller;

import java.util.HashMap;

/**
 * The ReferencesMap stores the titles of those Notes which contain the title of
 * its associated Note in the text, with the number of times it occurs.
 **/
@SuppressWarnings("serial")
public class ReferencesMap extends HashMap<String, Integer> {

	public void update(String TitleReferredTo, HashMap<Integer, Note> noteMap) {
		clear();
		for (Note referringNote : noteMap.values()) {
			if (referringNote.getNoteState().equals(NoteStates.CLOSED)) {
				put(referringNote.getTitle(),
						CountReferences(TitleReferredTo,
								referringNote.getText()));
			} else {
				put(referringNote.getTitle(),
						CountReferences(TitleReferredTo, referringNote
								.getFrame().getTextPane().getText()));
			}
		}
	}

	private int CountReferences(String pattern, String text) {
		int pos = 0;
		int i = 0;
		if (!pattern.isEmpty()) {
			while ((pos = text.toUpperCase()
					.indexOf(pattern.toUpperCase(), pos)) >= 0) {
				pos += pattern.length();
				i++;
			}
		}
		return i;
	}
}