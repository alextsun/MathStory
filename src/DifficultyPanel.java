//Alex Tsun
//5/24/13
//DifficultyPanel.java
//This panel allows the user to choose the
//difficulty of math questions using a JSlider

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;

public class DifficultyPanel extends JPanel implements ActionListener, ChangeListener
{
	private JSlider difficulty; //the slider to choose difficulty
	private JLabel instructions; //instructions on how to choose
	private JButton done; //button to confirm the difficulty
	private JLabel easy, medium, hard, impossible; //levels
	private Game game; //game object
	
	public DifficultyPanel(Game g)
	{
		game = g;
		
		setLayout(null); //null layout
		
		difficulty = new JSlider(JSlider.HORIZONTAL,0,3,0); //4 choices, start at easy
        difficulty.createStandardLabels(1); //create label for each tick mark
        difficulty.setMajorTickSpacing(1); //Turn on labels at major tick marks.
		difficulty.setPaintLabels(true); //Tells if labels are to be painted.
		difficulty.setPaintTicks(true); // Tells if tick marks are to be painted.
		difficulty.setSnapToTicks(true); //snap to one of four ticks/levels
		
		String instruction = "Choose a difficulty level for the math questions";
		//instructions for this panel
		instructions = new JLabel(instruction); //jlabel with these instructions
		
		//jlabels with the difficulty
		easy = new JLabel("Easy");
		medium = new JLabel("Medium");
		hard = new JLabel("Hard");
		impossible = new JLabel("Impossible");
		
		done = new JButton("Done"); //the done button
		done.addActionListener(this); //add action listener to make it work
		
		difficulty.addChangeListener(this); //add change listener to make slider work
		
		//set location/size of the components
		done.setBounds(305,500,200,50);
		difficulty.setBounds(50, 300, 700, 50); 
		instructions.setBounds(280,50,500,50);
		easy.setBounds(45,350,150,50);
		medium.setBounds(265,350,150,50);
		hard.setBounds(500,350,150,50);
		impossible.setBounds(715,350,150,50);
		
		//add components to the panel
		add(difficulty);
		add(done);
		add(instructions);
		add(easy);
		add(medium);
		add(hard);
		add(impossible);
	}
	
	public void stateChanged(ChangeEvent e) //when move the slider
	{
		game.setNumberRange(difficulty.getValue()); //set number range to the value
	}
	
	public void actionPerformed(ActionEvent e) //when they press the button
	{
		game.showGenderPanel(); //go to next panel
	}
}
