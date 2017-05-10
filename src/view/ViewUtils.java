package view;

import java.net.URL;

import javax.swing.ImageIcon;

public class ViewUtils {
	public static ImageIcon createIcon(Object object, String path) {
		URL url = object.getClass().getResource(path);
		if (url == null) {
			System.err.println("unable to load icon " + path);
		}
		ImageIcon icon = new ImageIcon(url);
		return icon;
	}
}
