package controller;

import java.awt.Dimension;
import java.util.prefs.Preferences;

import view.ColourGenerator;
import view.IndexFrame;
import view.NoteFrame;
import view.PreferencesDialogue;

public class PreferencesController implements PreferencesListener {

	private static PreferencesDialogue preferencesDialogue;
	private static Preferences preferences;
	private Index index;
	private NoteMap noteMap;

	protected PreferencesController(NoteMap noteMap, Index index) {
		preferencesDialogue = new PreferencesDialogue(null);
		preferencesDialogue.setPreferencesListener(this);
		this.noteMap = noteMap;
		this.index = index;
		setToSavedPreferences();
	}

	/**
	 * The preferencesSet method is called when save button is clicked in the
	 * dialogue. It adds the values to be saved to the preference map, and
	 * alters the values currently held in the program to correctly reflect the
	 * chosen settings.
	 **/
	@Override
	public void preferencesSet(FileExtensions savedFileType,
			boolean isColourSelected, int savedColourAdjustment,
			int savedIndexWidth, int savedNoteWidth, int savedNoteHeight,
			boolean savedIsMaxIndexSelected, int savedDelayValue) {

		// put values from dialogue into preferences map
		preferences.putInt("userSavedFileType", savedFileType.ordinal());
		preferences.putBoolean("userColourSetting", isColourSelected);
		preferences.putInt("userSavedColourAdjustment", savedColourAdjustment);
		preferences.putInt("userSavedIndexWidth", savedIndexWidth);
		preferences.putInt("userSavedNoteWidth", savedNoteWidth);
		preferences.putInt("userSavedNoteHeight", savedNoteHeight);
		preferences.putBoolean("userSavedMaxIndexSetting",
				savedIsMaxIndexSelected);
		preferences.putInt("userSavedDelayValue", savedDelayValue);

		// sets current values in controller to dialogue selections
		Controller.setDelay(savedDelayValue);
	}

	@Override
	public void fileTypeComboBoxChanged(FileExtensions fileTypeSelected) {
		noteMap.clear();
		NoteMap.setExtension("." + fileTypeSelected.toString());
	}

	@Override
	public void colourChanged(int colourSliderValue) {
		ColourGenerator.setAdjustment(colourSliderValue);
	}

	@Override
	public void colourCheckBoxChanged(boolean isColourSelected) {
		Controller.setColourMode(isColourSelected);
	}

	@Override
	public void indexSizeChanged(int newWidth) {
		resizeIndex(newWidth);
	}

	@Override
	public void noteSizeChanged(int newWidth, int newHeight) {
		resizeNotes(newWidth, newHeight);	//called for width
		resizeNotes(newWidth, newHeight);	//called for height
	}

	private void resizeNotes(int width, int height) {
		NoteFrame.setStaticPreferredSize(width, height);
		for (Note note : noteMap.values()) {
			if (note.getNoteState().equals(NoteStates.CLOSED)) {
				continue;
			}
			note.getFrame().setSize(new Dimension(width, height));
		}
	}

	private void resizeIndex(int width) {
		IndexFrame.setPreferredWidth(width);
		index.getFrame().setSize(
				new Dimension(width, IndexFrame.getPreferredHeight()));
	}

	/**
	 * The resetToDefaultValues method is called by the defaultsButton in the
	 * dialogue.
	 **/
	@Override
	public void resetToDefaultValues(boolean colourModeDefault,
			int colourValueDefault, int indexFrameWidthDefault,
			int noteFrameWidthDefault, int noteFrameHeightDefault,
			boolean maxIndexSettingDefault, int delayValueDefault) {

		Controller.setColourMode(colourModeDefault);
		ColourGenerator.setAdjustment(colourValueDefault);
		resizeNotes(noteFrameWidthDefault, noteFrameHeightDefault);
		resizeIndex(indexFrameWidthDefault);
		index.getFrame().setIsIndexMaximizedAtStart(maxIndexSettingDefault);
		Controller.setDelay(delayValueDefault);
	}

	/**
	 * The resetToUserPreference method is called by the cancelButton in the
	 * dialogue and the PreferencesController's constructor when the program
	 * starts. It puts the values back to the last saved settings.
	 **/
	@Override
	public void setToSavedPreferences() {

		// put next line in setup - doesn't need to be redefined
		preferences = Preferences.userRoot().node("userDefinedSettings");

		// get values saved in the preferences map
		int i = preferences.getInt("userSavedFileType", 0);
		FileExtensions userSavedFileType = FileExtensions.values()[i];

		boolean userColourSetting = preferences.getBoolean("userColourSetting",
				true);
		int userSavedColourAdjustment = preferences.getInt(
				"userSavedColourAdjustment", 32);
		int userSavedIndexWidth = preferences.getInt("userSavedIndexWidth",
				IndexFrame.getDefaultWidth());
		int userSavedNoteWidth = preferences.getInt("userSavedNoteWidth",
				NoteFrame.getDefaultWidth());
		int userSavedNoteHeight = preferences.getInt("userSavedNoteHeight",
				NoteFrame.getDefaultHeight());
		boolean userSavedMaxIndexSetting = preferences.getBoolean(
				"userSavedMaxIndexSetting", false);
		int userSavedDelayValue = preferences.getInt("userSavedDelayValue",
				1000);
		// apply settings to current components
		noteMap.clear();
		NoteMap.setExtension("." + userSavedFileType.toString());
		index.getFrame().setIsIndexMaximizedAtStart(userSavedMaxIndexSetting);
		Controller.setColourMode(userColourSetting);
		Controller.setDelay(userSavedDelayValue);
		
		// apply settings to dialogue controls
		preferencesDialogue.setControls(userSavedFileType, userColourSetting,
				userSavedColourAdjustment, userSavedIndexWidth,
				userSavedNoteWidth, userSavedNoteHeight,
				userSavedMaxIndexSetting, userSavedDelayValue);
	}

	// GETTERS AND SETTERS /////////////////////////////////////////////////////
	public static PreferencesDialogue getPreferencesDialogue() {
		return preferencesDialogue;
	}
}