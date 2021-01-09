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

public class Sprite {
	int xPos, yPos, sp, spriteType;
	
	public Sprite(int type, int x, int y) {
		xPos=x;
		yPos=y;
		spriteType = type;
		sp = 0;
	}
	
	public short[][] getGraphic() {
		short[][] returnedGraphics = new short[1][2];
		returnedGraphics[0][0] = 0x01; 	//Bottom Half
		returnedGraphics[0][1] = 0x00;	//Top Half
		
		return returnedGraphics;
	}
	
}
