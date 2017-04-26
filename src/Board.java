import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Image;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

//==== SOCKET ====
import java.lang.*;
import java.io.*;
import java.net.*;
//==== /SOCKET ====


public class Board extends JPanel implements ActionListener 
{
	//connection structure ----------------------------------
	
		//==SERVER==                         ==CLIENT==//
		//          |    Coin location      |          //
		//          |    it,                |          //
		//          |    player1 location,  |          //
		//          |    player2 location,  |          //
		//          |    timeSinceTagged,   |          //
		//          |    player1 score,     |          //
	    //          |    player2 score      |          //
		//          |  ------------------>  |          //
		//          |                       |          //
		//          |                       |          //
		//          |                       |          //
		//          |    player2 location   |          //
		//          |  <------------------  |          //
	
	// \connection structure --------------------------------
	
	
	
	
	//attributes
	public static String version = "2.4";      //game version
	private ImageIcon programIcon;             //program's window icon
    private Timer timer;                       //timer for the keeping track of player movement
    private Craft player1;                     //player 1
    private Craft player2;                     //player 2
    private Coin coin;                         //coin
    private Image background;                  //background of arena
    int it = 1;                                //indicates which player is "it"
    String status;                             //holds game status displayed in scoreboard
    String message;                            //holds information to send as a Datagram packet (changes if server or client)
    int tagbackTimeMilliseconds = 600;         //constant time until tagback can occur
    int timeSinceTagged = 0;                   //current time since last tag occurrence
    private final int DELAY = 10;              //timer delay
    private int COIN_Y_SIZE;                   //coin's y axis length
    private int COIN_X_SIZE;                   //coin's x axis length
    private int CRAFT_Y_SIZE;                  //player's y axis length
    private int CRAFT_X_SIZE;                  //player's x axis length
    private JLabel scoreboard;                 //game status
    public static String serverOrClient = "";  //text appended to title of frame, either "Client" or "Server"
    private JLabel connectedStatus;            //connection status, either "Disconnected", "Connecting", or "Connected"
    private int playerThatWon = 0;             //will keep track of the winning player (first player to get ten points)
    public JFrame winningFrame;                //window to appear TO SERVER when player wins, asking for rematch
    public JLabel winningLabel;                //part of winningFrame, notifies that a player has won
    
    
    // TCP/UDP Connection Components
    public static int port = 0;                       //{UDP}port to send Datagram packets to (client only, because the client need not check connection)              
    public static int returnPort = 0;                 //{UDP}port to return Datagram packets to (server only, because the server must check connection)
    public static DatagramSocket UDPSocket = null;    //{UDP}socket to transceive Datagram packets
    public static Socket socket = null;               //[OLD]{TCP} socket for TCP communication
    public static BufferedReader in = null;           //[OLD]{TCP} reader to read data from socket
    public static PrintWriter out = null;             //[OLD]{TCP} writer to write data from socket  
    public boolean isServer = true;                   //indicates if user is server or client
	public String ipAddress = "localhost";            //string form of user-entered IP address (if client, the IP of the server, if server, "localhost")
	InetAddress IPAddress = null;                     //InetAddress form of the IP address (if client, the IP of the server, if server, "localhost")	
	boolean correctConnectButtonPressed = false;      //indicates if correct user-input information has been entered for connection information    
    boolean player2Connected = false;                 //indicates if packets were sent from a client to the server (connection)
    
    //window icon
    public static ImageIcon icon = new ImageIcon("programIcon.png"); //icon for window
    
    //player animation
    private static Image[] sprites;            //sprite array of player icons
    private Image player1CurrentSprite;        //player1's current image
    private Image player2CurrentSprite;        //player2's current image
    int characterAnimationTimeToChange = 200;  //time until player animation iterates
    int timeSinceLastAnimation = 0;            //current time since last animation
    
    
    

    /**
     * Constructor to initialize board
     */
    public Board() 
    {
        initBoard();
    }
 
    
    
    
    
