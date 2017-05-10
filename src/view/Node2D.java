package view;

import java.awt.Color;
import java.util.HashMap;

public class Node2D {
	public double x;
	public double y;
	public double vx;
	public double vy;
	private String name;
	private Integer key;
	private HashMap<String, Node2D> nodesReferringToThisNode;
	private Color colour;

	public Node2D(Integer key, String name) {
		this.key = key;
		this.name = name;
		if (LinkedFrame.isColoured) {
			colour = ColourGenerator.generateColourFromTitle(name);
		} else {
			colour = Color.BLACK;
		}
		nodesReferringToThisNode = new HashMap<String, Node2D>();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setNodesReferingToThisNode(
			HashMap<String, Node2D> nodesReferringToThisNode) {
		this.nodesReferringToThisNode = nodesReferringToThisNode;
	}

	public HashMap<String, Node2D> getNodesReferringToThisNode() {
		return nodesReferringToThisNode;
	}

	public void setColour(Color colour) {
		this.colour = colour;
	}

	public Color getColour() {
		return colour;
	}
	
	public Integer getKey() {
		return key;
	}
}
