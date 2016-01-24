//Alex Tsun
//5/24/13
//Game.java
//This program runs my final game project, which is named
//MathStory.  A player chooses gender/class and attacks 
//monsters to finish 5 stages.  To attack successfully, he/she
//needs to answer a math question correctly

//necessary imports
import javax.swing.*;
import java.io.*;
import java.util.*;

public class Game 
{
	private JFrame frame; //the jframe object
	private MainPanel mainPanel; //contains all of the panels with cardlayout
	
	static final int MAXHOF = 100; //max number of people in hall of fame
	static final int MAXHP = 9999; //maximum player hp
	static final int MAXMONSTERHP = 2500; //maximum monster hp
	static final int PLAYERWIDTH = 105; //width of the player
	static final int CLOSEDIST = 30; //what is considered close distance to monster
	static final int MAXSAVEDPLAYERS = 20; //max number of saved players
	static final String HALLOFFAME_HOF = "halloffame.hof"; //the file with hall of famers

	private int currentHP, currentStage, currentMonsterHP; //current playerHP, stage, monsterHp
	private int playerX, playerY; //player's x and y coordinates (top left)
	private int monsterX, monsterY; //monster's x and y coordinates (top left)
	private int jumpStep = 0; //which step of the jump they are on
	private int numberRange; //range of numbers that can be used when asking math questions
	private String playerName; //player's name
	private boolean isBoy, isClass1; //player's gender and class
	private char lastKeyPressed; //facing left/right (use 'a' and 'd' to move)
	private boolean monsterFaceLeft; //which direction the monster is facing
	private boolean isCritical; //whether the attack is critical or not (2x dmg)
	private boolean alwaysCritical; //cheat, makes all attacks critical for that stage

	public Game() //constructor
	{
		reset(); //reset the game (all player information)
		init(); //initialize the coordinates, monsterHP, direction of monster
	}
	
	public static void main (String[] args)
	{
		Game g = new Game(); //the game object
		g.run(); //run the game
	}
	
	public void run()
	{
		frame = new JFrame("MathStory"); //new jframe with game title
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //stop program when exit
		frame.setSize(820, 660); //size of jframe
		//frame.setLocation(0, 0); //location of jframe
		frame.setResizable(false); //cannot resize window
		mainPanel = new MainPanel(this); //new mainPanel object (has cardlayout)
		frame.add(mainPanel); //add mainpanel to frame
		frame.setVisible(true); //make it visible
	}

	void reset() //reset the game (player information)
	{
		currentHP = MAXHP; //full hp when start
		playerName = ""; //no name yet
		isBoy = true; //default is boy
		isClass1 = true; //default is class 1
		currentStage = 1; //start at stage 1
		numberRange = 25; //easy level questions
	}

	void init() //initialize the coordinates, monsterHP, direction of monster
	{
		playerX = 100; //starting player x coordinate
		playerY = 480; //starting player y coordinate
		monsterX = 520; //starting monster x coordinate
		monsterY = 260; //starting monster y coordinate
		currentMonsterHP = currentStage*MAXMONSTERHP; //hp of the monster depending on stage
		lastKeyPressed = 'd'; //facing right at start
		monsterFaceLeft = true; //monster faces left at start
		isCritical = false; //not critical hit (set later)
		alwaysCritical = false; //it isn't activated until they press a button
	}

	void restart() //restart the game (when die)
	{
		setCurrentHP(MAXHP); //full health 
		setStage(1); //start at level 1
		init(); //initialize everything
		save(); //save the game
		showIntroPanel(); //show the intro screen
	}
	
	public void resetMHPBar() //resets the monster hp bar in battle panel
	{
		mainPanel.resetMHPBar(); //go through mainpanel to get to battlepanel
	}
	
	public void setAlwaysCritical(boolean b) //makes every attack critical
	{
		alwaysCritical = b;
	}
	
	public boolean getAlwaysCritical() //returns if attacks should be critical
	{
		return alwaysCritical;
	}

	public boolean getIsCritical() //returns if the hit is critical
	{
		return isCritical;
	}

	public void setIsCritical(boolean crit) //set whether or not hit is critical
	{
		isCritical = crit;
	}

	public void updateGender() //updates the gender of the player
	{
		mainPanel.updateGender();
	}

	public int getNumberRange() //returns the range of numbers allowed for math questions
	{
		return numberRange;
	}

