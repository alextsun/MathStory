//5/24/13
//ActionPanel.java
//This panel is where the battling occurs, and it contains
//three panels: an attack panel to choose attacks, the battle
//panel where the attacks are shown, and the player info panel,
//where it displays player name, class, and current hp

//necessary imports
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;

public class ActionPanel extends JPanel implements MouseListener
{
	private AttackPanel attackPanel; //the panel at the top to choose attacks
	private PlayerInfoPanel playerInfoPanel; //the panel at the bottom to display player info
	private BattlePanel battlePanel; //the panel in the center where battling occurs
	private Game game; //game object
	private int stage; //stage number
	private String imageName; //name of the image trying to be imported

	public ActionPanel(Game g)
	{
		game = g;
		setLayout(new BorderLayout()); //borderlayout - top is attack, center is battle, bottom is info

		attackPanel = new AttackPanel();
		playerInfoPanel = new PlayerInfoPanel();
		battlePanel = new BattlePanel();
		addMouseListener(this); //add mouse listener to this panel

		playerInfoPanel.setPreferredSize(new Dimension(700,30)); //set size of bottom panel
		
		//add the panels to the appropriate locations
		add(attackPanel, BorderLayout.NORTH);
		add(battlePanel, BorderLayout.CENTER);
		add(playerInfoPanel, BorderLayout.SOUTH);
	}
	
	public void resetMHPBar() //resets monster hp bar
	{
		battlePanel.resetMHPBar(); //call bpanel to reset
	}
	
	public void startMonster() //resumes the monster
	{
		battlePanel.resumeMonster();
	}

	class AttackPanel extends JPanel implements ActionListener //the top panel to choose attacks
	{
		private JButton atk1, atk2, atk3, atk4; //the 4 attack buttons
		private JButton save, exitGame;  //save button and exit button
		private int dmgToMonster; //damage player does to monster

		public AttackPanel()
		{
			setBackground(Color.CYAN); //background color of the panel
			
			//initializing the buttons
			atk1 = new JButton("Attack 1");
			atk2 = new JButton("Attack 2");
			atk3 = new JButton("Attack 3");
			atk4 = new JButton("Attack 4");
			save = new JButton("Save");
			exitGame = new JButton("X");

			//adding action listener to each
			save.addActionListener(this);
			exitGame.addActionListener(this);
			atk1.addActionListener(this);
			atk2.addActionListener(this);
			atk3.addActionListener(this);
			atk4.addActionListener(this);

			//set action commands for each to know which button was clicked
			save.setActionCommand("save");
			exitGame.setActionCommand("exit");
			atk1.setActionCommand("atk1");
			atk2.setActionCommand("atk2");
			atk3.setActionCommand("atk3");
			atk4.setActionCommand("atk4");

			//set size of each button
			atk1.setPreferredSize(new Dimension(140,35));
			atk2.setPreferredSize(new Dimension(140,35));
			atk3.setPreferredSize(new Dimension(140,35));
			atk4.setPreferredSize(new Dimension(140,35));
			save.setPreferredSize(new Dimension(140,35));
			exitGame.setPreferredSize(new Dimension(42,35));
			
			exitGame.setBackground(Color.red); //background color of exit is red
			
			//add buttons to the panel
			add(atk1);
			add(atk2);
			add(atk3);
			add(atk4);
			add(save);
			add(exitGame);
		}

		public void actionPerformed(ActionEvent e) //when a button is pressed
		{
			String command = e.getActionCommand(); //figure out which button was pressed
			if(command.equals("save")) //save game
			{
				game.save();
			}
			else if(command.equals("exit")) //exits game
			{
				battlePanel.pauseMonster(); //pauses the monster
				int n = JOptionPane.showConfirmDialog( //asks them if they want to save
						getParent(), "Game will exit. " +
								"\nWould you like to save the game?",
								"Save?",
								JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.YES_OPTION) //if so
				{
					game.save(); //save the game
					System.exit(0); //exit
				}
				else if (n == JOptionPane.NO_OPTION) //if not
				{
					System.exit(0); //exit
				}
				else //otherwise resume game
				{
					battlePanel.resumeMonster(); //start monster again
					battlePanel.requestFocus(); //request focus to battle panel
				}
			}
			else //if one of the attacks was chosen
			{
				//System.out.println("button pressed");
				if(game.getCurrentMonsterHP()>0) //if monster is still alive
				{
					Question q = new Question(game); //generate a new random question
					String s = (String)JOptionPane.showInputDialog( //prints out question/box to type answer
							getParent(),
							"Evaluate the expression:\n"
									+ q.getExpression(),
									"Math Question",
									JOptionPane.PLAIN_MESSAGE,
									null, null,"");
					
					if ((s != null) && (s.length() > 0) && (q.checkAnswer(s))) //if answer is correct
					{
						performAttack(command); //perform attack
					}
					else //if answer is wrong
					{
						int rand = (int)(400*Math.random()+101); //random number to inflict to player
						game.setCurrentHP(game.getCurrentHP()-rand); //inflict damage to player
					}
				}
				battlePanel.requestFocus(); //request focus to battlepanel
			}
		}

