package view;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import controller.FileExtensions;
import controller.PreferencesListener;

@SuppressWarnings("serial")
public class PreferencesDialogue extends JDialog {
	private static final boolean COLOUR_MODE_DEFAULT_SETTING = true;
	private static final int COLOUR_RANGE_MIN = 0;
	private static final int COLOUR_RANGE_MAX = 127;
	private static final int DEFAULT_COLOUR_VALUE = 32;
	private static final int NOTE_WINDOW_WIDTH_MIN = 100;
	private static final int NOTE_WINDOW_HEIGHT_MIN = 100;
	private static final Rectangle MAXIMUM_WINDOW_BOUNDS = GraphicsEnvironment
			.getLocalGraphicsEnvironment().getMaximumWindowBounds();
	private final static int NOTE_WINDOW_WIDTH_MAX = MAXIMUM_WINDOW_BOUNDS.width;
	private final static int NOTE_WINDOW_HEIGHT_MAX = MAXIMUM_WINDOW_BOUNDS.height;
	private final static boolean MAX_INDEX_DEFAULT_SETTING = false;
	private static final int DELAY_RANGE_MIN = 0;
	private static final int DELAY_RANGE_MAX = 3000;
	private static final int DEFAULT_DELAY_VALUE = 1000;
	@SuppressWarnings("rawtypes")
	private JComboBox fileTypeComboBox;
	private JCheckBox colourCheckBox;
	private JLabel colourSliderLabel;
	private JSlider colourSlider;
	private JSlider indexWidthSlider;
	private JSlider noteWidthSlider;
	private JSlider noteHeightSlider;
	private JCheckBox maxIndexCheckBox;
	private JSlider delaySlider;
	private JButton saveButton;
	private JButton defaultsButton;
	private JButton cancelButton;
	private PreferencesListener preferencesListener;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PreferencesDialogue(JFrame parent) {
		super(parent, "Preferences", false);

		Color background = UIManager.getColor("Panel.background");
		int red = background.getRed();
		int blue = background.getBlue();
		int green = background.getGreen();
		background = new Color(red, blue, green);
		UIManager.put("Button.background", background);
		UIManager.put("ComboBox.background", background);
		UIManager.put("CheckBox.background", background);

		fileTypeComboBox = new JComboBox();
		// set up fileTypeComboBox
		DefaultComboBoxModel fileTypeComboBoxModel = new DefaultComboBoxModel();
		fileTypeComboBoxModel.addElement(controller.FileExtensions.txt);
		fileTypeComboBoxModel.addElement(controller.FileExtensions.java);
		fileTypeComboBox.setModel(fileTypeComboBoxModel);

		colourCheckBox = new JCheckBox("Enable/Disable");
		colourSlider = new JSlider(COLOUR_RANGE_MIN, COLOUR_RANGE_MAX,
				DEFAULT_COLOUR_VALUE);
		colourSliderLabel = new JLabel("Adjust Colour");
		indexWidthSlider = new JSlider(IndexFrame.getMinimumWidth(),
				MAXIMUM_WINDOW_BOUNDS.width, IndexFrame.getDefaultWidth());
		noteWidthSlider = new JSlider(NOTE_WINDOW_WIDTH_MIN,
				NOTE_WINDOW_WIDTH_MAX, NoteFrame.getDefaultWidth());
		noteHeightSlider = new JSlider(NOTE_WINDOW_HEIGHT_MIN,
				NOTE_WINDOW_HEIGHT_MAX, NoteFrame.getDefaultHeight());
		maxIndexCheckBox = new JCheckBox("Enable/Disable");
		delaySlider = new JSlider(DELAY_RANGE_MIN, DELAY_RANGE_MAX,
				DEFAULT_DELAY_VALUE);

		Insets buttonInsets = new Insets(2, 8, 2, 8); // top, left, bottom,
														// right
		saveButton = new JButton("Save");
		saveButton.setMargin(buttonInsets);
		defaultsButton = new JButton("Defaults");
		defaultsButton.setMargin(buttonInsets);
		cancelButton = new JButton("Cancel");
		cancelButton.setMargin(buttonInsets);
		setSize(NoteFrame.getDefaultWidth(), NoteFrame.getDefaultHeight());
		setLocationRelativeTo(null);
		layoutControls();

		// LISTENERS //
		fileTypeComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileExtensions fileTypeSelected = (FileExtensions) fileTypeComboBox
						.getSelectedItem();
				if (preferencesListener != null) {
					preferencesListener
							.fileTypeComboBoxChanged(fileTypeSelected);
				}
			}
		});
		colourCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean checkBoxSelected = colourCheckBox.isSelected();
				colourSlider.setEnabled(checkBoxSelected);
				colourSliderLabel.setEnabled(checkBoxSelected);
				if (preferencesListener != null) {
					preferencesListener.colourCheckBoxChanged(checkBoxSelected);
				}
			}
		});
		colourSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
				JSlider source = (JSlider) ce.getSource();
				int colourSliderValue = (int) source.getValue();
				if (preferencesListener != null) {
					preferencesListener.colourChanged(colourSliderValue);
				}
			}
		});
		indexWidthSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
				newIndexSize();
			}
		});
		noteWidthSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
				newNoteSize();
			}
		});
		noteHeightSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
				newNoteSize();
			}
		});
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileExtensions savedFileType = (FileExtensions) fileTypeComboBox
						.getSelectedItem();
				boolean isColourSelected = colourCheckBox.isSelected();
				int savedColourAdjustment = colourSlider.getValue();
				int savedIndexWidth = indexWidthSlider.getValue();
				int savedNoteWidth = noteWidthSlider.getValue();
				int savedNoteHeight = noteHeightSlider.getValue();
				boolean isMaxIndexSelected = maxIndexCheckBox.isSelected();
				int savedDelayValue = delaySlider.getValue();

				if (preferencesListener != null) {
					preferencesListener.preferencesSet(savedFileType,
							isColourSelected, savedColourAdjustment,
							savedIndexWidth, savedNoteWidth, savedNoteHeight,
							isMaxIndexSelected, savedDelayValue);
				}
				setVisible(false);
			}
		});
		defaultsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// reset dialogue
				colourCheckBox.setSelected(COLOUR_MODE_DEFAULT_SETTING);
				colourSliderLabel.setEnabled(COLOUR_MODE_DEFAULT_SETTING);
				colourSlider.setEnabled(COLOUR_MODE_DEFAULT_SETTING);
				colourSlider.setValue(DEFAULT_COLOUR_VALUE);
				indexWidthSlider.setValue(IndexFrame.getDefaultWidth());
				noteWidthSlider.setValue(NoteFrame.getDefaultWidth());
				noteHeightSlider.setValue(NoteFrame.getDefaultHeight());
				maxIndexCheckBox.setSelected(MAX_INDEX_DEFAULT_SETTING);
				delaySlider.setValue(DEFAULT_DELAY_VALUE);
				// reset Controller
				if (preferencesListener != null) {
					preferencesListener.resetToDefaultValues(
							COLOUR_MODE_DEFAULT_SETTING, DEFAULT_COLOUR_VALUE,
							IndexFrame.getDefaultWidth(),
							NoteFrame.getDefaultWidth(),
							NoteFrame.getDefaultHeight(),
							MAX_INDEX_DEFAULT_SETTING, DEFAULT_DELAY_VALUE);
				}
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				if (preferencesListener != null) {
					preferencesListener.setToSavedPreferences();
				}
			}
		});
	} // END OF CONSTRUCTOR ////////////////////////////////////////////////////

	/**
	 * The setControls method resets the dialogue controls according to saved
	 * user preferences.
	 **/
	public void setControls(FileExtensions userFileType,
			boolean userColourSetting, int userSavedColourAdjustment,
			int userSavedIndexWidth, int userSavedNoteWidth,
			int userSavedNoteHeight, boolean userMaxIndexSetting,
			int userSavedDelayValue) {

		fileTypeComboBox.setSelectedItem(userFileType);
		colourCheckBox.setSelected(userColourSetting);
		colourSliderLabel.setEnabled(userColourSetting);
		colourSlider.setEnabled(userColourSetting);
		colourSlider.setValue(userSavedColourAdjustment);
		indexWidthSlider.setValue(userSavedIndexWidth);
		noteWidthSlider.setValue(userSavedNoteWidth);
		noteHeightSlider.setValue(userSavedNoteHeight);
		maxIndexCheckBox.setSelected(userMaxIndexSetting);
		delaySlider.setValue(userSavedDelayValue);
	}

	private void layoutControls() {
		JPanel filePanel = new JPanel();
		JPanel textPanel = new JPanel();
		JPanel windowsPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		filePanel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		textPanel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		windowsPanel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		buttonPanel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));

		GridBagConstraints gc = new GridBagConstraints();
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.gridx = 0;

		filePanel.setLayout(new GridBagLayout());

		// FILE PANEL //

		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(0, 6, 0, 0);
		filePanel.add(new JLabel("File Type To Index:"), gc);
		gc.gridx++;
		gc.insets = new Insets(0, 34, 0, 0);
		gc.anchor = GridBagConstraints.EAST;
		filePanel.add(new JLabel("."), gc);
		gc.gridx++;
		gc.insets = new Insets(0, 0, 0, 2);
		filePanel.add(fileTypeComboBox, gc);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridy++;

		textPanel.setLayout(new GridBagLayout());

		// TEXT PANEL //

		gc.gridx = 0;
		gc.insets = new Insets(0, 6, 0, 0);
		textPanel.add(new JLabel("Colour Mode"), gc);
		gc.gridx++;
		gc.anchor = GridBagConstraints.EAST;
		gc.insets = new Insets(0, 0, 0, 2);
		textPanel.add(colourCheckBox, gc);
		gc.anchor = GridBagConstraints.WEST;

		gc.gridy++;
		gc.gridwidth = 2;
		gc.gridx = 0;
		gc.insets = new Insets(0, 6, 0, 0);
		textPanel.add(colourSliderLabel, gc);

		gc.insets = new Insets(0, 0, 0, 0);
		gc.gridy++;
		gc.gridx = 0;
		textPanel.add(colourSlider, gc);

		// WINDOWS PANEL //
		windowsPanel.setLayout(new GridBagLayout());

		gc.insets = new Insets(0, 6, 0, 0);
		gc.gridx = 0;
		windowsPanel.add(new JLabel("Index Window Maximized At Start"), gc);
		gc.gridy++;
		gc.anchor = GridBagConstraints.EAST;
		gc.insets = new Insets(0, 0, 0, 2);
		windowsPanel.add(maxIndexCheckBox, gc);
		gc.anchor = GridBagConstraints.WEST;

		gc.insets = new Insets(0, 6, 0, 0);
		gc.gridy++;
		gc.gridx = 0;
		windowsPanel.add(new JLabel("Set Index Window Width"), gc);

		gc.insets = new Insets(0, 0, 0, 0);
		gc.gridy++;
		gc.gridx = 0;
		windowsPanel.add(indexWidthSlider, gc);

		gc.insets = new Insets(0, 6, 0, 0);
		gc.gridy++;
		gc.gridx = 0;
		windowsPanel.add(new JLabel("Set Note Window Width"), gc);

		gc.insets = new Insets(0, 0, 0, 0);
		gc.gridy++;
		gc.gridx = 0;
		windowsPanel.add(noteWidthSlider, gc);

		gc.insets = new Insets(0, 6, 0, 0);
		gc.gridy++;
		gc.gridx = 0;
		windowsPanel.add(new JLabel("Set Note Window Height"), gc);

		gc.insets = new Insets(0, 0, 0, 0);
		gc.gridy++;
		gc.gridx = 0;
		windowsPanel.add(noteHeightSlider, gc);

		gc.insets = new Insets(0, 6, 0, 0);
		gc.gridy++;
		gc.gridx = 0;
		windowsPanel.add(new JLabel("Set Refresh Rate"), gc);

		gc.insets = new Insets(0, 0, 0, 0);
		gc.gridy++;
		gc.gridx = 0;
		windowsPanel.add(delaySlider, gc);

		// BUTTON PANEL
		gc.gridy++;
		gc.gridwidth = 1;
		gc.gridx = 0;
		buttonPanel.add(saveButton, gc);
		gc.gridx++;
		buttonPanel.add(defaultsButton, gc);
		gc.gridx++;
		buttonPanel.add(cancelButton, gc);

		// WHOLE FRAME
		gc.anchor = GridBagConstraints.CENTER;
		setLayout(new GridBagLayout());
		add(filePanel, gc);
		gc.gridy++;
		add(textPanel, gc);
		gc.gridy++;
		add(windowsPanel, gc);
		gc.gridy++;
		add(buttonPanel, gc);

	}

	private void newNoteSize() {
		int newWidth = (int) noteWidthSlider.getValue();
		int newHeight = (int) noteHeightSlider.getValue();
		if (preferencesListener != null) {
			preferencesListener.noteSizeChanged(newWidth, newHeight);
		}
	}

	private void newIndexSize() {
		int newWidth = (int) indexWidthSlider.getValue();
		if (preferencesListener != null) {
			preferencesListener.indexSizeChanged(newWidth);
		}
	}

	// GETTERS AND SETTERS /////////////////////////////////////////////////////
	public void setPreferencesListener(PreferencesListener listener) {
		preferencesListener = listener;
	}
}
