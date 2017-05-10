package controller;

public class Utils {

	public static String removeFileExtension(String name) {
		int pointIndex = name.lastIndexOf(".");
		if (pointIndex == -1) {
			return null;
		}
		return name.substring(0, pointIndex);
	}
}
