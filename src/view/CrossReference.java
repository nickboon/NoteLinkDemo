package view;

/**
 * The CrossReference object stores an instance of a title from the index in the
 * text of a note, and its position in the text. It's used by the frame to
 * underline the instance and make it clickable.
 **/
public class CrossReference {
	private String text;
	private int start;
	private int end;

	CrossReference(int index, String text) {
		this.text = text;
		start = index;
		end = start + text.length();
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public int getLength() {
		return text.length();
	}

	public String getText() {
		return text;
	}
}
