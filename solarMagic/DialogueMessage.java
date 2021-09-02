/**
 * ======================================================
 *                     Solar Magic
 *                   Minif 2020-2021
 * ======================================================
 * A tool used for the modification of level files for 
 * the game Soap Mans World. Currently implements the 
 * functionality to open and safe level files, edit layer
 * 1 and 2, and to change the graphics and actions for 
 * each tile. 
 * ======================================================
 * Developed: January 2021
 * Version: 1.1
 * Distribution: Public Build
 * Soap Man's World level file compatability: v1
 * ======================================================
 */

package solarMagic;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import solarMagic.Window.panel;

public class DialogueMessage extends JFrame {					
	private static final long serialVersionUID = 1L;
	ArrayList<String> description = new ArrayList<String>();
	JPanel messages = new JPanel();	
	byte dialogueType = 0;
	JButton confirm, cancel;
	JPanel footerBar;
	int specificItems;
	Window dataa;
	
	panel levelView;
	
	int spOffset;
	LinkedList<Sprite> spList;
	JFormattedTextField spInput;
	
	public DialogueMessage() {									//Called whenever a message is needed
		footerBar = new JPanel();								//Configure the footer bar
		footerBar.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		confirm = new JButton("Ok");
		ActionListener close = new CloseAction();
		confirm.setActionCommand("close");
		confirm.addActionListener(close);
		
		footerBar.add(confirm);
		
		add(footerBar, BorderLayout.SOUTH);
		add(messages, BorderLayout.NORTH);
		
		this.setTitle("Error");									//Configure other window settings
		this.setSize(400,200);
	}
	
	public void prepareForAddingSprites(int offset, LinkedList<Sprite> lst, byte[] info, panel view) {
		this.setTitle("Add Sprite");							//Specific message for sprite window
		
		addLine("Enter an ID to add:");
		addLine("0=enemy, 1=goal, 2=enemy2");
		
		specificItems = 1;
		spInput = new JFormattedTextField();
		spInput.setValue(new Integer(0));						//Require an integer to be entered.
		spInput.setColumns(2);
		
		cancel = new JButton("Cancel");
		ActionListener close = new CloseAction();
		cancel.setActionCommand("close");
		cancel.addActionListener(close);
		
		dialogueType = 1;
		
		footerBar.add(cancel);
		confirm.setActionCommand("confirm");
		
		spList = lst;
		spOffset = offset;
		levelView = view;
	}
	
	public void prepareForNewLevel(int lvl, Window data) {
		this.setTitle("Change Level");
		addLine("Enter a level to change to:");
		specificItems = 2;
		spInput = new JFormattedTextField();
		spInput.setValue(new Integer(lvl));						//Require an integer to be entered.
		spInput.setColumns(2);
		
		cancel = new JButton("Cancel");
		ActionListener close = new CloseAction();
		cancel.setActionCommand("close");
		cancel.addActionListener(close);
		
		dialogueType = 2;
		
		footerBar.add(cancel);
		confirm.setActionCommand("confirm");
		
		dataa = data;
	}
	
	public void addLine(String line) {							//I wonder what it does?
		description.add(line);
	}
	
	private void addSpecificItems() {							//Add items based on what message it is
		switch (dialogueType) {
		case 1: 
			messages.add(spInput);
			break;
		case 2:
			messages.add(spInput);
		} 
	}
	
	public void showDialogue() {								//Show the message
		messages.setLayout(new GridLayout(description.size()+specificItems,0));
		for (int i=0; i<description.size(); i++) messages.add(new JLabel(description.get(i)));
		addSpecificItems();
		this.setVisible(true);
	}
	
	public void close() {										//Close the message
		this.setVisible(false);	
	}
	
	public void confirm() {										//Close the message and perform action
		this.setVisible(false);
		switch (dialogueType) {
		case 1: 												//If sprite message, add the sprite
			if (spList.size()<=95) {
				if ((Integer)spInput.getValue()!=95) {
					spList.add(new Sprite((Integer) spInput.getValue(), spOffset, 8));
					levelView.repaint();
				} else {
					DialogueMessage message = new DialogueMessage();
					message.addLine("Cannot add a restricted sprite.");
					message.addLine(spInput.getValue()+" is used for special purposes.");
					message.showDialogue();
				}
			} else {
				DialogueMessage message = new DialogueMessage();
				message.addLine("Cannot add more sprites.");
				message.showDialogue();
			}
			break;
		case 2: 
			Window.level = (Integer)spInput.getValue();
			if (Window.level>255) Window.level = 255;
			if (Window.level<0) Window.level = 0;
			dataa.openLevel();
			
		}
	}
	
	class CloseAction implements ActionListener {				//Called whenever a menu button is pressed
		public void actionPerformed(ActionEvent event) {
			String input = event.getActionCommand();
			
			if (input.equals("close")) close();
			else if (input.equals("confirm")) confirm();
		}
	}

}
