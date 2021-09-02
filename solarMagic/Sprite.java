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

public class Sprite {	//Sprite object
	int xPos, yPos, sp, spriteType;
	
	public Sprite(int type, int x, int y) {
		xPos=x;
		yPos=y;
		spriteType = type;
		sp = 0;
		switch (type) {
			case 0: sp=0; break;
			case 1: sp=0; break;
			case 95: sp=3; break;
		}
	}
	
	public short[][] getGraphic() {	//Hard coed sprite graphics
		short[][] returnedGraphics = new short[1][1];
		switch (spriteType) {
		case 0:
			returnedGraphics = new short[2][2];
			returnedGraphics[0][0] = 0x16; 	
			returnedGraphics[1][0] = 0x17; 	
			returnedGraphics[0][1] = 0x06; 	
			returnedGraphics[1][1] = 0x07; 	
			break;
		case 1:
			returnedGraphics = new short[2][2];
			returnedGraphics[0][0] = 0x10; 	
			returnedGraphics[1][0] = 0x11; 	
			returnedGraphics[0][1] = 0x00; 	
			returnedGraphics[1][1] = 0x01; 	
			break;
		case 2:
			returnedGraphics = new short[2][2];
			returnedGraphics[0][0] = 0x1A; 	
			returnedGraphics[1][0] = 0x1B; 	
			returnedGraphics[0][1] = 0x0A; 	
			returnedGraphics[1][1] = 0x0B; 
			break;
		case 95:
			returnedGraphics = new short[2][4];
			returnedGraphics[0][0] = 0x14; 	//Bottom Half
			returnedGraphics[1][0] = 0x15; 	//Bottom Half
			returnedGraphics[0][1] = 0x04; 	//Bottom Half
			returnedGraphics[1][1] = 0x05; 	//Bottom Half
			returnedGraphics[0][2] = 0x10;	//Top Half
			returnedGraphics[1][2] = 0x11;	//Top Half
			returnedGraphics[0][3] = 0x00;	//Top Half
			returnedGraphics[1][3] = 0x01;	//Top Half
			break;
		}
		return returnedGraphics;
	}
	
}
