package controller;

import java.awt.BorderLayout;

import view.GraphFrame;
import view.GraphPanel;

public class GraphController {

	private GraphFrame frame;
	private GraphPanel panel;

	public void addFrameAndPanel() {
		frame = new GraphFrame();
		panel = new GraphPanel();
		frame.setPanel(panel);
		frame.add(panel, BorderLayout.CENTER);
	}

	// GETTERS AND SETTERS ////////////////////////////////////////////////////
	public GraphPanel getPanel() {
		return panel;
	}

	public GraphFrame getFrame() {
		return frame;
	}
}
