package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.Timer;

import controller.Commands;

@SuppressWarnings("serial")
public class GraphFrame extends JFrame {
	// COLOUR AND SIZE
	private static final int DEFAULT_WIDTH = 350;
	private static final int DEFAULT_HEIGHT = 400;
	private static Dimension staticPreferredSize;
	private static final Color TOOLBAR_GREY = new Color(240, 240, 240);
	private static Color toolbarBackground;
	// TIMER
	private static final int DELAY = 1; // delay between refreshing frame
	private Timer timer;
	private int iteration;
	// COMPONENTS
	private GraphPanel panel;
	private JToolBar toolbar;
	private JButton zoomInButton;
	private JButton zoomOutButton;

	static {
		staticPreferredSize = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	public GraphFrame() {

		this.setLayout(new BorderLayout());
		// setSize(new Dimension(staticPreferredSize));
		setSize(staticPreferredSize);
		// setExtendedState(MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setTitle("Graph"); // at some point put name of root folder?
		toolbarBackground = TOOLBAR_GREY;

		// CLOSING EVENT //
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// dispose();
				setVisible(false);
			}
		});
		setVisible(true);

		// SET UP TIMER
		iteration = 1;
		timer = new Timer(DELAY, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setToolbarReady(false);
				panel.updateGraph();
				System.out.println(iteration);
				iteration++;
				if (iteration == 500) {
					timer.stop();
					iteration = 1;
					setToolbarReady(true);
				}
			}
		});
		timer.setInitialDelay(0);
		timer.start();

		// RESIZE WINDOW EVENT HANDLER
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				panel.setBuffer(null);
				panel.sizeBoundary();
				timer.start();
			}
		});

		// COMPONENTS
		toolbar = new JToolBar();
		setToolbar();
		add(toolbar, BorderLayout.SOUTH);
		
		Toolbar topToolbar = new Toolbar(0);
		// code from indexFrame
		topToolbar.saveButton.setCommand(Commands.SAVE_ALL);
		topToolbar.saveButton.setToolTipText("Save All Current Notes (Ctrl + S)");
		topToolbar.deleteButton.setCommand(Commands.DELETE_ALL);
		topToolbar.deleteButton
				.setToolTipText("Delete All Notes in Folder (Ctrl + D)");
		topToolbar.graphButton.setToolTipText("Refresh Graph (Ctrl + G)");
		topToolbar.add(topToolbar.graphButton);

		
			add(topToolbar, BorderLayout.NORTH);
	}

	private void setToolbar() {
		toolbar.setBackground(toolbarBackground);
		toolbar.setFloatable(false);

		zoomInButton = new JButton();
		zoomOutButton = new JButton();
		zoomInButton.setBackground(toolbarBackground);
		zoomOutButton.setBackground(toolbarBackground);
		zoomInButton.setIcon(ViewUtils.createIcon(this, "/icons/zoom_in.png"));
		zoomInButton.setToolTipText("Zoom In (Ctrl + =/+)");
		zoomOutButton
				.setIcon(ViewUtils.createIcon(this, "/icons/zoom_out.png"));
		zoomOutButton.setToolTipText("Zoom Out (Ctrl + -/_)");

		toolbar.add(zoomInButton);
		toolbar.add(zoomOutButton);

		final double zoomMagnitude = 0.1;
		zoomInButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.zoom(zoomMagnitude);
			}
		});
		zoomOutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.zoom(-zoomMagnitude);
			}
		});
	}

	public void setToolbarReady(boolean isReady) {
		zoomInButton.setEnabled(isReady);
		zoomOutButton.setEnabled(isReady);
	}

	public void setPanel(GraphPanel panel) {
		this.panel = panel;
	}
	
	public Timer getTimer() {
		return timer;
	}
}
