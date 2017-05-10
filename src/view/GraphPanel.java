package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class GraphPanel extends JPanel {

	// NODES NON REFERENTIAL FORCE DIRECTION
	private static final double PHI = 1.6180339887498949;
	private HashMap<Integer, Node2D> nodes;
	private double nodeIdealDiameter;
	private double speed;
	private double edgeRepulsionMagnitude;

	// DRAWING
	private BufferedImage buffer;

	public GraphPanel() {
		super();

		setBackground(Color.WHITE);
		// Set border to match textpanes in other windows.
		// Border border = new JTextPane().getBorder();
		// setBorder(border);

		// best guess:
		Border insideBorder = BorderFactory.createEtchedBorder();
		Border outsideBorder = BorderFactory
				.createBevelBorder(BevelBorder.LOWERED);
		setBorder(BorderFactory.createCompoundBorder(outsideBorder,
				insideBorder));
	}

	public void sizeBoundary() { // change this for an optimal. border
									// between
									// panel edge and nodes
		double boundingDiameter = nodeIdealDiameter * (PHI);
		double ratioHeightToBound = getHeight() / boundingDiameter;
		nodeIdealDiameter *= ratioHeightToBound;
	}

	public void initialNodesLayOut() {

		nodeIdealDiameter = 100; // this has become arbitrary
		// ARRANGE CIRCLES REGULARLY AROUND CENTRE
		int distanceFromCentre = 100; // arbitrary
		double angle = (2 * Math.PI) / nodes.size();
		int i = 0;
		for (Node2D node : nodes.values()) {
			node.x = getWidth() / 2;
			node.x -= Math.sin(angle * i) * distanceFromCentre;
			node.y = getHeight() / 2;
			node.y -= Math.cos(angle * i) * distanceFromCentre;
			i++;
		}
	}

	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		if (buffer == null) {
			buffer = new BufferedImage(getWidth(), getHeight(),
					BufferedImage.TYPE_INT_RGB);
		}
		Graphics2D g2 = (Graphics2D) buffer.getGraphics();
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// DRAW LINES
		for (Node2D node : nodes.values()) {
			g2.setColor(node.getColour());

			for (Node2D referringNode : node.getNodesReferringToThisNode()
					.values()) {
				Line2D.Double line = new Line2D.Double(node.x, node.y,
						referringNode.x, referringNode.y);
				g2.draw(line);
			}
		}

		// DRAW LABELS
		for (Node2D node : nodes.values()) {
			g2.setColor(node.getColour());

			FontRenderContext frc = g2.getFontRenderContext();
			Font font = new Font("Serif", Font.PLAIN, 14);
			String name = " " + node.getName() + " ";
			TextLayout layout = new TextLayout(name, font, frc);

			Rectangle2D bounds = layout.getBounds();
			float layoutX = (float) (node.x - bounds.getWidth() / 2);
			float layoutY = (float) (node.y + bounds.getHeight() / 2);

			Shape base = layout.getLogicalHighlightShape(0, name.length());
			AffineTransform at = AffineTransform.getTranslateInstance(layoutX,
					layoutY);
			Shape highlight = at.createTransformedShape(base);

			g2.setPaint(Color.white);
			g2.fill(highlight);

			g2.setColor(node.getColour());
			layout.draw(g2, layoutX, layoutY);
			g2.draw(highlight);
		}
		g.drawImage(buffer, 0, 0, null);
	}

	public void updateGraph() {
		spaceOut();
		repaint();
	}

	public void spaceOut() {

		// push in from edges
		for (Node2D node : nodes.values()) {
			curvedEdgeRepulsion(node);
		}

		// optimally space out nodes.
		int i = 0;
		for (Node2D nodeA : nodes.values()) {
			int j = 0;
			for (Node2D nodeB : nodes.values()) {
				if (j < i) {
					curvedNodeRepulsion(nodeA, nodeB);
				}
				j++;
			}
			i++;
		}
	}

	private void curvedNodeRepulsion(Node2D nodeA, Node2D nodeB) {

		double dx = nodeA.x - nodeB.x;
		double dy = nodeA.y - nodeB.y;
		double distSQ = Math.pow(dx, 2) + Math.pow(dy, 2);
		double dist = Math.sqrt(distSQ);
		double inverseDist = 1 / dist;

		speed = (1.0 / (nodes.size())) * 50; // need to balance speed and
		// good layout.
		if (speed < 1) {
			speed = 1;
		}
		double accel = (dist - nodeIdealDiameter) / (nodeIdealDiameter) * speed;

		nodeA.x -= dx * inverseDist * accel;
		nodeB.x += dx * inverseDist * accel;
		nodeA.y -= dy * inverseDist * accel;
		nodeB.y += dy * inverseDist * accel;

	}

	private void curvedEdgeRepulsion(Node2D node) {
		edgeRepulsionMagnitude = 50;
		node.x -= (node.x - getWidth() / 2) / getWidth() / 2
				* edgeRepulsionMagnitude;
		node.y -= (node.y - getHeight() / 2) / getWidth() / 2
				* edgeRepulsionMagnitude;
	}

	public void zoom(double magnitude) {

		for (Node2D node : nodes.values()) {
			double displacementX = node.x - getWidth() / 2;
			double displacementY = node.y - getHeight() / 2;

			node.x += displacementX * magnitude;
			node.y += displacementY * magnitude;
			repaint();
		}
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	// GETTERS AND SETTERS
	public void setBuffer(BufferedImage buffer) {
		this.buffer = buffer;
	}

	public void setNodes(HashMap<Integer, Node2D> nodes) {
		this.nodes = nodes;
	}

	public void addNodes(HashMap<Integer, Node2D> nodesToAdd) {
		nodesToAdd.putAll(nodes);
		nodes = nodesToAdd;
	}

	public void removeNodes(HashMap<Integer, Node2D> nodesToRemove) {
		for (Integer key : nodesToRemove.keySet()) {
			nodes.remove(key);
		}
	}

	public HashMap<Integer, Node2D> getNodes() {
		return nodes;
	}
}
