//Alex Tsun
//5/24/13
//PathFactory.java
//This class adds a prefix to files so that
//they can be read on a non-linux computer
//on a different computer, PREFIX NEEDS TO BE CHANGED

import java.io.*;

public class PathFactory 
{
	public static String addPrefix(String name) //for my computer, need this prefix
	{
		return ("C:\\Users\\Alex Tsun\\workspace\\Game\\src\\players\\"+name);
	}
	
	public static File getFile(String name) //returns a file with the correct prefix
	{
		return new File(addPrefix(name));
	}
}