		public void performAttack(String command) //performs the attack
		{
			//which attack to use based on what was chosen
			if(command=="atk1")
				battlePanel.setAttack(1);
			else if(command=="atk2")
				battlePanel.setAttack(2);
			else if(command=="atk3")
				battlePanel.setAttack(3);
			else
				battlePanel.setAttack(4);

			if((game.isClose(battlePanel.getMonsterWidth())&&game.getIsClass1())||(!game.getIsClass1()))
			//if they are class 1 (warrior), they need to be close to the monster; otherwise they can attack from afar
			{
				if(game.isValidAttack(battlePanel.getMonsterWidth())) 
				//determines if attack should be performed (must be facing monster)
				{
					if(!game.getIsClass1()) //if magician, do this random damage
						dmgToMonster = (int)(Math.random()*750+750);
					else //if warrior, do this random damage (more because must be close)
						dmgToMonster = (int)(Math.random()*1000+1000);
					if(game.getIsCritical()) //if the attack is supposed to be critical (random)
						dmgToMonster*=2; //do twice as much damage
					game.decrementMonsterHP(dmgToMonster); //inflict damage to monster
					if(game.getCurrentMonsterHP()==0) //if the monster is dead
						battlePanel.setPotionVisible(true); //show a potion that player can use
				}
			}
		}

		public int getDmgToMonster() //returns how much damage is inflicted to monster
		{
			return dmgToMonster;
		}
	}

	class PlayerInfoPanel extends JPanel implements ChangeListener
	//shows hp bar of player, name, and class
	{
		private JProgressBar hpBar; //hp bar of player
		private JLabel name; //name of player
		private JLabel job; //class/job of player

		public PlayerInfoPanel()
		{
			setLayout(new FlowLayout()); //flow layout
			setBackground(Color.black); //black background
			setForeground(Color.white); //white text

			name = new JLabel(""); //name not yet set at start of program
			job = new JLabel(""); //job/class not yet set at start of program

			hpBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, Game.MAXHP); //horizontal bar showing hp
			hpBar.setBackground(Color.red); //set color when empty to red
			hpBar.setForeground(Color.green); //set color when full to green
			//hpBar.addChangeListener(this); //need changelistener

			//set size of the components
			name.setPreferredSize(new Dimension(160,25));
			job.setPreferredSize(new Dimension(110,25));
			hpBar.setPreferredSize(new Dimension(520,25));
			
			//set text color
			name.setForeground(Color.white);  
			job.setForeground(Color.white);
			
			hpBar.setStringPainted(true);  //shows numbers in hp bar

			//add the components
			add(name);
			add(job);
			add(hpBar);
		}

		public void stateChanged(ChangeEvent e)
		{

		}

