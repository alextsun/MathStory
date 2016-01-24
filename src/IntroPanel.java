//Alex Tsun
//5/24/13
//IntroPanel.java
//This panel is the first to be shown and allows
//player to start a new game, resume a game, read instructions
//or show hall of fame.

//necessary imports
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

public class IntroPanel extends JPanel implements ActionListener
{
	private JButton newGameButton, resumeGameButton, instructionsButton, hallOfFameButton; //button names explain
	private JLabel gameName; //name of game at top
	private Game game;
	private Timer fontColorTimer; //changes the font color
	private FontColorChanger fontColorChanger; //the actionlistener for the timer
	private int colorCount; //which color to move to when timer runs
	private Color[] fontColors = {Color.CYAN, Color.YELLOW, Color.BLUE,  //the font colors
			Color.MAGENTA, Color.RED, Color.ORANGE, Color.PINK, Color.GREEN};

	public IntroPanel(Game g)
	{
		game = g;

		setLayout(null); //null layout

		//start timer, initialize it
		fontColorChanger = new FontColorChanger();
		fontColorTimer = new Timer(100,fontColorChanger);
		fontColorTimer.start();

		setBackground(Color.black); //set background to blackl
		
		//initialize the buttons, set text
		newGameButton = new JButton("New Game");
		resumeGameButton = new JButton("Resume Game");
		instructionsButton = new JButton("Instructions");
		hallOfFameButton = new JButton("Hall of Fame");

		//add actionlisteners to buttons
		newGameButton.addActionListener(this);
		resumeGameButton.addActionListener(this);
		instructionsButton.addActionListener(this);
		hallOfFameButton.addActionListener(this);

		//set action command to figure out which button is pressed later
		newGameButton.setActionCommand("New Game");
		resumeGameButton.setActionCommand("Resume Game");
		instructionsButton.setActionCommand("Instructions");
		hallOfFameButton.setActionCommand("Hall of Fame");

		//set x,y loc, width and height of buttons
		newGameButton.setBounds(150,150,500,100);
		resumeGameButton.setBounds(150,260,500,100);
		instructionsButton.setBounds(150,370,500,100);
		hallOfFameButton.setBounds(150,480,500,100);

		//set text color of buttons
		newGameButton.setForeground(Color.WHITE);
		resumeGameButton.setForeground(Color.WHITE);
		instructionsButton.setForeground(Color.WHITE);
		hallOfFameButton.setForeground(Color.WHITE);
		
		//set background color of buttons
		newGameButton.setBackground(Color.BLUE);
		resumeGameButton.setBackground(Color.RED);
		instructionsButton.setBackground(Color.GREEN);
		hallOfFameButton.setBackground(Color.CYAN);

		//add buttons to panel
		this.add(newGameButton);
		this.add(resumeGameButton);
		this.add(instructionsButton);
		this.add(hallOfFameButton);
	}

	public void startColorTimer() //start color timer if not running
	{
		if(!fontColorTimer.isRunning())
			fontColorTimer.start();
	}

	public void resetGameName() //make a new jlabel every time change color
	{
		gameName = new JLabel("MathStory"); //name of game
		gameName.setFont(new Font("Serif", Font.BOLD, 80)); //font type, size
		gameName.setBounds(206,40,600,100); //x,y loc, width height
		this.add(gameName); //add to panel
	}

	class FontColorChanger implements ActionListener //actionlistener for timer
	{
		public void actionPerformed(ActionEvent e) //called every 100ms by timer
		{
			if(gameName!=null) //if it is still there
			{
				remove(gameName); //remove it 
				gameName = null;
			}
			resetGameName(); //initialize it, with diff color
			colorCount++; //next color
			colorCount%=8; //loop it at the end
			gameName.setForeground(fontColors[colorCount%8]); //new color
		}
	}