    /**
     * Method to instantiate playing board
     */
    private void initBoard() 
    {
        //TAdapter listener listens for user keystrokes (arrow keys)
        this.addKeyListener(new TAdapter());
        
        //sets focusability of this panel to true
        setFocusable(true);

        //store sprite images
        sprites = new Image[10];
        sprites[0] = ((new ImageIcon("player1.png")).getImage());
        sprites[1] = ((new ImageIcon("player1_2.png")).getImage());
        sprites[3] = ((new ImageIcon("player1it.png")).getImage());
        sprites[4] = ((new ImageIcon("player1it_2.png")).getImage());
        sprites[5] = ((new ImageIcon("player2.png")).getImage());
        sprites[6] = ((new ImageIcon("player2_2.png")).getImage());
        sprites[7] = ((new ImageIcon("player2it.png")).getImage());
        sprites[8] = ((new ImageIcon("player2it_2.png")).getImage());
        sprites[9] = ((new ImageIcon("coin.png")).getImage());
        
        //instantiate player(s) and coin
        player1 = new Craft(sprites[3]);
        player1CurrentSprite = sprites[3];
        coin = new Coin();
        
        //assign image to background and set it
        ImageIcon imageIcon = new ImageIcon("background.png");
        background = imageIcon.getImage();

        //initializes the score board
        scoreboard = new JLabel("Score: " + player1.score);
        scoreboard.setForeground(Color.RED);
        scoreboard.setOpaque(true);
        scoreboard.setBackground(Color.BLUE);
        scoreboard.setFont(new Font("Serif", Font.BOLD, 24));
        add(scoreboard);

        //calculate coin dimensions
        COIN_X_SIZE = coin.getImage().getWidth(null);//50;
        COIN_Y_SIZE = coin.getImage().getHeight(null);//50;

        //calculate player dimensions
        CRAFT_X_SIZE = player1.getImage().getWidth(null);
        CRAFT_Y_SIZE = player1.getImage().getHeight(null);
        

        //instantiate and execute timer for the TAdapter listener
        timer = new Timer(DELAY, this);
        timer.start();
        
        //instantiate winning frame (viewed by server) to be viewed when winning occurs
        winningFrame = new JFrame("Winner!");
	        winningLabel = new JLabel("Player has won! Play again?");
	        JButton yesOption = new JButton("Yes");
	        JButton noOption = new JButton("No");
	        winningFrame.setLayout(new GridLayout(3,1));
	        winningFrame.add(winningLabel);
	        winningFrame.add(yesOption);
	        winningFrame.add(noOption);
	        winningFrame.setVisible(false);
	        winningFrame.setSize(200,150);
	        winningFrame.setResizable(false);
	        winningFrame.setLocationRelativeTo(null);
	        
	        //listener for play again options
	        class ButtonListener implements ActionListener
	        {
	        	public void actionPerformed(ActionEvent event)
	        	{
	        		if (event.getSource() == yesOption)
	        		{
	        			player2Connected = false; //to reset connections when connection must occur
	        			cleanUp(); //to close the connections (to reset them later)
	        			winningFrame.setVisible(false);
	        			//reset stats
	        			playerThatWon = 0;
	        			player1.setX(30);
	        			player1.setY(30);
	        			player2.setX(300);
	        			player2.setY(300);
	        			player1.score = 0;
	        			player2.score = 0;
	        			player2.setDY(0);
	        			player2.setDX(0);
	        			player1.setDY(0);
	        			player1.setDX(0);
	        			connectedStatus.setForeground(Color.BLUE);
	        			connectedStatus.setText("Connecting...");
	        		}
	        		else if (event.getSource() == noOption)
	        		{
	        			System.exit(0);
	        		}
	        	}
	        }
	        noOption.addActionListener(new ButtonListener());
	        yesOption.addActionListener(new ButtonListener());     
	        
    }
 
    
    
    
    
    
    /*
     * Method to clean up sockets and connections
     */
    private static void cleanUp() 
    {
    	//try to close the Datagram socket
        try 
        {
           if (UDPSocket != null) 
           {
        	   UDPSocket.close();
        	   UDPSocket = null;
           }
        }
        catch (Exception e) { UDPSocket = null; }

        
        
        //try to close the TCP socket
        try 
        {
           if (socket != null) 
           {
              socket.close();
              socket = null;
           }
        }
        catch (IOException e) { socket = null; }

        
        
        //try to close the buffered reader
        try 
        {
           if (in != null) 
           {
              in.close();
              in = null;
           }
        }
        catch (IOException e) { in = null; }

        
        
        //try to close the buffered writer
        if (out != null) 
        {
           out.close();
           out = null;
        }
     }

    
    
    
    
    

