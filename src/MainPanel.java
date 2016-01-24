//Alex Tsun
//5/24/13
//MainPanel.java
//This panel has a cardlayout and contains all of the
//other panels in it, and switches to the next panel
//when necessary.

//necessary imports
import java.awt.*;
import javax.swing.*;

public class MainPanel extends JPanel
{
	private CardLayout cards; //the cardlayout
	
	//the different panels
	private IntroPanel introPanel; //first panel, has instructions, new game, resume game, hall of fame
	private ClassPanel classPanel; //allows you to choose class
	private GenderPanel genderPanel; //allows you to choose gender
	private ActionPanel actionPanel; //the panel where battling occurs
	private DifficultyPanel difficultyPanel; //allows you to choose difficulty of math questions
	
	public MainPanel(Game g)
	{
		cards = new CardLayout();
		this.setLayout(cards); //set layout to cardlayout

		//pass game to other panels; they need information about player/monster
		introPanel = new IntroPanel(g);
		classPanel = new ClassPanel(g);
		genderPanel = new GenderPanel(g);
		actionPanel = new ActionPanel(g);
		difficultyPanel = new DifficultyPanel(g);
		
		//add panels to cardlayout
		this.add(introPanel, "Intro Panel");
		this.add(difficultyPanel,"Difficulty Panel");
		this.add(genderPanel,"Gender Panel");
		this.add(classPanel,"Class Panel");
		this.add(actionPanel, "Action Panel");
	}
	
	public void resetMHPBar() //resets monster hp bar in battlepanel
	{
		actionPanel.resetMHPBar(); //need to go through actionpanel first
	}
	
	public void updateGender() //update the gender of player
	{
		classPanel.updateIcon(); //shows class icons based on gender
	}

	public void showClassPanel() //switch to class panel
	{
		cards.show(this, "Class Panel");
	}
	
	public void showGenderPanel() //switch to gender panel
	{
		cards.show(this, "Gender Panel");
	}
	
	public void showDifficultyPanel() //switch to difficulty panel
	{
		cards.show(this, "Difficulty Panel");
	}
	
	public void updateName() //update name of player
	{
		genderPanel.updateName(); //update name so it can show player name in panel
	}
	
	public void showActionPanel() //switch to action panel
	{
		cards.show(this, "Action Panel");
		actionPanel.startMonster(); //start the monster
	}
	
	public void showIntroPanel() //switch to intro panel
	{
		cards.show(this, "Intro Panel");
		introPanel.startColorTimer(); //start the changing colors
	}
}