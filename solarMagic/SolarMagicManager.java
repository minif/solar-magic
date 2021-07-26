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

import javax.swing.JFrame;

public class SolarMagicManager {
	public static void main(String[] args) {			//Creates a new window, so that the program opens.
		Window programWindow = new Window();
		programWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		programWindow.setVisible(true);
	}
}