	public void actionPerformed(ActionEvent e) //actionlistener for buttons
	{
		String command = e.getActionCommand(); //get which button they pressed
		if(command.equals("New Game")) //if want to start a new game
		{
			boolean goodInput = false; //bad input currently
			while(!goodInput) //while bad input
			{
				String s = (String)JOptionPane.showInputDialog( //ask them for their name
						this, "Please enter your name below:",
						"Game Name", JOptionPane.PLAIN_MESSAGE,
						null,null,"");
				if(s==null) //if they press cancel
				{
					goodInput = true; //it is valid input
				}
				else if (s.trim().length()>0) //if they actually typed something
				{
					game.reset(); //set all of the settings for game
					game.setPlayerName(s); //set the player name
					fontColorTimer.stop(); //stop changing colors
					game.showDifficultyPanel(); //next panel
					goodInput = true; //it is valid input
				}
				else //invalid input
				{
					JOptionPane.showMessageDialog( //print error message and makes them enter again
							this,
							"Sorry, this isn't a valid response.\n"
									+ "Please enter your name."
									,"Name Not Entered",JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		else if(command.equals("Resume Game")) //if resume
		{
			String[] playerNames = listFiles(); //array of player names from files
			String s = (String)JOptionPane.showInputDialog( //lists all the names, asks which one to resume
					this, "Choose a file to resume:",
					"Resume Game", JOptionPane.PLAIN_MESSAGE,
					null, playerNames, "");
			if ((s != null) && (s.length() > 0)) //if it is valid
			{
				game.readFile(PathFactory.getFile(s+".txt")); //set all the settings they had before
				fontColorTimer.stop(); //stop timer
				game.resetMHPBar();
				game.showActionPanel(); //go to where they left off
				return;
			}
		}
		else if(command.equals("Instructions")) //if want to see instructions
		{
			String instructions = "Instructions:\n\nUse 'a' and 'd' to move" + //the instructions
					" left and right, 'q' and 'e' to jump left and right.\nKill" +
					" the boss monsters using one of four " +
					"attacks.\nTo attack successfully, answer a math question correctly.\n" +
					"If you successfully defeat a boss, you will be able to proceed to " +
					"the next stage\nby pressing the 'w' key in the portal.  \nIn " +
					"addition, the boss will drop a potion which will heal you when " +
					"you press 'p' near it.\nAfter clearing a stage, until you go into " +
					"the next portal, buttons will stop functioning.";
			JOptionPane.showMessageDialog(this,instructions); //show instructions in a pop-up dialog box
		}
		else if(command.equals("Hall of Fame")) //if they want to see hall of fame
		{
			String[] names = game.readHallOfFame(); //read hall of fame, store names in array
			String players = "Hall Of Fame\n\n"; //title of what will be displayed
			for(int count = 0; count < names.length; count++) //keep going for each name
			{
				if(names[count]!=null)
				{
					players+=((count+1)+".  "); //add their number to the string ("1. ", "2. ") etc
					players+=(names[count]);  //add the names
					players+="\n"; //add a line space
				}
			}
			JOptionPane.showMessageDialog(this, players); //show players in pop-up dialog box
		}
	}

	public String[] listFiles() //reads each text file and returns an array of their names
	{
		File folder = PathFactory.getFile("");
		String files; //stores the current player name being read
		File[] listOfFiles = folder.listFiles(); //gets all of the files saved (with player info)
		String[] playerNames = new String[game.MAXSAVEDPLAYERS]; //all of the players stored
		
		int count = 0;

		for (int i = 0; i < listOfFiles.length; i++) //reads each file 
		{
			if (listOfFiles[i].isFile())  //if it is a file
			{
				files = listOfFiles[i].getName(); //get the name of the player
				if(files.endsWith(".txt")) //if it is a txt file (players are stored in their own txt file, named "player".txt
				{
					files = files.substring(0, files.length()-4); //get their name, remove the .txt part
					if(count<game.MAXSAVEDPLAYERS) //if how many so far is less than max
					{
						playerNames[count] = files; //store that name
						count++; //increment count
					}
				}
			}
		}
		String[] shortenedList = new String[count]; //same array, but without empty spaces
		for(int i = 0; i < count; i++)
		{
			shortenedList[i] = playerNames[i]; //store in a smaller array, so won't display blank names
		}
		return shortenedList;
	}
}