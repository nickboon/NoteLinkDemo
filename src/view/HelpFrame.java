package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

@SuppressWarnings("serial")
public class HelpFrame extends JFrame {
	private JTextPane textPane;

	public HelpFrame() {
		setPreferredSize(new Dimension(350, 400)); // or whatever
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		setVisible(true);
		textPane = new JTextPane();
		textPane.setFont(new Font("Serif", Font.PLAIN, 14));
		textPane.setText(HelpText.getText());
		textPane.setEditable(false);
		add(new JScrollPane(textPane), BorderLayout.CENTER);
		pack();
	}
}
