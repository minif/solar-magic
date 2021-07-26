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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import solarMagic.Window.panel;

public class Options extends JFrame {							//The options menu.
	private static final long serialVersionUID = 1L;			
	byte[] levelOptions;
	String filePath;
	BufferedImage[][] graphics;
	JPanel optionsPanel;
	panel levelView;
	JButton saveButton;
	JFormattedTextField[] gfxSettings = new JFormattedTextField[8];
	JFormattedTextField screenSize;
	
	short[][][][] layer1Tiles;
	byte layer1Size;
	
	String[] tileNames = {
		"FG 1", "FG 2", "BG 1", "BG 2", "SP 1", "SP 2", "SP 3", "SP 4"
	};
	
	public Options(BufferedImage[][] gfx, byte[] options, String path, short[][][][] layer1, byte screens) {
		levelOptions = options;									//Set up stuff
		graphics = gfx;
		filePath = path;
		layer1Tiles = layer1;
		layer1Size = screens;
		
		optionsPanel = new JPanel(new GridLayout(9,2));
		
		for (int i=0; i<8; i++) gfxSettings[i] = createField(i+1);
		
		screenSize = createField(0x0A);
		
		for (int i=0; i<8; i++) {
			optionsPanel.add(new JLabel(tileNames[i]));
			optionsPanel.add(gfxSettings[i]);
		}
		
		optionsPanel.add(new JLabel("Level Size"));
		optionsPanel.add(screenSize);
		
		saveButton = createButton("save", "Save");
		
		add(optionsPanel, BorderLayout.CENTER);
		add(saveButton, BorderLayout.SOUTH);
		
		this.setTitle("Level Options");
		this.setSize(250,250);
	}
	
	public JButton createButton(String action, String text) {	//Used for setting up the menu.
		JButton button = new JButton(text);						//Mostly to avoid copy and paste code
		ActionListener menuAction = new buttonAction();
		button.setActionCommand(action);
		button.addActionListener(menuAction);
		return button;
	}
	
	public JFormattedTextField createField(int setting) {
		JFormattedTextField input = new JFormattedTextField();
		input.setValue(new Integer(levelOptions[setting]));		//Require an integer to be entered.
	    input.setColumns(2);
	    return input;
	}
	
	public void loadGraphics(int page, int fileID) throws IOException {
		BufferedImage image;									//Load graphics if changed and store in graphics buffer
		image = ImageIO.read(new File(filePath + "/gfxdata/gfx" + fileID + ".png"));
		for (int i=0; i<8; i++) for (int j=0; j<16; j++) graphics[page][(i*16)+j] = image.getSubimage(j*8, i*8, 8, 8);
	}
	
	
	public void saveSettings() {								//Save settings to level options
		int gfxfile;
		for (int i=0; i<8; i++) {
			gfxfile = Integer.parseInt(gfxSettings[i].getText());
			if (gfxfile<256 && gfxfile >= 0) {
				try {
					loadGraphics(i,gfxfile);
					levelOptions[i+1] = (byte)gfxfile;
				} catch (IOException e) {
					DialogueMessage message = new DialogueMessage();
					message.addLine("Issue with loadin Graphics File: " + gfxfile);
					message.addLine("Make sure it is in the  /graphics/ folder!");
					message.showDialogue();
					gfxSettings[i].setValue(levelOptions[i+1]);
				}
			} else {
				gfxSettings[i].setValue(levelOptions[i+1]);
			}
		}
		
		byte newScreenSize = (byte)Integer.parseInt((screenSize.getText())) ;
		
		if (newScreenSize != levelOptions[0x0A]) {
			if (newScreenSize < 0x20 && newScreenSize > 0) {
				
				
				short[][][] tempLayer1 = new short[newScreenSize][32][16];
				for (int screens = 0; screens < tempLayer1.length; screens++) {
					if (layer1Tiles[0].length > screens) {
						tempLayer1[screens] = layer1Tiles[0][screens];
					}
				}
				
				layer1Tiles[0] = tempLayer1;
				
				levelOptions[0x0A] = newScreenSize;
				
			} else screenSize.setValue(levelOptions[0x0A]);
		}
		
		levelView.repaint();
	}
	//Action Classes
	
	
	class buttonAction implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			String buttonPressed = event.getActionCommand();
			if (buttonPressed.equals("save")) saveSettings();
		}
	}
}