	public void setNumberRange(int diff) //set number range
	{
		if(diff==0) //easy difficulty 
			numberRange = 25; //numbers from 1-25
		else if(diff==1) //medium difficulty
			numberRange = 150; //numbers from 1-50
		else if(diff==2) //hard difficulty
			numberRange = 500; //numbers from 1-100
		else if(diff==3) //impossible difficulty
			numberRange = 5000; //numbers from 1-500
		else if(diff==4) //cheats 
			numberRange = 0; //numbers from 1-1
		else if(diff==5) //cheats
			numberRange = 10000; //numbers from 1-1000
	}
	
	public boolean isCloseToPotion(int potionX) 
	//determines if player is close enough to potion to use it
	{
		int playerRight = playerX+PLAYERWIDTH; //right x-coord of the player
		int potionRight = potionX+50; //right x-coord of the potion
		int dist; //distance from the player to the potion

		if(potionX>playerRight) //player on left
			dist = potionX-playerRight; //set distance accordingly
		else if(playerX>potionRight) //player on right
			dist = playerX-potionRight; //set distance accordingly
		else //on top of each other
			dist = 0; //close enough to pick up

		if(dist<3) //if they are close enough to pick up
			return true;

		return false; //too far away
	}

	public boolean isClose(int monsterWidth) //determines if player/monster are close
	{	
		int playerRight = playerX+PLAYERWIDTH; //player right x-coord
		int monsterRight = monsterX+monsterWidth; //monster right x-coord
		int dist;  //distance between player and monster

		if(monsterX>playerRight) //player on left
			dist = monsterX-playerRight; //set distance accordingly
		else if(playerX>monsterRight) //player on right
			dist = playerX-monsterRight; //set distance accordingly
		else //on top of each other
			dist = 0; //close

		int yDist = monsterY-playerY; //for cheating
		
		if(dist<CLOSEDIST && yDist<0) //if they are close and player is not above monster
			return true; //close

		return false; //not close
	}
	
	public boolean isValidAttack(int monsterWidth) //will the attack do damage to monster
	{
		if(isLeftOfMonster(monsterWidth)&&lastKeyPressed=='d') //if facing the monster
			return true; //do damage
		else if(isRightOfMonster()&&lastKeyPressed=='a') //if facing the monster
			return true; //do damage
		else
			return false; //will show attack animation, but no damage will be done
	}
	
	public boolean isLeftOfMonster(int monsterWidth) //is player on the left of monster
	{
		int monsterRight = monsterX+monsterWidth; //monster right x-coord
		if(playerX>monsterRight) //player on right
			return false; //not on the left
		else 
			return true; //on the left or on top
	}
	
	public boolean isRightOfMonster() //is player on right of monster
	{
		int playerRight = playerX+PLAYERWIDTH; //player right x-coord
		if(monsterX>playerRight) //player
			return false; //not on the right
		else 
			return true; //on the right or on top
	}

	public boolean getMonsterDirection() //returns direction of monster
	{
		return monsterFaceLeft;
	}

	public void setMonsterDirection(boolean dir) 
	//set direction of monster according to last movement
	{
		monsterFaceLeft = dir;
	}

	public void nextStage() //go to the next stage
	{
		currentStage++; //next stage
		init(); //reset location of player/monster, max monster hp
	}

	public void decrementMonsterHP(int dec) //monster takes damage
	{
		currentMonsterHP-=dec; //subtract how much he took
		if(currentMonsterHP<0) //if hp is negative
			currentMonsterHP = 0; //set hp to 0
	}

	public int getCurrentMonsterHP() //return current monster's hp
	{
		return currentMonsterHP;
	}

	public int getMaxMonsterHP() //return maximum monster hp
	{
		return currentStage*MAXMONSTERHP; 
		//2500*stage is the hp of any monster at a given stage
	}

	public void setPlayerName(String name) //sets the player name
	{
		playerName = name; 
		mainPanel.updateName(); //updates the name
	}

	public boolean getClass1() //returns the player's class
	{
		return isClass1;
	}

	public void setClass1(boolean cl) //sets the player's class
	{
		isClass1 = cl;
	}

	public String getPlayerName() //return player name
	{
		return playerName;
	}

	public void setIsBoy(boolean gender) //set the gender of player
	{
		isBoy = gender;
	}

	public void showDifficultyPanel() //show the difficulty panel
	{
		mainPanel.showDifficultyPanel();
	}

	public void showClassPanel() //show the panel where they choose class
	{
		mainPanel.showClassPanel();
	}

