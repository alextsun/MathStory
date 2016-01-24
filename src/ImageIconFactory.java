//Alex Tsun
//5/24/13
//ImageIconFactory.java
//This class returns the image needed.  The caller
//will provide the name of the image, and this class
//adds a prefix to it, and returns the correct image
//On a different computer, PREFIX MUST BE CHANGED TO MAKE THE GAME WORK

import java.awt.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public class ImageIconFactory 
{
	private static String addPrefix(String name) //prefix needed for my computer
	//on other computers, change the prefix as needed
	{
		return ("src/images/"+name);
	}
	
	public static ImageIcon getBoyIcon() //returns the image of the boy
	{
		ImageIcon boy = new ImageIcon(addPrefix("boy.gif")); //add a prefix to it and return the image
		return boy;
	}
	
	public static ImageIcon getGirlIcon() //returns the image of the girl
	{
		ImageIcon girl = new ImageIcon(addPrefix("girl.gif")); //add a prefix to it and return image
		return girl;
	}
	
	public static ImageIcon getClassIcon(boolean isBoy, boolean isClass1) //get image of class
	{
		ImageIcon classImage; //the imageicon to be returned
		String gender; //the gender
		int job; //the job/class
		
		if(isBoy) //if player is a boy
			gender = "boy"; //set gender
		else
			gender = "girl";
		
		if(isClass1) //if player is class 1
			job = 1; //set job/class
		else 
			job = 2;
		
		//set image based on gender/class
		classImage= new ImageIcon(addPrefix("class"+job+gender+"leftstand.gif"));
		
		return classImage;
	}
	
	public static Image getImage(String name) //returns any image with the provided name
    {
		String imageName = ImageIconFactory.addPrefix(name); //add prefix to the name
		Image image = null;
		
        try
        {
           image = ImageIO.read(new File(imageName)); //try reading the image with the name
        }
        catch (IOException e)
        {
        	//if no image has that name, print error/exit
            System.out.println("ERROR: "+name+" IMAGE NOT FOUND");
            System.exit(1);
        }
        return image; //returns the image
    }
	
	public static Image getBackgroundImage() //returns background
    {
		return getImage("dojo.jpg"); //gets the image named "dojo.jpg"
    }
	
	public static Image getPSImage(boolean isBoy, boolean isClass1, char lastKey) //get player standing image
	{
		String imageName = null; //name of image
		String gen = null; //gender
		String dir = null; //direction facing
		String job = null; //class
		
		//sets image name based on player info
		if(isBoy) 
			gen = "boy";
		else
			gen = "girl";
		if(isClass1)
			job = "class1";
		else
			job = "class2";
		if(lastKey=='a')
			dir = "left";
		else if(lastKey=='d')
			dir = "right";
		
		imageName = job+gen+dir+"stand.gif"; //name of the image
		
		return getImage(imageName); //returns image
	}
	
	
	public static Image getMonsterImage(int imageNum, boolean left) //gets monster image based on stage/direction
    {
		String imageName; //name of image
		if(left) //if facing left
			imageName = "monster"+imageNum+"left.png"; //returns left-facing monster
		else
			imageName = "monster"+imageNum+"right.png"; //returns right-facing monster
		return getImage(imageName); //returns image
    }
	
	public static Image getPortalImage() //gets image of portal
    {
		String imageName = "portal.gif"; //name of the image
		return getImage(imageName); //returns image
    }
	
	public static Image getAtkImage(int atkNum, boolean isBoy, boolean isClass1, char orient, int frame)
	//get the appropriate attack image based on which attack, gender, class, direction, and frame of the attack
    {
		String imageName = null; //name of image
		String gender = null; //gender of player
		String class1 = null; //class of player
		String orientation = null; //direction of player
		
		//sets gender, class, direction based on player info
		if(isBoy)
			gender = "boy";
		else
			gender = "girl";
		if(isClass1)
			class1 = "c1";
		else
			class1 = "c2";
		if(orient=='a')
			orientation = "left";
		else if(orient=='d')
			orientation = "right";
		
		if(isClass1) //if it is class 1 (warrior)
		{
			imageName = gender+class1+"atk"+atkNum+orientation+"f"+frame+".gif"; //this is the image
		}
		else //if it is class 2 (magician)
		{
			if(atkNum==1||atkNum==2) //these attacks don't care about orientation
				imageName = class1+"atk"+atkNum+"f"+frame+".gif";
			else //these attacks do care about orientation
				imageName = class1+"atk"+atkNum+orientation+"f"+frame+".gif";
		}
		return getImage(imageName); //returns the correct image
    }
}
