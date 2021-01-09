/**
 * ======================================================
 *                     Solar Magic
 *                      Minif 2020
 * ======================================================
 * A tool used for the modification of level files for 
 * the game Soap Mans World. Currently implements the 
 * functionality to open and safe level files, edit layer
 * 1 and 2, and to change the graphics and actions for 
 * each tile. 
 * ======================================================
 * Developed: October 2020
 * Version: 1.0
 * Distribution: Public Build
 * Soap Man's World level file compatability: v0
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

public class Options extends JFrame {
	private static final long serialVersionUID = 1L;
	byte[] levelOptions;
	String filePath;
	BufferedImage[][] graphics;
	JPanel optionsPanel;
	panel levelView;
	JButton saveButton;
	JFormattedTextField[] gfxSettings = new JFormattedTextField[8];
	JFormattedTextField screenSize;
	
	short[][] layer1Tiles;
	byte layer1Size;
	
	String[] tileNames = {
		"FG 1", "FG 2", "BG 1", "BG 2", "SP 1", "SP 2", "SP 3", "SP 4"
	};
	
	public Options(BufferedImage[][] gfx, byte[] options, String path, short[][] layer1, byte screens) {
		levelOptions = options;
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
	
	public JButton createButton(String action, String text) {
		JButton button = new JButton(text);
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
		BufferedImage image;
		image = ImageIO.read(new File(filePath + "/graphics/gfx" + fileID + ".png"));
		for (int i=0; i<8; i++) for (int j=0; j<8; j++) graphics[page][(i*8)+j] = image.getSubimage(j*16, i*16, 16, 16);
	}
	
	
	public void saveSettings() {
		int gfxfile;
		for (int i=0; i<8; i++) {
			gfxfile = Integer.parseInt(gfxSettings[i].getText());
			if (gfxfile<256 && gfxfile >= 0) {
				try {
					loadGraphics(i,gfxfile);
					levelOptions[i+1] = (byte)gfxfile;
				} catch (IOException e) {
					System.out.println("Graphics File: " + gfxfile + " does not exist in the /graphics/ folder");
					gfxSettings[i].setValue(levelOptions[i+1]);
				}
			} else {
				gfxSettings[i].setValue(levelOptions[i+1]);
			}
		}
		
		byte newScreenSize = (byte)Integer.parseInt((screenSize.getText())) ;
		
		if (newScreenSize != levelOptions[0x0A]) {
			if (newScreenSize < 0x20 && newScreenSize > 0) {
				
				short[] tempLayer1;
				for (int rows = 0; rows <layer1Tiles.length; rows++) {
					tempLayer1 = new short[newScreenSize*16];
					for (int newCols=0; newCols<tempLayer1.length; newCols++) {
						if (layer1Tiles[rows].length > newCols) {
							tempLayer1[newCols] = layer1Tiles[rows][newCols];
						}
					}
					layer1Tiles[rows] = tempLayer1;
				}
				
				levelOptions[0x0A] = newScreenSize;
				
			} else screenSize.setValue(levelOptions[0x0A]);
		}
		//layer1Tiles = new short[32][128];
		
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
