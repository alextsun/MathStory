//Alex Tsun
//5/24/13
//GenderPanel.java
//This panel shows two buttons and allows
//the player to choose which gender he/she wants to be

//necessary imports
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GenderPanel extends JPanel implements ActionListener
{
	private JButton boyButton, girlButton; //boy button and girl button
	private Game game;
	private JLabel chooseGender; //instructions
	
	public GenderPanel(Game g)
	{
		game = g;
		
		this.setLayout(new BorderLayout()); //borderlayout
		
		JPanel gPanel = new JPanel();
		
		String name; //name of player
		name = game.getPlayerName(); //get name of player
		chooseGender = new JLabel(""); //empty jlabel
		gPanel.setLayout(new GridLayout(1,2)); //grid layout, one button left, one right
		
		//initialize button, set icon on button, set action command, set back/foreground, add AL, add to panel
		boyButton =  new JButton("Boy");
		boyButton.setIcon(ImageIconFactory.getBoyIcon());
		boyButton.setActionCommand("boy");
		boyButton.setBackground(Color.blue);
		boyButton.setForeground(Color.white);
		boyButton.addActionListener(this);
		gPanel.add(boyButton);
		
		//initialize button, set icon on button, set action command, set back/foreground, add AL, add to panel
		girlButton = new JButton("Girl");
		girlButton.setIcon(ImageIconFactory.getGirlIcon());
		girlButton.setActionCommand("girl");
		girlButton.setBackground(Color.red);
		girlButton.setForeground(Color.white);
		girlButton.addActionListener(this);
		gPanel.add(girlButton);
		
		this.add(chooseGender, BorderLayout.NORTH); //add instructions at top
		this.add(gPanel, BorderLayout.CENTER); //add two buttons in middle
	}

	public void actionPerformed(ActionEvent e)  //when they click a button
	{
		String actionCommand = ((JButton) e.getSource()).getActionCommand(); //figure out which button pressed
		
		//set gender accordingly
        if(actionCommand.equals("boy"))
        	game.setIsBoy(true);
        else
        	game.setIsBoy(false);
        
        game.updateGender(); //update the gender
        game.showClassPanel(); //show the next panel (choose class)
	}
	
	public void updateName() //update the name
	{
		String name;
		name = game.getPlayerName(); //get player name
		chooseGender.setText("Welcome, " + name + ".  Are you a boy or a girl?"); //ask what gender
	}
}