    /**
     * Method to redraw and refresh graphics on page
     * (Called by Event Dispatcher Thread)
     */
    @Override
    public void paintComponent(Graphics g) 
    {
    	//call the parent class paint method
        super.paintComponent(g);
        
        //draw on-screen components and send packets
    	doDrawing(g);
    	
    	//handle packet reception
    	connectionHandling();

    	//sync graphics
        Toolkit.getDefaultToolkit().sync();
    }

    
    
    
    
    /**
     * Method to redraw player(s) and coin on the screen
     * (Called by Event Dispatcher Thread)
     * @param g
     */
    private void doDrawing(Graphics g) 
    {
    	//compute animation
    	if ((timeSinceLastAnimation >= characterAnimationTimeToChange) && player2Connected)
    	{    
    		//==================PLAYER1==================
    		//iterate between normal player1 sprites
    		if (player1CurrentSprite.equals((Image)sprites[0]) && it!=1)
    		{
    			player1.setImage(sprites[1]);
    			player1CurrentSprite = sprites[1];
    		}   		
    		else if (player1CurrentSprite.equals((Image)sprites[1]) && it!=1)
    		{
    			player1.setImage(sprites[0]);
    			player1CurrentSprite = sprites[0];
    		}
    		
    		//iterate between it player1 sprites
    		else if (player1CurrentSprite.equals(sprites[3]) && it==1)
    		{
    			player1.setImage(sprites[4]);
    			player1CurrentSprite = sprites[4];
    		}
    		else if (player1CurrentSprite.equals(sprites[4]) && it==1)
    		{
    			player1.setImage(sprites[3]);
    			player1CurrentSprite = sprites[3];			
    		}
    		
    		//transition between it and not it player1 sprites
    		else if ((player1CurrentSprite.equals(sprites[4])) || player1CurrentSprite.equals(sprites[3]) && it != 1)
    		{
    			player1.setImage(sprites[0]);
    			player1CurrentSprite = sprites[0];
    		}
    		else if ((player1CurrentSprite.equals(sprites[0]) || player1CurrentSprite.equals(sprites[1])) && it == 1)
    		{
    			player1.setImage(sprites[3]);
    			player1CurrentSprite = sprites[3];
    		}
    		
    		System.out.println("P1 done");
    		
    		
    		//==================PLAYER2==================
    		//iterate between normal player2 sprites
    		if (player2CurrentSprite.equals((Image)sprites[5]) && it!=2)
    		{
    			player2.setImage(sprites[6]);
    			player2CurrentSprite = sprites[6];
    		}   		
    		else if (player2CurrentSprite.equals((Image)sprites[6]) && it!=2)
    		{
    			player2.setImage(sprites[5]);
    			player2CurrentSprite = sprites[5];
    		}
    		
    		//iterate between it player2 sprites
    		else if (player2CurrentSprite.equals(sprites[7]) && it==2)
    		{
    			player2.setImage(sprites[8]);
    			player2CurrentSprite = sprites[8];
    		}
    		else if (player2CurrentSprite.equals(sprites[8]) && it==2)
    		{
    			player2.setImage(sprites[7]);
    			player2CurrentSprite = sprites[7];			
    		}
    		
    		//transition between it and not it player2 sprites
    		else if ((player2CurrentSprite.equals(sprites[7])) || player2CurrentSprite.equals(sprites[8]) && it != 2)
    		{
    			player2.setImage(sprites[5]);
    			player2CurrentSprite = sprites[5];
    		}
    		else if ((player2CurrentSprite.equals(sprites[5]) || player2CurrentSprite.equals(sprites[6])) && it == 2)
    		{
    			player2.setImage(sprites[7]);
    			player2CurrentSprite = sprites[7];
    		}

    		timeSinceLastAnimation = 0;
    	}
    	repaint();
    	
    	
    	
    	
    	//if win occurs, make winningFrame visible TO THE SERVER
    	if (player2Connected && (player1.score >= 10 || player2.score >= 10))
    	{
    		if (player1.score > player2.score){playerThatWon = 1;}
    		else {playerThatWon = 2;}
    		winningLabel.setText("Player " + playerThatWon + " has won!! Play Again?");
    		winningFrame.setVisible(true);
    	}
    	
        //draw background, player, and coin in respective positions
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(background, 0, 0, this);
        g2d.drawImage(player1.getImage(), player1.getX(), player1.getY(), this);
        g2d.drawImage(coin.getImage(), coin.getX(), coin.getY(), this);
        
        //interpret player1-coin collision (if server) and update status
        if (((player1.getX()+CRAFT_X_SIZE)>coin.getX() && player1.getX()<coin.getX()+COIN_X_SIZE) && (player1.getY()<coin.getY()+COIN_Y_SIZE && (player1.getY()+CRAFT_X_SIZE)>(coin.getY())) && (it == 2))
        {
        	if (isServer)
        	{
	        	coin.setX();
	        	coin.setY();
	        	player1.score += 1;
	        	updateStatus();
	        	scoreboard.setText(status);
        	}
        	repaint();
        }
        
        
        // if player2 is connected, interpret who is it, player2 coin reception
        if (player2Connected)
        {
        	//draw player2
        	g2d.drawImage(player2.getImage(), player2.getX(), player2.getY(), this); 
        	updateStatus();
        	scoreboard.setText(status);
        	
        	//if server, interpret player2 interaction
        	if (isServer)
        	{
	        	//interpret player2-coin collision
	            if (((player2.getX()+CRAFT_X_SIZE)>coin.getX() && player2.getX()<coin.getX()+COIN_X_SIZE) && (player2.getY()<coin.getY()+COIN_Y_SIZE && (player2.getY()+CRAFT_X_SIZE)>(coin.getY())) && (it == 1))
	            {
	            	coin.setX();
	            	coin.setY();
	            	player2.score += 1;
	            	updateStatus();
	            	scoreboard.setText(status);
	            }  
	            
	            //interpret player1 tagging player2
	            if (((player1.getX()+CRAFT_X_SIZE)>player2.getX() && player1.getX()<player2.getX()+CRAFT_X_SIZE) && (player1.getY()<player2.getY()+CRAFT_Y_SIZE && (player1.getY()+CRAFT_X_SIZE)>(player2.getY())) && (it == 1) && (timeSinceTagged > tagbackTimeMilliseconds))
	            {
	            	timeSinceTagged = 0;
	            	it = 2;
	            	updateStatus();
	            	scoreboard.setText(status);
	            	System.out.println("it: 2");
	            }  
	            
	            //interpret player2 tagging player1
	            if (((player2.getX()+CRAFT_X_SIZE)>player1.getX() && player2.getX()<player1.getX()+CRAFT_X_SIZE) && (player2.getY()<player1.getY()+CRAFT_Y_SIZE && (player2.getY()+CRAFT_X_SIZE)>(player1.getY())) && (it == 2) && (timeSinceTagged > tagbackTimeMilliseconds))
	            {
		            	timeSinceTagged = 0;
		            	it = 1;
		            	updateStatus();
		            	System.out.println("it: 1");
		            	scoreboard.setText(status);
	            } 
        	}
            repaint();

            
            //try to send Datagram packets:
            //if server, message is: p1x,p1y,p2x,p2y,coinx,coiny,p1score,p2score,it,timeSinceTagged \n
            //if client, message is: p2x,p2y \n
            try
            {
            	//create a new byte to store message in
	            byte[] sendData = new byte[1024];
		        
		        //if server, create specific message, update timeSinceTagged, send message as Datagram packet
		        if (isServer)
		        {
		        	//create message
		        	message = (player1.getX()+","+player1.getY()+","+player2.getX()+","+player2.getY()+","+coin.getX()+","+coin.getY()+","+player1.score+","+player2.score+","+it+","+timeSinceTagged +"\n");
		        	
		        	//iterate timeSinceTagged if not at limit
		        	if (timeSinceTagged <= tagbackTimeMilliseconds)
			        	timeSinceTagged++;
		        	
		        	
		        	//assign the message as the sendData byte[] object
		        	sendData = message.getBytes();
		        	
		        	//create Datagram packet to send to client's IP and reading port
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, returnPort);
					
					//send the packet
					UDPSocket.send(sendPacket);
					
		        }
		        
		        //if client, create message, and send message
		        else
		        {
		        	//create message
		        	message = (player2.getX()+","+player2.getY() +"\n");
		        	
		        	//assign message as the sendData byte[] object
					sendData = message.getBytes();
					
					//create Datagram packet to send to server's IP and reading port
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
					
					//send packet
					UDPSocket.send(sendPacket);
		        }
            }
            catch(IOException e){System.out.println(e);}
            
            
            //iterate timeSinceLastAnimation if not at limit
        	if (timeSinceLastAnimation <= characterAnimationTimeToChange)
        		timeSinceLastAnimation++;
            
            
            //TCP [OLD]
	        //out.print(message+"\n");
	        //out.flush();	        
        }
        
    }

    
    
    
    
    
    /**
     * Method to interpret key strokes by user
     * and call move methods of player to move the player
     * listens through the board panel for any event
     * (Influenced by timer)
     */
    @Override
    public void actionPerformed(ActionEvent e) 
    {
    	//if server, move player1
        if (isServer)
        {
	        player1.move();
	        
        }
        
        //if client, move player2
        else
        {
        	if (player2Connected)
        	{
	        	player2.move();
        	}
        }
        
        //refresh graphics
        repaint();
    }

    
    /*
     * This method updates the scoreboard information
     */
    private void updateStatus()
    {
    	//update status
    	status = ("Player 1 Score: " + player1.score + " Player 2 Score: " + player2.score + " It: " + "player " + it);
    }
    
    
    
    
    
    
    /*
     * This method manages recieving and parsing Datagram packets
     */
    private void connectionHandling()
    {    	
    	try
    	{
    		//create byte[] objects to store received data and sent data (needed to initially send to server)
    		byte[] receiveData = new byte[1024];
    		byte[] sendData = new byte[1024];
	        
    		//if not connected yet, instantiate Datagram sockets according to if is server or client
    		if(!player2Connected)
    		{
	    		//if server, open Datagram socket at server port and receive a packet
	    		if(isServer)
	    		{
	    			//open a Datagram socket at server port
	    			UDPSocket = new DatagramSocket(port);
	    			
	    			//create a packet to receive, receiving receiveData byte array
	    			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);	// Datagram packet handler
	    			
	    			//check timeout after 5 seconds (so user can exit)
	    			UDPSocket.setSoTimeout(5000);
	    			
    				//receive the UDP packet
    				UDPSocket.receive(receivePacket);

	    			//store the IP address that sent the packet as IPAddress
	    			IPAddress = receivePacket.getAddress();
	    			
	    			//store the port that sent the packet as returnPort
		        	returnPort = receivePacket.getPort();
			        	
	    		}
	    		
	    		//if client, open a Datagram socket
	    		else
	    		{
	    			UDPSocket = new DatagramSocket();
	    			
	    			// to send a packet, needed to initialize server's IPAddress and returnPort
	    			IPAddress = InetAddress.getByName(ipAddress);
	    			
	    			//message to send (initial message, content does not matter)
	    			String sentence = "HELLO, WORLD!";
	    			
	    			//convert message to bytes for packet transmit
	    			sendData = sentence.getBytes();
	    			
	    			//create packet to send to the server's IP address and port
	    			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
	    			
	    			//send packet
	    			UDPSocket.send(sendPacket);
	    		}
	    			
	    		//set socket timeout (throws SocketTimeoutExcpetion if nothing recieved in 1000ms)
	    		UDPSocket.setSoTimeout(1000);
	    		
		        //initialize player2 (if player2 not connected yet, so player2 will stay in start position until connected)
		        player2 = new Craft(sprites[5]);
		        player2CurrentSprite = sprites[5];
		        player2.setX(300);
		        player2.setY(300);
		        
		        //make player2 connected
		        player2Connected = true;
	
		        //update initial status
		        status = ("Player 1 Score: " + player1.score + " Player 2 Score: " + player2.score + " It: " + "player " + it);

    		}
    		
    		//if player2 is connected, parse received packets
    		else
    		{
    			//create packet to recieve
		        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);	// Datagram packet handler
		        
		        //receive packet
		        UDPSocket.receive(receivePacket);	// Recv UDP data packet
		        
		        //convert packet data to string
		        String s = new String(receivePacket.getData());	// Interpret UDP data as string
		        
		        //notify that data was received (not needed)
		        //System.out.println("RECEIVED: " + s);
		        
		        //update connectedStatus in connection window
		        connectedStatus.setText("Connected.");
				connectedStatus.setForeground(Color.GREEN);
		
		        //parse received data into a string array
		        String[] parsedInput = s.split("\n"); //"\n" is the delimiter
		        String[] input = parsedInput[0].split(","); //"," is the delimiter
		        
		        //declare variables to store string array contents into depending on if is server or client
		        String player1X;
		        String player1Y;
		        String player2X;
		        String player2Y;
		        String coinX;
		        String coinY;
		        String player1Score;
		        String player2Score;
		        String currentIt;
		        String timeSinceLastTagged;
		
		        //if server, parse player2 location into respective variables
		        if (isServer)
		        {
		        	//get IPAddress that packet was sent from
		        	IPAddress = receivePacket.getAddress();
		        	
		        	//get port that packet was sent from
		        	returnPort = receivePacket.getPort();
		        	
		        	//print source port (not needed)
		        	//System.out.println("PORT: " + returnPort);
		        	
		        	//store player2 location into respective variables
		        	player2X = input[0];
		        	player2Y = input[1];	        	
		        	
		        	//update player2 location accordingly
		        	player2.setX(Integer.parseInt(player2X));
		        	player2.setY(Integer.parseInt(player2Y));
		        }
		        else
		        {
		        	//store information into respective variables
			    	player1X = input[0];
			    	player1Y = input[1];
			    	player2X = input[2];
			    	player2Y = input[3];
			    	coinX = input[4];
			    	coinY = input[5];
			    	player1Score = input[6];
			    	player2Score = input[7];
			    	currentIt = input[8];
			    	timeSinceLastTagged = input[9];
			       
			    	//set player1 location, players' scores, it, coin location, and timeSinceTagged accordingly
			    	player1.setX(Integer.parseInt(player1X));
			    	player1.setY(Integer.parseInt(player1Y));
			    	player1.score = Integer.parseInt(player1Score);
			    	player2.score = Integer.parseInt(player2Score);
			    	it = Integer.parseInt(currentIt);
			    	coin.setHardCoordinates(Integer.parseInt(coinX),Integer.parseInt(coinY));
			    	timeSinceTagged = Integer.parseInt(timeSinceLastTagged);
			    }
			
		        //update scoreboard
			    updateStatus();
			    scoreboard.setText(status);
    		}
    	}
    	catch (SocketTimeoutException ste)
    	{
    		//if nothing recieved after awhile, assume disconnect (packet loss), and update connectedStatus accordingly
    		connectedStatus.setForeground(Color.RED);
    		connectedStatus.setText("Disconnected");
    	}
    	catch (IOException e)
    	{
    		//if socket trouble, print generated Exception and clean up
    		//System.out.println(e);
            cleanUp();
    	}

    	
    	
    }
    
    
    
    
    /**
     * Class listening for key events to interpret key EVENTS of player
     * and call methods of player accordingly to change direction variables of player
     * @author Trevor
     *
     */
    private class TAdapter extends KeyAdapter 
    {
    	//stop movement when key released
        @Override
        public void keyReleased(KeyEvent e) 
        {
        	//if server, player1 released key
        	if (isServer)
        	{
        		player1.keyReleased(e);
        	}
        	
        	//if client, player2 released key (if they are connected)
        	else
        	{
        		if (player2Connected)
        			player2.keyReleased(e);
        	}
        }

        //initiate movement when key pressed
        @Override
        public void keyPressed(KeyEvent e) 
        {
        	//if server, player1 pressed a key
        	if (isServer)
        	{
        		player1.keyPressed(e);
        	}
        	
        	//if client, player2 pressed a key (if they are connected)
        	else
        	{
        		if (player2Connected)
        			player2.keyPressed(e);
        	}
        	
        }
    }
    
    
    
    
    /*
     * This method initializes the options to connect in a GUI
     * (Called in MovingSprite class before invoking a new runnable of this 
     * Board class at the end of the Event Dispatch Thread)
     */
    public boolean initOptionFrame ()
	{		
    	//create main frame and main panel for the frame
		JFrame prompt = new JFrame();
		JPanel pane = new JPanel();
		
		//create panel with buttons to choose if server or client
		JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridLayout(1,2));
			ButtonGroup choice = new ButtonGroup();
				JRadioButton hostButton = new JRadioButton("Host");
				JRadioButton guestButton = new JRadioButton("Guest");
				choice.add(hostButton);
				choice.add(guestButton);
				hostButton.setSelected(true);
			buttonPanel.add(hostButton);
			buttonPanel.add(guestButton);
		
			//create text input for port and IP address
		JTextField portInput = new JTextField(10);
		JTextField ipInput = new JTextField(10);
		
		//create panel with connection status and connect button
		JPanel connectPanel = new JPanel();
			connectPanel.setLayout(new GridLayout(1,2));
			JButton connectButton = new JButton("Connect");
			connectedStatus = new JLabel("");
			connectPanel.add(connectedStatus);
			connectPanel.add(connectButton);
		
		//create panel for IP address input (label and text field)
		JPanel ipInputPanel = new JPanel();
			ipInputPanel.setLayout(new GridLayout(1,2));
			ipInputPanel.add(new JLabel("IP Address:"));
			JPanel ipPanel = new JPanel();
				ipPanel.setLayout(new FlowLayout());
				ipPanel.add(ipInput);
			ipInputPanel.add(ipPanel);
			ipInput.setEditable(false);
			
		//create panel for port input (label and text field)
		JPanel portInputPanel = new JPanel();
			portInputPanel.setLayout(new GridLayout(1,2));
			portInputPanel.add(new JLabel("Port:"));
			JPanel portPanel = new JPanel();
				portPanel.setLayout(new FlowLayout());
				portPanel.add(portInput);
			portInputPanel.add(portPanel);
		
			//create listener class for buttons
		class ButtonListener implements ActionListener
		{
			public void actionPerformed (ActionEvent event)
			{
				//if guest is chosen, user is guest, IP can be chosen
				if (event.getSource() == guestButton)
				{
					isServer = false;
					ipInput.setEditable(true);
				}
				
				//if host is chosen, user is server, IP cannot be chosen
				else if (event.getSource() == hostButton)
				{
					isServer = true;
					ipInput.setEditable(false);
				}
				
				//if connection initiated, try to become server or client, if error, notify user and start over
				else if (event.getSource() == connectButton)
				{
					try
					{
						//if client, be client, and store IP address (string) and port
						if (isServer == false)
						{
							serverOrClient = "Client";
							port = Integer.parseInt(portInput.getText());
							if (!ipInput.getText().equals(""))
								ipAddress = ipInput.getText();
							else
								throw new Exception("no ip");
						}
						
						//if server, be server, and store IP address (string) as "localhost" and port
						else if (isServer == true)
						{
							serverOrClient = "Server";
							port = Integer.parseInt(portInput.getText());
							ipAddress = "localhost";
							connectedStatus.setText("Connecting...");
							connectedStatus.setForeground(Color.BLUE);
						}
						
						//update connectionStatus
						correctConnectButtonPressed = true;
					}
					
					//if error occurs, notify user of incorrect data (in dialog box) and to re-enter data
					catch (Exception e)
					{
						JOptionPane.showMessageDialog(pane, "Make sure all fields are filled out correctly.", "Error", JOptionPane.WARNING_MESSAGE);
					}
					
					//test user input (not needed, commented out)
					//System.out.println(port);
					//System.out.println(isServer);
					//System.out.println(ipAddress);
				}
			}
		}
		
		//add listeners for each button
		hostButton.addActionListener(new ButtonListener());
		guestButton.addActionListener(new ButtonListener());
		connectButton.addActionListener(new ButtonListener());
			
		//add sub-panels to the main panel	
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		pane.add(buttonPanel);
		pane.add(portInputPanel);
		pane.add(ipInputPanel);
		pane.add(connectPanel);
		
		//add main panel to the main frame
		prompt.add(pane);
		prompt.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		prompt.setSize(250,200);
		prompt.setResizable(false);
		prompt.setVisible(true);
		
		//set icon
		prompt.setIconImage(icon.getImage());
		
		//if connect button not [properly] pressed, delay thread until pressed (InterruptedException)
		while (correctConnectButtonPressed == false)
		{
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException ie){}
		}
		
		//when connect button is pressed correctly, return true
		return true;
    }
}