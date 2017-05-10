package controller;

public interface NoteFrameListener {
	public void noteCommandCalled(int key, Commands command);

	public void noteContentChanged(int key);
}
