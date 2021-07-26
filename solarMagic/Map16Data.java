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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Map16Data {										//Map 16 object file (aka the tileset)
	boolean valid = true;
	final int map16Size = 256*16;
	String filePath;
	byte[][] Map16 = new byte[map16Size][6];
	short[][] Map8 = new short[8][16*8];
	byte[] header = {											//Hardcoded header in all map 16 files
		0x73, 0x6F, 0x61, 0x70, 0x6D, 0x61, 0x6E, 0x77, 
		0x6F, 0x72, 0x6C, 0x64, 0x6D, 0x31, 0x36, 0x00
	};
		
	
	public Map16Data(String path) {				
		filePath = path;
		loadMap8();
		loadMap16();
	}
	
	public void loadMap16() {									//Load Map 16 file
		try {
			@SuppressWarnings("resource")
			FileInputStream sf = new FileInputStream(new File(filePath));
			
			for (int i=0; i<header.length; i++) {				//Check header
				byte data = (byte) sf.read();
				if (data != header[i]) {
					IllegalArgumentException e = new IllegalArgumentException("Found byte " + data + " instead of" + header[i]); 
					throw e;
				}
			}
			
			for (int i=0; i<map16Size; i++) {					//Load 6 bytes per map16 tiles
				Map16[i][0] = (byte) sf.read();
				Map16[i][1] = (byte) sf.read();
				Map16[i][2] = (byte) sf.read();
				Map16[i][3] = (byte) sf.read();
				Map16[i][4] = (byte) sf.read();
				Map16[i][5] = (byte) sf.read();
			}
			sf.close(); 
			
		} catch (FileNotFoundException e) {
			DialogueMessage message = new DialogueMessage();
			message.addLine("Map 16 does not exist." + filePath);
			message.showDialogue();
			valid = false;
		} catch (IOException e) {
			DialogueMessage message = new DialogueMessage();
			message.addLine("Issue with reading Map 16.");
			message.addLine(e.getMessage());
			message.showDialogue();
			valid = false;
		} catch (IllegalArgumentException e) {
			DialogueMessage message = new DialogueMessage();
			message.addLine("Invalid Map 16.");
			message.addLine(e.getMessage());
			message.showDialogue();
			valid = false;
		} 
	}
	
	public void loadMap8() {									//Load graphics pointers, which just points to graphics data
		for (short j=0; j<8; j++) {
			for (short i=0; i<16*8; i++) {						//Actually, I think this is unused for now.
				Map8[j][i] = (short)((j<<7) + i);
			}
		}
	}
	public void saveMap16() {									//Save tiles in the same way it is loaded
		//for (int i=0; i<256; i++) Map16[i][1]= (byte) (i & 0xFF);
		try {
			FileOutputStream fw = new FileOutputStream(filePath);
			
			for (int i=0; i<header.length; i++) fw.write(header[i]);
			
			for (int i=0; i<Map16.length; i++) {
				fw.write(Map16[i][0]);
				fw.write(Map16[i][1]);
				fw.write(Map16[i][2]);
				fw.write(Map16[i][3]);
				fw.write(Map16[i][4]);
				fw.write(Map16[i][5]);
			}
			fw.close();
			
			System.out.println("Saved Map 16 to: " + filePath);
			
		} catch (IOException e) {
			DialogueMessage message = new DialogueMessage();
			message.addLine("Unable to save Map 16.");
			message.addLine(e.getMessage());
			message.showDialogue();
		}
	}
	
	/**
	 * Tile Format = uuuuuuus uTTTTTTT uTTTTTTT uTTTTTTT uTTTTTTT gghhiijj
	 * 
	 * u = unused
	 * s = Solid Tile (1=yes, 0=no)
	 * T = tile to use in graphic file
	 * g-h-i-j = Graphics page to use
	 * 
	 * (Note: The first byte actually is not completely unused, if it is equal to 
	 * either 69 or 95, it will play an SFX. This is apart of an easter egg.
	 */
	
	public int getTile(int tile, int corner) {					//Pass through tiles and other properties
		return (int) Map16[tile][1+corner] & 0x7F; 
		//So far, each gfx file is a 64 .png image, divided into 8x8 tiles.
		//That means each file is 16 tiles in width and 8 in height. Because of that, only
		//128 tiles exist, and so the requested tile on the map16 is anded with 128 (in hex)
		//so that we only use the lower 7 bits.
	}
	
	public int getTileGraphic(int tile, int corner) {
		return (int) (Map16[tile][5]>>(corner*2)) & 0x03;
	}
	
	public int getTileProperty(int tile) {
		return (int) (Map16[tile][0]) & 0x01;
	}
	
}