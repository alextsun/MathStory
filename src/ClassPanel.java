//Alex Tsun
//5/24/13
//ClassPanel.java
//This panel shows two buttons and allows
//the player to choose which class he/she wants to be

//necessary imports
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ClassPanel extends JPanel implements ActionListener
{
	private JButton class1Button, class2Button; //two buttons, one for each class
	private Game game;

	public ClassPanel(Game g)
	{
		game = g;

		this.setLayout(new BorderLayout()); //border layout
		JPanel gPanel = new JPanel();
		JLabel chooseGender = new JLabel("Choose Class"); //instructions for this panel
		gPanel.setLayout(new GridLayout(1,2)); //one button on left, one on right

		class1Button =  new JButton("Warrior"); //name on the button
		class1Button.setActionCommand("class1"); //action command to see if this button is pressed
		class1Button.addActionListener(this); //action listener to respond
		gPanel.add(class1Button); //add it to the left side

		class2Button = new JButton("Magician"); //same as above
		class2Button.setActionCommand("class2");
		class2Button.addActionListener(this);
		gPanel.add(class2Button);
		
		this.add(chooseGender, BorderLayout.NORTH); //add instructions to top
		this.add(gPanel, BorderLayout.CENTER); //buttons in middle
	}
	
	public void updateIcon() //change class icon based on gender chosen in previous panel
	{
		class1Button.setIcon(ImageIconFactory.getClassIcon(game.getIsBoy(),true));  //get class 1 icon, boy or girl
		class2Button.setIcon(ImageIconFactory.getClassIcon(game.getIsBoy(),false)); //get class 2 icon, boy or girl
	}

	public void actionPerformed(ActionEvent e) //when button is pressed
	{
		String actionCommand = ((JButton) e.getSource()).getActionCommand(); //figures out which button is pressed
		//set class based on which button they pressed
		if(actionCommand.equals("class1"))
        	game.setClass1(true);
        else
        	game.setClass1(false);

		game.showActionPanel(); //go to next panel
	}
}