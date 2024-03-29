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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import solarMagic.Window.panel;

public class LevelData {							//The main level data class
	
	byte solarMagicVersion = 2;						//Check this against level
	
	byte solarMagicLowestCompatableVersion = 1;		//If a major change occurs, this forces older versions to not be compatable.
	
	short[][][][] layer1;							//Declare various variables
	short[][] layer2;
	LinkedList<Sprite> sprites;
	BufferedImage[][] gfx;
	String filePath, levelPath;
	int levelNumber;
	byte screens = 4;
	boolean valid = true;
	
	panel levelView;
	
	TileSelect selectWindow;						//Windows within the program
	Options levelOptions;
	Map16Data map16;
	
	byte[] header = {								//Hard coded header
			0x73, 0x6F, 0x61, 0x70, 0x6D, 0x61, 0x6E, 0x77, 
			0x6F, 0x72, 0x6C, 0x64, 0x6C, 0x76, 0x6C, 0x00
		};
	
	byte[] levelInformation = new byte[16];			//Level header
	
	public LevelData(String path, int level) {		//Sets up level file
		
		filePath = path;
		levelNumber = level;
		levelPath = path + "/level/level" + levelNumber + ".sml";
		
		gfx = new BufferedImage[8][16*8];
		
		loadLevel();								//Load the level file
		if (!valid) return;
		
		map16 = new Map16Data(path + "/gfxdata/map16.m16");
		if (!map16.valid) {
			valid = false; 
			return;
		}
		
		levelOptions = new Options(gfx, levelInformation, filePath, layer1, screens);
		selectWindow = new TileSelect(gfx, map16.Map16);
	}
	
	public void loadLevel() {
		loadLevelFromFile();
	}
	
	public void saveLevel() {
		saveLevelToFile();
		map16.saveMap16();
	}
	
	public void close() {							//Properly close the level file, and associated windows
		selectWindow.setVisible(false);
		selectWindow = null;
		map16 = null;
		levelOptions.setVisible(false);
		levelOptions = null;
	}
	
	private void saveLevelToFile() {				//Save the level to file
		try {
			FileOutputStream fw = new FileOutputStream(levelPath);
			
			levelInformation[0x0B] = (byte) sprites.size();
			
			for (int i=0; i<header.length; i++) fw.write(header[i]);
			for (int i=0; i<levelInformation.length; i++) fw.write(levelInformation[i]);
			
			short s;
			
			for (int scr=0; scr<screens; scr++) {	//Save the layer 1
				for (int i=0; i<layer1[0][0].length; i++) {
					for (int j=0; j<16; j++) {
						s = layer1[0][scr][i][j];
						fw.write((byte) ((s>>8) &0xFF));
						fw.write(s);
					}
				}
			}
			
			for (int i=0; i<layer2.length; i++) {	//Save the layer 2
				for (int j=0; j<layer2[0].length; j++) {
					s = layer2[i][j];
					fw.write((byte) ((s>>8) &0xFF));
					fw.write(s);
				}
			}
			
			for (int i=0; i<sprites.size(); i++) {	//Save the sprites
				Sprite spriteToSave = sprites.get(i);
				int[] spriteBytes = new int[4];
				
				spriteBytes[0] = spriteToSave.spriteType;
				spriteBytes[1] = spriteToSave.xPos>>1;
				spriteBytes[2] = (spriteToSave.xPos & 0x01) <<7 ;
				spriteBytes[2] += spriteToSave.yPos <<2;
				
				for (int b=0; b<spriteBytes.length; b++) fw.write((byte)spriteBytes[b]);
			}
			
			fw.close();
			
			System.out.println("Saved Level " + levelNumber + " to: " + filePath);
			
		} catch (IOException e) {
			DialogueMessage message = new DialogueMessage();
			message.addLine("Unable to save level.");
			message.addLine(e.getMessage());
			message.showDialogue();
		}
	}
	