	public void showGenderPanel() //show panel where they choose gender
	{
		mainPanel.showGenderPanel();
	}

	public void showActionPanel() //show the panel where the action happens
	{
		mainPanel.showActionPanel();
	}

	public void readFile(File player) //read the player file to resume game
	{
		reset(); //resets the game before setting the values of the variables

		String fileName = player.getName(); //file is saved as player.txt
		playerName = fileName.substring(0, fileName.length()-4); //remove the .txt part
		Scanner input = null; //scanner to read file
		try
		{
			input = new Scanner (player); //reads from "players.txt" instead of keyboard
		}
		catch (FileNotFoundException e)//catches error, prevents program from crashing or prevents compiler error
		{
			System.err.println("ERROR: Cannot open file"); //prints error message
			System.exit(1); //exits system
		}
		//read from a saved file (resuming the game)
		isBoy = readBoolean(input); //reads and sets the gender 
		isClass1 = readBoolean(input); //reads and sets the class
		playerX = readInt(input); //reads and sets the last xcoordinate of player
		playerY = readInt(input); //reads and sets the last ycoordinate of player
		monsterX = readInt(input); //reads and sets the last xcoordinate of monster
		monsterY = readInt(input); //reads and sets the last ycoordinate of monster
		currentStage = readInt(input); //reads and sets the current stage
		currentHP = readInt(input); //reads and sets the player hp
		currentMonsterHP = readInt(input); //reads and sets the monster hp
		numberRange = readInt(input); //reads and sets the number range
		

		input.close(); //closes file
	}

