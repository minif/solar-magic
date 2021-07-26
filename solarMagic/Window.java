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
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Window extends JFrame {							//The main window class. 
	private static final long serialVersionUID = 1L;			//So eclipse does not complain
	JPanel buttonBar, footerBar; 								//Declare various UI elements to use
	panel levelView = new panel();
	LevelData currentLevel;
	JLabel message;												//Level editing message (tells what layer is being edited)
	byte selectedLayer = 1;										//Selected layer
	
	int scroll = 0;												//The scroll, or x offset
	
	int selectedSprite = -1;
	int ssOffx, ssOffy; 										//Used for sprite selection and dragging
	
	final String[] Information = {								//About Solar Magic
			"Solar Magic v1.1 (01-2021)",
			"2020-2021, Minif",
			"Compatable Level Versions: 1",
			"Public 2020-2021 Build",
			"Released (07-2021)"
	};
	
	final String[][] menuButtons = {							//A table that houses multiple menu buttons, with action and graphic data.
			//action, image
			{"load","1"},
			{"save","0"},
			{"SPEdit","6"},
			{"FGEdit","3"},
			{"BGEdit","4"},
			{"addSp","7"},
			{"tileSelect","2"},
			{"options","5"},
	};
	
	public Window() {											//Constructor to set up the window
		buttonBar = new JPanel();								//Configure the menu bar
		buttonBar.setLayout(new FlowLayout(FlowLayout.LEFT));	
		
		footerBar = new JPanel();								//Configure the footer bar
		footerBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		//Adds all the menu buttons to the footer
		for (int i=0; i<menuButtons.length; i++) buttonBar.add(createMenuButton(menuButtons[i][0], Integer.parseInt(menuButtons[i][1])));
		
		message = new JLabel("Editing Layer " + selectedLayer);	//Creates the message.
		
		footerBar.add(createButton("scrolll32", "<<<"));		//Add objects to the footer
		footerBar.add(createButton("scrolll4", "<"));
		footerBar.add(createButton("scrollr4", ">"));
		footerBar.add(createButton("scrollr32", ">>>"));
		footerBar.add(message);
		footerBar.add(createButton("about", "About Solar Magic"));
		
		add(buttonBar, BorderLayout.NORTH);						//Add the 3 views to the window
		add(levelView, BorderLayout.CENTER);
		add(footerBar, BorderLayout.SOUTH);
		
		this.setTitle("Solar Magic");							//Configure other window settings
		this.setSize(800,650);
	}
	
	
	//Loading
	
	public JButton createButton(String action, String Text) {	//Generic Button creation method
		JButton button = new JButton(Text);
		ActionListener menuAction = new GenericButtonAction();
		button.setActionCommand(action);
		button.addActionListener(menuAction);
		
		return button;
	}
	
	public JButton createMenuButton(String action, int i) {		//Menu Bar button creation method
		JButton button = new JButton();
		ActionListener menuAction = new LoadButtonAction();
		button.setActionCommand(action);
		button.addActionListener(menuAction);
		
		BufferedImage image;									//Add the image icon, from the icons.png file
		BufferedImage subImage;
		ImageIcon iIcon;
		try {													//Makes sure the image exists, if not then nothing is added.
			image = ImageIO.read(getClass().getClassLoader().getResource("assets/icons.png"));
			subImage = image.getSubimage((i%8)*16, (int)Math.floor(i/8)*16, 16, 16);
			iIcon = new ImageIcon(subImage);
			button.setIcon(iIcon);
		} catch (IOException e) {
			image = null;
		} catch (IllegalArgumentException r) {
			image = null;
		}
		
		return button;											//Return the button.
	}
	
	//Methods
	public void drawTile(MouseEvent e) {						//Method called each time a tile is drawn, with the mouse event associated
		if (currentLevel != null) {								//Make sure the level exists.
			int x= e.getPoint().x/16;							//Get the x and y position of the point
			int y= e.getPoint().y/16;
			
			x+=scroll;											//Add the scroll to the x position
				
			switch (selectedLayer) {							//Draw either the FG or BG tile, depending on the layer selected
			case 1: currentLevel.drawTile(x,y); break;
			case 2: currentLevel.drawBGTile(x,y); break;
			}
			
			levelView.repaint();								//Repaint
		}
	}
	
	public void selectSprite(MouseEvent e) {
		if (currentLevel != null) {								//Make sure the level exists.
			int x= e.getPoint().x/16;							//Get the x and y position of the point
			int y= e.getPoint().y/16;
			
			x+=scroll;											//Based on scroll
			
			Sprite spriteToIdentify;
			short[][] spriteSize;
			
			selectedSprite = -1;
			
			
			selectionLoop:										//Loop through all sprites onscreen to see if mouse is over
			for (int i=currentLevel.sprites.size()-1; i>=0; i--) {
				spriteToIdentify = currentLevel.sprites.get(i);
				spriteSize = spriteToIdentify.getGraphic();
				
				int posX = (spriteToIdentify.xPos);
				int posY = (spriteToIdentify.yPos);				//Also loop through all sprite tiles belonging to sprite
				
				for (int tileX = 0; tileX < spriteSize.length; tileX++) {
					for (int tileY = 0; tileY < spriteSize[0].length; tileY++) {
						int drawX = posX + (tileX/2);
						int drawY = posY - (tileY/2);
						
						if (drawX == x && drawY == y) {
							if (e.getButton() == 3 && spriteToIdentify.spriteType !=95) {
								selectedSprite = -1;
								currentLevel.sprites.remove(i);
								levelView.repaint();
							} else {
								selectedSprite = i;
								ssOffx = (tileX/2);
								ssOffy = (tileY/2);
							}
							break selectionLoop;
						}
					}
				}
			}
		}	
	}
	
	public void updateSpritePos(MouseEvent e) {
		if (currentLevel != null) {								//Make sure the level exists.
			int x= e.getPoint().x/16;							//Get the x and y position of the point
			int y= e.getPoint().y/16;
			x+=scroll;											//Add the scroll to the x position							//Repaint
			if (selectedSprite != -1) {
				Sprite spriteToMove = currentLevel.sprites.get(selectedSprite);
				spriteToMove.xPos = x-ssOffx;
				spriteToMove.yPos = y+ssOffy;
				levelView.repaint();
			}
		}
	}
	
	public void loadLevel() {									//Called when the "Load Level" button is clicked.
		JFileChooser fileC = new JFileChooser();				//Open "Choose File" Prompt
		fileC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fileC.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {			//If approved, load level
			
			if (currentLevel != null) {							//Close level if already opened
				currentLevel.close();
				currentLevel = null;
			}
			
			File file = fileC.getSelectedFile();				//Create new level data
			currentLevel = new LevelData(file.getAbsolutePath(), 105);
			
			scroll=0;											//Reset Scroll
			
			if (currentLevel.valid) {							//Make sure the level loaded. If so, set the level view.
				currentLevel.setLevelView(levelView);
			} else {
				currentLevel = null;
			}
			
			levelView.repaint();
		}
	}
	
	public void saveLevel() {									//Called when the save button is hit
		if (currentLevel != null) {
			currentLevel.saveLevel();
		}
	}
	
	public void showToolbar(int toolbar) {						//Called when a sub-window is needed
		if (currentLevel != null) {
			switch(toolbar) {									//Switch between the two windows (options or tile select)
			case 1:
				currentLevel.selectWindow.setVisible(true); break;
			case 2:
				currentLevel.levelOptions.setVisible(true); break;
			}
		}
	}
	
	public void addSp() {										//Called when a sub-window is needed
		if (currentLevel != null) {
			DialogueMessage prompt = new DialogueMessage();
			prompt.prepareForAddingSprites(scroll, currentLevel.sprites, currentLevel.levelInformation, levelView);
			prompt.showDialogue();
		}
	}
	
	public void aboutView() {
		DialogueMessage prompt = new DialogueMessage();
		for (int i=0; i<Information.length; i++) prompt.addLine(Information[i]);
		prompt.setTitle("About Solar Magic");
		prompt.showDialogue();
	}
	
	public void selectLayer(int layer) {						//Called when changing the layer being edited
		selectedLayer = (byte) layer;
		if (layer==0) message.setText("Editing Sprite Layer ");
		else message.setText("Editing Layer " + selectedLayer);
	}
	
	public void scrollView(int Direction) {						//Called when the view is being scrolled
		if (currentLevel != null) {
			scroll+=Direction;
			if (scroll < 0) scroll = 0;
			else if (scroll >= currentLevel.layer1[0].length*16) scroll = (currentLevel.layer1[0].length*16) -1;
			
			levelView.repaint();
		}
	}
	
	//Action Classes
	
	class LoadButtonAction implements ActionListener {			//Called whenever a menu button is pressed
		public void actionPerformed(ActionEvent event) {
			String buttonPressed = event.getActionCommand();
			if (buttonPressed.equals("load")) loadLevel();
			else if (buttonPressed.equals("save")) saveLevel();
			else if (buttonPressed.equals("tileSelect")) showToolbar(1);
			else if (buttonPressed.equals("FGEdit")) selectLayer(1);
			else if (buttonPressed.equals("BGEdit")) selectLayer(2);
			else if (buttonPressed.equals("SPEdit")) selectLayer(0);
			else if (buttonPressed.equals("options")) showToolbar(2);
			else if (buttonPressed.equals("addSp")) addSp();
		}
	}
	
	class GenericButtonAction implements ActionListener {		//Called whenever a generic button is pressed
		public void actionPerformed(ActionEvent event) {
			String buttonPressed = event.getActionCommand();
			if (buttonPressed.equals("scrolll4")) scrollView(-4);
			else if (buttonPressed.equals("scrollr4")) scrollView(4);
			else if (buttonPressed.equals("scrolll32")) scrollView(-32);
			else if (buttonPressed.equals("scrollr32")) scrollView(32);
			else if (buttonPressed.equals("about")) aboutView();
		}
	}
	
	class MouseMotionAction extends MouseMotionAdapter {		//Called whenever the mouse is dragged
		public void mouseDragged(MouseEvent e) {
			switch (selectedLayer) {
			case 0: updateSpritePos(e); break;
			default: drawTile(e); break;
			}
        }
	}
	
	class MouseAction extends MouseAdapter {					//Called whenever the mouse is clicked
		public void mousePressed(MouseEvent e) {
			switch (selectedLayer) {
			case 0: selectSprite(e); break;
			default: drawTile(e); break;
			}
        }
	}
	
	//Panel
	public class panel extends JPanel {							//The level view panel. 
		private static final long serialVersionUID = 1L;
		
		public panel() {
			addMouseMotionListener(new MouseMotionAction());	//Add listeners for mouse movements
			addMouseListener(new MouseAction());
		}
		
		public void paintComponent(Graphics g) {				//Main paint routine
			super.paintComponent(g);
			
			if (currentLevel != null) {							//Make sure the level exists
				currentLevel.screens = currentLevel.levelInformation[0xA];
				
				int levelHeight = 32;	//Set variables for screen size
				int levelWidth = currentLevel.screens;
				
				int[] inx;
				short[][] gfxToDraw;
				
				Sprite spriteToDraw;
				
				int screenSizeX = this.getSize().width;			//Optimization variable: get screen size to only draw tiles in the window
				
				//Draw layer 2 tiles
				for (int scr=0; scr<(currentLevel.screens); scr++) {
					for (int i=0; i<(16*32); i++) {
						inx = currentLevel.getTileIndex2((int)Math.floor(i/16),(i%16));
						
						int posX = (i%16)*16+(scr*256)-(scroll*16);
						int posY = (int)Math.floor(i/16)*16;
						
						if (posX > -16 && posX < screenSizeX) {
							g.drawImage(currentLevel.gfx[inx[1]][inx[0]], posX, posY , this);
							g.drawImage(currentLevel.gfx[inx[3]][inx[2]], posX+8, posY , this);
							g.drawImage(currentLevel.gfx[inx[5]][inx[4]], posX, posY+8 , this);
							g.drawImage(currentLevel.gfx[inx[7]][inx[6]], posX+8, posY+8 , this);
						}
					}
				}
				
				//Draw layer 1 tiles
				for (int s=0; s<levelWidth; s++) {
					for (int y=0; y<levelHeight; y++) {
						for (int x=0; x<16; x++) {
							inx = currentLevel.getTileIndex(s,y,x);
							
							int posX = (((s*16)+x)-scroll)*16;
							int posY = y*16;
							
							if (posX > -16 && posX < screenSizeX) {
								g.drawImage(currentLevel.gfx[inx[1]][inx[0]], posX, posY , this);
								g.drawImage(currentLevel.gfx[inx[3]][inx[2]], posX+8, posY, this);
								g.drawImage(currentLevel.gfx[inx[5]][inx[4]], posX, posY+8, this);
								g.drawImage(currentLevel.gfx[inx[7]][inx[6]], posX+8, posY+8, this);
							}
						}
					}
				}
				
				//Draw sprites (only one is soapman, but allows room for expansion)
				for (int i=0; i<currentLevel.sprites.size(); i++) {
					spriteToDraw = currentLevel.sprites.get(i);
					gfxToDraw = spriteToDraw.getGraphic();
					
					int sp = spriteToDraw.sp + 0x04;
					
					int posX = (spriteToDraw.xPos - scroll)*16;
					int posY = (spriteToDraw.yPos)*16;
					
					for (int tileX = 0; tileX < gfxToDraw.length; tileX++) {
						for (int tileY = 0; tileY < gfxToDraw[0].length; tileY++) {
							int drawX = posX + (tileX * 8);
							int drawY = posY - (tileY * 8)+8;
							
							g.drawImage(currentLevel.gfx[sp][gfxToDraw[tileX][tileY]], drawX, drawY , this);
						}
					}
				}
				
				g.drawRect(0, 0, ((levelWidth*16)-scroll)*16, (levelHeight)*16);	//Draw boundry around level
			}
		}
	}
}