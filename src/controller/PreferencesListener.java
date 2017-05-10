package controller;

public interface PreferencesListener {
	public void preferencesSet(FileExtensions savedFileType, boolean isColourSelected,
			int savedColourAdjustment, int savedIndexWidth, int savedNoteWidth,
			int savedNoteHeight, boolean savedIsMaxIndexSelected,
			int savedDelayValue);

	public void fileTypeComboBoxChanged(FileExtensions fileTypeSelected);
	
	public void colourChanged(int colourSliderValue);

	public void colourCheckBoxChanged(boolean isColourSelected);

	public void indexSizeChanged(int newWidth);

	public void noteSizeChanged(int newWidth, int newHeight);

	public void resetToDefaultValues(boolean colourModeDefault,
			int colourValueDefault, int indexFrameWidthDefault,
			int noteFrameWidthDefault, int noteFrameHeightDefault,
			boolean maxIndexSettingDefault, int delayValueDefault);

	public void setToSavedPreferences();
}