	public void addToHallOfFame(String name) //adds the player to hall of fame (after they win)
	{
		String fileName = HALLOFFAME_HOF; //name of the file to read
		File file = new File(PathFactory.addPrefix(fileName)); //add prefix to it (for use on my computer)

		try
		{
			if(!file.exists()) //if the file does not exist
				file.createNewFile(); //creat it

			FileWriter fileWriter = new FileWriter(file.getName(),true); //name of file, adding to end of file	
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter); //buffered writer needed
			PrintWriter pWriter = new PrintWriter(bufferedWriter); //the printwriter to write to file 

			pWriter.println(name); //add name to the file
			pWriter.close(); //close the file
		}
		catch(IOException e)
		{
			System.err.println(e); //print out the exception
			System.exit(1); //stop the program
		}
		deleteFile(); //delete the player file (can't resume a game they finished)
	}
	
	public void deleteFile() //delete the player file
	{
		File file = PathFactory.getFile(playerName+".txt"); //name of the file
		if(file.exists()) //if it exists
			file.delete(); //delete it
	}

	public String[] readHallOfFame() //read the hall of fame, return a String[] of names
	{
		File file = new File(HALLOFFAME_HOF); //file being read
		Scanner input = null; //scanner to read the file
		String[] players = new String[MAXHOF]; //array of players in hall of fame
		String[] lastTenPlayers = new String[10]; //gets the most recent 10 players
		int count = 0; //which player is being read
		
		try
		{
			input = new Scanner (file); //reads from "halloffame.hof" instead of keyboard
		}
		catch (FileNotFoundException e)//catches error, prevents program from crashing or prevents compiler error
		{
			System.err.println("ERROR: Cannot open file halloffame.hof"); //prints error message
			System.exit(1); //exits system
		}
		while(input.hasNext()&&count<MAXHOF) //while there are still names and doesn't exceed 100
		{
			String name = input.nextLine(); //stores the next name
			if(name!="\n") //if it isn't a blank line
			{
				players[count]= name; //store the name into the big array
				count++; //increment count
			}
		}
		count--; //subtract one from count at the end (get rid of extra increment that is unnecessary)
		for(int i = 0; i < 10; i++) //stores the last 10 players in reverse order into lastTenPlayers[]
		{
			if(count>=i) //if there are more than 10 players in hall of fame (otherwise outofbounds exception)
				lastTenPlayers[i] = players[count-i];
		}
		return lastTenPlayers; //return the names of last 10 players in hall of fame
	}

	public boolean readBoolean(Scanner input) //read a line and convert to boolean
	{
		boolean bValue = true; //default value
		if(input.hasNext()) //if more values, keep going
		{
			String value = input.nextLine(); //read what is on the line,should be either true or false
			bValue = Boolean.parseBoolean(value); //convert String "true" or "false" to boolean
		}
		return bValue; //returns the converted boolean
	}

	public int readInt(Scanner input) //read a line and convert to int
	{
		int value = 0; //default
		if(input.hasNext()) //if more integers, keep going
		{
			value = input.nextInt(); //read what is on next line, should be integer
		}
		return value; //returns the int read
	}

	public void save() //save the game
	{
		String fileName = playerName + ".txt"; //new text file with their name on it
		File playerFile = new File(PathFactory.addPrefix(fileName)); //add prefix (non-linux computers)
		PrintWriter pw = null; //printwriter that writes to their file
		
		try
		{
			pw = new PrintWriter(playerFile); //write to playerFile
		}
		catch(IOException e)
		{
			System.err.println(e); //print the exception
			System.exit(1); //stop program
		}
		
		//stores important game information
		pw.println(isBoy);
		pw.println(isClass1);
		pw.println(playerX);
		pw.println(playerY);
		pw.println(monsterX);
		pw.println(monsterY);
		pw.println(currentStage);
		pw.println(currentHP);
		pw.println(currentMonsterHP);
		pw.println(numberRange);
		
		pw.close(); //close the file
	}

	public int getCurrentHP() //get the player's current hp
	{
		return currentHP; 
	}

	public void setCurrentHP(int hp) //set the player hp
	{
		if(hp>MAXHP)  //if it is more than their hp
			hp = MAXHP; //cap it
		currentHP = hp; //currentHP is the modified hp
		if(currentHP<0) //if it is less than 0
			currentHP = 0; //it is 0
	}

	public int getPlayerX() //returns player xcoordinate
	{
		return playerX;
	}

	public int getPlayerY() //returns player ycoordinate
	{
		return playerY;
	}

	public void moveLeft(boolean left) //move left or right
	{
		if(left) //if move to left
		{
			playerX-=10; //move left 10 pixels
			lastKeyPressed = 'a'; //last key pressed was an 'a'
		}
		else //if move to right
		{
			playerX+=10; //move right 10 pixels
			lastKeyPressed = 'd'; //last key pressed was a 'd'
		}
	}

	public void moveUp(boolean up) //move up or down
	{
		if(up) //if move up
			playerY-=10; //move up 10 pixels
		else //if move down
			playerY+=10; //move down 10 pixels
	}

	public void jumpLeft(boolean left)
	{
		int dx, dy; //change in x and y
		dx = dy = 6; //it is 6 pixels

		if(left) //jumping left
		{
			if(jumpStep>9) //after the top of jump
			{
				playerX-=dx; //move left 6
				playerY+=dy; //move down 6
			}
			else //before top of jump
			{
				playerX-=dx; //move left 6
				playerY-=dy; //move up 6
			}
			lastKeyPressed = 'a'; //face left
		}
		else //same thing, but facing right
		{
			if(jumpStep>9)
			{
				playerX+=dx;
				playerY+=dy;
			}
			else
			{
				playerX+=dx;
				playerY-=dy;
			}
			lastKeyPressed = 'd'; //face right
		}
	}

	public void moveMonsterLeft(boolean left) //move monster left/right
	{
		if(left) //move left
			monsterX-=4;
		else //move right
			monsterX+=4;
	}

	public int getMonsterX() //get monster x coordinate
	{
		return monsterX;
	}

	public int getMonsterY() //get monster y coordinate
	{
		return monsterY;
	}

	public void showIntroPanel() //switch to intropanel
	{
		mainPanel.showIntroPanel();
	}

	public int getStage() //returns what stage player is on
	{
		return currentStage;
	}

	public void setStage(int st) //set the stage
	{
		currentStage = st;
	}

	public void setMonsterHP(int hp) //set monster hp to integer specified
	{
		currentMonsterHP = hp;
		if(currentMonsterHP<0) //if hp is negative
			currentMonsterHP = 0; //set hp to 0
	}

	public boolean isJumping() //determines if player is jumping and return
	{
		if(jumpStep<20) //before last step
		{
			return true; //still jumping
		}
		else
		{
			jumpStep = 0; //set it to first step for next jump
			return false; //done with jump
		}
	}

	public void nextJumpStep() //after one step, goes to next
	{
		jumpStep++;
	}

	public char getLastKeyPressed() //returns last key pressed (player face left/right)
	{
		return lastKeyPressed;
	}

	public boolean getIsBoy() //returns gender of player
	{
		return isBoy;
	}

	public boolean getIsClass1() //returns which class the player is
	{
		return isClass1;
	}
}