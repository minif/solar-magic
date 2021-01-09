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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Map16Data {
	boolean valid = true;
	final int map16Size = 256*16;
	String filePath;
	byte[][] Map16 = new byte[map16Size][2];
	byte[] header = {
		0x73, 0x6F, 0x61, 0x70, 0x6D, 0x61, 0x6E, 0x77, 
		0x6F, 0x72, 0x6C, 0x64, 0x6D, 0x31, 0x36, 0x00
	};
		
	
	public Map16Data(String path) {
		filePath = path;
		loadMap16();
	}
	
	public void loadMap16() {
		try {
			@SuppressWarnings("resource")
			FileInputStream sf = new FileInputStream(new File(filePath));
			
			for (int i=0; i<header.length; i++) {
				byte data = (byte) sf.read();
				if (data != header[i]) {
					IllegalArgumentException e = new IllegalArgumentException("Found byte " + data + " instead of" + header[i]); 
					throw e;
				}
			}
			
			for (int i=0; i<map16Size; i++) {
				Map16[i][0] = (byte) sf.read();
				Map16[i][1] = (byte) sf.read();
			}
			sf.close(); 
			
		} catch (FileNotFoundException e) {
			System.out.println("Map 16 does not exist." + filePath);
			valid = false;
		} catch (IOException e) {
			System.out.println("Issue with reading Map 16.");
			valid = false;
		} catch (IllegalArgumentException e) {
			System.out.println("Invalid Map 16.");
			valid = false;
		} 
	}
	
	public void saveMap16() {
		//for (int i=0; i<256; i++) Map16[i][1]= (byte) (i & 0xFF);
		try {
			FileOutputStream fw = new FileOutputStream(filePath);
			
			for (int i=0; i<header.length; i++) fw.write(header[i]);
			
			for (int i=0; i<Map16.length; i++) {
				fw.write(Map16[i][0]);
				fw.write(Map16[i][1]);
			}
			fw.close();
			
			System.out.println("Saved Map 16 to: " + filePath);
			
		} catch (IOException e) {
			System.out.println("Unable to save.");
		}
	}
	
	/**
	 * Tile Format = uuuuuuus GGTTTTTT
	 * 
	 * u = unused
	 * s = Solid Tile (1=yes, 0=no)
	 * G = page for tile
	 * T = tile to use in graphic file
	 */
	
	public int getTile(int tile) {
		return (int) Map16[tile][1] & 0x3F; 
		//So far, each gfx file is a 128x128 .png image, divided into 16x16 tiles.
		//That means each file is 8 tiles in width and height. Because of that, only
		//64 tiles exist, and so the requested tile on the map16 is anded with 64 (in hex)
		//so that we only use the lower 6 bits.
	}
	
	public int getTileGraphic(int tile) {
		return (int) (Map16[tile][1]>>6) & 0x03;
	}
	
	public int getTileProperty(int tile) {
		return (int) (Map16[tile][0]) & 0x01;
	}
	
}