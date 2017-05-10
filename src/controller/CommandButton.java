package controller;

import javax.swing.JButton;

/** The CommandButton can store one of the Commands enumeration. **/
@SuppressWarnings("serial")
public class CommandButton extends JButton {
	private Commands command;

	public void setCommand(Commands command) {
		this.command = command;
	}

	public Commands getCommand() {
		return command;
	}
}