		public void paintComponent(Graphics g) //draws
		{
			super.paintComponent(g);

			name.setText("  Player: " + game.getPlayerName()); //puts player name in the jlabel

			String jobName; //name of the job/class
			if(game.getClass1()) //if class 1
				jobName = "Warrior"; //warrior
			else
				jobName = "Magician"; //magician

			job.setText("Class: " + jobName); //puts job name in the jlabel
			hpBar.setValue(game.getCurrentHP()); //change the hp after taking damage
		}
	}

	class BattlePanel extends JPanel implements KeyListener, ActionListener, ChangeListener
	{
		private Timer monsterMoveTimer; //timer that moves monster
		private Timer playerAttackTimer; //timer than shows player attack animation
		private Timer portalRotateTimer; //timer that portal rotates with
		private Timer jumpTimer; //timer that controls jumping
		
		private PortalRotateListener prListener; //actionlistener for rotating portal
		private JumpListener jumpListener; //actionlistener for jumping
		private MonsterMover monsterMover; //actionlistener for moving monster
		
		private boolean jumpLeft; //jump left or right?
		private boolean potionVisible = false; //if player can see the potion
		private boolean attacking; //whether or not player is currently attacking
		
		private Image background, player, monster, potion; //some images, names describe
		private BufferedImage portal; //needs to be a bufferedimage to rotate
		private Image[][] atks = new Image[2][4]; //stores attack images - class, which attack
		
		private JProgressBar monsterHPBar; //monster hp bar above the monster

		private int currentFrame; //which frame of attack to show
		private int potionX, potionY; //potion x and y-coord
		private int rotationDegree = 0; //the degree of rotation of portal
		private int atkNum; //which attack they are using
		
		//each number corresponds to current monster/stage
		private int[] xOffset = {0,0,0,0,30}; //x offset when drawing monster
		private int[] yOffset = {10,0,50,60,10}; //y offset when drawing monster
		private int[] width = {250,280,150,175,340}; //width of a certain monster
		private int[] height = {300,300,350,350,320}; //height of a certain monster


		public BattlePanel()
		{
			setLayout(null); //null layout
			
			//set these images
			background = ImageIconFactory.getBackgroundImage();
			//get player image based on gender, class, direction facing
			player = ImageIconFactory.getPSImage(game.getIsBoy(), game.getIsClass1(), game.getLastKeyPressed());
			potion = ImageIconFactory.getImage("potion.gif");
			portal = (BufferedImage) ImageIconFactory.getPortalImage(); //bufferedimage for rotation

			currentFrame = 1; //3-4 frames per attack, start on first one
			attacking = false; //not currently attacking
			stage = game.getStage(); //current stage
			setMonsterHPBar(); //set monster hp bar foreground/background,add changelistener, add it
			
			//action listeners
			monsterMover = new MonsterMover(); 
			jumpListener = new JumpListener();
			prListener = new PortalRotateListener();
			
			//initialize timers, set the delay
			monsterMoveTimer = new Timer(400,monsterMover); //250
			playerAttackTimer = new Timer(300, this); //300
			jumpTimer = new Timer(60, jumpListener); //60
			portalRotateTimer = new Timer(400,prListener); //150
			
			portalRotateTimer.start(); //start rotating the portal
			monsterMoveTimer.setInitialDelay(250); //delay for 1/4 second before starting
			
			addKeyListener(this); //allow panel to get player input from keyboard
		}

		public void setPotionVisible(boolean vis) //set visibility of potion
		{
			potionVisible = vis;
		}
		
		public void resetMHPBar() //reset the monster hp bar
		{
			if(monsterHPBar!=null)
			{
				monsterHPBar.setMaximum(game.getMaxMonsterHP()); //set max to max monster hp
				monsterHPBar.setValue(game.getCurrentMonsterHP()); //set value to current hp
			}
		}

		public void setMonsterHPBar() //initialize monster hp bar, set preferences, add changelistener, add to panel
		{
			monsterHPBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, game.getMaxMonsterHP());
			monsterHPBar.setForeground(Color.GREEN);
			monsterHPBar.setBackground(Color.RED);
			monsterHPBar.addChangeListener(this);
			add(monsterHPBar);
		}

		private void pauseMonster() //pauses monster if monster is moving
		{
			if(monsterMoveTimer.isRunning())
				monsterMoveTimer.stop();
		}

		private void resumeMonster() //resumes monster if monster is not moving
		{
			if(!monsterMoveTimer.isRunning())
				monsterMoveTimer.start();
		}

		public void setAttack(int num) //set which attack they use
		{
			attacking = true; //they are attacking now
			atkNum = num; //which attack to use
			playerAttackTimer.start(); //start attacking
		}

		public void startJumpingLeft() //jump left
		{
			if(!jumpTimer.isRunning()) //if not currently jumping
			{
				jumpTimer.start(); //start jumping
				jumpLeft = true; //jump to left
			}
		}

		public void startJumpingRight() //jump right
		{
			if(!jumpTimer.isRunning()) //if not currently jumping
			{
				jumpTimer.start(); //start jumping
				jumpLeft = false; //jump to right
			}
		}

		public int getMonsterWidth() //get current monster width (different for each stage)
		{
			return width[stage-1];
		}

		public void paintComponent(Graphics g) //does the painting
		{
			super.paintComponent(g);

			g.drawImage(background, 0, 0, 815, 565,this); //draws the background

			//rotating the portal
			double rotationRequired = Math.toRadians(rotationDegree); //rotation degree needed to rotate portal
			AffineTransform at = AffineTransform.getRotateInstance(rotationRequired, 67.5, 67.5); //rotates coordinates around an anchor point
			AffineTransformOp atOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR); //linear transofrmation
			g.drawImage(atOp.filter(portal, null), 670, 410, null); //draw the rotated image
			
			//g.drawImage(portal,670,410,135,135,this);
			
			stage = game.getStage(); //set the stage

			if(game.getMonsterDirection()) //if monster is facing left
				monster = ImageIconFactory.getMonsterImage(stage, true); //get image of left-facing monster
			else
				monster = ImageIconFactory.getMonsterImage(stage, false); //get image of right-facing monster

			int stageIndex = stage - 1; //stageindex is one less than stage (arrays start at 0, not 1)

			if(game.getCurrentMonsterHP()>0 && stage<=5) //if monster is still alive, and game not over
			{
				g.drawImage(monster, game.getMonsterX()-xOffset[stageIndex], game.getMonsterY() //draw monster
						-yOffset[stageIndex], width[stageIndex], height[stageIndex], this);

				int rand = (int)(100*Math.random()+1); //up to 100 dmg
				if(game.isClose(getMonsterWidth())&&game.getCurrentHP()>0) //if player is close to monster
				{
					game.setCurrentHP(game.getCurrentHP()-rand); //decrement player hp
					g.setFont(new Font("Serif", Font.BOLD, 50)); 
					g.setColor(Color.MAGENTA);
					g.drawString(""+rand, game.getPlayerX()+37, game.getPlayerY()-40); //print out damage
				}
			}

			playerInfoPanel.hpBar.setValue(game.getCurrentHP()); //set value of player hp
			playerInfoPanel.hpBar.setString("HP : " + game.getCurrentHP()+ //set text to be currenthp/maxhp
					" / "+ game.MAXHP);
			//player image based on gender/class/direction facing
			player = ImageIconFactory.getPSImage(game.getIsBoy(), game.getIsClass1(), game.getLastKeyPressed());

			if(playerAttackTimer.isRunning()) //if player is currently attacking
			{
				if(game.getIsClass1()) //if warrior
				{
					//draw player in a different position instead of the standing image
					g.drawImage(atks[0][atkNum-1], game.getPlayerX(),game.getPlayerY()-40,105,115,this);
				}
				else //if magician
				{
					//draw the player normally
					g.drawImage(player,game.getPlayerX(),game.getPlayerY()-40,game.PLAYERWIDTH,115,this);
					
					if(atkNum==1||atkNum==2)//if it is one of the first 2 attacks (doesn't change based on direction of player)
					{
						//draw the attack over the monster
						g.drawImage(atks[1][atkNum-1], game.getMonsterX(), game.getMonsterY(), 200 ,300,this);
					}
					else //if one of the last 2 attacks (direction of player matters)
					{
						if(game.getLastKeyPressed()=='a') //facing left
						{
							if(currentFrame==1||currentFrame==2) //drawings based on player location
								g.drawImage(atks[1][atkNum-1], game.getPlayerX(), game.getPlayerY()-80,105,150,this);
							else
								g.drawImage(atks[1][atkNum-1], 0, game.getPlayerY()-80,game.getPlayerX()-50,150,this);
						}
						else if(game.getLastKeyPressed()=='d') //facing right
						{
							if(currentFrame==1||currentFrame==2)
								g.drawImage(atks[1][atkNum-1], game.getPlayerX(), game.getPlayerY()-80,100,150,this);
							else
								g.drawImage(atks[1][atkNum-1], game.getPlayerX()+50, game.getPlayerY()-80,800,150,this);
						}
					}
				}
			}
			else //if not attacking, just draw the standing player
			{
				g.drawImage(player,game.getPlayerX(),game.getPlayerY()-40,game.PLAYERWIDTH,115,this);
			}
			
			if(potionVisible) //if potion is visible (monster must die first)
			{
				potionX = game.getMonsterX()+(getMonsterWidth()-50)/2; //potion x loc
				potionY = 500; //potion y loc
				g.drawImage(potion, potionX, potionY, 50, 50, this); //draw potion where monster died
			}

			if(game.getCurrentMonsterHP()>0) //if monster is still alive
			{
				if(monsterHPBar!=null) //if monster hp bar is not null (null when monster dies and it disappears)
				{
					//redraw the monster hp bar to be over the monster
					monsterHPBar.setBounds(game.getMonsterX(),game.getMonsterY()-15,width[stageIndex],10);
					monsterHPBar.setValue(game.getCurrentMonsterHP()); //set the value
				}
			}
			else //if monster is dead
			{
				if(monsterHPBar!=null) //if it is still active
				{
					remove(monsterHPBar); //get rid of it (don't want to see if no monster)
					monsterHPBar = null;
				}
				monsterMoveTimer.stop(); //stop the timer, monster doesn't exist
				repaint();
			}

			if(attacking) //if currently attacking
			{
				if(game.isValidAttack(battlePanel.getMonsterWidth()) && attackPanel.getDmgToMonster()>0)
				{
				//if the attack will do damage, and the damage is greater than 0
					if(game.getIsCritical()) //if it is critical hit, set to certain font/color
					{
						g.setFont(new Font("Serif", Font.BOLD, 75));
						g.setColor(Color.RED);
					}
					else //set to normal font and color
					{
						g.setFont(new Font("Serif", Font.BOLD, 70));
						g.setColor(Color.ORANGE);		
					}
					//print out the damage
					g.drawString(""+attackPanel.getDmgToMonster(), game.getMonsterX()+42, game.getMonsterY()-15);
				}
			}

			if(game.getCurrentHP()>game.MAXHP/3) //if hp is more than 1/3, show green
				playerInfoPanel.hpBar.setForeground(Color.green);
			else //if hp is less than 1/3, show yellow  (warning of low hp)
				playerInfoPanel.hpBar.setForeground(Color.yellow);

			g.setColor(Color.red); //set color to red
			g.fillRoundRect(295, 50, 250, 55, 11, 11); //draw round rectangle
			g.setColor(Color.white); //set color to white
			g.setFont(new Font("Serif", Font.BOLD, 50)); //font

			if(game.getCurrentMonsterHP()==0 && game.getCurrentHP()>0) //if player is alive/monster is dead
				g.drawString("CLEAR", 330, 93); //show clear
			else //if monster is still alive
				g.drawString("Stage " + stage, 340, 90); //show what stage instead

			requestFocus();
		}

		public void keyPressed(KeyEvent e) //gets called when user types input
		{
			char key = e.getKeyChar(); //get the character they typed
			if(game.getCurrentHP()>0) //if player is alive
			{
				if(key=='a'&&game.getPlayerX()>-10) //move left, but not past a certain bound
					game.moveLeft(true);
				else if(key=='d'&&game.getPlayerX()<690) //move right, but not past a certain bound
					game.moveLeft(false);
				else if(key=='q'&&game.getPlayerX()>100) //jump left, but not past a certain bound
					startJumpingLeft();
				else if(key=='e'&&game.getPlayerX()<600) //jump right, but not past a certain bound
					startJumpingRight();
				else if(key=='p'&&potionVisible&&game.isCloseToPotion(potionX)) //if key pressed is p/player is close to potion
				{
					game.setCurrentHP(game.getCurrentHP()+2500); //heal
					potionVisible = false; //make it disappear
				}
				else if(key=='w'&&game.getPlayerX()>=680&&game.getCurrentMonsterHP()==0) //if monster dead/press up in portal
				{
					if(stage<5) //if not last stage
					{
						game.nextStage();  //go to next stage
						setMonsterHPBar(); //new monster hp bar
						potionVisible = false; //can't see potion
						resumeMonster(); //start monster timer
						repaint();  //new stage, need to repaint
					}
					else //if last stage cleared
					{
						String winmsg = "You have won.  Your name" + //add name to hall of fame
								" will be added to the Hall Of Fame.  \nGame will exit.  If you" +
								" relaunch the game, you will be able to see your name in it.";
						JOptionPane.showMessageDialog(battlePanel,winmsg);
						pauseMonster(); //stop monster
						game.addToHallOfFame(game.getPlayerName()); //add name to hall of fame
						System.exit(0); //exit game
					}
				}

				//cheats 
				
				if(key=='m')
					game.setMonsterHP(1); //make monster almost dead
				else if(key=='8')
					game.setCurrentHP(Game.MAXHP); //heal completely
				else if(key=='n')
					game.setCurrentHP(101); //make yourself almost dead
				else if(key=='h')
					game.setMonsterHP(game.MAXMONSTERHP); //fully heal monster */
				if(key=='[')
					game.setNumberRange(4); //always 1*1, 1-1, 1+1, or 1%1
				else if(key==']')
					game.setNumberRange(5); //extremely difficult math questions
				else if(key=='c')
					game.setAlwaysCritical(true); //always critical hits (for that stage)
				else if(key=='i')
					game.moveUp(true); //move up
				else if(key=='k')
					game.moveUp(false); //move down
				
				repaint();
			}
		}
		
		public void stateChanged(ChangeEvent e) //required method for Changelistener
		{

		}

		public void keyReleased(KeyEvent e) //required KL method
		{
			
		} 

		public void keyTyped(KeyEvent e) //required KL method
		{
			
		} 

		public void actionPerformed(ActionEvent e) //when attack
		{
			//shows attacks based on which attack number/frame, stops timer when attack is over
			//hard-to-understand code, necessary because some attacks have 3 frames, some have 4
			//and different pictures for each
			if(game.getIsClass1()) //if warrior
			{
				if(atkNum==1 || atkNum==3) //attacks 1 and 3
				{
					if(currentFrame>3) //if last frame
					{
						finishAttack(); //stop the attack
					}
					else //if not last frame
					{
						//get correct image
						atks[0][atkNum-1] = ImageIconFactory.getAtkImage(atkNum,game.getIsBoy(),
								game.getClass1(),game.getLastKeyPressed(),currentFrame);
						repaint();
					}
				}
				else //attacks 2 and 4, same as above, but 4 frames instead of 3
				{
					if(currentFrame>4)
					{
						finishAttack();
					}
					else
					{
						atks[0][atkNum-1] = ImageIconFactory.getAtkImage(atkNum,game.getIsBoy(),
								game.getClass1(),game.getLastKeyPressed(),currentFrame);
						repaint();
					}
				}
			}
			else //magician
			{
				if(currentFrame>3) //if last frame
				{
					finishAttack(); //stop the attack
				}
				else
				{
					//get the appropriate image
					atks[1][atkNum-1] = ImageIconFactory.getAtkImage(atkNum,game.getIsBoy(),
							game.getClass1(),game.getLastKeyPressed(),currentFrame);
					repaint();
				}
			}
			if(game.getAlwaysCritical()) //if cheated, critical is still true after each attack
				game.setIsCritical(true);
			currentFrame++; //go to next frame of attack
		}
		
		public void finishAttack() //does the steps to end an attack
		{
			currentFrame = 1; //current frame is 1
			attacking = false; //done attack
			game.setIsCritical(false);//if critical, not anymore
			playerAttackTimer.stop(); //stop attacking
		}

		class PortalRotateListener implements ActionListener //AL for rotating portal
		{
			public void actionPerformed(ActionEvent e)
			{
				rotationDegree-=5; //rotate it 5 degrees left
				rotationDegree%=360; //make it less than 360 (if >360)
			}
		}

		class MonsterMover implements ActionListener  //AL for moving monster left/right
		{
			public void actionPerformed(ActionEvent e) 
			{
				int mx = game.getMonsterX(); //monster x-coord
				int px = game.getPlayerX(); //player x-coord
				int dist = mx-px; //distance between them
				if(dist>0) //if monster is on the right
				{
					game.moveMonsterLeft(true); //move left to close the gap
					game.setMonsterDirection(true); //face left
				}
				else //if monster on left
				{
					game.moveMonsterLeft(false); //move right to close the gap
					game.setMonsterDirection(false); //face right
				}
				if(game.getCurrentHP()==0) //if player dies
				{
					String deathmsg = "You have died. Game will exit.";
					JOptionPane.showMessageDialog(battlePanel,deathmsg); //show death message
					pauseMonster(); //stop moving monster	
					System.exit(0); //exit game
				}
				else //otherwise continue normally
				{
					repaint(); 
				}
			}
		}

		class JumpListener implements ActionListener //AL for jumping
		{
			public void actionPerformed(ActionEvent e) 
			{
				if(game.isJumping()) //if currently jumping
				{
					game.jumpLeft(jumpLeft); //jump either left or right
					game.nextJumpStep(); //go to next step of the jump
				}
				else
				{
					jumpTimer.stop(); //stop jumping
				}
			}
		}
	}

	public void mouseClicked(MouseEvent e) //required method for mouselistener
	{

	}

	public void mouseEntered(MouseEvent e)  //required method for mouselistener
	{
		battlePanel.resumeMonster(); //resume monster when entering frame
	}

	public void mouseExited(MouseEvent e) //required method for mouselistener
	{
		int y = e.getY(); //get y loc of mouse
		if(y>100) //if it is not at the top (in attack button panel)
			battlePanel.pauseMonster(); //stop monster
	}

	public void mousePressed(MouseEvent e) //required method for mouselistener
	{

	}

	public void mouseReleased(MouseEvent e) //required method for mouselistener
	{

	}
}