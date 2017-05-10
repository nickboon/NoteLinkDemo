package controller;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;

import view.IndexFrame;

/**
 * The Index is a list of current Note titles, regularly updated by the
 * Controller, and displayed in its own IndexFrame. If the boolean
 * addReferences is true, each title in the IndexFrame is followed by a
 * list of other notes referencing it, and the number of references they
 * contain.
 **/
@SuppressWarnings("serial")
public class Index extends ArrayList<String> {
	private static boolean addReferences;
	private NoteMap noteMap;
	private IndexFrame frame;

	protected Index(NoteMap noteMap) {
		addReferences = (true);
		this.noteMap = noteMap;
		frame = new IndexFrame();
	}

	protected void update() {
		updateIndex();
		updateFrame();
	}

	private void updateIndex() {
		clear();
		for (Note note : noteMap.values()) {
			add(note.getTitle());
		}
	}

	private void updateFrame() {
		// update text colour according to Controller.colourMode
		if (Controller.getColourMode()) {
			frame.setColoured(true);
		} else {
			frame.setColoured(false);
			frame.setTextColour(Color.black);
		}
		
		//update displayed titles list, adding references if addReferences is true.
		if (addReferences) {
			ArrayList<String> titlesWithReferences = new ArrayList<String>();
			for (Note note : noteMap.values()) {
				ArrayList<Entry<String, Integer>> entries = new ArrayList<Entry<String, Integer>>();
				for (Entry<String, Integer> entry : note.getReferencesToThis()
						.entrySet()) {
					if (!entry.getKey().equalsIgnoreCase(note.getTitle())) {
						entries.add(entry);
					}
				}
				Collections.sort(entries,
						new Comparator<Entry<String, Integer>>() {
							@Override
							public int compare(Entry<String, Integer> p1,
									Entry<String, Integer> p2) {
								return p1.getValue() - p2.getValue();
							}
						});
				while (!entries.isEmpty() && entries.get(0).getValue() == 0) {
					entries.remove(0);
				}
				Collections.reverse(entries);
				StringBuilder titleWithReferencesSB = new StringBuilder(
						note.getTitle());
				int totalReferences = 0;
				for (Entry<String, Integer> entry : entries) {
					totalReferences += entry.getValue();
				}
				if (totalReferences == 1) {
					titleWithReferencesSB.append("\t\t - 1 reference:- ");
				} else if (totalReferences > 1) {
					titleWithReferencesSB.append("\t\t - " + totalReferences
							+ " references:- ");
				}
				for (Entry<String, Integer> entry : entries) {
					titleWithReferencesSB.append(entry.getKey() + " ("
							+ entry.getValue() + "), ");
				}
				if (!entries.isEmpty()) {
					titleWithReferencesSB.replace(
							titleWithReferencesSB.length() - 2,
							titleWithReferencesSB.length() - 1, ".");
				}
				titlesWithReferences.add(titleWithReferencesSB.toString());
			}
			frame.updateIndexFrameText(titlesWithReferences);
		} else {
			frame.updateIndexFrameText(this);
		}
		//	update links etc.
		frame.update(this);
	}

	// GETTERS AND SETTERS /////////////////////////////////////////////////////
	public IndexFrame getFrame() {
		return frame;
	}
}