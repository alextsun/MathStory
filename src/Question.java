//Alex Tsun
//5/24/13
//Question.java
//This class generates a random math question
//with the difficulty range specified by the
//player and a random operation.


public class Question 
{
	private int int1, int2, op, ans; //the two integers, operation, and answer
	private String eqn; //the equation to be printed to player
	private String[] ops = {" + "," - "," x "," % "}; //addition, subtraction, multiplication, modulus
	
	public Question(Game g)
	{
		int range = g.getNumberRange(); //get the number range from player (easy,medium,hard,impossible)
		int1 = (int)(range*Math.random()+1); //generates first random number
		int2 = (int)(range*Math.random()+1); //generates second random number
		op = (int)(4*Math.random()); //generates random sign/operation
		eqn = int1+ops[op]+int2+" = "; //sets the string equation
		if(op==0) //if addition
			ans = int1+int2; //add them
		else if(op==1) //subtraction
			ans = int1-int2; //subtract
		else if(op==2) //multiplication
			ans = int1*int2; //multiply
		else //modulus
			ans = int1%int2; //remainder
		
		//critical hits
		int crit = (int)(100*Math.random()+1); //number between 1-100
		if(crit>80) //20% chance of critical hit
			g.setIsCritical(true); //set next attack to critical (if successful)
	}
	
	public boolean checkAnswer(String resp) //check answer provided with correct answer
	{
		try
		{
			int response = Integer.parseInt(resp); //convert their String response into an int (if possible)
			if(response==ans) //if the answer provided is correct
				return true; 
			else //if it is wrong
				return false;
		}
		catch(NumberFormatException e) //if integer is not entered
		{
			return false; //it is wrong
		}
	}
	
	public String getExpression() //returns the String equation
	{
		return eqn;
	}
}