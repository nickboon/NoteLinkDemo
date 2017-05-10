package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import controller.LinkListener;

/**
 * The LinkedFrame class is the parent of the NoteFrame and IndexFrame classes.
 * It sets up the searching for, and styling of, cross references listed in the
 * index supplied by the Controller when it calls the LinkedFrame.update method.
 * 
 * The currentHoverLink is set to the value of a reference if a mouse passes
 * over it. If the user clicks on the reference, the referenceClicked event is
 * fired in the controller and the value of the currentHOverLink is passed to
 * it.
 **/
@SuppressWarnings("serial")
public class LinkedFrame extends JFrame implements MouseMotionListener {

	protected JTextPane textPane;
	protected ArrayList<CrossReference> referenceList;
	protected SimpleAttributeSet linkAttributes;
	protected static boolean isColoured;
	protected Color textColour;
	protected boolean isMouseOverLink;
	protected String currentHoverLink;
	protected LinkListener linkListener;

	public LinkedFrame() {

		Color background = UIManager.getColor("Panel.background");
		int red = background.getRed();
		int blue = background.getBlue();
		int green = background.getGreen();
		background = new Color(red, blue, green);
		ArrayList<Object> noGradient = new ArrayList<Object>();
		noGradient.add(0.3);
		noGradient.add(0.0);
		noGradient.add(background);
		noGradient.add(background);
		noGradient.add(background);
		UIManager.put("ScrollBar.gradient", noGradient);
		UIManager.put("ScrollBar.background", Color.DARK_GRAY);
		UIManager.put("ScrollBar.thumbShadow", Color.LIGHT_GRAY);
		UIManager.put("ScrollBar.thumbDarkShadow", Color.LIGHT_GRAY);
		UIManager.put("ScrollBar.thumb", Color.LIGHT_GRAY);
		UIManager.put("ScrollBar.thumbHighlight", background);
		UIManager.put("ScrollBar.trackHighlight", background);
		UIManager.put("ScrollBar.darkShadow", Color.LIGHT_GRAY);

		referenceList = new ArrayList<CrossReference>();
		// FRAME
		setLayout(new BorderLayout());
		setVisible(true);

		// TEXTPANE
		textPane = new JTextPane();
		add(textPane);
		textPane.setFont(new Font("Serif", Font.PLAIN, 14));
		textPane.setText("No text set");

		// DOCUMENT
		// general text colour
		textColour = Color.black; // if isColoured is true this is overridden by
									// update()
		// style distinguishing cross references in text
		linkAttributes = new SimpleAttributeSet();
		linkAttributes.addAttribute(
				StyleConstants.CharacterConstants.Underline, Boolean.TRUE);

		// LISTENERS
		textPane.addMouseMotionListener(this);
		textPane.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				if (isMouseOverLink) {
					if (linkListener != null) {
						linkListener.referenceClicked(currentHoverLink);
					}
				}
			}
		});
	} // END OF CONSTRUCTOR ////////////////////////////////////////////////////

	/**
	 * The update method updates text and Title styling and underline instances
	 * of cross references.
	 **/
	public void update(ArrayList<String> index) {
		if (isColoured) {
			textPane.setForeground(textColour);
		} else {
			textPane.setForeground(Color.black);
		}
		referenceList.clear();
		for (String title : index) {
			searchForReferences(title);
		}
		removeUnderline();
		for (CrossReference reference : referenceList) {
			if (isColoured) {
				colourReference(reference);
			} else {
				linkAttributes.addAttribute(
						StyleConstants.CharacterConstants.Foreground,
						Color.black);
			}
			underlineReference(reference);
		}
	}

	protected void searchForReferences(String pattern) {
		String frameText = textPane.getText();
		int pos = 0;
		int i = 0;
		if (!pattern.isEmpty()) {
			while ((pos = frameText.toUpperCase().indexOf(
					pattern.toUpperCase(), pos)) >= 0) {
				// add found references to refenceList
				referenceList.add(i, new CrossReference(pos, pattern));
				// find next instance of pattern
				pos += pattern.length();
				i++;
			}
		}
	}

	protected void underlineReference(CrossReference reference) {
		StyledDocument doc = (StyledDocument) textPane.getDocument();
		doc.setCharacterAttributes(reference.getStart(), reference.getLength(),
				linkAttributes, true);
	}

	protected void colourReference(CrossReference reference) {
		Color linkColour = ColourGenerator.generateColourFromTitle(reference
				.getText());
		linkAttributes.addAttribute(
				StyleConstants.CharacterConstants.Foreground, linkColour);
	}

	protected void removeUnderline() {
		StyledDocument doc = (StyledDocument) textPane.getDocument();
		SimpleAttributeSet attributes = new SimpleAttributeSet();
		attributes.addAttribute(StyleConstants.CharacterConstants.Underline,
				Boolean.FALSE);
		doc.setCharacterAttributes(0, doc.getLength(), attributes, true);
	}

	// LISTENERS ///////////////////////////////////////////////////////////////
	public void mouseDragged(MouseEvent e) {
	}

	/**
	 * The mouseMoved method checks if mouse is over a reference, and changes
	 * the cursor if it is. It also sets the currentHoverLink variable to that
	 * reference.
	 **/
	public void mouseMoved(MouseEvent e) {

		int offsetAtMouse = textPane.viewToModel(new Point(e.getX(), e.getY()));
		for (CrossReference reference : referenceList) {
			if (offsetAtMouse >= reference.getStart()
					&& offsetAtMouse < reference.getEnd()) {
				isMouseOverLink = true;
				textPane.setCursor(Cursor.getPredefinedCursor(12));// HAND_CURSOR
				currentHoverLink = reference.getText();
				break;
			} else {
				isMouseOverLink = false;
				textPane.setCursor(Cursor.getPredefinedCursor(2));// TEXT_CURSOR
			}
		}
	}

	// GETTERS AND SETTERS //
	public void setLinkListener(LinkListener listener) {
		linkListener = listener;
	}

	public JTextPane getTextPane() {
		return textPane;
	}

	public ArrayList<CrossReference> getReferenceList() {
		return referenceList;
	}

	public void setTextColour(Color textColour) {
		this.textColour = textColour;
	}

	public void setColoured(boolean b) {
		isColoured = b;
	}
}