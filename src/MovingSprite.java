import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;



public class MovingSprite extends JFrame 
{
    public static int WINDOW_SIZE_X = 600;
    public static int WINDOW_SIZE_Y = 600;
    public static Board board = new Board();
    
    
    
    
    /**
     * Constructor to call initUI()
     */
    public MovingSprite() 
    { 
        initUI();
    }
   
    
    
    /**
     * Method instantiates a frame with a Board panel
     */
    private void initUI() 
    { 
        add(board);
        
        
        setSize(WINDOW_SIZE_X,WINDOW_SIZE_Y);
        setResizable(false);
        this.setIconImage(board.icon.getImage());
        
        setTitle("Tag Game " + board.version + " " + board.serverOrClient);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }

    
    
    
    
    /**
     * main method to post an event (runnable) at the end of 
     * Swing's event list to process after all other GUI events are processed
     * @param args
     */
    public static void main(String[] args) 
    {
    	//if user entered correct connection data, create a new runnable instance of MovingSprite
    	//(at the end of the Event Dispatch Thread), which calls initUI()
        if (board.initOptionFrame())
        {
	        EventQueue.invokeLater(new Runnable() 
	        {
	            @Override
	            public void run() 
	            {     
	                MovingSprite ex = new MovingSprite();
	                ex.setVisible(true);
	
	            }
	        });
        }
    }
}