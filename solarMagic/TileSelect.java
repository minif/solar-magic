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
import java.awt.FlowLayout;
import java.awt.Graphics;
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
	JPanel buttonBar, menuBar; 
	JFormattedTextField input;
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
		buttonBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		menuBar = new JPanel();
		menuBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		JButton changeTileButton = new JButton("Change Graphic");
		changeTileButton.addActionListener(new ChangeButtonAction());
		
		input = new JFormattedTextField();
		input.setValue(new Integer(0));		//Require an integer to be entered.
	    input.setColumns(2);				//Sets the preferred size of field.
	    
	    buttonBar.add(input);
	    buttonBar.add(createChangeButton("cgraphic", "Set GFX"));
	    buttonBar.add(createChangeButton("caction", "Set Action"));
	    
	    menuBar.add(createMenuButton("-1", "<"));
	    menuBar.add(createMenuButton("1", ">"));
		
		add(buttonBar, BorderLayout.SOUTH);
		add(tileView, BorderLayout.CENTER);
		add(menuBar, BorderLayout.NORTH);
		
		this.setTitle("Tile Selection");
		this.setSize(250,250);
		
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
	
	public void selectTile(MouseEvent e) {
		x= e.getPoint().x/16;
		y= e.getPoint().y/16;
		
		cursorPage = offset;
		
		if (x<=7 && y<=7) {
			selectedTile = (short) (y*8+x + (offset*64));
			tileView.repaint();
		}
	}
	
	public int getTile(int tile) {
		return (int) tile & 0x3F; 
		//So far, each gfx file is a 128x128 .png image, divided into 16x16 tiles.
		//That means each file is 8 tiles in width and height. Because of that, only
		//64 tiles exist, and so the requested tile on the map16 is anded with 64 (in hex)
		//so that we only use the lower 6 bits.
	}
	
	public int getTileGraphic(int tile) {
		return (int) (tile>>6) & 0x03;
	}
	
	public void changeTile(short tile) {
		int value = Integer.parseInt(input.getText());
		if (value <=255 && value >=0) {
			map16Tiles[tile][1] = (byte) value;
			tileView.repaint();
			levelView.repaint();
		}
	}
	
	public void changeAction(short tile) {
		int value = Integer.parseInt(input.getText());
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
			if (buttonPressed.equals("cgraphic")) changeTile(selectedTile);
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
				inx = (int) map16Tiles[i+(offset*64)][1];
				g.drawImage(graphics[getTileGraphic(inx)][getTile(inx)], (i%levelWidth)*16, (int)Math.floor(i/levelWidth)*16, this);
			}
			
			g.drawString(("Page: "+offset), 140, 10);
			g.drawString(("Graphic: "+(int)(map16Tiles[selectedTile][1] & 0xFF)), 140, 30);
			g.drawString(("Act As: "+(int)(map16Tiles[selectedTile][0] & 0xFF)), 140, 50);
			
			g.drawRect(0, 0, levelWidth*16, levelHeight*16);
			if (cursorPage == offset) g.drawRect(x*16, y*16, 16, 16);
		}
	}
}
