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
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;

import solarMagic.Window.panel;

public class TileSelect extends JFrame {
	private static final long serialVersionUID = 1L;
	byte[][] map16Tiles;
	BufferedImage[][] graphics;
	layer1Panel tileView = new layer1Panel();
	JPanel buttonBar, menuBar, inputPanel; 
	JFormattedTextField[] input = new JFormattedTextField[4];
	JFormattedTextField aInput;
	short selectedTile = 0;
	short offset = 0;
	
	int x = 0;
	int y = 0;
	short cursorPage = 0;
	
	panel levelView;
	
	
	public TileSelect(BufferedImage[][] gfx, byte[][] map16) {
		map16Tiles = map16;
		graphics = gfx;
		
		buttonBar = new JPanel();
		buttonBar.setLayout(new GridLayout(5,1));
		
		menuBar = new JPanel();
		menuBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(2,2));
		
		JButton changeTileButton = new JButton("Change Graphic");
		changeTileButton.addActionListener(new ChangeButtonAction());
		
		aInput = new JFormattedTextField();
		aInput.setValue(new Integer(0));		//Require an integer to be entered.
		aInput.setColumns(2);	
		aInput.setValue(map16Tiles[selectedTile][0]);
		
		for (int i=0; i<4; i++) {
			input[i] = new JFormattedTextField();
			input[i].setValue(new Integer(0));		//Require an integer to be entered.
			input[i].setColumns(2);	
			inputPanel.add(input[i]);
			input[i].setValue(getTile(selectedTile, i) +(getTileGraphic(selectedTile, i)<<7));
		}
	    
	    buttonBar.add(inputPanel);
	    buttonBar.add(createChangeButton("cgraphic", "Set GFX"));
	    buttonBar.add(aInput);
	    buttonBar.add(createChangeButton("caction", "Set Action"));
	    
	    menuBar.add(createMenuButton("-1", "<"));
	    menuBar.add(createMenuButton("1", ">"));
		
		add(buttonBar, BorderLayout.EAST);
		add(tileView, BorderLayout.CENTER);
		add(menuBar, BorderLayout.NORTH);
		
		this.setTitle("Tile Selection");
		this.setSize(400,320);
		
		tileView.repaint();
	}
	
	public JButton createMenuButton(String action, String text) {
		JButton button = new JButton(text);
		ActionListener menuAction = new ChangePageAction();
		button.setActionCommand(action);
		button.addActionListener(menuAction);
		return button;
	}
	
	public JButton createChangeButton(String action, String text) {
		JButton button = new JButton(text);
		ActionListener menuAction = new ChangeButtonAction();
		button.setActionCommand(action);
		button.addActionListener(menuAction);
		return button;
	}
	
	public JFormattedTextField createGFXInput() {
		JFormattedTextField in = new JFormattedTextField();
		in.setValue(new Integer(0));		//Require an integer to be entered.
		in.setColumns(2);					//Sets the preferred size of field.
		return in;
	}
	
	public void selectTile(MouseEvent e) {
		int tx= e.getPoint().x/16;
		int ty= e.getPoint().y/16;
		
		cursorPage = offset;
		
		if (tx<=7 && ty<=7) {
			x=tx;
			y=ty;
			selectedTile = (short) (y*8+x + (offset*64));
			tileView.repaint();
			
			for(int i=0; i<4; i++) input[i].setValue(getTile(selectedTile, i) +(getTileGraphic(selectedTile, i)<<7));
			aInput.setValue(map16Tiles[selectedTile][0]);
		}
	}
	
	public int getTile(int tile, int corner) {
		return (int) map16Tiles[tile][1+corner] & 0x7F;
		//So far, each gfx file is a 128x128 .png image, divided into 16x16 tiles.
		//That means each file is 8 tiles in width and height. Because of that, only
		//64 tiles exist, and so the requested tile on the map16 is anded with 64 (in hex)
		//so that we only use the lower 6 bits.
	}
	
	public int getTileGraphic(int tile, int corner) {
		return (int) (map16Tiles[tile][5]>>(corner*2)) & 0x03;
	}
	
	public void changeTile(short tile,int corner) {
		int value = Integer.parseInt(input[corner].getText());
		if (value <=512 && value >=0) {
			map16Tiles[tile][1+corner] = (byte) (value & 0x7F);
			map16Tiles[tile][5] &= ~(0x03 << (corner*2)); 
			map16Tiles[tile][5] |= ((value & 0x0180) >> 7-(corner*2)); 
			tileView.repaint();
			levelView.repaint();
		}
	}
	
	public void changeAction(short tile) {
		int value = Integer.parseInt(aInput.getText());
		if (value <=255 && value >=0) {
			map16Tiles[tile][0] = (byte) value;
			tileView.repaint();
		}
	}
	
	public void changePage(int change) {
		if (offset+change >= 0 && ((offset+change)*64)+63 <= 256*16)
		offset+=change;
		tileView.repaint();
	}
	//Action Classes
	
	class SelectAction extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			selectTile(e);
        }
	}
	
	class ChangeButtonAction implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			String buttonPressed = event.getActionCommand();
			if (buttonPressed.equals("cgraphic")) for (int i=0; i<4; i++) changeTile(selectedTile,i);
			else if (buttonPressed.equals("caction")) changeAction(selectedTile);
		}
	}
	
	class ChangePageAction implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			changePage(Integer.parseInt(event.getActionCommand()));
		}
	}
	
	public class layer1Panel extends JPanel {
		private static final long serialVersionUID = 1L;
		
		public layer1Panel() {
			addMouseListener(new SelectAction());
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			int levelHeight = 8;
			int levelWidth = 8;
			int inx;
			for (int i=0; i<levelWidth*levelHeight; i++) {
				inx = i+(offset*64);
				g.drawImage(graphics[getTileGraphic(inx,0)][getTile(inx,0)], (i%levelWidth)*16, (int)Math.floor(i/levelWidth)*16, this);
				g.drawImage(graphics[getTileGraphic(inx,1)][getTile(inx,1)], (i%levelWidth)*16+8, (int)Math.floor(i/levelWidth)*16, this);
				g.drawImage(graphics[getTileGraphic(inx,2)][getTile(inx,2)], (i%levelWidth)*16, (int)Math.floor(i/levelWidth)*16+8, this);
				g.drawImage(graphics[getTileGraphic(inx,3)][getTile(inx,3)], (i%levelWidth)*16+8, (int)Math.floor(i/levelWidth)*16+8, this);
			}
			
			for (int k=0; k<4; k++) {
				for (int i=0; i<8; i++) {
					for (int j=0; j<16; j++) {
						g.drawImage(graphics[k][(i*16)+j], 140+(j*8), (i*8)+(k*64), this);
					}
				}
			}
			
			g.setColor(new Color(255,0,0));
			for (int i=0; i<4; i++) {
				inx = getTile(selectedTile, i) +(getTileGraphic(selectedTile, i)<<7);
				g.drawRect(140+((inx & 0x0F)*8), (inx>>4)*8, 8, 8);
			}
			g.setColor(new Color(0,0,0));
			g.drawString(("Page: "+offset), 10, 150);
			g.drawString(("Tile Selected: "+selectedTile), 10, 170);
			g.drawRect(0, 0, levelWidth*16, levelHeight*16);
			if (cursorPage == offset) g.drawRect(x*16, y*16, 16, 16);
		}
	}
}
