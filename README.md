# Solar Magic
  A tool used for the modification of level files for 
  the game Soap Man's World.

### Playing Soap Man's World
Download `test game` and `Soap Mans World.jar`. Open the command line, and type the following:

`java -jar /path/to/Soap\ Mans\ World.jar /path/to/test\ game`

This will launch a new window that should briefly display "Bathroom Co. Presents"

>Notice: Java 1.8 or later must be installed.

>Notice: The console will say "The file cannot be loaded." This issue will be adressed, but for now can be ignored.
#### Controlling the game

Use the arrow keys to move Soapman around.
Use the  `a` and `d` keys to pan the camera.

## Editing Levels
Download `Solar Magic.jar` and double click it to open. 
To open a level folder, Navigate to it by clicking (![rrrr.png](./preview/load.png)), and using the inbuilt file searching feature. `test game` is a valid level and can be used

### Menu Bar

![load.png](./preview/load.png) - Load a level folder

![save.png](./preview/save.png) - Save a level

![ls.png](./preview/ls.png) - Switch to Sprite Layer editing

In this mode, you can move and delete sprites. To move it, hover mouse over sprite and drag it with the left button. To delete a sprite, right click it.

![l1.png](./preview/l1.png) - Switch to Layer 1 editing

![l2.png](./preview/l2.png) - Switch to Layer 2 editing

In these modes, you can paint tiles using the left mouse button. See below on how to select a tile.

![sprite.png](./preview/sprite.png) - Add a sprite

Currently, this opens a window with the prompt to enter a sprite ID. Currently, there are only 2 sprites. New sprites are added in the top left corner of the screen.

![tiles.png](./preview/tiles.png) - Switch and customize tiles

This opens a menu with all of the tiles loaded into the level. It also included a view of all the graphics inserted into the level.
You can change the tile's graphics using the `Set GFX` button. Each number corresponds the graphic of each corner. 
For convenience, a red square highlights the selected graphic for that corner. You can also set an action, which can either be 0 for nothing or 1 for solid.

![options.png](./preview/options.png) - Load level settings

//This loads options that can be changed in the level. This includes the four tile graphics and four sprite graphics.
It also allows you to change the size of the level, determined by the value multiplied by 16.

### Footer Bar

The `<<<`, `<`, `>`, and `>>>` buttons are used to scroll the view. 

## Compiling Notes
This project is reccomended to be compiled with JavaSE-1.8, however JavaSE-1.6 was tested to be fully functional. `solarMagic` and `assets` are
both packages that are to be put inside of a source folder. 
