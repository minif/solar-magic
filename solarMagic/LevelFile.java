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
import java.io.IOException;

public class LevelFile {										//Level file object for easy access to files
	boolean valid = false;
	String levelPath;
	FileInputStream sf;
	
	public LevelFile(String path) throws FileNotFoundException {//Used when opening
		levelPath = path;
		sf = new FileInputStream(new File(levelPath));
		valid = true;
	}
		
	public byte read() throws IOException {						//Read bytes from file
		return (byte) sf.read();
	}
	
	public void close() throws IOException {					//Close file
		sf.close();
	}
	
	public void checkHeader(byte[] header) throws IOException, IllegalArgumentException {
		for (int i=0; i<header.length; i++) {					//Check level header based on given header
			byte data = (byte) sf.read();
			if (data != header[i]) {
				IllegalArgumentException e = new IllegalArgumentException("Found byte " + data + " instead of" + header[i]); 
				throw e;
			}
		}
	}
}
