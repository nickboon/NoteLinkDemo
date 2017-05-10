package view;

import java.awt.Color;

/**
 * The ColourGenerator class stores the MAX_TWENTY_ONE_DIGIT_BINARY which frames
 * a long number that can be split into three seven digit binaries used to set
 * values for the red, green and blue channel of a colour generated from the
 * characters of a title. The range of values possible is half of the full range
 * to stop the text being too light to read easily, but can be adjusted by the
 * user by a control in the PreferencesDialogue which sets the adjustTone
 * variable for best balance of legibility and discrimination of colour.
 **/
public class ColourGenerator {

	protected static final int MAX_TWENTY_ONE_DIGIT_BINARY = Integer.parseInt(
			"111111111111111111111", 2);
	protected static int adjustTone = 32; // Amount by which generatedColour is
											// lightened, if necessary

	static public Color generateColourFromTitle(String title) {
		// Add the values of the characters in the title
		int bytes = 0;
		byte[] chars = title.getBytes();
		for (byte b : chars) {
			bytes += b;
		}
		// multiply this to a power to get a number in the target range
		long largeNumber = (long) Math.pow(bytes, 4);
		// use modulo to get number between 0 and M21DB
		int remainder = (int) (largeNumber % MAX_TWENTY_ONE_DIGIT_BINARY);
		// format as a 21 digit binary
		String bigBinary = Integer.toBinaryString(remainder);
		int noughtsToAdd = 21 - bigBinary.length();
		for (int i = 0; i < noughtsToAdd; i++) {
			bigBinary = "0" + bigBinary;
		}
		// split into 3 7 digit binary numbers
		String redBinary = bigBinary.substring(0, 7);
		String blueBinary = bigBinary.substring(7, 14);
		String greenBinary = bigBinary.substring(14, 21);

		// convert binaries to integers
		int redInt = Integer.parseInt(redBinary, 2);
		int blueInt = Integer.parseInt(blueBinary, 2);
		int greenInt = Integer.parseInt(greenBinary, 2);
		redInt += adjustTone;
		blueInt += adjustTone;
		greenInt += adjustTone;
		Color generatedColour = new Color(redInt, greenInt, blueInt);

		return generatedColour;
	}

	public static void setAdjustment(int adjustment) {
		adjustTone = adjustment;
	}
}
