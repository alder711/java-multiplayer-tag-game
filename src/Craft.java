import java.awt.Image;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Craft 
{

    private int dx;      //craft change in x direction
    private int dy;      //craft change in y direction
    private int x;       //craft x coordinate
    private int y;       //craft y coordinate
    private Image image; //craft image
    public int score;    //craft score

    /**
     * Constructor calls initCraft method passing it the craft image
     * @param craftImage
     */
    public Craft(Image craftImage) 
    {
        
        initCraft(craftImage);
    }

    
    
    
    /**
     * Method giving craft an image, score, and coordinates
     * @param craftImage
     */
	private void initCraft(Image craftImage) 
    {
        
        image = craftImage;
        x = 40;
        y = 60; 
        score = 0;       
    }
	
	/**
	 * Method setting craft's image
	 * @param imageName
	 */
	public void setImage(Image imageName)
	{
		this.image = imageName;
	}

	
	

	/**
	 * Method to actually MOVE the craft by
	 * altering x and why by dx and dy
	 * (Called by the board panel's own Action Listener)
	 */
    public void move() 
    {
        if ((x+dx < MovingSprite.WINDOW_SIZE_X-image.getWidth(null))  && x+dx > 0)
        {
            x += dx;
        }

        if(y+dy < (MovingSprite.WINDOW_SIZE_Y-image.getHeight(null))  &&  y+dy > 0)
        {
            y += dy;
        }
    }

    
    
    
    /**
     * Method to return craft X coordinate
     * @return
     */
    public int getX()
    {
        return x;
    }

    
    
    
    /**
     * Method to set craft X coordinate
     * @param x
     */
    public void setX (int x)
    {
    	this.x = x;
    }
    
    
    
    
    /**
     * Method to set craft Y coordinate
     * @param y
     */
    public void setY(int y)
    {
    	this.y = y;
    }
    
    
    
    /**
     * Method to return the craft Y coordinate
     * @return
     */
    public int getY() 
    {
        return y;
    }

    
    /**
     * Method to set change in x
     */
    public void setDX(int x)
    {
    	this.dx = x;
    }
    
    
    /*
     * Method to set change in y
     */
    public void setDY(int y)
    {
    	this.dy = y;
    }
    
    /**
     * Method to return craft image
     * @return
     */
    public Image getImage() 
    {
        return image;
    }

    
    
    
    /**
     * Method interpreting a key pressed event
     * depending on which key was pressed
     * (modifying dx and dy accordingly)
     * @param e
     */
    public void keyPressed(KeyEvent e) 
    {
    	//interpret which arrow key was pressed
        int key = e.getKeyCode();

        //interpret actions depending on which arrow key was pressed (changing change in direction)
        if (key == KeyEvent.VK_LEFT) 
        {
            dx = -1;
        }

        if (key == KeyEvent.VK_RIGHT) 
        {
            dx = 1;
        }

        if (key == KeyEvent.VK_UP) 
        {
            dy = -1;
        }

        if (key == KeyEvent.VK_DOWN) 
        {
            dy = 1;
        }
    }

    
    
    
    /**
     * Method interpreting a key released event
     * depending on which key was released
     * (modifying the dx or dy variable accordingly)
     * @param e
     */
    public void keyReleased(KeyEvent e) 
    {
        //interpret which arrow key was released
        int key = e.getKeyCode();

        //interpret actions depending on which arrow key was released (changing change in direction)
        if (key == KeyEvent.VK_LEFT) 
        {
            dx = 0;
        }

        if (key == KeyEvent.VK_RIGHT) 
        {
            dx = 0;
        }

        if (key == KeyEvent.VK_UP) 
        {
            dy = 0;
        }

        if (key == KeyEvent.VK_DOWN) 
        {
            dy = 0;
        }
    }
}