	private void loadLevelFromFile() {				//Load levels from file
		try {
			LevelFile sf = new LevelFile(levelPath);
			
			sf.checkHeader(header);
			
			for (int i=0; i<header.length; i++) {	//Read the header
				levelInformation[i] = (byte) sf.read();
			}
			
			if (levelInformation[0] > solarMagicVersion) {	//Make sure the editor is not too old.
				IllegalArgumentException e = new IllegalArgumentException("The version for this level (" + 
						levelInformation[0] + ") is greater than this program (" + solarMagicVersion + ")."); 
				throw e; 
			}
			
			if (levelInformation[0] < solarMagicLowestCompatableVersion) {	//Make sure the editor is not too old.
				IllegalArgumentException e = new IllegalArgumentException("The version for this level (" + 
						levelInformation[0] + ") is too old for this program (" + solarMagicVersion + ")."); 
				throw e; 
			}
			
			levelInformation[0] = solarMagicVersion;//If the level version is lower, update it to the current version
			
			screens = levelInformation[0x0A];
			layer1 = new short[1][screens][32][16];
			layer2 = new short[32][16];
			sprites = new LinkedList<Sprite>();
			
			for (byte i = 0; i<8; i++) loadGraphics(i,levelInformation[i+1]);
			
			for (int scr=0; scr<screens; scr++) {	//Load layer 1
				for (int i=0; i<32; i++) {
					for (int j=0; j<16; j++) {
						short high = (short) sf.read();
						layer1[0][scr][i][j] = (short)((high<<8)+(sf.read()&0xFF));
					}
				}
			}
			
			for (int i=0; i<layer2.length; i++) {	//Load layer 2
				for (int j=0; j<layer2[0].length; j++) {
					short high = (short) sf.read();
					layer2[i][j] = (short)((high<<8)+(sf.read()&0xFF));
				}
			}
			
			int[] spriteBytes = new int[4];			//Load sprites
			
			for (int i=0; i<levelInformation[0x0B]; i++) {
				for (int b=0; b<spriteBytes.length; b++) spriteBytes[b]= sf.read() & 0xFF;
				
				int spriteType = spriteBytes[0];
				
				int spriteX = (spriteBytes[1] << 1) + ((spriteBytes[2] & 0x80) >> 7);
				int spriteY = ((spriteBytes[2] & 0x7C) >> 2);
				
				sprites.add(new Sprite(spriteType, spriteX, spriteY));
			}
			
			sf.close(); 
			
		} catch (FileNotFoundException e) {			//Catch invalid files
			DialogueMessage message = new DialogueMessage();
			message.addLine("Level " +levelNumber + " does not exist. ");
			message.addLine(levelPath);
			message.addLine("Tip: Copy and paste existing level file to create level.");
			message.addLine(e.getMessage());
			message.showDialogue();
			valid = false;
		} catch (IOException e) {
			DialogueMessage message = new DialogueMessage();
			message.addLine("Issue with reading level.");
			message.addLine(e.getMessage());
			message.showDialogue();
			valid = false;
		} catch (IllegalArgumentException e) {
			DialogueMessage message = new DialogueMessage();
			message.addLine("Invalid Level.");
			message.addLine(e.getMessage());
			message.showDialogue();
			valid = false;
		} 
	}
	
	public void loadGraphics(int page, int fileID) {//Load graphics in the level
		BufferedImage image;
		try {
			image = ImageIO.read(new File(filePath + "/gfxdata/gfx" + fileID + ".png"));
			for (int i=0; i<8; i++) for (int j=0; j<16; j++) gfx[page][(i*16)+j] = image.getSubimage(j*8, i*8, 8, 8);
		} catch (IOException e) {
			image = null;
			DialogueMessage message = new DialogueMessage();
			message.addLine("Unable to open Graphics File: " + fileID);
			message.showDialogue();
		} catch (IllegalArgumentException r) {
			image = null;
			DialogueMessage message = new DialogueMessage();
			message.addLine("Unable to open Graphics File: " + fileID);
			message.showDialogue();
		}
	}
	
	
	
	public void resetLevel() {						//Test functions for resetting the level
		for (int s=0; s<layer1.length; s++) {
			for (int i=0; i<layer1.length; i++) {
				for (int j=0; j<layer1[0].length; j++) {
					layer1[0][s][i][j] = 0;
				}
			}
		}
	}
	
	public void resetLevel2() {
		for (int i=0; i<layer2.length; i++) {
			for (int j=0; j<layer2[0].length; j++) {
				layer2[i][j] = 0x40;
			}
		}
	}
	
	public void setLevelView(panel view) {			//Pass through the level view up to the subwindows
		levelView = view;
		selectWindow.levelView = levelView;
		levelOptions.levelView = levelView;
	}
	
	public void drawTile(int x, int y) {			//Draw a tile on the FG layer (layer 1)
		if (x<layer1[0].length*16 && y<layer1[0][0].length&&x>=0&&y>=0) layer1[0][(x & 0xFF0)/16][y][x & 0xF] = selectWindow.selectedTile;
	}
	
	public void drawBGTile(int x, int y) {			//Draw a BG tile
		if (x<layer1[0].length*16 && y<layer1[0][0] .length&&x>=0&&y>=0) layer2[y][x%16] = selectWindow.selectedTile;
	}
	
	public int[] getTileIndex(int s, int y, int x) {//Get a tile by coords
		int[] tileToReturn = new int[8];
		
		for (int i=0; i<4; i++) {
			tileToReturn[2*i] = map16.getTile(layer1[0][s][y][x],i);
			tileToReturn[(2*i)+1] = map16.getTileGraphic(layer1[0][s][y][x],i);
		}
		return tileToReturn;
	}
		
	public int[] getTileIndex2(int x, int y) {		//Get a BG tile by coords
		int[] tileToReturn = new int[8];
		for (int i=0; i<4; i++) {
			tileToReturn[2*i] = map16.getTile(layer2[x][y]&0xFF,i);
			tileToReturn[(2*i)+1] = map16.getTileGraphic(layer2[x][y]&0xFF,i);
		}
		return tileToReturn;
	}
	
	public BufferedImage getTile(int page, int tile) {//Get a specific tile graphic
		return gfx[page][tile];
	}
}