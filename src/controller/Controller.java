package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.Timer;

import view.Node2D;

public class Controller implements LinkListener, ToolbarListener,
		MenubarListener, NoteFrameListener {

	private static boolean colourMode;
	private static int delay = 1000; // delay between refreshing open frames
	private static Timer timer;
	private Index index;
	private NoteMap noteMap;
	private NoteFrameController noteFrameController;
	@SuppressWarnings("unused")
	private PreferencesController preferencesController;
	private GraphController graphController;
	private HashMap<Integer, Node2D> nodes;

	/**
	 * The controller creates a NoteMap (which acts as a controller for the
	 * Notes it contains), an Index (a list of the current Note titles), a
	 * NoteFrameController (to manage open NoteFrames), and a
	 * PreferencesController (which saves the users settings from the
	 * PreferencesDialogue frame).
	 * 
	 * It then updates the the components and their GUI frames at regular
	 * intervals (stored in the delay variable, and set in the Preferences
	 * Dialogue).
	 * 
	 * The other boolean variable is colourMode. If this is true, then a colour
	 * is generated for each note from the characters of its title, and given to
	 * the text of that Note and references to it in other Notes.
	 * 
	 * Finally, the controller acts as a listener for all existing Toolbars,
	 * Menubars, LinkedFrames and NoteFrames, and calls methods in the
	 * appropriate component to handle events fired by them.
	 **/
	Controller() {

		// SETUP
		colourMode = false;
		noteMap = new NoteMap(this);
		index = new Index(noteMap);
		noteMap.setIndex(index);
		noteFrameController = new NoteFrameController(noteMap, index);
		preferencesController = new PreferencesController(noteMap, index);
		graphController = new GraphController();

		index.getFrame().setLinkListener(this);
		index.getFrame().getToolbar().setToolbarListener(this);
		index.getFrame().getMenubar().setMenubarListener(this);
		File[] textFiles = noteMap.getFilesInNoteDirectory();
		if (textFiles.length == 0) {
			noteMap.newNote();
		}

		// TIMED UPDATES
		timer = new Timer(delay, new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				noteMap.update();
				index.update();
				noteFrameController.update();
				graphUpdate();
			}
		});
		timer.setInitialDelay(0);
		timer.start();

	} // END OF CONSTRUCTOR ////////////////////////////////////////////////////

	protected void setNoteFrameListeners(Note note) {
		note.getFrame().setLinkListener(this);
		note.getFrame().getToolbar().setToolbarListener(this);
		note.getFrame().setNoteFrameListener(this);
	}

	// EVENT HANDLERS //////////////////////////////////////////////////////////
	/*
	 * If sources for events kept isolated, components (Toolbars, Menubars,
	 * CrossReferences) can be added to any frame.
	 */

	@Override
	public void referenceClicked(String link) {
		noteMap.openClickedNote(link);
	}

	@Override
	public void noteContentChanged(int key) {
		noteMap.changeStateToUnsaved(key);
	}

	@Override
	public void noteCommandCalled(int key, Commands command) {
		switch (command) {
		case NEW:
			noteMap.newNote();
			break;
		case SAVE:
			noteMap.manageSavingOfNote(key);
			break;
		case CLOSE:
			noteMap.manageClosingOfNote(key);
			break;
		case DELETE:
			noteMap.manageDeletionOfNote(key);
			break;
		case SAVE_ALL:
			noteMap.manageSaveAllNotes();
			break;
		case CLOSE_ALL:
			noteMap.manageCloseAllNotes();
			break;
		case DELETE_ALL:
			noteMap.manageDeleteAllNotes();
			break;
		case GRAPH:
			openRefreshGraph();
			break;
		default:
			System.err.println("Uncatered for command in NoteCommandSwitch."
					+ "checkCommandAndCallManage().");
		}
	}

	// GRAPH CONTROLLER /////////////////////////////////////////////////////
	private void graphUpdate() {
		if (graphController.getPanel() != null
				&& graphController.getPanel().isVisible()
				&& !graphController.getFrame().getTimer().isRunning()) {

			// redraw non force related changes: i.e connecting lines and new
			// nodes and repaint

			ArrayList<String> nodesNames = new ArrayList<String>();
			for (Node2D node : nodes.values()) {
				nodesNames.add(node.getName());
			}

			if (!nodesNames.containsAll(index)) {

				// i.e. if index >= nodes
				// add non represented nodes
				ArrayList<String> namesOfNotesAlreadyRepresentedByNodes = new ArrayList<String>(
						index);
				namesOfNotesAlreadyRepresentedByNodes.removeAll(nodesNames);

				// Clumsy way of doing it.
				ArrayList<Note> notesToAdd = new ArrayList<Note>();
				ArrayList<Note> notesInNoteMap = new ArrayList<Note>(
						noteMap.values());
				for (String title : namesOfNotesAlreadyRepresentedByNodes) {
					for (Note note : notesInNoteMap) {
						if (title.equals(note.getTitle())) {
							notesToAdd.add(note);
						}
					}
				}
				HashMap<Integer, Note> mapNotesToAdd = new HashMap<Integer, Note>();
				for (Note noteToAdd : notesToAdd) {
					mapNotesToAdd.put(noteToAdd.getKey(), noteToAdd);
				}
				HashMap<Integer, Node2D> nodesToAddMap = convertNotesToNodes(mapNotesToAdd);
				System.out.println(nodesToAddMap);
				System.out.println(nodesToAddMap.get(6)
						.getNodesReferringToThisNode());
				graphController.getPanel().addNodes(nodesToAddMap);

			} else if (!index.containsAll(nodesNames)) {

				// i.e. if nodes >= index
				// remove nodes from graph that where there are no notes
				ArrayList<Node2D> redundantNodes = new ArrayList<Node2D>(
						nodes.values());

				ArrayList<Node2D> nodesToRemove = new ArrayList<Node2D>();
				for (String title : index) {
					for (Node2D node : redundantNodes) {
						if (title == node.getName()) {
							nodesToRemove.add(node);
						}
					}
				}
				redundantNodes.removeAll(nodesToRemove);
				HashMap<Integer, Node2D> nodesToRemoveMap = new HashMap<Integer, Node2D>();
				for (Node2D nodeToRemove : redundantNodes) {
					nodesToRemoveMap.put(nodeToRemove.getKey(), nodeToRemove);
				}
				graphController.getPanel().removeNodes(nodesToRemoveMap);
			}

			// also need to update nodes referring to nodes

			graphController.getPanel().repaint();
		}
	}

	/**
	 * This directs what happens when the graph button in a toolbar is pressed.
	 * Could be sent to graphController if noteMap is passed in.
	 **/
	private void openRefreshGraph() {

		if (graphController.getPanel() == null) {
			// open - set up graph with nodes representing current notes
			graphController.addFrameAndPanel();
			graphController.getPanel().setNodes(convertNotesToNodes(noteMap));
			graphController.getPanel().initialNodesLayOut();

		} else {
			// refresh - apply forces to re-layout graph
		}
		if (!graphController.getFrame().isVisible()) {
			graphController.getFrame().setVisible(true);
		}
	}

	/**
	 * Turns map of Notes to map of Nodes with corresponding key could return
	 * Note Node Map, held by graphController or used temporarily by a function.
	 * Perhaps have a complimentary convertNodesToNotes. could then create a list
	 * of nodes to set or add to graph, or use directly if import note to view
	 * package.
	 **/
	private HashMap<Integer, Node2D> convertNotesToNodes(
			HashMap<Integer, Note> notes) {

		// convert notes to nodes
		nodes = new HashMap<Integer, Node2D>();
		for (Entry<Integer, Note> noteEntry : notes.entrySet()) {
			Node2D node = new Node2D(noteEntry.getKey(), noteEntry.getValue()
					.getTitle());
			nodes.put(noteEntry.getKey(), node);
		}

		// add nodes to nodesReferringToThisNode HashMap
		for (Entry<Integer, Note> noteEntry : notes.entrySet()) {

			Note note = noteEntry.getValue();
			String title = note.getTitle();

			HashMap<String, Node2D> nodesReferringToThisNode = new HashMap<String, Node2D>();
			for (Entry<String, Integer> entry : note.getReferencesToThis()
					.entrySet()) {

				Integer count = entry.getValue();
				if (!entry.getKey().equalsIgnoreCase(title) && count != 0) {
					String referringTitle = entry.getKey();
					Node2D referringNode = null;

					// this is a bit clumsy as long as getReferences to this is
					// a HashMap<String, Integer>
					// rather than HashMap<Note or Integer(to get Note),
					// Integer>
					// consider rewriting to set in more?
					for (Node2D node : nodes.values()) {
						if (node.getName() == referringTitle) {
							referringNode = node;
							break;
						}
					}
					nodesReferringToThisNode.put(referringTitle, referringNode);
				}
			}

			Node2D node = nodes.get(noteEntry.getKey());
			node.setNodesReferingToThisNode(nodesReferringToThisNode);
		}
		return nodes;
	}

	// GETTERS AND SETTERS /////////////////////////////////////////////////////
	public static void setDelay(int delay) {
		Controller.delay = delay;
	}

	public static boolean getColourMode() {
		return colourMode;
	}

	public static void setColourMode(boolean colourMode) {
		Controller.colourMode = colourMode;
	}

}